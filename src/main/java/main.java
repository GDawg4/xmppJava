
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
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.AccountManager;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.util.Collection;

/**
 * Simple console based XMPP client
 */
class Main{
    //Declare functions to be used later to be used later
    /**
     * Login function
     * @param con active XMPPConnection
     * @param userName JID to login, must not include the domain name
     * @param password password to login
     * @return true if login was successful, false if otherwise
     */
    static boolean login(@NotNull XMPPConnection con, String userName, String password){
        try{
            con.login("rodrigo", "redes");
            return true;
        }catch(Exception error){
            return false;
        }
    }

    /**
     * Logout function
     * @param con active XMPPConnection
     * Does not return anything
     */
    static void logout(@NotNull XMPPConnection con){
        con.disconnect();
    }

    /**
     * Sends a message to a specified user
     * @param con active and logged-in XMPPConnection
     * @param user string of user to send the message, must include domain name (e.g.: name@domain.com)
     * @param message string of message to send to the specified user
     * @return true if message was successful, false if otherwise
     */
    static boolean sendMessage(XMPPConnection con, String user, String message){
        ChatManager c = con.getChatManager();
        Chat chat = c.createChat(user, null);
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

    /**
     * Creates new user
     * @param connection active XMPPConnection
     * @param userName string of new user, must be new in server
     * @param password string of password of new user
     * Does not return anything
     */
    static void registerAccount(XMPPConnection connection, String userName, String password){
        AccountManager accountManager = new AccountManager(connection);
        try{
            accountManager.createAccount(userName, password);
            System.out.println("Usuario creado exitosamente");
        }catch (XMPPException e1){
            System.out.println(e1.toString());
        }
    }

    /**
     * Connects to domain
     * @param con new XMPPConnection to connect
     * @return true if connection was successful, false if otherwise
     */
    static boolean connect(XMPPConnection con){
        try{
            con.connect();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Prints all contacts of logged-in user
     * @param con active and logged-in XMPPConnection
     * @return true if fetch was successful, false if otherwise
     */
    static boolean showAllUsers(XMPPConnection con){
        Roster roster = con.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
            System.out.println(entry);
        }
        return true;
    }

    /**
     * Adds user to "Friends" roster
     * @param con active and logged-in XMPPConnection
     * @param userName string of userName to be named
     * @param alias string of alias given do user
     * @return
     */
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

    /**
     * Deletes current account
     * @param con active and logged-in XMPPConnection
     * @return true if deletion was succesfull, false if otherwise
     */
    static boolean deleteAccount(XMPPConnection con){
        AccountManager accountManager = con.getAccountManager();
        try{
            accountManager.deleteAccount();
            return true;
        }catch (Exception e){
            System.out.println(e.toString());
            return false;
        }
    }

    /**
     * Main driver program
     */
    public static void main(String args[]){
        //To keep track if user is logged-in
        boolean isLoggedIn = false;
        Scanner myObj = new Scanner(System.in);
        String input;
        Scanner loggedInScanner = new Scanner(System.in);
        String loggedInInput;
        //Creates new user to domain
        XMPPConnection con = new XMPPConnection("alumchat.xyz");
        //Tries to connect
        if (connect(con)){
            //Inifinite loop to run program
            while (true) {
                if (!isLoggedIn) {
                    do {
                        //Shows menu if not logged-in
                        System.out.println("Opciones:");
                        System.out.println("1. Registrar nueva cuenta");
                        System.out.println("2. Iniciar sesión");
                        System.out.println("3. Salir");
                        System.out.println("Ingrese su opción");
                        //Read input
                        input = myObj.nextLine();
                        switch (input) {
                            //New account
                            case "1":
                                //Asks for username and password and calls methid
                                System.out.println("Ingrese el nuevo nombre de usuario");
                                Scanner userNameScanner = new Scanner(System.in);
                                String userNameInput = userNameScanner.nextLine();
                                System.out.println("Ingrese la nueva contraseña");
                                Scanner passwordScanner = new Scanner(System.in);
                                String passwordInput = passwordScanner.nextLine();
                                registerAccount(con, userNameInput, passwordInput);
                                break;
                            //Login
                            case "2":
                                //Asks for username and password and calls method
                                System.out.println("Ingrese su nombre de usuario");
                                Scanner userNameLoginScanner = new Scanner(System.in);
                                String userNameLoginInput = userNameLoginScanner.nextLine();
                                System.out.println("Ingrese su contraseña");
                                Scanner passwordLoginScanner = new Scanner(System.in);
                                String passwordLoginInput = passwordLoginScanner.nextLine();
                                //If successful, prints message and  and changes input to exit loop
                                if (login(con, userNameLoginInput, passwordLoginInput)) {
                                    System.out.println("Successfully logged in");
                                    isLoggedIn = true;
                                    input = "3";
                                }
                                break;

                        }
                    } while (!input.equals("3"));

                } else {
                    //Creates ChatManager to listen for messages
                    ChatManager chatManager = con.getChatManager();
                    //ChatManager is assigned to different Thread
                    Thread newThread = new Thread(() -> {
                        chatManager.addChatListener(
                                new ChatManagerListener() {
                                    @Override
                                    public void chatCreated(Chat chat, boolean createdLocally) {
                                        chat.addMessageListener(new MessageListener() {
                                            //On message receivd, prints sender and message
                                            public void processMessage(Chat chat, Message msg) {
                                                if(msg.getBody() != null){
                                                    System.out.println(chat.getParticipant() + ":" + msg.getBody());
                                                }
                                            }
                                        });
                                    }
                                }
                        );
                    });
                    //Starts thread to run on background
                    newThread.start();
                    do {
                        //Show menu if not logged in
                        System.out.println("Opciones:");
                        System.out.println("1. Mostrar todos los usuarios");
                        System.out.println("2. Agregar un contacto");
                        System.out.println("3. Mostrar detalles");
                        System.out.println("4. Comunicación 1 a 1");
                        System.out.println("5. Comunicación grupal");
                        System.out.println("6. Eliminar cuenta");
                        System.out.println("7. Salir");
                        System.out.println("Ingrese su opción");
                        loggedInInput = myObj.nextLine();
                        switch (loggedInInput) {
                            //Shows all users
                            case "1":
                                showAllUsers(con);
                                break;
                            //Add to roster
                            case "2":
                                //Asks for input and calls method
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
                            //Sends message
                            case "4":
                                //Asks for input and calls method
                                System.out.println("Ingrese el nombre de usuario a enviar (usuario@dominio.xyz)");
                                Scanner userMessageScanner = new Scanner(System.in);
                                String userMessage = userMessageScanner.nextLine();
                                System.out.println("Ingrese el mensaje del usuario");
                                Scanner messageScanner = new Scanner(System.in);
                                String message = messageScanner.nextLine();
                                if (sendMessage(con, userMessage, message)){
                                    System.out.println("El mensaje fue enviado correctamente");
                                }else{
                                    System.out.println("Error, favor intentar nuevamente");
                                }
                            //Deletes account
                            case "6":
                                //Calls method on current connection and changes input to exit loop
                                deleteAccount(con);
                                loggedInInput = "7";
                            //Logouts
                            case "7":
                                logout(con);
                        }
                    } while (!loggedInInput.equals("7"));
                }
            }
        }
    }
}
