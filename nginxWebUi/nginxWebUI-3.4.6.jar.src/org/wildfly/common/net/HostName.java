/*     */ package org.wildfly.common.net;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.AccessController;
/*     */ import org.wildfly.common.Assert;
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
/*     */ public final class HostName
/*     */ {
/*  36 */   private static final Object lock = new Object();
/*     */   private static volatile String hostName;
/*     */   private static volatile String qualifiedHostName;
/*     */   private static volatile String nodeName;
/*     */   
/*     */   static {
/*  42 */     String[] names = AccessController.<String[]>doPrivileged(new GetHostInfoAction());
/*  43 */     hostName = names[0];
/*  44 */     qualifiedHostName = names[1];
/*  45 */     nodeName = names[2];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static InetAddress getLocalHost() throws UnknownHostException {
/*     */     InetAddress addr;
/*     */     try {
/*  54 */       addr = InetAddress.getLocalHost();
/*  55 */     } catch (ArrayIndexOutOfBoundsException e) {
/*  56 */       addr = InetAddress.getByName(null);
/*     */     } 
/*  58 */     return addr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getHostName() {
/*  67 */     return hostName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getQualifiedHostName() {
/*  76 */     return qualifiedHostName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getNodeName() {
/*  85 */     return nodeName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setQualifiedHostName(String qualifiedHostName) {
/*  95 */     Assert.checkNotNullParam("qualifiedHostName", qualifiedHostName);
/*  96 */     synchronized (lock) {
/*  97 */       HostName.qualifiedHostName = qualifiedHostName;
/*     */       
/*  99 */       int idx = qualifiedHostName.indexOf('.');
/* 100 */       hostName = (idx == -1) ? qualifiedHostName : qualifiedHostName.substring(0, idx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setNodeName(String nodeName) {
/* 110 */     Assert.checkNotNullParam("nodeName", nodeName);
/* 111 */     HostName.nodeName = nodeName;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\net\HostName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */