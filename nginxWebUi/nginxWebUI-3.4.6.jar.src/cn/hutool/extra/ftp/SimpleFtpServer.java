/*     */ package cn.hutool.extra.ftp;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.net.NetUtil;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.ftpserver.ConnectionConfig;
/*     */ import org.apache.ftpserver.FtpServerFactory;
/*     */ import org.apache.ftpserver.ftplet.Authority;
/*     */ import org.apache.ftpserver.ftplet.FtpException;
/*     */ import org.apache.ftpserver.ftplet.Ftplet;
/*     */ import org.apache.ftpserver.ftplet.User;
/*     */ import org.apache.ftpserver.ftplet.UserManager;
/*     */ import org.apache.ftpserver.listener.ListenerFactory;
/*     */ import org.apache.ftpserver.ssl.SslConfiguration;
/*     */ import org.apache.ftpserver.ssl.SslConfigurationFactory;
/*     */ import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
/*     */ import org.apache.ftpserver.usermanager.impl.BaseUser;
/*     */ import org.apache.ftpserver.usermanager.impl.WritePermission;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SimpleFtpServer
/*     */ {
/*     */   FtpServerFactory serverFactory;
/*     */   ListenerFactory listenerFactory;
/*     */   
/*     */   public static SimpleFtpServer create() {
/*  36 */     return new SimpleFtpServer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleFtpServer() {
/*  46 */     this.serverFactory = new FtpServerFactory();
/*  47 */     this.listenerFactory = new ListenerFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FtpServerFactory getServerFactory() {
/*  56 */     return this.serverFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleFtpServer setConnectionConfig(ConnectionConfig connectionConfig) {
/*  66 */     this.serverFactory.setConnectionConfig(connectionConfig);
/*  67 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListenerFactory getListenerFactory() {
/*  76 */     return this.listenerFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleFtpServer setPort(int port) {
/*  86 */     Assert.isTrue(NetUtil.isValidPort(port), "Invalid port!", new Object[0]);
/*  87 */     this.listenerFactory.setPort(port);
/*  88 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UserManager getUserManager() {
/*  97 */     return this.serverFactory.getUserManager();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleFtpServer addUser(User user) {
/*     */     try {
/* 108 */       getUserManager().save(user);
/* 109 */     } catch (FtpException e) {
/* 110 */       throw new FtpException(e);
/*     */     } 
/* 112 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleFtpServer addAnonymous(String homePath) {
/* 122 */     BaseUser user = new BaseUser();
/* 123 */     user.setName("anonymous");
/* 124 */     user.setHomeDirectory(homePath);
/* 125 */     List<Authority> authorities = new ArrayList<>();
/*     */     
/* 127 */     authorities.add(new WritePermission());
/* 128 */     user.setAuthorities(authorities);
/* 129 */     return addUser((User)user);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleFtpServer delUser(String userName) {
/*     */     try {
/* 140 */       getUserManager().delete(userName);
/* 141 */     } catch (FtpException e) {
/* 142 */       throw new FtpException(e);
/*     */     } 
/* 144 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleFtpServer setSsl(SslConfiguration ssl) {
/* 154 */     this.listenerFactory.setSslConfiguration(ssl);
/* 155 */     this.listenerFactory.setImplicitSsl(true);
/* 156 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleFtpServer setSsl(File keystoreFile, String password) {
/* 167 */     SslConfigurationFactory sslFactory = new SslConfigurationFactory();
/* 168 */     sslFactory.setKeystoreFile(keystoreFile);
/* 169 */     sslFactory.setKeystorePassword(password);
/* 170 */     return setSsl(sslFactory.createSslConfiguration());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleFtpServer setUserManager(UserManager userManager) {
/* 180 */     this.serverFactory.setUserManager(userManager);
/* 181 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleFtpServer setUsersConfig(File propertiesFile) {
/* 191 */     PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
/* 192 */     userManagerFactory.setFile(propertiesFile);
/* 193 */     return setUserManager(userManagerFactory.createUserManager());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleFtpServer addFtplet(String name, Ftplet ftplet) {
/* 204 */     this.serverFactory.getFtplets().put(name, ftplet);
/* 205 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/* 214 */     this.serverFactory.addListener("default", this.listenerFactory.createListener());
/*     */     try {
/* 216 */       this.serverFactory.createServer().start();
/* 217 */     } catch (FtpException e) {
/* 218 */       throw new FtpException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\ftp\SimpleFtpServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */