/*    */ package com.mysql.cj.protocol.x;
/*    */ 
/*    */ import java.io.OutputStream;
/*    */ import java.util.zip.Deflater;
/*    */ import java.util.zip.DeflaterOutputStream;
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
/*    */ 
/*    */ public class SyncFlushDeflaterOutputStream
/*    */   extends DeflaterOutputStream
/*    */ {
/*    */   public SyncFlushDeflaterOutputStream(OutputStream out) {
/* 41 */     super(out, new Deflater(), true);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\SyncFlushDeflaterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */