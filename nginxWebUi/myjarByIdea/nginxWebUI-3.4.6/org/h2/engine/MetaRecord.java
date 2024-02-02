package org.h2.engine;

import java.sql.SQLException;
import java.util.Comparator;
import org.h2.api.DatabaseEventListener;
import org.h2.command.Prepared;
import org.h2.message.DbException;
import org.h2.result.SearchRow;
import org.h2.value.ValueInteger;
import org.h2.value.ValueVarchar;

public class MetaRecord implements Comparable<MetaRecord> {
   static final Comparator<Prepared> CONSTRAINTS_COMPARATOR = (var0, var1) -> {
      int var2 = var0.getType();
      int var3 = var1.getType();
      boolean var4 = var2 == 6 || var2 == 4;
      boolean var5 = var3 == 6 || var3 == 4;
      if (var4 == var5) {
         return var0.getPersistedObjectId() - var1.getPersistedObjectId();
      } else {
         return var4 ? -1 : 1;
      }
   };
   private final int id;
   private final int objectType;
   private final String sql;

   public static void populateRowFromDBObject(DbObject var0, SearchRow var1) {
      var1.setValue(0, ValueInteger.get(var0.getId()));
      var1.setValue(1, ValueInteger.get(0));
      var1.setValue(2, ValueInteger.get(var0.getType()));
      var1.setValue(3, ValueVarchar.get(var0.getCreateSQLForMeta()));
   }

   public MetaRecord(SearchRow var1) {
      this.id = var1.getValue(0).getInt();
      this.objectType = var1.getValue(2).getInt();
      this.sql = var1.getValue(3).getString();
   }

   void prepareAndExecute(Database var1, SessionLocal var2, DatabaseEventListener var3) {
      try {
         Prepared var4 = var2.prepare(this.sql);
         var4.setPersistedObjectId(this.id);
         var4.update();
      } catch (DbException var5) {
         throwException(var1, var3, var5, this.sql);
      }

   }

   Prepared prepare(Database var1, SessionLocal var2, DatabaseEventListener var3) {
      try {
         Prepared var4 = var2.prepare(this.sql);
         var4.setPersistedObjectId(this.id);
         return var4;
      } catch (DbException var5) {
         throwException(var1, var3, var5, this.sql);
         return null;
      }
   }

   static void execute(Database var0, Prepared var1, DatabaseEventListener var2, String var3) {
      try {
         var1.update();
      } catch (DbException var5) {
         throwException(var0, var2, var5, var3);
      }

   }

   private static void throwException(Database var0, DatabaseEventListener var1, DbException var2, String var3) {
      var2 = var2.addSQL(var3);
      SQLException var4 = var2.getSQLException();
      var0.getTrace(2).error(var4, var3);
      if (var1 != null) {
         var1.exceptionThrown(var4, var3);
      } else {
         throw var2;
      }
   }

   public int getId() {
      return this.id;
   }

   public int getObjectType() {
      return this.objectType;
   }

   public String getSQL() {
      return this.sql;
   }

   public int compareTo(MetaRecord var1) {
      int var2 = this.getCreateOrder();
      int var3 = var1.getCreateOrder();
      return var2 != var3 ? var2 - var3 : this.getId() - var1.getId();
   }

   private int getCreateOrder() {
      switch (this.objectType) {
         case 0:
            return 7;
         case 1:
            return 8;
         case 2:
            return 1;
         case 3:
            return 5;
         case 4:
            return 10;
         case 5:
            return 9;
         case 6:
            return 0;
         case 7:
            return 12;
         case 8:
            return 13;
         case 9:
            return 3;
         case 10:
            return 2;
         case 11:
            return 6;
         case 12:
            return 4;
         case 13:
            return 15;
         case 14:
            return 14;
         case 15:
            return 11;
         default:
            throw DbException.getInternalError("type=" + this.objectType);
      }
   }

   public String toString() {
      return "MetaRecord [id=" + this.id + ", objectType=" + this.objectType + ", sql=" + this.sql + ']';
   }
}
