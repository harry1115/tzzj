package cn.comgroup.tzmedia.server.singer.entity;

import cn.comgroup.tzmedia.server.admin.entity.Gender;
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
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ObjectTypeConverters;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Entity class Singer.
 *
 * @author peter.liu@comgroup.cn
 */
@Entity
@Table(name = "SINGERS")
@NamedQueries({
    @NamedQuery(name = "Singer.findAll", query = "SELECT s FROM Singer s WHERE s.ordering > 0  order by s.ordering desc, s.singerId"),
    @NamedQuery(name = "Singer.findBySingerId", query = "SELECT s FROM Singer s WHERE  s.ordering > 0 and s.singerId = :singerId order by s.singerId"),
    @NamedQuery(name = "Singer.findBySingerName", query = "SELECT s FROM Singer s WHERE s.ordering > 0 and s.singerName = :singerName order by s.ordering desc, s.singerId")
})

@ObjectTypeConverters({
    @ObjectTypeConverter(name = "gender", objectType = Gender.class, dataType = String.class, conversionValues = {
        @ConversionValue(dataValue = "M", objectValue = "M"),
        @ConversionValue(dataValue = "F", objectValue = "F")}),
    @ObjectTypeConverter(name = "badgeTypsConvert", objectType = BadgeType.class, dataType = String.class, conversionValues = {
        @ConversionValue(dataValue = "NORMAL", objectValue = "NORMAL"),
        @ConversionValue(dataValue = "FAMOUS", objectValue = "FAMOUS")})
})

@XmlRootElement
public class Singer extends PictureHandleTemplate implements Serializable {

    @Id
    @Column(name = "SINGERID", nullable = false)
    @TableGenerator(name = "SINGER_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "SINGERID_GEN",
            allocationSize=1,
            initialValue = 1)
    @GeneratedValue(generator = "SINGER_GENERATOR")
    private int singerId;
    
    @Version
    private int version;

    @Column(name = "SINGERNAME", nullable = false)
    private String singerName;
    
    @Basic
    @Column(name = "ENGLISHNAME")
    private String englishName;

    @Basic
    @Column(name = "SINGERDESC")
    @Lob
    private String singerDesc;
    
    @Basic
    @Column(name = "SHORTDESC")
    private String shortDesc;
    
    @Basic
    @Column(name = "ORDERING")
    private double ordering;
    
    
    @Basic
    @Column(name = "SPECIALITY")
    @Lob
    //嘟囔爆料
    private String speciality;
    
    @Basic
    @Column(name = "SIGNATURE")
    @Lob
    //个性签名
    private String signature;
    
    @Basic
    @Column(name = "STYLE")
    private String style;
    
    
    @Basic
    @Column(name = "ADDRESS")
    private String address;
    
    @Basic
    @Column(name = "GENDER")
    @Convert("gender")
    private Gender gender = Gender.M;

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
    
    
    @Basic
    @Column(name = "BANDSMAN")
    private boolean bandsman;
    
    @Basic
    @Column(name = "BADGETYPE")
    @Convert("badgeTypsConvert")
    private BadgeType badgeType=BadgeType.NORMAL;
    
    
    @Basic
    @Column(name = "EXPERIENCE")
    @Lob
    private String experience;
    
    @Basic
    @Column(name = "WORKS")
    @Lob
    private String works;
    

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    @OrderBy("creationDateTime DESC")
    private List<SingerImage> singerImages = new ArrayList<>();
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    private List<SingerOwnedSong> singerOwnedSongs = new ArrayList<>();

    /**
     * Creates a new instance of UserEntity
     */
    public Singer() {
    }

    /**
     * Creates a new instance of UserEntity with the specified values.
     *
     * @param singerId the singerId of the Singer
     */
    public Singer(int singerId) {
        this.singerId = singerId;
    }

    /**
     * Creates a new instance of UserEntity with the specified values.
     *
     * @param singerId the singerId of the Singer
     * @param singerName the singerName of the Singer
     */
    public Singer(int singerId, String singerName) {
        this.singerId = singerId;
        this.singerName = singerName;
    }

    public int getSingerId() {
        return singerId;
    }

    public void setSingerId(int singerId) {
        this.singerId = singerId;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getSingerDesc() {
        return singerDesc;
    }

    public void setSingerDesc(String singerDesc) {
        this.singerDesc = singerDesc;
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
    
    public String getPortraitPath() {
        return portraitPath;
    }

    public void setPortraitPath(String portraitPath) {
        this.portraitPath = portraitPath;
    }

    public List<SingerImage> getSingerImages() {
        return singerImages;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public boolean isBandsman() {
        return bandsman;
    }

    public void setBandsman(boolean bandsman) {
        this.bandsman = bandsman;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getWorks() {
        return works;
    }

    public void setWorks(String works) {
        this.works = works;
    }

    public BadgeType getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(BadgeType badgeType) {
        this.badgeType = badgeType;
    }
    
    public void setSingerImages(List<SingerImage> singerImages) {
        this.singerImages = singerImages;
    }

    public SingerImage addImage(SingerImage singerImage) {
        singerImage.setOwner(this);
        singerImages.add(singerImage);
        return singerImage;
    }

    @Override
    public SingerImage removeImage(TZImage tzImage) {
        SingerImage singerImage=(SingerImage)tzImage;
        singerImages.remove(singerImage);
        singerImage.setOwner(null);
        return singerImage;
    }
    
    @Override
    public void removeAllImagesAndDeleteOnFS(String deployPath) {
        for (SingerImage si : new ArrayList<>(singerImages)) {
            removeImageAndDeleteOnFS(si.getPictureType(),
                    si.getImageName(), deployPath);
        }
    }

    public SingerImage getSingerImage(PictureType pictureType, String imageName) {
        if (pictureType.equals(PictureType.SUBSIDIARY)
                ||pictureType.equals(PictureType.SUBTHUMB)) {
            for (SingerImage si : singerImages) {
                if (si.getImageName().equals(imageName)) {
                    return si;
                }
            }
        } else {
            for (SingerImage si : singerImages) {
                if (si.getPictureType().equals(pictureType)) {
                    return si;
                }
            }
        }
        return null;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public List<SingerOwnedSong> getSingerOwnedSongs() {
        return singerOwnedSongs;
    }

    public void setSingerOwnedSongs(List<SingerOwnedSong> singerOwnedSongs) {
        this.singerOwnedSongs = singerOwnedSongs;
    }

    public SingerOwnedSong addSingerOwnedSong(SingerOwnedSong singerOwnedSong) {
        singerOwnedSong.setOwner(this);
        singerOwnedSongs.add(singerOwnedSong);
        return singerOwnedSong;
    }

    public SingerOwnedSong removeSingerOwnedSong(SingerOwnedSong singerOwnedSong) {
        singerOwnedSongs.remove(singerOwnedSong);
        singerOwnedSong.setOwner(null);
        return singerOwnedSong;
    }
    

    /**
     * Returns a hash code value for the object. This implementation computes a
     * hash code value based on the id fields in this object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return new Integer(singerId).hashCode();
    }

    /**
     * Determines whether another object is equal to this Administrator. The
     * result is <code>true</code> if and only if the argument is not null and
     * is a UserEntity object that has the same id field values as this object.
     *
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @SuppressWarnings("StringEquality")
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Singer)) {
            return false;
        }

        Singer other = (Singer) object;

        return this.singerId == other.singerId;
    }

    /**
     * Returns a string representation of the object. This implementation
     * constructs that representation based on the id fields.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Singer:singerId " + singerId;
    }

    @Override
    public String fetchCommonIdentifier() {
        return "singer";
    }

    @Override
    public String fetchUniqueIdentifier() {
        return String.valueOf(singerId);
    }
    
    @Override
    public TZImage findImage(PictureType pictureType, String imageName) {
        return getSingerImage(pictureType, imageName);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
