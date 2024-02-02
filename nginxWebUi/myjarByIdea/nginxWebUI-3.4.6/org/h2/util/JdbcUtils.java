package org.h2.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.TimeZone;
import javax.naming.Context;
import javax.sql.DataSource;
import org.h2.api.JavaObjectSerializer;
import org.h2.engine.CastDataProvider;
import org.h2.engine.SysProperties;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbc.JdbcPreparedStatement;
import org.h2.message.DbException;
import org.h2.tools.SimpleResultSet;
import org.h2.value.Value;
import org.h2.value.ValueLob;
import org.h2.value.ValueToObjectConverter;
import org.h2.value.ValueUuid;

public class JdbcUtils {
   public static JavaObjectSerializer serializer;
   private static final String[] DRIVERS = new String[]{"h2:", "org.h2.Driver", "Cache:", "com.intersys.jdbc.CacheDriver", "daffodilDB://", "in.co.daffodil.db.rmi.RmiDaffodilDBDriver", "daffodil", "in.co.daffodil.db.jdbc.DaffodilDBDriver", "db2:", "com.ibm.db2.jcc.DB2Driver", "derby:net:", "org.apache.derby.client.ClientAutoloadedDriver", "derby://", "org.apache.derby.client.ClientAutoloadedDriver", "derby:", "org.apache.derby.iapi.jdbc.AutoloadedDriver", "FrontBase:", "com.frontbase.jdbc.FBJDriver", "firebirdsql:", "org.firebirdsql.jdbc.FBDriver", "hsqldb:", "org.hsqldb.jdbcDriver", "informix-sqli:", "com.informix.jdbc.IfxDriver", "jtds:", "net.sourceforge.jtds.jdbc.Driver", "microsoft:", "com.microsoft.jdbc.sqlserver.SQLServerDriver", "mimer:", "com.mimer.jdbc.Driver", "mysql:", "com.mysql.cj.jdbc.Driver", "mariadb:", "org.mariadb.jdbc.Driver", "odbc:", "sun.jdbc.odbc.JdbcOdbcDriver", "oracle:", "oracle.jdbc.driver.OracleDriver", "pervasive:", "com.pervasive.jdbc.v2.Driver", "pointbase:micro:", "com.pointbase.me.jdbc.jdbcDriver", "pointbase:", "com.pointbase.jdbc.jdbcUniversalDriver", "postgresql:", "org.postgresql.Driver", "sybase:", "com.sybase.jdbc3.jdbc.SybDriver", "sqlserver:", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "teradata:", "com.ncr.teradata.TeraDriver"};
   private static final byte[] UUID_PREFIX;
   private static boolean allowAllClasses;
   private static HashSet<String> allowedClassNames;
   private static final ArrayList<Utils.ClassFactory> userClassFactories;
   private static String[] allowedClassNamePrefixes;

   private JdbcUtils() {
   }

   public static void addClassFactory(Utils.ClassFactory var0) {
      userClassFactories.add(var0);
   }

   public static void removeClassFactory(Utils.ClassFactory var0) {
      userClassFactories.remove(var0);
   }

   public static <Z> Class<Z> loadUserClass(String var0) {
      if (allowedClassNames == null) {
         String var1 = SysProperties.ALLOWED_CLASSES;
         ArrayList var2 = new ArrayList();
         boolean var3 = false;
         HashSet var4 = new HashSet();
         String[] var5 = StringUtils.arraySplit(var1, ',', true);
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            if (var8.equals("*")) {
               var3 = true;
            } else if (var8.endsWith("*")) {
               var2.add(var8.substring(0, var8.length() - 1));
            } else {
               var4.add(var8);
            }
         }

         allowedClassNamePrefixes = (String[])var2.toArray(new String[0]);
         allowAllClasses = var3;
         allowedClassNames = var4;
      }

      if (!allowAllClasses && !allowedClassNames.contains(var0)) {
         boolean var14 = false;
         String[] var15 = allowedClassNamePrefixes;
         int var18 = var15.length;

         for(int var20 = 0; var20 < var18; ++var20) {
            String var21 = var15[var20];
            if (var0.startsWith(var21)) {
               var14 = true;
               break;
            }
         }

         if (!var14) {
            throw DbException.get(90134, var0);
         }
      }

      Iterator var16 = userClassFactories.iterator();

      while(var16.hasNext()) {
         Utils.ClassFactory var17 = (Utils.ClassFactory)var16.next();
         if (var17.match(var0)) {
            try {
               Class var19 = var17.loadClass(var0);
               if (var19 != null) {
                  return var19;
               }
            } catch (Exception var13) {
               throw DbException.get(90086, var13, var0);
            }
         }
      }

      try {
         return Class.forName(var0);
      } catch (ClassNotFoundException var10) {
         try {
            return Class.forName(var0, true, Thread.currentThread().getContextClassLoader());
         } catch (Exception var9) {
            throw DbException.get(90086, var10, var0);
         }
      } catch (NoClassDefFoundError var11) {
         throw DbException.get(90086, var11, var0);
      } catch (Error var12) {
         throw DbException.get(50000, var12, var0);
      }
   }

   public static void closeSilently(Statement var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (SQLException var2) {
         }
      }

   }

   public static void closeSilently(Connection var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (SQLException var2) {
         }
      }

   }

   public static void closeSilently(ResultSet var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (SQLException var2) {
         }
      }

   }

   public static Connection getConnection(String var0, String var1, String var2, String var3) throws SQLException {
      return getConnection(var0, var1, var2, var3, (NetworkConnectionInfo)null, false);
   }

   public static Connection getConnection(String var0, String var1, String var2, String var3, NetworkConnectionInfo var4, boolean var5) throws SQLException {
      if (var1.startsWith("jdbc:h2:")) {
         JdbcConnection var11 = new JdbcConnection(var1, (Properties)null, var2, var3, var5);
         if (var4 != null) {
            var11.getSession().setNetworkConnectionInfo(var4);
         }

         return var11;
      } else {
         if (StringUtils.isNullOrEmpty(var0)) {
            load(var1);
         } else {
            Class var6 = loadUserClass(var0);

            try {
               if (Driver.class.isAssignableFrom(var6)) {
                  Driver var12 = (Driver)var6.getDeclaredConstructor().newInstance();
                  Properties var13 = new Properties();
                  if (var2 != null) {
                     var13.setProperty("user", var2);
                  }

                  if (var3 != null) {
                     var13.setProperty("password", var3);
                  }

                  Connection var9 = var12.connect(var1, var13);
                  if (var9 != null) {
                     return var9;
                  }

                  throw new SQLException("Driver " + var0 + " is not suitable for " + var1, "08001");
               }

               if (Context.class.isAssignableFrom(var6)) {
                  if (!var1.startsWith("java:")) {
                     throw new SQLException("Only java scheme is supported for JNDI lookups", "08001");
                  }

                  Context var7 = (Context)var6.getDeclaredConstructor().newInstance();
                  DataSource var8 = (DataSource)var7.lookup(var1);
                  if (StringUtils.isNullOrEmpty(var2) && StringUtils.isNullOrEmpty(var3)) {
                     return var8.getConnection();
                  }

                  return var8.getConnection(var2, var3);
               }
            } catch (Exception var10) {
               throw DbException.toSQLException(var10);
            }
         }

         return DriverManager.getConnection(var1, var2, var3);
      }
   }

   public static String getDriver(String var0) {
      if (var0.startsWith("jdbc:")) {
         var0 = var0.substring("jdbc:".length());

         for(int var1 = 0; var1 < DRIVERS.length; var1 += 2) {
            String var2 = DRIVERS[var1];
            if (var0.startsWith(var2)) {
               return DRIVERS[var1 + 1];
            }
         }
      }

      return null;
   }

   public static void load(String var0) {
      String var1 = getDriver(var0);
      if (var1 != null) {
         loadUserClass(var1);
      }

   }

   public static byte[] serialize(Object var0, JavaObjectSerializer var1) {
      try {
         if (var1 != null) {
            return var1.serialize(var0);
         } else if (serializer != null) {
            return serializer.serialize(var0);
         } else {
            ByteArrayOutputStream var2 = new ByteArrayOutputStream();
            ObjectOutputStream var3 = new ObjectOutputStream(var2);
            var3.writeObject(var0);
            return var2.toByteArray();
         }
      } catch (Throwable var4) {
         throw DbException.get(90026, var4, var4.toString());
      }
   }

   public static Object deserialize(byte[] var0, JavaObjectSerializer var1) {
      try {
         if (var1 != null) {
            return var1.deserialize(var0);
         } else if (serializer != null) {
            return serializer.deserialize(var0);
         } else {
            ByteArrayInputStream var2 = new ByteArrayInputStream(var0);
            ObjectInputStream var3;
            if (SysProperties.USE_THREAD_CONTEXT_CLASS_LOADER) {
               final ClassLoader var4 = Thread.currentThread().getContextClassLoader();
               var3 = new ObjectInputStream(var2) {
                  protected Class<?> resolveClass(ObjectStreamClass var1) throws IOException, ClassNotFoundException {
                     try {
                        return Class.forName(var1.getName(), true, var4);
                     } catch (ClassNotFoundException var3) {
                        return super.resolveClass(var1);
                     }
                  }
               };
            } else {
               var3 = new ObjectInputStream(var2);
            }

            return var3.readObject();
         }
      } catch (Throwable var5) {
         throw DbException.get(90027, var5, var5.toString());
      }
   }

   public static ValueUuid deserializeUuid(byte[] var0) {
      if (var0.length == 80) {
         int var1 = 0;

         while(true) {
            if (var1 >= 64) {
               return ValueUuid.get(Bits.readLong(var0, 72), Bits.readLong(var0, 64));
            }

            if (var0[var1] != UUID_PREFIX[var1]) {
               break;
            }

            ++var1;
         }
      }

      throw DbException.get(90027, "Is not a UUID");
   }

   public static void set(PreparedStatement var0, int var1, Value var2, JdbcConnection var3) throws SQLException {
      if (var0 instanceof JdbcPreparedStatement) {
         if (var2 instanceof ValueLob) {
            setLob(var0, var1, (ValueLob)var2);
         } else {
            var0.setObject(var1, var2);
         }
      } else {
         setOther(var0, var1, var2, var3);
      }

   }

   private static void setOther(PreparedStatement var0, int var1, Value var2, JdbcConnection var3) throws SQLException {
      int var4 = var2.getValueType();
      switch (var4) {
         case 0:
            var0.setNull(var1, 0);
            break;
         case 1:
            try {
               var0.setObject(var1, var2.getString(), 1);
            } catch (SQLException var6) {
               var0.setString(var1, var2.getString());
            }
            break;
         case 2:
         case 4:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 36:
            var0.setString(var1, var2.getString());
            break;
         case 3:
         case 7:
            setLob(var0, var1, (ValueLob)var2);
            break;
         case 5:
         case 6:
         case 37:
         case 38:
            var0.setBytes(var1, var2.getBytesNoCopy());
            break;
         case 8:
            var0.setBoolean(var1, var2.getBoolean());
            break;
         case 9:
            var0.setByte(var1, var2.getByte());
            break;
         case 10:
            var0.setShort(var1, var2.getShort());
            break;
         case 11:
            var0.setInt(var1, var2.getInt());
            break;
         case 12:
            var0.setLong(var1, var2.getLong());
            break;
         case 13:
         case 16:
            var0.setBigDecimal(var1, var2.getBigDecimal());
            break;
         case 14:
            var0.setFloat(var1, var2.getFloat());
            break;
         case 15:
            var0.setDouble(var1, var2.getDouble());
            break;
         case 17:
            try {
               var0.setObject(var1, JSR310Utils.valueToLocalDate(var2, (CastDataProvider)null), 91);
            } catch (SQLException var8) {
               var0.setDate(var1, LegacyDateTimeUtils.toDate((CastDataProvider)null, (TimeZone)null, var2));
            }
            break;
         case 18:
            try {
               var0.setObject(var1, JSR310Utils.valueToLocalTime(var2, (CastDataProvider)null), 92);
            } catch (SQLException var9) {
               var0.setTime(var1, LegacyDateTimeUtils.toTime((CastDataProvider)null, (TimeZone)null, var2));
            }
            break;
         case 19:
            try {
               var0.setObject(var1, JSR310Utils.valueToOffsetTime(var2, (CastDataProvider)null), 2013);
               return;
            } catch (SQLException var10) {
               var0.setString(var1, var2.getString());
               break;
            }
         case 20:
            try {
               var0.setObject(var1, JSR310Utils.valueToLocalDateTime(var2, (CastDataProvider)null), 93);
            } catch (SQLException var7) {
               var0.setTimestamp(var1, LegacyDateTimeUtils.toTimestamp((CastDataProvider)null, (TimeZone)null, var2));
            }
            break;
         case 21:
            try {
               var0.setObject(var1, JSR310Utils.valueToOffsetDateTime(var2, (CastDataProvider)null), 2014);
               return;
            } catch (SQLException var11) {
               var0.setString(var1, var2.getString());
               break;
            }
         case 35:
            var0.setObject(var1, deserialize(var2.getBytesNoCopy(), var3.getJavaObjectSerializer()), 2000);
            break;
         case 39:
            var0.setBytes(var1, var2.getBytes());
            break;
         case 40:
            var0.setArray(var1, var0.getConnection().createArrayOf("NULL", (Object[])((Object[])ValueToObjectConverter.valueToDefaultObject(var2, var3, true))));
            break;
         default:
            throw DbException.getUnsupportedException(Value.getTypeName(var4));
      }

   }

   private static void setLob(PreparedStatement var0, int var1, ValueLob var2) throws SQLException {
      long var3;
      if (var2.getValueType() == 7) {
         var3 = var2.octetLength();
         var0.setBinaryStream(var1, var2.getInputStream(), var3 > 2147483647L ? -1 : (int)var3);
      } else {
         var3 = var2.charLength();
         var0.setCharacterStream(var1, var2.getReader(), var3 > 2147483647L ? -1 : (int)var3);
      }

   }

   public static ResultSet getMetaResultSet(Connection var0, String var1) throws SQLException {
      DatabaseMetaData var2 = var0.getMetaData();
      String[] var3;
      boolean var10;
      if (isBuiltIn(var1, "@best_row_identifier")) {
         var3 = split(var1);
         int var13 = var3[4] == null ? 0 : Integer.parseInt(var3[4]);
         var10 = Boolean.parseBoolean(var3[5]);
         return var2.getBestRowIdentifier(var3[1], var3[2], var3[3], var13, var10);
      } else if (isBuiltIn(var1, "@catalogs")) {
         return var2.getCatalogs();
      } else if (isBuiltIn(var1, "@columns")) {
         var3 = split(var1);
         return var2.getColumns(var3[1], var3[2], var3[3], var3[4]);
      } else if (isBuiltIn(var1, "@column_privileges")) {
         var3 = split(var1);
         return var2.getColumnPrivileges(var3[1], var3[2], var3[3], var3[4]);
      } else if (isBuiltIn(var1, "@cross_references")) {
         var3 = split(var1);
         return var2.getCrossReference(var3[1], var3[2], var3[3], var3[4], var3[5], var3[6]);
      } else if (isBuiltIn(var1, "@exported_keys")) {
         var3 = split(var1);
         return var2.getExportedKeys(var3[1], var3[2], var3[3]);
      } else if (isBuiltIn(var1, "@imported_keys")) {
         var3 = split(var1);
         return var2.getImportedKeys(var3[1], var3[2], var3[3]);
      } else if (isBuiltIn(var1, "@index_info")) {
         var3 = split(var1);
         boolean var12 = Boolean.parseBoolean(var3[4]);
         var10 = Boolean.parseBoolean(var3[5]);
         return var2.getIndexInfo(var3[1], var3[2], var3[3], var12, var10);
      } else if (isBuiltIn(var1, "@primary_keys")) {
         var3 = split(var1);
         return var2.getPrimaryKeys(var3[1], var3[2], var3[3]);
      } else if (isBuiltIn(var1, "@procedures")) {
         var3 = split(var1);
         return var2.getProcedures(var3[1], var3[2], var3[3]);
      } else if (isBuiltIn(var1, "@procedure_columns")) {
         var3 = split(var1);
         return var2.getProcedureColumns(var3[1], var3[2], var3[3], var3[4]);
      } else if (isBuiltIn(var1, "@schemas")) {
         return var2.getSchemas();
      } else if (isBuiltIn(var1, "@tables")) {
         var3 = split(var1);
         String[] var11 = var3[4] == null ? null : StringUtils.arraySplit(var3[4], ',', false);
         return var2.getTables(var3[1], var3[2], var3[3], var11);
      } else if (isBuiltIn(var1, "@table_privileges")) {
         var3 = split(var1);
         return var2.getTablePrivileges(var3[1], var3[2], var3[3]);
      } else if (isBuiltIn(var1, "@table_types")) {
         return var2.getTableTypes();
      } else if (isBuiltIn(var1, "@type_info")) {
         return var2.getTypeInfo();
      } else if (!isBuiltIn(var1, "@udts")) {
         if (isBuiltIn(var1, "@version_columns")) {
            var3 = split(var1);
            return var2.getVersionColumns(var3[1], var3[2], var3[3]);
         } else {
            SimpleResultSet var8;
            if (isBuiltIn(var1, "@memory")) {
               var8 = new SimpleResultSet();
               var8.addColumn("Type", 12, 0, 0);
               var8.addColumn("KB", 12, 0, 0);
               var8.addRow("Used Memory", Long.toString(Utils.getMemoryUsed()));
               var8.addRow("Free Memory", Long.toString(Utils.getMemoryFree()));
               return var8;
            } else if (isBuiltIn(var1, "@info")) {
               var8 = new SimpleResultSet();
               var8.addColumn("KEY", 12, 0, 0);
               var8.addColumn("VALUE", 12, 0, 0);
               var8.addRow("conn.getCatalog", var0.getCatalog());
               var8.addRow("conn.getAutoCommit", Boolean.toString(var0.getAutoCommit()));
               var8.addRow("conn.getTransactionIsolation", Integer.toString(var0.getTransactionIsolation()));
               var8.addRow("conn.getWarnings", String.valueOf(var0.getWarnings()));

               String var9;
               try {
                  var9 = String.valueOf(var0.getTypeMap());
               } catch (SQLException var7) {
                  var9 = var7.toString();
               }

               var8.addRow("conn.getTypeMap", var9);
               var8.addRow("conn.isReadOnly", Boolean.toString(var0.isReadOnly()));
               var8.addRow("conn.getHoldability", Integer.toString(var0.getHoldability()));
               addDatabaseMetaData(var8, var2);
               return var8;
            } else if (isBuiltIn(var1, "@attributes")) {
               var3 = split(var1);
               return var2.getAttributes(var3[1], var3[2], var3[3], var3[4]);
            } else if (isBuiltIn(var1, "@super_tables")) {
               var3 = split(var1);
               return var2.getSuperTables(var3[1], var3[2], var3[3]);
            } else if (isBuiltIn(var1, "@super_types")) {
               var3 = split(var1);
               return var2.getSuperTypes(var3[1], var3[2], var3[3]);
            } else if (isBuiltIn(var1, "@pseudo_columns")) {
               var3 = split(var1);
               return var2.getPseudoColumns(var3[1], var3[2], var3[3], var3[4]);
            } else {
               return null;
            }
         }
      } else {
         var3 = split(var1);
         int[] var4;
         if (var3[4] == null) {
            var4 = null;
         } else {
            String[] var5 = StringUtils.arraySplit(var3[4], ',', false);
            var4 = new int[var5.length];

            for(int var6 = 0; var6 < var5.length; ++var6) {
               var4[var6] = Integer.parseInt(var5[var6]);
            }
         }

         return var2.getUDTs(var3[1], var3[2], var3[3], var4);
      }
   }

   private static void addDatabaseMetaData(SimpleResultSet var0, DatabaseMetaData var1) {
      Method[] var2 = DatabaseMetaData.class.getDeclaredMethods();
      Arrays.sort(var2, Comparator.comparing(Method::toString));
      Method[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Method var6 = var3[var5];
         if (var6.getParameterTypes().length == 0) {
            try {
               Object var7 = var6.invoke(var1);
               var0.addRow("meta." + var6.getName(), String.valueOf(var7));
            } catch (InvocationTargetException var8) {
               var0.addRow("meta." + var6.getName(), var8.getTargetException().toString());
            } catch (Exception var9) {
               var0.addRow("meta." + var6.getName(), var9.toString());
            }
         }
      }

   }

   public static boolean isBuiltIn(String var0, String var1) {
      return var0.regionMatches(true, 0, var1, 0, var1.length());
   }

   public static String[] split(String var0) {
      String[] var1 = StringUtils.arraySplit(var0, ' ', true);
      String[] var2 = new String[Math.max(10, var1.length)];
      System.arraycopy(var1, 0, var2, 0, var1.length);

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if ("null".equals(var2[var3])) {
            var2[var3] = null;
         }
      }

      return var2;
   }

   static {
      UUID_PREFIX = "¬í\u0000\u0005sr\u0000\u000ejava.util.UUID¼\u0099\u0003÷\u0098m\u0085/\u0002\u0000\u0002J\u0000\fleastSigBitsJ\u0000\u000bmostSigBitsxp".getBytes(StandardCharsets.ISO_8859_1);
      userClassFactories = new ArrayList();
      String var0 = SysProperties.JAVA_OBJECT_SERIALIZER;
      if (var0 != null) {
         try {
            serializer = (JavaObjectSerializer)loadUserClass(var0).getDeclaredConstructor().newInstance();
         } catch (Exception var2) {
            throw DbException.convert(var2);
         }
      }

   }
}
