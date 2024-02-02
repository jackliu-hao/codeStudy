package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;

public interface UnparseableExtraFieldBehavior {
   ZipExtraField onUnparseableExtraField(byte[] var1, int var2, int var3, boolean var4, int var5) throws ZipException;
}
