package cn.comgroup.tzmedia.server.util.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.TimeZone;

/**
 *
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
@Provider
public class MyObjectMapperProvider implements ContextResolver<ObjectMapper> {

    final ObjectMapper defaultObjectMapper;

    public MyObjectMapperProvider() {
        defaultObjectMapper = createDefaultMapper();
    }

    @Override
    public ObjectMapper getContext(final Class<?> type) {
        return defaultObjectMapper;
    }

    private static ObjectMapper createDefaultMapper() {
        final ObjectMapper result = new ObjectMapper();
        result.enable(SerializationFeature.INDENT_OUTPUT);
        result.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        result.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        result.setTimeZone(TimeZone.getTimeZone("CST"));
//        result.enable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
//        result.getDeserializationContext().setAttribute(result, result)
        System.out.println("MyObjectMapperProvider is called +++++++++++++++++++++++++++++++");
//        DateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        TimeZone timeZone=TimeZone.getTimeZone("CST");
//        result.setTimeZone(timeZone);
//        System.out.println("timeZone: "+timeZone);
        return result;
    }
}
