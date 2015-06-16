/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.admin.util;

import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.admin.entity.UserType;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * UserUtil
 *
 * @author pcnsh197
 */
public class UserUtil {
    public static User findUserByKeyAndType(EntityManager em, String registerKey,
            String userType) {
        if (UserType.QQ.name().equals(userType)) {
            Query queryQ = em.createNamedQuery("User.findByQQ");
            queryQ.setParameter("qq", registerKey);
            if (!queryQ.getResultList().isEmpty()) {
                return (User) queryQ.getResultList().get(0);
            }
        } else if (UserType.WEIBO.name().equals(userType)) {
            Query queryQ = em.createNamedQuery("User.findByWeibo");
            queryQ.setParameter("weibo", registerKey);
            if (!queryQ.getResultList().isEmpty()) {
                return (User) queryQ.getResultList().get(0);
            }
        }else if (UserType.EMAIL.name().equals(userType)) {
            Query queryQ = em.createNamedQuery("User.findByEmail");
            queryQ.setParameter("email", registerKey);
            if (!queryQ.getResultList().isEmpty()) {
                return (User) queryQ.getResultList().get(0);
            }
        }else if (UserType.MOBILE.name().equals(userType)) {
            Query queryQ = em.createNamedQuery("User.findByPhoneNumber");
            queryQ.setParameter("phoneNumber", registerKey);
            if (!queryQ.getResultList().isEmpty()) {
                return (User) queryQ.getResultList().get(0);
            }
        }else{
            Query queryQ = em.createNamedQuery("User.findByUserId");
            queryQ.setParameter("userId", registerKey);
            if (!queryQ.getResultList().isEmpty()) {
                return (User) queryQ.getResultList().get(0);
            }
        }
        return null;
    }
}
