/*     */ package cn.hutool.extra.ssh;
/*     */ 
/*     */ import ch.ethz.ssh2.Connection;
/*     */ import ch.ethz.ssh2.Session;
/*     */ import ch.ethz.ssh2.StreamGobbler;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
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
/*     */ public class GanymedUtil
/*     */ {
/*     */   public static Connection connect(String sshHost, int sshPort) {
/*  30 */     Connection conn = new Connection(sshHost, sshPort);
/*     */     try {
/*  32 */       conn.connect();
/*  33 */     } catch (IOException e) {
/*  34 */       throw new IORuntimeException(e);
/*     */     } 
/*  36 */     return conn;
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
/*     */ 
/*     */   
/*     */   public static Session openSession(String sshHost, int sshPort, String sshUser, String sshPass) {
/*  50 */     if (StrUtil.isEmpty(sshUser)) {
/*  51 */       sshUser = "root";
/*     */     }
/*     */     
/*  54 */     Connection connect = connect(sshHost, sshPort);
/*     */     try {
/*  56 */       connect.authenticateWithPassword(sshUser, sshPass);
/*  57 */       return connect.openSession();
/*  58 */     } catch (IOException e) {
/*  59 */       throw new IORuntimeException(e);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String exec(Session session, String cmd, Charset charset, OutputStream errStream) {
/*     */     String result;
/*     */     try {
/*  78 */       session.execCommand(cmd, charset.name());
/*  79 */       result = IoUtil.read((InputStream)new StreamGobbler(session.getStdout()), charset);
/*     */ 
/*     */       
/*  82 */       IoUtil.copy((InputStream)new StreamGobbler(session.getStderr()), errStream);
/*  83 */     } catch (IOException e) {
/*  84 */       throw new IORuntimeException(e);
/*     */     } finally {
/*  86 */       close(session);
/*     */     } 
/*  88 */     return result;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String execByShell(Session session, String cmd, Charset charset, OutputStream errStream) {
/*     */     String result;
/*     */     try {
/* 106 */       session.requestDumbPTY();
/* 107 */       IoUtil.write(session.getStdin(), charset, true, new Object[] { cmd });
/*     */       
/* 109 */       result = IoUtil.read((InputStream)new StreamGobbler(session.getStdout()), charset);
/* 110 */       if (null != errStream)
/*     */       {
/* 112 */         IoUtil.copy((InputStream)new StreamGobbler(session.getStderr()), errStream);
/*     */       }
/* 114 */     } catch (IOException e) {
/* 115 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 117 */       close(session);
/*     */     } 
/* 119 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void close(Session session) {
/* 128 */     if (session != null)
/* 129 */       session.close(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\ssh\GanymedUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */