package cn.comgroup.tzmedia.server.shop.resource;

import cn.comgroup.tzmedia.server.exception.ExtendedNotFoundException;
import cn.comgroup.tzmedia.server.shop.entity.Playbill;
import cn.comgroup.tzmedia.server.shop.entity.PlaybillLine;
import cn.comgroup.tzmedia.server.shop.entity.Shop;
import cn.comgroup.tzmedia.server.shop.entity.UserAction;
import cn.comgroup.tzmedia.server.singer.entity.Singer;
import cn.comgroup.tzmedia.server.singer.entity.Song;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.util.ArrayList;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

/**
 * PlaybillResource
 * peter.liu@comgroup.cn
 */
public class PlaybillResource { 

    private final int playbillId; // playbillId from url
    private final Playbill playbillEntity; // appropriate jpa Playbill entity

    private final UriInfo uriInfo; // actual uri info provided by parent resource
    private final EntityManagerFactory emf;
    private final EntityManager em; // entity manager provided by parent resource

    private final ServletConfig servletConfig;
    private final String deployPath;
    

    public PlaybillResource(UriInfo uriInfo,EntityManagerFactory emf, int playbillId,
            ServletConfig servletConfig) {
        this.uriInfo = uriInfo;
        this.playbillId = playbillId;
        this.emf=emf;
        this.em = emf.createEntityManager();
        this.playbillEntity = em.find(Playbill.class, playbillId);
        this.servletConfig = servletConfig;
        this.deployPath = PropertiesUtils.getProperties(this.servletConfig
                .getServletContext()).getProperty("deploy-path");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Playbill getPlaybill() {
        if (null == playbillEntity) {
            throw new ExtendedNotFoundException("playbillEntity "
                    + playbillId + "does not exist!");
        }
        return playbillEntity;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putPlaybill(final Playbill playbill) {
        if (playbill.getShopId()>0) {
            playbill.setShop(em.find(Shop.class, playbill.getShopId()));
        }

        if (playbill.getSingerId()>0) {
            playbill.setSinger(em.find(Singer.class, playbill.getSingerId()));
        }

        List<PlaybillLine> lines = new ArrayList(playbill.getPlaybillLines());
        playbill.setPlaybillLines(new ArrayList<PlaybillLine>());

        for (PlaybillLine line : lines) {
            Song song = em.find(Song.class, line.getSongId());
            if (song != null) {
                line.setSong(song);
                playbill.addPlaybillLine(line);
            }
        }

        if (playbill.getShareUrl() == null || playbill.getShareUrl().trim().equals("")) {
            playbill.setShareUrl(uriInfo.getBaseUri().toString().replace("resources", "#")
                    + "playbill-share/" + playbill.getPlaybillId());
        }
        playbill.setPlaybillImages(playbillEntity.getPlaybillImages());

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(playbill);
            }
        });
        return Response.status(Status.OK).build();
    }
    
    
     /**
     * Mainly used for data initialization.
     *
     * @param playbillLine
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("addplaybillline/")
    public Response addPlaybillLine(PlaybillLine playbillLine) {
        if (playbillEntity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        Query queryQ = em.createNamedQuery("Song.findBySongName");
        queryQ.setParameter("songName", playbillLine.getSongName());
        List<Song> songList = queryQ.getResultList();
        if (songList.size() > 0) {
            playbillLine.setSong(em.find(Song.class, songList.get(0).getSongId()));
            playbillEntity.addPlaybillLine(playbillLine);
            TransactionManager.manage(new Transactional(em) {
                @Override
                public void transact() {
                    em.merge(playbillEntity);
                }
            });
        }
        return Response.status(Status.OK).build();
    }
    
    

    @DELETE
    public void deletePlaybill() {
        if (null == playbillEntity) {
            throw new ExtendedNotFoundException("deletePlaybill playbillId "
                    + playbillId + "does not exist!");
        }
        playbillEntity.removeAllImagesAndDeleteOnFS(deployPath);
        Query query = em.createNamedQuery("UserAction.findByPlaybillId");
        query.setParameter("playbillId", playbillEntity.getPlaybillId());
        final List<UserAction> userActions = query.getResultList();
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.remove(playbillEntity);
                for(UserAction userAction: userActions){
                     em.remove(userAction);
                }
            }
        });
    }

    @Path("images/")
    public PlaybillImagesResource uploadPlaybillImage() {        
        return new PlaybillImagesResource(emf.createEntityManager(), 
                playbillEntity, servletConfig);
    }
    
    @Path("actions/")
    public UserActionsResource getUserActionsResource() {
        return new UserActionsResource(emf, playbillEntity,"",servletConfig);
    }

    @Path("actionsList/{actionType}/")
    public UserActionsResource getActionList(@PathParam("actionType") String actionType) {
        return new UserActionsResource(emf, playbillEntity,actionType,servletConfig);
    }
   
    @Path("reactivate/")
    public PlaybillReactivationResource getPlaybillReactivationResource() {
        return new PlaybillReactivationResource(emf.createEntityManager(),
        playbillEntity, servletConfig);
    }

    private void UserActionsResource(EntityManagerFactory emf, Playbill playbillEntity, ServletConfig servletConfig) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
