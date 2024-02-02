/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import org.h2.command.Prepared;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.mvstore.db.Store;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.table.Column;
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
/*     */ 
/*     */ 
/*     */ public class Explain
/*     */   extends Prepared
/*     */ {
/*     */   private Prepared command;
/*     */   private LocalResult result;
/*     */   private boolean executeCommand;
/*     */   
/*     */   public Explain(SessionLocal paramSessionLocal) {
/*  38 */     super(paramSessionLocal);
/*     */   }
/*     */   
/*     */   public void setCommand(Prepared paramPrepared) {
/*  42 */     this.command = paramPrepared;
/*     */   }
/*     */   
/*     */   public Prepared getCommand() {
/*  46 */     return this.command;
/*     */   }
/*     */ 
/*     */   
/*     */   public void prepare() {
/*  51 */     this.command.prepare();
/*     */   }
/*     */   
/*     */   public void setExecuteCommand(boolean paramBoolean) {
/*  55 */     this.executeCommand = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface queryMeta() {
/*  60 */     return query(-1L);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void checkParameters() {
/*  66 */     if (this.executeCommand) {
/*  67 */       super.checkParameters();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface query(long paramLong) {
/*  73 */     Database database = this.session.getDatabase();
/*  74 */     Expression[] arrayOfExpression = { (Expression)new ExpressionColumn(database, new Column("PLAN", TypeInfo.TYPE_VARCHAR)) };
/*  75 */     this.result = new LocalResult(this.session, arrayOfExpression, 1, 1);
/*  76 */     byte b = 8;
/*  77 */     if (paramLong >= 0L) {
/*     */       String str;
/*  79 */       if (this.executeCommand) {
/*  80 */         Store store = null;
/*  81 */         if (database.isPersistent()) {
/*  82 */           store = database.getStore();
/*  83 */           store.statisticsStart();
/*     */         } 
/*  85 */         if (this.command.isQuery()) {
/*  86 */           this.command.query(paramLong);
/*     */         } else {
/*  88 */           this.command.update();
/*     */         } 
/*  90 */         str = this.command.getPlanSQL(b);
/*  91 */         Map<?, ?> map = null;
/*  92 */         if (store != null) {
/*  93 */           map = store.statisticsEnd();
/*     */         }
/*  95 */         if (map != null) {
/*  96 */           int i = 0;
/*  97 */           for (Map.Entry entry : map.entrySet()) {
/*  98 */             i += ((Integer)entry.getValue()).intValue();
/*     */           }
/* 100 */           if (i > 0) {
/* 101 */             map = new TreeMap<>(map);
/* 102 */             StringBuilder stringBuilder = new StringBuilder();
/* 103 */             if (map.size() > 1) {
/* 104 */               stringBuilder.append("total: ").append(i).append('\n');
/*     */             }
/* 106 */             for (Map.Entry<?, ?> entry : map.entrySet()) {
/* 107 */               int j = ((Integer)entry.getValue()).intValue();
/* 108 */               int k = (int)(100L * j / i);
/* 109 */               stringBuilder.append((String)entry.getKey()).append(": ").append(j);
/* 110 */               if (map.size() > 1) {
/* 111 */                 stringBuilder.append(" (").append(k).append("%)");
/*     */               }
/* 113 */               stringBuilder.append('\n');
/*     */             } 
/* 115 */             str = str + "\n/*\n" + stringBuilder.toString() + "*/";
/*     */           } 
/*     */         } 
/*     */       } else {
/* 119 */         str = this.command.getPlanSQL(b);
/*     */       } 
/* 121 */       add(str);
/*     */     } 
/* 123 */     this.result.done();
/* 124 */     return (ResultInterface)this.result;
/*     */   }
/*     */   
/*     */   private void add(String paramString) {
/* 128 */     this.result.addRow(new Value[] { ValueVarchar.get(paramString) });
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isQuery() {
/* 133 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTransactional() {
/* 138 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/* 143 */     return this.command.isReadOnly();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 148 */     return this.executeCommand ? 86 : 60;
/*     */   }
/*     */ 
/*     */   
/*     */   public void collectDependencies(HashSet<DbObject> paramHashSet) {
/* 153 */     this.command.collectDependencies(paramHashSet);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\Explain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */