/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.common.entity;

import cn.comgroup.tzmedia.server.util.file.FileUtil;
import java.io.File;

/**
 * PictureHandleTemplate
 *
 * @author pcnsh197
 */
public abstract class PictureHandleTemplate {

    /**
     * Build the file path of the picture on the file system.
     * @param deployPath
     * @param fileName
     * @return String
     */
    public String buildFileSystemPath(String deployPath, String fileName) {
        return deployPath
                + "/images"
                + File.separator
                + fetchCommonIdentifier()
                + File.separator
                + fetchUniqueIdentifier()
                + File.separator
                + fileName;
    }

    /**
     * Build the path of the picture show on the web page.
     * @param fileName
     * @return String
     */
    public String buildWebDisplayPath(String fileName) {
        return "images/" 
                + fetchCommonIdentifier() 
                + "/" 
                + fetchUniqueIdentifier()
                + "/" 
                + fileName;
    }

    /**
     * Common folder name for example, product the common identifier is
     * 'product'.
     *
     * @return String
     */
    public abstract String fetchCommonIdentifier();

    /**
     * Unique folder name one class that has picture, for example ,product
     * 000001, the unique identifier is '000001'.
     *
     * @return String
     */
    public abstract String fetchUniqueIdentifier();
    
    
    /**
     * Remove the image object from the database.
     * @param image
     * @return TZImage
     */
    public abstract TZImage removeImage(TZImage image);
    
    public abstract void removeAllImagesAndDeleteOnFS(String deployPath);
    
    
    /**
     * Remove the image object from the database and delete the file on the file
     * system.
     *
     * @param pictureType
     * @param imageName
     * @param deployPath
     * @return TZImage
     */
    public TZImage removeImageAndDeleteOnFS(PictureType pictureType,
            String imageName, String deployPath) {
        TZImage image = findImage(pictureType, imageName);
        if (image != null) {
            String fileSystemPath = buildFileSystemPath(deployPath,
                    image.getImageName());
            FileUtil.deleteFile(fileSystemPath);
            return removeImage(image);
        } else {
            return null;
        }
    }
    
    /**
     * Find the image according to the picture type and image name
     *
     * @param pictureType
     * @param imageName
     * @return TZImage
     */
    public abstract TZImage findImage(PictureType pictureType, String imageName);

}
