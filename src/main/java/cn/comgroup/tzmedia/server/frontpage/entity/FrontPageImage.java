package cn.comgroup.tzmedia.server.frontpage.entity;

import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.common.entity.TZImage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import jersey.repackaged.com.google.common.base.Objects;

@Entity
@Table(name = "FRONTPAGEIMAGES")
public class FrontPageImage extends TZImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "FRONTPAGEID")
    @JsonIgnore
    private FrontPage owner;

    public FrontPageImage() {
        super();
    }

    public FrontPageImage(String imageName, String filePath,
            PictureType pictureType) {
        super();
        this.setImageName(imageName);
        this.setFilePath(filePath);
        this.setPictureType(pictureType);
    }

    protected FrontPage getOwner() {
        return owner;
    }

    protected void setOwner(FrontPage owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("frontPageId", owner.getFrontPageId()).
                add("imageName", this.getImageName()).toString();
    }
}
