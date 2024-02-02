package org.noear.snack.core;

import java.lang.reflect.Type;
import org.noear.snack.ONode;

public interface NodeDecoder<T> {
   T decode(ONode node, Type type);
}
