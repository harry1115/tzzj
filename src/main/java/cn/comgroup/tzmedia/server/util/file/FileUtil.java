/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.util.file;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;

/**
 * FileUtil
 *
 * @author pcnsh197
 */
public class FileUtil {
    
    public static final int BUFFER = 2048;
    
    private final static SecureRandom random = new SecureRandom();

    public static void writeToFile(InputStream uploadedInputStream,
            String uploadedFileLocation) {
        try {
            String filePath = uploadedFileLocation.substring(0,
                    uploadedFileLocation.lastIndexOf(File.separator));
            File parentFolder = new File(filePath);
            if (!parentFolder.exists()) {
                parentFolder.mkdirs();
            }
            OutputStream out = new FileOutputStream(new File(
                    uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];
            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public static void writeThumbnail(String fileSystemPath, String thumbnailFilePath) throws IOException {
        OutputStream os;
        try (InputStream is = new FileInputStream(fileSystemPath)) {
            os = new FileOutputStream(thumbnailFilePath);
            BufferedImage image = ImageIO.read(is);
            BufferedImage scaledBI = Scalr.resize(image, 200);
            String imageType = thumbnailFilePath.substring(thumbnailFilePath.lastIndexOf(".") + 1);
            ImageIO.write(scaledBI, imageType, os);
            os.close();
        }
    }
    
    public static String generateCommonThumbnailFileName(String originalName) {
        return originalName.replace(".", "_thumbnail.");
    }
    
    public static void deleteFile(String uploadedFileLocation) {
        File file = new File(uploadedFileLocation);
        if(file.exists()){
            file.delete();
        }
    }
    
    /**
     * There is a conflict for the file storage for System and web, as a
     * workaround, deployPath is a constant value specified in web.xml, and
     * pathForWeb is used for web client, so to service mobile client request,
     * we have to construct the real path based on the 2 paths mentioned.
     *
     * @param deployPath
     * @param pathForWeb
     * @return String
     */
    private static String getRealPathOfImage(String deployPath, String pathForWeb) {
        String realPortraitPath =deployPath+ pathForWeb;
        realPortraitPath = realPortraitPath.replace("/", File.separator);
        return realPortraitPath;
    }
    
    
    /**
     * Zip files in path.
     *
     * @param zipFileName the zip file name, Full path
     * @param filePath the file path, full path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public synchronized static void zipFilesInPath(final String zipFileName, final String filePath) throws IOException {
        FileOutputStream dest = null;
        ZipOutputStream out = null;
        try {
            byte[] data = new byte[BUFFER];
            final File zipFile = new File(zipFileName);
            zipFile.deleteOnExit();
            final File folder = new File(filePath);
            final List< String> files = Arrays.asList(folder.list());
            dest = new FileOutputStream(zipFileName);
            out = new ZipOutputStream(new BufferedOutputStream(dest));
            System.out.println("#########################: zipFilesInPath files.size: " + files.size());
            for (String file : files) {
                final FileInputStream fi = new FileInputStream(filePath + File.separator + file);
                final BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
                out.putNextEntry(new ZipEntry(file));
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
                fi.close();
            }
        } finally {
            if (out != null) {
                out.close();
            }
            if (dest != null) {
                dest.close();
            }
        }
    }
    
    public static String nextImageSuffix() {
        return new BigInteger(80, random).toString(10);
    }
    
    public static String generateImageName(String originalName) {
        return originalName.replace(originalName.substring(0, 
                originalName.lastIndexOf(".")+1), nextImageSuffix() + ".");
    }
}
