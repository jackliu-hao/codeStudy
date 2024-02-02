package org.h2.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.h2.value.ValueBlob;
import org.h2.value.ValueClob;
import org.h2.value.ValueLob;

public interface LobStorageInterface {
  ValueClob createClob(Reader paramReader, long paramLong);
  
  ValueBlob createBlob(InputStream paramInputStream, long paramLong);
  
  ValueLob copyLob(ValueLob paramValueLob, int paramInt);
  
  InputStream getInputStream(long paramLong1, long paramLong2) throws IOException;
  
  InputStream getInputStream(long paramLong1, int paramInt, long paramLong2) throws IOException;
  
  void removeLob(ValueLob paramValueLob);
  
  void removeAllForTable(int paramInt);
  
  boolean isReadOnly();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\LobStorageInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */