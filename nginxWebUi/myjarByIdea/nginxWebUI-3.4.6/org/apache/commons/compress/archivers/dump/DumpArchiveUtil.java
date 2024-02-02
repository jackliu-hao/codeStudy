package org.apache.commons.compress.archivers.dump;

import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.utils.ByteUtils;

class DumpArchiveUtil {
   private DumpArchiveUtil() {
   }

   public static int calculateChecksum(byte[] buffer) {
      int calc = 0;

      for(int i = 0; i < 256; ++i) {
         calc += convert32(buffer, 4 * i);
      }

      return 84446 - (calc - convert32(buffer, 28));
   }

   public static final boolean verify(byte[] buffer) {
      int magic = convert32(buffer, 24);
      if (magic != 60012) {
         return false;
      } else {
         int checksum = convert32(buffer, 28);
         return checksum == calculateChecksum(buffer);
      }
   }

   public static final int getIno(byte[] buffer) {
      return convert32(buffer, 20);
   }

   public static final long convert64(byte[] buffer, int offset) {
      return ByteUtils.fromLittleEndian(buffer, offset, 8);
   }

   public static final int convert32(byte[] buffer, int offset) {
      return (int)ByteUtils.fromLittleEndian(buffer, offset, 4);
   }

   public static final int convert16(byte[] buffer, int offset) {
      return (int)ByteUtils.fromLittleEndian(buffer, offset, 2);
   }

   static String decode(ZipEncoding encoding, byte[] b, int offset, int len) throws IOException {
      return encoding.decode(Arrays.copyOfRange(b, offset, offset + len));
   }
}
