package cn.comgroup.tzmedia.server.product.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ObjectTypeConverters;
import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
@Table(name = "PROMOTIONDEFINITIONS")

@ObjectTypeConverters({
    @ObjectTypeConverter(name = "promotionType", objectType = PromotionType.class,
            dataType = String.class, conversionValues = {
                @ConversionValue(dataValue = "BYQUANTITY", objectValue = "BYQUANTITY"),
                @ConversionValue(dataValue = "BYPRODUCT", objectValue = "BYPRODUCT"),
                @ConversionValue(dataValue = "BYAMOUNT", objectValue = "BYAMOUNT"),
                @ConversionValue(dataValue = "BYDISCOUNT", objectValue = "BYDISCOUNT")})
})
public class PromotionDefinition implements Serializable {

    @Id
    @Column(name = "PROMOTIONPRODUCTNUMBER", nullable = false)
    private String promotionProductNumber;
    
    @Basic
    @Column(name = "PRODUCTNAME")
    private String productName;
    
    @Basic
    @Column(name = "PROMOTIONTYPE")
    @Convert("promotionType")
    private PromotionType promotionType=PromotionType.BYPRODUCT;
    
    @Basic
    @Column(name = "MINIMALORDERQUANTITY")
    private int minimalOrderQuantity;
    
    @Basic
    @Column(name = "FREEPRODUCTQUANTITY")
    private int freeProductQuantity;
    
    @Basic
    @Column(name = "MINIMALORDERAMOUNT")
    private double minimalOrderAmount;
    
    @Basic
    @Column(name = "DISCOUNT")
    private double discount;
    
    @Basic
    @Column(name = "FIXPRICE")
    private double fixPrice;
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    private List<PromotionProduct> promotionProducts = new ArrayList<>();

    public PromotionDefinition() {
    }
    
    public PromotionDefinition(String promotionProductNumber,
            PromotionType promotionType) {
        this.promotionProductNumber = promotionProductNumber;
        this.promotionType = promotionType;
    }

    public String getPromotionProductNumber() {
        return promotionProductNumber;
    }

    public void setPromotionProductNumber(String promotionProductNumber) {
        this.promotionProductNumber = promotionProductNumber;
    }    

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public PromotionType getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(PromotionType promotionType) {
        this.promotionType = promotionType;
    }

    public int getMinimalOrderQuantity() {
        return minimalOrderQuantity;
    }

    public void setMinimalOrderQuantity(int minimalOrderQuantity) {
        this.minimalOrderQuantity = minimalOrderQuantity;
    }

    public int getFreeProductQuantity() {
        return freeProductQuantity;
    }

    public void setFreeProductQuantity(int freeProductQuantity) {
        this.freeProductQuantity = freeProductQuantity;
    }

    public double getMinimalOrderAmount() {
        return minimalOrderAmount;
    }

    public void setMinimalOrderAmount(double minimalOrderAmount) {
        this.minimalOrderAmount = minimalOrderAmount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getFixPrice() {
        return fixPrice;
    }

    public void setFixPrice(double fixPrice) {
        this.fixPrice = fixPrice;
    }

    public List<PromotionProduct> getPromotionProducts() {
        return promotionProducts;
    }

    public void setPromotionProducts(List<PromotionProduct> promotionProducts) {
        this.promotionProducts = promotionProducts;
    }
  
    
    public PromotionProduct addPromotionProduct(PromotionProduct promotionProduct) {
        promotionProduct.setOwner(this);
        promotionProducts.add(promotionProduct);
        return promotionProduct;
    }

    public PromotionProduct removePromotionProduct(PromotionProduct promotionProduct) {
        promotionProducts.remove(promotionProduct);
        promotionProduct.setOwner(null);
        return promotionProduct;
    }

}
