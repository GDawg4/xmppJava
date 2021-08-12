
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jetbrains.annotations.NotNull;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.util.Collection;

class Main{
    static boolean login(@NotNull XMPPConnection con, String userName, String password){
        try{
            con.login("rodrigo", "redes");
            return true;
        }catch(Exception error){
            return false;
        }
    }
    static void logout(@NotNull XMPPConnection con){
        con.disconnect();
    }
    static boolean sendMessage(XMPPConnection con, String user, String message){
        ChatManager c = con.getChatManager();
        Chat chat = c.createChat(user, null);
        registerAccount(con);
        c.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean create) {
                chat.addMessageListener(new MessageListener() {
                    public void processMessage(Chat chat, Message msg) {
                        System.out.println(chat.toString());
                        System.out.println(msg.toString());
                        System.out.println(chat.getParticipant() + ":" + msg.getBody());
                    }
                });
            }
        });
        try {
            chat.sendMessage(message);
            return true;
        }catch (Exception e){
            System.out.println(e.toString());
            return false;
        }
    }
    static void getAllXmppUsers(XMPPConnection con){
        try {
            UserSearchManager manager = new UserSearchManager(con);
            String searchFormString = "search." + con.getServiceName();
            System.out.println("***" + "SearchForm: " + searchFormString);
            Form searchForm = null;

            searchForm = manager.getSearchForm(searchFormString);

            Form answerForm = searchForm.createAnswerForm();

            UserSearch userSearch = new UserSearch();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", "*");
            ReportedData results = userSearch.sendSearchForm(con, answerForm, searchFormString);
            if (results != null) {
                List<Row> actualList = new ArrayList<Row>();
                results.getRows().forEachRemaining(actualList::add);
                for (Row row: actualList ) {
                    System.out.println("Usuario: " + row.getValues("JID").toString());
                }
            } else {
                System.out.println("no result found");
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    static void registerAccount(XMPPConnection connection){
        AccountManager accountManager = new AccountManager(connection);
        try{
            accountManager.createAccount("rodrigoRemote2", "redes");
            System.out.println("success");
        }catch (XMPPException e1){
            System.out.println(e1.toString());
        }
    }
    static boolean connect(XMPPConnection con){
        try{
            con.connect();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    static boolean showAllUsers(XMPPConnection con){
        Roster roster = con.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
            System.out.println(entry);
        }
        return true;
    }

    static boolean addToRoster(XMPPConnection con, String userName, String alias){
        Roster roster = con.getRoster();
        String[] groups = {"Friends"};
        try{
            roster.createEntry(userName, alias, groups);
            return true;
        }catch (Exception e){
            System.out.println("Roster error");
            System.out.println(e.toString());
            return false;
        }
    }

    public static void main(String args[]){
        boolean isLoggedIn = false;
        Scanner myObj = new Scanner(System.in);
        String input;
        Scanner loggedInScanner = new Scanner(System.in);
        String loggedInInput;
        XMPPConnection con = new XMPPConnection("alumchat.xyz");
        if (connect(con)){
            while (true) {
                if (!isLoggedIn) {
                    do {
                        System.out.println("Opciones:");
                        System.out.println("1. Registrar nueva cuenta");
                        System.out.println("2. Iniciar sesión");
                        System.out.println("3. Salir");
                        System.out.println("Ingrese su opción");
                        input = myObj.nextLine();
                        switch (input) {
                            case "1":
                                Scanner userNameScanner = new Scanner(System.in);
                                String userNameInput = userNameScanner.nextLine();
                                Scanner passwordScanner = new Scanner(System.in);
                                String passwordInput = passwordScanner.nextLine();
                                break;
                            case "2":
                                System.out.println("Ingrese su nombre de usuario");
                                Scanner userNameLoginScanner = new Scanner(System.in);
                                String userNameLoginInput = userNameLoginScanner.nextLine();
                                System.out.println("Ingrese su contraseña");
                                Scanner passwordLoginScanner = new Scanner(System.in);
                                String passwordLoginInput = passwordLoginScanner.nextLine();
                                if (login(con, userNameLoginInput, passwordLoginInput)) {
                                    System.out.println("Successfully logged in");
                                    isLoggedIn = true;
                                    input = "3";
                                }
                                break;

                        }
                    } while (!input.equals("3"));

                } else {
                    do {
                        System.out.println("Opciones:");
                        System.out.println("1. Mostrar todos los usuarios");
                        System.out.println("2. Agregar un contacto");
                        System.out.println("3. Mostrar detalles");
                        System.out.println("4. Comunicación 1 a 1");
                        System.out.println("5. Comunicación grupal");
                        System.out.println("6. Salir");
                        System.out.println("Ingrese su opción");
                        loggedInInput = myObj.nextLine();
                        switch (loggedInInput) {
                            case "1":
                                showAllUsers(con);
                                break;
                            case "2":
                                System.out.println("Ingrese el nombre de usuario a agregar (usuario@dominio.xyz)");
                                Scanner userNameScanner = new Scanner(System.in);
                                String userName = userNameScanner.nextLine();
                                System.out.println("Ingrese el alias del usuario");
                                Scanner aliasScanner = new Scanner(System.in);
                                String alias = aliasScanner.nextLine();
                                if (addToRoster(con, userName, alias)){
                                    System.out.println("Contacto agregado");
                                }else {
                                    System.out.println("Error, favor intentar nuevamente");
                                }
                                break;
                            case "3":
                                System.out.println("Show users");
                            case "4":
                                System.out.println("Ingrese el nombre de usuario a agregar (usuario@dominio.xyz)");
                                Scanner userMessageScanner = new Scanner(System.in);
                                String userMessage = userMessageScanner.nextLine();
                                System.out.println("Ingrese el alias del usuario");
                                Scanner messageScanner = new Scanner(System.in);
                                String message = messageScanner.nextLine();
                                if (sendMessage(con, userMessage, message)){
                                    System.out.println("El mensaje fue enviado correctamente");
                                }else{
                                    System.out.println("Error, favor intentar nuevamente");
                                }
                        }
                    } while (!loggedInInput.equals("6"));
                }
            }
        }

/*        try{
            if (login(con, "rodrigo", "redes")) {
                ChatManager c = con.getChatManager();
                Chat chat = c.createChat("echobot@alumchat.xyz", null);
                registerAccount(con);
                c.addChatListener(new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean create) {
                        chat.addMessageListener(new MessageListener() {
                            public void processMessage(Chat chat, Message msg) {
                                System.out.println(chat.toString());
                                System.out.println(msg.toString());
                                System.out.println(chat.getParticipant() + ":" + msg.getBody());
                            }
                        });
                    }
                });
//                chat.sendMessage("Buenas noches");
                System.out.println("done");
                while (true) ;
            }else{
                System.out.println("falló");
            }
        }catch (Exception error){
            System.out.println(error);
        }*/
    }
}
