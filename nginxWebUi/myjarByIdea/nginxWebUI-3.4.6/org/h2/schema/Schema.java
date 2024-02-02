package org.h2.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.h2.command.ddl.CreateSynonymData;
import org.h2.command.ddl.CreateTableData;
import org.h2.constraint.Constraint;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.DbSettings;
import org.h2.engine.Right;
import org.h2.engine.RightOwner;
import org.h2.engine.SessionLocal;
import org.h2.engine.SysProperties;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.table.MetaTable;
import org.h2.table.Table;
import org.h2.table.TableLink;
import org.h2.table.TableSynonym;
import org.h2.util.Utils;

public class Schema extends DbObject {
   private RightOwner owner;
   private final boolean system;
   private ArrayList<String> tableEngineParams;
   private final ConcurrentHashMap<String, Table> tablesAndViews;
   private final ConcurrentHashMap<String, Domain> domains;
   private final ConcurrentHashMap<String, TableSynonym> synonyms;
   private final ConcurrentHashMap<String, Index> indexes;
   private final ConcurrentHashMap<String, Sequence> sequences;
   private final ConcurrentHashMap<String, TriggerObject> triggers;
   private final ConcurrentHashMap<String, Constraint> constraints;
   private final ConcurrentHashMap<String, Constant> constants;
   private final ConcurrentHashMap<String, UserDefinedFunction> functionsAndAggregates;
   private final HashSet<String> temporaryUniqueNames = new HashSet();

   public Schema(Database var1, int var2, String var3, RightOwner var4, boolean var5) {
      super(var1, var2, var3, 8);
      this.tablesAndViews = var1.newConcurrentStringMap();
      this.domains = var1.newConcurrentStringMap();
      this.synonyms = var1.newConcurrentStringMap();
      this.indexes = var1.newConcurrentStringMap();
      this.sequences = var1.newConcurrentStringMap();
      this.triggers = var1.newConcurrentStringMap();
      this.constraints = var1.newConcurrentStringMap();
      this.constants = var1.newConcurrentStringMap();
      this.functionsAndAggregates = var1.newConcurrentStringMap();
      this.owner = var4;
      this.system = var5;
   }

   public boolean canDrop() {
      return !this.system;
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      throw DbException.getInternalError(this.toString());
   }

   public String getCreateSQL() {
      if (this.system) {
         return null;
      } else {
         StringBuilder var1 = new StringBuilder("CREATE SCHEMA IF NOT EXISTS ");
         this.getSQL(var1, 0).append(" AUTHORIZATION ");
         this.owner.getSQL(var1, 0);
         return var1.toString();
      }
   }

   public int getType() {
      return 10;
   }

   public boolean isEmpty() {
      return this.tablesAndViews.isEmpty() && this.domains.isEmpty() && this.synonyms.isEmpty() && this.indexes.isEmpty() && this.sequences.isEmpty() && this.triggers.isEmpty() && this.constraints.isEmpty() && this.constants.isEmpty() && this.functionsAndAggregates.isEmpty();
   }

   public ArrayList<DbObject> getChildren() {
      ArrayList var1 = Utils.newSmallArrayList();
      ArrayList var2 = this.database.getAllRights();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Right var4 = (Right)var3.next();
         if (var4.getGrantedObject() == this) {
            var1.add(var4);
         }
      }

      return var1;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      this.removeChildrenFromMap(var1, this.triggers);
      this.removeChildrenFromMap(var1, this.constraints);

      boolean var3;
      for(boolean var2 = true; !this.tablesAndViews.isEmpty(); var2 = var3) {
         var3 = false;
         Iterator var4 = this.tablesAndViews.values().iterator();

         while(var4.hasNext()) {
            Table var5 = (Table)var4.next();
            if (var5.getName() != null) {
               Table var6 = this.database.getDependentTable(var5, var5);
               if (var6 == null) {
                  this.database.removeSchemaObject(var1, var5);
                  var3 = true;
               } else {
                  if (var6.getSchema() != this) {
                     throw DbException.get(90107, var5.getTraceSQL(), var6.getTraceSQL());
                  }

                  if (!var2) {
                     var6.removeColumnExpressionsDependencies(var1);
                     var6.setModified();
                     this.database.updateMeta(var1, var6);
                  }
               }
            }
         }
      }

      this.removeChildrenFromMap(var1, this.domains);
      this.removeChildrenFromMap(var1, this.indexes);
      this.removeChildrenFromMap(var1, this.sequences);
      this.removeChildrenFromMap(var1, this.constants);
      this.removeChildrenFromMap(var1, this.functionsAndAggregates);
      Iterator var7 = this.database.getAllRights().iterator();

      while(var7.hasNext()) {
         Right var8 = (Right)var7.next();
         if (var8.getGrantedObject() == this) {
            this.database.removeDatabaseObject(var1, var8);
         }
      }

      this.database.removeMeta(var1, this.getId());
      this.owner = null;
      this.invalidate();
   }

   private void removeChildrenFromMap(SessionLocal var1, ConcurrentHashMap<String, ? extends SchemaObject> var2) {
      if (!var2.isEmpty()) {
         Iterator var3 = var2.values().iterator();

         while(var3.hasNext()) {
            SchemaObject var4 = (SchemaObject)var3.next();
            if (var4.isValid()) {
               this.database.removeSchemaObject(var1, var4);
            }
         }
      }

   }

   public RightOwner getOwner() {
      return this.owner;
   }

   public ArrayList<String> getTableEngineParams() {
      return this.tableEngineParams;
   }

   public void setTableEngineParams(ArrayList<String> var1) {
      this.tableEngineParams = var1;
   }

   private Map<String, SchemaObject> getMap(int var1) {
      ConcurrentHashMap var2;
      switch (var1) {
         case 0:
            var2 = this.tablesAndViews;
            break;
         case 1:
            var2 = this.indexes;
            break;
         case 2:
         case 6:
         case 7:
         case 8:
         case 10:
         case 13:
         default:
            throw DbException.getInternalError("type=" + var1);
         case 3:
            var2 = this.sequences;
            break;
         case 4:
            var2 = this.triggers;
            break;
         case 5:
            var2 = this.constraints;
            break;
         case 9:
         case 14:
            var2 = this.functionsAndAggregates;
            break;
         case 11:
            var2 = this.constants;
            break;
         case 12:
            var2 = this.domains;
            break;
         case 15:
            var2 = this.synonyms;
      }

      return var2;
   }

   public void add(SchemaObject var1) {
      if (var1.getSchema() != this) {
         throw DbException.getInternalError("wrong schema");
      } else {
         String var2 = var1.getName();
         Map var3 = this.getMap(var1.getType());
         if (var3.putIfAbsent(var2, var1) != null) {
            throw DbException.getInternalError("object already exists: " + var2);
         } else {
            this.freeUniqueName(var2);
         }
      }
   }

   public void rename(SchemaObject var1, String var2) {
      int var3 = var1.getType();
      Map var4 = this.getMap(var3);
      if (SysProperties.CHECK) {
         if (!var4.containsKey(var1.getName()) && !(var1 instanceof MetaTable)) {
            throw DbException.getInternalError("not found: " + var1.getName());
         }

         if (var1.getName().equals(var2) || var4.containsKey(var2)) {
            throw DbException.getInternalError("object already exists: " + var2);
         }
      }

      var1.checkRename();
      var4.remove(var1.getName());
      this.freeUniqueName(var1.getName());
      var1.rename(var2);
      var4.put(var2, var1);
      this.freeUniqueName(var2);
   }

   public Table findTableOrView(SessionLocal var1, String var2) {
      Table var3 = (Table)this.tablesAndViews.get(var2);
      if (var3 == null && var1 != null) {
         var3 = var1.findLocalTempTable(var2);
      }

      return var3;
   }

   public Table resolveTableOrView(SessionLocal var1, String var2) {
      Table var3 = this.findTableOrView(var1, var2);
      if (var3 == null) {
         TableSynonym var4 = (TableSynonym)this.synonyms.get(var2);
         if (var4 != null) {
            return var4.getSynonymFor();
         }
      }

      return var3;
   }

   public TableSynonym getSynonym(String var1) {
      return (TableSynonym)this.synonyms.get(var1);
   }

   public Domain findDomain(String var1) {
      return (Domain)this.domains.get(var1);
   }

   public Index findIndex(SessionLocal var1, String var2) {
      Index var3 = (Index)this.indexes.get(var2);
      if (var3 == null) {
         var3 = var1.findLocalTempTableIndex(var2);
      }

      return var3;
   }

   public TriggerObject findTrigger(String var1) {
      return (TriggerObject)this.triggers.get(var1);
   }

   public Sequence findSequence(String var1) {
      return (Sequence)this.sequences.get(var1);
   }

   public Constraint findConstraint(SessionLocal var1, String var2) {
      Constraint var3 = (Constraint)this.constraints.get(var2);
      if (var3 == null) {
         var3 = var1.findLocalTempTableConstraint(var2);
      }

      return var3;
   }

   public Constant findConstant(String var1) {
      return (Constant)this.constants.get(var1);
   }

   public FunctionAlias findFunction(String var1) {
      UserDefinedFunction var2 = this.findFunctionOrAggregate(var1);
      return var2 instanceof FunctionAlias ? (FunctionAlias)var2 : null;
   }

   public UserAggregate findAggregate(String var1) {
      UserDefinedFunction var2 = this.findFunctionOrAggregate(var1);
      return var2 instanceof UserAggregate ? (UserAggregate)var2 : null;
   }

   public UserDefinedFunction findFunctionOrAggregate(String var1) {
      return (UserDefinedFunction)this.functionsAndAggregates.get(var1);
   }

   public void reserveUniqueName(String var1) {
      if (var1 != null) {
         synchronized(this.temporaryUniqueNames) {
            this.temporaryUniqueNames.add(var1);
         }
      }

   }

   public void freeUniqueName(String var1) {
      if (var1 != null) {
         synchronized(this.temporaryUniqueNames) {
            this.temporaryUniqueNames.remove(var1);
         }
      }

   }

   private String getUniqueName(DbObject var1, Map<String, ? extends SchemaObject> var2, String var3) {
      StringBuilder var4 = new StringBuilder(var3);
      String var5 = Integer.toHexString(var1.getName().hashCode());
      synchronized(this.temporaryUniqueNames) {
         int var7 = 0;

         int var8;
         for(var8 = var5.length(); var7 < var8; ++var7) {
            char var9 = var5.charAt(var7);
            String var10 = var4.append(var9 >= 'a' ? (char)(var9 - 32) : var9).toString();
            if (!var2.containsKey(var10) && this.temporaryUniqueNames.add(var10)) {
               return var10;
            }
         }

         var7 = var4.append('_').length();
         var8 = 0;

         while(true) {
            String var13 = var4.append(var8).toString();
            if (!var2.containsKey(var13) && this.temporaryUniqueNames.add(var13)) {
               return var13;
            }

            var4.setLength(var7);
            ++var8;
         }
      }
   }

   public String getUniqueConstraintName(SessionLocal var1, Table var2) {
      Object var3;
      if (var2.isTemporary() && !var2.isGlobalTemporary()) {
         var3 = var1.getLocalTempTableConstraints();
      } else {
         var3 = this.constraints;
      }

      return this.getUniqueName(var2, (Map)var3, "CONSTRAINT_");
   }

   public String getUniqueDomainConstraintName(SessionLocal var1, Domain var2) {
      return this.getUniqueName(var2, this.constraints, "CONSTRAINT_");
   }

   public String getUniqueIndexName(SessionLocal var1, Table var2, String var3) {
      Object var4;
      if (var2.isTemporary() && !var2.isGlobalTemporary()) {
         var4 = var1.getLocalTempTableIndexes();
      } else {
         var4 = this.indexes;
      }

      return this.getUniqueName(var2, (Map)var4, var3);
   }

   public Table getTableOrView(SessionLocal var1, String var2) {
      Table var3 = (Table)this.tablesAndViews.get(var2);
      if (var3 == null) {
         if (var1 != null) {
            var3 = var1.findLocalTempTable(var2);
         }

         if (var3 == null) {
            throw DbException.get(42102, (String)var2);
         }
      }

      return var3;
   }

   public Domain getDomain(String var1) {
      Domain var2 = (Domain)this.domains.get(var1);
      if (var2 == null) {
         throw DbException.get(90120, var1);
      } else {
         return var2;
      }
   }

   public Index getIndex(String var1) {
      Index var2 = (Index)this.indexes.get(var1);
      if (var2 == null) {
         throw DbException.get(42112, (String)var1);
      } else {
         return var2;
      }
   }

   public Constraint getConstraint(String var1) {
      Constraint var2 = (Constraint)this.constraints.get(var1);
      if (var2 == null) {
         throw DbException.get(90057, var1);
      } else {
         return var2;
      }
   }

   public Constant getConstant(String var1) {
      Constant var2 = (Constant)this.constants.get(var1);
      if (var2 == null) {
         throw DbException.get(90115, var1);
      } else {
         return var2;
      }
   }

   public Sequence getSequence(String var1) {
      Sequence var2 = (Sequence)this.sequences.get(var1);
      if (var2 == null) {
         throw DbException.get(90036, var1);
      } else {
         return var2;
      }
   }

   public ArrayList<SchemaObject> getAll(ArrayList<SchemaObject> var1) {
      if (var1 == null) {
         var1 = Utils.newSmallArrayList();
      }

      var1.addAll(this.tablesAndViews.values());
      var1.addAll(this.domains.values());
      var1.addAll(this.synonyms.values());
      var1.addAll(this.sequences.values());
      var1.addAll(this.indexes.values());
      var1.addAll(this.triggers.values());
      var1.addAll(this.constraints.values());
      var1.addAll(this.constants.values());
      var1.addAll(this.functionsAndAggregates.values());
      return var1;
   }

   public void getAll(int var1, ArrayList<SchemaObject> var2) {
      var2.addAll(this.getMap(var1).values());
   }

   public Collection<Domain> getAllDomains() {
      return this.domains.values();
   }

   public Collection<Constraint> getAllConstraints() {
      return this.constraints.values();
   }

   public Collection<Constant> getAllConstants() {
      return this.constants.values();
   }

   public Collection<Sequence> getAllSequences() {
      return this.sequences.values();
   }

   public Collection<TriggerObject> getAllTriggers() {
      return this.triggers.values();
   }

   public Collection<Table> getAllTablesAndViews(SessionLocal var1) {
      return this.tablesAndViews.values();
   }

   public Collection<Index> getAllIndexes() {
      return this.indexes.values();
   }

   public Collection<TableSynonym> getAllSynonyms() {
      return this.synonyms.values();
   }

   public Collection<UserDefinedFunction> getAllFunctionsAndAggregates() {
      return this.functionsAndAggregates.values();
   }

   public Table getTableOrViewByName(SessionLocal var1, String var2) {
      return (Table)this.tablesAndViews.get(var2);
   }

   public void remove(SchemaObject var1) {
      String var2 = var1.getName();
      Map var3 = this.getMap(var1.getType());
      if (var3.remove(var2) == null) {
         throw DbException.getInternalError("not found: " + var2);
      } else {
         this.freeUniqueName(var2);
      }
   }

   public Table createTable(CreateTableData var1) {
      synchronized(this.database) {
         if (!var1.temporary || var1.globalTemporary) {
            this.database.lockMeta(var1.session);
         }

         var1.schema = this;
         String var3 = var1.tableEngine;
         if (var3 == null) {
            DbSettings var4 = this.database.getSettings();
            var3 = var4.defaultTableEngine;
            if (var3 == null) {
               return this.database.getStore().createTable(var1);
            }

            var1.tableEngine = var3;
         }

         if (var1.tableEngineParams == null) {
            var1.tableEngineParams = this.tableEngineParams;
         }

         return this.database.getTableEngine(var3).createTable(var1);
      }
   }

   public TableSynonym createSynonym(CreateSynonymData var1) {
      synchronized(this.database) {
         this.database.lockMeta(var1.session);
         var1.schema = this;
         return new TableSynonym(var1);
      }
   }

   public TableLink createTableLink(int var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, boolean var9, boolean var10) {
      synchronized(this.database) {
         return new TableLink(this, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }
   }
}
