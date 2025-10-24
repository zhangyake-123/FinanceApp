package persistence;

import model.Ledger;
import model.Transaction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonWriterTest extends JsonTest {

    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException expected");
        } catch (IOException e) {
        }
    }

    @Test
    void testWriterEmptyLedger() {
        try {
            Ledger ledger = new Ledger();
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyLedger.json");
            writer.open();
            writer.write(ledger);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyLedger.json");
            ledger = reader.read();
            assertEquals(0, ledger.getAll().size());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralLedger() {
        try {
            Ledger ledger = new Ledger();
            Transaction t1 = new Transaction("t1", 1000, LocalDate.of(2025, 10, 1),
                    Transaction.Category.FOOD, Transaction.TxnType.EXPENSE, "Lunch");
            Transaction t2 = new Transaction("t2", 5000, LocalDate.of(2025, 10, 2),
                    Transaction.Category.SALARY, Transaction.TxnType.INCOME, "Paycheck");
            ledger.add(t1);
            ledger.add(t2);

            JsonWriter writer = new JsonWriter("./data/testWriterGeneralLedger.json");
            writer.open();
            writer.write(ledger);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralLedger.json");
            ledger = reader.read();
            List<Transaction> txs = ledger.getAll();
            assertEquals(2, txs.size());
            checkTransaction("t1", 1000, Transaction.Category.FOOD,
                    Transaction.TxnType.EXPENSE, "Lunch", txs.get(0));
            checkTransaction("t2", 5000, Transaction.Category.SALARY,
                    Transaction.TxnType.INCOME, "Paycheck", txs.get(1));
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}