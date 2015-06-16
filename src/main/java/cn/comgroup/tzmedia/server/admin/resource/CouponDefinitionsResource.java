package cn.comgroup.tzmedia.server.admin.resource;

import cn.comgroup.tzmedia.server.admin.entity.CouponDefinition;
import cn.comgroup.tzmedia.server.admin.entity.CouponStatus;
import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.admin.entity.UserCoupon;
import cn.comgroup.tzmedia.server.util.query.QueryUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * CouponDefinitionsResource
 *
 * @author peter.liu@comgroup.cn
 */
@Path("/coupondefinitions/")
public class CouponDefinitionsResource {

    @Context
    UriInfo uriInfo;

    @Context
    ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    EntityManagerFactory emf;
    
    private final int COUPON_USED=2;

    /**
     * Creates a new instance of Users.
     */
    public CouponDefinitionsResource() {
    }

    private List<CouponDefinition> getCouponDefinitions(
            long couponDefinitionNumber,
            String fromDate, String toDate) throws ParseException {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CouponDefinition> c = cb.createQuery(CouponDefinition.class);
        Root<CouponDefinition> couponDefinition = c.from(CouponDefinition.class);
        c.select(couponDefinition);
        c.orderBy(cb.asc(couponDefinition.get("couponDefinitionNumber")));
        c.distinct(true);
        List<Predicate> criteria = new ArrayList<>();
        if (couponDefinitionNumber > 0) {
            ParameterExpression<Long> p = cb.parameter(Long.class, "couponDefinitionNumber");
            criteria.add(cb.equal(couponDefinition.get("couponDefinitionNumber"), p));
        }
       
        if (QueryUtil.queryParameterProvided(fromDate)) {
            ParameterExpression<Calendar> p = cb.parameter(Calendar.class, "fromDate");
            criteria.add(cb.greaterThanOrEqualTo(couponDefinition.<Calendar>get("expiryDate"), p));
        }

        if (QueryUtil.queryParameterProvided(toDate)) {
            ParameterExpression<Calendar> p = cb.parameter(Calendar.class, "toDate");
            criteria.add(cb.lessThanOrEqualTo(couponDefinition.<Calendar>get("expiryDate"), p));
        }

        if (criteria.size() == 1) {
            c.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[0])));
        }
        TypedQuery<CouponDefinition> q = em.createQuery(c);

        if (couponDefinitionNumber > 0) {
            q.setParameter("couponDefinitionNumber", couponDefinitionNumber);
        }

        if (QueryUtil.queryParameterProvided(fromDate)) {
            Calendar fromExpiryDate = Calendar.getInstance();
            fromExpiryDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(fromDate));
            q.setParameter("fromDate", fromExpiryDate);
        }

        if (QueryUtil.queryParameterProvided(toDate)) {
            Calendar toExpiryDate = Calendar.getInstance();
            toExpiryDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(toDate));
            q.setParameter("toDate", toExpiryDate);
        }

        return q.getResultList();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCouponDefinition(final CouponDefinition couponDefinition) {
        final EntityManager em = emf.createEntityManager();
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(couponDefinition);
                if (couponDefinition.isForAllUser()) {
                    Query queryU = em.createNamedQuery("User.findAll");
                    List<User> users = queryU.getResultList();
                    for (User user : users) {
                        UserCoupon userCoupon = new UserCoupon(user.getUserId());
                        userCoupon.setUser(user);
                        userCoupon.setCouponDefinitionNumber(
                                couponDefinition.getCouponDefinitionNumber());
                        userCoupon.setCouponDefinition(couponDefinition);
                        em.persist(userCoupon);
                    }
                }
            }
        });
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CouponDefinition[] getCouponDefinitionsAsJsonArray(
            @QueryParam("couponDefinitionNumber") long couponDefinitionNumber,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate) throws ParseException {
        List<CouponDefinition> couponDefinitions = getCouponDefinitions(
                couponDefinitionNumber,
                fromDate, toDate);
        return couponDefinitions.toArray(new CouponDefinition[couponDefinitions.size()]);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("detail")
    public Response putCouponDefinition(final CouponDefinition couponDefinition) {
        final EntityManager em = emf.createEntityManager();
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(couponDefinition);
                if (couponDefinition.isForAllUser()) {
                    Query queryU = em.createNamedQuery("User.findAll");
                    List<User> users = queryU.getResultList();
                    for (User user : users) {
                        Query queryUC = em.createNamedQuery("UserCoupon.findByUserIdAndCDNumber");
                        queryUC.setParameter("userId", user.getUserId());
                        queryUC.setParameter("couponDefinitionNumber", couponDefinition.getCouponDefinitionNumber());
                        List<UserCoupon> ucList = queryUC.getResultList();
                        if (ucList.isEmpty()) {
                            UserCoupon userCoupon = new UserCoupon(user.getUserId());
                            userCoupon.setUser(user);
                            userCoupon.setCouponDefinitionNumber(
                                    couponDefinition.getCouponDefinitionNumber());
                            userCoupon.setCouponDefinition(couponDefinition);
                            em.persist(userCoupon);
                        }
                    }
                }
            }
        });
        return Response.ok().build();
    }
    
    

    @GET
    @Path("detail")
    public CouponDefinition getCouponDefinition(@QueryParam("couponDefinitionNumber") long couponDefinitionNumber) {
        final EntityManager em = emf.createEntityManager();
        CouponDefinition couponDefinition=em.find(CouponDefinition.class, couponDefinitionNumber);
        return couponDefinition;
    }

    @DELETE
    public Response deleteCouponDefinition(
            @QueryParam("couponDefinitionNumber") long couponDefinitionNumber) {
        final EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("UserCoupon.findCountByCDNumberAndStatus");
        query.setParameter("couponDefinitionNumber", couponDefinitionNumber);
        query.setParameter("couponStatus", CouponStatus.USED);
        int numberOfUCUsed = ((Number) query.getSingleResult()).intValue();
        if (numberOfUCUsed > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(COUPON_USED).build();
        }
        final CouponDefinition couponDefinition = em.find(CouponDefinition.class, couponDefinitionNumber);
        if (couponDefinition != null) {
            TransactionManager.manage(new Transactional(em) {
                @Override
                public void transact() {
                    em.remove(couponDefinition);
                }
            });
        }
        return Response.ok().build();
    }
}
