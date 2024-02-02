package javax.mail.event;

public abstract class FolderAdapter implements FolderListener {
  public void folderCreated(FolderEvent e) {}
  
  public void folderRenamed(FolderEvent e) {}
  
  public void folderDeleted(FolderEvent e) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\event\FolderAdapter.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */