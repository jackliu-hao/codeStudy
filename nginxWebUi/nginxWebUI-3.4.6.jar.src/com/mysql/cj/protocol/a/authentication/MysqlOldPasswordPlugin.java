/*     */ package com.mysql.cj.protocol.a.authentication;
/*     */ 
/*     */ import com.mysql.cj.callback.MysqlCallback;
/*     */ import com.mysql.cj.callback.MysqlCallbackHandler;
/*     */ import com.mysql.cj.callback.UsernameCallback;
/*     */ import com.mysql.cj.protocol.AuthenticationPlugin;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.Protocol;
/*     */ import com.mysql.cj.protocol.a.NativeConstants;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.List;
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
/*     */ public class MysqlOldPasswordPlugin
/*     */   implements AuthenticationPlugin<NativePacketPayload>
/*     */ {
/*  48 */   public static String PLUGIN_NAME = "mysql_old_password";
/*     */   
/*  50 */   private Protocol<NativePacketPayload> protocol = null;
/*  51 */   private MysqlCallbackHandler usernameCallbackHandler = null;
/*  52 */   private String password = null;
/*     */ 
/*     */   
/*     */   public void init(Protocol<NativePacketPayload> prot, MysqlCallbackHandler cbh) {
/*  56 */     this.protocol = prot;
/*  57 */     this.usernameCallbackHandler = cbh;
/*     */   }
/*     */   
/*     */   public void destroy() {
/*  61 */     this.password = null;
/*     */   }
/*     */   
/*     */   public String getProtocolPluginName() {
/*  65 */     return PLUGIN_NAME;
/*     */   }
/*     */   
/*     */   public boolean requiresConfidentiality() {
/*  69 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isReusable() {
/*  73 */     return true;
/*     */   }
/*     */   
/*     */   public void setAuthenticationParameters(String user, String password) {
/*  77 */     this.password = password;
/*  78 */     if (user == null && this.usernameCallbackHandler != null)
/*     */     {
/*  80 */       this.usernameCallbackHandler.handle((MysqlCallback)new UsernameCallback(System.getProperty("user.name")));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean nextAuthenticationStep(NativePacketPayload fromServer, List<NativePacketPayload> toServer) {
/*  86 */     toServer.clear();
/*     */     
/*  88 */     NativePacketPayload bresp = null;
/*     */     
/*  90 */     String pwd = this.password;
/*     */     
/*  92 */     if (fromServer == null || pwd == null || pwd.length() == 0) {
/*  93 */       bresp = new NativePacketPayload(new byte[0]);
/*     */     } else {
/*  95 */       bresp = new NativePacketPayload(StringUtils.getBytes(newCrypt(pwd, fromServer.readString(NativeConstants.StringSelfDataType.STRING_TERM, null).substring(0, 8), this.protocol
/*  96 */               .getServerSession().getCharsetSettings().getPasswordCharacterEncoding())));
/*     */       
/*  98 */       bresp.setPosition(bresp.getPayloadLength());
/*  99 */       bresp.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
/* 100 */       bresp.setPosition(0);
/*     */     } 
/* 102 */     toServer.add(bresp);
/*     */     
/* 104 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String newCrypt(String password, String seed, String encoding) {
/* 112 */     if (password == null || password.length() == 0) {
/* 113 */       return password;
/*     */     }
/*     */     
/* 116 */     long[] pw = newHash(seed.getBytes());
/* 117 */     long[] msg = hashPre41Password(password, encoding);
/* 118 */     long max = 1073741823L;
/* 119 */     long seed1 = (pw[0] ^ msg[0]) % max;
/* 120 */     long seed2 = (pw[1] ^ msg[1]) % max;
/* 121 */     char[] chars = new char[seed.length()];
/*     */     int i;
/* 123 */     for (i = 0; i < seed.length(); i++) {
/* 124 */       seed1 = (seed1 * 3L + seed2) % max;
/* 125 */       seed2 = (seed1 + seed2 + 33L) % max;
/* 126 */       double d1 = seed1 / max;
/* 127 */       byte b1 = (byte)(int)Math.floor(d1 * 31.0D + 64.0D);
/* 128 */       chars[i] = (char)b1;
/*     */     } 
/*     */     
/* 131 */     seed1 = (seed1 * 3L + seed2) % max;
/* 132 */     seed2 = (seed1 + seed2 + 33L) % max;
/* 133 */     double d = seed1 / max;
/* 134 */     byte b = (byte)(int)Math.floor(d * 31.0D);
/*     */     
/* 136 */     for (i = 0; i < seed.length(); i++) {
/* 137 */       chars[i] = (char)(chars[i] ^ (char)b);
/*     */     }
/*     */     
/* 140 */     return new String(chars);
/*     */   }
/*     */ 
/*     */   
/*     */   private static long[] hashPre41Password(String password, String encoding) {
/*     */     try {
/* 146 */       return newHash(password.replaceAll("\\s", "").getBytes(encoding));
/* 147 */     } catch (UnsupportedEncodingException e) {
/* 148 */       return new long[0];
/*     */     } 
/*     */   }
/*     */   
/*     */   private static long[] newHash(byte[] password) {
/* 153 */     long nr = 1345345333L;
/* 154 */     long add = 7L;
/* 155 */     long nr2 = 305419889L;
/*     */ 
/*     */     
/* 158 */     for (byte b : password) {
/* 159 */       long tmp = (0xFF & b);
/* 160 */       nr ^= ((nr & 0x3FL) + add) * tmp + (nr << 8L);
/* 161 */       nr2 += nr2 << 8L ^ nr;
/* 162 */       add += tmp;
/*     */     } 
/*     */     
/* 165 */     long[] result = new long[2];
/* 166 */     result[0] = nr & 0x7FFFFFFFL;
/* 167 */     result[1] = nr2 & 0x7FFFFFFFL;
/*     */     
/* 169 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\authentication\MysqlOldPasswordPlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */