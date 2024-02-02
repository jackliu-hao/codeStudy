package javax.mail.search;

import javax.mail.Address;

public abstract class AddressTerm extends SearchTerm {
   protected Address address;
   private static final long serialVersionUID = 2005405551929769980L;

   protected AddressTerm(Address address) {
      this.address = address;
   }

   public Address getAddress() {
      return this.address;
   }

   protected boolean match(Address a) {
      return a.equals(this.address);
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof AddressTerm)) {
         return false;
      } else {
         AddressTerm at = (AddressTerm)obj;
         return at.address.equals(this.address);
      }
   }

   public int hashCode() {
      return this.address.hashCode();
   }
}
