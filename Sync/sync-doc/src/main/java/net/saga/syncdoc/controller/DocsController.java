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
import net.saga.syncdoc.vo.Doc;
import org.keycloak.KeycloakPrincipal;

@Path("/docs")
@RolesAllowed("user")
@Consumes("application/json")
@Produces("application/json")
public class DocsController {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @GET
    public List<Doc> getDocs(@Context SecurityContext context) {
        String userId = ((KeycloakPrincipal)context.getUserPrincipal()).getKeycloakSecurityContext().getToken().getPreferredUsername();
        return em.createQuery("from Doc where userId = :userId", Doc.class).setParameter("userId", userId).getResultList();
    }

    @PUT
    @Path("{docId}")
    public Doc saveDoc(Doc doc, @PathParam("docId") Long docId, @Context SecurityContext context) {
        
        return saveDoc(doc, context);
    }
    
    @POST
    public Doc saveDoc(Doc doc, @Context SecurityContext context) {
        String userId = ((KeycloakPrincipal)context.getUserPrincipal()).getKeycloakSecurityContext().getToken().getPreferredUsername();
        
        //The next two ifs are a by product of how sharing works
        //If you want to share a document you submit a new doc with no id
        //but with a userId and isShared set.
        
        //If userId isn't set then you will own it.
        
        if (doc.getUserId() == null || doc.getUserId().isEmpty()) {
            doc.setUserId(userId);
        }
        
        
        if (doc.isShared() && doc.getId() != null) {
            throw new IllegalArgumentException("Cannot edit shared documents");
        }
        try {
            //Setup Transaction
            utx.begin();
            em.joinTransaction();

            
            if (doc.getId() == null) {
                //Create doc if it doesn't exist
                em.persist(doc);
            } else {
                //Otherwise just merge the changes
                doc = em.merge(doc);
            }

            utx.commit();

            return doc;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(DocsController.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(DocsController.class.getName()).log(Level.SEVERE, null, ex1);
                throw new IllegalStateException(ex1);
            }
            throw new IllegalStateException(ex);

        } 

    }
    
    @DELETE
    @Path("/{docId}")
    public Doc deleteDoc(@PathParam("docId") Long docId, @Context SecurityContext context) {
        
        try {
            utx.begin();
            Doc doc = em.find(Doc.class, docId);
            em.remove(doc);
            utx.commit();
            return doc;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(DocsController.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(DocsController.class.getName()).log(Level.SEVERE, null, ex1);
                throw new IllegalStateException(ex1);
            }
            throw new IllegalStateException(ex);
        }
        
         
    }

}
