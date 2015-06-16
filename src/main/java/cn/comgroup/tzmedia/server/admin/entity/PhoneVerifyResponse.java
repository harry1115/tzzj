/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.admin.entity;

/**
 * PhoneVerifyResponse
 *
 * @author pcnsh197
 */
public class PhoneVerifyResponse {

    private String status;
    private String providerResponse;
    private String verificationCode;

    public PhoneVerifyResponse() {
    }

    public PhoneVerifyResponse(String status, String providerResponse,
            String verificationCode) {
        this.status = status;
        this.providerResponse = providerResponse;
        this.verificationCode=verificationCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProviderResponse() {
        return providerResponse;
    }

    public void setProviderResponse(String providerResponse) {
        this.providerResponse = providerResponse;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
    
}
