package client;

import utils.ChatHistory;
import utils.FileSerialized;
import utils.Message;
import utils.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.*;

class ClientModelTest {


    private SplittableRandom rnd;
    private User sender_kalle;
    private User reveicer_Anders;
    private ClientModel clientModel;
    private ClientModel clientModel2;
    private ChatHistory chatHistory;
    private ChatHistory chatHistory2;
    private ArrayList<User> userList;
    private FileSerialized file;
    private Message message;
    @org.junit.jupiter.api.BeforeEach
    void setUp() throws IOException {
        this.rnd = new SplittableRandom(1);
        clientModel = new ClientModel();
        clientModel2 = new ClientModel();
        sender_kalle = new User("Kalle");
        reveicer_Anders = new User("Anders");
        chatHistory = new ChatHistory(new User("Kalle"));
        chatHistory2 = new ChatHistory(new User("Kalle"));
        userList = new ArrayList<>();
        file = new FileSerialized("src/123.png");   //kräver att det ligger en bild i src som heter 123.png
        //Vi skapar ett meddelande från kalle till Anders, det kommer sparas i Anders namn. Det innehåller allt som ett meddelande kan innehålla.
        message = Message.builder()
                .messageType(Message.MessageType.REGULAR_MESSAGE)
                .sender(sender_kalle)
                .receiver(reveicer_Anders)
                .text("tjenare")
                .file(file)
                .onlineUsers(userList)
                .chatHistory(chatHistory2)
                .build();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    public void testUpdateUserList() {
        ClientModel model = new ClientModel();
        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("Alice"));
        userList.add(new User("Bob"));
        userList.add(new User("Charlie"));
        model.updateUserList(userList);
        ArrayList<User> updatedList = model.getUserList();

        //Test om en lista med 3 namn skickas in
        assertEquals(updatedList.size(), 3);
        assertEquals(updatedList.get(0).getName(), "Alice");
        assertEquals(updatedList.get(1).getName(), "Bob");
        assertEquals(updatedList.get(2).getName(), "Charlie");

        //TEST om en tom lista skickas in och ersätter den gamla.
        ArrayList<User> userList2 = new ArrayList<>();
        model.updateUserList(userList2);
        ArrayList<User> updatedList2 = model.getUserList();
        assertEquals(updatedList2.size(), 0);
        assertNotEquals(updatedList2.size(), 1);

        //Test med siffror som namn, ska vara OK. Ersätter den tomma listan
        userList2.add(new User("12345"));
        model.updateUserList(userList2);
        ArrayList<User> updatedList3 = model.getUserList();
        assertEquals(updatedList3.size(), 1);
        assertEquals(updatedList3.get(0).getName(), "12345");

    }

    @org.junit.jupiter.api.Test
    void updateChatHistory() {

        chatHistory.addHistory(sender_kalle, reveicer_Anders,message);
        clientModel.updateChatHistory(chatHistory);

        //test om clientmodel innehållar samma lista av meddelanden som chathistory innehåller.
        assertEquals(chatHistory.getHistory(reveicer_Anders), clientModel.getHistory(reveicer_Anders));

        //Test om meddelandet innehåller samma komponenter
        ArrayList<Message> messagelist = clientModel.getHistory(reveicer_Anders);

        assertEquals(Message.MessageType.REGULAR_MESSAGE,messagelist.get(0).getMessageType());
        assertEquals(sender_kalle,messagelist.get(0).getSender());
        assertEquals(reveicer_Anders,messagelist.get(0).getReceiver());
        assertEquals("tjenare",messagelist.get(0).getText());
        assertEquals(file,messagelist.get(0).getFile());
        assertEquals(userList,messagelist.get(0).getOnlineUsers());
        assertEquals(chatHistory2,messagelist.get(0).getChatHistory());

        assertNotEquals("hejsan",messagelist.get(0).getText());
        assertNotEquals(2,messagelist.size());

    }




    @org.junit.jupiter.api.Test
    void getUser() {
        User user1;
        User user2 = new User("Randomname");

        ClientModel model1 = new ClientModel();

        //Först kollar vi att getUserName kastar exeption om user = null
        assertThrows(NullPointerException.class, () -> model1.getUser());

        for(int i = 0; i < 100; i++){
            //testar en rad av olika symboler som input.
            user1 = new User(": \uD83D\uDE00 \u00A9 \u20AC \u260E" + (char) ('A' + rnd.nextInt(26)) + rnd.nextInt() + "!@#$%^&()-+=[]{}|\\\\/:;\\\"<>,.?_~åäö¡¿´");



            Thread trad = new Thread(()-> {   //Inte den snyggaste lösningen, men det fungerar.
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                model1.setValidated(Message.VALIDATE.ALLOWED); //behövs för att checkuser ska komma vidare. Användarens namn måste valideras.
                // Nu antar vi att alla namn är godkända bara för att se att de kommer in korrekt i modelen och
                //sedan kan plockas ut. I vanliga fall skulle servern tillfrågas om namnet redan är inloggat.
            });
            trad.start();

            model1.checkUser(user1.getName());  //denna metod sätter användarnamn i modellen om den inte redan har ett.

            assertEquals(user1,model1.getUser());
            assertNotEquals(user2,model1.getUser());

        }

    }

    @org.junit.jupiter.api.Test
    void getUserList() {
        ClientModel model1 = new ClientModel();

        ArrayList<User> userList1 = new ArrayList<>();
        StringBuilder name;
        for(int i = 0; i < 100; i++){  //Lägger till 100 namn
            name = new StringBuilder(); //Skapar namn med random 5 random bokstäver och ett tal.
            for(int j = 0; j < 6; j++){
                name.append((char) ('A' + rnd.nextInt(26)));

            }
            userList1.add(new User(name.toString()));
        }

        model1.updateUserList(userList1);
        //Kolla så att listan inte blivit muterad.
        assertEquals(userList1, model1.getUserList());
        //Kolla så att den inte är samma som en annan lista
        ArrayList<User> userList2 = new ArrayList<>();
        assertNotEquals(userList2, model1.getUserList());

    }

    @org.junit.jupiter.api.Test
    void addLocalHistory(){
        clientModel.updateChatHistory(chatHistory); //Lägg till en tom chathistory i modellen.
        clientModel.addLocalHistory(message); //Lägg till medelandet i den ANDERS chathistorik, då anders är receiver för message

        assertEquals(chatHistory.getHistory(sender_kalle), clientModel.getHistory(sender_kalle)); //KOLLA SÅ ATT Kalles chathistorik är tom.

        assertEquals(chatHistory.getHistory(reveicer_Anders), clientModel.getHistory(reveicer_Anders));  //testar att lsitan är samma för anders chathistorik
        assertNotEquals(chatHistory.getHistory(sender_kalle), clientModel.getHistory(reveicer_Anders));//testar att anders chathisorik INTE samma som kalles.
    }

    @org.junit.jupiter.api.Test
    void getHistory() {

        clientModel.updateChatHistory(chatHistory); //Lägg till en tom chathistorik med User Kalle.

        //Kollar så att den returnerar rätt chathistorik
        assertEquals(chatHistory.getHistory(reveicer_Anders)  ,clientModel.getHistory(reveicer_Anders));

        // Kollar så att getHistory returnerar en tom list ifall ingen historik existerar.
        ArrayList<Message> empty = new ArrayList<>();
        assertEquals(empty  ,clientModel.getHistory(sender_kalle));

    }

    @org.junit.jupiter.api.Test
    void checkUser() {
        ClientModel model = new ClientModel();
        model.checkUser("");

        //Kolla så att en user inte skapats, dvs den är null
        assertThrows(NullPointerException.class,() -> {model.getUser();});
        //Kolla så att en chatHistory inte skapats, dvs den är null
        assertThrows(NullPointerException.class,() -> {model.getHistory(reveicer_Anders);});


        Thread trad = new Thread(()-> {   //Inte den snyggaste lösningen, men det fungerar.
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            model.setValidated(Message.VALIDATE.ALLOWED); //behövs för att checkuser ska komma vidare. Användarens namn måste valideras.
            // Nu antar vi att alla namn är godkända bara för att se att de kommer in korrekt i modelen och
            //sedan kan plockas ut. I vanliga fall skulle servern tillfrågas om namnet redan är inloggat.
        });
        trad.start();
        //Nu borde en användare skapas med namn David
        model.checkUser("David");
        ChatHistory chatHistoryDavid = new ChatHistory(model.getUser());
        //Testa så att usernamenet har satts till David
        assertEquals("David",model.getUser().getName());
        //Testa så att den returnerar en chathistory för David
        assertEquals(chatHistoryDavid.getHistory(model.getUser()),model.getHistory(model.getUser()));

    }

    @org.junit.jupiter.api.Test
    void setValidated() {
        assertThrows(NullPointerException.class, () -> {
            clientModel.setValidated(null);
        });

        clientModel.setValidated(Message.VALIDATE.ALLOWED);

        assertEquals(clientModel.getServerStatus(), Message.VALIDATE.ALLOWED);
        assertTrue(clientModel.loggedIn());

        clientModel2.setValidated(Message.VALIDATE.DENIED);

        assertEquals(clientModel2.getServerStatus(), Message.VALIDATE.DENIED);
        assertFalse(clientModel2.loggedIn());

        clientModel2.setValidated(Message.VALIDATE.NETWORK_ERROR);

        assertEquals(clientModel2.getServerStatus(), Message.VALIDATE.NETWORK_ERROR);
        assertFalse(clientModel2.loggedIn());


    }

    @org.junit.jupiter.api.Test
    void set_serverstatus() {
        clientModel.setServerStatus(Message.VALIDATE.ALLOWED);

        assertEquals(Message.VALIDATE.ALLOWED, clientModel.getServerStatus());

        clientModel.setServerStatus(Message.VALIDATE.DENIED);

        assertEquals(Message.VALIDATE.DENIED, clientModel.getServerStatus());

        clientModel.setServerStatus(Message.VALIDATE.NETWORK_ERROR);

        assertEquals(Message.VALIDATE.NETWORK_ERROR, clientModel.getServerStatus());


        assertThrows(NullPointerException.class, ()->{
            clientModel.setServerStatus(null);});

    }

    @org.junit.jupiter.api.Test
    void get_serverstatus() {

        //Kollar så att getServerstatus kastar om serverOnline är null
        ClientModel clientmodel3 = new ClientModel(); //Create a new model with variabel serverOnline  = null. Getserverstatus will then throw if serveronline = null
        assertThrows(NullPointerException.class, ()->{clientmodel3.getServerStatus();});

        clientmodel3.setServerStatus(Message.VALIDATE.ALLOWED);

        assertEquals(Message.VALIDATE.ALLOWED, clientmodel3.getServerStatus());

        clientmodel3.setServerStatus(Message.VALIDATE.DENIED);

        assertEquals(Message.VALIDATE.DENIED, clientmodel3.getServerStatus());

        clientmodel3.setServerStatus(Message.VALIDATE.NETWORK_ERROR);

        assertEquals(Message.VALIDATE.NETWORK_ERROR, clientmodel3.getServerStatus());
    }
    @org.junit.jupiter.api.Test
    void loggedIn() {
        clientModel.setValidated(Message.VALIDATE.ALLOWED);
        assertTrue(clientModel.loggedIn());

        clientModel2.setValidated(Message.VALIDATE.DENIED);
        assertFalse(clientModel2.loggedIn());
    }


}



