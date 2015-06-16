package cn.comgroup.tzmedia.server.admin.entity;

import cn.comgroup.tzmedia.server.util.jackson.CustomJsonDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.DATE;
import javax.persistence.Version;

@Entity
@Table(name = "COUPONDEFINITIONS")
@NamedQueries({
    @NamedQuery(name = "CouponDefinition.findAll", query = "SELECT cd FROM CouponDefinition cd"),
    @NamedQuery(name = "CouponDefinition.findByCDId", query = "SELECT cd FROM CouponDefinition cd WHERE cd.couponDefinitionNumber = :couponDefinitionNumber"),
    @NamedQuery(name = "CouponDefinition.findValidCoupon", query = "SELECT cd FROM CouponDefinition cd  WHERE cd.expiryDate>= :expiryDate"),})
public class CouponDefinition implements Serializable {

    @Id
    @Column(name = "COUPONDEFINITIONNUMBER", nullable = false)
    @TableGenerator(name = "COUPONDEFINITION_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "COUPONDEFINITIONNUMBER_GEN",
            allocationSize = 1,
            initialValue = 1)
    @GeneratedValue(generator = "COUPONDEFINITION_GENERATOR")
    private long couponDefinitionNumber;
    
    @Version
    private int version;
    
    @Basic
    @Column(name = "COUPONNAME")
    private String couponName;
    
    @Basic
    @Column(name = "MEETVALUE")
    private double meetValue;
    
    @Basic
    @Column(name = "FACEVALUE")
    private double faceValue;
    
    @Basic
    @Column(name = "FORALLUSER")
    private boolean forAllUser;

    @Temporal(DATE)
    @Column(name = "EXPIRYDATE")
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    private Calendar expiryDate;


    public CouponDefinition() {
    }

    public CouponDefinition(String couponName, 
            double faceValue,Calendar expiryDate) {
        this.couponName = couponName;
        this.faceValue=faceValue;
        this.expiryDate = expiryDate;
    }

    public long getCouponDefinitionNumber() {
        return couponDefinitionNumber;
    }

    public void setCouponDefinitionNumber(long couponDefinitionNumber) {
        this.couponDefinitionNumber = couponDefinitionNumber;
    }

    public Calendar getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Calendar expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public double getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(double faceValue) {
        this.faceValue = faceValue;
    }

    public boolean isForAllUser() {
        return forAllUser;
    }

    public void setForAllUser(boolean forAllUser) {
        this.forAllUser = forAllUser;
    }

    public double getMeetValue() {
        return meetValue;
    }

    public void setMeetValue(double meetValue) {
        this.meetValue = meetValue;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
