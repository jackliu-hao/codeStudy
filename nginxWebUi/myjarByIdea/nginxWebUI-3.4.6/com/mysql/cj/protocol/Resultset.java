package com.mysql.cj.protocol;

public interface Resultset extends ProtocolEntity {
   void setColumnDefinition(ColumnDefinition var1);

   ColumnDefinition getColumnDefinition();

   boolean hasRows();

   ResultsetRows getRows();

   void initRowsWithMetadata();

   int getResultId();

   void setNextResultset(Resultset var1);

   Resultset getNextResultset();

   void clearNextResultset();

   long getUpdateCount();

   long getUpdateID();

   String getServerInfo();

   public static enum Type {
      FORWARD_ONLY(1003),
      SCROLL_INSENSITIVE(1004),
      SCROLL_SENSITIVE(1005);

      private int value;

      private Type(int jdbcRsType) {
         this.value = jdbcRsType;
      }

      public int getIntValue() {
         return this.value;
      }

      public static Type fromValue(int rsType, Type backupValue) {
         Type[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Type t = var2[var4];
            if (t.getIntValue() == rsType) {
               return t;
            }
         }

         return backupValue;
      }
   }

   public static enum Concurrency {
      READ_ONLY(1007),
      UPDATABLE(1008);

      private int value;

      private Concurrency(int jdbcRsConcur) {
         this.value = jdbcRsConcur;
      }

      public int getIntValue() {
         return this.value;
      }

      public static Concurrency fromValue(int concurMode, Concurrency backupValue) {
         Concurrency[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Concurrency c = var2[var4];
            if (c.getIntValue() == concurMode) {
               return c;
            }
         }

         return backupValue;
      }
   }
}
