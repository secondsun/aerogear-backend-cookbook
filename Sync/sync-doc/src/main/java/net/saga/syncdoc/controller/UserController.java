package net.saga.syncdoc.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import net.saga.syncdoc.vo.DocUser;
import org.keycloak.KeycloakPrincipal;

@Path("/user")
@RolesAllowed("user")
@Consumes("application/json")
@Produces("application/json")
public class UserController {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @GET
    public List<DocUser> getUser(@Context SecurityContext context) {
                String userId = ((KeycloakPrincipal)context.getUserPrincipal()).getKeycloakSecurityContext().getToken().getPreferredUsername();

        return em.createQuery("from DocUser where userId = :userId")
                .setParameter("userId", userId)
                .getResultList();
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    @Path("{userId}")
    public DocUser updateUser(DocUser user, @PathParam("userId") Long userId, @Context SecurityContext context) {
        return saveUser(user, context);
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public DocUser saveUser(DocUser user, @Context SecurityContext context) {
        try {
            utx.begin();
            em.joinTransaction();
            user.setUserId(((KeycloakPrincipal)context.getUserPrincipal()).getKeycloakSecurityContext().getToken().getPreferredUsername());
            if (user.getId() == null) {
                em.persist(user);
            } else {
                user = em.merge(user);
            }
            utx.commit();
            return user;
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex1);
            }
            throw new IllegalStateException(ex);
        }
    }

    @DELETE
    @Path("{userId}")
    public void deleteOp() {
        throw new IllegalStateException("Deleting users is not supported");
    }

}
