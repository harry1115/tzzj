package cn.comgroup.tzmedia.server.product.entity;

import cn.comgroup.tzmedia.server.common.entity.TZImage;
import cn.comgroup.tzmedia.server.common.entity.PictureType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PRODUCTTYPEIMAGES")
public class ProductTypeImage extends TZImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "TYPEID")
    @JsonIgnore
    private ProductType owner;

    protected ProductType getOwner() {
        return owner;
    }

    protected void setOwner(ProductType owner) {
        this.owner = owner;
    }

    public ProductTypeImage() {
        super();
    }

    public ProductTypeImage(String imageName, String filePath,
            PictureType pictureType) {
        super();
        this.setImageName(imageName);
        this.setFilePath(filePath);
        this.setPictureType(pictureType);
    }

    @Override
    public String toString() {
        return "ProductType:typeId " + owner.getTypeId() + " imageName "
                + this.getImageName();
    }
}
