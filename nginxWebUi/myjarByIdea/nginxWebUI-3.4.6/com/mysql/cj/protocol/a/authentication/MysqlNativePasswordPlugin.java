package com.mysql.cj.protocol.a.authentication;

import com.mysql.cj.callback.MysqlCallbackHandler;
import com.mysql.cj.callback.UsernameCallback;
import com.mysql.cj.protocol.AuthenticationPlugin;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.Security;
import com.mysql.cj.protocol.a.NativeConstants;
import com.mysql.cj.protocol.a.NativePacketPayload;
import java.util.List;

public class MysqlNativePasswordPlugin implements AuthenticationPlugin<NativePacketPayload> {
   public static String PLUGIN_NAME = "mysql_native_password";
   private Protocol<NativePacketPayload> protocol = null;
   private MysqlCallbackHandler usernameCallbackHandler = null;
   private String password = null;

   public void init(Protocol<NativePacketPayload> prot, MysqlCallbackHandler cbh) {
      this.protocol = prot;
      this.usernameCallbackHandler = cbh;
   }

   public void destroy() {
      this.password = null;
   }

   public String getProtocolPluginName() {
      return PLUGIN_NAME;
   }

   public boolean requiresConfidentiality() {
      return false;
   }

   public boolean isReusable() {
      return true;
   }

   public void setAuthenticationParameters(String user, String password) {
      this.password = password;
      if (user == null && this.usernameCallbackHandler != null) {
         this.usernameCallbackHandler.handle(new UsernameCallback(System.getProperty("user.name")));
      }

   }

   public boolean nextAuthenticationStep(NativePacketPayload fromServer, List<NativePacketPayload> toServer) {
      toServer.clear();
      NativePacketPayload bresp = null;
      String pwd = this.password;
      if (fromServer != null && pwd != null && pwd.length() != 0) {
         bresp = new NativePacketPayload(Security.scramble411(pwd, fromServer.readBytes(NativeConstants.StringSelfDataType.STRING_TERM), this.protocol.getServerSession().getCharsetSettings().getPasswordCharacterEncoding()));
      } else {
         bresp = new NativePacketPayload(new byte[0]);
      }

      toServer.add(bresp);
      return true;
   }
}
