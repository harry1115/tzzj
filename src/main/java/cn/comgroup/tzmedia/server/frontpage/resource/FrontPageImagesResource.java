/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.frontpage.resource;

import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.frontpage.entity.FrontPage;
import cn.comgroup.tzmedia.server.frontpage.entity.FrontPageImage;
import cn.comgroup.tzmedia.server.util.file.FileUtil;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import cn.comgroup.tzmedia.server.util.tx.PersistenceUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
 * ProductImagesResource
 *
 * @author pcnsh197
 */
public class FrontPageImagesResource {

    private final FrontPage frontPageEntity; // appropriate jpa FrontPage entity
    private final EntityManager em; // entity manager provided by parent resource
    private final ServletConfig servletConfig;
    private final String deployPath;

    public FrontPageImagesResource(EntityManager em, FrontPage frontPageEntity,
            ServletConfig servletConfig) {
        this.em = em;
        this.frontPageEntity = frontPageEntity;
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
        if (frontPageEntity == null) {
            throw new WebApplicationException(404);
        }
        
        FrontPageImage frontPageImage = frontPageEntity.getFrontPageImage(pictureType, imageName);
        if (frontPageImage != null) {
            String fileSystemPath = frontPageEntity.buildFileSystemPath(deployPath, 
                frontPageImage.getImageName());
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
     * One FrontPage has many pictures, the pictures will be uploaded using POST
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
        
        if (PictureType.MAIN.equals(pictureType) || PictureType.PORTRAIT.equals(pictureType)) {
            frontPageEntity.removeImageAndDeleteOnFS(pictureType,
                    fileDetail.getFileName(), deployPath);
        }
        String fileName=FileUtil.generateImageName(fileDetail.getFileName());

        String fileSystemPath = frontPageEntity.buildFileSystemPath(deployPath, 
                fileName);
        FileUtil.writeToFile(uploadedInputStream, fileSystemPath);
        
        String webDisplayPath = frontPageEntity.buildWebDisplayPath(fileName);
        FrontPageImage frontPageImage = new FrontPageImage(fileName,
                webDisplayPath, pictureType);
        frontPageEntity.addImage(frontPageImage);
        if (pictureType.equals(PictureType.SUBSIDIARY)) {
            String thumbnailFileName = FileUtil.generateCommonThumbnailFileName(fileName);
            String thumbnailFilePath = frontPageEntity.buildFileSystemPath(deployPath, thumbnailFileName);
            FileUtil.writeThumbnail(fileSystemPath, thumbnailFilePath);
            FrontPageImage frontPageThumbnailImage = new FrontPageImage(thumbnailFileName,
                    frontPageEntity.buildWebDisplayPath(thumbnailFileName), PictureType.SUBTHUMB);
            frontPageEntity.addImage(frontPageThumbnailImage);
        }
        
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(frontPageEntity);
            }
        });

        String output = "FrontPage " + frontPageEntity.getFrontPageId() + " picture "
                + fileName + " is uploaded to : " + webDisplayPath;
        return Response.ok().entity(output).build();
    }
    
    @DELETE
    public void deleteImage(@QueryParam("imageName") String imageName) {
        PersistenceUtil.deleteImage(frontPageEntity,PictureType.SUBSIDIARY,
                imageName,deployPath,em);
    }
}
