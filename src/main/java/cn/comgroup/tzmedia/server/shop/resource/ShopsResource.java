package cn.comgroup.tzmedia.server.shop.resource;

import cn.comgroup.tzmedia.server.shop.entity.Shop;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;
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

/**
 * @author peter.liu@comgroup.cn
 */
@Path("/shops/")
public class ShopsResource {

    @Context
    UriInfo uriInfo;

    @Context
    private ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    EntityManagerFactory emf;

    /**
     * Creates a new instance of Users.
     */
    public ShopsResource() {
    }

    public List<Shop> getShops(int shopId, Boolean o2o) {
        EntityManager em = emf.createEntityManager();
        if (shopId>0) {
            Query query = em.createNamedQuery("Shop.findByShopId");
            query.setParameter("shopId", shopId);
            return query.getResultList();
        } else if (o2o != null) {
            Query query = em.createNamedQuery("Shop.findByO2o");
            query.setParameter("o2o", o2o);
            return query.getResultList();
        } else {
            Query queryUR = em.createNamedQuery("Shop.findAll");
            return queryUR.getResultList();
        }
    }

    @Path("{shopId}/")
    public ShopResource getShop(@PathParam("shopId") int shopId) {
        return new ShopResource(emf, shopId, servletConfig, uriInfo);
    }
    

    /**
     * Method for shop creation
     *
     * @param shop
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postShop(final Shop shop) {
        final EntityManager em = emf.createEntityManager();
        Properties tzProperties = PropertiesUtils
                .getProperties(servletConfig.getServletContext());
        String share_base = tzProperties.getProperty("share-base-shop",
                "http://www.dudunangnang.com:9090/tzzjservice2/share/shop.html?type=");
        share_base += "shop";
        Query q = em.createNativeQuery("SELECT generator_value FROM id_generator i where generator_name='SHOPID_GEN'");
        BigDecimal maxShopId = (BigDecimal) q.getSingleResult();
        if(maxShopId==null){
            maxShopId = new BigDecimal(0);
        }
        maxShopId = maxShopId.add(new BigDecimal(1));
        if (shop.getShareUrl() == null || shop.getShareUrl().trim().equals("")) {
            shop.setShareUrl(share_base
                    + "&id=" + maxShopId);
        }

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(shop);
            }
        });
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Shop[] getShopsAsJsonArray(@QueryParam("shopId") int shopId,
            @QueryParam("o2o") Boolean o2o) {
        List<Shop> shops = getShops(shopId,o2o);
        return shops.toArray(new Shop[shops.size()]);
    }
}
