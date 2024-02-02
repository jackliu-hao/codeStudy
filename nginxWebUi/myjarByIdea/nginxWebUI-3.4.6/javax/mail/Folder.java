package javax.mail;

import java.util.Vector;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import javax.mail.event.MailEvent;
import javax.mail.event.MessageChangedEvent;
import javax.mail.event.MessageChangedListener;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.search.SearchTerm;

public abstract class Folder {
   protected Store store;
   protected int mode = -1;
   public static final int HOLDS_MESSAGES = 1;
   public static final int HOLDS_FOLDERS = 2;
   public static final int READ_ONLY = 1;
   public static final int READ_WRITE = 2;
   private volatile Vector connectionListeners = null;
   private volatile Vector folderListeners = null;
   private volatile Vector messageCountListeners = null;
   private volatile Vector messageChangedListeners = null;
   private EventQueue q;
   private Object qLock = new Object();

   protected Folder(Store store) {
      this.store = store;
   }

   public abstract String getName();

   public abstract String getFullName();

   public URLName getURLName() throws MessagingException {
      URLName storeURL = this.getStore().getURLName();
      String fullname = this.getFullName();
      StringBuffer encodedName = new StringBuffer();
      if (fullname != null) {
         encodedName.append(fullname);
      }

      return new URLName(storeURL.getProtocol(), storeURL.getHost(), storeURL.getPort(), encodedName.toString(), storeURL.getUsername(), (String)null);
   }

   public Store getStore() {
      return this.store;
   }

   public abstract Folder getParent() throws MessagingException;

   public abstract boolean exists() throws MessagingException;

   public abstract Folder[] list(String var1) throws MessagingException;

   public Folder[] listSubscribed(String pattern) throws MessagingException {
      return this.list(pattern);
   }

   public Folder[] list() throws MessagingException {
      return this.list("%");
   }

   public Folder[] listSubscribed() throws MessagingException {
      return this.listSubscribed("%");
   }

   public abstract char getSeparator() throws MessagingException;

   public abstract int getType() throws MessagingException;

   public abstract boolean create(int var1) throws MessagingException;

   public boolean isSubscribed() {
      return true;
   }

   public void setSubscribed(boolean subscribe) throws MessagingException {
      throw new MethodNotSupportedException();
   }

   public abstract boolean hasNewMessages() throws MessagingException;

   public abstract Folder getFolder(String var1) throws MessagingException;

   public abstract boolean delete(boolean var1) throws MessagingException;

   public abstract boolean renameTo(Folder var1) throws MessagingException;

   public abstract void open(int var1) throws MessagingException;

   public abstract void close(boolean var1) throws MessagingException;

   public abstract boolean isOpen();

   public synchronized int getMode() {
      if (!this.isOpen()) {
         throw new IllegalStateException("Folder not open");
      } else {
         return this.mode;
      }
   }

   public abstract Flags getPermanentFlags();

   public abstract int getMessageCount() throws MessagingException;

   public synchronized int getNewMessageCount() throws MessagingException {
      if (!this.isOpen()) {
         return -1;
      } else {
         int newmsgs = 0;
         int total = this.getMessageCount();

         for(int i = 1; i <= total; ++i) {
            try {
               if (this.getMessage(i).isSet(Flags.Flag.RECENT)) {
                  ++newmsgs;
               }
            } catch (MessageRemovedException var5) {
            }
         }

         return newmsgs;
      }
   }

   public synchronized int getUnreadMessageCount() throws MessagingException {
      if (!this.isOpen()) {
         return -1;
      } else {
         int unread = 0;
         int total = this.getMessageCount();

         for(int i = 1; i <= total; ++i) {
            try {
               if (!this.getMessage(i).isSet(Flags.Flag.SEEN)) {
                  ++unread;
               }
            } catch (MessageRemovedException var5) {
            }
         }

         return unread;
      }
   }

   public synchronized int getDeletedMessageCount() throws MessagingException {
      if (!this.isOpen()) {
         return -1;
      } else {
         int deleted = 0;
         int total = this.getMessageCount();

         for(int i = 1; i <= total; ++i) {
            try {
               if (this.getMessage(i).isSet(Flags.Flag.DELETED)) {
                  ++deleted;
               }
            } catch (MessageRemovedException var5) {
            }
         }

         return deleted;
      }
   }

   public abstract Message getMessage(int var1) throws MessagingException;

   public synchronized Message[] getMessages(int start, int end) throws MessagingException {
      Message[] msgs = new Message[end - start + 1];

      for(int i = start; i <= end; ++i) {
         msgs[i - start] = this.getMessage(i);
      }

      return msgs;
   }

   public synchronized Message[] getMessages(int[] msgnums) throws MessagingException {
      int len = msgnums.length;
      Message[] msgs = new Message[len];

      for(int i = 0; i < len; ++i) {
         msgs[i] = this.getMessage(msgnums[i]);
      }

      return msgs;
   }

   public synchronized Message[] getMessages() throws MessagingException {
      if (!this.isOpen()) {
         throw new IllegalStateException("Folder not open");
      } else {
         int total = this.getMessageCount();
         Message[] msgs = new Message[total];

         for(int i = 1; i <= total; ++i) {
            msgs[i - 1] = this.getMessage(i);
         }

         return msgs;
      }
   }

   public abstract void appendMessages(Message[] var1) throws MessagingException;

   public void fetch(Message[] msgs, FetchProfile fp) throws MessagingException {
   }

   public synchronized void setFlags(Message[] msgs, Flags flag, boolean value) throws MessagingException {
      for(int i = 0; i < msgs.length; ++i) {
         try {
            msgs[i].setFlags(flag, value);
         } catch (MessageRemovedException var6) {
         }
      }

   }

   public synchronized void setFlags(int start, int end, Flags flag, boolean value) throws MessagingException {
      for(int i = start; i <= end; ++i) {
         try {
            Message msg = this.getMessage(i);
            msg.setFlags(flag, value);
         } catch (MessageRemovedException var7) {
         }
      }

   }

   public synchronized void setFlags(int[] msgnums, Flags flag, boolean value) throws MessagingException {
      for(int i = 0; i < msgnums.length; ++i) {
         try {
            Message msg = this.getMessage(msgnums[i]);
            msg.setFlags(flag, value);
         } catch (MessageRemovedException var6) {
         }
      }

   }

   public void copyMessages(Message[] msgs, Folder folder) throws MessagingException {
      if (!folder.exists()) {
         throw new FolderNotFoundException(folder.getFullName() + " does not exist", folder);
      } else {
         folder.appendMessages(msgs);
      }
   }

   public abstract Message[] expunge() throws MessagingException;

   public Message[] search(SearchTerm term) throws MessagingException {
      return this.search(term, this.getMessages());
   }

   public Message[] search(SearchTerm term, Message[] msgs) throws MessagingException {
      Vector matchedMsgs = new Vector();

      for(int i = 0; i < msgs.length; ++i) {
         try {
            if (msgs[i].match(term)) {
               matchedMsgs.addElement(msgs[i]);
            }
         } catch (MessageRemovedException var6) {
         }
      }

      Message[] m = new Message[matchedMsgs.size()];
      matchedMsgs.copyInto(m);
      return m;
   }

   public synchronized void addConnectionListener(ConnectionListener l) {
      if (this.connectionListeners == null) {
         this.connectionListeners = new Vector();
      }

      this.connectionListeners.addElement(l);
   }

   public synchronized void removeConnectionListener(ConnectionListener l) {
      if (this.connectionListeners != null) {
         this.connectionListeners.removeElement(l);
      }

   }

   protected void notifyConnectionListeners(int type) {
      if (this.connectionListeners != null) {
         ConnectionEvent e = new ConnectionEvent(this, type);
         this.queueEvent(e, this.connectionListeners);
      }

      if (type == 3) {
         this.terminateQueue();
      }

   }

   public synchronized void addFolderListener(FolderListener l) {
      if (this.folderListeners == null) {
         this.folderListeners = new Vector();
      }

      this.folderListeners.addElement(l);
   }

   public synchronized void removeFolderListener(FolderListener l) {
      if (this.folderListeners != null) {
         this.folderListeners.removeElement(l);
      }

   }

   protected void notifyFolderListeners(int type) {
      if (this.folderListeners != null) {
         FolderEvent e = new FolderEvent(this, this, type);
         this.queueEvent(e, this.folderListeners);
      }

      this.store.notifyFolderListeners(type, this);
   }

   protected void notifyFolderRenamedListeners(Folder folder) {
      if (this.folderListeners != null) {
         FolderEvent e = new FolderEvent(this, this, folder, 3);
         this.queueEvent(e, this.folderListeners);
      }

      this.store.notifyFolderRenamedListeners(this, folder);
   }

   public synchronized void addMessageCountListener(MessageCountListener l) {
      if (this.messageCountListeners == null) {
         this.messageCountListeners = new Vector();
      }

      this.messageCountListeners.addElement(l);
   }

   public synchronized void removeMessageCountListener(MessageCountListener l) {
      if (this.messageCountListeners != null) {
         this.messageCountListeners.removeElement(l);
      }

   }

   protected void notifyMessageAddedListeners(Message[] msgs) {
      if (this.messageCountListeners != null) {
         MessageCountEvent e = new MessageCountEvent(this, 1, false, msgs);
         this.queueEvent(e, this.messageCountListeners);
      }
   }

   protected void notifyMessageRemovedListeners(boolean removed, Message[] msgs) {
      if (this.messageCountListeners != null) {
         MessageCountEvent e = new MessageCountEvent(this, 2, removed, msgs);
         this.queueEvent(e, this.messageCountListeners);
      }
   }

   public synchronized void addMessageChangedListener(MessageChangedListener l) {
      if (this.messageChangedListeners == null) {
         this.messageChangedListeners = new Vector();
      }

      this.messageChangedListeners.addElement(l);
   }

   public synchronized void removeMessageChangedListener(MessageChangedListener l) {
      if (this.messageChangedListeners != null) {
         this.messageChangedListeners.removeElement(l);
      }

   }

   protected void notifyMessageChangedListeners(int type, Message msg) {
      if (this.messageChangedListeners != null) {
         MessageChangedEvent e = new MessageChangedEvent(this, type, msg);
         this.queueEvent(e, this.messageChangedListeners);
      }
   }

   private void queueEvent(MailEvent event, Vector vector) {
      synchronized(this.qLock) {
         if (this.q == null) {
            this.q = new EventQueue();
         }
      }

      Vector v = (Vector)vector.clone();
      this.q.enqueue(event, v);
   }

   private void terminateQueue() {
      synchronized(this.qLock) {
         if (this.q != null) {
            Vector dummyListeners = new Vector();
            dummyListeners.setSize(1);
            this.q.enqueue(new TerminatorEvent(), dummyListeners);
            this.q = null;
         }

      }
   }

   protected void finalize() throws Throwable {
      super.finalize();
      this.terminateQueue();
   }

   public String toString() {
      String s = this.getFullName();
      return s != null ? s : super.toString();
   }

   static class TerminatorEvent extends MailEvent {
      private static final long serialVersionUID = 3765761925441296565L;

      TerminatorEvent() {
         super(new Object());
      }

      public void dispatch(Object listener) {
         Thread.currentThread().interrupt();
      }
   }
}
