// Based on the CPSC 210 JsonSerializationDemo pattern; adapted for Ledger/Transaction.

package persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Ledger;
import model.Transaction;

public class JsonReader {
    private String source;

    // MODIFIES: this
    // EFFECTS: constructs a reader for source
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads ledger from file and returns it; throws IOException on read
    // error
    public Ledger read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseLedger(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> lines = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            lines.forEach(s -> contentBuilder.append(s));
        }
        return contentBuilder.toString();
    }

    // EFFECTS: parses ledger from JSON object and returns it
    private Ledger parseLedger(JSONObject jsonObject) {
        Ledger ledger = new Ledger();
        JSONArray arr = jsonObject.optJSONArray("transactions");
        if (arr != null) {
            for (Object obj : arr) {
                JSONObject o = (JSONObject) obj;
                String id = o.getString("id");
                int amount = o.getInt("amountInCents");
                LocalDate date = LocalDate.parse(o.getString("date"));
                Transaction.Category cat = Transaction.Category.valueOf(o.getString("category"));
                Transaction.TxnType type = Transaction.TxnType.valueOf(o.getString("type"));
                String note = o.optString("note", "");

                ledger.add(new Transaction(id, amount, date, cat, type, note));
            }
        }
        return ledger;
    }
}
