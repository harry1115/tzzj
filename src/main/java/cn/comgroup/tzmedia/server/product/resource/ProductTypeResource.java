package cn.comgroup.tzmedia.server.product.resource;

import cn.comgroup.tzmedia.server.authentication.filter.AuthenticationConstants;
import cn.comgroup.tzmedia.server.product.entity.ProductType;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import javax.persistence.EntityManager;
import javax.servlet.ServletConfig;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * ProductTypeResource peter.liu@comgroup.cn
 */
public class ProductTypeResource {

    private final ProductType productTypeEntity; // appropriate jpa user entity

//    UriInfo uriInfo; // actual uri info provided by parent resource
    private final EntityManager em; // entity manager provided by parent resource
    private final ServletConfig servletConfig;
    private final String deployPath;

    /**
     * Creates a new instance of ProductResource
     *
     * @param em
     * @param typeId
     * @param servletConfig
     */
    public ProductTypeResource(EntityManager em, int typeId, ServletConfig servletConfig) {
        this.em = em;
        this.productTypeEntity = em.find(ProductType.class, typeId);
        this.servletConfig = servletConfig;
        this.deployPath = PropertiesUtils.getProperties(this.servletConfig
                .getServletContext()).getProperty("deploy-path");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ProductType getProductType() {
        if (null == productTypeEntity) {
            throw new WebApplicationException(404);
        }
        return productTypeEntity;
    }

    /**
     * Method for update ProductType
     *
     * @param productType
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putProductType(final ProductType productType) {
        if (null == productTypeEntity) {
            throw new WebApplicationException(404);
        }

        productTypeEntity.setTypeName(productType.getTypeName());
        productTypeEntity.setShopId(productType.getShopId());

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(productTypeEntity);
            }
        });
        return Response.ok(AuthenticationConstants.UPDATESUCCESS).build();
    }

    @DELETE
    public void deleteProductType() {
        if (null == productTypeEntity) {
            throw new WebApplicationException(404);
        }
        productTypeEntity.removeAllImagesAndDeleteOnFS(deployPath);
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.remove(productTypeEntity);
            }
        });
    }

    @Path("images/")
    public ProductTypeImagesResource uploadProductTypeImage() {
        return new ProductTypeImagesResource(em, productTypeEntity, servletConfig);
    }
}
