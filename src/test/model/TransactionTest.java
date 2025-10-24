package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {
    private Transaction incomeTxn;
    private Transaction expenseTxn;

    @BeforeEach
    void runBefore() {
        incomeTxn = new Transaction("inc1", 
                10000,
                LocalDate.of(2025, 10, 1),
                Transaction.Category.SALARY,
                Transaction.TxnType.INCOME,
                "October salary");

        expenseTxn = new Transaction("exp1", 
                2500,
                LocalDate.of(2025, 10, 2),
                Transaction.Category.FOOD,
                Transaction.TxnType.EXPENSE,
                "Lunch");
    }

    @Test
    void testConstructor() {
        assertEquals("inc1", incomeTxn.getId());
        assertEquals(10000, incomeTxn.getAmountInCents());
        assertEquals(LocalDate.of(2025, 10, 1), incomeTxn.getDate());
        assertEquals(Transaction.Category.SALARY, incomeTxn.getCategory());
        assertEquals(Transaction.TxnType.INCOME, incomeTxn.getType());
        assertEquals("October salary", incomeTxn.getNote());

        assertEquals("exp1", expenseTxn.getId());
        assertEquals(2500, expenseTxn.getAmountInCents());
        assertEquals(LocalDate.of(2025, 10, 2), expenseTxn.getDate());
        assertEquals(Transaction.Category.FOOD, expenseTxn.getCategory());
        assertEquals(Transaction.TxnType.EXPENSE, expenseTxn.getType());
        assertEquals("Lunch", expenseTxn.getNote());
    }

    @Test
    void testIsIncomeAndIsExpense() {
        assertTrue(incomeTxn.isIncome());
        assertFalse(incomeTxn.isExpense());
        assertTrue(expenseTxn.isExpense());
        assertFalse(expenseTxn.isIncome());
    }

    @Test
    void testSetNote() {
        assertEquals("Lunch", expenseTxn.getNote());
        expenseTxn.setNote("Dinner");
        assertEquals("Dinner", expenseTxn.getNote());
    }

    @Test
    void testToString() {
        assertNotNull(incomeTxn.toString());
        assertNotNull(expenseTxn.toString());
    } 

    @Test
    void testConstructorHandlesNullNote() {
        Transaction t = new Transaction("id", 500, LocalDate.of(2025, 10, 3),
                Transaction.Category.OTHER, Transaction.TxnType.EXPENSE, null);
        assertEquals("", t.getNote()); 
    }

    @Test
    void testSetNoteHandlesNull() {
        expenseTxn.setNote(null);
        assertEquals("", expenseTxn.getNote());
    }

    @Test
    void testToStringWhenNoteEmpty() {
        Transaction noNoteTxn = new Transaction("n1", 3000,
                LocalDate.of(2025, 10, 4),
                Transaction.Category.OTHER, Transaction.TxnType.EXPENSE, "");
        String s = noNoteTxn.toString();
        assertFalse(s.contains(" (")); 
    }
}