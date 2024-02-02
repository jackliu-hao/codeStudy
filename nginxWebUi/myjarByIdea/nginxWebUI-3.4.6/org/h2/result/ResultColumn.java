package org.h2.result;

import java.io.IOException;
import org.h2.value.Transfer;
import org.h2.value.TypeInfo;

public class ResultColumn {
   final String alias;
   final String schemaName;
   final String tableName;
   final String columnName;
   final TypeInfo columnType;
   final boolean identity;
   final int nullable;

   ResultColumn(Transfer var1) throws IOException {
      this.alias = var1.readString();
      this.schemaName = var1.readString();
      this.tableName = var1.readString();
      this.columnName = var1.readString();
      this.columnType = var1.readTypeInfo();
      if (var1.getVersion() < 20) {
         var1.readInt();
      }

      this.identity = var1.readBoolean();
      this.nullable = var1.readInt();
   }

   public static void writeColumn(Transfer var0, ResultInterface var1, int var2) throws IOException {
      var0.writeString(var1.getAlias(var2));
      var0.writeString(var1.getSchemaName(var2));
      var0.writeString(var1.getTableName(var2));
      var0.writeString(var1.getColumnName(var2));
      TypeInfo var3 = var1.getColumnType(var2);
      var0.writeTypeInfo(var3);
      if (var0.getVersion() < 20) {
         var0.writeInt(var3.getDisplaySize());
      }

      var0.writeBoolean(var1.isIdentity(var2));
      var0.writeInt(var1.getNullable(var2));
   }
}
