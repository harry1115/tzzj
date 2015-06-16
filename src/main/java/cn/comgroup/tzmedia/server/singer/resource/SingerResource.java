package cn.comgroup.tzmedia.server.singer.resource;

import cn.comgroup.tzmedia.server.exception.ExtendedNotFoundException;
import cn.comgroup.tzmedia.server.frontpage.entity.FrontPage;
import cn.comgroup.tzmedia.server.singer.entity.Singer;
import cn.comgroup.tzmedia.server.singer.entity.SingerOwnedSong;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

/**
 * peter.liu@comgroup.cn
 */
public class SingerResource {

    private final int singerId; // singerId from url
    private final Singer singerEntity; // appropriate jpa Singer entity

    private final UriInfo uriInfo; // actual uri info provided by parent resource
    private final EntityManager em; // entity manager provided by parent resource
    private final EntityManagerFactory emf;
    private final ServletConfig servletConfig;
    private final String deployPath;
    


    
    public SingerResource(UriInfo uriInfo, EntityManagerFactory emf, int singerId,
            ServletConfig servletConfig) {
        this.uriInfo = uriInfo;
        this.singerId = singerId;
        this.emf=emf;
        this.em = emf.createEntityManager();
        this.singerEntity = em.find(Singer.class, singerId);
        this.servletConfig = servletConfig;
        this.deployPath = PropertiesUtils.getProperties(this.servletConfig
                .getServletContext()).getProperty("deploy-path");
        
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Singer getSinger() {
        if (null == singerEntity) {
            throw new ExtendedNotFoundException("singerEntity " 
                    + singerId + "does not exist!");
        }
        return singerEntity;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putSinger(final Singer singer) {
        List<SingerOwnedSong> singerOwnedSongs = new ArrayList(singer.getSingerOwnedSongs());
        singer.setSingerOwnedSongs(new ArrayList<SingerOwnedSong>());

        for (SingerOwnedSong singerOwnedSong : singerOwnedSongs) {
            singerOwnedSong.setSong(em.find(Song.class, singerOwnedSong.getSongId()));
            singer.addSingerOwnedSong(singerOwnedSong);
        }
        
//        singer.setShareUrl(uriInfo.getBaseUri().toString().replace("resources", "#")
//                + "singer-share/" + singer.getSingerId());

        singer.setSingerImages(singerEntity.getSingerImages());
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(singer);
                Query query = em.createNamedQuery("FrontPage.findBySingerId");
                query.setParameter("singerId", singer.getSingerId());
                List<FrontPage> frontPages = query.getResultList();
                if (!frontPages.isEmpty()) {
                    FrontPage fp = frontPages.get(0);
                    fp.setTitle(singer.getSingerName());
                    fp.setSubtitle(singer.getSingerName());
                    fp.setContent(singer.getSingerDesc());
                    em.merge(fp);
                }
                
            }
        });
        return Response.status(Status.OK).build();
    }
    
    /**
     * Mainly used for data initialization.
     *
     * @param singerOwnedSong
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("addsong/")
    public Response addSingerOwnedSong(SingerOwnedSong singerOwnedSong) {
        if (singerEntity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        Query queryQ = em.createNamedQuery("Song.findBySongName");
        queryQ.setParameter("songName", singerOwnedSong.getSongName());
        List<Song> songList = queryQ.getResultList();
        if (songList.size() > 0) {
            singerOwnedSong.setSong(em.find(Song.class, songList.get(0).getSongId()));
            singerEntity.addSingerOwnedSong(singerOwnedSong);
            TransactionManager.manage(new Transactional(em) {
                @Override
                public void transact() {
                    em.merge(singerEntity);
                }
            });
        }
        return Response.status(Status.OK).build();
    }

    @DELETE
    public void deleteSinger() {
        if (null == singerEntity) {
            throw new ExtendedNotFoundException("deleteSinger singerId " 
                    + singerId + "does not exist!");
        }
        singerEntity.removeAllImagesAndDeleteOnFS(deployPath);
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.remove(singerEntity);
            }
        });
    }
    
    
    @Path("images/")
    public SingerImagesResource uploadSingerImage() {
        return new SingerImagesResource(emf.createEntityManager(),
                singerEntity, servletConfig);
    }

    public Singer getAdministratorEntity() {
        return singerEntity;
    }
}
