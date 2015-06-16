package cn.comgroup.tzmedia.server.shop.entity;

import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.common.entity.TZImage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PLAYBILLIMAGES")
public class PlaybillImage  extends TZImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "PLAYBILLID")
    @JsonIgnore
    private Playbill owner;

    public PlaybillImage() {
    }

    public PlaybillImage(String imageName, String filePath,
            PictureType pictureType) {
        this.setImageName(imageName);
        this.setFilePath(filePath);
        this.setPictureType(pictureType);
    }

    protected Playbill getOwner() {
        return owner;
    }

    protected void setOwner(Playbill owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "PlaybillImage:playbillId" + owner.getPlaybillId()
                + " imageName " + this.getImageName();
    }
}
