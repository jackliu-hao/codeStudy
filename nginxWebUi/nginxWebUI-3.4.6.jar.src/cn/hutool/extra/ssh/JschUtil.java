/*     */ package cn.hutool.extra.ssh;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.net.LocalPortGenerater;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import com.jcraft.jsch.Channel;
/*     */ import com.jcraft.jsch.ChannelExec;
/*     */ import com.jcraft.jsch.ChannelSftp;
/*     */ import com.jcraft.jsch.ChannelShell;
/*     */ import com.jcraft.jsch.JSch;
/*     */ import com.jcraft.jsch.JSchException;
/*     */ import com.jcraft.jsch.Session;
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
/*     */ public class JschUtil
/*     */ {
/*     */   public static final String SSH_NONE = "none";
/*  34 */   private static final LocalPortGenerater portGenerater = new LocalPortGenerater(10000);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int generateLocalPort() {
/*  42 */     return portGenerater.generate();
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
/*     */   public static Session getSession(String sshHost, int sshPort, String sshUser, String sshPass) {
/*  55 */     return JschSessionPool.INSTANCE.getSession(sshHost, sshPort, sshUser, sshPass);
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
/*     */   public static Session getSession(String sshHost, int sshPort, String sshUser, String privateKeyPath, byte[] passphrase) {
/*  69 */     return JschSessionPool.INSTANCE.getSession(sshHost, sshPort, sshUser, privateKeyPath, passphrase);
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
/*     */   public static Session openSession(String sshHost, int sshPort, String sshUser, String sshPass) {
/*  82 */     return openSession(sshHost, sshPort, sshUser, sshPass, 0);
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
/*     */   public static Session openSession(String sshHost, int sshPort, String sshUser, String sshPass, int timeout) {
/*  97 */     Session session = createSession(sshHost, sshPort, sshUser, sshPass);
/*     */     try {
/*  99 */       session.connect(timeout);
/* 100 */     } catch (JSchException e) {
/* 101 */       throw new JschRuntimeException(e);
/*     */     } 
/* 103 */     return session;
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
/*     */   public static Session openSession(String sshHost, int sshPort, String sshUser, String privateKeyPath, byte[] passphrase) {
/* 117 */     Session session = createSession(sshHost, sshPort, sshUser, privateKeyPath, passphrase);
/*     */     try {
/* 119 */       session.connect();
/* 120 */     } catch (JSchException e) {
/* 121 */       throw new JschRuntimeException(e);
/*     */     } 
/* 123 */     return session;
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
/*     */   public static Session createSession(String sshHost, int sshPort, String sshUser, String sshPass) {
/* 137 */     JSch jsch = new JSch();
/* 138 */     Session session = createSession(jsch, sshHost, sshPort, sshUser);
/*     */     
/* 140 */     if (StrUtil.isNotEmpty(sshPass)) {
/* 141 */       session.setPassword(sshPass);
/*     */     }
/*     */     
/* 144 */     return session;
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
/*     */   public static Session createSession(String sshHost, int sshPort, String sshUser, String privateKeyPath, byte[] passphrase) {
/* 159 */     Assert.notEmpty(privateKeyPath, "PrivateKey Path must be not empty!", new Object[0]);
/*     */     
/* 161 */     JSch jsch = new JSch();
/*     */     try {
/* 163 */       jsch.addIdentity(privateKeyPath, passphrase);
/* 164 */     } catch (JSchException e) {
/* 165 */       throw new JschRuntimeException(e);
/*     */     } 
/*     */     
/* 168 */     return createSession(jsch, sshHost, sshPort, sshUser);
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
/*     */   public static Session createSession(JSch jsch, String sshHost, int sshPort, String sshUser) {
/*     */     Session session;
/* 182 */     Assert.notEmpty(sshHost, "SSH Host must be not empty!", new Object[0]);
/* 183 */     Assert.isTrue((sshPort > 0), "SSH port must be > 0", new Object[0]);
/*     */ 
/*     */     
/* 186 */     if (StrUtil.isEmpty(sshUser)) {
/* 187 */       sshUser = "root";
/*     */     }
/*     */     
/* 190 */     if (null == jsch) {
/* 191 */       jsch = new JSch();
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 196 */       session = jsch.getSession(sshUser, sshHost, sshPort);
/* 197 */     } catch (JSchException e) {
/* 198 */       throw new JschRuntimeException(e);
/*     */     } 
/*     */ 
/*     */     
/* 202 */     session.setConfig("StrictHostKeyChecking", "no");
/*     */     
/* 204 */     return session;
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
/*     */   public static boolean bindPort(Session session, String remoteHost, int remotePort, int localPort) throws JschRuntimeException {
/* 218 */     return bindPort(session, remoteHost, remotePort, "127.0.0.1", localPort);
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
/*     */   public static boolean bindPort(Session session, String remoteHost, int remotePort, String localHost, int localPort) throws JschRuntimeException {
/* 234 */     if (session != null && session.isConnected()) {
/*     */       try {
/* 236 */         session.setPortForwardingL(localHost, localPort, remoteHost, remotePort);
/* 237 */       } catch (JSchException e) {
/* 238 */         throw new JschRuntimeException(e, "From [{}:{}] mapping to [{}:{}] error！", new Object[] { remoteHost, Integer.valueOf(remotePort), localHost, Integer.valueOf(localPort) });
/*     */       } 
/* 240 */       return true;
/*     */     } 
/* 242 */     return false;
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
/*     */   
/*     */   public static boolean bindRemotePort(Session session, int bindPort, String host, int port) throws JschRuntimeException {
/* 259 */     if (session != null && session.isConnected()) {
/*     */       try {
/* 261 */         session.setPortForwardingR(bindPort, host, port);
/* 262 */       } catch (JSchException e) {
/* 263 */         throw new JschRuntimeException(e, "From [{}] mapping to [{}] error！", new Object[] { Integer.valueOf(bindPort), Integer.valueOf(port) });
/*     */       } 
/* 265 */       return true;
/*     */     } 
/* 267 */     return false;
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
/*     */   public static boolean unBindPort(Session session, int localPort) {
/*     */     try {
/* 280 */       session.delPortForwardingL(localPort);
/* 281 */     } catch (JSchException e) {
/* 282 */       throw new JschRuntimeException(e);
/*     */     } 
/* 284 */     return true;
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
/*     */   public static int openAndBindPortToLocal(Connector sshConn, String remoteHost, int remotePort) throws JschRuntimeException {
/* 297 */     Session session = openSession(sshConn.getHost(), sshConn.getPort(), sshConn.getUser(), sshConn.getPassword());
/* 298 */     int localPort = generateLocalPort();
/* 299 */     bindPort(session, remoteHost, remotePort, localPort);
/* 300 */     return localPort;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ChannelSftp openSftp(Session session) {
/* 311 */     return openSftp(session, 0);
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
/*     */   public static ChannelSftp openSftp(Session session, int timeout) {
/* 323 */     return (ChannelSftp)openChannel(session, ChannelType.SFTP, timeout);
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
/*     */   public static Sftp createSftp(String sshHost, int sshPort, String sshUser, String sshPass) {
/* 337 */     return new Sftp(sshHost, sshPort, sshUser, sshPass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Sftp createSftp(Session session) {
/* 348 */     return new Sftp(session);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ChannelShell openShell(Session session) {
/* 359 */     return (ChannelShell)openChannel(session, ChannelType.SHELL);
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
/*     */   public static Channel openChannel(Session session, ChannelType channelType) {
/* 371 */     return openChannel(session, channelType, 0);
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
/*     */   public static Channel openChannel(Session session, ChannelType channelType, int timeout) {
/* 384 */     Channel channel = createChannel(session, channelType);
/*     */     try {
/* 386 */       channel.connect(Math.max(timeout, 0));
/* 387 */     } catch (JSchException e) {
/* 388 */       throw new JschRuntimeException(e);
/*     */     } 
/* 390 */     return channel;
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
/*     */   public static Channel createChannel(Session session, ChannelType channelType) {
/*     */     Channel channel;
/*     */     try {
/* 404 */       if (false == session.isConnected()) {
/* 405 */         session.connect();
/*     */       }
/* 407 */       channel = session.openChannel(channelType.getValue());
/* 408 */     } catch (JSchException e) {
/* 409 */       throw new JschRuntimeException(e);
/*     */     } 
/* 411 */     return channel;
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
/*     */   public static String exec(Session session, String cmd, Charset charset) {
/* 424 */     return exec(session, cmd, charset, System.err);
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
/*     */   
/*     */   public static String exec(Session session, String cmd, Charset charset, OutputStream errStream) {
/* 441 */     if (null == charset) {
/* 442 */       charset = CharsetUtil.CHARSET_UTF_8;
/*     */     }
/* 444 */     ChannelExec channel = (ChannelExec)createChannel(session, ChannelType.EXEC);
/* 445 */     channel.setCommand(StrUtil.bytes(cmd, charset));
/* 446 */     channel.setInputStream(null);
/* 447 */     channel.setErrStream(errStream);
/* 448 */     InputStream in = null;
/*     */     try {
/* 450 */       channel.connect();
/* 451 */       in = channel.getInputStream();
/* 452 */       return IoUtil.read(in, charset);
/* 453 */     } catch (IOException e) {
/* 454 */       throw new IORuntimeException(e);
/* 455 */     } catch (JSchException e) {
/* 456 */       throw new JschRuntimeException(e);
/*     */     } finally {
/* 458 */       IoUtil.close(in);
/* 459 */       close((Channel)channel);
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
/*     */   public static String execByShell(Session session, String cmd, Charset charset) {
/* 476 */     ChannelShell shell = openShell(session);
/*     */     
/* 478 */     shell.setPty(true);
/* 479 */     OutputStream out = null;
/* 480 */     InputStream in = null;
/*     */     try {
/* 482 */       out = shell.getOutputStream();
/* 483 */       in = shell.getInputStream();
/*     */       
/* 485 */       out.write(StrUtil.bytes(cmd, charset));
/* 486 */       out.flush();
/*     */       
/* 488 */       return IoUtil.read(in, charset);
/* 489 */     } catch (IOException e) {
/* 490 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 492 */       IoUtil.close(out);
/* 493 */       IoUtil.close(in);
/* 494 */       close((Channel)shell);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void close(Session session) {
/* 504 */     if (session != null && session.isConnected()) {
/* 505 */       session.disconnect();
/*     */     }
/* 507 */     JschSessionPool.INSTANCE.remove(session);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void close(Channel channel) {
/* 517 */     if (channel != null && channel.isConnected()) {
/* 518 */       channel.disconnect();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void close(String key) {
/* 528 */     JschSessionPool.INSTANCE.close(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void closeAll() {
/* 535 */     JschSessionPool.INSTANCE.closeAll();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\ssh\JschUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */