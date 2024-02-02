package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

public final class XMLFunction extends FunctionN {
   public static final int XMLATTR = 0;
   public static final int XMLCDATA = 1;
   public static final int XMLCOMMENT = 2;
   public static final int XMLNODE = 3;
   public static final int XMLSTARTDOC = 4;
   public static final int XMLTEXT = 5;
   private static final String[] NAMES = new String[]{"XMLATTR", "XMLCDATA", "XMLCOMMENT", "XMLNODE", "XMLSTARTDOC", "XMLTEXT"};
   private final int function;

   public XMLFunction(int var1) {
      super(new Expression[4]);
      this.function = var1;
   }

   public Value getValue(SessionLocal var1) {
      switch (this.function) {
         case 3:
            return this.xmlNode(var1);
         case 4:
            return ValueVarchar.get(StringUtils.xmlStartDoc(), var1);
         default:
            return super.getValue(var1);
      }
   }

   private Value xmlNode(SessionLocal var1) {
      Value var2 = this.args[0].getValue(var1);
      if (var2 == ValueNull.INSTANCE) {
         return ValueNull.INSTANCE;
      } else {
         int var3 = this.args.length;
         String var4 = var3 >= 2 ? this.args[1].getValue(var1).getString() : null;
         String var5 = var3 >= 3 ? this.args[2].getValue(var1).getString() : null;
         boolean var6;
         if (var3 >= 4) {
            Value var7 = this.args[3].getValue(var1);
            if (var7 == ValueNull.INSTANCE) {
               return ValueNull.INSTANCE;
            }

            var6 = var7.getBoolean();
         } else {
            var6 = true;
         }

         return ValueVarchar.get(StringUtils.xmlNode(var2.getString(), var4, var5, var6), var1);
      }
   }

   protected Value getValue(SessionLocal var1, Value var2, Value var3, Value var4) {
      switch (this.function) {
         case 0:
            var2 = ValueVarchar.get(StringUtils.xmlAttr(var2.getString(), var3.getString()), var1);
            break;
         case 1:
            var2 = ValueVarchar.get(StringUtils.xmlCData(var2.getString()), var1);
            break;
         case 2:
            var2 = ValueVarchar.get(StringUtils.xmlComment(var2.getString()), var1);
            break;
         case 3:
         case 4:
         default:
            throw DbException.getInternalError("function=" + this.function);
         case 5:
            var2 = ValueVarchar.get(StringUtils.xmlText(var2.getString(), var3 != null && var3.getBoolean()), var1);
      }

      return var2;
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1, true);
      byte var3;
      byte var4;
      switch (this.function) {
         case 0:
            var3 = 2;
            var4 = 2;
            break;
         case 1:
         case 2:
            var3 = 1;
            var4 = 1;
            break;
         case 3:
            var3 = 1;
            var4 = 4;
            break;
         case 4:
            var3 = 0;
            var4 = 0;
            break;
         case 5:
            var3 = 1;
            var4 = 2;
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      int var5 = this.args.length;
      if (var5 >= var3 && var5 <= var4) {
         this.type = TypeInfo.TYPE_VARCHAR;
         return (Expression)(var2 ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
      } else {
         throw DbException.get(7001, (String[])(this.getName(), var3 + ".." + var4));
      }
   }

   public String getName() {
      return NAMES[this.function];
   }
}
