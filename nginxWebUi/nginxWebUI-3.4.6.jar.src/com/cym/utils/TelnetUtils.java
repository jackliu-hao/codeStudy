/*    */ package com.cym.utils;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.Socket;
/*    */ import java.net.SocketAddress;
/*    */ import java.net.SocketTimeoutException;
/*    */ import java.net.UnknownHostException;
/*    */ 
/*    */ public class TelnetUtils {
/*    */   public static boolean isRunning(String host, int port) {
/* 12 */     Socket sClient = null;
/*    */     try {
/* 14 */       SocketAddress saAdd = new InetSocketAddress(host.trim(), port);
/* 15 */       sClient = new Socket();
/* 16 */       sClient.connect(saAdd, 1000);
/* 17 */     } catch (UnknownHostException e) {
/* 18 */       return false;
/* 19 */     } catch (SocketTimeoutException e) {
/* 20 */       return false;
/* 21 */     } catch (IOException e) {
/* 22 */       return false;
/* 23 */     } catch (Exception e) {
/* 24 */       return false;
/*    */     } finally {
/*    */       try {
/* 27 */         if (sClient != null) {
/* 28 */           sClient.close();
/*    */         }
/* 30 */       } catch (Exception exception) {}
/*    */     } 
/*    */     
/* 33 */     return true;
/*    */   }
/*    */   
/*    */   public static void main(String[] args) {
/* 37 */     System.out.println(isRunning("127.0.0.1", 8080));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\TelnetUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */