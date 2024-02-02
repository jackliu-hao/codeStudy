/*      */ package com.sun.mail.imap;
/*      */ 
/*      */ import com.sun.mail.iap.BadCommandException;
/*      */ import com.sun.mail.iap.CommandFailedException;
/*      */ import com.sun.mail.iap.ConnectionException;
/*      */ import com.sun.mail.iap.ProtocolException;
/*      */ import com.sun.mail.iap.Response;
/*      */ import com.sun.mail.iap.ResponseHandler;
/*      */ import com.sun.mail.imap.protocol.FetchItem;
/*      */ import com.sun.mail.imap.protocol.FetchResponse;
/*      */ import com.sun.mail.imap.protocol.IMAPProtocol;
/*      */ import com.sun.mail.imap.protocol.IMAPResponse;
/*      */ import com.sun.mail.imap.protocol.Item;
/*      */ import com.sun.mail.imap.protocol.ListInfo;
/*      */ import com.sun.mail.imap.protocol.MailboxInfo;
/*      */ import com.sun.mail.imap.protocol.MessageSet;
/*      */ import com.sun.mail.imap.protocol.Status;
/*      */ import com.sun.mail.imap.protocol.UID;
/*      */ import com.sun.mail.util.MailLogger;
/*      */ import java.io.IOException;
/*      */ import java.util.Date;
/*      */ import java.util.Hashtable;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Vector;
/*      */ import java.util.logging.Level;
/*      */ import javax.mail.FetchProfile;
/*      */ import javax.mail.Flags;
/*      */ import javax.mail.Folder;
/*      */ import javax.mail.FolderClosedException;
/*      */ import javax.mail.FolderNotFoundException;
/*      */ import javax.mail.Message;
/*      */ import javax.mail.MessageRemovedException;
/*      */ import javax.mail.MessagingException;
/*      */ import javax.mail.Quota;
/*      */ import javax.mail.ReadOnlyFolderException;
/*      */ import javax.mail.StoreClosedException;
/*      */ import javax.mail.UIDFolder;
/*      */ import javax.mail.event.MessageCountListener;
/*      */ import javax.mail.internet.MimeMessage;
/*      */ import javax.mail.search.FlagTerm;
/*      */ import javax.mail.search.SearchException;
/*      */ import javax.mail.search.SearchTerm;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class IMAPFolder
/*      */   extends Folder
/*      */   implements UIDFolder, ResponseHandler
/*      */ {
/*      */   protected String fullName;
/*      */   protected String name;
/*      */   protected int type;
/*      */   protected char separator;
/*      */   protected Flags availableFlags;
/*      */   protected Flags permanentFlags;
/*      */   protected volatile boolean exists;
/*      */   protected boolean isNamespace = false;
/*      */   protected volatile String[] attributes;
/*      */   protected volatile IMAPProtocol protocol;
/*      */   protected MessageCache messageCache;
/*  178 */   protected final Object messageCacheLock = new Object();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Hashtable uidTable;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static final char UNKNOWN_SEPARATOR = '￿';
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile boolean opened = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean reallyClosed = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int RUNNING = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int IDLE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int ABORTING = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  254 */   private int idleState = 0;
/*      */   
/*  256 */   private volatile int total = -1;
/*      */   
/*  258 */   private volatile int recent = -1;
/*  259 */   private int realTotal = -1;
/*      */   
/*  261 */   private long uidvalidity = -1L;
/*  262 */   private long uidnext = -1L;
/*      */   
/*      */   private boolean doExpungeNotification = true;
/*  265 */   private Status cachedStatus = null;
/*  266 */   private long cachedStatusTime = 0L;
/*      */ 
/*      */   
/*      */   private boolean hasMessageCountListener = false;
/*      */   
/*      */   protected MailLogger logger;
/*      */   
/*      */   private MailLogger connectionPoolLogger;
/*      */   
/*      */   static final boolean $assertionsDisabled;
/*      */ 
/*      */   
/*      */   public static class FetchProfileItem
/*      */     extends FetchProfile.Item
/*      */   {
/*      */     protected FetchProfileItem(String name) {
/*  282 */       super(name);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  300 */     public static final FetchProfileItem HEADERS = new FetchProfileItem("HEADERS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  311 */     public static final FetchProfileItem SIZE = new FetchProfileItem("SIZE");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IMAPFolder(String fullName, char separator, IMAPStore store, Boolean isNamespace) {
/*  325 */     super(store);
/*  326 */     if (fullName == null)
/*  327 */       throw new NullPointerException("Folder name is null"); 
/*  328 */     this.fullName = fullName;
/*  329 */     this.separator = separator;
/*  330 */     this.logger = new MailLogger(getClass(), "DEBUG IMAP", store.getSession());
/*      */     
/*  332 */     this.connectionPoolLogger = store.getConnectionPoolLogger();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  345 */     this.isNamespace = false;
/*  346 */     if (separator != Character.MAX_VALUE && separator != '\000') {
/*  347 */       int i = this.fullName.indexOf(separator);
/*  348 */       if (i > 0 && i == this.fullName.length() - 1) {
/*  349 */         this.fullName = this.fullName.substring(0, i);
/*  350 */         this.isNamespace = true;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  355 */     if (isNamespace != null) {
/*  356 */       this.isNamespace = isNamespace.booleanValue();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected IMAPFolder(ListInfo li, IMAPStore store) {
/*  363 */     this(li.name, li.separator, store, (Boolean)null);
/*      */     
/*  365 */     if (li.hasInferiors)
/*  366 */       this.type |= 0x2; 
/*  367 */     if (li.canOpen)
/*  368 */       this.type |= 0x1; 
/*  369 */     this.exists = true;
/*  370 */     this.attributes = li.attrs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void checkExists() throws MessagingException {
/*  382 */     if (!this.exists && !exists()) {
/*  383 */       throw new FolderNotFoundException(this, this.fullName + " not found");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void checkClosed() {
/*  392 */     if (this.opened) {
/*  393 */       throw new IllegalStateException("This operation is not allowed on an open folder");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void checkOpened() throws FolderClosedException {
/*  403 */     assert Thread.holdsLock(this);
/*  404 */     if (!this.opened) {
/*  405 */       if (this.reallyClosed) {
/*  406 */         throw new IllegalStateException("This operation is not allowed on a closed folder");
/*      */       }
/*      */ 
/*      */       
/*  410 */       throw new FolderClosedException(this, "Lost folder connection to server");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void checkRange(int msgno) throws MessagingException {
/*  423 */     if (msgno < 1) {
/*  424 */       throw new IndexOutOfBoundsException("message number < 1");
/*      */     }
/*  426 */     if (msgno <= this.total) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  432 */     synchronized (this.messageCacheLock) {
/*      */       try {
/*  434 */         keepConnectionAlive(false);
/*  435 */       } catch (ConnectionException cex) {
/*      */         
/*  437 */         throw new FolderClosedException(this, cex.getMessage());
/*  438 */       } catch (ProtocolException pex) {
/*  439 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */     } 
/*      */     
/*  443 */     if (msgno > this.total) {
/*  444 */       throw new IndexOutOfBoundsException(msgno + " > " + this.total);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkFlags(Flags flags) throws MessagingException {
/*  452 */     assert Thread.holdsLock(this);
/*  453 */     if (this.mode != 2) {
/*  454 */       throw new IllegalStateException("Cannot change flags on READ_ONLY folder: " + this.fullName);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized String getName() {
/*  472 */     if (this.name == null) {
/*      */       try {
/*  474 */         this.name = this.fullName.substring(this.fullName.lastIndexOf(getSeparator()) + 1);
/*      */       
/*      */       }
/*  477 */       catch (MessagingException mex) {}
/*      */     }
/*  479 */     return this.name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized String getFullName() {
/*  486 */     return this.fullName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Folder getParent() throws MessagingException {
/*  493 */     char c = getSeparator();
/*      */     int index;
/*  495 */     if ((index = this.fullName.lastIndexOf(c)) != -1) {
/*  496 */       return ((IMAPStore)this.store).newIMAPFolder(this.fullName.substring(0, index), c);
/*      */     }
/*      */     
/*  499 */     return new DefaultFolder((IMAPStore)this.store);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean exists() throws MessagingException {
/*      */     final String lname;
/*  507 */     ListInfo[] li = null;
/*      */     
/*  509 */     if (this.isNamespace && this.separator != '\000') {
/*  510 */       lname = this.fullName + this.separator;
/*      */     } else {
/*  512 */       lname = this.fullName;
/*      */     } 
/*  514 */     li = (ListInfo[])doCommand(new ProtocolCommand() { private final String val$lname; private final IMAPFolder this$0;
/*      */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/*  516 */             return p.list("", lname);
/*      */           } }
/*      */       );
/*      */     
/*  520 */     if (li != null) {
/*  521 */       int i = findName(li, lname);
/*  522 */       this.fullName = (li[i]).name;
/*  523 */       this.separator = (li[i]).separator;
/*  524 */       int len = this.fullName.length();
/*  525 */       if (this.separator != '\000' && len > 0 && this.fullName.charAt(len - 1) == this.separator)
/*      */       {
/*  527 */         this.fullName = this.fullName.substring(0, len - 1);
/*      */       }
/*  529 */       this.type = 0;
/*  530 */       if ((li[i]).hasInferiors)
/*  531 */         this.type |= 0x2; 
/*  532 */       if ((li[i]).canOpen)
/*  533 */         this.type |= 0x1; 
/*  534 */       this.exists = true;
/*  535 */       this.attributes = (li[i]).attrs;
/*      */     } else {
/*  537 */       this.exists = this.opened;
/*  538 */       this.attributes = null;
/*      */     } 
/*      */     
/*  541 */     return this.exists;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int findName(ListInfo[] li, String lname) {
/*      */     int i;
/*  552 */     for (i = 0; i < li.length && 
/*  553 */       !(li[i]).name.equals(lname); i++);
/*      */ 
/*      */     
/*  556 */     if (i >= li.length)
/*      */     {
/*      */ 
/*      */       
/*  560 */       i = 0;
/*      */     }
/*  562 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Folder[] list(String pattern) throws MessagingException {
/*  569 */     return doList(pattern, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Folder[] listSubscribed(String pattern) throws MessagingException {
/*  576 */     return doList(pattern, true);
/*      */   }
/*      */ 
/*      */   
/*      */   private synchronized Folder[] doList(final String pattern, final boolean subscribed) throws MessagingException {
/*  581 */     checkExists();
/*      */ 
/*      */     
/*  584 */     if (this.attributes != null && !isDirectory()) {
/*  585 */       return new Folder[0];
/*      */     }
/*  587 */     final char c = getSeparator();
/*      */     
/*  589 */     ListInfo[] li = (ListInfo[])doCommandIgnoreFailure(new ProtocolCommand() { private final boolean val$subscribed; private final char val$c; private final String val$pattern;
/*      */           private final IMAPFolder this$0;
/*      */           
/*      */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/*  593 */             if (subscribed) {
/*  594 */               return p.lsub("", IMAPFolder.this.fullName + c + pattern);
/*      */             }
/*  596 */             return p.list("", IMAPFolder.this.fullName + c + pattern);
/*      */           } }
/*      */       );
/*      */     
/*  600 */     if (li == null) {
/*  601 */       return new Folder[0];
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  614 */     int start = 0;
/*      */     
/*  616 */     if (li.length > 0 && (li[0]).name.equals(this.fullName + c)) {
/*  617 */       start = 1;
/*      */     }
/*  619 */     IMAPFolder[] folders = new IMAPFolder[li.length - start];
/*  620 */     IMAPStore st = (IMAPStore)this.store;
/*  621 */     for (int i = start; i < li.length; i++)
/*  622 */       folders[i - start] = st.newIMAPFolder(li[i]); 
/*  623 */     return (Folder[])folders;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized char getSeparator() throws MessagingException {
/*  630 */     if (this.separator == Character.MAX_VALUE) {
/*  631 */       ListInfo[] li = null;
/*      */       
/*  633 */       li = (ListInfo[])doCommand(new ProtocolCommand()
/*      */           {
/*      */             private final IMAPFolder this$0;
/*      */             
/*      */             public Object doCommand(IMAPProtocol p) throws ProtocolException {
/*  638 */               if (p.isREV1()) {
/*  639 */                 return p.list(IMAPFolder.this.fullName, "");
/*      */               }
/*      */               
/*  642 */               return p.list("", IMAPFolder.this.fullName);
/*      */             }
/*      */           });
/*      */       
/*  646 */       if (li != null) {
/*  647 */         this.separator = (li[0]).separator;
/*      */       } else {
/*  649 */         this.separator = '/';
/*      */       } 
/*  651 */     }  return this.separator;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized int getType() throws MessagingException {
/*  658 */     if (this.opened) {
/*      */       
/*  660 */       if (this.attributes == null)
/*  661 */         exists(); 
/*      */     } else {
/*  663 */       checkExists();
/*      */     } 
/*  665 */     return this.type;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean isSubscribed() {
/*      */     final String lname;
/*  672 */     ListInfo[] li = null;
/*      */     
/*  674 */     if (this.isNamespace && this.separator != '\000') {
/*  675 */       lname = this.fullName + this.separator;
/*      */     } else {
/*  677 */       lname = this.fullName;
/*      */     } 
/*      */     try {
/*  680 */       li = (ListInfo[])doProtocolCommand(new ProtocolCommand() { private final String val$lname; private final IMAPFolder this$0;
/*      */             
/*      */             public Object doCommand(IMAPProtocol p) throws ProtocolException {
/*  683 */               return p.lsub("", lname);
/*      */             } }
/*      */         );
/*  686 */     } catch (ProtocolException pex) {}
/*      */ 
/*      */     
/*  689 */     if (li != null) {
/*  690 */       int i = findName(li, lname);
/*  691 */       return (li[i]).canOpen;
/*      */     } 
/*  693 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setSubscribed(final boolean subscribe) throws MessagingException {
/*  701 */     doCommandIgnoreFailure(new ProtocolCommand() { private final boolean val$subscribe; private final IMAPFolder this$0;
/*      */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/*  703 */             if (subscribe) {
/*  704 */               p.subscribe(IMAPFolder.this.fullName);
/*      */             } else {
/*  706 */               p.unsubscribe(IMAPFolder.this.fullName);
/*  707 */             }  return null;
/*      */           } }
/*      */       );
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean create(final int type) throws MessagingException {
/*  718 */     char c = Character.MIN_VALUE;
/*  719 */     if ((type & 0x1) == 0)
/*  720 */       c = getSeparator(); 
/*  721 */     final char sep = c;
/*  722 */     Object ret = doCommandIgnoreFailure(new ProtocolCommand() { private final int val$type; private final char val$sep; private final IMAPFolder this$0;
/*      */           
/*      */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/*  725 */             if ((type & 0x1) == 0) {
/*  726 */               p.create(IMAPFolder.this.fullName + sep);
/*      */             } else {
/*  728 */               p.create(IMAPFolder.this.fullName);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*  734 */               if ((type & 0x2) != 0) {
/*      */ 
/*      */                 
/*  737 */                 ListInfo[] li = p.list("", IMAPFolder.this.fullName);
/*  738 */                 if (li != null && !(li[0]).hasInferiors) {
/*      */ 
/*      */                   
/*  741 */                   p.delete(IMAPFolder.this.fullName);
/*  742 */                   throw new ProtocolException("Unsupported type");
/*      */                 } 
/*      */               } 
/*      */             } 
/*  746 */             return Boolean.TRUE;
/*      */           } }
/*      */       );
/*      */     
/*  750 */     if (ret == null) {
/*  751 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  756 */     boolean retb = exists();
/*  757 */     if (retb)
/*  758 */       notifyFolderListeners(1); 
/*  759 */     return retb;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean hasNewMessages() throws MessagingException {
/*      */     final String lname;
/*  766 */     if (this.opened)
/*      */     {
/*  768 */       synchronized (this.messageCacheLock) {
/*      */         
/*      */         try {
/*  771 */           keepConnectionAlive(true);
/*  772 */         } catch (ConnectionException cex) {
/*  773 */           throw new FolderClosedException(this, cex.getMessage());
/*  774 */         } catch (ProtocolException pex) {
/*  775 */           throw new MessagingException(pex.getMessage(), pex);
/*      */         } 
/*  777 */         return (this.recent > 0);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  784 */     ListInfo[] li = null;
/*      */     
/*  786 */     if (this.isNamespace && this.separator != '\000') {
/*  787 */       lname = this.fullName + this.separator;
/*      */     } else {
/*  789 */       lname = this.fullName;
/*  790 */     }  li = (ListInfo[])doCommandIgnoreFailure(new ProtocolCommand() { private final String val$lname; private final IMAPFolder this$0;
/*      */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/*  792 */             return p.list("", lname);
/*      */           } }
/*      */       );
/*      */ 
/*      */     
/*  797 */     if (li == null) {
/*  798 */       throw new FolderNotFoundException(this, this.fullName + " not found");
/*      */     }
/*  800 */     int i = findName(li, lname);
/*  801 */     if ((li[i]).changeState == 1)
/*  802 */       return true; 
/*  803 */     if ((li[i]).changeState == 2) {
/*  804 */       return false;
/*      */     }
/*      */     
/*      */     try {
/*  808 */       Status status = getStatus();
/*  809 */       if (status.recent > 0) {
/*  810 */         return true;
/*      */       }
/*  812 */       return false;
/*  813 */     } catch (BadCommandException bex) {
/*      */       
/*  815 */       return false;
/*  816 */     } catch (ConnectionException cex) {
/*  817 */       throw new StoreClosedException(this.store, cex.getMessage());
/*  818 */     } catch (ProtocolException pex) {
/*  819 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Folder getFolder(String name) throws MessagingException {
/*  830 */     if (this.attributes != null && !isDirectory()) {
/*  831 */       throw new MessagingException("Cannot contain subfolders");
/*      */     }
/*  833 */     char c = getSeparator();
/*  834 */     return ((IMAPStore)this.store).newIMAPFolder(this.fullName + c + name, c);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean delete(boolean recurse) throws MessagingException {
/*  842 */     checkClosed();
/*      */     
/*  844 */     if (recurse) {
/*      */       
/*  846 */       Folder[] f = list();
/*  847 */       for (int i = 0; i < f.length; i++) {
/*  848 */         f[i].delete(recurse);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  853 */     Object ret = doCommandIgnoreFailure(new ProtocolCommand() { private final IMAPFolder this$0;
/*      */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/*  855 */             p.delete(IMAPFolder.this.fullName);
/*  856 */             return Boolean.TRUE;
/*      */           } }
/*      */       );
/*      */     
/*  860 */     if (ret == null)
/*      */     {
/*  862 */       return false;
/*      */     }
/*      */     
/*  865 */     this.exists = false;
/*  866 */     this.attributes = null;
/*      */ 
/*      */     
/*  869 */     notifyFolderListeners(2);
/*  870 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean renameTo(final Folder f) throws MessagingException {
/*  878 */     checkClosed();
/*  879 */     checkExists();
/*  880 */     if (f.getStore() != this.store) {
/*  881 */       throw new MessagingException("Can't rename across Stores");
/*      */     }
/*      */     
/*  884 */     Object ret = doCommandIgnoreFailure(new ProtocolCommand() { private final Folder val$f; private final IMAPFolder this$0;
/*      */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/*  886 */             p.rename(IMAPFolder.this.fullName, f.getFullName());
/*  887 */             return Boolean.TRUE;
/*      */           } }
/*      */       );
/*      */     
/*  891 */     if (ret == null) {
/*  892 */       return false;
/*      */     }
/*  894 */     this.exists = false;
/*  895 */     this.attributes = null;
/*  896 */     notifyFolderRenamedListeners(f);
/*  897 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void open(int mode) throws MessagingException {
/*  904 */     checkClosed();
/*      */     
/*  906 */     MailboxInfo mi = null;
/*      */     
/*  908 */     this.protocol = ((IMAPStore)this.store).getProtocol(this);
/*      */     
/*  910 */     synchronized (this.messageCacheLock) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  918 */       this.protocol.addResponseHandler(this);
/*      */       
/*      */       try {
/*  921 */         if (mode == 1)
/*  922 */         { mi = this.protocol.examine(this.fullName); }
/*      */         else
/*  924 */         { mi = this.protocol.select(this.fullName); } 
/*  925 */       } catch (CommandFailedException cex) {
/*      */         
/*      */         try
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  934 */           checkExists();
/*      */           
/*  936 */           if ((this.type & 0x1) == 0) {
/*  937 */             throw new MessagingException("folder cannot contain messages");
/*      */           }
/*  939 */           throw new MessagingException(cex.getMessage(), cex);
/*      */         }
/*      */         finally
/*      */         {
/*  943 */           this.exists = false;
/*  944 */           this.attributes = null;
/*  945 */           this.type = 0;
/*      */           
/*  947 */           releaseProtocol(true);
/*      */         }
/*      */       
/*  950 */       } catch (ProtocolException pex) {
/*      */         
/*      */         try {
/*  953 */           this.protocol.logout();
/*  954 */         } catch (ProtocolException pex2) {
/*      */         
/*      */         } finally {
/*  957 */           releaseProtocol(false);
/*  958 */           throw new MessagingException(pex.getMessage(), pex);
/*      */         } 
/*      */       } 
/*      */       
/*  962 */       if (mi.mode != mode && (
/*  963 */         mode != 2 || mi.mode != 1 || !((IMAPStore)this.store).allowReadOnlySelect())) {
/*      */         
/*      */         try {
/*      */ 
/*      */ 
/*      */           
/*  969 */           this.protocol.close();
/*  970 */           releaseProtocol(true);
/*  971 */         } catch (ProtocolException pex) {
/*      */           
/*      */           try {
/*  974 */             this.protocol.logout();
/*  975 */           } catch (ProtocolException pex2) {
/*      */           
/*      */           } finally {
/*  978 */             releaseProtocol(false);
/*      */           } 
/*      */         } finally {
/*  981 */           throw new ReadOnlyFolderException(this, "Cannot open in desired mode");
/*      */         } 
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  989 */       this.opened = true;
/*  990 */       this.reallyClosed = false;
/*  991 */       this.mode = mi.mode;
/*  992 */       this.availableFlags = mi.availableFlags;
/*  993 */       this.permanentFlags = mi.permanentFlags;
/*  994 */       this.total = this.realTotal = mi.total;
/*  995 */       this.recent = mi.recent;
/*  996 */       this.uidvalidity = mi.uidvalidity;
/*  997 */       this.uidnext = mi.uidnext;
/*      */ 
/*      */       
/* 1000 */       this.messageCache = new MessageCache(this, (IMAPStore)this.store, this.total);
/*      */     } 
/*      */ 
/*      */     
/* 1004 */     this.exists = true;
/* 1005 */     this.attributes = null;
/* 1006 */     this.type = 1;
/*      */ 
/*      */     
/* 1009 */     notifyConnectionListeners(1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void fetch(Message[] msgs, FetchProfile fp) throws MessagingException {
/* 1017 */     checkOpened();
/*      */     
/* 1019 */     StringBuffer command = new StringBuffer();
/* 1020 */     boolean first = true;
/* 1021 */     boolean allHeaders = false;
/*      */     
/* 1023 */     if (fp.contains(FetchProfile.Item.ENVELOPE)) {
/* 1024 */       command.append(getEnvelopeCommand());
/* 1025 */       first = false;
/*      */     } 
/* 1027 */     if (fp.contains(FetchProfile.Item.FLAGS)) {
/* 1028 */       command.append(first ? "FLAGS" : " FLAGS");
/* 1029 */       first = false;
/*      */     } 
/* 1031 */     if (fp.contains(FetchProfile.Item.CONTENT_INFO)) {
/* 1032 */       command.append(first ? "BODYSTRUCTURE" : " BODYSTRUCTURE");
/* 1033 */       first = false;
/*      */     } 
/* 1035 */     if (fp.contains((FetchProfile.Item)UIDFolder.FetchProfileItem.UID)) {
/* 1036 */       command.append(first ? "UID" : " UID");
/* 1037 */       first = false;
/*      */     } 
/* 1039 */     if (fp.contains(FetchProfileItem.HEADERS)) {
/* 1040 */       allHeaders = true;
/* 1041 */       if (this.protocol.isREV1()) {
/* 1042 */         command.append(first ? "BODY.PEEK[HEADER]" : " BODY.PEEK[HEADER]");
/*      */       } else {
/*      */         
/* 1045 */         command.append(first ? "RFC822.HEADER" : " RFC822.HEADER");
/* 1046 */       }  first = false;
/*      */     } 
/* 1048 */     if (fp.contains(FetchProfileItem.SIZE)) {
/* 1049 */       command.append(first ? "RFC822.SIZE" : " RFC822.SIZE");
/* 1050 */       first = false;
/*      */     } 
/*      */ 
/*      */     
/* 1054 */     String[] hdrs = null;
/* 1055 */     if (!allHeaders) {
/* 1056 */       hdrs = fp.getHeaderNames();
/* 1057 */       if (hdrs.length > 0) {
/* 1058 */         if (!first)
/* 1059 */           command.append(" "); 
/* 1060 */         command.append(createHeaderCommand(hdrs));
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1067 */     FetchItem[] fitems = this.protocol.getFetchItems();
/* 1068 */     for (int i = 0; i < fitems.length; i++) {
/* 1069 */       if (fp.contains(fitems[i].getFetchProfileItem())) {
/* 1070 */         if (command.length() != 0)
/* 1071 */           command.append(" "); 
/* 1072 */         command.append(fitems[i].getName());
/*      */       } 
/*      */     } 
/*      */     
/* 1076 */     Utility.Condition condition = new IMAPMessage.FetchProfileCondition(fp, fitems);
/*      */ 
/*      */ 
/*      */     
/* 1080 */     synchronized (this.messageCacheLock) {
/*      */ 
/*      */ 
/*      */       
/* 1084 */       MessageSet[] msgsets = Utility.toMessageSet(msgs, condition);
/*      */       
/* 1086 */       if (msgsets == null) {
/*      */         return;
/*      */       }
/*      */       
/* 1090 */       Response[] r = null;
/* 1091 */       Vector v = new Vector();
/*      */       
/*      */       try {
/* 1094 */         r = getProtocol().fetch(msgsets, command.toString());
/* 1095 */       } catch (ConnectionException cex) {
/* 1096 */         throw new FolderClosedException(this, cex.getMessage());
/* 1097 */       } catch (CommandFailedException cfx) {
/*      */       
/* 1099 */       } catch (ProtocolException pex) {
/* 1100 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */       
/* 1103 */       if (r == null) {
/*      */         return;
/*      */       }
/* 1106 */       for (int j = 0; j < r.length; j++) {
/* 1107 */         if (r[j] != null)
/*      */         {
/* 1109 */           if (!(r[j] instanceof FetchResponse)) {
/* 1110 */             v.addElement(r[j]);
/*      */           
/*      */           }
/*      */           else {
/*      */             
/* 1115 */             FetchResponse f = (FetchResponse)r[j];
/*      */             
/* 1117 */             IMAPMessage msg = getMessageBySeqNumber(f.getNumber());
/*      */             
/* 1119 */             int count = f.getItemCount();
/* 1120 */             boolean unsolicitedFlags = false;
/*      */             
/* 1122 */             for (int k = 0; k < count; k++) {
/* 1123 */               Item item = f.getItem(k);
/*      */               
/* 1125 */               if (item instanceof Flags && (!fp.contains(FetchProfile.Item.FLAGS) || msg == null)) {
/*      */ 
/*      */ 
/*      */                 
/* 1129 */                 unsolicitedFlags = true;
/* 1130 */               } else if (msg != null) {
/* 1131 */                 msg.handleFetchItem(item, hdrs, allHeaders);
/*      */               } 
/* 1133 */             }  if (msg != null) {
/* 1134 */               msg.handleExtensionFetchItems(f.getExtensionItems());
/*      */             }
/*      */ 
/*      */             
/* 1138 */             if (unsolicitedFlags)
/* 1139 */               v.addElement(f); 
/*      */           } 
/*      */         }
/*      */       } 
/* 1143 */       int size = v.size();
/* 1144 */       if (size != 0) {
/* 1145 */         Response[] responses = new Response[size];
/* 1146 */         v.copyInto((Object[])responses);
/* 1147 */         handleResponses(responses);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String getEnvelopeCommand() {
/* 1162 */     return "ENVELOPE INTERNALDATE RFC822.SIZE";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IMAPMessage newIMAPMessage(int msgnum) {
/* 1173 */     return new IMAPMessage(this, msgnum);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String createHeaderCommand(String[] hdrs) {
/*      */     StringBuffer sb;
/* 1183 */     if (this.protocol.isREV1()) {
/* 1184 */       sb = new StringBuffer("BODY.PEEK[HEADER.FIELDS (");
/*      */     } else {
/* 1186 */       sb = new StringBuffer("RFC822.HEADER.LINES (");
/*      */     } 
/* 1188 */     for (int i = 0; i < hdrs.length; i++) {
/* 1189 */       if (i > 0)
/* 1190 */         sb.append(" "); 
/* 1191 */       sb.append(hdrs[i]);
/*      */     } 
/*      */     
/* 1194 */     if (this.protocol.isREV1()) {
/* 1195 */       sb.append(")]");
/*      */     } else {
/* 1197 */       sb.append(")");
/*      */     } 
/* 1199 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setFlags(Message[] msgs, Flags flag, boolean value) throws MessagingException {
/* 1207 */     checkOpened();
/* 1208 */     checkFlags(flag);
/*      */     
/* 1210 */     if (msgs.length == 0) {
/*      */       return;
/*      */     }
/* 1213 */     synchronized (this.messageCacheLock) {
/*      */       try {
/* 1215 */         IMAPProtocol p = getProtocol();
/* 1216 */         MessageSet[] ms = Utility.toMessageSet(msgs, null);
/* 1217 */         if (ms == null) {
/* 1218 */           throw new MessageRemovedException("Messages have been removed");
/*      */         }
/* 1220 */         p.storeFlags(ms, flag, value);
/* 1221 */       } catch (ConnectionException cex) {
/* 1222 */         throw new FolderClosedException(this, cex.getMessage());
/* 1223 */       } catch (ProtocolException pex) {
/* 1224 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setFlags(int start, int end, Flags flag, boolean value) throws MessagingException {
/* 1234 */     checkOpened();
/* 1235 */     Message[] msgs = new Message[end - start + 1];
/* 1236 */     int i = 0;
/* 1237 */     for (int n = start; n <= end; n++)
/* 1238 */       msgs[i++] = getMessage(n); 
/* 1239 */     setFlags(msgs, flag, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setFlags(int[] msgnums, Flags flag, boolean value) throws MessagingException {
/* 1247 */     checkOpened();
/* 1248 */     Message[] msgs = new Message[msgnums.length];
/* 1249 */     for (int i = 0; i < msgnums.length; i++)
/* 1250 */       msgs[i] = getMessage(msgnums[i]); 
/* 1251 */     setFlags(msgs, flag, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void close(boolean expunge) throws MessagingException {
/* 1258 */     close(expunge, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void forceClose() throws MessagingException {
/* 1265 */     close(false, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void close(boolean expunge, boolean force) throws MessagingException {
/* 1273 */     assert Thread.holdsLock(this);
/* 1274 */     synchronized (this.messageCacheLock) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1281 */       if (!this.opened && this.reallyClosed) {
/* 1282 */         throw new IllegalStateException("This operation is not allowed on a closed folder");
/*      */       }
/*      */ 
/*      */       
/* 1286 */       this.reallyClosed = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1292 */       if (!this.opened) {
/*      */         return;
/*      */       }
/*      */       try {
/* 1296 */         waitIfIdle();
/* 1297 */         if (force) {
/* 1298 */           this.logger.log(Level.FINE, "forcing folder {0} to close", this.fullName);
/*      */           
/* 1300 */           if (this.protocol != null)
/* 1301 */             this.protocol.disconnect(); 
/* 1302 */         } else if (((IMAPStore)this.store).isConnectionPoolFull()) {
/*      */           
/* 1304 */           this.logger.fine("pool is full, not adding an Authenticated connection");
/*      */ 
/*      */ 
/*      */           
/* 1308 */           if (expunge && this.protocol != null) {
/* 1309 */             this.protocol.close();
/*      */           }
/* 1311 */           if (this.protocol != null) {
/* 1312 */             this.protocol.logout();
/*      */           
/*      */           }
/*      */         
/*      */         }
/* 1317 */         else if (!expunge && this.mode == 2) {
/*      */           try {
/* 1319 */             if (this.protocol != null && this.protocol.hasCapability("UNSELECT")) {
/*      */               
/* 1321 */               this.protocol.unselect();
/*      */             }
/* 1323 */             else if (this.protocol != null) {
/* 1324 */               MailboxInfo mi = this.protocol.examine(this.fullName);
/* 1325 */               if (this.protocol != null) {
/* 1326 */                 this.protocol.close();
/*      */               }
/*      */             } 
/* 1329 */           } catch (ProtocolException pex2) {
/* 1330 */             if (this.protocol != null) {
/* 1331 */               this.protocol.disconnect();
/*      */             }
/*      */           } 
/* 1334 */         } else if (this.protocol != null) {
/* 1335 */           this.protocol.close();
/*      */         }
/*      */       
/* 1338 */       } catch (ProtocolException pex) {
/* 1339 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } finally {
/*      */         
/* 1342 */         if (this.opened) {
/* 1343 */           cleanup(true);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void cleanup(boolean returnToPool) {
/* 1354 */     assert Thread.holdsLock(this.messageCacheLock);
/* 1355 */     releaseProtocol(returnToPool);
/* 1356 */     this.messageCache = null;
/* 1357 */     this.uidTable = null;
/* 1358 */     this.exists = false;
/* 1359 */     this.attributes = null;
/* 1360 */     this.opened = false;
/* 1361 */     this.idleState = 0;
/* 1362 */     notifyConnectionListeners(3);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized boolean isOpen() {
/* 1369 */     synchronized (this.messageCacheLock) {
/*      */       
/* 1371 */       if (this.opened) {
/*      */         try {
/* 1373 */           keepConnectionAlive(false);
/* 1374 */         } catch (ProtocolException pex) {}
/*      */       }
/*      */     } 
/*      */     
/* 1378 */     return this.opened;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Flags getPermanentFlags() {
/* 1385 */     if (this.permanentFlags == null)
/* 1386 */       return null; 
/* 1387 */     return (Flags)this.permanentFlags.clone();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized int getMessageCount() throws MessagingException {
/* 1394 */     if (!this.opened) {
/* 1395 */       checkExists();
/*      */ 
/*      */       
/*      */       try {
/* 1399 */         Status status = getStatus();
/* 1400 */         return status.total;
/* 1401 */       } catch (BadCommandException bex) {
/*      */ 
/*      */         
/* 1404 */         IMAPProtocol p = null;
/*      */         
/*      */         try {
/* 1407 */           p = getStoreProtocol();
/* 1408 */           MailboxInfo minfo = p.examine(this.fullName);
/* 1409 */           p.close();
/* 1410 */           return minfo.total;
/* 1411 */         } catch (ProtocolException pex) {
/*      */           
/* 1413 */           throw new MessagingException(pex.getMessage(), pex);
/*      */         } finally {
/* 1415 */           releaseStoreProtocol(p);
/*      */         } 
/* 1417 */       } catch (ConnectionException cex) {
/* 1418 */         throw new StoreClosedException(this.store, cex.getMessage());
/* 1419 */       } catch (ProtocolException pex) {
/* 1420 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1425 */     synchronized (this.messageCacheLock) {
/*      */ 
/*      */       
/* 1428 */       keepConnectionAlive(true);
/* 1429 */       return this.total;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized int getNewMessageCount() throws MessagingException {
/* 1443 */     if (!this.opened) {
/* 1444 */       checkExists();
/*      */ 
/*      */       
/*      */       try {
/* 1448 */         Status status = getStatus();
/* 1449 */         return status.recent;
/* 1450 */       } catch (BadCommandException bex) {
/*      */ 
/*      */         
/* 1453 */         IMAPProtocol p = null;
/*      */         
/*      */         try {
/* 1456 */           p = getStoreProtocol();
/* 1457 */           MailboxInfo minfo = p.examine(this.fullName);
/* 1458 */           p.close();
/* 1459 */           return minfo.recent;
/* 1460 */         } catch (ProtocolException pex) {
/*      */           
/* 1462 */           throw new MessagingException(pex.getMessage(), pex);
/*      */         } finally {
/* 1464 */           releaseStoreProtocol(p);
/*      */         } 
/* 1466 */       } catch (ConnectionException cex) {
/* 1467 */         throw new StoreClosedException(this.store, cex.getMessage());
/* 1468 */       } catch (ProtocolException pex) {
/* 1469 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1474 */     synchronized (this.messageCacheLock) {
/*      */ 
/*      */       
/* 1477 */       keepConnectionAlive(true);
/* 1478 */       return this.recent;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized int getUnreadMessageCount() throws MessagingException {
/* 1492 */     if (!this.opened) {
/* 1493 */       checkExists();
/*      */ 
/*      */       
/*      */       try {
/* 1497 */         Status status = getStatus();
/* 1498 */         return status.unseen;
/* 1499 */       } catch (BadCommandException bex) {
/*      */ 
/*      */ 
/*      */         
/* 1503 */         return -1;
/* 1504 */       } catch (ConnectionException cex) {
/* 1505 */         throw new StoreClosedException(this.store, cex.getMessage());
/* 1506 */       } catch (ProtocolException pex) {
/* 1507 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1513 */     Flags f = new Flags();
/* 1514 */     f.add(Flags.Flag.SEEN);
/*      */     try {
/* 1516 */       synchronized (this.messageCacheLock) {
/* 1517 */         int[] matches = getProtocol().search((SearchTerm)new FlagTerm(f, false));
/* 1518 */         return matches.length;
/*      */       } 
/* 1520 */     } catch (ConnectionException cex) {
/* 1521 */       throw new FolderClosedException(this, cex.getMessage());
/* 1522 */     } catch (ProtocolException pex) {
/*      */       
/* 1524 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized int getDeletedMessageCount() throws MessagingException {
/* 1533 */     if (!this.opened) {
/* 1534 */       checkExists();
/*      */       
/* 1536 */       return -1;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1541 */     Flags f = new Flags();
/* 1542 */     f.add(Flags.Flag.DELETED);
/*      */     try {
/* 1544 */       synchronized (this.messageCacheLock) {
/* 1545 */         int[] matches = getProtocol().search((SearchTerm)new FlagTerm(f, true));
/* 1546 */         return matches.length;
/*      */       } 
/* 1548 */     } catch (ConnectionException cex) {
/* 1549 */       throw new FolderClosedException(this, cex.getMessage());
/* 1550 */     } catch (ProtocolException pex) {
/*      */       
/* 1552 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Status getStatus() throws ProtocolException {
/* 1562 */     int statusCacheTimeout = ((IMAPStore)this.store).getStatusCacheTimeout();
/*      */ 
/*      */     
/* 1565 */     if (statusCacheTimeout > 0 && this.cachedStatus != null && System.currentTimeMillis() - this.cachedStatusTime < statusCacheTimeout)
/*      */     {
/* 1567 */       return this.cachedStatus;
/*      */     }
/* 1569 */     IMAPProtocol p = null;
/*      */     
/*      */     try {
/* 1572 */       p = getStoreProtocol();
/* 1573 */       Status s = p.status(this.fullName, null);
/*      */       
/* 1575 */       if (statusCacheTimeout > 0) {
/* 1576 */         this.cachedStatus = s;
/* 1577 */         this.cachedStatusTime = System.currentTimeMillis();
/*      */       } 
/* 1579 */       return s;
/*      */     } finally {
/* 1581 */       releaseStoreProtocol(p);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Message getMessage(int msgnum) throws MessagingException {
/* 1590 */     checkOpened();
/* 1591 */     checkRange(msgnum);
/*      */     
/* 1593 */     return (Message)this.messageCache.getMessage(msgnum);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void appendMessages(Message[] msgs) throws MessagingException {
/* 1601 */     checkExists();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1608 */     int maxsize = ((IMAPStore)this.store).getAppendBufferSize();
/*      */     
/* 1610 */     for (int i = 0; i < msgs.length; i++) {
/* 1611 */       final MessageLiteral mos; Message m = msgs[i];
/* 1612 */       Date d = m.getReceivedDate();
/* 1613 */       if (d == null)
/* 1614 */         d = m.getSentDate(); 
/* 1615 */       final Date dd = d;
/* 1616 */       final Flags f = m.getFlags();
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/* 1621 */         mos = new MessageLiteral(m, (m.getSize() > maxsize) ? 0 : maxsize);
/*      */       }
/* 1623 */       catch (IOException ex) {
/* 1624 */         throw new MessagingException("IOException while appending messages", ex);
/*      */       }
/* 1626 */       catch (MessageRemovedException mrex) {}
/*      */ 
/*      */ 
/*      */       
/* 1630 */       doCommand(new ProtocolCommand() { private final Flags val$f; private final Date val$dd;
/*      */             
/*      */             public Object doCommand(IMAPProtocol p) throws ProtocolException {
/* 1633 */               p.append(IMAPFolder.this.fullName, f, dd, mos);
/* 1634 */               return null;
/*      */             }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             private final MessageLiteral val$mos;
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             private final IMAPFolder this$0; }
/*      */         );
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized AppendUID[] appendUIDMessages(Message[] msgs) throws MessagingException {
/* 1657 */     checkExists();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1664 */     int maxsize = ((IMAPStore)this.store).getAppendBufferSize();
/*      */     
/* 1666 */     AppendUID[] uids = new AppendUID[msgs.length];
/* 1667 */     for (int i = 0; i < msgs.length; i++) {
/* 1668 */       final MessageLiteral mos; Message m = msgs[i];
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/* 1673 */         mos = new MessageLiteral(m, (m.getSize() > maxsize) ? 0 : maxsize);
/*      */       }
/* 1675 */       catch (IOException ex) {
/* 1676 */         throw new MessagingException("IOException while appending messages", ex);
/*      */       }
/* 1678 */       catch (MessageRemovedException mrex) {}
/*      */ 
/*      */ 
/*      */       
/* 1682 */       Date d = m.getReceivedDate();
/* 1683 */       if (d == null)
/* 1684 */         d = m.getSentDate(); 
/* 1685 */       final Date dd = d;
/* 1686 */       final Flags f = m.getFlags();
/* 1687 */       AppendUID auid = (AppendUID)doCommand(new ProtocolCommand() { private final Flags val$f; private final Date val$dd;
/*      */             
/*      */             public Object doCommand(IMAPProtocol p) throws ProtocolException {
/* 1690 */               return p.appenduid(IMAPFolder.this.fullName, f, dd, mos);
/*      */             } private final MessageLiteral val$mos; private final IMAPFolder this$0; }
/*      */         );
/* 1693 */       uids[i] = auid;
/*      */     } 
/* 1695 */     return uids;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Message[] addMessages(Message[] msgs) throws MessagingException {
/* 1716 */     checkOpened();
/* 1717 */     MimeMessage[] arrayOfMimeMessage = new MimeMessage[msgs.length];
/* 1718 */     AppendUID[] uids = appendUIDMessages(msgs);
/* 1719 */     for (int i = 0; i < uids.length; i++) {
/* 1720 */       AppendUID auid = uids[i];
/* 1721 */       if (auid != null && 
/* 1722 */         auid.uidvalidity == this.uidvalidity) {
/*      */         try {
/* 1724 */           arrayOfMimeMessage[i] = (MimeMessage)getMessageByUID(auid.uid);
/* 1725 */         } catch (MessagingException mex) {}
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1731 */     return (Message[])arrayOfMimeMessage;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void copyMessages(Message[] msgs, Folder folder) throws MessagingException {
/* 1740 */     checkOpened();
/*      */     
/* 1742 */     if (msgs.length == 0) {
/*      */       return;
/*      */     }
/*      */     
/* 1746 */     if (folder.getStore() == this.store) {
/* 1747 */       synchronized (this.messageCacheLock) {
/*      */         try {
/* 1749 */           IMAPProtocol p = getProtocol();
/* 1750 */           MessageSet[] ms = Utility.toMessageSet(msgs, null);
/* 1751 */           if (ms == null) {
/* 1752 */             throw new MessageRemovedException("Messages have been removed");
/*      */           }
/* 1754 */           p.copy(ms, folder.getFullName());
/* 1755 */         } catch (CommandFailedException cfx) {
/* 1756 */           if (cfx.getMessage().indexOf("TRYCREATE") != -1) {
/* 1757 */             throw new FolderNotFoundException(folder, folder.getFullName() + " does not exist");
/*      */           }
/*      */ 
/*      */ 
/*      */           
/* 1762 */           throw new MessagingException(cfx.getMessage(), cfx);
/* 1763 */         } catch (ConnectionException cex) {
/* 1764 */           throw new FolderClosedException(this, cex.getMessage());
/* 1765 */         } catch (ProtocolException pex) {
/* 1766 */           throw new MessagingException(pex.getMessage(), pex);
/*      */         } 
/*      */       } 
/*      */     } else {
/* 1770 */       super.copyMessages(msgs, folder);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Message[] expunge() throws MessagingException {
/* 1777 */     return expunge((Message[])null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Message[] expunge(Message[] msgs) throws MessagingException {
/*      */     IMAPMessage[] rmsgs;
/* 1788 */     checkOpened();
/*      */     
/* 1790 */     if (msgs != null) {
/*      */       
/* 1792 */       FetchProfile fp = new FetchProfile();
/* 1793 */       fp.add((FetchProfile.Item)UIDFolder.FetchProfileItem.UID);
/* 1794 */       fetch(msgs, fp);
/*      */     } 
/*      */ 
/*      */     
/* 1798 */     synchronized (this.messageCacheLock) {
/* 1799 */       this.doExpungeNotification = false;
/*      */       try {
/* 1801 */         IMAPProtocol p = getProtocol();
/* 1802 */         if (msgs != null)
/* 1803 */         { p.uidexpunge(Utility.toUIDSet(msgs)); }
/*      */         else
/* 1805 */         { p.expunge(); } 
/* 1806 */       } catch (CommandFailedException cfx) {
/*      */         
/* 1808 */         if (this.mode != 2) {
/* 1809 */           throw new IllegalStateException("Cannot expunge READ_ONLY folder: " + this.fullName);
/*      */         }
/*      */         
/* 1812 */         throw new MessagingException(cfx.getMessage(), cfx);
/* 1813 */       } catch (ConnectionException cex) {
/* 1814 */         throw new FolderClosedException(this, cex.getMessage());
/* 1815 */       } catch (ProtocolException pex) {
/*      */         
/* 1817 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } finally {
/* 1819 */         this.doExpungeNotification = true;
/*      */       } 
/*      */ 
/*      */       
/* 1823 */       if (msgs != null) {
/* 1824 */         rmsgs = this.messageCache.removeExpungedMessages(msgs);
/*      */       } else {
/* 1826 */         rmsgs = this.messageCache.removeExpungedMessages();
/* 1827 */       }  if (this.uidTable != null) {
/* 1828 */         for (int i = 0; i < rmsgs.length; i++) {
/* 1829 */           IMAPMessage m = rmsgs[i];
/*      */           
/* 1831 */           long uid = m.getUID();
/* 1832 */           if (uid != -1L) {
/* 1833 */             this.uidTable.remove(new Long(uid));
/*      */           }
/*      */         } 
/*      */       }
/*      */       
/* 1838 */       this.total = this.messageCache.size();
/*      */     } 
/*      */ 
/*      */     
/* 1842 */     if (rmsgs.length > 0)
/* 1843 */       notifyMessageRemovedListeners(true, (Message[])rmsgs); 
/* 1844 */     return (Message[])rmsgs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Message[] search(SearchTerm term) throws MessagingException {
/* 1852 */     checkOpened();
/*      */     try {
/*      */       IMAPMessage[] arrayOfIMAPMessage;
/* 1855 */       Message[] matchMsgs = null;
/*      */       
/* 1857 */       synchronized (this.messageCacheLock) {
/* 1858 */         int[] matches = getProtocol().search(term);
/* 1859 */         if (matches != null) {
/* 1860 */           arrayOfIMAPMessage = new IMAPMessage[matches.length];
/*      */           
/* 1862 */           for (int i = 0; i < matches.length; i++)
/* 1863 */             arrayOfIMAPMessage[i] = getMessageBySeqNumber(matches[i]); 
/*      */         } 
/*      */       } 
/* 1866 */       return (Message[])arrayOfIMAPMessage;
/*      */     }
/* 1868 */     catch (CommandFailedException cfx) {
/*      */       
/* 1870 */       return super.search(term);
/* 1871 */     } catch (SearchException sex) {
/*      */       
/* 1873 */       return super.search(term);
/* 1874 */     } catch (ConnectionException cex) {
/* 1875 */       throw new FolderClosedException(this, cex.getMessage());
/* 1876 */     } catch (ProtocolException pex) {
/*      */       
/* 1878 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Message[] search(SearchTerm term, Message[] msgs) throws MessagingException {
/* 1889 */     checkOpened();
/*      */     
/* 1891 */     if (msgs.length == 0)
/*      */     {
/* 1893 */       return msgs; } 
/*      */     try {
/*      */       IMAPMessage[] arrayOfIMAPMessage;
/* 1896 */       Message[] matchMsgs = null;
/*      */       
/* 1898 */       synchronized (this.messageCacheLock) {
/* 1899 */         IMAPProtocol p = getProtocol();
/* 1900 */         MessageSet[] ms = Utility.toMessageSet(msgs, null);
/* 1901 */         if (ms == null) {
/* 1902 */           throw new MessageRemovedException("Messages have been removed");
/*      */         }
/* 1904 */         int[] matches = p.search(ms, term);
/* 1905 */         if (matches != null) {
/* 1906 */           arrayOfIMAPMessage = new IMAPMessage[matches.length];
/* 1907 */           for (int i = 0; i < matches.length; i++)
/* 1908 */             arrayOfIMAPMessage[i] = getMessageBySeqNumber(matches[i]); 
/*      */         } 
/*      */       } 
/* 1911 */       return (Message[])arrayOfIMAPMessage;
/*      */     }
/* 1913 */     catch (CommandFailedException cfx) {
/*      */       
/* 1915 */       return super.search(term, msgs);
/* 1916 */     } catch (SearchException sex) {
/*      */       
/* 1918 */       return super.search(term, msgs);
/* 1919 */     } catch (ConnectionException cex) {
/* 1920 */       throw new FolderClosedException(this, cex.getMessage());
/* 1921 */     } catch (ProtocolException pex) {
/*      */       
/* 1923 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Message[] getSortedMessages(SortTerm[] term) throws MessagingException {
/* 1939 */     return getSortedMessages(term, (SearchTerm)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Message[] getSortedMessages(SortTerm[] term, SearchTerm sterm) throws MessagingException {
/* 1955 */     checkOpened();
/*      */     try {
/*      */       IMAPMessage[] arrayOfIMAPMessage;
/* 1958 */       Message[] matchMsgs = null;
/*      */       
/* 1960 */       synchronized (this.messageCacheLock) {
/* 1961 */         int[] matches = getProtocol().sort(term, sterm);
/* 1962 */         if (matches != null) {
/* 1963 */           arrayOfIMAPMessage = new IMAPMessage[matches.length];
/*      */           
/* 1965 */           for (int i = 0; i < matches.length; i++)
/* 1966 */             arrayOfIMAPMessage[i] = getMessageBySeqNumber(matches[i]); 
/*      */         } 
/*      */       } 
/* 1969 */       return (Message[])arrayOfIMAPMessage;
/*      */     }
/* 1971 */     catch (CommandFailedException cfx) {
/*      */       
/* 1973 */       throw new MessagingException(cfx.getMessage(), cfx);
/* 1974 */     } catch (SearchException sex) {
/*      */       
/* 1976 */       throw new MessagingException(sex.getMessage(), sex);
/* 1977 */     } catch (ConnectionException cex) {
/* 1978 */       throw new FolderClosedException(this, cex.getMessage());
/* 1979 */     } catch (ProtocolException pex) {
/*      */       
/* 1981 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void addMessageCountListener(MessageCountListener l) {
/* 1993 */     super.addMessageCountListener(l);
/* 1994 */     this.hasMessageCountListener = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized long getUIDValidity() throws MessagingException {
/* 2005 */     if (this.opened) {
/* 2006 */       return this.uidvalidity;
/*      */     }
/* 2008 */     IMAPProtocol p = null;
/* 2009 */     Status status = null;
/*      */     
/*      */     try {
/* 2012 */       p = getStoreProtocol();
/* 2013 */       String[] item = { "UIDVALIDITY" };
/* 2014 */       status = p.status(this.fullName, item);
/* 2015 */     } catch (BadCommandException bex) {
/*      */       
/* 2017 */       throw new MessagingException("Cannot obtain UIDValidity", bex);
/* 2018 */     } catch (ConnectionException cex) {
/*      */       
/* 2020 */       throwClosedException(cex);
/* 2021 */     } catch (ProtocolException pex) {
/* 2022 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } finally {
/* 2024 */       releaseStoreProtocol(p);
/*      */     } 
/*      */     
/* 2027 */     return status.uidvalidity;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized long getUIDNext() throws MessagingException {
/* 2049 */     if (this.opened) {
/* 2050 */       return this.uidnext;
/*      */     }
/* 2052 */     IMAPProtocol p = null;
/* 2053 */     Status status = null;
/*      */     
/*      */     try {
/* 2056 */       p = getStoreProtocol();
/* 2057 */       String[] item = { "UIDNEXT" };
/* 2058 */       status = p.status(this.fullName, item);
/* 2059 */     } catch (BadCommandException bex) {
/*      */       
/* 2061 */       throw new MessagingException("Cannot obtain UIDNext", bex);
/* 2062 */     } catch (ConnectionException cex) {
/*      */       
/* 2064 */       throwClosedException(cex);
/* 2065 */     } catch (ProtocolException pex) {
/* 2066 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } finally {
/* 2068 */       releaseStoreProtocol(p);
/*      */     } 
/*      */     
/* 2071 */     return status.uidnext;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Message getMessageByUID(long uid) throws MessagingException {
/* 2080 */     checkOpened();
/*      */     
/* 2082 */     IMAPMessage m = null;
/*      */     
/*      */     try {
/* 2085 */       synchronized (this.messageCacheLock) {
/* 2086 */         Long l = new Long(uid);
/*      */         
/* 2088 */         if (this.uidTable != null) {
/*      */           
/* 2090 */           m = (IMAPMessage)this.uidTable.get(l);
/* 2091 */           if (m != null)
/* 2092 */             return (Message)m; 
/*      */         } else {
/* 2094 */           this.uidTable = new Hashtable();
/*      */         } 
/*      */ 
/*      */         
/* 2098 */         UID u = getProtocol().fetchSequenceNumber(uid);
/*      */         
/* 2100 */         if (u != null && u.seqnum <= this.total) {
/* 2101 */           m = getMessageBySeqNumber(u.seqnum);
/* 2102 */           m.setUID(u.uid);
/*      */           
/* 2104 */           this.uidTable.put(l, m);
/*      */         } 
/*      */       } 
/* 2107 */     } catch (ConnectionException cex) {
/* 2108 */       throw new FolderClosedException(this, cex.getMessage());
/* 2109 */     } catch (ProtocolException pex) {
/* 2110 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } 
/*      */     
/* 2113 */     return (Message)m;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Message[] getMessagesByUID(long start, long end) throws MessagingException {
/*      */     Message[] msgs;
/* 2123 */     checkOpened();
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 2128 */       synchronized (this.messageCacheLock) {
/* 2129 */         if (this.uidTable == null) {
/* 2130 */           this.uidTable = new Hashtable();
/*      */         }
/*      */         
/* 2133 */         UID[] ua = getProtocol().fetchSequenceNumbers(start, end);
/*      */         
/* 2135 */         msgs = new Message[ua.length];
/*      */ 
/*      */         
/* 2138 */         for (int i = 0; i < ua.length; i++) {
/* 2139 */           IMAPMessage m = getMessageBySeqNumber((ua[i]).seqnum);
/* 2140 */           m.setUID((ua[i]).uid);
/* 2141 */           msgs[i] = (Message)m;
/* 2142 */           this.uidTable.put(new Long((ua[i]).uid), m);
/*      */         } 
/*      */       } 
/* 2145 */     } catch (ConnectionException cex) {
/* 2146 */       throw new FolderClosedException(this, cex.getMessage());
/* 2147 */     } catch (ProtocolException pex) {
/* 2148 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } 
/*      */     
/* 2151 */     return msgs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Message[] getMessagesByUID(long[] uids) throws MessagingException {
/* 2163 */     checkOpened();
/*      */     
/*      */     try {
/* 2166 */       synchronized (this.messageCacheLock) {
/* 2167 */         long[] unavailUids = uids;
/* 2168 */         if (this.uidTable != null) {
/* 2169 */           Vector v = new Vector();
/*      */           
/* 2171 */           for (int j = 0; j < uids.length; j++) {
/* 2172 */             Long l; if (!this.uidTable.containsKey(l = new Long(uids[j])))
/*      */             {
/* 2174 */               v.addElement(l);
/*      */             }
/*      */           } 
/* 2177 */           int vsize = v.size();
/* 2178 */           unavailUids = new long[vsize];
/* 2179 */           for (int k = 0; k < vsize; k++)
/* 2180 */             unavailUids[k] = ((Long)v.elementAt(k)).longValue(); 
/*      */         } else {
/* 2182 */           this.uidTable = new Hashtable();
/*      */         } 
/* 2184 */         if (unavailUids.length > 0) {
/*      */           
/* 2186 */           UID[] ua = getProtocol().fetchSequenceNumbers(unavailUids);
/*      */           
/* 2188 */           for (int j = 0; j < ua.length; j++) {
/* 2189 */             IMAPMessage m = getMessageBySeqNumber((ua[j]).seqnum);
/* 2190 */             m.setUID((ua[j]).uid);
/* 2191 */             this.uidTable.put(new Long((ua[j]).uid), m);
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/* 2196 */         Message[] msgs = new Message[uids.length];
/* 2197 */         for (int i = 0; i < uids.length; i++)
/* 2198 */           msgs[i] = (Message)this.uidTable.get(new Long(uids[i])); 
/* 2199 */         return msgs;
/*      */       } 
/* 2201 */     } catch (ConnectionException cex) {
/* 2202 */       throw new FolderClosedException(this, cex.getMessage());
/* 2203 */     } catch (ProtocolException pex) {
/* 2204 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized long getUID(Message message) throws MessagingException {
/* 2213 */     if (message.getFolder() != this) {
/* 2214 */       throw new NoSuchElementException("Message does not belong to this folder");
/*      */     }
/*      */     
/* 2217 */     checkOpened();
/*      */     
/* 2219 */     IMAPMessage m = (IMAPMessage)message;
/*      */     
/*      */     long uid;
/* 2222 */     if ((uid = m.getUID()) != -1L) {
/* 2223 */       return uid;
/*      */     }
/* 2225 */     synchronized (this.messageCacheLock) {
/*      */       try {
/* 2227 */         IMAPProtocol p = getProtocol();
/* 2228 */         m.checkExpunged();
/* 2229 */         UID u = p.fetchUID(m.getSequenceNumber());
/*      */         
/* 2231 */         if (u != null) {
/* 2232 */           uid = u.uid;
/* 2233 */           m.setUID(uid);
/*      */ 
/*      */           
/* 2236 */           if (this.uidTable == null)
/* 2237 */             this.uidTable = new Hashtable(); 
/* 2238 */           this.uidTable.put(new Long(uid), m);
/*      */         } 
/* 2240 */       } catch (ConnectionException cex) {
/* 2241 */         throw new FolderClosedException(this, cex.getMessage());
/* 2242 */       } catch (ProtocolException pex) {
/* 2243 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */     } 
/*      */     
/* 2247 */     return uid;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Quota[] getQuota() throws MessagingException {
/* 2268 */     return (Quota[])doOptionalCommand("QUOTA not supported", new ProtocolCommand() {
/*      */           private final IMAPFolder this$0;
/*      */           
/*      */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/* 2272 */             return p.getQuotaRoot(IMAPFolder.this.fullName);
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setQuota(final Quota quota) throws MessagingException {
/* 2288 */     doOptionalCommand("QUOTA not supported", new ProtocolCommand() { private final Quota val$quota;
/*      */           private final IMAPFolder this$0;
/*      */           
/*      */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/* 2292 */             p.setQuota(quota);
/* 2293 */             return null;
/*      */           } }
/*      */       );
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ACL[] getACL() throws MessagingException {
/* 2306 */     return (ACL[])doOptionalCommand("ACL not supported", new ProtocolCommand() {
/*      */           private final IMAPFolder this$0;
/*      */           
/*      */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/* 2310 */             return p.getACL(IMAPFolder.this.fullName);
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addACL(ACL acl) throws MessagingException {
/* 2324 */     setACL(acl, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeACL(final String name) throws MessagingException {
/* 2336 */     doOptionalCommand("ACL not supported", new ProtocolCommand() { private final String val$name;
/*      */           private final IMAPFolder this$0;
/*      */           
/*      */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/* 2340 */             p.deleteACL(IMAPFolder.this.fullName, name);
/* 2341 */             return null;
/*      */           } }
/*      */       );
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addRights(ACL acl) throws MessagingException {
/* 2356 */     setACL(acl, '+');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeRights(ACL acl) throws MessagingException {
/* 2368 */     setACL(acl, '-');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Rights[] listRights(final String name) throws MessagingException {
/* 2391 */     return (Rights[])doOptionalCommand("ACL not supported", new ProtocolCommand() { private final String val$name;
/*      */           private final IMAPFolder this$0;
/*      */           
/*      */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/* 2395 */             return p.listRights(IMAPFolder.this.fullName, name);
/*      */           } }
/*      */       );
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Rights myRights() throws MessagingException {
/* 2408 */     return (Rights)doOptionalCommand("ACL not supported", new ProtocolCommand() {
/*      */           private final IMAPFolder this$0;
/*      */           
/*      */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/* 2412 */             return p.myRights(IMAPFolder.this.fullName);
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */   
/*      */   private void setACL(final ACL acl, final char mod) throws MessagingException {
/* 2419 */     doOptionalCommand("ACL not supported", new ProtocolCommand() { private final char val$mod; private final ACL val$acl;
/*      */           private final IMAPFolder this$0;
/*      */           
/*      */           public Object doCommand(IMAPProtocol p) throws ProtocolException {
/* 2423 */             p.setACL(IMAPFolder.this.fullName, mod, acl);
/* 2424 */             return null;
/*      */           } }
/*      */       );
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized String[] getAttributes() throws MessagingException {
/* 2436 */     checkExists();
/* 2437 */     if (this.attributes == null)
/* 2438 */       exists(); 
/* 2439 */     return (this.attributes == null) ? new String[0] : (String[])this.attributes.clone();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void idle() throws MessagingException {
/* 2467 */     idle(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void idle(boolean once) throws MessagingException {
/* 2484 */     assert !Thread.holdsLock(this);
/* 2485 */     synchronized (this) {
/* 2486 */       checkOpened();
/* 2487 */       Boolean started = (Boolean)doOptionalCommand("IDLE not supported", new ProtocolCommand() {
/*      */             private final IMAPFolder this$0;
/*      */             
/*      */             public Object doCommand(IMAPProtocol p) throws ProtocolException {
/* 2491 */               if (IMAPFolder.this.idleState == 0) {
/* 2492 */                 p.idleStart();
/* 2493 */                 IMAPFolder.this.idleState = 1;
/* 2494 */                 return Boolean.TRUE;
/*      */               } 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               try {
/* 2501 */                 IMAPFolder.this.messageCacheLock.wait();
/* 2502 */               } catch (InterruptedException ex) {}
/* 2503 */               return Boolean.FALSE;
/*      */             }
/*      */           });
/*      */       
/* 2507 */       if (!started.booleanValue()) {
/*      */         return;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 2527 */       Response r = this.protocol.readIdleResponse();
/*      */       try {
/* 2529 */         synchronized (this.messageCacheLock) {
/*      */           try {
/* 2531 */             if (r == null || this.protocol == null || !this.protocol.processIdleResponse(r)) {
/*      */               
/* 2533 */               this.idleState = 0;
/* 2534 */               this.messageCacheLock.notifyAll();
/*      */               break;
/*      */             } 
/* 2537 */           } catch (ProtocolException pex) {
/* 2538 */             this.idleState = 0;
/* 2539 */             this.messageCacheLock.notifyAll();
/* 2540 */             throw pex;
/*      */           } 
/* 2542 */           if (once && 
/* 2543 */             this.idleState == 1) {
/* 2544 */             this.protocol.idleAbort();
/* 2545 */             this.idleState = 2;
/*      */           }
/*      */         
/*      */         } 
/* 2549 */       } catch (ConnectionException cex) {
/*      */         
/* 2551 */         throwClosedException(cex);
/* 2552 */       } catch (ProtocolException pex) {
/* 2553 */         throw new MessagingException(pex.getMessage(), pex);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2562 */     int minidle = ((IMAPStore)this.store).getMinIdleTime();
/* 2563 */     if (minidle > 0) {
/*      */       try {
/* 2565 */         Thread.sleep(minidle);
/* 2566 */       } catch (InterruptedException ex) {}
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void waitIfIdle() throws ProtocolException {
/* 2576 */     assert Thread.holdsLock(this.messageCacheLock);
/* 2577 */     while (this.idleState != 0) {
/* 2578 */       if (this.idleState == 1) {
/* 2579 */         this.protocol.idleAbort();
/* 2580 */         this.idleState = 2;
/*      */       } 
/*      */       
/*      */       try {
/* 2584 */         this.messageCacheLock.wait();
/* 2585 */       } catch (InterruptedException ex) {}
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handleResponse(Response r) {
/* 2602 */     assert Thread.holdsLock(this.messageCacheLock);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2607 */     if (r.isOK() || r.isNO() || r.isBAD() || r.isBYE()) {
/* 2608 */       ((IMAPStore)this.store).handleResponseCode(r);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2614 */     if (r.isBYE()) {
/* 2615 */       if (this.opened)
/* 2616 */         cleanup(false);  return;
/*      */     } 
/* 2618 */     if (r.isOK())
/*      */       return; 
/* 2620 */     if (!r.isUnTagged()) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 2625 */     if (!(r instanceof IMAPResponse)) {
/*      */ 
/*      */       
/* 2628 */       this.logger.fine("UNEXPECTED RESPONSE : " + r.toString());
/*      */       
/*      */       return;
/*      */     } 
/* 2632 */     IMAPResponse ir = (IMAPResponse)r;
/*      */     
/* 2634 */     if (ir.keyEquals("EXISTS")) {
/* 2635 */       int exists = ir.getNumber();
/* 2636 */       if (exists <= this.realTotal) {
/*      */         return;
/*      */       }
/*      */       
/* 2640 */       int count = exists - this.realTotal;
/* 2641 */       Message[] msgs = new Message[count];
/*      */ 
/*      */       
/* 2644 */       this.messageCache.addMessages(count, this.realTotal + 1);
/* 2645 */       int oldtotal = this.total;
/* 2646 */       this.realTotal += count;
/* 2647 */       this.total += count;
/*      */ 
/*      */       
/* 2650 */       if (this.hasMessageCountListener) {
/* 2651 */         for (int i = 0; i < count; i++) {
/* 2652 */           msgs[i] = (Message)this.messageCache.getMessage(++oldtotal);
/*      */         }
/*      */         
/* 2655 */         notifyMessageAddedListeners(msgs);
/*      */       }
/*      */     
/* 2658 */     } else if (ir.keyEquals("EXPUNGE")) {
/*      */ 
/*      */       
/* 2661 */       int seqnum = ir.getNumber();
/* 2662 */       Message[] msgs = null;
/* 2663 */       if (this.doExpungeNotification && this.hasMessageCountListener)
/*      */       {
/*      */         
/* 2666 */         msgs = new Message[] { (Message)getMessageBySeqNumber(seqnum) };
/*      */       }
/*      */       
/* 2669 */       this.messageCache.expungeMessage(seqnum);
/*      */ 
/*      */       
/* 2672 */       this.realTotal--;
/*      */       
/* 2674 */       if (msgs != null) {
/* 2675 */         notifyMessageRemovedListeners(false, msgs);
/*      */       }
/* 2677 */     } else if (ir.keyEquals("FETCH")) {
/*      */ 
/*      */       
/* 2680 */       assert ir instanceof FetchResponse : "!ir instanceof FetchResponse";
/* 2681 */       FetchResponse f = (FetchResponse)ir;
/*      */       
/* 2683 */       Flags flags = (Flags)f.getItem(Flags.class);
/*      */       
/* 2685 */       if (flags != null) {
/* 2686 */         IMAPMessage msg = getMessageBySeqNumber(f.getNumber());
/* 2687 */         if (msg != null) {
/* 2688 */           msg._setFlags(flags);
/* 2689 */           notifyMessageChangedListeners(1, (Message)msg);
/*      */         }
/*      */       
/*      */       }
/*      */     
/* 2694 */     } else if (ir.keyEquals("RECENT")) {
/*      */       
/* 2696 */       this.recent = ir.getNumber();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void handleResponses(Response[] r) {
/* 2707 */     for (int i = 0; i < r.length; i++) {
/* 2708 */       if (r[i] != null) {
/* 2709 */         handleResponse(r[i]);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized IMAPProtocol getStoreProtocol() throws ProtocolException {
/* 2731 */     this.connectionPoolLogger.fine("getStoreProtocol() borrowing a connection");
/* 2732 */     return ((IMAPStore)this.store).getFolderStoreProtocol();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized void throwClosedException(ConnectionException cex) throws FolderClosedException, StoreClosedException {
/* 2747 */     if ((this.protocol != null && cex.getProtocol() == this.protocol) || (this.protocol == null && !this.reallyClosed))
/*      */     {
/* 2749 */       throw new FolderClosedException(this, cex.getMessage());
/*      */     }
/* 2751 */     throw new StoreClosedException(this.store, cex.getMessage());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IMAPProtocol getProtocol() throws ProtocolException {
/* 2763 */     assert Thread.holdsLock(this.messageCacheLock);
/* 2764 */     waitIfIdle();
/* 2765 */     return this.protocol;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object doCommand(ProtocolCommand cmd) throws MessagingException {
/*      */     try {
/* 2868 */       return doProtocolCommand(cmd);
/* 2869 */     } catch (ConnectionException cex) {
/*      */       
/* 2871 */       throwClosedException(cex);
/* 2872 */     } catch (ProtocolException pex) {
/* 2873 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } 
/* 2875 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public Object doOptionalCommand(String err, ProtocolCommand cmd) throws MessagingException {
/*      */     try {
/* 2881 */       return doProtocolCommand(cmd);
/* 2882 */     } catch (BadCommandException bex) {
/* 2883 */       throw new MessagingException(err, bex);
/* 2884 */     } catch (ConnectionException cex) {
/*      */       
/* 2886 */       throwClosedException(cex);
/* 2887 */     } catch (ProtocolException pex) {
/* 2888 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } 
/* 2890 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public Object doCommandIgnoreFailure(ProtocolCommand cmd) throws MessagingException {
/*      */     try {
/* 2896 */       return doProtocolCommand(cmd);
/* 2897 */     } catch (CommandFailedException cfx) {
/* 2898 */       return null;
/* 2899 */     } catch (ConnectionException cex) {
/*      */       
/* 2901 */       throwClosedException(cex);
/* 2902 */     } catch (ProtocolException pex) {
/* 2903 */       throw new MessagingException(pex.getMessage(), pex);
/*      */     } 
/* 2905 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object doProtocolCommand(ProtocolCommand cmd) throws ProtocolException {
/* 2910 */     synchronized (this) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2916 */       if (this.protocol != null) {
/* 2917 */         synchronized (this.messageCacheLock) {
/* 2918 */           return cmd.doCommand(getProtocol());
/*      */         } 
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 2924 */     IMAPProtocol p = null;
/*      */     
/*      */     try {
/* 2927 */       p = getStoreProtocol();
/* 2928 */       return cmd.doCommand(p);
/*      */     } finally {
/* 2930 */       releaseStoreProtocol(p);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized void releaseStoreProtocol(IMAPProtocol p) {
/* 2940 */     if (p != this.protocol) {
/* 2941 */       ((IMAPStore)this.store).releaseFolderStoreProtocol(p);
/*      */     } else {
/*      */       
/* 2944 */       this.logger.fine("releasing our protocol as store protocol?");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void releaseProtocol(boolean returnToPool) {
/* 2955 */     if (this.protocol != null) {
/* 2956 */       this.protocol.removeResponseHandler(this);
/*      */       
/* 2958 */       if (returnToPool) {
/* 2959 */         ((IMAPStore)this.store).releaseProtocol(this, this.protocol);
/*      */       } else {
/* 2961 */         this.protocol.disconnect();
/* 2962 */         ((IMAPStore)this.store).releaseProtocol(this, null);
/*      */       } 
/* 2964 */       this.protocol = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void keepConnectionAlive(boolean keepStoreAlive) throws ProtocolException {
/* 2979 */     if (System.currentTimeMillis() - this.protocol.getTimestamp() > 1000L) {
/* 2980 */       waitIfIdle();
/* 2981 */       if (this.protocol != null) {
/* 2982 */         this.protocol.noop();
/*      */       }
/*      */     } 
/* 2985 */     if (keepStoreAlive && ((IMAPStore)this.store).hasSeparateStoreConnection()) {
/* 2986 */       IMAPProtocol p = null;
/*      */       try {
/* 2988 */         p = ((IMAPStore)this.store).getFolderStoreProtocol();
/* 2989 */         if (System.currentTimeMillis() - p.getTimestamp() > 1000L)
/* 2990 */           p.noop(); 
/*      */       } finally {
/* 2992 */         ((IMAPStore)this.store).releaseFolderStoreProtocol(p);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected IMAPMessage getMessageBySeqNumber(int seqnum) {
/* 3005 */     return this.messageCache.getMessageBySeqnum(seqnum);
/*      */   }
/*      */   
/*      */   private boolean isDirectory() {
/* 3009 */     return ((this.type & 0x2) != 0);
/*      */   }
/*      */   
/*      */   public static interface ProtocolCommand {
/*      */     Object doCommand(IMAPProtocol param1IMAPProtocol) throws ProtocolException;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\IMAPFolder.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */