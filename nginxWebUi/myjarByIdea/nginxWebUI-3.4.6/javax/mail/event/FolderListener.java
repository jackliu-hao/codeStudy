package javax.mail.event;

import java.util.EventListener;

public interface FolderListener extends EventListener {
   void folderCreated(FolderEvent var1);

   void folderDeleted(FolderEvent var1);

   void folderRenamed(FolderEvent var1);
}
