package cn.comgroup.tzmedia.server.product.resource;

import cn.comgroup.tzmedia.server.product.entity.Product;
import cn.comgroup.tzmedia.server.product.entity.PromotionDefinition;
import cn.comgroup.tzmedia.server.product.entity.ProductType;
import cn.comgroup.tzmedia.server.product.entity.PromotionBuilder;
import cn.comgroup.tzmedia.server.product.entity.PromotionType;
import cn.comgroup.tzmedia.server.shop.entity.Shop;
import cn.comgroup.tzmedia.server.util.query.QueryUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.util.ArrayList;
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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
@Path("/products/")
public class ProductsResource {

    @Context
    UriInfo uriInfo;

    @Context
    private ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    EntityManagerFactory emf;

    /**
     * Creates a new instance of ProductsResource.
     */
    public ProductsResource() {
    }
    
    private List<Product> getProductsUsingCriteria(String productNumber, String productName,
            int shopId, int productType, Boolean sellable) {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> c = cb.createQuery(Product.class);
        Root<Product> product = c.from(Product.class);
        c.select(product);
        c.orderBy(cb.desc(product.get("ordering")));
        c.distinct(true);
        List<Predicate> criteria = new ArrayList<>();
        if (QueryUtil.queryParameterProvided(productNumber)) {
            ParameterExpression<String> p = cb.parameter(String.class, "productNumber");
            if (productNumber.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(product.<String>get("productNumber"), p));
            } else {
                criteria.add(cb.equal(product.get("productNumber"), p));
            }
        }
        if (QueryUtil.queryParameterProvided(productName)) {
            ParameterExpression<String> p = cb.parameter(String.class, "productName");
            if (productName.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(product.<String>get("productName"), p));
            } else {
                criteria.add(cb.equal(product.get("productName"), p));
            }
        }
        if (shopId>0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "shopId");
            criteria.add(cb.equal(product.get("shopId"), p));
        }
        if (productType > 0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "typeId");
            criteria.add(cb.equal(product.get("typeId"), p));
        }
        if (sellable != null) {
            ParameterExpression<Boolean> p = cb.parameter(Boolean.class, "sellable");
            criteria.add(cb.equal(product.get("sellable"), p));
        }
       
   
        
        if (criteria.size() == 1) {
            c.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[0])));
        }
        TypedQuery<Product> q = em.createQuery(c);
        if (QueryUtil.queryParameterProvided(productNumber)) {
            q.setParameter("productNumber", productNumber
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }
        if (QueryUtil.queryParameterProvided(productName)) {
            q.setParameter("productName", productName
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }
        if (shopId>0) {
            q.setParameter("shopId", shopId);
        }
        
        
        if (productType > 0) {
            q.setParameter("typeId", productType);
        }
        if (sellable != null) {
            q.setParameter("sellable", sellable);
        }
         List<Product> lObj= q.getResultList();
        return lObj; 
    }
     
     
     

    @Path("{productNumber}/")
    public ProductResource getProduct(@PathParam("productNumber") String productNumber) {
        return new ProductResource(emf, productNumber, servletConfig);
    }

    /**
     * Method for Product creation
     *
     * @param product
     * @return Response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postProduct(final Product product) {
        String productNumber = product.getProductNumber();
        final EntityManager em = emf.createEntityManager();
        
        if (em.find(Product.class, productNumber) != null) {
            return Response.status(Status.CONFLICT).entity("Product already exists!\n").build();
        }
        
        if (product.getShopId() >0) {
            product.setShop(em.find(Shop.class, product.getShopId()));
        }
        if (product.getTypeId() > 0) {
            product.setProductType(em.find(ProductType.class, product.getTypeId()));
        }
        if (product.isPromotion()) {
            PromotionBuilder
                    .build(product, em);
        } else {
            product.setPromotionDefinition(
                    new PromotionDefinition(product.getProductNumber(),PromotionType.BYPRODUCT));
        }

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(product);
//                em.persist(product.getPromotionDefinition());
            }
        });
        return Response.ok().build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Product[] getProductsAsJsonArray(
            @QueryParam("productNumber") String productNumber,
            @QueryParam("productName") String productName,
            @QueryParam("shopId") int shopId,
            @QueryParam("productType") int productType,
            @QueryParam("sellable") Boolean sellable,
            @QueryParam("resultLength") int resultLength) {
        List<Product> products = getProductsUsingCriteria(productNumber, productName,
                shopId, productType, sellable);
        
        if (resultLength > 0 && resultLength < products.size()) {
            return products.subList(0, resultLength).toArray(new Product[resultLength]);
        } else {
            return products.toArray(new Product[products.size()]);
        }
    }
}
