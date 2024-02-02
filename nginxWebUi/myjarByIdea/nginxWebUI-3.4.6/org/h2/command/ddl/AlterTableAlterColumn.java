package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.command.CommandContainer;
import org.h2.command.Parser;
import org.h2.command.Prepared;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintReferential;
import org.h2.constraint.ConstraintUnique;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.schema.Sequence;
import org.h2.schema.TriggerObject;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.table.TableBase;
import org.h2.table.TableView;
import org.h2.util.Utils;

public class AlterTableAlterColumn extends CommandWithColumns {
   private String tableName;
   private Column oldColumn;
   private Column newColumn;
   private int type;
   private Expression defaultExpression;
   private Expression newSelectivity;
   private Expression usingExpression;
   private boolean addFirst;
   private String addBefore;
   private String addAfter;
   private boolean ifTableExists;
   private boolean ifNotExists;
   private ArrayList<Column> columnsToAdd;
   private ArrayList<Column> columnsToRemove;
   private boolean booleanFlag;

   public AlterTableAlterColumn(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setIfTableExists(boolean var1) {
      this.ifTableExists = var1;
   }

   public void setTableName(String var1) {
      this.tableName = var1;
   }

   public void setOldColumn(Column var1) {
      this.oldColumn = var1;
   }

   public void setAddFirst() {
      this.addFirst = true;
   }

   public void setAddBefore(String var1) {
      this.addBefore = var1;
   }

   public void setAddAfter(String var1) {
      this.addAfter = var1;
   }

   public long update() {
      Database var1 = this.session.getDatabase();
      Table var2 = this.getSchema().resolveTableOrView(this.session, this.tableName);
      if (var2 == null) {
         if (this.ifTableExists) {
            return 0L;
         } else {
            throw DbException.get(42102, (String)this.tableName);
         }
      } else {
         this.session.getUser().checkTableRight(var2, 32);
         var2.checkSupportAlter();
         var2.lock(this.session, 2);
         if (this.newColumn != null) {
            checkDefaultReferencesTable(var2, this.newColumn.getDefaultExpression());
            this.checkClustering(this.newColumn);
         }

         if (this.columnsToAdd != null) {
            Iterator var3 = this.columnsToAdd.iterator();

            while(var3.hasNext()) {
               Column var4 = (Column)var3.next();
               checkDefaultReferencesTable(var2, var4.getDefaultExpression());
               this.checkClustering(var4);
            }
         }

         switch (this.type) {
            case 7:
               if (!this.ifNotExists || this.columnsToAdd == null || this.columnsToAdd.size() != 1 || !var2.doesColumnExist(((Column)this.columnsToAdd.get(0)).getName())) {
                  ArrayList var7 = this.generateSequences(this.columnsToAdd, false);
                  if (this.columnsToAdd != null) {
                     this.changePrimaryKeysToNotNull(this.columnsToAdd);
                  }

                  this.copyData(var2, var7, true);
               }
               break;
            case 8:
               if (this.oldColumn != null && this.oldColumn.isNullable()) {
                  this.checkNoNullValues(var2);
                  this.oldColumn.setNullable(false);
                  var1.updateMeta(this.session, var2);
               }
               break;
            case 9:
               if (this.oldColumn != null && !this.oldColumn.isNullable()) {
                  this.checkNullable(var2);
                  this.oldColumn.setNullable(true);
                  var1.updateMeta(this.session, var2);
               }
               break;
            case 10:
            case 98:
               if (this.oldColumn != null && !this.oldColumn.isIdentity()) {
                  if (this.defaultExpression != null) {
                     if (this.oldColumn.isGenerated()) {
                        return 0L;
                     }

                     checkDefaultReferencesTable(var2, this.defaultExpression);
                     this.oldColumn.setDefaultExpression(this.session, this.defaultExpression);
                  } else {
                     if (this.type == 98 != this.oldColumn.isGenerated()) {
                        return 0L;
                     }

                     this.oldColumn.setDefaultExpression(this.session, (Expression)null);
                  }

                  var1.updateMeta(this.session, var2);
               }
               break;
            case 11:
               if (this.oldColumn != null) {
                  if (this.oldColumn.isWideningConversion(this.newColumn) && this.usingExpression == null) {
                     this.convertIdentityColumn(var2, this.newColumn);
                     this.oldColumn.copy(this.newColumn);
                     var1.updateMeta(this.session, var2);
                  } else {
                     this.oldColumn.setSequence((Sequence)null, false);
                     this.oldColumn.setDefaultExpression(this.session, (Expression)null);
                     if (this.oldColumn.isNullable() && !this.newColumn.isNullable()) {
                        this.checkNoNullValues(var2);
                     } else if (!this.oldColumn.isNullable() && this.newColumn.isNullable()) {
                        this.checkNullable(var2);
                     }

                     if (this.oldColumn.getVisible() ^ this.newColumn.getVisible()) {
                        this.oldColumn.setVisible(this.newColumn.getVisible());
                     }

                     this.convertIdentityColumn(var2, this.newColumn);
                     this.copyData(var2, (ArrayList)null, true);
                  }

                  var2.setModified();
               }
               break;
            case 12:
               if (var2.getColumns().length - this.columnsToRemove.size() < 1) {
                  throw DbException.get(90084, ((Column)this.columnsToRemove.get(0)).getTraceSQL());
               }

               var2.dropMultipleColumnsConstraintsAndIndexes(this.session, this.columnsToRemove);
               this.copyData(var2, (ArrayList)null, false);
               break;
            case 13:
               if (this.oldColumn != null) {
                  int var6 = this.newSelectivity.optimize(this.session).getValue(this.session).getInt();
                  this.oldColumn.setSelectivity(var6);
                  var1.updateMeta(this.session, var2);
               }
               break;
            case 87:
               if (this.oldColumn != null && this.oldColumn.getVisible() != this.booleanFlag) {
                  this.oldColumn.setVisible(this.booleanFlag);
                  var2.setModified();
                  var1.updateMeta(this.session, var2);
               }
               break;
            case 90:
               if (this.oldColumn != null) {
                  if (this.defaultExpression != null) {
                     if (this.oldColumn.isIdentity() || this.oldColumn.isGenerated()) {
                        return 0L;
                     }

                     checkDefaultReferencesTable(var2, this.defaultExpression);
                     this.oldColumn.setOnUpdateExpression(this.session, this.defaultExpression);
                  } else {
                     this.oldColumn.setOnUpdateExpression(this.session, (Expression)null);
                  }

                  var1.updateMeta(this.session, var2);
               }
               break;
            case 99:
               if (this.oldColumn != null) {
                  Sequence var5 = this.oldColumn.getSequence();
                  if (var5 != null) {
                     this.oldColumn.setSequence((Sequence)null, false);
                     this.removeSequence(var2, var5);
                     var1.updateMeta(this.session, var2);
                  }
               }
               break;
            case 100:
               if (this.oldColumn != null && this.oldColumn.isDefaultOnNull() != this.booleanFlag) {
                  this.oldColumn.setDefaultOnNull(this.booleanFlag);
                  var2.setModified();
                  var1.updateMeta(this.session, var2);
               }
               break;
            default:
               throw DbException.getInternalError("type=" + this.type);
         }

         return 0L;
      }
   }

   private static void checkDefaultReferencesTable(Table var0, Expression var1) {
      if (var1 != null) {
         HashSet var2 = new HashSet();
         ExpressionVisitor var3 = ExpressionVisitor.getDependenciesVisitor(var2);
         var1.isEverything(var3);
         if (var2.contains(var0)) {
            throw DbException.get(90083, var1.getTraceSQL());
         }
      }
   }

   private void checkClustering(Column var1) {
      if (!"''".equals(this.session.getDatabase().getCluster()) && var1.hasIdentityOptions()) {
         throw DbException.getUnsupportedException("CLUSTERING && identity columns");
      }
   }

   private void convertIdentityColumn(Table var1, Column var2) {
      if (var2.hasIdentityOptions()) {
         if (var2.isPrimaryKey()) {
            this.addConstraintCommand(Parser.newPrimaryKeyConstraintCommand(this.session, var1.getSchema(), var1.getName(), var2));
         }

         int var3 = this.getObjectId();
         var2.initializeSequence(this.session, this.getSchema(), var3, var1.isTemporary());
      }

   }

   private void removeSequence(Table var1, Sequence var2) {
      if (var2 != null) {
         var1.removeSequence(var2);
         var2.setBelongsToTable(false);
         Database var3 = this.session.getDatabase();
         var3.removeSchemaObject(this.session, var2);
      }

   }

   private void copyData(Table var1, ArrayList<Sequence> var2, boolean var3) {
      if (var1.isTemporary()) {
         throw DbException.getUnsupportedException("TEMP TABLE");
      } else {
         Database var4 = this.session.getDatabase();
         String var5 = var1.getName();
         String var6 = var4.getTempTableName(var5, this.session);
         Column[] var7 = var1.getColumns();
         ArrayList var8 = new ArrayList(var7.length);
         Table var9 = this.cloneTableStructure(var1, var7, var4, var6, var8);
         if (var2 != null) {
            Iterator var10 = var2.iterator();

            while(var10.hasNext()) {
               Sequence var11 = (Sequence)var10.next();
               var1.addSequence(var11);
            }
         }

         try {
            this.checkViews(var1, var9);
         } catch (DbException var17) {
            StringBuilder var19 = new StringBuilder("DROP TABLE ");
            var9.getSQL(var19, 0);
            this.execute(var19.toString());
            throw var17;
         }

         String var18 = var1.getName();
         ArrayList var20 = new ArrayList(var1.getDependentViews());
         Iterator var12 = var20.iterator();

         while(var12.hasNext()) {
            TableView var13 = (TableView)var12.next();
            var1.removeDependentView(var13);
         }

         StringBuilder var21 = new StringBuilder("DROP TABLE ");
         var1.getSQL(var21, 0).append(" IGNORE");
         this.execute(var21.toString());
         var4.renameSchemaObject(this.session, var9, var18);
         Iterator var22 = var9.getChildren().iterator();

         String var15;
         while(var22.hasNext()) {
            DbObject var14 = (DbObject)var22.next();
            if (!(var14 instanceof Sequence)) {
               var15 = var14.getName();
               if (var15 != null && var14.getCreateSQL() != null && var15.startsWith(var6 + "_")) {
                  var15 = var15.substring(var6.length() + 1);
                  SchemaObject var16 = (SchemaObject)var14;
                  if (var16 instanceof Constraint) {
                     if (var16.getSchema().findConstraint(this.session, var15) != null) {
                        var15 = var16.getSchema().getUniqueConstraintName(this.session, var9);
                     }
                  } else if (var16 instanceof Index && var16.getSchema().findIndex(this.session, var15) != null) {
                     var15 = var16.getSchema().getUniqueIndexName(this.session, var9, var15);
                  }

                  var4.renameSchemaObject(this.session, var16, var15);
               }
            }
         }

         if (var3) {
            this.createConstraints();
         }

         var22 = var20.iterator();

         while(var22.hasNext()) {
            TableView var23 = (TableView)var22.next();
            var15 = var23.getCreateSQL(true, true);
            this.execute(var15);
         }

      }
   }

   private Table cloneTableStructure(Table var1, Column[] var2, Database var3, String var4, ArrayList<Column> var5) {
      Column[] var6 = var2;
      int var7 = var2.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Column var9 = var6[var8];
         var5.add(var9.getClone());
      }

      int var26;
      Column var30;
      switch (this.type) {
         case 7:
            if (this.addFirst) {
               var26 = 0;
            } else if (this.addBefore != null) {
               var26 = var1.getColumn(this.addBefore).getColumnId();
            } else if (this.addAfter != null) {
               var26 = var1.getColumn(this.addAfter).getColumnId() + 1;
            } else {
               var26 = var2.length;
            }

            if (this.columnsToAdd != null) {
               Iterator var28 = this.columnsToAdd.iterator();

               while(var28.hasNext()) {
                  var30 = (Column)var28.next();
                  var5.add(var26++, var30);
               }
            }
            break;
         case 11:
            var5.set(this.oldColumn.getColumnId(), this.newColumn);
            break;
         case 12:
            Iterator var25 = this.columnsToRemove.iterator();

            while(var25.hasNext()) {
               Column var27 = (Column)var25.next();
               var30 = null;
               Iterator var31 = var5.iterator();

               while(var31.hasNext()) {
                  Column var10 = (Column)var31.next();
                  if (var10.getName().equals(var27.getName())) {
                     var30 = var10;
                     break;
                  }
               }

               if (var30 == null) {
                  throw DbException.getInternalError(var27.getCreateSQL());
               }

               var5.remove(var30);
            }
      }

      var26 = var3.allocateObjectId();
      CreateTableData var29 = new CreateTableData();
      var29.tableName = var4;
      var29.id = var26;
      var29.columns = var5;
      var29.temporary = var1.isTemporary();
      var29.persistData = var1.isPersistData();
      var29.persistIndexes = var1.isPersistIndexes();
      var29.isHidden = var1.isHidden();
      var29.session = this.session;
      Table var33 = this.getSchema().createTable(var29);
      var33.setComment(var1.getComment());
      String var32 = var33.getCreateSQLForMeta();
      StringBuilder var34 = new StringBuilder();
      StringBuilder var11 = new StringBuilder();
      Iterator var12 = var5.iterator();

      while(true) {
         while(true) {
            Column var13;
            do {
               if (!var12.hasNext()) {
                  String var35 = var33.getName();
                  Schema var36 = var33.getSchema();
                  var33.removeChildrenAndResources(this.session);
                  this.execute(var32);
                  var33 = var36.getTableOrView(this.session, var35);
                  ArrayList var14 = Utils.newSmallArrayList();
                  ArrayList var15 = Utils.newSmallArrayList();
                  boolean var16 = false;
                  Iterator var17 = var1.getChildren().iterator();

                  while(true) {
                     while(true) {
                        DbObject var18;
                        String var21;
                        do {
                           String var39;
                           do {
                              do {
                                 Index var19;
                                 do {
                                    do {
                                       if (!var17.hasNext()) {
                                          StringBuilder var37 = var33.getSQL((new StringBuilder(128)).append("INSERT INTO "), 0).append('(').append(var34).append(") OVERRIDING SYSTEM VALUE SELECT ");
                                          if (var11.length() == 0) {
                                             var37.append('*');
                                          } else {
                                             var37.append(var11);
                                          }

                                          var1.getSQL(var37.append(" FROM "), 0);

                                          try {
                                             this.execute(var37.toString());
                                          } catch (Throwable var24) {
                                             var37 = new StringBuilder("DROP TABLE ");
                                             var33.getSQL(var37, 0);
                                             this.execute(var37.toString());
                                             throw var24;
                                          }

                                          Iterator var38 = var14.iterator();

                                          while(var38.hasNext()) {
                                             var39 = (String)var38.next();
                                             this.execute(var39);
                                          }

                                          var1.setModified();
                                          var38 = var5.iterator();

                                          while(var38.hasNext()) {
                                             Column var40 = (Column)var38.next();
                                             Sequence var41 = var40.getSequence();
                                             if (var41 != null) {
                                                var1.removeSequence(var41);
                                                var40.setSequence((Sequence)null, false);
                                             }
                                          }

                                          var38 = var15.iterator();

                                          while(var38.hasNext()) {
                                             var39 = (String)var38.next();
                                             this.execute(var39);
                                          }

                                          return var33;
                                       }

                                       var18 = (DbObject)var17.next();
                                    } while(var18 instanceof Sequence);

                                    if (!(var18 instanceof Index)) {
                                       break;
                                    }

                                    var19 = (Index)var18;
                                 } while(var19.getIndexType().getBelongsToConstraint());

                                 var39 = var18.getCreateSQL();
                              } while(var39 == null);
                           } while(var18 instanceof TableView);

                           if (var18.getType() == 0) {
                              throw DbException.getInternalError();
                           }

                           String var20 = Parser.quoteIdentifier(var4 + "_" + var18.getName(), 0);
                           var21 = null;
                           if (var18 instanceof ConstraintReferential) {
                              ConstraintReferential var22 = (ConstraintReferential)var18;
                              if (var22.getTable() != var1) {
                                 var21 = var22.getCreateSQLForCopy(var22.getTable(), var33, var20, false);
                              }
                           }

                           if (var21 == null) {
                              var21 = var18.getCreateSQLForCopy(var33, var20);
                           }
                        } while(var21 == null);

                        if (var18 instanceof TriggerObject) {
                           var15.add(var21);
                        } else {
                           if (!var16) {
                              Index var42 = null;
                              if (var18 instanceof ConstraintUnique) {
                                 ConstraintUnique var23 = (ConstraintUnique)var18;
                                 if (var23.getConstraintType() == Constraint.Type.PRIMARY_KEY) {
                                    var42 = var23.getIndex();
                                 }
                              } else if (var18 instanceof Index) {
                                 var42 = (Index)var18;
                              }

                              if (var42 != null && TableBase.getMainIndexColumn(var42.getIndexType(), var42.getIndexColumns()) != -1) {
                                 this.execute(var21);
                                 var16 = true;
                                 continue;
                              }
                           }

                           var14.add(var21);
                        }
                     }
                  }
               }

               var13 = (Column)var12.next();
            } while(var13.isGenerated());

            switch (this.type) {
               case 7:
                  if (this.columnsToAdd != null && this.columnsToAdd.contains(var13)) {
                     if (this.usingExpression != null) {
                        this.usingExpression.getUnenclosedSQL(addColumn(var13, var34, var11), 0);
                     }
                     continue;
                  }
                  break;
               case 11:
                  if (var13.equals(this.newColumn) && this.usingExpression != null) {
                     this.usingExpression.getUnenclosedSQL(addColumn(var13, var34, var11), 0);
                     continue;
                  }
            }

            var13.getSQL(addColumn(var13, var34, var11), 0);
         }
      }
   }

   private static StringBuilder addColumn(Column var0, StringBuilder var1, StringBuilder var2) {
      if (var1.length() > 0) {
         var1.append(", ");
      }

      var0.getSQL(var1, 0);
      if (var2.length() > 0) {
         var2.append(", ");
      }

      return var2;
   }

   private void checkViews(SchemaObject var1, SchemaObject var2) {
      String var3 = var1.getName();
      String var4 = var2.getName();
      Database var5 = var1.getDatabase();
      String var6 = var5.getTempTableName(var3, this.session);
      var5.renameSchemaObject(this.session, var1, var6);

      try {
         var5.renameSchemaObject(this.session, var2, var3);
         this.checkViewsAreValid(var1);
      } finally {
         try {
            var5.renameSchemaObject(this.session, var2, var4);
         } finally {
            var5.renameSchemaObject(this.session, var1, var3);
         }
      }

   }

   private void checkViewsAreValid(DbObject var1) {
      Iterator var2 = var1.getChildren().iterator();

      while(var2.hasNext()) {
         DbObject var3 = (DbObject)var2.next();
         if (var3 instanceof TableView) {
            String var4 = ((TableView)var3).getQuery();

            try {
               this.session.prepare(var4);
            } catch (DbException var6) {
               throw DbException.get(90083, var6, var3.getTraceSQL());
            }

            this.checkViewsAreValid(var3);
         }
      }

   }

   private void execute(String var1) {
      Prepared var2 = this.session.prepare(var1);
      CommandContainer var3 = new CommandContainer(this.session, var1, var2);
      var3.executeUpdate((Object)null);
   }

   private void checkNullable(Table var1) {
      if (this.oldColumn.isIdentity()) {
         throw DbException.get(90023, this.oldColumn.getName());
      } else {
         Iterator var2 = var1.getIndexes().iterator();

         while(var2.hasNext()) {
            Index var3 = (Index)var2.next();
            if (var3.getColumnIndex(this.oldColumn) >= 0) {
               IndexType var4 = var3.getIndexType();
               if (var4.isPrimaryKey()) {
                  throw DbException.get(90023, this.oldColumn.getName());
               }
            }
         }

      }
   }

   private void checkNoNullValues(Table var1) {
      StringBuilder var2 = new StringBuilder("SELECT COUNT(*) FROM ");
      var1.getSQL(var2, 0).append(" WHERE ");
      this.oldColumn.getSQL(var2, 0).append(" IS NULL");
      String var3 = var2.toString();
      Prepared var4 = this.session.prepare(var3);
      ResultInterface var5 = var4.query(0L);
      var5.next();
      if (var5.currentRow()[0].getInt() > 0) {
         throw DbException.get(90081, this.oldColumn.getTraceSQL());
      }
   }

   public void setType(int var1) {
      this.type = var1;
   }

   public void setSelectivity(Expression var1) {
      this.newSelectivity = var1;
   }

   public void setDefaultExpression(Expression var1) {
      this.defaultExpression = var1;
   }

   public void setUsingExpression(Expression var1) {
      this.usingExpression = var1;
   }

   public void setNewColumn(Column var1) {
      this.newColumn = var1;
   }

   public int getType() {
      return this.type;
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   public void addColumn(Column var1) {
      if (this.columnsToAdd == null) {
         this.columnsToAdd = new ArrayList();
      }

      this.columnsToAdd.add(var1);
   }

   public void setColumnsToRemove(ArrayList<Column> var1) {
      this.columnsToRemove = var1;
   }

   public void setBooleanFlag(boolean var1) {
      this.booleanFlag = var1;
   }
}
