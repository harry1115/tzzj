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
@Table(name = "SINGERIMAGES")
public class SingerImage extends TZImage implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ManyToOne
    @JoinColumn(name = "SINGERID")
    @JsonIgnore
    private Singer owner;

    public SingerImage() {
        super();
    }

    public SingerImage( String imageName, String filePath,
            PictureType pictureType) {
        super();
        this.setImageName(imageName);
        this.setFilePath(filePath);
        this.setPictureType(pictureType);
    }

    protected Singer getOwner() {
        return owner;
    }

    protected void setOwner(Singer singer) {
        this.owner = singer;
    }
    
    @Override
    public String toString() {
        return "SingerImage:singerId " + owner.getSingerId()
                + " imageName " + this.getImageName();
    }

}
