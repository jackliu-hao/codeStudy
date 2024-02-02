package javax.mail.search;

import java.util.Date;
import javax.mail.Message;

public final class ReceivedDateTerm extends DateTerm {
   private static final long serialVersionUID = -2756695246195503170L;

   public ReceivedDateTerm(int comparison, Date date) {
      super(comparison, date);
   }

   public boolean match(Message msg) {
      Date d;
      try {
         d = msg.getReceivedDate();
      } catch (Exception var4) {
         return false;
      }

      return d == null ? false : super.match(d);
   }

   public boolean equals(Object obj) {
      return !(obj instanceof ReceivedDateTerm) ? false : super.equals(obj);
   }
}
