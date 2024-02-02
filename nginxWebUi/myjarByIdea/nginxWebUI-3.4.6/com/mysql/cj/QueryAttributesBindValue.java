package com.mysql.cj;

public interface QueryAttributesBindValue {
   boolean isNull();

   String getName();

   int getType();

   Object getValue();

   long getBoundLength();
}
