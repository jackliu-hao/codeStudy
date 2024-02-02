package org.noear.snack.core;

import org.noear.snack.ONode;

public interface NodeEncoder<T> {
   void encode(T data, ONode node);
}
