package ui;

import model.Ledger;
import model.Transaction;
import model.Transaction.Category;
import model.Transaction.TxnType;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

// A simple console-based UI for the Personal Finance Tracker
public class FinanceApp {
    private Scanner in;         // input reader
    private Ledger ledger;      // model

    // EFFECTS: constructs the console app with a fresh ledger and input scanner
    public FinanceApp() {
        this.in = new Scanner(System.in);
        this.ledger = new Ledger();
    }

    // MODIFIES: this
    // EFFECTS: runs the main menu loop until user quits
    public void run() {
        printWelcome();
        boolean running = true;
        while (running) {
            printMenu();
            String cmd = in.nextLine().trim();
            if (cmd.equalsIgnoreCase("q")) {
                running = false;
            } else if (cmd.equals("1")) {
                doAdd();
            } else if (cmd.equals("2")) {
                doListAll();
            } else if (cmd.equals("3")) {
                doSummary();
            } else if (cmd.equals("4")) {
                doByCategory();
            } else if (cmd.equals("5")) {
                doByMonth();
            } else if (cmd.equals("6")) {
                doByYear();
            } else if (cmd.equals("7")) {
                doDelete();
            } else if (cmd.equals("8")) {
                doRangeSummary();
            } else if (cmd.equals("9")) {
                doExpenseTotalsByCategory();
            } else {
                System.out.println("Unknown option.");
            }
        }
        System.out.println("Goodbye!");
    }

    // EFFECTS: prints welcome banner
    private void printWelcome() {
        System.out.println("Welcome to the Personal Finance Tracker!");
    }

    // EFFECTS: prints main menu
    private void printMenu() {
        System.out.println();
        System.out.println("Select from:");
        System.out.println("1 -> Add transaction");
        System.out.println("2 -> View all transactions");
        System.out.println("3 -> Show summary (income / expense / balance)");
        System.out.println("4 -> View by category");
        System.out.println("5 -> View by month (YYYY-MM)");
        System.out.println("6 -> View by year (YYYY)");
        System.out.println("7 -> Delete transaction by id");
        System.out.println("8 -> Range summary (start YYYY-MM-DD, end YYYY-MM-DD)");
        System.out.println("9 -> Expense totals by category");
        System.out.println("q -> Quit");
        System.out.print("> ");
    }

    // MODIFIES: this
    // EFFECTS: prompts user and adds a transaction into the ledger
    private void doAdd() {
        try {
            System.out.print("Type (I=income, E=expense): ");
            String t = in.nextLine().trim().toUpperCase();
            TxnType type = t.startsWith("I") ? TxnType.INCOME : TxnType.EXPENSE;

            System.out.print("Amount (e.g., 12.99): ");
            String amtStr = in.nextLine().trim();
            int cents = parseDollarsToCents(amtStr);

            System.out.print("Date (YYYY-MM-DD) (e.g., 2006-01-11): ");
            LocalDate date = LocalDate.parse(in.nextLine().trim());

            System.out.print("Category (FOOD, SHOPPING, TRANSPORT, RENT, UTILITIES, ENTERTAINMENT, EDUCATION, SALARY, OTHER): ");
            Category category = Category.valueOf(in.nextLine().trim().toUpperCase());

            System.out.print("Note (optional): ");
            String note = in.nextLine();

            String id = UUID.randomUUID().toString();
            Transaction tx = new Transaction(id, cents, date, category, type, note);
            boolean added = ledger.add(tx);
            if (added) {
                System.out.println("Added with id: " + id);
            } else {
                System.out.println("A transaction with the same id already exists. Try again.");
            }
        } catch (IllegalArgumentException | DateTimeParseException e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    // EFFECTS: prints all transactions
    private void doListAll() {
        if (ledger.getAll().isEmpty()) {
            System.out.println("(no transactions yet)");
            return;
        }
        for (Transaction t : ledger.getAll()) {
            System.out.println(t.toString());
        }
    }

    // EFFECTS: prints income, expense, balance summary
    private void doSummary() {
        int income = ledger.totalIncome();
        int expense = ledger.totalExpense();
        int balance = ledger.balance();
        System.out.println("Income : $" + centsToDollars(income));
        System.out.println("Expense: $" + centsToDollars(expense));
        System.out.println("Balance: $" + centsToDollars(balance));
    }

    // EFFECTS: prompts a category and prints transactions of that category
    private void doByCategory() {
        try {
            System.out.print("Category: ");
            Category c = Category.valueOf(in.nextLine().trim().toUpperCase());
            if (ledger.byCategory(c).isEmpty()) {
                System.out.println("(no transactions for " + c + ")");
                return;
            }
            for (Transaction t : ledger.byCategory(c)) {
                System.out.println(t.toString());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid category.");
        }
    }

    // EFFECTS: prompts a YearMonth and prints transactions
    private void doByMonth() {
        try {
            System.out.print("Month (YYYY-MM): ");
            YearMonth ym = YearMonth.parse(in.nextLine().trim());
            if (ledger.byMonth(ym).isEmpty()) {
                System.out.println("(no transactions in " + ym + ")");
                return;
            }
            for (Transaction t : ledger.byMonth(ym)) {
                System.out.println(t.toString());
            }
        } catch (Exception e) {
            System.out.println("Invalid month.");
        }
    }

    // EFFECTS: prompts a year and prints transactions
    private void doByYear() {
        try {
            System.out.print("Year (YYYY): ");
            int year = Integer.parseInt(in.nextLine().trim());
            if (ledger.byYear(year).isEmpty()) {
                System.out.println("(no transactions in " + year + ")");
                return;
            }
            for (Transaction t : ledger.byYear(year)) {
                System.out.println(t.toString());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid year.");
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes a transaction by id, if present
    private void doDelete() {
        System.out.print("Enter id to delete: ");
        String id = in.nextLine().trim();
        boolean removed = ledger.removeById(id);
        if (removed) {
            System.out.println("Removed.");
        } else {
            System.out.println("Not found.");
        }
    }

    // EFFECTS: prompts for start/end dates and prints in-range income/expense totals
    private void doRangeSummary() {
        try {
            System.out.print("Start date (YYYY-MM-DD): ");
            LocalDate start = LocalDate.parse(in.nextLine().trim());
            System.out.print("End date (YYYY-MM-DD): ");
            LocalDate end = LocalDate.parse(in.nextLine().trim());
            int income = ledger.incomeBetween(start, end);
            int expense = ledger.expenseBetween(start, end);
            System.out.println("Income  in range: $" + centsToDollars(income));
            System.out.println("Expense in range: $" + centsToDollars(expense));
            System.out.println("Balance in range: $" + centsToDollars(income - expense));
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date(s).");
        }
    }

    // EFFECTS: prints a table of expense totals by category
    private void doExpenseTotalsByCategory() {
        Map<Category, Integer> totals = ledger.expenseTotalsByCategory();
        if (totals.isEmpty()) {
            System.out.println("(no expense totals yet)");
            return;
        }
        System.out.println("Expense totals by category:");
        for (Map.Entry<Category, Integer> e : totals.entrySet()) {
            System.out.println(" - " + e.getKey() + ": $" + centsToDollars(e.getValue()));
        }
    }

    // EFFECTS: parses a dollars string (e.g., "12.99", "-3", "5.5") into cents (int)
    private int parseDollarsToCents(String amt) {
        String s = amt.trim();
        boolean negative = s.startsWith("-");
        if (negative) s = s.substring(1);

        String[] parts = s.split("\\.");
        int dollars = Integer.parseInt(parts[0].isEmpty() ? "0" : parts[0]);
        int cents = 0;
        if (parts.length > 1) {
            String frac = (parts[1] + "00").substring(0, 2); // pad/truncate to 2 digits
            cents = Integer.parseInt(frac);
        }
        int total = dollars * 100 + cents;
        return negative ? -total : total; // negative allowed if you ever want it
    }

    // EFFECTS: converts cents to a printable dollars string (e.g., 1299 -> "12.99")
    private String centsToDollars(int cents) {
        int abs = Math.abs(cents);
        String s = (abs / 100) + "." + String.format("%02d", abs % 100);
        return cents < 0 ? "-" + s : s;
    }
}