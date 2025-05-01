/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Persistence;

import Model.Habits;
import Persistence.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import javax.persistence.Persistence;

/**
 *
 * @author juand
 */
public class HabitsJpaController implements Serializable {

    public HabitsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public HabitsJpaController(){
        emf = Persistence.createEntityManagerFactory("HabitPu");
    }

    public void create(Habits habits) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(habits);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Habits habits) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            habits = em.merge(habits);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = habits.getId();
                if (findHabits(id) == null) {
                    throw new NonexistentEntityException("The habits with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Habits habits;
            try {
                habits = em.getReference(Habits.class, id);
                habits.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The habits with id " + id + " no longer exists.", enfe);
            }
            em.remove(habits);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Habits> findHabitsEntities() {
        return findHabitsEntities(true, -1, -1);
    }

    public List<Habits> findHabitsEntities(int maxResults, int firstResult) {
        return findHabitsEntities(false, maxResults, firstResult);
    }

    private List<Habits> findHabitsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Habits.class));
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

    public Habits findHabits(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Habits.class, id);
        } finally {
            em.close();
        }
    }

    public int getHabitsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Habits> rt = cq.from(Habits.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
