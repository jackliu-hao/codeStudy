package org.h2.table;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.index.LinkedIndex;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbc.JdbcResultSet;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.schema.Schema;
import org.h2.util.JdbcUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public class TableLink extends Table {
   private static final int MAX_RETRY = 2;
   private static final long ROW_COUNT_APPROXIMATION = 100000L;
   private final String originalSchema;
   private String driver;
   private String url;
   private String user;
   private String password;
   private String originalTable;
   private String qualifiedTableName;
   private TableLinkConnection conn;
   private HashMap<String, PreparedStatement> preparedMap = new HashMap();
   private final ArrayList<Index> indexes = Utils.newSmallArrayList();
   private final boolean emitUpdates;
   private LinkedIndex linkedIndex;
   private DbException connectException;
   private boolean storesLowerCase;
   private boolean storesMixedCase;
   private boolean storesMixedCaseQuoted;
   private boolean supportsMixedCaseIdentifiers;
   private boolean globalTemporary;
   private boolean readOnly;
   private final boolean targetsMySql;
   private int fetchSize = 0;
   private boolean autocommit = true;

   public TableLink(Schema var1, int var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9, boolean var10, boolean var11) {
      super(var1, var2, var3, false, true);
      this.driver = var4;
      this.url = var5;
      this.user = var6;
      this.password = var7;
      this.originalSchema = var8;
      this.originalTable = var9;
      this.emitUpdates = var10;
      this.targetsMySql = isMySqlUrl(this.url);

      try {
         this.connect();
      } catch (DbException var14) {
         if (!var11) {
            throw var14;
         }

         Column[] var13 = new Column[0];
         this.setColumns(var13);
         this.linkedIndex = new LinkedIndex(this, var2, IndexColumn.wrap(var13), 0, IndexType.createNonUnique(false));
         this.indexes.add(this.linkedIndex);
      }

   }

   private void connect() {
      this.connectException = null;
      int var1 = 0;

      while(true) {
         try {
            this.conn = this.database.getLinkConnection(this.driver, this.url, this.user, this.password);
            this.conn.setAutoCommit(this.autocommit);
            synchronized(this.conn) {
               try {
                  this.readMetaData();
               } catch (Exception var5) {
                  this.conn.close(true);
                  this.conn = null;
                  throw DbException.convert(var5);
               }

               return;
            }
         } catch (DbException var7) {
            if (var1 >= 2) {
               this.connectException = var7;
               throw var7;
            }

            ++var1;
         }
      }
   }

   private void readMetaData() throws SQLException {
      DatabaseMetaData var1 = this.conn.getConnection().getMetaData();
      this.storesLowerCase = var1.storesLowerCaseIdentifiers();
      this.storesMixedCase = var1.storesMixedCaseIdentifiers();
      this.storesMixedCaseQuoted = var1.storesMixedCaseQuotedIdentifiers();
      this.supportsMixedCaseIdentifiers = var1.supportsMixedCaseIdentifiers();
      ArrayList var2 = Utils.newSmallArrayList();
      HashMap var3 = new HashMap();
      String var4 = null;
      boolean var5 = this.originalTable.startsWith("(");
      Throwable var7;
      long var15;
      int var17;
      int var18;
      Column var19;
      if (!var5) {
         ResultSet var6 = var1.getTables((String)null, this.originalSchema, this.originalTable, (String[])null);
         var7 = null;

         try {
            if (var6.next() && var6.next()) {
               throw DbException.get(90080, this.originalTable);
            }
         } catch (Throwable var99) {
            var7 = var99;
            throw var99;
         } finally {
            if (var6 != null) {
               if (var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var90) {
                     var7.addSuppressed(var90);
                  }
               } else {
                  var6.close();
               }
            }

         }

         var6 = var1.getColumns((String)null, this.originalSchema, this.originalTable, (String)null);
         var7 = null;

         try {
            int var8 = 0;
            String var9 = null;

            while(var6.next()) {
               String var10 = var6.getString("TABLE_CAT");
               if (var9 == null) {
                  var9 = var10;
               }

               String var11 = var6.getString("TABLE_SCHEM");
               if (var4 == null) {
                  var4 = var11;
               }

               if (!Objects.equals(var9, var10) || !Objects.equals(var4, var11)) {
                  var3.clear();
                  var2.clear();
                  break;
               }

               String var12 = var6.getString("COLUMN_NAME");
               var12 = this.convertColumnName(var12);
               int var13 = var6.getInt("DATA_TYPE");
               String var14 = var6.getString("TYPE_NAME");
               var15 = (long)var6.getInt("COLUMN_SIZE");
               var15 = convertPrecision(var13, var15);
               var17 = var6.getInt("DECIMAL_DIGITS");
               var17 = convertScale(var13, var17);
               var18 = DataType.convertSQLTypeToValueType(var13, var14);
               var19 = new Column(var12, TypeInfo.getTypeInfo(var18, var15, var17, (ExtTypeInfo)null), this, var8++);
               var2.add(var19);
               var3.put(var12, var19);
            }
         } catch (Throwable var97) {
            var7 = var97;
            throw var97;
         } finally {
            if (var6 != null) {
               if (var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var88) {
                     var7.addSuppressed(var88);
                  }
               } else {
                  var6.close();
               }
            }

         }
      }

      if (this.originalTable.indexOf(46) < 0 && !StringUtils.isNullOrEmpty(var4)) {
         this.qualifiedTableName = var4 + '.' + this.originalTable;
      } else {
         this.qualifiedTableName = this.originalTable;
      }

      try {
         Statement var101 = this.conn.getConnection().createStatement();
         var7 = null;

         try {
            ResultSet var103 = var101.executeQuery("SELECT * FROM " + this.qualifiedTableName + " T WHERE 1=0");
            Throwable var104 = null;

            try {
               int var108;
               int var109;
               String var110;
               if (var103 instanceof JdbcResultSet) {
                  ResultInterface var106 = ((JdbcResultSet)var103).getResult();
                  var2.clear();
                  var3.clear();
                  var108 = 0;
                  var109 = var106.getVisibleColumnCount();

                  while(var108 < var109) {
                     var110 = var106.getColumnName(var108);
                     TypeInfo var10003 = var106.getColumnType(var108);
                     ++var108;
                     Column var111 = new Column(var110, var10003, this, var108);
                     var2.add(var111);
                     var3.put(var110, var111);
                  }
               } else if (var2.isEmpty()) {
                  ResultSetMetaData var107 = var103.getMetaData();
                  var108 = 0;
                  var109 = var107.getColumnCount();

                  while(var108 < var109) {
                     var110 = var107.getColumnName(var108 + 1);
                     var110 = this.convertColumnName(var110);
                     int var112 = var107.getColumnType(var108 + 1);
                     var15 = (long)var107.getPrecision(var108 + 1);
                     var15 = convertPrecision(var112, var15);
                     var17 = var107.getScale(var108 + 1);
                     var17 = convertScale(var112, var17);
                     var18 = DataType.getValueTypeFromResultSet(var107, var108 + 1);
                     var19 = new Column(var110, TypeInfo.getTypeInfo(var18, var15, var17, (ExtTypeInfo)null), this, var108++);
                     var2.add(var19);
                     var3.put(var110, var19);
                  }
               }
            } catch (Throwable var92) {
               var104 = var92;
               throw var92;
            } finally {
               if (var103 != null) {
                  if (var104 != null) {
                     try {
                        var103.close();
                     } catch (Throwable var91) {
                        var104.addSuppressed(var91);
                     }
                  } else {
                     var103.close();
                  }
               }

            }
         } catch (Throwable var94) {
            var7 = var94;
            throw var94;
         } finally {
            if (var101 != null) {
               if (var7 != null) {
                  try {
                     var101.close();
                  } catch (Throwable var89) {
                     var7.addSuppressed(var89);
                  }
               } else {
                  var101.close();
               }
            }

         }
      } catch (Exception var96) {
         throw DbException.get(42102, var96, this.originalTable + '(' + var96 + ')');
      }

      Column[] var102 = (Column[])var2.toArray(new Column[0]);
      this.setColumns(var102);
      int var105 = this.getId();
      this.linkedIndex = new LinkedIndex(this, var105, IndexColumn.wrap(var102), 0, IndexType.createNonUnique(false));
      this.indexes.add(this.linkedIndex);
      if (!var5) {
         this.readIndexes(var1, var3);
      }

   }

   private void readIndexes(DatabaseMetaData var1, HashMap<String, Column> var2) {
      String var3 = null;

      ResultSet var4;
      Throwable var5;
      try {
         var4 = var1.getPrimaryKeys((String)null, this.originalSchema, this.originalTable);
         var5 = null;

         try {
            if (var4.next()) {
               var3 = this.readPrimaryKey(var4, var2);
            }
         } catch (Throwable var33) {
            var5 = var33;
            throw var33;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var32) {
                     var5.addSuppressed(var32);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (Exception var37) {
      }

      try {
         var4 = var1.getIndexInfo((String)null, this.originalSchema, this.originalTable, false, true);
         var5 = null;

         try {
            this.readIndexes(var4, var2, var3);
         } catch (Throwable var31) {
            var5 = var31;
            throw var31;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var30) {
                     var5.addSuppressed(var30);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (Exception var35) {
      }

   }

   private String readPrimaryKey(ResultSet var1, HashMap<String, Column> var2) throws SQLException {
      String var3 = null;
      ArrayList var4 = Utils.newSmallArrayList();

      do {
         int var5 = var1.getInt("KEY_SEQ");
         if (StringUtils.isNullOrEmpty(var3)) {
            var3 = var1.getString("PK_NAME");
         }

         while(var4.size() < var5) {
            var4.add((Object)null);
         }

         String var6 = var1.getString("COLUMN_NAME");
         var6 = this.convertColumnName(var6);
         Column var7 = (Column)var2.get(var6);
         if (var5 == 0) {
            var4.add(var7);
         } else {
            var4.set(var5 - 1, var7);
         }
      } while(var1.next());

      this.addIndex(var4, var4.size(), IndexType.createPrimaryKey(false, false));
      return var3;
   }

   private void readIndexes(ResultSet var1, HashMap<String, Column> var2, String var3) throws SQLException {
      String var4 = null;
      ArrayList var5 = Utils.newSmallArrayList();
      int var6 = 0;
      IndexType var7 = null;

      while(true) {
         String var8;
         do {
            do {
               if (!var1.next()) {
                  if (var4 != null) {
                     this.addIndex(var5, var6, var7);
                  }

                  return;
               }
            } while(var1.getShort("TYPE") == 0);

            var8 = var1.getString("INDEX_NAME");
         } while(var3 != null && var3.equals(var8));

         if (var4 != null && !var4.equals(var8)) {
            this.addIndex(var5, var6, var7);
            var6 = 0;
            var4 = null;
         }

         if (var4 == null) {
            var4 = var8;
            var5.clear();
         }

         if (!var1.getBoolean("NON_UNIQUE")) {
            ++var6;
         }

         var7 = var6 > 0 ? IndexType.createUnique(false, false) : IndexType.createNonUnique(false);
         String var9 = var1.getString("COLUMN_NAME");
         var9 = this.convertColumnName(var9);
         Column var10 = (Column)var2.get(var9);
         var5.add(var10);
      }
   }

   private static long convertPrecision(int var0, long var1) {
      switch (var0) {
         case 2:
         case 3:
            if (var1 == 0L) {
               var1 = 65535L;
            }
            break;
         case 91:
            var1 = Math.max(10L, var1);
            break;
         case 92:
            var1 = Math.max(18L, var1);
            break;
         case 93:
            var1 = Math.max(29L, var1);
      }

      return var1;
   }

   private static int convertScale(int var0, int var1) {
      switch (var0) {
         case 2:
         case 3:
            if (var1 < 0) {
               var1 = 32767;
            }
         default:
            return var1;
      }
   }

   private String convertColumnName(String var1) {
      if (this.targetsMySql) {
         var1 = StringUtils.toUpperEnglish(var1);
      } else if ((this.storesMixedCase || this.storesLowerCase) && var1.equals(StringUtils.toLowerEnglish(var1))) {
         var1 = StringUtils.toUpperEnglish(var1);
      } else if (this.storesMixedCase && !this.supportsMixedCaseIdentifiers) {
         var1 = StringUtils.toUpperEnglish(var1);
      } else if (this.storesMixedCase && this.storesMixedCaseQuoted) {
         var1 = StringUtils.toUpperEnglish(var1);
      }

      return var1;
   }

   private void addIndex(List<Column> var1, int var2, IndexType var3) {
      int var4 = var1.indexOf((Object)null);
      if (var4 == 0) {
         this.trace.info("Omitting linked index - no recognized columns.");
      } else {
         if (var4 > 0) {
            this.trace.info("Unrecognized columns in linked index. Registering the index against the leading {0} recognized columns of {1} total columns.", var4, var1.size());
            var1 = var1.subList(0, var4);
         }

         Column[] var5 = (Column[])var1.toArray(new Column[0]);
         LinkedIndex var6 = new LinkedIndex(this, 0, IndexColumn.wrap(var5), var2, var3);
         this.indexes.add(var6);
      }
   }

   public String getDropSQL() {
      StringBuilder var1 = new StringBuilder("DROP TABLE IF EXISTS ");
      return this.getSQL(var1, 0).toString();
   }

   public String getCreateSQL() {
      StringBuilder var1 = new StringBuilder("CREATE FORCE ");
      if (this.isTemporary()) {
         if (this.globalTemporary) {
            var1.append("GLOBAL ");
         } else {
            var1.append("LOCAL ");
         }

         var1.append("TEMPORARY ");
      }

      var1.append("LINKED TABLE ");
      this.getSQL(var1, 0);
      if (this.comment != null) {
         var1.append(" COMMENT ");
         StringUtils.quoteStringSQL(var1, this.comment);
      }

      var1.append('(');
      StringUtils.quoteStringSQL(var1, this.driver).append(", ");
      StringUtils.quoteStringSQL(var1, this.url).append(", ");
      StringUtils.quoteStringSQL(var1, this.user).append(", ");
      StringUtils.quoteStringSQL(var1, this.password).append(", ");
      StringUtils.quoteStringSQL(var1, this.originalTable).append(')');
      if (this.emitUpdates) {
         var1.append(" EMIT UPDATES");
      }

      if (this.readOnly) {
         var1.append(" READONLY");
      }

      if (this.fetchSize != 0) {
         var1.append(" FETCH_SIZE ").append(this.fetchSize);
      }

      if (!this.autocommit) {
         var1.append(" AUTOCOMMIT OFF");
      }

      var1.append(" /*").append("--hide--").append("*/");
      return var1.toString();
   }

   public Index addIndex(SessionLocal var1, String var2, int var3, IndexColumn[] var4, int var5, IndexType var6, boolean var7, String var8) {
      throw DbException.getUnsupportedException("LINK");
   }

   public Index getScanIndex(SessionLocal var1) {
      return this.linkedIndex;
   }

   public boolean isInsertable() {
      return !this.readOnly;
   }

   private void checkReadOnly() {
      if (this.readOnly) {
         throw DbException.get(90097);
      }
   }

   public void removeRow(SessionLocal var1, Row var2) {
      this.checkReadOnly();
      this.getScanIndex(var1).remove(var1, var2);
   }

   public void addRow(SessionLocal var1, Row var2) {
      this.checkReadOnly();
      this.getScanIndex(var1).add(var1, var2);
   }

   public void close(SessionLocal var1) {
      if (this.conn != null) {
         try {
            this.conn.close(false);
         } finally {
            this.conn = null;
         }
      }

   }

   public synchronized long getRowCount(SessionLocal var1) {
      String var2 = "SELECT COUNT(*) FROM " + this.qualifiedTableName + " as foo";

      try {
         PreparedStatement var3 = this.execute(var2, (ArrayList)null, false, var1);
         ResultSet var4 = var3.getResultSet();
         var4.next();
         long var5 = var4.getLong(1);
         var4.close();
         this.reusePreparedStatement(var3, var2);
         return var5;
      } catch (Exception var7) {
         throw wrapException(var2, var7);
      }
   }

   public static DbException wrapException(String var0, Exception var1) {
      SQLException var2 = DbException.toSQLException(var1);
      return DbException.get(90111, var2, var0, var2.toString());
   }

   public String getQualifiedTable() {
      return this.qualifiedTableName;
   }

   public PreparedStatement execute(String var1, ArrayList<Value> var2, boolean var3, SessionLocal var4) {
      if (this.conn == null) {
         throw this.connectException;
      } else {
         int var5 = 0;

         while(true) {
            try {
               synchronized(this.conn) {
                  PreparedStatement var7 = (PreparedStatement)this.preparedMap.remove(var1);
                  if (var7 == null) {
                     var7 = this.conn.getConnection().prepareStatement(var1);
                     if (this.fetchSize != 0) {
                        var7.setFetchSize(this.fetchSize);
                     }
                  }

                  int var9;
                  int var10;
                  Value var11;
                  if (this.trace.isDebugEnabled()) {
                     StringBuilder var8 = (new StringBuilder(this.getName())).append(":\n").append(var1);
                     if (var2 != null && !var2.isEmpty()) {
                        var8.append(" {");
                        var9 = 0;
                        var10 = var2.size();

                        while(var9 < var10) {
                           var11 = (Value)var2.get(var9);
                           if (var9 > 0) {
                              var8.append(", ");
                           }

                           ++var9;
                           var8.append(var9).append(": ");
                           var11.getSQL(var8, 0);
                        }

                        var8.append('}');
                     }

                     var8.append(';');
                     this.trace.debug(var8.toString());
                  }

                  if (var2 != null) {
                     JdbcConnection var15 = var4.createConnection(false);
                     var9 = 0;

                     for(var10 = var2.size(); var9 < var10; ++var9) {
                        var11 = (Value)var2.get(var9);
                        JdbcUtils.set(var7, var9 + 1, var11, var15);
                     }
                  }

                  var7.execute();
                  if (var3) {
                     this.reusePreparedStatement(var7, var1);
                     return null;
                  }

                  return var7;
               }
            } catch (SQLException var14) {
               if (var5 >= 2) {
                  throw DbException.convert(var14);
               }

               this.conn.close(true);
               this.connect();
               ++var5;
            }
         }
      }
   }

   public void checkSupportAlter() {
      throw DbException.getUnsupportedException("LINK");
   }

   public long truncate(SessionLocal var1) {
      throw DbException.getUnsupportedException("LINK");
   }

   public boolean canGetRowCount(SessionLocal var1) {
      return true;
   }

   public boolean canDrop() {
      return true;
   }

   public TableType getTableType() {
      return TableType.TABLE_LINK;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      super.removeChildrenAndResources(var1);
      this.close(var1);
      this.database.removeMeta(var1, this.getId());
      this.driver = null;
      this.url = this.user = this.password = this.originalTable = null;
      this.preparedMap = null;
      this.invalidate();
   }

   public boolean isOracle() {
      return this.url.startsWith("jdbc:oracle:");
   }

   private static boolean isMySqlUrl(String var0) {
      return var0.startsWith("jdbc:mysql:") || var0.startsWith("jdbc:mariadb:");
   }

   public ArrayList<Index> getIndexes() {
      return this.indexes;
   }

   public long getMaxDataModificationId() {
      return Long.MAX_VALUE;
   }

   public void updateRows(Prepared var1, SessionLocal var2, LocalResult var3) {
      this.checkReadOnly();
      if (this.emitUpdates) {
         while(var3.next()) {
            var1.checkCanceled();
            Row var4 = var3.currentRowForTable();
            var3.next();
            Row var5 = var3.currentRowForTable();
            this.linkedIndex.update(var4, var5, var2);
         }
      } else {
         super.updateRows(var1, var2, var3);
      }

   }

   public void setGlobalTemporary(boolean var1) {
      this.globalTemporary = var1;
   }

   public void setReadOnly(boolean var1) {
      this.readOnly = var1;
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return 100000L;
   }

   public void reusePreparedStatement(PreparedStatement var1, String var2) {
      synchronized(this.conn) {
         this.preparedMap.put(var2, var1);
      }
   }

   public boolean isDeterministic() {
      return false;
   }

   public void checkWritingAllowed() {
   }

   public void convertInsertRow(SessionLocal var1, Row var2, Boolean var3) {
      this.convertRow(var1, var2);
   }

   public void convertUpdateRow(SessionLocal var1, Row var2, boolean var3) {
      this.convertRow(var1, var2);
   }

   private void convertRow(SessionLocal var1, Row var2) {
      for(int var3 = 0; var3 < this.columns.length; ++var3) {
         Value var4 = var2.getValue(var3);
         if (var4 != null) {
            Column var5 = this.columns[var3];
            Value var6 = var5.validateConvertUpdateSequence(var1, var4, var2);
            if (var6 != var4) {
               var2.setValue(var3, var6);
            }
         }
      }

   }

   public void setFetchSize(int var1) {
      this.fetchSize = var1;
   }

   public void setAutoCommit(boolean var1) {
      this.autocommit = var1;
   }

   public boolean getAutocommit() {
      return this.autocommit;
   }

   public int getFetchSize() {
      return this.fetchSize;
   }
}
