package org.h2.command.dml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import org.h2.command.Prepared;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.table.Column;
import org.h2.tools.Csv;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.ValueVarchar;

public class Help extends Prepared {
   private final String[] conditions;
   private final Expression[] expressions;

   public Help(SessionLocal var1, String[] var2) {
      super(var1);
      this.conditions = var2;
      Database var3 = var1.getDatabase();
      this.expressions = new Expression[]{new ExpressionColumn(var3, new Column("SECTION", TypeInfo.TYPE_VARCHAR)), new ExpressionColumn(var3, new Column("TOPIC", TypeInfo.TYPE_VARCHAR)), new ExpressionColumn(var3, new Column("SYNTAX", TypeInfo.TYPE_VARCHAR)), new ExpressionColumn(var3, new Column("TEXT", TypeInfo.TYPE_VARCHAR))};
   }

   public ResultInterface queryMeta() {
      LocalResult var1 = new LocalResult(this.session, this.expressions, 4, 4);
      var1.done();
      return var1;
   }

   public ResultInterface query(long var1) {
      LocalResult var3 = new LocalResult(this.session, this.expressions, 4, 4);

      try {
         ResultSet var4 = getTable();

         label27:
         while(var4.next()) {
            String var5 = var4.getString(2).trim();
            String[] var6 = this.conditions;
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               String var9 = var6[var8];
               if (!var5.contains(var9)) {
                  continue label27;
               }
            }

            var3.addRow(ValueVarchar.get(var4.getString(1).trim(), this.session), ValueVarchar.get(var5, this.session), ValueVarchar.get(stripAnnotationsFromSyntax(var4.getString(3)), this.session), ValueVarchar.get(processHelpText(var4.getString(4)), this.session));
         }
      } catch (Exception var10) {
         throw DbException.convert(var10);
      }

      var3.done();
      return var3;
   }

   public static String stripAnnotationsFromSyntax(String var0) {
      return var0.replaceAll("@c@ ", "").replaceAll("@h2@ ", "").replaceAll("@c@", "").replaceAll("@h2@", "").trim();
   }

   public static String processHelpText(String var0) {
      int var1 = var0.length();

      int var2;
      for(var2 = 0; var2 < var1; ++var2) {
         char var3 = var0.charAt(var2);
         if (var3 == '.') {
            ++var2;
            break;
         }

         if (var3 == '"') {
            do {
               ++var2;
            } while(var2 < var1 && var0.charAt(var2) != '"');
         }
      }

      var0 = var0.substring(0, var2);
      return var0.trim();
   }

   public static ResultSet getTable() throws IOException {
      InputStreamReader var0 = new InputStreamReader(new ByteArrayInputStream(Utils.getResource("/org/h2/res/help.csv")));
      Csv var1 = new Csv();
      var1.setLineCommentCharacter('#');
      return var1.read(var0, (String[])null);
   }

   public boolean isQuery() {
      return true;
   }

   public boolean isTransactional() {
      return true;
   }

   public boolean isReadOnly() {
      return true;
   }

   public int getType() {
      return 57;
   }

   public boolean isCacheable() {
      return true;
   }
}
