package jaengine.modules.messages;

import java.util.ArrayList;
import java.util.HashMap;
import jaengine.logic.ZeroArgFuncWrapper;

/**
 * The MessageHub class creates MessageHubs. MessageHubs are centralized places where modules must be atached to in order to receive updates from other modules. 
 */
public class MessageHub implements Runnable{
    private ArrayList<Messageable> callingList = new ArrayList<Messageable>();
    private ArrayList<Message> messageStack = new ArrayList<Message> ();

    public static boolean endProgram = false;

    private HashMap<Integer,ZeroArgFuncWrapper> listens = new HashMap<Integer, ZeroArgFuncWrapper>(); //unused
    /**
     * Create a new MessageHub
     */
    public MessageHub(){
        Debug.init(); //i dont trust it to target my debug, prob shouldve renamed it
        (new Thread(this,"MHUB")).start();
    }
    /**
     * Add a new member to this MessageHub
     * @param n The member to add
     */
    public void addMember(Messageable n) {
        callingList.add(n);
    }
    /**
     * Return he ArrayList of members on the calling lsit
     * @return the members of this hub
     */
    public ArrayList<Messageable> getMembers() {
        return callingList;
    }
    /**
     * Attach a message to this MessageHub; should only be used by the members
     * @param m
     */
    public void append(Message m) {
        this.messageStack.add(m);
    }
    /**
     * Sends the oldest message on the messageStack to all members
     */
    public void mail() {
    
        Message msg = messageStack.remove(0);
        // if (msg.code != 502) {
            Debug.log( "Processing message: " + msg);
            // if (listens.containsKey(msg.code)) {
            //     listens.get(msg.code).f();

            // }
        // }
        for (Messageable m : callingList) {
            m.addMessage(msg);
            Debug.log("Mailing " + msg + " to " + m);
        }
        // if (msg.code != 502) {
            Debug.log("Processed!");
        // }
    }
    /**
     * Contains the mailing loop
     */
    public void run() {
        while (!MessageHub.endProgram) {
            while (messageStack.size() > 0) {
                mail();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("FATAL ERROR! MESSAGE HUB SHUTDOWN");
            }
        }
        Debug.close();
    }

//    public void setNewScene(Scene n) {
//        currentScene = n;
//    }
//    public Scene getScene() {
//        return currentScene;
//    }

    //THESE ARE FOR HIGHER USES
    public void attachListener(int code, ZeroArgFuncWrapper wrapper) {
        if (listens.containsKey(code)) {
            listens.put(code, new ZeroArgFuncWrapper(){  
                @Override
                public void f() {
                    listens.get(code);
                    wrapper.f();
                }
            });
        } else {
            listens.put(code, wrapper);
        }
    }
}