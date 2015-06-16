package cn.comgroup.tzmedia.server.shop.resource;

import cn.comgroup.tzmedia.server.common.entity.GlobalConfiguration;
import cn.comgroup.tzmedia.server.shop.entity.Playbill;
import cn.comgroup.tzmedia.server.shop.entity.PlaybillLine;
import cn.comgroup.tzmedia.server.shop.entity.PlaybillNotification;
import cn.comgroup.tzmedia.server.shop.entity.DMPlaybill;
import cn.comgroup.tzmedia.server.shop.entity.PlaybillState;
import cn.comgroup.tzmedia.server.shop.entity.Shop;
import cn.comgroup.tzmedia.server.shop.entity.UserAction;
import cn.comgroup.tzmedia.server.singer.entity.Singer;
import cn.comgroup.tzmedia.server.singer.entity.Song;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
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
 * PlaybillsResource
 *
 * @author peter.liu@comgroup.cn
 */
@Path("/playbills/")
public class PlaybillsResource {

    @Context
    private UriInfo uriInfo;

    @PersistenceUnit(unitName = "TZMediaPU")
    private EntityManagerFactory emf;

    @Context
    private ServletConfig servletConfig;

    

    /**
     * Creates a new instance of PlaybillsResource.
     */
    public PlaybillsResource() {
    }
    
    /**
     * getPlaybillsUsingCriteria If userId provided,we have to set the canGrab
     * attribute, client will use it.
     *
     */
    private List<Playbill> getPlaybillsUsingCriteria(
            int playbillId, 
            String playbillName,
            String playbillDate, 
            int singerId,
            String userId,
            PlaybillState playbillState,
            int shopId,
            boolean forWeb) throws ParseException {
        final EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Properties tzProperties = PropertiesUtils
                .getProperties(servletConfig.getServletContext());
        //Get the configuration on the page
        boolean checkPlaybillTime = false;
        Query query = em.createNamedQuery("GlobalConfiguration.findAll");
        List<GlobalConfiguration> gcs = query.getResultList();
        if(gcs.size()==1){
            checkPlaybillTime=gcs.get(0).isGrabSongCheckTime();
        }
        CriteriaQuery<Playbill> c = cb.createQuery(Playbill.class);
        Root<Playbill> playbill = c.from(Playbill.class);
        c.select(playbill);
        c.orderBy(cb.asc(playbill.get("fromTime")));
        c.distinct(true);
        List<Predicate> criteria = new ArrayList<>();
        if (playbillId > 0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "playbillId");
            criteria.add(cb.equal(playbill.get("playbillId"), p));
        }
        
        if (QueryUtil.queryParameterProvided(playbillName)) {
             ParameterExpression<String> p = cb.parameter(String.class, "playbillName");
            if (playbillName.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(playbill.<String>get("playbillName"), p));
            } else {
                criteria.add(cb.equal(playbill.get("playbillName"), p));
            }
        }
        
        if (QueryUtil.queryParameterProvided(playbillDate)) {
            ParameterExpression<Calendar> p = cb.parameter(Calendar.class, "playbillDate");
            criteria.add(cb.equal(playbill.<Calendar>get("playbillDate"), p));
        }
        
        if (singerId > 0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "singerId");
            criteria.add(cb.equal(playbill.get("singerId"), p));
        }
        
        if (playbillState!=null) {
            ParameterExpression<PlaybillState> p = cb
                    .parameter(PlaybillState.class, "playbillState");
            criteria.add(cb.equal(playbill.get("playbillState"), p));
        }
        
        if (shopId > 0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "shopId");
            criteria.add(cb.equal(playbill.get("shopId"), p));
        }
        
        if (criteria.size() == 1) {
            c.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[0])));
        }
        TypedQuery<Playbill> q = em.createQuery(c);

        if (playbillId >0) {
            q.setParameter("playbillId", playbillId);
        }
        
        if (singerId > 0) {
             q.setParameter("singerId", singerId);
        }
        
        if (QueryUtil.queryParameterProvided(playbillName)) {
            q.setParameter("playbillName", playbillName
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }
        
        if (QueryUtil.queryParameterProvided(playbillDate)) {
            Calendar fromDate = Calendar.getInstance();
            fromDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(playbillDate));
            q.setParameter("playbillDate", fromDate);
        }
        
        if (playbillState !=null) {
            q.setParameter("playbillState", playbillState);
        }
        
        if (shopId > 0) {
             q.setParameter("shopId", shopId);
        }
        List<Playbill> playbills = q.getResultList();
        if (forWeb==false) {
            if(checkPlaybillTime) {
            for (Playbill pb : playbills) {
                if (pb.getPlaybillState().equals(PlaybillState.ACTIVE)) {
                    Calendar fromGrabSongTime = Calendar.getInstance();
                    fromGrabSongTime.setTime(pb.getFromTime().getTime());
                    fromGrabSongTime.add(Calendar.HOUR, -1);

                    Calendar toGrabSongTime = Calendar.getInstance();
                    toGrabSongTime.setTime(pb.getFromTime().getTime());
                    toGrabSongTime.add(Calendar.MINUTE, 15);

                    Calendar now = Calendar.getInstance();
                    now.setTime(new Date());

                    if (now.before(fromGrabSongTime) || now.after(toGrabSongTime)) {
                        pb.setPlaybillState(PlaybillState.INACTIVE);
                    }
//                  pb.setCanGrab(true);
                }
            }
            } else {
                for (Playbill pb : playbills) {
                    if (pb.getPlaybillState().equals(PlaybillState.ACTIVE)) {
                        String startHour_s = tzProperties.
                                getProperty("playbill-starthour","19");
                        String startMinute_s = tzProperties.
                                getProperty("playbill-startminute", "30");
                        String endHour_s = tzProperties.
                                getProperty("playbill-endhour","6");
                        String endMinute_s = tzProperties.
                                getProperty("playbill-endminute","0");
                        int startHour = Integer.valueOf(startHour_s);
                        int startMinute = Integer.valueOf(startMinute_s);
                        int endHour = Integer.valueOf(endHour_s);
                        int endMinute = Integer.valueOf(endMinute_s);
                        Date today = new Date();
                        today.setHours(startHour);
                        today.setMinutes(startMinute);
                        today.setSeconds(0);
                        Calendar tomorrow_c = Calendar.getInstance();
                        tomorrow_c.setTime(today);
                        tomorrow_c.add(Calendar.DATE, 1);
                        Date tomorrow = tomorrow_c.getTime();
                        tomorrow.setHours(endHour);
                        tomorrow.setMinutes(endMinute);
                        Calendar fromGrabSongTime = Calendar.getInstance();
                        fromGrabSongTime.setTime(today);
                        Calendar toGrabSongTime = Calendar.getInstance();
                        toGrabSongTime.setTime(tomorrow);
                        
                        Calendar itemOverTime = Calendar.getInstance();
                        itemOverTime.setTime(pb.getFromTime().getTime());
                        itemOverTime.add(Calendar.MINUTE, 25);
                        //System.out.println(pb.getToTime().getTime().toString()
                        //        + " " + fromGrabSongTime.getTime().toString());
                        //System.out.println(pb.getFromTime().getTime().toString()
                        //        + " " + toGrabSongTime.getTime().toString());
                        Calendar now = Calendar.getInstance();
                        now.setTime(new Date());
                        if (pb.getToTime().before(fromGrabSongTime)
                                  || pb.getFromTime().after(toGrabSongTime)
                                  || now.before(fromGrabSongTime)
                                  || now.after(toGrabSongTime)
                                  || now.after(itemOverTime)) {
                            pb.setPlaybillState(PlaybillState.INACTIVE);
        }
//                      pb.setCanGrab(true);
                    }
                }
            }

        }
        return playbills;
    }
    

    @Path("{playbillId}/")
    public PlaybillResource getPlaybill(@PathParam("playbillId") int playbillId) {
        return new PlaybillResource(uriInfo, emf, playbillId, servletConfig);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Playbill[] getPlaybillsAsJsonArray(@QueryParam("playbillId") int playbillId,
            @QueryParam("playbillName") String playbillName,
            @QueryParam("playbillDate") String playbillDate,
            @QueryParam("singerId") int singerId,
            @QueryParam("resultLength") int resultLength,
            @QueryParam("userId") String userId,
            @QueryParam("playbillState") PlaybillState playbillState,
            @QueryParam("shopId") int shopId,
            @QueryParam("fromWeb") Boolean fromWeb)
            throws ParseException {
        boolean forWeb = false;
        if (fromWeb != null) {
            forWeb = true;
        }
        List<Playbill> playbills = getPlaybillsUsingCriteria(playbillId,
                playbillName, playbillDate, singerId, userId,
                playbillState, shopId, forWeb);
        if (resultLength > 0 && resultLength < playbills.size()) {
            return playbills.subList(0, resultLength).toArray(new Playbill[resultLength]);
        } else {
            return playbills.toArray(new Playbill[playbills.size()]);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postPlaybill(final Playbill playbill) {
        final EntityManager em = emf.createEntityManager();
        if (playbill.getShopId() >0) {
            Shop shop = em.find(Shop.class, playbill.getShopId());
            playbill.setShop(shop);
        }
        if (playbill.getSingerId()>0) {
            Singer singer = em.find(Singer.class, playbill.getSingerId());
            playbill.setSinger(singer);
        }
        
        List<PlaybillLine> lines=new ArrayList(playbill.getPlaybillLines());
        playbill.setPlaybillLines(new ArrayList<PlaybillLine>());
        
        Query q = em.createNativeQuery("SELECT MAX(playbill.PLAYBILLID) FROM PLAYBILLS playbill");
        Integer maxPlayBillId = (Integer) q.getSingleResult();
        if(maxPlayBillId==null){
            maxPlayBillId=0;
        }
        maxPlayBillId++;
        
        if (playbill.getShareUrl() == null || playbill.getShareUrl().trim().equals("")) {
            playbill.setShareUrl(uriInfo.getBaseUri().toString().replace("resources", "#")
                    + "playbill-share/" + maxPlayBillId);
        }
        
        for (PlaybillLine line : lines) {
            Song song = em.find(Song.class, line.getSongId());
            if (song != null) {
                line.setSong(song);
                playbill.addPlaybillLine(line);
            }
        }
        

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(playbill);
            } 
        });
        return Response.ok().build();
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("playbillnotification/")
    public PlaybillNotification[] getPlaybillNotification(@QueryParam("shopId") int shopId,
            @QueryParam("playbillDate") String playbillDate)
            throws ParseException {
        List<Playbill> playbills = getPlaybillsUsingCriteria(0,
                null, playbillDate, 0, null, null, shopId,false);

        List<PlaybillNotification> pns = new LinkedList<>();
        for (Playbill playbill : playbills) {
            pns.add(new PlaybillNotification(playbill));
        }
        return pns.toArray(new PlaybillNotification[pns.size()]);
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("dm/")
    public   DMPlaybill getDMPlaybillNotification(@QueryParam("shopId") int shopId,
            @QueryParam("playbillId") int id)
            throws ParseException {
        
            Date date=new Date();
            SimpleDateFormat lformatter=new SimpleDateFormat("yyyy-MM-dd"); 
           
        List<Playbill> playbills = getPlaybillsUsingCriteria(0,
                null, lformatter.format(date), 0, null, null, shopId,false);
       
        DMPlaybill lDMP = null;
        for (int i = playbills.size()-1; i >= 0 ;i--) {
             int lId = playbills.get(i).getPlaybillId();
              Calendar c = Calendar.getInstance();
             long time=c.getTimeInMillis();
             long fromTime =playbills.get(i).getFromTime().getTimeInMillis();
             long toTime =playbills.get(i).getToTime().getTimeInMillis();
             if(time < fromTime || time > toTime)
             {
                    playbills.remove(i);
             }
             else
             {
                   playbills.get(i).setPlaybillId(492);
                   Long num = Long.parseLong("0");
                   UserActionsResource lAction = new UserActionsResource(emf,  playbills.get(i),"",servletConfig);
                   UserAction[] lRes = lAction.getUserActions(null,null,num,null,null);
                   if(playbills.get(i).getPlaybillId() == id)
                   {
                       playbills.get(i).setSinger(null);
                   }
                   lDMP = new DMPlaybill(playbills.get(i),lRes);
               
             }
        }

         return lDMP;
    }
    
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("dm/action")
//    public  UserAction[] getDMAction(@QueryParam("shopId") int shopId,
//            @QueryParam("playbillId") int id)
//            throws ParseException {
//            
//            Date date=new Date();
//            SimpleDateFormat lformatter=new SimpleDateFormat("yyyy-MM-dd"); 
//           
//        List<Playbill> playbills = getPlaybillsUsingCriteria(0,
//                null, lformatter.format(date), 0, null, null, shopId,false);
// 
//        for (int i = playbills.size()-1; i >= 0 ;i--) {
//              Calendar c = Calendar.getInstance();
//             long time=c.getTimeInMillis();
//             long fromTime =playbills.get(i).getFromTime().getTimeInMillis();
//             long toTime =playbills.get(i).getToTime().getTimeInMillis();
//             if(time < fromTime || time > toTime)
//             {
//                    playbills.remove(i);
//             }
//             else
//             {
//                   Long num = Long.parseLong("0");
//                   UserActionsResource lAction = new UserActionsResource(emf,  playbills.get(i),"",servletConfig);
//                   UserAction[] lRes = lAction.getUserActions(null,null,num,null,null);
//                   return lRes;
//             }
//        }
//
//        return null;
//    }
    
    
    
}
