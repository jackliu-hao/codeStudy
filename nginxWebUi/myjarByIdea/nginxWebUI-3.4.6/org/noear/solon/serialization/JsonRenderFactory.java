package org.noear.solon.serialization;

import org.noear.solon.core.handle.Render;

public interface JsonRenderFactory {
   <T> void addConvertor(Class<T> clz, JsonConverter<T> converter);

   Render create();
}
