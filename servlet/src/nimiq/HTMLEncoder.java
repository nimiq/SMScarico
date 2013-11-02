package nimiq;


public class HTMLEncoder {

   public static String encode(String str) {

      if (str.compareTo("<") == 0) {
         str = "&lt;";
      }
      else if (str.compareTo(">") == 0) {
         str = "&gt;";
      }
      else if (str.compareTo("&") == 0) {
         str = "&amp;";
      }
      else if (str.compareTo("\"") == 0) {
         str = "&quot;";
      }
      else if (str.compareTo("à") == 0) {
         str = "&agrave;";
      }
      else if (str.compareTo("À") == 0) {
         str = "&Agrave;";
      }
      else if (str.compareTo("â") == 0) {
         str = "&acirc;";
      }
      else if (str.compareTo("ä") == 0) {
         str = "&auml;";
      }
      else if (str.compareTo("Ä") == 0) {
         str = "&Auml;";
      }
      else if (str.compareTo("Â") == 0) {
         str = "&Acirc;";
      }
      else if (str.compareTo("å") == 0) {
         str = "&aring;";
      }
      else if (str.compareTo("Å") == 0) {
         str = "&Aring;";
      }
      else if (str.compareTo("æ") == 0) {
         str = "&aelig;";
      }
      else if (str.compareTo("Æ") == 0) {
         str = "&AElig;";
      }
      else if (str.compareTo("ç") == 0) {
         str = "&ccedil;";
      }
      else if (str.compareTo("Ç") == 0) {
         str = "&Ccedil;";
      }
      else if (str.compareTo("é") == 0) {
         str = "&eacute;";
      }
      else if (str.compareTo("É") == 0) {
         str = "&Eacute;";
      }
      else if (str.compareTo("è") == 0) {
         str = "&egrave;";
      }
      else if (str.compareTo("È") == 0) {
         str = "&Egrave;";
      }
      else if (str.compareTo("ê") == 0) {
         str = "&ecirc;";
      }
      else if (str.compareTo("Ê") == 0) {
         str = "&Ecirc;";
      }
      else if (str.compareTo("ë") == 0) {
         str = "&euml;";
      }
      else if (str.compareTo("Ë") == 0) {
         str = "&Euml;";
      }
      else if (str.compareTo("ï") == 0) {
         str = "&iuml;";
      }
      else if (str.compareTo("Ï") == 0) {
         str = "&Iuml;";
      }
      else if (str.compareTo("ô") == 0) {
         str = "&ocirc;";
      }
      else if (str.compareTo("Ô") == 0) {
         str = "&Ocirc;";
      }
      else if (str.compareTo("ö") == 0) {
         str = "&ouml;";
      }
      else if (str.compareTo("Ö") == 0) {
         str = "&Ouml;";
      }
      else if (str.compareTo("ø") == 0) {
         str = "&oslash;";
      }
      else if (str.compareTo("Ø") == 0) {
         str = "&Oslash;";
      }
      else if (str.compareTo("ß") == 0) {
         str = "&szlig;";
      }
      else if (str.compareTo("ù") == 0) {
         str = "&ugrave;";
      }
      else if (str.compareTo("Ù") == 0) {
         str = "&Ugrave;";
      }
      else if (str.compareTo("û") == 0) {
         str = "&ucirc;";
      }
      else if (str.compareTo("Û") == 0) {
         str = "&Ucirc;";
      }
      else if (str.compareTo("ü") == 0) {
         str = "&uuml;";
      }
      else if (str.compareTo("Ü") == 0) {
         str = "&Uuml;";
      }
      else if (str.compareTo(" ") == 0) {
         str = "&nbsp;";
      }
      else if (str.compareTo("\u00a9") == 0) {
         str = "&reg;";
      }
      else if (str.compareTo("\u00ae") == 0) {
         str = "&copy;";
      }
      else if (str.compareTo("\u20a0") == 0) {
         str = "&euro;";
      }

      return str;
   }
}
