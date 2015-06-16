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
@Table(name = "USERIMAGES")
public class UserImage extends TZImage implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "USERID")
    @JsonIgnore
    private User owner;

    public UserImage() {
        super();
    }

    public UserImage(String imageName, String filePath,
            PictureType pictureType) {
        super();
        this.setImageName(imageName);
        this.setFilePath(filePath);
        this.setPictureType(pictureType);
    }

    protected User getOwner() {
        return owner;
    }

    protected void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
       return Objects.toStringHelper(this).add("userId", owner.getUserId()).
                add("imageName", this.getImageName()).toString();
    }
}
