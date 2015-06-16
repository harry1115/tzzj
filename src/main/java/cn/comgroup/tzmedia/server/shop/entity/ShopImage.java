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
@Table(name = "SHOPIMAGES")
public class ShopImage extends TZImage implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ManyToOne
    @JoinColumn(name = "SHOPID")
    @JsonIgnore
    private Shop  owner;

    public ShopImage() {
        super();
    }

    public ShopImage(String imageName, String filePath, PictureType pictureType) {
        super();
        this.setImageName(imageName);
        this.setFilePath(filePath);
        this.setPictureType(pictureType);
    }

    protected Shop getOwner() {
        return owner;
    }

    protected void setOwner(Shop owner) {
        this.owner = owner;
    }
    
    @Override
    public String toString() {
        return "ShopImage:shopId " + owner.getShopId()
                + " imageName " + this.getImageName();
    }
}
