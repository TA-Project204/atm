package atm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginGUI extends JFrame {
    private JTextField cardNumberField;
    private JPasswordField pinField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginGUI loginGUI = new LoginGUI();
            loginGUI.setTitle("ATM Login");
            loginGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginGUI.createAndShowGUI();
        });
    }

    void createAndShowGUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);

        JLabel cardNumberLabel = new JLabel("Card Number:");
        cardNumberField = new JTextField(15);
        cardNumberField.setToolTipText("Enter your card number");

        JLabel pinLabel = new JLabel("PIN:");
        pinField = new JPasswordField(15);
        pinField.setToolTipText("Enter your PIN");

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(cardNumberLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(cardNumberField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(pinLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(pinField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(loginButton, constraints);

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleLogin() {
        // Get the entered card number and PIN
        String cardNumber = cardNumberField.getText();
        String pin = new String(pinField.getPassword());

        // Perform validation and database check here
        // For simplicity, we assume that the card number and PIN are correct

        // If the validation is successful, proceed to the main menu
        User user = getUserByCardNumber(cardNumber);
        if (user != null && Integer.parseInt(pin) == user.getPin()) {
            OptionMenuGUI optionMenu = new OptionMenuGUI(cardNumber, user.getBalance());
            optionMenu.setTitle("ATM Main Menu");
            optionMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            optionMenu.createAndShowGUI();

            // Close the login window
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid card number or PIN.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to fetch user details from the database based on the card number
    private User getUserByCardNumber(String cardNumber) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT pin, balance FROM User WHERE cardNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int pin = resultSet.getInt("pin");
                double balance = resultSet.getDouble("balance");
                return new User(cardNumber, pin, balance);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean validateLogin(String cardNumber, String pin) {
        // Perform validation and database check here
        // For simplicity, we assume that the card number and PIN are correct
        // You should replace this with actual database queries and validations

        try {
            // Get the database connection
            Connection connection = DatabaseConnection.getConnection();

            // Prepare the SQL statement to retrieve user data based on the card number
            String query = "SELECT * FROM User WHERE cardNumber = ? AND pin = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            preparedStatement.setString(2, pin);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if the query returned any rows (i.e., if the login credentials are valid)
            if (resultSet.next()) {
                // Valid login
                return true;
            } else {
                // Invalid login
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
