
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Integer.parseInt;
import java.net.Socket;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author chad
 */
public class ClientChat extends javax.swing.JFrame {
    private ChatAgent agent;
    private String name;
    private String agentName = "REPRESENTATIVE@1011";
    private int ZIP;
    private String uName;
    
    /**
     * Creates new form ClientChat
     */
    ClientChat(String n, String z) {
        initComponents();
        name = n;
        ZIP = parseInt(z);
        uName = name + "@" + ZIP;

        setVisible(true);
        new Thread(agent = new ChatAgent(msgBrd, uName)).start();

        System.out.println("Name: " + name + "\nZIP: " + ZIP);
        agent.sendMessage("CLIENT " + uName);
    }
    
    public void appendToPane(JTextPane tp, String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Sendbtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        msgBrd = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        textArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("3390 - Client");

        Sendbtn.setText("Send");
        Sendbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SendbtnMouseClicked(evt);
            }
        });

        msgBrd.setFocusable(false);
        jScrollPane1.setViewportView(msgBrd);

        textArea1.setColumns(20);
        textArea1.setLineWrap(true);
        textArea1.setRows(5);
        jScrollPane2.setViewportView(textArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(105, 105, 105)
                .addComponent(Sendbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(121, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Sendbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SendbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SendbtnMouseClicked
        // TODO add your handling code here:
        agent.sendMessage(uName + " " + uName + ": " + textArea1.getText());
        textArea1.setText("");
    }//GEN-LAST:event_SendbtnMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Sendbtn;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextPane msgBrd;
    private javax.swing.JTextArea textArea1;
    // End of variables declaration//GEN-END:variables
}

class ChatAgent implements Runnable{
    final String host = "localhost";
    final int    port = 9999;
    Socket sk;
    ObjectInputStream in;
    ObjectOutputStream out;
    JTextPane board;
    String uName;

    public ChatAgent ( JTextPane brd, String name) {
        uName = name;
        board = brd;
        try {
            sk = new Socket(host, port);
            in = new ObjectInputStream( sk.getInputStream() );
            out = new ObjectOutputStream( sk.getOutputStream() );
        } catch (IOException e) {
            System.out.println("Unable to connect to server");
        }
    }
    @Override
    public void run ()  {
        String msg;

        try {
            msg = "Welcome!\n A representative will message shortly.\n";
//            appendToPane( board, msg, Color.black);
            while ( true ) {
                switch (msg.split("@")[0]) {
                    case "REPRESENTATIVE" :
                        appendToPane( board, msg, Color.blue);
                        break;
                    default :
                        appendToPane( board, msg, Color.black);
                        System.out.print("append: " + msg);
                        break;
                }
//                appendToPane( board, msg, );
                msg = (String) in.readObject() + '\n';
            }
        }
        catch (Exception e) { e.printStackTrace(); }
        finally {
            sendMessage("DISCONNECT " + uName);
        }
    }

    public void sendMessage(String msg) {
        try {
            out.writeObject(msg);
        } catch (IOException e) { e.printStackTrace(); };
    }
    
    public void appendToPane(JTextPane tp, String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
        tp.repaint();
    }
}