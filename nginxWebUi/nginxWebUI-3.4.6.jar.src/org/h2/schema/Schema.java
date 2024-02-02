/*     */ package org.h2.schema;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.h2.command.ddl.CreateSynonymData;
/*     */ import org.h2.command.ddl.CreateTableData;
/*     */ import org.h2.constraint.Constraint;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.DbSettings;
/*     */ import org.h2.engine.Right;
/*     */ import org.h2.engine.RightOwner;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableLink;
/*     */ import org.h2.table.TableSynonym;
/*     */ import org.h2.util.Utils;
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
/*     */ public class Schema
/*     */   extends DbObject
/*     */ {
/*     */   private RightOwner owner;
/*     */   private final boolean system;
/*     */   private ArrayList<String> tableEngineParams;
/*     */   private final ConcurrentHashMap<String, Table> tablesAndViews;
/*     */   private final ConcurrentHashMap<String, Domain> domains;
/*     */   private final ConcurrentHashMap<String, TableSynonym> synonyms;
/*     */   private final ConcurrentHashMap<String, Index> indexes;
/*     */   private final ConcurrentHashMap<String, Sequence> sequences;
/*     */   private final ConcurrentHashMap<String, TriggerObject> triggers;
/*     */   private final ConcurrentHashMap<String, Constraint> constraints;
/*     */   private final ConcurrentHashMap<String, Constant> constants;
/*     */   private final ConcurrentHashMap<String, UserDefinedFunction> functionsAndAggregates;
/*  59 */   private final HashSet<String> temporaryUniqueNames = new HashSet<>();
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
/*     */   public Schema(Database paramDatabase, int paramInt, String paramString, RightOwner paramRightOwner, boolean paramBoolean) {
/*  72 */     super(paramDatabase, paramInt, paramString, 8);
/*  73 */     this.tablesAndViews = paramDatabase.newConcurrentStringMap();
/*  74 */     this.domains = paramDatabase.newConcurrentStringMap();
/*  75 */     this.synonyms = paramDatabase.newConcurrentStringMap();
/*  76 */     this.indexes = paramDatabase.newConcurrentStringMap();
/*  77 */     this.sequences = paramDatabase.newConcurrentStringMap();
/*  78 */     this.triggers = paramDatabase.newConcurrentStringMap();
/*  79 */     this.constraints = paramDatabase.newConcurrentStringMap();
/*  80 */     this.constants = paramDatabase.newConcurrentStringMap();
/*  81 */     this.functionsAndAggregates = paramDatabase.newConcurrentStringMap();
/*  82 */     this.owner = paramRightOwner;
/*  83 */     this.system = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canDrop() {
/*  92 */     return !this.system;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/*  97 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/* 102 */     if (this.system) {
/* 103 */       return null;
/*     */     }
/* 105 */     StringBuilder stringBuilder = new StringBuilder("CREATE SCHEMA IF NOT EXISTS ");
/* 106 */     getSQL(stringBuilder, 0).append(" AUTHORIZATION ");
/* 107 */     this.owner.getSQL(stringBuilder, 0);
/* 108 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 113 */     return 10;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 122 */     return (this.tablesAndViews.isEmpty() && this.domains.isEmpty() && this.synonyms.isEmpty() && this.indexes.isEmpty() && this.sequences
/* 123 */       .isEmpty() && this.triggers.isEmpty() && this.constraints.isEmpty() && this.constants.isEmpty() && this.functionsAndAggregates
/* 124 */       .isEmpty());
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<DbObject> getChildren() {
/* 129 */     ArrayList<Right> arrayList = Utils.newSmallArrayList();
/* 130 */     ArrayList arrayList1 = this.database.getAllRights();
/* 131 */     for (Right right : arrayList1) {
/* 132 */       if (right.getGrantedObject() == this) {
/* 133 */         arrayList.add(right);
/*     */       }
/*     */     } 
/* 136 */     return (ArrayList)arrayList;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 141 */     removeChildrenFromMap(paramSessionLocal, (ConcurrentHashMap)this.triggers);
/* 142 */     removeChildrenFromMap(paramSessionLocal, (ConcurrentHashMap)this.constraints);
/*     */ 
/*     */     
/* 145 */     boolean bool = true;
/* 146 */     while (!this.tablesAndViews.isEmpty()) {
/* 147 */       boolean bool1 = false;
/* 148 */       for (Table table : this.tablesAndViews.values()) {
/* 149 */         if (table.getName() != null) {
/*     */ 
/*     */           
/* 152 */           Table table1 = this.database.getDependentTable((SchemaObject)table, table);
/* 153 */           if (table1 == null) {
/* 154 */             this.database.removeSchemaObject(paramSessionLocal, (SchemaObject)table);
/* 155 */             bool1 = true; continue;
/* 156 */           }  if (table1.getSchema() != this)
/* 157 */             throw DbException.get(90107, new String[] { table
/* 158 */                   .getTraceSQL(), table1.getTraceSQL() }); 
/* 159 */           if (!bool) {
/* 160 */             table1.removeColumnExpressionsDependencies(paramSessionLocal);
/* 161 */             table1.setModified();
/* 162 */             this.database.updateMeta(paramSessionLocal, (DbObject)table1);
/*     */           } 
/*     */         } 
/*     */       } 
/* 166 */       bool = bool1;
/*     */     } 
/* 168 */     removeChildrenFromMap(paramSessionLocal, (ConcurrentHashMap)this.domains);
/* 169 */     removeChildrenFromMap(paramSessionLocal, (ConcurrentHashMap)this.indexes);
/* 170 */     removeChildrenFromMap(paramSessionLocal, (ConcurrentHashMap)this.sequences);
/* 171 */     removeChildrenFromMap(paramSessionLocal, (ConcurrentHashMap)this.constants);
/* 172 */     removeChildrenFromMap(paramSessionLocal, (ConcurrentHashMap)this.functionsAndAggregates);
/* 173 */     for (Right right : this.database.getAllRights()) {
/* 174 */       if (right.getGrantedObject() == this) {
/* 175 */         this.database.removeDatabaseObject(paramSessionLocal, (DbObject)right);
/*     */       }
/*     */     } 
/* 178 */     this.database.removeMeta(paramSessionLocal, getId());
/* 179 */     this.owner = null;
/* 180 */     invalidate();
/*     */   }
/*     */   
/*     */   private void removeChildrenFromMap(SessionLocal paramSessionLocal, ConcurrentHashMap<String, ? extends SchemaObject> paramConcurrentHashMap) {
/* 184 */     if (!paramConcurrentHashMap.isEmpty()) {
/* 185 */       for (SchemaObject schemaObject : paramConcurrentHashMap.values()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 191 */         if (schemaObject.isValid())
/*     */         {
/*     */           
/* 194 */           this.database.removeSchemaObject(paramSessionLocal, schemaObject);
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
/*     */   public RightOwner getOwner() {
/* 206 */     return this.owner;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<String> getTableEngineParams() {
/* 215 */     return this.tableEngineParams;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTableEngineParams(ArrayList<String> paramArrayList) {
/* 223 */     this.tableEngineParams = paramArrayList; } private Map<String, SchemaObject> getMap(int paramInt) { ConcurrentHashMap<String, Table> concurrentHashMap7; ConcurrentHashMap<String, Domain> concurrentHashMap6; ConcurrentHashMap<String, TableSynonym> concurrentHashMap5;
/*     */     ConcurrentHashMap<String, Sequence> concurrentHashMap4;
/*     */     ConcurrentHashMap<String, Index> concurrentHashMap3;
/*     */     ConcurrentHashMap<String, TriggerObject> concurrentHashMap2;
/*     */     ConcurrentHashMap<String, Constraint> concurrentHashMap1;
/*     */     ConcurrentHashMap<String, Constant> concurrentHashMap;
/* 229 */     switch (paramInt) {
/*     */       case 0:
/* 231 */         return (Map)this.tablesAndViews;
/*     */       
/*     */       case 12:
/* 234 */         return (Map)this.domains;
/*     */       
/*     */       case 15:
/* 237 */         return (Map)this.synonyms;
/*     */       
/*     */       case 3:
/* 240 */         return (Map)this.sequences;
/*     */       
/*     */       case 1:
/* 243 */         return (Map)this.indexes;
/*     */       
/*     */       case 4:
/* 246 */         return (Map)this.triggers;
/*     */       
/*     */       case 5:
/* 249 */         return (Map)this.constraints;
/*     */       
/*     */       case 11:
/* 252 */         return (Map)this.constants;
/*     */       
/*     */       case 9:
/*     */       case 14:
/* 256 */         return (Map)this.functionsAndAggregates;
/*     */     } 
/*     */     
/* 259 */     throw DbException.getInternalError("type=" + paramInt); }
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
/*     */   public void add(SchemaObject paramSchemaObject) {
/* 272 */     if (paramSchemaObject.getSchema() != this) {
/* 273 */       throw DbException.getInternalError("wrong schema");
/*     */     }
/* 275 */     String str = paramSchemaObject.getName();
/* 276 */     Map<String, SchemaObject> map = getMap(paramSchemaObject.getType());
/* 277 */     if (map.putIfAbsent(str, paramSchemaObject) != null) {
/* 278 */       throw DbException.getInternalError("object already exists: " + str);
/*     */     }
/* 280 */     freeUniqueName(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rename(SchemaObject paramSchemaObject, String paramString) {
/* 290 */     int i = paramSchemaObject.getType();
/* 291 */     Map<String, SchemaObject> map = getMap(i);
/* 292 */     if (SysProperties.CHECK) {
/* 293 */       if (!map.containsKey(paramSchemaObject.getName()) && !(paramSchemaObject instanceof org.h2.table.MetaTable)) {
/* 294 */         throw DbException.getInternalError("not found: " + paramSchemaObject.getName());
/*     */       }
/* 296 */       if (paramSchemaObject.getName().equals(paramString) || map.containsKey(paramString)) {
/* 297 */         throw DbException.getInternalError("object already exists: " + paramString);
/*     */       }
/*     */     } 
/* 300 */     paramSchemaObject.checkRename();
/* 301 */     map.remove(paramSchemaObject.getName());
/* 302 */     freeUniqueName(paramSchemaObject.getName());
/* 303 */     paramSchemaObject.rename(paramString);
/* 304 */     map.put(paramString, paramSchemaObject);
/* 305 */     freeUniqueName(paramString);
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
/*     */   public Table findTableOrView(SessionLocal paramSessionLocal, String paramString) {
/* 318 */     Table table = this.tablesAndViews.get(paramString);
/* 319 */     if (table == null && paramSessionLocal != null) {
/* 320 */       table = paramSessionLocal.findLocalTempTable(paramString);
/*     */     }
/* 322 */     return table;
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
/*     */   public Table resolveTableOrView(SessionLocal paramSessionLocal, String paramString) {
/* 336 */     Table table = findTableOrView(paramSessionLocal, paramString);
/* 337 */     if (table == null) {
/* 338 */       TableSynonym tableSynonym = this.synonyms.get(paramString);
/* 339 */       if (tableSynonym != null) {
/* 340 */         return tableSynonym.getSynonymFor();
/*     */       }
/*     */     } 
/* 343 */     return table;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TableSynonym getSynonym(String paramString) {
/* 354 */     return this.synonyms.get(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Domain findDomain(String paramString) {
/* 364 */     return this.domains.get(paramString);
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
/*     */   public Index findIndex(SessionLocal paramSessionLocal, String paramString) {
/* 376 */     Index index = this.indexes.get(paramString);
/* 377 */     if (index == null) {
/* 378 */       index = paramSessionLocal.findLocalTempTableIndex(paramString);
/*     */     }
/* 380 */     return index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TriggerObject findTrigger(String paramString) {
/* 391 */     return this.triggers.get(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Sequence findSequence(String paramString) {
/* 402 */     return this.sequences.get(paramString);
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
/*     */   public Constraint findConstraint(SessionLocal paramSessionLocal, String paramString) {
/* 414 */     Constraint constraint = this.constraints.get(paramString);
/* 415 */     if (constraint == null) {
/* 416 */       constraint = paramSessionLocal.findLocalTempTableConstraint(paramString);
/*     */     }
/* 418 */     return constraint;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Constant findConstant(String paramString) {
/* 429 */     return this.constants.get(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FunctionAlias findFunction(String paramString) {
/* 440 */     UserDefinedFunction userDefinedFunction = findFunctionOrAggregate(paramString);
/* 441 */     return (userDefinedFunction instanceof FunctionAlias) ? (FunctionAlias)userDefinedFunction : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UserAggregate findAggregate(String paramString) {
/* 452 */     UserDefinedFunction userDefinedFunction = findFunctionOrAggregate(paramString);
/* 453 */     return (userDefinedFunction instanceof UserAggregate) ? (UserAggregate)userDefinedFunction : null;
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
/*     */   public UserDefinedFunction findFunctionOrAggregate(String paramString) {
/* 466 */     return this.functionsAndAggregates.get(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reserveUniqueName(String paramString) {
/* 475 */     if (paramString != null) {
/* 476 */       synchronized (this.temporaryUniqueNames) {
/* 477 */         this.temporaryUniqueNames.add(paramString);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void freeUniqueName(String paramString) {
/* 488 */     if (paramString != null) {
/* 489 */       synchronized (this.temporaryUniqueNames) {
/* 490 */         this.temporaryUniqueNames.remove(paramString);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private String getUniqueName(DbObject paramDbObject, Map<String, ? extends SchemaObject> paramMap, String paramString) {
/* 496 */     StringBuilder stringBuilder = new StringBuilder(paramString);
/* 497 */     String str = Integer.toHexString(paramDbObject.getName().hashCode());
/* 498 */     synchronized (this.temporaryUniqueNames) {
/* 499 */       int i; int j; for (i = 0, j = str.length(); i < j; i++) {
/* 500 */         char c = str.charAt(i);
/* 501 */         String str1 = stringBuilder.append((c >= 'a') ? (char)(c - 32) : c).toString();
/* 502 */         if (!paramMap.containsKey(str1) && this.temporaryUniqueNames.add(str1)) {
/* 503 */           return str1;
/*     */         }
/*     */       } 
/* 506 */       i = stringBuilder.append('_').length();
/* 507 */       for (j = 0;; j++) {
/* 508 */         String str1 = stringBuilder.append(j).toString();
/* 509 */         if (!paramMap.containsKey(str1) && this.temporaryUniqueNames.add(str1)) {
/* 510 */           return str1;
/*     */         }
/* 512 */         stringBuilder.setLength(i);
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
/*     */   public String getUniqueConstraintName(SessionLocal paramSessionLocal, Table paramTable) {
/*     */     ConcurrentHashMap<String, Constraint> concurrentHashMap;
/* 526 */     if (paramTable.isTemporary() && !paramTable.isGlobalTemporary()) {
/* 527 */       HashMap hashMap = paramSessionLocal.getLocalTempTableConstraints();
/*     */     } else {
/* 529 */       concurrentHashMap = this.constraints;
/*     */     } 
/* 531 */     return getUniqueName((DbObject)paramTable, (Map)concurrentHashMap, "CONSTRAINT_");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUniqueDomainConstraintName(SessionLocal paramSessionLocal, Domain paramDomain) {
/* 542 */     return getUniqueName(paramDomain, (Map)this.constraints, "CONSTRAINT_");
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
/*     */   public String getUniqueIndexName(SessionLocal paramSessionLocal, Table paramTable, String paramString) {
/*     */     ConcurrentHashMap<String, Index> concurrentHashMap;
/* 555 */     if (paramTable.isTemporary() && !paramTable.isGlobalTemporary()) {
/* 556 */       HashMap hashMap = paramSessionLocal.getLocalTempTableIndexes();
/*     */     } else {
/* 558 */       concurrentHashMap = this.indexes;
/*     */     } 
/* 560 */     return getUniqueName((DbObject)paramTable, (Map)concurrentHashMap, paramString);
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
/*     */   public Table getTableOrView(SessionLocal paramSessionLocal, String paramString) {
/* 573 */     Table table = this.tablesAndViews.get(paramString);
/* 574 */     if (table == null) {
/* 575 */       if (paramSessionLocal != null) {
/* 576 */         table = paramSessionLocal.findLocalTempTable(paramString);
/*     */       }
/* 578 */       if (table == null) {
/* 579 */         throw DbException.get(42102, paramString);
/*     */       }
/*     */     } 
/* 582 */     return table;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Domain getDomain(String paramString) {
/* 593 */     Domain domain = this.domains.get(paramString);
/* 594 */     if (domain == null) {
/* 595 */       throw DbException.get(90120, paramString);
/*     */     }
/* 597 */     return domain;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Index getIndex(String paramString) {
/* 608 */     Index index = this.indexes.get(paramString);
/* 609 */     if (index == null) {
/* 610 */       throw DbException.get(42112, paramString);
/*     */     }
/* 612 */     return index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Constraint getConstraint(String paramString) {
/* 623 */     Constraint constraint = this.constraints.get(paramString);
/* 624 */     if (constraint == null) {
/* 625 */       throw DbException.get(90057, paramString);
/*     */     }
/* 627 */     return constraint;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Constant getConstant(String paramString) {
/* 638 */     Constant constant = this.constants.get(paramString);
/* 639 */     if (constant == null) {
/* 640 */       throw DbException.get(90115, paramString);
/*     */     }
/* 642 */     return constant;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Sequence getSequence(String paramString) {
/* 653 */     Sequence sequence = this.sequences.get(paramString);
/* 654 */     if (sequence == null) {
/* 655 */       throw DbException.get(90036, paramString);
/*     */     }
/* 657 */     return sequence;
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
/*     */   public ArrayList<SchemaObject> getAll(ArrayList<SchemaObject> paramArrayList) {
/* 670 */     if (paramArrayList == null) {
/* 671 */       paramArrayList = Utils.newSmallArrayList();
/*     */     }
/* 673 */     paramArrayList.addAll((Collection)this.tablesAndViews.values());
/* 674 */     paramArrayList.addAll(this.domains.values());
/* 675 */     paramArrayList.addAll((Collection)this.synonyms.values());
/* 676 */     paramArrayList.addAll(this.sequences.values());
/* 677 */     paramArrayList.addAll((Collection)this.indexes.values());
/* 678 */     paramArrayList.addAll(this.triggers.values());
/* 679 */     paramArrayList.addAll((Collection)this.constraints.values());
/* 680 */     paramArrayList.addAll(this.constants.values());
/* 681 */     paramArrayList.addAll(this.functionsAndAggregates.values());
/* 682 */     return paramArrayList;
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
/*     */   public void getAll(int paramInt, ArrayList<SchemaObject> paramArrayList) {
/* 694 */     paramArrayList.addAll(getMap(paramInt).values());
/*     */   }
/*     */   
/*     */   public Collection<Domain> getAllDomains() {
/* 698 */     return this.domains.values();
/*     */   }
/*     */   
/*     */   public Collection<Constraint> getAllConstraints() {
/* 702 */     return this.constraints.values();
/*     */   }
/*     */   
/*     */   public Collection<Constant> getAllConstants() {
/* 706 */     return this.constants.values();
/*     */   }
/*     */   
/*     */   public Collection<Sequence> getAllSequences() {
/* 710 */     return this.sequences.values();
/*     */   }
/*     */   
/*     */   public Collection<TriggerObject> getAllTriggers() {
/* 714 */     return this.triggers.values();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<Table> getAllTablesAndViews(SessionLocal paramSessionLocal) {
/* 724 */     return this.tablesAndViews.values();
/*     */   }
/*     */   
/*     */   public Collection<Index> getAllIndexes() {
/* 728 */     return this.indexes.values();
/*     */   }
/*     */   
/*     */   public Collection<TableSynonym> getAllSynonyms() {
/* 732 */     return this.synonyms.values();
/*     */   }
/*     */   
/*     */   public Collection<UserDefinedFunction> getAllFunctionsAndAggregates() {
/* 736 */     return this.functionsAndAggregates.values();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Table getTableOrViewByName(SessionLocal paramSessionLocal, String paramString) {
/* 747 */     return this.tablesAndViews.get(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(SchemaObject paramSchemaObject) {
/* 756 */     String str = paramSchemaObject.getName();
/* 757 */     Map<String, SchemaObject> map = getMap(paramSchemaObject.getType());
/* 758 */     if (map.remove(str) == null) {
/* 759 */       throw DbException.getInternalError("not found: " + str);
/*     */     }
/* 761 */     freeUniqueName(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Table createTable(CreateTableData paramCreateTableData) {
/* 771 */     synchronized (this.database) {
/* 772 */       if (!paramCreateTableData.temporary || paramCreateTableData.globalTemporary) {
/* 773 */         this.database.lockMeta(paramCreateTableData.session);
/*     */       }
/* 775 */       paramCreateTableData.schema = this;
/* 776 */       String str = paramCreateTableData.tableEngine;
/* 777 */       if (str == null) {
/* 778 */         DbSettings dbSettings = this.database.getSettings();
/* 779 */         str = dbSettings.defaultTableEngine;
/* 780 */         if (str == null) {
/* 781 */           return (Table)this.database.getStore().createTable(paramCreateTableData);
/*     */         }
/* 783 */         paramCreateTableData.tableEngine = str;
/*     */       } 
/* 785 */       if (paramCreateTableData.tableEngineParams == null) {
/* 786 */         paramCreateTableData.tableEngineParams = this.tableEngineParams;
/*     */       }
/* 788 */       return this.database.getTableEngine(str).createTable(paramCreateTableData);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TableSynonym createSynonym(CreateSynonymData paramCreateSynonymData) {
/* 799 */     synchronized (this.database) {
/* 800 */       this.database.lockMeta(paramCreateSynonymData.session);
/* 801 */       paramCreateSynonymData.schema = this;
/* 802 */       return new TableSynonym(paramCreateSynonymData);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TableLink createTableLink(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, boolean paramBoolean1, boolean paramBoolean2) {
/* 824 */     synchronized (this.database) {
/* 825 */       return new TableLink(this, paramInt, paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, paramBoolean1, paramBoolean2);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\schema\Schema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */