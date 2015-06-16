package cn.comgroup.tzmedia.server.shop.resource;

import cn.comgroup.tzmedia.server.authentication.filter.AuthenticationConstants;
import cn.comgroup.tzmedia.server.frontpage.entity.FrontPage;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * ShopResource
 * peter.liu@comgroup.cn
 */
public class ShopResource {

    private final int shopId; // userid from url
    private final Shop shopEntity; // appropriate jpa user entity

//    UriInfo uriInfo; // actual uri info provided by parent resource
    private final EntityManagerFactory emf;
    private final EntityManager em; // entity manager provided by parent resource
    private final ServletConfig servletConfig;
    private final UriInfo uriInfo;
    private final String deployPath;

    /**
     * Creates a new instance of UserResource
     *
     * @param emf
     * @param shopId
     * @param servletConfig
     * @param uriInfo
     */
    public ShopResource(EntityManagerFactory emf, int shopId,ServletConfig servletConfig,
            UriInfo uriInfo) {
        this.shopId = shopId;
        this.emf=emf;
        this.em = emf.createEntityManager();
        this.shopEntity = em.find(Shop.class, shopId);
        this.servletConfig=servletConfig;
        this.uriInfo=uriInfo;
        this.deployPath = PropertiesUtils.getProperties(this.servletConfig
                .getServletContext()).getProperty("deploy-path");
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Shop getShop() {
        if (null == shopEntity) {
            throw new WebApplicationException(404);
        }
        return shopEntity;
    }

    /**
     * Method for update shop
     *
     * @param shop
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putShop(final Shop shop) {
        if (null == shopEntity) {
            throw new WebApplicationException(404);
        }

        shop.setShareUrl(uriInfo.getBaseUri().toString().replace("resources", "#")
                + "shop-share/" + shopId);
        shop.setShopImages(shopEntity.getShopImages());
            
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(shop);
                Query query = em.createNamedQuery("FrontPage.findByShopId");
                query.setParameter("shopId", shop.getShopId());
                List<FrontPage> frontPages=query.getResultList();
                if(!frontPages.isEmpty()){
                    FrontPage fp=frontPages.get(0);
                    fp.setTitle(shop.getShopName());
                    fp.setSubtitle(shop.getShopName());
                    fp.setContent(shop.getShopDesc());
                     em.merge(fp);
                }
            }
        });
        return Response.ok(AuthenticationConstants.UPDATESUCCESS).build();
    }

    @DELETE
    public void deleteShop() {
        if (null == shopEntity) {
            throw new WebApplicationException(404);
        }
        shopEntity.removeAllImagesAndDeleteOnFS(deployPath);
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.remove(shopEntity);
            }
        });
    }
    
    @Path("images/")
    public ShopImagesResource uploadShopImage() {
        return new ShopImagesResource(emf.createEntityManager(), 
                shopEntity,servletConfig);
    }
}
