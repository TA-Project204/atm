package atm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OptionMenuGUI extends JFrame {
    private String cardNumber;
    private double balance;
    private static User user;
    private JLabel balanceLabel;

    public OptionMenuGUI(String cardNumber, double balance) {
        this.cardNumber = cardNumber;
        this.balance = this.balance;
        this.user = user;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OptionMenuGUI optionMenuGUI = new OptionMenuGUI("1234", user.getBalance()); // Replace "1234" with the actual card number
            optionMenuGUI.setTitle("ATM Main Menu");
            optionMenuGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            optionMenuGUI.createAndShowGUI();
        });
    }

    void createAndShowGUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);

        // Create a label to display the account balance
        balanceLabel = new JLabel("Balance: " + balance);
        constraints.gridx = 1;
        constraints.gridy = 3;
        panel.add(balanceLabel, constraints);

        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeposit();
            }
        });

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleWithdraw();
            }
        });

        JButton viewHistoryButton = new JButton("View History");
        viewHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleViewHistory();
            }
        });

        JButton changePINButton = new JButton("Change PIN");
        changePINButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleChangePIN();
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(depositButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(withdrawButton, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(viewHistoryButton, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(changePINButton, constraints);

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleDeposit() {
        // Show a dialog to get the deposit amount from the user
        String depositAmountStr = JOptionPane.showInputDialog(this, "Enter the deposit amount:", "Deposit", JOptionPane.PLAIN_MESSAGE);
        try {
            double depositAmount = Double.parseDouble(depositAmountStr);

            // Perform the deposit operation for the user with the given amount
            User user = getUserByCardNumber(cardNumber);
            if (user != null) {
                user.deposit(depositAmount);

                // Update the user's balance in the database
                updateUserBalance(user.getCardNumber(), user.getBalance());

                // Save the transaction in the database
                saveTransaction(cardNumber, depositAmount, Transaction.TransactionType.DEPOSIT);

                // Update the balance label with the new balance
                balanceLabel.setText("Balance: " + user.getBalance());

                // Show a success message
                JOptionPane.showMessageDialog(this, "Deposit of " + depositAmount + " successful. New balance: " + user.getBalance(), "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid card number.", "Deposit Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount entered. Please enter a valid number.", "Deposit Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleWithdraw() {
        // Show a dialog to get the withdrawal amount from the user
        String withdrawalAmountStr = JOptionPane.showInputDialog(this, "Enter the withdrawal amount:", "Withdrawal", JOptionPane.PLAIN_MESSAGE);
        try {
            double withdrawalAmount = Double.parseDouble(withdrawalAmountStr);

            // Perform the withdrawal operation for the user with the given amount
            User user = getUserByCardNumber(cardNumber);
            if (user != null) {
                if (user.getBalance() >= withdrawalAmount) {
                    user.withdraw(withdrawalAmount);

                    // Update the user's balance in the database
                    updateUserBalance(user.getCardNumber(), user.getBalance());

                    // Save the transaction in the database
                    saveTransaction(cardNumber, withdrawalAmount, Transaction.TransactionType.WITHDRAWAL);

                    // Update the balance label with the new balance
                    balanceLabel.setText("Balance: " + user.getBalance());

                    // Show a success message
                    JOptionPane.showMessageDialog(this, "Withdrawal of " + withdrawalAmount + " successful. New balance: " + user.getBalance(), "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient balance.", "Withdrawal Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid card number.", "Withdrawal Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount entered. Please enter a valid number.", "Withdrawal Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleViewHistory() {
        // Retrieve the transaction history for the user with the given card number
        TransactionHistory transactionHistory = getTransactionHistory(cardNumber);
        if (transactionHistory != null) {
            // Show the transaction history in a dialog box
            JTextArea textArea = new JTextArea(transactionHistory.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            JOptionPane.showMessageDialog(this, scrollPane, "Transaction History for Card Number: " + cardNumber, JOptionPane.PLAIN_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid card number or no transaction history found.", "Transaction History Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // Helper method to save the transaction in the database
    private void saveTransaction(String cardNumber, double amount, Transaction.TransactionType type) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO Transaction (cardNumber, amount, type) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            preparedStatement.setDouble(2, amount);
            preparedStatement.setString(3, type.name());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to retrieve the transaction history from the database
    private TransactionHistory getTransactionHistory(String cardNumber) {
        // Connect to the database and fetch the transaction history for the given card number
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT amount, date, type FROM Transaction WHERE cardNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            TransactionHistory transactionHistory = new TransactionHistory();
            while (resultSet.next()) {
                double amount = resultSet.getDouble("amount");
                String typeStr = resultSet.getString("type");
                Transaction.TransactionType type = typeStr.equals("DEPOSIT") ? Transaction.TransactionType.DEPOSIT : Transaction.TransactionType.WITHDRAWAL;
                Transaction transaction = new Transaction(amount, type);
                transactionHistory.addTransaction(transaction);
            }

            resultSet.close();
            preparedStatement.close();

            return transactionHistory;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    private void handleChangePIN() {
        // Show a dialog to get the new PIN from the user
        String newPINStr = JOptionPane.showInputDialog(this, "Enter the new PIN (4 digits):", "Change PIN", JOptionPane.PLAIN_MESSAGE);
        try {
            int newPIN = Integer.parseInt(newPINStr);

            // Perform the change PIN operation for the user with the new PIN
            User user = getUserByCardNumber(cardNumber);
            if (user != null) {
                user.setPin(newPIN);

                // Update the user's PIN in the database
                updateUserPIN(user.getCardNumber(), user.getPin());

                System.out.println("PIN successfully changed for card number: " + cardNumber);

                // Show a success message
                JOptionPane.showMessageDialog(this, "PIN successfully changed.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid card number.", "Change PIN Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid PIN entered. Please enter a 4-digit number.", "Change PIN Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper methods to access the database

    private User getUserByCardNumber(String cardNumber) {
        User user = null;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM User WHERE cardNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String dbCardNumber = resultSet.getString("cardNumber");
                int dbPIN = resultSet.getInt("pin");
                double dbBalance = resultSet.getDouble("balance");
                user = new User(dbCardNumber, dbPIN, dbBalance);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    private void updateUserBalance(String cardNumber, double newBalance) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "UPDATE User SET balance = ? WHERE cardNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, newBalance);
            preparedStatement.setString(2, cardNumber);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUserPIN(String cardNumber, int newPIN) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "UPDATE User SET pin = ? WHERE cardNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, newPIN);
            preparedStatement.setString(2, cardNumber);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
