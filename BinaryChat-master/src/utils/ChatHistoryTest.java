package utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ChatHistoryTest {

    User testUser1;
    User testUser2;
    Message testMessage;
    Message testMessage2;

    /**
     * Sets up two users and two messages
     */

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        testUser1 = new User("testReceiver");
        testUser2 = new User("testSender");

        testMessage = Message.builder()
                .sender(testUser2)
                .receiver(testUser1)
                .text("testText")
                .messageType(Message.MessageType.REGULAR_MESSAGE)
                .build();

        testMessage2 = Message.builder()
                .sender(testUser1)
                .receiver(testUser2)
                .text("testText")
                .messageType(Message.MessageType.REGULAR_MESSAGE)
                .build();
    }

    /**
     * Tests if getHistory returns an empty array if there hasn't been any communication between testUser1 and testUser2 yet
     */

    @Test
    void getHistoryTestReturnsEmptyArrayIfNull() {
        var history = new ChatHistory(testUser2);

        ArrayList<Message> actual = history.getHistory(testUser1);

        var expected = new ArrayList<Message>();

        assertEquals(expected, actual);

        assertEquals(0, actual.size());

    }

    /**
     * Tests if getHistory returns an array with the correct history
     */

    @Test
    void getHistoryTestReturnsCorrectIfNotNull() {
        var history = new ChatHistory(testUser2);
        history.addHistory(testMessage.getSender(), testMessage.getReceiver(), testMessage);

        ArrayList<Message> actual = history.getHistory(testUser1);

        var expected = new ArrayList<Message>();
        expected.add(testMessage);

        assertEquals(expected, actual);

        assertEquals(1, actual.size());

    }


    /**
     * Tests if addHistory works as intended if testUser1 adds a message sent to and one message received from testUser2 to its history
     */

    @Test
    void addHistorySentMessage() {
        var history = new ChatHistory(testUser1);
        assertEquals(0, history.getHistory(testUser2).size());

        //testUser2 -> testUser1
        history.addHistory(testMessage.getSender(), testMessage.getReceiver(), testMessage);

        assertEquals(1, history.getHistory(testUser2).size());

        //testUser1 -> testUser2
        history.addHistory(testMessage2.getSender(), testMessage2.getReceiver(), testMessage2);

        assertEquals(2, history.getHistory(testUser2).size());
    }

}

