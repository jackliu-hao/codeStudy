package org.apache.commons.compress.archivers;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public interface ArchiveStreamProvider {
   ArchiveInputStream createArchiveInputStream(String var1, InputStream var2, String var3) throws ArchiveException;

   ArchiveOutputStream createArchiveOutputStream(String var1, OutputStream var2, String var3) throws ArchiveException;

   Set<String> getInputStreamArchiveNames();

   Set<String> getOutputStreamArchiveNames();
}
