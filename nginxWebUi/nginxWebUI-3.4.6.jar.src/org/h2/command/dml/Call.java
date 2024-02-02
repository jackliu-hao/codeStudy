/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import org.h2.command.Prepared;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Alias;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.function.table.TableFunction;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.table.Column;
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
/*     */ 
/*     */ 
/*     */ public class Call
/*     */   extends Prepared
/*     */ {
/*     */   private Expression expression;
/*     */   private TableFunction tableFunction;
/*     */   private Expression[] expressions;
/*     */   
/*     */   public Call(SessionLocal paramSessionLocal) {
/*  34 */     super(paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface queryMeta() {
/*  39 */     int i = this.expressions.length;
/*  40 */     LocalResult localResult = new LocalResult(this.session, this.expressions, i, i);
/*  41 */     localResult.done();
/*  42 */     return (ResultInterface)localResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public long update() {
/*  47 */     if (this.tableFunction != null)
/*     */     {
/*     */       
/*  50 */       return super.update();
/*     */     }
/*  52 */     Value value = this.expression.getValue(this.session);
/*  53 */     int i = value.getValueType();
/*  54 */     switch (i) {
/*     */       case -1:
/*     */       case 0:
/*  57 */         return 0L;
/*     */     } 
/*  59 */     return value.getInt();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface query(long paramLong) {
/*  65 */     setCurrentRowNumber(1L);
/*  66 */     if (this.tableFunction != null) {
/*  67 */       return this.tableFunction.getValue(this.session);
/*     */     }
/*  69 */     LocalResult localResult = new LocalResult(this.session, this.expressions, 1, 1);
/*  70 */     localResult.addRow(new Value[] { this.expression.getValue(this.session) });
/*  71 */     localResult.done();
/*  72 */     return (ResultInterface)localResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public void prepare() {
/*  77 */     if (this.tableFunction != null) {
/*  78 */       this.prepareAlways = true;
/*  79 */       this.tableFunction.optimize(this.session);
/*  80 */       ResultInterface resultInterface = this.tableFunction.getValueTemplate(this.session);
/*  81 */       int i = resultInterface.getVisibleColumnCount();
/*  82 */       this.expressions = new Expression[i];
/*  83 */       for (byte b = 0; b < i; b++) {
/*  84 */         Alias alias; String str1 = resultInterface.getColumnName(b);
/*  85 */         String str2 = resultInterface.getAlias(b);
/*  86 */         ExpressionColumn expressionColumn = new ExpressionColumn(this.session.getDatabase(), new Column(str1, resultInterface.getColumnType(b)));
/*  87 */         if (!str2.equals(str1)) {
/*  88 */           alias = new Alias((Expression)expressionColumn, str2, false);
/*     */         }
/*  90 */         this.expressions[b] = (Expression)alias;
/*     */       } 
/*     */     } else {
/*  93 */       this.expressions = new Expression[] { this.expression = this.expression.optimize(this.session) };
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setExpression(Expression paramExpression) {
/*  98 */     this.expression = paramExpression;
/*     */   }
/*     */   
/*     */   public void setTableFunction(TableFunction paramTableFunction) {
/* 102 */     this.tableFunction = paramTableFunction;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isQuery() {
/* 107 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTransactional() {
/* 112 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/* 117 */     return (this.tableFunction == null && this.expression.isEverything(ExpressionVisitor.READONLY_VISITOR));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getType() {
/* 123 */     return 57;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCacheable() {
/* 128 */     return (this.tableFunction == null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\Call.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */