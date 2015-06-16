/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.shop.resource;

import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.shop.entity.Playbill;
import cn.comgroup.tzmedia.server.shop.entity.PlaybillImage;
import cn.comgroup.tzmedia.server.util.file.FileUtil;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import cn.comgroup.tzmedia.server.util.tx.PersistenceUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.MimetypesFileTypeMap;
import javax.persistence.EntityManager;
import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * PlaybillImagesResource
 *
 * @author pcnsh197
 */
public class PlaybillImagesResource {

    private final Playbill playbillEntity; // appropriate jpa Playbill entity
    private final EntityManager em; // entity manager provided by parent resource
    private final ServletConfig servletConfig;
    private final String deployPath;

    public PlaybillImagesResource(EntityManager em, Playbill playbillEntity, 
            ServletConfig servletConfig) {
        this.em = em;
        this.playbillEntity = playbillEntity;
        this.servletConfig = servletConfig;
        this.deployPath = PropertiesUtils.getProperties(this.servletConfig
                .getServletContext()).getProperty("deploy-path");
    }

    /**
     * getImage
     *
     * @param pictureType
     * @param imageName
     * @return Response
     * @throws java.io.IOException
     */
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getImage(@QueryParam("pictureType") PictureType pictureType,
            @QueryParam("imageName") String imageName) throws IOException {
        if (playbillEntity == null) {
            throw new WebApplicationException(404);
        }
        
        PlaybillImage playbillImage = playbillEntity.getPlaybillImage(pictureType, imageName);
        if (playbillImage != null) {
            String fileSystemPath = playbillEntity.buildFileSystemPath(deployPath, 
                playbillImage.getImageName());
            File f = new File(fileSystemPath);
            if (f.exists()) {
//                String mt = new MimetypesFileTypeMap().getContentType(f);
                return Response.ok().entity(f).build();
            }
        }
        //HTTP code 204
        return Response.noContent().build();
    }
     
    /**
     * One Playbill has many pictures, the pictures will be uploaded using POST
     * method.
     *
     * @param pictureType
     * @param uploadedInputStream
     * @param fileDetail
     * @return Response
     * @throws java.io.IOException
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadPictures(@QueryParam("pictureType") PictureType pictureType,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {
        playbillEntity.removeImageAndDeleteOnFS(pictureType, 
                fileDetail.getFileName(),deployPath);
        
        String fileSystemPath = playbillEntity.buildFileSystemPath(deployPath, 
                fileDetail.getFileName());
        FileUtil.writeToFile(uploadedInputStream,  fileSystemPath);
        
        String webDisplayPath = playbillEntity.buildWebDisplayPath(fileDetail.getFileName());
        PlaybillImage playbillImage = new PlaybillImage(fileDetail.getFileName(),
                webDisplayPath, pictureType);
        playbillEntity.addImage(playbillImage);
        if (pictureType.equals(PictureType.SUBSIDIARY)) {
            String thumbnailFileName = FileUtil.generateCommonThumbnailFileName(fileDetail.getFileName());
            String thumbnailFilePath = playbillEntity.buildFileSystemPath(deployPath, thumbnailFileName);
            FileUtil.writeThumbnail(fileSystemPath, thumbnailFilePath);
            PlaybillImage playbillThumbnailImage = new PlaybillImage(thumbnailFileName,
                    playbillEntity.buildWebDisplayPath(thumbnailFileName), PictureType.SUBTHUMB);
            playbillEntity.addImage(playbillThumbnailImage);
        }
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(playbillEntity);
            }
        });

        String output = "Playbill " + playbillEntity.getPlaybillId() + " picture "
                + fileDetail.getFileName() + " is uploaded to : " + webDisplayPath;
        return Response.ok().entity(output).build();
    }
    
    @DELETE
    public void deleteImage(@QueryParam("imageName") String imageName) {
        PersistenceUtil.deleteImage(playbillEntity,PictureType.SUBSIDIARY,
                imageName,deployPath,em);
    }
}
