package org.noear.snack.core;

import java.lang.reflect.Type;
import org.noear.snack.ONode;

public interface NodeDecoder<T> {
  T decode(ONode paramONode, Type paramType);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\NodeDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */