/*     */ package org.h2.command;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.h2.command.dml.DataChangeStatement;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.DbSettings;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.expression.Parameter;
/*     */ import org.h2.expression.ParameterInterface;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.ResultTarget;
/*     */ import org.h2.result.ResultWithGeneratedKeys;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.DataChangeDeltaTable;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableView;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.Value;
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
/*     */ public class CommandContainer
/*     */   extends Command
/*     */ {
/*     */   private Prepared prepared;
/*     */   private boolean readOnlyKnown;
/*     */   private boolean readOnly;
/*     */   
/*     */   private static final class GeneratedKeysCollector
/*     */     implements ResultTarget
/*     */   {
/*     */     private final int[] indexes;
/*     */     private final LocalResult result;
/*     */     
/*     */     GeneratedKeysCollector(int[] param1ArrayOfint, LocalResult param1LocalResult) {
/*  53 */       this.indexes = param1ArrayOfint;
/*  54 */       this.result = param1LocalResult;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void limitsWereApplied() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public long getRowCount() {
/*  65 */       return 0L;
/*     */     }
/*     */ 
/*     */     
/*     */     public void addRow(Value... param1VarArgs) {
/*  70 */       int i = this.indexes.length;
/*  71 */       Value[] arrayOfValue = new Value[i];
/*  72 */       for (byte b = 0; b < i; b++) {
/*  73 */         arrayOfValue[b] = param1VarArgs[this.indexes[b]];
/*     */       }
/*  75 */       this.result.addRow(arrayOfValue);
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
/*     */   static void clearCTE(SessionLocal paramSessionLocal, Prepared paramPrepared) {
/*  91 */     List<TableView> list = paramPrepared.getCteCleanups();
/*  92 */     if (list != null) {
/*  93 */       clearCTE(paramSessionLocal, list);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void clearCTE(SessionLocal paramSessionLocal, List<TableView> paramList) {
/* 104 */     for (TableView tableView : paramList) {
/*     */ 
/*     */       
/* 107 */       if (tableView.getName() != null) {
/* 108 */         paramSessionLocal.removeLocalTempTable((Table)tableView);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public CommandContainer(SessionLocal paramSessionLocal, String paramString, Prepared paramPrepared) {
/* 114 */     super(paramSessionLocal, paramString);
/* 115 */     paramPrepared.setCommand(this);
/* 116 */     this.prepared = paramPrepared;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<? extends ParameterInterface> getParameters() {
/* 121 */     return (ArrayList)this.prepared.getParameters();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTransactional() {
/* 126 */     return this.prepared.isTransactional();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isQuery() {
/* 131 */     return this.prepared.isQuery();
/*     */   }
/*     */   
/*     */   private void recompileIfRequired() {
/* 135 */     if (this.prepared.needRecompile()) {
/*     */       
/* 137 */       this.prepared.setModificationMetaId(0L);
/* 138 */       String str = this.prepared.getSQL();
/* 139 */       ArrayList<Token> arrayList = this.prepared.getSQLTokens();
/* 140 */       ArrayList<Parameter> arrayList1 = this.prepared.getParameters();
/* 141 */       Parser parser = new Parser(this.session);
/* 142 */       this.prepared = parser.parse(str, arrayList);
/* 143 */       long l = this.prepared.getModificationMetaId();
/* 144 */       this.prepared.setModificationMetaId(0L);
/* 145 */       ArrayList<Parameter> arrayList2 = this.prepared.getParameters(); byte b; int i;
/* 146 */       for (b = 0, i = Math.min(arrayList2.size(), arrayList1.size()); b < i; b++) {
/* 147 */         Parameter parameter = arrayList1.get(b);
/* 148 */         if (parameter.isValueSet()) {
/* 149 */           Value value = parameter.getValue(this.session);
/* 150 */           Parameter parameter1 = arrayList2.get(b);
/* 151 */           parameter1.setValue(value);
/*     */         } 
/*     */       } 
/* 154 */       this.prepared.prepare();
/* 155 */       this.prepared.setModificationMetaId(l);
/*     */     } 
/*     */   }
/*     */   
/*     */   public ResultWithGeneratedKeys update(Object paramObject) {
/*     */     ResultWithGeneratedKeys resultWithGeneratedKeys;
/* 161 */     recompileIfRequired();
/* 162 */     setProgress(5);
/* 163 */     start();
/* 164 */     this.prepared.checkParameters();
/*     */     
/* 166 */     if (paramObject != null && !Boolean.FALSE.equals(paramObject)) {
/* 167 */       if (this.prepared instanceof DataChangeStatement && this.prepared.getType() != 58) {
/* 168 */         resultWithGeneratedKeys = executeUpdateWithGeneratedKeys((DataChangeStatement)this.prepared, paramObject);
/*     */       } else {
/*     */         
/* 171 */         ResultWithGeneratedKeys.WithKeys withKeys = new ResultWithGeneratedKeys.WithKeys(this.prepared.update(), (ResultInterface)new LocalResult());
/*     */       } 
/*     */     } else {
/* 174 */       resultWithGeneratedKeys = ResultWithGeneratedKeys.of(this.prepared.update());
/*     */     } 
/* 176 */     this.prepared.trace(this.startTimeNanos, resultWithGeneratedKeys.getUpdateCount());
/* 177 */     setProgress(6);
/* 178 */     return resultWithGeneratedKeys;
/*     */   }
/*     */   
/*     */   private ResultWithGeneratedKeys executeUpdateWithGeneratedKeys(DataChangeStatement paramDataChangeStatement, Object paramObject) {
/*     */     ArrayList<ExpressionColumn> arrayList;
/* 183 */     Database database = this.session.getDatabase();
/* 184 */     Table table = paramDataChangeStatement.getTable();
/*     */     
/* 186 */     if (Boolean.TRUE.equals(paramObject)) {
/* 187 */       arrayList = Utils.newSmallArrayList();
/* 188 */       Column[] arrayOfColumn = table.getColumns();
/* 189 */       Index index = table.findPrimaryKey();
/* 190 */       for (Column column : arrayOfColumn) {
/*     */         Expression expression;
/* 192 */         if (column.isIdentity() || ((
/* 193 */           expression = column.getEffectiveDefaultExpression()) != null && !expression.isConstant()) || (index != null && index
/* 194 */           .getColumnIndex(column) >= 0)) {
/* 195 */           arrayList.add(new ExpressionColumn(database, column));
/*     */         }
/*     */       } 
/* 198 */     } else if (paramObject instanceof int[]) {
/* 199 */       int[] arrayOfInt1 = (int[])paramObject;
/* 200 */       Column[] arrayOfColumn = table.getColumns();
/* 201 */       int j = arrayOfColumn.length;
/* 202 */       arrayList = new ArrayList(arrayOfInt1.length);
/* 203 */       for (int k : arrayOfInt1) {
/* 204 */         if (k < 1 || k > j) {
/* 205 */           throw DbException.get(42122, "Index: " + k);
/*     */         }
/* 207 */         arrayList.add(new ExpressionColumn(database, arrayOfColumn[k - 1]));
/*     */       } 
/* 209 */     } else if (paramObject instanceof String[]) {
/* 210 */       String[] arrayOfString = (String[])paramObject;
/* 211 */       arrayList = new ArrayList(arrayOfString.length);
/* 212 */       for (String str : arrayOfString) {
/* 213 */         Column column = table.findColumn(str);
/* 214 */         if (column == null) {
/* 215 */           DbSettings dbSettings = database.getSettings();
/* 216 */           if (dbSettings.databaseToUpper) {
/* 217 */             column = table.findColumn(StringUtils.toUpperEnglish(str));
/* 218 */           } else if (dbSettings.databaseToLower) {
/* 219 */             column = table.findColumn(StringUtils.toLowerEnglish(str));
/*     */           } 
/* 221 */           if (column == null) {
/* 222 */             Column[] arrayOfColumn = table.getColumns(); int j = arrayOfColumn.length; byte b1 = 0; while (true) { if (b1 < j) { Column column1 = arrayOfColumn[b1];
/* 223 */                 if (column1.getName().equalsIgnoreCase(str)) {
/* 224 */                   column = column1; break;
/*     */                 }  b1++;
/*     */                 continue; }
/*     */               
/* 228 */               throw DbException.get(42122, str); }
/*     */           
/*     */           } 
/* 231 */         }  arrayList.add(new ExpressionColumn(database, column));
/*     */       } 
/*     */     } else {
/* 234 */       throw DbException.getInternalError();
/*     */     } 
/* 236 */     int i = arrayList.size();
/* 237 */     if (i == 0) {
/* 238 */       return (ResultWithGeneratedKeys)new ResultWithGeneratedKeys.WithKeys(paramDataChangeStatement.update(), (ResultInterface)new LocalResult());
/*     */     }
/* 240 */     int[] arrayOfInt = new int[i];
/* 241 */     ExpressionColumn[] arrayOfExpressionColumn = arrayList.<ExpressionColumn>toArray(new ExpressionColumn[0]);
/* 242 */     for (byte b = 0; b < i; b++) {
/* 243 */       arrayOfInt[b] = arrayOfExpressionColumn[b].getColumn().getColumnId();
/*     */     }
/* 245 */     LocalResult localResult = new LocalResult(this.session, (Expression[])arrayOfExpressionColumn, i, i);
/* 246 */     return (ResultWithGeneratedKeys)new ResultWithGeneratedKeys.WithKeys(paramDataChangeStatement
/* 247 */         .update(new GeneratedKeysCollector(arrayOfInt, localResult), DataChangeDeltaTable.ResultOption.FINAL), (ResultInterface)localResult);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface query(long paramLong) {
/* 252 */     recompileIfRequired();
/* 253 */     setProgress(5);
/* 254 */     start();
/* 255 */     this.prepared.checkParameters();
/* 256 */     ResultInterface resultInterface = this.prepared.query(paramLong);
/* 257 */     this.prepared.trace(this.startTimeNanos, resultInterface.isLazy() ? 0L : resultInterface.getRowCount());
/* 258 */     setProgress(6);
/* 259 */     return resultInterface;
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/* 264 */     super.stop();
/*     */ 
/*     */     
/* 267 */     clearCTE(this.session, this.prepared);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canReuse() {
/* 272 */     return (super.canReuse() && this.prepared.getCteCleanups() == null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/* 277 */     if (!this.readOnlyKnown) {
/* 278 */       this.readOnly = this.prepared.isReadOnly();
/* 279 */       this.readOnlyKnown = true;
/*     */     } 
/* 281 */     return this.readOnly;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface queryMeta() {
/* 286 */     return this.prepared.queryMeta();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCacheable() {
/* 291 */     return this.prepared.isCacheable();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCommandType() {
/* 296 */     return this.prepared.getType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void clearCTE() {
/* 303 */     clearCTE(this.session, this.prepared);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<DbObject> getDependencies() {
/* 308 */     HashSet<DbObject> hashSet = new HashSet();
/* 309 */     this.prepared.collectDependencies(hashSet);
/* 310 */     return hashSet;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isCurrentCommandADefineCommand() {
/* 315 */     return this.prepared instanceof org.h2.command.ddl.DefineCommand;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\CommandContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */