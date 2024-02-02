package org.h2.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.h2.engine.SessionRemote;
import org.h2.value.ValueBlob;
import org.h2.value.ValueClob;
import org.h2.value.ValueLob;

public class LobStorageFrontend implements LobStorageInterface {
   public static final int TABLE_ID_SESSION_VARIABLE = -1;
   public static final int TABLE_TEMP = -2;
   public static final int TABLE_RESULT = -3;
   private final SessionRemote sessionRemote;

   public LobStorageFrontend(SessionRemote var1) {
      this.sessionRemote = var1;
   }

   public void removeLob(ValueLob var1) {
   }

   public InputStream getInputStream(long var1, long var3) throws IOException {
      throw new IllegalStateException();
   }

   public InputStream getInputStream(long var1, int var3, long var4) throws IOException {
      throw new IllegalStateException();
   }

   public boolean isReadOnly() {
      return false;
   }

   public ValueLob copyLob(ValueLob var1, int var2) {
      throw new UnsupportedOperationException();
   }

   public void removeAllForTable(int var1) {
      throw new UnsupportedOperationException();
   }

   public ValueBlob createBlob(InputStream var1, long var2) {
      return ValueBlob.createTempBlob(var1, var2, this.sessionRemote);
   }

   public ValueClob createClob(Reader var1, long var2) {
      return ValueClob.createTempClob(var1, var2, this.sessionRemote);
   }
}
