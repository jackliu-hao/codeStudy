package org.apache.commons.compress.compressors;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public interface CompressorStreamProvider {
  CompressorInputStream createCompressorInputStream(String paramString, InputStream paramInputStream, boolean paramBoolean) throws CompressorException;
  
  CompressorOutputStream createCompressorOutputStream(String paramString, OutputStream paramOutputStream) throws CompressorException;
  
  Set<String> getInputStreamCompressorNames();
  
  Set<String> getOutputStreamCompressorNames();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\CompressorStreamProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */