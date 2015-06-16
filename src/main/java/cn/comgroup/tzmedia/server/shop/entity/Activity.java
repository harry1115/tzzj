package cn.comgroup.tzmedia.server.shop.entity;

import cn.comgroup.tzmedia.server.common.entity.PictureHandleTemplate;
import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.common.entity.TZImage;
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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.DATE;
import javax.persistence.Version;
import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
@Table(name = "ACTIVITIES")

@NamedQueries({
    @NamedQuery(name = "Activity.findAll", query = "SELECT a FROM Activity a order by a.ordering desc, a.fromDate"),
    @NamedQuery(name = "Activity.findByActivityName", query = "SELECT a FROM Activity a WHERE a.activityName = :activityName order by a.ordering desc, a.fromDate"),
    @NamedQuery(name = "Activity.findByActivitySubject", query = "SELECT a FROM Activity a WHERE a.activitySubject = :activitySubject order by a.ordering desc, a.fromDate"),
    @NamedQuery(name = "Activity.findByDate", query = "SELECT a FROM Activity a WHERE a.fromDate <= :date and a.toDate>= :date order by a.ordering desc , a.fromDate"),
    @NamedQuery(name = "Activity.findByShopId", query = "SELECT a FROM Activity a WHERE a.shopId = :shopId order by a.ordering desc, a.fromDate"),})
public class Activity extends PictureHandleTemplate implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "ACTIVITYNAME", updatable = false)
    @TableGenerator(name = "ACTIVITY_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "ACTIVITYNAME_GEN",
            allocationSize=1,
            initialValue = 1)
    @GeneratedValue(generator = "ACTIVITY_GENERATOR")
    private int activityName;
    
    @Version
    private int version;
    
    
    @Column(name = "SHOPID", updatable = false, insertable = false)
    private int shopId;

    @Basic
    @Column(name = "ACTIVITYSUBJECT")
    private String activitySubject;
    
    @Basic
    @Column(name = "ACTIVITYTYPE")
    private String activityType;
    
    @Basic
    @Column(name = "ACTIVITYDESC")
    @Lob
    private String activityDesc;
    
    @Basic
    @Column(name = "SHORTDESC")
    private String shortDesc;
    
    @Basic
    @Column(name = "ORDERING")
    private double ordering;
    
    @Temporal(DATE)
    @Column(name = "FROMDATE")
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    private Calendar fromDate;

    @Temporal(DATE)
    @Column(name = "TODATE")
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    private Calendar toDate;

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
    @JoinColumn(name = "SHOPID")
    @JsonIgnore
    private Shop owner;
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    @OrderBy("creationDateTime DESC")
    private List<ActivityImage> activityImages = new ArrayList<>();
    
    public Activity() {
    }

    public Activity(int shopId, int activityName) {
        this.shopId = shopId;
        this.activityName = activityName;
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

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivitySubject() {
        return activitySubject;
    }

    public void setActivitySubject(String activitySubject) {
        this.activitySubject = activitySubject;
    }
    

    public String getActivityDesc() {
        return activityDesc;
    }

    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }
    

    public double getOrdering() {
        return ordering;
    }

    public void setOrdering(double ordering) {
        this.ordering = ordering;
    }

    public Calendar getFromDate() {
        return fromDate;
    }

    public void setFromDate(Calendar fromDate) {
        this.fromDate = fromDate;
    }
    
    public Calendar getToDate() {
        return toDate;
    }

    public String getPortraitPath() {
        return portraitPath;
    }

    public void setPortraitPath(String portraitPath) {
        this.portraitPath = portraitPath;
    }

    public void setToDate(Calendar toDate) {
        this.toDate = toDate;
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
    
    
    public Shop getOwner() { 
        return owner;
    }

    public void setOwner(Shop owner) {
        this.owner = owner;
    }

    public List<ActivityImage> getActivityImages() {
        return activityImages;
    }

    public void setActivityImages(List<ActivityImage> activityImages) {
        this.activityImages = activityImages;
    }

    public ActivityImage addImage(ActivityImage activityImage) {
        activityImage.setOwner(this);
        activityImages.add(activityImage);
        return activityImage;
    }

    @Override
    public ActivityImage removeImage(TZImage tzImage) {
        ActivityImage activityImage=(ActivityImage)tzImage;
        activityImages.remove(activityImage);
        activityImage.setOwner(null);
        return activityImage;
    }
     
    @Override
    public void removeAllImagesAndDeleteOnFS(String deployPath) {
        for (ActivityImage ai : new ArrayList<>(activityImages)) {
            removeImageAndDeleteOnFS(ai.getPictureType(),
                    ai.getImageName(), deployPath);
        }
    }
    
    public ActivityImage getActivityImage(PictureType pictureType, String imageName) {
        if (pictureType.equals(PictureType.SUBSIDIARY)
                ||pictureType.equals(PictureType.SUBTHUMB)) {
            for (ActivityImage ai : activityImages) {
                if (ai.getImageName().equals(imageName)) {
                    return ai;
                }
            }
        } else {
            for (ActivityImage ai : activityImages) {
                if (ai.getPictureType().equals(pictureType)) {
                    return ai;
                }
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "Activity:activityName " + activityName + " shopId " + shopId;
    }

    @Override
    public String fetchCommonIdentifier() {
        return "activity";
    }

    @Override
    public String fetchUniqueIdentifier() {
        return String.valueOf(activityName);
    }
    
    @Override
    public TZImage findImage(PictureType pictureType, String imageName) {
        return getActivityImage(pictureType, imageName);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
   
}
