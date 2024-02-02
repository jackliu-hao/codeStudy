package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;

public interface UnparseableExtraFieldBehavior {
  ZipExtraField onUnparseableExtraField(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3) throws ZipException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\UnparseableExtraFieldBehavior.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */