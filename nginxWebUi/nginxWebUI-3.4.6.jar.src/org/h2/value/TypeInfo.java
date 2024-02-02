/*      */ package org.h2.value;
/*      */ 
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.Set;
/*      */ import org.h2.api.IntervalQualifier;
/*      */ import org.h2.message.DbException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TypeInfo
/*      */   extends ExtTypeInfo
/*      */   implements Typed
/*      */ {
/*      */   static {
/*  221 */     TypeInfo[] arrayOfTypeInfo = new TypeInfo[42];
/*  222 */   } public static final TypeInfo TYPE_UNKNOWN = new TypeInfo(-1); public static final TypeInfo TYPE_NULL; public static final TypeInfo TYPE_CHAR; public static final TypeInfo TYPE_VARCHAR; public static final TypeInfo TYPE_VARCHAR_IGNORECASE; public static final TypeInfo TYPE_CLOB; public static final TypeInfo TYPE_BINARY; public static final TypeInfo TYPE_VARBINARY; public static final TypeInfo TYPE_BLOB; public static final TypeInfo TYPE_BOOLEAN; public static final TypeInfo TYPE_TINYINT; public static final TypeInfo TYPE_SMALLINT; public static final TypeInfo TYPE_INTEGER; public static final TypeInfo TYPE_BIGINT;
/*      */   static {
/*  224 */     arrayOfTypeInfo[0] = TYPE_NULL = new TypeInfo(0);
/*      */     
/*  226 */     arrayOfTypeInfo[1] = TYPE_CHAR = new TypeInfo(1, -1L);
/*  227 */     arrayOfTypeInfo[2] = TYPE_VARCHAR = new TypeInfo(2);
/*  228 */     arrayOfTypeInfo[3] = TYPE_CLOB = new TypeInfo(3);
/*  229 */     arrayOfTypeInfo[4] = TYPE_VARCHAR_IGNORECASE = new TypeInfo(4);
/*      */     
/*  231 */     arrayOfTypeInfo[5] = TYPE_BINARY = new TypeInfo(5, -1L);
/*  232 */     arrayOfTypeInfo[6] = TYPE_VARBINARY = new TypeInfo(6);
/*  233 */     arrayOfTypeInfo[7] = TYPE_BLOB = new TypeInfo(7);
/*      */     
/*  235 */     arrayOfTypeInfo[8] = TYPE_BOOLEAN = new TypeInfo(8);
/*      */     
/*  237 */     arrayOfTypeInfo[9] = TYPE_TINYINT = new TypeInfo(9);
/*  238 */     arrayOfTypeInfo[10] = TYPE_SMALLINT = new TypeInfo(10);
/*  239 */     arrayOfTypeInfo[11] = TYPE_INTEGER = new TypeInfo(11);
/*  240 */     arrayOfTypeInfo[12] = TYPE_BIGINT = new TypeInfo(12);
/*  241 */   } public static final TypeInfo TYPE_NUMERIC_SCALE_0 = new TypeInfo(13, 100000L, 0, null);
/*  242 */   public static final TypeInfo TYPE_NUMERIC_BIGINT = new TypeInfo(13, 19L, 0, null); public static final TypeInfo TYPE_NUMERIC_FLOATING_POINT; public static final TypeInfo TYPE_REAL; public static final TypeInfo TYPE_DOUBLE; public static final TypeInfo TYPE_DECFLOAT; static {
/*  243 */     arrayOfTypeInfo[13] = TYPE_NUMERIC_FLOATING_POINT = new TypeInfo(13, 100000L, 50000, null);
/*      */     
/*  245 */     arrayOfTypeInfo[14] = TYPE_REAL = new TypeInfo(14);
/*  246 */     arrayOfTypeInfo[15] = TYPE_DOUBLE = new TypeInfo(15);
/*  247 */     arrayOfTypeInfo[16] = TYPE_DECFLOAT = new TypeInfo(16);
/*  248 */   } public static final TypeInfo TYPE_DECFLOAT_BIGINT = new TypeInfo(16, 19L); public static final TypeInfo TYPE_DATE; public static final TypeInfo TYPE_TIME; public static final TypeInfo TYPE_TIME_TZ; public static final TypeInfo TYPE_TIMESTAMP; public static final TypeInfo TYPE_TIMESTAMP_TZ; public static final TypeInfo TYPE_INTERVAL_DAY; public static final TypeInfo TYPE_INTERVAL_YEAR_TO_MONTH; public static final TypeInfo TYPE_INTERVAL_DAY_TO_SECOND; public static final TypeInfo TYPE_INTERVAL_HOUR_TO_SECOND; public static final TypeInfo TYPE_JAVA_OBJECT;
/*      */   static {
/*  250 */     arrayOfTypeInfo[17] = TYPE_DATE = new TypeInfo(17);
/*  251 */     arrayOfTypeInfo[18] = TYPE_TIME = new TypeInfo(18, 9);
/*  252 */     arrayOfTypeInfo[19] = TYPE_TIME_TZ = new TypeInfo(19, 9);
/*  253 */     arrayOfTypeInfo[20] = TYPE_TIMESTAMP = new TypeInfo(20, 9);
/*  254 */     arrayOfTypeInfo[21] = TYPE_TIMESTAMP_TZ = new TypeInfo(21, 9);
/*      */     
/*  256 */     for (byte b = 22; b <= 34; b++) {
/*  257 */       arrayOfTypeInfo[b] = new TypeInfo(b, 18L, 
/*  258 */           IntervalQualifier.valueOf(b - 22).hasSeconds() ? 9 : -1, null);
/*      */     }
/*      */     
/*  261 */     TYPE_INTERVAL_DAY = arrayOfTypeInfo[24];
/*  262 */     TYPE_INTERVAL_YEAR_TO_MONTH = arrayOfTypeInfo[28];
/*  263 */     TYPE_INTERVAL_DAY_TO_SECOND = arrayOfTypeInfo[31];
/*  264 */     TYPE_INTERVAL_HOUR_TO_SECOND = arrayOfTypeInfo[33];
/*      */     
/*  266 */     arrayOfTypeInfo[35] = TYPE_JAVA_OBJECT = new TypeInfo(35);
/*  267 */     arrayOfTypeInfo[36] = TYPE_ENUM_UNDEFINED = new TypeInfo(36);
/*  268 */     arrayOfTypeInfo[37] = TYPE_GEOMETRY = new TypeInfo(37);
/*  269 */     arrayOfTypeInfo[38] = TYPE_JSON = new TypeInfo(38);
/*  270 */     arrayOfTypeInfo[39] = TYPE_UUID = new TypeInfo(39);
/*      */     
/*  272 */     arrayOfTypeInfo[40] = TYPE_ARRAY_UNKNOWN = new TypeInfo(40);
/*  273 */     arrayOfTypeInfo[41] = TYPE_ROW_EMPTY = new TypeInfo(41, -1L, -1, new ExtTypeInfoRow(new LinkedHashMap<>()));
/*      */     
/*  275 */     TYPE_INFOS_BY_VALUE_TYPE = arrayOfTypeInfo;
/*      */   }
/*      */   public static final TypeInfo TYPE_ENUM_UNDEFINED; public static final TypeInfo TYPE_GEOMETRY; public static final TypeInfo TYPE_JSON; public static final TypeInfo TYPE_UUID;
/*      */   public static final TypeInfo TYPE_ARRAY_UNKNOWN;
/*      */   public static final TypeInfo TYPE_ROW_EMPTY;
/*      */   private static final TypeInfo[] TYPE_INFOS_BY_VALUE_TYPE;
/*      */   private final int valueType;
/*      */   private final long precision;
/*      */   private final int scale;
/*      */   private final ExtTypeInfo extTypeInfo;
/*      */   
/*      */   public static TypeInfo getTypeInfo(int paramInt) {
/*  287 */     if (paramInt == -1) {
/*  288 */       throw DbException.get(50004, "?");
/*      */     }
/*  290 */     if (paramInt >= 0 && paramInt < 42) {
/*  291 */       TypeInfo typeInfo = TYPE_INFOS_BY_VALUE_TYPE[paramInt];
/*  292 */       if (typeInfo != null) {
/*  293 */         return typeInfo;
/*      */       }
/*      */     } 
/*  296 */     return TYPE_NULL;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static TypeInfo getTypeInfo(int paramInt1, long paramLong, int paramInt2, ExtTypeInfo paramExtTypeInfo) {
/*  314 */     switch (paramInt1) {
/*      */       case 0:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       case 17:
/*      */       case 39:
/*  323 */         return TYPE_INFOS_BY_VALUE_TYPE[paramInt1];
/*      */       case -1:
/*  325 */         return TYPE_UNKNOWN;
/*      */       case 1:
/*  327 */         if (paramLong < 1L) {
/*  328 */           return TYPE_CHAR;
/*      */         }
/*  330 */         if (paramLong > 1048576L) {
/*  331 */           paramLong = 1048576L;
/*      */         }
/*  333 */         return new TypeInfo(1, paramLong);
/*      */       case 2:
/*  335 */         if (paramLong < 1L || paramLong >= 1048576L) {
/*  336 */           if (paramLong != 0L) {
/*  337 */             return TYPE_VARCHAR;
/*      */           }
/*  339 */           paramLong = 1L;
/*      */         } 
/*  341 */         return new TypeInfo(2, paramLong);
/*      */       case 3:
/*  343 */         if (paramLong < 1L) {
/*  344 */           return TYPE_CLOB;
/*      */         }
/*  346 */         return new TypeInfo(3, paramLong);
/*      */       case 4:
/*  348 */         if (paramLong < 1L || paramLong >= 1048576L) {
/*  349 */           if (paramLong != 0L) {
/*  350 */             return TYPE_VARCHAR_IGNORECASE;
/*      */           }
/*  352 */           paramLong = 1L;
/*      */         } 
/*  354 */         return new TypeInfo(4, paramLong);
/*      */       case 5:
/*  356 */         if (paramLong < 1L) {
/*  357 */           return TYPE_BINARY;
/*      */         }
/*  359 */         if (paramLong > 1048576L) {
/*  360 */           paramLong = 1048576L;
/*      */         }
/*  362 */         return new TypeInfo(5, paramLong);
/*      */       case 6:
/*  364 */         if (paramLong < 1L || paramLong >= 1048576L) {
/*  365 */           if (paramLong != 0L) {
/*  366 */             return TYPE_VARBINARY;
/*      */           }
/*  368 */           paramLong = 1L;
/*      */         } 
/*  370 */         return new TypeInfo(6, paramLong);
/*      */       case 7:
/*  372 */         if (paramLong < 1L) {
/*  373 */           return TYPE_BLOB;
/*      */         }
/*  375 */         return new TypeInfo(7, paramLong);
/*      */       case 13:
/*  377 */         if (paramLong < 1L) {
/*  378 */           paramLong = -1L;
/*  379 */         } else if (paramLong > 100000L) {
/*  380 */           paramLong = 100000L;
/*      */         } 
/*  382 */         if (paramInt2 < 0) {
/*  383 */           paramInt2 = -1;
/*  384 */         } else if (paramInt2 > 100000) {
/*  385 */           paramInt2 = 100000;
/*      */         } 
/*  387 */         return new TypeInfo(13, paramLong, paramInt2, (paramExtTypeInfo instanceof ExtTypeInfoNumeric) ? paramExtTypeInfo : null);
/*      */       
/*      */       case 14:
/*  390 */         if (paramLong >= 1L && paramLong <= 24L) {
/*  391 */           return new TypeInfo(14, paramLong, -1, paramExtTypeInfo);
/*      */         }
/*  393 */         return TYPE_REAL;
/*      */       case 15:
/*  395 */         if (paramLong == 0L || (paramLong >= 25L && paramLong <= 53L)) {
/*  396 */           return new TypeInfo(15, paramLong, -1, paramExtTypeInfo);
/*      */         }
/*  398 */         return TYPE_DOUBLE;
/*      */       case 16:
/*  400 */         if (paramLong < 1L) {
/*  401 */           paramLong = -1L;
/*  402 */         } else if (paramLong >= 100000L) {
/*  403 */           return TYPE_DECFLOAT;
/*      */         } 
/*  405 */         return new TypeInfo(16, paramLong, -1, null);
/*      */       case 18:
/*  407 */         if (paramInt2 < 0) {
/*  408 */           paramInt2 = -1;
/*  409 */         } else if (paramInt2 >= 9) {
/*  410 */           return TYPE_TIME;
/*      */         } 
/*  412 */         return new TypeInfo(18, paramInt2);
/*      */       case 19:
/*  414 */         if (paramInt2 < 0) {
/*  415 */           paramInt2 = -1;
/*  416 */         } else if (paramInt2 >= 9) {
/*  417 */           return TYPE_TIME_TZ;
/*      */         } 
/*  419 */         return new TypeInfo(19, paramInt2);
/*      */       case 20:
/*  421 */         if (paramInt2 < 0) {
/*  422 */           paramInt2 = -1;
/*  423 */         } else if (paramInt2 >= 9) {
/*  424 */           return TYPE_TIMESTAMP;
/*      */         } 
/*  426 */         return new TypeInfo(20, paramInt2);
/*      */       case 21:
/*  428 */         if (paramInt2 < 0) {
/*  429 */           paramInt2 = -1;
/*  430 */         } else if (paramInt2 >= 9) {
/*  431 */           return TYPE_TIMESTAMP_TZ;
/*      */         } 
/*  433 */         return new TypeInfo(21, paramInt2);
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 28:
/*      */       case 29:
/*      */       case 30:
/*      */       case 32:
/*  443 */         if (paramLong < 1L) {
/*  444 */           paramLong = -1L;
/*  445 */         } else if (paramLong > 18L) {
/*  446 */           paramLong = 18L;
/*      */         } 
/*  448 */         return new TypeInfo(paramInt1, paramLong);
/*      */       case 27:
/*      */       case 31:
/*      */       case 33:
/*      */       case 34:
/*  453 */         if (paramLong < 1L) {
/*  454 */           paramLong = -1L;
/*  455 */         } else if (paramLong > 18L) {
/*  456 */           paramLong = 18L;
/*      */         } 
/*  458 */         if (paramInt2 < 0) {
/*  459 */           paramInt2 = -1;
/*  460 */         } else if (paramInt2 > 9) {
/*  461 */           paramInt2 = 9;
/*      */         } 
/*  463 */         return new TypeInfo(paramInt1, paramLong, paramInt2, null);
/*      */       case 35:
/*  465 */         if (paramLong < 1L)
/*  466 */           return TYPE_JAVA_OBJECT; 
/*  467 */         if (paramLong > 1048576L) {
/*  468 */           paramLong = 1048576L;
/*      */         }
/*  470 */         return new TypeInfo(35, paramLong);
/*      */       case 36:
/*  472 */         if (paramExtTypeInfo instanceof ExtTypeInfoEnum) {
/*  473 */           return ((ExtTypeInfoEnum)paramExtTypeInfo).getType();
/*      */         }
/*  475 */         return TYPE_ENUM_UNDEFINED;
/*      */       
/*      */       case 37:
/*  478 */         if (paramExtTypeInfo instanceof ExtTypeInfoGeometry) {
/*  479 */           return new TypeInfo(37, -1L, -1, paramExtTypeInfo);
/*      */         }
/*  481 */         return TYPE_GEOMETRY;
/*      */       
/*      */       case 38:
/*  484 */         if (paramLong < 1L)
/*  485 */           return TYPE_JSON; 
/*  486 */         if (paramLong > 1048576L) {
/*  487 */           paramLong = 1048576L;
/*      */         }
/*  489 */         return new TypeInfo(38, paramLong);
/*      */       case 40:
/*  491 */         if (!(paramExtTypeInfo instanceof TypeInfo)) {
/*  492 */           throw new IllegalArgumentException();
/*      */         }
/*  494 */         if (paramLong < 0L || paramLong >= 65536L) {
/*  495 */           paramLong = -1L;
/*      */         }
/*  497 */         return new TypeInfo(40, paramLong, -1, paramExtTypeInfo);
/*      */       case 41:
/*  499 */         if (!(paramExtTypeInfo instanceof ExtTypeInfoRow)) {
/*  500 */           throw new IllegalArgumentException();
/*      */         }
/*  502 */         return new TypeInfo(41, -1L, -1, paramExtTypeInfo);
/*      */     } 
/*  504 */     return TYPE_NULL;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static TypeInfo getHigherType(Typed[] paramArrayOfTyped) {
/*      */     TypeInfo typeInfo;
/*  515 */     int i = paramArrayOfTyped.length;
/*      */     
/*  517 */     if (i == 0) {
/*  518 */       typeInfo = TYPE_NULL;
/*      */     } else {
/*  520 */       typeInfo = paramArrayOfTyped[0].getType();
/*  521 */       boolean bool1 = false, bool2 = false;
/*  522 */       switch (typeInfo.getValueType()) {
/*      */         case -1:
/*  524 */           bool1 = true;
/*      */           break;
/*      */         case 0:
/*  527 */           bool2 = true; break;
/*      */       } 
/*  529 */       for (byte b = 1; b < i; b++) {
/*  530 */         TypeInfo typeInfo1 = paramArrayOfTyped[b].getType();
/*  531 */         switch (typeInfo1.getValueType()) {
/*      */           case -1:
/*  533 */             bool1 = true;
/*      */             break;
/*      */           case 0:
/*  536 */             bool2 = true;
/*      */             break;
/*      */           default:
/*  539 */             typeInfo = getHigherType(typeInfo, typeInfo1); break;
/*      */         } 
/*      */       } 
/*  542 */       if (typeInfo.getValueType() <= 0 && bool1) {
/*  543 */         throw DbException.get(50004, bool2 ? "NULL, ?" : "?");
/*      */       }
/*      */     } 
/*  546 */     return typeInfo;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static TypeInfo getHigherType(TypeInfo paramTypeInfo1, TypeInfo paramTypeInfo2) {
/*      */     int k;
/*      */     long l1, l2, l3;
/*  561 */     int m, n, i1, i = paramTypeInfo1.getValueType(), j = paramTypeInfo2.getValueType();
/*  562 */     if (i == j) {
/*  563 */       if (i == -1) {
/*  564 */         throw DbException.get(50004, "?, ?");
/*      */       }
/*  566 */       k = i;
/*      */     } else {
/*  568 */       if (i < j) {
/*  569 */         int i2 = i;
/*  570 */         i = j;
/*  571 */         j = i2;
/*  572 */         TypeInfo typeInfo = paramTypeInfo1;
/*  573 */         paramTypeInfo1 = paramTypeInfo2;
/*  574 */         paramTypeInfo2 = typeInfo;
/*      */       } 
/*  576 */       if (i == -1) {
/*  577 */         if (j == 0) {
/*  578 */           throw DbException.get(50004, "?, NULL");
/*      */         }
/*  580 */         return paramTypeInfo2;
/*  581 */       }  if (j == -1) {
/*  582 */         if (i == 0) {
/*  583 */           throw DbException.get(50004, "NULL, ?");
/*      */         }
/*  585 */         return paramTypeInfo1;
/*      */       } 
/*  587 */       if (j == 0) {
/*  588 */         return paramTypeInfo1;
/*      */       }
/*  590 */       k = Value.getHigherOrderKnown(i, j);
/*      */     } 
/*      */     
/*  593 */     switch (k) {
/*      */       case 13:
/*  595 */         paramTypeInfo1 = paramTypeInfo1.toNumericType();
/*  596 */         paramTypeInfo2 = paramTypeInfo2.toNumericType();
/*  597 */         l2 = paramTypeInfo1.getPrecision(); l3 = paramTypeInfo2.getPrecision();
/*  598 */         m = paramTypeInfo1.getScale(); n = paramTypeInfo2.getScale();
/*  599 */         if (m < n) {
/*  600 */           l2 += (n - m);
/*  601 */           i1 = n;
/*      */         } else {
/*  603 */           l3 += (m - n);
/*  604 */           i1 = m;
/*      */         } 
/*  606 */         return getTypeInfo(13, Math.max(l2, l3), i1, (ExtTypeInfo)null);
/*      */       
/*      */       case 14:
/*      */       case 15:
/*  610 */         l1 = -1L;
/*      */         break;
/*      */       case 40:
/*  613 */         return getHigherArray(paramTypeInfo1, paramTypeInfo2, dimensions(paramTypeInfo1), dimensions(paramTypeInfo2));
/*      */       case 41:
/*  615 */         return getHigherRow(paramTypeInfo1, paramTypeInfo2);
/*      */       default:
/*  617 */         l1 = Math.max(paramTypeInfo1.getPrecision(), paramTypeInfo2.getPrecision()); break;
/*      */     } 
/*  619 */     ExtTypeInfo extTypeInfo = paramTypeInfo1.extTypeInfo;
/*  620 */     return getTypeInfo(k, l1, 
/*      */         
/*  622 */         Math.max(paramTypeInfo1.getScale(), paramTypeInfo2.getScale()), (k == i && extTypeInfo != null) ? extTypeInfo : ((k == j) ? paramTypeInfo2.extTypeInfo : null));
/*      */   }
/*      */ 
/*      */   
/*      */   private static int dimensions(TypeInfo paramTypeInfo) {
/*      */     byte b;
/*  628 */     for (b = 0; paramTypeInfo.getValueType() == 40; b++) {
/*  629 */       paramTypeInfo = (TypeInfo)paramTypeInfo.extTypeInfo;
/*      */     }
/*  631 */     return b;
/*      */   }
/*      */   
/*      */   private static TypeInfo getHigherArray(TypeInfo paramTypeInfo1, TypeInfo paramTypeInfo2, int paramInt1, int paramInt2) {
/*      */     long l;
/*  636 */     if (paramInt1 > paramInt2) {
/*  637 */       paramInt1--;
/*  638 */       l = Math.max(paramTypeInfo1.getPrecision(), 1L);
/*  639 */       paramTypeInfo1 = (TypeInfo)paramTypeInfo1.extTypeInfo;
/*  640 */     } else if (paramInt1 < paramInt2) {
/*  641 */       paramInt2--;
/*  642 */       l = Math.max(1L, paramTypeInfo2.getPrecision());
/*  643 */       paramTypeInfo2 = (TypeInfo)paramTypeInfo2.extTypeInfo;
/*  644 */     } else if (paramInt1 > 0) {
/*  645 */       paramInt1--;
/*  646 */       paramInt2--;
/*  647 */       l = Math.max(paramTypeInfo1.getPrecision(), paramTypeInfo2.getPrecision());
/*  648 */       paramTypeInfo1 = (TypeInfo)paramTypeInfo1.extTypeInfo;
/*  649 */       paramTypeInfo2 = (TypeInfo)paramTypeInfo2.extTypeInfo;
/*      */     } else {
/*  651 */       return getHigherType(paramTypeInfo1, paramTypeInfo2);
/*      */     } 
/*  653 */     return getTypeInfo(40, l, 0, getHigherArray(paramTypeInfo1, paramTypeInfo2, paramInt1, paramInt2));
/*      */   }
/*      */   
/*      */   private static TypeInfo getHigherRow(TypeInfo paramTypeInfo1, TypeInfo paramTypeInfo2) {
/*  657 */     if (paramTypeInfo1.getValueType() != 41) {
/*  658 */       paramTypeInfo1 = typeToRow(paramTypeInfo1);
/*      */     }
/*  660 */     if (paramTypeInfo2.getValueType() != 41) {
/*  661 */       paramTypeInfo2 = typeToRow(paramTypeInfo2);
/*      */     }
/*  663 */     ExtTypeInfoRow extTypeInfoRow1 = (ExtTypeInfoRow)paramTypeInfo1.getExtTypeInfo(), extTypeInfoRow2 = (ExtTypeInfoRow)paramTypeInfo2.getExtTypeInfo();
/*  664 */     if (extTypeInfoRow1.equals(extTypeInfoRow2)) {
/*  665 */       return paramTypeInfo1;
/*      */     }
/*  667 */     Set<Map.Entry<String, TypeInfo>> set1 = extTypeInfoRow1.getFields(), set2 = extTypeInfoRow2.getFields();
/*  668 */     int i = set1.size();
/*  669 */     if (set2.size() != i) {
/*  670 */       throw DbException.get(21002);
/*      */     }
/*  672 */     LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>((int)Math.ceil(i / 0.75D));
/*  673 */     for (Iterator<Map.Entry<String, TypeInfo>> iterator1 = set1.iterator(), iterator2 = set2.iterator(); iterator1.hasNext(); ) {
/*  674 */       Map.Entry entry = iterator1.next();
/*  675 */       linkedHashMap.put(entry.getKey(), getHigherType((TypeInfo)entry.getValue(), (TypeInfo)((Map.Entry)iterator2.next()).getValue()));
/*      */     } 
/*  677 */     return getTypeInfo(41, 0L, 0, new ExtTypeInfoRow((LinkedHashMap)linkedHashMap));
/*      */   }
/*      */   
/*      */   private static TypeInfo typeToRow(TypeInfo paramTypeInfo) {
/*  681 */     LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>(2);
/*  682 */     linkedHashMap.put("C1", paramTypeInfo);
/*  683 */     return getTypeInfo(41, 0L, 0, new ExtTypeInfoRow((LinkedHashMap)linkedHashMap));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean areSameTypes(TypeInfo paramTypeInfo1, TypeInfo paramTypeInfo2) {
/*      */     while (true) {
/*  698 */       int i = paramTypeInfo1.getValueType();
/*  699 */       if (i != paramTypeInfo2.getValueType()) {
/*  700 */         return false;
/*      */       }
/*  702 */       ExtTypeInfo extTypeInfo1 = paramTypeInfo1.getExtTypeInfo(), extTypeInfo2 = paramTypeInfo2.getExtTypeInfo();
/*  703 */       if (i != 40) {
/*  704 */         return Objects.equals(extTypeInfo1, extTypeInfo2);
/*      */       }
/*  706 */       paramTypeInfo1 = (TypeInfo)extTypeInfo1;
/*  707 */       paramTypeInfo2 = (TypeInfo)extTypeInfo2;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void checkComparable(TypeInfo paramTypeInfo1, TypeInfo paramTypeInfo2) {
/*  723 */     if (!areComparable(paramTypeInfo1, paramTypeInfo2)) {
/*  724 */       throw DbException.get(90110, new String[] { paramTypeInfo1.getTraceSQL(), paramTypeInfo2.getTraceSQL() });
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean areComparable(TypeInfo paramTypeInfo1, TypeInfo paramTypeInfo2) {
/*  738 */     int i = (paramTypeInfo1 = paramTypeInfo1.unwrapRow()).getValueType(), j = (paramTypeInfo2 = paramTypeInfo2.unwrapRow()).getValueType();
/*  739 */     if (i > j) {
/*  740 */       int k = i;
/*  741 */       i = j;
/*  742 */       j = k;
/*  743 */       TypeInfo typeInfo = paramTypeInfo1;
/*  744 */       paramTypeInfo1 = paramTypeInfo2;
/*  745 */       paramTypeInfo2 = typeInfo;
/*      */     } 
/*  747 */     if (i <= 0) {
/*  748 */       return true;
/*      */     }
/*  750 */     if (i == j) {
/*  751 */       Set<Map.Entry<String, TypeInfo>> set1; Set<Map.Entry<String, TypeInfo>> set2; int k; Iterator<Map.Entry<String, TypeInfo>> iterator1; Iterator<Map.Entry<String, TypeInfo>> iterator2; switch (i) {
/*      */         case 40:
/*  753 */           return areComparable((TypeInfo)paramTypeInfo1.getExtTypeInfo(), (TypeInfo)paramTypeInfo2.getExtTypeInfo());
/*      */         case 41:
/*  755 */           set1 = ((ExtTypeInfoRow)paramTypeInfo1.getExtTypeInfo()).getFields();
/*  756 */           set2 = ((ExtTypeInfoRow)paramTypeInfo2.getExtTypeInfo()).getFields();
/*  757 */           k = set1.size();
/*  758 */           if (set2.size() != k) {
/*  759 */             return false;
/*      */           }
/*  761 */           iterator1 = set1.iterator(); iterator2 = set2.iterator();
/*  762 */           while (iterator1.hasNext()) {
/*  763 */             if (!areComparable((TypeInfo)((Map.Entry)iterator1.next()).getValue(), (TypeInfo)((Map.Entry)iterator2.next()).getValue())) {
/*  764 */               return false;
/*      */             }
/*      */           } 
/*      */           break;
/*      */       } 
/*      */       
/*  770 */       return true;
/*      */     } 
/*      */     
/*  773 */     byte b1 = Value.GROUPS[i], b2 = Value.GROUPS[j];
/*  774 */     if (b1 == b2) {
/*  775 */       switch (b1)
/*      */       { default:
/*  777 */           return true;
/*      */         case 5:
/*  779 */           return (i != 17 || (j != 18 && j != 19));
/*      */         case 8:
/*      */         case 9:
/*  782 */           break; }  return false;
/*      */     } 
/*      */     
/*  785 */     switch (b1) {
/*      */       case 1:
/*  787 */         switch (b2) {
/*      */           case 4:
/*      */           case 5:
/*      */           case 6:
/*      */           case 7:
/*  792 */             return true;
/*      */           case 8:
/*  794 */             switch (j) {
/*      */               case 36:
/*      */               case 37:
/*      */               case 38:
/*      */               case 39:
/*  799 */                 return true;
/*      */             } 
/*  801 */             return false;
/*      */         } 
/*      */         
/*  804 */         return false;
/*      */       
/*      */       case 2:
/*  807 */         switch (j) {
/*      */           case 35:
/*      */           case 37:
/*      */           case 38:
/*      */           case 39:
/*  812 */             return true;
/*      */         } 
/*  814 */         return false;
/*      */     } 
/*      */     
/*  817 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean haveSameOrdering(TypeInfo paramTypeInfo1, TypeInfo paramTypeInfo2) {
/*  830 */     int i = (paramTypeInfo1 = paramTypeInfo1.unwrapRow()).getValueType(), j = (paramTypeInfo2 = paramTypeInfo2.unwrapRow()).getValueType();
/*  831 */     if (i > j) {
/*  832 */       int k = i;
/*  833 */       i = j;
/*  834 */       j = k;
/*  835 */       TypeInfo typeInfo = paramTypeInfo1;
/*  836 */       paramTypeInfo1 = paramTypeInfo2;
/*  837 */       paramTypeInfo2 = typeInfo;
/*      */     } 
/*  839 */     if (i <= 0) {
/*  840 */       return true;
/*      */     }
/*  842 */     if (i == j) {
/*  843 */       Set<Map.Entry<String, TypeInfo>> set1; Set<Map.Entry<String, TypeInfo>> set2; int k; Iterator<Map.Entry<String, TypeInfo>> iterator1; Iterator<Map.Entry<String, TypeInfo>> iterator2; switch (i) {
/*      */         case 40:
/*  845 */           return haveSameOrdering((TypeInfo)paramTypeInfo1.getExtTypeInfo(), (TypeInfo)paramTypeInfo2.getExtTypeInfo());
/*      */         case 41:
/*  847 */           set1 = ((ExtTypeInfoRow)paramTypeInfo1.getExtTypeInfo()).getFields();
/*  848 */           set2 = ((ExtTypeInfoRow)paramTypeInfo2.getExtTypeInfo()).getFields();
/*  849 */           k = set1.size();
/*  850 */           if (set2.size() != k) {
/*  851 */             return false;
/*      */           }
/*  853 */           iterator1 = set1.iterator(); iterator2 = set2.iterator();
/*  854 */           while (iterator1.hasNext()) {
/*  855 */             if (!haveSameOrdering((TypeInfo)((Map.Entry)iterator1.next()).getValue(), (TypeInfo)((Map.Entry)iterator2.next()).getValue())) {
/*  856 */               return false;
/*      */             }
/*      */           } 
/*      */           break;
/*      */       } 
/*      */       
/*  862 */       return true;
/*      */     } 
/*      */     
/*  865 */     byte b1 = Value.GROUPS[i], b2 = Value.GROUPS[j];
/*  866 */     if (b1 == b2) {
/*  867 */       switch (b1) {
/*      */         default:
/*  869 */           return true;
/*      */         case 1:
/*  871 */           return (((i == 4) ? true : false) == ((j == 4) ? true : false));
/*      */         case 5:
/*  873 */           switch (i) {
/*      */             case 17:
/*  875 */               return (j == 20 || j == 21);
/*      */             case 18:
/*      */             case 19:
/*  878 */               return (j == 18 || j == 19);
/*      */           } 
/*  880 */           return true;
/*      */         case 8:
/*      */         case 9:
/*      */           break;
/*  884 */       }  return false;
/*      */     } 
/*      */     
/*  887 */     if (b1 == 2) {
/*  888 */       switch (j) {
/*      */         case 35:
/*      */         case 37:
/*      */         case 38:
/*      */         case 39:
/*  893 */           return true;
/*      */       } 
/*  895 */       return false;
/*      */     } 
/*      */     
/*  898 */     return false;
/*      */   }
/*      */   
/*      */   private TypeInfo(int paramInt) {
/*  902 */     this.valueType = paramInt;
/*  903 */     this.precision = -1L;
/*  904 */     this.scale = -1;
/*  905 */     this.extTypeInfo = null;
/*      */   }
/*      */   
/*      */   private TypeInfo(int paramInt, long paramLong) {
/*  909 */     this.valueType = paramInt;
/*  910 */     this.precision = paramLong;
/*  911 */     this.scale = -1;
/*  912 */     this.extTypeInfo = null;
/*      */   }
/*      */   
/*      */   private TypeInfo(int paramInt1, int paramInt2) {
/*  916 */     this.valueType = paramInt1;
/*  917 */     this.precision = -1L;
/*  918 */     this.scale = paramInt2;
/*  919 */     this.extTypeInfo = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeInfo(int paramInt1, long paramLong, int paramInt2, ExtTypeInfo paramExtTypeInfo) {
/*  935 */     this.valueType = paramInt1;
/*  936 */     this.precision = paramLong;
/*  937 */     this.scale = paramInt2;
/*  938 */     this.extTypeInfo = paramExtTypeInfo;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeInfo getType() {
/*  948 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getValueType() {
/*  957 */     return this.valueType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getPrecision() {
/*      */     byte b;
/*  966 */     switch (this.valueType) {
/*      */       case -1:
/*  968 */         return -1L;
/*      */       case 0:
/*  970 */         return 1L;
/*      */       case 1:
/*      */       case 5:
/*  973 */         return (this.precision >= 0L) ? this.precision : 1L;
/*      */       case 2:
/*      */       case 4:
/*      */       case 6:
/*      */       case 35:
/*      */       case 36:
/*      */       case 37:
/*      */       case 38:
/*  981 */         return (this.precision >= 0L) ? this.precision : 1048576L;
/*      */       case 3:
/*      */       case 7:
/*  984 */         return (this.precision >= 0L) ? this.precision : Long.MAX_VALUE;
/*      */       case 8:
/*  986 */         return 1L;
/*      */       case 9:
/*  988 */         return 8L;
/*      */       case 10:
/*  990 */         return 16L;
/*      */       case 11:
/*  992 */         return 32L;
/*      */       case 12:
/*  994 */         return 64L;
/*      */       case 13:
/*  996 */         return (this.precision >= 0L) ? this.precision : 100000L;
/*      */       case 14:
/*  998 */         return 24L;
/*      */       case 15:
/* 1000 */         return 53L;
/*      */       case 16:
/* 1002 */         return (this.precision >= 0L) ? this.precision : 100000L;
/*      */       case 17:
/* 1004 */         return 10L;
/*      */       case 18:
/* 1006 */         b = (this.scale >= 0) ? this.scale : 0;
/* 1007 */         return !b ? 8L : (9 + b);
/*      */       
/*      */       case 19:
/* 1010 */         b = (this.scale >= 0) ? this.scale : 0;
/* 1011 */         return (b == 0) ? 14L : (15 + b);
/*      */       
/*      */       case 20:
/* 1014 */         b = (this.scale >= 0) ? this.scale : 6;
/* 1015 */         return (b == 0) ? 19L : (20 + b);
/*      */       
/*      */       case 21:
/* 1018 */         b = (this.scale >= 0) ? this.scale : 6;
/* 1019 */         return (b == 0) ? 25L : (26 + b);
/*      */       
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 29:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/* 1034 */         return (this.precision >= 0L) ? this.precision : 2L;
/*      */       case 41:
/* 1036 */         return 2147483647L;
/*      */       case 39:
/* 1038 */         return 16L;
/*      */       case 40:
/* 1040 */         return (this.precision >= 0L) ? this.precision : 65536L;
/*      */     } 
/* 1042 */     return this.precision;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getDeclaredPrecision() {
/* 1054 */     return this.precision;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getScale() {
/* 1063 */     switch (this.valueType) {
/*      */       case -1:
/* 1065 */         return -1;
/*      */       case 0:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       case 14:
/*      */       case 15:
/*      */       case 16:
/*      */       case 17:
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 28:
/*      */       case 29:
/*      */       case 30:
/*      */       case 32:
/*      */       case 35:
/*      */       case 36:
/*      */       case 37:
/*      */       case 38:
/*      */       case 39:
/*      */       case 40:
/*      */       case 41:
/* 1099 */         return 0;
/*      */       case 13:
/* 1101 */         return (this.scale >= 0) ? this.scale : 0;
/*      */       case 18:
/*      */       case 19:
/* 1104 */         return (this.scale >= 0) ? this.scale : 0;
/*      */       case 20:
/*      */       case 21:
/* 1107 */         return (this.scale >= 0) ? this.scale : 6;
/*      */       case 27:
/*      */       case 31:
/*      */       case 33:
/*      */       case 34:
/* 1112 */         return (this.scale >= 0) ? this.scale : 6;
/*      */     } 
/* 1114 */     return this.scale;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDeclaredScale() {
/* 1125 */     return this.scale;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDisplaySize() {
/*      */     byte b;
/* 1134 */     switch (this.valueType)
/*      */     
/*      */     { default:
/* 1137 */         return -1;
/*      */       case 0:
/* 1139 */         return 4;
/*      */       case 1:
/* 1141 */         return (this.precision >= 0L) ? (int)this.precision : 1;
/*      */       case 2:
/*      */       case 4:
/*      */       case 38:
/* 1145 */         return (this.precision >= 0L) ? (int)this.precision : 1048576;
/*      */       case 3:
/* 1147 */         return (this.precision >= 0L && this.precision <= 2147483647L) ? (int)this.precision : Integer.MAX_VALUE;
/*      */       case 5:
/* 1149 */         return (this.precision >= 0L) ? ((int)this.precision * 2) : 2;
/*      */       case 6:
/*      */       case 35:
/* 1152 */         return (this.precision >= 0L) ? ((int)this.precision * 2) : 2097152;
/*      */       case 7:
/* 1154 */         return (this.precision >= 0L && this.precision <= 1073741823L) ? ((int)this.precision * 2) : Integer.MAX_VALUE;
/*      */       case 8:
/* 1156 */         return 5;
/*      */       case 9:
/* 1158 */         return 4;
/*      */       case 10:
/* 1160 */         return 6;
/*      */       case 11:
/* 1162 */         return 11;
/*      */       case 12:
/* 1164 */         return 20;
/*      */       case 13:
/* 1166 */         return (this.precision >= 0L) ? ((int)this.precision + 2) : 100002;
/*      */       case 14:
/* 1168 */         return 15;
/*      */       case 15:
/* 1170 */         return 24;
/*      */       case 16:
/* 1172 */         return (this.precision >= 0L) ? ((int)this.precision + 12) : 100012;
/*      */       case 17:
/* 1174 */         return 10;
/*      */       case 18:
/* 1176 */         b = (this.scale >= 0) ? this.scale : 0;
/* 1177 */         return !b ? 8 : (9 + b);
/*      */       
/*      */       case 19:
/* 1180 */         b = (this.scale >= 0) ? this.scale : 0;
/* 1181 */         return (b == 0) ? 14 : (15 + b);
/*      */       
/*      */       case 20:
/* 1184 */         b = (this.scale >= 0) ? this.scale : 6;
/* 1185 */         return (b == 0) ? 19 : (20 + b);
/*      */       
/*      */       case 21:
/* 1188 */         b = (this.scale >= 0) ? this.scale : 6;
/* 1189 */         return (b == 0) ? 25 : (26 + b);
/*      */       
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 29:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/* 1204 */         return ValueInterval.getDisplaySize(this.valueType, (this.precision >= 0L) ? (int)this.precision : 2, (this.scale >= 0) ? this.scale : 6);
/*      */ 
/*      */       
/*      */       case 37:
/*      */       case 40:
/*      */       case 41:
/* 1210 */         return Integer.MAX_VALUE;
/*      */       case 36:
/* 1212 */         return (this.extTypeInfo != null) ? (int)this.precision : 1048576;
/*      */       case 39:
/* 1214 */         break; }  return 36;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExtTypeInfo getExtTypeInfo() {
/* 1224 */     return this.extTypeInfo;
/*      */   }
/*      */   
/*      */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/*      */     boolean bool1, bool2;
/* 1229 */     switch (this.valueType)
/*      */     { case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 35:
/*      */       case 38:
/* 1239 */         paramStringBuilder.append(Value.getTypeName(this.valueType));
/* 1240 */         if (this.precision >= 0L) {
/* 1241 */           paramStringBuilder.append('(').append(this.precision).append(')');
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1341 */         return paramStringBuilder;case 13: if (this.extTypeInfo != null) { this.extTypeInfo.getSQL(paramStringBuilder, paramInt); } else { paramStringBuilder.append("NUMERIC"); }  bool1 = (this.precision >= 0L) ? true : false; bool2 = (this.scale >= 0) ? true : false; if (bool1 || bool2) { paramStringBuilder.append('(').append(bool1 ? this.precision : 100000L); if (bool2) paramStringBuilder.append(", ").append(this.scale);  paramStringBuilder.append(')'); }  return paramStringBuilder;case 14: case 15: if (this.precision < 0L) { paramStringBuilder.append(Value.getTypeName(this.valueType)); } else { paramStringBuilder.append("FLOAT"); if (this.precision > 0L) paramStringBuilder.append('(').append(this.precision).append(')');  }  return paramStringBuilder;case 16: paramStringBuilder.append("DECFLOAT"); if (this.precision >= 0L) paramStringBuilder.append('(').append(this.precision).append(')');  return paramStringBuilder;case 18: case 19: paramStringBuilder.append("TIME"); if (this.scale >= 0) paramStringBuilder.append('(').append(this.scale).append(')');  if (this.valueType == 19) paramStringBuilder.append(" WITH TIME ZONE");  return paramStringBuilder;case 20: case 21: paramStringBuilder.append("TIMESTAMP"); if (this.scale >= 0) paramStringBuilder.append('(').append(this.scale).append(')');  if (this.valueType == 21) paramStringBuilder.append(" WITH TIME ZONE");  return paramStringBuilder;case 22: case 23: case 24: case 25: case 26: case 27: case 28: case 29: case 30: case 31: case 32: case 33: case 34: IntervalQualifier.valueOf(this.valueType - 22).getTypeName(paramStringBuilder, (int)this.precision, this.scale, false); return paramStringBuilder;case 36: this.extTypeInfo.getSQL(paramStringBuilder.append("ENUM"), paramInt); return paramStringBuilder;case 37: paramStringBuilder.append("GEOMETRY"); if (this.extTypeInfo != null) this.extTypeInfo.getSQL(paramStringBuilder, paramInt);  return paramStringBuilder;case 40: if (this.extTypeInfo != null) this.extTypeInfo.getSQL(paramStringBuilder, paramInt).append(' ');  paramStringBuilder.append("ARRAY"); if (this.precision >= 0L) paramStringBuilder.append('[').append(this.precision).append(']');  return paramStringBuilder;case 41: paramStringBuilder.append("ROW"); if (this.extTypeInfo != null) this.extTypeInfo.getSQL(paramStringBuilder, paramInt);  return paramStringBuilder; }  paramStringBuilder.append(Value.getTypeName(this.valueType)); return paramStringBuilder;
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/* 1346 */     int i = 1;
/* 1347 */     i = 31 * i + this.valueType;
/* 1348 */     i = 31 * i + (int)(this.precision ^ this.precision >>> 32L);
/* 1349 */     i = 31 * i + this.scale;
/* 1350 */     i = 31 * i + ((this.extTypeInfo == null) ? 0 : this.extTypeInfo.hashCode());
/* 1351 */     return i;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object paramObject) {
/* 1356 */     if (this == paramObject) {
/* 1357 */       return true;
/*      */     }
/* 1359 */     if (paramObject == null || paramObject.getClass() != TypeInfo.class) {
/* 1360 */       return false;
/*      */     }
/* 1362 */     TypeInfo typeInfo = (TypeInfo)paramObject;
/* 1363 */     return (this.valueType == typeInfo.valueType && this.precision == typeInfo.precision && this.scale == typeInfo.scale && 
/* 1364 */       Objects.equals(this.extTypeInfo, typeInfo.extTypeInfo));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeInfo toNumericType() {
/* 1373 */     switch (this.valueType) {
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/* 1378 */         return getTypeInfo(13, getDecimalPrecision(), 0, (ExtTypeInfo)null);
/*      */       case 12:
/* 1380 */         return TYPE_NUMERIC_BIGINT;
/*      */       case 13:
/* 1382 */         return this;
/*      */ 
/*      */ 
/*      */       
/*      */       case 14:
/* 1387 */         return getTypeInfo(13, 85L, 46, (ExtTypeInfo)null);
/*      */ 
/*      */ 
/*      */       
/*      */       case 15:
/* 1392 */         return getTypeInfo(13, 634L, 325, (ExtTypeInfo)null);
/*      */     } 
/* 1394 */     return TYPE_NUMERIC_FLOATING_POINT;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeInfo toDecfloatType() {
/* 1404 */     switch (this.valueType) {
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/* 1409 */         return getTypeInfo(16, getDecimalPrecision(), 0, (ExtTypeInfo)null);
/*      */       case 12:
/* 1411 */         return TYPE_DECFLOAT_BIGINT;
/*      */       case 13:
/* 1413 */         return getTypeInfo(16, getPrecision(), 0, (ExtTypeInfo)null);
/*      */       case 14:
/* 1415 */         return getTypeInfo(16, 7L, 0, (ExtTypeInfo)null);
/*      */       case 15:
/* 1417 */         return getTypeInfo(16, 7L, 0, (ExtTypeInfo)null);
/*      */       case 16:
/* 1419 */         return this;
/*      */     } 
/* 1421 */     return TYPE_DECFLOAT;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeInfo unwrapRow() {
/* 1433 */     if (this.valueType == 41) {
/* 1434 */       Set<Map.Entry<String, TypeInfo>> set = ((ExtTypeInfoRow)this.extTypeInfo).getFields();
/* 1435 */       if (set.size() == 1) {
/* 1436 */         return ((TypeInfo)((Map.Entry)set.iterator().next()).getValue()).unwrapRow();
/*      */       }
/*      */     } 
/* 1439 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getDecimalPrecision() {
/* 1449 */     switch (this.valueType) {
/*      */       case 9:
/* 1451 */         return 3L;
/*      */       case 10:
/* 1453 */         return 5L;
/*      */       case 11:
/* 1455 */         return 10L;
/*      */       case 12:
/* 1457 */         return 19L;
/*      */       case 14:
/* 1459 */         return 7L;
/*      */       case 15:
/* 1461 */         return 17L;
/*      */     } 
/* 1463 */     return this.precision;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDeclaredTypeName() {
/*      */     TypeInfo typeInfo;
/* 1475 */     switch (this.valueType) {
/*      */       case 13:
/* 1477 */         return (this.extTypeInfo != null) ? "DECIMAL" : "NUMERIC";
/*      */       case 14:
/*      */       case 15:
/* 1480 */         if (this.extTypeInfo != null) {
/* 1481 */           return "FLOAT";
/*      */         }
/*      */         break;
/*      */       case 36:
/*      */       case 37:
/*      */       case 41:
/* 1487 */         return getSQL(0);
/*      */       case 40:
/* 1489 */         typeInfo = (TypeInfo)this.extTypeInfo;
/*      */         
/* 1491 */         return typeInfo.getSQL(new StringBuilder(), 0).append(" ARRAY").toString();
/*      */     } 
/* 1493 */     return Value.getTypeName(this.valueType);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\TypeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */