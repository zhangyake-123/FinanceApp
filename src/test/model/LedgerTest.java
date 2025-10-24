package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LedgerTest {
    private Ledger ledger;
    private Transaction t1;
    private Transaction t2;
    private Transaction t3;
    private Transaction t4;

    @BeforeEach
    void runBefore() {
        ledger = new Ledger();

        t1 = new Transaction("t1", 1000, LocalDate.of(2025, 10, 1),
                Transaction.Category.FOOD, Transaction.TxnType.EXPENSE, "Lunch");
        t2 = new Transaction("t2", 5000, LocalDate.of(2025, 10, 2),
                Transaction.Category.SALARY, Transaction.TxnType.INCOME, "Paycheck");
        t3 = new Transaction("t3", 200, LocalDate.of(2025, 11, 1),
                Transaction.Category.TRANSPORT, Transaction.TxnType.EXPENSE, "Bus");
        t4 = new Transaction("t4", 800, LocalDate.of(2025, 10, 3),
                Transaction.Category.SHOPPING, Transaction.TxnType.EXPENSE, "Clothes");
    }

    @Test
    void testAdd() {
        assertTrue(ledger.add(t1));
        assertTrue(ledger.add(t2));
        assertEquals(2, ledger.getAll().size());

        assertFalse(ledger.add(t1));
        assertEquals(2, ledger.getAll().size()); 

        Ledger emptyLedger = new Ledger();
        assertEquals(0, emptyLedger.totalIncome());
        assertEquals(0, emptyLedger.totalExpense());
        assertEquals(0, emptyLedger.balance());
    }

    @Test
    void testRemoveById() {
        ledger.add(t1);
        ledger.add(t2);
        assertTrue(ledger.removeById("t1"));
        assertFalse(ledger.removeById("t1")); 
        assertFalse(ledger.removeById("t3"));
    }

    @Test
    void testGetAll() {
        ledger.add(t1);
        ledger.add(t2);
        List<Transaction> list = ledger.getAll();
        assertEquals(2, list.size());
        assertThrows(UnsupportedOperationException.class, () -> list.add(t3));
    }

    @Test
    void testTotalsAndBalance() {
        ledger.add(t1);
        ledger.add(t2);
        ledger.add(t3);
        ledger.add(t4);
        assertEquals(5000, ledger.totalIncome());
        assertEquals(2000, ledger.totalExpense());
        assertEquals(3000, ledger.balance());
    }

    @Test
    void testByCategory() {
        ledger.add(t1);
        ledger.add(t4);
        ledger.add(t2);
        List<Transaction> foodList = ledger.byCategory(Transaction.Category.FOOD);
        assertEquals(1, foodList.size());
        assertEquals(Transaction.Category.FOOD, foodList.get(0).getCategory());
    }

    @Test
    void testByMonthAndYear() {
        ledger.add(t1);
        ledger.add(t2);
        ledger.add(t3);
        List<Transaction> october = ledger.byMonth(YearMonth.of(2025, 10));
        assertEquals(2, october.size());
        List<Transaction> year2025 = ledger.byYear(2025);
        assertEquals(3, year2025.size()); 

        Transaction tPast = new Transaction(
                "tPast",
                123,
                LocalDate.of(2024, 10, 10),
                Transaction.Category.OTHER,
                Transaction.TxnType.EXPENSE,
                "past");
        ledger.add(tPast);

        List<Transaction> november = ledger.byMonth(YearMonth.of(2025, 11));
        assertEquals(1, november.size());
    }

    @Test
    void testExpenseTotalsByCategory() {
        ledger.add(t1);
        ledger.add(t2);
        ledger.add(t3);
        ledger.add(t4);
        Map<Transaction.Category, Integer> totals = ledger.expenseTotalsByCategory();
        assertEquals(3, totals.size()); 
        assertEquals(1000, totals.get(Transaction.Category.FOOD));
        assertEquals(200, totals.get(Transaction.Category.TRANSPORT));
        assertEquals(800, totals.get(Transaction.Category.SHOPPING));
        assertFalse(totals.containsKey(Transaction.Category.SALARY)); 
    }

    @Test
    void testIncomeAndExpenseBetween() {
        ledger.add(t1);
        ledger.add(t2);
        ledger.add(t3);
        LocalDate start = LocalDate.of(2025, 10, 1);
        LocalDate end = LocalDate.of(2025, 10, 31);
        assertEquals(5000, ledger.incomeBetween(start, end));
        assertEquals(1000, ledger.expenseBetween(start, end));
    } 

     @Test
    void testByMonthAndYear_NoMatches() {
        ledger.add(t1);

        List<Transaction> none = ledger.byMonth(YearMonth.of(2025, 9));
        assertTrue(none.isEmpty());

        List<Transaction> noneYear = ledger.byYear(2024);
        assertTrue(noneYear.isEmpty());
    }

    @Test
    void testIncomeAndExpenseBetween_EdgeCases() {
        ledger.add(t1);
        ledger.add(t2);

        LocalDate date = LocalDate.of(2025, 10, 1);
        assertEquals(0, ledger.incomeBetween(date, date));
        assertEquals(1000, ledger.expenseBetween(date, date));

        LocalDate start = LocalDate.of(2025, 11, 1);
        LocalDate end = LocalDate.of(2025, 10, 1);
        assertEquals(0, ledger.incomeBetween(start, end));
        assertEquals(0, ledger.expenseBetween(start, end));
    }
}