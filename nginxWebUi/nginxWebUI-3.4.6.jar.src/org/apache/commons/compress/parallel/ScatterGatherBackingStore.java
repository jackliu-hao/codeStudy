package org.apache.commons.compress.parallel;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public interface ScatterGatherBackingStore extends Closeable {
  InputStream getInputStream() throws IOException;
  
  void writeOut(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  void closeForWriting() throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\parallel\ScatterGatherBackingStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */