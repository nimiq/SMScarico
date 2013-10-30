package nimiq;


import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;




public class nimServlet extends HttpServlet {


   //Parametri di INPUT
   private String id; //id da usare x l'autenticazione al servere alice
   private String pw; //id da usare x l'autenticazione al servere alice
   private String mitt; //mittente
   private String num; //numero di cellulare del destinatario (es. 3477788908) - è anche un header di OUTPUT
   private String txt; //testo dell'sms - è anche un header di OUTPUT
   private String action; //info o invia o debug - è anche un header di OUTPUT

   //Header di OUTPUT
   private String smsStatus; //Inviato!! o NON inviato!!
   private String smsRimanenti; //boh o 10 o 9 o 8 o ...
   private String erroreServer; //Nessuno o Messaggio HTTP num.3 risposta: ...
   //+ num, txt e action (elencati tra i parametri di INPUT)

   //Messaggio HTTP di output (formato dagli Header elencati sopra + Html)
   private HttpServletResponse msgHttpRisposta; //messaggio HTTP di risposta in output del servlet
   private PrintWriter outHtml; //scrive sul file htm contenuto nel messaggio HTTP di risposta del servlet

   //Procedure
   boolean isInfo = false; //procedura INFO che chiede il num di sms rimanenti
   boolean isInvia = false; //procedura INVIA che invia un sms + email
   boolean isDebug = false; //procedura DEBUG che invia un sms + email e scrive tutte le risposte dei server contattati
   boolean isScarico = false; //procedura SCARICO che manda un sms senza email (qs procedura viene usata da un altro prg che ho fatto io e che si attiva
   // la sera prima delle 24 e controlla tutti i sms rimanenti di tutte le identità e per ogni sms rimanente chiama qs procedura)

   //Connessione, input ed output
   HttpURLConnection connex = null;
   InputStream inputDaServerAlice = null;
   OutputStream outputSuServerAlice = null;







   /**
    * doGet: Funzione eseguita qdo il Servlet riceve una get
    *
    * @param request HttpServletRequest
    * @param r HttpServletResponse
    * @throws ServletException
    * @throws IOException
    */
   public void doGet(HttpServletRequest request, HttpServletResponse r) throws ServletException, IOException {
      msgHttpRisposta = r;

      //Lettura dei parametri di input dalla GET
      id = request.getParameter("id");
      pw = request.getParameter("pw");
      mitt = request.getParameter("mitt");
      num = request.getParameter("num");
      txt = request.getParameter("txt");
      action = request.getParameter("action");

      //Se i parametri sono assenti li sostituisco con "" per evitare eccezioni nel codice successivo
      if (id == null) {
         id = "";
      }
      if (pw == null) {
         pw = "";
      }
      if (mitt == null) {
         mitt = "";
      }
      if (num == null) {
         num = "";
      }
      if (txt == null) {
         txt = "";
      }
      if (action == null) { //la procedura di default è invia
         action = "invia";
      }

      //Inizializzazione di tutti le variabili rimanenti
      smsStatus = "NON inviato!!";
      smsRimanenti = "Boh";
      erroreServer = "Nessuno";

      //Inizializzazione del msg HTTP di risposta
      msgHttpRisposta.setContentType("text/html");
      outHtml = msgHttpRisposta.getWriter();



      //========= CONTROLLLO CORRETTEZZA PARAMETRI ====================================================================
      //Procedura INFO e SCARICO
      if (action.compareToIgnoreCase("info") == 0 ||
          action.compareToIgnoreCase("scarico") == 0) {

         if (action.compareToIgnoreCase("info") == 0) { //procedura INFO
            isInfo = true;
            isInvia = false;
            isDebug = false;
            isScarico = false;
         }
         else if (action.compareToIgnoreCase("scarico") == 0) { //procedura SCARICO
            isInfo = false;
            isInvia = false;
            isDebug = false;
            isScarico = true;
         }


         if (id.compareTo("") == 0) { //se manca l'id
            id = "ID mancante!!";
            stampaReport();
            return;
         }
         else if (pw.compareTo("") == 0) { //se manca la pw
            id = "PW mancante!!";
            stampaReport();
            return;
         }
         /*else if (mitt.compareTo("") == 0) { //se manca il mittente
            mitt = "Mittente mancante!!";
            stampaReport();
            return;
                   }*/
         else {
            outHtml.println("<html>");
            vai(); //se tutto va bene viene lanciata la procedura vai
            stampaReport();
            return;
         }

      } //Procedura DEBUG e INVIA  (hanno gli stessi controlli)
      else if (action.compareToIgnoreCase("debug") == 0 ||
               action.compareToIgnoreCase("invia") == 0) {

         if (action.compareToIgnoreCase("debug") == 0) { //procedura DEBUG
            isInfo = false;
            isInvia = false;
            isDebug = true;
            isScarico = false;
         }
         else if (action.compareToIgnoreCase("invia") == 0) { //procedura INVIA
            isInfo = false;
            isInvia = true;
            isDebug = false;
            isScarico = false;
         }


         if (id.compareTo("") == 0) { //se manca l'id
            id = "ID mancante!!";
            stampaReport();
            return;
         }
         else if (pw.compareTo("") == 0) { //se manca la pw
            id = "PW mancante!!";
            stampaReport();
            return;
         }
         else if (mitt.compareTo("") == 0) { //se manca il mittente
            mitt = "MITTENTE mancante!!";
            stampaReport();
            return;
         }
         else if (txt.compareTo("") == 0) { //se manca il testo dell'sms
            txt = "Mancante!!";
            stampaReport();
            return;
         }
         else if (num.compareTo("") == 0) { //se manca il numero
            num = "Mancante!!";
            stampaReport();
            return;
         }
         else if (num.length() != 10) { //se il numero nn è composto da 10 cifre
            num = "Non è composto da 10 cifre!!";
            stampaReport();
            return;
         }
         else {
            try {
               Long.valueOf(num); //se il numero nn è in formato numerico
            }
            catch (NumberFormatException e) {
               num = "Non è in formato numerico!!";
               stampaReport();
               return;
            }

            outHtml.println("<html>");
            vai(); //se tutto va bene viene lanciata la procedura vai
            stampaReport();
            return;
         }

      }
      else { //action errata (cioè ne INFO nè INVIA nè DEBUG nè SCARICO)
         action = "action puo valere: null, invia, info, debug o scarico!!";
         stampaReport();
         return;
      }
      //---------------------------------------------------------------------------------------------------------------
   } //fine doGet












   /**
    * stampaReport: Crea il msg HTTP di risposta sia in caso di errore sia in caso di spedizione sms avvenuta.
    * Il msg HTTP di risposta è composto da HEADER + HTML.
    * Gli header vengono impostati.
    * La parte html non è altro che la copia degli header.
    */
   private void stampaReport() {
      //NB: Header e Html devono scrivere le stesse cose!!!

      //HEADER
      msgHttpRisposta.addHeader("ID", id);
      msgHttpRisposta.addHeader("MITT", mitt);
      msgHttpRisposta.addHeader("NUM", num);
      msgHttpRisposta.addHeader("TESTO", txt);
      msgHttpRisposta.addHeader("AZIONE", action);
      msgHttpRisposta.addHeader("SMS_STATUS", smsStatus);
      msgHttpRisposta.addHeader("SMS_RIMANENTI", smsRimanenti);
      msgHttpRisposta.addHeader("ERRORE_SERVER", erroreServer);

      //HTML (la copia degli header)
      outHtml.println("<p><BR>DI SEGUITO GLI HEADER DEL MSG HTTP INVIATO COME RISPOSTA DA QS SERVLET AL CLIENT:<BR>");
      outHtml.println("ID: " + id + "<BR>");
      outHtml.println("MITT: " + mitt + "<BR>");
      outHtml.println("NUM: " + num + "<BR>");
      outHtml.println("TESTO: " + txt + "<BR>");
      outHtml.println("AZIONE: " + action + "<BR>");
      outHtml.println("SMS_STATUS: " + smsStatus + "<BR>");
      outHtml.println("SMS_RIMANENTI: " + smsRimanenti + "<BR>");
      outHtml.println("ERRORE_SERVER: " + erroreServer);
      outHtml.println("</p>");
      outHtml.println("</html>");
      outHtml.close();


      //Chiusura connessioni e uscita
      try {
         if (inputDaServerAlice != null) {
            inputDaServerAlice.close();
         }
         if (outputSuServerAlice != null) {
            outputSuServerAlice.close();
         }
         if (connex != null) {
            connex.disconnect();
         }
      }
      catch (IOException e4) {
         erroreServer = e4.toString();
         return;
      }

      return;
   }









   /**
    * doPost: Funzione eseguita qdo il Servlet riceve una get
    *
    * @param request HttpServletRequest
    * @param msgHttpRisposta HttpServletResponse
    * @throws ServletException
    * @throws IOException
    */
   public void doPost(HttpServletRequest request, HttpServletResponse msgHttpRisposta) throws ServletException, IOException {
      doGet(request, msgHttpRisposta);
   } //fine doPost





   //Initialize global variables
   public void init() throws ServletException {
   }






   //Clean up resources
   public void destroy() {
   } //fine destroy







   /**
    * vai: Funzione principale che si occupa di comunicare con il server di Alice
    */
   private void vai() {

      String cookies1 = ""; //memorizza i cookie SUNRISE, SUNRISE2, kp e CALIE_NUP2
      String jsessionid = ""; //memorizza il JSESSIONID
      String location = ""; //memorizza l'header Location
      boolean isErroreServer = false; //vale true se c'è qualche risposta dal server nn prevista (es. mi aspetto un 200 OK, ma ricevo un 302 Moved Temporarily)


      try {

         /***********************************************************************************************************************
          ********************************************* CONNESSIONE #1 - LOGIN1*************************************************/
         //SETTAGGI DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica l'inzio della connessione 1
            outHtml.println("<p>**** CONNESSIONE 1:<BR>");
            outHtml.println("Devo leggere SUNRISE<BR><BR>");
         }
         URL u1 = new URL("http://auth.rossoalice.virgilio.it/aap/validatecredential");
         connex = (HttpURLConnection) u1.openConnection();
         connex.setInstanceFollowRedirects(false);

         connex.setRequestMethod("POST");
         connex.setRequestProperty("Host", "auth.rossoalice.virgilio.it");
         connex.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8) Gecko/20051111 Firefox/1.5");
         connex.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
         connex.setRequestProperty("Accept-Language", "it-it,it;q=0.8,en-us;q=0.5,en;q=0.3");
         connex.setRequestProperty("Accept-Encoding", "gzip,deflate");
         connex.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
         connex.setRequestProperty("Keep-Alive", "300");
         connex.setRequestProperty("Connection", "Keep-Alive");
         connex.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


         //APERTURA DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         //connex.connect();       //connessione nel caso di un GET
         connex.setDoOutput(true); //connessione nel caso di un POST


         //SCRITTURA DELL'OUTPUT DEL POST (ovviamente qs 3 righe non esistono con un GET) ---------------------------------------
         outputSuServerAlice = connex.getOutputStream();
         outputSuServerAlice.write( ("URL_OK=http%3A%2F%2Fauth.rossoalice.virgilio.it%2Faap%2Fserviceforworder%3Fsf_dest%3Dps%26servizio%3Dmail_webmail&URL_KO=http%3A%2F%2Fauth.rossoalice.virgilio.it%2Fps%2Fpages%2Ferror%2Falice%2Flogin_from_ra.jsp%3Fservizio%3Dmail_webmail%26error%3Dok&URL_PROV=http%3A%2F%2Fportale.rossoalice.virgilio.it%2Fps%2Fpages%2Ferror%2Falice%2Fcliente_in_attivazione.jsp&usr=domenik76&login=" + id + "%40alice.it&password=" + pw).getBytes());
         outputSuServerAlice.flush();


         //LETTURA DEGLI HEADER DAL MSG DI RISP DEL SERVER ALICE E ESTRAZ DEI COOKIES -------------------------------------------
         if (isDebug) {
            outHtml.println("RISPOSTA - HEADERS:<BR>");
         }
         int rc = connex.getResponseCode();
         if (rc != 302) {
            if (isDebug) {
               outHtml.println("ERRORE!! RISPOSTA DAL SERVER NON PREVISTA: ");
            }
            erroreServer = "Errore al msg HTTP n.1 = " + rc + " " + connex.getResponseMessage();
            isErroreServer = true;
         }
         if (isDebug) {
            outHtml.println(connex.getResponseCode() + " " + connex.getResponseMessage() + "<BR>");
         }
         int nHd = 0;
         String hdValue = connex.getHeaderField(nHd); //legge l'nHD-esimo header del messaggio HTTP di ** RISPOSTA ** del server Alice
         while (hdValue != null) {
            String hdKey = connex.getHeaderFieldKey(nHd);
            if (hdKey != null) {
               if (hdKey.compareToIgnoreCase("Set-Cookie") == 0) {
                  cookies1 = cookies1 + hdValue.substring(0, hdValue.indexOf(";")) + "; "; //estrazione del cookie SUNRISE
               }

               if (isDebug) { //se la procedura scelta è DEBUG stampo tutti gli header
                  outHtml.println(hdKey + ": " + hdValue + "<BR>");
               }
            }
            nHd++;
            hdValue = connex.getHeaderField(nHd);
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo tutti i cookie
            outHtml.println("<BR>COOKIES ESTRATTI:<BR>");
            outHtml.println(cookies1 + "<BR>");
         }


         //LETTURA DEL CONTENUTO DAL MSG DI RISP DEL SERVER ALICE ** SOLO ** NEL CASO IN CUI LA PROCEDURA SCELTA SIA ** DEBUG **
         if (isDebug) {
            inputDaServerAlice = connex.getInputStream();
            String report = "";
            outHtml.println("<BR>RISPOSTA - HTML:<BR>");
            int ch;
            while ( (ch = inputDaServerAlice.read()) != -1) {
               report = String.valueOf( (char) ch);
               report = HTMLEncoder.encode(report);
               outHtml.print(report);
            }
         }


         //CHIUSURA CONNESSIONE -------------------------------------------------------------------------------------------------
         if (isErroreServer) {
            return;
         }
         else if (connex != null) {
            connex.disconnect();
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica la fine della connessione 1
            outHtml.println("<BR><BR>FINE CONNESSIONE 1</p>");
         }
         //======================================================================================================================










         /***********************************************************************************************************************
          ********************************************* CONNESSIONE #2 - LOGIN2 ************************************************/
         //SETTAGGI DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica l'inzio della connessione 2
            outHtml.println("<p><BR>**** CONNESSIONE 2:<BR>");
            outHtml.println("Devo leggere SUNRISE2<BR><BR>");
         }
         URL u2 = new URL("http://portale.rossoalice.virgilio.it/ps/PortaleServizi.do?servizio=mail_webmail");
         connex = (HttpURLConnection) u2.openConnection();
         connex.setInstanceFollowRedirects(false);

         connex.setRequestMethod("GET");
         connex.setRequestProperty("Host", "portale.rossoalice.virgilio.it");
         connex.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8) Gecko/20051111 Firefox/1.5");
         connex.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
         connex.setRequestProperty("Accept-Language", "it-it,it;q=0.8,en-us;q=0.5,en;q=0.3");
         connex.setRequestProperty("Accept-Encoding", "gzip,deflate");
         connex.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
         connex.setRequestProperty("Keep-Alive", "300");
         connex.setRequestProperty("Connection", "Keep-Alive");
         connex.setRequestProperty("Cookie", cookies1);


         //APERTURA DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         connex.connect(); //connessione nel caso di un GET
         //connex.setDoOutput(true); //connessione nel caso di un POST


         //LETTURA DEGLI HEADER DAL MSG DI RISP DEL SERVER ALICE E ESTRAZ DEI COOKIES -------------------------------------------
         if (isDebug) {
            outHtml.println("RISPOSTA - HEADERS:<BR>");
         }
         rc = connex.getResponseCode();
         if (rc != 200) {
            if (isDebug) {
               outHtml.println("ERRORE!! RISPOSTA DAL SERVER NON PREVISTA: ");
            }
            erroreServer = "Errore al msg HTTP n.2 = " + rc + " " + connex.getResponseMessage();
            isErroreServer = true;
         }
         if (isDebug) {
            outHtml.println(connex.getResponseCode() + " " + connex.getResponseMessage() + "<BR>");
         }
         nHd = 0;
         hdValue = connex.getHeaderField(nHd); //legge l'nHD-esimo header del messaggio HTTP di ** RISPOSTA ** del server Alice
         while (hdValue != null) {
            String hdKey = connex.getHeaderFieldKey(nHd);
            if (hdKey != null) {
               if (hdKey.compareToIgnoreCase("Set-Cookie") == 0) {
                  if (hdValue.startsWith("SUNRISE2")) { //estrazione del cookie SUNRISE2
                     cookies1 = cookies1 + hdValue.substring(0, hdValue.indexOf(";")) + "; ";
                  }
               }

               if (isDebug) { //se la procedura scelta è DEBUG stampo tutti gli header
                  outHtml.println(hdKey + ": " + hdValue + "<BR>");
               }
            }
            nHd++;
            hdValue = connex.getHeaderField(nHd);
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo tutti i cookie
            outHtml.println("<BR>COOKIES ESTRATTI:<BR>");
            outHtml.println(cookies1 + "<BR>");
         }


         //LETTURA DEL CONTENUTO DAL MSG DI RISP DEL SERVER ALICE ** SOLO ** NEL CASO IN CUI LA PROCEDURA SCELTA SIA ** DEBUG **
         if (isDebug) {
            inputDaServerAlice = connex.getInputStream();
            outHtml.println("<BR>RISPOSTA - HTML:<BR>");
            String report = "";
            int ch;
            while ( (ch = inputDaServerAlice.read()) != -1) {
               report = String.valueOf( (char) ch);
               report = HTMLEncoder.encode(report);
               outHtml.print(report);
            }
         }


         //CHIUSURA CONNESSIONE -------------------------------------------------------------------------------------------------
         if (isErroreServer) {
            return;
         }
         else if (connex != null) {
            connex.disconnect();
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica la fine della connessione 1
            outHtml.println("<BR><BR>FINE CONNESSIONE 2</p>");
         }
         //======================================================================================================================










         /***********************************************************************************************************************
          ********************************************* CONNESSIONE #3 - LOGIN3 ************************************************/
         //SETTAGGI DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica l'inzio della connessione 3
            outHtml.println("<p><BR>**** CONNESSIONE 3:<BR>");
            outHtml.println("Devo leggere KP<BR><BR>");
         }
         URL u3 = new URL("http://www.virgilio.it/common/includes/header/inc_header_sunrise.html");
         connex = (HttpURLConnection) u3.openConnection();
         connex.setInstanceFollowRedirects(false);

         connex.setRequestMethod("GET");
         connex.setRequestProperty("Host", "www.virgilio.it");
         connex.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8) Gecko/20051111 Firefox/1.5");
         connex.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
         connex.setRequestProperty("Accept-Language", "it-it,it;q=0.8,en-us;q=0.5,en;q=0.3");
         connex.setRequestProperty("Accept-Encoding", "gzip,deflate");
         connex.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
         connex.setRequestProperty("Keep-Alive", "300");
         connex.setRequestProperty("Connection", "Keep-Alive");
         //connex.setRequestProperty("Refer", "http://www.rossoalice.virgilio.it/alice/portal/service/entry.do?service=sms");
         connex.setRequestProperty("Cookie", cookies1);


         //APERTURA DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         connex.connect(); //connessione nel caso di un GET
         //connex.setDoOutput(true); //connessione nel caso di un POST


         //LETTURA DEGLI HEADER DAL MSG DI RISP DEL SERVER ALICE E ESTRAZ DEI COOKIES -------------------------------------------
         if (isDebug) {
            outHtml.println("RISPOSTA - HEADERS:<BR>");
         }
         rc = connex.getResponseCode();
         if (rc != 200) {
            if (isDebug) {
               outHtml.println("ERRORE!! RISPOSTA DAL SERVER NON PREVISTA: ");
            }
            erroreServer = "Errore al msg HTTP n.3 = " + rc + " " + connex.getResponseMessage();
            isErroreServer = true;
         }
         if (isDebug) {
            outHtml.println(connex.getResponseCode() + " " + connex.getResponseMessage() + "<BR>");
         }
         nHd = 0;
         hdValue = connex.getHeaderField(nHd); //legge l'nHD-esimo header del messaggio HTTP di ** RISPOSTA ** del server Alice
         while (hdValue != null) {
            String hdKey = connex.getHeaderFieldKey(nHd);
            if (hdKey != null) {
               if (hdKey.compareToIgnoreCase("Set-Cookie") == 0) {
                  cookies1 = cookies1 + hdValue.substring(0, hdValue.indexOf(";")) + "; "; //estrazione del cookie KP
               }

               if (isDebug) { //se la procedura scelta è DEBUG stampo tutti gli header
                  outHtml.println(hdKey + ": " + hdValue + "<BR>");
               }
            }
            nHd++;
            hdValue = connex.getHeaderField(nHd);
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo tutti i cookie
            outHtml.println("<BR>COOKIES ESTRATTI:<BR>");
            outHtml.println(cookies1 + "<BR>");
         }


         //LETTURA DEL CONTENUTO DAL MSG DI RISP DEL SERVER ALICE ** SOLO ** NEL CASO IN CUI LA PROCEDURA SCELTA SIA ** DEBUG **
         if (isDebug) {
            inputDaServerAlice = connex.getInputStream();
            outHtml.println("<BR>RISPOSTA - HTML:<BR>");
            String report = "";
            int ch;
            while ( (ch = inputDaServerAlice.read()) != -1) {
               report = String.valueOf( (char) ch);
               report = HTMLEncoder.encode(report);
               outHtml.print(report);
            }
         }


         //CHIUSURA CONNESSIONE -------------------------------------------------------------------------------------------------
         if (isErroreServer) {
            return;
         }
         else if (connex != null) {
            connex.disconnect();
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica la fine della connessione 1
            outHtml.println("<BR><BR>FINE CONNESSIONE 3</p>");
         }
         //======================================================================================================================










         /***********************************************************************************************************************
          ********************************************* CONNESSIONE #4 - LOGIN4 ************************************************/
         //SETTAGGI DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica l'inzio della connessione 4
            outHtml.println("<p><BR>**** CONNESSIONE 4:<BR>");
            outHtml.println("Devo leggere LOCATION<BR><BR>");
         }
         URL u4 = new URL("http://auth.rossoalice.virgilio.it/aap/serviceforwarder?sf_dest=mail_webmail");
         connex = (HttpURLConnection) u4.openConnection();
         connex.setInstanceFollowRedirects(false);

         connex.setRequestMethod("GET");
         connex.setRequestProperty("Host", "auth.rossoalice.virgilio.it");
         connex.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8) Gecko/20051111 Firefox/1.5");
         connex.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
         connex.setRequestProperty("Accept-Language", "it-it,it;q=0.8,en-us;q=0.5,en;q=0.3");
         connex.setRequestProperty("Accept-Encoding", "gzip,deflate");
         connex.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
         connex.setRequestProperty("Keep-Alive", "300");
         connex.setRequestProperty("Connection", "Keep-Alive");
         //connex.setRequestProperty("Refer", "http://www.rossoalice.virgilio.it/alice/portal/service/entry.do?service=sms");
         connex.setRequestProperty("Cookie", cookies1);


         //APERTURA DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         connex.connect(); //connessione nel caso di un GET
         //connex.setDoOutput(true); //connessione nel caso di un POST


         //LETTURA DEGLI HEADER DAL MSG DI RISP DEL SERVER ALICE E ESTRAZ DI LOCATION -------------------------------------------
         if (isDebug) {
            outHtml.println("RISPOSTA - HEADERS:<BR>");
         }
         rc = connex.getResponseCode();
         if (rc != 302) {
            if (isDebug) {
               outHtml.println("ERRORE!! RISPOSTA DAL SERVER NON PREVISTA: ");
            }
            erroreServer = "Errore al msg HTTP n.4 = " + rc + " " + connex.getResponseMessage();
            isErroreServer = true;
         }
         if (isDebug) {
            outHtml.println(connex.getResponseCode() + " " + connex.getResponseMessage() + "<BR>");
         }
         nHd = 0;
         hdValue = connex.getHeaderField(nHd); //legge l'nHD-esimo header del messaggio HTTP di ** RISPOSTA ** del server Alice
         while (hdValue != null) {
            String hdKey = connex.getHeaderFieldKey(nHd);
            if (hdKey != null) {
               if (hdKey.compareToIgnoreCase("Location") == 0) {
                  location = hdValue; //estrazione di location
               }

               if (isDebug) { //se la procedura scelta è DEBUG stampo tutti gli header
                  outHtml.println(hdKey + ": " + hdValue + "<BR>");
               }
            }
            nHd++;
            hdValue = connex.getHeaderField(nHd);
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo tutti i cookie
            outHtml.println("<BR>COOKIES ESTRATTI:<BR>");
            outHtml.println(cookies1 + "<BR>");
            outHtml.println("LOCATION: " + location + "<BR>");
         }


         //LETTURA DEL CONTENUTO DAL MSG DI RISP DEL SERVER ALICE ** SOLO ** NEL CASO IN CUI LA PROCEDURA SCELTA SIA ** DEBUG **
         if (isDebug) {
            inputDaServerAlice = connex.getInputStream();
            outHtml.println("<BR>RISPOSTA - HTML:<BR>");
            String report = "";
            int ch;
            while ( (ch = inputDaServerAlice.read()) != -1) {
               report = String.valueOf( (char) ch);
               report = HTMLEncoder.encode(report);
               outHtml.print(report);
            }
         }


         //CHIUSURA CONNESSIONE -------------------------------------------------------------------------------------------------
         if (isErroreServer) {
            return;
         }
         else if (connex != null) {
            connex.disconnect();
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica la fine della connessione 1
            outHtml.println("<BR><BR>FINE CONNESSIONE 4</p>");
         }
         //======================================================================================================================










         /***********************************************************************************************************************
          ********************************************* CONNESSIONE #5 - LOGIN5 ************************************************/
         //SETTAGGI DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica l'inzio della connessione 5
            outHtml.println("<p><BR>**** CONNESSIONE 5:<BR>");
            outHtml.println("Devo leggere CALIE_NUP2<BR><BR>");
         }
         URL u5 = new URL(location);
         connex = (HttpURLConnection) u5.openConnection();
         connex.setInstanceFollowRedirects(false);

         connex.setRequestMethod("GET");
         connex.setRequestProperty("Host", "mail.rossoalice.virgilio.it");
         connex.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8) Gecko/20051111 Firefox/1.5");
         connex.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
         connex.setRequestProperty("Accept-Language", "it-it,it;q=0.8,en-us;q=0.5,en;q=0.3");
         connex.setRequestProperty("Accept-Encoding", "gzip,deflate");
         connex.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
         connex.setRequestProperty("Keep-Alive", "300");
         connex.setRequestProperty("Connection", "Keep-Alive");
         connex.setRequestProperty("Cookie", cookies1);


         //APERTURA DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         connex.connect(); //connessione nel caso di un GET
         //connex.setDoOutput(true); //connessione nel caso di un POST


         //LETTURA DEGLI HEADER DAL MSG DI RISP DEL SERVER ALICE ----------------------------------------------------------------
         if (isDebug) {
            outHtml.println("RISPOSTA - HEADERS:<BR>");
         }
         rc = connex.getResponseCode();
         if (rc != 200) {
            if (isDebug) {
               outHtml.println("ERRORE!! RISPOSTA DAL SERVER NON PREVISTA: ");
            }
            erroreServer = "Errore al msg HTTP n.5 = " + rc + " " + connex.getResponseMessage();
            isErroreServer = true;
         }
         if (isDebug) {
            outHtml.println(connex.getResponseCode() + " " + connex.getResponseMessage() + "<BR>");
         }
         nHd = 0;
         hdValue = connex.getHeaderField(nHd); //legge l'nHD-esimo header del messaggio HTTP di ** RISPOSTA ** del server Alice
         while (hdValue != null) {
            String hdKey = connex.getHeaderFieldKey(nHd);
            if (hdKey != null) {
               if (hdKey.compareToIgnoreCase("Set-Cookie") == 0) {
                  if (hdValue.startsWith("CALIE_NUP2")) { //estrazione del cookie CALIE_NUP2
                     cookies1 = cookies1 + hdValue.substring(0, hdValue.indexOf(";")) + "; ";
                  }
               }

               if (isDebug) { //se la procedura scelta è DEBUG stampo tutti gli header
                  outHtml.println(hdKey + ": " + hdValue + "<BR>");
               }
            }
            nHd++;
            hdValue = connex.getHeaderField(nHd);
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo tutti i cookie
            outHtml.println("<BR>COOKIES ESTRATTI:<BR>");
            outHtml.println(cookies1 + "<BR>");
            outHtml.println("LOCATION: " + location + "<BR>");
         }


         //LETTURA DEL CONTENUTO DAL MSG DI RISP DEL SERVER ALICE ** SOLO ** NEL CASO IN CUI LA PROCEDURA SCELTA SIA ** DEBUG **
         if (isDebug) {
            inputDaServerAlice = connex.getInputStream();
            outHtml.println("<BR>RISPOSTA - HTML:<BR>");
            String report = "";
            int ch;
            while ( (ch = inputDaServerAlice.read()) != -1) {
               report = String.valueOf( (char) ch);
               report = HTMLEncoder.encode(report);
               outHtml.print(report);
            }
         }


         //CHIUSURA CONNESSIONE -------------------------------------------------------------------------------------------------
         if (isErroreServer) {
            return;
         }
         else if (connex != null) {
            connex.disconnect();
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica la fine della connessione 1
            outHtml.println("<BR><BR>FINE CONNESSIONE 5</p>");
         }
         //======================================================================================================================










         /***********************************************************************************************************************
          ********************************************* CONNESSIONE #6 - RICHIESTA SERVIZIO SMS1 *******************************/
         //SETTAGGI DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica l'inzio della connessione 6
            outHtml.println("<p><BR>**** CONNESSIONE 6:<BR>");
            outHtml.println("Devo leggere LOCATION<BR><BR>");
         }
         URL u6 = new URL("HTTP://auth.rossoalice.virgilio.it/aap/serviceforwarder?sf_dest=sms_inviosmsalice");
         connex = (HttpURLConnection) u6.openConnection();
         connex.setInstanceFollowRedirects(false);

         connex.setRequestMethod("GET");
         connex.setRequestProperty("Host", "auth.rossoalice.virgilio.it");
         connex.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8) Gecko/20051111 Firefox/1.5");
         connex.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
         connex.setRequestProperty("Accept-Language", "it-it,it;q=0.8,en-us;q=0.5,en;q=0.3");
         connex.setRequestProperty("Accept-Encoding", "gzip,deflate");
         connex.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
         connex.setRequestProperty("Keep-Alive", "300");
         connex.setRequestProperty("Connection", "Keep-Alive");
         connex.setRequestProperty("Cookie", cookies1);


         //APERTURA DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         connex.connect(); //connessione nel caso di un GET
         //connex.setDoOutput(true); //connessione nel caso di un POST


         //LETTURA DEGLI HEADER DAL MSG DI RISP DEL SERVER ALICE E ESTRAZ DI LOCATION -------------------------------------------
         if (isDebug) {
            outHtml.println("RISPOSTA - HEADERS:<BR>");
         }
         rc = connex.getResponseCode();
         if (rc != 302) {
            if (isDebug) {
               outHtml.println("ERRORE!! RISPOSTA DAL SERVER NON PREVISTA: ");
            }
            erroreServer = "Errore al msg HTTP n.6 = " + rc + " " + connex.getResponseMessage();
            isErroreServer = true;
         }
         if (isDebug) {
            outHtml.println(connex.getResponseCode() + " " + connex.getResponseMessage() + "<BR>");
         }
         nHd = 0;
         hdValue = connex.getHeaderField(nHd); //legge l'nHD-esimo header del messaggio HTTP di ** RISPOSTA ** del server Alice
         while (hdValue != null) {
            String hdKey = connex.getHeaderFieldKey(nHd);
            if (hdKey != null) {
               if (hdKey.compareToIgnoreCase("Location") == 0) {
                  location = hdValue;
               }

               if (isDebug) { //se la procedura scelta è DEBUG stampo tutti gli header
                  outHtml.println(hdKey + ": " + hdValue + "<BR>");
               }
            }
            nHd++;
            hdValue = connex.getHeaderField(nHd);
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo tutti i cookie
            outHtml.println("<BR>COOKIES ESTRATTI:<BR>");
            outHtml.println(cookies1 + "<BR>");
            outHtml.println("LOCATION: " + location + "<BR>");
         }


         //LETTURA DEL CONTENUTO DAL MSG DI RISP DEL SERVER ALICE ** SOLO ** NEL CASO IN CUI LA PROCEDURA SCELTA SIA ** DEBUG **
         if (isDebug) {
            inputDaServerAlice = connex.getInputStream();
            outHtml.println("<BR>RISPOSTA - HTML:<BR>");
            String report = "";
            int ch;
            while ( (ch = inputDaServerAlice.read()) != -1) {
               report = String.valueOf( (char) ch);
               report = HTMLEncoder.encode(report);
               outHtml.print(report);
            }
         }


         //CHIUSURA CONNESSIONE -------------------------------------------------------------------------------------------------
         if (isErroreServer) {
            return;
         }
         else if (connex != null) {
            connex.disconnect();
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica la fine della connessione 1
            outHtml.println("<BR><BR>FINE CONNESSIONE 6</p>");
         }
         //======================================================================================================================









         /***********************************************************************************************************************
          ********************************************* CONNESSIONE #7 - RICHIESTA SERVIZIO SMS2 *******************************/
         //SETTAGGI DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica l'inzio della connessione 7
            outHtml.println("<p><BR>**** CONNESSIONE 7:<BR>");
            outHtml.println("Devo leggere JSESSIONID<BR><BR>");
         }
         URL u7 = new URL(location);
         connex = (HttpURLConnection) u7.openConnection();
         connex.setInstanceFollowRedirects(false);

         connex.setRequestMethod("GET");
         connex.setRequestProperty("Host", "sms.alice.it");
         connex.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8) Gecko/20051111 Firefox/1.5");
         connex.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
         connex.setRequestProperty("Accept-Language", "it-it,it;q=0.8,en-us;q=0.5,en;q=0.3");
         connex.setRequestProperty("Accept-Encoding", "gzip,deflate");
         connex.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
         connex.setRequestProperty("Keep-Alive", "300");
         connex.setRequestProperty("Connection", "Keep-Alive");
         //connex.setRequestProperty("Cookie", cookies1);


         //APERTURA DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         connex.connect(); //connessione nel caso di un GET
         //connex.setDoOutput(true); //connessione nel caso di un POST


         //LETTURA DEGLI HEADER DAL MSG DI RISP DEL SERVER ALICE ----------------------------------------------------------------
         if (isDebug) {
            outHtml.println("RISPOSTA - HEADERS:<BR>");
         }
         rc = connex.getResponseCode();
         if (rc != 200) {
            if (isDebug) {
               outHtml.println("ERRORE!! RISPOSTA DAL SERVER NON PREVISTA: ");
            }
            erroreServer = "Errore al msg HTTP n.7 = " + rc + " " + connex.getResponseMessage();
            isErroreServer = true;
         }
         if (isDebug) {
            outHtml.println(connex.getResponseCode() + " " + connex.getResponseMessage() + "<BR>");
         }
         nHd = 0;
         hdValue = connex.getHeaderField(nHd); //legge l'nHD-esimo header del messaggio HTTP di ** RISPOSTA ** del server Alice
         while (hdValue != null) {
            String hdKey = connex.getHeaderFieldKey(nHd);
            if (hdKey != null) {
               if (hdKey.compareToIgnoreCase("Set-Cookie") == 0) {
                  jsessionid = hdValue.substring(0, hdValue.indexOf(";")) + "; ";
               }

               if (isDebug) { //se la procedura scelta è DEBUG stampo tutti gli header
                  outHtml.println(hdKey + ": " + hdValue + "<BR>");
               }
            }
            nHd++;
            hdValue = connex.getHeaderField(nHd);
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo tutti i cookie
            outHtml.println("<BR>JSESSIONID ESTRATTO:<BR>");
            outHtml.println(jsessionid + "<BR>");
            outHtml.println("LOCATION: " + location + "<BR>");
         }


         //LETTURA DEL CONTENUTO DAL MSG DI RISP DEL SERVER ALICE E ESTRAZIONE DEL N SMS RESTANTI -------------------------------
         if (isDebug) {
            outHtml.println("<BR>RISPOSTA - HTML:<BR>");
         }
         String report = "";
         StringBuffer sb = new StringBuffer(3500);
         inputDaServerAlice = connex.getInputStream();
         int ch;
         //while ( (ch = inputDaServerAlice.read()) != -1) {
         for (int i = 0; i < 3500; i++) { //leggo solo 3500 caratteri altrimenti lo StringBuffer diventa troppo grande e estrazione del num di sms restanti
            if ( (ch = inputDaServerAlice.read()) != -1) {
               report = String.valueOf( (char) ch);
               sb.append(report);
               if (isDebug) {
                  report = HTMLEncoder.encode(report);
                  outHtml.print(report);
               }
            }
         }

         //leggo il numero di sms rimanenti
         if (sb.indexOf("possibile inviare gratuitamente fino a 10 SMS al giorno") != -1) {
            smsRimanenti = "0 - Hai gia raggiunto i 10 sms!!";
            return;
         }
         else if (sb.indexOf("Puoi inviare ancora un messaggio gratis") != -1) {
            smsRimanenti = "1";
         }
         else {
            int index = sb.indexOf("<td><input disabled type=\"text\" name=\"n-msg\" size=\"2\" maxlength=\"3\" class=\"input2\" value=");
            try { //se il num di sms rimanenti è una doppia cifra (tipo 10)
               smsRimanenti = Integer.valueOf(sb.substring(index + 89, index + 91)).toString();
            }
            catch (NumberFormatException ex1) { //se il num di sms rimanenti è una singola cifra (tipo 9)
               try {
                  smsRimanenti = Integer.valueOf(sb.substring(index + 89, index + 90)).toString();
               }
               catch (NumberFormatException ex2) { //se c'è stato un errore
                  erroreServer = "Errore al msg HTTP n.7 = Non riesco a leggere numero di sms rimanenti!!";
                  return;
               }
            }
         }

         if (isDebug) { //stampo dal carattere 3501 fino alla fine
            report = "";
            while ( (ch = inputDaServerAlice.read()) != -1) {
               report = String.valueOf( (char) ch);
               report = HTMLEncoder.encode(report);
               outHtml.print(report);
            }
         }


         //CHIUSURA CONNESSIONE -------------------------------------------------------------------------------------------------
         if (isErroreServer) {
            return;
         }
         else if (connex != null) {
            connex.disconnect();
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica la fine della connessione 1
            outHtml.println("<BR><BR>FINE CONNESSIONE 7</p>");
         }
         //======================================================================================================================









         if (isInvia || isDebug || isScarico) {
            /***********************************************************************************************************************
             ********************************************* CONNESSIONE #8 - INVIO SMS **********************************************/
            //SETTAGGI DELLA CONNESSIONE -------------------------------------------------------------------------------------------
            if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica l'inzio della connessione 8
               outHtml.println("<p><BR>**** CONNESSIONE 8:<BR>");
               outHtml.println("Invio SMS<BR><BR>");
            }
            URL u8 = new URL("HTTP://sms.alice.it/scu187/wond_inviaSms.do;" + jsessionid);
            connex = (HttpURLConnection) u8.openConnection();
            connex.setInstanceFollowRedirects(false);

            connex.setRequestMethod("POST");
            connex.setRequestProperty("Host", "sms.alice.it");
            connex.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8) Gecko/20051111 Firefox/1.5");
            connex.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
            connex.setRequestProperty("Accept-Language", "it-it,it;q=0.8,en-us;q=0.5,en;q=0.3");
            connex.setRequestProperty("Accept-Encoding", "gzip,deflate");
            connex.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
            connex.setRequestProperty("Keep-Alive", "300");
            connex.setRequestProperty("Connection", "Keep-Alive");
            connex.setRequestProperty("Cookie", jsessionid);


            //APERTURA DELLA CONNESSIONE -------------------------------------------------------------------------------------------
            //connex.connect(); //connessione nel caso di un GET
            connex.setDoOutput(true); //connessione nel caso di un POST


            //SCRITTURA DELL'OUTPUT DEL POST ---------------------------------------------------------------------------------------
            String txtBuf = txt;
            txtBuf = txtBuf.replaceAll("%", "%25"); //Sostituisco ogni % presente nel testo dell'sms con %25 (DEVE ESSERE LA PRIMA ISTR)

            StringBuffer buf = new StringBuffer(txtBuf); //Sostituisco ogni + presente nel testo dell'sms con %2B (DEVE ESSERE LA SECONDA ISTR)
            for (int i = 0; i < buf.length(); i++) { //Non è possibile farlo con un replaceAll perchè genera un'eccezione
               if (buf.charAt(i) == '+') {
                  buf.deleteCharAt(i);
                  buf.insert(i, "%2B");
               }
            }

            txtBuf = buf.toString();
            txtBuf = txtBuf.replaceAll("&", "%26"); //Sostituisco ogni & presente nel testo dell'sms con %26
            txtBuf = txtBuf.replaceAll("=", "%3D"); //Sostituisco ogni = presente nel testo dell'sms con %3D
            txtBuf = txtBuf.replaceAll("#", "%23"); //Sostituisco ogni # presente nel testo dell'sms con %23
            txtBuf = txtBuf.replaceAll(" ", "+"); //Sostituisco ogni spazio presente nel testo dell'sms con +

            outputSuServerAlice = connex.getOutputStream();
            outputSuServerAlice.write( ("prefisso=" + num.substring(0, 3) + "&numDest=" + num.substring(3, num.length()) + "&destFax=&nomeDest=&cognomeDest=&invio=0&data=&ora=&minuti=&testo=" + txtBuf + "&insNumMittente=0").getBytes());
            outputSuServerAlice.flush();


            //LETTURA DEGLI HEADER DAL MSG DI RISP DEL SERVER ALICE ----------------------------------------------------------------
            if (isDebug) {
               outHtml.println("RISPOSTA - HEADERS:<BR>");
               outHtml.println(connex.getResponseCode() + " " + connex.getResponseMessage() + "<BR>");
            }
            nHd = 0;
            hdValue = connex.getHeaderField(nHd); //legge l'nHD-esimo header del messaggio HTTP di ** RISPOSTA ** del server Alice
            if (isDebug) {
               while (hdValue != null) {
                  String hdKey = connex.getHeaderFieldKey(nHd);
                  outHtml.println(hdKey + ": " + hdValue + "<BR>");
                  nHd++;
                  hdValue = connex.getHeaderField(nHd);
               }
               outHtml.println("<BR>JSESSIONID ESTRATTO:<BR>");
               outHtml.println(jsessionid + "<BR>");
               outHtml.println("LOCATION: " + location + "<BR>");
            }


            //INVIO EMAIL (non viene fatta nel caso di procedura SCARICO)
            if (isInvia || isDebug) {
               EmailSender mail = new EmailSender(id, pw, mitt, num, txt, isDebug, outHtml);
               mail.invia();
            }

            //LETTURA DEL CONTENUTO DAL MSG DI RISP DEL SERVER ALICE E ESTRAZNE DI "OK" CHE CONFERMA L'AVVENUTO INVIO DELL'SMS
            if (isDebug) {
               outHtml.println("<BR>RISPOSTA - HTML:<BR>");
            }
            report = "";
            sb = new StringBuffer(4500);
            inputDaServerAlice = connex.getInputStream();
            //while ( (ch = inputDaServerAlice.read()) != -1) {
            for (int i = 0; i < 4500; i++) { //leggo solo 3000 caratteri altrimenti lo StringBuffer diventa troppo grande e estrazione del num di sms restanti
               if ( (ch = inputDaServerAlice.read()) != -1) {
                  report = String.valueOf( (char) ch);
                  sb.append(report);
                  if (isDebug) {
                     report = HTMLEncoder.encode(report);
                     outHtml.print(report);
                  }
               }
            }

            //Controllo cha la pagina stampi l'OK che conferma l'invio dell'sms
            if (sb.indexOf("possibile inviare gratuitamente fino a 10 SMS al giorno") != -1) {
               smsStatus = "Inviato!!";
            }
            else if (sb.indexOf("<td class=\"white11\" style=\"padding-top: 5px; padding-bottom: 5px;\">OK</td>") == -1) {
               smsStatus = "NON inviato!!";
               erroreServer = "Errore al msg HTTP n.8 = Non riesco a leggere l'OK dopo l'invio dell'sms!!";
               stampaReport();
               return;
            }
            else {
               smsStatus = "Inviato!!";
            }

            //leggo il numero di sms rimanenti
            if (sb.indexOf("possibile inviare gratuitamente fino a 10 SMS al giorno") != -1) {
               smsRimanenti = "0 - Qs era l'ultimo dei 10 sms!!";
            }
            else if (sb.indexOf("Puoi inviare ancora un messaggio gratis") != -1) {
               smsRimanenti = "1";
            }
            else {
               int index = sb.indexOf("<td><input disabled type=\"text\" name=\"n-msg\" size=\"2\" maxlength=\"3\" class=\"input2\" value=");
               try { //se il num di sms rimanenti è una doppia cifra (tipo 10)
                  smsRimanenti = Integer.valueOf(sb.substring(index + 89, index + 91)).toString();
               }
               catch (NumberFormatException ex1) { //se il num di sms rimanenti è una singola cifra (tipo 9)
                  try {
                     smsRimanenti = Integer.valueOf(sb.substring(index + 89, index + 90)).toString();
                  }
                  catch (NumberFormatException ex2) { //se c'è stato un errore
                     erroreServer = "Errore al msg HTTP n.8 = Non riesco a leggere numero di sms rimanenti!!";
                     return;
                  }
               }
            }

            if (isDebug) { //stampo dal carattere 3501 fino alla fine
               report = "";
               while ( (ch = inputDaServerAlice.read()) != -1) {
                  report = String.valueOf( (char) ch);
                  report = HTMLEncoder.encode(report);
                  outHtml.print(report);
               }
            }


            //CHIUSURA CONNESSIONE -------------------------------------------------------------------------------------------------
            if (isErroreServer) {
               return;
            }
            else if (connex != null) {
               connex.disconnect();
            }
            if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica la fine della connessione 1
               outHtml.println("<BR><BR>FINE CONNESSIONE 8</p>");
            }
            //======================================================================================================================
         }









         /***********************************************************************************************************************
          ********************************************* CONNESSIONE #9 - LOGOUT ************************************************/
         //SETTAGGI DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica l'inzio della connessione 9
            outHtml.println("<p><BR>**** CONNESSIONE 9:<BR>");
            outHtml.println("Logout<BR><BR>");
         }
         URL u9 = new URL("http://auth.rossoalice.virgilio.it/aap/deletecredential");
         connex = (HttpURLConnection) u9.openConnection();
         connex.setInstanceFollowRedirects(false);

         connex.setRequestMethod("GET");
         connex.setRequestProperty("Host", "auth.rossoalice.virgilio.it");
         connex.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8) Gecko/20051111 Firefox/1.5");
         connex.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
         connex.setRequestProperty("Accept-Language", "it-it,it;q=0.8,en-us;q=0.5,en;q=0.3");
         connex.setRequestProperty("Accept-Encoding", "gzip,deflate");
         connex.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
         connex.setRequestProperty("Keep-Alive", "300");
         connex.setRequestProperty("Connection", "Keep-Alive");
         connex.setRequestProperty("Cookie", cookies1);


         //APERTURA DELLA CONNESSIONE -------------------------------------------------------------------------------------------
         connex.connect(); //connessione nel caso di un GET
         //connex.setDoOutput(true); //connessione nel caso di un POST


         //LETTURA DEGLI HEADER DAL MSG DI RISP DEL SERVER ALICE E ESTRAZ DI LOCATION -------------------------------------------
         if (isDebug) {
            outHtml.println("RISPOSTA - HEADERS:<BR>");
            outHtml.println(connex.getResponseCode() + " " + connex.getResponseMessage() + "<BR>");
         }
         nHd = 0;
         hdValue = connex.getHeaderField(nHd); //legge l'nHD-esimo header del messaggio HTTP di ** RISPOSTA ** del server Alice
         if (isDebug) {
            while (hdValue != null) {
               String hdKey = connex.getHeaderFieldKey(nHd);
               outHtml.println(hdKey + ": " + hdValue + "<BR>");
               nHd++;
               hdValue = connex.getHeaderField(nHd);
            }
            outHtml.println("<BR>JSESSIONID ESTRATTO:<BR>");
            outHtml.println(jsessionid + "<BR>");
            outHtml.println("LOCATION: " + location + "<BR>");
         }


         //LETTURA DEL CONTENUTO DAL MSG DI RISP DEL SERVER ALICE ** SOLO ** NEL CASO IN CUI LA PROCEDURA SCELTA SIA ** DEBUG **
         if (isDebug) {
            inputDaServerAlice = connex.getInputStream();
            outHtml.println("<BR>RISPOSTA - HTML:<BR>");
            report = "";
            //int ch;
            while ( (ch = inputDaServerAlice.read()) != -1) {
               report = String.valueOf( (char) ch);
               report = HTMLEncoder.encode(report);
               outHtml.print(report);
            }
         }


         //CHIUSURA CONNESSIONE -------------------------------------------------------------------------------------------------
         if (connex != null) {
            connex.disconnect();
         }
         if (isDebug) { //se la procedura scelta è DEBUG stampo una riga che indica la fine della connessione 1
            outHtml.println("<BR><BR>FINE CONNESSIONE 9</p>");
         }
         //======================================================================================================================


      } //fine try





      catch (ClassCastException e1) {
         //throw new IllegalArgumentException("Not an HTTP URL");
         erroreServer = e1.toString();
         return;
      }
      catch (IOException e2) {
         erroreServer = "Impossbile collegarsi al server Alice. " + e2.toString();
         return;
      }
      /*catch (Exception e3) {
         erroreServer = "Gettata eccezione: " + e3.toString();
         return;
             }*/
      finally {
         try {
            if (inputDaServerAlice != null) {
               inputDaServerAlice.close();
            }
            if (outputSuServerAlice != null) {
               outputSuServerAlice.close();
            }
            if (connex != null) {
               connex.disconnect();
            }
            //stampaReport();
            //return;
         }
         catch (IOException e4) {
            erroreServer = e4.toString();
            return;
         }
      }
   } //fine vai()




} //FINE CLASSE
