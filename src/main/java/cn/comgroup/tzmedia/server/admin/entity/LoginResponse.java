/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.admin.entity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author pcnsh197
 */
@XmlRootElement
public class LoginResponse {
    private String userId;
    private String sessionId;
    private String status;
    private AdminRole adminRole=AdminRole.NULL;
    private int shopId;

    public LoginResponse(String userId,String sessionId,String status){
        this.userId=userId;
        this.sessionId=sessionId;
        this.status=status;
    }
    
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStatus() {
        return status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AdminRole getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(AdminRole adminRole) {
        this.adminRole = adminRole;
    }

    /**
     * @return the shopId
     */
    public int getShopId() {
        return shopId;
    }

    /**
     * @param shopId the shopId to set
     */
    public void setShopId(int shopId) {
        this.shopId = shopId;
    }
}
