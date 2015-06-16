package cn.comgroup.tzmedia.server.admin.entity;

import cn.comgroup.tzmedia.server.shop.entity.Activity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "USERACTIVITIES")
@NamedQueries({
    @NamedQuery(name = "UserActivity.findAll", query = "SELECT ua FROM UserActivity ua"),
    @NamedQuery(name = "UserActivity.findByUserId", query = "SELECT ua FROM UserActivity ua WHERE ua.userId = :userId"),
    @NamedQuery(name = "UserActivity.findAttendedUsers", query = "SELECT count(ua) FROM UserActivity ua WHERE ua.activityName = :activityName"),
    @NamedQuery(name = "UserActivity.findAttendedActivities", query = "SELECT count(ua) FROM UserActivity ua WHERE ua.userId = :userId")
})
@IdClass(UserActivity.UserActivityID.class)
public class UserActivity implements Serializable {
    @Id
    @Basic
    @Column(name = "USERID", updatable = false, insertable = false)
    private String userId;

    @Id
    @Basic
    @Column(name = "ACTIVITYNAME", updatable = false, insertable = false)
    private int activityName;

    @ManyToOne
    @JoinColumn(name = "USERID")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "ACTIVITYNAME")
    @JsonIgnore
    private Activity activity;

    public UserActivity() {
    }

    public UserActivity(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public int getActivityName() {
        return activityName;
    }

    public void setActivityName(int activityName) {
        this.activityName = activityName;
    }

    protected Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        if (activity != null) {
            this.activityName = activity.getActivityName();
        }
    }

    public static class UserActivityID implements Serializable {

        private static final long serialVersionUID = 1L;

        public String userId;
        public int activityName;

        public UserActivityID() {
        }

        public UserActivityID(String userId, int activityName) {
            this.userId = userId;
            this.activityName = activityName;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof UserActivityID) {
                final UserActivityID otherID = (UserActivityID) other;
                return otherID.activityName == activityName && otherID.userId.equals(userId);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
