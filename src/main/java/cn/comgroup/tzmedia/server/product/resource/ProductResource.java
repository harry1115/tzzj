package cn.comgroup.tzmedia.server.product.resource;

import cn.comgroup.tzmedia.server.authentication.filter.AuthenticationConstants;
import cn.comgroup.tzmedia.server.product.entity.Product;
import cn.comgroup.tzmedia.server.product.entity.PromotionDefinition;
import cn.comgroup.tzmedia.server.product.entity.ProductType;
import cn.comgroup.tzmedia.server.product.entity.PromotionBuilder;
import cn.comgroup.tzmedia.server.product.entity.PromotionProduct;
import cn.comgroup.tzmedia.server.product.entity.PromotionType;
import cn.comgroup.tzmedia.server.shop.entity.Playbill;
import cn.comgroup.tzmedia.server.shop.entity.Shop;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletConfig;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * ProductResource peter.liu@comgroup.cn
 */
public class ProductResource {

    private final String productNumber; // productNumber from url
    private final Product productEntity; // appropriate jpa user entity

    private final EntityManagerFactory emf;
    private final EntityManager em; // entity manager provided by parent resource
    private final ServletConfig servletConfig;
    private final String deployPath;

    private final int ORDER_EXIST_FOR_PRODUCT = 2;
    private final int PRODUCT_IS_FREE_PRODUCT = 3;

    /**
     * Creates a new instance of ProductResource
     *
     * @param emf
     * @param productNumber
     * @param servletConfig
     */
    public ProductResource(EntityManagerFactory emf, String productNumber, ServletConfig servletConfig) {
        this.productNumber = productNumber;
        this.emf=emf;
        this.em = emf.createEntityManager();
        this.productEntity = em.find(Product.class, productNumber);
        this.servletConfig = servletConfig;
        this.deployPath = PropertiesUtils.getProperties(this.servletConfig
                .getServletContext()).getProperty("deploy-path");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Product getProduct() {
        if (null == productEntity) {
            throw new WebApplicationException(404);
        }
        return productEntity;
    }

    /**
     * Method for update shop
     *
     * @param product
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putProduct(final Product product) {
        if (null == productEntity) {
            throw new WebApplicationException(404);
        }

        if (product.getShopId() > 0) {
            product.setShop(em.find(Shop.class, product.getShopId()));
        }

        if (product.getTypeId() > 0) {
            product.setProductType(em.find(ProductType.class, product.getTypeId()));
        }

        if (product.isPromotion()) {
            PromotionBuilder.build(product, em);
        } else {
            product.setPromotionDefinition(
                    new PromotionDefinition(product.getProductNumber(), PromotionType.BYPRODUCT));
        }
        product.setProductImages(productEntity.getProductImages());

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(product.getPromotionDefinition());
                em.merge(product);
            }
        });
        return Response.ok(AuthenticationConstants.UPDATESUCCESS).build();
    }

    @DELETE
    public Response deleteProduct() {
        if (null == productEntity) {
            throw new WebApplicationException(404);
        }

        Query query = em.createNamedQuery("CustomerOrderLine.findLinesByProductNumber");
        query.setParameter("productNumber", productNumber);
        int numberOfCOL = ((Number) query.getSingleResult()).intValue();
        if (numberOfCOL > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ORDER_EXIST_FOR_PRODUCT).build();
        }

        query = em.createNamedQuery("PromotionProduct.findByFreeProductNumber");
        query.setParameter("freeProductNumber", productEntity.getProductNumber());
        final List<PromotionProduct> pps = query.getResultList();

        productEntity.removeAllImagesAndDeleteOnFS(deployPath);
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                for (PromotionProduct pp : pps) {
                    em.remove(pp);
                }
                em.remove(productEntity);
            }
        });
        return Response.ok().build();
    }

    @Path("images/")
    public ProductImagesResource uploadProductImage() {
        return new ProductImagesResource(emf.createEntityManager(), 
                productEntity, servletConfig);
    }
}
