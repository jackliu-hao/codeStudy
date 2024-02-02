/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.google.protobuf.ByteString;
/*     */ import com.mysql.cj.exceptions.FeatureNotAvailableException;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.util.TimeUtil;
/*     */ import com.mysql.cj.x.protobuf.MysqlxCrud;
/*     */ import com.mysql.cj.x.protobuf.MysqlxDatatypes;
/*     */ import com.mysql.cj.x.protobuf.MysqlxExpr;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.sql.Date;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.stream.Collectors;
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
/*     */ public class ExprUtil
/*     */ {
/*  55 */   private static SimpleDateFormat javaSqlDateFormat = TimeUtil.getSimpleDateFormat(null, "yyyy-MM-dd", null);
/*  56 */   private static SimpleDateFormat javaSqlTimestampFormat = TimeUtil.getSimpleDateFormat(null, "yyyy-MM-dd'T'HH:mm:ss.S", null);
/*  57 */   private static SimpleDateFormat javaSqlTimeFormat = TimeUtil.getSimpleDateFormat(null, "HH:mm:ss.S", null);
/*  58 */   private static SimpleDateFormat javaUtilDateFormat = TimeUtil.getSimpleDateFormat(null, "yyyy-MM-dd'T'HH:mm:ss.S", null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxExpr.Expr buildLiteralNullScalar() {
/*  66 */     return buildLiteralExpr(nullScalar());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxExpr.Expr buildLiteralScalar(double d) {
/*  77 */     return buildLiteralExpr(scalarOf(d));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxExpr.Expr buildLiteralScalar(long l) {
/*  88 */     return buildLiteralExpr(scalarOf(l));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxExpr.Expr buildLiteralScalar(String str) {
/*  99 */     return buildLiteralExpr(scalarOf(str));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxExpr.Expr buildLiteralScalar(byte[] bytes) {
/* 110 */     return buildLiteralExpr(scalarOf(bytes));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxExpr.Expr buildLiteralScalar(boolean b) {
/* 121 */     return buildLiteralExpr(scalarOf(b));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxExpr.Expr buildLiteralExpr(MysqlxDatatypes.Scalar scalar) {
/* 132 */     return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.LITERAL).setLiteral(scalar).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxExpr.Expr buildPlaceholderExpr(int pos) {
/* 143 */     return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.PLACEHOLDER).setPosition(pos).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxDatatypes.Scalar nullScalar() {
/* 152 */     return MysqlxDatatypes.Scalar.newBuilder().setType(MysqlxDatatypes.Scalar.Type.V_NULL).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxDatatypes.Scalar scalarOf(double d) {
/* 163 */     return MysqlxDatatypes.Scalar.newBuilder().setType(MysqlxDatatypes.Scalar.Type.V_DOUBLE).setVDouble(d).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxDatatypes.Scalar scalarOf(long l) {
/* 174 */     return MysqlxDatatypes.Scalar.newBuilder().setType(MysqlxDatatypes.Scalar.Type.V_SINT).setVSignedInt(l).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxDatatypes.Scalar scalarOf(String str) {
/* 185 */     MysqlxDatatypes.Scalar.String sstr = MysqlxDatatypes.Scalar.String.newBuilder().setValue(ByteString.copyFromUtf8(str)).build();
/* 186 */     return MysqlxDatatypes.Scalar.newBuilder().setType(MysqlxDatatypes.Scalar.Type.V_STRING).setVString(sstr).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxDatatypes.Scalar scalarOf(byte[] bytes) {
/* 197 */     MysqlxDatatypes.Scalar.Octets.Builder o = MysqlxDatatypes.Scalar.Octets.newBuilder().setValue(ByteString.copyFrom(bytes));
/* 198 */     return MysqlxDatatypes.Scalar.newBuilder().setType(MysqlxDatatypes.Scalar.Type.V_OCTETS).setVOctets(o).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxDatatypes.Scalar scalarOf(boolean b) {
/* 209 */     return MysqlxDatatypes.Scalar.newBuilder().setType(MysqlxDatatypes.Scalar.Type.V_BOOL).setVBool(b).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxDatatypes.Any anyOf(MysqlxDatatypes.Scalar s) {
/* 220 */     return MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.SCALAR).setScalar(s).build();
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
/*     */   public static MysqlxDatatypes.Any buildAny(String str) {
/* 232 */     MysqlxDatatypes.Scalar.String sstr = MysqlxDatatypes.Scalar.String.newBuilder().setValue(ByteString.copyFromUtf8(str)).build();
/* 233 */     MysqlxDatatypes.Scalar s = MysqlxDatatypes.Scalar.newBuilder().setType(MysqlxDatatypes.Scalar.Type.V_STRING).setVString(sstr).build();
/* 234 */     return anyOf(s);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxDatatypes.Any buildAny(boolean b) {
/* 245 */     return MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.SCALAR).setScalar(scalarOf(b)).build();
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
/*     */   public static MysqlxCrud.Collection buildCollection(String schemaName, String collectionName) {
/* 258 */     return MysqlxCrud.Collection.newBuilder().setSchema(schemaName).setName(collectionName).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxDatatypes.Scalar argObjectToScalar(Object value) {
/* 269 */     MysqlxExpr.Expr e = argObjectToExpr(value, false);
/* 270 */     if (!e.hasLiteral()) {
/* 271 */       throw new WrongArgumentException("No literal interpretation of argument: " + value);
/*     */     }
/* 273 */     return e.getLiteral();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MysqlxDatatypes.Any argObjectToScalarAny(Object value) {
/* 284 */     MysqlxDatatypes.Scalar s = argObjectToScalar(value);
/* 285 */     return MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.SCALAR).setScalar(s).build();
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
/*     */   public static MysqlxExpr.Expr argObjectToExpr(Object value, boolean allowRelationalColumns) {
/* 298 */     if (value == null) {
/* 299 */       return buildLiteralNullScalar();
/*     */     }
/*     */     
/* 302 */     Class<? extends Object> cls = (Class)value.getClass();
/*     */     
/* 304 */     if (cls == Boolean.class) {
/* 305 */       return buildLiteralScalar(((Boolean)value).booleanValue());
/*     */     }
/* 307 */     if (cls == Byte.class || cls == Short.class || cls == Integer.class || cls == Long.class || cls == BigInteger.class) {
/* 308 */       return buildLiteralScalar(((Number)value).longValue());
/*     */     }
/* 310 */     if (cls == Float.class || cls == Double.class || cls == BigDecimal.class) {
/* 311 */       return buildLiteralScalar(((Number)value).doubleValue());
/*     */     }
/* 313 */     if (cls == String.class) {
/* 314 */       return buildLiteralScalar((String)value);
/*     */     }
/* 316 */     if (cls == Character.class) {
/* 317 */       return buildLiteralScalar(((Character)value).toString());
/*     */     }
/* 319 */     if (cls == Expression.class) {
/* 320 */       return (new ExprParser(((Expression)value).getExpressionString(), allowRelationalColumns)).parse();
/*     */     }
/* 322 */     if (cls == Date.class) {
/* 323 */       return buildLiteralScalar(javaSqlDateFormat.format((Date)value));
/*     */     }
/* 325 */     if (cls == Time.class) {
/* 326 */       return buildLiteralScalar(javaSqlTimeFormat.format((Date)value));
/*     */     }
/* 328 */     if (cls == Timestamp.class) {
/* 329 */       return buildLiteralScalar(javaSqlTimestampFormat.format((Date)value));
/*     */     }
/* 331 */     if (cls == Date.class) {
/* 332 */       return buildLiteralScalar(javaUtilDateFormat.format((Date)value));
/*     */     }
/* 334 */     if (DbDoc.class.isAssignableFrom(cls)) {
/* 335 */       return (new ExprParser(((DbDoc)value).toString())).parse();
/*     */     }
/* 337 */     if (cls == JsonArray.class) {
/* 338 */       return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.ARRAY).setArray(MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.ARRAY).getArrayBuilder()
/* 339 */           .addAllValue((Iterable)((JsonArray)value).stream().map(f -> argObjectToExpr(f, true)).collect(Collectors.toList()))).build();
/*     */     }
/* 341 */     if (cls == JsonString.class) {
/* 342 */       return buildLiteralScalar(((JsonString)value).getString());
/*     */     }
/* 344 */     if (cls == JsonNumber.class) {
/* 345 */       return buildLiteralScalar(((JsonNumber)value).getInteger().intValue());
/*     */     }
/*     */ 
/*     */     
/* 349 */     throw new FeatureNotAvailableException("Can not create an expression from " + cls);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\ExprUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */