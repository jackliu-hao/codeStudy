package org.h2.store;

import java.io.IOException;
import java.io.InputStream;
import org.h2.engine.SessionRemote;
import org.h2.message.DbException;
import org.h2.mvstore.DataUtils;

public class LobStorageRemoteInputStream extends InputStream {
   private final SessionRemote sessionRemote;
   private final long lobId;
   private final byte[] hmac;
   private long pos;

   public LobStorageRemoteInputStream(SessionRemote var1, long var2, byte[] var4) {
      this.sessionRemote = var1;
      this.lobId = var2;
      this.hmac = var4;
   }

   public int read() throws IOException {
      byte[] var1 = new byte[1];
      int var2 = this.read(var1, 0, 1);
      return var2 < 0 ? var2 : var1[0] & 255;
   }

   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      assert var3 >= 0;

      if (var3 == 0) {
         return 0;
      } else {
         try {
            var3 = this.sessionRemote.readLob(this.lobId, this.hmac, this.pos, var1, var2, var3);
         } catch (DbException var5) {
            throw DataUtils.convertToIOException(var5);
         }

         if (var3 == 0) {
            return -1;
         } else {
            this.pos += (long)var3;
            return var3;
         }
      }
   }
}
