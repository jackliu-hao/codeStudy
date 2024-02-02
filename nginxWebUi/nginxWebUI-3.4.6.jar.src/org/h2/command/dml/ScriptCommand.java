/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Array;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import org.h2.constraint.Constraint;
/*     */ import org.h2.engine.Comment;
/*     */ import org.h2.engine.Constants;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.Right;
/*     */ import org.h2.engine.RightOwner;
/*     */ import org.h2.engine.Role;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.engine.Setting;
/*     */ import org.h2.engine.User;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.index.Cursor;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.schema.Constant;
/*     */ import org.h2.schema.Domain;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.schema.Sequence;
/*     */ import org.h2.schema.TriggerObject;
/*     */ import org.h2.schema.UserDefinedFunction;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.PlanItem;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableType;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueVarchar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ScriptCommand
/*     */   extends ScriptBase
/*     */ {
/*     */   private static final Comparator<? super DbObject> BY_NAME_COMPARATOR;
/*     */   
/*     */   static {
/*  75 */     BY_NAME_COMPARATOR = ((paramDbObject1, paramDbObject2) -> {
/*     */         if (paramDbObject1 instanceof SchemaObject && paramDbObject2 instanceof SchemaObject) {
/*     */           int i = ((SchemaObject)paramDbObject1).getSchema().getName().compareTo(((SchemaObject)paramDbObject2).getSchema().getName());
/*     */           if (i != 0)
/*     */             return i; 
/*     */         } 
/*     */         return paramDbObject1.getName().compareTo(paramDbObject2.getName());
/*     */       });
/*     */   }
/*     */   
/*  85 */   private Charset charset = StandardCharsets.UTF_8;
/*     */   
/*     */   private Set<String> schemaNames;
/*     */   
/*     */   private Collection<Table> tables;
/*     */   
/*     */   private boolean passwords;
/*     */   
/*     */   private boolean data;
/*     */   
/*     */   private boolean settings;
/*     */   private boolean drop;
/*     */   private boolean simple;
/*     */   private boolean withColumns;
/*     */   private boolean version = true;
/*     */   private LocalResult result;
/*     */   private String lineSeparatorString;
/*     */   private byte[] lineSeparator;
/*     */   private byte[] buffer;
/*     */   private boolean tempLobTableCreated;
/*     */   private int nextLobId;
/* 106 */   private int lobBlockSize = 4096;
/*     */   
/*     */   public ScriptCommand(SessionLocal paramSessionLocal) {
/* 109 */     super(paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isQuery() {
/* 114 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSchemaNames(Set<String> paramSet) {
/* 120 */     this.schemaNames = paramSet;
/*     */   }
/*     */   
/*     */   public void setTables(Collection<Table> paramCollection) {
/* 124 */     this.tables = paramCollection;
/*     */   }
/*     */   
/*     */   public void setData(boolean paramBoolean) {
/* 128 */     this.data = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setPasswords(boolean paramBoolean) {
/* 132 */     this.passwords = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setSettings(boolean paramBoolean) {
/* 136 */     this.settings = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setLobBlockSize(long paramLong) {
/* 140 */     this.lobBlockSize = MathUtils.convertLongToInt(paramLong);
/*     */   }
/*     */   
/*     */   public void setDrop(boolean paramBoolean) {
/* 144 */     this.drop = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface queryMeta() {
/* 149 */     LocalResult localResult = createResult();
/* 150 */     localResult.done();
/* 151 */     return (ResultInterface)localResult;
/*     */   }
/*     */   
/*     */   private LocalResult createResult() {
/* 155 */     return new LocalResult(this.session, new Expression[] { (Expression)new ExpressionColumn(this.session
/* 156 */             .getDatabase(), new Column("SCRIPT", TypeInfo.TYPE_VARCHAR)) }1, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface query(long paramLong) {
/* 161 */     this.session.getUser().checkAdmin();
/* 162 */     reset();
/* 163 */     Database database = this.session.getDatabase();
/* 164 */     if (this.schemaNames != null) {
/* 165 */       for (String str : this.schemaNames) {
/* 166 */         Schema schema = database.findSchema(str);
/* 167 */         if (schema == null) {
/* 168 */           throw DbException.get(90079, str);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     try {
/* 174 */       this.result = createResult();
/* 175 */       deleteStore();
/* 176 */       openOutput();
/* 177 */       if (this.out != null) {
/* 178 */         this.buffer = new byte[4096];
/*     */       }
/* 180 */       if (this.version) {
/* 181 */         add("-- H2 " + Constants.VERSION, true);
/*     */       }
/* 183 */       if (this.settings) {
/* 184 */         for (Setting setting : database.getAllSettings()) {
/* 185 */           if (setting.getName().equals(SetTypes.getTypeName(28))) {
/*     */             continue;
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 191 */           add(setting.getCreateSQL(), false);
/*     */         } 
/*     */       }
/* 194 */       if (this.out != null) {
/* 195 */         add("", true);
/*     */       }
/* 197 */       RightOwner[] arrayOfRightOwner = (RightOwner[])database.getAllUsersAndRoles().toArray((Object[])new RightOwner[0]);
/*     */       
/* 199 */       Arrays.sort(arrayOfRightOwner, (paramRightOwner1, paramRightOwner2) -> {
/*     */             boolean bool = paramRightOwner1 instanceof User;
/*     */             if (bool != paramRightOwner2 instanceof User) {
/*     */               return bool ? -1 : 1;
/*     */             }
/*     */             if (bool) {
/*     */               bool = ((User)paramRightOwner1).isAdmin();
/*     */               if (bool != ((User)paramRightOwner2).isAdmin()) {
/*     */                 return bool ? -1 : 1;
/*     */               }
/*     */             } 
/*     */             return paramRightOwner1.getName().compareTo(paramRightOwner2.getName());
/*     */           });
/* 212 */       for (RightOwner rightOwner : arrayOfRightOwner) {
/* 213 */         if (rightOwner instanceof User) {
/* 214 */           add(((User)rightOwner).getCreateSQL(this.passwords), false);
/*     */         } else {
/* 216 */           add(((Role)rightOwner).getCreateSQL(true), false);
/*     */         } 
/*     */       } 
/* 219 */       ArrayList<Schema> arrayList = new ArrayList();
/* 220 */       for (Schema schema : database.getAllSchemas()) {
/* 221 */         if (excludeSchema(schema)) {
/*     */           continue;
/*     */         }
/* 224 */         arrayList.add(schema);
/* 225 */         add(schema.getCreateSQL(), false);
/*     */       } 
/* 227 */       dumpDomains(arrayList);
/* 228 */       for (Schema schema : arrayList) {
/* 229 */         for (Constant constant : (Constant[])sorted(schema.getAllConstants(), Constant.class)) {
/* 230 */           add(constant.getCreateSQL(), false);
/*     */         }
/*     */       } 
/*     */       
/* 234 */       ArrayList arrayList1 = database.getAllTablesAndViews();
/*     */ 
/*     */       
/* 237 */       arrayList1.sort(Comparator.comparingInt(DbObject::getId));
/*     */ 
/*     */       
/* 240 */       for (Table table : arrayList1) {
/* 241 */         if (excludeSchema(table.getSchema())) {
/*     */           continue;
/*     */         }
/* 244 */         if (excludeTable(table)) {
/*     */           continue;
/*     */         }
/* 247 */         if (table.isHidden()) {
/*     */           continue;
/*     */         }
/* 250 */         table.lock(this.session, 0);
/* 251 */         String str = table.getCreateSQL();
/* 252 */         if (str == null) {
/*     */           continue;
/*     */         }
/*     */         
/* 256 */         if (this.drop) {
/* 257 */           add(table.getDropSQL(), false);
/*     */         }
/*     */       } 
/* 260 */       for (Schema schema : arrayList) {
/* 261 */         for (UserDefinedFunction userDefinedFunction : (UserDefinedFunction[])sorted(schema.getAllFunctionsAndAggregates(), UserDefinedFunction.class)) {
/*     */           
/* 263 */           if (this.drop) {
/* 264 */             add(userDefinedFunction.getDropSQL(), false);
/*     */           }
/* 266 */           add(userDefinedFunction.getCreateSQL(), false);
/*     */         } 
/*     */       } 
/* 269 */       for (Schema schema : arrayList) {
/* 270 */         for (Sequence sequence : (Sequence[])sorted(schema.getAllSequences(), Sequence.class)) {
/* 271 */           if (!sequence.getBelongsToTable()) {
/*     */ 
/*     */             
/* 274 */             if (this.drop) {
/* 275 */               add(sequence.getDropSQL(), false);
/*     */             }
/* 277 */             add(sequence.getCreateSQL(), false);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 282 */       int i = 0;
/* 283 */       for (Table table : arrayList1) {
/* 284 */         if (excludeSchema(table.getSchema())) {
/*     */           continue;
/*     */         }
/* 287 */         if (excludeTable(table)) {
/*     */           continue;
/*     */         }
/* 290 */         if (table.isHidden()) {
/*     */           continue;
/*     */         }
/* 293 */         table.lock(this.session, 0);
/* 294 */         String str = table.getCreateSQL();
/* 295 */         if (str == null) {
/*     */           continue;
/*     */         }
/*     */         
/* 299 */         TableType tableType = table.getTableType();
/* 300 */         add(str, false);
/* 301 */         ArrayList arrayList3 = table.getConstraints();
/* 302 */         if (arrayList3 != null) {
/* 303 */           for (Constraint constraint : arrayList3) {
/* 304 */             if (Constraint.Type.PRIMARY_KEY == constraint.getConstraintType()) {
/* 305 */               add(constraint.getCreateSQLWithoutIndexes(), false);
/*     */             }
/*     */           } 
/*     */         }
/* 309 */         if (TableType.TABLE == tableType) {
/* 310 */           if (table.canGetRowCount(this.session)) {
/*     */ 
/*     */             
/* 313 */             StringBuilder stringBuilder = (new StringBuilder("-- ")).append(table.getRowCountApproximation(this.session)).append(" +/- SELECT COUNT(*) FROM ");
/* 314 */             table.getSQL(stringBuilder, 3);
/* 315 */             add(stringBuilder.toString(), false);
/*     */           } 
/* 317 */           if (this.data) {
/* 318 */             i = generateInsertValues(i, table);
/*     */           }
/*     */         } 
/* 321 */         ArrayList<Index> arrayList4 = table.getIndexes();
/* 322 */         for (byte b = 0; arrayList4 != null && b < arrayList4.size(); b++) {
/* 323 */           Index index = arrayList4.get(b);
/* 324 */           if (!index.getIndexType().getBelongsToConstraint()) {
/* 325 */             add(index.getCreateSQL(), false);
/*     */           }
/*     */         } 
/*     */       } 
/* 329 */       if (this.tempLobTableCreated) {
/* 330 */         add("DROP TABLE IF EXISTS SYSTEM_LOB_STREAM", true);
/* 331 */         add("DROP ALIAS IF EXISTS SYSTEM_COMBINE_CLOB", true);
/* 332 */         add("DROP ALIAS IF EXISTS SYSTEM_COMBINE_BLOB", true);
/* 333 */         this.tempLobTableCreated = false;
/*     */       } 
/*     */       
/* 336 */       ArrayList<Constraint> arrayList2 = new ArrayList();
/* 337 */       for (Schema schema : arrayList) {
/* 338 */         for (Constraint constraint : schema.getAllConstraints()) {
/* 339 */           if (excludeTable(constraint.getTable())) {
/*     */             continue;
/*     */           }
/* 342 */           Constraint.Type type = constraint.getConstraintType();
/* 343 */           if (type != Constraint.Type.DOMAIN && constraint.getTable().isHidden()) {
/*     */             continue;
/*     */           }
/* 346 */           if (type != Constraint.Type.PRIMARY_KEY) {
/* 347 */             arrayList2.add(constraint);
/*     */           }
/*     */         } 
/*     */       } 
/* 351 */       arrayList2.sort(null);
/* 352 */       for (Constraint constraint : arrayList2) {
/* 353 */         add(constraint.getCreateSQLWithoutIndexes(), false);
/*     */       }
/*     */       
/* 356 */       for (Schema schema : arrayList) {
/* 357 */         for (TriggerObject triggerObject : schema.getAllTriggers()) {
/* 358 */           if (excludeTable(triggerObject.getTable())) {
/*     */             continue;
/*     */           }
/* 361 */           add(triggerObject.getCreateSQL(), false);
/*     */         } 
/*     */       } 
/*     */       
/* 365 */       dumpRights(database);
/*     */       
/* 367 */       for (Comment comment : database.getAllComments()) {
/* 368 */         add(comment.getCreateSQL(), false);
/*     */       }
/* 370 */       if (this.out != null) {
/* 371 */         this.out.close();
/*     */       }
/* 373 */     } catch (IOException iOException) {
/* 374 */       throw DbException.convertIOException(iOException, getFileName());
/*     */     } finally {
/* 376 */       closeIO();
/*     */     } 
/* 378 */     this.result.done();
/* 379 */     LocalResult localResult = this.result;
/* 380 */     reset();
/* 381 */     return (ResultInterface)localResult;
/*     */   }
/*     */   
/*     */   private void dumpDomains(ArrayList<Schema> paramArrayList) throws IOException {
/* 385 */     TreeMap<DbObject, Object> treeMap = new TreeMap<>(BY_NAME_COMPARATOR);
/* 386 */     TreeSet<DbObject> treeSet = new TreeSet<>(BY_NAME_COMPARATOR);
/* 387 */     for (Schema schema : paramArrayList) {
/* 388 */       for (Domain domain1 : (Domain[])sorted(schema.getAllDomains(), Domain.class)) {
/* 389 */         Domain domain2 = domain1.getDomain();
/* 390 */         if (domain2 == null) {
/* 391 */           addDomain(domain1);
/*     */         } else {
/* 393 */           TreeSet<DbObject> treeSet1 = (TreeSet)treeMap.get(domain2);
/* 394 */           if (treeSet1 == null) {
/* 395 */             treeSet1 = new TreeSet<>(BY_NAME_COMPARATOR);
/* 396 */             treeMap.put(domain2, treeSet1);
/*     */           } 
/* 398 */           treeSet1.add(domain1);
/* 399 */           if (domain2.getDomain() == null || !paramArrayList.contains(domain2.getSchema())) {
/* 400 */             treeSet.add(domain2);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 405 */     while (!treeMap.isEmpty()) {
/* 406 */       TreeSet<DbObject> treeSet1 = new TreeSet<>(BY_NAME_COMPARATOR);
/* 407 */       for (Domain domain : treeSet) {
/* 408 */         TreeSet treeSet2 = (TreeSet)treeMap.remove(domain);
/* 409 */         if (treeSet2 != null) {
/* 410 */           for (Domain domain1 : treeSet2) {
/* 411 */             addDomain(domain1);
/* 412 */             treeSet1.add(domain1);
/*     */           } 
/*     */         }
/*     */       } 
/* 416 */       treeSet = treeSet1;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void dumpRights(Database paramDatabase) throws IOException {
/* 421 */     Right[] arrayOfRight = (Right[])paramDatabase.getAllRights().toArray((Object[])new Right[0]);
/* 422 */     Arrays.sort(arrayOfRight, (paramRight1, paramRight2) -> {
/*     */           Role role1 = paramRight1.getGrantedRole();
/*     */           Role role2 = paramRight2.getGrantedRole();
/*     */           if (((role1 == null) ? true : false) != ((role2 == null) ? true : false)) {
/*     */             return (role1 == null) ? -1 : 1;
/*     */           }
/*     */           if (role1 == null) {
/*     */             DbObject dbObject1 = paramRight1.getGrantedObject();
/*     */             DbObject dbObject2 = paramRight2.getGrantedObject();
/*     */             if (((dbObject1 == null) ? true : false) != ((dbObject2 == null) ? true : false)) {
/*     */               return (dbObject1 == null) ? -1 : 1;
/*     */             }
/*     */             if (dbObject1 != null) {
/*     */               if (dbObject1 instanceof Schema != dbObject2 instanceof Schema) {
/*     */                 return (dbObject1 instanceof Schema) ? -1 : 1;
/*     */               }
/*     */               int i = dbObject1.getName().compareTo(dbObject2.getName());
/*     */               if (i != 0)
/*     */                 return i; 
/*     */             } 
/*     */           } else {
/*     */             int i = role1.getName().compareTo(role2.getName());
/*     */             if (i != 0)
/*     */               return i; 
/*     */           } 
/*     */           return paramRight1.getGrantee().getName().compareTo(paramRight2.getGrantee().getName());
/*     */         });
/* 449 */     for (Right right : arrayOfRight) {
/* 450 */       DbObject dbObject = right.getGrantedObject();
/* 451 */       if (dbObject != null) {
/* 452 */         if (dbObject instanceof Schema) {
/* 453 */           if (excludeSchema((Schema)dbObject)) {
/*     */             continue;
/*     */           }
/* 456 */         } else if (dbObject instanceof Table) {
/* 457 */           Table table = (Table)dbObject;
/* 458 */           if (excludeSchema(table.getSchema())) {
/*     */             continue;
/*     */           }
/* 461 */           if (excludeTable(table)) {
/*     */             continue;
/*     */           }
/*     */         } 
/*     */       }
/* 466 */       add(right.getCreateSQL(), false);
/*     */       continue;
/*     */     } 
/*     */   }
/*     */   private void addDomain(Domain paramDomain) throws IOException {
/* 471 */     if (this.drop) {
/* 472 */       add(paramDomain.getDropSQL(), false);
/*     */     }
/* 474 */     add(paramDomain.getCreateSQL(), false);
/*     */   }
/*     */ 
/*     */   
/*     */   private static <T extends DbObject> T[] sorted(Collection<T> paramCollection, Class<T> paramClass) {
/* 479 */     DbObject[] arrayOfDbObject = paramCollection.<DbObject>toArray((DbObject[])Array.newInstance(paramClass, 0));
/* 480 */     Arrays.sort(arrayOfDbObject, BY_NAME_COMPARATOR);
/* 481 */     return (T[])arrayOfDbObject;
/*     */   }
/*     */   
/*     */   private int generateInsertValues(int paramInt, Table paramTable) throws IOException {
/* 485 */     PlanItem planItem = paramTable.getBestPlanItem(this.session, null, null, -1, null, null);
/* 486 */     Index index = planItem.getIndex();
/* 487 */     Cursor cursor = index.find(this.session, null, null);
/* 488 */     Column[] arrayOfColumn = paramTable.getColumns();
/* 489 */     boolean bool1 = false, bool2 = false;
/* 490 */     for (Column column : arrayOfColumn) {
/* 491 */       if (column.isGeneratedAlways()) {
/* 492 */         if (column.isIdentity()) {
/* 493 */           bool2 = true;
/*     */         } else {
/* 495 */           bool1 = true;
/*     */         } 
/*     */       }
/*     */     } 
/* 499 */     StringBuilder stringBuilder = new StringBuilder("INSERT INTO ");
/* 500 */     paramTable.getSQL(stringBuilder, 0);
/* 501 */     if (bool1 || bool2 || this.withColumns) {
/* 502 */       stringBuilder.append('(');
/* 503 */       boolean bool = false;
/* 504 */       for (Column column : arrayOfColumn) {
/* 505 */         if (!column.isGenerated()) {
/* 506 */           if (bool) {
/* 507 */             stringBuilder.append(", ");
/*     */           }
/* 509 */           bool = true;
/* 510 */           column.getSQL(stringBuilder, 0);
/*     */         } 
/*     */       } 
/* 513 */       stringBuilder.append(')');
/* 514 */       if (bool2) {
/* 515 */         stringBuilder.append(" OVERRIDING SYSTEM VALUE");
/*     */       }
/*     */     } 
/* 518 */     stringBuilder.append(" VALUES");
/* 519 */     if (!this.simple) {
/* 520 */       stringBuilder.append('\n');
/*     */     }
/* 522 */     stringBuilder.append('(');
/* 523 */     String str = stringBuilder.toString();
/* 524 */     stringBuilder = null;
/* 525 */     int i = arrayOfColumn.length;
/* 526 */     while (cursor.next()) {
/* 527 */       Row row = cursor.get();
/* 528 */       if (stringBuilder == null) {
/* 529 */         stringBuilder = new StringBuilder(str);
/*     */       } else {
/* 531 */         stringBuilder.append(",\n(");
/*     */       } 
/* 533 */       boolean bool = false;
/* 534 */       for (byte b = 0; b < i; b++) {
/* 535 */         if (!arrayOfColumn[b].isGenerated()) {
/*     */ 
/*     */           
/* 538 */           if (bool) {
/* 539 */             stringBuilder.append(", ");
/*     */           }
/* 541 */           bool = true;
/* 542 */           Value value = row.getValue(b);
/* 543 */           if (value.getType().getPrecision() > this.lobBlockSize) {
/*     */             
/* 545 */             if (value.getValueType() == 3) {
/* 546 */               int j = writeLobStream(value);
/* 547 */               stringBuilder.append("SYSTEM_COMBINE_CLOB(").append(j).append(')');
/* 548 */             } else if (value.getValueType() == 7) {
/* 549 */               int j = writeLobStream(value);
/* 550 */               stringBuilder.append("SYSTEM_COMBINE_BLOB(").append(j).append(')');
/*     */             } else {
/* 552 */               value.getSQL(stringBuilder, 4);
/*     */             } 
/*     */           } else {
/* 555 */             value.getSQL(stringBuilder, 4);
/*     */           } 
/*     */         } 
/* 558 */       }  stringBuilder.append(')');
/* 559 */       paramInt++;
/* 560 */       if ((paramInt & 0x7F) == 0) {
/* 561 */         checkCanceled();
/*     */       }
/* 563 */       if (this.simple || stringBuilder.length() > 4096) {
/* 564 */         add(stringBuilder.toString(), true);
/* 565 */         stringBuilder = null;
/*     */       } 
/*     */     } 
/* 568 */     if (stringBuilder != null) {
/* 569 */       add(stringBuilder.toString(), true);
/*     */     }
/* 571 */     return paramInt;
/*     */   } private int writeLobStream(Value paramValue) throws IOException {
/*     */     byte[] arrayOfByte;
/*     */     char[] arrayOfChar;
/* 575 */     if (!this.tempLobTableCreated) {
/* 576 */       add("CREATE CACHED LOCAL TEMPORARY TABLE IF NOT EXISTS SYSTEM_LOB_STREAM(ID INT NOT NULL, PART INT NOT NULL, CDATA VARCHAR, BDATA VARBINARY)", true);
/*     */ 
/*     */ 
/*     */       
/* 580 */       add("ALTER TABLE SYSTEM_LOB_STREAM ADD CONSTRAINT SYSTEM_LOB_STREAM_PRIMARY_KEY PRIMARY KEY(ID, PART)", true);
/*     */       
/* 582 */       String str = getClass().getName();
/* 583 */       add("CREATE ALIAS IF NOT EXISTS SYSTEM_COMBINE_CLOB FOR '" + str + ".combineClob'", true);
/* 584 */       add("CREATE ALIAS IF NOT EXISTS SYSTEM_COMBINE_BLOB FOR '" + str + ".combineBlob'", true);
/* 585 */       this.tempLobTableCreated = true;
/*     */     } 
/* 587 */     int i = this.nextLobId++;
/* 588 */     switch (paramValue.getValueType()) {
/*     */       case 7:
/* 590 */         arrayOfByte = new byte[this.lobBlockSize];
/* 591 */         try (InputStream null = paramValue.getInputStream()) {
/* 592 */           for (byte b = 0;; b++) {
/* 593 */             StringBuilder stringBuilder = new StringBuilder(this.lobBlockSize * 2);
/* 594 */             stringBuilder.append("INSERT INTO SYSTEM_LOB_STREAM VALUES(").append(i)
/* 595 */               .append(", ").append(b).append(", NULL, X'");
/* 596 */             int j = IOUtils.readFully(inputStream, arrayOfByte, this.lobBlockSize);
/* 597 */             if (j <= 0) {
/*     */               break;
/*     */             }
/* 600 */             StringUtils.convertBytesToHex(stringBuilder, arrayOfByte, j).append("')");
/* 601 */             String str = stringBuilder.toString();
/* 602 */             add(str, true);
/*     */           } 
/*     */         } 
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
/* 630 */         return i;case 3: arrayOfChar = new char[this.lobBlockSize]; try (Reader null = paramValue.getReader()) { for (byte b = 0;; b++) { StringBuilder stringBuilder = new StringBuilder(this.lobBlockSize * 2); stringBuilder.append("INSERT INTO SYSTEM_LOB_STREAM VALUES(").append(i).append(", ").append(b).append(", "); int j = IOUtils.readFully(reader, arrayOfChar, this.lobBlockSize); if (j == 0) break;  StringUtils.quoteStringSQL(stringBuilder, new String(arrayOfChar, 0, j)).append(", NULL)"); String str = stringBuilder.toString(); add(str, true); }  }  return i;
/*     */     } 
/*     */     throw DbException.getInternalError("type:" + paramValue.getValueType());
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
/*     */   public static InputStream combineBlob(Connection paramConnection, int paramInt) throws SQLException {
/* 645 */     if (paramInt < 0) {
/* 646 */       return null;
/*     */     }
/* 648 */     final ResultSet rs = getLobStream(paramConnection, "BDATA", paramInt);
/* 649 */     return new InputStream() {
/*     */         private InputStream current;
/*     */         private boolean closed;
/*     */         
/*     */         public int read() throws IOException {
/*     */           try {
/*     */             while (true)
/* 656 */             { if (this.current == null) {
/* 657 */                 if (this.closed) {
/* 658 */                   return -1;
/*     */                 }
/* 660 */                 if (!rs.next()) {
/* 661 */                   close();
/* 662 */                   return -1;
/*     */                 } 
/* 664 */                 this.current = rs.getBinaryStream(1);
/* 665 */                 this.current = new BufferedInputStream(this.current);
/*     */               } 
/* 667 */               int i = this.current.read();
/* 668 */               if (i >= 0) {
/* 669 */                 return i;
/*     */               }
/* 671 */               this.current = null; } 
/* 672 */           } catch (SQLException sQLException) {
/* 673 */             throw DataUtils.convertToIOException(sQLException);
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public void close() throws IOException {
/* 679 */           if (this.closed) {
/*     */             return;
/*     */           }
/* 682 */           this.closed = true;
/*     */           try {
/* 684 */             rs.close();
/* 685 */           } catch (SQLException sQLException) {
/* 686 */             throw DataUtils.convertToIOException(sQLException);
/*     */           } 
/*     */         }
/*     */       };
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
/*     */   public static Reader combineClob(Connection paramConnection, int paramInt) throws SQLException {
/* 702 */     if (paramInt < 0) {
/* 703 */       return null;
/*     */     }
/* 705 */     final ResultSet rs = getLobStream(paramConnection, "CDATA", paramInt);
/* 706 */     return new Reader() {
/*     */         private Reader current;
/*     */         private boolean closed;
/*     */         
/*     */         public int read() throws IOException {
/*     */           try {
/*     */             while (true)
/* 713 */             { if (this.current == null) {
/* 714 */                 if (this.closed) {
/* 715 */                   return -1;
/*     */                 }
/* 717 */                 if (!rs.next()) {
/* 718 */                   close();
/* 719 */                   return -1;
/*     */                 } 
/* 721 */                 this.current = rs.getCharacterStream(1);
/* 722 */                 this.current = new BufferedReader(this.current);
/*     */               } 
/* 724 */               int i = this.current.read();
/* 725 */               if (i >= 0) {
/* 726 */                 return i;
/*     */               }
/* 728 */               this.current = null; } 
/* 729 */           } catch (SQLException sQLException) {
/* 730 */             throw DataUtils.convertToIOException(sQLException);
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public void close() throws IOException {
/* 736 */           if (this.closed) {
/*     */             return;
/*     */           }
/* 739 */           this.closed = true;
/*     */           try {
/* 741 */             rs.close();
/* 742 */           } catch (SQLException sQLException) {
/* 743 */             throw DataUtils.convertToIOException(sQLException);
/*     */           } 
/*     */         }
/*     */         
/*     */         public int read(char[] param1ArrayOfchar, int param1Int1, int param1Int2) throws IOException {
/* 748 */           if (param1Int2 == 0) {
/* 749 */             return 0;
/*     */           }
/* 751 */           int i = read();
/* 752 */           if (i == -1) {
/* 753 */             return -1;
/*     */           }
/* 755 */           param1ArrayOfchar[param1Int1] = (char)i;
/* 756 */           byte b = 1;
/* 757 */           for (; b < param1Int2; b++) {
/* 758 */             i = read();
/* 759 */             if (i == -1) {
/*     */               break;
/*     */             }
/* 762 */             param1ArrayOfchar[param1Int1 + b] = (char)i;
/*     */           } 
/* 764 */           return b;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   private static ResultSet getLobStream(Connection paramConnection, String paramString, int paramInt) throws SQLException {
/* 771 */     PreparedStatement preparedStatement = paramConnection.prepareStatement("SELECT " + paramString + " FROM SYSTEM_LOB_STREAM WHERE ID=? ORDER BY PART");
/*     */     
/* 773 */     preparedStatement.setInt(1, paramInt);
/* 774 */     return preparedStatement.executeQuery();
/*     */   }
/*     */   
/*     */   private void reset() {
/* 778 */     this.result = null;
/* 779 */     this.buffer = null;
/* 780 */     this.lineSeparatorString = System.lineSeparator();
/* 781 */     this.lineSeparator = this.lineSeparatorString.getBytes(this.charset);
/*     */   }
/*     */   
/*     */   private boolean excludeSchema(Schema paramSchema) {
/* 785 */     if (this.schemaNames != null && !this.schemaNames.contains(paramSchema.getName())) {
/* 786 */       return true;
/*     */     }
/* 788 */     if (this.tables != null) {
/*     */       
/* 790 */       for (Table table : paramSchema.getAllTablesAndViews(this.session)) {
/* 791 */         if (this.tables.contains(table)) {
/* 792 */           return false;
/*     */         }
/*     */       } 
/* 795 */       return true;
/*     */     } 
/* 797 */     return false;
/*     */   }
/*     */   
/*     */   private boolean excludeTable(Table paramTable) {
/* 801 */     return (this.tables != null && !this.tables.contains(paramTable));
/*     */   }
/*     */   
/*     */   private void add(String paramString, boolean paramBoolean) throws IOException {
/* 805 */     if (paramString == null) {
/*     */       return;
/*     */     }
/* 808 */     if (this.lineSeparator.length > 1 || this.lineSeparator[0] != 10) {
/* 809 */       paramString = StringUtils.replaceAll(paramString, "\n", this.lineSeparatorString);
/*     */     }
/* 811 */     paramString = paramString + ";";
/* 812 */     if (this.out != null) {
/* 813 */       byte[] arrayOfByte = paramString.getBytes(this.charset);
/* 814 */       int i = MathUtils.roundUpInt(arrayOfByte.length + this.lineSeparator.length, 16);
/*     */       
/* 816 */       this.buffer = Utils.copy(arrayOfByte, this.buffer);
/*     */       
/* 818 */       if (i > this.buffer.length) {
/* 819 */         this.buffer = new byte[i];
/*     */       }
/* 821 */       System.arraycopy(arrayOfByte, 0, this.buffer, 0, arrayOfByte.length); int j;
/* 822 */       for (j = arrayOfByte.length; j < i - this.lineSeparator.length; j++)
/* 823 */         this.buffer[j] = 32; 
/*     */       int k;
/* 825 */       for (j = 0, k = i - this.lineSeparator.length; k < i; k++, j++) {
/* 826 */         this.buffer[k] = this.lineSeparator[j];
/*     */       }
/* 828 */       this.out.write(this.buffer, 0, i);
/* 829 */       if (!paramBoolean) {
/* 830 */         this.result.addRow(new Value[] { ValueVarchar.get(paramString) });
/*     */       }
/*     */     } else {
/* 833 */       this.result.addRow(new Value[] { ValueVarchar.get(paramString) });
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setSimple(boolean paramBoolean) {
/* 838 */     this.simple = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setWithColumns(boolean paramBoolean) {
/* 842 */     this.withColumns = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setVersion(boolean paramBoolean) {
/* 846 */     this.version = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setCharset(Charset paramCharset) {
/* 850 */     this.charset = paramCharset;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 855 */     return 65;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\ScriptCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */