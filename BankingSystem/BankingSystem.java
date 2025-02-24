import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

// Base class for Bank Accounts
abstract class BankAccount {
    protected String accountNumber;
    protected String accountHolder;
    protected double balance;
    protected List<String> transactionHistory;

    public BankAccount(String accountNumber, String accountHolder, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.transactionHistory = new ArrayList<>();
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public double getBalance() { return balance; }
    
    public void deposit(double amount) {
        balance += amount;
        transactionHistory.add(LocalDateTime.now() + " - Deposited: " + amount);
    }
    
    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            transactionHistory.add(LocalDateTime.now() + " - Withdrawn: " + amount);
            return true;
        }
        return false;
    }
    
    public List<String> getTransactionHistory() { return transactionHistory; }
    public abstract String getAccountType();
}

// Savings Account class with Interest Calculation
class SavingsAccount extends BankAccount {
    private static final double INTEREST_RATE = 0.04;
    
    public SavingsAccount(String accountNumber, String accountHolder, double balance) {
        super(accountNumber, accountHolder, balance);
    }
    
    public void applyInterest() {
        double interest = balance * INTEREST_RATE;
        deposit(interest);
    }
    
    public String getAccountType() { return "Savings"; }
}

// Current Account class
class CurrentAccount extends BankAccount {
    public CurrentAccount(String accountNumber, String accountHolder, double balance) {
        super(accountNumber, accountHolder, balance);
    }
    public String getAccountType() { return "Current"; }
}

// Main GUI Class
public class BankingSystem extends JFrame {
    private Map<String, BankAccount> accounts = new HashMap<>();
    private JTextArea outputArea;
    
    public BankingSystem() {
        setTitle("Banking System");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1));
        
        JButton createAccountBtn = new JButton("Create Account");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton transferBtn = new JButton("Fund Transfer");
        JButton listAccountsBtn = new JButton("List Accounts");
        JButton transactionHistoryBtn = new JButton("Transaction History");
        JButton exitBtn = new JButton("Exit");

        panel.add(createAccountBtn);
        panel.add(depositBtn);
        panel.add(withdrawBtn);
        panel.add(transferBtn);
        panel.add(listAccountsBtn);
        panel.add(transactionHistoryBtn);
        panel.add(exitBtn);

        add(panel, BorderLayout.WEST);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        createAccountBtn.addActionListener(e -> createAccount());
        depositBtn.addActionListener(e -> deposit());
        withdrawBtn.addActionListener(e -> withdraw());
        transferBtn.addActionListener(e -> fundTransfer());
        listAccountsBtn.addActionListener(e -> listAccounts());
        transactionHistoryBtn.addActionListener(e -> transactionHistory());
        exitBtn.addActionListener(e -> System.exit(0));
    }

    private void createAccount() {
        String accountNumber = JOptionPane.showInputDialog("Enter Account Number:");
        String accountHolder = JOptionPane.showInputDialog("Enter Account Holder Name:");
        String[] options = {"Savings", "Current"};
        int choice = JOptionPane.showOptionDialog(null, "Select Account Type:", "Account Type",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (accountNumber != null && accountHolder != null) {
            if (choice == 0) {
                accounts.put(accountNumber, new SavingsAccount(accountNumber, accountHolder, 0));
            } else {
                accounts.put(accountNumber, new CurrentAccount(accountNumber, accountHolder, 0));
            }
            JOptionPane.showMessageDialog(null, "Account Created Successfully!");
        }
    }

    private void deposit() {
        String accountNumber = JOptionPane.showInputDialog("Enter Account Number:");
        BankAccount account = accounts.get(accountNumber);
        if (account != null) {
            double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter Deposit Amount:"));
            account.deposit(amount);
            JOptionPane.showMessageDialog(null, "Deposit Successful! New Balance: " + account.getBalance());
        } else {
            JOptionPane.showMessageDialog(null, "Account Not Found!");
        }
    }

    private void withdraw() {
        String accountNumber = JOptionPane.showInputDialog("Enter Account Number:");
        BankAccount account = accounts.get(accountNumber);
        if (account != null) {
            double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter Withdrawal Amount:"));
            if (account.withdraw(amount)) {
                JOptionPane.showMessageDialog(null, "Withdrawal Successful! New Balance: " + account.getBalance());
            } else {
                JOptionPane.showMessageDialog(null, "Insufficient Balance!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Account Not Found!");
        }
    }

    private void fundTransfer() {
        String fromAcc = JOptionPane.showInputDialog("Enter Sender Account Number:");
        String toAcc = JOptionPane.showInputDialog("Enter Receiver Account Number:");
        double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter Transfer Amount:"));

        BankAccount sender = accounts.get(fromAcc);
        BankAccount receiver = accounts.get(toAcc);

        if (sender != null && receiver != null) {
            if (sender.withdraw(amount)) {
                receiver.deposit(amount);
                JOptionPane.showMessageDialog(null, "Transfer Successful!");
            } else {
                JOptionPane.showMessageDialog(null, "Insufficient Balance!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid Account Number!");
        }
    }

    private void transactionHistory() {
        String accountNumber = JOptionPane.showInputDialog("Enter Account Number:");
        BankAccount account = accounts.get(accountNumber);
        if (account != null) {
            outputArea.setText("Transaction History:\n" + String.join("\n", account.getTransactionHistory()));
        } else {
            JOptionPane.showMessageDialog(null, "Account Not Found!");
        }
    }

    private void listAccounts() {
        StringBuilder accountList = new StringBuilder("Accounts:\n");
        
        for (BankAccount account : accounts.values()) {
            accountList.append("Account Number: ").append(account.getAccountNumber())
                       .append(", Holder: ").append(account.getAccountHolder())
                       .append(", Type: ").append(account.getAccountType())
                       .append(", Balance: ").append(account.getBalance()).append("\n");
        }

        outputArea.setText(accountList.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankingSystem().setVisible(true));
    }
}
