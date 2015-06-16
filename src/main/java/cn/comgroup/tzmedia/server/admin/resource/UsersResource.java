package cn.comgroup.tzmedia.server.admin.resource;

import cn.comgroup.tzmedia.server.admin.entity.CouponDefinition;
import cn.comgroup.tzmedia.server.admin.entity.PhoneVerifyResponse;
import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.admin.entity.UserCoupon;
import cn.comgroup.tzmedia.server.admin.entity.UserPasswordResetResponse;
import cn.comgroup.tzmedia.server.admin.entity.UserType;
import cn.comgroup.tzmedia.server.admin.util.UserUtil;
import cn.comgroup.tzmedia.server.authentication.filter.AuthenticationConstants;
import cn.comgroup.tzmedia.server.authentication.filter.TokenGenerator;
import cn.comgroup.tzmedia.server.authentication.filter.TokenHolder;
import cn.comgroup.tzmedia.server.authentication.filter.VerificationCodeGenerator;
import cn.comgroup.tzmedia.server.util.encrypt.EncryptUtil;
import cn.comgroup.tzmedia.server.util.mail.SendCloudMail;
import cn.comgroup.tzmedia.server.util.mail.TZMediaMail;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import cn.comgroup.tzmedia.server.util.query.QueryUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.mail.MessagingException;
import javax.net.ssl.SSLContext;
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
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.internal.util.Base64;

/**
 * 
 * UsersResource
 * @author peter.liu@comgroup.cn
 */
@Path("/users/")
public class UsersResource {

    @Context
    UriInfo uriInfo;

    @Context
    ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    EntityManagerFactory emf;
    
    /**
     * Creates a new instance of Users.
     */
    public UsersResource() {
    }
    
    
    private List<User> getUsersUsingCriteria(String fromUserId, String toUserId,
            String userName, String email, String qq,
            String weibo, String phoneNumber,String fromCreationDate,
            String toCreationDate) throws ParseException {
        if (QueryUtil.queryParameterProvided(fromUserId) 
                && !QueryUtil.queryParameterProvided(toUserId)) {
            toUserId = fromUserId;
        }
        if (QueryUtil.queryParameterProvided(toUserId) 
                && !QueryUtil.queryParameterProvided(fromUserId)) {
            fromUserId = toUserId;
        }
        
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> c = cb.createQuery(User.class);
        Root<User> user = c.from(User.class);
        c.select(user);
        c.orderBy(cb.asc(user.get("userId")));
        c.distinct(true);
        List<Predicate> criteria = new ArrayList<>();
        
        if (QueryUtil.queryParameterProvided(fromUserId)) {
            ParameterExpression<String> p = cb.parameter(String.class, "fromUserId");
            criteria.add(cb.greaterThanOrEqualTo(user.<String>get("userId"), p));
        }
        
        if (QueryUtil.queryParameterProvided(toUserId)) {
            ParameterExpression<String> p = cb.parameter(String.class, "toUserId");
            criteria.add(cb.lessThanOrEqualTo(user.<String>get("userId"), p));
        }
        
        if (QueryUtil.queryParameterProvided(userName)) {
            ParameterExpression<String> p = cb.parameter(String.class, "userName");
            if (userName.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(user.<String>get("userName"), p));
            } else {
                criteria.add(cb.equal(user.get("userName"), p));
            }
        }
        
        if (QueryUtil.queryParameterProvided(fromCreationDate)) {
            ParameterExpression<Calendar> p = cb.parameter(Calendar.class, "fromCreationDate");
            criteria.add(cb.greaterThanOrEqualTo(user.<Calendar>get("creationDate"), p));
        }
        
        if (QueryUtil.queryParameterProvided(toCreationDate)) {
            ParameterExpression<Calendar> p = cb.parameter(Calendar.class, "toCreationDate");
            criteria.add(cb.lessThanOrEqualTo(user.<Calendar>get("creationDate"), p));
        }
        
        if (QueryUtil.queryParameterProvided(email)) {
            ParameterExpression<String> p = cb.parameter(String.class, "email");
            if (email.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(user.<String>get("email"), p));
            } else {
                criteria.add(cb.equal(user.get("email"), p));
            }
        }
        if (QueryUtil.queryParameterProvided(qq)) {
            ParameterExpression<String> p = cb.parameter(String.class, "qq");
            if (qq.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(user.<String>get("qq"), p));
            } else {
                criteria.add(cb.equal(user.get("qq"), p));
            }
        }
        
        if (QueryUtil.queryParameterProvided(weibo)) {
            ParameterExpression<String> p = cb.parameter(String.class, "weibo");
            if (weibo.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(user.<String>get("weibo"), p));
            } else {
                criteria.add(cb.equal(user.get("weibo"), p));
            }
        }
        
        if (QueryUtil.queryParameterProvided(phoneNumber)) {
            ParameterExpression<String> p = cb.parameter(String.class, "phoneNumber");
            if (phoneNumber.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(user.<String>get("phoneNumber"), p));
            } else {
                criteria.add(cb.equal(user.get("phoneNumber"), p));
            }
        }

        if (criteria.size() == 1) {
            c.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[0])));
        }
        TypedQuery<User> q = em.createQuery(c);

        
        if (QueryUtil.queryParameterProvided(fromUserId)) {
             q.setParameter("fromUserId", fromUserId);
        }
        
        if (QueryUtil.queryParameterProvided(toUserId)) {
            q.setParameter("toUserId", toUserId);
        }
        
        if (QueryUtil.queryParameterProvided(userName)) {
            q.setParameter("userName", userName
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }
        
        if (QueryUtil.queryParameterProvided(email)) {
            q.setParameter("email", email
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }
        if (QueryUtil.queryParameterProvided(qq)) {
            q.setParameter("qq", qq
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }
        
        if (QueryUtil.queryParameterProvided(weibo)) {
            q.setParameter("weibo", weibo
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }
        
        if (QueryUtil.queryParameterProvided(phoneNumber)) {
            q.setParameter("phoneNumber", phoneNumber
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }
        
         if (QueryUtil.queryParameterProvided(fromCreationDate)) {
            Calendar fromDate = Calendar.getInstance();
            fromDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(fromCreationDate));
            q.setParameter("fromCreationDate", fromDate);
        }

        if (QueryUtil.queryParameterProvided(toCreationDate)) {
            Calendar toDate = Calendar.getInstance();
            toDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(toCreationDate));
            q.setParameter("toCreationDate", toDate);
        }

        return q.getResultList();
    }
    

    @Path("{userid}/")
    public UserResource getUser(@PathParam("userid") String userid) {
        return new UserResource(uriInfo, emf, userid,servletConfig);
    }
    /**
     * Method for user register
     *
     * @param user
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postUser(final User user) {
        String[] userKeysForLog = {user.getQq(),
            user.getWeibo(),
            user.getEmail(),
            user.getPhoneNumber()};
        Logger.getLogger(UsersResource.class.getName())
                .log(Level.INFO,
                        "######### User registeration: QQ: {0}, Weibo: {1}, Mail: {2}, Mobile: {3}.",
                        userKeysForLog);
        boolean userExist = false;

        switch (user.getUserType()) {
            case QQ:
                userExist = userExist(user.getQq(), user.getUserType());
                break;
            case WEIBO:
                userExist = userExist(user.getWeibo(), user.getUserType());
                break;
            case MOBILE:
                userExist = userExist(user.getPhoneNumber(), user.getUserType());
                break;
            case EMAIL:
                userExist = userExist(user.getEmail(), user.getUserType());
                break;
            default:
                break;
        }
        if (userExist) {
            Logger.getLogger(UsersResource.class.getName())
                    .log(Level.WARNING,
                            "######### User registeration failed: QQ: {0}, Weibo: {1}, Mail: {2}, Mobile: {3}.",
                            userKeysForLog);
            return Response.ok(AuthenticationConstants.DUPLICATEUSERID).entity(user).build();
        }
        if (user.getUserType() == null
                || UserType.DUDU.equals(user.getUserType())) {
            Logger.getLogger(UsersResource.class.getName())
                    .log(Level.WARNING,
                            "######### User registeration failed: QQ: {0}, Weibo: {1}, Mail: {2}, Mobile: {3}.",
                            userKeysForLog);
            return Response.ok(AuthenticationConstants.USERTYPEWRONG).entity(user).build();
        }
        
        if(user.getUserName()!=null){
            user.setUserName(user.getUserName().trim());
        }
        final EntityManager em = emf.createEntityManager();
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(user);
                Calendar today = Calendar.getInstance();
                today.setTime(new Date());
                Query queryCD = em.createNamedQuery("CouponDefinition.findValidCoupon");
                queryCD.setParameter("expiryDate", today);
                List<CouponDefinition> couponDefinitions = queryCD.getResultList();
                for (CouponDefinition cd : couponDefinitions) {
                    if (cd.isForAllUser()) {
                        UserCoupon userCoupon = new UserCoupon(user.getUserId());
                        userCoupon.setUser(user);
                        userCoupon.setCouponDefinitionNumber(
                                cd.getCouponDefinitionNumber());
                        userCoupon.setCouponDefinition(cd);
                        em.persist(userCoupon);
                    }
                }

            }
        });
        return Response.ok(AuthenticationConstants.REGISTERSUCCESS).entity(user).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User[] getUsersAsJsonArray(@QueryParam("fromUserId") String fromUserId,
            @QueryParam("toUserId") String toUserId, 
            @QueryParam("userName") String userName,
            @QueryParam("email") String email,
            @QueryParam("qq") String qq,
            @QueryParam("weibo") String weibo,
            @QueryParam("phoneNumber") String phoneNumber,
            @QueryParam("resultLength") int resultLength,
            @QueryParam("fromCreationDate") String fromCreationDate,
            @QueryParam("toCreationDate") String toCreationDate) throws ParseException {             
        List<User> users = getUsersUsingCriteria(fromUserId, toUserId,
                userName, email, qq,
                weibo, phoneNumber,fromCreationDate,toCreationDate);
        if (resultLength > 0 && resultLength < users.size()) {
            return users.subList(0, resultLength).toArray(new User[resultLength]);
        } else {
            return users.toArray(new User[users.size()]);
        }
    }
    
    /**
     *  The response entity from message service is 
     * {"statusCode":"000000","templateSMS":{"dateCreated":"20140827105250",
     * "smsMessageSid":"20140827105250065847"}}
     * 
     * @param phoneNumber
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException 
     */
    @GET
    @Path("registration/sms")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendVerifyMessageToMobile(@QueryParam("phoneNumber") String phoneNumber)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (phoneNumber == null) {
            return Response.ok(
                    new PhoneVerifyResponse(
                            AuthenticationConstants.MESSAGESENDFAILURE,
                            "Parameter phoneNumber provided is null",
                            null)).build();
        }
        Properties tzProperties = PropertiesUtils
                .getProperties(servletConfig.getServletContext());
        SslConfigurator sslConfig = SslConfigurator.newInstance()
                .trustStoreFile(tzProperties.getProperty("ssl.trust.store.file"))
                .trustStorePassword(tzProperties.getProperty("ssl.trust.store.pass"))
                .trustStoreType("JKS")
                .trustManagerFactoryAlgorithm("PKIX")
                .keyStoreFile(tzProperties.getProperty("ssl.key.store.file"))
                .keyPassword(tzProperties.getProperty("ssl.key.store.pass"))
                .keyStoreType("JKS")
                .keyManagerFactoryAlgorithm("SunX509")
                .keyStoreProvider("SUN")
                .securityProtocol("SSL");
        
        SSLContext sslContext = sslConfig.createSSLContext();
        Client client = ClientBuilder.newBuilder().sslContext(sslContext).build();
       
        WebTarget target = client.target(tzProperties.getProperty("mobile.verify.service"));
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String accountSid=tzProperties.getProperty("mobile.account.sid");
        String encodeString = accountSid
                + tzProperties.getProperty("mobile.auth.token") 
                + timeStamp;    
        String authenticationString = accountSid+":"+ timeStamp;
        String sig = new EncryptUtil().md5Digest(encodeString);

        JsonObjectBuilder messageBuilder = Json.createObjectBuilder();
        JsonArrayBuilder datasBuilder = Json.createArrayBuilder();
        String verificationCode=String.valueOf(VerificationCodeGenerator
                .randInt(100000, 999999));
        datasBuilder
                .add(verificationCode)
                .add(tzProperties.getProperty("mobile.code.active.time"));
        messageBuilder
                .add("to", phoneNumber)
                .add("appId", tzProperties.getProperty("mobile.appid"))
                .add("templateId", tzProperties.getProperty("mobile.templateid"))
                .add("datas", datasBuilder);

        Response providerResponse=target.queryParam("sig", sig)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, Base64
                        .encodeAsString(authenticationString))
                .post(Entity.entity(messageBuilder.build(),
                                MediaType.APPLICATION_JSON));        
        JsonObject jsonObject = providerResponse.readEntity(JsonObject.class);
        if (jsonObject.getString("statusCode").equals("000000")) {
            TokenHolder.verificationCodeMap.put(phoneNumber, verificationCode);
            return Response.ok(
                    new PhoneVerifyResponse(
                            AuthenticationConstants.MESSAGESENDSUCCESS,
                            jsonObject.toString(),
                            verificationCode)).build();
        } else {
            return Response.ok(
                    new PhoneVerifyResponse(
                            AuthenticationConstants.MESSAGESENDFAILURE,
                            jsonObject.toString(),
                            null)).build();
        }
    }
    
    @GET
    @Path("registration/verify")
    public Response verifyMessage(
            @QueryParam("phoneOrMail") String phoneOrMail,
            @QueryParam("verificationCode") String verificationCode){
        if(phoneOrMail==null){
            return Response.ok(AuthenticationConstants.VERIFYFAILURE).build();
        }
        String vc=TokenHolder.verificationCodeMap.get(phoneOrMail);
        if(vc!=null&&vc.equals(verificationCode)){
            TokenHolder.verificationCodeMap.remove(phoneOrMail);
            return Response.ok(AuthenticationConstants.VERIFYSUCCESS).build();
        }else{
            return Response.ok(AuthenticationConstants.VERIFYFAILURE).build();
        }
    }
    
    
    @GET
    @Path("registration/mail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendVerifyMessageToEmail(@QueryParam("mail") String mail)
            throws MessagingException, IOException {
        ServletContext sc = servletConfig.getServletContext();
        Properties tzMediaProperties = PropertiesUtils.getProperties(sc);
        String verificationCode = String.valueOf(VerificationCodeGenerator
                .randInt(100000, 999999));

        String subject = tzMediaProperties.getProperty("validation.with.mail.subject");
        String tail = tzMediaProperties.getProperty("password.retrival.mail.tail");
        if (mail != null) {
            final EntityManager em = emf.createEntityManager();
            Query queryE = em.createNamedQuery("User.findByEmail");
            queryE.setParameter("email", mail);
            List<User> userList = queryE.getResultList();
            //User already registered
            if (userList.size() > 0) {
                return Response.ok(
                        new PhoneVerifyResponse(
                                AuthenticationConstants.EMAILPROVIDEDISNOTCORRECT,
                                mail,
                                null)).build();
            }
            String body = "<p>亲爱的 " + mail + ",</p>" + "<p>您的嘟嘟囔囔邮箱注册验证码是:"
                    + verificationCode
                    + "</p>"
                    + tail;
//            TZMediaMail.send(mail, subject, body, null, sc);
            SendCloudMail.send(mail, subject, body);
            TokenHolder.verificationCodeMap.put(mail, verificationCode);
            return Response.ok(
                    new PhoneVerifyResponse(
                            AuthenticationConstants.EMAILSUCCESSFULLYSEND,
                            null,
                            verificationCode)).build();
        } else {
            return Response.ok(
                    new PhoneVerifyResponse(
                            AuthenticationConstants.EMAILPROVIDEDISNOTCORRECT,
                            mail,
                            null)).build();
        }

    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("resetpassword/webpage")
    public Response getPasswordResetWebPage(@QueryParam("mail") String mail)
            throws MessagingException, IOException {
        if ((mail == null || mail.trim().equals(""))) {
            return Response.ok(AuthenticationConstants.EMAILPROVIDEDISNOTCORRECT).build();
        }
        
        final EntityManager em = emf.createEntityManager();
        Query queryE = em.createNamedQuery("User.findByEmail");
        queryE.setParameter("email", mail);
        List<User> userList = queryE.getResultList();
        if (userList.isEmpty() || userList.size() > 1) {
            return Response.ok(AuthenticationConstants.EMAILPROVIDEDISNOTCORRECT).build();
        }
        
        ServletContext sc = servletConfig.getServletContext();
        Properties tzMediaProperties = PropertiesUtils.getProperties(sc);
        String subject = tzMediaProperties.getProperty("password.retrival.mail.subject");
        String tail = tzMediaProperties.getProperty("password.retrival.mail.tail");
        
        final User userEntity = userList.get(0);
        String tokeyGenerated = TokenGenerator.nextToken();
        
        //Put the stake that holds the user password reset.
        TokenHolder.userPasswordTokenMap.put(userEntity.getUserId(), tokeyGenerated);
        String body = "<p>亲爱的"
                + userEntity.getEmail()
                + ",</p><p>"
                + "重新设置嘟嘟囔囔密码请点击下面的链接:</p>"
                //链接
                + "<p><a href="+uriInfo.getBaseUri().toString().replace("resources", "#")
                + "resetuserpassword>"+uriInfo.getBaseUri().toString().replace("resources", "#") + "resetuserpassword</a>"
                + "</p>"
                //mail last text
                + tail;
        SendCloudMail.send(mail, subject, body);
        //TZMediaMail.send(mail, subject, body, null, sc);
        return Response.ok(AuthenticationConstants.EMAILSUCCESSFULLYSEND).build();
    }
    
    
    /**
     * Method for user reset password
     *
     * @param user
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("resetpassword/mail")
    @Produces(MediaType.APPLICATION_JSON)
    public UserPasswordResetResponse resetPasswordWithMail(final User user) {
        String email = user.getEmail();
        final EntityManager em = emf.createEntityManager();
        Query queryE = em.createNamedQuery("User.findByEmail");
        queryE.setParameter("email", email);
        List<User> userList = queryE.getResultList();
        if (userList.isEmpty() || userList.size() > 1) {
            return new UserPasswordResetResponse(AuthenticationConstants.RESETPASSWORDFAILURE);
        }

        final User userEntity = userList.get(0);
        userEntity.setPassword(user.getPassword());

        String tokenGotFromServer = TokenHolder.userPasswordTokenMap.get(userEntity.getUserId());
        //The user is not request for reset password
        if (tokenGotFromServer == null) {
            return new UserPasswordResetResponse(AuthenticationConstants.RESETPASSWORDFAILURE);
        }
        //Remove the stake that holds the user password reset.
        TokenHolder.userPasswordTokenMap.remove(userEntity.getUserId());

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(userEntity);
            }
        });
        return new UserPasswordResetResponse(AuthenticationConstants.RESETPASSWORDSUCCESS);
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("checkuserexist")
    public boolean checkUserExist(@QueryParam("registerKey") String registerKey,
            @QueryParam("userType") UserType userType) {
        return userExist(registerKey,userType);
    }
    
    private boolean userExist(String registerKey, UserType userType) {
        String[] authenticateKeysForLog = {registerKey, userType.name()};
        Logger.getLogger(UsersResource.class.getName())
                .log(Level.INFO,
                        "######### Check if user exists: registerKey: {0}, userType: {1}.",
                        authenticateKeysForLog);
        EntityManager em = emf.createEntityManager();
        if (UserType.MOBILE.equals(userType)) {
            Query queryQ = em.createNamedQuery("User.findByPhoneNumber");
            queryQ.setParameter("phoneNumber", registerKey);
            return !queryQ.getResultList().isEmpty();
        }
        if (UserType.DUDU.equals(userType)) {
            Query queryQ = em.createNamedQuery("User.findByUserId");
            queryQ.setParameter("userId", registerKey);
            return !queryQ.getResultList().isEmpty();
        }
        if (UserType.QQ.equals(userType)) {
            Query queryQ = em.createNamedQuery("User.findByQQ");
            queryQ.setParameter("qq", registerKey);
            return !queryQ.getResultList().isEmpty();
        }
        if (UserType.WEIBO.equals(userType)) {
            Query queryQ = em.createNamedQuery("User.findByWeibo");
            queryQ.setParameter("weibo", registerKey);
            return !queryQ.getResultList().isEmpty();
        }
        if (UserType.EMAIL.equals(userType)) {
            Query queryQ = em.createNamedQuery("User.findByEmail");
            queryQ.setParameter("email", registerKey);
            return !queryQ.getResultList().isEmpty();
        }
        return true;
    }
    
    @PUT
    @Path("changepswd")
    public Response changePassword(final User user) {
        User userFound = null;
        final EntityManager em = emf.createEntityManager();
        if (UserType.EMAIL.equals(user.getUserType())) {
            userFound = UserUtil.findUserByKeyAndType(em, user.getEmail(),
                    user.getUserType().name());
        } else if (UserType.MOBILE.equals(user.getUserType())) {
            userFound = UserUtil.findUserByKeyAndType(em, user.getPhoneNumber(),
                    user.getUserType().name());
        }
        if (userFound != null) {
            final User userEntity = userFound;
            userEntity.setPassword(user.getPassword());
            TransactionManager.manage(new Transactional(em) {
                @Override
                public void transact() {
                    em.merge(userEntity);
                }
            });
            return Response.ok(AuthenticationConstants.UPDATESUCCESS).build();
        } else {
            String[] keysForLog = {user.getEmail(), user.getPhoneNumber(), 
                user.getUserType().name()};
            Logger.getLogger(UsersResource.class.getName())
                    .log(Level.INFO,
                            "Change password faild: mail: {0}, phoneNumber: {1}, userType: {2}.",
                            keysForLog);
            return Response.ok(AuthenticationConstants.UPDATEFAILURE).build();
        }
    }
}
