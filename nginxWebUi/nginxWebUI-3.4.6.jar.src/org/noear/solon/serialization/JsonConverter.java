package org.noear.solon.serialization;

@FunctionalInterface
public interface JsonConverter<T> {
  Object convert(T paramT);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\serialization\JsonConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */