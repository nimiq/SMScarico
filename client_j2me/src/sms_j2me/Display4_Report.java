package sms_j2me;

import javax.microedition.lcdui.*;

public class Display4_Report extends Form implements CommandListener {
   private MainMIDlet midlet;
   private Sms sms;
   private Display2_Sms precedente;

   public Display4_Report(Display2_Sms d, Sms s) {
      super("REPORT");
      try {
         sms = s;
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
      addCommand(new Command("Indietro", Command.EXIT, 1));
   }

   public void commandAction(Command command, Displayable displayable) {
      /** @todo Add command handling code */
      Display.getDisplay(midlet).setCurrent(precedente);
   }

   public void vai(){
      Connection conn = new Connection(this, sms);
      conn.start();
   }

}
