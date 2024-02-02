package javax.mail.search;

import javax.mail.Address;
import javax.mail.Message;

public final class RecipientTerm extends AddressTerm {
   protected Message.RecipientType type;
   private static final long serialVersionUID = 6548700653122680468L;

   public RecipientTerm(Message.RecipientType type, Address address) {
      super(address);
      this.type = type;
   }

   public Message.RecipientType getRecipientType() {
      return this.type;
   }

   public boolean match(Message msg) {
      Address[] recipients;
      try {
         recipients = msg.getRecipients(this.type);
      } catch (Exception var4) {
         return false;
      }

      if (recipients == null) {
         return false;
      } else {
         for(int i = 0; i < recipients.length; ++i) {
            if (super.match(recipients[i])) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof RecipientTerm)) {
         return false;
      } else {
         RecipientTerm rt = (RecipientTerm)obj;
         return rt.type.equals(this.type) && super.equals(obj);
      }
   }

   public int hashCode() {
      return this.type.hashCode() + super.hashCode();
   }
}
