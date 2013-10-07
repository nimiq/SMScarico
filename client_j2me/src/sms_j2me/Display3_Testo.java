package sms_j2me;

import javax.microedition.lcdui.*;

public class Display3_Testo extends TextBox implements CommandListener  {
   MainMIDlet midlet;
   Display2_Sms precedente;


   public Display3_Testo(Display2_Sms d) {
      super("TESTO [max140 car]", "", 50, TextField.ANY);
      try {
         precedente = d;
         midlet = precedente.getMainMidlet();
         jbInit();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      // Set up this Displayable to listen to command events
      setCommandListener(this);


      // add the Exit command
      addCommand(new Command("OK", Command.OK, 1));
      this.setMaxSize(147);
      this.setInitialInputMode("IS_FULLWIDTH_DIGITS");
      //this.setInitialInputMode();
   }

   public void commandAction(Command command, Displayable displayable) {
      /** @todo Add command handling code */
      if (command.getCommandType() == Command.OK) { //il tasto OK
         precedente.testoText.setString(this.getString());
         precedente.itemStateChanged(precedente.testoText);
         Display.getDisplay(midlet).setCurrent(precedente);
      }

   }


}
