package ui;

import javax.swing.SwingUtilities;

public class Main {
    // EFFECTS: starts the console application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FinanceAppGUI gui = new FinanceAppGUI();
            gui.setVisible(true);
        });
    }
}