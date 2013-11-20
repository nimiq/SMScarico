package sms_scarico;


import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import sun.audio.*;




public class Azione_ScaricoSMS implements Runnable {

   Thread thread;
   FramePrincipale frame;
   ArrayList<String> rimanentiAllInizioList;


   /* E' possibile creare messaggi di testo lunghi che verranno inviata sotto forma di più sms.
    *  Ho studiato la form sul sito www.rossoalice.itè ho capito che non è possibile immetere più di 627 caratteri, che
    *  corrispondono a 4 sms.
    *  1 SMS => da 001 a 147 carettrei
    *  2 SMS => da 148 a 307 carettrei
    *  3 SMS => da 308 a 467 carettrei
    *  4 SMS => da 468 a 627 carettrei
    *  Non è possibile immetere più di 627 caratteri, di conseguenza non è possibile inviare un messaggio composta da + di 4 sms.
    *  NB: non inviare più di 627 caratteri altrimenti gli sms nn mi arrivano, ma il num di sms rimanenti mi viene scalato
    *  NB: CON 627 CARATTERI MI VENGONO SCALATI 4 SMS DAL CONTEGGIO DEGLI SMS RIMANENTI, MA MI ARRIVANO ** 5 ** SMS SUL CELL!!!
    */


   public Azione_ScaricoSMS(FramePrincipale f) {
      thread = new Thread(this);
      frame = f;

      rimanentiAllInizioList = new ArrayList<String> ();
      for (int i = 0; i < frame.idList.size(); i++) {
         rimanentiAllInizioList.add("0");
      }
   }





   public void run() {
      vai();
   }







   private void vai() {
      String id = "", pw = "";

      //Azzero la lista degli sms inviati
      for (int i = 0; i < frame.idList.size(); i++) {
         rimanentiAllInizioList.set(i, "0");
      }

      frame.inviatiText.setText("0");
      frame.daRicevereText.setText("0");
      frame.progressBar.setValue(0);

      //Pulisco il report
      frame.reportArea.setText("");


      //IMPOSTO UN CICLO SU TUTTI GLI ID O SOLO SU UN ID a seconda che sia selez o meno il checkBox "Tutti gli ID"
      int DA_ID = 0;
      int A_ID = 0;
      if (frame.tuttiIdCheck.isSelected()) {
         DA_ID = 0;
         A_ID = frame.idList.size();
      }
      else {
         DA_ID = frame.idCombo.getSelectedIndex();
         A_ID = DA_ID + 1;
      }

      //Ciclo FOR che scorre tutti gli ID
      scorriid:for (int indiceID = DA_ID; indiceID < A_ID; indiceID++) {
         id = frame.idList.get(indiceID);
         pw = frame.pwList.get(indiceID);

         //Prima stringa del report
         frame.reportArea.append("\n\n---------------\n");
         frame.reportArea.append("L'ID è " + id + "\n");
         if (frame.tuttiIdCheck.isSelected()) {
            frame.progressBar.setValue(frame.progressBar.getValue() + Math.round( ( (100 / frame.idList.size()) / 2)));
         }
         else {
            frame.progressBar.setValue(50);
         }



         //************************ FOR 10 volte **********************************************************************
          // ricerca i rimanenti per l'identità ID e invia 1 messaggio (composto 1, 2 o 3 sms pieni corrispond a 1, 3 o 4 sms ricevuti)
          cercaRimanentiEInvia:for (int i = 0; i < 10; i++) {

            //Le 2 seguenti righe vengono eseguite sequenzialmente, cioè nn sono un thread, ma l'esecuzione si ferma e aspetta che sia terminata la chiamata della funzione getRimanentiSingleId()
            Azione_InfoRimanenti c = new Azione_InfoRimanenti(frame);
            int rimanenti = 0;
            try {
               rimanenti = c.getRimanentiSingleId(indiceID, false);
               frame.reportArea.append("\nSMS rimanenti: " + rimanenti + "\n");
            }
            catch (Exception ex) {
               continue cercaRimanentiEInvia; //in caso di errore ritorna alla valutazione del for
            }

            //Se la lista dei rimanenti contiene un valore inferiore a quello che leggo ora la aggiorno
            try {
               if (Integer.valueOf(rimanentiAllInizioList.get(indiceID)) < rimanenti) {
                  rimanentiAllInizioList.set(indiceID, String.valueOf(rimanenti));
               }
            }
            catch (NumberFormatException ex1) {
               continue cercaRimanentiEInvia; //in caso di errore ritorna alla valutazione del for
            }


            //Se non ci sono più sms disponibili aggiorno il num di sms inviati e azzero quelli rimanenti
            if (rimanenti == 0) {
               break cercaRimanentiEInvia; //esce dal for che x 10 volte cerca i rimanenti e invia 1 messaggio
            }

            //Per 10 volte tenta dl'invio di 1 messaggio (composto da un min di 1 a un max di 4 sms)
            inviaSms(id, pw, rimanenti);

         } //fine FOR (ricerca i rimanenti e invia 1 sms per 10 volte)
         //*************************************************************************************************************






          //Arrivati a qs punto sono sicuro che per l'id=id sono stati inviati rimanentiAllInizioList.get(indiceID) sms pieni che corrispondono ad un numero maggiore di sms ricevuti sul cell
          frame.reportArea.append("\n!!!!! Ho inviato un TOT di " + rimanentiAllInizioList.get(indiceID) + " SMS pieni da " + id + "\n");
         frame.reportArea.append("!!!!! Dovresti ricevere un TOT di " + getSMSDaRicevere(rimanentiAllInizioList.get(indiceID)) + " SMS da " + id + "\n");
         if (frame.tuttiIdCheck.isSelected()) {
            frame.progressBar.setValue(frame.progressBar.getValue() + Math.round( ( (100 / frame.idList.size()) / 2)));
         }
         else {
            frame.progressBar.setValue(100);
         }

         //Aggiorno i soldi e suono il rumore dei soldi
         try {
            Double e1 = 0.04 * (double) getSMSDaRicevere(rimanentiAllInizioList.get(indiceID));
            Double e2 = Double.valueOf(frame.euriText.getText());
            e1 += e2;
            frame.euriText.setText(e1.toString());
            suonaSoldi();
         }
         catch (Exception ex2) {
            ex2.printStackTrace();
         }


      } //fine FOR (per ogni id)




      //calcolare tot sms inviati e da ricevere
      int TOT_INVIATI = 0;
      int TOT_DA_RICEVERE = 0;
      for (int j = 0; j < rimanentiAllInizioList.size(); j++) {
         TOT_INVIATI += Integer.valueOf(rimanentiAllInizioList.get(j));
         TOT_DA_RICEVERE += getSMSDaRicevere(rimanentiAllInizioList.get(j));
      }

      StringBuffer riassunto = new StringBuffer("===============\n");
      riassunto.append("RIASSUNTO\n");
      riassunto.append("TOT SMS inviati: " + TOT_INVIATI + "\n");
      riassunto.append("TOT SMS da ricevere: " + TOT_DA_RICEVERE + "\n");
      riassunto.append("===============\n\n\n\n");
      frame.reportArea.setText(riassunto + frame.reportArea.getText());

      frame.inviatiText.setText("" + TOT_INVIATI);
      frame.daRicevereText.setText("" + TOT_DA_RICEVERE);

      Double euro = 0.04 * (double) TOT_DA_RICEVERE;
      frame.euriText.setText(euro.toString());


      suonaFine();

      StringTokenizer st = new StringTokenizer(frame.dialogSveglia.riavviaText.getText(), ";");
      while (st.hasMoreTokens()) {
         Process p = null;
         try {
            p = Runtime.getRuntime().exec(st.nextToken());
            //p.waitFor();
         }
         //catch (InterruptedException ex1) {
         //}
         catch (IOException ex) {
         }
      }

      JOptionPane.showMessageDialog(null, "SMS scaricati !!!!!!!!!", "ATTENZIONE", JOptionPane.ERROR_MESSAGE);


   } //fine vai()












   private void inviaSms(String id, String pw, int rimanenti) {
      HttpURLConnection connex = null;
      OutputStream ostream = null;
      int nServletDown = 0;

      String txt = impostaTesto(rimanenti);

      //tenta 10 volte l'invio di 1 messaggio composta dal testo txt (che possono corriapondere a 1, 2 o 3 sms inviati)
      int MAX_RIP = 10;
      tentativi:for (int i = 0; i < MAX_RIP; i++) {

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
            ///////////////URL u1 = new URL("http://www.myjavaserver.com/servlet/" + frame.servletCombo.getSelectedItem().toString() + ".nimServlet"); //in remoto su www.myjavaserver.com
            URL u1 = new URL("http://127.0.0.1:7070/WebModule1/nimservlet"); //in locale con tomcat5
            connex = (HttpURLConnection) u1.openConnection();
            connex.setInstanceFollowRedirects(false);

            connex.setRequestMethod("POST");
            ////////////////connex.setRequestProperty("Host", "www.myjavaserver.com"); //in remoto su www.myjavaserver.com
            connex.setRequestProperty("Host", "127.0.0.1:7070"); //in locale con tomcat5
            connex.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8) Gecko/20051111 Firefox/1.5");
            connex.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
            connex.setRequestProperty("Accept-Language", "it-it,it;q=0.8,en-us;q=0.5,en;q=0.3");
            connex.setRequestProperty("Accept-Encoding", "gzip,deflate");
            connex.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
            connex.setRequestProperty("Keep-Alive", "300");
            connex.setRequestProperty("Connection", "Keep-Alive");


            //APERTURA DELLA CONNESSIONE -------------------------------------------------------------------------------------------
            connex.setDoOutput(true); //connessione nel caso di un POST


            //SCRITTURA DELL'OUTPUT DEL POST (ovviamente qs 3 righe non esistono con un GET) ---------------------------------------
            ostream = connex.getOutputStream();
            //ostream.write("service=mail&reload=&teldest=&finalurlok=http%3A%2F%2Fwww.rossoalice.virgilio.it%2Falice%2Fportal%2Fservice%2Fentry.do%3Fservice%3Dsms&rchid=36&usr=mauroromeopi&pwd=123456".getBytes());
            ostream.write( ("id=" + id + "&pw=" + pw + "&mitt=" + frame.mittText.getText() + "&action=" + frame.actionCombo.getSelectedItem().toString() + "&num=" + frame.numText.getText() + "&txt=" + txt).getBytes());
            ostream.flush();


            //VERIFICA CHE LA RISPOSTA DEL SERVER SIA OK ---------------------------------------------------------------------------
            int rc = connex.getResponseCode();
            frame.reportArea.append("Tentativo invio #" + (i + 1) + "\n");
            frame.reportArea.append("Risp. Server: " + rc + " " + connex.getResponseMessage() + "\n");
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

                     frame.reportArea.append(hdKey + ": " + hdValue + "\n");
                  }
                  else if (hdKey.toLowerCase().compareTo("sms_rimanenti") == 0) {
                     frame.reportArea.append(hdKey + ": " + hdValue + "\n");
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
               if (i == (MAX_RIP - 1)) {
                  frame.reportArea.append("ERRORE NEL LEGGERE IL NUM DI SMS RIMANENTI!!!!\n");
               }
               continue tentativi; //c'è stato un errore e ritorno alla valutazione del for incrementando il contatore
            }

            if (numI <= 10 && numI >= 0) {
               //ok l'ha inviato bene
               frame.reportArea.append( (rimanenti - numI) + " SMS pieni inviati!!\n");
               break tentativi; //esce dal for (che fa ripetere la procedura INFO 10 volte)
            }




         }
         catch (ClassCastException e) {
            //throw new IllegalArgumentException();
            frame.reportArea.append("!!!!!!!! Non è un HTTP URL!!\n");
         }
         catch (IOException e) {
            frame.reportArea.append("!!!!!!!! Impossibile collegarsi al server!!\n");
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


      } //fine FOR 10 volte

   } //fine inviaSMS()







   private String impostaTesto(int rimanenti) {

      //3 SMS PIENI = 467 caratteri = 3 SMS SCALATI = 4 SMS RICEVUTI
      String _3smsPieni = "3Nel+mezzo+del+cammin+di+nostra+vita+mi+ritrovai+per+una+selva+oscura%2C+che%27+la+diritta+via+era+smarrita.+Ahi+quanto+a+dir+qual+era+e%27+cosa+dura+esta+selva+selvaggia+e+aspra+e+forte+che+nel+pensier+rinova+la+paura%21+Tant%27+e%27+amara+che+poco+e%27+piu%27+morte%3B+ma+per+trattar+del+ben+ch%27i%27+vi+trovai%2C+diro%27+de+l%27altre+cose+ch%27i%27+v%27ho+scorte.+Io+non+so+ben+ridir+com%27+i%27+%27intrai%2C+tant%27+era+pien+di+sonno+a+quel+punto+che+la+verace+via+abbandonai.+Ma+poi+ch%27i%27+fui+al+pie%27+d%27uX";

      //2 SMS PIENI = 307 caratteri = 2 SMS SCALATI = 3 SMS RICEVUTI
      String _2smsPieni = "2Nel+mezzo+del+cammin+di+nostra+vita+mi+ritrovai+per+una+selva+oscura%2C+che%27+la+diritta+via+era+smarrita.+Ahi+quanto+a+dir+qual+era+e%27+cosa+dura+esta+selva+selvaggia+e+aspra+e+forte+che+nel+pensier+rinova+la+paura%21+Tant%27+e%27+amara+che+poco+e%27+piu%27+morte%3B+ma+per+trattar+del+ben+ch%27i%27+vi+trovai%2C+diro%27+de+l%27alX";

      if (rimanenti == 3) { //se sono rimasti solo 3 sms disponibili
         return _3smsPieni;
      }
      else if (rimanenti == 1) { //se è rimasto 1 solo sms disponibile
         return "1Scarico l'unico sms disponibile";
      }
      else { //se sono rimasti 10, 9, 8, 7, 6, 5, 4 o 2 sms disponibili
         return _2smsPieni;
      }

   }






   private int getSMSDaRicevere(String inviatiS) {
      int inviati = Integer.valueOf(inviatiS);

      if (inviati == 10) {
         return 15;
      }
      else if (inviati == 9) {
         return 13;
      }
      else if (inviati == 8) {
         return 12;
      }
      else if (inviati == 7) {
         return 10;
      }
      else if (inviati == 6) {
         return 9;
      }
      else if (inviati == 5) {
         return 7;
      }
      else if (inviati == 4) {
         return 6;
      }
      else if (inviati == 3) {
         return 4;
      }
      else if (inviati == 2) {
         return 3;
      }
      else if (inviati == 1) {
         return 1;
      }
      else { //if (inviati == 0){
         return 0;
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





   public static void suonaSoldi() {
      AudioPlayer p = AudioPlayer.player;
      try {
         AudioStream as = new AudioStream(new FileInputStream("soldi.wav"));
         p.start(as);
      }
      catch (Exception err) {
         err.printStackTrace();
      }
   }





   public static void suonaFine() {
      AudioPlayer p = AudioPlayer.player;
      try {
         AudioStream as = new AudioStream(new FileInputStream("fine.mid"));
         p.start(as);
      }
      catch (Exception err) {
         err.printStackTrace();
      }
   }


} //FINE CLASSE
