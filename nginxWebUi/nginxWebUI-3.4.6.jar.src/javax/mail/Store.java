/*     */ package javax.mail;
/*     */ 
/*     */ import java.util.Vector;
/*     */ import javax.mail.event.FolderEvent;
/*     */ import javax.mail.event.FolderListener;
/*     */ import javax.mail.event.MailEvent;
/*     */ import javax.mail.event.StoreEvent;
/*     */ import javax.mail.event.StoreListener;
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
/*     */ public abstract class Store
/*     */   extends Service
/*     */ {
/*     */   private volatile Vector storeListeners;
/*     */   private volatile Vector folderListeners;
/*     */   
/*     */   protected Store(Session session, URLName urlname) {
/*  74 */     super(session, urlname);
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
/* 187 */     this.storeListeners = null;
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
/* 237 */     this.folderListeners = null;
/*     */   }
/*     */   
/*     */   public abstract Folder getDefaultFolder() throws MessagingException;
/*     */   
/*     */   public abstract Folder getFolder(String paramString) throws MessagingException;
/*     */   
/*     */   public abstract Folder getFolder(URLName paramURLName) throws MessagingException;
/*     */   
/*     */   public Folder[] getPersonalNamespaces() throws MessagingException {
/*     */     return new Folder[] { getDefaultFolder() };
/*     */   }
/*     */   
/*     */   public synchronized void addFolderListener(FolderListener l)
/*     */   {
/* 252 */     if (this.folderListeners == null)
/* 253 */       this.folderListeners = new Vector(); 
/* 254 */     this.folderListeners.addElement(l);
/*     */   }
/*     */ 
/*     */   
/*     */   public Folder[] getUserNamespaces(String user) throws MessagingException {
/*     */     return new Folder[0];
/*     */   }
/*     */   
/*     */   public Folder[] getSharedNamespaces() throws MessagingException {
/*     */     return new Folder[0];
/*     */   }
/*     */   
/*     */   public synchronized void removeFolderListener(FolderListener l) {
/* 267 */     if (this.folderListeners != null) {
/* 268 */       this.folderListeners.removeElement(l);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void addStoreListener(StoreListener l) {
/*     */     if (this.storeListeners == null) {
/*     */       this.storeListeners = new Vector();
/*     */     }
/*     */     this.storeListeners.addElement(l);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void notifyFolderListeners(int type, Folder folder) {
/* 286 */     if (this.folderListeners == null) {
/*     */       return;
/*     */     }
/* 289 */     FolderEvent e = new FolderEvent(this, folder, type);
/* 290 */     queueEvent((MailEvent)e, this.folderListeners);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void removeStoreListener(StoreListener l) {
/*     */     if (this.storeListeners != null) {
/*     */       this.storeListeners.removeElement(l);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void notifyStoreListeners(int type, String message) {
/*     */     if (this.storeListeners == null) {
/*     */       return;
/*     */     }
/*     */     StoreEvent e = new StoreEvent(this, type, message);
/*     */     queueEvent((MailEvent)e, this.storeListeners);
/*     */   }
/*     */   
/*     */   protected void notifyFolderRenamedListeners(Folder oldF, Folder newF) {
/* 309 */     if (this.folderListeners == null) {
/*     */       return;
/*     */     }
/* 312 */     FolderEvent e = new FolderEvent(this, oldF, newF, 3);
/* 313 */     queueEvent((MailEvent)e, this.folderListeners);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\Store.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */