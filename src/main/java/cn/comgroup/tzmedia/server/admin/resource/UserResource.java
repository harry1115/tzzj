package cn.comgroup.tzmedia.server.admin.resource;

import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.admin.entity.UserCoupon;
import cn.comgroup.tzmedia.server.admin.entity.UserFeedback;
import cn.comgroup.tzmedia.server.admin.entity.UserToken;
import cn.comgroup.tzmedia.server.authentication.filter.AuthenticationConstants;
import cn.comgroup.tzmedia.server.exception.ExtendedNotFoundException;
import cn.comgroup.tzmedia.server.shop.entity.UserAction;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletConfig;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * peter.liu@comgroup.cn
 */
public class UserResource {

    private final String userId; // userid from url
    private final User userEntity; // appropriate jpa user entity

    private final UriInfo uriInfo; // actual uri info provided by parent resource
    private final EntityManagerFactory emf;
    private final EntityManager em; // entity manager provided by parent resource
    private final ServletConfig servletConfig;
    
    private final int USER_NOT_EXIST=2;
    private final int USER_HAS_ORDER=3;

    /**
     * Creates a new instance of UserResource
     *
     * @param uriInfo
     * @param emf
     * @param userId
     * @param servletConfig
     */
    public UserResource(UriInfo uriInfo, EntityManagerFactory emf,
            String userId,ServletConfig servletConfig) {
        this.uriInfo = uriInfo;
        this.userId = userId;
        this.emf=emf;
        this.em = emf.createEntityManager();
        userEntity = em.find(User.class, userId);
        this.servletConfig=servletConfig;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser() {
        if (null == userEntity) {
            throw new ExtendedNotFoundException("userId " + userId + "does not exist!");
        }
        return userEntity;
    }

    /**
     * Method for update user
     *
     * @param user
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putUser(final User user) {
        if (null == userEntity) {
            throw new ExtendedNotFoundException("userId " + userId + "does not exist!");
        }
        user.setUserImages(userEntity.getUserImages());
        user.setVersion(userEntity.getVersion());
        if(user.getUserName()!=null){
            user.setUserName(user.getUserName());
        }
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(user);
            }
        });
        return Response.ok(AuthenticationConstants.UPDATESUCCESS).build();
    }
    

    @DELETE
    public Response deleteUser() {
        if (null == userEntity) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(USER_NOT_EXIST).build();
        }
        Query queryOrder = em.createNamedQuery("CustomerOrder.findCountByUserId");
        queryOrder.setParameter("userId", userId);
        int numberOfOrders=((Number)queryOrder.getSingleResult()).intValue();
        if(numberOfOrders>0){
            return Response.status(Response.Status.CONFLICT)
                    .entity(USER_HAS_ORDER).build();
        }
        
        Query query = em.createNamedQuery("UserFeedback.findByUserId");
        query.setParameter("userId", userId);
        final List<UserFeedback> userFeedBacks=query.getResultList();
        
        Query queryUserAction = em.createNamedQuery("UserAction.findByUserId");
        queryUserAction.setParameter("userId", userId);
        final List<UserAction> userActions = queryUserAction.getResultList();
        
        
        Query queryUserCoupon = em.createNamedQuery("UserCoupon.findByUserId");
        queryUserCoupon.setParameter("userId", userId);
        final List<UserCoupon> userCoupons = queryUserCoupon.getResultList();
        
        

        String deployPath = PropertiesUtils.getProperties(this.servletConfig
                .getServletContext()).getProperty("deploy-path");
        userEntity.removeAllImagesAndDeleteOnFS(deployPath);
        final UserToken userToken = em.find(UserToken.class, userEntity.getUserId());
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                for (UserAction ua : userActions) {
                    em.remove(ua);
                }
                for (UserFeedback ufb : userFeedBacks) {
                    em.remove(ufb);
                }
                for (UserCoupon uc : userCoupons) {
                    em.remove(uc);
                }
                if (userToken != null) {
                    em.remove(userToken);
                }

                em.remove(userEntity);
            }
        });
        return Response.ok().build();
    }
    
    @Path("images/")
    public UserImagesResource getUserImage() {
        return new UserImagesResource(emf.createEntityManager(), 
                userId,servletConfig);
    }
}
