package org.h2.expression.function;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionWithFlags;
import org.h2.expression.Format;
import org.h2.expression.OperationN;
import org.h2.expression.Subquery;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.json.JsonConstructorUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueJson;
import org.h2.value.ValueNull;

public final class JsonConstructorFunction extends OperationN implements ExpressionWithFlags, NamedExpression {
   private final boolean array;
   private int flags;

   public JsonConstructorFunction(boolean var1) {
      super(new Expression[4]);
      this.array = var1;
   }

   public void setFlags(int var1) {
      this.flags = var1;
   }

   public int getFlags() {
      return this.flags;
   }

   public Value getValue(SessionLocal var1) {
      return this.array ? this.jsonArray(var1, this.args) : this.jsonObject(var1, this.args);
   }

   private Value jsonObject(SessionLocal var1, Expression[] var2) {
      ByteArrayOutputStream var3 = new ByteArrayOutputStream();
      var3.write(123);
      int var4 = 0;
      int var5 = var2.length;

      while(true) {
         String var6;
         Object var7;
         while(true) {
            if (var4 >= var5) {
               return JsonConstructorUtils.jsonObjectFinish(var3, this.flags);
            }

            var6 = var2[var4++].getValue(var1).getString();
            if (var6 == null) {
               throw DbException.getInvalidValueException("JSON_OBJECT key", "NULL");
            }

            var7 = var2[var4++].getValue(var1);
            if (var7 != ValueNull.INSTANCE) {
               break;
            }

            if ((this.flags & 1) == 0) {
               var7 = ValueJson.NULL;
               break;
            }
         }

         JsonConstructorUtils.jsonObjectAppend(var3, var6, (Value)var7);
      }
   }

   private Value jsonArray(SessionLocal var1, Expression[] var2) {
      ByteArrayOutputStream var3;
      label43: {
         var3 = new ByteArrayOutputStream();
         var3.write(91);
         int var4 = var2.length;
         if (var4 == 1) {
            Expression var5 = var2[0];
            if (var5 instanceof Subquery) {
               Subquery var11 = (Subquery)var5;
               Iterator var12 = var11.getAllRows(var1).iterator();

               while(true) {
                  if (!var12.hasNext()) {
                     break label43;
                  }

                  Value var13 = (Value)var12.next();
                  JsonConstructorUtils.jsonArrayAppend(var3, var13, this.flags);
               }
            }

            if (var5 instanceof Format) {
               Format var6 = (Format)var5;
               var5 = var6.getSubexpression(0);
               if (var5 instanceof Subquery) {
                  Subquery var7 = (Subquery)var5;
                  Iterator var8 = var7.getAllRows(var1).iterator();

                  while(true) {
                     if (!var8.hasNext()) {
                        break label43;
                     }

                     Value var9 = (Value)var8.next();
                     JsonConstructorUtils.jsonArrayAppend(var3, var6.getValue(var9), this.flags);
                  }
               }
            }
         }

         int var10 = 0;

         while(var10 < var4) {
            JsonConstructorUtils.jsonArrayAppend(var3, var2[var10++].getValue(var1), this.flags);
         }
      }

      var3.write(93);
      return ValueJson.getInternal(var3.toByteArray());
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1, true);
      this.type = TypeInfo.TYPE_JSON;
      return (Expression)(var2 ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      var1.append(this.getName()).append('(');
      if (this.array) {
         writeExpressions(var1, this.args, var2);
      } else {
         int var3 = 0;
         int var4 = this.args.length;

         while(var3 < var4) {
            if (var3 > 0) {
               var1.append(", ");
            }

            this.args[var3++].getUnenclosedSQL(var1, var2).append(": ");
            this.args[var3++].getUnenclosedSQL(var1, var2);
         }
      }

      return getJsonFunctionFlagsSQL(var1, this.flags, this.array).append(')');
   }

   public static StringBuilder getJsonFunctionFlagsSQL(StringBuilder var0, int var1, boolean var2) {
      if ((var1 & 1) != 0) {
         if (!var2) {
            var0.append(" ABSENT ON NULL");
         }
      } else if (var2) {
         var0.append(" NULL ON NULL");
      }

      if (!var2 && (var1 & 2) != 0) {
         var0.append(" WITH UNIQUE KEYS");
      }

      return var0;
   }

   public String getName() {
      return this.array ? "JSON_ARRAY" : "JSON_OBJECT";
   }
}
