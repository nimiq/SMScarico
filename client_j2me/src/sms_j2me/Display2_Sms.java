package sms_j2me;


import javax.microedition.lcdui.*;




public class Display2_Sms extends Form implements CommandListener, ItemStateListener {
   private MainMIDlet midlet;
   private Sms sms;
   Display3_Testo displayTesto;
   Display4_Report displayReport;
   Display1_Impostazioni precedente;


   public Display2_Sms(Display1_Impostazioni d, Sms s) {
      super("SMS");
      try {
         sms = s;
         precedente = d;
         midlet = precedente.getMainMidlet();
         jbInit();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }





   private void jbInit() throws Exception {
      // Set up this Displayable to listen to command events
      setCommandListener(this);
      setItemStateListener(this);


      // add the Exit command
      addCommand(new Command("Indietro", Command.EXIT, 1));
      addCommand(new Command("Invia", Command.OK, 1));
      addCommand(new Command("Modif. Testo", Command.ITEM, 1));
      addCommand(new Command("SMS Restanti", Command.ITEM, 1));

      numText.setLabel("Num:");
      numText.setLayout(Item.LAYOUT_EXPAND | Item.LAYOUT_VEXPAND);
      numText.setConstraints(TextField.PHONENUMBER);
      numText.setMaxSize(10);
      this.append(numText);
      this.append(testoText);
      testoText.setLabel("Testo [0/140 car.]:");
      testoText.setLayout(Item.LAYOUT_EXPAND | Item.LAYOUT_VEXPAND | Item.LAYOUT_NEWLINE_BEFORE);
      testoText.setMaxSize(147);
      testoText.setPreferredSize( -1, -1);
   }





   public void commandAction(Command command, Displayable displayable) {
      /** @todo Add command handling code */
      if (command.getCommandType() == Command.EXIT) { //il tasto INDIETRO
         Display.getDisplay(midlet).setCurrent(precedente);
      }
      else if (command.getLabel().compareTo("Modif. Testo") == 0) { //il tasto Modif. Testo
         if (displayTesto == null) {
            displayTesto = new Display3_Testo(this);
         }
         displayTesto.setString(testoText.getString());
         Display.getDisplay(midlet).setCurrent(displayTesto);
      }
      else {
         try {
            if (command.getCommandType() == Command.OK) { //il tasto INVIA
               sms.setAzione("Invia");
               long num = Long.parseLong(numText.getString());
               sms.setNum(num);
               sms.setTesto(testoText.getString());
            }
            else if (command.getLabel().compareTo("SMS Restanti") == 0) { //il tasto SMS Restanti
               sms.setAzione("Info");
               sms.setNum(0);
               sms.setTesto(testoText.getString());
            }

            if (displayReport == null) {
               displayReport = new Display4_Report(this, sms);
            }
            displayReport.deleteAll();
            displayReport.vai();
            Display.getDisplay(midlet).setCurrent(displayReport);

         }
         catch (NumberFormatException ex) {
            Alert a = new Alert("ATTENZIONE", "Il numero di cellulare non è nel formato corretto!!", null, AlertType.ERROR);
            a.setTimeout(Alert.FOREVER);
            Display.getDisplay(midlet).setCurrent(a);
         }
      }
   }





   public void itemStateChanged(Item item) {
      if (item.getLabel().startsWith("Testo")) {
         testoText.setLabel("Testo [" + testoText.size() + "/140 car.]:");
      }
      //!!! INSERIRE CONTROLLO SUI CARATTERI INSERITI !!!!
      //CARATT NN DISPONIBILI: & [ ] à _ @
      //!!! INSERIRE CONTROLLO SUL NUMERO !!!
   }


   public MainMIDlet getMainMidlet() {
      return midlet;
   }




   TextField numText = new TextField("", "", 15, TextField.ANY);
   TextField testoText = new TextField("", "", 15, TextField.ANY);

}
