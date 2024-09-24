package utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    User anders;
    User sven;
    User alice;
    User bob;
    User charlie;

    Message msg;
    String message;


    @BeforeEach
    void setUp() {
        anders = new User("Anders");
        sven = new User("Sven");
        alice = new User("Alice");
        bob = new User("Bob");
        charlie = new User("Charlie");

        ChatHistory chatHistory = new ChatHistory(anders);
        ArrayList<User> userList = new ArrayList<>();
        userList.add(alice);
        userList.add(bob);
        userList.add(charlie);

        message = "Hello Anders, how are you?";
        msg = Message.builder()
                .sender(sven)
                .receiver(anders)
                .chatHistory(chatHistory)
                .onlineUsers(userList)
                .text(message)
                .messageType(Message.MessageType.REGULAR_MESSAGE)
                .build();


        chatHistory.addHistory(msg.getSender(), msg.getReceiver(), msg);
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void getTime() {
        Locale locale = new Locale("se", "SE");
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
        String expectedTime = dateFormat.format(new Date());

        String actualTime = msg.getTime();

        assertEquals(expectedTime, actualTime);
    }

    @Test
    void getSender() {
        assertEquals(sven, msg.getSender());
        assertNotEquals(anders, msg.getSender());
    }

    @Test
    void getReceiver() {
        assertEquals(anders, msg.getReceiver());
        assertNotEquals(sven, msg.getReceiver());
    }

    @Test
    void getText() {
        assertEquals(message, msg.getText());
        assertNotEquals("Input som inte borde finnas i meddelandet", msg.getText());
    }

    @Test
    void getFile() {
    }

    @Test
    void getMessageType() {
        assertEquals(Message.MessageType.REGULAR_MESSAGE, msg.getMessageType());
        assertNotEquals(Message.MessageType.CLIENT_INFO, msg.getMessageType());
    }


    @Test
    void getOnlineUsers() {
        ArrayList<User> msgUserList = msg.getOnlineUsers();

        //Checks if getOnlineUsers contains the correct users (alice, bob and charlie)
        assertEquals(3, msgUserList.size());
        assertTrue(msgUserList.contains(alice));
        assertTrue(msgUserList.contains(bob));
        assertTrue(msgUserList.contains(charlie));

        //Checks if the array contains any null value
        assertFalse(msgUserList.contains(null));

        //Checks on a user that should not be in the array
        assertFalse(msgUserList.contains(anders));

    }

    @Test
    void getChatHistory() {
        ChatHistory retrievedChatHistory = msg.getChatHistory();
        System.out.println("Retrieved chat history:");
        for (Message msg : retrievedChatHistory.getHistory(sven)) {
            assertEquals(message, msg.getText());
        }
    }

    @Test
    void testToString() {
        assertEquals(message, msg.getText());
        assertNotEquals("Input som inte borde finnas i meddelandet", msg.getText());
    }
}