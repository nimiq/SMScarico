package sms_j2me;


import javax.microedition.lcdui.*;




public class Display1_Impostazioni extends Form implements CommandListener, ItemStateListener {
   private String mittente = "mittente";

   private MainMIDlet midlet;
   private Display2_Sms displaySms;
   private Sms sms;
   private String[][] identita = {
      {"uno", "pass1"},
      {"due", "pass2"}
   };

   ChoiceGroup identitaChoice = new ChoiceGroup("", ChoiceGroup.EXCLUSIVE);
   ChoiceGroup servletChoice = new ChoiceGroup("", ChoiceGroup.EXCLUSIVE);
   int i = 0;
   public Display1_Impostazioni(MainMIDlet m) {
      super("IMPOSTAZIONI");
      try {
         midlet = m;
         jbInit();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }





   private void jbInit() throws Exception {
      // Set up this Displayable to listen to command events
      setCommandListener(this);
      //setItemStateListener(this);

      // add the Exit command
      addCommand(new Command("Esci", Command.EXIT, 1));
      addCommand(new Command("Avanti", Command.OK, 1));
      identitaChoice.setLabel("Identità:");
      identitaChoice.setLayout(Item.LAYOUT_EXPAND | Item.LAYOUT_VEXPAND);
      identitaChoice.append(identita[0][0], null);
      identitaChoice.append(identita[1][0], null);
      this.append(identitaChoice);
      this.append(servletChoice);
      servletChoice.setLabel("Servlet:");
      servletChoice.setLayout(Item.LAYOUT_EXPAND | Item.LAYOUT_VEXPAND | Item.LAYOUT_NEWLINE_BEFORE);
      servletChoice.append("nimiq.nimServlet", null);
      servletChoice.append("nimiq77.nimServlet", null);
   }





   public void commandAction(Command command, Displayable displayable) {
      /** @todo Add command handling code */
      if (command.getCommandType() == Command.EXIT) {
         // stop the MIDlet
         MainMIDlet.quitApp();
      }
      if (command.getCommandType() == Command.OK) {
         sms = new Sms();
         sms.setId(identita[identitaChoice.getSelectedIndex()][0]);
         sms.setPw(identita[identitaChoice.getSelectedIndex()][1]);
         sms.setMitt(mittente);
         sms.setServlet(servletChoice.getString(servletChoice.getSelectedIndex()));

         if (displaySms == null) {
            displaySms = new Display2_Sms(this, sms);
         }
         Display.getDisplay(midlet).setCurrent(displaySms);
      }
   }


   public void itemStateChanged(Item item) {
      //nel caso in cui voglio aggiungere una funzione che si attiva qdo modifico la choice selezionata
   }

   public MainMIDlet getMainMidlet() {
      return midlet;
   }



   public String[] getIdentita(int n) {
      return identita[n];
   }

}
