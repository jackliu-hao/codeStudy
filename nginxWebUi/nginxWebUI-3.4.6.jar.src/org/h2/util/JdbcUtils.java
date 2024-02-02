/*     */ package org.h2.util;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.ObjectStreamClass;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.Properties;
/*     */ import javax.naming.Context;
/*     */ import javax.sql.DataSource;
/*     */ import org.h2.api.JavaObjectSerializer;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.tools.SimpleResultSet;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueLob;
/*     */ import org.h2.value.ValueToObjectConverter;
/*     */ import org.h2.value.ValueUuid;
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
/*     */ public class JdbcUtils
/*     */ {
/*     */   public static JavaObjectSerializer serializer;
/*  58 */   private static final String[] DRIVERS = new String[] { "h2:", "org.h2.Driver", "Cache:", "com.intersys.jdbc.CacheDriver", "daffodilDB://", "in.co.daffodil.db.rmi.RmiDaffodilDBDriver", "daffodil", "in.co.daffodil.db.jdbc.DaffodilDBDriver", "db2:", "com.ibm.db2.jcc.DB2Driver", "derby:net:", "org.apache.derby.client.ClientAutoloadedDriver", "derby://", "org.apache.derby.client.ClientAutoloadedDriver", "derby:", "org.apache.derby.iapi.jdbc.AutoloadedDriver", "FrontBase:", "com.frontbase.jdbc.FBJDriver", "firebirdsql:", "org.firebirdsql.jdbc.FBDriver", "hsqldb:", "org.hsqldb.jdbcDriver", "informix-sqli:", "com.informix.jdbc.IfxDriver", "jtds:", "net.sourceforge.jtds.jdbc.Driver", "microsoft:", "com.microsoft.jdbc.sqlserver.SQLServerDriver", "mimer:", "com.mimer.jdbc.Driver", "mysql:", "com.mysql.cj.jdbc.Driver", "mariadb:", "org.mariadb.jdbc.Driver", "odbc:", "sun.jdbc.odbc.JdbcOdbcDriver", "oracle:", "oracle.jdbc.driver.OracleDriver", "pervasive:", "com.pervasive.jdbc.v2.Driver", "pointbase:micro:", "com.pointbase.me.jdbc.jdbcDriver", "pointbase:", "com.pointbase.jdbc.jdbcUniversalDriver", "postgresql:", "org.postgresql.Driver", "sybase:", "com.sybase.jdbc3.jdbc.SybDriver", "sqlserver:", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "teradata:", "com.ncr.teradata.TeraDriver" };
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
/*  87 */   private static final byte[] UUID_PREFIX = "¬í\000\005sr\000\016java.util.UUID¼\003÷m/\002\000\002J\000\fleastSigBitsJ\000\013mostSigBitsxp"
/*     */     
/*  89 */     .getBytes(StandardCharsets.ISO_8859_1);
/*     */ 
/*     */   
/*     */   private static boolean allowAllClasses;
/*     */ 
/*     */   
/*     */   private static HashSet<String> allowedClassNames;
/*     */   
/*  97 */   private static final ArrayList<Utils.ClassFactory> userClassFactories = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String[] allowedClassNamePrefixes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void addClassFactory(Utils.ClassFactory paramClassFactory) {
/* 111 */     userClassFactories.add(paramClassFactory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void removeClassFactory(Utils.ClassFactory paramClassFactory) {
/* 120 */     userClassFactories.remove(paramClassFactory);
/*     */   }
/*     */   
/*     */   static {
/* 124 */     String str = SysProperties.JAVA_OBJECT_SERIALIZER;
/* 125 */     if (str != null) {
/*     */       try {
/* 127 */         serializer = loadUserClass(str).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 128 */       } catch (Exception exception) {
/* 129 */         throw DbException.convert(exception);
/*     */       } 
/*     */     }
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
/*     */   public static <Z> Class<Z> loadUserClass(String paramString) {
/* 145 */     if (allowedClassNames == null) {
/*     */       
/* 147 */       String str = SysProperties.ALLOWED_CLASSES;
/* 148 */       ArrayList<String> arrayList = new ArrayList();
/* 149 */       boolean bool = false;
/* 150 */       HashSet<String> hashSet = new HashSet();
/* 151 */       for (String str1 : StringUtils.arraySplit(str, ',', true)) {
/* 152 */         if (str1.equals("*")) {
/* 153 */           bool = true;
/* 154 */         } else if (str1.endsWith("*")) {
/* 155 */           arrayList.add(str1.substring(0, str1.length() - 1));
/*     */         } else {
/* 157 */           hashSet.add(str1);
/*     */         } 
/*     */       } 
/* 160 */       allowedClassNamePrefixes = arrayList.<String>toArray(new String[0]);
/* 161 */       allowAllClasses = bool;
/* 162 */       allowedClassNames = hashSet;
/*     */     } 
/* 164 */     if (!allowAllClasses && !allowedClassNames.contains(paramString)) {
/* 165 */       boolean bool = false;
/* 166 */       for (String str : allowedClassNamePrefixes) {
/* 167 */         if (paramString.startsWith(str)) {
/* 168 */           bool = true;
/*     */           break;
/*     */         } 
/*     */       } 
/* 172 */       if (!bool) {
/* 173 */         throw DbException.get(90134, paramString);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 178 */     for (Utils.ClassFactory classFactory : userClassFactories) {
/* 179 */       if (classFactory.match(paramString)) {
/*     */         try {
/* 181 */           Class<?> clazz = classFactory.loadClass(paramString);
/* 182 */           if (clazz != null) {
/* 183 */             return (Class)clazz;
/*     */           }
/* 185 */         } catch (Exception exception) {
/* 186 */           throw DbException.get(90086, exception, new String[] { paramString });
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 193 */       return (Class)Class.forName(paramString);
/* 194 */     } catch (ClassNotFoundException classNotFoundException) {
/*     */       try {
/* 196 */         return (Class)Class.forName(paramString, true, 
/*     */             
/* 198 */             Thread.currentThread().getContextClassLoader());
/* 199 */       } catch (Exception exception) {
/* 200 */         throw DbException.get(90086, classNotFoundException, new String[] { paramString });
/*     */       }
/*     */     
/* 203 */     } catch (NoClassDefFoundError noClassDefFoundError) {
/* 204 */       throw DbException.get(90086, noClassDefFoundError, new String[] { paramString });
/*     */     }
/* 206 */     catch (Error error) {
/*     */       
/* 208 */       throw DbException.get(50000, error, new String[] { paramString });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void closeSilently(Statement paramStatement) {
/* 219 */     if (paramStatement != null) {
/*     */       try {
/* 221 */         paramStatement.close();
/* 222 */       } catch (SQLException sQLException) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void closeSilently(Connection paramConnection) {
/* 234 */     if (paramConnection != null) {
/*     */       try {
/* 236 */         paramConnection.close();
/* 237 */       } catch (SQLException sQLException) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void closeSilently(ResultSet paramResultSet) {
/* 249 */     if (paramResultSet != null) {
/*     */       try {
/* 251 */         paramResultSet.close();
/* 252 */       } catch (SQLException sQLException) {}
/*     */     }
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
/*     */   
/*     */   public static Connection getConnection(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
/* 270 */     return getConnection(paramString1, paramString2, paramString3, paramString4, null, false);
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
/*     */   
/*     */   public static Connection getConnection(String paramString1, String paramString2, String paramString3, String paramString4, NetworkConnectionInfo paramNetworkConnectionInfo, boolean paramBoolean) throws SQLException {
/* 287 */     if (paramString2.startsWith("jdbc:h2:")) {
/* 288 */       JdbcConnection jdbcConnection = new JdbcConnection(paramString2, null, paramString3, paramString4, paramBoolean);
/* 289 */       if (paramNetworkConnectionInfo != null) {
/* 290 */         jdbcConnection.getSession().setNetworkConnectionInfo(paramNetworkConnectionInfo);
/*     */       }
/* 292 */       return (Connection)jdbcConnection;
/*     */     } 
/* 294 */     if (StringUtils.isNullOrEmpty(paramString1)) {
/* 295 */       load(paramString2);
/*     */     } else {
/* 297 */       Class<?> clazz = loadUserClass(paramString1);
/*     */       try {
/* 299 */         if (Driver.class.isAssignableFrom(clazz)) {
/* 300 */           Driver driver = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 301 */           Properties properties = new Properties();
/* 302 */           if (paramString3 != null) {
/* 303 */             properties.setProperty("user", paramString3);
/*     */           }
/* 305 */           if (paramString4 != null) {
/* 306 */             properties.setProperty("password", paramString4);
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 313 */           Connection connection = driver.connect(paramString2, properties);
/* 314 */           if (connection != null) {
/* 315 */             return connection;
/*     */           }
/* 317 */           throw new SQLException("Driver " + paramString1 + " is not suitable for " + paramString2, "08001");
/* 318 */         }  if (Context.class.isAssignableFrom(clazz)) {
/* 319 */           if (!paramString2.startsWith("java:")) {
/* 320 */             throw new SQLException("Only java scheme is supported for JNDI lookups", "08001");
/*     */           }
/*     */           
/* 323 */           Context context = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 324 */           DataSource dataSource = (DataSource)context.lookup(paramString2);
/* 325 */           if (StringUtils.isNullOrEmpty(paramString3) && StringUtils.isNullOrEmpty(paramString4)) {
/* 326 */             return dataSource.getConnection();
/*     */           }
/* 328 */           return dataSource.getConnection(paramString3, paramString4);
/*     */         } 
/* 330 */       } catch (Exception exception) {
/* 331 */         throw DbException.toSQLException(exception);
/*     */       } 
/*     */     } 
/*     */     
/* 335 */     return DriverManager.getConnection(paramString2, paramString3, paramString4);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getDriver(String paramString) {
/* 346 */     if (paramString.startsWith("jdbc:")) {
/* 347 */       paramString = paramString.substring("jdbc:".length());
/* 348 */       for (byte b = 0; b < DRIVERS.length; b += 2) {
/* 349 */         String str = DRIVERS[b];
/* 350 */         if (paramString.startsWith(str)) {
/* 351 */           return DRIVERS[b + 1];
/*     */         }
/*     */       } 
/*     */     } 
/* 355 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void load(String paramString) {
/* 364 */     String str = getDriver(paramString);
/* 365 */     if (str != null) {
/* 366 */       loadUserClass(str);
/*     */     }
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
/*     */   public static byte[] serialize(Object paramObject, JavaObjectSerializer paramJavaObjectSerializer) {
/*     */     try {
/* 380 */       if (paramJavaObjectSerializer != null) {
/* 381 */         return paramJavaObjectSerializer.serialize(paramObject);
/*     */       }
/* 383 */       if (serializer != null) {
/* 384 */         return serializer.serialize(paramObject);
/*     */       }
/* 386 */       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/* 387 */       ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
/* 388 */       objectOutputStream.writeObject(paramObject);
/* 389 */       return byteArrayOutputStream.toByteArray();
/* 390 */     } catch (Throwable throwable) {
/* 391 */       throw DbException.get(90026, throwable, new String[] { throwable.toString() });
/*     */     } 
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
/*     */   public static Object deserialize(byte[] paramArrayOfbyte, JavaObjectSerializer paramJavaObjectSerializer) {
/*     */     try {
/*     */       ObjectInputStream objectInputStream;
/* 406 */       if (paramJavaObjectSerializer != null) {
/* 407 */         return paramJavaObjectSerializer.deserialize(paramArrayOfbyte);
/*     */       }
/* 409 */       if (serializer != null) {
/* 410 */         return serializer.deserialize(paramArrayOfbyte);
/*     */       }
/* 412 */       ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
/*     */       
/* 414 */       if (SysProperties.USE_THREAD_CONTEXT_CLASS_LOADER) {
/* 415 */         final ClassLoader loader = Thread.currentThread().getContextClassLoader();
/* 416 */         objectInputStream = new ObjectInputStream(byteArrayInputStream)
/*     */           {
/*     */             protected Class<?> resolveClass(ObjectStreamClass param1ObjectStreamClass) throws IOException, ClassNotFoundException
/*     */             {
/*     */               try {
/* 421 */                 return Class.forName(param1ObjectStreamClass.getName(), true, loader);
/* 422 */               } catch (ClassNotFoundException classNotFoundException) {
/* 423 */                 return super.resolveClass(param1ObjectStreamClass);
/*     */               } 
/*     */             }
/*     */           };
/*     */       } else {
/* 428 */         objectInputStream = new ObjectInputStream(byteArrayInputStream);
/*     */       } 
/* 430 */       return objectInputStream.readObject();
/* 431 */     } catch (Throwable throwable) {
/* 432 */       throw DbException.get(90027, throwable, new String[] { throwable.toString() });
/*     */     } 
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
/*     */   public static ValueUuid deserializeUuid(byte[] paramArrayOfbyte) {
/* 448 */     if (paramArrayOfbyte.length == 80) {
/* 449 */       byte b = 0; while (true) { if (b < 64) {
/* 450 */           if (paramArrayOfbyte[b] != UUID_PREFIX[b])
/*     */             break;  b++;
/*     */           continue;
/*     */         } 
/* 454 */         return ValueUuid.get(Bits.readLong(paramArrayOfbyte, 72), Bits.readLong(paramArrayOfbyte, 64)); }
/*     */     
/* 456 */     }  throw DbException.get(90027, "Is not a UUID");
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
/*     */   public static void set(PreparedStatement paramPreparedStatement, int paramInt, Value paramValue, JdbcConnection paramJdbcConnection) throws SQLException {
/* 470 */     if (paramPreparedStatement instanceof org.h2.jdbc.JdbcPreparedStatement) {
/* 471 */       if (paramValue instanceof ValueLob) {
/* 472 */         setLob(paramPreparedStatement, paramInt, (ValueLob)paramValue);
/*     */       } else {
/* 474 */         paramPreparedStatement.setObject(paramInt, paramValue);
/*     */       } 
/*     */     } else {
/* 477 */       setOther(paramPreparedStatement, paramInt, paramValue, paramJdbcConnection);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void setOther(PreparedStatement paramPreparedStatement, int paramInt, Value paramValue, JdbcConnection paramJdbcConnection) throws SQLException {
/* 483 */     int i = paramValue.getValueType();
/* 484 */     switch (i) {
/*     */       case 0:
/* 486 */         paramPreparedStatement.setNull(paramInt, 0);
/*     */         return;
/*     */       case 8:
/* 489 */         paramPreparedStatement.setBoolean(paramInt, paramValue.getBoolean());
/*     */         return;
/*     */       case 9:
/* 492 */         paramPreparedStatement.setByte(paramInt, paramValue.getByte());
/*     */         return;
/*     */       case 10:
/* 495 */         paramPreparedStatement.setShort(paramInt, paramValue.getShort());
/*     */         return;
/*     */       case 11:
/* 498 */         paramPreparedStatement.setInt(paramInt, paramValue.getInt());
/*     */         return;
/*     */       case 12:
/* 501 */         paramPreparedStatement.setLong(paramInt, paramValue.getLong());
/*     */         return;
/*     */       case 13:
/*     */       case 16:
/* 505 */         paramPreparedStatement.setBigDecimal(paramInt, paramValue.getBigDecimal());
/*     */         return;
/*     */       case 15:
/* 508 */         paramPreparedStatement.setDouble(paramInt, paramValue.getDouble());
/*     */         return;
/*     */       case 14:
/* 511 */         paramPreparedStatement.setFloat(paramInt, paramValue.getFloat());
/*     */         return;
/*     */       case 18:
/*     */         try {
/* 515 */           paramPreparedStatement.setObject(paramInt, JSR310Utils.valueToLocalTime(paramValue, null), 92);
/* 516 */         } catch (SQLException sQLException) {
/* 517 */           paramPreparedStatement.setTime(paramInt, LegacyDateTimeUtils.toTime(null, null, paramValue));
/*     */         } 
/*     */         return;
/*     */       case 17:
/*     */         try {
/* 522 */           paramPreparedStatement.setObject(paramInt, JSR310Utils.valueToLocalDate(paramValue, null), 91);
/* 523 */         } catch (SQLException sQLException) {
/* 524 */           paramPreparedStatement.setDate(paramInt, LegacyDateTimeUtils.toDate(null, null, paramValue));
/*     */         } 
/*     */         return;
/*     */       case 20:
/*     */         try {
/* 529 */           paramPreparedStatement.setObject(paramInt, JSR310Utils.valueToLocalDateTime(paramValue, null), 93);
/* 530 */         } catch (SQLException sQLException) {
/* 531 */           paramPreparedStatement.setTimestamp(paramInt, LegacyDateTimeUtils.toTimestamp(null, null, paramValue));
/*     */         } 
/*     */         return;
/*     */       case 5:
/*     */       case 6:
/*     */       case 37:
/*     */       case 38:
/* 538 */         paramPreparedStatement.setBytes(paramInt, paramValue.getBytesNoCopy());
/*     */         return;
/*     */       case 2:
/*     */       case 4:
/*     */       case 22:
/*     */       case 23:
/*     */       case 24:
/*     */       case 25:
/*     */       case 26:
/*     */       case 27:
/*     */       case 28:
/*     */       case 29:
/*     */       case 30:
/*     */       case 31:
/*     */       case 32:
/*     */       case 33:
/*     */       case 34:
/*     */       case 36:
/* 556 */         paramPreparedStatement.setString(paramInt, paramValue.getString());
/*     */         return;
/*     */       case 3:
/*     */       case 7:
/* 560 */         setLob(paramPreparedStatement, paramInt, (ValueLob)paramValue);
/*     */         return;
/*     */       case 40:
/* 563 */         paramPreparedStatement.setArray(paramInt, paramPreparedStatement.getConnection().createArrayOf("NULL", 
/* 564 */               (Object[])ValueToObjectConverter.valueToDefaultObject(paramValue, paramJdbcConnection, true)));
/*     */         return;
/*     */       case 35:
/* 567 */         paramPreparedStatement.setObject(paramInt, 
/* 568 */             deserialize(paramValue.getBytesNoCopy(), paramJdbcConnection.getJavaObjectSerializer()), 2000);
/*     */         return;
/*     */       
/*     */       case 39:
/* 572 */         paramPreparedStatement.setBytes(paramInt, paramValue.getBytes());
/*     */         return;
/*     */       case 1:
/*     */         try {
/* 576 */           paramPreparedStatement.setObject(paramInt, paramValue.getString(), 1);
/* 577 */         } catch (SQLException sQLException) {
/* 578 */           paramPreparedStatement.setString(paramInt, paramValue.getString());
/*     */         } 
/*     */         return;
/*     */       case 21:
/*     */         try {
/* 583 */           paramPreparedStatement.setObject(paramInt, JSR310Utils.valueToOffsetDateTime(paramValue, null), 2014);
/*     */           
/*     */           return;
/* 586 */         } catch (SQLException sQLException) {
/* 587 */           paramPreparedStatement.setString(paramInt, paramValue.getString());
/*     */         } 
/*     */         return;
/*     */       case 19:
/*     */         try {
/* 592 */           paramPreparedStatement.setObject(paramInt, JSR310Utils.valueToOffsetTime(paramValue, null), 2013);
/*     */           return;
/* 594 */         } catch (SQLException sQLException) {
/* 595 */           paramPreparedStatement.setString(paramInt, paramValue.getString());
/*     */         } 
/*     */         return;
/*     */     } 
/* 599 */     throw DbException.getUnsupportedException(Value.getTypeName(i));
/*     */   }
/*     */ 
/*     */   
/*     */   private static void setLob(PreparedStatement paramPreparedStatement, int paramInt, ValueLob paramValueLob) throws SQLException {
/* 604 */     if (paramValueLob.getValueType() == 7) {
/* 605 */       long l = paramValueLob.octetLength();
/* 606 */       paramPreparedStatement.setBinaryStream(paramInt, paramValueLob.getInputStream(), (l > 2147483647L) ? -1 : (int)l);
/*     */     } else {
/* 608 */       long l = paramValueLob.charLength();
/* 609 */       paramPreparedStatement.setCharacterStream(paramInt, paramValueLob.getReader(), (l > 2147483647L) ? -1 : (int)l);
/*     */     } 
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
/*     */   public static ResultSet getMetaResultSet(Connection paramConnection, String paramString) throws SQLException {
/* 623 */     DatabaseMetaData databaseMetaData = paramConnection.getMetaData();
/* 624 */     if (isBuiltIn(paramString, "@best_row_identifier")) {
/* 625 */       String[] arrayOfString = split(paramString);
/* 626 */       boolean bool = (arrayOfString[4] == null) ? false : Integer.parseInt(arrayOfString[4]);
/* 627 */       boolean bool1 = Boolean.parseBoolean(arrayOfString[5]);
/* 628 */       return databaseMetaData.getBestRowIdentifier(arrayOfString[1], arrayOfString[2], arrayOfString[3], bool, bool1);
/* 629 */     }  if (isBuiltIn(paramString, "@catalogs"))
/* 630 */       return databaseMetaData.getCatalogs(); 
/* 631 */     if (isBuiltIn(paramString, "@columns")) {
/* 632 */       String[] arrayOfString = split(paramString);
/* 633 */       return databaseMetaData.getColumns(arrayOfString[1], arrayOfString[2], arrayOfString[3], arrayOfString[4]);
/* 634 */     }  if (isBuiltIn(paramString, "@column_privileges")) {
/* 635 */       String[] arrayOfString = split(paramString);
/* 636 */       return databaseMetaData.getColumnPrivileges(arrayOfString[1], arrayOfString[2], arrayOfString[3], arrayOfString[4]);
/* 637 */     }  if (isBuiltIn(paramString, "@cross_references")) {
/* 638 */       String[] arrayOfString = split(paramString);
/* 639 */       return databaseMetaData.getCrossReference(arrayOfString[1], arrayOfString[2], arrayOfString[3], arrayOfString[4], arrayOfString[5], arrayOfString[6]);
/* 640 */     }  if (isBuiltIn(paramString, "@exported_keys")) {
/* 641 */       String[] arrayOfString = split(paramString);
/* 642 */       return databaseMetaData.getExportedKeys(arrayOfString[1], arrayOfString[2], arrayOfString[3]);
/* 643 */     }  if (isBuiltIn(paramString, "@imported_keys")) {
/* 644 */       String[] arrayOfString = split(paramString);
/* 645 */       return databaseMetaData.getImportedKeys(arrayOfString[1], arrayOfString[2], arrayOfString[3]);
/* 646 */     }  if (isBuiltIn(paramString, "@index_info")) {
/* 647 */       String[] arrayOfString = split(paramString);
/* 648 */       boolean bool1 = Boolean.parseBoolean(arrayOfString[4]);
/* 649 */       boolean bool2 = Boolean.parseBoolean(arrayOfString[5]);
/* 650 */       return databaseMetaData.getIndexInfo(arrayOfString[1], arrayOfString[2], arrayOfString[3], bool1, bool2);
/* 651 */     }  if (isBuiltIn(paramString, "@primary_keys")) {
/* 652 */       String[] arrayOfString = split(paramString);
/* 653 */       return databaseMetaData.getPrimaryKeys(arrayOfString[1], arrayOfString[2], arrayOfString[3]);
/* 654 */     }  if (isBuiltIn(paramString, "@procedures")) {
/* 655 */       String[] arrayOfString = split(paramString);
/* 656 */       return databaseMetaData.getProcedures(arrayOfString[1], arrayOfString[2], arrayOfString[3]);
/* 657 */     }  if (isBuiltIn(paramString, "@procedure_columns")) {
/* 658 */       String[] arrayOfString = split(paramString);
/* 659 */       return databaseMetaData.getProcedureColumns(arrayOfString[1], arrayOfString[2], arrayOfString[3], arrayOfString[4]);
/* 660 */     }  if (isBuiltIn(paramString, "@schemas"))
/* 661 */       return databaseMetaData.getSchemas(); 
/* 662 */     if (isBuiltIn(paramString, "@tables")) {
/* 663 */       String[] arrayOfString1 = split(paramString);
/* 664 */       String[] arrayOfString2 = (arrayOfString1[4] == null) ? null : StringUtils.arraySplit(arrayOfString1[4], ',', false);
/* 665 */       return databaseMetaData.getTables(arrayOfString1[1], arrayOfString1[2], arrayOfString1[3], arrayOfString2);
/* 666 */     }  if (isBuiltIn(paramString, "@table_privileges")) {
/* 667 */       String[] arrayOfString = split(paramString);
/* 668 */       return databaseMetaData.getTablePrivileges(arrayOfString[1], arrayOfString[2], arrayOfString[3]);
/* 669 */     }  if (isBuiltIn(paramString, "@table_types"))
/* 670 */       return databaseMetaData.getTableTypes(); 
/* 671 */     if (isBuiltIn(paramString, "@type_info"))
/* 672 */       return databaseMetaData.getTypeInfo(); 
/* 673 */     if (isBuiltIn(paramString, "@udts")) {
/* 674 */       int[] arrayOfInt; String[] arrayOfString = split(paramString);
/*     */       
/* 676 */       if (arrayOfString[4] == null) {
/* 677 */         arrayOfInt = null;
/*     */       } else {
/* 679 */         String[] arrayOfString1 = StringUtils.arraySplit(arrayOfString[4], ',', false);
/* 680 */         arrayOfInt = new int[arrayOfString1.length];
/* 681 */         for (byte b = 0; b < arrayOfString1.length; b++) {
/* 682 */           arrayOfInt[b] = Integer.parseInt(arrayOfString1[b]);
/*     */         }
/*     */       } 
/* 685 */       return databaseMetaData.getUDTs(arrayOfString[1], arrayOfString[2], arrayOfString[3], arrayOfInt);
/* 686 */     }  if (isBuiltIn(paramString, "@version_columns")) {
/* 687 */       String[] arrayOfString = split(paramString);
/* 688 */       return databaseMetaData.getVersionColumns(arrayOfString[1], arrayOfString[2], arrayOfString[3]);
/* 689 */     }  if (isBuiltIn(paramString, "@memory")) {
/* 690 */       SimpleResultSet simpleResultSet = new SimpleResultSet();
/* 691 */       simpleResultSet.addColumn("Type", 12, 0, 0);
/* 692 */       simpleResultSet.addColumn("KB", 12, 0, 0);
/* 693 */       simpleResultSet.addRow(new Object[] { "Used Memory", Long.toString(Utils.getMemoryUsed()) });
/* 694 */       simpleResultSet.addRow(new Object[] { "Free Memory", Long.toString(Utils.getMemoryFree()) });
/* 695 */       return (ResultSet)simpleResultSet;
/* 696 */     }  if (isBuiltIn(paramString, "@info")) {
/* 697 */       String str; SimpleResultSet simpleResultSet = new SimpleResultSet();
/* 698 */       simpleResultSet.addColumn("KEY", 12, 0, 0);
/* 699 */       simpleResultSet.addColumn("VALUE", 12, 0, 0);
/* 700 */       simpleResultSet.addRow(new Object[] { "conn.getCatalog", paramConnection.getCatalog() });
/* 701 */       simpleResultSet.addRow(new Object[] { "conn.getAutoCommit", Boolean.toString(paramConnection.getAutoCommit()) });
/* 702 */       simpleResultSet.addRow(new Object[] { "conn.getTransactionIsolation", Integer.toString(paramConnection.getTransactionIsolation()) });
/* 703 */       simpleResultSet.addRow(new Object[] { "conn.getWarnings", String.valueOf(paramConnection.getWarnings()) });
/*     */       
/*     */       try {
/* 706 */         str = String.valueOf(paramConnection.getTypeMap());
/* 707 */       } catch (SQLException sQLException) {
/* 708 */         str = sQLException.toString();
/*     */       } 
/* 710 */       simpleResultSet.addRow(new Object[] { "conn.getTypeMap", str });
/* 711 */       simpleResultSet.addRow(new Object[] { "conn.isReadOnly", Boolean.toString(paramConnection.isReadOnly()) });
/* 712 */       simpleResultSet.addRow(new Object[] { "conn.getHoldability", Integer.toString(paramConnection.getHoldability()) });
/* 713 */       addDatabaseMetaData(simpleResultSet, databaseMetaData);
/* 714 */       return (ResultSet)simpleResultSet;
/* 715 */     }  if (isBuiltIn(paramString, "@attributes")) {
/* 716 */       String[] arrayOfString = split(paramString);
/* 717 */       return databaseMetaData.getAttributes(arrayOfString[1], arrayOfString[2], arrayOfString[3], arrayOfString[4]);
/* 718 */     }  if (isBuiltIn(paramString, "@super_tables")) {
/* 719 */       String[] arrayOfString = split(paramString);
/* 720 */       return databaseMetaData.getSuperTables(arrayOfString[1], arrayOfString[2], arrayOfString[3]);
/* 721 */     }  if (isBuiltIn(paramString, "@super_types")) {
/* 722 */       String[] arrayOfString = split(paramString);
/* 723 */       return databaseMetaData.getSuperTypes(arrayOfString[1], arrayOfString[2], arrayOfString[3]);
/* 724 */     }  if (isBuiltIn(paramString, "@pseudo_columns")) {
/* 725 */       String[] arrayOfString = split(paramString);
/* 726 */       return databaseMetaData.getPseudoColumns(arrayOfString[1], arrayOfString[2], arrayOfString[3], arrayOfString[4]);
/*     */     } 
/* 728 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void addDatabaseMetaData(SimpleResultSet paramSimpleResultSet, DatabaseMetaData paramDatabaseMetaData) {
/* 733 */     Method[] arrayOfMethod = DatabaseMetaData.class.getDeclaredMethods();
/* 734 */     Arrays.sort(arrayOfMethod, Comparator.comparing(Method::toString));
/* 735 */     for (Method method : arrayOfMethod) {
/* 736 */       if ((method.getParameterTypes()).length == 0) {
/*     */         try {
/* 738 */           Object object = method.invoke(paramDatabaseMetaData, new Object[0]);
/* 739 */           paramSimpleResultSet.addRow(new Object[] { "meta." + method.getName(), String.valueOf(object) });
/* 740 */         } catch (InvocationTargetException invocationTargetException) {
/* 741 */           paramSimpleResultSet.addRow(new Object[] { "meta." + method.getName(), invocationTargetException.getTargetException().toString() });
/* 742 */         } catch (Exception exception) {
/* 743 */           paramSimpleResultSet.addRow(new Object[] { "meta." + method.getName(), exception.toString() });
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isBuiltIn(String paramString1, String paramString2) {
/* 757 */     return paramString1.regionMatches(true, 0, paramString2, 0, paramString2.length());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String[] split(String paramString) {
/* 767 */     String[] arrayOfString1 = StringUtils.arraySplit(paramString, ' ', true);
/* 768 */     String[] arrayOfString2 = new String[Math.max(10, arrayOfString1.length)];
/* 769 */     System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, arrayOfString1.length);
/* 770 */     for (byte b = 0; b < arrayOfString2.length; b++) {
/* 771 */       if ("null".equals(arrayOfString2[b])) {
/* 772 */         arrayOfString2[b] = null;
/*     */       }
/*     */     } 
/* 775 */     return arrayOfString2;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\JdbcUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */