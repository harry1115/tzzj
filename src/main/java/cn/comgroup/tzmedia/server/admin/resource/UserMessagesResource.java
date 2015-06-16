package cn.comgroup.tzmedia.server.admin.resource;

import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.admin.entity.UserDevice;
import cn.comgroup.tzmedia.server.admin.entity.UserMessage;
import cn.comgroup.tzmedia.server.shop.entity.UserAction;
import cn.comgroup.tzmedia.server.util.query.QueryUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


@Path("/usermessages/")
public class UserMessagesResource {

    @Context
    UriInfo uriInfo;

    @Context
    ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    EntityManagerFactory emf;
    
    
    private final int MESSAGE_NOT_EXISTED=2;

    /**
     * Creates a new instance of UserDevicesResource.
     */
    public UserMessagesResource() {
    }

    private List<UserMessage> getUserMessagesUsingCriteria(
            Long messageId,String userId,String fromUserId,
            String fromCreationDate,
            String toCreationDate) throws ParseException {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserMessage> c = cb.createQuery(UserMessage.class);
        Root<UserMessage> userMessage = c.from(UserMessage.class);
        c.select(userMessage);
        c.orderBy(cb.desc(userMessage.get("creationDate")));
        c.distinct(true);
        List<Predicate> criteria = new ArrayList<>();
        
        if (messageId != null) {
            Query query = em.createNamedQuery("UserMessage.findByMessageId");
            query.setParameter("messageId", messageId);
            return query.getResultList();
        }
        


        if (QueryUtil.queryParameterProvided(userId)) {
            ParameterExpression<String> p = cb.parameter(String.class, "userId");
            if (userId.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(userMessage.<String>get("userId"), p));
            } else {
                criteria.add(cb.equal(userMessage.get("userId"), p));
            }
        }
        if (QueryUtil.queryParameterProvided(fromUserId)) {
            ParameterExpression<String> p = cb.parameter(String.class, "fromUserId");
            if (fromUserId.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(userMessage.<String>get("fromUserId"), p));
            } else {
                criteria.add(cb.equal(userMessage.get("fromUserId"), p));
            }
        }
        
        if (QueryUtil.queryParameterProvided(fromCreationDate)) {
            ParameterExpression<Calendar> p = cb.parameter(Calendar.class, "fromCreationDate");
            criteria.add(cb.greaterThanOrEqualTo(userMessage.<Calendar>get("creationDate"), p));
        }
    
        if (QueryUtil.queryParameterProvided(toCreationDate)) {
            ParameterExpression<Calendar> p = cb.parameter(Calendar.class, "toCreationDate");
            criteria.add(cb.lessThanOrEqualTo(userMessage.<Calendar>get("creationDate"), p));
        }

        if (criteria.size() == 1) {
            c.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[0])));
        }
        TypedQuery<UserMessage> q = em.createQuery(c);
        if (QueryUtil.queryParameterProvided(userId)) {
            q.setParameter("userId", userId
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }
        

        return q.getResultList();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("readmessage")
    public Response changeNewStatus(
            @QueryParam("messageId") Long messageId){
        final EntityManager em = emf.createEntityManager();
        final UserMessage userMessage = em.find(UserMessage.class, messageId);
        if (userMessage != null) {
            TransactionManager.manage(new Transactional(em) {
                @Override
                public void transact() {
                    userMessage.setNewMessage(false);
                    em.persist(userMessage);
                }
            });
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(MESSAGE_NOT_EXISTED).build();
        }
    }

    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserMessage[] getUserMessagesAsJsonArray(
            @QueryParam("messageId") Long messageId,
            @QueryParam("userId") String userId,
            @QueryParam("fromUserId") String fromUserId, 
            @QueryParam("fromCreationDate") String fromCreationDate,
            @QueryParam("toCreationDate") String toCreationDate
    ) throws ParseException {
        List<UserMessage> messages = getUserMessagesUsingCriteria(messageId,userId, fromUserId, fromCreationDate, toCreationDate);
        return messages.toArray(new UserMessage[messages.size()]);
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUserMessage(@QueryParam("messageId") Long messageId) {
        final EntityManager em = emf.createEntityManager();
        final UserMessage userMessage = em.find(UserMessage.class, messageId);
        if (userMessage != null) {
            TransactionManager.manage(new Transactional(em) {
                @Override
                public void transact() {
                    em.remove(userMessage);
                }
            });
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.CONFLICT)
                    .entity(MESSAGE_NOT_EXISTED).build();
        }
    }
}
