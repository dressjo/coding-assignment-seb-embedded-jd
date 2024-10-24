package se.seb.embedded.coding_assignment.payments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import se.seb.embedded.coding_assignment.payments.TransactionService.Transaction;

public class TransactionAggregation {

    private BigDecimal average = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal max = BigDecimal.ZERO;
    private Date lastTransactionDate;
    private List<Transaction> transactionList = new ArrayList<Transaction>();

    public BigDecimal getAverage() {
        return average;
    }

    public void setAverage(BigDecimal average) {
        this.average = average;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(
        List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }
}