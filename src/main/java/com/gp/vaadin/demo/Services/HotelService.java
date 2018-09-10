package com.gp.vaadin.demo.Services;

import com.gp.vaadin.demo.Entity.Hotel;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.List;

public class HotelService {

    private static HotelService instance;

    private EntityManager em = Persistence.createEntityManagerFactory("demo_hotels").createEntityManager();

    public static HotelService getInstance() {
        if (instance == null) {
            instance = new HotelService();
        }
        return instance;
    }

    private HotelService() {
    }

    public void save(Hotel hotel) {
        em.getTransaction().begin();
        if (hotel.getId() == null) {
            em.persist(hotel);
        } else {
            em.merge(hotel);
        }
        try {
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateHotels(Iterable<Hotel> iterable) {
        em.getTransaction().begin();
        iterable.forEach(hotel -> em.merge(hotel));
        try {
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Hotel> getAll(@NotNull String nameFilter, @NotNull String addressFilter) {
        TypedQuery<Hotel> query = em.createNamedQuery("Hotel.filter", Hotel.class);
        query.setParameter("filterByName", "%" + nameFilter.toLowerCase() + "%");
        query.setParameter("filterByAddress", "%" + addressFilter.toLowerCase() + "%");
        return query.getResultList();
    }

    public Hotel get(long id) {
        return em.find(Hotel.class, id);
    }

    public void delete(Iterable<Hotel> hotels) {
        em.getTransaction().begin();
        hotels.forEach(hotel -> em.remove(get(hotel.getId())));
        try {
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Hotel> getAll() {
        TypedQuery<Hotel> query = em.createNamedQuery("Hotel.getAll", Hotel.class);
        return query.getResultList();
    }
}