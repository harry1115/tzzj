/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.ticketLog;

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
@Table(name = "ticketlog")
@NamedQueries({
    @NamedQuery(name = "Ticketlog.findAll", query = "SELECT t FROM Ticketlog t"),
    @NamedQuery(name = "Ticketlog.findByTicketlogid", query = "SELECT t FROM Ticketlog t WHERE t.ticketlogid = :ticketlogid"),
    @NamedQuery(name = "Ticketlog.findByTicketid", query = "SELECT t FROM Ticketlog t WHERE t.ticketid = :ticketid"),
    @NamedQuery(name = "Ticketlog.findByHasused", query = "SELECT t FROM Ticketlog t WHERE t.hasused = :hasused"),
    @NamedQuery(name = "Ticketlog.findByUserid", query = "SELECT t FROM Ticketlog t WHERE t.userid = :userid"),
    @NamedQuery(name = "Ticketlog.findByBuytime", query = "SELECT t FROM Ticketlog t WHERE t.buytime = :buytime"),
    @NamedQuery(name = "Ticketlog.findByUsetime", query = "SELECT t FROM Ticketlog t WHERE t.usetime = :usetime")})
public class Ticketlog implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "TICKETLOGID")
    private String ticketlogid;
    @Size(max = 45)
    @Column(name = "TICKETID")
    private String ticketid;
    @Size(max = 255)
    @Column(name = "HASUSED")
    private String hasused;
    @Size(max = 20)
    @Column(name = "USERID")
    private String userid;
    @Column(name = "BUYTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date buytime;
    @Column(name = "USETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date usetime;

    public Ticketlog() {
    }

    public Ticketlog(String ticketlogid) {
        this.ticketlogid = ticketlogid;
    }

    public String getTicketlogid() {
        return ticketlogid;
    }

    public void setTicketlogid(String ticketlogid) {
        this.ticketlogid = ticketlogid;
    }

    public String getTicketid() {
        return ticketid;
    }

    public void setTicketid(String ticketid) {
        this.ticketid = ticketid;
    }

    public String getHasused() {
        return hasused;
    }

    public void setHasused(String hasused) {
        this.hasused = hasused;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Date getBuytime() {
        return buytime;
    }

    public void setBuytime(Date buytime) {
        this.buytime = buytime;
    }

    public Date getUsetime() {
        return usetime;
    }

    public void setUsetime(Date usetime) {
        this.usetime = usetime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ticketlogid != null ? ticketlogid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ticketlog)) {
            return false;
        }
        Ticketlog other = (Ticketlog) object;
        if ((this.ticketlogid == null && other.ticketlogid != null) || (this.ticketlogid != null && !this.ticketlogid.equals(other.ticketlogid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.comgroup.tzmedia.server.ticketLog.Ticketlog[ ticketlogid=" + ticketlogid + " ]";
    }
    
}
