package sms_scarico;


import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.IOException;



public class Timer implements Runnable {

   Thread thread;
   DialogSveglia ds;
   int minMancanti = 2000;
   private boolean alive = false;


   public Timer(DialogSveglia dialog) {
      thread = new Thread(this);
      ds = dialog;
   }




   public void run(){
      alive = true;
      ds.statoLabel.setHorizontalAlignment(SwingConstants.LEFT);
      ds.statoLabel.setText("Sveglia IMPOSTATA!!");
      controllaOra();
   }



   public void stop(){
      alive = false;
   }



   public void controllaOra() {
      //boolean bipbip = false; //indica che la sveglia suona

      while (alive) {
         Calendar calendar = Calendar.getInstance();

         int[] h2 = { (Integer) ds.hSpinner.getValue(), (Integer) ds.mSpinner.getValue()}; //leggo l'ora impostata per la sveglia
         int[] h1 = {calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)}; //leggo l'ora attuale
         int[] r = sottrai(h1, h2); //esegue h2 - h1

         ds.mancaLabel.setText("- " + r[0] + "h " + r[1] + "m"); //stampa il tempo mancante
         ds.mancaLabel.setVisible(true);
         int minMancantiBuf = r[0] * 60 + r[1]; //converte il tempo mancante in minuti

         if (minMancantiBuf == 0) { //è l'ora X e devo far scattare l'allarme
            alive = false;
            svegliaScattata();
         } else if (minMancantiBuf < minMancanti) { //mi sto avvicinando all'ora X, devo dormire per 1 minuto
            minMancanti = minMancantiBuf;

            try {
               thread.sleep(60 * 1000); //dormo per 1 minuto
            }
            catch (InterruptedException ex) {
            }

         } else if ( (minMancantiBuf > minMancanti) && (minMancantiBuf > ((24*60)-10) ) ) { //ho già passato l'ora x e devo far scattare l'allarme
            /* Ti chiederai: com'è possibile passare l'ora X senza far scattare l'allarme se il thread dorme al max per 1 min?
             *  Supponiamo che il thread dorma x 1 minuto, poi qdo è il momento di ripartire, il pc è superimpegnato perchè
             *  x.es. edonkey sta sfruttando il 99% della cpu, il thread subisce un ritardo e l'ora X viene attraversata senza
             *  far scattare l'allarme.
             * La seconda parte della &&, cioè: minMancantiBuf < ((24*60)-10)   serve per verificare che l'ora X sia passata da
             *  meno di 10 minuti (es. l'ora X è passata da 2 minuti --> minMancantiBuf vale 1438 (= 24*60-2))
             */
            alive = false;
            svegliaScattata();
         }

      }
      //fr.jTextArea1.append("M = " + minMancanti);
  }//fine controllaOra



  public static int[] sottrai(int[] h1, int[] h2){ //fa h2-h1

     int[] r = {0, 0};

     if (h2[1] < h1[1]) { //se i minuti di h2 sono inferiori a quelli di h1 chiedo il riporto di 60 (= 1 ora)
        h2[1] += 60;
        if (h2[0] == 0)
           h2[0] = 23;
        else
           h2[0] --;
     }
     r[1] = h2[1] - h1[1];


     if (h2[0] < h1[0]) //se le ore di h2 sono inferiori a quelle di h1 chiedo il riporto di 24 (= 1 giorno)
        h2[0] += 24;

     r[0] = h2[0] - h1[0];


     return r;
  }








  public void svegliaScattata() {
     ds.statoLabel.setHorizontalAlignment(SwingConstants.CENTER); //centrato
     ds.mancaLabel.setForeground(new Color(124, 0, 0)); //rosso
     ds.statoLabel.setText("Sveglia SUONATA!!");
     ds.mancaLabel.setVisible(false);
     ds.svegliaButton.setText("CHIUDI");



     //ORA LA SVEGLIA E' SCATTATA: UCCIDO I PROCESSI E SCARICO GLI SMS
     //Uccisione dei processi
     if (ds.chiudiCheck.isSelected()) {
        StringTokenizer st = new StringTokenizer(ds.chiudiText.getText(), ";");
        while (st.hasMoreTokens()) {
           Process p = null;
           try {
              p = Runtime.getRuntime().exec("pskill -t " + st.nextToken());
              p.waitFor();
           }
           catch (InterruptedException ex1) {
           }
           catch (IOException ex) {
           }
        }

     }

     //Scarico degli SMS
     //JOptionPane.showMessageDialog(null, "Scarico sms", "ATTENZIONE", JOptionPane.INFORMATION_MESSAGE);
     ds.fr.scaricaButton_actionPerformed(null);
  }



}
