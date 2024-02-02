package javax.mail;

public class MessageContext {
   private Part part;

   public MessageContext(Part part) {
      this.part = part;
   }

   public Part getPart() {
      return this.part;
   }

   public Message getMessage() {
      try {
         return getMessage(this.part);
      } catch (MessagingException var2) {
         return null;
      }
   }

   private static Message getMessage(Part p) throws MessagingException {
      while(p != null) {
         if (p instanceof Message) {
            return (Message)p;
         }

         BodyPart bp = (BodyPart)p;
         Multipart mp = bp.getParent();
         if (mp == null) {
            return null;
         }

         p = mp.getParent();
      }

      return null;
   }

   public Session getSession() {
      Message msg = this.getMessage();
      return msg != null ? msg.session : null;
   }
}
