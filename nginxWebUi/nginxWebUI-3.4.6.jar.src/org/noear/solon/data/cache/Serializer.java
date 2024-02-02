package org.noear.solon.data.cache;

public interface Serializer<T> {
  String name();
  
  T serialize(Object paramObject) throws Exception;
  
  Object deserialize(T paramT) throws Exception;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\cache\Serializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */