package cn.comgroup.tzmedia.server.frontpage.resource;

import cn.comgroup.tzmedia.server.admin.entity.Administrator;
import cn.comgroup.tzmedia.server.frontpage.entity.FrontPage;
import cn.comgroup.tzmedia.server.frontpage.entity.FrontPageType;
import cn.comgroup.tzmedia.server.shop.entity.Activity;
import cn.comgroup.tzmedia.server.shop.entity.Shop;
import cn.comgroup.tzmedia.server.singer.entity.Singer;
import cn.comgroup.tzmedia.server.singer.entity.Song;
import cn.comgroup.tzmedia.server.util.query.QueryUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * FrontPagesResource
 *
 * @author peter.liu@comgroup.cn
 */
@Path("/frontpages/")
public class FrontPagesResource {

    @Context
    private UriInfo uriInfo;

    @Context
    private ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    private EntityManagerFactory emf;

    /**
     * Creates a new instance of FrontPagesResource.
     */
    public FrontPagesResource() {
    }

    private List<FrontPage> getFrontPagesUsingCriteria(int frontPageId,int shopId, int activityName,
            int singerId, Boolean push, FrontPageType type, String title) {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FrontPage> c = cb.createQuery(FrontPage.class);
        Root<FrontPage> frontPage = c.from(FrontPage.class);
        c.select(frontPage);
        c.orderBy(cb.desc(frontPage.get("ordering")));
        c.distinct(true);
        List<Predicate> criteria = new ArrayList<>();
        if (frontPageId > 0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "frontPageId");
            criteria.add(cb.equal(frontPage.get("frontPageId"), p));
        }
        if (shopId > 0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "shopId");
            criteria.add(cb.equal(frontPage.get("shopId"), p));
        }
        if (activityName > 0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "activityName");
            criteria.add(cb.equal(frontPage.get("activityName"), p));
        }

        if (singerId > 0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "singerId");
            criteria.add(cb.equal(frontPage.get("singerId"), p));

        }
        if (push != null) {
            ParameterExpression<Boolean> p = cb.parameter(Boolean.class, "push");
            criteria.add(cb.equal(frontPage.get("push"), p));
        }

        if (type != null) {
            ParameterExpression<FrontPageType> p = cb.parameter(FrontPageType.class, "type");
            criteria.add(cb.equal(frontPage.get("type"), p));
        }

        if (QueryUtil.queryParameterProvided(title)) {
            ParameterExpression<String> p = cb.parameter(String.class, "title");
            if (title.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(frontPage.<String>get("title"), p));
            } else {
                criteria.add(cb.equal(frontPage.get("title"), p));
            }
        }

        if (criteria.size() == 1) {
            c.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[0])));
        }
        TypedQuery<FrontPage> q = em.createQuery(c);
        if (frontPageId > 0) {
            q.setParameter("frontPageId", frontPageId);
        }
                
        if (shopId > 0) {
            q.setParameter("shopId", shopId);
        }
        if (activityName > 0) {
            q.setParameter("activityName", activityName);
        }
        if (singerId > 0) {
            q.setParameter("singerId", singerId);
        }

        if (QueryUtil.queryParameterProvided(title)) {
            q.setParameter("title", title
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }

        if (push != null) {
            q.setParameter("push", push);
        }
        if (type != null) {
            q.setParameter("type", type);
        }

        return q.getResultList();
    }

    @Path("{frontPageId}/")
    public FrontPageResource getFrontPage(@PathParam("frontPageId") int frontPageId) {
        return new FrontPageResource(emf.createEntityManager(),
                frontPageId, servletConfig, uriInfo);
    }

    /**
     * Method for FrontPage creation
     *
     * @param frontPage
     * @return Response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postFrontPage(final FrontPage frontPage) {
        final EntityManager em = emf.createEntityManager();
        if (frontPage.getUserId() != null) {
            frontPage.setAdministrator(em.find(Administrator.class,
                    frontPage.getUserId()));
        }
        if (frontPage.getShopId() > 0) {
            frontPage.setShop(em.find(Shop.class,
                    frontPage.getShopId()));
        }

        if (frontPage.getActivityName() > 0) {
            frontPage.setActivity(em.find(Activity.class,
                    frontPage.getActivityName()));
        }

        if (frontPage.getSingerId() > 0) {
            frontPage.setSinger(em.find(Singer.class,
                    frontPage.getSingerId()));
        }
        
        Query q = em.createNativeQuery("SELECT MAX(frontPage.FRONTPAGEID) FROM FRONTPAGES frontPage");
        Integer maxFrontPageId = (Integer) q.getSingleResult();
        if(maxFrontPageId==null){
            maxFrontPageId=0;
        }
        maxFrontPageId++;

        if (frontPage.getShareUrl() == null || frontPage.getShareUrl().trim().equals("")) {
            frontPage.setShareUrl(uriInfo.getBaseUri().toString().replace("resources", "#")
                    + "frontpage-share/" + maxFrontPageId);
        }
        
        Query qOrdering = em.createNativeQuery("SELECT MAX(frontPage.ordering) FROM FRONTPAGES frontPage");
        Double maxOrdering = (Double) qOrdering.getSingleResult();

        if (maxOrdering == null) {
            maxOrdering = 0.0D;
        }
        maxOrdering = new Double(maxOrdering.intValue()+1);
        frontPage.setOrdering(maxOrdering);

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(frontPage);
            }
        });
        return Response.ok(frontPage).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public FrontPage[] getFrontPagesAsJsonArray(
            @QueryParam("frontPageId") int frontPageId,
            @QueryParam("shopId") int shopId,
            @QueryParam("activityName") int activityName,
            @QueryParam("singerId") int singerId,
            @QueryParam("push") Boolean push,
            @QueryParam("type") FrontPageType type,
            @QueryParam("title") String title,
            @QueryParam("resultLength") int resultLength) {
        List<FrontPage> frontPages = getFrontPagesUsingCriteria(frontPageId,
                shopId, activityName,singerId, push, type, title);
        if (resultLength > 0 && resultLength < frontPages.size()) {
            return frontPages.subList(0, resultLength).toArray(new FrontPage[resultLength]);
        } else {
            return frontPages.toArray(new FrontPage[frontPages.size()]);
        }
    }
}
