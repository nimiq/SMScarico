package sms_j2me;


import java.io.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;




/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Connection implements Runnable {

   private Display4_Report displayReport;
   //private DisplaySms displaySms;
   private Sms sms;




   public Connection(Display4_Report dr, Sms s) {
      sms = s;
      displayReport = dr;
      //displaySms = precedente.;


      //il problema è che String.replace() funziona solo cn char e nn cn String
      StringBuffer txtBuf = new StringBuffer(sms.getTesto());
      for (int i = 0; i < txtBuf.length(); i++) {
         if (txtBuf.charAt(i) == '+') {
            txtBuf.deleteCharAt(i);
            txtBuf.insert(i, "%2B");
         }
         if (txtBuf.charAt(i) == '=') {
            txtBuf.deleteCharAt(i);
            txtBuf.insert(i, "%3D");
         }
         if (txtBuf.charAt(i) == '#') {
            txtBuf.deleteCharAt(i);
            txtBuf.insert(i, "%23");
         }
         if (txtBuf.charAt(i) == ' ') {
            txtBuf.setCharAt(i, '+');
         }
      }
      sms.setTesto(txtBuf.toString());
   }





   public void start() {
      Thread t = new Thread(this);
      t.start();
   }






   public void run() {

      HttpConnection connex = null;


      try {

         //SETTAGGI DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         connex = (HttpConnection) Connector.open("http://www.myjavaserver.com/servlet/" + sms.getServlet() + "?id=" + sms.getId() + "&pw=" + sms.getPw() + "&mitt=" + sms.getMitt() + "&action=" + sms.getAzione() + "&num=" + sms.getNum() + "&txt=" + sms.getTesto());
         connex.setRequestMethod(HttpConnection.GET);
         connex.setRequestProperty("Host", "www.myjavaserver.com");
         connex.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8) Gecko/20051111 Firefox/1.5");
         connex.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
         connex.setRequestProperty("Accept-Language", "it-it,it;q=0.8,en-us;q=0.5,en;q=0.3");
         connex.setRequestProperty("Accept-Encoding", "gzip,deflate");
         connex.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
         connex.setRequestProperty("Keep-Alive", "300");
         connex.setRequestProperty("Connection", "Keep-Alive");


         //LETTURA DEGLI HEADER DAL MSG DI RISP DEL SERVER ------------------------------------------------------------
         int nHd = 0;
         String hdValue = connex.getHeaderField(0); //leggere il nHd header del messaggio HTTP di ** RISPOSTA ** del server
         while (hdValue != null) {
            String hdKey = connex.getHeaderFieldKey(nHd);
            if (hdKey != null) {
               if (hdKey.toLowerCase().compareTo("num") == 0 || //NB: scrivere qui nel codice in ** minuscolo **
                 hdKey.toLowerCase().compareTo("testo") == 0 ||
                 hdKey.toLowerCase().compareTo("azione") == 0 ||
                 hdKey.toLowerCase().compareTo("sms_status") == 0 ||
                 hdKey.toLowerCase().compareTo("sms_rimanenti") == 0 ||
                 hdKey.toLowerCase().compareTo("errore_server") == 0 ||
                 hdKey.toLowerCase().compareTo("id") == 0 ||
                 hdKey.toLowerCase().compareTo("mitt") == 0) {

                  displayReport.append(hdKey + ": " + hdValue + "\n");
               }
            }

            nHd++;
            hdValue = connex.getHeaderField(nHd);
         } //fine while


      }
      catch (ClassCastException e) {
         //throw new IllegalArgumentException();
         displayReport.append("Non è un HTTP URL!!");
         return;
      }
      catch (IOException e) {
         displayReport.append("Impossibile collegarsi al server!!");
         return;
      }
      finally {
         try {
            if (connex != null) {
               connex.close();
            }
         }
         catch (IOException e) {
            e.printStackTrace();
         }
      }

      return;
   } //fine run()


} //fine classe Connection
