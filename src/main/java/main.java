
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jetbrains.annotations.NotNull;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Main{
    static boolean login(@NotNull XMPPConnection con){
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
    static boolean sendMessage(Chat chat, String JID, String message){
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

    public static void main(String args[]){
        System.out.println("hola");
        XMPPConnection con = new XMPPConnection("alumchat.xyz");
        try{
            con.connect();
            if (login(con)) {
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
        }
    }
}
