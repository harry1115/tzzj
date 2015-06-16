/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.admin.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.DATE;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ObjectTypeConverters;

/**
 *
 * @author pcnsh222
 */
@Entity
@Table(name = "USERSMESSAGE")
@NamedQueries({
    @NamedQuery(name = "UserMessage.findByMessageId", query = "SELECT s FROM UserMessage s WHERE s.messageId = :messageId"),
})
@ObjectTypeConverters({
        @ObjectTypeConverter(name = "userMessageType", objectType = UserMessageType.class, dataType = String.class, conversionValues = {
        @ConversionValue(dataValue = "USER", objectValue = "USER"),
        @ConversionValue(dataValue = "SYSTEM", objectValue = "SYSTEM")})
})
@XmlRootElement
public class UserMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "MESSAGEID", nullable = false)
    @TableGenerator(name = "USERMESSAGER_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "USERMESSAGEID_GEN",
            allocationSize = 1,
            initialValue = 10000)
    @GeneratedValue(generator = "USERMESSAGER_GENERATOR")
    private Long messageId;
    
    @Column(name = "USERID")
    private String userId;

    @Column(name = "FROMUSERID")
    private String fromUserId;
    
    @Column(name = "FROMUSERNAME")
    private String fromUserName;
    
    @Column(name = "PLAYBILLID")
    private int playbillId;

    @Column(name = "SHOPID")
    private int shopId;


    
    @Column(name = "ACTIONID")
    private long actionId;
    
    @Column(name = "FROMUSERROLE")
    private UserRole fromUserRole;

    @Temporal(DATE)
    @Column(name = "CREATIONDATE")
    private Calendar creationDate;
    
    @Column(name = "CONTENT")
    private String content;
    
    @Column(name = "TITLE")
    private String title;
    
    @Column(name = "ISNEW")
    private Boolean newMessage = true;
    
    @Basic
    @Column(name = "USERMESSAGETYPE")
    @Convert("userMessageType")
    private UserMessageType userMessageType = UserMessageType.SYSTEM;
    
    

    public UserMessage() {
        creationDate = Calendar.getInstance();
        creationDate.setTime(new Date());
    }
    
    public UserMessage(Long messageId) {
        this.messageId = messageId;
        creationDate = Calendar.getInstance();
        creationDate.setTime(new Date());
    }

    public UserMessage(Long messageId, String userId, String fromUserId, Calendar creationDate, String content, String title, String fromUserName,Boolean newMessage) {
        this.messageId = messageId;
        this.userId = userId;
        this.fromUserId = fromUserId;
        this.creationDate = creationDate;
        this.content = content;
        this.title = title;
        this.fromUserName = fromUserName;
        this.newMessage = newMessage;
    }
    
    
    
    
    public UserMessageType getUserMessageType() {
        return userMessageType;
    }
    
    public void setUserMessageType(UserMessageType userMessageType) {
        this.userMessageType = userMessageType;
    }
    
    public Boolean isNewMessage() {
        return newMessage;
    }
    
    public void setNewMessage(Boolean newMessage) {
        this.newMessage = newMessage;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }
    
    public UserRole getFromUserRole() {
        return fromUserRole;
    }

    public void setFromUserRole(UserRole fromUserRole) {
        this.fromUserRole = fromUserRole;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    
    public String getFromUserName() {
        return fromUserName;
    }
    
    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }
    
    public String getFromUserId() {
        return fromUserId;
    }
    
    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    
    public Calendar getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }
    
        public int getPlaybillId() {
        return playbillId;
    }

    public void setPlaybillId(int playbillId) {
        this.playbillId = playbillId;
    }

    public long getActionId() {
        return actionId;
    }

    public void setActionId(long actionId) {
        this.actionId = actionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (messageId != null ? messageId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserMessage)) {
            return false;
        }
        UserMessage other = (UserMessage) object;
        if ((this.messageId == null && other.messageId != null) || (this.messageId != null && !this.messageId.equals(other.messageId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.comgroup.tzmedia.server.admin.entity.UserMessageEntity[ messageId=" + messageId + " ]";
    }
    
}
