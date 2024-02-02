package javax.mail;

public abstract class BodyPart implements Part {
   protected Multipart parent;

   public Multipart getParent() {
      return this.parent;
   }

   void setParent(Multipart parent) {
      this.parent = parent;
   }
}
