package com.mysql.cj.xdevapi;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TableFilterParams extends AbstractFilterParams {
   public TableFilterParams(String schemaName, String collectionName) {
      this(schemaName, collectionName, true);
   }

   public TableFilterParams(String schemaName, String collectionName, boolean supportsOffset) {
      super(schemaName, collectionName, supportsOffset, true);
   }

   public void setFields(String... projection) {
      this.projection = projection;
      this.fields = (new ExprParser((String)Arrays.stream(projection).collect(Collectors.joining(", ")), true)).parseTableSelectProjection();
   }
}
