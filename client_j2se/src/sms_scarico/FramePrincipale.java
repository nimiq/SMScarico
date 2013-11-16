package sms_scarico;


import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import com.borland.jbcl.layout.*;
import java.awt.Font;




public class FramePrincipale extends JFrame {
   JPanel contentPane;
   JPanel impostazioniPanel = new JPanel();
   XYLayout xYLayout1 = new XYLayout();
   Border border1 = BorderFactory.createEtchedBorder(Color.white, new Color(148, 145, 140));
   Border border2 = new TitledBorder(border1, "Impostazioni");
   JComboBox idCombo = new JComboBox();
   XYLayout xYLayout2 = new XYLayout();
   JTextField pwText = new JTextField();

   ArrayList<String> idList = new ArrayList<String> ();
   ArrayList<String> pwList = new ArrayList<String> ();
   ArrayList<String> rimanentiList = new ArrayList<String> ();

   JTextField numText = new JTextField();
   JComboBox actionCombo = new JComboBox();
   JTextField mittText = new JTextField();
   JLabel jLabel1 = new JLabel();
   JLabel jLabel2 = new JLabel();
   JLabel jLabel3 = new JLabel();
   JLabel jLabel4 = new JLabel();
   JLabel jLabel5 = new JLabel();
   JCheckBox tuttiIdCheck = new JCheckBox();
   JPanel rimanentiPanel = new JPanel();
   XYLayout xYLayout3 = new XYLayout();
   Border border3 = BorderFactory.createEtchedBorder(Color.white, new Color(148, 145, 140));
   Border border4 = new TitledBorder(border3, "SMS Rimanenti");
   JButton rimanentiButton = new JButton();
   JScrollPane jScrollPane1 = new JScrollPane();
   JTextArea reportArea = new JTextArea();
   JProgressBar progressBar = new JProgressBar();
   JLabel jLabel7 = new JLabel();
   JComboBox servletCombo = new JComboBox();
   JLabel jLabel8 = new JLabel();
   JCheckBox tutteServletCheck = new JCheckBox();
   JButton scaricaButton = new JButton();
   JTextField inviatiText = new JTextField();
   JTextField daRicevereText = new JTextField();
   JLabel jLabel6 = new JLabel();
   JTextField euriText = new JTextField();
   JLabel jLabel9 = new JLabel();
   JCheckBox svegliaCheck = new JCheckBox();
   DialogSveglia dialogSveglia = new DialogSveglia(this, "SVEGLIA", false);

   public FramePrincipale() {
      try {
         setDefaultCloseOperation(EXIT_ON_CLOSE);
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }





   /**
    * Component initialization.
    *
    * @throws java.lang.Exception
    */
   private void jbInit() throws Exception {
      border4 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(148, 145, 140)), "SMS Rimanenti");
      contentPane = (JPanel) getContentPane();
      contentPane.setLayout(xYLayout1);
      setSize(new Dimension(350, 539));
      setTitle("SMS Scarico");
      impostazioniPanel.setBorder(border2);
      impostazioniPanel.setLayout(xYLayout2);

      idCombo.addActionListener(new Frame1_idCombo_actionAdapter(this));
      numText.setText("3398375608");
      mittText.setText("xyz");
      jLabel1.setText("ID =");
      jLabel2.setText("PW =");
      jLabel3.setText("NUM = ");
      jLabel4.setText("ACTION =");
      jLabel5.setText("MITT =");
      tuttiIdCheck.setFocusPainted(false);
      tuttiIdCheck.setHorizontalTextPosition(SwingConstants.RIGHT);
      tuttiIdCheck.setSelected(true);
      tuttiIdCheck.setText("Tutti gli ID");
      tuttiIdCheck.addActionListener(new Frame1_tuttiIdCheck_actionAdapter(this));
      rimanentiPanel.setLayout(xYLayout3);
      rimanentiPanel.setBorder(border4);
      rimanentiButton.setMargin(new Insets(0, 0, 0, 0));
      rimanentiButton.setText("INFO RIMANENTI");
      rimanentiButton.addActionListener(new Frame1_rimanentiButton_actionAdapter(this));
      reportArea.setFont(new java.awt.Font("Dialog", Font.PLAIN, 9));
      reportArea.setText("REPORT:");
      jLabel7.setToolTipText("");
      jLabel7.setText("= TOT SMS pieni inviati");
      jLabel8.setText("SERVLET =");
      jScrollPane1.setAutoscrolls(true);
      tutteServletCheck.setFocusPainted(false);
      tutteServletCheck.setSelected(true);
      tutteServletCheck.setText("Tutte le Servlet");
      tutteServletCheck.addActionListener(new Frame1_tutteServletCheck_actionAdapter(this));
      scaricaButton.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11));
      scaricaButton.setForeground(new Color(124, 0, 0));
      scaricaButton.setMaximumSize(new Dimension(90, 20));
      scaricaButton.setMinimumSize(new Dimension(90, 20));
      scaricaButton.setPreferredSize(new Dimension(90, 20));
      scaricaButton.setMargin(new Insets(0, 0, 0, 0));
      scaricaButton.setText("S C A R I C A");
      scaricaButton.addActionListener(new Frame1_scaricaButton_actionAdapter(this));
      progressBar.setForeground(new Color(124, 0, 0));
      progressBar.setDoubleBuffered(true);
      progressBar.setStringPainted(true);
      inviatiText.setEditable(false);
      inviatiText.setText("0");
      daRicevereText.setEditable(false);
      daRicevereText.setText("0");
      jLabel6.setText("= TOT SMS da ricevere sul cell");
      euriText.setFont(new java.awt.Font("Dialog", Font.BOLD, 12));
      euriText.setForeground(new Color(124, 0, 0));
      euriText.setEditable(false);
      euriText.setText("0.00");
      euriText.setHorizontalAlignment(SwingConstants.CENTER);
      euriText.addMouseListener(new FramePrincipale_euriText_mouseAdapter(this));
      jLabel9.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12));
      jLabel9.setForeground(new Color(124, 0, 0));
      jLabel9.setHorizontalAlignment(SwingConstants.RIGHT);
      jLabel9.setText("€");
      svegliaCheck.setFocusPainted(false);
      svegliaCheck.setText("Sveglia");
      svegliaCheck.addActionListener(new FramePrincipale_svegliaCheck_actionAdapter(this));
      rimanentiPanel.add(rimanentiButton, new XYConstraints(7, 1, 104, 24));
      rimanentiPanel.add(jScrollPane1, new XYConstraints(7, 55, 302, 143));
      rimanentiPanel.add(progressBar, new XYConstraints(6, 31, 303, 17));
      rimanentiPanel.add(scaricaButton, new XYConstraints(205, 1, 104, 24));
      rimanentiPanel.add(daRicevereText, new XYConstraints(7, 226, 31, -1));
      rimanentiPanel.add(inviatiText, new XYConstraints(7, 204, 31, -1));
      rimanentiPanel.add(jLabel6, new XYConstraints(41, 229, -1, -1));
      rimanentiPanel.add(jLabel7, new XYConstraints(41, 206, -1, -1));
      rimanentiPanel.add(euriText, new XYConstraints(266, 209, 41, 29));
      rimanentiPanel.add(jLabel9, new XYConstraints(252, 216, 12, -1));
      jScrollPane1.getViewport().add(reportArea);

      impostazioniPanel.add(jLabel1, new XYConstraints(34, 4, -1, -1));
      impostazioniPanel.add(idCombo, new XYConstraints(64, 0, 245, -1));
      impostazioniPanel.add(jLabel2, new XYConstraints(29, 29, -1, -1));
      impostazioniPanel.add(pwText, new XYConstraints(64, 27, 245, -1));
      impostazioniPanel.add(numText, new XYConstraints(64, 51, 245, -1));
      impostazioniPanel.add(jLabel3, new XYConstraints(20, 53, -1, -1));
      impostazioniPanel.add(jLabel4, new XYConstraints(6, 78, -1, -1));
      impostazioniPanel.add(jLabel5, new XYConstraints(21, 104, -1, -1));
      impostazioniPanel.add(mittText, new XYConstraints(64, 102, 245, -1));
      impostazioniPanel.add(actionCombo, new XYConstraints(64, 75, 245, -1));
      impostazioniPanel.add(servletCombo, new XYConstraints(64, 126, 245, -1));
      impostazioniPanel.add(jLabel8, new XYConstraints(3, 130, -1, -1));
      impostazioniPanel.add(tutteServletCheck, new XYConstraints(0, 154, -1, -1));
      contentPane.add(rimanentiPanel, new XYConstraints(7, 218, 328, 274));
      contentPane.add(impostazioniPanel, new XYConstraints(7, 5, 328, 205));
      impostazioniPanel.add(tuttiIdCheck, new XYConstraints(121, 154, -1, -1));
      impostazioniPanel.add(svegliaCheck, new XYConstraints(254, 156, -1, -1));
      inizializza();

      // Registro il listener che permette a DialogSveglia di stare accanto a FramePrincipale anche se trascinata
      this.addComponentListener(listener);
   }







   public void idCombo_actionPerformed(ActionEvent e) {
      pwText.setText(pwList.get(idCombo.getSelectedIndex()));
   }





   public void tuttiIdCheck_actionPerformed(ActionEvent e) {
      if (tuttiIdCheck.isSelected()) {
         idCombo.setEnabled(false);
         pwText.setEnabled(false);
         jLabel1.setEnabled(false);
         jLabel2.setEnabled(false);
      }
      else {
         idCombo.setEnabled(true);
         pwText.setEnabled(true);
         jLabel1.setEnabled(true);
         jLabel2.setEnabled(true);
      }
   }





   public void tutteServletCheck_actionPerformed(ActionEvent e) {
      if (tutteServletCheck.isSelected()) {
         servletCombo.setEnabled(false);
         jLabel8.setEnabled(false);
      }
      else {
         servletCombo.setEnabled(true);
         jLabel8.setEnabled(true);
      }
   }







   public void inizializza() {
      //idList.add("domenik76");
      //pwList.add("domenico");

      idList.add("xyz");
      pwList.add("xyz");

      idList.add("xyz");
      pwList.add("xyz");

      idList.add("xyz");
      pwList.add("xyz");

      idList.add("xyz");
      pwList.add("xyz");

      //iteratore
      for (String i : idList) {
         String value = i; //senza ne casting ne i.next
         idCombo.addItem(value);
      }

      servletCombo.addItem("xyz");
      servletCombo.addItem("xyz");

      actionCombo.addItem("SCARICO");
      actionCombo.addItem("INFO");
      actionCombo.addItem("INVIA");
      actionCombo.addItem("DEBUG");


      idCombo_actionPerformed(null);
      tutteServletCheck_actionPerformed(null);
      tuttiIdCheck_actionPerformed(null);


      ///////////////////////////**************\\\\\\\\\\\\\\\\\\\\\\\\\\\
       //scaricaButton_actionPerformed(null); //parte automaticamente a scaricare gli sms
       ////////////////////////////*************\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

   } //fine inizializza





   public void euriText_mouseClicked(MouseEvent e) {
      Azione_ScaricoSMS.suonaSoldi();
   }







   //***************************** SCARICO ***************************************
    /**
     * Azione esgeuita qdo viene premuto il tasto SCARICA
     *
     * @param e ActionEvent
     */
    public void scaricaButton_actionPerformed(ActionEvent e) {
       Thread t = new Thread(new Azione_ScaricoSMS(this));
       t.start();
    } //*****************************************************************************









   //*************************** INFO RIMANENTI **********************************
    /**
     * Azione esgeuita qdo viene premuto il tasto INFO RIMANENTI
     *
     * @param e ActionEvent
     */
    public void rimanentiButton_actionPerformed(ActionEvent e) {
       Thread t = new Thread(new Azione_InfoRimanenti(this));
       t.start();
    } //****************************************************************************





   public void svegliaCheck_actionPerformed(ActionEvent e) {
      if (svegliaCheck.isSelected()) {
         dialogSveglia = new DialogSveglia(this, "SVEGLIA", false);
         dialogSveglia.setLocation(this.getLocationOnScreen().x + this.getSize().width + 5, this.getLocationOnScreen().y);
         dialogSveglia.setVisible(true);
         dialogSveglia.svegliaButton.requestFocus();
      }
      else {
         if (dialogSveglia != null) {
            dialogSveglia.dispose();
         }

      }
   }






   // Questo è un listener che ascolta i vari eventi che accadono al Frame
   ComponentListener listener = new ComponentAdapter() {

      // This method is called after the component's location within its container changes
      public void componentMoved(ComponentEvent evt) {
         Component c = (Component) evt.getSource();

         // Get new location
         //Point newLoc = c.getLocation();
         if (dialogSveglia != null) {
            if (dialogSveglia.isVisible()) {
               dialogSveglia.setLocation(c.getLocationOnScreen().x + c.getSize().width + 5, c.getLocationOnScreen().y);
            }
         }
      }

      /*
             // This method is called only if the component was hidden and setVisible(true) was called
             public void componentShown(ComponentEvent evt) {
             // Component is now visible
             Component c = (Component) evt.getSource();
                }

                // This method is called only if the component was visible and setVisible(false) was called
                public void componentHidden(ComponentEvent evt) {
         // Component is now hidden
         Component c = (Component) evt.getSource();
                }

                // This method is called after the component's size changes
                public void componentResized(ComponentEvent evt) {
         Component c = (Component) evt.getSource();

         // Get new size
         Dimension newSize = c.getSize();
                }*/

   };


} // fine classe Frame1///////////////////////////////////////////////




















class FramePrincipale_svegliaCheck_actionAdapter implements ActionListener {
   private FramePrincipale adaptee;
   FramePrincipale_svegliaCheck_actionAdapter(FramePrincipale adaptee) {
      this.adaptee = adaptee;
   }





   public void actionPerformed(ActionEvent e) {
      adaptee.svegliaCheck_actionPerformed(e);
   }
}




















class FramePrincipale_euriText_mouseAdapter extends MouseAdapter {
   private FramePrincipale adaptee;
   FramePrincipale_euriText_mouseAdapter(FramePrincipale adaptee) {
      this.adaptee = adaptee;
   }





   public void mouseClicked(MouseEvent e) {
      adaptee.euriText_mouseClicked(e);
   }
}




















class Frame1_scaricaButton_actionAdapter implements ActionListener {
   private FramePrincipale adaptee;
   Frame1_scaricaButton_actionAdapter(FramePrincipale adaptee) {
      this.adaptee = adaptee;
   }





   public void actionPerformed(ActionEvent e) {
      adaptee.scaricaButton_actionPerformed(e);
   }
}




















class Frame1_rimanentiButton_actionAdapter implements ActionListener {
   private FramePrincipale adaptee;
   Frame1_rimanentiButton_actionAdapter(FramePrincipale adaptee) {
      this.adaptee = adaptee;
   }





   public void actionPerformed(ActionEvent e) {

      adaptee.rimanentiButton_actionPerformed(e);
   }
}






















class Frame1_tuttiIdCheck_actionAdapter implements ActionListener {
   private FramePrincipale adaptee;
   Frame1_tuttiIdCheck_actionAdapter(FramePrincipale adaptee) {
      this.adaptee = adaptee;
   }





   public void actionPerformed(ActionEvent e) {
      adaptee.tuttiIdCheck_actionPerformed(e);
   }
}




















class Frame1_tutteServletCheck_actionAdapter implements ActionListener {
   private FramePrincipale adaptee;
   Frame1_tutteServletCheck_actionAdapter(FramePrincipale adaptee) {
      this.adaptee = adaptee;
   }





   public void actionPerformed(ActionEvent e) {
      adaptee.tutteServletCheck_actionPerformed(e);
   }
}




















class Frame1_idCombo_actionAdapter implements ActionListener {
   private FramePrincipale adaptee;
   Frame1_idCombo_actionAdapter(FramePrincipale adaptee) {
      this.adaptee = adaptee;
   }





   public void actionPerformed(ActionEvent e) {
      adaptee.idCombo_actionPerformed(e);
   }
}
