/*    */ package org.h2.jdbcx;
/*    */ 
/*    */ import java.util.Base64;
/*    */ import javax.transaction.xa.Xid;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.message.TraceObject;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class JdbcXid
/*    */   extends TraceObject
/*    */   implements Xid
/*    */ {
/*    */   private static final String PREFIX = "XID";
/* 22 */   private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
/*    */   
/*    */   private final int formatId;
/*    */   private final byte[] branchQualifier;
/*    */   private final byte[] globalTransactionId;
/*    */   
/*    */   JdbcXid(JdbcDataSourceFactory paramJdbcDataSourceFactory, int paramInt, String paramString) {
/* 29 */     setTrace(paramJdbcDataSourceFactory.getTrace(), 15, paramInt);
/*    */     try {
/* 31 */       String[] arrayOfString = paramString.split("\\|");
/* 32 */       if (arrayOfString.length == 4 && "XID".equals(arrayOfString[0])) {
/* 33 */         this.formatId = Integer.parseInt(arrayOfString[1]);
/* 34 */         Base64.Decoder decoder = Base64.getUrlDecoder();
/* 35 */         this.branchQualifier = decoder.decode(arrayOfString[2]);
/* 36 */         this.globalTransactionId = decoder.decode(arrayOfString[3]);
/*    */         return;
/*    */       } 
/* 39 */     } catch (IllegalArgumentException illegalArgumentException) {}
/*    */     
/* 41 */     throw DbException.get(90101, paramString);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static StringBuilder toString(StringBuilder paramStringBuilder, Xid paramXid) {
/* 51 */     return paramStringBuilder.append("XID").append('|').append(paramXid.getFormatId())
/* 52 */       .append('|').append(ENCODER.encodeToString(paramXid.getBranchQualifier()))
/* 53 */       .append('|').append(ENCODER.encodeToString(paramXid.getGlobalTransactionId()));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getFormatId() {
/* 63 */     debugCodeCall("getFormatId");
/* 64 */     return this.formatId;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public byte[] getBranchQualifier() {
/* 74 */     debugCodeCall("getBranchQualifier");
/* 75 */     return this.branchQualifier;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public byte[] getGlobalTransactionId() {
/* 85 */     debugCodeCall("getGlobalTransactionId");
/* 86 */     return this.globalTransactionId;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbcx\JdbcXid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */