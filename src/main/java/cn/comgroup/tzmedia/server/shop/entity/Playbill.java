 package cn.comgroup.tzmedia.server.shop.entity;

import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.common.entity.PictureHandleTemplate;
import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.common.entity.TZImage;
import cn.comgroup.tzmedia.server.singer.entity.Singer;
import cn.comgroup.tzmedia.server.util.jackson.CustomJsonDateSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ObjectTypeConverters;
import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
@Table(name = "PLAYBILLS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Playbill.findAll", query = "SELECT p FROM Playbill p order by p.playbillId"),
    @NamedQuery(name = "Playbill.findByPlaybillId", query = "SELECT p FROM Playbill p WHERE p.playbillId = :playbillId"),
    @NamedQuery(name = "Playbill.findByPlaybillDate", query = "SELECT p FROM Playbill p WHERE p.playbillDate = :playbillDate order by p.fromTime"),
    @NamedQuery(name = "Playbill.findByPlaybillName", query = "SELECT p FROM Playbill p WHERE p.playbillName = :playbillName order by p.playbillDate"),
    @NamedQuery(name = "Playbill.findBySingerId", query = "SELECT p FROM Playbill p WHERE p.singerId = :singerId order by p.playbillDate"),
})
@ObjectTypeConverters({
    @ObjectTypeConverter(name = "stateConverter", objectType = PlaybillState.class,
            dataType = String.class, conversionValues = {
                @ConversionValue(dataValue = "ACTIVE", objectValue = "ACTIVE"),
                @ConversionValue(dataValue = "INACTIVE", objectValue = "INACTIVE"),
                @ConversionValue(dataValue = "PAID", objectValue = "PAID")})
})
public class Playbill extends PictureHandleTemplate implements Serializable {

    @Id
    @Column(name = "PLAYBILLID", nullable = false)
    @TableGenerator(name = "PLAYBILL_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "PLAYBILLID_GEN",
            allocationSize=1,
            initialValue = 1)
    @GeneratedValue(generator = "PLAYBILL_GENERATOR")
    private int playbillId;
    
    @Version
    private int version;

    
    @Column(name = "SHOPID", updatable = false, insertable = false)
    private int shopId;
    
    
    @Column(name = "SINGERID", updatable = false, insertable = false)
    private int singerId;
    
    @Basic
    @Column(name = "USERID",updatable = false,insertable = false)
    private String userId;
    
    @Basic
    @Column(name = "PLAYBILLNAME")
    private String playbillName;
    
      @Basic
    @Column(name = "ACTIVITYID")
    private int activityId;
    
    @Basic
    @Column(name = "PLAYBILLSTATE")
    @Convert("stateConverter")
    private PlaybillState playbillState = PlaybillState.ACTIVE;
    
    
    @Temporal(DATE)
    @Column(name = "PLAYBILLDATE")
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    private Calendar playbillDate;

    @Temporal(TIMESTAMP)
    @Column(name = "FROMTIME")
    private Calendar fromTime;

    @Temporal(TIMESTAMP)
    @Column(name = "TOTIME")
    private Calendar toTime;
    
    @Basic
    @Column(name = "PORTRAITPATH")
    private String portraitPath;
    
    @ManyToOne
    @JoinColumn(name = "SHOPID")
    @JsonIgnore
    private Shop shop;
    
    @ManyToOne
    @JoinColumn(name = "SINGERID")
    @JsonIgnore
    private Singer singer;
    
    @ManyToOne
    @JoinColumn(name = "USERID")
    @JsonIgnore
    private User user;
    
    @Basic
    @Column(name = "NUMBEROFPRAISES")
    private int numberOfPraises=0;
    
    @Basic
    @Column(name = "NUMBEROFCOMMENTS")
    private int numberOfComments=0;
    
    private int numberOfOrders;
    
    @Basic
    @Column(name = "SHARECONTENT")
    @Lob
    private String shareContent;
    
    @Basic
    @Column(name = "SHAREURL")
    private String shareUrl;
    
    @Basic
    @Column(name = "CURRENTORDERS")
    private int currentOrders = 0;
    
    @Transient
    private boolean canGrab=false;
    
//    @Transient
//    private AtomicInteger checkGrabSong=new AtomicInteger(0);
    
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    private List<PlaybillImage> playbillImages = new ArrayList<>();
    
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    private List<PlaybillLine> playbillLines = new ArrayList<>();
    
    
    public Playbill() {
    }

    public Playbill(int playbillId, String playbillName) {
        this.playbillId = playbillId;
        this.playbillName = playbillName;
    }
    
    public int getCurrentOrders() {
        return currentOrders;
    }
    
    public void setCurrentOrders(int currentOrders) {
        this.currentOrders = currentOrders;
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

    public String getPortraitPath() {
        return portraitPath;
    }

    public void setPortraitPath(String portraitPath) {
        this.portraitPath = portraitPath;
    }

    public int getPlaybillId() {
        return playbillId;
    }

    public void setPlaybillId(int playbillId) {
        this.playbillId = playbillId;
    }

    public String getPlaybillName() {
        return playbillName;
    }

    public void setPlaybillName(String playbillName) {
        this.playbillName = playbillName;
    }
    

    public int getSingerId() {
        return singerId;
    }

    public PlaybillState getPlaybillState() {
        return playbillState;
    }

    public void setPlaybillState(PlaybillState playbillState) {
        this.playbillState = playbillState;
    }

    public void setSingerId(int singerId) {
        this.singerId = singerId;
    }

    public Calendar getPlaybillDate() {
        return playbillDate;
    }

    public void setPlaybillDate(Calendar playbillDate) {
        this.playbillDate = playbillDate;
    }

    public Calendar getFromTime() {
        return fromTime;
    }

    public void setFromTime(Calendar fromTime) {
        this.fromTime = fromTime;
    }

    public Calendar getToTime() {
        return toTime;
    }

    public void setToTime(Calendar toTime) {
        this.toTime = toTime;
    }

    public int getNumberOfPraises() {
        return numberOfPraises;
    }

    public void setNumberOfPraises(int numberOfPraises) {
        this.numberOfPraises = numberOfPraises;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }
    
    public void increasePraise(){
        numberOfPraises++;
    }
    
    public void increaseComment(){
        numberOfComments++;
    }

    public List<PlaybillImage> getPlaybillImages() {
        return playbillImages;
    }

    public void setPlaybillImages(List<PlaybillImage> playbillImages) {
        this.playbillImages = playbillImages;
    }
  
    public PlaybillImage addImage(PlaybillImage playbillImage) {
        playbillImage.setOwner(this);
        playbillImages.add(playbillImage);
        return playbillImage;
    }

    @Override
    public PlaybillImage removeImage(TZImage tzImage) {
        PlaybillImage playbillImage=(PlaybillImage)tzImage;
        playbillImages.remove(playbillImage);
        playbillImage.setOwner(null);
        return playbillImage;
    }
    
    @Override
    public void removeAllImagesAndDeleteOnFS(String deployPath) {
        for (PlaybillImage pi : new ArrayList<>(playbillImages)) {
            removeImageAndDeleteOnFS(pi.getPictureType(),
                    pi.getImageName(), deployPath);
        }
    }
    
    public PlaybillImage getPlaybillImage(PictureType pictureType, String imageName) {
        if (pictureType.equals(PictureType.SUBSIDIARY)
                ||pictureType.equals(PictureType.SUBTHUMB)) {
            for (PlaybillImage pi : playbillImages) {
                if (pi.getImageName().equals(imageName)) {
                    return pi;
                }
            }
        } else {
            for (PlaybillImage pi : playbillImages) {
                if (pi.getPictureType().equals(pictureType)) {
                    return pi;
                }
            }
        }
        return null;
    }
    
    public List<PlaybillLine> getPlaybillLines() {
        return playbillLines;
    }
    
    public PlaybillLine getPlaybillLine(int lineNumber) {
        for (PlaybillLine pl : playbillLines) {
            if (pl.getLineNumber() == lineNumber) {
                return pl;
            }
        }
        return null;
    }

    public void setPlaybillLines(List<PlaybillLine> playbillLines) {
        this.playbillLines = playbillLines;
    }
    
    public PlaybillLine addPlaybillLine(PlaybillLine playbillLine) {
        playbillLine.setOwner(this);
        if (playbillLine.getLineNumber() == 0) {
            playbillLine.setLineNumber(getNextLineNumber());
        }
        playbillLines.add(playbillLine);
        return playbillLine;
    }

    private int getNextLineNumber() {
        int nextNumber = 0;
        for (PlaybillLine playbillLine : playbillLines) {
            if (playbillLine.getLineNumber() > nextNumber) {
                nextNumber = playbillLine.getLineNumber();
            }
        }
        return nextNumber + 1;
    }

    public PlaybillLine removePlaybillLine(PlaybillLine playbillLine) {
        playbillLines.remove(playbillLine);
        playbillLine.setOwner(null);
        return playbillLine;
    }
    
    public synchronized boolean reactiveSong() {
        if (playbillState.equals(PlaybillState.INACTIVE)) {
            playbillState = PlaybillState.ACTIVE;
            this.setUser(null);
            Logger.getLogger(Playbill.class.getName())
                    .log(Level.INFO, "Reactive the Playbill: {0}",
                            playbillId);
            return true;
        }
        return false;
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
    }

    public Singer getSinger() {
        return singer;
    }

    public void setSinger(Singer singer) {
        this.singer = singer;
    }

    public int getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(int numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }
    
//    public synchronized boolean grabSong(UserAction userAction) {
//        String[] logParam = {String.valueOf(playbillId),
//            String.valueOf(checkGrabSong.incrementAndGet()),playbillState.toString(),userId};
//        Logger.getLogger(Playbill.class.getName())
//                .log(Level.INFO, 
//                        "Grap song  of playbill {0} is {1} time called, playbill state is {2}, user is {3}",
//                        logParam);
//        if (playbillState.equals(PlaybillState.PAID)
//                || (playbillState.equals(PlaybillState.CHOSEN)
//                && !userId.equals(userAction.getUserId()))) {
//            return false;
//        } else {
//            playbillState = PlaybillState.CHOSEN;
//            this.setUser(userAction.getUser());
//            return true;
//        }
//    }

    public synchronized boolean paySong(UserAction userAction, Playbill playbill) {

        if (playbillState.equals(PlaybillState.ACTIVE)){
            if(playbill.getCurrentOrders() >= 4) {
                playbillState = PlaybillState.PAID;
//                return true;
            }
//                && userId != null
//                && userId.equals(userAction.getUserId())) {

        }
            return true;
        }
    
    
    /**
     * Returns a hash code value for the object. This implementation computes a
     * hash code value based on the id fields in this object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return new Integer(playbillId).hashCode();
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
        if (!(object instanceof Playbill)) {
            return false;
        }

        Playbill other = (Playbill) object;
        return this.playbillId == other.playbillId;
    }

    /**
     * Returns a string representation of the object. This implementation
     * constructs that representation based on the id fields.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Playbill:playbillId "+playbillId;
    }

    @Override
    public String fetchCommonIdentifier() {
        return "playbill";
    }

    @Override
    public String fetchUniqueIdentifier() {
        return String.valueOf(playbillId);
    }
    
    @Override
    public TZImage findImage(PictureType pictureType, String imageName) {
        return getPlaybillImage(pictureType, imageName);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public User getUser() {
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

    public boolean isCanGrab() {
        return canGrab;
    }

    public void setCanGrab(boolean canGrab) {
        this.canGrab = canGrab;
    }

//    public AtomicInteger getCheckGrabSong() {
//        return checkGrabSong;
//    }
//
//    public void setCheckGrabSong(AtomicInteger checkGrabSong) {
//        this.checkGrabSong = checkGrabSong;
//    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * @return the activityId
     */
    public int getActivityId() {
        return activityId;
    }

    /**
     * @param activityId the activityId to set
     */
    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

 
    
}
