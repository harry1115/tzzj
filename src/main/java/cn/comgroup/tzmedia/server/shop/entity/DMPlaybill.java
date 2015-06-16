/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.shop.entity;
import cn.comgroup.tzmedia.server.shop.resource.UserActionsResource;
import cn.comgroup.tzmedia.server.singer.entity.Singer;

import java.util.Calendar;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * PlaybillNotification
 *
 * @author pcnsh197
 */
public class DMPlaybill {

    private int playbillId;
    @Temporal(DATE)
    private Calendar playbillDate;
    @Temporal(TIMESTAMP)
    private Calendar fromTime;
    @Temporal(TIMESTAMP)
    private Calendar toTime;
    

    private String playbillName;
    
    private Singer singer;
    
    private UserAction[] action;
    
    public DMPlaybill(Playbill playbill,UserAction[] action) {
        this.playbillId = playbill.getPlaybillId();
        this.playbillDate = playbill.getPlaybillDate();
        this.fromTime = playbill.getFromTime();
        this.toTime = playbill.getToTime();
        this.singer = playbill.getSinger();
        this.playbillName = playbill.getPlaybillName();
        Long num = Long.parseLong("0");
        this.action = action;           
    }
    
    public DMPlaybill(){
        
    }

    public int getPlaybillId() {
        return playbillId;
    }

    public void setPlaybillId(int playbillId) {
        this.playbillId = playbillId;
    }

    public Calendar getPlaybillDate() {
        return playbillDate;
    }

    public void setPlaybillDate(Calendar playbillDate) {
        this.playbillDate = playbillDate;
    }

    public Calendar getFromTime() {
        return fromTime;
    }

    public void setFromTime(Calendar fromTime) {
        this.fromTime = fromTime;
    }

    public Calendar getToTime() {
        return toTime;
    }

    public void setToTime(Calendar toTime) {
        this.toTime = toTime;
    }

    /**
     * @return the singer
     */
    public Singer getSinger() {
        return singer;
    }

    /**
     * @param singer the singer to set
     */
    public void setSinger(Singer singer) {
        this.singer = singer;
    }

    /**
     * @return the action
     */
    public UserAction[] getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(UserAction[] action) {
        this.action = action;
    }

    /**
     * @return the playbillName
     */
    public String getPlaybillName() {
        return playbillName;
    }

    /**
     * @param playbillName the playbillName to set
     */
    public void setPlaybillName(String playbillName) {
        this.playbillName = playbillName;
    }

}
