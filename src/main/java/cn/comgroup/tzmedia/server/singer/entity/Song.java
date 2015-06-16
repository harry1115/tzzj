package cn.comgroup.tzmedia.server.singer.entity;

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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
@Table(name = "SONGS")
@NamedQueries({
    @NamedQuery(name = "Song.findAll", query = "SELECT s FROM Song s"),
    @NamedQuery(name = "Song.findBySongId", query = "SELECT s FROM Song s WHERE s.songId = :songId"),
    @NamedQuery(name = "Song.findBySongName", query = "SELECT s FROM Song s WHERE s.songName like :songName"),
    @NamedQuery(name = "Song.findBySongType", query = "SELECT s FROM Song s WHERE s.type = :type"),
})

public class Song extends PictureHandleTemplate implements Serializable {

    @Id
    @Column(name = "SONGID", nullable = false)
    @TableGenerator(name = "SONG_GENERATOR",
            table = "ID_GENERATOR",
            pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE",
            pkColumnValue = "SONGID_GEN",
            allocationSize=1,
            initialValue = 1)
    @GeneratedValue(generator = "SONG_GENERATOR")
    private int songId;    
    
    @Basic
    @Column(name = "TYPE")
    private String type;
    
    @Basic
    @Column(name = "SONGNAME")
    private String songName;
    
    @Basic
    @Column(name = "SONGDESC")
    private String songDesc;
    
    @Basic
    @Column(name = "ORIGINALSINGER")
    private String originalSinger;
    
    @Basic
    @Column(name = "PORTRAITPATH")
    private String portraitPath;
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    private List<SongImage> songImages = new ArrayList<>();
    
    public Song() {
    }

    public Song(String songName,String songDesc) {
        this.songName = songName;
        this.songDesc = songDesc;
    }

    public String getPortraitPath() {
        return portraitPath;
    }

    public void setPortraitPath(String portraitPath) {
        this.portraitPath = portraitPath;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongDesc() {
        return songDesc;
    }

    public void setSongDesc(String songDesc) {
        this.songDesc = songDesc;
    }

    public String getOriginalSinger() {
        return originalSinger;
    }

    public void setOriginalSinger(String originalSinger) {
        this.originalSinger = originalSinger;
    }
    

    public List<SongImage> getSongImages() {
        return songImages;
    }

    public void setSongImages(List<SongImage> songImages) {
        this.songImages = songImages;
    }
    
    public SongImage addImage(SongImage songImage) {
        songImage.setOwner(this);
        songImages.add(songImage);
        return songImage;
    }

    @Override
    public SongImage removeImage(TZImage tzImage) {
        SongImage songImage=(SongImage)tzImage;
        songImages.remove(songImage);
        songImage.setOwner(null);
        return songImage;
    }
    
    @Override
    public void removeAllImagesAndDeleteOnFS(String deployPath) {
        for (SongImage si : new ArrayList<>(songImages)) {
            removeImageAndDeleteOnFS(si.getPictureType(),
                    si.getImageName(), deployPath);
        }
    }
    
    
    public SongImage getSongImage(PictureType pictureType, String imageName) {
        if (pictureType.equals(PictureType.SUBSIDIARY)) {
            for (SongImage si : songImages) {
                if (si.getImageName().equals(imageName)) {
                    return si;
                }
            }
        } else {
            for (SongImage si : songImages) {
                if (si.getPictureType().equals(pictureType)) {
                    return si;
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
        return songId;
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
        if (!(object instanceof Song)) {
            return false;
        }

        Song other = (Song) object;

        return this.songId == other.songId;
    }

    /**
     * Returns a string representation of the object. This implementation
     * constructs that representation based on the id fields.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Song:songId "+songId;
    }

    @Override
    public String fetchCommonIdentifier() {
        return "song";
    }

    @Override
    public String fetchUniqueIdentifier() {
        return String.valueOf(songId);
    }
    
    @Override
    public TZImage findImage(PictureType pictureType, String imageName) {
        return getSongImage(pictureType, imageName);
    }

}
