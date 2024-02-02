package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;

public interface ExtraFieldParsingBehavior extends UnparseableExtraFieldBehavior {
   ZipExtraField createExtraField(ZipShort var1) throws ZipException, InstantiationException, IllegalAccessException;

   ZipExtraField fill(ZipExtraField var1, byte[] var2, int var3, int var4, boolean var5) throws ZipException;
}
