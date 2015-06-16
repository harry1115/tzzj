/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.shop.entity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author pcnsh197
 */
@XmlRootElement
public class GrabResponse {
    private int responseStatus;
    private int minutesToReactive;
    

    public GrabResponse(int responseStatus,int minutesToReactive){
        this.responseStatus=responseStatus;
        this.minutesToReactive=minutesToReactive;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public int getMinutesToReactive() {
        return minutesToReactive;
    }

    public void setMinutesToReactive(int minutesToReactive) {
        this.minutesToReactive = minutesToReactive;
    }
    
    
}
