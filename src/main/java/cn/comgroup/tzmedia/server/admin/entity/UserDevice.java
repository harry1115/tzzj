package cn.comgroup.tzmedia.server.admin.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * Entity class UserDevice.
 *
 * @author peter.liu@comgroup.cn
 */
@Entity
@Table(name = "USERDEVICES")
@NamedQueries({
    @NamedQuery(name = "UserDevice.findAll", query = "SELECT u FROM UserDevice u"),
    @NamedQuery(name = "UserDevice.findByUserId", query = "SELECT u FROM UserDevice u WHERE u.userId = :userId"),
    @NamedQuery(name = "UserDevice.findByDeviceToken", query = "SELECT u FROM UserDevice u WHERE u.devicetoken = :devicetoken"),
})
public class UserDevice implements Serializable {

    @Id
    @Column(name = "USERID", nullable = false)
    private String userId;

    @Column(name = "DEVICETOKEN")
    private String devicetoken;
    
    @Version
    private int version;
    /**
     * Creates a new instance of UserEntity
     */
    public UserDevice() {
    }


    /**
     * Creates a new instance of UserDevice with the specified values.
     *
     * @param userId the userId of the UserEntity
     */
    public UserDevice(String userId) {
        this.userId = userId;
    }

    /**
     * Creates a new instance of UserDevice with the specified values.
     *
     * @param userId the userId of the UserEntity
     * @param devicetoken
     */
    public UserDevice(String userId, String devicetoken) {
        this.userId = userId;
        this.devicetoken = devicetoken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDevicetoken() {
        return devicetoken;
    }

    public void setDevicetoken(String devicetoken) {
        this.devicetoken = devicetoken;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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
        if (!(object instanceof UserDevice)) {
            return false;
        }

        UserDevice other = (UserDevice) object;

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
        return "UserToken:userId"+userId;
        
    }

}
