package org.h2.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.h2.engine.MetaRecord;
import org.h2.message.DbException;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.MVStoreTool;
import org.h2.mvstore.StreamStore;
import org.h2.mvstore.db.LobStorageMap;
import org.h2.mvstore.db.ValueDataType;
import org.h2.mvstore.tx.TransactionMap;
import org.h2.mvstore.tx.TransactionStore;
import org.h2.mvstore.type.MetaType;
import org.h2.mvstore.type.StringDataType;
import org.h2.result.Row;
import org.h2.store.DataHandler;
import org.h2.store.FileLister;
import org.h2.store.FileStore;
import org.h2.store.LobStorageInterface;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.SmallLRUCache;
import org.h2.util.StringUtils;
import org.h2.util.TempFileDeleter;
import org.h2.util.Tool;
import org.h2.value.CompareMode;
import org.h2.value.Value;
import org.h2.value.ValueCollectionBase;
import org.h2.value.ValueLob;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;

public class Recover extends Tool implements DataHandler {
   private String databaseName;
   private int storageId;
   private String storageName;
   private int recordLength;
   private int valueId;
   private boolean trace;
   private ArrayList<MetaRecord> schema;
   private HashSet<Integer> objectIdSet;
   private HashMap<Integer, String> tableMap;
   private HashMap<String, String> columnTypeMap;
   private boolean lobMaps;

   public static void main(String... var0) throws SQLException {
      (new Recover()).runTool(var0);
   }

   public void runTool(String... var1) throws SQLException {
      String var2 = ".";
      String var3 = null;

      for(int var4 = 0; var1 != null && var4 < var1.length; ++var4) {
         String var5 = var1[var4];
         if ("-dir".equals(var5)) {
            ++var4;
            var2 = var1[var4];
         } else if ("-db".equals(var5)) {
            ++var4;
            var3 = var1[var4];
         } else if ("-trace".equals(var5)) {
            this.trace = true;
         } else {
            if (var5.equals("-help") || var5.equals("-?")) {
               this.showUsage();
               return;
            }

            this.showUsageAndThrowUnsupportedOption(var5);
         }
      }

      this.process(var2, var3);
   }

   public static InputStream readBlobMap(Connection var0, long var1, long var3) throws SQLException {
      final PreparedStatement var5 = var0.prepareStatement("SELECT DATA FROM INFORMATION_SCHEMA.LOB_BLOCKS WHERE LOB_ID = ? AND SEQ = ? AND ? > 0");
      var5.setLong(1, var1);
      var5.setLong(3, var3);
      return new SequenceInputStream(new Enumeration<InputStream>() {
         private int seq;
         private byte[] data = this.fetch();

         private byte[] fetch() {
            try {
               var5.setInt(2, this.seq++);
               ResultSet var1 = var5.executeQuery();
               return var1.next() ? var1.getBytes(1) : null;
            } catch (SQLException var2) {
               throw DbException.convert(var2);
            }
         }

         public boolean hasMoreElements() {
            return this.data != null;
         }

         public InputStream nextElement() {
            ByteArrayInputStream var1 = new ByteArrayInputStream(this.data);
            this.data = this.fetch();
            return var1;
         }
      });
   }

   public static Reader readClobMap(Connection var0, long var1, long var3) throws Exception {
      InputStream var5 = readBlobMap(var0, var1, var3);
      return new BufferedReader(new InputStreamReader(var5, StandardCharsets.UTF_8));
   }

   private void trace(String var1) {
      if (this.trace) {
         this.out.println(var1);
      }

   }

   private void traceError(String var1, Throwable var2) {
      this.out.println(var1 + ": " + var2.toString());
      if (this.trace) {
         var2.printStackTrace(this.out);
      }

   }

   public static void execute(String var0, String var1) throws SQLException {
      try {
         (new Recover()).process(var0, var1);
      } catch (DbException var3) {
         throw DbException.toSQLException(var3);
      }
   }

   private void process(String var1, String var2) {
      ArrayList var3 = FileLister.getDatabaseFiles(var1, var2, true);
      if (var3.isEmpty()) {
         this.printNoDatabaseFilesFound(var1, var2);
      }

      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         if (var5.endsWith(".mv.db")) {
            String var6 = var5.substring(0, var5.length() - ".mv.db".length());
            PrintWriter var7 = this.getWriter(var5, ".txt");
            Throwable var8 = null;

            try {
               MVStoreTool.dump(var5, var7, true);
               MVStoreTool.info(var5, var7);
            } catch (Throwable var32) {
               var8 = var32;
               throw var32;
            } finally {
               if (var7 != null) {
                  if (var8 != null) {
                     try {
                        var7.close();
                     } catch (Throwable var30) {
                        var8.addSuppressed(var30);
                     }
                  } else {
                     var7.close();
                  }
               }

            }

            var7 = this.getWriter(var6 + ".h2.db", ".sql");
            var8 = null;

            try {
               this.dumpMVStoreFile(var7, var5);
            } catch (Throwable var31) {
               var8 = var31;
               throw var31;
            } finally {
               if (var7 != null) {
                  if (var8 != null) {
                     try {
                        var7.close();
                     } catch (Throwable var29) {
                        var8.addSuppressed(var29);
                     }
                  } else {
                     var7.close();
                  }
               }

            }
         }
      }

   }

   private PrintWriter getWriter(String var1, String var2) {
      var1 = var1.substring(0, var1.length() - 3);
      String var3 = var1 + var2;
      this.trace("Created file: " + var3);

      try {
         return new PrintWriter(IOUtils.getBufferedWriter(FileUtils.newOutputStream(var3, false)));
      } catch (IOException var5) {
         throw DbException.convertIOException(var5, (String)null);
      }
   }

   private void getSQL(StringBuilder var1, String var2, Value var3) {
      if (var3 instanceof ValueLob) {
         ValueLob var4 = (ValueLob)var3;
         LobData var5 = var4.getLobData();
         if (var5 instanceof LobDataDatabase) {
            LobDataDatabase var6 = (LobDataDatabase)var5;
            int var7 = var3.getValueType();
            long var8 = var6.getLobId();
            long var10;
            String var12;
            if (var7 == 7) {
               var10 = var4.octetLength();
               var12 = "BLOB";
               var1.append("READ_BLOB");
            } else {
               var10 = var4.charLength();
               var12 = "CLOB";
               var1.append("READ_CLOB");
            }

            if (this.lobMaps) {
               var1.append("_MAP");
            } else {
               var1.append("_DB");
            }

            this.columnTypeMap.put(var2, var12);
            var1.append('(').append(var8).append(", ").append(var10).append(')');
            return;
         }
      }

      var3.getSQL(var1, 4);
   }

   private void setDatabaseName(String var1) {
      this.databaseName = var1;
   }

   private void dumpMVStoreFile(PrintWriter var1, String var2) {
      var1.println("-- MVStore");
      String var3 = this.getClass().getName();
      var1.println("CREATE ALIAS IF NOT EXISTS READ_BLOB_MAP FOR '" + var3 + ".readBlobMap';");
      var1.println("CREATE ALIAS IF NOT EXISTS READ_CLOB_MAP FOR '" + var3 + ".readClobMap';");
      this.resetSchema();
      this.setDatabaseName(var2.substring(0, var2.length() - ".mv.db".length()));

      try {
         MVStore var4 = (new MVStore.Builder()).fileName(var2).recoveryMode().readOnly().open();
         Throwable var5 = null;

         try {
            this.dumpLobMaps(var1, var4);
            var1.println("-- Layout");
            dumpLayout(var1, var4);
            var1.println("-- Meta");
            dumpMeta(var1, var4);
            var1.println("-- Types");
            dumpTypes(var1, var4);
            var1.println("-- Tables");
            TransactionStore var6 = new TransactionStore(var4, new ValueDataType());

            try {
               var6.init();
            } catch (Throwable var29) {
               this.writeError(var1, var29);
            }

            Iterator var7 = var4.getMapNames().iterator();

            while(true) {
               String var8;
               String var9;
               TransactionMap var10;
               Iterator var11;
               do {
                  do {
                     if (!var7.hasNext()) {
                        this.writeSchemaSET(var1);
                        var1.println("---- Table Data ----");
                        var7 = var4.getMapNames().iterator();

                        while(true) {
                           do {
                              do {
                                 if (!var7.hasNext()) {
                                    this.writeSchema(var1);
                                    var1.println("DROP ALIAS READ_BLOB_MAP;");
                                    var1.println("DROP ALIAS READ_CLOB_MAP;");
                                    var1.println("DROP TABLE IF EXISTS INFORMATION_SCHEMA.LOB_BLOCKS;");
                                    return;
                                 }

                                 var8 = (String)var7.next();
                              } while(!var8.startsWith("table."));

                              var9 = var8.substring("table.".length());
                           } while(Integer.parseInt(var9) == 0);

                           var10 = var6.begin().openMap(var8);
                           var11 = var10.keyIterator((Object)null);
                           boolean var33 = false;

                           while(var11.hasNext()) {
                              Object var34 = var11.next();
                              Object var14 = var10.get(var34);
                              Value[] var15;
                              if (var14 instanceof Row) {
                                 var15 = ((Row)var14).getValueList();
                                 this.recordLength = var15.length;
                              } else {
                                 var15 = ((ValueCollectionBase)var14).getList();
                                 this.recordLength = var15.length - 1;
                              }

                              StringBuilder var16;
                              String var17;
                              if (!var33) {
                                 this.setStorage(Integer.parseInt(var9));
                                 var16 = new StringBuilder();

                                 for(this.valueId = 0; this.valueId < this.recordLength; ++this.valueId) {
                                    var17 = this.storageName + "." + this.valueId;
                                    var16.setLength(0);
                                    this.getSQL(var16, var17, var15[this.valueId]);
                                 }

                                 this.createTemporaryTable(var1);
                                 var33 = true;
                              }

                              var16 = new StringBuilder();
                              var16.append("INSERT INTO O_").append(var9).append(" VALUES(");

                              for(this.valueId = 0; this.valueId < this.recordLength; ++this.valueId) {
                                 if (this.valueId > 0) {
                                    var16.append(", ");
                                 }

                                 var17 = this.storageName + "." + this.valueId;
                                 this.getSQL(var16, var17, var15[this.valueId]);
                              }

                              var16.append(");");
                              var1.println(var16.toString());
                           }
                        }
                     }

                     var8 = (String)var7.next();
                  } while(!var8.startsWith("table."));

                  var9 = var8.substring("table.".length());
               } while(Integer.parseInt(var9) != 0);

               var10 = var6.begin().openMap(var8);
               var11 = var10.keyIterator((Object)null);

               while(var11.hasNext()) {
                  Long var12 = (Long)var11.next();
                  Row var13 = (Row)var10.get(var12);

                  try {
                     this.writeMetaRow(var13);
                  } catch (Throwable var28) {
                     this.writeError(var1, var28);
                  }
               }
            }
         } catch (Throwable var30) {
            var5 = var30;
            throw var30;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var27) {
                     var5.addSuppressed(var27);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (Throwable var32) {
         this.writeError(var1, var32);
      }
   }

   private static void dumpLayout(PrintWriter var0, MVStore var1) {
      MVMap var2 = var1.getLayoutMap();
      Iterator var3 = var2.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         var0.println("-- " + (String)var4.getKey() + " = " + (String)var4.getValue());
      }

   }

   private static void dumpMeta(PrintWriter var0, MVStore var1) {
      MVMap var2 = var1.getMetaMap();
      Iterator var3 = var2.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         var0.println("-- " + (String)var4.getKey() + " = " + (String)var4.getValue());
      }

   }

   private static void dumpTypes(PrintWriter var0, MVStore var1) {
      MVMap.Builder var2 = (new MVMap.Builder()).keyType(StringDataType.INSTANCE).valueType(new MetaType((Object)null, (Thread.UncaughtExceptionHandler)null));
      MVMap var3 = var1.openMap("_", var2);
      Iterator var4 = var3.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry var5 = (Map.Entry)var4.next();
         var0.println("-- " + (String)var5.getKey() + " = " + var5.getValue());
      }

   }

   private void dumpLobMaps(PrintWriter var1, MVStore var2) {
      this.lobMaps = var2.hasMap("lobData");
      if (this.lobMaps) {
         TransactionStore var3 = new TransactionStore(var2);
         MVMap var4 = LobStorageMap.openLobDataMap(var3);
         StreamStore var5 = new StreamStore(var4);
         MVMap var6 = LobStorageMap.openLobMap(var3);
         var1.println("-- LOB");
         var1.println("CREATE TABLE IF NOT EXISTS INFORMATION_SCHEMA.LOB_BLOCKS(LOB_ID BIGINT, SEQ INT, DATA VARBINARY, PRIMARY KEY(LOB_ID, SEQ));");
         boolean var7 = false;
         Iterator var8 = var6.entrySet().iterator();

         while(var8.hasNext()) {
            Map.Entry var9 = (Map.Entry)var8.next();
            long var10 = (Long)var9.getKey();
            LobStorageMap.BlobMeta var12 = (LobStorageMap.BlobMeta)var9.getValue();
            byte[] var13 = var12.streamStoreId;
            InputStream var14 = var5.get(var13);
            short var15 = 8192;
            byte[] var16 = new byte[var15];

            try {
               int var17 = 0;

               while(true) {
                  int var18 = IOUtils.readFully(var14, var16, var16.length);
                  if (var18 > 0) {
                     var1.print("INSERT INTO INFORMATION_SCHEMA.LOB_BLOCKS VALUES(" + var10 + ", " + var17 + ", X'");
                     var1.print(StringUtils.convertBytesToHex(var16, var18));
                     var1.println("');");
                  }

                  if (var18 != var15) {
                     break;
                  }

                  ++var17;
               }
            } catch (IOException var19) {
               this.writeError(var1, var19);
               var7 = true;
            }
         }

         var1.println("-- lobMap.size: " + var6.sizeAsLong());
         var1.println("-- lobData.size: " + var4.sizeAsLong());
         if (var7) {
            var1.println("-- lobMap");
            var8 = var6.keyList().iterator();

            Long var20;
            while(var8.hasNext()) {
               var20 = (Long)var8.next();
               LobStorageMap.BlobMeta var21 = (LobStorageMap.BlobMeta)var6.get(var20);
               byte[] var11 = var21.streamStoreId;
               var1.println("--     " + var20 + " " + StreamStore.toString(var11));
            }

            var1.println("-- lobData");
            var8 = var4.keyList().iterator();

            while(var8.hasNext()) {
               var20 = (Long)var8.next();
               var1.println("--     " + var20 + " len " + ((byte[])var4.get(var20)).length);
            }
         }

      }
   }

   private String setStorage(int var1) {
      this.storageId = var1;
      this.storageName = "O_" + Integer.toString(var1).replace('-', 'M');
      return this.storageName;
   }

   private void writeMetaRow(Row var1) {
      MetaRecord var2 = new MetaRecord(var1);
      int var3 = var2.getObjectType();
      if (var3 != 1 || !var2.getSQL().startsWith("CREATE PRIMARY KEY ")) {
         this.schema.add(var2);
         if (var3 == 0) {
            this.tableMap.put(var2.getId(), extractTableOrViewName(var2.getSQL()));
         }

      }
   }

   private void resetSchema() {
      this.schema = new ArrayList();
      this.objectIdSet = new HashSet();
      this.tableMap = new HashMap();
      this.columnTypeMap = new HashMap();
   }

   private void writeSchemaSET(PrintWriter var1) {
      var1.println("---- Schema SET ----");
      Iterator var2 = this.schema.iterator();

      while(var2.hasNext()) {
         MetaRecord var3 = (MetaRecord)var2.next();
         if (var3.getObjectType() == 6) {
            String var4 = var3.getSQL();
            var1.println(var4 + ";");
         }
      }

   }

   private void writeSchema(PrintWriter var1) {
      var1.println("---- Schema ----");
      Collections.sort(this.schema);
      Iterator var2 = this.schema.iterator();

      while(var2.hasNext()) {
         MetaRecord var3 = (MetaRecord)var2.next();
         if (var3.getObjectType() != 6 && !isSchemaObjectTypeDelayed(var3)) {
            String var4 = var3.getSQL();
            var1.println(var4 + ";");
         }
      }

      boolean var7 = false;
      Iterator var8 = this.tableMap.entrySet().iterator();

      while(true) {
         String var6;
         do {
            Integer var5;
            do {
               do {
                  Map.Entry var9;
                  if (!var8.hasNext()) {
                     var8 = this.tableMap.entrySet().iterator();

                     while(var8.hasNext()) {
                        var9 = (Map.Entry)var8.next();
                        var5 = (Integer)var9.getKey();
                        var6 = (String)var9.getValue();
                        if (this.objectIdSet.contains(var5)) {
                           this.setStorage(var5);
                           if (!isLobTable(var6)) {
                              var1.println("INSERT INTO " + var6 + " SELECT * FROM " + this.storageName + ";");
                           }
                        }
                     }

                     var8 = this.objectIdSet.iterator();

                     while(var8.hasNext()) {
                        Integer var11 = (Integer)var8.next();
                        this.setStorage(var11);
                        var1.println("DROP TABLE " + this.storageName + ";");
                     }

                     if (var7) {
                        var1.println("DELETE FROM INFORMATION_SCHEMA.LOBS WHERE `TABLE` = -2;");
                     }

                     ArrayList var10 = new ArrayList();
                     Iterator var12 = this.schema.iterator();

                     while(true) {
                        while(true) {
                           MetaRecord var13;
                           do {
                              if (!var12.hasNext()) {
                                 var12 = var10.iterator();

                                 while(var12.hasNext()) {
                                    String var14 = (String)var12.next();
                                    var1.println(var14 + ';');
                                 }

                                 return;
                              }

                              var13 = (MetaRecord)var12.next();
                           } while(!isSchemaObjectTypeDelayed(var13));

                           var6 = var13.getSQL();
                           if (var13.getObjectType() == 5 && var6.endsWith("NOCHECK") && var6.contains(" FOREIGN KEY") && var6.contains("REFERENCES ")) {
                              var10.add(var6);
                           } else {
                              var1.println(var6 + ';');
                           }
                        }
                     }
                  }

                  var9 = (Map.Entry)var8.next();
                  var5 = (Integer)var9.getKey();
                  var6 = (String)var9.getValue();
               } while(!this.objectIdSet.contains(var5));
            } while(!isLobTable(var6));

            this.setStorage(var5);
            var1.println("DELETE FROM " + var6 + ";");
            var1.println("INSERT INTO " + var6 + " SELECT * FROM " + this.storageName + ";");
         } while(!var6.equals("INFORMATION_SCHEMA.LOBS") && !var6.equalsIgnoreCase("\"INFORMATION_SCHEMA\".\"LOBS\""));

         var1.println("UPDATE " + var6 + " SET `TABLE` = " + -2 + ";");
         var7 = true;
      }
   }

   private static boolean isLobTable(String var0) {
      return var0.startsWith("INFORMATION_SCHEMA.LOB") || var0.startsWith("\"INFORMATION_SCHEMA\".\"LOB") || var0.startsWith("\"information_schema\".\"lob");
   }

   private static boolean isSchemaObjectTypeDelayed(MetaRecord var0) {
      switch (var0.getObjectType()) {
         case 1:
         case 4:
         case 5:
            return true;
         case 2:
         case 3:
         default:
            return false;
      }
   }

   private void createTemporaryTable(PrintWriter var1) {
      if (!this.objectIdSet.contains(this.storageId)) {
         this.objectIdSet.add(this.storageId);
         var1.write("CREATE TABLE ");
         var1.write(this.storageName);
         var1.write(40);

         for(int var2 = 0; var2 < this.recordLength; ++var2) {
            if (var2 > 0) {
               var1.print(", ");
            }

            var1.write(67);
            var1.print(var2);
            var1.write(32);
            String var3 = (String)this.columnTypeMap.get(this.storageName + "." + var2);
            var1.write(var3 == null ? "VARCHAR" : var3);
         }

         var1.println(");");
         var1.flush();
      }

   }

   private static String extractTableOrViewName(String var0) {
      int var1 = var0.indexOf(" TABLE ");
      int var2 = var0.indexOf(" VIEW ");
      if (var1 > 0 && var2 > 0) {
         if (var1 < var2) {
            var2 = -1;
         } else {
            var1 = -1;
         }
      }

      if (var2 > 0) {
         var0 = var0.substring(var2 + " VIEW ".length());
      } else {
         if (var1 <= 0) {
            return "UNKNOWN";
         }

         var0 = var0.substring(var1 + " TABLE ".length());
      }

      if (var0.startsWith("IF NOT EXISTS ")) {
         var0 = var0.substring("IF NOT EXISTS ".length());
      }

      boolean var3 = false;

      for(int var4 = 0; var4 < var0.length(); ++var4) {
         char var5 = var0.charAt(var4);
         if (var5 == '"') {
            var3 = !var3;
         } else if (!var3 && (var5 <= ' ' || var5 == '(')) {
            var0 = var0.substring(0, var4);
            return var0;
         }
      }

      return "UNKNOWN";
   }

   private void writeError(PrintWriter var1, Throwable var2) {
      if (var1 != null) {
         var1.println("// error: " + var2);
      }

      this.traceError("Error", var2);
   }

   public String getDatabasePath() {
      return this.databaseName;
   }

   public FileStore openFile(String var1, String var2, boolean var3) {
      return FileStore.open(this, var1, "rw");
   }

   public void checkPowerOff() {
   }

   public void checkWritingAllowed() {
   }

   public int getMaxLengthInplaceLob() {
      throw DbException.getInternalError();
   }

   public Object getLobSyncObject() {
      return this;
   }

   public SmallLRUCache<String, String[]> getLobFileListCache() {
      return null;
   }

   public TempFileDeleter getTempFileDeleter() {
      return TempFileDeleter.getInstance();
   }

   public LobStorageInterface getLobStorage() {
      return null;
   }

   public int readLob(long var1, byte[] var3, long var4, byte[] var6, int var7, int var8) {
      throw DbException.getInternalError();
   }

   public CompareMode getCompareMode() {
      return CompareMode.getInstance((String)null, 0);
   }
}
