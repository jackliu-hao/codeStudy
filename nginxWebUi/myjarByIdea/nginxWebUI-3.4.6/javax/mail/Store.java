package javax.mail;

import java.util.Vector;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import javax.mail.event.StoreEvent;
import javax.mail.event.StoreListener;

public abstract class Store extends Service {
   private volatile Vector storeListeners = null;
   private volatile Vector folderListeners = null;

   protected Store(Session session, URLName urlname) {
      super(session, urlname);
   }

   public abstract Folder getDefaultFolder() throws MessagingException;

   public abstract Folder getFolder(String var1) throws MessagingException;

   public abstract Folder getFolder(URLName var1) throws MessagingException;

   public Folder[] getPersonalNamespaces() throws MessagingException {
      return new Folder[]{this.getDefaultFolder()};
   }

   public Folder[] getUserNamespaces(String user) throws MessagingException {
      return new Folder[0];
   }

   public Folder[] getSharedNamespaces() throws MessagingException {
      return new Folder[0];
   }

   public synchronized void addStoreListener(StoreListener l) {
      if (this.storeListeners == null) {
         this.storeListeners = new Vector();
      }

      this.storeListeners.addElement(l);
   }

   public synchronized void removeStoreListener(StoreListener l) {
      if (this.storeListeners != null) {
         this.storeListeners.removeElement(l);
      }

   }

   protected void notifyStoreListeners(int type, String message) {
      if (this.storeListeners != null) {
         StoreEvent e = new StoreEvent(this, type, message);
         this.queueEvent(e, this.storeListeners);
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

   protected void notifyFolderListeners(int type, Folder folder) {
      if (this.folderListeners != null) {
         FolderEvent e = new FolderEvent(this, folder, type);
         this.queueEvent(e, this.folderListeners);
      }
   }

   protected void notifyFolderRenamedListeners(Folder oldF, Folder newF) {
      if (this.folderListeners != null) {
         FolderEvent e = new FolderEvent(this, oldF, newF, 3);
         this.queueEvent(e, this.folderListeners);
      }
   }
}
