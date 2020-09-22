import java.io.IOException;
import java.net.ServerSocket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Integer.parseInt;

/**
 *
 * @author hwang
 * Modified by: Chad Manning
 * Course: CMPS 3390
 */
public class ChatroomServer {
    static Socket       sk  = null;
    static ServerSocket ssk = null;
    static int         port = 9999;
    
    static int  numConnection = 0;

    static SharedOuts sOuts = new SharedOuts();

    
    public static void main( String args[] ) {
        try {
            
            ssk = new ServerSocket(port);
            while ( true ) {
                sk = ssk.accept();
                System.out.printf("%d connections.\n", ++numConnection);
                new Thread ( new SAgent( sk, sOuts )).start();    
            }
        }
        catch ( Exception e ) {e.printStackTrace();}
    }
    
}

class  SharedOuts {
    
    ArrayList<Integer> clients = null;
    ArrayList<Integer> reps = null;
    Map<Integer, ObjectOutputStream> uMap;
    Map<Integer, ObjectOutputStream> rMap;
    static int numclients = 0;


    public SharedOuts() {
        clients = new ArrayList();
        reps = new ArrayList<>();
        uMap = new HashMap();
        rMap = new HashMap();
    }
    public void addClient( Integer userID, ObjectOutputStream out ) {
        clients.add( userID );
        uMap.put( userID, out);
        ++numclients;
        logConnected();
    }
    public void addRep ( Integer rID, ObjectOutputStream out) {
        reps.add(rID);
        rMap.put(rID, out);
        logConnected();
    }
    public void remove (Integer uID) {
        clients.remove(uID);
        uMap.remove(uID);
        reps.remove(uID);
        rMap.remove(uID);
        logConnected();
    }
    public int size() { return uMap.size(); }

    /**
     * Sends message to every user regardless of sendList
     * @param obj : obj to be broadcasted to all clients
     */
    public void broadcast( Object obj) {
        for ( Integer c : clients ) {
                try {
                    System.out.println(obj);
                    uMap.get(c).writeObject(obj);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
        for ( Integer r : reps ) {
            try {
                System.out.println(obj);
                rMap.get(r).writeObject(obj);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        logConnected();
    }
    
    /**
     * Sends message to only clients on the sendList
     * @param msg : message to be sent
     * @param uID : client who will receive message
     */
    public void selectiveBroadcast( String msg, Integer uID) {
        if (clients.contains(uID))
            try {
                uMap.get(uID).writeObject(msg);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NullPointerException n) {}
        else
            try {
                rMap.get(uID).writeObject(msg);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NullPointerException n) {
                System.out.println("User not found");
                System.out.println(msg);
                logConnected();
            }
    }

    public void sendTo (String uN, String msg) {
        System.out.println(msg);
        selectiveBroadcast(msg, uN.hashCode());
        selectiveBroadcast(msg, reps.get(0));
    }
    
    /**
     * Sends updated user list to all clients after a connection or disconnection
     * @param evnt : either a JOIN or DISCONNECT string
     */
    public void updateclients(String evnt) {
        //get clients into a plain string with only commas
        for ( Integer u : clients ) {
                try {
                    System.out.println(evnt);
                    uMap.get(u).writeObject(evnt);
                    uMap.get(u).writeObject(clients.clone());
                } catch (IOException ex) {
            ex.printStackTrace();
                }
            }
        logConnected();

    }
    protected void logConnected() {
        System.out.print("Current clients: " + clients.toString() + "\n");
        System.out.print("Current reps: " + reps.toString() + "\n");
    }
}

class SAgent implements Runnable {

    Socket sk = null;
    ObjectOutputStream out = null;
    ObjectInputStream   in = null;
    SharedOuts sout = null;
    String userName = "John Doe";
    Integer uID;
    static ArrayList<Integer> clientList = null;

    public SAgent ( Socket sk, SharedOuts sout ) {
	    this.sk = sk; this.sout = sout;
	    clientList = new ArrayList<>();
        try {
            out = new ObjectOutputStream ( sk.getOutputStream()) ;
            in= new ObjectInputStream ( sk.getInputStream() );
        } catch ( Exception e ) { e.printStackTrace(); }
    }
  
  public void run() {
    try {
        String input = (String) in.readObject();

        if (input.contains("CLIENT")) {
            userName = input.substring(7);
            uID = userName.hashCode();
            sout.addClient(userName.hashCode(), out);

            sout.sendTo( userName, userName + ": Has connected!");
        }
        else {
            userName = input;
            uID = userName.hashCode();
            sout.addRep( uID, out);
        }
        while (true ) {
            input = (String) in.readObject();
            if (input.contains("DISCONNECT"))
                break;
            else {
                String recipient = input.split(" ")[0];
                input = input.split(" ", 2)[1];
                sout.sendTo(recipient, input);
            }
//            System.out.println(input.split(":")[0].hashCode());

        }
    } 
    catch ( IOException e) {}
    catch (ClassNotFoundException c) {}
    finally {
        sout.remove(uID);
        sout.sendTo( userName, "DISCONNECT " + userName );
        System.out.println(userName + " has disconnected.\n");
    }
  }
}