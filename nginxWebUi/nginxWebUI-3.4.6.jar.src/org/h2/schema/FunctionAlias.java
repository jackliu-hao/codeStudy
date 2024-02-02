/*     */ package org.h2.schema;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Session;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Alias;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.util.JdbcUtils;
/*     */ import org.h2.util.SourceCompiler;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.ExtTypeInfo;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueToObjectConverter;
/*     */ import org.h2.value.ValueToObjectConverter2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class FunctionAlias
/*     */   extends UserDefinedFunction
/*     */ {
/*     */   private String methodName;
/*     */   private String source;
/*     */   private JavaMethod[] javaMethods;
/*     */   private boolean deterministic;
/*     */   
/*     */   private FunctionAlias(Schema paramSchema, int paramInt, String paramString) {
/*  58 */     super(paramSchema, paramInt, paramString, 3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FunctionAlias newInstance(Schema paramSchema, int paramInt, String paramString1, String paramString2, boolean paramBoolean) {
/*  74 */     FunctionAlias functionAlias = new FunctionAlias(paramSchema, paramInt, paramString1);
/*  75 */     int i = paramString2.indexOf('(');
/*  76 */     int j = paramString2.lastIndexOf('.', (i < 0) ? paramString2
/*  77 */         .length() : i);
/*  78 */     if (j < 0) {
/*  79 */       throw DbException.get(42000, paramString2);
/*     */     }
/*  81 */     functionAlias.className = paramString2.substring(0, j);
/*  82 */     functionAlias.methodName = paramString2.substring(j + 1);
/*  83 */     functionAlias.init(paramBoolean);
/*  84 */     return functionAlias;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FunctionAlias newInstanceFromSource(Schema paramSchema, int paramInt, String paramString1, String paramString2, boolean paramBoolean) {
/*  99 */     FunctionAlias functionAlias = new FunctionAlias(paramSchema, paramInt, paramString1);
/* 100 */     functionAlias.source = paramString2;
/* 101 */     functionAlias.init(paramBoolean);
/* 102 */     return functionAlias;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void init(boolean paramBoolean) {
/*     */     try {
/* 109 */       load();
/* 110 */     } catch (DbException dbException) {
/* 111 */       if (!paramBoolean) {
/* 112 */         throw dbException;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private synchronized void load() {
/* 118 */     if (this.javaMethods != null) {
/*     */       return;
/*     */     }
/* 121 */     if (this.source != null) {
/* 122 */       loadFromSource();
/*     */     } else {
/* 124 */       loadClass();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void loadFromSource() {
/* 129 */     SourceCompiler sourceCompiler = this.database.getCompiler();
/* 130 */     synchronized (sourceCompiler) {
/* 131 */       String str = "org.h2.dynamic." + getName();
/* 132 */       sourceCompiler.setSource(str, this.source);
/*     */       try {
/* 134 */         Method method = sourceCompiler.getMethod(str);
/* 135 */         JavaMethod javaMethod = new JavaMethod(method, 0);
/* 136 */         this.javaMethods = new JavaMethod[] { javaMethod };
/*     */       
/*     */       }
/* 139 */       catch (DbException dbException) {
/* 140 */         throw dbException;
/* 141 */       } catch (Exception exception) {
/* 142 */         throw DbException.get(42000, exception, new String[] { this.source });
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void loadClass() {
/* 148 */     Class clazz = JdbcUtils.loadUserClass(this.className);
/* 149 */     Method[] arrayOfMethod = clazz.getMethods();
/* 150 */     ArrayList<JavaMethod> arrayList = new ArrayList(1); byte b; int i;
/* 151 */     for (b = 0, i = arrayOfMethod.length; b < i; b++) {
/* 152 */       Method method = arrayOfMethod[b];
/* 153 */       if (Modifier.isStatic(method.getModifiers()))
/*     */       {
/*     */         
/* 156 */         if (method.getName().equals(this.methodName) || 
/* 157 */           getMethodSignature(method).equals(this.methodName)) {
/* 158 */           JavaMethod javaMethod = new JavaMethod(method, b);
/* 159 */           for (JavaMethod javaMethod1 : arrayList) {
/* 160 */             if (javaMethod1.getParameterCount() == javaMethod.getParameterCount()) {
/* 161 */               throw DbException.get(90073, new String[] { javaMethod1
/*     */                     
/* 163 */                     .toString(), javaMethod.toString() });
/*     */             }
/*     */           } 
/* 166 */           arrayList.add(javaMethod);
/*     */         }  } 
/*     */     } 
/* 169 */     if (arrayList.isEmpty()) {
/* 170 */       throw DbException.get(90139, this.methodName + " (" + this.className + ")");
/*     */     }
/*     */ 
/*     */     
/* 174 */     this.javaMethods = arrayList.<JavaMethod>toArray(new JavaMethod[0]);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 179 */     Arrays.sort((Object[])this.javaMethods);
/*     */   }
/*     */   
/*     */   private static String getMethodSignature(Method paramMethod) {
/* 183 */     StringBuilder stringBuilder = new StringBuilder(paramMethod.getName());
/* 184 */     stringBuilder.append('(');
/* 185 */     Class[] arrayOfClass = paramMethod.getParameterTypes(); byte b; int i;
/* 186 */     for (b = 0, i = arrayOfClass.length; b < i; b++) {
/* 187 */       if (b > 0)
/*     */       {
/*     */         
/* 190 */         stringBuilder.append(',');
/*     */       }
/* 192 */       Class clazz = arrayOfClass[b];
/* 193 */       if (clazz.isArray()) {
/* 194 */         stringBuilder.append(clazz.getComponentType().getName()).append("[]");
/*     */       } else {
/* 196 */         stringBuilder.append(clazz.getName());
/*     */       } 
/*     */     } 
/* 199 */     return stringBuilder.append(')').toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDropSQL() {
/* 204 */     return getSQL(new StringBuilder("DROP ALIAS IF EXISTS "), 0).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/* 209 */     StringBuilder stringBuilder = new StringBuilder("CREATE FORCE ALIAS ");
/* 210 */     getSQL(stringBuilder, 0);
/* 211 */     if (this.deterministic) {
/* 212 */       stringBuilder.append(" DETERMINISTIC");
/*     */     }
/* 214 */     if (this.source != null) {
/* 215 */       StringUtils.quoteStringSQL(stringBuilder.append(" AS "), this.source);
/*     */     } else {
/* 217 */       StringUtils.quoteStringSQL(stringBuilder.append(" FOR "), this.className + '.' + this.methodName);
/*     */     } 
/* 219 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 224 */     return 9;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 229 */     this.database.removeMeta(paramSessionLocal, getId());
/* 230 */     this.className = null;
/* 231 */     this.methodName = null;
/* 232 */     this.javaMethods = null;
/* 233 */     invalidate();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaMethod findJavaMethod(Expression[] paramArrayOfExpression) {
/* 244 */     load();
/* 245 */     int i = paramArrayOfExpression.length;
/* 246 */     for (JavaMethod javaMethod : this.javaMethods) {
/* 247 */       int j = javaMethod.getParameterCount();
/* 248 */       if (j == i || (javaMethod.isVarArgs() && j <= i + 1))
/*     */       {
/* 250 */         return javaMethod;
/*     */       }
/*     */     } 
/* 253 */     throw DbException.get(90087, getName() + " (" + this.className + ", parameter count: " + i + ")");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getJavaMethodName() {
/* 258 */     return this.methodName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaMethod[] getJavaMethods() {
/* 267 */     load();
/* 268 */     return this.javaMethods;
/*     */   }
/*     */   
/*     */   public void setDeterministic(boolean paramBoolean) {
/* 272 */     this.deterministic = paramBoolean;
/*     */   }
/*     */   
/*     */   public boolean isDeterministic() {
/* 276 */     return this.deterministic;
/*     */   }
/*     */   
/*     */   public String getSource() {
/* 280 */     return this.source;
/*     */   }
/*     */ 
/*     */   
/*     */   public static class JavaMethod
/*     */     implements Comparable<JavaMethod>
/*     */   {
/*     */     private final int id;
/*     */     
/*     */     private final Method method;
/*     */     
/*     */     private final TypeInfo dataType;
/*     */     private boolean hasConnectionParam;
/*     */     private boolean varArgs;
/*     */     private Class<?> varArgClass;
/*     */     private int paramCount;
/*     */     
/*     */     JavaMethod(Method param1Method, int param1Int) {
/* 298 */       this.method = param1Method;
/* 299 */       this.id = param1Int;
/* 300 */       Class[] arrayOfClass = param1Method.getParameterTypes();
/* 301 */       this.paramCount = arrayOfClass.length;
/* 302 */       if (this.paramCount > 0) {
/* 303 */         Class<?> clazz1 = arrayOfClass[0];
/* 304 */         if (Connection.class.isAssignableFrom(clazz1)) {
/* 305 */           this.hasConnectionParam = true;
/* 306 */           this.paramCount--;
/*     */         } 
/*     */       } 
/* 309 */       if (this.paramCount > 0) {
/* 310 */         Class clazz1 = arrayOfClass[arrayOfClass.length - 1];
/* 311 */         if (clazz1.isArray() && param1Method.isVarArgs()) {
/* 312 */           this.varArgs = true;
/* 313 */           this.varArgClass = clazz1.getComponentType();
/*     */         } 
/*     */       } 
/* 316 */       Class<?> clazz = param1Method.getReturnType();
/* 317 */       this
/* 318 */         .dataType = ResultSet.class.isAssignableFrom(clazz) ? null : ValueToObjectConverter2.classToType(clazz);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 323 */       return this.method.toString();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasConnectionParam() {
/* 332 */       return this.hasConnectionParam;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value getValue(SessionLocal param1SessionLocal, Expression[] param1ArrayOfExpression, boolean param1Boolean) {
/* 345 */       Object object = execute(param1SessionLocal, param1ArrayOfExpression, param1Boolean);
/* 346 */       if (Value.class.isAssignableFrom(this.method.getReturnType())) {
/* 347 */         return (Value)object;
/*     */       }
/* 349 */       return ValueToObjectConverter.objectToValue((Session)param1SessionLocal, object, this.dataType.getValueType())
/* 350 */         .convertTo(this.dataType, (CastDataProvider)param1SessionLocal);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ResultInterface getTableValue(SessionLocal param1SessionLocal, Expression[] param1ArrayOfExpression, boolean param1Boolean) {
/* 363 */       Object object = execute(param1SessionLocal, param1ArrayOfExpression, param1Boolean);
/* 364 */       if (object == null) {
/* 365 */         throw DbException.get(90000, this.method.getName());
/*     */       }
/* 367 */       if (ResultInterface.class.isAssignableFrom(this.method.getReturnType())) {
/* 368 */         return (ResultInterface)object;
/*     */       }
/* 370 */       return resultSetToResult(param1SessionLocal, (ResultSet)object, param1Boolean ? 0 : Integer.MAX_VALUE);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static ResultInterface resultSetToResult(SessionLocal param1SessionLocal, ResultSet param1ResultSet, int param1Int) {
/*     */       try {
/* 384 */         ResultSetMetaData resultSetMetaData = param1ResultSet.getMetaData();
/* 385 */         int i = resultSetMetaData.getColumnCount();
/* 386 */         Expression[] arrayOfExpression = new Expression[i];
/* 387 */         for (byte b1 = 0; b1 < i; b1++) {
/* 388 */           TypeInfo typeInfo; Alias alias; String str1 = resultSetMetaData.getColumnLabel(b1 + 1);
/* 389 */           String str2 = resultSetMetaData.getColumnName(b1 + 1);
/* 390 */           String str3 = resultSetMetaData.getColumnTypeName(b1 + 1);
/* 391 */           int j = DataType.convertSQLTypeToValueType(resultSetMetaData.getColumnType(b1 + 1), str3);
/* 392 */           int k = resultSetMetaData.getPrecision(b1 + 1);
/* 393 */           int m = resultSetMetaData.getScale(b1 + 1);
/*     */           
/* 395 */           if (j == 40 && str3.endsWith(" ARRAY")) {
/*     */             
/* 397 */             typeInfo = TypeInfo.getTypeInfo(40, -1L, 0, 
/* 398 */                 (ExtTypeInfo)TypeInfo.getTypeInfo((DataType.getTypeByName(str3
/* 399 */                     .substring(0, str3.length() - 6), param1SessionLocal
/* 400 */                     .getMode())).type));
/*     */           } else {
/* 402 */             typeInfo = TypeInfo.getTypeInfo(j, k, m, null);
/*     */           } 
/* 404 */           ExpressionColumn expressionColumn = new ExpressionColumn(param1SessionLocal.getDatabase(), new Column(str2, typeInfo));
/* 405 */           if (!str1.equals(str2)) {
/* 406 */             alias = new Alias((Expression)expressionColumn, str1, false);
/*     */           }
/* 408 */           arrayOfExpression[b1] = (Expression)alias;
/*     */         } 
/* 410 */         LocalResult localResult = new LocalResult(param1SessionLocal, arrayOfExpression, i, i);
/* 411 */         for (byte b2 = 0; b2 < param1Int && param1ResultSet.next(); b2++) {
/* 412 */           Value[] arrayOfValue = new Value[i];
/* 413 */           for (byte b = 0; b < i; b++) {
/* 414 */             arrayOfValue[b] = ValueToObjectConverter.objectToValue((Session)param1SessionLocal, param1ResultSet.getObject(b + 1), arrayOfExpression[b]
/* 415 */                 .getType().getValueType());
/*     */           }
/* 417 */           localResult.addRow(arrayOfValue);
/*     */         } 
/* 419 */         localResult.done();
/* 420 */         return (ResultInterface)localResult;
/* 421 */       } catch (SQLException sQLException) {
/* 422 */         throw DbException.convert(sQLException);
/*     */       } 
/*     */     } private Object execute(SessionLocal param1SessionLocal, Expression[] param1ArrayOfExpression, boolean param1Boolean) {
/*     */       Value value;
/*     */       boolean bool2;
/* 427 */       Class[] arrayOfClass = this.method.getParameterTypes();
/* 428 */       Object[] arrayOfObject = new Object[arrayOfClass.length];
/* 429 */       byte b1 = 0;
/* 430 */       JdbcConnection jdbcConnection = param1SessionLocal.createConnection(param1Boolean);
/* 431 */       if (this.hasConnectionParam && arrayOfObject.length > 0) {
/* 432 */         arrayOfObject[b1++] = jdbcConnection;
/*     */       }
/*     */ 
/*     */       
/* 436 */       Object object = null;
/* 437 */       if (this.varArgs) {
/* 438 */         int j = param1ArrayOfExpression.length - arrayOfObject.length + 1 + (this.hasConnectionParam ? 1 : 0);
/*     */         
/* 440 */         object = Array.newInstance(this.varArgClass, j);
/* 441 */         arrayOfObject[arrayOfObject.length - 1] = object;
/*     */       }  byte b2;
/*     */       int i;
/* 444 */       for (b2 = 0, i = param1ArrayOfExpression.length; b2 < i; b2++, b1++) {
/* 445 */         Class<?> clazz; Object object1; boolean bool = (this.varArgs && b1 >= arrayOfClass.length - 1) ? true : false;
/*     */ 
/*     */         
/* 448 */         if (bool) {
/* 449 */           clazz = this.varArgClass;
/*     */         } else {
/* 451 */           clazz = arrayOfClass[b1];
/*     */         } 
/* 453 */         Value value1 = param1ArrayOfExpression[b2].getValue(param1SessionLocal);
/*     */         
/* 455 */         if (Value.class.isAssignableFrom(clazz)) {
/* 456 */           object1 = value1;
/*     */         } else {
/* 458 */           boolean bool3 = clazz.isPrimitive();
/* 459 */           if (value1 == ValueNull.INSTANCE) {
/* 460 */             if (bool3) {
/* 461 */               if (param1Boolean) {
/*     */ 
/*     */ 
/*     */                 
/* 465 */                 object1 = DataType.getDefaultForPrimitiveType(clazz);
/*     */               } else {
/*     */                 
/* 468 */                 return null;
/*     */               } 
/*     */             } else {
/* 471 */               object1 = null;
/*     */             } 
/*     */           } else {
/* 474 */             object1 = ValueToObjectConverter.valueToObject(bool3 ? 
/* 475 */                 Utils.getNonPrimitiveClass(clazz) : clazz, value1, jdbcConnection);
/*     */           } 
/*     */         } 
/* 478 */         if (bool) {
/* 479 */           Array.set(object, b1 - arrayOfObject.length + 1, object1);
/*     */         } else {
/* 481 */           arrayOfObject[b1] = object1;
/*     */         } 
/*     */       } 
/* 484 */       boolean bool1 = param1SessionLocal.getAutoCommit();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Class<?>[] getColumnClasses() {
/* 523 */       return this.method.getParameterTypes();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public TypeInfo getDataType() {
/* 534 */       return this.dataType;
/*     */     }
/*     */     
/*     */     public int getParameterCount() {
/* 538 */       return this.paramCount;
/*     */     }
/*     */     
/*     */     public boolean isVarArgs() {
/* 542 */       return this.varArgs;
/*     */     }
/*     */ 
/*     */     
/*     */     public int compareTo(JavaMethod param1JavaMethod) {
/* 547 */       if (this.varArgs != param1JavaMethod.varArgs) {
/* 548 */         return this.varArgs ? 1 : -1;
/*     */       }
/* 550 */       if (this.paramCount != param1JavaMethod.paramCount) {
/* 551 */         return this.paramCount - param1JavaMethod.paramCount;
/*     */       }
/* 553 */       if (this.hasConnectionParam != param1JavaMethod.hasConnectionParam) {
/* 554 */         return this.hasConnectionParam ? 1 : -1;
/*     */       }
/* 556 */       return this.id - param1JavaMethod.id;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\schema\FunctionAlias.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */