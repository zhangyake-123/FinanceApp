# FinanceApp — Personal Finance Tracker (Java Desktop)

FinanceApp is a Java desktop application for tracking personal income and expenses.  
It allows users to record transactions, organize them by category, view financial summaries, and visualize spending patterns using charts.

This project demonstrates object-oriented design, GUI development, JSON data persistence, and event logging in Java.

---

# Features

- Add income or expense transactions with amount, date, category, and optional notes
- View and manage a list of recorded transactions
- Delete transactions if they were entered incorrectly
- Filter transactions by category
- Calculate financial summaries including:
  - total income
  - total expenses
  - current balance
- Visualize financial data using pie charts
- Save and load transaction data using JSON files
- Log important application events during runtime

---

# Technologies Used

- **Java**
- **Swing GUI**
- **JSON persistence**
- **Object-Oriented Design**
- **Event Logging System**

---

# Getting Started

## Prerequisites

- Java 11 or newer
- An IDE such as IntelliJ IDEA or VS Code with Java support

## Running the Application

1. Clone the repository

```bash
git clone <repository-url>
```

2. Open the project in your IDE

3. Run the main class

```
ui.Main
```

When the application launches, the main window will display a **Transactions panel** that shows all recorded transactions.

---

# How to Use

## Add a Transaction

Click **Add transaction** in the Actions panel and follow the prompts:

1. Enter the transaction type  
   - `I` for income  
   - `E` for expense

2. Enter the amount (example: `12.99`)

3. Enter the date in format

```
YYYY-MM-DD
```

4. Enter a category (example: FOOD, SHOPPING, TRANSPORT)

5. Optionally enter a note

After completing these steps, the transaction will be added to the ledger and displayed in the transactions table.

---

## View Transactions by Category

Click **View by category** and enter a category name such as:

```
FOOD
SHOPPING
RENT
```

The transactions table will update to display only transactions in that category.

---

## View Charts

Click **Show charts** to open a new window containing two pie charts:

1. A chart showing the distribution of transaction amounts by category
2. A chart comparing **income vs expense**

These charts provide a quick overview of financial activity.

---

## Save Data

Click **Save** in the Actions panel.

The application will save all transactions to the file:

```
./data/ledger.json
```

A confirmation dialog will appear when saving is successful.

---

## Load Data

Click **Load** to restore previously saved data.

The application will read the ledger from:

```
./data/ledger.json
```

The transactions table will update so you can continue where you left off.

---

# Example Event Log

The application records important actions during execution.

Example log output:

```
Transaction added: id=ad9ecf6c-f4bc-442e-bccd-a2c0506894e6, type=INCOME, category=FOOD, amountInCents=1399, date=2013-03-14

Transaction added: id=a6da9bab-3685-41a8-8095-22d9e5dd9c5a, type=EXPENSE, category=OTHER, amountInCents=4500, date=2006-11-11

Transaction removed: id=ad9ecf6c-f4bc-442e-bccd-a2c0506894e6, amountInCents=1399, category=FOOD, date=2013-03-14
```

---

# Future Improvements

If more development time were available, the following improvements could be made:

- Introduce a controller layer so multiple user interfaces can share a single Ledger instance
- Separate GUI responsibilities (input validation, table updates, chart rendering) into helper classes
- Add more advanced financial analytics such as weekly or monthly spending summaries
- Improve the overall UI layout and interaction flow

These changes would make the architecture cleaner and improve maintainability without changing the core functionality.

---

# Project Motivation

Personal finance management is an important practical skill.  
This project combines software development with a useful real-world application that helps users better understand and manage their spending habits.