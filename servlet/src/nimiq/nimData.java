package nimiq;


import java.util.*;




public class nimData {

   private Calendar cal = null;

   public nimData(Calendar c) {
      cal = c;
   }





   public String toString() { //es. Tue, 21 Mar 2006 20:56:03 +0100
      String str = "";

      switch (cal.get(Calendar.DAY_OF_WEEK)) { //Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
         case 1:
            str = "Sun, ";
            break;
         case 2:
            str = "Mon, ";
            break;
         case 3:
            str = "Tue, ";
            break;
         case 4:
            str = "Wed, ";
            break;
         case 5:
            str = "Thu, ";
            break;
         case 6:
            str = "Fri, ";
            break;
         case 7:
            str = "Sat, ";
            break;
      }


      str += cal.get(Calendar.DAY_OF_MONTH) + " ";


      switch (cal.get(Calendar.MONTH)) { //January February March April May June July August September October November December
         case 0:
            str += "Jan ";
            break;
         case 1:
            str += "Feb ";
            break;
         case 2:
            str += "Mar ";
            break;
         case 3:
            str += "Apr ";
            break;
         case 4:
            str += "May ";
            break;
         case 5:
            str += "Jun ";
            break;
         case 6:
            str += "Jul ";
            break;
         case 7:
            str += "Aug ";
            break;
         case 8:
            str += "Sep ";
            break;
         case 9:
            str += "Oct ";
            break;
         case 10:
            str += "Nov ";
            break;
         case 11:
            str += "Dec ";
            break;
      }


      str += cal.get(Calendar.YEAR) + " ";
      str += cal.get(Calendar.HOUR_OF_DAY) + ":";
      str += cal.get(Calendar.MINUTE) + ":";
      str += cal.get(Calendar.SECOND) + " ";
      str += "+0100";



      return str;
   }

}
