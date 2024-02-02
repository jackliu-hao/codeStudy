package com.mysql.cj.xdevapi;

public class DatabaseObjectDescription {
   private String objectName;
   private DatabaseObject.DbObjectType objectType;

   public DatabaseObjectDescription(String name, String type) {
      this.objectName = name;
      this.objectType = DatabaseObject.DbObjectType.valueOf(type);
   }

   public String getObjectName() {
      return this.objectName;
   }

   public DatabaseObject.DbObjectType getObjectType() {
      return this.objectType;
   }
}
