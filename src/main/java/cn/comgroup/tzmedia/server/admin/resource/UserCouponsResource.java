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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * UserCouponsResource
 *
 * @author peter.liu@comgroup.cn
 */
@Path("/usercoupons/")
public class UserCouponsResource {

    @Context
    UriInfo uriInfo;

    @Context
    ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    EntityManagerFactory emf;

    /**
     * Creates a new instance of Users.
     */
    public UserCouponsResource() {
    }

    private List<UserCoupon> getUserCoupons(
            long couponNumber, String userId,
            String fromDate, String toDate, CouponStatus couponStatus) throws ParseException {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserCoupon> c = cb.createQuery(UserCoupon.class);
        Root<UserCoupon> userCoupon = c.from(UserCoupon.class);
        c.select(userCoupon);
        c.orderBy(cb.asc(userCoupon.get("couponNumber")));
        c.distinct(true);
        List<Predicate> criteria = new ArrayList<>();
        if (couponNumber > 0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "couponNumber");
            criteria.add(cb.equal(userCoupon.get("couponNumber"), p));
        }
        if (QueryUtil.queryParameterProvided(userId)) {
            ParameterExpression<String> p = cb.parameter(String.class, "userId");
            if (userId.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(userCoupon.<String>get("userId"), p));
            } else {
                criteria.add(cb.equal(userCoupon.get("userId"), p));
            }
        }

        if (QueryUtil.queryParameterProvided(fromDate)) {
            ParameterExpression<Calendar> p = cb.parameter(Calendar.class, "fromDate");
            criteria.add(cb.greaterThanOrEqualTo(userCoupon.<Calendar>get("expiryDate"), p));
        }

        if (QueryUtil.queryParameterProvided(toDate)) {
            ParameterExpression<Calendar> p = cb.parameter(Calendar.class, "toDate");
            criteria.add(cb.lessThanOrEqualTo(userCoupon.<Calendar>get("expiryDate"), p));
        }

        if (couponStatus != null) {
            ParameterExpression<CouponStatus> p = cb
                    .parameter(CouponStatus.class, "couponStatus");
            criteria.add(cb.equal(userCoupon.get("couponStatus"), p));
        }

        if (criteria.size() == 1) {
            c.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[0])));
        }
        TypedQuery<UserCoupon> q = em.createQuery(c);

        if (couponNumber > 0) {
            q.setParameter("couponNumber", couponNumber);
        }

        if (QueryUtil.queryParameterProvided(userId)) {
            q.setParameter("userId", userId
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
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

        if (couponStatus != null) {
            q.setParameter("couponStatus", couponStatus);
        }

        return q.getResultList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postUserCoupon(final UserCoupon userCoupon) {
        String userId = userCoupon.getUserId();
        final EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, userId);
        userCoupon.setUser(user);
        if (user == null) {
            Logger.getLogger(UserCouponsResource.class.getName())
                    .log(Level.INFO, "######### UserCoupon {0} has no user associated",
                            userCoupon.getUserId());
            return Response.status(Response.Status.CONFLICT).build();
        }

        CouponDefinition cd = em.find(CouponDefinition.class, userCoupon.getCouponDefinitionNumber());
        userCoupon.setCouponDefinition(cd);
        if (cd == null) {
            String[] keyForLog = {userCoupon.getUserId(), String.valueOf(userCoupon.getCouponDefinitionNumber())};
            Logger.getLogger(UserCouponsResource.class.getName())
                    .log(Level.INFO, "######### UserCoupon {0} has no CouponDefinition {1} associated",
                            keyForLog);
            return Response.status(Response.Status.CONFLICT).build();
        }

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(userCoupon);
            }
        });
        return Response.ok().build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserCoupon[] getUserCouponsAsJsonArray(
            @QueryParam("couponNumber") long couponNumber,
            @QueryParam("userId") String userId,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("couponStatus") CouponStatus couponStatus) throws ParseException {
        List<UserCoupon> userCoupons = getUserCoupons(
                couponNumber, userId,
                fromDate, toDate, couponStatus);
        final List<UserCoupon> expireduserCoupons = new LinkedList<>();
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        for (UserCoupon userCoupon : userCoupons) {
            if (userCoupon.getCouponStatus().equals(CouponStatus.NEW)
                    && userCoupon.getExpiryDate().before(today)) {
                userCoupon.setCouponStatus(CouponStatus.EXPIRED);
                expireduserCoupons.add(userCoupon);
            }
        }
        final EntityManager em = emf.createEntityManager();
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                for (UserCoupon userCoupon : expireduserCoupons) {
                    em.merge(userCoupon);
                }
            }
        });
        return userCoupons.toArray(new UserCoupon[userCoupons.size()]);
    }

    @GET
    @Path("detail")
    public UserCoupon getUserCoupon(@QueryParam("couponNumber") long couponNumber) {
        final EntityManager em = emf.createEntityManager();
        final UserCoupon userCoupon = em.find(UserCoupon.class, couponNumber);
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        if(userCoupon.getCouponStatus().equals(CouponStatus.NEW)
                && userCoupon.getExpiryDate().before(today)) {
            userCoupon.setCouponStatus(CouponStatus.EXPIRED);
            TransactionManager.manage(new Transactional(em) {
                @Override
                public void transact() {
                    em.merge(userCoupon);
                }
            });
        }
        return userCoupon;
    }

    @DELETE
    public void deleteUserCoupon(@QueryParam("couponNumber") long couponNumber) {
        final EntityManager em = emf.createEntityManager();
        final UserCoupon userCoupon = em.find(UserCoupon.class, couponNumber);
        if (userCoupon != null) {
            TransactionManager.manage(new Transactional(em) {
                @Override
                public void transact() {
                    em.remove(userCoupon);
                }
            });
        }
    }
}
