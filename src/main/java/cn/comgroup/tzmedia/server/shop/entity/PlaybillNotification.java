/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.shop.entity;

import java.util.Calendar;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * PlaybillNotification
 *
 * @author pcnsh197
 */
public class PlaybillNotification {

    private int playbillId;
    @Temporal(DATE)
    private Calendar playbillDate;
    @Temporal(TIMESTAMP)
    private Calendar fromTime;
    @Temporal(TIMESTAMP)
    private Calendar toTime;
    
    public PlaybillNotification(Playbill playbill) {
        this.playbillId = playbill.getPlaybillId();
        this.playbillDate = playbill.getPlaybillDate();
        this.fromTime = playbill.getFromTime();
        this.toTime = playbill.getToTime();
    }
    
    public PlaybillNotification(){
        
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

}
