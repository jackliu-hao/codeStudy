package org.h2.engine;

import org.h2.message.DbException;
import org.h2.table.Table;
import org.h2.util.StringUtils;

public final class Comment extends DbObject {
   private final int objectType;
   private final String quotedObjectName;
   private String commentText;

   public Comment(Database var1, int var2, DbObject var3) {
      super(var1, var2, getKey(var3), 2);
      this.objectType = var3.getType();
      this.quotedObjectName = var3.getSQL(0);
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      throw DbException.getInternalError(this.toString());
   }

   private static String getTypeName(int var0) {
      switch (var0) {
         case 0:
            return "TABLE";
         case 1:
            return "INDEX";
         case 2:
            return "USER";
         case 3:
            return "SEQUENCE";
         case 4:
            return "TRIGGER";
         case 5:
            return "CONSTRAINT";
         case 6:
         case 8:
         default:
            return "type" + var0;
         case 7:
            return "ROLE";
         case 9:
            return "ALIAS";
         case 10:
            return "SCHEMA";
         case 11:
            return "CONSTANT";
         case 12:
            return "DOMAIN";
      }
   }

   public String getCreateSQL() {
      StringBuilder var1 = new StringBuilder("COMMENT ON ");
      var1.append(getTypeName(this.objectType)).append(' ').append(this.quotedObjectName).append(" IS ");
      if (this.commentText == null) {
         var1.append("NULL");
      } else {
         StringUtils.quoteStringSQL(var1, this.commentText);
      }

      return var1.toString();
   }

   public int getType() {
      return 13;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      this.database.removeMeta(var1, this.getId());
   }

   public void checkRename() {
      throw DbException.getInternalError();
   }

   static String getKey(DbObject var0) {
      StringBuilder var1 = (new StringBuilder(getTypeName(var0.getType()))).append(' ');
      var0.getSQL(var1, 0);
      return var1.toString();
   }

   public void setCommentText(String var1) {
      this.commentText = var1;
   }
}
