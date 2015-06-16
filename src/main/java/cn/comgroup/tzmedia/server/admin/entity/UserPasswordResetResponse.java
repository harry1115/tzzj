/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.admin.entity;

/**
 * UserPasswordResetResponse
 *
 * @author pcnsh197
 */
public class UserPasswordResetResponse {

    private String status;

    public UserPasswordResetResponse() {

    }

    public UserPasswordResetResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
