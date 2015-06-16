package cn.comgroup.tzmedia.server.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "PROMOTIONPRODUCTS")
@IdClass(PromotionProduct.ID.class)
@NamedQueries({
    @NamedQuery(name = "PromotionProduct.findCountByFreeProductNumber", 
            query = "SELECT count(pp) FROM PromotionProduct pp WHERE pp.freeProductNumber = :freeProductNumber"),
    @NamedQuery(name = "PromotionProduct.findByFreeProductNumber", 
            query = "SELECT pp FROM PromotionProduct pp WHERE pp.freeProductNumber = :freeProductNumber")
})
public class PromotionProduct implements Serializable {

    @Id
    @Column(name = "PRODUCTNUMBER", updatable = false, insertable = false)
    private String productNumber;

    @Id
    @Column(name = "FREEPRODUCTNUMBER", updatable = false, insertable = false)
    private String freeProductNumber;

    @Basic
    @Column(name = "PRODUCTNAME")
    private String productName;

    @Basic
    @Column(name = "FREEPRODUCTNAME")
    private String freeProductName;

    @Basic
    @Column(name = "FREEPRODUCTQUANTITY")
    private int freeProductQuantity;

    @ManyToOne
    @JoinColumn(name = "PRODUCTNUMBER")
    @JsonIgnore
    private PromotionDefinition owner;
   
   
    @ManyToOne
    @JoinColumn(name = "FREEPRODUCTNUMBER")
    private Product freeProduct;

    public PromotionProduct() {
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getFreeProductNumber() {
        return freeProductNumber;
    }

    public void setFreeProductNumber(String freeProductNumber) {
        this.freeProductNumber = freeProductNumber;
    }

    public String getFreeProductName() {
        return freeProductName;
    }

    public void setFreeProductName(String freeProductName) {
        this.freeProductName = freeProductName;
    }

    public int getFreeProductQuantity() {
        return freeProductQuantity;
    }

    public void setFreeProductQuantity(int freeProductQuantity) {
        this.freeProductQuantity = freeProductQuantity;
    }
    
    protected PromotionDefinition getOwner() {
        return owner;
    }

    protected void setOwner(PromotionDefinition promotionDefinition) {
        this.owner = promotionDefinition;
        if (promotionDefinition != null) {
            this.productNumber = promotionDefinition.getPromotionProductNumber();
            this.productName = promotionDefinition.getProductName();
        }
    }

    protected Product getFreeProduct() {
        return freeProduct;
    }

    public void setFreeProduct(Product freeProduct) {
        this.freeProduct = freeProduct;
        if (freeProduct != null) {
            this.freeProductNumber = freeProduct.getProductNumber();
            this.freeProductName = freeProduct.getProductName();
        }
    }

    public static class ID implements Serializable {

        private static final long serialVersionUID = 1L;
        public String productNumber;
        public String freeProductNumber;

        public ID() {
        }

        public ID(String productNumber, String freeProductNumber) {
            this.productNumber = productNumber;
            this.freeProductNumber = freeProductNumber;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof ID) {
                final ID otherID = (ID) other;
                return otherID.productNumber.equals(productNumber)
                        && otherID.freeProductNumber.equals(freeProductNumber);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

}