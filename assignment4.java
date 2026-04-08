package Assignment_4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

class InvalidCidException extends Exception {
    public InvalidCidException(String message) {
        super(message);
    }
}

class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }
}

class MinimumBalanceException extends Exception {
    public MinimumBalanceException(String message) {
        super(message);
    }
}

class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}

class Customer {
    private final int cid;
    private final String cname;
    private double amount;

    public Customer(int cid, String cname, double amount) {
        this.cid = cid;
        this.cname = cname;
        this.amount = amount;
    }

    public int getCid() {
        return cid;
    }

    public String getCname() {
        return cname;
    }

    public double getAmount() {
        return amount;
    }

    public void deposit(double value) {
        amount += value;
    }

    public void withdraw(double value) {
        amount -= value;
    }

    @Override
    public String toString() {
        return "CID: " + cid + ", Name: " + cname + ", Balance: Rs. " + String.format("%.2f", amount);
    }
}

public class assignment4 {
    private static final int MIN_CID = 1;
    private static final int MAX_CID = 20;
    private static final double MIN_OPENING_BALANCE = 1000.0;
    private static final String FILE_NAME = "Assignment_4/customers.txt";

    private final Map<Integer, Customer> customers = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        assignment4 app = new assignment4();
        app.loadFromFile();
        app.runMenu();
    }

    private void runMenu() {
        int choice;
        do {
            printMenu();
            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    depositAmount();
                    break;
                case 3:
                    withdrawAmount();
                    break;
                case 4:
                    displayCustomers();
                    break;
                case 5:
                    saveToFile();
                    System.out.println("Data saved. Exiting program...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);

        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n===== Banking Menu =====");
        System.out.println("1. Create Account");
        System.out.println("2. Deposit Amount");
        System.out.println("3. Withdraw Amount");
        System.out.println("4. Display All Customers");
        System.out.println("5. Save and Exit");
    }

    private void createAccount() {
        try {
            int cid = readInt("Enter Customer ID (1-20): ");
            validateCid(cid);

            if (customers.containsKey(cid)) {
                System.out.println("Account with this CID already exists.");
                return;
            }

            System.out.print("Enter Customer Name: ");
            String cname = scanner.nextLine().trim();
            if (cname.isEmpty()) {
                System.out.println("Customer name cannot be empty.");
                return;
            }

            double amount = readDouble("Enter Opening Amount: ");
            validatePositiveAmount(amount);
            validateMinimumOpeningBalance(amount);

            customers.put(cid, new Customer(cid, cname, amount));
            saveToFile();
            System.out.println("Account created successfully.");
        } catch (InvalidCidException | InvalidAmountException | MinimumBalanceException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void depositAmount() {
        try {
            int cid = readInt("Enter Customer ID: ");
            validateCid(cid);
            Customer customer = customers.get(cid);

            if (customer == null) {
                System.out.println("No account found for this CID.");
                return;
            }

            double amount = readDouble("Enter Deposit Amount: ");
            validatePositiveAmount(amount);

            customer.deposit(amount);
            saveToFile();
            System.out.println("Deposit successful. Updated balance: Rs. " + String.format("%.2f", customer.getAmount()));
        } catch (InvalidCidException | InvalidAmountException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void withdrawAmount() {
        try {
            int cid = readInt("Enter Customer ID: ");
            validateCid(cid);
            Customer customer = customers.get(cid);

            if (customer == null) {
                System.out.println("No account found for this CID.");
                return;
            }

            double amount = readDouble("Enter Withdraw Amount: ");
            validatePositiveAmount(amount);
            validateWithdrawal(customer.getAmount(), amount);

            customer.withdraw(amount);
            saveToFile();
            System.out.println("Withdrawal successful. Updated balance: Rs. " + String.format("%.2f", customer.getAmount()));
        } catch (InvalidCidException | InvalidAmountException | InsufficientBalanceException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void displayCustomers() {
        if (customers.isEmpty()) {
            System.out.println("No customer records found.");
            return;
        }

        System.out.println("\n===== Customer Records =====");
        for (Customer customer : customers.values()) {
            System.out.println(customer);
        }
    }

    private void validateCid(int cid) throws InvalidCidException {
        if (cid < MIN_CID || cid > MAX_CID) {
            throw new InvalidCidException("CID must be in range " + MIN_CID + " to " + MAX_CID + ".");
        }
    }

    private void validatePositiveAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Entered amount should be positive.");
        }
    }

    private void validateMinimumOpeningBalance(double amount) throws MinimumBalanceException {
        if (amount < MIN_OPENING_BALANCE) {
            throw new MinimumBalanceException("Account should be created with minimum Rs. 1000.");
        }
    }

    private void validateWithdrawal(double totalAmount, double withdrawalAmount) throws InsufficientBalanceException {
        if (withdrawalAmount > totalAmount) {
            throw new InsufficientBalanceException("Withdrawal amount cannot exceed total amount.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException ex) {
                System.out.println("Please enter a valid integer.");
                scanner.nextLine();
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = scanner.nextDouble();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException ex) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length != 3) {
                    continue;
                }

                int cid = Integer.parseInt(parts[0].trim());
                String cname = parts[1].trim();
                double amount = Double.parseDouble(parts[2].trim());

                if (cid >= MIN_CID && cid <= MAX_CID && amount > 0) {
                    customers.put(cid, new Customer(cid, cname, amount));
                }
            }
        } catch (IOException | NumberFormatException ex) {
            System.out.println("Could not read file data: " + ex.getMessage());
        }
    }

    private void saveToFile() {
        File parent = new File(FILE_NAME).getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Customer customer : customers.values()) {
                writer.write(customer.getCid() + "," + customer.getCname() + "," + customer.getAmount());
                writer.newLine();
            }
        } catch (IOException ex) {
            System.out.println("Could not save records to file: " + ex.getMessage());
        }
    }
}
