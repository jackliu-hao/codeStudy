/*     */ package com.mysql.cj;
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
/*     */ public class ClientPreparedQuery
/*     */   extends AbstractPreparedQuery<ClientPreparedQueryBindings>
/*     */ {
/*     */   public ClientPreparedQuery(NativeSession sess) {
/*  37 */     super(sess);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs) {
/*  46 */     long sizeOfEntireBatch = 1L;
/*  47 */     long maxSizeOfParameterSet = 0L;
/*     */     
/*  49 */     if (this.session.getServerSession().supportsQueryAttributes()) {
/*  50 */       sizeOfEntireBatch += 10L;
/*  51 */       sizeOfEntireBatch += ((this.queryAttributesBindings.getCount() + 7) / 8 + 1);
/*  52 */       for (int j = 0; j < this.queryAttributesBindings.getCount(); j++) {
/*  53 */         QueryAttributesBindValue queryAttribute = this.queryAttributesBindings.getAttributeValue(j);
/*  54 */         sizeOfEntireBatch += (2 + queryAttribute.getName().length()) + queryAttribute.getBoundLength();
/*     */       } 
/*     */     } 
/*     */     
/*  58 */     for (int i = 0; i < numBatchedArgs; i++) {
/*  59 */       ClientPreparedQueryBindings qBindings = this.batchedArgs.get(i);
/*     */       
/*  61 */       ClientPreparedQueryBindValue[] arrayOfClientPreparedQueryBindValue = qBindings.getBindValues();
/*     */       
/*  63 */       long sizeOfParameterSet = 0L;
/*     */       
/*  65 */       for (int j = 0; j < arrayOfClientPreparedQueryBindValue.length; j++) {
/*  66 */         if (!arrayOfClientPreparedQueryBindValue[j].isNull()) {
/*     */           
/*  68 */           if (arrayOfClientPreparedQueryBindValue[j].isStream()) {
/*  69 */             long streamLength = arrayOfClientPreparedQueryBindValue[j].getStreamLength();
/*     */             
/*  71 */             if (streamLength != -1L) {
/*  72 */               sizeOfParameterSet += streamLength * 2L;
/*     */             } else {
/*  74 */               int paramLength = (qBindings.getBindValues()[j].getByteValue()).length;
/*  75 */               sizeOfParameterSet += paramLength;
/*     */             } 
/*     */           } else {
/*  78 */             sizeOfParameterSet += (qBindings.getBindValues()[j].getByteValue()).length;
/*     */           } 
/*     */         } else {
/*  81 */           sizeOfParameterSet += 4L;
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  91 */       if (this.parseInfo.getValuesClause() != null) {
/*  92 */         sizeOfParameterSet += (this.parseInfo.getValuesClause().length() + 1);
/*     */       } else {
/*  94 */         sizeOfParameterSet += (this.originalSql.length() + 1);
/*     */       } 
/*     */       
/*  97 */       sizeOfEntireBatch += sizeOfParameterSet;
/*     */       
/*  99 */       if (sizeOfParameterSet > maxSizeOfParameterSet) {
/* 100 */         maxSizeOfParameterSet = sizeOfParameterSet;
/*     */       }
/*     */     } 
/*     */     
/* 104 */     return new long[] { maxSizeOfParameterSet, sizeOfEntireBatch };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\ClientPreparedQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */