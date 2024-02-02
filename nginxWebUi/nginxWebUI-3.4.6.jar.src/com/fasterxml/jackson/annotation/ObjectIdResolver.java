package com.fasterxml.jackson.annotation;

public interface ObjectIdResolver {
  void bindItem(ObjectIdGenerator.IdKey paramIdKey, Object paramObject);
  
  Object resolveId(ObjectIdGenerator.IdKey paramIdKey);
  
  ObjectIdResolver newForDeserialization(Object paramObject);
  
  boolean canUseFor(ObjectIdResolver paramObjectIdResolver);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\fasterxml\jackson\annotation\ObjectIdResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */