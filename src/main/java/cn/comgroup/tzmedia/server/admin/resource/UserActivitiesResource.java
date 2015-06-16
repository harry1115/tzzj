package cn.comgroup.tzmedia.server.admin.resource;

import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.admin.entity.UserActivity;
import cn.comgroup.tzmedia.server.shop.entity.Activity;
import cn.comgroup.tzmedia.server.util.query.QueryUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

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
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * UserActivitiesResource
 *
 * @author peter.liu@comgroup.cn
 */
@Path("/useractivities/")
public class UserActivitiesResource {

    @Context
    UriInfo uriInfo;

    @Context
    ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    EntityManagerFactory emf;

    /**
     * Creates a new instance of Users.
     */
    public UserActivitiesResource() {
    }

    private List<UserActivity> getUserActivities(
            String userId,
            int activityName) throws ParseException {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserActivity> c = cb.createQuery(UserActivity.class);
        Root<UserActivity> userActivity = c.from(UserActivity.class);
        c.select(userActivity);
        c.distinct(true);
        List<Predicate> criteria = new ArrayList<>();
        if (QueryUtil.queryParameterProvided(userId)) {
            ParameterExpression<String> p = cb.parameter(String.class, "userId");
            if (userId.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(userActivity.<String>get("userId"), p));
            } else {
                criteria.add(cb.equal(userActivity.get("userId"), p));
            }
        }
        if (activityName > 0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "activityName");
            criteria.add(cb.equal(userActivity.get("activityName"), p));
        }
        
        if (criteria.size() == 1) {
            c.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[0])));
        }
        TypedQuery<UserActivity> q = em.createQuery(c);

        if (QueryUtil.queryParameterProvided(userId)) {
            q.setParameter("userId", userId
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }
        
        if (activityName > 0) {
            q.setParameter("activityName", activityName);
        }
        return q.getResultList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postUserActivity(final UserActivity userActivity) {
        final EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, userActivity.getUserId());
        Activity activity=em.find(Activity.class, userActivity.getActivityName());
        
        UserActivity.UserActivityID uaID = new UserActivity.UserActivityID(
                userActivity.getUserId(), userActivity.getActivityName());
        if (em.find(UserActivity.class, uaID) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("User activity already exists! userId: "
                            + userActivity.getUserId()
                            + " activityName: "
                            + userActivity.getActivityName()
                            + "\n").build();
        }
        if (user == null || activity == null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("User activity not valid! userId: "
                            + userActivity.getUserId()
                            + " activityName: "
                            + userActivity.getActivityName()
                            + "\n").build();
        }
        
        userActivity.setUser(user);
        userActivity.setActivity(activity);
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(userActivity);
            }
        });
        return Response.ok().build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserActivity[] getUserActivitiesAsJsonArray(
             @QueryParam("userId") String userId,
            @QueryParam("activityName") int activityName
           ) throws ParseException {
        List<UserActivity> userActivities = getUserActivities(
                userId,activityName);
        return userActivities.toArray(new UserActivity[userActivities.size()]);
    }
    
    @DELETE
    public void deleteUserActivity(@QueryParam("userId") String userId,
            @QueryParam("activityName") int activityName) {
        final EntityManager em = emf.createEntityManager();
        UserActivity.UserActivityID uaID = new UserActivity.UserActivityID(userId, activityName);
        final UserActivity ua = em.find(UserActivity.class, uaID);
        if (ua != null) {
            TransactionManager.manage(new Transactional(em) {
                @Override
                public void transact() {
                    em.remove(ua);
                }
            });
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("attendeduser")
    public int getAttendedUsers(
            @QueryParam("activityName") int activityName){
        final EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("UserActivity.findAttendedUsers");
        query.setParameter("activityName", activityName);
        int attendedUsers = ((Number) query.getSingleResult()).intValue();
        return attendedUsers;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("attendedactivity")
    public int getAttendedActivities(
            @QueryParam("userId") String userId){
        final EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("UserActivity.findAttendedActivities");
        query.setParameter("userId", userId);
        int attendedActivities = ((Number) query.getSingleResult()).intValue();
        return attendedActivities;
    }
}
