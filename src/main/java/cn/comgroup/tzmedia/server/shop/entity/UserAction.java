package cn.comgroup.tzmedia.server.shop.entity;

import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.admin.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.TIME;
import static javax.persistence.TemporalType.TIMESTAMP;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ObjectTypeConverters;

@Entity
@Table(name = "USERACTIONS")
@NamedQueries({
    @NamedQuery(name = "UserAction.findByOrderNumber", query = "SELECT ua FROM UserAction ua WHERE ua.replyComment = :replyComment order by ua.actionId"),
    @NamedQuery(name = "UserAction.findByPlaybillId", query = "SELECT ua FROM UserAction ua WHERE ua.playbillId = :playbillId order by ua.actionId"),
    @NamedQuery(name = "UserAction.findByPIDAndActionType", query = "SELECT ua FROM UserAction ua WHERE ua.playbillId = :playbillId and ua.actionType= :actionType order by ua.actionId"),
    @NamedQuery(name = "UserAction.findByPIdAndChecked", 
            query = "SELECT ua FROM UserAction ua WHERE ua.playbillId = :playbillId AND ua.checked = :checked  AND ua.actionType =:actionType order by ua.actionId"),
    @NamedQuery(name = "UserAction.findByPIdUIdActionType", 
            query = "SELECT ua FROM UserAction ua WHERE ua.playbillId = :playbillId AND ua.userId = :userId  AND ua.actionType =:actionType order by ua.actionId"),
    @NamedQuery(name = "UserAction.findByUserId", query = "SELECT ua FROM UserAction ua WHERE ua.userId = :userId order by ua.actionId"),
})
@ObjectTypeConverters({
    @ObjectTypeConverter(name = "actionType", objectType = ActionType.class,
            dataType = String.class, conversionValues = {
                @ConversionValue(dataValue = "PRAISE", objectValue = "PRAISE"),
                @ConversionValue(dataValue = "COMMENT", objectValue = "COMMENT"),
                @ConversionValue(dataValue = "GRABSONG", objectValue = "GRABSONG"),
                @ConversionValue(dataValue = "PAY", objectValue = "PAY")})

})
public class UserAction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ACTIONID", updatable = false)
    /*
    @TableGenerator(name = "ACTION_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "ACTIONID_GEN",
            initialValue = 1)
    @GeneratedValue(generator = "ACTION_GENERATOR")*/
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long actionId;
    
    @Version
    private int version;
    
    @Basic
    @Column(name = "USERID",updatable = false,insertable = false)
    private String userId;
    
    @Transient
    private String userName;
    
    @Transient
    private UserRole userRole;
    
    @Basic
    @Column(name = "REPLYUSERID",updatable = false,insertable = false)
    private String replyUserId;
    
    @Transient
    private String replyUserName;
    
    @Transient
    private UserRole replyUserRole;
    
    
    @Basic
    @Column(name = "PLAYBILLID",updatable = false,insertable = false)
    private int playbillId;
    
    @Basic
    @Column(name = "SHOPID", updatable = false, insertable = false)
    private int shopId;
    
    @Basic
    @Column(name = "ACTIVITYNAME",  updatable = false, insertable = false)
    private int activityName;
    
    @Temporal(TIME)
    @Column(name = "ACTIONDATETIME")
    private Calendar actionDateTime;
    
    @Temporal(TIMESTAMP)
    @Column(name = "ACTIONORDERTIME")
    private Calendar actionOrderTime;


    
    
    @Basic
    @Column(name = "ACTIONTYPE")
    @Convert("actionType")
    private ActionType actionType = ActionType.PRAISE;
    
    @Basic
    @Column(name = "CHECKED")
    private boolean checked=true;
    
    @Basic
    @Column(name = "COMMENT")
    @Lob
    private String comment;
    
    @Basic
    @Column(name = "REPLYCOMMENT")
    @Lob
    private String replyComment;

    @ManyToOne
    @JoinColumn(name = "USERID")
    @JsonIgnore
    private User user;
    
    
    @ManyToOne
    @JoinColumn(name = "REPLYUSERID")
    @JsonIgnore
    private User replyUser;
    
    
    @ManyToOne
    @JoinColumn(name = "PLAYBILLID")
    @JsonIgnore
    private Playbill playbill;
    
    @ManyToOne
    @JoinColumn(name = "SHOPID")
    private Shop shop;
    
    @ManyToOne
    @JoinColumn(name = "ACTIVITYNAME")
    private Activity activity;
    
    @Basic
    @Column(name = "GRABCOMMENT")
    @Lob
    private String grabComment;

    public UserAction() {
    }

    public UserAction(ActionType actionType, Calendar actionDateTime, 
            String comment,String userId) {
        this.actionType = actionType;
        this.actionDateTime = actionDateTime;
        this.comment = comment;
        this.userId=userId;
    }

    public long getActionId() {
        return actionId;
    }

    public void setActionId(long actionId) {
        this.actionId = actionId;
    }

    public Calendar getActionDateTime() {
        return actionDateTime;
    }

    public void setActionDateTime(Calendar actionDateTime) {
        this.actionDateTime = actionDateTime;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReplyComment() {
        return replyComment;
    }

    public void setReplyComment(String replyComment) {
        this.replyComment = replyComment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        if (user != null) {
            return user.getUserName();
        }
        return "";
    }

    public Calendar getActionOrderTime() {
        return actionOrderTime;
    }

    public void setActionOrderTime(Calendar actionOrderTime) {
        this.actionOrderTime = actionOrderTime;
    }
    
    public UserRole getUserRole() {
        if (user != null) {
            return user.getUserRole();
        }
        return null;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    protected User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if(user!=null){
            this.userId=user.getUserId();
        }
    }

    public String getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(String replyUserId) {
        this.replyUserId = replyUserId;
    }

    public String getReplyUserName() {
        if (replyUser != null) {
            return replyUser.getUserName();
        }
        return "";
    }

    public void setReplyUserName(String replyUserName) {
        this.replyUserName = replyUserName;
    }

    public UserRole getReplyUserRole() {
        if (replyUser != null) {
            return replyUser.getUserRole();
        }
        return null;
    }

    public void setReplyUserRole(UserRole replyUserRole) {
        this.replyUserRole = replyUserRole;
    }

    protected User getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(User replyUser) {
        this.replyUser = replyUser;
        if(replyUser!=null){
            this.replyUserId=replyUser.getUserId();
        }
        
    }
    
    public int getPlaybillId() {
        return playbillId;
    }

    public void setPlaybillId(int playbillId) {
        this.playbillId = playbillId;
    }

    protected Playbill getPlaybill() {
        return playbill;
    }

    public void setPlaybill(Playbill playbill) {
        this.playbill = playbill;
        if (playbill != null) {
            this.playbillId = playbill.getPlaybillId();
        }
    }
    

    @Override
    public String toString() {
        return " actionId " + actionId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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
    
    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
        if (shop != null) {
            this.shopId = shop.getShopId();
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        if (activity != null) {
            this.activityName = activity.getActivityName();
        }
    }
    
    public String getGrabComment() {
        return grabComment;
    }

    public void setGrabComment(String grabComment) {
        this.grabComment = grabComment;
    }
}
