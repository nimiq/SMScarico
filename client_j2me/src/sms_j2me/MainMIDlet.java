package sms_j2me;


import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;




public class MainMIDlet extends MIDlet {
   static MainMIDlet instance;
   Display1_Impostazioni displayImpostazioni = new Display1_Impostazioni(this);

   public MainMIDlet() {
      instance = this;
   }





   public void startApp() {
      Display.getDisplay(this).setCurrent(displayImpostazioni);
   }





   public void pauseApp() {
   }





   public void destroyApp(boolean unconditional) {
   }





   public static void quitApp() {
      instance.destroyApp(true);
      instance.notifyDestroyed();
      instance = null;
   }

}
