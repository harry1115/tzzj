package cn.comgroup.tzmedia.server.orderprocessing.resource;

import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrder;
import cn.comgroup.tzmedia.server.orderprocessing.entity.OrderNotification;
import cn.comgroup.tzmedia.server.orderprocessing.entity.OrderType;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

/**
 * OrderNotificationResource
 *
 */
@Path("/ordernotification/")
@Singleton
public class OrderNotificationResource {

    private static final Logger LOGGER = Logger.getLogger(OrderNotificationResource.class.getName());

    private static final ReentrantReadWriteLock storeLock = new ReentrantReadWriteLock();
    private static final LinkedList<String> itemStore = new LinkedList<>();
    private static final SseBroadcaster broadcaster = new SseBroadcaster();

    private static volatile long reconnectDelay = 0;
    
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");    

    private int numberOfCalls = 0;

    @PersistenceUnit(unitName = "TZMediaPU")
    EntityManagerFactory emf;

    /**
     * List all stored items.
     *
     * @return list of all stored items.
     */
    @GET
//    @Produces(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public OrderNotification[] listItems() {
        try {
            storeLock.readLock().lock();
//            return itemStore.toString();
//            return itemStore.toArray(new String[itemStore.size()]);
            OrderNotification on = new OrderNotification(1001, 0);
            OrderNotification[] ons = new OrderNotification[1];
            ons[0] = on;
            return ons;
        } finally {
            storeLock.readLock().unlock();
        }
    }

    /**
     * Receive & process commands sent by the test client that control the
     * internal resource state.
     *
     * Following is the list of recognized commands:
     * <ul>
     * <li><b>disconnect</b> - disconnect all registered event streams.</li>
     * <li><b>reconnect now</b> - enable client reconnecting.</li>
     * <li><b>reconnect &lt;seconds&gt;</b> - disable client reconnecting.
     * Reconnecting clients will receive a HTTP 503 response with
     * {@value javax.ws.rs.core.HttpHeaders#RETRY_AFTER} set to the amount of
     * milliseconds specified.</li>
     * </ul>
     *
     * @param command command to be processed.
     * @return message about processing result.
     * @throws BadRequestException in case the command is not recognized or not
     * specified.
     */
    @POST
    @Path("commands")
    public String processCommand(String command) {
        if (command == null || command.isEmpty()) {
            throw new BadRequestException("No command specified.");
        }

        if ("disconnect".equals(command)) {
            broadcaster.closeAll();
            return "Disconnected.";
        } else if (command.length() > "reconnect ".length() && command.startsWith("reconnect ")) {
            final String when = command.substring("reconnect ".length());
            try {
                reconnectDelay = "now".equals(when) ? 0 : Long.parseLong(when);
                return "Reconnect strategy updated: " + when;
            } catch (NumberFormatException ignore) {
                // ignored
            }
        }

        throw new BadRequestException("Command not recognized: '" + command + "'");
    }

    /**
     * Connect or re-connect to SSE event stream.
     *
     * @param lastEventId Value of custom SSE HTTP
     * <tt>{@value SseFeature#LAST_EVENT_ID_HEADER}</tt> header. Defaults to
     * {@code -1} if not set.
     * @return new SSE event output stream representing the (re-)established SSE
     * client connection.
     * @throws java.io.IOException
     * @throws java.text.ParseException
     */
    @GET
    @Path("events")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput itemEvents(
            @HeaderParam(SseFeature.LAST_EVENT_ID_HEADER) @DefaultValue("-1") int lastEventId) throws IOException, ParseException {
        final EventOutput eventOutput = new EventOutput();
        if (lastEventId >= 0) {
            LOGGER.log(Level.INFO, "Received last event id :{0}", lastEventId);

            // decide the reconnect handling strategy based on current reconnect delay value.
            final long delay = reconnectDelay;
            if (delay > 0) {
                LOGGER.log(Level.INFO, "Non-zero reconnect delay [{0}] - responding with HTTP 503.", delay);
                throw new ServiceUnavailableException(delay);
            } else {
                LOGGER.info("Zero reconnect delay - reconnecting.");
//                replayMissedEvents(lastEventId, eventOutput);
            }
        }

        if (!broadcaster.add(eventOutput)) {
            LOGGER.severe("!!! Unable to add new event output to the broadcaster !!!");
            // let's try to force a 5s delayed client reconnect attempt
            throw new ServiceUnavailableException(5L);
        } else {
            numberOfCalls++;
//            System.out.println("#################eventOutput is successfully added to broadcaster: " + numberOfCalls);
        }

        final OutboundEvent.Builder eventBuilder
                = new OutboundEvent.Builder();
        eventBuilder.name("order-notification").mediaType(MediaType.APPLICATION_JSON_TYPE);
        
        Date orderDate=new Date();
        String orderDateString=dateFormat.format(orderDate);
        List<CustomerOrder> orders = OrderUtil.getOrdersUsingCriteria(
                null,orderDateString,orderDateString,
                null,null,0,true,0,null,null,0,
                emf.createEntityManager());
        int normalOrders=0;
        int grabSongOrders = 0;
        for (CustomerOrder order : orders) {
            if (order.getOrderType().equals(OrderType.GRABSONG)) {
                grabSongOrders++;
            }else{
                normalOrders++;
            }
        }
        eventBuilder.data(OrderNotification.class,
                new OrderNotification(normalOrders,grabSongOrders));
        final OutboundEvent event = eventBuilder.build();
        eventOutput.write(event);

        return eventOutput;
    }

//    private void replayMissedEvents(final int lastEventId, final EventOutput eventOutput) {
//        try {
//            storeLock.readLock().lock();
//            final int firstUnreceived = lastEventId + 1;
//            final int missingCount = itemStore.size() - firstUnreceived;
//            if (missingCount > 0) {
//                LOGGER.log(Level.INFO, "Replaying events - starting with id {0}", firstUnreceived);
//                final ListIterator<String> it = itemStore.subList(firstUnreceived, itemStore.size()).listIterator();
//                while (it.hasNext()) {
//                    eventOutput.write(createItemEvent(it.nextIndex() + firstUnreceived, it.next()));
//                }
//            } else {
//                LOGGER.info("No events to replay.");
//            }
//        } catch (IOException ex) {
//            throw new InternalServerErrorException("Error replaying missed events", ex);
//        } finally {
//            storeLock.readLock().unlock();
//        }
//    }

    /**
     * Add new item to the item store.
     *
     * Invoking this method will fire 2 new SSE events - 1st about newly added
     * item and 2nd about the new item store size.
     *
     * @param name item name.
     */
    @POST
    public void addItem(@QueryParam("name") String name) {
        System.out.println("addItem name: " + name);
        if (name == null) {
            return;
        }

        final int eventId;
        try {
            storeLock.writeLock().lock();

            eventId = itemStore.size();

            itemStore.add(name);

            // Broadcasting an un-named event with the name of the newly added item in data
//            broadcaster.broadcast(createItemEvent(eventId, name));
            // Broadcasting a named "size" event with the current size of the items collection in data
            OutboundEvent.Builder b = new OutboundEvent.Builder();
            b.mediaType(MediaType.APPLICATION_JSON_TYPE);

            broadcaster.broadcast(b.name("message-to-client").data(String.class, name + "is creating an new order").build());

//            broadcaster.broadcast(new OutboundEvent.Builder().name("message-to-client")
//                    .mediaType(MediaType.APPLICATION_JSON_TYPE).data(Integer.class, eventId + 1).build());
            System.out.println("#######################broadcaster size event######################");

        } finally {
            storeLock.writeLock().unlock();
        }
    }

//    private OutboundEvent createItemEvent(final int eventId, final String name) {
//        System.out.println("Creating event id [" + eventId + "] name [" + name + "]");
//        return new OutboundEvent.Builder().id("" + eventId).data(String.class, name).build();
//    }
}
