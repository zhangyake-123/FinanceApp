package model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

// A class representing a ledger that stores multiple transactions
public class Ledger {
    private List<Transaction> transactions;

    // EFFECTS: constructs an empty ledger
    public Ledger() {
        this.transactions = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: adds the given transaction to the ledger
    //          returns true if added, false if a transaction
    //          with the same id already exists
    public boolean add(Transaction t) {
        return false;
    }

    // MODIFIES: this
    // EFFECTS: removes the transaction with the given id
    //          returns true if removed, false if not found
    public boolean removeById(String id) {
        return false;
    }

    // EFFECTS: returns an unmodifiable list of all transactions
    public List<Transaction> getAll() {
        return Collections.emptyList();
    }

    // EFFECTS: returns total income in cents
    public int totalIncome() {
        return 0;
    }

    // EFFECTS: returns total expense in cents
    public int totalExpense() {
        return 0;
    }

    // EFFECTS: returns the balance (income - expense)
    public int balance() {
        return 0;
    }

    // EFFECTS: returns all transactions of the given category
    public List<Transaction> byCategory(Transaction.Category c) {
        return Collections.emptyList();
    }

    // EFFECTS: returns all transactions within the given month
    public List<Transaction> byMonth(YearMonth ym) {
        return Collections.emptyList();
    }

    // EFFECTS: returns all transactions within the given year
    public List<Transaction> byYear(int year) {
        return Collections.emptyList();
    }

    // EFFECTS: returns a map of total expenses by category (EXPENSE only)
    public Map<Transaction.Category, Integer> expenseTotalsByCategory() {
        return Collections.emptyMap();
    }

    // EFFECTS: returns total income between start and end dates (inclusive)
    public int incomeBetween(LocalDate start, LocalDate end) {
        return 0;
    }

    // EFFECTS: returns total expense between start and end dates (inclusive)
    public int expenseBetween(LocalDate start, LocalDate end) {
        return 0;
    }
}