package com.mysql.cj.protocol;

public interface ResultBuilder<T> {
   boolean addProtocolEntity(ProtocolEntity var1);

   T build();
}
