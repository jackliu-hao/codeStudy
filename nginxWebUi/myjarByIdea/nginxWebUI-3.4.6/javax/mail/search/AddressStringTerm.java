package javax.mail.search;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

public abstract class AddressStringTerm extends StringTerm {
   private static final long serialVersionUID = 3086821234204980368L;

   protected AddressStringTerm(String pattern) {
      super(pattern, true);
   }

   protected boolean match(Address a) {
      if (a instanceof InternetAddress) {
         InternetAddress ia = (InternetAddress)a;
         return super.match(ia.toUnicodeString());
      } else {
         return super.match(a.toString());
      }
   }

   public boolean equals(Object obj) {
      return !(obj instanceof AddressStringTerm) ? false : super.equals(obj);
   }
}
