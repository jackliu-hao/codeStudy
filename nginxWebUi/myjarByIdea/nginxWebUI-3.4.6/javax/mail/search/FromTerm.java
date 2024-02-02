package javax.mail.search;

import javax.mail.Address;
import javax.mail.Message;

public final class FromTerm extends AddressTerm {
   private static final long serialVersionUID = 5214730291502658665L;

   public FromTerm(Address address) {
      super(address);
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
      return !(obj instanceof FromTerm) ? false : super.equals(obj);
   }
}
