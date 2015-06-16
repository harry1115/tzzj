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
@Table(name = "ACTIVITYIMAGES")
public class ActivityImage  extends TZImage implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ManyToOne
    @JoinColumn(name = "ACTIVITYNAME")
    @JsonIgnore
    private Activity  owner;

    public ActivityImage() {
        super();
    }

    public ActivityImage(String imageName, String filePath, PictureType pictureType) {
        super();
        this.setImageName(imageName);
        this.setFilePath(filePath);
        this.setPictureType(pictureType);
    }

    protected Activity getOwner() {
        return owner;
    }

    protected void setOwner(Activity owner) {
        this.owner = owner;
    }
    
    @Override
    public String toString() {
        return "ActivityImage: activityName " + owner.getActivityName()
                + " imageName " + this.getImageName();
    }
}
