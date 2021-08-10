
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jetbrains.annotations.NotNull;

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

    public static void main(String args[]){
        System.out.println("hola");
        XMPPConnection con = new XMPPConnection("alumchat.xyz");
        try{
            con.connect();
            if (login(con)) {
                ChatManager c = con.getChatManager();
                Chat chat = c.createChat("echobot@alumchat.xyz", null);
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
                chat.sendMessage("Buenas noches");
                while (true) ;
            }else{
                System.out.println("falló");
            }
        }catch (Exception error){
            System.out.println(error);
        }
    }
}
