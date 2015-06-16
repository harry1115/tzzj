package cn.comgroup.tzmedia.server.singer.resource;

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
import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * SongsResource
 *
 * @author peter.liu@comgroup.cn
 */
@Path("/songs/")
public class SongsResource {

    @Context
    UriInfo uriInfo;

    @Context
    private ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    EntityManagerFactory emf;
    
    private static final int QUERYBYSONGID = 1;
    private static final int QUERYBYSONGNAME = 2;
    private static final int QUERYALL = 3;

    /**
     * Creates a new instance of SongsResource.
     */
    public SongsResource() {
    }

    public List<Song> getSongs(int songId, String songName) {
        int queryMethod = decideQueryMethod(songId, songName);
        EntityManager em = emf.createEntityManager();
        List<Song> songList = new ArrayList<>();
        switch (queryMethod) {
            case QUERYBYSONGID:
                Query queryU = em.createNamedQuery("Song.findBySongId");
                queryU.setParameter("songId", songId);
                songList = queryU.getResultList();
                break;
            case QUERYBYSONGNAME:
                Query queryQ = em.createNamedQuery("Song.findBySongName");
                queryQ.setParameter("songName", songName
                        .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
                songList = queryQ.getResultList();
                break;
            case QUERYALL:
                Query queryA = em.createNamedQuery("Song.findAll");
                songList = queryA.getResultList();
                break;
            default:
                break;
        }
        return songList;
    }
    
   
    private int decideQueryMethod(int songId, String songName) {
        boolean songIdProvided = (songId>0);
        boolean songNameProvided = QueryUtil.queryParameterProvided(songName);

        if (songIdProvided && !songNameProvided) {
            return QUERYBYSONGID;
        }
        if (!songIdProvided && songNameProvided) {
            return QUERYBYSONGNAME;
        }

        return QUERYALL;
    }

    @Path("{songId}/")
    public SongResource getSong(@PathParam("songId") int songId) {
        return new SongResource(emf.createEntityManager(), songId, servletConfig);
    }

    /**
     * Method for Song creation
     *
     * @param song
     * @return Response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postSong(final Song song) {
        int songId = song.getSongId();
        final EntityManager em = emf.createEntityManager();

        if (em.find(Song.class, songId) != null) {
            return Response.status(Status.CONFLICT).entity("Song already exists!\n").build();
        }

        Query queryQ = em.createNamedQuery("Song.findBySongName");
        queryQ.setParameter("songName", song.getSongName());

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(song);
            }
        });
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Song[] getSongsAsJsonArray(@QueryParam("songId") int songId,
            @QueryParam("songName") String songName,
            @QueryParam("resultLength") int resultLength) {
        List<Song> songs = getSongs(songId, songName);
        if (resultLength > 0 && resultLength < songs.size()) {
            return songs.subList(0, resultLength).toArray(new Song[resultLength]);
        } else {
            return songs.toArray(new Song[songs.size()]);
        }
    }
}
    