package cn.comgroup.tzmedia.server.orderprocessing.entity;

import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.common.entity.PaymentTerm;
import cn.comgroup.tzmedia.server.shop.entity.Shop;
import cn.comgroup.tzmedia.server.util.calculator.DoubleUtil;
import cn.comgroup.tzmedia.server.util.jackson.CustomJsonDateSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ObjectTypeConverters;
import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
@Table(name = "CUSTOMERORDERS")
@ObjectTypeConverters({
    @ObjectTypeConverter(name = "orderStatusConverter", objectType = CustomerOrderStatus.class,
            dataType = String.class, conversionValues = {
                @ConversionValue(dataValue = "TOBEPAID", objectValue = "TOBEPAID"),
                @ConversionValue(dataValue = "TOBECOLLECTED", objectValue = "TOBECOLLECTED"),
                @ConversionValue(dataValue = "PAID", objectValue = "PAID"),
                @ConversionValue(dataValue = "PROCESSED", objectValue = "PROCESSED"),
                @ConversionValue(dataValue = "OVERTIME", objectValue = "OVERTIME"),
                @ConversionValue(dataValue = "CLOSED", objectValue = "CLOSED"),
                @ConversionValue(dataValue = "REFUNDED", objectValue = "REFUNDED")}),   
    
    @ObjectTypeConverter(name = "orderTypeConverter", objectType = OrderType.class,
            dataType = String.class, conversionValues = {
                @ConversionValue(dataValue = "NORMAL", objectValue = "NORMAL"),
                @ConversionValue(dataValue = "GRABSONG", objectValue = "GRABSONG")}),
            
    @ObjectTypeConverter(name = "paymentTermConverter", objectType = PaymentTerm.class,
            dataType = String.class, conversionValues = {
                @ConversionValue(dataValue = "NONE", objectValue = "NONE"),
                @ConversionValue(dataValue = "CASH", objectValue = "CASH"),
                @ConversionValue(dataValue = "ONLINE", objectValue = "ONLINE")})
})

@NamedQueries({
    @NamedQuery(name = "CustomerOrder.findAll", query = "SELECT o FROM CustomerOrder o"),
    @NamedQuery(name = "CustomerOrder.findByOrderNumber", query = "SELECT o FROM CustomerOrder o WHERE o.orderNumber = :orderNumber"),
    @NamedQuery(name = "CustomerOrder.findByUserId", query = "SELECT o FROM CustomerOrder o WHERE o.userId = :userId"),
    @NamedQuery(name = "CustomerOrder.findCountByUserId", query = "SELECT count(o) FROM CustomerOrder o WHERE o.userId = :userId"),
    @NamedQuery(name = "CustomerOrder.findByOrderDate", query = "SELECT o FROM CustomerOrder o WHERE o.orderDate >= :fromOrderDate AND o.orderDate <= :toOrderDate"),
    @NamedQuery(name = "CustomerOrder.findByOrderStatus", query = "SELECT o FROM CustomerOrder o WHERE o.orderStatus = :orderStatus"),
    @NamedQuery(name = "CustomerOrder.findByPlaybillId", query = "SELECT o FROM CustomerOrder o WHERE o.playbillId = :playbillId"),
    @NamedQuery(name = "CustomerOrder.findByPlaybillIdAndStatus", query = "SELECT o FROM CustomerOrder o WHERE o.playbillId = :playbillId AND o.orderStatus = :orderStatus"),
    @NamedQuery(name = "CustomerOrder.findByCouponNumber", query = "SELECT o FROM CustomerOrder o WHERE o.couponNumber = :couponNumber")
})
public class CustomerOrder implements Serializable {

    @Id
    @Column(name = "ORDERNUMBER", nullable = false)
    private String orderNumber;
    
    @Version
    private int version;

    @Basic
    @Column(name = "EXTERNALTRANSACTIONNUMBER")
    private String externalTransactionNumber;
    
    @Basic
    @Column(name = "SHOPID", updatable = false, insertable = false,nullable = false)
    private int shopId;
    
    @Transient
    private String shopName;

    @Basic
    @Column(name = "USERID", updatable = false, insertable = false,nullable = false)
    private String userId;

    @Transient
    private String userName;
    
    @Column(name = "TABLENUMBER")
    private int tableNumber;
    
    @Basic
    @Column(name = "PAYMENTTERM")
    @Convert("paymentTermConverter")
    private PaymentTerm paymentTerm = PaymentTerm.NONE; 
    
    @Temporal(DATE)
    @Column(name = "ORDERDATE")
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    private Calendar orderDate;
    
    @Basic
    @Column(name = "COMMENT")
    private String comment;
    
    @Transient
    private double orderAmount;
    
    @Temporal(TIMESTAMP)
    @Column(name = "ORDERTIME")
    private Calendar orderTime;
    
    @Column(name = "PLAYBILLID")
    private int playbillId;
    
    @Basic
    @Column(name = "PLAYBILLNAME")
    private String playbillName;
    
    @Column(name = "SINGERID")
    private int singerId;
    
    @Column(name = "SINGERNAME")
    private String singerName;

    @Basic
    @Column(name = "ORDERSTATUS")
    @Convert("orderStatusConverter")
    private CustomerOrderStatus orderStatus = CustomerOrderStatus.TOBEPAID;
    
    @Basic
    @Column(name = "ORDERTYPE")
    @Convert("orderTypeConverter")
    private OrderType orderType=OrderType.NORMAL;
    
    @Column(name = "COUPONNUMBER")
    private long couponNumber;
    
    @Basic
    @Column(name = "COUPONAMOUNT")
    private double couponAmount;

    @ManyToOne
    @JoinColumn(name = "SHOPID")
    @JsonIgnore
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "USERID")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    private List<CustomerOrderLine> customerOrderLines = new ArrayList<>();

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getExternalTransactionNumber() {
        return externalTransactionNumber;
    }

    public void setExternalTransactionNumber(String externalTransactionNumber) {
        this.externalTransactionNumber = externalTransactionNumber;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        if (user != null) {
            return user.getUserName();
        }
        return "";
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public PaymentTerm getPaymentTerm() {
        return paymentTerm;
    }

    public void setPaymentTerm(PaymentTerm paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    public double getOrderAmount() {
        orderAmount = 0.0D;
        for (CustomerOrderLine line : customerOrderLines) {
            orderAmount += line.getLineAmount();
        }
        orderAmount-=couponAmount;
        return DoubleUtil.round(orderAmount);
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Calendar getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Calendar orderDate) {
        this.orderDate = orderDate;
    }

    public Calendar getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Calendar orderTime) {
        this.orderTime = orderTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getPlaybillId() {
        return playbillId;
    }

    public void setPlaybillId(int playbillId) {
        this.playbillId = playbillId;
    }

    public int getSingerId() {
        return singerId;
    }

    public void setSingerId(int singerId) {
        this.singerId = singerId;
    }

    protected Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    protected User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CustomerOrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(CustomerOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }
    
    public List<CustomerOrderLine> getCustomerOrderLines() {
        return customerOrderLines;
    }

    public String getPlaybillName() {
        return playbillName;
    }

    public void setPlaybillName(String playbillName) {
        this.playbillName = playbillName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getShopName() {
        if(shop!=null){
            return shop.getShopName();
        }
        return "";
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public long getCouponNumber() {
        return couponNumber;
    }

    public void setCouponNumber(long couponNumber) {
        this.couponNumber = couponNumber;
    }

    public double getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(double couponAmount) {
        this.couponAmount = couponAmount;
    }

    public void setCustomerOrderLines(List<CustomerOrderLine> customerOrderLines) {
        this.customerOrderLines = customerOrderLines;
    }

    public CustomerOrderLine addCustomerOrderLine(CustomerOrderLine customerOrderLine) {
        customerOrderLine.setOwner(this);
        customerOrderLines.add(customerOrderLine);
        if(customerOrderLine.getPlaybillId()>0){
            this.playbillId=customerOrderLine.getPlaybillId();
        }
        return customerOrderLine;
    }

    public CustomerOrderLine removeCustomerOrderLine(CustomerOrderLine customerOrderLine) {
        customerOrderLines.remove(customerOrderLine);
        customerOrderLine.setOwner(null);
        return customerOrderLine;
    }

    /**
     * Returns a string representation of the object. This implementation
     * constructs that representation based on the id fields.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "CustomerOrder:orderNumber " + orderNumber;
    }
    
    /**
     *
     * PROCESSED CLOSED OVERTIME order can not modify
     * @param customerOrder
     * @return 
     */
    public boolean changeStatusValid(CustomerOrder customerOrder) {
        if (this.orderStatus.equals(CustomerOrderStatus.PROCESSED)
                || this.orderStatus.equals(CustomerOrderStatus.CLOSED)
                || this.orderStatus.equals(CustomerOrderStatus.OVERTIME)
                || this.orderStatus.equals(CustomerOrderStatus.REFUNDED)) {
            return false;
        }
        if (this.orderStatus.equals(CustomerOrderStatus.TOBEPAID)) {
            if (customerOrder.getOrderStatus().equals(CustomerOrderStatus.PROCESSED)) {
                return false;
            }
        }
        return true;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    
    
}
