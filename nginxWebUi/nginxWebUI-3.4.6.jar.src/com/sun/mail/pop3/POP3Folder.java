/*     */ package com.sun.mail.pop3;
/*     */ 
/*     */ import com.sun.mail.util.LineInputStream;
/*     */ import com.sun.mail.util.MailLogger;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import java.util.logging.Level;
/*     */ import javax.mail.FetchProfile;
/*     */ import javax.mail.Flags;
/*     */ import javax.mail.Folder;
/*     */ import javax.mail.FolderClosedException;
/*     */ import javax.mail.FolderNotFoundException;
/*     */ import javax.mail.Message;
/*     */ import javax.mail.MessageRemovedException;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.MethodNotSupportedException;
/*     */ import javax.mail.UIDFolder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class POP3Folder
/*     */   extends Folder
/*     */ {
/*     */   private String name;
/*     */   private POP3Store store;
/*     */   private volatile Protocol port;
/*     */   private int total;
/*     */   private int size;
/*     */   private boolean exists = false;
/*     */   private volatile boolean opened = false;
/*     */   private Vector message_cache;
/*     */   private boolean doneUidl = false;
/*  79 */   private volatile TempFile fileCache = null;
/*     */   
/*     */   MailLogger logger;
/*     */   
/*     */   POP3Folder(POP3Store store, String name) {
/*  84 */     super(store);
/*  85 */     this.name = name;
/*  86 */     this.store = store;
/*  87 */     if (name.equalsIgnoreCase("INBOX"))
/*  88 */       this.exists = true; 
/*  89 */     this.logger = new MailLogger(getClass(), "DEBUG POP3", store.getSession());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  94 */     return this.name;
/*     */   }
/*     */   
/*     */   public String getFullName() {
/*  98 */     return this.name;
/*     */   }
/*     */   
/*     */   public Folder getParent() {
/* 102 */     return new DefaultFolder(this.store);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean exists() {
/* 112 */     return this.exists;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Folder[] list(String pattern) throws MessagingException {
/* 122 */     throw new MessagingException("not a directory");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char getSeparator() {
/* 131 */     return Character.MIN_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getType() {
/* 140 */     return 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean create(int type) throws MessagingException {
/* 150 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasNewMessages() throws MessagingException {
/* 160 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Folder getFolder(String name) throws MessagingException {
/* 170 */     throw new MessagingException("not a directory");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean delete(boolean recurse) throws MessagingException {
/* 181 */     throw new MethodNotSupportedException("delete");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean renameTo(Folder f) throws MessagingException {
/* 191 */     throw new MethodNotSupportedException("renameTo");
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
/*     */   public synchronized void open(int mode) throws MessagingException {
/* 203 */     checkClosed();
/* 204 */     if (!this.exists) {
/* 205 */       throw new FolderNotFoundException(this, "folder is not INBOX");
/*     */     }
/*     */     try {
/* 208 */       this.port = this.store.getPort(this);
/* 209 */       Status s = this.port.stat();
/* 210 */       this.total = s.total;
/* 211 */       this.size = s.size;
/* 212 */       this.mode = mode;
/* 213 */       if (this.store.useFileCache) {
/*     */         try {
/* 215 */           this.fileCache = new TempFile(this.store.fileCacheDir);
/* 216 */         } catch (IOException ex) {
/* 217 */           this.logger.log(Level.FINE, "failed to create file cache", ex);
/* 218 */           throw ex;
/*     */         } 
/*     */       }
/* 221 */       this.opened = true;
/* 222 */     } catch (IOException ioex) {
/*     */       try {
/* 224 */         if (this.port != null)
/* 225 */           this.port.quit(); 
/* 226 */       } catch (IOException ioex2) {
/*     */       
/*     */       } finally {
/* 229 */         this.port = null;
/* 230 */         this.store.closePort(this);
/*     */       } 
/* 232 */       throw new MessagingException("Open failed", ioex);
/*     */     } 
/*     */ 
/*     */     
/* 236 */     this.message_cache = new Vector(this.total);
/* 237 */     this.message_cache.setSize(this.total);
/* 238 */     this.doneUidl = false;
/*     */     
/* 240 */     notifyConnectionListeners(1);
/*     */   }
/*     */   
/*     */   public synchronized void close(boolean expunge) throws MessagingException {
/* 244 */     checkOpen();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 256 */       if (this.store.rsetBeforeQuit) {
/* 257 */         this.port.rset();
/*     */       }
/* 259 */       if (expunge && this.mode == 2)
/*     */       {
/* 261 */         for (int j = 0; j < this.message_cache.size(); j++) {
/* 262 */           POP3Message m; if ((m = this.message_cache.elementAt(j)) != null && 
/* 263 */             m.isSet(Flags.Flag.DELETED)) {
/*     */             try {
/* 265 */               this.port.dele(j + 1);
/* 266 */             } catch (IOException ioex) {
/* 267 */               throw new MessagingException("Exception deleting messages during close", ioex);
/*     */             } 
/*     */           }
/*     */         } 
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 278 */       for (int i = 0; i < this.message_cache.size(); i++) {
/* 279 */         POP3Message m; if ((m = this.message_cache.elementAt(i)) != null) {
/* 280 */           m.invalidate(true);
/*     */         }
/*     */       } 
/* 283 */       this.port.quit();
/* 284 */     } catch (IOException ex) {
/*     */     
/*     */     } finally {
/* 287 */       this.port = null;
/* 288 */       this.store.closePort(this);
/* 289 */       this.message_cache = null;
/* 290 */       this.opened = false;
/* 291 */       notifyConnectionListeners(3);
/* 292 */       if (this.fileCache != null) {
/* 293 */         this.fileCache.close();
/* 294 */         this.fileCache = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized boolean isOpen() {
/* 300 */     if (!this.opened)
/* 301 */       return false; 
/*     */     try {
/* 303 */       if (!this.port.noop())
/* 304 */         throw new IOException("NOOP failed"); 
/* 305 */     } catch (IOException ioex) {
/*     */       try {
/* 307 */         close(false);
/* 308 */       } catch (MessagingException mex) {
/*     */       
/*     */       } finally {
/* 311 */         return false;
/*     */       } 
/*     */     } 
/* 314 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flags getPermanentFlags() {
/* 324 */     return new Flags();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized int getMessageCount() throws MessagingException {
/* 333 */     if (!this.opened)
/* 334 */       return -1; 
/* 335 */     checkReadable();
/* 336 */     return this.total;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized Message getMessage(int msgno) throws MessagingException {
/* 341 */     checkOpen();
/*     */ 
/*     */     
/*     */     POP3Message m;
/*     */     
/* 346 */     if ((m = this.message_cache.elementAt(msgno - 1)) == null) {
/* 347 */       m = createMessage(this, msgno);
/* 348 */       this.message_cache.setElementAt(m, msgno - 1);
/*     */     } 
/* 350 */     return (Message)m;
/*     */   }
/*     */ 
/*     */   
/*     */   protected POP3Message createMessage(Folder f, int msgno) throws MessagingException {
/* 355 */     POP3Message m = null;
/* 356 */     Constructor cons = this.store.messageConstructor;
/* 357 */     if (cons != null) {
/*     */       try {
/* 359 */         Object[] o = { this, new Integer(msgno) };
/* 360 */         m = cons.newInstance(o);
/* 361 */       } catch (Exception ex) {}
/*     */     }
/*     */ 
/*     */     
/* 365 */     if (m == null)
/* 366 */       m = new POP3Message(this, msgno); 
/* 367 */     return m;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void appendMessages(Message[] msgs) throws MessagingException {
/* 377 */     throw new MethodNotSupportedException("Append not supported");
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
/*     */   public Message[] expunge() throws MessagingException {
/* 390 */     throw new MethodNotSupportedException("Expunge not supported");
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
/*     */   public synchronized void fetch(Message[] msgs, FetchProfile fp) throws MessagingException {
/* 404 */     checkReadable();
/* 405 */     if (!this.doneUidl && this.store.supportsUidl && fp.contains((FetchProfile.Item)UIDFolder.FetchProfileItem.UID)) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 415 */       String[] uids = new String[this.message_cache.size()];
/*     */       try {
/* 417 */         if (!this.port.uidl(uids))
/*     */           return; 
/* 419 */       } catch (EOFException eex) {
/* 420 */         close(false);
/* 421 */         throw new FolderClosedException(this, eex.toString());
/* 422 */       } catch (IOException ex) {
/* 423 */         throw new MessagingException("error getting UIDL", ex);
/*     */       } 
/* 425 */       for (int i = 0; i < uids.length; i++) {
/* 426 */         if (uids[i] != null) {
/*     */           
/* 428 */           POP3Message m = (POP3Message)getMessage(i + 1);
/* 429 */           m.uid = uids[i];
/*     */         } 
/* 431 */       }  this.doneUidl = true;
/*     */     } 
/* 433 */     if (fp.contains(FetchProfile.Item.ENVELOPE)) {
/* 434 */       for (int i = 0; i < msgs.length; i++) {
/*     */         try {
/* 436 */           POP3Message msg = (POP3Message)msgs[i];
/*     */           
/* 438 */           msg.getHeader("");
/*     */           
/* 440 */           msg.getSize();
/* 441 */         } catch (MessageRemovedException mex) {}
/*     */       } 
/*     */     }
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
/*     */   public synchronized String getUID(Message msg) throws MessagingException {
/* 456 */     checkOpen();
/* 457 */     POP3Message m = (POP3Message)msg;
/*     */     try {
/* 459 */       if (!this.store.supportsUidl)
/* 460 */         return null; 
/* 461 */       if (m.uid == "UNKNOWN")
/* 462 */         m.uid = this.port.uidl(m.getMessageNumber()); 
/* 463 */       return m.uid;
/* 464 */     } catch (EOFException eex) {
/* 465 */       close(false);
/* 466 */       throw new FolderClosedException(this, eex.toString());
/* 467 */     } catch (IOException ex) {
/* 468 */       throw new MessagingException("error getting UIDL", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized int getSize() throws MessagingException {
/* 480 */     checkOpen();
/* 481 */     return this.size;
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
/*     */   public synchronized int[] getSizes() throws MessagingException {
/* 494 */     checkOpen();
/* 495 */     int[] sizes = new int[this.total];
/* 496 */     InputStream is = null;
/* 497 */     LineInputStream lis = null;
/*     */     try {
/* 499 */       is = this.port.list();
/* 500 */       lis = new LineInputStream(is);
/*     */       String line;
/* 502 */       while ((line = lis.readLine()) != null) {
/*     */         try {
/* 504 */           StringTokenizer st = new StringTokenizer(line);
/* 505 */           int msgnum = Integer.parseInt(st.nextToken());
/* 506 */           int size = Integer.parseInt(st.nextToken());
/* 507 */           if (msgnum > 0 && msgnum <= this.total)
/* 508 */             sizes[msgnum - 1] = size; 
/* 509 */         } catch (Exception e) {}
/*     */       }
/*     */     
/* 512 */     } catch (IOException ex) {
/*     */     
/*     */     } finally {
/*     */       try {
/* 516 */         if (lis != null)
/* 517 */           lis.close(); 
/* 518 */       } catch (IOException cex) {}
/*     */       try {
/* 520 */         if (is != null)
/* 521 */           is.close(); 
/* 522 */       } catch (IOException cex) {}
/*     */     } 
/* 524 */     return sizes;
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
/*     */   public synchronized InputStream listCommand() throws MessagingException, IOException {
/* 536 */     checkOpen();
/* 537 */     return this.port.list();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 544 */     super.finalize();
/* 545 */     close(false);
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkOpen() throws IllegalStateException {
/* 550 */     if (!this.opened) {
/* 551 */       throw new IllegalStateException("Folder is not Open");
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkClosed() throws IllegalStateException {
/* 556 */     if (this.opened) {
/* 557 */       throw new IllegalStateException("Folder is Open");
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkReadable() throws IllegalStateException {
/* 562 */     if (!this.opened || (this.mode != 1 && this.mode != 2)) {
/* 563 */       throw new IllegalStateException("Folder is not Readable");
/*     */     }
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
/*     */   
/*     */   Protocol getProtocol() throws MessagingException {
/* 580 */     Protocol p = this.port;
/* 581 */     checkOpen();
/*     */     
/* 583 */     return p;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void notifyMessageChangedListeners(int type, Message m) {
/* 590 */     super.notifyMessageChangedListeners(type, m);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   TempFile getFileCache() {
/* 597 */     return this.fileCache;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\pop3\POP3Folder.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */