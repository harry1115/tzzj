package cn.comgroup.tzmedia.server.admin.entity;

import cn.comgroup.tzmedia.server.common.entity.PictureHandleTemplate;
import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.common.entity.TZImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.DATE;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ObjectTypeConverters;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Entity class User.
 *
 * @author peter.liu@comgroup.cn
 */
@Entity
@Table(name = "USERS")
@ObjectTypeConverters({
    @ObjectTypeConverter(name = "gender", objectType = Gender.class, dataType = String.class, conversionValues = {
        @ConversionValue(dataValue = "M", objectValue = "M"),
        @ConversionValue(dataValue = "F", objectValue = "F")}),
    @ObjectTypeConverter(name = "userType", objectType = UserType.class, dataType = String.class, conversionValues = {
        @ConversionValue(dataValue = "DUDU", objectValue = "DUDU"),
        @ConversionValue(dataValue = "QQ", objectValue = "QQ"),
        @ConversionValue(dataValue = "WEIBO", objectValue = "WEIBO"),
        @ConversionValue(dataValue = "MOBILE", objectValue = "MOBILE"),
        @ConversionValue(dataValue = "EMAIL", objectValue = "EMAIL")}),
     @ObjectTypeConverter(name = "userRole", objectType = UserRole.class, dataType = String.class, conversionValues = {
        @ConversionValue(dataValue = "NORMAL", objectValue = "NORMAL"),
        @ConversionValue(dataValue = "SINGER", objectValue = "SINGER"),
        @ConversionValue(dataValue = "INTERNAL", objectValue = "INTERNAL"),
        @ConversionValue(dataValue = "BAND", objectValue = "BAND")
        })
})
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findByUserRange", query = "SELECT u FROM User u WHERE u.userId >= :fromUserId  AND u.userId <= :toUserId"),
    @NamedQuery(name = "User.findByUserId", query = "SELECT u FROM User u WHERE u.userId = :userId"),
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.userName like :userName"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email=:email"),
    @NamedQuery(name = "User.findByQQ", query = "SELECT u FROM User u WHERE u.qq = :qq"),
    @NamedQuery(name = "User.findByWeibo", query = "SELECT u FROM User u WHERE u.weibo = :weibo"),
    @NamedQuery(name = "User.findByPhoneNumber", query = "SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber"),
})
@XmlRootElement
public class User extends PictureHandleTemplate implements Serializable {

    @Id
    @Column(name = "USERID", nullable = false)
    @TableGenerator(name = "USER_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "USERID_GEN",
            allocationSize = 1,
            initialValue = 10000)
    @GeneratedValue(generator = "USER_GENERATOR")
    private String userId; 
    
    @Version
    private int version;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "USERNAME")
    private String userName;

    /**
     * Gender mapped using Basic with an ObjectTypeConverter to map between
     * single char code value in database to enum. JPA only supports mapping to
     * the full name of the enum or its ordinal value.
     */
    @Basic
    @Column(name = "GENDER")
    @Convert("gender")
    private Gender gender = Gender.M;
    
    @Basic
    @Column(name = "USERTYPE")
    @Convert("userType")
    private UserType userType = UserType.DUDU;
    
    @Basic
    @Column(name = "USERROLE")
    @Convert("userRole")
    private UserRole userRole = UserRole.NORMAL;

    @Temporal(DATE)
    @Column(name = "BIRTHDAY")
    private Calendar birthDay;

    @Column(name = "SIGNATURE")
    private String signature;

    @Column(name = "SCORE")
    private int score;

    @Column(name = "ADDRESS")
    private String address;
    
    @Column(name = "EMAIL")
    private String email;
    
    @Column(name = "QQ")
    private String qq;
    
    @Column(name = "WEIBO")
    private String weibo;
    
    @Column(name = "PHONENUMBER")
    private String phoneNumber;
    
    @Temporal(DATE)
    @Column(name = "CREATIONDATE")
    private Calendar creationDate;
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    private List<UserImage> userImages = new ArrayList<>();

    /**
     * Creates a new instance of UserEntity
     */
    public User() {
        creationDate = Calendar.getInstance();
        creationDate.setTime(new Date());
    }

    /**
     * Creates a new instance of UserEntity with the specified values.
     *
     * @param userId the userId of the UserEntity
     */
    public User(String userId) {
        this.userId = userId;
        creationDate = Calendar.getInstance();
        creationDate.setTime(new Date());
    }

    /**
     * Creates a new instance of UserEntity with the specified values.
     *
     * @param userId the userId of the UserEntity
     * @param password the password of the UserEntity
     * @param email
     * @param qq
     * @param weibo
     * @param phoneNumber
     */
    public User(String userId, String password, String email, String qq, String weibo, String phoneNumber) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.qq = qq;
        this.weibo = weibo;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the password of this UserEntity.
     *
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the password of this UserEntity to the specified value.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the email of this UserEntity.
     *
     * @return the email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email of this UserEntity to the specified value.
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Calendar getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Calendar birthDay) {
        this.birthDay = birthDay;
    }


    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
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
        hash += (this.userId != null ? this.userId.hashCode() : 0);
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }

        User other = (User) object;

        return !(this.userId != other.userId && (this.userId == null || !this.userId.equals(other.userId)));
    }

    /**
     * Returns a string representation of the object. This implementation
     * constructs that representation based on the id fields.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "User:userId"+userId;
        
    }

    public List<UserImage> getUserImages() {
        return userImages;
    }

    public void setUserImages(List<UserImage> userImages) {
        this.userImages = userImages;
    }

    @Override
    public String fetchCommonIdentifier() {
        return "user";
    }

    @Override
    public String fetchUniqueIdentifier() {
        return userId;
    }

    @Override
    public TZImage removeImage(TZImage image) {
        UserImage userImage = (UserImage) image;
        userImages.remove(userImage);
        userImage.setOwner(null);
        return userImage;
    }

    @Override
    public void removeAllImagesAndDeleteOnFS(String deployPath) {
        for (UserImage ui : new ArrayList<>(userImages)) {
            removeImageAndDeleteOnFS(ui.getPictureType(),
                    ui.getImageName(), deployPath);
        }
    }

    @Override
    public TZImage findImage(PictureType pictureType, String imageName) {
        return getUserImage(pictureType, imageName);
    }

    public UserImage getUserImage(PictureType pictureType, String imageName) {
        if (pictureType.equals(PictureType.SUBSIDIARY)
                || pictureType.equals(PictureType.SUBTHUMB)) {
            for (UserImage ui : userImages) {
                if (ui.getImageName().equals(imageName)) {
                    return ui;
                }
            }
        } else {
            for (UserImage ui : userImages) {
                if (ui.getPictureType().equals(pictureType)) {
                    return ui;
                }
            }
        }
        return null;
    }
    
    public UserImage addImage(UserImage userImage) {
        userImage.setOwner(this);
        userImages.add(userImage);
        return userImage;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
    
    
}
