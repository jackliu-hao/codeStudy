package javax.mail.search;

import javax.mail.MessagingException;

public class SearchException extends MessagingException {
   private static final long serialVersionUID = -7092886778226268686L;

   public SearchException() {
   }

   public SearchException(String s) {
      super(s);
   }
}
