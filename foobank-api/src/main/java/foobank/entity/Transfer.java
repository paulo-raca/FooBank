package foobank.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Transfer {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private BankAccount source;

    @ManyToOne
    private BankAccount destination;
    
    @Column
    private double amount;

    
    public Transfer() {
    }
    
    public Transfer(BankAccount source, BankAccount dest, double amount) {
        setSource(source);
        setDestination(dest);
        setAmount(amount);
    }
    
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public BankAccount getSource() {
        return source;
    }
    public void setSource(BankAccount source) {
        this.source = source;
    }
    public BankAccount getDestination() {
        return destination;
    }
    public void setDestination(BankAccount destination) {
        this.destination = destination;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    

    @Override
    public String toString() {
        return "Transfer #" + id + " ($" + amount + " from " + source + " to  " + destination + ")";
    }
}
