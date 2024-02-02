package org.noear.solon.serialization;

import org.noear.solon.core.handle.Render;

public interface JsonRenderFactory {
  <T> void addConvertor(Class<T> paramClass, JsonConverter<T> paramJsonConverter);
  
  Render create();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\serialization\JsonRenderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */