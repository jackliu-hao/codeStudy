package org.apache.commons.compress.archivers;

import java.util.Date;

public interface ArchiveEntry {
  public static final long SIZE_UNKNOWN = -1L;
  
  String getName();
  
  long getSize();
  
  boolean isDirectory();
  
  Date getLastModifiedDate();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\ArchiveEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */