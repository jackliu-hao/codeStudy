package org.apache.commons.compress.parallel;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public interface ScatterGatherBackingStore extends Closeable {
   InputStream getInputStream() throws IOException;

   void writeOut(byte[] var1, int var2, int var3) throws IOException;

   void closeForWriting() throws IOException;
}
