package org.h2.mvstore.type;

import java.nio.ByteBuffer;
import java.util.Comparator;
import org.h2.mvstore.WriteBuffer;

public interface DataType<T> extends Comparator<T> {
  int compare(T paramT1, T paramT2);
  
  int binarySearch(T paramT, Object paramObject, int paramInt1, int paramInt2);
  
  int getMemory(T paramT);
  
  boolean isMemoryEstimationAllowed();
  
  void write(WriteBuffer paramWriteBuffer, T paramT);
  
  void write(WriteBuffer paramWriteBuffer, Object paramObject, int paramInt);
  
  T read(ByteBuffer paramByteBuffer);
  
  void read(ByteBuffer paramByteBuffer, Object paramObject, int paramInt);
  
  T[] createStorage(int paramInt);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\type\DataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */