// Based on the CPSC 210 JsonSerializationDemo pattern; adapted for Ledger/Transaction. 

package persistence;

import model.Ledger;
import org.json.JSONObject;

import java.io.*;

public class JsonWriter {
    private static final int INDENT = 2;
    private PrintWriter writer;
    private String destination;

    // MODIFIES: this
    // EFFECTS: constructs a writer for destination
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens underlying writer; throws if cannot open
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // MODIFIES: this, filesystem
    // EFFECTS: writes ledger as JSON to file
    public void write(Ledger ledger) {
        JSONObject json = ledger.toJson();
        saveToFile(json.toString(INDENT));
    }

    // MODIFIES: this
    // EFFECTS: closes underlying writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }
}