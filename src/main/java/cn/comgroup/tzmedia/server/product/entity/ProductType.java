/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.product.entity;

import cn.comgroup.tzmedia.server.common.entity.PictureHandleTemplate;
import cn.comgroup.tzmedia.server.common.entity.PictureType;
import cn.comgroup.tzmedia.server.common.entity.TZImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * ProductType
 *
 * @author pcnsh197
 */
@Entity
@Table(name = "PRODUCTTYPES")
@NamedQueries({
    @NamedQuery(name = "ProductType.findAll", query = "SELECT pt FROM ProductType pt order by pt.typeId"),
    @NamedQuery(name = "ProductType.findByTypeId", query = "SELECT pt FROM ProductType pt WHERE pt.typeId = :typeId"),  
    @NamedQuery(name = "ProductType.findByShopId", query = "SELECT pt FROM ProductType pt WHERE pt.shopId = :shopId"),
})
    
public class ProductType extends PictureHandleTemplate implements Serializable {

    @Id
    @Column(name = "TYPEID", nullable = false)
    private int typeId;

    @Basic
    @Column(name = "TYPENAME")
    private String typeName;
    
    @Basic
    @Column(name = "SHOPID")
    private int shopId;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    private List<ProductTypeImage> productTypeImages = new ArrayList<>();

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<ProductTypeImage> getProductTypeImages() {
        return productTypeImages;
    }

    public void setProductTypeImages(List<ProductTypeImage> productTypeImages) {
        this.productTypeImages = productTypeImages;
    }

    public ProductTypeImage addImage(ProductTypeImage productTypeImage) {
        productTypeImage.setOwner(this);
        productTypeImages.add(productTypeImage);
        return productTypeImage;
    }

    @Override
    public TZImage removeImage(TZImage tzImage) {
        ProductTypeImage productTypeImage = (ProductTypeImage) tzImage;
        productTypeImages.remove(productTypeImage);
//        productTypeImage.setOwner(null);
        return productTypeImage;
    }
    
    @Override
    public void removeAllImagesAndDeleteOnFS(String deployPath) {
        for (ProductTypeImage pi : new ArrayList<>(productTypeImages)) {
            removeImageAndDeleteOnFS(pi.getPictureType(),
                    pi.getImageName(), deployPath);
        }
    }
    

    public ProductTypeImage getProductTypeImage(PictureType pictureType, String imageName) {
        if (pictureType.equals(PictureType.SUBSIDIARY)
                ||pictureType.equals(PictureType.SUBTHUMB)) {
            for (ProductTypeImage pi : productTypeImages) {
                if (pi.getImageName().equals(imageName)) {
                    return pi;
                }
            }
        } else {
            for (ProductTypeImage pi : productTypeImages) {
                if (pi.getPictureType().equals(pictureType)) {
                    return pi;
                }
            }
        }
        return null;
    }

    @Override
    public String fetchCommonIdentifier() {
        return "producttype";
    }

    @Override
    public String fetchUniqueIdentifier() {
        return String.valueOf(typeId);
    }

    @Override
    public TZImage findImage(PictureType pictureType, String imageName) {
        return getProductTypeImage(pictureType, imageName);
    }

    /**
     * @return the shopId
     */
    public int getShopId() {
        return shopId;
    }

    /**
     * @param shopId the shopId to set
     */
    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

  
}
