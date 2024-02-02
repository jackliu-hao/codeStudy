/*     */ package org.h2.command;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.Parameter;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.Trace;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.table.TableView;
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
/*     */ 
/*     */ 
/*     */ public abstract class Prepared
/*     */ {
/*     */   protected SessionLocal session;
/*     */   protected String sqlStatement;
/*     */   protected ArrayList<Token> sqlTokens;
/*     */   protected boolean create = true;
/*     */   protected ArrayList<Parameter> parameters;
/*     */   protected boolean prepareAlways;
/*     */   private long modificationMetaId;
/*     */   private Command command;
/*     */   private int persistedObjectId;
/*     */   private long currentRowNumber;
/*     */   private int rowScanCount;
/*     */   private List<TableView> cteCleanups;
/*     */   
/*     */   public Prepared(SessionLocal paramSessionLocal) {
/*  83 */     this.session = paramSessionLocal;
/*  84 */     this.modificationMetaId = paramSessionLocal.getDatabase().getModificationMetaId();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean isTransactional();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ResultInterface queryMeta();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int getType();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/* 116 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean needRecompile() {
/* 125 */     Database database = this.session.getDatabase();
/* 126 */     if (database == null) {
/* 127 */       throw DbException.get(90067, "database closed");
/*     */     }
/*     */ 
/*     */     
/* 131 */     return (this.prepareAlways || this.modificationMetaId < database
/* 132 */       .getModificationMetaId() || 
/* 133 */       (database.getSettings()).recompileAlways);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long getModificationMetaId() {
/* 143 */     return this.modificationMetaId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setModificationMetaId(long paramLong) {
/* 152 */     this.modificationMetaId = paramLong;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setParameterList(ArrayList<Parameter> paramArrayList) {
/* 161 */     this.parameters = paramArrayList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<Parameter> getParameters() {
/* 170 */     return this.parameters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void checkParameters() {
/* 179 */     if (this.persistedObjectId < 0)
/*     */     {
/*     */       
/* 182 */       this.persistedObjectId ^= 0xFFFFFFFF;
/*     */     }
/* 184 */     if (this.parameters != null) {
/* 185 */       for (Parameter parameter : this.parameters) {
/* 186 */         parameter.checkSet();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCommand(Command paramCommand) {
/* 197 */     this.command = paramCommand;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isQuery() {
/* 206 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void prepare() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long update() {
/* 223 */     throw DbException.get(90001);
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
/*     */   public ResultInterface query(long paramLong) {
/* 235 */     throw DbException.get(90002);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void setSQL(String paramString, ArrayList<Token> paramArrayList) {
/* 245 */     this.sqlStatement = paramString;
/* 246 */     this.sqlTokens = paramArrayList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getSQL() {
/* 255 */     return this.sqlStatement;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ArrayList<Token> getSQLTokens() {
/* 264 */     return this.sqlTokens;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPersistedObjectId() {
/* 275 */     int i = this.persistedObjectId;
/* 276 */     return (i >= 0) ? i : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getObjectId() {
/* 287 */     int i = this.persistedObjectId;
/* 288 */     if (i == 0) {
/* 289 */       i = this.session.getDatabase().allocateObjectId();
/* 290 */     } else if (i < 0) {
/* 291 */       throw DbException.getInternalError("Prepared.getObjectId() was called before");
/*     */     } 
/* 293 */     this.persistedObjectId ^= 0xFFFFFFFF;
/* 294 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPlanSQL(int paramInt) {
/* 304 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkCanceled() {
/* 313 */     this.session.checkCanceled();
/* 314 */     Command command = (this.command != null) ? this.command : this.session.getCurrentCommand();
/* 315 */     if (command != null) {
/* 316 */       command.checkCanceled();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPersistedObjectId(int paramInt) {
/* 326 */     this.persistedObjectId = paramInt;
/* 327 */     this.create = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSession(SessionLocal paramSessionLocal) {
/* 336 */     this.session = paramSessionLocal;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void trace(long paramLong1, long paramLong2) {
/* 347 */     if (this.session.getTrace().isInfoEnabled() && paramLong1 > 0L) {
/* 348 */       long l = System.nanoTime() - paramLong1;
/* 349 */       String str = Trace.formatParams(this.parameters);
/* 350 */       this.session.getTrace().infoSQL(this.sqlStatement, str, paramLong2, l / 1000000L);
/*     */     } 
/*     */ 
/*     */     
/* 354 */     if (this.session.getDatabase().getQueryStatistics() && paramLong1 != 0L) {
/* 355 */       long l = System.nanoTime() - paramLong1;
/* 356 */       this.session.getDatabase().getQueryStatisticsData().update(toString(), l, paramLong2);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPrepareAlways(boolean paramBoolean) {
/* 367 */     this.prepareAlways = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCurrentRowNumber(long paramLong) {
/* 376 */     if ((++this.rowScanCount & 0x7F) == 0) {
/* 377 */       checkCanceled();
/*     */     }
/* 379 */     this.currentRowNumber = paramLong;
/* 380 */     setProgress();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCurrentRowNumber() {
/* 389 */     return this.currentRowNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setProgress() {
/* 396 */     if ((this.currentRowNumber & 0x7FL) == 0L) {
/* 397 */       this.session.getDatabase().setProgress(7, this.sqlStatement, this.currentRowNumber, 0L);
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
/*     */   public String toString() {
/* 409 */     return this.sqlStatement;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getSimpleSQL(Expression[] paramArrayOfExpression) {
/* 419 */     return Expression.writeExpressions(new StringBuilder(), paramArrayOfExpression, 3).toString();
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
/*     */   protected DbException setRow(DbException paramDbException, long paramLong, String paramString) {
/* 431 */     StringBuilder stringBuilder = new StringBuilder();
/* 432 */     if (this.sqlStatement != null) {
/* 433 */       stringBuilder.append(this.sqlStatement);
/*     */     }
/* 435 */     stringBuilder.append(" -- ");
/* 436 */     if (paramLong > 0L) {
/* 437 */       stringBuilder.append("row #").append(paramLong + 1L).append(' ');
/*     */     }
/* 439 */     stringBuilder.append('(').append(paramString).append(')');
/* 440 */     return paramDbException.addSQL(stringBuilder.toString());
/*     */   }
/*     */   
/*     */   public boolean isCacheable() {
/* 444 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<TableView> getCteCleanups() {
/* 451 */     return this.cteCleanups;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCteCleanups(List<TableView> paramList) {
/* 460 */     this.cteCleanups = paramList;
/*     */   }
/*     */   
/*     */   public final SessionLocal getSession() {
/* 464 */     return this.session;
/*     */   }
/*     */   
/*     */   public void collectDependencies(HashSet<DbObject> paramHashSet) {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\Prepared.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */