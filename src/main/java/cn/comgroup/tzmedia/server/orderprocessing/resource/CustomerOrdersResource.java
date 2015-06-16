package cn.comgroup.tzmedia.server.orderprocessing.resource;

import cn.comgroup.tzmedia.server.admin.entity.CouponStatus;
import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.admin.entity.UserCoupon;
import cn.comgroup.tzmedia.server.common.entity.PaymentTerm;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrder;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrderLine;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrderReportResult;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrderStatus;
import cn.comgroup.tzmedia.server.orderprocessing.entity.OrderType;
import cn.comgroup.tzmedia.server.product.entity.Product;
import cn.comgroup.tzmedia.server.report.CustomerOrderReport;
import cn.comgroup.tzmedia.server.shop.entity.Playbill;
import cn.comgroup.tzmedia.server.shop.entity.PlaybillState;
import cn.comgroup.tzmedia.server.shop.entity.Shop;
import cn.comgroup.tzmedia.server.singer.entity.Song;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * CustomerOrdersResource
 *
 * @author peter.liu@comgroup.cn
 */
@Path("/orders/")
public class CustomerOrdersResource {

    @Context
    private UriInfo uriInfo;

    @Context
    private ServletConfig servletConfig;

    @PersistenceUnit(unitName = "TZMediaPU")
    private EntityManagerFactory emf;
    
    private final int CREATE_ORDER_SUCCESS = 1;
    private final int CREATE_ORDER_NEED_USER = 2;
    private final int CREATE_ORDER_NEED_SHOP = 3;
    private final int CREATE_ORDER_PLAYBILL_STATE_WRONG = 4;
    private final int CREATE_ORDER_ALREADY_CREATED = 6;
    private final int CREATE_ORDER_FINISH = 7;
    
    
    /**
     * Creates a new instance of CustomerOrdersResource.
     */
    public CustomerOrdersResource() {
    } 
   
    @Path("{orderNumber}/")
    public CustomerOrderResource getCustomerOrder(@PathParam("orderNumber") String orderNumber) {
        return new CustomerOrderResource(emf.createEntityManager(), orderNumber, servletConfig);
    }

    /**
     * Method for Product creation
     *
     * @param customerOrder
     * @return Response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCustomerOrder(final CustomerOrder customerOrder) {
        final EntityManager em = emf.createEntityManager();
        int playbillCurrentOrders = 0;
        CustomerOrder orderExist=em.find(CustomerOrder.class, customerOrder.getOrderNumber());
        if(orderExist!=null){
            Logger.getLogger(CustomerOrdersResource.class.getName())
                    .log(Level.WARNING, "Order {0} has already been created.",
                            customerOrder.getOrderNumber());
            return Response.status(Response.Status.CONFLICT)
                    .entity(CREATE_ORDER_ALREADY_CREATED).build();
        }
        
        Shop shop = em.find(Shop.class, customerOrder.getShopId());
        if (shop != null) {
            customerOrder.setShop(shop);
        } else {
            Logger.getLogger(CustomerOrdersResource.class.getName())
                    .log(Level.WARNING, "Order {0} has no shop associated",
                            customerOrder.getOrderNumber());
            return Response.status(Response.Status.CONFLICT)
                    .entity(CREATE_ORDER_NEED_SHOP).build();
        }
        
        User user = em.find(User.class, customerOrder.getUserId());
        if (user != null) {
            customerOrder.setUser(user);
        } else {
            Logger.getLogger(CustomerOrdersResource.class.getName())
                    .log(Level.WARNING, "Order {0} has no user associated",
                            customerOrder.getOrderNumber());
            return Response.status(Response.Status.CONFLICT).entity(CREATE_ORDER_NEED_USER).build();
        }

        List<CustomerOrderLine> orderLines = customerOrder.getCustomerOrderLines();
        customerOrder.setCustomerOrderLines(new ArrayList<CustomerOrderLine>());
        for (CustomerOrderLine orderLine : orderLines) {
            if (orderLine.getProductNumber() != null) {
                orderLine.setProductName(em.find(Product.class,
                        orderLine.getProductNumber()).getProductName());
            }
            if (orderLine.getPlaybillId() > 0) {
                Playbill playbill = em.find(Playbill.class,
                        orderLine.getPlaybillId());
                if (playbill != null) {
                    playbillCurrentOrders = playbill.getCurrentOrders();
                    if(playbillCurrentOrders < 4) {
                    customerOrder.setOrderType(OrderType.GRABSONG);
                    orderLine.setPlaybillName(playbill.getPlaybillName());
//                        playbill.setCurrentOrders(playbillCurrentOrders + 1);
                    } else {
                         return Response.status(Response.Status.CONFLICT).entity(CREATE_ORDER_FINISH).build();
                    }

                }
            }

            if (orderLine.getSongId() > 0) {
                orderLine.setSongName(em.find(Song.class,
                        orderLine.getSongId()).getSongName());
            }
            customerOrder.addCustomerOrderLine(orderLine);
        }
        if (customerOrder.getPlaybillId() > 0) {
            Playbill playbill = em.find(Playbill.class, customerOrder.getPlaybillId());
            if (playbill != null) {
                if (playbill.getPlaybillState().equals(PlaybillState.INACTIVE)
                        || playbill.getPlaybillState().equals(PlaybillState.PAID)) {
                    String[] keysForLog = {customerOrder.getUserId(),
                        String.valueOf(customerOrder.getPlaybillId()),
                        playbill.getPlaybillState().toString()};
                    Logger.getLogger(CustomerOrdersResource.class.getName())
                            .log(Level.WARNING,
                                    "Order for user {0} can not be created as playbill {1} state is {2}.",
                                    keysForLog);
                    return Response.status(Response.Status.CONFLICT)
                            .entity(CREATE_ORDER_PLAYBILL_STATE_WRONG).build();
                } 
                customerOrder.setSingerId(playbill.getSingerId());
                customerOrder.setPlaybillName(playbill.getPlaybillName());
                customerOrder.setSingerName(playbill.getSinger().getSingerName());
                customerOrder.setOrderType(OrderType.GRABSONG);
            }

        }

        final UserCoupon usercoupon = em.find(UserCoupon.class, customerOrder.getCouponNumber());
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(customerOrder);
                if (usercoupon != null) {
                    usercoupon.setCouponStatus(CouponStatus.USED);
                    em.merge(usercoupon);
                }
            }
        });

        return Response.ok().entity(CREATE_ORDER_SUCCESS).build();
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CustomerOrder[] getsAsJsonArray(
            @QueryParam("orderNumber") String orderNumber,
            @QueryParam("fromOrderDate") String fromOrderDate,
            @QueryParam("toOrderDate") String toOrderDate,
            @QueryParam("userId") String userId,
            @QueryParam("orderStatus") CustomerOrderStatus orderStatus,
            @QueryParam("playbillId") int playbillId,
            @QueryParam("hasToBeProcessed") Boolean hasToBeProcessed,
            @QueryParam("tableNumber") int tableNumber,
            @QueryParam("resultLength") int resultLength,
            @QueryParam("orderType") OrderType orderType,
            @QueryParam(value = "paymentTerm") final PaymentTerm paymentTerm,
            @QueryParam(value = "shopId") final int shopId)throws ParseException {
        List<CustomerOrder> orders = OrderUtil.getOrdersUsingCriteria(
                orderNumber,
                fromOrderDate,
                toOrderDate,
                userId,
                orderStatus,
                playbillId,
                hasToBeProcessed,
                tableNumber,
                orderType,
                paymentTerm,
                shopId,
                emf.createEntityManager());
        if (resultLength > 0 && resultLength < orders.size()) {
            return orders.subList(0, resultLength).toArray(new CustomerOrder[resultLength]);
        } else {
            return orders.toArray(new CustomerOrder[orders.size()]);
        }
    }
    
    @GET
    @Path(value = "/report")
    @Produces(value = MediaType.APPLICATION_JSON)
    public CustomerOrderReportResult getOrderReport( 
            @QueryParam(value = "orderNumber") final String orderNumber, 
            @QueryParam(value = "fromOrderDate") final String fromOrderDate, 
            @QueryParam(value = "toOrderDate") final String toOrderDate,
            @QueryParam(value = "userId") final String userId, 
            @QueryParam(value = "orderStatus") final CustomerOrderStatus orderStatus,
            @QueryParam(value = "playbillId") final int playbillId, 
            @QueryParam(value = "hasToBeProcessed") final Boolean hasToBeProcessed, 
            @QueryParam(value = "tableNumber") final int tableNumber,
            @QueryParam(value = "resultLength") final int resultLength, 
            @QueryParam(value = "orderType") final OrderType orderType,
            @QueryParam(value = "paymentTerm") final PaymentTerm paymentTerm,
            @QueryParam(value = "shopId") final int shopId)  
            throws ParseException, FileNotFoundException, IOException{
        List<CustomerOrder> orders = OrderUtil.getOrdersUsingCriteria(
                orderNumber,
                fromOrderDate,
                toOrderDate,
                userId,
                orderStatus,
                playbillId,
                hasToBeProcessed,
                tableNumber,
                orderType,
                paymentTerm,
                shopId,
                emf.createEntityManager());
        String deployPath = PropertiesUtils.getProperties(servletConfig
                .getServletContext()).getProperty("deploy-path");
        return new CustomerOrderReport().runOrderReport(deployPath,orders);
    }
}
