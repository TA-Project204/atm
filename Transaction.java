package atm;

import java.time.LocalDateTime;

public class Transaction {
    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL
    }

    private double amount;
    private LocalDateTime date;
    private TransactionType type;

    public Transaction(double amount, TransactionType type) {
        this.amount = amount;
        this.date = LocalDateTime.now();
        this.type = type;
    }

    // Getters and setters for amount, date, and type

    @Override
    public String toString() {
        String transactionTypeStr = type == TransactionType.DEPOSIT ? "Deposit" : "Withdrawal";
        return "Transaction{" +
                "amount=" + amount +
                ", date=" + date +
                ", type=" + transactionTypeStr +
                '}';
    }
}
