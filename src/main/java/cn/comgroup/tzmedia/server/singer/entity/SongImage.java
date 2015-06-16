package cn.comgroup.tzmedia.server.singer.entity;

import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.common.entity.TZImage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SONGIMAGES")
public class SongImage extends TZImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "SONGID")
    @JsonIgnore
    private Song owner;

    public SongImage() {
    }

    public SongImage(String imageName, String filePath,
            PictureType pictureType) {
        this.setImageName(imageName);
        this.setFilePath(filePath);
        this.setPictureType(pictureType);
    }

    protected Song getOwner() {
        return owner;
    }

    protected void setOwner(Song owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "SongImage:songId " + owner.getSongId()
                + " imageName " + this.getImageName();
    }
}
