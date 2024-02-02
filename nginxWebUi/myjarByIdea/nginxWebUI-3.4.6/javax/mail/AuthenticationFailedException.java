package javax.mail;

public class AuthenticationFailedException extends MessagingException {
   private static final long serialVersionUID = 492080754054436511L;

   public AuthenticationFailedException() {
   }

   public AuthenticationFailedException(String message) {
      super(message);
   }
}
