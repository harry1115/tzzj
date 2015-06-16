package cn.comgroup.tzmedia.server.admin.resource;

import cn.comgroup.tzmedia.server.admin.entity.*;
import cn.comgroup.tzmedia.server.exception.ExtendedNotFoundException;
import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletConfig;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

/**
 * peter.liu@comgroup.cn
 */
public class AdministratorResource {

    private final String userId; // userid from url
    private final Administrator administratorEntity; // appropriate jpa user entity

    private final ServletConfig servletConfig;
    private final UriInfo uriInfo; // actual uri info provided by parent resource
    private final EntityManagerFactory emf;
    private final EntityManager em; // entity manager provided by parent resource
    private final String deployPath;

 
    public AdministratorResource(UriInfo uriInfo, EntityManagerFactory emf, 
            String userId,ServletConfig servletConfig) {
        this.uriInfo = uriInfo;
        this.userId = userId;
        this.emf=emf;
        this.em = emf.createEntityManager();
        this.administratorEntity = em.find(Administrator.class, userId);
        this.servletConfig = servletConfig;
        this.deployPath = PropertiesUtils.getProperties(this.servletConfig
                .getServletContext()).getProperty("deploy-path");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Administrator getAdministrator() {
        if (null == administratorEntity) {
            throw new ExtendedNotFoundException("administrator " + userId + "does not exist!");
        }
        return administratorEntity;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putAdministrator(Administrator administrator) {
        if (administrator.getUserName() != null) {
            administratorEntity.setUserName(administrator.getUserName());
        }

        if (administrator.getPassword() != null && !administrator.getPassword().equals("")) {
            administratorEntity.setPassword(administrator.getPassword());
        }
        administratorEntity.setAdminRole(administrator.getAdminRole());
        
        administratorEntity.setShopId(administrator.getShopId());

        
        TransactionManager.manage(new Transactional(em) {
            public void transact() {
                em.merge(administratorEntity);
            }
        });
        return Response.status(Status.OK).build();
    }

    @DELETE
    public void deleteAdministrator() {
        if (null == administratorEntity) {
            throw new ExtendedNotFoundException("deleteAdministrator administratorId " + userId + "does not exist!");
        }
        administratorEntity.removeAllImagesAndDeleteOnFS(deployPath);
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.remove(administratorEntity);
            }
        });
    }
    
    @Path("images/")
    public AdministratorImagesResource uploadAdministratorImage() {
        return new AdministratorImagesResource(emf.createEntityManager(), 
                administratorEntity, servletConfig);
    }

    @Override
    public String toString() {
        return administratorEntity.getUserId();
    }

    public Administrator getAdministratorEntity() {
        return administratorEntity;
    }
}
