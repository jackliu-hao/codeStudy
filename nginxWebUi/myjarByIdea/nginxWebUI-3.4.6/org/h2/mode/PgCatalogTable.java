package org.h2.mode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.constraint.Constraint;
import org.h2.engine.CastDataProvider;
import org.h2.engine.RightOwner;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.schema.Schema;
import org.h2.server.pg.PgServer;
import org.h2.table.Column;
import org.h2.table.MetaTable;
import org.h2.table.Table;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueSmallint;

public final class PgCatalogTable extends MetaTable {
   private static final int PG_AM = 0;
   private static final int PG_ATTRDEF = 1;
   private static final int PG_ATTRIBUTE = 2;
   private static final int PG_AUTHID = 3;
   private static final int PG_CLASS = 4;
   private static final int PG_CONSTRAINT = 5;
   private static final int PG_DATABASE = 6;
   private static final int PG_DESCRIPTION = 7;
   private static final int PG_GROUP = 8;
   private static final int PG_INDEX = 9;
   private static final int PG_INHERITS = 10;
   private static final int PG_NAMESPACE = 11;
   private static final int PG_PROC = 12;
   private static final int PG_ROLES = 13;
   private static final int PG_SETTINGS = 14;
   private static final int PG_TABLESPACE = 15;
   private static final int PG_TRIGGER = 16;
   private static final int PG_TYPE = 17;
   private static final int PG_USER = 18;
   public static final int META_TABLE_TYPE_COUNT = 19;
   private static final Object[][] PG_EXTRA_TYPES = new Object[][]{{18, "char", 1, 0}, {19, "name", 64, 18}, {22, "int2vector", -1, 21}, {24, "regproc", 4, 0}, {1005, "_int2", -1, 21}, {1007, "_int4", -1, 23}, {1015, "_varchar", -1, 1043}, {2205, "regclass", 4, 0}};

   public PgCatalogTable(Schema var1, int var2, int var3) {
      super(var1, var2, var3);
      Column[] var4;
      switch (var3) {
         case 0:
            this.setMetaTableName("PG_AM");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("AMNAME", TypeInfo.TYPE_VARCHAR)};
            break;
         case 1:
            this.setMetaTableName("PG_ATTRDEF");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("ADSRC", TypeInfo.TYPE_INTEGER), this.column("ADRELID", TypeInfo.TYPE_INTEGER), this.column("ADNUM", TypeInfo.TYPE_INTEGER), this.column("ADBIN", TypeInfo.TYPE_VARCHAR)};
            break;
         case 2:
            this.setMetaTableName("PG_ATTRIBUTE");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("ATTRELID", TypeInfo.TYPE_INTEGER), this.column("ATTNAME", TypeInfo.TYPE_VARCHAR), this.column("ATTTYPID", TypeInfo.TYPE_INTEGER), this.column("ATTLEN", TypeInfo.TYPE_INTEGER), this.column("ATTNUM", TypeInfo.TYPE_INTEGER), this.column("ATTTYPMOD", TypeInfo.TYPE_INTEGER), this.column("ATTNOTNULL", TypeInfo.TYPE_BOOLEAN), this.column("ATTISDROPPED", TypeInfo.TYPE_BOOLEAN), this.column("ATTHASDEF", TypeInfo.TYPE_BOOLEAN)};
            break;
         case 3:
            this.setMetaTableName("PG_AUTHID");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("ROLNAME", TypeInfo.TYPE_VARCHAR), this.column("ROLSUPER", TypeInfo.TYPE_BOOLEAN), this.column("ROLINHERIT", TypeInfo.TYPE_BOOLEAN), this.column("ROLCREATEROLE", TypeInfo.TYPE_BOOLEAN), this.column("ROLCREATEDB", TypeInfo.TYPE_BOOLEAN), this.column("ROLCATUPDATE", TypeInfo.TYPE_BOOLEAN), this.column("ROLCANLOGIN", TypeInfo.TYPE_BOOLEAN), this.column("ROLCONNLIMIT", TypeInfo.TYPE_BOOLEAN), this.column("ROLPASSWORD", TypeInfo.TYPE_BOOLEAN), this.column("ROLVALIDUNTIL", TypeInfo.TYPE_TIMESTAMP_TZ), this.column("ROLCONFIG", TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.TYPE_VARCHAR))};
            break;
         case 4:
            this.setMetaTableName("PG_CLASS");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("RELNAME", TypeInfo.TYPE_VARCHAR), this.column("RELNAMESPACE", TypeInfo.TYPE_INTEGER), this.column("RELKIND", TypeInfo.TYPE_CHAR), this.column("RELAM", TypeInfo.TYPE_INTEGER), this.column("RELTUPLES", TypeInfo.TYPE_DOUBLE), this.column("RELTABLESPACE", TypeInfo.TYPE_INTEGER), this.column("RELPAGES", TypeInfo.TYPE_INTEGER), this.column("RELHASINDEX", TypeInfo.TYPE_BOOLEAN), this.column("RELHASRULES", TypeInfo.TYPE_BOOLEAN), this.column("RELHASOIDS", TypeInfo.TYPE_BOOLEAN), this.column("RELCHECKS", TypeInfo.TYPE_SMALLINT), this.column("RELTRIGGERS", TypeInfo.TYPE_INTEGER)};
            break;
         case 5:
            this.setMetaTableName("PG_CONSTRAINT");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("CONNAME", TypeInfo.TYPE_VARCHAR), this.column("CONTYPE", TypeInfo.TYPE_VARCHAR), this.column("CONRELID", TypeInfo.TYPE_INTEGER), this.column("CONFRELID", TypeInfo.TYPE_INTEGER), this.column("CONKEY", TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.TYPE_SMALLINT))};
            break;
         case 6:
            this.setMetaTableName("PG_DATABASE");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("DATNAME", TypeInfo.TYPE_VARCHAR), this.column("ENCODING", TypeInfo.TYPE_INTEGER), this.column("DATLASTSYSOID", TypeInfo.TYPE_INTEGER), this.column("DATALLOWCONN", TypeInfo.TYPE_BOOLEAN), this.column("DATCONFIG", TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.TYPE_VARCHAR)), this.column("DATACL", TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.TYPE_VARCHAR)), this.column("DATDBA", TypeInfo.TYPE_INTEGER), this.column("DATTABLESPACE", TypeInfo.TYPE_INTEGER)};
            break;
         case 7:
            this.setMetaTableName("PG_DESCRIPTION");
            var4 = new Column[]{this.column("OBJOID", TypeInfo.TYPE_INTEGER), this.column("OBJSUBID", TypeInfo.TYPE_INTEGER), this.column("CLASSOID", TypeInfo.TYPE_INTEGER), this.column("DESCRIPTION", TypeInfo.TYPE_VARCHAR)};
            break;
         case 8:
            this.setMetaTableName("PG_GROUP");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("GRONAME", TypeInfo.TYPE_VARCHAR)};
            break;
         case 9:
            this.setMetaTableName("PG_INDEX");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("INDEXRELID", TypeInfo.TYPE_INTEGER), this.column("INDRELID", TypeInfo.TYPE_INTEGER), this.column("INDISCLUSTERED", TypeInfo.TYPE_BOOLEAN), this.column("INDISUNIQUE", TypeInfo.TYPE_BOOLEAN), this.column("INDISPRIMARY", TypeInfo.TYPE_BOOLEAN), this.column("INDEXPRS", TypeInfo.TYPE_VARCHAR), this.column("INDKEY", TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.TYPE_INTEGER)), this.column("INDPRED", TypeInfo.TYPE_VARCHAR)};
            break;
         case 10:
            this.setMetaTableName("PG_INHERITS");
            var4 = new Column[]{this.column("INHRELID", TypeInfo.TYPE_INTEGER), this.column("INHPARENT", TypeInfo.TYPE_INTEGER), this.column("INHSEQNO", TypeInfo.TYPE_INTEGER)};
            break;
         case 11:
            this.setMetaTableName("PG_NAMESPACE");
            var4 = new Column[]{this.column("ID", TypeInfo.TYPE_INTEGER), this.column("NSPNAME", TypeInfo.TYPE_VARCHAR)};
            break;
         case 12:
            this.setMetaTableName("PG_PROC");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("PRONAME", TypeInfo.TYPE_VARCHAR), this.column("PRORETTYPE", TypeInfo.TYPE_INTEGER), this.column("PROARGTYPES", TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.TYPE_INTEGER)), this.column("PRONAMESPACE", TypeInfo.TYPE_INTEGER)};
            break;
         case 13:
            this.setMetaTableName("PG_ROLES");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("ROLNAME", TypeInfo.TYPE_VARCHAR), this.column("ROLSUPER", TypeInfo.TYPE_CHAR), this.column("ROLCREATEROLE", TypeInfo.TYPE_CHAR), this.column("ROLCREATEDB", TypeInfo.TYPE_CHAR)};
            break;
         case 14:
            this.setMetaTableName("PG_SETTINGS");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("NAME", TypeInfo.TYPE_VARCHAR), this.column("SETTING", TypeInfo.TYPE_VARCHAR)};
            break;
         case 15:
            this.setMetaTableName("PG_TABLESPACE");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("SPCNAME", TypeInfo.TYPE_VARCHAR), this.column("SPCLOCATION", TypeInfo.TYPE_VARCHAR), this.column("SPCOWNER", TypeInfo.TYPE_INTEGER), this.column("SPCACL", TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.TYPE_VARCHAR))};
            break;
         case 16:
            this.setMetaTableName("PG_TRIGGER");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("TGCONSTRRELID", TypeInfo.TYPE_INTEGER), this.column("TGFOID", TypeInfo.TYPE_INTEGER), this.column("TGARGS", TypeInfo.TYPE_INTEGER), this.column("TGNARGS", TypeInfo.TYPE_INTEGER), this.column("TGDEFERRABLE", TypeInfo.TYPE_BOOLEAN), this.column("TGINITDEFERRED", TypeInfo.TYPE_BOOLEAN), this.column("TGCONSTRNAME", TypeInfo.TYPE_VARCHAR), this.column("TGRELID", TypeInfo.TYPE_INTEGER)};
            break;
         case 17:
            this.setMetaTableName("PG_TYPE");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("TYPNAME", TypeInfo.TYPE_VARCHAR), this.column("TYPNAMESPACE", TypeInfo.TYPE_INTEGER), this.column("TYPLEN", TypeInfo.TYPE_INTEGER), this.column("TYPTYPE", TypeInfo.TYPE_VARCHAR), this.column("TYPDELIM", TypeInfo.TYPE_VARCHAR), this.column("TYPRELID", TypeInfo.TYPE_INTEGER), this.column("TYPELEM", TypeInfo.TYPE_INTEGER), this.column("TYPBASETYPE", TypeInfo.TYPE_INTEGER), this.column("TYPTYPMOD", TypeInfo.TYPE_INTEGER), this.column("TYPNOTNULL", TypeInfo.TYPE_BOOLEAN), this.column("TYPINPUT", TypeInfo.TYPE_VARCHAR)};
            break;
         case 18:
            this.setMetaTableName("PG_USER");
            var4 = new Column[]{this.column("OID", TypeInfo.TYPE_INTEGER), this.column("USENAME", TypeInfo.TYPE_VARCHAR), this.column("USECREATEDB", TypeInfo.TYPE_BOOLEAN), this.column("USESUPER", TypeInfo.TYPE_BOOLEAN)};
            break;
         default:
            throw DbException.getInternalError("type=" + var3);
      }

      this.setColumns(var4);
      this.indexColumn = -1;
      this.metaIndex = null;
   }

   public ArrayList<Row> generateRows(SessionLocal var1, SearchRow var2, SearchRow var3) {
      ArrayList var4 = Utils.newSmallArrayList();
      String var5 = this.database.getShortName();
      boolean var6 = var1.getUser().isAdmin();
      Iterator var7;
      RightOwner var8;
      int var14;
      int var17;
      Schema var18;
      int var24;
      Iterator var25;
      Table var27;
      Table var29;
      switch (this.type) {
         case 0:
            String[] var21 = new String[]{"btree", "hash"};
            var14 = 0;

            for(var17 = var21.length; var14 < var17; ++var14) {
               this.add(var1, var4, new Object[]{ValueInteger.get(var14), var21[var14]});
            }
         case 1:
         case 3:
         case 8:
         case 9:
         case 10:
         case 12:
         case 16:
            break;
         case 2:
            var7 = this.database.getAllSchemas().iterator();

            while(var7.hasNext()) {
               var18 = (Schema)var7.next();
               var25 = var18.getAllTablesAndViews(var1).iterator();

               while(var25.hasNext()) {
                  var29 = (Table)var25.next();
                  if (!this.hideTable(var29, var1)) {
                     this.pgAttribute(var1, var4, var29);
                  }
               }
            }

            var7 = var1.getLocalTempTables().iterator();

            while(var7.hasNext()) {
               var27 = (Table)var7.next();
               if (!this.hideTable(var27, var1)) {
                  this.pgAttribute(var1, var4, var27);
               }
            }

            return var4;
         case 4:
            var7 = this.database.getAllSchemas().iterator();

            while(var7.hasNext()) {
               var18 = (Schema)var7.next();
               var25 = var18.getAllTablesAndViews(var1).iterator();

               while(var25.hasNext()) {
                  var29 = (Table)var25.next();
                  if (!this.hideTable(var29, var1)) {
                     this.pgClass(var1, var4, var29);
                  }
               }
            }

            var7 = var1.getLocalTempTables().iterator();

            while(var7.hasNext()) {
               var27 = (Table)var7.next();
               if (!this.hideTable(var27, var1)) {
                  this.pgClass(var1, var4, var27);
               }
            }

            return var4;
         case 5:
            this.pgConstraint(var1, var4);
            break;
         case 6:
            int var15 = Integer.MAX_VALUE;
            Iterator var22 = this.database.getAllUsersAndRoles().iterator();

            while(var22.hasNext()) {
               RightOwner var23 = (RightOwner)var22.next();
               if (var23 instanceof User && ((User)var23).isAdmin()) {
                  var24 = var23.getId();
                  if (var24 < var15) {
                     var15 = var24;
                  }
               }
            }

            this.add(var1, var4, new Object[]{ValueInteger.get(100001), var5, ValueInteger.get(6), ValueInteger.get(100000), ValueBoolean.TRUE, null, null, ValueInteger.get(var15), ValueInteger.get(0)});
            break;
         case 7:
            this.add(var1, var4, new Object[]{ValueInteger.get(0), ValueInteger.get(0), ValueInteger.get(-1), var5});
            break;
         case 11:
            var7 = this.database.getAllSchemas().iterator();

            while(var7.hasNext()) {
               var18 = (Schema)var7.next();
               this.add(var1, var4, new Object[]{ValueInteger.get(var18.getId()), var18.getName()});
            }

            return var4;
         case 13:
            var7 = this.database.getAllUsersAndRoles().iterator();

            while(true) {
               do {
                  if (!var7.hasNext()) {
                     return var4;
                  }

                  var8 = (RightOwner)var7.next();
               } while(!var6 && var1.getUser() != var8);

               String var20 = var8 instanceof User && ((User)var8).isAdmin() ? "t" : "f";
               this.add(var1, var4, new Object[]{ValueInteger.get(var8.getId()), this.identifier(var8.getName()), var20, var20, var20});
            }
         case 14:
            String[][] var13 = new String[][]{{"autovacuum", "on"}, {"stats_start_collector", "on"}, {"stats_row_level", "on"}};
            var14 = 0;

            for(var17 = var13.length; var14 < var17; ++var14) {
               String[] var26 = var13[var14];
               this.add(var1, var4, new Object[]{ValueInteger.get(var14), var26[0], var26[1]});
            }

            return var4;
         case 15:
            this.add(var1, var4, new Object[]{ValueInteger.get(0), "main", "?", ValueInteger.get(0), null});
            break;
         case 17:
            HashSet var12 = new HashSet();
            var14 = 1;

            for(var17 = 42; var14 < var17; ++var14) {
               DataType var19 = DataType.getDataType(var14);
               if (var19.type != 40) {
                  int var11 = PgServer.convertType(TypeInfo.getTypeInfo(var19.type));
                  if (var11 != 705 && var12.add(var11)) {
                     this.add(var1, var4, new Object[]{ValueInteger.get(var11), Value.getTypeName(var19.type), ValueInteger.get(-1000), ValueInteger.get(-1), "b", ",", ValueInteger.get(0), ValueInteger.get(0), ValueInteger.get(0), ValueInteger.get(-1), ValueBoolean.FALSE, null});
                  }
               }
            }

            Object[][] var16 = PG_EXTRA_TYPES;
            var17 = var16.length;

            for(var24 = 0; var24 < var17; ++var24) {
               Object[] var28 = var16[var24];
               this.add(var1, var4, new Object[]{ValueInteger.get((Integer)var28[0]), var28[1], ValueInteger.get(-1000), ValueInteger.get((Integer)var28[2]), "b", ",", ValueInteger.get(0), ValueInteger.get((Integer)var28[3]), ValueInteger.get(0), ValueInteger.get(-1), ValueBoolean.FALSE, null});
            }

            return var4;
         case 18:
            var7 = this.database.getAllUsersAndRoles().iterator();

            while(true) {
               User var9;
               do {
                  do {
                     if (!var7.hasNext()) {
                        return var4;
                     }

                     var8 = (RightOwner)var7.next();
                  } while(!(var8 instanceof User));

                  var9 = (User)var8;
               } while(!var6 && var1.getUser() != var9);

               ValueBoolean var10 = ValueBoolean.get(var9.isAdmin());
               this.add(var1, var4, new Object[]{ValueInteger.get(var9.getId()), this.identifier(var9.getName()), var10, var10});
            }
         default:
            throw DbException.getInternalError("type=" + this.type);
      }

      return var4;
   }

   private void pgAttribute(SessionLocal var1, ArrayList<Row> var2, Table var3) {
      Column[] var4 = var3.getColumns();
      int var5 = var3.getId();
      int var6 = 0;

      while(var6 < var4.length) {
         Column var7 = var4[var6++];
         this.addAttribute(var1, var2, var5 * 10000 + var6, var5, var3, var7, var6);
      }

      Iterator var11 = var3.getIndexes().iterator();

      while(true) {
         Index var12;
         do {
            if (!var11.hasNext()) {
               return;
            }

            var12 = (Index)var11.next();
         } while(var12.getCreateSQL() == null);

         var4 = var12.getColumns();
         int var8 = 0;

         while(var8 < var4.length) {
            Column var9 = var4[var8++];
            int var10 = var12.getId();
            this.addAttribute(var1, var2, 1000000 * var10 + var5 * 10000 + var8, var10, var3, var9, var8);
         }
      }
   }

   private void pgClass(SessionLocal var1, ArrayList<Row> var2, Table var3) {
      ArrayList var4 = var3.getTriggers();
      this.addClass(var1, var2, var3.getId(), var3.getName(), var3.getSchema().getId(), var3.isView() ? "v" : "r", false, var4 != null ? var4.size() : 0);
      ArrayList var5 = var3.getIndexes();
      if (var5 != null) {
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            Index var7 = (Index)var6.next();
            if (var7.getCreateSQL() != null) {
               this.addClass(var1, var2, var7.getId(), var7.getName(), var7.getSchema().getId(), "i", true, 0);
            }
         }
      }

   }

   private void pgConstraint(SessionLocal var1, ArrayList<Row> var2) {
      Iterator var3 = this.database.getAllSchemasNoMeta().iterator();

      label50:
      while(var3.hasNext()) {
         Schema var4 = (Schema)var3.next();
         Iterator var5 = var4.getAllConstraints().iterator();

         while(true) {
            Constraint var6;
            Constraint.Type var7;
            Table var8;
            do {
               do {
                  if (!var5.hasNext()) {
                     continue label50;
                  }

                  var6 = (Constraint)var5.next();
                  var7 = var6.getConstraintType();
               } while(var7 == Constraint.Type.DOMAIN);

               var8 = var6.getTable();
            } while(this.hideTable(var8, var1));

            ArrayList var9 = new ArrayList();
            Iterator var10 = var6.getReferencedColumns(var8).iterator();

            while(var10.hasNext()) {
               Column var11 = (Column)var10.next();
               var9.add(ValueSmallint.get((short)(var11.getColumnId() + 1)));
            }

            Table var12 = var6.getRefTable();
            this.add(var1, var2, new Object[]{ValueInteger.get(var6.getId()), var6.getName(), StringUtils.toLowerEnglish(var7.getSqlName().substring(0, 1)), ValueInteger.get(var8.getId()), ValueInteger.get(var12 != null && var12 != var8 && !this.hideTable(var12, var1) ? var8.getId() : 0), ValueArray.get(TypeInfo.TYPE_SMALLINT, (Value[])var9.toArray(Value.EMPTY_VALUES), (CastDataProvider)null)});
         }
      }

   }

   private void addAttribute(SessionLocal var1, ArrayList<Row> var2, int var3, int var4, Table var5, Column var6, int var7) {
      long var8 = var6.getType().getPrecision();
      this.add(var1, var2, new Object[]{ValueInteger.get(var3), ValueInteger.get(var4), var6.getName(), ValueInteger.get(PgServer.convertType(var6.getType())), ValueInteger.get(var8 > 255L ? -1 : (int)var8), ValueInteger.get(var7), ValueInteger.get(-1), ValueBoolean.get(!var6.isNullable()), ValueBoolean.FALSE, ValueBoolean.FALSE});
   }

   private void addClass(SessionLocal var1, ArrayList<Row> var2, int var3, String var4, int var5, String var6, boolean var7, int var8) {
      this.add(var1, var2, new Object[]{ValueInteger.get(var3), var4, ValueInteger.get(var5), var6, ValueInteger.get(0), ValueDouble.get(0.0), ValueInteger.get(0), ValueInteger.get(0), ValueBoolean.get(var7), ValueBoolean.FALSE, ValueBoolean.FALSE, ValueSmallint.get((short)0), ValueInteger.get(var8)});
   }

   public long getMaxDataModificationId() {
      return this.database.getModificationDataId();
   }
}
