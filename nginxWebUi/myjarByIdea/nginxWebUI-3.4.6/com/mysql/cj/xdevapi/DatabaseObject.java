package com.mysql.cj.xdevapi;

public interface DatabaseObject {
   Session getSession();

   Schema getSchema();

   String getName();

   DbObjectStatus existsInDatabase();

   public static enum DbObjectStatus {
      EXISTS,
      NOT_EXISTS,
      UNKNOWN;
   }

   public static enum DbObjectType {
      COLLECTION,
      TABLE,
      VIEW,
      COLLECTION_VIEW;
   }
}
