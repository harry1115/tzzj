package cn.comgroup.tzmedia.server;

import cn.comgroup.tzmedia.server.admin.resource.AdministratorLoginResource;
import cn.comgroup.tzmedia.server.admin.resource.AdministratorsResource;
import cn.comgroup.tzmedia.server.admin.resource.CouponDefinitionsResource;
import cn.comgroup.tzmedia.server.admin.resource.UserActivitiesResource;
import cn.comgroup.tzmedia.server.admin.resource.UserCouponsResource;
import cn.comgroup.tzmedia.server.admin.resource.UserDevicesResource;
import cn.comgroup.tzmedia.server.admin.resource.UserFeedbacksResource;
import cn.comgroup.tzmedia.server.admin.resource.UserLoginResource;
import cn.comgroup.tzmedia.server.admin.resource.UserMessagesResource;
import cn.comgroup.tzmedia.server.admin.resource.UsersResource;
import cn.comgroup.tzmedia.server.authentication.filter.AuthenticationExceptionMapper;
import cn.comgroup.tzmedia.server.authentication.filter.SecurityFilter;
import cn.comgroup.tzmedia.server.common.resource.GlobalConfigurationResource;
import cn.comgroup.tzmedia.server.frontpage.resource.FrontPagesResource;
import cn.comgroup.tzmedia.server.orderprocessing.resource.CustomerOrdersResource;
import cn.comgroup.tzmedia.server.orderprocessing.resource.OrderNotificationResource;
import cn.comgroup.tzmedia.server.product.resource.ProductTypesResource;
import cn.comgroup.tzmedia.server.product.resource.ProductsResource;
import cn.comgroup.tzmedia.server.shop.resource.ActivitiesResource;
import cn.comgroup.tzmedia.server.shop.resource.CommonUserActionsResource;
import cn.comgroup.tzmedia.server.shop.resource.PlaybillsResource;
import cn.comgroup.tzmedia.server.shop.resource.ShopsResource;
import cn.comgroup.tzmedia.server.singer.resource.SingersResource;
import cn.comgroup.tzmedia.server.singer.resource.SongsResource;
import cn.comgroup.tzmedia.server.ticket.service.TicketRESTFacade;
import cn.comgroup.tzmedia.server.ticketLog.service.TicketlogRESTFacade;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * StrongServer
 * @author peter.liu@comgroup.cn
 */
@ApplicationPath("/")
public class StrongServer extends ResourceConfig {

    public StrongServer() {
        registerClasses(UsersResource.class);
        registerClasses(AdministratorsResource.class);
        registerClasses(UserLoginResource.class,SecurityFilter.class, 
                AuthenticationExceptionMapper.class);
        registerClasses(AdministratorLoginResource.class);
        registerClasses(ShopsResource.class);
        registerClasses(ActivitiesResource.class);
        registerClasses(SingersResource.class);
        registerClasses(PlaybillsResource.class);
        registerClasses(ProductTypesResource.class);
        registerClasses(ProductsResource.class);
        registerClasses(SongsResource.class);
        registerClasses(FrontPagesResource.class);
        registerClasses(CustomerOrdersResource.class);
        registerClasses(UserFeedbacksResource.class);
        registerClasses(UserCouponsResource.class);
        registerClasses(CouponDefinitionsResource.class);
        registerClasses(UserActivitiesResource.class);
        registerClasses(UserDevicesResource.class);
        registerClasses(OrderNotificationResource.class,SseFeature.class);
        registerClasses(GlobalConfigurationResource.class);
        registerClasses(CommonUserActionsResource.class);
        registerClasses(UserMessagesResource.class);
//        register(new JettisonFeature());
        register(new JacksonFeature());
        register(MultiPartFeature.class);
        register(TicketRESTFacade.class);
        register(TicketlogRESTFacade.class);
//        register(LoggingFilter.class);
    }
}
