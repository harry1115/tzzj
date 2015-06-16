/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.util.tx;

import cn.comgroup.tzmedia.server.common.entity.PictureHandleTemplate;
import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.util.file.FileUtil;
import javax.persistence.EntityManager;

/**
 *
 * @author pcnsh197
 */
public class PersistenceUtil {
    
    public static void deleteImage(final PictureHandleTemplate entity,
            PictureType pictureType,
            String imageName,
            String deployPath,
            EntityManager em) {
        String thumbnailFileName = FileUtil
                .generateCommonThumbnailFileName(imageName);
        entity.removeImageAndDeleteOnFS(PictureType.SUBSIDIARY,
                imageName, deployPath);
         entity.removeImageAndDeleteOnFS(PictureType.SUBTHUMB,
                thumbnailFileName, deployPath);
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(entity);
            }
        });
    }
}
