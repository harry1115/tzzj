package cn.comgroup.tzmedia.server.shop.entity;

import cn.comgroup.tzmedia.server.common.entity.PictureHandleTemplate;
import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.common.entity.TZImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
@Table(name = "SHOPS")

@NamedQueries({
    @NamedQuery(name = "Shop.findAll", query = "SELECT s FROM Shop s order by s.ordering desc"),
    @NamedQuery(name = "Shop.findByShopId", query = "SELECT s FROM Shop s WHERE s.shopId = :shopId"),
    @NamedQuery(name = "Shop.findByO2o", query = "SELECT s FROM Shop s WHERE s.o2o = :o2o order by s.ordering desc"),
    @NamedQuery(name = "Shop.findMaxShopId", query = "SELECT max(s.shopId) FROM Shop s"),
})
public class Shop extends PictureHandleTemplate implements Serializable {

    @Id
    @Column(name = "SHOPID", nullable = false)
    @TableGenerator(name = "SHOP_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "SHOPID_GEN",
            allocationSize=1,
            initialValue = 1)
    @GeneratedValue(generator = "SHOP_GENERATOR")
    private int shopId;
    
    @Version
    private int version;

    @Basic
    @Column(name = "SHOPNAME")
    private String shopName;

    @Basic
    @Column(name = "SHOPDESC")
    @Lob
    private String shopDesc;
    
    @Basic
    @Column(name = "SHORTDESC")
    private String shortDesc;
    
    @Basic
    @Column(name = "ORDERING")
    private double ordering;
    
    @Basic
    @Column(name = "PORTRAITPATH")
    private String portraitPath;
    
    
    @Basic
    @Column(name = "ADDRESS")
    private String address;

    @Basic
    @Column(name = "PHONENUMBER1")
    private String phoneNumber1;
    
    
    @Basic
    @Column(name = "PHONENUMBER2")
    private String phoneNumber2;
    
    @Basic
    @Column(name = "O2O")
    private boolean o2o;
    
    @Basic
    @Column(name = "SONG")
    private boolean song;
    
    @Basic
    @Column(name = "PAY")
    private boolean pay;
    
    @Basic
    @Column(name = "SHARECONTENT")
    @Lob
    private String shareContent;
    
    @Basic
    @Column(name = "SHAREURL")
    private String shareUrl;
    
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    @OrderBy("creationDateTime DESC")
    private List<ShopImage> shopImages = new ArrayList<>();
    
    
    public Shop() {
    }

    public Shop(int shopId, String shopName, String shopDesc) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopDesc = shopDesc;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopDesc() {
        return shopDesc;
    }

    public void setShopDesc(String shopDesc) {
        this.shopDesc = shopDesc;
    }

    public double getOrdering() {
        return ordering;
    }

    public void setOrdering(double ordering) {
        this.ordering = ordering;
    }

    public String getPortraitPath() {
        return portraitPath;
    }

    public void setPortraitPath(String portraitPath) {
        this.portraitPath = portraitPath;
    }
    

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public boolean isO2o() {
        return o2o;
    }

    public void setO2o(boolean o2o) {
        this.o2o = o2o;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public List<ShopImage> getShopImages() {
        return shopImages;
    }

    public void setShopImages(List<ShopImage> shopImages) {
        this.shopImages = shopImages;
    }

    public ShopImage addImage(ShopImage shopImage) {
        shopImage.setOwner(this);
        shopImages.add(shopImage);
        return shopImage;
    }

    @Override
    public ShopImage removeImage(TZImage tzImage) {
        ShopImage shopImage=(ShopImage)tzImage;
        shopImages.remove(shopImage);
        shopImage.setOwner(null);
        return shopImage;
    }
    
    @Override
    public void removeAllImagesAndDeleteOnFS(String deployPath) {
        for (ShopImage si : new ArrayList<>(shopImages)) {
            removeImageAndDeleteOnFS(si.getPictureType(),
                    si.getImageName(), deployPath);
        }
    }
    
    
    public ShopImage getShopImage(PictureType pictureType, String imageName) {
        if (pictureType.equals(PictureType.SUBSIDIARY)
                ||pictureType.equals(PictureType.SUBTHUMB)) {
            for (ShopImage si : shopImages) {
                if (si.getImageName().equals(imageName)) {
                    return si;
                }
            }
        } else {
            for (ShopImage si : shopImages) {
                if (si.getPictureType().equals(pictureType)) {
                    return si;
                }
            }
        }
        return null;
    }
    
    /**
     * Returns a hash code value for the object. This implementation computes a
     * hash code value based on the id fields in this object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return new Integer(this.shopId).hashCode();
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
        if (!(object instanceof Shop)) {
            return false;
        }

        Shop other = (Shop) object;
        return this.shopId==other.shopId;
    }

    /**
     * Returns a string representation of the object. This implementation
     * constructs that representation based on the id fields.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Shop:shopId " + shopId;
    }

    @Override
    public String fetchCommonIdentifier() {
        return "shop";
    }

    @Override
    public String fetchUniqueIdentifier() {
        return String.valueOf(shopId);
    }
    
    @Override
    public TZImage findImage(PictureType pictureType, String imageName) {
        return getShopImage(pictureType, imageName);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * @return the pay
     */
    public boolean isPay() {
        return pay;
    }

    /**
     * @param pay the pay to set
     */
    public void setPay(boolean pay) {
        this.pay = pay;
    }

    /**
     * @return the song
     */
    public boolean isSong() {
        return song;
    }

    /**
     * @param song the song to set
     */
    public void setSong(boolean song) {
        this.song = song;
    }
    
    

}
