/*     */ package com.mysql.cj.xdevapi;
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
/*     */ public interface FindStatement
/*     */   extends Statement<FindStatement, DocResult>
/*     */ {
/*     */   FindStatement fields(String... paramVarArgs);
/*     */   
/*     */   FindStatement fields(Expression paramExpression);
/*     */   
/*     */   FindStatement groupBy(String... paramVarArgs);
/*     */   
/*     */   FindStatement having(String paramString);
/*     */   
/*     */   FindStatement orderBy(String... paramVarArgs);
/*     */   
/*     */   FindStatement sort(String... paramVarArgs);
/*     */   
/*     */   @Deprecated
/*     */   default FindStatement skip(long limitOffset) {
/* 102 */     return offset(limitOffset);
/*     */   }
/*     */   
/*     */   FindStatement offset(long paramLong);
/*     */   
/*     */   FindStatement limit(long paramLong);
/*     */   
/*     */   FindStatement lockShared();
/*     */   
/*     */   FindStatement lockShared(Statement.LockContention paramLockContention);
/*     */   
/*     */   FindStatement lockExclusive();
/*     */   
/*     */   FindStatement lockExclusive(Statement.LockContention paramLockContention);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\FindStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */