package org.h2.command.dml;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.constraint.Constraint;
import org.h2.engine.Comment;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.Right;
import org.h2.engine.RightOwner;
import org.h2.engine.Role;
import org.h2.engine.SessionLocal;
import org.h2.engine.Setting;
import org.h2.engine.User;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.index.Cursor;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.mvstore.DataUtils;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.schema.Constant;
import org.h2.schema.Domain;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.schema.Sequence;
import org.h2.schema.TriggerObject;
import org.h2.schema.UserDefinedFunction;
import org.h2.table.Column;
import org.h2.table.PlanItem;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.table.TableType;
import org.h2.util.IOUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueVarchar;

public class ScriptCommand extends ScriptBase {
   private static final Comparator<? super DbObject> BY_NAME_COMPARATOR = (var0, var1) -> {
      if (var0 instanceof SchemaObject && var1 instanceof SchemaObject) {
         int var2 = ((SchemaObject)var0).getSchema().getName().compareTo(((SchemaObject)var1).getSchema().getName());
         if (var2 != 0) {
            return var2;
         }
      }

      return var0.getName().compareTo(var1.getName());
   };
   private Charset charset;
   private java.util.Set<String> schemaNames;
   private Collection<Table> tables;
   private boolean passwords;
   private boolean data;
   private boolean settings;
   private boolean drop;
   private boolean simple;
   private boolean withColumns;
   private boolean version;
   private LocalResult result;
   private String lineSeparatorString;
   private byte[] lineSeparator;
   private byte[] buffer;
   private boolean tempLobTableCreated;
   private int nextLobId;
   private int lobBlockSize;

   public ScriptCommand(SessionLocal var1) {
      super(var1);
      this.charset = StandardCharsets.UTF_8;
      this.version = true;
      this.lobBlockSize = 4096;
   }

   public boolean isQuery() {
      return true;
   }

   public void setSchemaNames(java.util.Set<String> var1) {
      this.schemaNames = var1;
   }

   public void setTables(Collection<Table> var1) {
      this.tables = var1;
   }

   public void setData(boolean var1) {
      this.data = var1;
   }

   public void setPasswords(boolean var1) {
      this.passwords = var1;
   }

   public void setSettings(boolean var1) {
      this.settings = var1;
   }

   public void setLobBlockSize(long var1) {
      this.lobBlockSize = MathUtils.convertLongToInt(var1);
   }

   public void setDrop(boolean var1) {
      this.drop = var1;
   }

   public ResultInterface queryMeta() {
      LocalResult var1 = this.createResult();
      var1.done();
      return var1;
   }

   private LocalResult createResult() {
      return new LocalResult(this.session, new Expression[]{new ExpressionColumn(this.session.getDatabase(), new Column("SCRIPT", TypeInfo.TYPE_VARCHAR))}, 1, 1);
   }

   public ResultInterface query(long var1) {
      this.session.getUser().checkAdmin();
      this.reset();
      Database var3 = this.session.getDatabase();
      Iterator var4;
      if (this.schemaNames != null) {
         var4 = this.schemaNames.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            Schema var6 = var3.findSchema(var5);
            if (var6 == null) {
               throw DbException.get(90079, var5);
            }
         }
      }

      try {
         this.result = this.createResult();
         this.deleteStore();
         this.openOutput();
         if (this.out != null) {
            this.buffer = new byte[4096];
         }

         if (this.version) {
            this.add("-- H2 " + Constants.VERSION, true);
         }

         if (this.settings) {
            var4 = var3.getAllSettings().iterator();

            while(var4.hasNext()) {
               Setting var23 = (Setting)var4.next();
               if (!var23.getName().equals(SetTypes.getTypeName(28))) {
                  this.add(var23.getCreateSQL(), false);
               }
            }
         }

         if (this.out != null) {
            this.add("", true);
         }

         RightOwner[] var21 = (RightOwner[])var3.getAllUsersAndRoles().toArray(new RightOwner[0]);
         Arrays.sort(var21, (var0, var1x) -> {
            boolean var2 = var0 instanceof User;
            if (var2 != (var1x instanceof User)) {
               return var2 ? -1 : 1;
            } else {
               if (var2) {
                  var2 = ((User)var0).isAdmin();
                  if (var2 != ((User)var1x).isAdmin()) {
                     return var2 ? -1 : 1;
                  }
               }

               return var0.getName().compareTo(var1x.getName());
            }
         });
         RightOwner[] var24 = var21;
         int var26 = var21.length;

         int var7;
         for(var7 = 0; var7 < var26; ++var7) {
            RightOwner var8 = var24[var7];
            if (var8 instanceof User) {
               this.add(((User)var8).getCreateSQL(this.passwords), false);
            } else {
               this.add(((Role)var8).getCreateSQL(true), false);
            }
         }

         ArrayList var25 = new ArrayList();
         Iterator var27 = var3.getAllSchemas().iterator();

         Schema var29;
         while(var27.hasNext()) {
            var29 = (Schema)var27.next();
            if (!this.excludeSchema(var29)) {
               var25.add(var29);
               this.add(var29.getCreateSQL(), false);
            }
         }

         this.dumpDomains(var25);
         var27 = var25.iterator();

         int var10;
         while(var27.hasNext()) {
            var29 = (Schema)var27.next();
            Constant[] var31 = (Constant[])sorted(var29.getAllConstants(), Constant.class);
            int var9 = var31.length;

            for(var10 = 0; var10 < var9; ++var10) {
               Constant var11 = var31[var10];
               this.add(var11.getCreateSQL(), false);
            }
         }

         ArrayList var28 = var3.getAllTablesAndViews();
         var28.sort(Comparator.comparingInt(DbObject::getId));
         Iterator var30 = var28.iterator();

         while(var30.hasNext()) {
            Table var32 = (Table)var30.next();
            if (!this.excludeSchema(var32.getSchema()) && !this.excludeTable(var32) && !var32.isHidden()) {
               var32.lock(this.session, 0);
               String var34 = var32.getCreateSQL();
               if (var34 != null && this.drop) {
                  this.add(var32.getDropSQL(), false);
               }
            }
         }

         var30 = var25.iterator();

         Schema var33;
         int var41;
         while(var30.hasNext()) {
            var33 = (Schema)var30.next();
            UserDefinedFunction[] var35 = (UserDefinedFunction[])sorted(var33.getAllFunctionsAndAggregates(), UserDefinedFunction.class);
            var10 = var35.length;

            for(var41 = 0; var41 < var10; ++var41) {
               UserDefinedFunction var12 = var35[var41];
               if (this.drop) {
                  this.add(var12.getDropSQL(), false);
               }

               this.add(var12.getCreateSQL(), false);
            }
         }

         var30 = var25.iterator();

         while(var30.hasNext()) {
            var33 = (Schema)var30.next();
            Sequence[] var37 = (Sequence[])sorted(var33.getAllSequences(), Sequence.class);
            var10 = var37.length;

            for(var41 = 0; var41 < var10; ++var41) {
               Sequence var45 = var37[var41];
               if (!var45.getBelongsToTable()) {
                  if (this.drop) {
                     this.add(var45.getDropSQL(), false);
                  }

                  this.add(var45.getCreateSQL(), false);
               }
            }
         }

         var7 = 0;
         Iterator var36 = var28.iterator();

         label580:
         while(true) {
            Table var39;
            String var42;
            do {
               do {
                  do {
                     do {
                        if (!var36.hasNext()) {
                           if (this.tempLobTableCreated) {
                              this.add("DROP TABLE IF EXISTS SYSTEM_LOB_STREAM", true);
                              this.add("DROP ALIAS IF EXISTS SYSTEM_COMBINE_CLOB", true);
                              this.add("DROP ALIAS IF EXISTS SYSTEM_COMBINE_BLOB", true);
                              this.tempLobTableCreated = false;
                           }

                           ArrayList var38 = new ArrayList();
                           Iterator var40 = var25.iterator();

                           Schema var43;
                           Iterator var49;
                           label601:
                           while(var40.hasNext()) {
                              var43 = (Schema)var40.next();
                              var49 = var43.getAllConstraints().iterator();

                              while(true) {
                                 Constraint var50;
                                 Constraint.Type var54;
                                 do {
                                    do {
                                       if (!var49.hasNext()) {
                                          continue label601;
                                       }

                                       var50 = (Constraint)var49.next();
                                    } while(this.excludeTable(var50.getTable()));

                                    var54 = var50.getConstraintType();
                                 } while(var54 != Constraint.Type.DOMAIN && var50.getTable().isHidden());

                                 if (var54 != Constraint.Type.PRIMARY_KEY) {
                                    var38.add(var50);
                                 }
                              }
                           }

                           var38.sort((Comparator)null);
                           var40 = var38.iterator();

                           while(var40.hasNext()) {
                              Constraint var44 = (Constraint)var40.next();
                              this.add(var44.getCreateSQLWithoutIndexes(), false);
                           }

                           var40 = var25.iterator();

                           while(var40.hasNext()) {
                              var43 = (Schema)var40.next();
                              var49 = var43.getAllTriggers().iterator();

                              while(var49.hasNext()) {
                                 TriggerObject var51 = (TriggerObject)var49.next();
                                 if (!this.excludeTable(var51.getTable())) {
                                    this.add(var51.getCreateSQL(), false);
                                 }
                              }
                           }

                           this.dumpRights(var3);
                           var40 = var3.getAllComments().iterator();

                           while(var40.hasNext()) {
                              Comment var48 = (Comment)var40.next();
                              this.add(var48.getCreateSQL(), false);
                           }

                           if (this.out != null) {
                              this.out.close();
                           }
                           break label580;
                        }

                        var39 = (Table)var36.next();
                     } while(this.excludeSchema(var39.getSchema()));
                  } while(this.excludeTable(var39));
               } while(var39.isHidden());

               var39.lock(this.session, 0);
               var42 = var39.getCreateSQL();
            } while(var42 == null);

            TableType var46 = var39.getTableType();
            this.add(var42, false);
            ArrayList var47 = var39.getConstraints();
            if (var47 != null) {
               Iterator var13 = var47.iterator();

               while(var13.hasNext()) {
                  Constraint var14 = (Constraint)var13.next();
                  if (Constraint.Type.PRIMARY_KEY == var14.getConstraintType()) {
                     this.add(var14.getCreateSQLWithoutIndexes(), false);
                  }
               }
            }

            if (TableType.TABLE == var46) {
               if (var39.canGetRowCount(this.session)) {
                  StringBuilder var52 = (new StringBuilder("-- ")).append(var39.getRowCountApproximation(this.session)).append(" +/- SELECT COUNT(*) FROM ");
                  var39.getSQL(var52, 3);
                  this.add(var52.toString(), false);
               }

               if (this.data) {
                  var7 = this.generateInsertValues(var7, var39);
               }
            }

            ArrayList var53 = var39.getIndexes();

            for(int var55 = 0; var53 != null && var55 < var53.size(); ++var55) {
               Index var15 = (Index)var53.get(var55);
               if (!var15.getIndexType().getBelongsToConstraint()) {
                  this.add(var15.getCreateSQL(), false);
               }
            }
         }
      } catch (IOException var19) {
         throw DbException.convertIOException(var19, this.getFileName());
      } finally {
         this.closeIO();
      }

      this.result.done();
      LocalResult var22 = this.result;
      this.reset();
      return var22;
   }

   private void dumpDomains(ArrayList<Schema> var1) throws IOException {
      TreeMap var2 = new TreeMap(BY_NAME_COMPARATOR);
      TreeSet var3 = new TreeSet(BY_NAME_COMPARATOR);
      Iterator var4 = var1.iterator();

      Domain var9;
      while(var4.hasNext()) {
         Schema var5 = (Schema)var4.next();
         Domain[] var6 = (Domain[])sorted(var5.getAllDomains(), Domain.class);
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            var9 = var6[var8];
            Domain var10 = var9.getDomain();
            if (var10 == null) {
               this.addDomain(var9);
            } else {
               TreeSet var11 = (TreeSet)var2.get(var10);
               if (var11 == null) {
                  var11 = new TreeSet(BY_NAME_COMPARATOR);
                  var2.put(var10, var11);
               }

               var11.add(var9);
               if (var10.getDomain() == null || !var1.contains(var10.getSchema())) {
                  var3.add(var10);
               }
            }
         }
      }

      TreeSet var12;
      label45:
      for(; !var2.isEmpty(); var3 = var12) {
         var12 = new TreeSet(BY_NAME_COMPARATOR);
         Iterator var13 = var3.iterator();

         while(true) {
            TreeSet var15;
            do {
               if (!var13.hasNext()) {
                  continue label45;
               }

               Domain var14 = (Domain)var13.next();
               var15 = (TreeSet)var2.remove(var14);
            } while(var15 == null);

            Iterator var16 = var15.iterator();

            while(var16.hasNext()) {
               var9 = (Domain)var16.next();
               this.addDomain(var9);
               var12.add(var9);
            }
         }
      }

   }

   private void dumpRights(Database var1) throws IOException {
      Right[] var2 = (Right[])var1.getAllRights().toArray(new Right[0]);
      Arrays.sort(var2, (var0, var1x) -> {
         Role var2 = var0.getGrantedRole();
         Role var3 = var1x.getGrantedRole();
         if (var2 == null != (var3 == null)) {
            return var2 == null ? -1 : 1;
         } else {
            if (var2 == null) {
               DbObject var4 = var0.getGrantedObject();
               DbObject var5 = var1x.getGrantedObject();
               if (var4 == null != (var5 == null)) {
                  return var4 == null ? -1 : 1;
               }

               if (var4 != null) {
                  if (var4 instanceof Schema != (var5 instanceof Schema)) {
                     return var4 instanceof Schema ? -1 : 1;
                  }

                  int var6 = var4.getName().compareTo(var5.getName());
                  if (var6 != 0) {
                     return var6;
                  }
               }
            } else {
               int var7 = var2.getName().compareTo(var3.getName());
               if (var7 != 0) {
                  return var7;
               }
            }

            return var0.getGrantee().getName().compareTo(var1x.getGrantee().getName());
         }
      });
      Right[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Right var6 = var3[var5];
         DbObject var7 = var6.getGrantedObject();
         if (var7 != null) {
            if (var7 instanceof Schema) {
               if (this.excludeSchema((Schema)var7)) {
                  continue;
               }
            } else if (var7 instanceof Table) {
               Table var8 = (Table)var7;
               if (this.excludeSchema(var8.getSchema()) || this.excludeTable(var8)) {
                  continue;
               }
            }
         }

         this.add(var6.getCreateSQL(), false);
      }

   }

   private void addDomain(Domain var1) throws IOException {
      if (this.drop) {
         this.add(var1.getDropSQL(), false);
      }

      this.add(var1.getCreateSQL(), false);
   }

   private static <T extends DbObject> T[] sorted(Collection<T> var0, Class<T> var1) {
      DbObject[] var2 = (DbObject[])var0.toArray((DbObject[])((DbObject[])Array.newInstance(var1, 0)));
      Arrays.sort(var2, BY_NAME_COMPARATOR);
      return var2;
   }

   private int generateInsertValues(int var1, Table var2) throws IOException {
      PlanItem var3 = var2.getBestPlanItem(this.session, (int[])null, (TableFilter[])null, -1, (SortOrder)null, (AllColumnsForPlan)null);
      Index var4 = var3.getIndex();
      Cursor var5 = var4.find(this.session, (SearchRow)null, (SearchRow)null);
      Column[] var6 = var2.getColumns();
      boolean var7 = false;
      boolean var8 = false;
      Column[] var9 = var6;
      int var10 = var6.length;

      int var11;
      for(var11 = 0; var11 < var10; ++var11) {
         Column var12 = var9[var11];
         if (var12.isGeneratedAlways()) {
            if (var12.isIdentity()) {
               var8 = true;
            } else {
               var7 = true;
            }
         }
      }

      StringBuilder var17 = new StringBuilder("INSERT INTO ");
      var2.getSQL(var17, 0);
      if (var7 || var8 || this.withColumns) {
         var17.append('(');
         boolean var18 = false;
         Column[] var20 = var6;
         int var21 = var6.length;

         for(int var13 = 0; var13 < var21; ++var13) {
            Column var14 = var20[var13];
            if (!var14.isGenerated()) {
               if (var18) {
                  var17.append(", ");
               }

               var18 = true;
               var14.getSQL(var17, 0);
            }
         }

         var17.append(')');
         if (var8) {
            var17.append(" OVERRIDING SYSTEM VALUE");
         }
      }

      var17.append(" VALUES");
      if (!this.simple) {
         var17.append('\n');
      }

      var17.append('(');
      String var19 = var17.toString();
      var17 = null;
      var11 = var6.length;

      while(true) {
         do {
            if (!var5.next()) {
               if (var17 != null) {
                  this.add(var17.toString(), true);
               }

               return var1;
            }

            Row var22 = var5.get();
            if (var17 == null) {
               var17 = new StringBuilder(var19);
            } else {
               var17.append(",\n(");
            }

            boolean var23 = false;

            for(int var24 = 0; var24 < var11; ++var24) {
               if (!var6[var24].isGenerated()) {
                  if (var23) {
                     var17.append(", ");
                  }

                  var23 = true;
                  Value var15 = var22.getValue(var24);
                  if (var15.getType().getPrecision() > (long)this.lobBlockSize) {
                     int var16;
                     if (var15.getValueType() == 3) {
                        var16 = this.writeLobStream(var15);
                        var17.append("SYSTEM_COMBINE_CLOB(").append(var16).append(')');
                     } else if (var15.getValueType() == 7) {
                        var16 = this.writeLobStream(var15);
                        var17.append("SYSTEM_COMBINE_BLOB(").append(var16).append(')');
                     } else {
                        var15.getSQL(var17, 4);
                     }
                  } else {
                     var15.getSQL(var17, 4);
                  }
               }
            }

            var17.append(')');
            ++var1;
            if ((var1 & 127) == 0) {
               this.checkCanceled();
            }
         } while(!this.simple && var17.length() <= 4096);

         this.add(var17.toString(), true);
         var17 = null;
      }
   }

   private int writeLobStream(Value var1) throws IOException {
      if (!this.tempLobTableCreated) {
         this.add("CREATE CACHED LOCAL TEMPORARY TABLE IF NOT EXISTS SYSTEM_LOB_STREAM(ID INT NOT NULL, PART INT NOT NULL, CDATA VARCHAR, BDATA VARBINARY)", true);
         this.add("ALTER TABLE SYSTEM_LOB_STREAM ADD CONSTRAINT SYSTEM_LOB_STREAM_PRIMARY_KEY PRIMARY KEY(ID, PART)", true);
         String var2 = this.getClass().getName();
         this.add("CREATE ALIAS IF NOT EXISTS SYSTEM_COMBINE_CLOB FOR '" + var2 + ".combineClob'", true);
         this.add("CREATE ALIAS IF NOT EXISTS SYSTEM_COMBINE_BLOB FOR '" + var2 + ".combineBlob'", true);
         this.tempLobTableCreated = true;
      }

      int var35 = this.nextLobId++;
      Throwable var5;
      int var6;
      StringBuilder var7;
      int var8;
      String var9;
      switch (var1.getValueType()) {
         case 3:
            char[] var36 = new char[this.lobBlockSize];
            Reader var37 = var1.getReader();
            var5 = null;

            try {
               var6 = 0;

               while(true) {
                  var7 = new StringBuilder(this.lobBlockSize * 2);
                  var7.append("INSERT INTO SYSTEM_LOB_STREAM VALUES(").append(var35).append(", ").append(var6).append(", ");
                  var8 = IOUtils.readFully(var37, var36, this.lobBlockSize);
                  if (var8 == 0) {
                     return var35;
                  }

                  StringUtils.quoteStringSQL(var7, new String(var36, 0, var8)).append(", NULL)");
                  var9 = var7.toString();
                  this.add(var9, true);
                  ++var6;
               }
            } catch (Throwable var31) {
               var5 = var31;
               throw var31;
            } finally {
               if (var37 != null) {
                  if (var5 != null) {
                     try {
                        var37.close();
                     } catch (Throwable var30) {
                        var5.addSuppressed(var30);
                     }
                  } else {
                     var37.close();
                  }
               }

            }
         case 7:
            byte[] var3 = new byte[this.lobBlockSize];
            InputStream var4 = var1.getInputStream();
            var5 = null;

            try {
               var6 = 0;

               while(true) {
                  var7 = new StringBuilder(this.lobBlockSize * 2);
                  var7.append("INSERT INTO SYSTEM_LOB_STREAM VALUES(").append(var35).append(", ").append(var6).append(", NULL, X'");
                  var8 = IOUtils.readFully(var4, var3, this.lobBlockSize);
                  if (var8 <= 0) {
                     return var35;
                  }

                  StringUtils.convertBytesToHex(var7, var3, var8).append("')");
                  var9 = var7.toString();
                  this.add(var9, true);
                  ++var6;
               }
            } catch (Throwable var33) {
               var5 = var33;
               throw var33;
            } finally {
               if (var4 != null) {
                  if (var5 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var29) {
                        var5.addSuppressed(var29);
                     }
                  } else {
                     var4.close();
                  }
               }

            }
         default:
            throw DbException.getInternalError("type:" + var1.getValueType());
      }
   }

   public static InputStream combineBlob(Connection var0, int var1) throws SQLException {
      if (var1 < 0) {
         return null;
      } else {
         final ResultSet var2 = getLobStream(var0, "BDATA", var1);
         return new InputStream() {
            private InputStream current;
            private boolean closed;

            public int read() throws IOException {
               while(true) {
                  try {
                     if (this.current == null) {
                        if (this.closed) {
                           return -1;
                        }

                        if (!var2.next()) {
                           this.close();
                           return -1;
                        }

                        this.current = var2.getBinaryStream(1);
                        this.current = new BufferedInputStream(this.current);
                     }

                     int var1 = this.current.read();
                     if (var1 >= 0) {
                        return var1;
                     }

                     this.current = null;
                  } catch (SQLException var2x) {
                     throw DataUtils.convertToIOException(var2x);
                  }
               }
            }

            public void close() throws IOException {
               if (!this.closed) {
                  this.closed = true;

                  try {
                     var2.close();
                  } catch (SQLException var2x) {
                     throw DataUtils.convertToIOException(var2x);
                  }
               }
            }
         };
      }
   }

   public static Reader combineClob(Connection var0, int var1) throws SQLException {
      if (var1 < 0) {
         return null;
      } else {
         final ResultSet var2 = getLobStream(var0, "CDATA", var1);
         return new Reader() {
            private Reader current;
            private boolean closed;

            public int read() throws IOException {
               while(true) {
                  try {
                     if (this.current == null) {
                        if (this.closed) {
                           return -1;
                        }

                        if (!var2.next()) {
                           this.close();
                           return -1;
                        }

                        this.current = var2.getCharacterStream(1);
                        this.current = new BufferedReader(this.current);
                     }

                     int var1 = this.current.read();
                     if (var1 >= 0) {
                        return var1;
                     }

                     this.current = null;
                  } catch (SQLException var2x) {
                     throw DataUtils.convertToIOException(var2x);
                  }
               }
            }

            public void close() throws IOException {
               if (!this.closed) {
                  this.closed = true;

                  try {
                     var2.close();
                  } catch (SQLException var2x) {
                     throw DataUtils.convertToIOException(var2x);
                  }
               }
            }

            public int read(char[] var1, int var2x, int var3) throws IOException {
               if (var3 == 0) {
                  return 0;
               } else {
                  int var4 = this.read();
                  if (var4 == -1) {
                     return -1;
                  } else {
                     var1[var2x] = (char)var4;

                     int var5;
                     for(var5 = 1; var5 < var3; ++var5) {
                        var4 = this.read();
                        if (var4 == -1) {
                           break;
                        }

                        var1[var2x + var5] = (char)var4;
                     }

                     return var5;
                  }
               }
            }
         };
      }
   }

   private static ResultSet getLobStream(Connection var0, String var1, int var2) throws SQLException {
      PreparedStatement var3 = var0.prepareStatement("SELECT " + var1 + " FROM SYSTEM_LOB_STREAM WHERE ID=? ORDER BY PART");
      var3.setInt(1, var2);
      return var3.executeQuery();
   }

   private void reset() {
      this.result = null;
      this.buffer = null;
      this.lineSeparatorString = System.lineSeparator();
      this.lineSeparator = this.lineSeparatorString.getBytes(this.charset);
   }

   private boolean excludeSchema(Schema var1) {
      if (this.schemaNames != null && !this.schemaNames.contains(var1.getName())) {
         return true;
      } else if (this.tables != null) {
         Iterator var2 = var1.getAllTablesAndViews(this.session).iterator();

         Table var3;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (Table)var2.next();
         } while(!this.tables.contains(var3));

         return false;
      } else {
         return false;
      }
   }

   private boolean excludeTable(Table var1) {
      return this.tables != null && !this.tables.contains(var1);
   }

   private void add(String var1, boolean var2) throws IOException {
      if (var1 != null) {
         if (this.lineSeparator.length > 1 || this.lineSeparator[0] != 10) {
            var1 = StringUtils.replaceAll(var1, "\n", this.lineSeparatorString);
         }

         var1 = var1 + ";";
         if (this.out != null) {
            byte[] var3 = var1.getBytes(this.charset);
            int var4 = MathUtils.roundUpInt(var3.length + this.lineSeparator.length, 16);
            this.buffer = Utils.copy(var3, this.buffer);
            if (var4 > this.buffer.length) {
               this.buffer = new byte[var4];
            }

            System.arraycopy(var3, 0, this.buffer, 0, var3.length);

            int var5;
            for(var5 = var3.length; var5 < var4 - this.lineSeparator.length; ++var5) {
               this.buffer[var5] = 32;
            }

            var5 = 0;

            for(int var6 = var4 - this.lineSeparator.length; var6 < var4; ++var5) {
               this.buffer[var6] = this.lineSeparator[var5];
               ++var6;
            }

            this.out.write(this.buffer, 0, var4);
            if (!var2) {
               this.result.addRow(ValueVarchar.get(var1));
            }
         } else {
            this.result.addRow(ValueVarchar.get(var1));
         }

      }
   }

   public void setSimple(boolean var1) {
      this.simple = var1;
   }

   public void setWithColumns(boolean var1) {
      this.withColumns = var1;
   }

   public void setVersion(boolean var1) {
      this.version = var1;
   }

   public void setCharset(Charset var1) {
      this.charset = var1;
   }

   public int getType() {
      return 65;
   }
}
