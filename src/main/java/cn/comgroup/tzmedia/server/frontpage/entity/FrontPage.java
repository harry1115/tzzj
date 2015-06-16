package cn.comgroup.tzmedia.server.frontpage.entity;

import cn.comgroup.tzmedia.server.admin.entity.Administrator;
import cn.comgroup.tzmedia.server.common.entity.PictureHandleTemplate;
import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.common.entity.TZImage;
import cn.comgroup.tzmedia.server.shop.entity.Activity;
import cn.comgroup.tzmedia.server.shop.entity.Shop;
import cn.comgroup.tzmedia.server.singer.entity.Singer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.TIMESTAMP;
import javax.persistence.Version;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ObjectTypeConverters;
import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
@Table(name = "FRONTPAGES")
@ObjectTypeConverters({
    @ObjectTypeConverter(name = "frontPageType", objectType = FrontPageType.class,
            dataType = String.class, conversionValues = {
                @ConversionValue(dataValue = "SHOP", objectValue = "SHOP"),
                @ConversionValue(dataValue = "ACTIVITY", objectValue = "ACTIVITY"),
                @ConversionValue(dataValue = "SINGER", objectValue = "SINGER")
            })
})

@NamedQueries({
    @NamedQuery(name = "FrontPage.findAll", query = "SELECT fp FROM FrontPage fp order by fp.ordering desc"),
    @NamedQuery(name = "FrontPage.findByPush", query = "SELECT fp FROM FrontPage fp WHERE fp.push = :push order by fp.ordering desc"),
    @NamedQuery(name = "FrontPage.findBySingerId", query = "SELECT fp FROM FrontPage fp WHERE fp.singerId = :singerId"),
    @NamedQuery(name = "FrontPage.findByShopId", query = "SELECT fp FROM FrontPage fp WHERE fp.shopId = :shopId"),
    @NamedQuery(name = "FrontPage.findByActivityName", query = "SELECT fp FROM FrontPage fp WHERE fp.activityName = :activityName"),
})

public class FrontPage extends PictureHandleTemplate implements Serializable {

    @Id
    @Column(name = "FRONTPAGEID", nullable = false)
    @TableGenerator(name = "FRONTPAGE_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "FRONTPAGEID_GEN",
            allocationSize=1,
            initialValue = 1)
    @GeneratedValue(generator = "FRONTPAGE_GENERATOR")
    private int frontPageId;
    
    @Version
    private int version;

    @Basic
    @Column(name = "USERID", updatable = false, insertable = false)
    private String userId;
    
    @Basic
    @Column(name = "SHOPID", updatable = false, insertable = false)
    private int shopId;
    
    @Basic
    @Column(name = "ACTIVITYNAME",  updatable = false, insertable = false)
    private int activityName;
    
     @Basic
    @Column(name = "SINGERID",  updatable = false, insertable = false)
    private int singerId;

    @Basic
    @Column(name = "TITLE")
    private String title;

    @Basic
    @Column(name = "SUBTITLE")
    private String subtitle;
    
    @Basic
    @Column(name = "ORDERING")
    private double ordering;

    @Basic
    @Column(name = "TYPE")
    @Convert("frontPageType")
    private FrontPageType type=FrontPageType.ACTIVITY;
       

    @Temporal(TIMESTAMP)
    @Column(name = "OCCURREDDATE")
    private Calendar occurredDate;

    @Basic
    @Column(name = "CONTENT")
    @Lob
    private String content;
    
    @Basic
    @Column(name = "PUSH")
    private boolean push;

    @Basic
    @Column(name = "PORTRAITPATH")
    private String portraitPath;
    
    
    @Basic
    @Column(name = "SHARECONTENT")
    @Lob
    private String shareContent;
    
    @Basic
    @Column(name = "SHAREURL")
    private String shareUrl;
    

    @ManyToOne
    @JoinColumn(name = "USERID")
    @JsonIgnore
    private Administrator administrator;
    
    @ManyToOne
    @JoinColumn(name = "SHOPID")
    private Shop shop;
    
    @ManyToOne
    @JoinColumn(name = "ACTIVITYNAME")
    private Activity activity;
    
    @ManyToOne
    @JoinColumn(name = "SINGERID")
    private Singer singer;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    private List<FrontPageImage> frontPageImages = new ArrayList<>();

    public FrontPage() {
    }

    public FrontPage(int frontPageId, String userId, String title) {
        this.frontPageId = frontPageId;
        this.userId = userId;
        this.title = title;
    }

    public String getPortraitPath() {
        return portraitPath;
    }

    public void setPortraitPath(String portraitPath) {
        this.portraitPath = portraitPath;
    }

    public int getFrontPageId() {
        return frontPageId;
    }

    public void setFrontPageId(int frontPageId) {
        this.frontPageId = frontPageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public FrontPageType getType() {
        return type;
    }

    public void setType(FrontPageType type) {
        this.type = type;
    }


    public Calendar getOccurredDate() {
        return occurredDate;
    }

    public void setOccurredDate(Calendar occurredDate) {
        this.occurredDate = occurredDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
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
    
    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getActivityName() {
        return activityName;
    }

    public void setActivityName(int activityName) {
        this.activityName = activityName;
    }

    public double getOrdering() {
        return ordering;
    }

    public void setOrdering(double ordering) {
        this.ordering = ordering;
    }

    public Administrator getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Administrator administrator) {
        this.administrator = administrator;
    }

    public List<FrontPageImage> getFrontPageImages() {
        return frontPageImages;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public int getSingerId() {
        return singerId;
    }

    public void setSingerId(int singerId) {
        this.singerId = singerId;
    }

    public Singer getSinger() {
        return singer;
    }

    public void setSinger(Singer singer) {
        this.singer = singer;
    }

    public void setFrontPageImages(List<FrontPageImage> frontPageImages) {
        this.frontPageImages = frontPageImages;
    }

    public FrontPageImage addImage(FrontPageImage frontPageImage) {
        frontPageImage.setOwner(this);
        frontPageImages.add(frontPageImage);
        return frontPageImage;
    }
    
    @Override
    public void removeAllImagesAndDeleteOnFS(String deployPath) {
        for (FrontPageImage fi : new ArrayList<>(frontPageImages)) {
            removeImageAndDeleteOnFS(fi.getPictureType(),
                    fi.getImageName(), deployPath);
        }
    }

    @Override
    public FrontPageImage removeImage(TZImage tzImage) {
        FrontPageImage frontPageImage=(FrontPageImage)tzImage;
        frontPageImages.remove(frontPageImage);
        frontPageImage.setOwner(null);
        return frontPageImage;
    }
    
    
    public FrontPageImage getFrontPageImage(PictureType pictureType, String imageName) {
        if (pictureType.equals(PictureType.SUBSIDIARY)
                ||pictureType.equals(PictureType.SUBTHUMB)) {
            for (FrontPageImage fi : frontPageImages) {
                if (fi.getImageName().equals(imageName)) {
                    return fi;
                }
            }
        } else {
            for (FrontPageImage fi : frontPageImages) {
                if (fi.getPictureType().equals(pictureType)) {
                    return fi;
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
        return Integer.valueOf(frontPageId).hashCode();
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
        if (!(object instanceof FrontPage)) {
            return false;
        }

        FrontPage other = (FrontPage) object;
        return this.frontPageId == other.frontPageId;
    }

    /**
     * Returns a string representation of the object. This implementation
     * constructs that representation based on the id fields.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "FrontPage:frontPageId "+ frontPageId;
    }

    @Override
    public String fetchCommonIdentifier() {
        return "frontpage";
    }

    @Override
    public String fetchUniqueIdentifier() {
        return String.valueOf(frontPageId);
    }
    
    @Override
    public TZImage findImage(PictureType pictureType, String imageName) {
        return getFrontPageImage(pictureType, imageName);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    
    

}
