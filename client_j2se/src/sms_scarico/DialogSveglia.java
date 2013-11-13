package sms_scarico;


import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.borland.jbcl.layout.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;



public class DialogSveglia extends JWindow {
   JPanel principalePanel = new JPanel();
   JLabel jLabel1 = new JLabel();
   XYLayout xYLayout1 = new XYLayout();
   FramePrincipale fr;
   PaneLayout paneLayout1 = new PaneLayout();
   Border border1 = BorderFactory.createEtchedBorder(Color.white, new Color(156, 156, 158));
   JSpinner hSpinner = new JSpinner();
   JSpinner mSpinner = new JSpinner();
   JLabel jLabel2 = new JLabel();
   JLabel jLabel3 = new JLabel();
   JCheckBox chiudiCheck = new JCheckBox();
   JTextField chiudiText = new JTextField();
   JCheckBox riavviaCheck = new JCheckBox();
   JTextField riavviaText = new JTextField();
   JButton svegliaButton = new JButton();
   JPanel statoPanel = new JPanel();
   XYLayout xYLayout2 = new XYLayout();
   Border border2 = BorderFactory.createLineBorder(SystemColor.controlText, 2);
   JLabel jLabel4 = new JLabel();
   JLabel statoLabel = new JLabel();
   JLabel mancaLabel = new JLabel();
   JLabel jLabel5 = new JLabel();
   Timer timer;
   private boolean active = false;

   public DialogSveglia(FramePrincipale owner, String title, boolean modal) {
      //super(owner, title, modal);
      super(owner);
      try {
         //setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         jbInit();
         pack();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }

      fr = owner;
   }







   private void jbInit() throws Exception {
      border1 = BorderFactory.createLineBorder(Color.gray, 3);
      principalePanel.setLayout(xYLayout1);
      jLabel1.setText("Alle ore");
      this.getContentPane().setLayout(paneLayout1);
      principalePanel.setBackground(SystemColor.control);
      principalePanel.setBorder(border1);
      xYLayout1.setWidth(235);
      xYLayout1.setHeight(273);
      jLabel2.setText(":");

      SpinnerNumberModel smodel1 = new SpinnerNumberModel(23, 0, 23, 1);
      hSpinner.setFont(new java.awt.Font("Dialog", Font.PLAIN, 12));
      hSpinner.setModel(smodel1);

      mancaLabel.setVisible(false);

      SpinnerNumberModel smodel2 = new SpinnerNumberModel(20, 0, 59, 1);
      mSpinner.setFont(new java.awt.Font("Dialog", Font.PLAIN, 12));
      mSpinner.setModel(smodel2);
      jLabel3.setText("avvia lo scarico e:");
      chiudiCheck.setOpaque(false);
      chiudiCheck.setSelected(true);
      chiudiCheck.setText("Chiudi i seguenti processi:");
      chiudiText.setFont(new java.awt.Font("Dialog", Font.PLAIN, 10));
      chiudiText.setToolTipText("Inserire il nome dei processi separati da ; - Es. \"edonkey2000.exe;emule.exe\"");
      chiudiText.setText("edonkey2000.exe");
      riavviaCheck.setOpaque(false);
      riavviaCheck.setSelected(true);
      riavviaCheck.setText("Al termine dello scarico, esegui:");
      riavviaText.setFont(new java.awt.Font("Dialog", Font.PLAIN, 10));
      riavviaText.setToolTipText("Inserire i comandi separati da ; - Es. \"S:\\eDonkey2000\\edonkey2000.exe;S:\\eMule\\emule.exe\"");
      riavviaText.setText("S:\\eDonkey2000\\edonkey2000.exe");
      svegliaButton.setMargin(new Insets(0, 0, 0, 0));
      svegliaButton.setText("ATTIVA SVEGLIA >");
      svegliaButton.addActionListener(new DialogSveglia_svegliaButton_actionAdapter(this));
      statoPanel.setLayout(xYLayout2);
      statoPanel.setBackground(Color.lightGray);
      statoPanel.setBorder(border2);
      jLabel4.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11));
      jLabel4.setText("STATO");
      statoLabel.setHorizontalAlignment(SwingConstants.CENTER);
      statoLabel.setText("Sveglia inattiva!!");
      mancaLabel.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14));
      mancaLabel.setForeground(new Color(124, 0, 0));
      mancaLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      mancaLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
      mancaLabel.setText("- 88h. 88m.");
      jLabel5.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14));
      jLabel5.setForeground(Color.gray);
      jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel5.setText("SVEGLIA");

      this.getContentPane().add(principalePanel, new PaneConstraints("panel1", "panel1", PaneConstraints.ROOT, 0.5F));
      statoPanel.add(jLabel4, new XYConstraints(71, 1, -1, -1));
      statoPanel.add(mancaLabel, new XYConstraints(84, 16, 92, -1));
      statoPanel.add(statoLabel, new XYConstraints(3, 19, 171, -1));
      principalePanel.add(riavviaText, new XYConstraints(32, 115, 186, -1));
      principalePanel.add(riavviaCheck, new XYConstraints(11, 96, -1, -1));
      principalePanel.add(chiudiText, new XYConstraints(32, 75, 186, -1));
      principalePanel.add(chiudiCheck, new XYConstraints(11, 56, -1, -1));
      principalePanel.add(hSpinner, new XYConstraints(45, 30, 35, -1));
      principalePanel.add(jLabel2, new XYConstraints(82, 33, 6, -1));
      principalePanel.add(mSpinner, new XYConstraints(88, 30, 35, -1));
      principalePanel.add(jLabel3, new XYConstraints(132, 34, -1, -1));
      principalePanel.add(jLabel1, new XYConstraints(6, 33, -1, -1));
      principalePanel.add(statoPanel, new XYConstraints(23, 162, 182, 42));
      principalePanel.add(svegliaButton, new XYConstraints(56, 228, 116, 26));
      principalePanel.add(jLabel5, new XYConstraints(83, 2, -1, -1));
   }





   public void attivaSveglia() {
      timer = new Timer(this);
      new Thread(timer).start();
      svegliaButton.setText("ANNULLA SVEGLIA >");
      jLabel1.setEnabled(false);
      jLabel2.setEnabled(false);
      jLabel3.setEnabled(false);
      hSpinner.setEnabled(false);
      mSpinner.setEnabled(false);
      chiudiCheck.setEnabled(false);
      riavviaCheck.setEnabled(false);
      chiudiText.setEnabled(false);
      riavviaText.setEnabled(false);
      statoPanel.setBackground(Color.yellow);
      statoPanel.requestFocus();
      fr.svegliaCheck.setEnabled(false);
   }




   public void annullaSveglia(){
      timer.stop();
      this.dispose();
      fr.svegliaCheck.setEnabled(true);
      fr.svegliaCheck.setSelected(false);
   }





   public void svegliaButton_actionPerformed(ActionEvent e) {
      if (!active) {
         attivaSveglia();
         active = true;
      } else {
         annullaSveglia();
         active = false;
      }
      //se la sveglia non è attiva, la attiva: attivaSveglia()
      //se la sveglia è attiva la annulla: annullaSveglia()

   }


}//FINE CLASSE




















class DialogSveglia_svegliaButton_actionAdapter implements ActionListener {
   private DialogSveglia adaptee;
   DialogSveglia_svegliaButton_actionAdapter(DialogSveglia adaptee) {
      this.adaptee = adaptee;
   }





   public void actionPerformed(ActionEvent e) {
      adaptee.svegliaButton_actionPerformed(e);
   }
}
