package cn.comgroup.tzmedia.server.common.resource;

import cn.comgroup.tzmedia.server.common.entity.GlobalConfiguration;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * GlobalConfigurationResource
 *
 * @author peter.liu@comgroup.cn
 */
@Path("/configuration/")
public class GlobalConfigurationResource {

    @Context
    private UriInfo uriInfo;

    @Context
    private ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    private EntityManagerFactory emf;
    
    private final int POST_CONFIGURATION_SUCCESS = 1;
    private final int POST_CONFIGURATION_FAILURE = 2;
    

    /**
     * Creates a new instance of GlobalConfigurationResource.
     */
    public GlobalConfigurationResource() {
    }

    private List<GlobalConfiguration> getGlobalConfigurations() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("GlobalConfiguration.findAll");
        List<GlobalConfiguration> gcs = query.getResultList();
        return gcs;
    }


    /**
     * Method for GlobalConfiguration creation and updating
     *
     * @param globalConfiguration
     * @return Response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postGlobalConfiguration(
            final GlobalConfiguration globalConfiguration) {
        final EntityManager em = emf.createEntityManager();
        List<GlobalConfiguration> gcs = getGlobalConfigurations();
        if (gcs.size() == 1) {
            globalConfiguration.setId(gcs.get(0).getId());
        } else if (gcs.size() > 1) {
            Logger.getLogger(GlobalConfigurationResource.class.getName())
                    .log(Level.WARNING,
                            "Configuration singleton condition is unable to maintain.");
            return Response.ok(POST_CONFIGURATION_FAILURE).build();
        }

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.merge(globalConfiguration);
            }
        });
        return Response.ok(POST_CONFIGURATION_SUCCESS).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GlobalConfiguration getGlobalConfiguration() {
        List<GlobalConfiguration> gcs = getGlobalConfigurations();
        if (gcs.isEmpty()) {
            return null;
        } else if (gcs.size() == 1) {
            return gcs.get(0);
        } else {
            Logger.getLogger(GlobalConfigurationResource.class.getName())
                    .log(Level.WARNING,
                            "Configuration singleton condition is unable to maintain.");
            return null;
        }
    }
}
