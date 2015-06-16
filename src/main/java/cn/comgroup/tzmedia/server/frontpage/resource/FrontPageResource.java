package cn.comgroup.tzmedia.server.frontpage.resource;

import cn.comgroup.tzmedia.server.admin.entity.Administrator;
import cn.comgroup.tzmedia.server.authentication.filter.AuthenticationConstants;
import cn.comgroup.tzmedia.server.frontpage.entity.FrontPage;
import cn.comgroup.tzmedia.server.shop.entity.Activity;
import cn.comgroup.tzmedia.server.shop.entity.Shop;
import cn.comgroup.tzmedia.server.singer.entity.Singer;
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
import javax.ws.rs.core.UriInfo;

/**
 * FrontPageResource 
 * peter.liu@comgroup.cn
 */
public class FrontPageResource {

    private final FrontPage frontPageEntity; // appropriate jpa user entity
//    UriInfo uriInfo; // actual uri info provided by parent resource
    private final EntityManager em; // entity manager provided by parent resource
    private final ServletConfig servletConfig;
    private final UriInfo uriInfo;
    private final String deployPath;

    /**
     * Creates a new instance of FrontPageResource
     *
     * @param em
     * @param frontPageId
     * @param servletConfig
     * @param uriInfo
     */
    public FrontPageResource(EntityManager em, int frontPageId, 
            ServletConfig servletConfig,UriInfo uriInfo) {
        this.em = em;
        this.frontPageEntity = em.find(FrontPage.class, frontPageId);
        this.servletConfig = servletConfig;
        this.uriInfo=uriInfo;
        this.deployPath = PropertiesUtils.getProperties(this.servletConfig
                .getServletContext()).getProperty("deploy-path");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public FrontPage getFrontPage() {
        if (null == frontPageEntity) {
            throw new WebApplicationException(404);
        }
        return frontPageEntity;
    }

    /**
     * Method for update FrontPage
     *
     * @param frontPage
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putFrontPage(final FrontPage frontPage) {
        if (frontPage.getUserId() != null) {
            frontPage.setAdministrator(em.find(Administrator.class,
                    frontPage.getUserId()));
        }
        
        if (frontPage.getShopId()>0) {
            frontPage.setShop(em.find(Shop.class,
                    frontPage.getShopId()));
        } else {
            frontPage.setShop(null);
        }
        
        if (frontPage.getActivityName() >0) {
            frontPage.setActivity(em.find(Activity.class,
                    frontPage.getActivityName()));
        } else {
            frontPage.setActivity(null);
        }
        
        if (frontPage.getSingerId() >0) {
            frontPage.setSinger(em.find(Singer.class,
                    frontPage.getSingerId()));
        } else {
            frontPage.setSinger(null);
        }
        
        frontPage.setShareUrl(uriInfo.getBaseUri().toString().replace("resources", "#")
                + "frontpage-share/" + frontPage.getFrontPageId());

        frontPage.setFrontPageImages(frontPageEntity.getFrontPageImages());
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(frontPage);
            }
        });
        return Response.ok(AuthenticationConstants.UPDATESUCCESS).build();
    }

    @DELETE
    public void deleteFrontPage() {
        if (null == frontPageEntity) {
            throw new WebApplicationException(404);
        }
        frontPageEntity.removeAllImagesAndDeleteOnFS(deployPath);
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.remove(frontPageEntity);
            }
        });
    }

    @Path("images/")
    public FrontPageImagesResource uploadFrontPageImage() {
        return new FrontPageImagesResource(em, frontPageEntity, servletConfig);
    }
}
