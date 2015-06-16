package cn.comgroup.tzmedia.server.admin.resource;

import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.admin.entity.UserDevice;
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
@Path("/userdevices/")
public class UserDevicesResource {

    @Context
    UriInfo uriInfo;

    @Context
    ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    EntityManagerFactory emf;
    
    
    private final int USER_DEVICE_NOT_EXIST=2;

    /**
     * Creates a new instance of UserDevicesResource.
     */
    public UserDevicesResource() {
    }

    private List<UserDevice> getUserDevices(
            String userId,
            String devicetoken) throws ParseException {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserDevice> c = cb.createQuery(UserDevice.class);
        Root<UserDevice> userDevice = c.from(UserDevice.class);
        c.select(userDevice);
        c.distinct(true);
        List<Predicate> criteria = new ArrayList<>();
        if (QueryUtil.queryParameterProvided(userId)) {
            ParameterExpression<String> p = cb.parameter(String.class, "userId");
            if (userId.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(userDevice.<String>get("userId"), p));
            } else {
                criteria.add(cb.equal(userDevice.get("userId"), p));
            }
        }
        if (QueryUtil.queryParameterProvided(devicetoken)) {
            ParameterExpression<String> p = cb.parameter(String.class, "devicetoken");
            criteria.add(cb.equal(userDevice.get("devicetoken"), p));
        }

        if (criteria.size() == 1) {
            c.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[0])));
        }
        TypedQuery<UserDevice> q = em.createQuery(c);

        if (QueryUtil.queryParameterProvided(userId)) {
            q.setParameter("userId", userId
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }

        if (QueryUtil.queryParameterProvided(devicetoken)) {
            q.setParameter("devicetoken", devicetoken);
        }
        return q.getResultList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postUserDevice(final UserDevice userDevice) {
        final EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, userDevice.getUserId());
        if (user == null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("User device not valid, user not exist, userId: "
                            + userDevice.getUserId()
                            + "\n").build();
        }
        final UserDevice userDeviceEntity = em.find(UserDevice.class,
                userDevice.getUserId());
        if (userDeviceEntity != null) {
            userDeviceEntity.setDevicetoken(userDevice.getDevicetoken());
        }

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                if (userDeviceEntity != null) {
                    em.merge(userDeviceEntity);
                } else {
                    em.persist(userDevice);
                }
            }
        });
        return Response.ok().build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserDevice[] getUserDevicesAsJsonArray(
            @QueryParam("userId") String userId,
            @QueryParam("devicetoken") String devicetoken
    ) throws ParseException {
        List<UserDevice> userDevices = getUserDevices(
                userId, devicetoken);
        return userDevices.toArray(new UserDevice[userDevices.size()]);
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUserDevice(@QueryParam("userId") String userId) {
        final EntityManager em = emf.createEntityManager();
        final UserDevice userDeviceEntity = em.find(UserDevice.class, userId);
        if (userDeviceEntity != null) {
            TransactionManager.manage(new Transactional(em) {
                @Override
                public void transact() {
                    em.remove(userDeviceEntity);
                }
            });
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.CONFLICT)
                    .entity(USER_DEVICE_NOT_EXIST).build();
        }
    }
}
