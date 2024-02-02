package org.apache.commons.compress.archivers;

public interface EntryStreamOffsets {
  public static final long OFFSET_UNKNOWN = -1L;
  
  long getDataOffset();
  
  boolean isStreamContiguous();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\EntryStreamOffsets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */