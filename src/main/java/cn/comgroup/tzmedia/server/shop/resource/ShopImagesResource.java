/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.shop.resource;

import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.shop.entity.Shop;
import cn.comgroup.tzmedia.server.shop.entity.ShopImage;
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
 * ShopImagesResource
 *
 * @author pcnsh197
 */
public class ShopImagesResource {

    private final ServletConfig servletConfig;
    private final Shop shopEntity; // appropriate jpa user entity
    private final EntityManager em; // entity manager provided by parent resource
    private final String deployPath;

    public ShopImagesResource(EntityManager em, Shop shopEntity, ServletConfig servletConfig) {
        this.em = em;
        this.servletConfig = servletConfig;
        this.shopEntity = shopEntity;
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
        if (shopEntity == null) {
            throw new WebApplicationException(404);
        }

        ShopImage shopImage = shopEntity.getShopImage(pictureType, imageName);
        if (shopImage != null) {
            String fileSystemPath = shopEntity.buildFileSystemPath(deployPath, 
                shopImage.getImageName());
            File f = new File(fileSystemPath);
            if (f.exists()) {
                return Response.ok().entity(f).build();
            }
        }
        //HTTP code 204
        return Response.noContent().build();
    }

    /**
     * One Shop has many pictures, the pictures will be uploaded using POST
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
            @FormDataParam("file") FormDataContentDisposition fileDetail)throws IOException {
        if (PictureType.MAIN.equals(pictureType) || PictureType.PORTRAIT.equals(pictureType)) {
            shopEntity.removeImageAndDeleteOnFS(pictureType,
                    fileDetail.getFileName(), deployPath);
        }
        String fileName=FileUtil.generateImageName(fileDetail.getFileName());
        
        String fileSystemPath = shopEntity.buildFileSystemPath(deployPath, 
                fileName);
        FileUtil.writeToFile(uploadedInputStream, fileSystemPath);
        
        
        String webDisplayPath = shopEntity.buildWebDisplayPath(fileName);
        ShopImage shopImage = new ShopImage(fileName,
                webDisplayPath,pictureType);
        shopEntity.addImage(shopImage);
        
        if (pictureType.equals(PictureType.SUBSIDIARY)) {
            String thumbnailFileName = FileUtil.generateCommonThumbnailFileName(fileName);
            String thumbnailFilePath = shopEntity.buildFileSystemPath(deployPath, thumbnailFileName);
            FileUtil.writeThumbnail(fileSystemPath, thumbnailFilePath);
            ShopImage shopThumbnailImage = new ShopImage(thumbnailFileName,
                    shopEntity.buildWebDisplayPath(thumbnailFileName), PictureType.SUBTHUMB);
            shopEntity.addImage(shopThumbnailImage);
        }
        
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(shopEntity);
                em.getEntityManagerFactory().getCache().evictAll();
            }
        });

        String output = "Shop " + shopEntity.getShopId() + " picture "
                + fileName + " is uploaded to : " + webDisplayPath;
        return Response.ok().entity(output).build();
    }
    
    @DELETE
    public void deleteImage(@QueryParam("imageName") String imageName) {
        PersistenceUtil.deleteImage(shopEntity,PictureType.SUBSIDIARY,
                imageName,deployPath,em);
    }
}
