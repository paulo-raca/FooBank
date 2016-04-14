package foobank.service;

public class TransferInfo {
    private Long id;
    private Long sourceAccountId;
    private Long destinationAccountId;
    private double amount;

    
    public TransferInfo() {
    }
    
    public TransferInfo(Long sourceAccountId, Long destAccountId, double amount) {
        setSourceAccountId(sourceAccountId);
        setDestinationAccountId(destAccountId);
        setAmount(amount);
    }
    
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getSourceAccountId() {
        return sourceAccountId;
    }
    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }
    public Long getDestinationAccountId() {
        return destinationAccountId;
    }
    public void setDestinationAccountId(Long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    

    @Override
    public String toString() {
        return "Transfer #" + id + " ($" + amount + " from #" + sourceAccountId + " to  #" + destinationAccountId + ")";
    }
}
