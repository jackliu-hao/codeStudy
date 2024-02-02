package javax.mail.internet;

public class AddressException extends ParseException {
   protected String ref = null;
   protected int pos = -1;
   private static final long serialVersionUID = 9134583443539323120L;

   public AddressException() {
   }

   public AddressException(String s) {
      super(s);
   }

   public AddressException(String s, String ref) {
      super(s);
      this.ref = ref;
   }

   public AddressException(String s, String ref, int pos) {
      super(s);
      this.ref = ref;
      this.pos = pos;
   }

   public String getRef() {
      return this.ref;
   }

   public int getPos() {
      return this.pos;
   }

   public String toString() {
      String s = super.toString();
      if (this.ref == null) {
         return s;
      } else {
         s = s + " in string ``" + this.ref + "''";
         return this.pos < 0 ? s : s + " at position " + this.pos;
      }
   }
}
