package sms_j2me;


public class Sms {
   private String id = "";
   private String pw = "";
   private String mitt = "";
   private long num = 0;
   private String testo = "";
   private String azione = "";
   private String servlet = "";

   public Sms() {
   }






   public void setId(String s) {
      id = s;
   }

   public void setPw(String s) {
      pw = s;
   }
   public void setMitt(String s) {
      mitt = s;
   }
   public void setNum(long s) {
      num = s;
   }
   public void setTesto(String s) {
      testo = s;
   }
   public void setAzione(String s) {
      azione = s;
   }
   public void setServlet(String s) {
      servlet = s;
   }



   public String getId(){
      return id;
   }
   public String getPw(){
      return pw;
   }
   public String getMitt(){
      return mitt;
   }
   public long getNum(){
      return num;
   }
   public String getTesto(){
      return testo;
   }
   public String getAzione(){
      return azione;
   }
   public String getServlet(){
      return servlet;
   }

}
