/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.singer.resource;

import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.singer.entity.Song;
import cn.comgroup.tzmedia.server.singer.entity.SongImage;
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
public class SongImagesResource {

    private final Song songEntity; // appropriate jpa Product entity
    private final EntityManager em; // entity manager provided by parent resource
    private final ServletConfig servletConfig;
    private final String deployPath;

    public SongImagesResource(EntityManager em, Song song, 
            ServletConfig servletConfig) {
        this.em = em;
        this.songEntity = song;
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
        if (songEntity == null) {
            throw new WebApplicationException(404);
        }

        SongImage songImage = songEntity.getSongImage(pictureType, imageName);
        if (songImage != null) {
            String fileSystemPath = songEntity.buildFileSystemPath(deployPath, 
                songImage.getImageName());
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
     * One Song has many pictures, the pictures will be uploaded using POST
     * method.
     *
     * @param pictureType
     * @param uploadedInputStream
     * @param fileDetail
     * @return Response
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadPictures(@QueryParam("pictureType") PictureType pictureType,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {
        songEntity.removeImageAndDeleteOnFS(pictureType, 
                fileDetail.getFileName(),deployPath);
        
        String fileSystemPath = songEntity.buildFileSystemPath(deployPath, 
                fileDetail.getFileName());
        FileUtil.writeToFile(uploadedInputStream, fileSystemPath);
        
        String webDisplayPath = songEntity.buildWebDisplayPath(fileDetail.getFileName());
        SongImage songImage = new SongImage(fileDetail.getFileName(),
                webDisplayPath, pictureType);
        songEntity.addImage(songImage);
        if (pictureType.equals(PictureType.SUBSIDIARY)) {
            String thumbnailFileName = FileUtil.generateCommonThumbnailFileName(fileDetail.getFileName());
            String thumbnailFilePath = songEntity.buildFileSystemPath(deployPath, thumbnailFileName);
            FileUtil.writeThumbnail(fileSystemPath, thumbnailFilePath);
            SongImage songThumbnailImage = new SongImage(thumbnailFileName,
                    songEntity.buildWebDisplayPath(thumbnailFileName), PictureType.SUBTHUMB);
            songEntity.addImage(songThumbnailImage);
        }
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(songEntity);
                em.getEntityManagerFactory().getCache().evictAll();
            }
        });
        
        String output = "Song "+songEntity.getSongId()+" picture "
                +fileDetail.getFileName()+" is uploaded to : " + webDisplayPath;
        return Response.ok().entity(output).build();
    }
    
    
    @DELETE
    public void deleteImage(@QueryParam("imageName") String imageName) {
        PersistenceUtil.deleteImage(songEntity,PictureType.SUBSIDIARY,
                imageName,deployPath,em);
    }
}
