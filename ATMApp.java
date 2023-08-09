package atm;

import javax.swing.*;

public class ATMApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Show the login window
            LoginGUI loginGUI = new LoginGUI();
            loginGUI.setTitle("ATM Login");
            loginGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginGUI.createAndShowGUI();
        });
    }
}