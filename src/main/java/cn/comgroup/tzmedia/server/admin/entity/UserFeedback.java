package cn.comgroup.tzmedia.server.admin.entity;

import cn.comgroup.tzmedia.server.util.jackson.CustomJsonDateSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.DATE;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "USERFEEDBACKS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserFeedback.findAll", query = "SELECT uf FROM UserFeedback uf order by uf.feedBackDate"),
    @NamedQuery(name = "UserFeedback.findByUserId", query = "SELECT uf FROM UserFeedback uf WHERE uf.userId = :userId"),
    @NamedQuery(name = "UserFeedback.findByDate", query = "SELECT uf FROM UserFeedback uf WHERE uf.feedBackDate >= :fromDate And  uf.feedBackDate <= :toDate"),})
public class UserFeedback implements Serializable {

    @Id
    @Column(name = "FEEDBACKNUMBER", nullable = false)
    @TableGenerator(name = "FEEDBACK_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "FEEDBACKNUMBER_GEN",
            initialValue = 1)
    @GeneratedValue(generator = "FEEDBACK_GENERATOR")
    private long feedbackNumber;

    @Basic
    @Column(name = "USERID", updatable = false, insertable = false)
    private String userId;
    
    @Transient
    private String userName;
    
    @Basic
    @Column(name = "CONTENT")
    @Lob
    private String content;

    @Temporal(DATE)
    @Column(name = "FEEDBACKDATE")
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    private Calendar feedBackDate;

    @ManyToOne
    @JoinColumn(name = "USERID")
    @JsonIgnore
    private User user;

    public UserFeedback() {
    }

    public UserFeedback(String userId, String content, Calendar feedBackDate) {
        this.userId = userId;
        this.content = content;
        this.feedBackDate = feedBackDate;
    }

    public long getFeedbackNumber() {
        return feedbackNumber;
    }

    public void setFeedbackNumber(long feedbackNumber) {
        this.feedbackNumber = feedbackNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Calendar getFeedBackDate() {
        return feedBackDate;
    }

    public void setFeedBackDate(Calendar feedBackDate) {
        this.feedBackDate = feedBackDate;
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

    protected User getUser() {
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

}
