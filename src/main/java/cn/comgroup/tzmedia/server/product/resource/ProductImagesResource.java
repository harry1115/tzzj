/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.product.resource;

import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.product.entity.Product;
import cn.comgroup.tzmedia.server.product.entity.ProductImage;
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
 * ProductImagesResource
 *
 * @author pcnsh197
 */
public class ProductImagesResource {

    private final Product productEntity; // appropriate jpa Product entity
    private final EntityManager em; // entity manager provided by parent resource
    private final ServletConfig servletConfig;
    private final String deployPath;

    public ProductImagesResource(EntityManager em, Product productEntity,
            ServletConfig servletConfig) {
        this.em = em;
        this.productEntity = productEntity;
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
        if (productEntity == null) {
            throw new WebApplicationException(404);
        }
        ProductImage productImage = productEntity.getProductImage(pictureType, imageName);
        if (productImage != null) {
            String fileSystemPath = productEntity.buildFileSystemPath(deployPath, 
                productImage.getImageName());
            File f = new File(fileSystemPath);
            if (f.exists()) {
//                String mt = new MimetypesFileTypeMap().getContentType(f);
//                System.out.println("#########################:ActivityImagesResource mt: " + mt);
                return Response.ok().entity(f).build();
            }
        }
        //HTTP code 204
        return Response.noContent().build();
    }
    
    
    /**
     * One Product has many pictures, the pictures will be uploaded using POST
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
    public Response uploadPictures(
            @QueryParam("pictureType") PictureType pictureType,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {
        if (PictureType.MAIN.equals(pictureType)) {
            productEntity.removeImageAndDeleteOnFS(pictureType,
                    fileDetail.getFileName(), deployPath);
            productEntity.removeImageAndDeleteOnFS(PictureType.PORTRAIT,
                    fileDetail.getFileName(), deployPath);
        }
        String fileName = productEntity.getProductNumber()+
                fileDetail.getFileName().substring(fileDetail.getFileName().lastIndexOf("."));

        String fileSystemPath = productEntity.buildFileSystemPath(deployPath,
                fileName);
        FileUtil.writeToFile(uploadedInputStream, fileSystemPath);

        String webDisplayPath = productEntity.buildWebDisplayPath(fileName);
        ProductImage productImage = new ProductImage(fileName,
                webDisplayPath, pictureType);
        productEntity.addImage(productImage);
        
        //Add product image
//        Query query = em.createNamedQuery("ProductType.findByTypeId");
//        query.setParameter("typeId", productEntity.getTypeId());
//        final ProductType productType = (ProductType) query.getResultList().get(0);
//        if (pictureType.equals(PictureType.PORTRAIT)) {
//            InputStream is = new FileInputStream(fileSystemPath);
//            productType.removeImageAndDeleteOnFS(pictureType,
//                    fileName, deployPath);
//            String productTypefileSystemPath = productType.buildFileSystemPath(deployPath,
//                    fileName);
//            FileUtil.writeToFile(is, productTypefileSystemPath);
//            String productTypeWebDisplayPath = productType.buildWebDisplayPath(fileName);
//            ProductTypeImage productTypeImage = new ProductTypeImage(fileName,
//                    productTypeWebDisplayPath, pictureType);
//            productType.addImage(productTypeImage);
//        }

//        if (pictureType.equals(PictureType.SUBSIDIARY)) {
        String thumbnailFileName = FileUtil.generateCommonThumbnailFileName(fileName);
        String thumbnailFilePath = productEntity.buildFileSystemPath(deployPath, thumbnailFileName);
        FileUtil.writeThumbnail(fileSystemPath, thumbnailFilePath);
        ProductImage productThumbnailImage = new ProductImage(thumbnailFileName,
                productEntity.buildWebDisplayPath(thumbnailFileName), PictureType.PORTRAIT);
        productEntity.addImage(productThumbnailImage);
//        }
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(productEntity);
//                em.merge(productType);
            }
        });

        String output = "Product " + productEntity.getProductNumber() + " picture "
                + fileName + " is uploaded to : " + webDisplayPath;
        return Response.ok().entity(output).build();
    }
    
    
    @DELETE
    public void deleteImage(@QueryParam("imageName") String imageName) {
        PersistenceUtil.deleteImage(productEntity,PictureType.SUBSIDIARY,
                imageName,deployPath,em);
    }
}
