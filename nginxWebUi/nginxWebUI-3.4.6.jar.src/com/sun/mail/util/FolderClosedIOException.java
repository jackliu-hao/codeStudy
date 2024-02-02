/*    */ package com.sun.mail.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.mail.Folder;
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
/*    */ public class FolderClosedIOException
/*    */   extends IOException
/*    */ {
/*    */   private transient Folder folder;
/*    */   private static final long serialVersionUID = 4281122580365555735L;
/*    */   
/*    */   public FolderClosedIOException(Folder folder) {
/* 64 */     this(folder, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FolderClosedIOException(Folder folder, String message) {
/* 73 */     super(message);
/* 74 */     this.folder = folder;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Folder getFolder() {
/* 81 */     return this.folder;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\FolderClosedIOException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */