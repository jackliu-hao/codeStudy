package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;

public interface ZipExtraField {
  public static final int EXTRAFIELD_HEADER_SIZE = 4;
  
  ZipShort getHeaderId();
  
  ZipShort getLocalFileDataLength();
  
  ZipShort getCentralDirectoryLength();
  
  byte[] getLocalFileDataData();
  
  byte[] getCentralDirectoryData();
  
  void parseFromLocalFileData(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws ZipException;
  
  void parseFromCentralDirectoryData(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws ZipException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ZipExtraField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */