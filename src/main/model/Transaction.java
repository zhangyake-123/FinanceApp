package model;

import java.time.LocalDate;

// A class representing a single financial transaction (income or expense)
public class Transaction {
    private String id;
    private int amountInCents;
    private LocalDate date;
    private Category category;
    private TxnType type;
    private String note;

    // EFFECTS: constructs a Transaction with given values
    public Transaction(String id, int amountInCents, LocalDate date,
                       Category category, TxnType type, String note) {
        this.id = id;
        this.amountInCents = amountInCents;
        this.date = date;
        this.category = category;
        this.type = type;
        this.note = (note == null ? "" : note);
    }

    // EFFECTS: returns true if this transaction is of type INCOME
    public boolean isIncome() {
        return this.type == TxnType.INCOME;
    }

    // EFFECTS: returns true if this transaction is of type EXPENSE
    public boolean isExpense() {
        return this.type == TxnType.EXPENSE;
    }

    // EFFECTS: returns the transaction's unique id
    public String getId() {
        return this.id;
    }

    // EFFECTS: returns the transaction's amount in cents
    public int getAmountInCents() {
        return this.amountInCents;
    }

    // EFFECTS: returns the transaction's date
    public LocalDate getDate() {
        return this.date;
    }

    // EFFECTS: returns the transaction's category
    public Category getCategory() {
        return this.category;
    }

    // EFFECTS: returns the transaction's type (INCOME or EXPENSE)
    public TxnType getType() {
        return this.type;
    }

    // EFFECTS: returns the transaction's note
    public String getNote() {
        return this.note;
    }

    // MODIFIES: this
    // EFFECTS: sets the note to the new given value
    public void setNote(String newNote) {
        this.note = (newNote == null ? "" : newNote);
    }

    // EFFECTS: returns a short text summary of this transaction
    public String toString() {
        return  "(" + id + ")" 
                + "[" + this.date + "] "
                + this.category + " "
                + this.type + " "
                + (this.amountInCents / 100) + "."
                + String.format("%02d", this.amountInCents % 100)
                + (this.note.isEmpty() ? "" : " (" + this.note + ")");
    }

    public enum TxnType { INCOME, EXPENSE }

    public enum Category {
        FOOD, SHOPPING, TRANSPORT, RENT, UTILITIES,
        ENTERTAINMENT, EDUCATION, SALARY, OTHER
    }
}