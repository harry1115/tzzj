package cn.comgroup.tzmedia.server.admin.resource;

import cn.comgroup.tzmedia.server.admin.entity.Administrator;
import cn.comgroup.tzmedia.server.authentication.filter.AuthenticationConstants;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
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
import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.internal.util.Base64;

/**
 * @author peter.liu@comgroup.cn
 */
@Path("/administrators/")
public class AdministratorsResource {

    @Context
    private UriInfo uriInfo;
    
    @Context
    private ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    private EntityManagerFactory emf;

    /**
     * Creates a new instance of AdministratorsResource.
     */
    public AdministratorsResource() {
    }

    @SuppressWarnings("unchecked")
    public List<Administrator> getAdministrators() {
        return emf.createEntityManager().createQuery("SELECT u from Administrator u").
                getResultList();
    }

    @Path("{userid}/")
    public AdministratorResource getAdministrator(@PathParam("userid") String userid) {
        return new AdministratorResource(uriInfo, emf,
                userid, servletConfig);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Administrator[] getAdministratorsAsJsonArray() {
        List<Administrator> admins = getAdministrators();
         return admins.toArray(new Administrator[admins.size()]);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postAdministrator(final Administrator administrator) {
        final EntityManager em = emf.createEntityManager();
        Administrator administratorExist = em.find(Administrator.class, administrator.getUserId());
        if (administratorExist != null) {
            return Response.status(409).entity("administrator already exist!\n").build();
        }
        if (administrator.getPassword() == null || administrator.getPassword().trim().equals("")) {
            administrator.setPassword(Base64.encodeAsString("abc123"));
        }

        TransactionManager.manage(new Transactional(em) {
            public void transact() {
                em.persist(administrator);
            }
        });
        return Response.ok(AuthenticationConstants.REGISTERSUCCESS).build();
    }
}
