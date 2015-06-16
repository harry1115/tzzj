package cn.comgroup.tzmedia.server.exception;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

/**
 * @author peter.liu@comgroup.cn
 */
public class ExtendedNotFoundException extends NotFoundException {

    public ExtendedNotFoundException(final String message) {
        super(Response.status(Response.Status.NOT_FOUND).entity(message).build());
    }
}
