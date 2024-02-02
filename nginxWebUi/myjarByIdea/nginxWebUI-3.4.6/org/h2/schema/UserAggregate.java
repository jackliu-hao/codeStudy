package org.h2.schema;

import java.sql.Connection;
import java.sql.SQLException;
import org.h2.api.Aggregate;
import org.h2.api.AggregateFunction;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.util.JdbcUtils;
import org.h2.util.StringUtils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;

public final class UserAggregate extends UserDefinedFunction {
   private Class<?> javaClass;

   public UserAggregate(Schema var1, int var2, String var3, String var4, boolean var5) {
      super(var1, var2, var3, 3);
      this.className = var4;
      if (!var5) {
         this.getInstance();
      }

   }

   public Aggregate getInstance() {
      if (this.javaClass == null) {
         this.javaClass = JdbcUtils.loadUserClass(this.className);
      }

      try {
         Object var1 = this.javaClass.getDeclaredConstructor().newInstance();
         Object var2;
         if (var1 instanceof Aggregate) {
            var2 = (Aggregate)var1;
         } else {
            var2 = new AggregateWrapper((AggregateFunction)var1);
         }

         return (Aggregate)var2;
      } catch (Exception var3) {
         throw DbException.convert(var3);
      }
   }

   public String getDropSQL() {
      StringBuilder var1 = new StringBuilder("DROP AGGREGATE IF EXISTS ");
      return this.getSQL(var1, 0).toString();
   }

   public String getCreateSQL() {
      StringBuilder var1 = new StringBuilder("CREATE FORCE AGGREGATE ");
      this.getSQL(var1, 0).append(" FOR ");
      return StringUtils.quoteStringSQL(var1, this.className).toString();
   }

   public int getType() {
      return 14;
   }

   public synchronized void removeChildrenAndResources(SessionLocal var1) {
      this.database.removeMeta(var1, this.getId());
      this.className = null;
      this.javaClass = null;
      this.invalidate();
   }

   private static class AggregateWrapper implements Aggregate {
      private final AggregateFunction aggregateFunction;

      AggregateWrapper(AggregateFunction var1) {
         this.aggregateFunction = var1;
      }

      public void init(Connection var1) throws SQLException {
         this.aggregateFunction.init(var1);
      }

      public int getInternalType(int[] var1) throws SQLException {
         int[] var2 = new int[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var2[var3] = DataType.convertTypeToSQLType(TypeInfo.getTypeInfo(var1[var3]));
         }

         return DataType.convertSQLTypeToValueType(this.aggregateFunction.getType(var2));
      }

      public void add(Object var1) throws SQLException {
         this.aggregateFunction.add(var1);
      }

      public Object getResult() throws SQLException {
         return this.aggregateFunction.getResult();
      }
   }
}
