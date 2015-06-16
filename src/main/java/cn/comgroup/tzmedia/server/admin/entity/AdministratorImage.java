package cn.comgroup.tzmedia.server.admin.entity;

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
@Table(name = "ADMINISTRATORIMAGES")
public class AdministratorImage extends TZImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "USERID")
    @JsonIgnore
    private Administrator owner;

    public AdministratorImage() {
        super();
    }

    public AdministratorImage(String imageName, String filePath,
            PictureType pictureType) {
        super();
        this.setImageName(imageName);
        this.setFilePath(filePath);
        this.setPictureType(pictureType);
    }

    protected Administrator getOwner() {
        return owner;
    }

    protected void setOwner(Administrator owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("userId", owner.getUserId()).
                add("imageName", this.getImageName()).toString();
    }
}
