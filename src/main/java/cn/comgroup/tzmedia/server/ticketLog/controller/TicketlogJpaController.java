/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.ticketLog.controller;

import cn.comgroup.tzmedia.server.ticketLog.Ticketlog;
import cn.comgroup.tzmedia.server.ticketLog.controller.exceptions.NonexistentEntityException;
import cn.comgroup.tzmedia.server.ticketLog.controller.exceptions.PreexistingEntityException;
import cn.comgroup.tzmedia.server.ticketLog.controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

/**
 *
 * @author Administrator
 */
public class TicketlogJpaController implements Serializable {

    public TicketlogJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ticketlog ticketlog) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            em.persist(ticketlog);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findTicketlog(ticketlog.getTicketlogid()) != null) {
                throw new PreexistingEntityException("Ticketlog " + ticketlog + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ticketlog ticketlog) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            ticketlog = em.merge(ticketlog);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = ticketlog.getTicketlogid();
                if (findTicketlog(id) == null) {
                    throw new NonexistentEntityException("The ticketlog with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ticketlog ticketlog;
            try {
                ticketlog = em.getReference(Ticketlog.class, id);
                ticketlog.getTicketlogid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ticketlog with id " + id + " no longer exists.", enfe);
            }
            em.remove(ticketlog);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ticketlog> findTicketlogEntities() {
        return findTicketlogEntities(true, -1, -1);
    }

    public List<Ticketlog> findTicketlogEntities(int maxResults, int firstResult) {
        return findTicketlogEntities(false, maxResults, firstResult);
    }

    private List<Ticketlog> findTicketlogEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ticketlog.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Ticketlog findTicketlog(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ticketlog.class, id);
        } finally {
            em.close();
        }
    }

    public int getTicketlogCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ticketlog> rt = cq.from(Ticketlog.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
