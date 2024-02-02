/*    */ package javax.mail;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FolderClosedException
/*    */   extends MessagingException
/*    */ {
/*    */   private transient Folder folder;
/*    */   private static final long serialVersionUID = 1687879213433302315L;
/*    */   
/*    */   public FolderClosedException(Folder folder) {
/* 68 */     this(folder, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FolderClosedException(Folder folder, String message) {
/* 77 */     super(message);
/* 78 */     this.folder = folder;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Folder getFolder() {
/* 85 */     return this.folder;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\FolderClosedException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */