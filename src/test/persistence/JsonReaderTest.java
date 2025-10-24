package persistence;

import model.Ledger;
import model.Transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderTest extends JsonTest { 

    private Ledger ledger;
    private Transaction t1;
    private Transaction t2;
    private final String path = "./data/testReaderGeneralLedger_gen.json";

    @BeforeEach
    void runBefore() throws IOException {
        ledger = new Ledger();
        t1 = new Transaction("t1", 1000,
                java.time.LocalDate.of(2025, 10, 1),
                Transaction.Category.FOOD, Transaction.TxnType.EXPENSE, "Lunch");
        t2 = new Transaction("t2", 5000,
                java.time.LocalDate.of(2025, 10, 2),
                Transaction.Category.SALARY, Transaction.TxnType.INCOME, "Paycheck");
        ledger.add(t1);
        ledger.add(t2);

        JsonWriter writer = new JsonWriter(path);
        writer.open();
        writer.write(ledger);
        writer.close();
    }

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            Ledger ledger = reader.read();
            fail("IOException expected");
        } catch (IOException e) { 
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void testReaderEmptyLedgerwrittenThenRead() {
        try {
            Ledger ledger = new Ledger();
            JsonWriter writer = new JsonWriter("./data/testReaderEmptyLedger_gen.json");
            writer.open();
            writer.write(ledger);
            writer.close();

            JsonReader reader = new JsonReader("./data/testReaderEmptyLedger_gen.json");
            Ledger readBack = reader.read();
            assertEquals(0, readBack.getAll().size());

            String p1 = "./data/testReaderNoTransactionsKey.json";
            Files.writeString(Paths.get(p1), "{}", StandardCharsets.UTF_8);
            Ledger noKeyLedger = new JsonReader(p1).read();
            assertTrue(noKeyLedger.getAll().isEmpty());

            String p2 = "./data/testReaderTransactionsNotArray.json";
            Files.writeString(Paths.get(p2), "{\"transactions\":123}", StandardCharsets.UTF_8);
            Ledger notArrayLedger = new JsonReader(p2).read();
            assertTrue(notArrayLedger.getAll().isEmpty());

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testReaderGeneralLedgerWrittenThenRead() {
        try {
            JsonReader reader = new JsonReader(path);
            Ledger readBack = reader.read();
            List<Transaction> txs = readBack.getAll();
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