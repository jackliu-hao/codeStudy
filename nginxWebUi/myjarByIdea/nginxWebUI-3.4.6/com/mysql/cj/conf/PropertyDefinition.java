package com.mysql.cj.conf;

import com.mysql.cj.exceptions.ExceptionInterceptor;

public interface PropertyDefinition<T> {
   boolean hasValueConstraints();

   boolean isRangeBased();

   PropertyKey getPropertyKey();

   String getName();

   String getCcAlias();

   boolean hasCcAlias();

   T getDefaultValue();

   boolean isRuntimeModifiable();

   String getDescription();

   String getSinceVersion();

   String getCategory();

   int getOrder();

   String[] getAllowableValues();

   int getLowerBound();

   int getUpperBound();

   T parseObject(String var1, ExceptionInterceptor var2);

   RuntimeProperty<T> createRuntimeProperty();
}
