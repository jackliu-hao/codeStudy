/*    */ package com.mysql.cj.protocol;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.net.Socket;
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
/*    */ public class NetworkResources
/*    */ {
/*    */   private final Socket mysqlConnection;
/*    */   private final InputStream mysqlInput;
/*    */   private final OutputStream mysqlOutput;
/*    */   
/*    */   public NetworkResources(Socket mysqlConnection, InputStream mysqlInput, OutputStream mysqlOutput) {
/* 43 */     this.mysqlConnection = mysqlConnection;
/* 44 */     this.mysqlInput = mysqlInput;
/* 45 */     this.mysqlOutput = mysqlOutput;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final void forceClose() {
/*    */     try {
/* 53 */       if (!ExportControlled.isSSLEstablished(this.mysqlConnection)) {
/*    */         try {
/* 55 */           if (this.mysqlInput != null) {
/* 56 */             this.mysqlInput.close();
/*    */           }
/*    */         } finally {
/* 59 */           if (this.mysqlConnection != null && !this.mysqlConnection.isClosed() && !this.mysqlConnection.isInputShutdown()) {
/*    */             try {
/* 61 */               this.mysqlConnection.shutdownInput();
/* 62 */             } catch (UnsupportedOperationException unsupportedOperationException) {}
/*    */           }
/*    */         }
/*    */       
/*    */       }
/*    */     }
/* 68 */     catch (IOException iOException) {}
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 73 */       if (!ExportControlled.isSSLEstablished(this.mysqlConnection)) {
/*    */         try {
/* 75 */           if (this.mysqlOutput != null) {
/* 76 */             this.mysqlOutput.close();
/*    */           }
/*    */         } finally {
/* 79 */           if (this.mysqlConnection != null && !this.mysqlConnection.isClosed() && !this.mysqlConnection.isOutputShutdown()) {
/*    */             try {
/* 81 */               this.mysqlConnection.shutdownOutput();
/* 82 */             } catch (UnsupportedOperationException unsupportedOperationException) {}
/*    */           }
/*    */         }
/*    */       
/*    */       }
/*    */     }
/* 88 */     catch (IOException iOException) {}
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 93 */       if (this.mysqlConnection != null) {
/* 94 */         this.mysqlConnection.close();
/*    */       }
/* 96 */     } catch (IOException iOException) {}
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\NetworkResources.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */