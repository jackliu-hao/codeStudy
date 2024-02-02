package javax.mail;

public class SendFailedException extends MessagingException {
   protected transient Address[] invalid;
   protected transient Address[] validSent;
   protected transient Address[] validUnsent;
   private static final long serialVersionUID = -6457531621682372913L;

   public SendFailedException() {
   }

   public SendFailedException(String s) {
      super(s);
   }

   public SendFailedException(String s, Exception e) {
      super(s, e);
   }

   public SendFailedException(String msg, Exception ex, Address[] validSent, Address[] validUnsent, Address[] invalid) {
      super(msg, ex);
      this.validSent = validSent;
      this.validUnsent = validUnsent;
      this.invalid = invalid;
   }

   public Address[] getValidSentAddresses() {
      return this.validSent;
   }

   public Address[] getValidUnsentAddresses() {
      return this.validUnsent;
   }

   public Address[] getInvalidAddresses() {
      return this.invalid;
   }
}
