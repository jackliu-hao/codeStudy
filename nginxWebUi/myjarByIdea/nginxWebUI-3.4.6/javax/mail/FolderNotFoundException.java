package javax.mail;

public class FolderNotFoundException extends MessagingException {
   private transient Folder folder;
   private static final long serialVersionUID = 472612108891249403L;

   public FolderNotFoundException() {
   }

   public FolderNotFoundException(Folder folder) {
      this.folder = folder;
   }

   public FolderNotFoundException(Folder folder, String s) {
      super(s);
      this.folder = folder;
   }

   public FolderNotFoundException(String s, Folder folder) {
      super(s);
      this.folder = folder;
   }

   public Folder getFolder() {
      return this.folder;
   }
}
