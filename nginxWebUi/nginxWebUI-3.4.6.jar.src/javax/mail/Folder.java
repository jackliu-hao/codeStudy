/*      */ package javax.mail;
/*      */ 
/*      */ import java.util.Vector;
/*      */ import javax.mail.event.ConnectionEvent;
/*      */ import javax.mail.event.ConnectionListener;
/*      */ import javax.mail.event.FolderEvent;
/*      */ import javax.mail.event.FolderListener;
/*      */ import javax.mail.event.MailEvent;
/*      */ import javax.mail.event.MessageChangedEvent;
/*      */ import javax.mail.event.MessageChangedListener;
/*      */ import javax.mail.event.MessageCountEvent;
/*      */ import javax.mail.event.MessageCountListener;
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
/*      */ public abstract class Folder
/*      */ {
/*      */   protected Store store;
/*  130 */   protected int mode = -1;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int HOLDS_MESSAGES = 1;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int HOLDS_FOLDERS = 2;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int READ_ONLY = 1;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int READ_WRITE = 2;
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile Vector connectionListeners;
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile Vector folderListeners;
/*      */ 
/*      */   
/*      */   private volatile Vector messageCountListeners;
/*      */ 
/*      */   
/*      */   private volatile Vector messageChangedListeners;
/*      */ 
/*      */   
/*      */   private EventQueue q;
/*      */ 
/*      */   
/*      */   private Object qLock;
/*      */ 
/*      */ 
/*      */   
/*      */   public URLName getURLName() throws MessagingException {
/*  171 */     URLName storeURL = getStore().getURLName();
/*  172 */     String fullname = getFullName();
/*  173 */     StringBuffer encodedName = new StringBuffer();
/*      */     
/*  175 */     if (fullname != null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  193 */       encodedName.append(fullname);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  200 */     return new URLName(storeURL.getProtocol(), storeURL.getHost(), storeURL.getPort(), encodedName.toString(), storeURL.getUsername(), null);
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
/*      */   public Store getStore() {
/*  212 */     return this.store;
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
/*      */   public Folder[] listSubscribed(String pattern) throws MessagingException {
/*  308 */     return list(pattern);
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
/*      */   public Folder[] list() throws MessagingException {
/*  326 */     return list("%");
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
/*      */   public Folder[] listSubscribed() throws MessagingException {
/*  344 */     return listSubscribed("%");
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
/*      */   public boolean isSubscribed() {
/*  409 */     return true;
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
/*      */   public void setSubscribed(boolean subscribe) throws MessagingException {
/*  430 */     throw new MethodNotSupportedException();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized int getMode() {
/*  641 */     if (!isOpen())
/*  642 */       throw new IllegalStateException("Folder not open"); 
/*  643 */     return this.mode;
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
/*      */   public synchronized int getNewMessageCount() throws MessagingException {
/*  712 */     if (!isOpen()) {
/*  713 */       return -1;
/*      */     }
/*  715 */     int newmsgs = 0;
/*  716 */     int total = getMessageCount();
/*  717 */     for (int i = 1; i <= total; i++) {
/*      */       try {
/*  719 */         if (getMessage(i).isSet(Flags.Flag.RECENT))
/*  720 */           newmsgs++; 
/*  721 */       } catch (MessageRemovedException me) {}
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  726 */     return newmsgs;
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
/*      */   public synchronized int getUnreadMessageCount() throws MessagingException {
/*  758 */     if (!isOpen()) {
/*  759 */       return -1;
/*      */     }
/*  761 */     int unread = 0;
/*  762 */     int total = getMessageCount();
/*  763 */     for (int i = 1; i <= total; i++) {
/*      */       try {
/*  765 */         if (!getMessage(i).isSet(Flags.Flag.SEEN))
/*  766 */           unread++; 
/*  767 */       } catch (MessageRemovedException me) {}
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  772 */     return unread;
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
/*      */   public synchronized int getDeletedMessageCount() throws MessagingException {
/*  804 */     if (!isOpen()) {
/*  805 */       return -1;
/*      */     }
/*  807 */     int deleted = 0;
/*  808 */     int total = getMessageCount();
/*  809 */     for (int i = 1; i <= total; i++) {
/*      */       try {
/*  811 */         if (getMessage(i).isSet(Flags.Flag.DELETED))
/*  812 */           deleted++; 
/*  813 */       } catch (MessageRemovedException me) {}
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  818 */     return deleted;
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
/*      */   public synchronized Message[] getMessages(int start, int end) throws MessagingException {
/*  882 */     Message[] msgs = new Message[end - start + 1];
/*  883 */     for (int i = start; i <= end; i++)
/*  884 */       msgs[i - start] = getMessage(i); 
/*  885 */     return msgs;
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
/*      */   public synchronized Message[] getMessages(int[] msgnums) throws MessagingException {
/*  912 */     int len = msgnums.length;
/*  913 */     Message[] msgs = new Message[len];
/*  914 */     for (int i = 0; i < len; i++)
/*  915 */       msgs[i] = getMessage(msgnums[i]); 
/*  916 */     return msgs;
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
/*      */   public synchronized Message[] getMessages() throws MessagingException {
/*  942 */     if (!isOpen())
/*  943 */       throw new IllegalStateException("Folder not open"); 
/*  944 */     int total = getMessageCount();
/*  945 */     Message[] msgs = new Message[total];
/*  946 */     for (int i = 1; i <= total; i++)
/*  947 */       msgs[i - 1] = getMessage(i); 
/*  948 */     return msgs;
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
/*      */   public void fetch(Message[] msgs, FetchProfile fp) throws MessagingException {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setFlags(Message[] msgs, Flags flag, boolean value) throws MessagingException {
/* 1042 */     for (int i = 0; i < msgs.length; i++) {
/*      */       try {
/* 1044 */         msgs[i].setFlags(flag, value);
/* 1045 */       } catch (MessageRemovedException me) {}
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setFlags(int start, int end, Flags flag, boolean value) throws MessagingException {
/* 1085 */     for (int i = start; i <= end; i++) {
/*      */       try {
/* 1087 */         Message msg = getMessage(i);
/* 1088 */         msg.setFlags(flag, value);
/* 1089 */       } catch (MessageRemovedException me) {}
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setFlags(int[] msgnums, Flags flag, boolean value) throws MessagingException {
/* 1127 */     for (int i = 0; i < msgnums.length; i++) {
/*      */       try {
/* 1129 */         Message msg = getMessage(msgnums[i]);
/* 1130 */         msg.setFlags(flag, value);
/* 1131 */       } catch (MessageRemovedException me) {}
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void copyMessages(Message[] msgs, Folder folder) throws MessagingException {
/* 1167 */     if (!folder.exists()) {
/* 1168 */       throw new FolderNotFoundException(folder.getFullName() + " does not exist", folder);
/*      */     }
/*      */ 
/*      */     
/* 1172 */     folder.appendMessages(msgs);
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
/*      */   public Message[] search(SearchTerm term) throws MessagingException {
/* 1231 */     return search(term, getMessages());
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
/*      */   public Message[] search(SearchTerm term, Message[] msgs) throws MessagingException {
/* 1265 */     Vector matchedMsgs = new Vector();
/*      */ 
/*      */     
/* 1268 */     for (int i = 0; i < msgs.length; i++) {
/*      */       try {
/* 1270 */         if (msgs[i].match(term))
/* 1271 */           matchedMsgs.addElement(msgs[i]); 
/* 1272 */       } catch (MessageRemovedException mrex) {}
/*      */     } 
/*      */     
/* 1275 */     Message[] m = new Message[matchedMsgs.size()];
/* 1276 */     matchedMsgs.copyInto((Object[])m);
/* 1277 */     return m;
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
/*      */   protected Folder(Store store) {
/* 1302 */     this.connectionListeners = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1369 */     this.folderListeners = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1450 */     this.messageCountListeners = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1533 */     this.messageChangedListeners = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1595 */     this.qLock = new Object(); this.store = store;
/*      */   }
/*      */   public synchronized void addConnectionListener(ConnectionListener l) { if (this.connectionListeners == null) this.connectionListeners = new Vector();  this.connectionListeners.addElement(l); }
/*      */   public synchronized void removeConnectionListener(ConnectionListener l) { if (this.connectionListeners != null) this.connectionListeners.removeElement(l);  }
/*      */   protected void notifyConnectionListeners(int type) { if (this.connectionListeners != null) { ConnectionEvent e = new ConnectionEvent(this, type); queueEvent((MailEvent)e, this.connectionListeners); }  if (type == 3) terminateQueue();  }
/*      */   public synchronized void addFolderListener(FolderListener l) { if (this.folderListeners == null) this.folderListeners = new Vector();  this.folderListeners.addElement(l); }
/*      */   public synchronized void removeFolderListener(FolderListener l) { if (this.folderListeners != null) this.folderListeners.removeElement(l);  }
/* 1602 */   protected void notifyFolderListeners(int type) { if (this.folderListeners != null) { FolderEvent e = new FolderEvent(this, this, type); queueEvent((MailEvent)e, this.folderListeners); }  this.store.notifyFolderListeners(type, this); } protected void notifyFolderRenamedListeners(Folder folder) { if (this.folderListeners != null) { FolderEvent e = new FolderEvent(this, this, folder, 3); queueEvent((MailEvent)e, this.folderListeners); }  this.store.notifyFolderRenamedListeners(this, folder); } private void queueEvent(MailEvent event, Vector vector) { synchronized (this.qLock) {
/* 1603 */       if (this.q == null) {
/* 1604 */         this.q = new EventQueue();
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
/* 1615 */     Vector v = (Vector)vector.clone();
/* 1616 */     this.q.enqueue(event, v); }
/*      */   public synchronized void addMessageCountListener(MessageCountListener l) { if (this.messageCountListeners == null) this.messageCountListeners = new Vector();  this.messageCountListeners.addElement(l); }
/*      */   public synchronized void removeMessageCountListener(MessageCountListener l) { if (this.messageCountListeners != null) this.messageCountListeners.removeElement(l);  }
/*      */   protected void notifyMessageAddedListeners(Message[] msgs) { if (this.messageCountListeners == null) return;  MessageCountEvent e = new MessageCountEvent(this, 1, false, msgs); queueEvent((MailEvent)e, this.messageCountListeners); }
/*      */   protected void notifyMessageRemovedListeners(boolean removed, Message[] msgs) { if (this.messageCountListeners == null) return;  MessageCountEvent e = new MessageCountEvent(this, 2, removed, msgs); queueEvent((MailEvent)e, this.messageCountListeners); }
/*      */   public synchronized void addMessageChangedListener(MessageChangedListener l) { if (this.messageChangedListeners == null) this.messageChangedListeners = new Vector();  this.messageChangedListeners.addElement(l); } public synchronized void removeMessageChangedListener(MessageChangedListener l) { if (this.messageChangedListeners != null) this.messageChangedListeners.removeElement(l);  } protected void notifyMessageChangedListeners(int type, Message msg) { if (this.messageChangedListeners == null) return;  MessageChangedEvent e = new MessageChangedEvent(this, type, msg); queueEvent((MailEvent)e, this.messageChangedListeners); } static class TerminatorEvent extends MailEvent
/*      */   {
/* 1623 */     private static final long serialVersionUID = 3765761925441296565L; TerminatorEvent() { super(new Object()); }
/*      */ 
/*      */ 
/*      */     
/*      */     public void dispatch(Object listener) {
/* 1628 */       Thread.currentThread().interrupt();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void terminateQueue() {
/* 1634 */     synchronized (this.qLock) {
/* 1635 */       if (this.q != null) {
/* 1636 */         Vector dummyListeners = new Vector();
/* 1637 */         dummyListeners.setSize(1);
/* 1638 */         this.q.enqueue(new TerminatorEvent(), dummyListeners);
/* 1639 */         this.q = null;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   protected void finalize() throws Throwable {
/* 1645 */     super.finalize();
/* 1646 */     terminateQueue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1656 */     String s = getFullName();
/* 1657 */     if (s != null) {
/* 1658 */       return s;
/*      */     }
/* 1660 */     return super.toString();
/*      */   }
/*      */   
/*      */   public abstract String getName();
/*      */   
/*      */   public abstract String getFullName();
/*      */   
/*      */   public abstract Folder getParent() throws MessagingException;
/*      */   
/*      */   public abstract boolean exists() throws MessagingException;
/*      */   
/*      */   public abstract Folder[] list(String paramString) throws MessagingException;
/*      */   
/*      */   public abstract char getSeparator() throws MessagingException;
/*      */   
/*      */   public abstract int getType() throws MessagingException;
/*      */   
/*      */   public abstract boolean create(int paramInt) throws MessagingException;
/*      */   
/*      */   public abstract boolean hasNewMessages() throws MessagingException;
/*      */   
/*      */   public abstract Folder getFolder(String paramString) throws MessagingException;
/*      */   
/*      */   public abstract boolean delete(boolean paramBoolean) throws MessagingException;
/*      */   
/*      */   public abstract boolean renameTo(Folder paramFolder) throws MessagingException;
/*      */   
/*      */   public abstract void open(int paramInt) throws MessagingException;
/*      */   
/*      */   public abstract void close(boolean paramBoolean) throws MessagingException;
/*      */   
/*      */   public abstract boolean isOpen();
/*      */   
/*      */   public abstract Flags getPermanentFlags();
/*      */   
/*      */   public abstract int getMessageCount() throws MessagingException;
/*      */   
/*      */   public abstract Message getMessage(int paramInt) throws MessagingException;
/*      */   
/*      */   public abstract void appendMessages(Message[] paramArrayOfMessage) throws MessagingException;
/*      */   
/*      */   public abstract Message[] expunge() throws MessagingException;
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\Folder.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */