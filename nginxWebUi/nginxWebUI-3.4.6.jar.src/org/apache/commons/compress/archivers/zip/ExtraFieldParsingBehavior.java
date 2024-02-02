package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;

public interface ExtraFieldParsingBehavior extends UnparseableExtraFieldBehavior {
  ZipExtraField createExtraField(ZipShort paramZipShort) throws ZipException, InstantiationException, IllegalAccessException;
  
  ZipExtraField fill(ZipExtraField paramZipExtraField, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, boolean paramBoolean) throws ZipException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ExtraFieldParsingBehavior.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */