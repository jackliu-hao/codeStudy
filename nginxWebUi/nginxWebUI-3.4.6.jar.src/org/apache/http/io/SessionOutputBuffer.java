package org.apache.http.io;

import java.io.IOException;
import org.apache.http.util.CharArrayBuffer;

public interface SessionOutputBuffer {
  void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  void write(byte[] paramArrayOfbyte) throws IOException;
  
  void write(int paramInt) throws IOException;
  
  void writeLine(String paramString) throws IOException;
  
  void writeLine(CharArrayBuffer paramCharArrayBuffer) throws IOException;
  
  void flush() throws IOException;
  
  HttpTransportMetrics getMetrics();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\io\SessionOutputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */