package org.h2.expression.aggregate;

import java.sql.SQLException;
import java.util.Iterator;
import org.h2.command.query.Select;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.h2.schema.UserAggregate;
import org.h2.util.ParserUtil;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;
import org.h2.value.ValueToObjectConverter;

public class JavaAggregate extends AbstractAggregate {
   private final UserAggregate userAggregate;
   private int[] argTypes;
   private int dataType;
   private JdbcConnection userConnection;

   public JavaAggregate(UserAggregate var1, Expression[] var2, Select var3, boolean var4) {
      super(var3, var2, var4);
      this.userAggregate = var1;
   }

   public int getCost() {
      int var1 = 5;
      Expression[] var2 = this.args;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Expression var5 = var2[var4];
         var1 += var5.getCost();
      }

      if (this.filterCondition != null) {
         var1 += this.filterCondition.getCost();
      }

      return var1;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      ParserUtil.quoteIdentifier(var1, this.userAggregate.getName(), var2).append('(');
      writeExpressions(var1, this.args, var2).append(')');
      return this.appendTailConditions(var1, var2, false);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      if (!super.isEverything(var1)) {
         return false;
      } else {
         switch (var1.getType()) {
            case 1:
            case 2:
               return false;
            case 7:
               var1.addDependency(this.userAggregate);
            default:
               Expression[] var2 = this.args;
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  Expression var5 = var2[var4];
                  if (var5 != null && !var5.isEverything(var1)) {
                     return false;
                  }
               }

               return this.filterCondition == null || this.filterCondition.isEverything(var1);
         }
      }
   }

   public Expression optimize(SessionLocal var1) {
      super.optimize(var1);
      this.userConnection = var1.createConnection(false);
      int var2 = this.args.length;
      this.argTypes = new int[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = this.args[var3].getType().getValueType();
         this.argTypes[var3] = var4;
      }

      try {
         org.h2.api.Aggregate var6 = this.getInstance();
         this.dataType = var6.getInternalType(this.argTypes);
         this.type = TypeInfo.getTypeInfo(this.dataType);
         return this;
      } catch (SQLException var5) {
         throw DbException.convert(var5);
      }
   }

   private org.h2.api.Aggregate getInstance() {
      org.h2.api.Aggregate var1 = this.userAggregate.getInstance();

      try {
         var1.init(this.userConnection);
         return var1;
      } catch (SQLException var3) {
         throw DbException.convert(var3);
      }
   }

   public Value getAggregatedValue(SessionLocal var1, Object var2) {
      try {
         org.h2.api.Aggregate var3;
         if (this.distinct) {
            var3 = this.getInstance();
            AggregateDataCollecting var4 = (AggregateDataCollecting)var2;
            if (var4 != null) {
               Iterator var5 = var4.values.iterator();

               label44:
               while(true) {
                  while(true) {
                     if (!var5.hasNext()) {
                        break label44;
                     }

                     Value var6 = (Value)var5.next();
                     if (this.args.length == 1) {
                        var3.add(ValueToObjectConverter.valueToDefaultObject(var6, this.userConnection, false));
                     } else {
                        Value[] var7 = ((ValueRow)var6).getList();
                        Object[] var8 = new Object[this.args.length];
                        int var9 = 0;

                        for(int var10 = this.args.length; var9 < var10; ++var9) {
                           var8[var9] = ValueToObjectConverter.valueToDefaultObject(var7[var9], this.userConnection, false);
                        }

                        var3.add(var8);
                     }
                  }
               }
            }
         } else {
            var3 = (org.h2.api.Aggregate)var2;
            if (var3 == null) {
               var3 = this.getInstance();
            }
         }

         Object var12 = var3.getResult();
         return (Value)(var12 == null ? ValueNull.INSTANCE : ValueToObjectConverter.objectToValue(var1, var12, this.dataType));
      } catch (SQLException var11) {
         throw DbException.convert(var11);
      }
   }

   protected void updateAggregate(SessionLocal var1, Object var2) {
      this.updateData(var1, var2, (Value[])null);
   }

   private void updateData(SessionLocal var1, Object var2, Value[] var3) {
      try {
         int var7;
         int var8;
         if (this.distinct) {
            AggregateDataCollecting var4 = (AggregateDataCollecting)var2;
            Value[] var5 = new Value[this.args.length];
            Value var6 = null;
            var7 = 0;

            for(var8 = this.args.length; var7 < var8; ++var7) {
               var6 = var3 == null ? this.args[var7].getValue(var1) : var3[var7];
               var5[var7] = var6;
            }

            var4.add(var1, (Value)(this.args.length == 1 ? var6 : ValueRow.get(var5)));
         } else {
            org.h2.api.Aggregate var11 = (org.h2.api.Aggregate)var2;
            Object[] var12 = new Object[this.args.length];
            Object var13 = null;
            var7 = 0;

            for(var8 = this.args.length; var7 < var8; ++var7) {
               Value var9 = var3 == null ? this.args[var7].getValue(var1) : var3[var7];
               var13 = ValueToObjectConverter.valueToDefaultObject(var9, this.userConnection, false);
               var12[var7] = var13;
            }

            var11.add(this.args.length == 1 ? var13 : var12);
         }

      } catch (SQLException var10) {
         throw DbException.convert(var10);
      }
   }

   protected void updateGroupAggregates(SessionLocal var1, int var2) {
      super.updateGroupAggregates(var1, var2);
      Expression[] var3 = this.args;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Expression var6 = var3[var5];
         var6.updateAggregate(var1, var2);
      }

   }

   protected int getNumExpressions() {
      int var1 = this.args.length;
      if (this.filterCondition != null) {
         ++var1;
      }

      return var1;
   }

   protected void rememberExpressions(SessionLocal var1, Value[] var2) {
      int var3 = this.args.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         var2[var4] = this.args[var4].getValue(var1);
      }

      if (this.filterCondition != null) {
         var2[var3] = ValueBoolean.get(this.filterCondition.getBooleanValue(var1));
      }

   }

   protected void updateFromExpressions(SessionLocal var1, Object var2, Value[] var3) {
      if (this.filterCondition == null || var3[this.getNumExpressions() - 1].isTrue()) {
         this.updateData(var1, var2, var3);
      }

   }

   protected Object createAggregateData() {
      return this.distinct ? new AggregateDataCollecting(true, false, AggregateDataCollecting.NullCollectionMode.IGNORED) : this.getInstance();
   }
}
