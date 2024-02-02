/*     */ package javax.mail.event;
/*     */ 
/*     */ import javax.mail.Folder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FolderEvent
/*     */   extends MailEvent
/*     */ {
/*     */   public static final int CREATED = 1;
/*     */   public static final int DELETED = 2;
/*     */   public static final int RENAMED = 3;
/*     */   protected int type;
/*     */   protected transient Folder folder;
/*     */   protected transient Folder newFolder;
/*     */   private static final long serialVersionUID = 5278131310563694307L;
/*     */   
/*     */   public FolderEvent(Object source, Folder folder, int type) {
/* 102 */     this(source, folder, folder, type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FolderEvent(Object source, Folder oldFolder, Folder newFolder, int type) {
/* 116 */     super(source);
/* 117 */     this.folder = oldFolder;
/* 118 */     this.newFolder = newFolder;
/* 119 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getType() {
/* 128 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Folder getFolder() {
/* 138 */     return this.folder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Folder getNewFolder() {
/* 153 */     return this.newFolder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dispatch(Object listener) {
/* 160 */     if (this.type == 1) {
/* 161 */       ((FolderListener)listener).folderCreated(this);
/* 162 */     } else if (this.type == 2) {
/* 163 */       ((FolderListener)listener).folderDeleted(this);
/* 164 */     } else if (this.type == 3) {
/* 165 */       ((FolderListener)listener).folderRenamed(this);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\event\FolderEvent.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */