package org.h2.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.h2.value.ValueBlob;
import org.h2.value.ValueClob;
import org.h2.value.ValueLob;

public interface LobStorageInterface {
   ValueClob createClob(Reader var1, long var2);

   ValueBlob createBlob(InputStream var1, long var2);

   ValueLob copyLob(ValueLob var1, int var2);

   InputStream getInputStream(long var1, long var3) throws IOException;

   InputStream getInputStream(long var1, int var3, long var4) throws IOException;

   void removeLob(ValueLob var1);

   void removeAllForTable(int var1);

   boolean isReadOnly();
}
