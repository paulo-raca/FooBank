package foobank.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Persistence;

@Entity
public class Customer {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Customer #" + id + " (name: " + name + ")";
    }

    public static void main(String[] args) {
        Customer c = new Customer();
        c.setName("Paulo Costa");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("foobank");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(c);
        em.getTransaction().commit();
        System.out.println(em.find(Customer.class, c.getId()));
        System.out.println(em.createQuery("from Customer", Customer.class).getResultList());
        //System.out.println(em.createNativeQuery("select 1").getResultList());
        //em.find(Customer.class, arg1)
        em.close();
        emf.close();
    }
}
