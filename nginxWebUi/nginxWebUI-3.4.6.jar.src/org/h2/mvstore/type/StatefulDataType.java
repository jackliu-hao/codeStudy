package org.h2.mvstore.type;

import java.nio.ByteBuffer;
import org.h2.mvstore.WriteBuffer;

public interface StatefulDataType<D> {
  void save(WriteBuffer paramWriteBuffer, MetaType<D> paramMetaType);
  
  Factory<D> getFactory();
  
  public static interface Factory<D> {
    DataType<?> create(ByteBuffer param1ByteBuffer, MetaType<D> param1MetaType, D param1D);
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\type\StatefulDataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */