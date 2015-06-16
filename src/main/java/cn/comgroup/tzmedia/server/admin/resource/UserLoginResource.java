package cn.comgroup.tzmedia.server.admin.resource;

import cn.comgroup.tzmedia.server.admin.entity.LoginResponse;
import cn.comgroup.tzmedia.server.authentication.filter.AuthenticationConstants;
import cn.comgroup.tzmedia.server.authentication.filter.TokenHolder;
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
@Path("/authentication/user")
public class UserLoginResource {

    /**
     * Creates a new instance of LoginResource.
     */
    public UserLoginResource() {
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public LoginResponse login(@Context HttpHeaders headers) {
        String userId = headers.getHeaderString(
                AuthenticationConstants.USERIDADDEDINSECURITYFILTER);
        return new LoginResponse(userId,TokenHolder.userTokenMap.get(userId), 
                AuthenticationConstants.LOGINSUCCESS);
    }
}
