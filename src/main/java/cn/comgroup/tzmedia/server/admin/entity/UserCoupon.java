package cn.comgroup.tzmedia.server.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ObjectTypeConverters;

@Entity
@Table(name = "USERCOUPONS")
@NamedQueries({
    @NamedQuery(name = "UserCoupon.findAll", 
            query = "SELECT uc FROM UserCoupon uc"),
    @NamedQuery(name = "UserCoupon.findByUserId", 
            query = "SELECT uc FROM UserCoupon uc WHERE uc.userId = :userId"),
    @NamedQuery(name = "UserCoupon.findCountByCDNumberAndStatus",
            query = "SELECT count(uc) FROM UserCoupon uc WHERE uc.couponDefinitionNumber = :couponDefinitionNumber and uc.couponStatus = :couponStatus"),
    @NamedQuery(name = "UserCoupon.findByUserIdAndCDNumber",
            query = "SELECT uc FROM UserCoupon uc WHERE uc.userId = :userId and uc.couponDefinitionNumber = :couponDefinitionNumber"),
    @NamedQuery(name = "UserCoupon.findByDate", 
            query = "SELECT uc FROM UserCoupon uc, CouponDefinition cd WHERE "
            + "uc.couponDefinitionNumber=cd.couponDefinitionNumber and  cd.expiryDate>= :expiryDate And  cd.expiryDate <= :expiryDate"),})

@ObjectTypeConverters({
    @ObjectTypeConverter(name = "couponStatus", objectType = CouponStatus.class, dataType = String.class, conversionValues = {
        @ConversionValue(dataValue = "NEW", objectValue = "NEW"),
        @ConversionValue(dataValue = "USED", objectValue = "USED"),
        @ConversionValue(dataValue = "EXPIRED", objectValue = "EXPIRED")})
})
public class UserCoupon implements Serializable {

    @Id
    @Column(name = "COUPONNUMBER", nullable = false)
    @TableGenerator(name = "COUPON_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "COUPONNUMBER_GEN",
            allocationSize = 1,
            initialValue = 1)
    @GeneratedValue(generator = "COUPON_GENERATOR")
    private long couponNumber;

    @Basic
    @Column(name = "USERID", updatable = false, insertable = false,nullable = false)
    private String userId;

    @Column(name = "COUPONDEFINITIONNUMBER", updatable = false, insertable = false,nullable = false)
    private long couponDefinitionNumber;

    @Basic
    @Column(name = "COUPONSTATUS")
    @Convert("couponStatus")
    private CouponStatus couponStatus = CouponStatus.NEW;

//    private String couponName;
//
//    private double faceValue;
//
//    @Temporal(DATE)
//    private Calendar expiryDate;

    @ManyToOne
    @JoinColumn(name = "USERID")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "COUPONDEFINITIONNUMBER")
    @JsonIgnore
    private CouponDefinition couponDefinition;

    public UserCoupon() {
    }

    public UserCoupon(String userId) {
        this.userId = userId;
    }

    public long getCouponNumber() {
        return couponNumber;
    }

    public void setCouponNumber(long couponNumber) {
        this.couponNumber = couponNumber;
    }

//    @Temporal(DATE)
    public Calendar getExpiryDate() {
        if (couponDefinition != null) {
            return couponDefinition.getExpiryDate();
        }
        return null;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CouponStatus getCouponStatus() {
        return couponStatus;
    }

    public void setCouponStatus(CouponStatus couponStatus) {
        this.couponStatus = couponStatus;
    }

    public String getCouponName() {
        if (couponDefinition != null) {
            return couponDefinition.getCouponName();
        }
        return "";
    }

    public double getFaceValue() {
        if (couponDefinition != null) {
            return couponDefinition.getFaceValue();
        }
        return 0.0D;
    }
    
    public double getMeetValue() {
        if (couponDefinition != null) {
            return couponDefinition.getMeetValue();
        }
        return 0.0D;
    }

    public long getCouponDefinitionNumber() {
        return couponDefinitionNumber;
    }

    public void setCouponDefinitionNumber(long couponDefinitionNumber) {
        this.couponDefinitionNumber = couponDefinitionNumber;
    }

    public CouponDefinition getCouponDefinition() {
        return couponDefinition;
    }

    public void setCouponDefinition(CouponDefinition couponDefinition) {
        this.couponDefinition = couponDefinition;
    }

    protected User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getUserId();
        } else {
            this.userId = null;
        }
    }

}
