package sms_scarico;


import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.borland.jbcl.layout.*;




public class DialogAttendi extends JDialog implements Runnable {
   Thread thread;

   JPanel panel1 = new JPanel();
   XYLayout xYLayout1 = new XYLayout();
   XYLayout xYLayout2 = new XYLayout();
   JProgressBar jProgressBar1 = new JProgressBar();
   Frame owner;
   Border border1 = BorderFactory.createMatteBorder(6, 6, 6, 6, Color.orange);
   Border border2 = BorderFactory.createLineBorder(new Color(124, 0, 0), 10);
   JTextArea jTextArea1 = new JTextArea();

   public DialogAttendi(Frame owner, String title, boolean modal) {
      super(owner, title, modal);
      this.owner = owner;
      try {
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         jbInit();
         pack();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }





   public DialogAttendi() {
      this(new Frame(), "Dialog1", false);
   }





   private void jbInit() throws Exception {
      border2 = BorderFactory.createLineBorder(Color.black, 3);
      panel1.setLayout(xYLayout2);
      this.getContentPane().setLayout(xYLayout1);
      panel1.setBackground(new Color(125, 125, 125));
      panel1.setBorder(border2);
      jProgressBar1.setBackground(Color.lightGray);
      jProgressBar1.setForeground(new Color(124, 0, 0));
      jProgressBar1.setDoubleBuffered(true);
      jProgressBar1.setStringPainted(true);
      this.setUndecorated(true);
      this.getContentPane().add(panel1, new XYConstraints(0, 0, 376, 278));
      jTextArea1.setBackground(new Color(125, 125, 125));
      jTextArea1.setFont(new java.awt.Font("Dialog", Font.BOLD, 14));
      jTextArea1.setEditable(false);
      jTextArea1.setText("Tutte le servlet sono momentaneamente indisponibili!!!\n\n\n\nAttendo " +
        "1 minuto prima di ritentare...");
      jTextArea1.setLineWrap(true);
      jTextArea1.setWrapStyleWord(true);
      panel1.add(jProgressBar1, new XYConstraints(38, 156, 294, -1));
      panel1.add(jTextArea1, new XYConstraints(38, 30, 294, 126));
   }





   public void run() {
      thread = new Thread(this);
      try {
         for (int i = 0; i < 10; i++) {
            thread.sleep(6000);
            jProgressBar1.setValue(jProgressBar1.getValue() + 10);
         }
         this.dispose();
      }
      catch (InterruptedException ex) {
      }
   }


}
