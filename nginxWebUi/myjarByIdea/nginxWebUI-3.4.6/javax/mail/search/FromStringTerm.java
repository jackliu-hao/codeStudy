package javax.mail.search;

import javax.mail.Address;
import javax.mail.Message;

public final class FromStringTerm extends AddressStringTerm {
   private static final long serialVersionUID = 5801127523826772788L;

   public FromStringTerm(String pattern) {
      super(pattern);
   }

   public boolean match(Message msg) {
      Address[] from;
      try {
         from = msg.getFrom();
      } catch (Exception var4) {
         return false;
      }

      if (from == null) {
         return false;
      } else {
         for(int i = 0; i < from.length; ++i) {
            if (super.match(from[i])) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean equals(Object obj) {
      return !(obj instanceof FromStringTerm) ? false : super.equals(obj);
   }
}
