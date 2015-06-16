/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.ticket;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "ticket") 
@NamedQueries({
    @NamedQuery(name = "Ticket.findAll", query = "SELECT t FROM Ticket t"),
    @NamedQuery(name = "Ticket.findByTicketid", query = "SELECT t FROM Ticket t WHERE t.ticketid = :ticketid"),
    @NamedQuery(name = "Ticket.findByName", query = "SELECT t FROM Ticket t WHERE t.name = :name"),
    @NamedQuery(name = "Ticket.findByRemark", query = "SELECT t FROM Ticket t WHERE t.remark = :remark"),
    @NamedQuery(name = "Ticket.findByCount", query = "SELECT t FROM Ticket t WHERE t.count = :count"),
    @NamedQuery(name = "Ticket.findByPrize", query = "SELECT t FROM Ticket t WHERE t.prize = :prize"),
    @NamedQuery(name = "Ticket.findByStarttime", query = "SELECT t FROM Ticket t WHERE t.starttime = :starttime"),
    @NamedQuery(name = "Ticket.findByEndtime", query = "SELECT t FROM Ticket t WHERE t.endtime = :endtime"),
    @NamedQuery(name = "Ticket.findByShopid", query = "SELECT t FROM Ticket t WHERE t.shopid = :shopid"),
    @NamedQuery(name = "Ticket.findByActivityid", query = "SELECT t FROM Ticket t WHERE t.activityid = :activityid")})
public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "TICKETID")
    private String ticketid;
    @Size(max = 100)
    @Column(name = "NAME")
    private String name;
    @Size(max = 255)
    @Column(name = "REMARK")
    private String remark;
    @Column(name = "COUNT")
    private Integer count;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PRIZE")
    private Float prize;
    @Column(name = "STARTTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date starttime;
    @Column(name = "ENDTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endtime;
    @Size(max = 20)
    @Column(name = "SHOPID")
    private String shopid;
    @Size(max = 45)
    @Column(name = "ACTIVITYID")
    private String activityid;

    public Ticket() {
    }

    public Ticket(String ticketid) {
        this.ticketid = ticketid;
    }

    public String getTicketid() {
        return ticketid;
    }

    public void setTicketid(String ticketid) {
        this.ticketid = ticketid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Float getPrize() {
        return prize;
    }

    public void setPrize(Float prize) {
        this.prize = prize;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getActivityid() {
        return activityid;
    }

    public void setActivityid(String activityid) {
        this.activityid = activityid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ticketid != null ? ticketid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ticket)) {
            return false;
        }
        Ticket other = (Ticket) object;
        if ((this.ticketid == null && other.ticketid != null) || (this.ticketid != null && !this.ticketid.equals(other.ticketid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.comgroup.tzmedia.server.ticket.Ticket[ ticketid=" + ticketid + " ]";
    }
    
}
