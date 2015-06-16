package cn.comgroup.tzmedia.server.authentication.filter;

import cn.comgroup.tzmedia.server.admin.entity.Administrator;
import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.admin.entity.UserToken;
import cn.comgroup.tzmedia.server.admin.util.UserUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.server.ContainerRequest;

/**
 * Simple authentication filter.
 *
 * Returns response with http status 401 when proper authentication is not
 * provided in incoming request.
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 * @see ContainerRequestFilter
 */
@Provider
@PreMatching
public class SecurityFilter implements ContainerRequestFilter {

    @Inject
    javax.inject.Provider<UriInfo> uriInfo;
    private static final String REALM = "HTTPS authentication for dudunangnang server";

    @PersistenceUnit(unitName = "TZMediaPU")
    private EntityManagerFactory emf;

    @Override
    public void filter(ContainerRequestContext filterContext) throws IOException {
        ContainerRequest containerRequest = (ContainerRequest) filterContext.getRequest();
        String authenticationWeb = containerRequest.
                getHeaderString(AuthenticationConstants.AUTHENTICATIONWEB);
        if (authenticationWeb == null || authenticationWeb.equals("")) {
            User user = authenticateUser(containerRequest);
            filterContext.setSecurityContext(new AuthorizedUser(user));
        } else {
            Administrator administrator = authenticateAdministrator(containerRequest);
            filterContext.setSecurityContext(new AuthorizedAdministrator(administrator));
        }
    }
    
     /**
     * Authentication method for mobile user
     *
     *
     */

    private User authenticateUser(ContainerRequest containerRequest) {
        

        String[] authenticateKeysForLog = {containerRequest.getRequestUri().getPath(),
            containerRequest.getMethod(),};
        Logger.getLogger(SecurityFilter.class.getName())
                .log(Level.FINE,
                        "User authentication: path: {0}, method: {1}.",
                        authenticateKeysForLog);
        
        if (containerRequest.getRequestUri().getPath().contains("/resources/ordernotification")) {
            return null;
        }
        
        if (containerRequest.getRequestUri().getPath().contains("/resources/ticket")) {
            return null;
        }
        
        //For IOS client notification
        if (containerRequest.getRequestUri().getPath().contains("/resources/userdevices")) {
            return null;
        }
        
        if (containerRequest.getRequestUri().getPath().contains("/resources/playbills/dm")) {
            return null;
        }
        
        
        if ((containerRequest.getRequestUri().getPath().contains("/users/registration/sms") 
                ||containerRequest.getRequestUri().getPath().contains("/users/registration/verify")
                ||containerRequest.getRequestUri().getPath().contains("/users/registration/mail"))
                && containerRequest.getMethod().equals("GET")) {
            return null;
        }
        
        if (containerRequest.getRequestUri().getPath().contains("/resources/users") 
                && containerRequest.getMethod().equals("POST")) {
            return null;
        }
        
        //Search if user is already registered
        //should add a hearder indicator
        if (containerRequest.getRequestUri().getPath().contains("/resources/users/checkuserexist") 
                && containerRequest.getRequestUri().getQuery()!=null
                && containerRequest.getRequestUri().getQuery().contains("registerKey")
                && containerRequest.getRequestUri().getQuery().contains("userType")
                && containerRequest.getMethod().equals("GET")) {
            return null;
        }
        
        //user password reset web page retrival
        if (containerRequest.getRequestUri().getPath().contains("/resources/users/resetpassword/webpage")
                && containerRequest.getMethod().equals("GET")) {
            if (containerRequest.getRequestUri().getQuery() != null) {
                if (containerRequest.getRequestUri().getQuery().contains("mail")) {
                    return null;
                }
            }
        }

        //
        if (containerRequest.getRequestUri().getPath().contains("/resources/users")
                &&containerRequest.getRequestUri().getPath().contains("changepswd")){
            return null;
        }
        
        
        // Extract authentication credentials
        String authenticationToken = containerRequest.
                getHeaderString(AuthenticationConstants.AUTHENTICATIONTOKEN);
        String authentication = containerRequest.
                getHeaderString(HttpHeaders.AUTHORIZATION);
        final EntityManager em = emf.createEntityManager();
        if (authentication == null) {
            if (authenticationToken != null) {
                if (TokenHolder.tokenUserMap.
                        containsKey(authenticationToken)) {
                    String userId = TokenHolder.tokenUserMap.get(authenticationToken);
                    return em.find(User.class, userId);
                } else {
                    Query queryUT = em.createNamedQuery("UserToken.findByUserToken");
                    queryUT.setParameter("token", authenticationToken);
                    List<UserToken> userTokenList = queryUT.getResultList();
                    if (userTokenList.isEmpty() || userTokenList.size() > 1) {
                        throw new AuthenticationException(AuthenticationConstants.REQUIRECREDENTIALS, REALM);
                    } else {
                        //Reload user token from database.
                        UserToken userToken = userTokenList.get(0);
                        TokenHolder.tokenUserMap.put(userToken.getToken(), userToken.getUserId());
                        TokenHolder.userTokenMap.put(userToken.getUserId(), userToken.getToken());
                        return em.find(User.class, userToken.getUserId());
                    }
                }
            } else {
                throw new AuthenticationException(AuthenticationConstants.REQUIRECREDENTIALS, REALM);
            }
        }
        if (!authentication.startsWith("Basic ")) {
            throw new AuthenticationException(AuthenticationConstants.AUTHENTICATIONHEADERSHOULDSTARTWITHBASIC, REALM);
            // additional checks should be done here
            // "Only HTTP Basic authentication is supported"
        }
        authentication = authentication.substring("Basic ".length());
        String[] values = Base64.decodeAsString(authentication).split(":");
        if (values.length < 3) {
            throw new AuthenticationException(AuthenticationConstants.INVALIDSYNTAXFORIDANDPASSWORD, REALM);
        }
        String loginKey = values[0];
        String password = values[1];
        String userType = values[2];

        if (loginKey == null || password == null||userType==null) {
            throw new AuthenticationException(AuthenticationConstants.MISSINGIDORPASSWORD, REALM);
        }

        // Validate the extracted credentials
        User user = UserUtil.findUserByKeyAndType(em, loginKey, userType);
        
        if (user == null) {
            throw new AuthenticationException(AuthenticationConstants.INVALIDIDORPASSWORD, REALM);
        }

        String[] loginKeys = {user.getUserId(), userType, loginKey};
        if (Base64.decodeAsString(user.getPassword()).equals(password)) {
            Logger.getLogger(SecurityFilter.class.getName())
                    .log(Level.FINE,
                            "User login with key and password: userId: {0}, userType: {1}, key: {2}, password: ******.",
                            loginKeys);
            String userId=user.getUserId();
            //The token is persisted in the database.
            if (!TokenHolder.userTokenMap.containsKey(userId)) {
                boolean userTokenExist = em.find(UserToken.class, userId) != null;
                final UserToken userToken = userTokenExist
                        ? em.find(UserToken.class, userId)
                        : new UserToken(userId, TokenGenerator.nextToken(), null);
                if (!userTokenExist) {
                    TransactionManager.manage(new Transactional(em) {
                        @Override
                        public void transact() {
                            em.persist(userToken);
                        }
                    });
                }

                TokenHolder.tokenUserMap.put(userToken.getToken(), userId);
                TokenHolder.userTokenMap.put(userId, userToken.getToken());
            }
            containerRequest.header(AuthenticationConstants.USERIDADDEDINSECURITYFILTER,
                    userId);
        } else {
            Logger.getLogger(SecurityFilter.class.getName())
                    .log(Level.WARNING,
                            "User not authenticated: userId: {0}, userType: {1}, key: {2}, password: {3}.",
                            loginKeys);
            throw new AuthenticationException(AuthenticationConstants.INVALIDIDORPASSWORD, REALM);
        }
        return user;
    }

    /**
     * Authentication method for web administrator
     *
     *
     */
    private Administrator authenticateAdministrator(ContainerRequest containerRequest) {

        //mobile user reset password from web page
        if (containerRequest.getRequestUri().getPath()
                .contains("/resources/users/resetpassword/mail")
                && containerRequest.getMethod().equals("POST")) {
            return null;
        }
        
        //share page
        if (containerRequest.getRequestUri().getPath().contains("/resources/shops")
                || containerRequest.getRequestUri().getPath().contains("/resources/singers")
                || containerRequest.getRequestUri().getPath().contains("/resources/activities")
                || containerRequest.getRequestUri().getPath().contains("/resources/frontpages")
                || containerRequest.getRequestUri().getPath().contains("/resources/playbills")) {
            if (containerRequest.getRequestUri().getQuery() != null) {
                if (containerRequest.getRequestUri().getQuery().contains("displaySharePage")) {
                    return null;
                }
            }
        }
       
        
        // Extract authentication credentials
        String authenticationToken = containerRequest.
                getHeaderString(AuthenticationConstants.AUTHENTICATIONTOKEN);
        String authentication = containerRequest.
                getHeaderString(HttpHeaders.AUTHORIZATION);
        EntityManager em = emf.createEntityManager();
        if (authentication == null||authentication.trim().equals("")) {
            if (authenticationToken != null && TokenHolder.tokenAdministratorMap.
                    containsKey(authenticationToken)) {
                String administratorId = TokenHolder.tokenAdministratorMap.get(authenticationToken);
                return em.find(Administrator.class, administratorId);
            }
            throw new AuthenticationException(AuthenticationConstants.REQUIRECREDENTIALS, REALM);
        }
        if (!authentication.startsWith("Basic ")) {
            throw new AuthenticationException(AuthenticationConstants.AUTHENTICATIONHEADERSHOULDSTARTWITHBASIC, REALM);
            // additional checks should be done here
            // "Only HTTP Basic authentication is supported"
        }
        authentication = authentication.substring("Basic ".length());
        String[] values = Base64.decodeAsString(authentication).split(":");
        if (values.length < 2) {
            throw new AuthenticationException(AuthenticationConstants.INVALIDSYNTAXFORIDANDPASSWORD, REALM);
        }
        String administratorId = values[0];
        String password = values[1];
        //TODO hole for test
        if (administratorId.equals("datainitialization") && password.
                equals("ddfi3FD89dfdadfdJJOidf")) {
            String tokeyGenerated = "thistokencanbeusedtopentheheartofthewomenyouloved";
            containerRequest.header(AuthenticationConstants.USERIDADDEDINSECURITYFILTER,
                    administratorId);
            TokenHolder.tokenAdministratorMap.put(tokeyGenerated, administratorId);
            TokenHolder.administratorTokenMap.put(administratorId, tokeyGenerated);
            return new Administrator("datainitialization", "ddfi3FD89dfdadfdJJOidf");
        }
        //End for test hole

        if ((administratorId == null) || (password == null)) {
            throw new AuthenticationException(AuthenticationConstants.MISSINGIDORPASSWORD, REALM);
        }

        // Validate the extracted credentials
        Administrator administrator = em.find(Administrator.class, administratorId);
        if (administrator == null) {
            throw new AuthenticationException(AuthenticationConstants.INVALIDIDORPASSWORD, REALM);
        }

        if (Base64.decodeAsString(administrator.getPassword()).equals(password)) {
            String tokeyGenerated = TokenGenerator.nextToken();
            while (TokenHolder.tokenAdministratorMap.containsKey(tokeyGenerated)) {
                tokeyGenerated = TokenGenerator.nextToken();
            }
            TokenHolder.tokenAdministratorMap.put(tokeyGenerated, administratorId);
            TokenHolder.administratorTokenMap.put(administratorId, tokeyGenerated);
            containerRequest.header(AuthenticationConstants.USERIDADDEDINSECURITYFILTER,
                    administratorId);
        } else {
            throw new AuthenticationException(AuthenticationConstants.INVALIDIDORPASSWORD, REALM);
        }
        return administrator;
    }

    public class AuthorizedUser implements SecurityContext {

        private User user;
        private Principal principal;

        public AuthorizedUser(final User user) {
            this.user = user;
            this.principal = new Principal() {

                public String getName() {
                    return user.getUserId();
                }
            };
        }

        public Principal getUserPrincipal() {
            return this.principal;
        }

        public boolean isUserInRole(String role) {
            //TODO 
            return (role.equals(user.getUserName()));
        }

        public boolean isSecure() {
            return "https".equals(uriInfo.get().getRequestUri().getScheme());
        }

        public String getAuthenticationScheme() {
            return SecurityContext.BASIC_AUTH;
        }
    }

    public class AuthorizedAdministrator implements SecurityContext {

        private Administrator administrator;
        private Principal principal;

        public AuthorizedAdministrator(final Administrator admin) {
            this.administrator = admin;
            this.principal = new Principal() {

                public String getName() {
                    return administrator.getUserId();
                }
            };
        }

        public Principal getUserPrincipal() {
            return this.principal;
        }

        public boolean isUserInRole(String role) {
            //TODO 
            return (role.equals(administrator.getUserName()));
        }

        public boolean isSecure() {
            return "https".equals(uriInfo.get().getRequestUri().getScheme());
        }

        public String getAuthenticationScheme() {
            return SecurityContext.BASIC_AUTH;
        }
    }
}
