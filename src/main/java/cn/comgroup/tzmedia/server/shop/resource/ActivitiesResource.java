package cn.comgroup.tzmedia.server.shop.resource;

import cn.comgroup.tzmedia.server.shop.entity.Activity;
import cn.comgroup.tzmedia.server.shop.entity.Shop;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import cn.comgroup.tzmedia.server.util.query.QueryUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author peter.liu@comgroup.cn
 */
@Path("/activities/")
public class ActivitiesResource {

    @Context
    UriInfo uriInfo; 

    @Context
    private ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    EntityManagerFactory emf;

    /**
     * Creates a new instance of ActivitiesResource.
     */
    public ActivitiesResource() {
    }
    
    private List<Activity> getActivitiesUsingCriteria(int activityName, String activitySubject,
            String activityDate,int shopId) throws ParseException {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Activity> c = cb.createQuery(Activity.class);
        Root<Activity> activity = c.from(Activity.class);
        c.select(activity);
        c.orderBy(cb.desc(activity.get("ordering")),cb.desc(activity.get("fromDate")));
        c.distinct(true);
        List<Predicate> criteria = new ArrayList<>();
        if (QueryUtil.queryParameterProvided(activitySubject)) {
            ParameterExpression<String> p = cb.parameter(String.class, "activitySubject");
            if (activitySubject.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(activity.<String>get("activitySubject"), p));
            } else {
                criteria.add(cb.equal(activity.get("activitySubject"), p));
            }
        }
        
        if (QueryUtil.queryParameterProvided(activityDate)) {
            ParameterExpression<Calendar> p = cb.parameter(Calendar.class, "activityDate");
            criteria.add(cb.lessThanOrEqualTo(activity.<Calendar>get("fromDate"), p));
            criteria.add(cb.greaterThanOrEqualTo(activity.<Calendar>get("toDate"), p));
        }
        
        
        if (shopId>0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "shopId");
            criteria.add(cb.equal(activity.get("shopId"), p));
        }
        if (activityName > 0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "activityName");
            criteria.add(cb.equal(activity.get("activityName"), p));
        }
        
        if (criteria.size() == 1) {
            c.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[0])));
        }
        TypedQuery<Activity> q = em.createQuery(c);
        if (QueryUtil.queryParameterProvided(activitySubject)) {
            q.setParameter("activitySubject", activitySubject
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }
        
        if (QueryUtil.queryParameterProvided(activityDate)) {
            Calendar aDate = Calendar.getInstance();
            aDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(activityDate));
            q.setParameter("activityDate", aDate);
        }
       
        if (shopId>0) {
            q.setParameter("shopId", shopId);
        }
        if (activityName > 0) {
            q.setParameter("activityName", activityName);
        }
        return q.getResultList();
    }
    
    

    @Path("{activityName}/")
    public ActivityResource getActivity(@PathParam("activityName") int activityName) {
        return new ActivityResource(emf, 
                activityName, servletConfig,uriInfo);
    }

    /**
     * Method for shop activity creation
     *
     * @param activity
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postActivity(final Activity activity) {
        final EntityManager em = emf.createEntityManager();
        Properties tzProperties = PropertiesUtils
                .getProperties(servletConfig.getServletContext());
        String share_base = tzProperties.getProperty("share-base-activity",
                "http://www.dudunangnang.com:9090/tzzjservice2/share/activity.html?type=");
        share_base += "activity";
        Query q = em.createNativeQuery("SELECT generator_value FROM id_generator i where generator_name='ACTIVITYNAME_GEN'");
        BigDecimal maxActivityName = (BigDecimal) q.getSingleResult();
        if(maxActivityName==null){
            maxActivityName= new BigDecimal(0);
        }
        maxActivityName = maxActivityName.add(new BigDecimal(1));
        
        Shop shop = em.find(Shop.class, activity.getShopId());
        activity.setOwner(shop);
        if (activity.getShareUrl() == null || activity.getShareUrl().trim().equals("")) {
            activity.setShareUrl(share_base
                    + "&id=" + maxActivityName);
        }
        
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(activity);
            }
        });
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Activity[] getActivitiesAsJsonArray(
            @QueryParam("activityName") int activityName,
            @QueryParam("activitySubject") String activitySubject,
            @QueryParam("activityDate") String activityDate,
            @QueryParam("shopId") int shopId,
            @QueryParam("resultLength") int resultLength) throws ParseException {
        List<Activity> activities = getActivitiesUsingCriteria(
                activityName, 
                activitySubject, 
                activityDate,
                shopId);
        if (resultLength > 0 && resultLength < activities.size()) {
            return activities.subList(0, resultLength).toArray(new Activity[resultLength]);
        } else {
            return activities.toArray(new Activity[activities.size()]);
        }
    }
}
