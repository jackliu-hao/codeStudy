package org.apache.commons.compress.archivers;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public interface ArchiveStreamProvider {
  ArchiveInputStream createArchiveInputStream(String paramString1, InputStream paramInputStream, String paramString2) throws ArchiveException;
  
  ArchiveOutputStream createArchiveOutputStream(String paramString1, OutputStream paramOutputStream, String paramString2) throws ArchiveException;
  
  Set<String> getInputStreamArchiveNames();
  
  Set<String> getOutputStreamArchiveNames();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\ArchiveStreamProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */