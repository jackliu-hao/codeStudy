package javax.mail.internet;

import javax.mail.Session;

class UniqueValue {
   private static int id = 0;

   public static String getUniqueBoundaryValue() {
      StringBuffer s = new StringBuffer();
      s.append("----=_Part_").append(getUniqueId()).append("_").append(s.hashCode()).append('.').append(System.currentTimeMillis());
      return s.toString();
   }

   public static String getUniqueMessageIDValue(Session ssn) {
      String suffix = null;
      InternetAddress addr = InternetAddress.getLocalAddress(ssn);
      if (addr != null) {
         suffix = addr.getAddress();
      } else {
         suffix = "javamailuser@localhost";
      }

      StringBuffer s = new StringBuffer();
      s.append(s.hashCode()).append('.').append(getUniqueId()).append('.').append(System.currentTimeMillis()).append('.').append("JavaMail.").append(suffix);
      return s.toString();
   }

   private static synchronized int getUniqueId() {
      return id++;
   }
}
