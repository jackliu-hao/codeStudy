package javax.mail.internet;

import javax.mail.MessagingException;

public class ParseException extends MessagingException {
   private static final long serialVersionUID = 7649991205183658089L;

   public ParseException() {
   }

   public ParseException(String s) {
      super(s);
   }
}
