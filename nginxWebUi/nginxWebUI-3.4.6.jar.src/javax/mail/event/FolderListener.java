package javax.mail.event;

import java.util.EventListener;

public interface FolderListener extends EventListener {
  void folderCreated(FolderEvent paramFolderEvent);
  
  void folderDeleted(FolderEvent paramFolderEvent);
  
  void folderRenamed(FolderEvent paramFolderEvent);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\event\FolderListener.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */