package cn.comgroup.tzmedia.server.shop.resource;

import cn.comgroup.tzmedia.server.authentication.filter.AuthenticationConstants;
import cn.comgroup.tzmedia.server.exception.ExtendedNotFoundException;
import cn.comgroup.tzmedia.server.frontpage.entity.FrontPage;
import cn.comgroup.tzmedia.server.shop.entity.Activity;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * peter.liu@comgroup.cn
 */
public class ActivityResource {

    private final int activityName;
    private final Activity activityEntity;
    private final EntityManagerFactory emf;
    private final EntityManager em; // entity manager provided by parent resource
    private final ServletConfig servletConfig;
    private final UriInfo uriInfo;
    private final String deployPath;

    /**
     * Creates a new instance of ShopActivityResource
     *
     * @param emf
     * @param activityName
     * @param servletConfig
     * @param uriInfo
     * 
     */
    public ActivityResource(EntityManagerFactory emf, int activityName,
            ServletConfig servletConfig, UriInfo uriInfo) {
        this.activityName = activityName;
        this.emf = emf;
        this.em = emf.createEntityManager();
        this.activityEntity = em.find(Activity.class, activityName);
        this.servletConfig = servletConfig;
        this.uriInfo = uriInfo;
        this.deployPath = PropertiesUtils.getProperties(this.servletConfig
                .getServletContext()).getProperty("deploy-path");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Activity getActivity() {
        if (null == activityEntity) {
            throw new ExtendedNotFoundException("Activity " + activityName + "does not exist!");
        }
        return activityEntity;
    }

    /**
     * Method for update activity
     *
     * @param activity
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putActivity(final Activity activity) {
        if (null == activityEntity) {
            throw new ExtendedNotFoundException("Activity " + activityName + "does not exist!");
        }
        activity.setActivityImages(activityEntity.getActivityImages());

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(activity);
                Query query = em.createNamedQuery("FrontPage.findByActivityName");
                query.setParameter("activityName", activity.getActivityName());
                List<FrontPage> frontPages=query.getResultList();
                if(!frontPages.isEmpty()){
                    FrontPage fp=frontPages.get(0);
                    fp.setTitle(activity.getActivitySubject());
                    fp.setSubtitle(activity.getActivitySubject());
                    fp.setContent(activity.getActivityDesc());
                    em.merge(fp);
                }
            }
        });
        return Response.ok(AuthenticationConstants.UPDATESUCCESS).build();
    }

    @DELETE
    public void deleteActivity() {
        if (null == activityEntity) {
            throw new ExtendedNotFoundException("Activity " + activityName + "does not exist!");
        }
        activityEntity.removeAllImagesAndDeleteOnFS(deployPath);
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.remove(activityEntity);
            }
        });
    }

    @Path("images/")
    public ActivityImagesResource uploadActivityImage() {
        return new ActivityImagesResource(emf.createEntityManager(),
                activityEntity,servletConfig);
    }
}
