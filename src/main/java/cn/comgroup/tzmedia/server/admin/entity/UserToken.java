package cn.comgroup.tzmedia.server.admin.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Entity class User.
 *
 * @author peter.liu@comgroup.cn
 */
@Entity
@Table(name = "USERTOKENS")

@NamedQueries({
    @NamedQuery(name = "UserToken.findAll", query = "SELECT u FROM UserToken u"),
    @NamedQuery(name = "UserToken.findByUserId", query = "SELECT u FROM UserToken u WHERE u.userId = :userId"),
    @NamedQuery(name = "UserToken.findByUserToken", query = "SELECT u FROM UserToken u WHERE u.token = :token"),
})
@XmlRootElement
public class UserToken implements Serializable {

    @Id
    @Column(name = "USERID", nullable = false)
    private String userId;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "TOKEN")
    private String token;
    /**
     * Creates a new instance of UserEntity
     */
    public UserToken() {
    }


    /**
     * Creates a new instance of UserEntity with the specified values.
     *
     * @param userId the userId of the UserEntity
     */
    public UserToken(String userId) {
        this.userId = userId;
    }

    /**
     * Creates a new instance of UserEntity with the specified values.
     *
     * @param userId the userId of the UserEntity
     * @param token
     * @param password the password of the UserEntity
     */
    public UserToken(String userId, String token,String password) {
        this.userId = userId;
        this.token = token;
        this.password=password;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
        if (!(object instanceof UserToken)) {
            return false;
        }

        UserToken other = (UserToken) object;

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
