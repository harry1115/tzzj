package cn.comgroup.tzmedia.server.admin.resource;

import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.admin.entity.UserFeedback;
import cn.comgroup.tzmedia.server.util.query.QueryUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * UsersResource
 *
 * @author peter.liu@comgroup.cn
 */
@Path("/userfeedbacks/")
public class UserFeedbacksResource {

    @Context
    UriInfo uriInfo;

    @Context
    ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    EntityManagerFactory emf;

    /**
     * Creates a new instance of Users.
     */
    public UserFeedbacksResource() {
    }

    private List<UserFeedback> getUserFeedbacks(String userId,
            String fromDate, String toDate) throws ParseException {
        EntityManager em = emf.createEntityManager();
        if (QueryUtil.queryParameterProvided(userId)) {
            Query query = em.createNamedQuery("UserFeedback.findByUserId");
            query.setParameter("userId", userId);
            return query.getResultList();
        } else if (QueryUtil.queryParameterProvided(fromDate)
                || QueryUtil.queryParameterProvided(toDate)) {
            if (fromDate == null) {
                fromDate = toDate;
            }
            if (toDate == null) {
                toDate = fromDate;
            }
            Calendar from = Calendar.getInstance();
            from.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(fromDate));
            Calendar to = Calendar.getInstance();
            to.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(toDate));

            Query query = em.createNamedQuery("UserFeedback.findByDate");
            query.setParameter("fromDate", from);
            query.setParameter("toDate", to);
            return query.getResultList();
        } else {
            Query queryUR = em.createNamedQuery("UserFeedback.findAll");
            return queryUR.getResultList();
        }
    }

    /**
     * Method for user register
     *
     * @param userFeedback
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postUserFeedback(final UserFeedback userFeedback) {
        String userId = userFeedback.getUserId();
        final EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, userId);
        userFeedback.setUser(user);
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(userFeedback);
            }
        });
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserFeedback[] getUserFeedBacksAsJsonArray(
            @QueryParam("userId") String userId,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("resultLength") int resultLength) throws ParseException {
        List<UserFeedback> userFeedbacks = getUserFeedbacks(userId, fromDate,
                toDate);
        if (resultLength > 0 && resultLength < userFeedbacks.size()) {
            return userFeedbacks.subList(0, resultLength).toArray(new UserFeedback[resultLength]);
        } else {
            return userFeedbacks.toArray(new UserFeedback[userFeedbacks.size()]);
        }
    }
    
    @GET
    @Path("detail")
    public UserFeedback getUserFeedback(@QueryParam("feedbackNumber") long feedbackNumber) {
        final EntityManager em = emf.createEntityManager();
        UserFeedback userFeedback = em.find(UserFeedback.class, feedbackNumber);
        return userFeedback;
    }
    

    @DELETE
    public void deleteUserFeedback(@QueryParam("feedbackNumber") long feedbackNumber) {
        final EntityManager em = emf.createEntityManager();
        final UserFeedback userFeedback = em.find(UserFeedback.class, feedbackNumber);
        if (userFeedback != null) {
            TransactionManager.manage(new Transactional(em) {
                @Override
                public void transact() {
                    em.remove(userFeedback);
                }
            });
        }
    }
}
