package ui;

import model.Event;
import model.EventLog;

// Utility class to print all events in the EventLog to the console.
public class LogPrinter {

    // EFFECTS: prints all events in the event log to the console
    public static void printLog() {
        for (Event e : EventLog.getInstance()) {
            System.out.println(e.toString());
        }
    }
}