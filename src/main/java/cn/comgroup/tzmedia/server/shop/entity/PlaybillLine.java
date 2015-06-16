package cn.comgroup.tzmedia.server.shop.entity;

import cn.comgroup.tzmedia.server.singer.entity.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "PLAYBILLLINES")
public class PlaybillLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "UNIQUENUMBER", updatable = false)
    @TableGenerator(name = "PLAYBILLLINE_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "PLAYBILLLINE_GEN",
            initialValue = 1)
    @GeneratedValue(generator = "PLAYBILLLINE_GENERATOR")
    private long uniqueNumber;
    
    
    @Column(name = "LINENUMBER")
    private int lineNumber;
    
    @Basic
    @Column(name = "SONGID",updatable = false,insertable = false)
    private int songId;
    
    
    
    @Basic
    @Column(name = "SONGNAME")
    private String songName;


    @ManyToOne
    @JoinColumn(name = "SONGID")
    @JsonIgnore
    private Song song;
    
    
    @ManyToOne
    @JoinColumn(name = "PLAYBILLID")
    @JsonIgnore
    private Playbill owner;
    
    public PlaybillLine() {
    }

    public long getUniqueNumber() {
        return uniqueNumber;
    }

    public void setUniqueNumber(long uniqueNumber) {
        this.uniqueNumber = uniqueNumber;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
        if (song != null) {
            this.songName = song.getSongName();
            this.songId = song.getSongId();
        }
    }

    protected Playbill getOwner() {
        return owner;
    }

    protected void setOwner(Playbill owner) {
        this.owner = owner;
    }


    @Override
    public String toString() {
        return "PlaybillLine:playbillId " + owner.getPlaybillId() + " lineNumber "
                + lineNumber;
    }

}
