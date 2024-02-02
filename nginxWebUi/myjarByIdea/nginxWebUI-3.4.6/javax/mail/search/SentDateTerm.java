package javax.mail.search;

import java.util.Date;
import javax.mail.Message;

public final class SentDateTerm extends DateTerm {
   private static final long serialVersionUID = 5647755030530907263L;

   public SentDateTerm(int comparison, Date date) {
      super(comparison, date);
   }

   public boolean match(Message msg) {
      Date d;
      try {
         d = msg.getSentDate();
      } catch (Exception var4) {
         return false;
      }

      return d == null ? false : super.match(d);
   }

   public boolean equals(Object obj) {
      return !(obj instanceof SentDateTerm) ? false : super.equals(obj);
   }
}
