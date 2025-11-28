package ui;

import model.Ledger;
import model.Transaction;
import model.Transaction.Category;
import model.Transaction.TxnType;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Swing GUI for Personal Finance Tracker.
public class FinanceAppGUI extends JFrame {

    private static final String JSON_STORE = "./data/ledger.json";

    private Ledger ledger;

    private TransactionTableModel tableModel;
    private JTable transactionTable;

    // MODIFIES: this
    // EFFECTS: constructs a new ledger and initializes the main GUI window
    // with a top button panel and a transaction table panel
    public FinanceAppGUI() {
        ledger = new Ledger();

        setTitle("Personal Finance Tracker");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(buildButtonPanel(), BorderLayout.NORTH);
        getContentPane().add(buildTablePanel(), BorderLayout.CENTER);

        pack();
    }

    // REQUIRES: none
    // MODIFIES: none
    // EFFECTS: returns a panel containing all action buttons laid out horizontally
    private JComponent buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Actions"));
        addPrimaryButtons(panel);
        addPersistenceButtons(panel);
        addQuitButton(panel);
        return panel;
    }

    // REQUIRES: panel not null
    // MODIFIES: panel
    // EFFECTS: adds main action buttons to the panel
    private void addPrimaryButtons(JPanel panel) {
        panel.add(makeButton("Add transaction", () -> handleAddTransaction()));
        panel.add(makeButton("View all", () -> handleViewAll()));
        panel.add(makeButton("Show summary", () -> handleShowSummary()));
        panel.add(makeButton("View by category", () -> handleViewByCategory()));
        panel.add(makeButton("View by month", () -> handleViewByMonth()));
        panel.add(makeButton("View by year", () -> handleViewByYear()));
        panel.add(makeButton("Delete by id", () -> handleDeleteById()));
        panel.add(makeButton("View by range", () -> handleViewByRange()));
        panel.add(makeButton("Expense totals", () -> handleExpenseTotalsByCategory()));
        panel.add(makeButton("Show charts", () -> handleShowCharts()));
    }

    // REQUIRES: panel not null
    // MODIFIES: panel
    // EFFECTS: adds save/load buttons to the panel
    private void addPersistenceButtons(JPanel panel) {
        panel.add(makeButton("Save", () -> doSave()));
        panel.add(makeButton("Load", () -> doLoad()));
    }

    // REQUIRES: panel not null
    // MODIFIES: panel
    // EFFECTS: adds quit button to the panel
    private void addQuitButton(JPanel panel) {
        panel.add(makeButton("Quit", this::exitApplication));
    }

    // REQUIRES: action not null
    // MODIFIES: none
    // EFFECTS: creates a JButton with given text and action
    private JButton makeButton(String text, Runnable action) {
        JButton b = new JButton(text);
        b.addActionListener(e -> action.run());
        return b;
    }

    // REQUIRES: none
    // MODIFIES: this
    // EFFECTS: creates and returns a scrollable table panel that displays
    // transactions
    // and initializes tableModel and transactionTable fields
    private JComponent buildTablePanel() {
        tableModel = new TransactionTableModel();
        transactionTable = new JTable(tableModel);
        transactionTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Transactions"));

        tableModel.setData(ledger.getAll());

        return scrollPane;
    }

    // REQUIRES: none
    // MODIFIES: this
    // EFFECTS: if ledger has no transactions, shows an info dialog;
    // otherwise opens a non-modal dialog containing two pie charts
    // (category share and income vs expense) based on current ledger
    private void handleShowCharts() {
        if (ledger.getAll().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No transactions to chart.",
                    "Charts",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Category & I/E Charts", false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        dialog.add(new PieChartsPanel(ledger));
        dialog.setVisible(true);
    }

    // REQUIRES: none
    // MODIFIES: this, ledger, tableModel
    // EFFECTS: orchestrates prompting for transaction fields and adds transaction
    private void handleAddTransaction() {
        TxnType type = promptTxnType();
        if (type == null) {
            return;
        }

        Integer cents = promptAmountInCents();
        if (cents == null) {
            return;
        }

        LocalDate date = promptDate();
        if (date == null) {
            return;
        }

        Category category = promptCategory();
        if (category == null) {
            return;
        }

        String note = promptNote();
        if (note == null) {
            note = "";
        }

        addTransactionToLedger(type, cents, date, category, note);
    }

    // REQUIRES: all parameters non-null
    // MODIFIES: this, ledger, tableModel
    // EFFECTS: creates a transaction and adds it to ledger; updates table
    // and shows success or failure dialog
    private void addTransactionToLedger(TxnType type, int cents, LocalDate date,
            Category category, String note) {
        String id = UUID.randomUUID().toString();
        Transaction tx = new Transaction(id, cents, date, category, type, note);
        boolean added = ledger.add(tx);

        if (added) {
            tableModel.setData(ledger.getAll());
            JOptionPane.showMessageDialog(this,
                    "Transaction added successfully.\nID: " + id,
                    "Add Transaction",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "A transaction with the same id already exists. (Very unlikely with UUID)\nPlease try again.",
                    "Add Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // REQUIRES: none
    // MODIFIES: tableModel
    // EFFECTS: updates table to display all transactions in the ledger
    private void handleViewAll() {
        tableModel.setData(ledger.getAll());
    }

    // REQUIRES: none
    // MODIFIES: none
    // EFFECTS: computes income, expense, and balance from ledger
    // and shows them in an information dialog
    private void handleShowSummary() {
        int income = ledger.totalIncome();
        int expense = ledger.totalExpense();
        int balance = ledger.balance();

        String msg = "Income : $" + centsToDollars(income)
                + "\nExpense: $" + centsToDollars(expense)
                + "\nBalance: $" + centsToDollars(balance);

        JOptionPane.showMessageDialog(this,
                msg,
                "Summary",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // REQUIRES: none
    // MODIFIES: tableModel
    // EFFECTS: prompts user for a category; if valid, updates table to show
    // only transactions in that category; if invalid, shows an error dialog
    private void handleViewByCategory() {
        String input = JOptionPane.showInputDialog(
                this,
                "Enter category (FOOD, SHOPPING, TRANSPORT, RENT, UTILITIES,\n"
                        + "ENTERTAINMENT, EDUCATION, SALARY, OTHER):",
                "View by Category",
                JOptionPane.QUESTION_MESSAGE);
        if (input == null) {
            return;
        }

        try {
            Category c = Category.valueOf(input.trim().toUpperCase());
            List<Transaction> list = ledger.byCategory(c);
            tableModel.setData(list);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid category.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // REQUIRES: none
    // MODIFIES: tableModel
    // EFFECTS: prompts user for a month (YYYY-MM); if valid, updates table to show
    // only transactions in that month; if invalid, shows an error dialog
    private void handleViewByMonth() {
        String input = JOptionPane.showInputDialog(
                this,
                "Month (YYYY-MM):",
                "View by Month",
                JOptionPane.QUESTION_MESSAGE);
        if (input == null) {
            return;
        }

        try {
            YearMonth ym = YearMonth.parse(input.trim());
            List<Transaction> list = ledger.byMonth(ym);
            tableModel.setData(list);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid month format. Use YYYY-MM.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // REQUIRES: none
    // MODIFIES: tableModel
    // EFFECTS: prompts user for a year; if valid, updates table to show
    // only transactions in that year; if invalid, shows an error dialog
    private void handleViewByYear() {
        String input = JOptionPane.showInputDialog(
                this,
                "Year (YYYY):",
                "View by Year",
                JOptionPane.QUESTION_MESSAGE);
        if (input == null) {
            return;
        }

        try {
            int year = Integer.parseInt(input.trim());
            List<Transaction> list = ledger.byYear(year);
            tableModel.setData(list);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid year. Use a 4-digit number (e.g., 2025).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // REQUIRES: none
    // MODIFIES: ledger, tableModel
    // EFFECTS: prompts user for an id, attempts to remove the corresponding
    // transaction from ledger; if successful, updates table and shows
    // a success message; otherwise shows a warning
    private void handleDeleteById() {
        String id = JOptionPane.showInputDialog(
                this,
                "Enter id to delete:",
                "Delete Transaction",
                JOptionPane.QUESTION_MESSAGE);
        if (id == null) {
            return;
        }

        boolean removed = ledger.removeById(id.trim());
        if (removed) {
            tableModel.setData(ledger.getAll());
            JOptionPane.showMessageDialog(this,
                    "Deleted transaction with id: " + id,
                    "Delete",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No transaction found with id: " + id,
                    "Delete",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // REQUIRES: none
    // MODIFIES: tableModel
    // EFFECTS: prompts user for a date range; if valid, shows transactions in
    // range;
    // otherwise shows error dialog
    private void handleViewByRange() {
        LocalDate[] range = promptDateRange();
        if (range == null) {
            return;
        }
        applyRangeFilter(range[0], range[1]);
    }

    // REQUIRES: none
    // MODIFIES: none
    // EFFECTS: prompts user for start and end dates; if both are valid,
    // returns an array {start, end}; if format invalid, shows an
    // error dialog and returns null; if cancelled at any step,
    // returns null without showing an error
    private LocalDate[] promptDateRange() {
        String startStr = promptDateInput(
                "Start date (YYYY-MM-DD):",
                "View by Range");
        if (startStr == null) {
            return null;
        }

        String endStr = promptDateInput(
                "End date (YYYY-MM-DD):",
                "View by Range");
        if (endStr == null) {
            return null;
        }

        try {
            LocalDate start = LocalDate.parse(startStr.trim());
            LocalDate end = LocalDate.parse(endStr.trim());
            return new LocalDate[] { start, end };
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Use YYYY-MM-DD.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // REQUIRES: none
    // MODIFIES: none
    // EFFECTS: shows an input dialog with given message and title,
    // returns user input string, or null if cancelled
    private String promptDateInput(String message, String title) {
        return JOptionPane.showInputDialog(
                this,
                message,
                title,
                JOptionPane.QUESTION_MESSAGE);
    }

    // REQUIRES: start and end not null
    // MODIFIES: tableModel
    // EFFECTS: filters ledger transactions between start and end (inclusive)
    // and updates the table
    private void applyRangeFilter(LocalDate start, LocalDate end) {
        List<Transaction> selected = new ArrayList<>();
        for (Transaction t : ledger.getAll()) {
            LocalDate d = t.getDate();
            if (!d.isBefore(start) && !d.isAfter(end)) {
                selected.add(t);
            }
        }
        tableModel.setData(selected);
    }

    // REQUIRES: none
    // MODIFIES: none
    // EFFECTS: if there are no expenses, shows an info dialog; otherwise
    // constructs a textual summary of expense totals by category
    // and shows it in a dialog
    private void handleExpenseTotalsByCategory() {
        Map<Category, Integer> totals = ledger.expenseTotalsByCategory();
        if (totals.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No expense totals yet.",
                    "Expense Totals",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Expense totals by category:\n");
        for (Map.Entry<Category, Integer> e : totals.entrySet()) {
            sb.append(" - ").append(e.getKey()).append(": $")
                    .append(centsToDollars(e.getValue())).append("\n");
        }

        JOptionPane.showMessageDialog(this,
                sb.toString(),
                "Expense Totals",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // REQUIRES: none
    // MODIFIES: filesystem
    // EFFECTS: writes current ledger to JSON_STORE; if successful,
    // shows success dialog; if file cannot be opened, shows error dialog
    private void doSave() {
        try {
            JsonWriter writer = new JsonWriter(JSON_STORE);
            writer.open();
            writer.write(ledger);
            writer.close();
            JOptionPane.showMessageDialog(this,
                    "Saved ledger to " + JSON_STORE,
                    "Save",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to write to file: " + JSON_STORE,
                    "Save Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // REQUIRES: none
    // MODIFIES: this, ledger, tableModel
    // EFFECTS: attempts to read a ledger from JSON_STORE; if successful,
    // replaces current ledger with loaded one, updates table, and
    // shows success dialog; otherwise shows error dialog
    private void doLoad() {
        try {
            JsonReader reader = new JsonReader(JSON_STORE);
            ledger = reader.read();
            tableModel.setData(ledger.getAll());
            JOptionPane.showMessageDialog(this,
                    "Loaded ledger from " + JSON_STORE,
                    "Load",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to read from file: " + JSON_STORE,
                    "Load Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // REQUIRES: none
    // MODIFIES: none
    // EFFECTS: repeatedly prompts user for transaction type (I/E) until
    // a valid value is entered, then returns the corresponding TxnType;
    // returns null if user cancels the dialog
    private TxnType promptTxnType() {
        while (true) {
            String input = JOptionPane.showInputDialog(
                    this,
                    "Enter type (I = income, E = expense):",
                    "Add Transaction - Step 1/5",
                    JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                return null;
            }
            String s = input.trim().toUpperCase();
            if (s.equals("I")) {
                return TxnType.INCOME;
            } else if (s.equals("E")) {
                return TxnType.EXPENSE;
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid type. Please enter I or E.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // REQUIRES: none
    // MODIFIES: none
    // EFFECTS: repeatedly prompts user for amount in dollars (e.g., "12.99"),
    // parses it to cents and returns the value; returns null if user cancels
    private Integer promptAmountInCents() {
        while (true) {
            String input = JOptionPane.showInputDialog(
                    this,
                    "Enter amount (e.g., 12.99):",
                    "Add Transaction - Step 2/5",
                    JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                return null;
            }
            try {
                int cents = parseDollarsToCents(input.trim());
                return cents;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid amount. Please enter a number like 12.99.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // REQUIRES: none
    // MODIFIES: none
    // EFFECTS: repeatedly prompts user for a date (YYYY-MM-DD) until a valid
    // LocalDate is parsed; returns that date, or null if user cancels
    private LocalDate promptDate() {
        while (true) {
            String input = JOptionPane.showInputDialog(
                    this,
                    "Enter date (YYYY-MM-DD):",
                    "Add Transaction - Step 3/5",
                    JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                return null;
            }
            try {
                return LocalDate.parse(input.trim());
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date. Use format YYYY-MM-DD.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // REQUIRES: none
    // MODIFIES: none
    // EFFECTS: repeatedly prompts user for a category name until a valid
    // Transaction.Category value is entered; returns that category,
    // or null if user cancels
    private Category promptCategory() {
        while (true) {
            String input = JOptionPane.showInputDialog(
                    this,
                    "Enter category (FOOD, SHOPPING, TRANSPORT, RENT, UTILITIES,\n"
                            + "ENTERTAINMENT, EDUCATION, SALARY, OTHER):",
                    "Add Transaction - Step 4/5",
                    JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                return null;
            }
            try {
                return Category.valueOf(input.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid category.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // REQUIRES: none
    // MODIFIES: none
    // EFFECTS: prompts user for an optional note; returns the entered string,
    // or null if user cancels
    private String promptNote() {
        return JOptionPane.showInputDialog(
                this,
                "Enter note (optional):",
                "Add Transaction - Step 5/5",
                JOptionPane.QUESTION_MESSAGE);
    }

    // REQUIRES: amt is a non-empty string representing a decimal number
    // optionally starting with '-'
    // MODIFIES: none
    // EFFECTS: parses amt as dollars.cents and returns the corresponding
    // value in cents as an int; throws NumberFormatException if amt
    // cannot be parsed as a number
    private int parseDollarsToCents(String amt) {
        String s = amt.trim();
        boolean negative = s.startsWith("-");
        if (negative) {
            s = s.substring(1);
        }

        String[] parts = s.split("\\.");
        int dollars = Integer.parseInt(parts[0].isEmpty() ? "0" : parts[0]);
        int cents = 0;
        if (parts.length > 1) {
            String frac = (parts[1] + "00").substring(0, 2);
            cents = Integer.parseInt(frac);
        }
        int total = dollars * 100 + cents;
        return negative ? -total : total;
    }

    // REQUIRES: none
    // MODIFIES: none
    // EFFECTS: converts cents into a string representation in dollars.cents
    // format (e.g., 1299 -> "12.99", -250 -> "-2.50")
    private String centsToDollars(int cents) {
        int abs = Math.abs(cents);
        String s = (abs / 100) + "." + String.format("%02d", abs % 100);
        return cents < 0 ? "-" + s : s;
    }

    // Table model used to display transactions in a JTable
    private static class TransactionTableModel extends AbstractTableModel {

        private final String[] columnNames = {
                "ID", "Date", "Category", "Type", "Amount ($)", "Note"
        };

        private List<Transaction> data = new ArrayList<>();

        // REQUIRES: txs is not null
        // MODIFIES: this
        // EFFECTS: replaces current data with a copy of txs and notifies the table
        public void setData(List<Transaction> txs) {
            this.data = new ArrayList<>(txs);
            fireTableDataChanged();
        }

        // REQUIRES: none
        // MODIFIES: none
        // EFFECTS: returns number of rows in this table (number of transactions)
        @Override
        public int getRowCount() {
            return data.size();
        }

        // REQUIRES: none
        // MODIFIES: none
        // EFFECTS: returns number of columns in this table (fixed)
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        // REQUIRES: 0 <= column < getColumnCount()
        // MODIFIES: none
        // EFFECTS: returns column name at given index
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        // REQUIRES: 0 <= rowIndex < getRowCount(),
        // 0 <= columnIndex < getColumnCount()
        // MODIFIES: none
        // EFFECTS: returns value to display at given row and column
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Transaction t = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return t.getId();
                case 1:
                    return t.getDate().toString();
                case 2:
                    return t.getCategory().name();
                case 3:
                    return t.getType().name();
                case 4:
                    int cents = t.getAmountInCents();
                    int abs = Math.abs(cents);
                    String s = (abs / 100) + "." + String.format("%02d", abs % 100);
                    return (cents < 0 ? "-" : "") + s;
                case 5:
                    return t.getNote();
                default:
                    return "";
            }
        }
    }

    // Panel that draws two pie charts: category share and income vs expense
    private static class PieChartsPanel extends JPanel {

        private final Ledger ledger;

        // REQUIRES: ledger is not null
        // MODIFIES: this
        // EFFECTS: stores ledger and sets preferred size for the chart panel
        public PieChartsPanel(Ledger ledger) {
            this.ledger = ledger;
            setPreferredSize(new Dimension(800, 500));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            drawCharts(g2);

            g2.dispose();
        }

        // REQUIRES: g2 not null
        // MODIFIES: g2
        // EFFECTS: Draw two pie charts based on the current window size and ledger
        // content
        private void drawCharts(Graphics2D g2) {
            int w = getWidth();
            int h = getHeight();

            Map<Transaction.Category, Integer> catTotals = computeCategoryTotals();
            int totalCat = computeTotal(catTotals);

            int income = Math.abs(ledger.totalIncome());
            int expense = Math.abs(ledger.totalExpense());
            int totalIE = income + expense;

            if (totalCat == 0 && totalIE == 0) {
                drawNoDataMessage(g2);
                return;
            }

            int pieDiameter = Math.min(w / 2 - 80, h - 120);
            if (pieDiameter <= 0) {
                return;
            }

            int centerY = h / 2 + 20;
            int centerX1 = w / 4;
            int centerX2 = 3 * w / 4;

            Color[] palette = createPalette();
            drawTitles(g2, centerX1, centerX2);
            drawCategoryPie(g2, catTotals, totalCat, centerX1, centerY, pieDiameter, palette);
            drawIncomeExpensePie(g2, income, expense, totalIE, centerX2, centerY, pieDiameter);
        }

        // REQUIRES: none
        // MODIFIES: none
        // EFFECTS: computes total amount per category (absolute values)
        private Map<Transaction.Category, Integer> computeCategoryTotals() {
            Map<Transaction.Category, Integer> totals = new HashMap<>();
            for (Transaction t : ledger.getAll()) {
                int v = Math.abs(t.getAmountInCents());
                if (v > 0) {
                    totals.merge(t.getCategory(), v, Integer::sum);
                }
            }
            return totals;
        }

        // REQUIRES: map not null
        // MODIFIES: none
        // EFFECTS: returns sum of all values in map
        private int computeTotal(Map<Transaction.Category, Integer> map) {
            int total = 0;
            for (int v : map.values()) {
                total += v;
            }
            return total;
        }

        // REQUIRES: g2 not null
        // MODIFIES: g2
        // EFFECTS: draws message when there is no data
        private void drawNoDataMessage(Graphics2D g2) {
            g2.setColor(Color.BLACK);
            g2.drawString("No non-zero amounts to display.", 20, 20);
        }

        // REQUIRES: none
        // MODIFIES: none
        // EFFECTS: returns a palette of colors for categories
        private Color[] createPalette() {
            return new Color[] {
                    Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE,
                    Color.MAGENTA, Color.CYAN, Color.PINK, Color.YELLOW,
                    Color.LIGHT_GRAY
            };
        }

        // REQUIRES: g2 not null
        // MODIFIES: g2
        // EFFECTS: draws chart titles
        private void drawTitles(Graphics2D g2, int centerX1, int centerX2) {
            g2.setColor(Color.BLACK);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
            g2.drawString("Category share", centerX1 - 70, 30);
            g2.drawString("Income vs Expense", centerX2 - 90, 30);
        }

        // REQUIRES: g2 not null
        // MODIFIES: g2
        // EFFECTS: draws pie chart for category shares or "no data" text
        private void drawCategoryPie(Graphics2D g2,
                Map<Transaction.Category, Integer> catTotals,
                int totalCat,
                int centerX,
                int centerY,
                int pieDiameter,
                Color[] palette) {
            if (totalCat <= 0) {
                g2.setColor(Color.BLACK);
                g2.drawString("No category data.", centerX - 60, centerY);
                return;
            }

            int x = centerX - pieDiameter / 2;
            int y = centerY - pieDiameter / 2;
            int startAngle = 0;
            int idx = 0;

            for (Map.Entry<Transaction.Category, Integer> entry : catTotals.entrySet()) {
                Transaction.Category c = entry.getKey();
                int value = entry.getValue();
                double frac = (double) value / totalCat;
                int arcAngle = (int) Math.round(frac * 360);

                g2.setColor(palette[idx % palette.length]);
                g2.fillArc(x, y, pieDiameter, pieDiameter, startAngle, arcAngle);

                g2.setColor(Color.WHITE);
                g2.drawArc(x, y, pieDiameter, pieDiameter, startAngle, arcAngle);

                drawCategoryLabel(g2, c, frac, centerX, centerY,
                        pieDiameter, startAngle, arcAngle);

                startAngle += arcAngle;
                idx++;
            }
        }

        // REQUIRES: g2 not null
        // MODIFIES: g2
        // EFFECTS: draws label and leader line for one category slice
        private void drawCategoryLabel(Graphics2D g2,
                Transaction.Category c,
                double frac,
                int centerX,
                int centerY,
                int pieDiameter,
                int startAngle,
                int arcAngle) {
            double midAngle = Math.toRadians(startAngle + arcAngle / 2.0);
            int rx = centerX + (int) (Math.cos(midAngle) * (pieDiameter / 2.0));
            int ry = centerY + (int) (Math.sin(midAngle) * (pieDiameter / 2.0));

            int labelRadius = pieDiameter / 2 + 30;
            int lx = centerX + (int) (Math.cos(midAngle) * labelRadius);
            int ly = centerY + (int) (Math.sin(midAngle) * labelRadius);

            g2.setColor(Color.BLACK);
            g2.drawLine(rx, ry, lx, ly);

            String text = c.name() + " " + String.format("%.1f%%", frac * 100);
            g2.drawString(text, lx, ly);
        }

        // REQUIRES: g2 not null
        // MODIFIES: g2
        // EFFECTS: draws pie chart for income vs expense or "no data" text
        private void drawIncomeExpensePie(Graphics2D g2,
                int income,
                int expense,
                int totalIE,
                int centerX,
                int centerY,
                int pieDiameter) {
            if (totalIE <= 0) {
                g2.setColor(Color.BLACK);
                g2.drawString("No income/expense data.", centerX - 80, centerY);
                return;
            }

            int x = centerX - pieDiameter / 2;
            int y = centerY - pieDiameter / 2;
            int startAngleIE = 0;

            double fracIncome = (double) income / totalIE;
            int arcIncome = (int) Math.round(fracIncome * 360);
            drawIncomeSlice(g2, centerX, centerY, pieDiameter,
                    x, y, startAngleIE, arcIncome, fracIncome);

            double fracExpense = (double) expense / totalIE;
            int arcExpense = 360 - arcIncome;
            int startExpense = startAngleIE + arcIncome;
            drawExpenseSlice(g2, centerX, centerY, pieDiameter,
                    x, y, startExpense, arcExpense, fracExpense);
        }

        // REQUIRES: g2 not null
        // MODIFIES: g2
        // EFFECTS: draws income slice and label
        private void drawIncomeSlice(Graphics2D g2,
                int centerX,
                int centerY,
                int pieDiameter,
                int x,
                int y,
                int startAngle,
                int arcAngle,
                double frac) {
            g2.setColor(new Color(0, 153, 0));
            g2.fillArc(x, y, pieDiameter, pieDiameter, startAngle, arcAngle);
            g2.setColor(Color.WHITE);
            g2.drawArc(x, y, pieDiameter, pieDiameter, startAngle, arcAngle);

            double midAngle = Math.toRadians(startAngle + arcAngle / 2.0);
            int rx = centerX + (int) (Math.cos(midAngle) * (pieDiameter / 2.0));
            int ry = centerY + (int) (Math.sin(midAngle) * (pieDiameter / 2.0));
            int labelRadius = pieDiameter / 2 + 30;
            int lx = centerX + (int) (Math.cos(midAngle) * labelRadius);
            int ly = centerY + (int) (Math.sin(midAngle) * labelRadius);

            g2.setColor(Color.BLACK);
            g2.drawLine(rx, ry, lx, ly);
            g2.drawString("INCOME " + String.format("%.1f%%", frac * 100), lx, ly);
        }

        // REQUIRES: g2 not null
        // MODIFIES: g2
        // EFFECTS: draws expense slice and label
        private void drawExpenseSlice(Graphics2D g2,
                int centerX,
                int centerY,
                int pieDiameter,
                int x,
                int y,
                int startAngle,
                int arcAngle,
                double frac) {
            g2.setColor(new Color(204, 0, 0));
            g2.fillArc(x, y, pieDiameter, pieDiameter, startAngle, arcAngle);
            g2.setColor(Color.WHITE);
            g2.drawArc(x, y, pieDiameter, pieDiameter, startAngle, arcAngle);

            double midAngle = Math.toRadians(startAngle + arcAngle / 2.0);
            int rx = centerX + (int) (Math.cos(midAngle) * (pieDiameter / 2.0));
            int ry = centerY + (int) (Math.sin(midAngle) * (pieDiameter / 2.0));
            int labelRadius = pieDiameter / 2 + 30;
            int lx = centerX + (int) (Math.cos(midAngle) * labelRadius);
            int ly = centerY + (int) (Math.sin(midAngle) * labelRadius);

            g2.setColor(Color.BLACK);
            g2.drawLine(rx, ry, lx, ly);
            g2.drawString("EXPENSE " + String.format("%.1f%%", frac * 100), lx, ly);
        }
    }

    // REQUIRES: none
    // MODIFIES: this
    // EFFECTS: prints event log to console and closes the window
    private void exitApplication() {
        System.out.println("Event log:");
        LogPrinter.printLog();
        dispose();
    }
}