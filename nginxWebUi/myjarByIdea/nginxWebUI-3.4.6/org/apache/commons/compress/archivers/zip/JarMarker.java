package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;
import org.apache.commons.compress.utils.ByteUtils;

public final class JarMarker implements ZipExtraField {
   private static final ZipShort ID = new ZipShort(51966);
   private static final ZipShort NULL = new ZipShort(0);
   private static final JarMarker DEFAULT = new JarMarker();

   public static JarMarker getInstance() {
      return DEFAULT;
   }

   public ZipShort getHeaderId() {
      return ID;
   }

   public ZipShort getLocalFileDataLength() {
      return NULL;
   }

   public ZipShort getCentralDirectoryLength() {
      return NULL;
   }

   public byte[] getLocalFileDataData() {
      return ByteUtils.EMPTY_BYTE_ARRAY;
   }

   public byte[] getCentralDirectoryData() {
      return ByteUtils.EMPTY_BYTE_ARRAY;
   }

   public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
      if (length != 0) {
         throw new ZipException("JarMarker doesn't expect any data");
      }
   }

   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
      this.parseFromLocalFileData(buffer, offset, length);
   }
}
