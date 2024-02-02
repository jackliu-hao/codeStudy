package org.h2.schema;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.h2.Driver;
import org.h2.engine.SessionLocal;
import org.h2.expression.Alias;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.table.Column;
import org.h2.util.JdbcUtils;
import org.h2.util.SourceCompiler;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueToObjectConverter;
import org.h2.value.ValueToObjectConverter2;

public final class FunctionAlias extends UserDefinedFunction {
   private String methodName;
   private String source;
   private JavaMethod[] javaMethods;
   private boolean deterministic;

   private FunctionAlias(Schema var1, int var2, String var3) {
      super(var1, var2, var3, 3);
   }

   public static FunctionAlias newInstance(Schema var0, int var1, String var2, String var3, boolean var4) {
      FunctionAlias var5 = new FunctionAlias(var0, var1, var2);
      int var6 = var3.indexOf(40);
      int var7 = var3.lastIndexOf(46, var6 < 0 ? var3.length() : var6);
      if (var7 < 0) {
         throw DbException.get(42000, (String)var3);
      } else {
         var5.className = var3.substring(0, var7);
         var5.methodName = var3.substring(var7 + 1);
         var5.init(var4);
         return var5;
      }
   }

   public static FunctionAlias newInstanceFromSource(Schema var0, int var1, String var2, String var3, boolean var4) {
      FunctionAlias var5 = new FunctionAlias(var0, var1, var2);
      var5.source = var3;
      var5.init(var4);
      return var5;
   }

   private void init(boolean var1) {
      try {
         this.load();
      } catch (DbException var3) {
         if (!var1) {
            throw var3;
         }
      }

   }

   private synchronized void load() {
      if (this.javaMethods == null) {
         if (this.source != null) {
            this.loadFromSource();
         } else {
            this.loadClass();
         }

      }
   }

   private void loadFromSource() {
      SourceCompiler var1 = this.database.getCompiler();
      synchronized(var1) {
         String var3 = "org.h2.dynamic." + this.getName();
         var1.setSource(var3, this.source);

         try {
            Method var4 = var1.getMethod(var3);
            JavaMethod var5 = new JavaMethod(var4, 0);
            this.javaMethods = new JavaMethod[]{var5};
         } catch (DbException var7) {
            throw var7;
         } catch (Exception var8) {
            throw DbException.get(42000, var8, this.source);
         }

      }
   }

   private void loadClass() {
      Class var1 = JdbcUtils.loadUserClass(this.className);
      Method[] var2 = var1.getMethods();
      ArrayList var3 = new ArrayList(1);
      int var4 = 0;

      for(int var5 = var2.length; var4 < var5; ++var4) {
         Method var6 = var2[var4];
         if (Modifier.isStatic(var6.getModifiers()) && (var6.getName().equals(this.methodName) || getMethodSignature(var6).equals(this.methodName))) {
            JavaMethod var7 = new JavaMethod(var6, var4);
            Iterator var8 = var3.iterator();

            while(var8.hasNext()) {
               JavaMethod var9 = (JavaMethod)var8.next();
               if (var9.getParameterCount() == var7.getParameterCount()) {
                  throw DbException.get(90073, var9.toString(), var7.toString());
               }
            }

            var3.add(var7);
         }
      }

      if (var3.isEmpty()) {
         throw DbException.get(90139, this.methodName + " (" + this.className + ")");
      } else {
         this.javaMethods = (JavaMethod[])var3.toArray(new JavaMethod[0]);
         Arrays.sort(this.javaMethods);
      }
   }

   private static String getMethodSignature(Method var0) {
      StringBuilder var1 = new StringBuilder(var0.getName());
      var1.append('(');
      Class[] var2 = var0.getParameterTypes();
      int var3 = 0;

      for(int var4 = var2.length; var3 < var4; ++var3) {
         if (var3 > 0) {
            var1.append(',');
         }

         Class var5 = var2[var3];
         if (var5.isArray()) {
            var1.append(var5.getComponentType().getName()).append("[]");
         } else {
            var1.append(var5.getName());
         }
      }

      return var1.append(')').toString();
   }

   public String getDropSQL() {
      return this.getSQL(new StringBuilder("DROP ALIAS IF EXISTS "), 0).toString();
   }

   public String getCreateSQL() {
      StringBuilder var1 = new StringBuilder("CREATE FORCE ALIAS ");
      this.getSQL(var1, 0);
      if (this.deterministic) {
         var1.append(" DETERMINISTIC");
      }

      if (this.source != null) {
         StringUtils.quoteStringSQL(var1.append(" AS "), this.source);
      } else {
         StringUtils.quoteStringSQL(var1.append(" FOR "), this.className + '.' + this.methodName);
      }

      return var1.toString();
   }

   public int getType() {
      return 9;
   }

   public synchronized void removeChildrenAndResources(SessionLocal var1) {
      this.database.removeMeta(var1, this.getId());
      this.className = null;
      this.methodName = null;
      this.javaMethods = null;
      this.invalidate();
   }

   public JavaMethod findJavaMethod(Expression[] var1) {
      this.load();
      int var2 = var1.length;
      JavaMethod[] var3 = this.javaMethods;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         JavaMethod var6 = var3[var5];
         int var7 = var6.getParameterCount();
         if (var7 == var2 || var6.isVarArgs() && var7 <= var2 + 1) {
            return var6;
         }
      }

      throw DbException.get(90087, this.getName() + " (" + this.className + ", parameter count: " + var2 + ")");
   }

   public String getJavaMethodName() {
      return this.methodName;
   }

   public JavaMethod[] getJavaMethods() {
      this.load();
      return this.javaMethods;
   }

   public void setDeterministic(boolean var1) {
      this.deterministic = var1;
   }

   public boolean isDeterministic() {
      return this.deterministic;
   }

   public String getSource() {
      return this.source;
   }

   public static class JavaMethod implements Comparable<JavaMethod> {
      private final int id;
      private final Method method;
      private final TypeInfo dataType;
      private boolean hasConnectionParam;
      private boolean varArgs;
      private Class<?> varArgClass;
      private int paramCount;

      JavaMethod(Method var1, int var2) {
         this.method = var1;
         this.id = var2;
         Class[] var3 = var1.getParameterTypes();
         this.paramCount = var3.length;
         Class var4;
         if (this.paramCount > 0) {
            var4 = var3[0];
            if (Connection.class.isAssignableFrom(var4)) {
               this.hasConnectionParam = true;
               --this.paramCount;
            }
         }

         if (this.paramCount > 0) {
            var4 = var3[var3.length - 1];
            if (var4.isArray() && var1.isVarArgs()) {
               this.varArgs = true;
               this.varArgClass = var4.getComponentType();
            }
         }

         var4 = var1.getReturnType();
         this.dataType = ResultSet.class.isAssignableFrom(var4) ? null : ValueToObjectConverter2.classToType(var4);
      }

      public String toString() {
         return this.method.toString();
      }

      public boolean hasConnectionParam() {
         return this.hasConnectionParam;
      }

      public Value getValue(SessionLocal var1, Expression[] var2, boolean var3) {
         Object var4 = this.execute(var1, var2, var3);
         return Value.class.isAssignableFrom(this.method.getReturnType()) ? (Value)var4 : ValueToObjectConverter.objectToValue(var1, var4, this.dataType.getValueType()).convertTo(this.dataType, var1);
      }

      public ResultInterface getTableValue(SessionLocal var1, Expression[] var2, boolean var3) {
         Object var4 = this.execute(var1, var2, var3);
         if (var4 == null) {
            throw DbException.get(90000, this.method.getName());
         } else {
            return ResultInterface.class.isAssignableFrom(this.method.getReturnType()) ? (ResultInterface)var4 : resultSetToResult(var1, (ResultSet)var4, var3 ? 0 : Integer.MAX_VALUE);
         }
      }

      public static ResultInterface resultSetToResult(SessionLocal var0, ResultSet var1, int var2) {
         try {
            ResultSetMetaData var3 = var1.getMetaData();
            int var4 = var3.getColumnCount();
            Expression[] var5 = new Expression[var4];

            for(int var6 = 0; var6 < var4; ++var6) {
               String var7 = var3.getColumnLabel(var6 + 1);
               String var8 = var3.getColumnName(var6 + 1);
               String var9 = var3.getColumnTypeName(var6 + 1);
               int var10 = DataType.convertSQLTypeToValueType(var3.getColumnType(var6 + 1), var9);
               int var11 = var3.getPrecision(var6 + 1);
               int var12 = var3.getScale(var6 + 1);
               TypeInfo var13;
               if (var10 == 40 && var9.endsWith(" ARRAY")) {
                  var13 = TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.getTypeInfo(DataType.getTypeByName(var9.substring(0, var9.length() - 6), var0.getMode()).type));
               } else {
                  var13 = TypeInfo.getTypeInfo(var10, (long)var11, var12, (ExtTypeInfo)null);
               }

               Object var14 = new ExpressionColumn(var0.getDatabase(), new Column(var8, var13));
               if (!var7.equals(var8)) {
                  var14 = new Alias((Expression)var14, var7, false);
               }

               var5[var6] = (Expression)var14;
            }

            LocalResult var16 = new LocalResult(var0, var5, var4, var4);

            for(int var17 = 0; var17 < var2 && var1.next(); ++var17) {
               Value[] var18 = new Value[var4];

               for(int var19 = 0; var19 < var4; ++var19) {
                  var18[var19] = ValueToObjectConverter.objectToValue(var0, var1.getObject(var19 + 1), var5[var19].getType().getValueType());
               }

               var16.addRow(var18);
            }

            var16.done();
            return var16;
         } catch (SQLException var15) {
            throw DbException.convert(var15);
         }
      }

      private Object execute(SessionLocal var1, Expression[] var2, boolean var3) {
         Class[] var4 = this.method.getParameterTypes();
         Object[] var5 = new Object[var4.length];
         int var6 = 0;
         JdbcConnection var7 = var1.createConnection(var3);
         if (this.hasConnectionParam && var5.length > 0) {
            var5[var6++] = var7;
         }

         Object var8 = null;
         int var9;
         if (this.varArgs) {
            var9 = var2.length - var5.length + 1 + (this.hasConnectionParam ? 1 : 0);
            var8 = Array.newInstance(this.varArgClass, var9);
            var5[var5.length - 1] = var8;
         }

         var9 = 0;

         boolean var11;
         Value var13;
         for(int var10 = var2.length; var9 < var10; ++var6) {
            var11 = this.varArgs && var6 >= var4.length - 1;
            Class var12;
            if (var11) {
               var12 = this.varArgClass;
            } else {
               var12 = var4[var6];
            }

            var13 = var2[var9].getValue(var1);
            Object var14;
            if (Value.class.isAssignableFrom(var12)) {
               var14 = var13;
            } else {
               boolean var15 = var12.isPrimitive();
               if (var13 == ValueNull.INSTANCE) {
                  if (var15) {
                     if (!var3) {
                        return null;
                     }

                     var14 = DataType.getDefaultForPrimitiveType(var12);
                  } else {
                     var14 = null;
                  }
               } else {
                  var14 = ValueToObjectConverter.valueToObject(var15 ? Utils.getNonPrimitiveClass(var12) : var12, var13, var7);
               }
            }

            if (var11) {
               Array.set(var8, var6 - var5.length + 1, var14);
            } else {
               var5[var6] = var14;
            }

            ++var9;
         }

         boolean var24 = var1.getAutoCommit();
         Value var25 = var1.getLastIdentity();
         var11 = var1.getDatabase().getSettings().defaultConnection;

         Object var27;
         try {
            var1.setAutoCommit(false);

            Object var26;
            try {
               if (var11) {
                  Driver.setDefaultConnection(var1.createConnection(var3));
               }

               var26 = this.method.invoke((Object)null, var5);
               if (var26 == null) {
                  var13 = null;
                  return var13;
               }
            } catch (InvocationTargetException var21) {
               StringBuilder var29 = (new StringBuilder(this.method.getName())).append('(');
               int var28 = 0;

               for(int var16 = var5.length; var28 < var16; ++var28) {
                  if (var28 > 0) {
                     var29.append(", ");
                  }

                  var29.append(var5[var28]);
               }

               var29.append(')');
               throw DbException.convertInvocation(var21, var29.toString());
            } catch (Exception var22) {
               throw DbException.convert(var22);
            }

            var27 = var26;
         } finally {
            var1.setLastIdentity(var25);
            var1.setAutoCommit(var24);
            if (var11) {
               Driver.setDefaultConnection((Connection)null);
            }

         }

         return var27;
      }

      public Class<?>[] getColumnClasses() {
         return this.method.getParameterTypes();
      }

      public TypeInfo getDataType() {
         return this.dataType;
      }

      public int getParameterCount() {
         return this.paramCount;
      }

      public boolean isVarArgs() {
         return this.varArgs;
      }

      public int compareTo(JavaMethod var1) {
         if (this.varArgs != var1.varArgs) {
            return this.varArgs ? 1 : -1;
         } else if (this.paramCount != var1.paramCount) {
            return this.paramCount - var1.paramCount;
         } else if (this.hasConnectionParam != var1.hasConnectionParam) {
            return this.hasConnectionParam ? 1 : -1;
         } else {
            return this.id - var1.id;
         }
      }
   }
}
