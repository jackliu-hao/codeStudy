package org.noear.solon.serialization;

import java.io.IOException;

@FunctionalInterface
public interface StringSerializer<T> {
  String serialize(T paramT) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\serialization\StringSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */