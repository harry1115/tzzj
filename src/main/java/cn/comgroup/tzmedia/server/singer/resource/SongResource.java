package cn.comgroup.tzmedia.server.singer.resource;

import cn.comgroup.tzmedia.server.authentication.filter.AuthenticationConstants;
import cn.comgroup.tzmedia.server.singer.entity.Song;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import javax.persistence.EntityManager;
import javax.servlet.ServletConfig;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * ProductResource peter.liu@comgroup.cn
 */
public class SongResource {

    private final Song songEntity; // appropriate jpa Song entity

//    UriInfo uriInfo; // actual uri info provided by parent resource
    private final EntityManager em; // entity manager provided by parent resource
    private final ServletConfig servletConfig;
    private final String deployPath;

    /**
     * Creates a new instance of UserResource
     *
     * @param em
     * @param songId
     * @param servletConfig
     */
    public SongResource(EntityManager em, int songId, ServletConfig servletConfig) {
//        this.songId = songId;
        this.em = em;
        this.songEntity = em.find(Song.class, songId);
        this.servletConfig = servletConfig;
        this.deployPath = PropertiesUtils.getProperties(this.servletConfig
                .getServletContext()).getProperty("deploy-path");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Song getSong() {
        if (null == songEntity) {
            throw new WebApplicationException(404);
        }
        return songEntity;
    }

    /**
     * Method for update Song
     *
     * @param song
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putSong(final Song song) {
        if (null == songEntity) {
            throw new WebApplicationException(404);
        }
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(song);
            }
        });
        return Response.ok(AuthenticationConstants.UPDATESUCCESS).build();
    }

    @DELETE
    public void deleteSong() {
        if (null == songEntity) {
            throw new WebApplicationException(404);
        }
        songEntity.removeAllImagesAndDeleteOnFS(deployPath);
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.remove(songEntity);
            }
        });
    }

    @Path("images/")
    public SongImagesResource uploadSongImage() {
        return new SongImagesResource(em, songEntity, servletConfig);
    }
}
