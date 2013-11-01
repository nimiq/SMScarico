package nimiq;


import java.io.*;
import java.net.*;
import java.util.*;



public class EmailSender {
   private String id, pw, num, txt, mitt;
   private boolean isDebug;
   private PrintWriter out;
   private BufferedReader in;
   private PrintWriter outHtml;

   public EmailSender(String i, String p, String m, String n, String t, boolean isDeb, PrintWriter outH) {
      id = i;
      pw = p;
      mitt = m;
      num = n;
      txt = t;
      isDebug = isDeb;
      outHtml = outH;
   }





   public void invia() {
      try {

         Socket s = new Socket("smtp.mail.yahoo.it", 587);

         out = new PrintWriter(s.getOutputStream());
         in = new BufferedReader(new InputStreamReader(s.getInputStream()));

         //La seguente stringa serve per leggere il nome dell'host (es. nimiqpc)
         //String hostName = InetAddress.getLocalHost().getHostName();

         if (isDebug) {
            outHtml.println("<BR><BR>==SPEDIZIONE EMAIL<BR>");
         }
         receive();


         send("EHLO nimSMS");
         receive();

         send("AUTH LOGIN");
         String ss;
         for (int jj = 1; jj <= 10; jj++) {
            ss = receive();
            if (ss.substring(0, 3).compareTo("334") == 0) {
               break;
            }
         }

         send( new sun.misc.BASE64Encoder().encode("xyz".getBytes()) );
         receive();
         send( new sun.misc.BASE64Encoder().encode("xyz".getBytes()) );
         receive();


         send("MAIL FROM: <xyz@yahoo.it>");
         receive();

         send("RCPT TO: <xyz@yahoo.it>");
         receive();

         send("DATA");
         receive();

         send("FROM: \"Servlet SMS - " + mitt + "\"<xyz@yahoo.it>");
         send("TO: \"Archivio SMS\" <xyz@yahoo.it>");
         send("SUBJECT: SMS");

         try {
            Calendar calendar = new GregorianCalendar(new SimpleTimeZone(3600000, "Europe/Rome"));
            nimData data = new nimData(calendar);

            send("DATE: " + data.toString());
         }
         catch (IOException ex1) {
         }

         send("");

         //invio del testo scritto nella mail
         send("ID: " + id);
         send("PW: " + pw);
         send("MITTENTE: " + mitt);
         send("NUM DESTINATARIO: " + num);
         send("TXT: " + txt);


         //fase di chiusura della mail
         send("");
         send(".");
         receive();
         send("QUIT");
         receive();
         s.close();

         if (isDebug) {
            outHtml.println("==FINE SPEDIZIONE EMAIL<BR><BR>");
         }

      }
      catch (IOException ex) {
         System.out.println(ex);
      }
   }










   /**
    * send
    *
    * @param s String
    * @throws IOException
    */
   public void send(String s) throws IOException {
      if (isDebug) {
         outHtml.println("MANDO: " + s + "<BR>");
      }

      out.print(s);
      out.print("\r\n");
      out.flush();
   }





   /**
    * Receives a string from the socket and displays it in the communication text area.
    *
    * @throws IOException
    * @return String
    */
   public String receive() throws IOException {
      String line = in.readLine();

      if (isDebug) {
         outHtml.println("RICEVO: " + line + "<BR>");
      }

      return line;
   }






} //fine classe
