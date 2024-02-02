package org.apache.commons.compress.compressors;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public interface CompressorStreamProvider {
   CompressorInputStream createCompressorInputStream(String var1, InputStream var2, boolean var3) throws CompressorException;

   CompressorOutputStream createCompressorOutputStream(String var1, OutputStream var2) throws CompressorException;

   Set<String> getInputStreamCompressorNames();

   Set<String> getOutputStreamCompressorNames();
}
