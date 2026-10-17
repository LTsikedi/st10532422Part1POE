package com.mycompany.main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

class Login {

    static String storedUser;
    static String storedPassword;

    public static boolean checkUsername(String username) {
        boolean hasUnderScore = false;
        boolean lessThanFive = false;

        if (username.length() < 5) {
            lessThanFive = true;
        }

        if (username.contains("_")) {
            hasUnderScore = true;
        }

        return hasUnderScore && lessThanFive;
    }

    public static boolean checkPasswordComplexity(String password) {
        boolean hasDigit = false;
        boolean hasSpecial = false;
        boolean hasCapital = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasCapital = true;
            }

            if (Character.isDigit(c)) {
                hasDigit = true;
            }

            if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        return hasSpecial && hasDigit && hasCapital;
    }

    public static boolean checkCellPhoneNumber(String number) {
        boolean isValid = false;
        boolean hasCode = false;

        if (number.length() == 12) {
            isValid = true;
        }

        if (number.startsWith("+27")) {
            hasCode = true;
        }

        return hasCode && isValid;
    }

    public static void registerUser() {
        Scanner input = new Scanner(System.in);

        System.out.println("Please Create Username");
        String user = input.nextLine();

        System.out.println("Please Create a Password");
        String password = input.nextLine();

        System.out.println("Please Enter CellPhone Number");
        String number = input.nextLine();

        while (!checkUsername(user)) {
            System.out.println("Username invalid");

            System.out.println("Please Re-create Username");
            user = input.nextLine();
        }

        while (!checkPasswordComplexity(password)) {
            System.out.println("Password invalid");

            System.out.println("Please Re-create a Password");
            password = input.nextLine();
        }

        while (!checkCellPhoneNumber(number)) {
            System.out.println("Cellphone Number invalid");

            System.out.println("Please Enter a Valid CellPhone Number");
            number = input.nextLine();
        }

        storedUser = user;
        storedPassword = password;
        System.out.println("You have been successfully been registered");
    }

    // Now returns true once the user has logged in successfully
    public static boolean loginUser() {
        boolean hasLoggedIn = false;

        System.out.println("Login to your Chat App");
        System.out.println("---------------------------------------------------");
        Scanner input = new Scanner(System.in);

        System.out.println("Please Enter Your Username");
        String user = input.nextLine();

        System.out.println("Please Enter Your Password");
        String enterPass = input.nextLine();

        do {
            if (user.equals(storedUser) && enterPass.equals(storedPassword)) {
                System.out.println("Welcome to Your Chatapp");
                hasLoggedIn = true;
            } else {
                System.out.println("Login unsuccessful");
                System.out.println("Please Enter Your Username");
                user = input.nextLine();
                System.out.println("Please Enter Your Password");
                enterPass = input.nextLine();
            }
        } while (!hasLoggedIn);

        return hasLoggedIn;
    }
}

class Message {

    public static final int MAX_LENGTH = 250;
    public static final String JSON_FILE = "messages.json";

    // Shared by every message while the program is running
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static int numMessagesSent = 0;

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String text;
    private final String messageHash;

    // Used by the application: the ID is random and the message number
    // is the count of messages sent so far (starts at 0, goes up as messages are sent)
    public Message(String recipient, String text) {
        this(generateMessageID(), numMessagesSent, recipient, text);
    }

    // Lets the unit tests supply a known ID and message number
    Message(String messageID, int messageNumber, String recipient, String text) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash();
    }

    // Random ten-digit number (leading zeros allowed, e.g. 0012345678)
    private static String generateMessageID() {
        long id = ThreadLocalRandom.current().nextLong(10_000_000_000L);
        return String.format("%010d", id);
    }

    // Boolean: checkMessageID() - the message ID must be no more than ten characters
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    // String: checkRecipientCell() - checks this message's recipient number
    public String checkRecipientCell() {
        return checkRecipientCell(recipient);
    }

    // Same check for a number that has not been attached to a message yet.
    // Must start with an international code (+) followed by the number;
    // the part after the country code may be no more than ten digits.
    public static String checkRecipientCell(String cell) {
        if (cell != null && cell.matches("\\+\\d{1,3}\\d{1,10}")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    // Message must not exceed 250 characters
    public static String checkMessageLength(String message) {
        if (message.length() <= MAX_LENGTH) {
            return "Message ready to send.";
        }
        int over = message.length() - MAX_LENGTH;
        return "Message exceeds 250 characters by " + over + "; please reduce the size.";
    }

    // String: createMessageHash() - first two digits of ID : message number : FIRSTLAST (all caps)
    public String createMessageHash() {
        String[] words = text.trim().split("\\s+");
        String firstWord = cleanWord(words[0]);
        String lastWord = cleanWord(words[words.length - 1]);
        String firstTwo = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        return (firstTwo + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    private static String cleanWord(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    public String messageIDGenerated() {
        return "Message ID generated: " + messageID;
    }

    // String: SentMessage() - the user chooses to send, disregard or store the message
    public String sentMessage(java.util.Scanner input) {
        System.out.println("Please choose an option:");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");

        int choice = 0;
        while (choice < 1 || choice > 3) {
            System.out.print("Enter option (1-3): ");
            try {
                choice = Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                choice = 0;
            }
        }
        return sentMessage(choice);
    }

    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                numMessagesSent++;
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                return storeMessage();
            default:
                return "Invalid option.";
        }
    }

    // Store the message in a JSON file (the whole stored list is rewritten each time)
    public String storeMessage() {
        storedMessages.add(this);
        try (FileWriter writer = new FileWriter(JSON_FILE)) {
            writer.write(toJsonArray(storedMessages));
        } catch (IOException e) {
            return "Message could not be stored: " + e.getMessage();
        }
        return "Message successfully stored.";
    }

    private static String toJsonArray(List<Message> messages) {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < messages.size(); i++) {
            json.append(messages.get(i).toJson());
            if (i < messages.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        return json.append("]\n").toString();
    }

    private String toJson() {
        return "  {\n"
                + "    \"messageID\": \"" + escape(messageID) + "\",\n"
                + "    \"messageNumber\": " + messageNumber + ",\n"
                + "    \"messageHash\": \"" + escape(messageHash) + "\",\n"
                + "    \"recipient\": \"" + escape(recipient) + "\",\n"
                + "    \"message\": \"" + escape(text) + "\"\n"
                + "  }";
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Message ID, Message Hash, Recipient, Message - in that order
    public String getDetails() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + text + "\n";
    }

    // String: printMessages() - every message sent while the program is running
    public static String printMessages() {
        StringBuilder all = new StringBuilder();
        for (Message m : sentMessages) {
            all.append(m.getDetails()).append("\n");
        }
        return all.toString();
    }

    // Int: returnTotalMessages() - total number of messages sent
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    // Clears the shared state (used by the unit tests)
    static void reset() {
        sentMessages.clear();
        storedMessages.clear();
        numMessagesSent = 0;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getText() {
        return text;
    }

    public int getMessageNumber() {
        return messageNumber;
    }
}

public class Main {

    // How many messages the user said they want to enter, and how many they have entered so far
    static int totalToEnter;
    static int entered;

    public static void main(String[] args) {

        Login.registerUser();

        // Messages can only be sent once the user has logged in successfully
        if (Login.loginUser()) {
            runQuickChat();
        }
    }

    static void runQuickChat() {
        Scanner input = new Scanner(System.in);

        System.out.println("Welcome to QuickChat.");

        totalToEnter = readInt(input, "How many messages do you wish to enter? ", 1, Integer.MAX_VALUE);
        entered = 0;

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("Please choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");

            int choice = readInt(input, "Enter option (1-3): ", 1, 3);

            switch (choice) {
                case 1:
                    sendMessages(input);
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    System.out.println("Goodbye.");
                    running = false;
                    break;
            }
        }
    }

    static void sendMessages(Scanner input) {
        if (entered >= totalToEnter) {
            System.out.println("You have already entered all " + totalToEnter + " of your messages.");
            return;
        }

        while (entered < totalToEnter) {
            System.out.println();
            System.out.println("Message " + (entered + 1) + " of " + totalToEnter);

            String recipient = readRecipient(input);
            String body = readMessageBody(input);

            Message message = new Message(recipient, body);
            String result = message.sentMessage(input);
            System.out.println(result);

            if (result.equals("Message successfully sent.")) {
                System.out.println();
                System.out.print(message.getDetails());
            } else if (result.equals("Press 0 to delete the message.")) {
                String answer = "";
                while (!answer.equals("0")) {
                    answer = input.nextLine().trim();
                }
                System.out.println("Message deleted.");
            }

            entered++;
        }

        System.out.println();
        System.out.println("Total number of messages sent: " + Message.returnTotalMessages());
    }

    static String readRecipient(Scanner input) {
        while (true) {
            System.out.print("Enter the recipient's cell number (with international code, e.g. +27718693002): ");
            String cell = input.nextLine().trim();
            String result = Message.checkRecipientCell(cell);
            System.out.println(result);
            if (result.equals("Cell phone number successfully captured.")) {
                return cell;
            }
        }
    }

    static String readMessageBody(Scanner input) {
        while (true) {
            System.out.print("Enter your message: ");
            String body = input.nextLine();

            if (body.trim().isEmpty()) {
                System.out.println("A message cannot be empty.");
            } else if (body.length() > Message.MAX_LENGTH) {
                System.out.println("Please enter a message of less than 250 characters.");
            } else {
                System.out.println("Message sent");
                return body;
            }
        }
    }

    static int readInt(Scanner input, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(input.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // fall through and ask again
            }
            System.out.println("Invalid input, please try again.");
        }
    }
}
