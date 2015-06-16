package cn.comgroup.tzmedia.server.singer.resource;

import cn.comgroup.tzmedia.server.singer.entity.BadgeType;
import cn.comgroup.tzmedia.server.singer.entity.Singer;
import cn.comgroup.tzmedia.server.singer.entity.SingerOwnedSong;
import cn.comgroup.tzmedia.server.singer.entity.Song;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import cn.comgroup.tzmedia.server.util.query.QueryUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
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
 *
 * SingersResource
 *
 * @author peter.liu@comgroup.cn
 */
@Path("/singers/")
public class SingersResource {

    @Context
    private UriInfo uriInfo;

    @PersistenceUnit(unitName = "TZMediaPU")
    private EntityManagerFactory emf;
    
    @Context
    private ServletConfig servletConfig;

    /**
     * Creates a new instance of AdministratorsResource.
     */
    public SingersResource() {
    }
    
    private List<Singer> getSingersUsingCriteria(int singerId, String singerName,
            Boolean isBandsman, BadgeType badgeType) {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Singer> c = cb.createQuery(Singer.class);
        Root<Singer> singer = c.from(Singer.class);
        c.select(singer);
        c.orderBy(cb.desc(singer.get("ordering")), cb.asc(singer.get("singerId")));
        c.distinct(true);
        List<Predicate> criteria = new ArrayList<>();
        if (singerId > 0) {
            ParameterExpression<String> p = cb.parameter(String.class, "singerId");
            criteria.add(cb.equal(singer.get("singerId"), p));
        }
        if (QueryUtil.queryParameterProvided(singerName)) {
            ParameterExpression<String> p = cb.parameter(String.class, "singerName");
            if (singerName.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(singer.<String>get("singerName"), p));
            } else {
                criteria.add(cb.equal(singer.get("singerName"), p));
            }
        }

        if (isBandsman != null) {
            ParameterExpression<Boolean> p = cb.parameter(Boolean.class, "bandsman");
            criteria.add(cb.equal(singer.get("bandsman"), p));
        }

        if (badgeType != null) {
            ParameterExpression<BadgeType> p = cb.parameter(BadgeType.class, "badgeType");
            criteria.add(cb.equal(singer.get("badgeType"), p));
        }
        if (criteria.size() == 1) {
            c.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[0])));
        }
        TypedQuery<Singer> q = em.createQuery(c);
        if (singerId > 0) {
            q.setParameter("singerId", singerId);
        }
        if (QueryUtil.queryParameterProvided(singerName)) {
            q.setParameter("singerName", singerName
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }

        if (isBandsman != null) {
            q.setParameter("bandsman", isBandsman);
        }

        if (badgeType != null) {
            q.setParameter("badgeType", badgeType);
        }
        return q.getResultList();
    }

    @Path("{singerId}/")
    public SingerResource getSinger(@PathParam("singerId") int singerId) {
        return new SingerResource(uriInfo, emf, singerId,servletConfig);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Singer[] getSingersAsJsonArray(@QueryParam("singerId") int singerId,
            @QueryParam("singerName") String singerName,
            @QueryParam("isBandsman") Boolean isBandsman,
            @QueryParam("badgeType") BadgeType badgeType,
            @QueryParam("resultLength") int resultLength,
            @QueryParam("isPc") Boolean isPc
            ) {
        List<Singer> singers = getSingersUsingCriteria(singerId, singerName, isBandsman,badgeType);
        if(isPc == null)
        {
            for(int i = singers.size()-1; i >=0;i--)
            {
                double order = singers.get(i).getOrdering(); 
                if(order<0)
                {
                   singers.remove(i);
                }
            }
        }
        if (resultLength > 0 && resultLength < singers.size()) {
            return singers.subList(0, resultLength).toArray(new Singer[resultLength]);
        } else {
          
            return singers.toArray(new Singer[singers.size()]);
        }
    }
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postSinger(final Singer singer) {
        final EntityManager em = emf.createEntityManager();
        List<SingerOwnedSong> singerOwnedSongs=new ArrayList(singer.getSingerOwnedSongs());
        singer.setSingerOwnedSongs(new ArrayList<SingerOwnedSong>());
        Properties tzProperties = PropertiesUtils
                .getProperties(servletConfig.getServletContext());
          String share_base ="";
        if(!singer.isBandsman())
        {
              share_base = tzProperties.getProperty("share-base",
                "http://www.dudunangnang.com:9090/tzzjservice2/share/singer.html?type=");
                 share_base += "singer";
        }
        else
        {
              share_base = tzProperties.getProperty("share-base",
                "http://www.dudunangnang.com:9090/tzzjservice2/bandsman.html?type=");
                 share_base += "bandsman";
        }


        for(SingerOwnedSong singerOwnedSong:singerOwnedSongs){
            singerOwnedSong.setSong(em.find(Song.class, singerOwnedSong.getSongId()));
            singer.addSingerOwnedSong(singerOwnedSong);
        }
        
        Query q = em.createNativeQuery("SELECT generator_value FROM id_generator i where generator_name='SINGERID_GEN'");
        BigDecimal maxSingerId = (BigDecimal) q.getSingleResult();
        if(maxSingerId==null){
            maxSingerId= new BigDecimal(0);
        }
        maxSingerId = maxSingerId.add(new BigDecimal(1));
        
        if (singer.getShareUrl() == null || singer.getShareUrl().trim().equals("")) {
            singer.setShareUrl(share_base
                    + "&id=" + maxSingerId);
        }

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(singer);
            }
        });
        return Response.ok().build();
    }
}
