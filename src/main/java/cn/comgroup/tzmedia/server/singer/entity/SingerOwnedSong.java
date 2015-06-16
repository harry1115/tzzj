package cn.comgroup.tzmedia.server.singer.entity;

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
@Table(name = "SINGEROWNEDSONGS")
public class SingerOwnedSong implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "SINGEROWNEDSONGID", nullable = false)
    @TableGenerator(name = "SINGEROWNEDSONG_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "SINGEROWNEDSONGID_GEN",
            initialValue = 1)
    @GeneratedValue(generator = "SINGEROWNEDSONG_GENERATOR")
    private int singerOwnedSongId;

    @ManyToOne
    @JoinColumn(name = "SINGERID")
    @JsonIgnore
    private Singer owner;

    @Column(name = "SONGID", updatable = false, insertable = false)
    private int songId;
    
    @Basic
    @Column(name = "SONGNAME")
    private String songName;

    @ManyToOne
    @JoinColumn(name = "SONGID")
    private Song song;

    public SingerOwnedSong() {
    }

    protected Singer getOwner() {
        return owner;
    }

    protected void setOwner(Singer singer) {
        this.owner = singer;
    }

    public int getSingerOwnedSongId() {
        return singerOwnedSongId;
    }

    public void setSingerOwnedSongId(int singerOwnedSongId) {
        this.singerOwnedSongId = singerOwnedSongId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
        if(song!=null){
            this.songName=song.getSongName();
            this.songId=song.getSongId();
        }
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
}
