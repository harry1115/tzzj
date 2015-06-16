/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.admin.resource;

import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.admin.entity.UserImage;
import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.util.file.FileUtil;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.io.File;
import java.io.InputStream;
import javax.activation.MimetypesFileTypeMap;
import javax.persistence.EntityManager;
import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ContainerRequest;

/**
 * UserImagesResource
 *
 * @author pcnsh197
 */
public class UserImagesResource {

    private final User userEntity; // appropriate jpa user entity

    private final EntityManager em; // entity manager provided by parent resource

    private final ServletConfig servletConfig;
    private final String deployPath;

    public UserImagesResource(EntityManager em, String userId, ServletConfig servletConfig) {
        this.em = em;
        this.userEntity = em.find(User.class, userId);
        this.servletConfig = servletConfig;
        this.deployPath = PropertiesUtils.getProperties(this.servletConfig
                .getServletContext()).getProperty("deploy-path");
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getImage(@QueryParam("pictureType") PictureType pictureType,
            @QueryParam("imageName") String imageName) {
        if (userEntity == null) {
            throw new WebApplicationException(404);
        }
        
        UserImage userImage;
        if(pictureType==null||imageName==null){
            userImage=userEntity.getUserImage(PictureType.PORTRAIT,null);
        }else{
            userImage=userEntity.getUserImage(pictureType, imageName);
        }
        if (userImage != null) {
            String fileSystemPath = userEntity.buildFileSystemPath(deployPath, 
                userImage.getImageName());
            File f = new File(fileSystemPath);
            if (f.exists()) {
                String mt = new MimetypesFileTypeMap().getContentType(f);
                return Response.ok().entity(f).build();
            }
        }
        //HTTP code 204
        return Response.noContent().build();
    }

    /**
     * putImage The image can be created and updated using this method.
     *
     * @param request
     * @param imageName
     * @param uploadedInputStream
     * @return
     * @throws Throwable
     */
    @PUT
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response putImage(@Context ContainerRequest request,
            @QueryParam("imageName") String imageName,
            InputStream uploadedInputStream) throws Throwable {

        userEntity.removeImageAndDeleteOnFS(PictureType.PORTRAIT,
                imageName, deployPath);

        String fileSystemPath = userEntity.buildFileSystemPath(deployPath,
                imageName);
        FileUtil.writeToFile(uploadedInputStream, fileSystemPath);

        String webDisplayPath = userEntity.buildWebDisplayPath(imageName);
        UserImage userImage = new UserImage(imageName,
                webDisplayPath, PictureType.PORTRAIT);
        userEntity.addImage(userImage);

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(userEntity);
            }
        });

        String output = "User " + userEntity.getUserId() + " picture "
                + imageName + " is uploaded to : " + webDisplayPath;
        return Response.ok().entity(output).build();
    }
}
