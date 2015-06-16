package cn.comgroup.tzmedia.server.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * We maintain unique GlobalConfiguration in the whole system.
 *
 * @author pcnsh197
 */
@Entity
@Table(name = "GLOBALCONFIGURATIONS")

@NamedQueries({
    @NamedQuery(name = "GlobalConfiguration.findAll", query = "SELECT gc FROM GlobalConfiguration gc")
})
public class GlobalConfiguration implements Serializable {

    @Id
    @Column(name = "ID", nullable = false)
    private int id = 1;

    @Version
    private int version;

    @Column(name = "LATESTANDRIODVERSION")
    private String latestAndriodVersion;

    @Column(name = "GRABSONGCHECKTIME")
    private boolean grabSongCheckTime;

    public GlobalConfiguration() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getLatestAndriodVersion() {
        return latestAndriodVersion;
    }

    public void setLatestAndriodVersion(String latestAndriodVersion) {
        this.latestAndriodVersion = latestAndriodVersion;
    }

    public boolean isGrabSongCheckTime() {
        return grabSongCheckTime;
    }

    public void setGrabSongCheckTime(boolean grabSongCheckTime) {
        this.grabSongCheckTime = grabSongCheckTime;
    }
}
