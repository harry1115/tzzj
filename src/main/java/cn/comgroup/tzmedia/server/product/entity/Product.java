package cn.comgroup.tzmedia.server.product.entity;

import cn.comgroup.tzmedia.server.common.entity.PictureHandleTemplate;
import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.common.entity.TZImage;
import cn.comgroup.tzmedia.server.shop.entity.Shop;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ObjectTypeConverters;
import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
@Table(name = "PRODUCTS")

@ObjectTypeConverters({
    @ObjectTypeConverter(name = "productShowType", objectType = ProductShowType.class, dataType = String.class, conversionValues = {
        @ConversionValue(dataValue = "NORMAL", objectValue = "NORMAL"),
        @ConversionValue(dataValue = "SUPERSCRIPT", objectValue = "SUPERSCRIPT"),
        @ConversionValue(dataValue = "POPUP", objectValue = "POPUP")})
})
@NamedQueries({
    @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p order by p.productNumber"),
    @NamedQuery(name = "Product.findByProductNumber", query = "SELECT p FROM Product p WHERE p.productNumber = :productNumber order by p.productNumber"),
    @NamedQuery(name = "Product.findByProductName", query = "SELECT p FROM Product p WHERE p.productName = :productName order by p.productNumber"),
    @NamedQuery(name = "Product.findByShopAndType", query = "SELECT p FROM Product p WHERE p.shopId = :shopId AND p.typeId =:typeId order by p.ordering desc, p.productNumber"),})
public class Product extends PictureHandleTemplate implements Serializable {

    @Id
    @Column(name = "PRODUCTNUMBER", nullable = false)
    private String productNumber;

    @Version
    private int version;

    @Basic
    @Column(name = "SHOPID", updatable = false, insertable = false)
    private int shopId;

    @Basic
    @Column(name = "TYPEID", updatable = false, insertable = false)
    private int typeId;
    
    @Basic
    @Column(name = "PRESENT")
    private boolean present;

    @Basic
    @Column(name = "PRODUCTNAME")
    private String productName;

    @Basic
    @Column(name = "PRODUCTDESC")
    private String productDesc;

    @Basic
    @Column(name = "PRICE")
    private double price;

   

    @Basic
    @Column(name = "PROMOTION")
    private boolean promotion;

    @Basic
    @Column(name = "SELLABLE")
    private boolean sellable;

    @Basic
    @Column(name = "PORTRAITPATH")
    private String portraitPath;

    @Basic
    @Column(name = "ORDERING")
    private double ordering;

    @ManyToOne
    @JoinColumn(name = "SHOPID")
    @JsonIgnore
    private Shop shop;

    @Basic
    @Column(name = "PRODUCTSHOWTYPE")
    @Convert("productShowType")
    private ProductShowType productShowType = ProductShowType.NORMAL;
    
   

    @ManyToOne
    @JoinColumn(name = "TYPEID")
    @JsonIgnore
    private ProductType productType;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    private List<ProductImage> productImages = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PROMOTIONPRODUCTNUMBER")
    @JsonIgnore
    private PromotionDefinition promotionDefinition;

    public Product() {
    }

    public Product(String productNumber, int shopId, String productName) {
        this.productNumber = productNumber;
        this.shopId = shopId;
        this.productName = productName;
    }

    public String getPortraitPath() {
        return portraitPath;
    }

    public void setPortraitPath(String portraitPath) {
        this.portraitPath = portraitPath;
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

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
        if (productType != null) {
            this.typeId = productType.getTypeId();
        }
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isPromotion() {
        return promotion;
    }

    public void setPromotion(boolean promotion) {
        this.promotion = promotion;
    }

    public boolean isSellable() {
        return sellable;
    }

    public void setSellable(boolean sellable) {
        this.sellable = sellable;
    }

    public double getOrdering() {
        return ordering;
    }

    public void setOrdering(double ordering) {
        this.ordering = ordering;
    }

    public ProductShowType getProductShowType() {
        return productShowType;
    }

    public void setProductShowType(ProductShowType productShowType) {
        this.productShowType = productShowType;
    }

    public PromotionDefinition getPromotionDefinition() {
        return promotionDefinition;
    }

    public void setPromotionDefinition(PromotionDefinition promotionDefinition) {
        this.promotionDefinition = promotionDefinition;
    }

    public List<ProductImage> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImage> productImages) {
        this.productImages = productImages;
    }

    public ProductImage addImage(ProductImage productImage) {
        productImage.setOwner(this);
        productImages.add(productImage);
        return productImage;
    }

    @Override
    public TZImage removeImage(TZImage tzImage) {
        ProductImage productImage = (ProductImage) tzImage;
        productImages.remove(productImage);
        productImage.setOwner(null);
        return productImage;
    }

    @Override
    public void removeAllImagesAndDeleteOnFS(String deployPath) {
        for (ProductImage pi : new ArrayList<>(productImages)) {
            removeImageAndDeleteOnFS(pi.getPictureType(),
                    pi.getImageName(), deployPath);
        }
    }

    public ProductImage getProductImage(PictureType pictureType, String imageName) {
        if (pictureType.equals(PictureType.SUBSIDIARY)
                || pictureType.equals(PictureType.SUBTHUMB)) {
            for (ProductImage pi : productImages) {
                if (pi.getImageName().equals(imageName)) {
                    return pi;
                }
            }
        } else {
            for (ProductImage pi : productImages) {
                if (pi.getPictureType().equals(pictureType)) {
                    return pi;
                }
            }
        }
        return null;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    protected Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
        if (shop != null) {
            this.shopId = shop.getShopId();
        }
    }
       
    public void setPresent(boolean present) {
        this.present = present;
    }

    public boolean getPresent() {
        return present;
    }
    

    /**
     * Returns a hash code value for the object. This implementation computes a
     * hash code value based on the id fields in this object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.productNumber != null ? this.productNumber.hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this UserEntity. The result
     * is <code>true</code> if and only if the argument is not null and is a
     * UserEntity object that has the same id field values as this object.
     *
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @SuppressWarnings("StringEquality")
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Product)) {
            return false;
        }

        Product other = (Product) object;

        return !(this.productNumber != other.productNumber
                && (this.productNumber == null
                || !this.productNumber.equals(other.productNumber)));
    }

    /**
     * Returns a string representation of the object. This implementation
     * constructs that representation based on the id fields.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Product:productNumber " + productNumber;
    }

    @Override
    public String fetchCommonIdentifier() {
        return "product";
    }

    @Override
    public String fetchUniqueIdentifier() {
        return productNumber;
    }

    @Override
    public TZImage findImage(PictureType pictureType, String imageName) {
        return getProductImage(pictureType, imageName);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    

}
