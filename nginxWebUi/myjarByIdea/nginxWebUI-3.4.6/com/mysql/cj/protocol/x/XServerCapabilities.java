package com.mysql.cj.protocol.x;

import com.mysql.cj.ServerVersion;
import com.mysql.cj.protocol.ServerCapabilities;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import com.mysql.cj.xdevapi.ExprUtil;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XServerCapabilities implements ServerCapabilities {
   private Map<String, MysqlxDatatypes.Any> capabilities;
   static String KEY_COMPRESSION = "compression";
   static String KEY_SESSION_CONNECT_ATTRS = "session_connect_attrs";
   static String KEY_TLS = "tls";
   static String KEY_NODE_TYPE = "node_type";
   static String KEY_CLIENT_PWD_EXPIRE_OK = "client.pwd_expire_ok";
   static String KEY_AUTHENTICATION_MECHANISMS = "authentication.mechanisms";
   static String KEY_DOC_FORMATS = "doc.formats";
   static String SUBKEY_COMPRESSION_ALGORITHM = "algorithm";
   static String SUBKEY_COMPRESSION_SERVER_COMBINE_MIXED_MESSAGES = "server_combine_mixed_messages";
   static String SUBKEY_COMPRESSION_SERVER_MAX_COMBINE_MESSAGES = "server_max_combine_messages";
   private long clientId = -1L;

   public XServerCapabilities(Map<String, MysqlxDatatypes.Any> capabilities) {
      this.capabilities = capabilities;
   }

   public void setCapability(String name, Object value) {
      if (!KEY_SESSION_CONNECT_ATTRS.equals(name) && !KEY_COMPRESSION.equals(name)) {
         this.capabilities.put(name, ExprUtil.argObjectToScalarAny(value));
      }

   }

   public boolean hasCapability(String name) {
      return this.capabilities.containsKey(name);
   }

   public String getNodeType() {
      return ((MysqlxDatatypes.Any)this.capabilities.get(KEY_NODE_TYPE)).getScalar().getVString().getValue().toStringUtf8();
   }

   public boolean getTls() {
      return this.hasCapability(KEY_TLS) ? ((MysqlxDatatypes.Any)this.capabilities.get(KEY_TLS)).getScalar().getVBool() : false;
   }

   public boolean getClientPwdExpireOk() {
      return ((MysqlxDatatypes.Any)this.capabilities.get(KEY_CLIENT_PWD_EXPIRE_OK)).getScalar().getVBool();
   }

   public List<String> getAuthenticationMechanisms() {
      return (List)((MysqlxDatatypes.Any)this.capabilities.get(KEY_AUTHENTICATION_MECHANISMS)).getArray().getValueList().stream().map((v) -> {
         return v.getScalar().getVString().getValue().toStringUtf8();
      }).collect(Collectors.toList());
   }

   public String getDocFormats() {
      return ((MysqlxDatatypes.Any)this.capabilities.get(KEY_DOC_FORMATS)).getScalar().getVString().getValue().toStringUtf8();
   }

   public Map<String, List<String>> getCompression() {
      return this.hasCapability(KEY_COMPRESSION) ? (Map)((MysqlxDatatypes.Any)this.capabilities.get(KEY_COMPRESSION)).getObj().getFldList().stream().collect(Collectors.toMap((f) -> {
         return f.getKey().toLowerCase();
      }, (f) -> {
         return (List)f.getValue().getArray().getValueList().stream().map((v) -> {
            return v.getScalar().getVString().getValue().toStringUtf8().toLowerCase();
         }).collect(Collectors.toList());
      })) : Collections.emptyMap();
   }

   public int getCapabilityFlags() {
      return 0;
   }

   public void setCapabilityFlags(int capabilityFlags) {
   }

   public ServerVersion getServerVersion() {
      return null;
   }

   public boolean serverSupportsFracSecs() {
      return true;
   }

   public int getServerDefaultCollationIndex() {
      return 0;
   }

   public long getThreadId() {
      return this.clientId;
   }

   public void setThreadId(long threadId) {
      this.clientId = threadId;
   }
}
