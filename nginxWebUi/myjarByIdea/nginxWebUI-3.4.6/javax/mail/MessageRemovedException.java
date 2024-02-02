package javax.mail;

public class MessageRemovedException extends MessagingException {
   private static final long serialVersionUID = 1951292550679528690L;

   public MessageRemovedException() {
   }

   public MessageRemovedException(String s) {
      super(s);
   }
}
