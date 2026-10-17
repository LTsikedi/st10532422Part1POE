package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessageTest {

    // Test data from the assignment
    static final String RECIPIENT_1 = "+27718693002";
    static final String MESSAGE_1 = "Hi Mike, can you join us for dinner tonight?";
    static final String RECIPIENT_2 = "08575975889";
    static final String MESSAGE_2 = "Hi Keegan, did you receive the payment?";

    @BeforeEach
    void setUp() {
        Message.reset();
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(Path.of(Message.JSON_FILE));
    }

    // ---- The message should not be more than 250 characters ----

    @Test
    void messageLengthSuccess() {
        assertEquals("Message ready to send.", Message.checkMessageLength(MESSAGE_1));
    }

    @Test
    void messageLengthFailure() {
        String tooLong = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength(tooLong));
    }

    @Test
    void messageLengthExactly250IsAllowed() {
        assertEquals("Message ready to send.", Message.checkMessageLength("a".repeat(250)));
    }

    @Test
    void messageLength251IsOverByOne() {
        assertEquals("Message exceeds 250 characters by 1; please reduce the size.",
                Message.checkMessageLength("a".repeat(251)));
    }

    // ---- The recipient number is correctly formatted ----

    @Test
    void recipientNumberSuccess() {
        assertEquals("Cell phone number successfully captured.",
                Message.checkRecipientCell(RECIPIENT_1));
    }

    @Test
    void recipientNumberFailure() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                Message.checkRecipientCell(RECIPIENT_2));
    }

    @Test
    void recipientNumberTooLongFails() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                Message.checkRecipientCell("+271234567890123"));
    }

    @Test
    void recipientNumberNullFails() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                Message.checkRecipientCell(null));
    }

    @Test
    void recipientCheckWorksOnAMessageObject() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1);
        assertEquals("Cell phone number successfully captured.", message.checkRecipientCell());
    }

    // ---- Message hash is correct ----

    @Test
    void messageHashTestCase1() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    void messageHashesInALoop() {
        String[] ids = {"0012345678", "0198765432", "5543210987"};
        int[] numbers = {0, 1, 2};
        String[] texts = {MESSAGE_1, MESSAGE_2, "Thanks"};
        String[] expected = {"00:0:HITONIGHT", "01:1:HIPAYMENT", "55:2:THANKSTHANKS"};

        for (int i = 0; i < ids.length; i++) {
            Message message = new Message(ids[i], numbers[i], RECIPIENT_1, texts[i]);
            assertEquals(expected[i], message.createMessageHash());
        }
    }

    @Test
    void messageNumberGoesUpAfterASend() {
        Message first = new Message(RECIPIENT_1, MESSAGE_1);
        assertEquals(0, first.getMessageNumber());
        first.sentMessage(1);

        Message second = new Message(RECIPIENT_1, MESSAGE_2);
        assertEquals(1, second.getMessageNumber());
        assertTrue(second.getMessageHash().startsWith(second.getMessageID().substring(0, 2) + ":1:"));
        assertTrue(second.getMessageHash().endsWith("HIPAYMENT"));
    }

    // ---- Message ID is created ----

    @Test
    void messageIdIsCreated() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1);

        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageID().length());
        assertEquals("Message ID generated: " + message.getMessageID(), message.messageIDGenerated());
    }

    @Test
    void messageIdIsTenDigits() {
        for (int i = 0; i < 50; i++) {
            Message message = new Message(RECIPIENT_1, MESSAGE_1);
            assertTrue(message.getMessageID().matches("\\d{10}"));
        }
    }

    @Test
    void messageIdLongerThanTenCharactersFailsTheCheck() {
        Message message = new Message("12345678901", 0, RECIPIENT_1, MESSAGE_1);
        assertFalse(message.checkMessageID());
    }

    // ---- MessageSent ----

    @Test
    void messageSentSend() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1);
        assertEquals("Message successfully sent.", message.sentMessage(1));
    }

    @Test
    void messageSentDisregard() {
        Message message = new Message(RECIPIENT_2, MESSAGE_2);
        assertEquals("Press 0 to delete the message.", message.sentMessage(2));
    }

    @Test
    void messageSentStore() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1);
        assertEquals("Message successfully stored.", message.sentMessage(3));
    }

    @Test
    void messageSentInvalidChoice() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1);
        assertEquals("Invalid option.", message.sentMessage(9));
    }

    @Test
    void messageSentUsingTheMenuKeepsAskingUntilValid() {
        Message message = new Message(RECIPIENT_1, MESSAGE_1);
        Scanner input = new Scanner(new ByteArrayInputStream("x\n7\n1\n".getBytes()));
        assertEquals("Message successfully sent.", message.sentMessage(input));
    }

    // ---- Totals and printing ----

    // Test data: 2 messages, the first is sent and the second is discarded
    @Test
    void totalMessagesSent() {
        Message first = new Message(RECIPIENT_1, MESSAGE_1);
        first.sentMessage(1);

        Message second = new Message(RECIPIENT_2, MESSAGE_2);
        second.sentMessage(2);

        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void totalMessagesSentWhenAllAreSent() {
        for (int i = 0; i < 3; i++) {
            new Message(RECIPIENT_1, MESSAGE_1).sentMessage(1);
        }
        assertEquals(3, Message.returnTotalMessages());
    }

    @Test
    void storedMessagesAreNotCountedAsSent() {
        new Message(RECIPIENT_1, MESSAGE_1).sentMessage(3);
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    void printMessagesListsOnlySentMessages() {
        Message first = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        first.sentMessage(1);
        Message second = new Message("0198765432", 1, RECIPIENT_1, MESSAGE_2);
        second.sentMessage(2);

        String printed = Message.printMessages();
        assertTrue(printed.contains("Message ID: 0012345678"));
        assertTrue(printed.contains("Message Hash: 00:0:HITONIGHT"));
        assertTrue(printed.contains("Recipient: " + RECIPIENT_1));
        assertTrue(printed.contains("Message: " + MESSAGE_1));
        assertFalse(printed.contains(MESSAGE_2));
    }

    @Test
    void detailsAreInTheRequiredOrder() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        String expected = "Message ID: 0012345678\n"
                + "Message Hash: 00:0:HITONIGHT\n"
                + "Recipient: +27718693002\n"
                + "Message: " + MESSAGE_1 + "\n";
        assertEquals(expected, message.getDetails());
    }

    // ---- Store message in a JSON file ----

    @Test
    void storedMessageIsWrittenToJsonFile() throws IOException {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        message.sentMessage(3);

        String json = Files.readString(Path.of(Message.JSON_FILE));
        assertTrue(json.startsWith("["));
        assertTrue(json.contains("\"messageID\": \"0012345678\""));
        assertTrue(json.contains("\"messageHash\": \"00:0:HITONIGHT\""));
        assertTrue(json.contains("\"recipient\": \"+27718693002\""));
        assertTrue(json.contains("\"message\": \"" + MESSAGE_1 + "\""));
    }

    @Test
    void severalStoredMessagesAreAllInTheJsonFile() throws IOException {
        new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1).sentMessage(3);
        new Message("0198765432", 1, RECIPIENT_1, MESSAGE_2).sentMessage(3);

        String json = Files.readString(Path.of(Message.JSON_FILE));
        assertTrue(json.contains("0012345678"));
        assertTrue(json.contains("0198765432"));
    }

    @Test
    void quotesInAMessageAreEscapedInJson() throws IOException {
        Message message = new Message("0012345678", 0, RECIPIENT_1, "Say \"hi\" now");
        message.sentMessage(3);

        String json = Files.readString(Path.of(Message.JSON_FILE));
        assertTrue(json.contains("Say \\\"hi\\\" now"));
    }
}
