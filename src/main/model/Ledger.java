package model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
        for (Transaction existing : transactions) {
            if (existing.getId().equals(t.getId())) {
                return false; // duplicate id
            }
        }
        transactions.add(t);
        return true;
    }

    // MODIFIES: this
    // EFFECTS: removes the transaction with the given id
    //          returns true if removed, false if not found
    public boolean removeById(String id) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId().equals(id)) {
                transactions.remove(i);
                return true;
            }
        }
        return false;
    }

    // EFFECTS: returns an unmodifiable list of all transactions
    public List<Transaction> getAll() {
        return Collections.unmodifiableList(transactions);
    }

    // EFFECTS: returns total income in cents
    public int totalIncome() {
        int sum = 0;
        for (Transaction t : transactions) {
            if (t.isIncome()) {
                sum += t.getAmountInCents();
            }
        }
        return sum;
    }

    // EFFECTS: returns total expense in cents
    public int totalExpense() {
        int sum = 0;
        for (Transaction t : transactions) {
            if (t.isExpense()) {
                sum += t.getAmountInCents();
            }
        }
        return sum;
    }

    // EFFECTS: returns the balance (income - expense)
    public int balance() {
        return totalIncome() - totalExpense();
    }

    // EFFECTS: returns all transactions of the given category
    public List<Transaction> byCategory(Transaction.Category c) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getCategory() == c) {
                result.add(t);
            }
        }
        return result;
    }

    // EFFECTS: returns all transactions within the given month
    public List<Transaction> byMonth(YearMonth ym) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            LocalDate d = t.getDate();
            if (d.getYear() == ym.getYear() && d.getMonth() == ym.getMonth()) {
                result.add(t);
            }
        }
        return result;
    }

    // EFFECTS: returns all transactions within the given year
    public List<Transaction> byYear(int year) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getDate().getYear() == year) {
                result.add(t);
            }
        }
        return result;
    }

    // EFFECTS: returns a map of total expenses by category (EXPENSE only)
    public Map<Transaction.Category, Integer> expenseTotalsByCategory() {
        Map<Transaction.Category, Integer> totals = new HashMap<>();
        for (Transaction t : transactions) {
            if (t.isExpense()) {
                Transaction.Category cat = t.getCategory();
                int current = totals.getOrDefault(cat, 0);
                totals.put(cat, current + t.getAmountInCents());
            }
        }
        return Collections.unmodifiableMap(totals);
    }

    // EFFECTS: returns total income between start and end dates (inclusive)
    public int incomeBetween(LocalDate start, LocalDate end) {
        int sum = 0;
        for (Transaction t : transactions) {
            LocalDate d = t.getDate();
            if (t.isIncome() && !d.isBefore(start) && !d.isAfter(end)) {
                sum += t.getAmountInCents();
            }
        }
        return sum;
    }

    // EFFECTS: returns total expense between start and end dates (inclusive)
    public int expenseBetween(LocalDate start, LocalDate end) {
        int sum = 0;
        for (Transaction t : transactions) {
            LocalDate d = t.getDate();
            if (t.isExpense() && !d.isBefore(start) && !d.isAfter(end)) {
                sum += t.getAmountInCents();
            }
        }
        return sum;
    }
}