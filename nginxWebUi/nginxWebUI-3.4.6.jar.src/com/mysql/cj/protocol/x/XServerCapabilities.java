/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.mysql.cj.ServerVersion;
/*     */ import com.mysql.cj.protocol.ServerCapabilities;
/*     */ import com.mysql.cj.x.protobuf.MysqlxDatatypes;
/*     */ import com.mysql.cj.xdevapi.ExprUtil;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
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
/*     */ public class XServerCapabilities
/*     */   implements ServerCapabilities
/*     */ {
/*     */   private Map<String, MysqlxDatatypes.Any> capabilities;
/*  46 */   static String KEY_COMPRESSION = "compression";
/*  47 */   static String KEY_SESSION_CONNECT_ATTRS = "session_connect_attrs";
/*  48 */   static String KEY_TLS = "tls";
/*  49 */   static String KEY_NODE_TYPE = "node_type";
/*  50 */   static String KEY_CLIENT_PWD_EXPIRE_OK = "client.pwd_expire_ok";
/*  51 */   static String KEY_AUTHENTICATION_MECHANISMS = "authentication.mechanisms";
/*  52 */   static String KEY_DOC_FORMATS = "doc.formats";
/*     */   
/*  54 */   static String SUBKEY_COMPRESSION_ALGORITHM = "algorithm";
/*  55 */   static String SUBKEY_COMPRESSION_SERVER_COMBINE_MIXED_MESSAGES = "server_combine_mixed_messages";
/*  56 */   static String SUBKEY_COMPRESSION_SERVER_MAX_COMBINE_MESSAGES = "server_max_combine_messages";
/*     */ 
/*     */   
/*  59 */   private long clientId = -1L;
/*     */   
/*     */   public XServerCapabilities(Map<String, MysqlxDatatypes.Any> capabilities) {
/*  62 */     this.capabilities = capabilities;
/*     */   }
/*     */   
/*     */   public void setCapability(String name, Object value) {
/*  66 */     if (!KEY_SESSION_CONNECT_ATTRS.equals(name) && !KEY_COMPRESSION.equals(name)) {
/*  67 */       this.capabilities.put(name, ExprUtil.argObjectToScalarAny(value));
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean hasCapability(String name) {
/*  72 */     return this.capabilities.containsKey(name);
/*     */   }
/*     */   
/*     */   public String getNodeType() {
/*  76 */     return ((MysqlxDatatypes.Any)this.capabilities.get(KEY_NODE_TYPE)).getScalar().getVString().getValue().toStringUtf8();
/*     */   }
/*     */   
/*     */   public boolean getTls() {
/*  80 */     return hasCapability(KEY_TLS) ? ((MysqlxDatatypes.Any)this.capabilities.get(KEY_TLS)).getScalar().getVBool() : false;
/*     */   }
/*     */   
/*     */   public boolean getClientPwdExpireOk() {
/*  84 */     return ((MysqlxDatatypes.Any)this.capabilities.get(KEY_CLIENT_PWD_EXPIRE_OK)).getScalar().getVBool();
/*     */   }
/*     */   
/*     */   public List<String> getAuthenticationMechanisms() {
/*  88 */     return (List<String>)((MysqlxDatatypes.Any)this.capabilities.get(KEY_AUTHENTICATION_MECHANISMS)).getArray().getValueList().stream()
/*  89 */       .map(v -> v.getScalar().getVString().getValue().toStringUtf8()).collect(Collectors.toList());
/*     */   }
/*     */   
/*     */   public String getDocFormats() {
/*  93 */     return ((MysqlxDatatypes.Any)this.capabilities.get(KEY_DOC_FORMATS)).getScalar().getVString().getValue().toStringUtf8();
/*     */   }
/*     */   
/*     */   public Map<String, List<String>> getCompression() {
/*  97 */     if (hasCapability(KEY_COMPRESSION)) {
/*  98 */       return (Map<String, List<String>>)((MysqlxDatatypes.Any)this.capabilities.get(KEY_COMPRESSION)).getObj().getFldList().stream()
/*  99 */         .collect(Collectors.toMap(f -> f.getKey().toLowerCase(), f -> (List)f.getValue().getArray().getValueList().stream().map(()).collect(Collectors.toList())));
/*     */     }
/*     */     
/* 102 */     return Collections.emptyMap();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCapabilityFlags() {
/* 108 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCapabilityFlags(int capabilityFlags) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerVersion getServerVersion() {
/* 120 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean serverSupportsFracSecs() {
/* 125 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getServerDefaultCollationIndex() {
/* 131 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getThreadId() {
/* 136 */     return this.clientId;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setThreadId(long threadId) {
/* 141 */     this.clientId = threadId;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\XServerCapabilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */