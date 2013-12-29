package sms_scarico;


import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;




public class Azione_InfoRimanenti implements Runnable {

   Thread thread;
   FramePrincipale frame;


   public Azione_InfoRimanenti(FramePrincipale f) {
      thread = new Thread(this);
      frame = f;

      for (int i = 0; i < frame.idList.size(); i++) {
         frame.rimanentiList.add("0");
      }

   }





   public void run() {
      if (frame.tuttiIdCheck.isSelected()) {
         getRimanentiAllId(true);
      }
      else {
         getRimanentiSingleId(frame.idCombo.getSelectedIndex(), true);
      }
   }





   private void attendi() {
      try {

         DialogAttendi d = new DialogAttendi(frame, "Attendi...", true);

         Thread th = new Thread(d);
         th.start();
         d.validate();
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         Dimension frameSize = d.getSize();
         if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
         }
         if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
         }
         d.setLocation( (screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
         d.setVisible(true);

         while (th.isAlive()) {
            thread.sleep(500);
         }
      }
      catch (InterruptedException ex) {
      }

   }








   public int getRimanentiSingleId(int idN, boolean isModificaFramePrincip) {
      return getRimanenti(idN, false, isModificaFramePrincip);
   }





   public int getRimanentiAllId(boolean isModificaFramePrincip) {
      return getRimanenti( -1, true, isModificaFramePrincip);
   }









   /**
    * getRimanenti
    *
    * @param idN int: numero dell'id in frame.idList di cui controllare gli sms rimanenti. VALIDO SOLO SE isAll E' TRUE.
    * @param isAll boolean: true se deve controllare gli sms rimanenti su tutti gli id
    * @param isModificaFramePrincip boolean: true se qs funzione deve modificare progressBar e reportArea del FramePrincipale
    * @return int: il numero di sms rimanenti
    */
   private int getRimanenti(int idN, boolean isAllId, boolean isModificaFramePrincip) {
      HttpURLConnection connex = null;
      OutputStream ostream = null;
      int smsRimanentiTot = 0;
      int nServletDown = 0;
      String id = "", pw = "";


      if (isModificaFramePrincip) {
         frame.reportArea.setText("");
         frame.progressBar.setValue(0);
      }


      //Azzero la lista dei rimanenti
      for (int i = 0; i < frame.idList.size(); i++) {
         frame.rimanentiList.set(i, "0");
      }


      //IMPOSTO UN CICLO SU TUTTI GLI ID O SOLO SU UN ID a seconda del valore di isAllID
      int DA_ID = 0;
      int A_ID = 0;
      if (isAllId) {
         DA_ID = 0;
         A_ID = frame.idList.size();
      }
      else {
         DA_ID = idN;
         A_ID = DA_ID + 1;
      }

      for (int indiceID = DA_ID; indiceID < A_ID; indiceID++) { //per ogni ID
         id = frame.idList.get(indiceID);
         pw = frame.pwList.get(indiceID);


         if (isModificaFramePrincip) {
            if (isAllId) {
               frame.progressBar.setValue(frame.progressBar.getValue() + Math.round( ( (100 / frame.idList.size()) / 2)));
            }
            else {
               frame.progressBar.setValue(50);
            }
         }


         final int MAX_RIP = 10;
         tentativi:for (int indiceRip = 0; indiceRip < MAX_RIP; indiceRip++) { //ripeti fino a MAX_RIP volte in caso di errore
            try {

               //CAMBIA AD OGNI PASSO LA SERVLET se è selezionato "Tutte le Servlet" (per non sovraccaricare troppo una singola servlet)
               if (frame.tutteServletCheck.isSelected()) {
                  int nTotServlet = frame.servletCombo.getItemCount();
                  int nServlet = frame.servletCombo.getSelectedIndex();
                  nServlet++;
                  if (nServlet < nTotServlet) {
                     frame.servletCombo.setSelectedIndex(nServlet);
                  }
                  else {
                     frame.servletCombo.setSelectedIndex(0);
                  }
               }

               //SETTAGGI DELLA CONNESSIONE -------------------------------------------------------------------------------------------
               //connex = (HttpConnection) Connector.open();
               //URL u1 = new URL("http://www.myjavaserver.com/servlet/" + frame.servletCombo.getSelectedItem().toString() + ".nimServlet"); //in remoto su www.myjavaserver.com
               URL u1 = new URL("http://127.0.0.1:7070/WebModule1/nimservlet"); //in locale con tomcat5
               connex = (HttpURLConnection) u1.openConnection();
               connex.setInstanceFollowRedirects(false);

               connex.setRequestMethod("POST");
               //connex.setRequestProperty("Host", "www.myjavaserver.com"); //in remoto su www.myjavaserver.com
               connex.setRequestProperty("Host", "127.0.0.1:7070"); //in locale con tomcat5
               connex.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8) Gecko/20051111 Firefox/1.5");
               connex.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
               connex.setRequestProperty("Accept-Language", "it-it,it;q=0.8,en-us;q=0.5,en;q=0.3");
               connex.setRequestProperty("Accept-Encoding", "gzip,deflate");
               connex.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
               connex.setRequestProperty("Keep-Alive", "300");
               connex.setRequestProperty("Connection", "Keep-Alive");


               //APERTURA DELLA CONNESSIONE -------------------------------------------------------------------------------------------
               //connex.connect();       //connessione nel caso di un GET
               connex.setDoOutput(true); //connessione nel caso di un POST


               //SCRITTURA DELL'OUTPUT DEL POST (ovviamente qs 3 righe non esistono con un GET) ---------------------------------------
               ostream = connex.getOutputStream();
               //ostream.write("service=mail&reload=&teldest=&finalurlok=http%3A%2F%2Fwww.rossoalice.virgilio.it%2Falice%2Fportal%2Fservice%2Fentry.do%3Fservice%3Dsms&rchid=36&usr=mauroromeopi&pwd=123456".getBytes());
               ostream.write( ("id=" + id + "&pw=" + pw + "&mitt=" + frame.mittText.getText() + "&action=info").getBytes());
               ostream.flush();


               //VERIFICA CHE LA RISPOSTA DEL SERVER SIA OK ---------------------------------------------------------------------------
               int rc = connex.getResponseCode();
               if (isModificaFramePrincip) {
                  frame.reportArea.append("---------------\n");
                  frame.reportArea.append("Ripetizione #" + (indiceRip + 1) + "\n");
                  frame.reportArea.append("Risp. Server: " + rc + " " + connex.getResponseMessage() + "\n");
               }
               if (rc != HttpURLConnection.HTTP_OK) { //Cioè se il server ha risposto in modo diverso da: 200 OK
                  nServletDown++;
                  if (frame.tutteServletCheck.isSelected()) { //se è selezionato "Tutte le Servlet"
                     if (nServletDown >= frame.servletCombo.getItemCount()) { //se tutte le servlet sono down
                        attendi();
                     }
                  }
                  else { //se non è selezionato "Tutte le Servlet"
                     attendi();
                  }

                  continue tentativi; //c'è stato un errore e ritorno alla valutazione del for incrementando il contatore (e automat viene anche cambiata la servlet)
               }
               else { ////Cioè se il server ha risposto con: 200 OK
                  nServletDown = 0;
               }


               //LETTURA DEGLI HEADER DAL MSG DI RISP DEL SERVER ------------------------------------------------------------
               String numS = "";
               int nHd = 0;
               String hdValue = connex.getHeaderField(0); //leggere il nHd header del messaggio HTTP di ** RISPOSTA ** del server
               while (hdValue != null) {
                  String hdKey = connex.getHeaderFieldKey(nHd);
                  if (hdKey != null) {
                     if (hdKey.toLowerCase().compareTo("num") == 0 || //NB: scrivere qui nel codice in ** minuscolo **
                       hdKey.toLowerCase().compareTo("testo") == 0 ||
                       hdKey.toLowerCase().compareTo("azione") == 0 ||
                       hdKey.toLowerCase().compareTo("sms_status") == 0 ||
                       hdKey.toLowerCase().compareTo("errore_server") == 0 ||
                       hdKey.toLowerCase().compareTo("id") == 0 ||
                       hdKey.toLowerCase().compareTo("mitt") == 0) {

                        if (isModificaFramePrincip) {
                           frame.reportArea.append(hdKey + ": " + hdValue + "\n");
                        }
                     }
                     else if (hdKey.toLowerCase().compareTo("sms_rimanenti") == 0) {
                        if (isModificaFramePrincip) {
                           frame.reportArea.append(hdKey + ": " + hdValue + "\n");
                        }
                        numS = hdValue;
                     }

                  }

                  nHd++;
                  hdValue = connex.getHeaderField(nHd);
               } //fine while


               //Cerco di tradurre numS (ossia il valore dell'header SMS_RIMANENTI) in numero
               if (numS.length() > 1) {
                  numS = numS.substring(0, 2);
                  numS = numS.trim(); //elimino gli eventuali spazi
               }

               int numI = -1;
               try {
                  numI = Integer.valueOf(numS);
               }
               catch (Exception ex) {
                  if (indiceRip == (MAX_RIP - 1)) {
                     frame.rimanentiList.add(indiceID, "ERRORE!!");
                  }
                  continue tentativi; //c'è stato un errore e ritorno alla valutazione del for incrementando il contatore
               }

               if (numI <= 10 && numI >= 0) {
                  frame.rimanentiList.add(indiceID, String.valueOf(numI));
                  smsRimanentiTot += numI;
                  break tentativi; //esce dal for (che fa ripetere la procedura INFO 10 volte)
               }



            }
            catch (ClassCastException e) {
               //throw new IllegalArgumentException();
               frame.reportArea.append("!!!!!!!! Non è un HTTP URL!!\n");
               /////////////return -1;
            }
            catch (IOException e) {
               frame.reportArea.append("!!!!!!!! Impossibile collegarsi al server!!\n");
               ////////////return -1;
            }
            finally {
               if (connex != null) {
                  connex.disconnect();
               }
               if (ostream != null) {
                  try {
                     ostream.close();
                  }
                  catch (IOException ex1) {
                  }
               }
            }
         } //fine for (ciclo di dieci volte)

         if (isModificaFramePrincip) {
            if (isAllId) {
               frame.progressBar.setValue(frame.progressBar.getValue() + Math.round( ( (100 / frame.idList.size()) / 2)));
            }
            else {
               frame.progressBar.setValue(100);
            }
         }

      } //fine for (per tutti gli id)


      if (isModificaFramePrincip) {
         StringBuffer riassunto = new StringBuffer("===============\n");
         riassunto.append("RIASSUNTO:\n");
         for (int i = DA_ID; i < A_ID; i++) {
            riassunto.append(frame.idList.get(i) + " = " + frame.rimanentiList.get(i) + " SMS\n");
         }
         riassunto.append("TOT = " + smsRimanentiTot + " SMS rimanenti\n");
         riassunto.append("===============\n\n\nLOG:\n");
         frame.reportArea.setText(riassunto + frame.reportArea.getText());
      }

      return smsRimanentiTot;
   }

}
