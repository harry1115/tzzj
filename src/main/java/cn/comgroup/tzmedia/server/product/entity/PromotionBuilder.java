/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.product.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * PromotionDefinition
 *
 * @author pcnsh197
 */
public class PromotionBuilder {

    public static void build(Product product,
            EntityManager em) {
        if (product.getPromotionDefinition() != null) {
            PromotionDefinition pd = product.getPromotionDefinition();
            pd.setPromotionProductNumber(product.getProductNumber());
            pd.setProductName(product.getProductName());
            List<PromotionProduct> promotionProducts = pd.getPromotionProducts();
            pd.setPromotionProducts(new ArrayList<PromotionProduct>());
            switch (pd.getPromotionType()) {
                case BYQUANTITY:
                    setPromotionProduct(pd, promotionProducts, em);
                    break;
                case BYPRODUCT:
                    pd.setMinimalOrderQuantity(1);
                    setPromotionProduct(pd, promotionProducts, em);
                    break;
                case BYAMOUNT:
                    break;
                case BYDISCOUNT:
                    break;
                default:
                    break;
            }
        }

    }

    private static void setPromotionProduct(PromotionDefinition pd,
            List<PromotionProduct> promotionProducts,
            EntityManager em) {
        for (PromotionProduct promotionProduct : promotionProducts) {
            promotionProduct.setFreeProduct(em.find(Product.class,
                    promotionProduct.getFreeProductNumber()));
            pd.addPromotionProduct(promotionProduct);
        }
    }

}
