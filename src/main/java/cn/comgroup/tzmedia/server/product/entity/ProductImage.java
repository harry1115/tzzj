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
@Table(name = "PRODUCTIMAGES")
public class ProductImage extends TZImage implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "PRODUCTNUMBER")
    @JsonIgnore
    private Product owner;

    public ProductImage() {
    }

    public ProductImage(String imageName, String filePath,
            PictureType pictureType) {
        this.setImageName(imageName);
        this.setFilePath(filePath);
        this.setPictureType(pictureType);
    }

    protected Product getOwner() {
        return owner;
    }

    protected void setOwner(Product owner) {
        this.owner = owner;
    }
    @Override
    public String toString() {
        return "Product:productNumber " + owner.getProductNumber() + " imageName "
                + this.getImageName();
    }
}
