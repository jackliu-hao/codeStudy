package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;

public interface ZipExtraField {
   int EXTRAFIELD_HEADER_SIZE = 4;

   ZipShort getHeaderId();

   ZipShort getLocalFileDataLength();

   ZipShort getCentralDirectoryLength();

   byte[] getLocalFileDataData();

   byte[] getCentralDirectoryData();

   void parseFromLocalFileData(byte[] var1, int var2, int var3) throws ZipException;

   void parseFromCentralDirectoryData(byte[] var1, int var2, int var3) throws ZipException;
}
