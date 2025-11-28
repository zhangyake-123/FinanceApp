# Personal Finance Tracker

## Introduction
This project is a **Personal Finance Tracker**, a desktop Java application that helps users keep track of their expenses and incomes.  
It allows users to record transactions, categorize them, and view summaries of their financial activity.

- **What will the application do?**  
  The application will let users add transactions, view a list of past transactions, and calculate summaries such as total income, expenses, and balance.

- **Who will use it?**  
  This application will be useful for students and individuals who want a simple way to track their daily finances.

- **Why is this project of interest to me?**  
  I am interested in this project because personal finance management is a practical skill. Building this application allows me to combine my interest in programming with a useful tool that I can use myself.

## User Stories
- As a user, I want to be able to **add a transaction** (with amount, category, and type: income or expense) to my ledger.  
- As a user, I want to be able to **view the list of transactions** in my ledger.  
- As a user, I want to be able to **calculate and display the total income, total expenses, and overall balance**.  
- As a user, I want to be able to **delete a transaction** from my ledger if I made a mistake.  
- As a user, I want to be able to **view my expenses grouped by category** (e.g., Shopping, Food, Entertainment).  
- As a user, I want to be able to **view summaries of my income and expenses over different time periods** (e.g., weekly, monthly, yearly).  
- As a user, I want to optionally **save the entire ledger (all transactions)** to a JSON file from the menu so I can keep my data.
- As a user, I want to optionally **load my ledger** from a JSON file and resume exactly where I left off last time.

# Instructions for End User

- You can view the panel that displays the Xs that have already been added to the Y by **running the `ui.Main` class**.  
  When the window opens, the lower part of the main GUI shows a table inside a panel titled **"Transactions"**.  
  This table displays all the transactions (Xs) that have already been added to the ledger (Y).

- You can generate the first required action related to the user story **"adding multiple Xs to a Y"** by  
  **clicking the button labelled "Add transaction"** in the top Actions panel.  
  A sequence of dialog windows will appear, prompting you to enter:
  1. Type (I = income, E = expense)  
  2. Amount (e.g., `12.99`)  
  3. Date (`YYYY-MM-DD`)  
  4. Category (FOOD, SHOPPING, TRANSPORT, etc.)  
  5. Optional note  
  After you finish all steps with valid input, the new transaction is added to the ledger and appears in the Transactions table.

- You can generate the second required action related to the user story **"adding multiple Xs to a Y"** by  
  **clicking the button labelled "View by category"** in the top Actions panel.  
  Enter a category name (e.g., `FOOD`, `SHOPPING`, `RENT`) in the dialog.  
  The Transactions table will then update to display only the subset of transactions (Xs) in the ledger (Y) that belong to that category.

- You can locate my visual component by **clicking the button labelled "Show charts"** in the top Actions panel.  
  This opens a new window containing two pie charts:
  1. A pie chart showing the share of total amount by **category** (each category shown in a different colour with labels and percentages).  
  2. A pie chart showing the share of total amount by **INCOME vs EXPENSE**.  

- You can save the state of my application by **clicking the button labelled "Save"** in the top Actions panel.  
  This writes the current ledger (all transactions in the table) to the JSON file located at `./data/ledger.json`, and a dialog will confirm that the save was successful (or show an error if it fails).

- You can reload the state of my application by **clicking the button labelled "Load"** in the top Actions panel.  
  This reads the previously saved ledger from `./data/ledger.json`, replaces the current in-memory ledger, and updates the Transactions table so you can continue from where you left off. A dialog will confirm that the load was successful (or show an error if it fails). 

## Phase 4: Task 2 

Below is a sample of the events that were logged in one run of the application:

Event log:  
Fri Nov 28 02:08:17 PST 2025  
Transaction added: id=ad9ecf6c-f4bc-442e-bccd-a2c0506894e6, type=INCOME, category=FOOD, amountInCents=1399, date=2013-03-14  
Fri Nov 28 02:08:32 PST 2025  
Transaction added: id=a6da9bab-3685-41a8-8095-22d9e5dd9c5a, type=EXPENSE, category=OTHER, amountInCents=4500, date=2006-11-11  
Fri Nov 28 02:09:22 PST 2025  
Transaction removed: id=ad9ecf6c-f4bc-442e-bccd-a2c0506894e6, amountInCents=1399, category=FOOD, date=2013-03-14  
Fri Nov 28 02:09:45 PST 2025  
Transaction added: id=52eb9d21-18d6-43a6-b69e-42d96bf67b53, type=INCOME, category=SHOPPING, amountInCents=6700, date=2012-01-23  

## Phase 4: Task 3  

After looking at my UML diagram, I noticed a few things I would refactor if I had more time. One issue is that both FinanceApp (the console version) and FinanceAppGUI (the GUI version) each keep their own Ledger. This means the model is duplicated in two places, which can make the program harder to maintain. If I were to improve the design, I would introduce a small controller class that holds one shared Ledger, and then let both UIs talk to that controller instead of directly working with the model. This would make the structure cleaner and reduce repeated logic.

I also realized that FinanceAppGUI is doing a lot of different jobs at the same time: building the UI, checking inputs, updating the table, drawing charts, and saving/loading files. If I had more time, I would split some of these responsibilities into separate helper classes so the GUI would be smaller and easier to read. These changes would not add new features, but they would make the design clearer and the code easier to maintain in the future.