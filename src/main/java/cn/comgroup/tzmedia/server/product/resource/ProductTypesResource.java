package cn.comgroup.tzmedia.server.product.resource;

import cn.comgroup.tzmedia.server.product.entity.ProductType;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.util.List;
import javax.persistence.EntityManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * ProductsResource
 *
 * @author peter.liu@comgroup.cn
 */
@Path("/producttypes/")
public class ProductTypesResource {

    @Context
    UriInfo uriInfo;

    @Context
    private ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    EntityManagerFactory emf;

    /**
     * Creates a new instance of ProductsResource.
     */
    public ProductTypesResource() {
    }

    private List<ProductType> getProductTypes(int typeId,int shopId) {
        EntityManager em = emf.createEntityManager();
        if (typeId > 0) {
            Query query = em.createNamedQuery("ProductType.findByTypeId");
            query.setParameter("typeId", typeId);
          
            return query.getResultList();
        }else if(shopId > 0){
              Query query = em.createNamedQuery("ProductType.findByShopId");
            query.setParameter("shopId", shopId);
          
            return query.getResultList();
        }
        else {
            Query queryUR = em.createNamedQuery("ProductType.findAll");
            return queryUR.getResultList();
        }
    }

    @Path("{typeId}/")
    public ProductTypeResource getProduct(@PathParam("typeId") int typeId) {
        return new ProductTypeResource(emf.createEntityManager(), typeId, servletConfig);
    }

    /**
     * Method for Product type creation
     *
     * @param productType
     * @return Response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postProductType(final ProductType productType) {
        int typeId = productType.getTypeId();
        final EntityManager em = emf.createEntityManager();

        if (em.find(ProductType.class, typeId) != null) {
            return Response.status(Status.CONFLICT)
                    .entity("ProductType " + typeId + " already exists!\n")
                    .build();
        }

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(productType);
            }
        });
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ProductType[] getProductTypesAsJsonArray(
            @QueryParam("typeId") int typeId,
            @QueryParam("shopId") int shopId) {
        List<ProductType> productTypes = getProductTypes(typeId,shopId);
        return productTypes.toArray(new ProductType[productTypes.size()]);
    }

}
