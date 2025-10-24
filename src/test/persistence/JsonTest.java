package persistence;

import model.Transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTest {
    protected void checkTransaction(String id, int amountInCents,
                                    Transaction.Category category,
                                    Transaction.TxnType type,
                                    String note,
                                    Transaction tx) {
        assertEquals(id, tx.getId());
        assertEquals(amountInCents, tx.getAmountInCents());
        assertEquals(category, tx.getCategory());
        assertEquals(type, tx.getType());
        assertEquals(note, tx.getNote());
    }
}