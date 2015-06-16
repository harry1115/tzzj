package cn.comgroup.tzmedia.server.admin.entity;

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
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ObjectTypeConverters;
import org.eclipse.persistence.annotations.PrivateOwned;


/**
 * Entity class Administrator,the user is logged from web page.
 *
 * @author peter.liu@comgroup.cn
 */
@Entity
@Table(name = "ADMINISTRATORS")
@ObjectTypeConverters({
    @ObjectTypeConverter(name = "adminRole", objectType = AdminRole.class, dataType = String.class, conversionValues = {
        @ConversionValue(dataValue = "INSHOP", objectValue = "INSHOP"),
        @ConversionValue(dataValue = "BACKEND", objectValue = "BACKEND"),
        @ConversionValue(dataValue = "SUPER", objectValue = "SUPER"),
        @ConversionValue(dataValue = "NULL", objectValue = "NULL")}),
})

@NamedQueries({
    @NamedQuery(name = "Administrator.findByUserid", query = "SELECT u FROM Administrator u WHERE u.userId = :userid"),
    @NamedQuery(name = "Administrator.findByPassword", query = "SELECT u FROM Administrator u WHERE u.password = :password"),
    @NamedQuery(name = "Administrator.findByUsername", query = "SELECT u FROM Administrator u WHERE u.userName = :username"),
})
@XmlRootElement
public class Administrator extends PictureHandleTemplate implements Serializable {

    @Id
    @Column(name = "USERID", nullable = false)
    private String userId;
    
    @Version
    private int version;
    

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "USERNAME")
    private String userName;
    
    @Basic
    @Column(name = "ADMINROLE")
    @Convert("adminRole")
    private AdminRole adminRole = AdminRole.INSHOP;
    
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    private List<AdministratorImage> administratorImages = new ArrayList<>();
    
    @Column(name = "SHOPID")
    private int shopId;
    
    /**
     * Creates a new instance of UserEntity
     */
    public Administrator() {
    }

    /**
     * Creates a new instance of UserEntity with the specified values.
     *
     * @param userId the userId of the UserEntity
     */
    public Administrator(String userId) {
        this.userId = userId;
    }

    /**
     * Creates a new instance of UserEntity with the specified values.
     *
     * @param userId the userId of the UserEntity
     * @param password the password of the UserEntity
     */
    public Administrator(String userId, String password) {
        this.userId = userId;
        this.password = password;
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
    
    public int getShopId() {
        return this.shopId;
    }
    

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public AdminRole getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(AdminRole adminRole) {
        this.adminRole = adminRole;
    }

    public List<AdministratorImage> getAdministratorImages() {
        return administratorImages;
    }

    public void setAdministratorImages(List<AdministratorImage> administratorImages) {
        this.administratorImages = administratorImages;
    }
    
    
     public AdministratorImage addImage(AdministratorImage administratorImage) {
        administratorImage.setOwner(this);
        administratorImages.add(administratorImage);
        return administratorImage;
    }

    @Override
    public AdministratorImage removeImage(TZImage tzImage) {
        AdministratorImage administratorImage = (AdministratorImage) tzImage;
        administratorImages.remove(administratorImage);
        administratorImage.setOwner(null);
        return administratorImage;
    }
    
    @Override
    public void removeAllImagesAndDeleteOnFS(String deployPath) {
        for (AdministratorImage ai : new ArrayList<>(administratorImages)) {
            removeImageAndDeleteOnFS(ai.getPictureType(),
                    ai.getImageName(), deployPath);
        }
    }
    

    public AdministratorImage getAdministratorImage(PictureType pictureType, String imageName) {
        if (pictureType.equals(PictureType.SUBSIDIARY)
                || pictureType.equals(PictureType.SUBTHUMB)) {
            for (AdministratorImage ai : administratorImages) {
                if (ai.getImageName().equals(imageName)) {
                    return ai;
                }
            }
        } else {
            for (AdministratorImage ai : administratorImages) {
                if (ai.getPictureType().equals(pictureType)) {
                    return ai;
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
        int hash = 0;
        hash += (this.userId != null ? this.userId.hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this Administrator. The result
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
        if (!(object instanceof Administrator)) {
            return false;
        }

        Administrator other = (Administrator) object;

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
        return "Administrator:userId" + userId;
    }

    @Override
    public String fetchCommonIdentifier() {
        return "administrator";
    }

    @Override
    public String fetchUniqueIdentifier() {
        return userId;
    }

    @Override
    public TZImage findImage(PictureType pictureType, String imageName) {
        return getAdministratorImage(pictureType, imageName);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
