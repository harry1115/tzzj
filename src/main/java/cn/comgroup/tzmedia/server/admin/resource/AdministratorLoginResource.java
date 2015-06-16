package cn.comgroup.tzmedia.server.admin.resource;

import cn.comgroup.tzmedia.server.admin.entity.Administrator;
import cn.comgroup.tzmedia.server.admin.entity.LoginResponse;
import cn.comgroup.tzmedia.server.authentication.filter.AuthenticationConstants;
import cn.comgroup.tzmedia.server.authentication.filter.TokenHolder;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author pcnsh197
 */
@Path("/authentication/administrator")
public class AdministratorLoginResource {

    @PersistenceUnit(unitName = "TZMediaPU")
    private EntityManagerFactory emf;

    /**
     * Creates a new instance of LoginResource.
     */
    public AdministratorLoginResource() {
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public LoginResponse login(@Context HttpHeaders headers) {
        String userId = headers.getHeaderString(
                AuthenticationConstants.USERIDADDEDINSECURITYFILTER);
        Administrator administrator = emf.createEntityManager()
                .find(Administrator.class, userId);
        LoginResponse response = new LoginResponse(userId,
                TokenHolder.administratorTokenMap.get(userId),
                AuthenticationConstants.LOGINSUCCESS);
        if (administrator != null) {
            response.setAdminRole(administrator.getAdminRole());
            response.setShopId(administrator.getShopId());
        }
        return response;
    }
}
