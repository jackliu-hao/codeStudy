package org.h2.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.h2.command.Prepared;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.constraint.Constraint;
import org.h2.engine.CastDataProvider;
import org.h2.engine.DbObject;
import org.h2.engine.Right;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.result.DefaultRow;
import org.h2.result.LocalResult;
import org.h2.result.Row;
import org.h2.result.RowFactory;
import org.h2.result.SearchRow;
import org.h2.result.SimpleRowValue;
import org.h2.result.SortOrder;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.schema.Sequence;
import org.h2.schema.TriggerObject;
import org.h2.util.Utils;
import org.h2.value.CompareMode;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public abstract class Table extends SchemaObject {
   public static final int TYPE_CACHED = 0;
   public static final int TYPE_MEMORY = 1;
   public static final int READ_LOCK = 0;
   public static final int WRITE_LOCK = 1;
   public static final int EXCLUSIVE_LOCK = 2;
   protected Column[] columns;
   protected CompareMode compareMode;
   protected boolean isHidden;
   private final HashMap<String, Column> columnMap;
   private final boolean persistIndexes;
   private final boolean persistData;
   private ArrayList<TriggerObject> triggers;
   private ArrayList<Constraint> constraints;
   private ArrayList<Sequence> sequences;
   private final CopyOnWriteArrayList<TableView> dependentViews = new CopyOnWriteArrayList();
   private ArrayList<TableSynonym> synonyms;
   private boolean checkForeignKeyConstraints = true;
   private boolean onCommitDrop;
   private boolean onCommitTruncate;
   private volatile Row nullRow;
   private RowFactory rowFactory = RowFactory.getRowFactory();
   private boolean tableExpression;

   protected Table(Schema var1, int var2, String var3, boolean var4, boolean var5) {
      super(var1, var2, var3, 11);
      this.columnMap = var1.getDatabase().newStringMap();
      this.persistIndexes = var4;
      this.persistData = var5;
      this.compareMode = var1.getDatabase().getCompareMode();
   }

   public void rename(String var1) {
      super.rename(var1);
      if (this.constraints != null) {
         Iterator var2 = this.constraints.iterator();

         while(var2.hasNext()) {
            Constraint var3 = (Constraint)var2.next();
            var3.rebuild();
         }
      }

   }

   public boolean isView() {
      return false;
   }

   public boolean lock(SessionLocal var1, int var2) {
      return false;
   }

   public abstract void close(SessionLocal var1);

   public void unlock(SessionLocal var1) {
   }

   public abstract Index addIndex(SessionLocal var1, String var2, int var3, IndexColumn[] var4, int var5, IndexType var6, boolean var7, String var8);

   public Row getRow(SessionLocal var1, long var2) {
      return null;
   }

   public boolean isInsertable() {
      return true;
   }

   public abstract void removeRow(SessionLocal var1, Row var2);

   public Row lockRow(SessionLocal var1, Row var2) {
      throw DbException.getUnsupportedException("lockRow()");
   }

   public abstract long truncate(SessionLocal var1);

   public abstract void addRow(SessionLocal var1, Row var2);

   public void updateRow(SessionLocal var1, Row var2, Row var3) {
      var3.setKey(var2.getKey());
      this.removeRow(var1, var2);
      this.addRow(var1, var3);
   }

   public abstract void checkSupportAlter();

   public abstract TableType getTableType();

   public String getSQLTableType() {
      if (this.isView()) {
         return "VIEW";
      } else if (this.isTemporary()) {
         return this.isGlobalTemporary() ? "GLOBAL TEMPORARY" : "LOCAL TEMPORARY";
      } else {
         return "BASE TABLE";
      }
   }

   public abstract Index getScanIndex(SessionLocal var1);

   public Index getScanIndex(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6) {
      return this.getScanIndex(var1);
   }

   public abstract ArrayList<Index> getIndexes();

   public Index getIndex(String var1) {
      ArrayList var2 = this.getIndexes();
      if (var2 != null) {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            Index var4 = (Index)var3.next();
            if (var4.getName().equals(var1)) {
               return var4;
            }
         }
      }

      throw DbException.get(42112, (String)var1);
   }

   public boolean isLockedExclusively() {
      return false;
   }

   public abstract long getMaxDataModificationId();

   public abstract boolean isDeterministic();

   public abstract boolean canGetRowCount(SessionLocal var1);

   public boolean canReference() {
      return true;
   }

   public abstract boolean canDrop();

   public abstract long getRowCount(SessionLocal var1);

   public abstract long getRowCountApproximation(SessionLocal var1);

   public long getDiskSpaceUsed() {
      return 0L;
   }

   public Column getRowIdColumn() {
      return null;
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      throw DbException.getInternalError(this.toString());
   }

   public boolean isQueryComparable() {
      return true;
   }

   public void addDependencies(HashSet<DbObject> var1) {
      if (!var1.contains(this)) {
         if (this.sequences != null) {
            var1.addAll(this.sequences);
         }

         ExpressionVisitor var2 = ExpressionVisitor.getDependenciesVisitor(var1);
         Column[] var3 = this.columns;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Column var6 = var3[var5];
            var6.isEverything(var2);
         }

         if (this.constraints != null) {
            Iterator var7 = this.constraints.iterator();

            while(var7.hasNext()) {
               Constraint var8 = (Constraint)var7.next();
               var8.isEverything(var2);
            }
         }

         var1.add(this);
      }
   }

   public ArrayList<DbObject> getChildren() {
      ArrayList var1 = Utils.newSmallArrayList();
      ArrayList var2 = this.getIndexes();
      if (var2 != null) {
         var1.addAll(var2);
      }

      if (this.constraints != null) {
         var1.addAll(this.constraints);
      }

      if (this.triggers != null) {
         var1.addAll(this.triggers);
      }

      if (this.sequences != null) {
         var1.addAll(this.sequences);
      }

      var1.addAll(this.dependentViews);
      if (this.synonyms != null) {
         var1.addAll(this.synonyms);
      }

      ArrayList var3 = this.database.getAllRights();
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         Right var5 = (Right)var4.next();
         if (var5.getGrantedObject() == this) {
            var1.add(var5);
         }
      }

      return var1;
   }

   protected void setColumns(Column[] var1) {
      if (var1.length > 16384) {
         throw DbException.get(54011, (String)"16384");
      } else {
         this.columns = var1;
         if (this.columnMap.size() > 0) {
            this.columnMap.clear();
         }

         for(int var2 = 0; var2 < var1.length; ++var2) {
            Column var3 = var1[var2];
            int var4 = var3.getType().getValueType();
            if (var4 == -1) {
               throw DbException.get(50004, (String)var3.getTraceSQL());
            }

            var3.setTable(this, var2);
            String var5 = var3.getName();
            if (this.columnMap.putIfAbsent(var5, var3) != null) {
               throw DbException.get(42121, (String)var5);
            }
         }

         this.rowFactory = this.database.getRowFactory().createRowFactory(this.database, this.database.getCompareMode(), this.database, var1, (IndexColumn[])null, false);
      }
   }

   public void renameColumn(Column var1, String var2) {
      Column[] var3 = this.columns;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Column var6 = var3[var5];
         if (var6 != var1 && var6.getName().equals(var2)) {
            throw DbException.get(42121, (String)var2);
         }
      }

      this.columnMap.remove(var1.getName());
      var1.rename(var2);
      this.columnMap.put(var2, var1);
   }

   public boolean isLockedExclusivelyBy(SessionLocal var1) {
      return false;
   }

   public void updateRows(Prepared var1, SessionLocal var2, LocalResult var3) {
      SessionLocal.Savepoint var4 = var2.setSavepoint();
      int var5 = 0;

      Row var6;
      while(var3.next()) {
         ++var5;
         if ((var5 & 127) == 0) {
            var1.checkCanceled();
         }

         var6 = var3.currentRowForTable();
         var3.next();

         try {
            this.removeRow(var2, var6);
         } catch (DbException var8) {
            if (var8.getErrorCode() == 90131 || var8.getErrorCode() == 90112) {
               var2.rollbackTo(var4);
            }

            throw var8;
         }
      }

      var3.reset();

      while(var3.next()) {
         ++var5;
         if ((var5 & 127) == 0) {
            var1.checkCanceled();
         }

         var3.next();
         var6 = var3.currentRowForTable();

         try {
            this.addRow(var2, var6);
         } catch (DbException var9) {
            if (var9.getErrorCode() == 90131) {
               var2.rollbackTo(var4);
            }

            throw var9;
         }
      }

   }

   public CopyOnWriteArrayList<TableView> getDependentViews() {
      return this.dependentViews;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      while(!this.dependentViews.isEmpty()) {
         TableView var2 = (TableView)this.dependentViews.get(0);
         this.dependentViews.remove(0);
         this.database.removeSchemaObject(var1, var2);
      }

      while(this.synonyms != null && !this.synonyms.isEmpty()) {
         TableSynonym var4 = (TableSynonym)this.synonyms.remove(0);
         this.database.removeSchemaObject(var1, var4);
      }

      while(this.triggers != null && !this.triggers.isEmpty()) {
         TriggerObject var5 = (TriggerObject)this.triggers.remove(0);
         this.database.removeSchemaObject(var1, var5);
      }

      while(this.constraints != null && !this.constraints.isEmpty()) {
         Constraint var6 = (Constraint)this.constraints.remove(0);
         this.database.removeSchemaObject(var1, var6);
      }

      Iterator var7 = this.database.getAllRights().iterator();

      while(var7.hasNext()) {
         Right var3 = (Right)var7.next();
         if (var3.getGrantedObject() == this) {
            this.database.removeDatabaseObject(var1, var3);
         }
      }

      this.database.removeMeta(var1, this.getId());

      while(this.sequences != null && !this.sequences.isEmpty()) {
         Sequence var8 = (Sequence)this.sequences.remove(0);
         if (this.database.getDependentTable(var8, this) == null) {
            this.database.removeSchemaObject(var1, var8);
         }
      }

   }

   public void dropMultipleColumnsConstraintsAndIndexes(SessionLocal var1, ArrayList<Column> var2) {
      HashSet var3 = new HashSet();
      Iterator var6;
      Constraint var7;
      if (this.constraints != null) {
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            Column var5 = (Column)var4.next();
            var6 = this.constraints.iterator();

            while(var6.hasNext()) {
               var7 = (Constraint)var6.next();
               HashSet var8 = var7.getReferencedColumns(this);
               if (var8.contains(var5)) {
                  if (var8.size() != 1) {
                     throw DbException.get(90083, var7.getTraceSQL());
                  }

                  var3.add(var7);
               }
            }
         }
      }

      HashSet var10 = new HashSet();
      ArrayList var11 = this.getIndexes();
      if (var11 != null) {
         var6 = var2.iterator();

         while(var6.hasNext()) {
            Column var12 = (Column)var6.next();
            Iterator var14 = var11.iterator();

            while(var14.hasNext()) {
               Index var9 = (Index)var14.next();
               if (var9.getCreateSQL() != null && var9.getColumnIndex(var12) >= 0) {
                  if (var9.getColumns().length != 1) {
                     throw DbException.get(90083, var9.getTraceSQL());
                  }

                  var10.add(var9);
               }
            }
         }
      }

      var6 = var3.iterator();

      while(var6.hasNext()) {
         var7 = (Constraint)var6.next();
         if (var7.isValid()) {
            var1.getDatabase().removeSchemaObject(var1, var7);
         }
      }

      var6 = var10.iterator();

      while(var6.hasNext()) {
         Index var13 = (Index)var6.next();
         if (this.getIndexes().contains(var13)) {
            var1.getDatabase().removeSchemaObject(var1, var13);
         }
      }

   }

   public RowFactory getRowFactory() {
      return this.rowFactory;
   }

   public Row createRow(Value[] var1, int var2) {
      return this.rowFactory.createRow(var1, var2);
   }

   public Row getTemplateRow() {
      return this.createRow(new Value[this.getColumns().length], -1);
   }

   public SearchRow getTemplateSimpleRow(boolean var1) {
      return (SearchRow)(var1 ? new SimpleRowValue(this.columns.length) : new DefaultRow(new Value[this.columns.length]));
   }

   public Row getNullRow() {
      Row var1 = this.nullRow;
      if (var1 == null) {
         Value[] var2 = new Value[this.columns.length];
         Arrays.fill(var2, ValueNull.INSTANCE);
         this.nullRow = var1 = this.createRow(var2, 1);
      }

      return var1;
   }

   public Column[] getColumns() {
      return this.columns;
   }

   public int getType() {
      return 0;
   }

   public Column getColumn(int var1) {
      return this.columns[var1];
   }

   public Column getColumn(String var1) {
      Column var2 = (Column)this.columnMap.get(var1);
      if (var2 == null) {
         throw DbException.get(42122, (String)var1);
      } else {
         return var2;
      }
   }

   public Column getColumn(String var1, boolean var2) {
      Column var3 = (Column)this.columnMap.get(var1);
      if (var3 == null && !var2) {
         throw DbException.get(42122, (String)var1);
      } else {
         return var3;
      }
   }

   public Column findColumn(String var1) {
      return (Column)this.columnMap.get(var1);
   }

   public boolean doesColumnExist(String var1) {
      return this.columnMap.containsKey(var1);
   }

   public Column getIdentityColumn() {
      Column[] var1 = this.columns;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Column var4 = var1[var3];
         if (var4.isIdentity()) {
            return var4;
         }
      }

      return null;
   }

   public PlanItem getBestPlanItem(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6) {
      PlanItem var7 = new PlanItem();
      var7.setIndex(this.getScanIndex(var1));
      var7.cost = var7.getIndex().getCost(var1, (int[])null, var3, var4, (SortOrder)null, var6);
      Trace var8 = var1.getTrace();
      if (var8.isDebugEnabled()) {
         var8.debug("Table      :     potential plan item cost {0} index {1}", var7.cost, var7.getIndex().getPlanSQL());
      }

      ArrayList var9 = this.getIndexes();
      IndexHints var10 = getIndexHints(var3, var4);
      if (var9 != null && var2 != null) {
         int var11 = 1;

         for(int var12 = var9.size(); var11 < var12; ++var11) {
            Index var13 = (Index)var9.get(var11);
            if (!isIndexExcludedByHints(var10, var13)) {
               double var14 = var13.getCost(var1, var2, var3, var4, var5, var6);
               if (var8.isDebugEnabled()) {
                  var8.debug("Table      :     potential plan item cost {0} index {1}", var14, var13.getPlanSQL());
               }

               if (var14 < var7.cost) {
                  var7.cost = var14;
                  var7.setIndex(var13);
               }
            }
         }
      }

      return var7;
   }

   private static boolean isIndexExcludedByHints(IndexHints var0, Index var1) {
      return var0 != null && !var0.allowIndex(var1);
   }

   private static IndexHints getIndexHints(TableFilter[] var0, int var1) {
      return var0 == null ? null : var0[var1].getIndexHints();
   }

   public Index findPrimaryKey() {
      ArrayList var1 = this.getIndexes();
      if (var1 != null) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Index var3 = (Index)var2.next();
            if (var3.getIndexType().isPrimaryKey()) {
               return var3;
            }
         }
      }

      return null;
   }

   public Index getPrimaryKey() {
      Index var1 = this.findPrimaryKey();
      if (var1 != null) {
         return var1;
      } else {
         throw DbException.get(42112, (String)"PRIMARY_KEY_");
      }
   }

   public void convertInsertRow(SessionLocal var1, Row var2, Boolean var3) {
      int var4 = this.columns.length;
      int var5 = 0;

      int var6;
      Value var7;
      for(var6 = 0; var6 < var4; ++var6) {
         var7 = var2.getValue(var6);
         Column var8 = this.columns[var6];
         if (var7 == ValueNull.INSTANCE && var8.isDefaultOnNull()) {
            var7 = null;
         }

         if (var8.isIdentity()) {
            if (var3 != null) {
               if (!var3) {
                  var7 = null;
               }
            } else if (var7 != null && var8.isGeneratedAlways()) {
               throw DbException.get(90154, var8.getSQLWithTable(new StringBuilder(), 3).toString());
            }
         } else if (var8.isGeneratedAlways()) {
            if (var7 != null) {
               throw DbException.get(90154, var8.getSQLWithTable(new StringBuilder(), 3).toString());
            }

            ++var5;
            continue;
         }

         Value var9 = var8.validateConvertUpdateSequence(var1, var7, var2);
         if (var9 != var7) {
            var2.setValue(var6, var9);
         }
      }

      if (var5 > 0) {
         for(var6 = 0; var6 < var4; ++var6) {
            var7 = var2.getValue(var6);
            if (var7 == null) {
               var2.setValue(var6, this.columns[var6].validateConvertUpdateSequence(var1, (Value)null, var2));
            }
         }
      }

   }

   public void convertUpdateRow(SessionLocal var1, Row var2, boolean var3) {
      int var4 = this.columns.length;
      int var5 = 0;

      int var6;
      Value var7;
      for(var6 = 0; var6 < var4; ++var6) {
         var7 = var2.getValue(var6);
         Column var8 = this.columns[var6];
         if (var8.isGenerated()) {
            if (var7 != null) {
               if (!var3) {
                  throw DbException.get(90154, var8.getSQLWithTable(new StringBuilder(), 3).toString());
               }

               var2.setValue(var6, (Value)null);
            }

            ++var5;
         } else {
            Value var9 = var8.validateConvertUpdateSequence(var1, var7, var2);
            if (var9 != var7) {
               var2.setValue(var6, var9);
            }
         }
      }

      if (var5 > 0) {
         for(var6 = 0; var6 < var4; ++var6) {
            var7 = var2.getValue(var6);
            if (var7 == null) {
               var2.setValue(var6, this.columns[var6].validateConvertUpdateSequence(var1, (Value)null, var2));
            }
         }
      }

   }

   private static void remove(ArrayList<? extends DbObject> var0, DbObject var1) {
      if (var0 != null) {
         var0.remove(var1);
      }

   }

   public void removeIndex(Index var1) {
      ArrayList var2 = this.getIndexes();
      if (var2 != null) {
         remove(var2, var1);
         if (var1.getIndexType().isPrimaryKey()) {
            Column[] var3 = var1.getColumns();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Column var6 = var3[var5];
               var6.setPrimaryKey(false);
            }
         }
      }

   }

   public void removeDependentView(TableView var1) {
      this.dependentViews.remove(var1);
   }

   public void removeSynonym(TableSynonym var1) {
      remove(this.synonyms, var1);
   }

   public void removeConstraint(Constraint var1) {
      remove(this.constraints, var1);
   }

   public final void removeSequence(Sequence var1) {
      remove(this.sequences, var1);
   }

   public void removeTrigger(TriggerObject var1) {
      remove(this.triggers, var1);
   }

   public void addDependentView(TableView var1) {
      this.dependentViews.add(var1);
   }

   public void addSynonym(TableSynonym var1) {
      this.synonyms = add(this.synonyms, var1);
   }

   public void addConstraint(Constraint var1) {
      if (this.constraints == null || !this.constraints.contains(var1)) {
         this.constraints = add(this.constraints, var1);
      }

   }

   public ArrayList<Constraint> getConstraints() {
      return this.constraints;
   }

   public void addSequence(Sequence var1) {
      this.sequences = add(this.sequences, var1);
   }

   public void addTrigger(TriggerObject var1) {
      this.triggers = add(this.triggers, var1);
   }

   private static <T> ArrayList<T> add(ArrayList<T> var0, T var1) {
      if (var0 == null) {
         var0 = Utils.newSmallArrayList();
      }

      var0.add(var1);
      return var0;
   }

   public void fire(SessionLocal var1, int var2, boolean var3) {
      if (this.triggers != null) {
         Iterator var4 = this.triggers.iterator();

         while(var4.hasNext()) {
            TriggerObject var5 = (TriggerObject)var4.next();
            var5.fire(var1, var2, var3);
         }
      }

   }

   public boolean hasSelectTrigger() {
      if (this.triggers != null) {
         Iterator var1 = this.triggers.iterator();

         while(var1.hasNext()) {
            TriggerObject var2 = (TriggerObject)var1.next();
            if (var2.isSelectTrigger()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean fireRow() {
      return this.constraints != null && !this.constraints.isEmpty() || this.triggers != null && !this.triggers.isEmpty();
   }

   public boolean fireBeforeRow(SessionLocal var1, Row var2, Row var3) {
      boolean var4 = this.fireRow(var1, var2, var3, true, false);
      this.fireConstraints(var1, var2, var3, true);
      return var4;
   }

   private void fireConstraints(SessionLocal var1, Row var2, Row var3, boolean var4) {
      if (this.constraints != null) {
         Iterator var5 = this.constraints.iterator();

         while(var5.hasNext()) {
            Constraint var6 = (Constraint)var5.next();
            if (var6.isBefore() == var4) {
               var6.checkRow(var1, this, var2, var3);
            }
         }
      }

   }

   public void fireAfterRow(SessionLocal var1, Row var2, Row var3, boolean var4) {
      this.fireRow(var1, var2, var3, false, var4);
      if (!var4) {
         this.fireConstraints(var1, var2, var3, false);
      }

   }

   private boolean fireRow(SessionLocal var1, Row var2, Row var3, boolean var4, boolean var5) {
      if (this.triggers != null) {
         Iterator var6 = this.triggers.iterator();

         while(var6.hasNext()) {
            TriggerObject var7 = (TriggerObject)var6.next();
            boolean var8 = var7.fireRow(var1, this, var2, var3, var4, var5);
            if (var8) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean isGlobalTemporary() {
      return false;
   }

   public boolean canTruncate() {
      return false;
   }

   public void setCheckForeignKeyConstraints(SessionLocal var1, boolean var2, boolean var3) {
      if (var2 && var3 && this.constraints != null) {
         Iterator var4 = this.constraints.iterator();

         while(var4.hasNext()) {
            Constraint var5 = (Constraint)var4.next();
            if (var5.getConstraintType() == Constraint.Type.REFERENTIAL) {
               var5.checkExistingData(var1);
            }
         }
      }

      this.checkForeignKeyConstraints = var2;
   }

   public boolean getCheckForeignKeyConstraints() {
      return this.checkForeignKeyConstraints;
   }

   public Index getIndexForColumn(Column var1, boolean var2, boolean var3) {
      ArrayList var4 = this.getIndexes();
      Index var5 = null;
      if (var4 != null) {
         int var6 = 1;

         for(int var7 = var4.size(); var6 < var7; ++var6) {
            Index var8 = (Index)var4.get(var6);
            if ((!var2 || var8.canGetFirstOrLast()) && (!var3 || var8.canFindNext()) && var8.isFirstColumn(var1) && (var5 == null || var5.getColumns().length > var8.getColumns().length)) {
               var5 = var8;
            }
         }
      }

      return var5;
   }

   public boolean getOnCommitDrop() {
      return this.onCommitDrop;
   }

   public void setOnCommitDrop(boolean var1) {
      this.onCommitDrop = var1;
   }

   public boolean getOnCommitTruncate() {
      return this.onCommitTruncate;
   }

   public void setOnCommitTruncate(boolean var1) {
      this.onCommitTruncate = var1;
   }

   public void removeIndexOrTransferOwnership(SessionLocal var1, Index var2) {
      boolean var3 = false;
      if (this.constraints != null) {
         Iterator var4 = this.constraints.iterator();

         while(var4.hasNext()) {
            Constraint var5 = (Constraint)var4.next();
            if (var5.usesIndex(var2)) {
               var5.setIndexOwner(var2);
               this.database.updateMeta(var1, var5);
               var3 = true;
            }
         }
      }

      if (!var3) {
         this.database.removeSchemaObject(var1, var2);
      }

   }

   public void removeColumnExpressionsDependencies(SessionLocal var1) {
      Column[] var2 = this.columns;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Column var5 = var2[var4];
         var5.setDefaultExpression(var1, (Expression)null);
         var5.setOnUpdateExpression(var1, (Expression)null);
      }

   }

   public ArrayList<SessionLocal> checkDeadlock(SessionLocal var1, SessionLocal var2, Set<SessionLocal> var3) {
      return null;
   }

   public boolean isPersistIndexes() {
      return this.persistIndexes;
   }

   public boolean isPersistData() {
      return this.persistData;
   }

   public int compareValues(CastDataProvider var1, Value var2, Value var3) {
      return var2.compareTo(var3, var1, this.compareMode);
   }

   public CompareMode getCompareMode() {
      return this.compareMode;
   }

   public void checkWritingAllowed() {
      this.database.checkWritingAllowed();
   }

   public boolean isHidden() {
      return this.isHidden;
   }

   public void setHidden(boolean var1) {
      this.isHidden = var1;
   }

   public boolean isRowLockable() {
      return false;
   }

   public void setTableExpression(boolean var1) {
      this.tableExpression = var1;
   }

   public boolean isTableExpression() {
      return this.tableExpression;
   }

   public ArrayList<TriggerObject> getTriggers() {
      return this.triggers;
   }

   public int getMainIndexColumn() {
      return -1;
   }
}
