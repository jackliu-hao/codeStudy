package cn.hutool.core.net;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class SSLContextBuilder implements SSLProtocols, Builder<SSLContext> {
   private static final long serialVersionUID = 1L;
   private String protocol = "TLS";
   private KeyManager[] keyManagers;
   private TrustManager[] trustManagers;
   private SecureRandom secureRandom;

   public SSLContextBuilder() {
      this.trustManagers = new TrustManager[]{DefaultTrustManager.INSTANCE};
      this.secureRandom = new SecureRandom();
   }

   public static SSLContextBuilder create() {
      return new SSLContextBuilder();
   }

   public SSLContextBuilder setProtocol(String protocol) {
      if (StrUtil.isNotBlank(protocol)) {
         this.protocol = protocol;
      }

      return this;
   }

   public SSLContextBuilder setTrustManagers(TrustManager... trustManagers) {
      if (ArrayUtil.isNotEmpty((Object[])trustManagers)) {
         this.trustManagers = trustManagers;
      }

      return this;
   }

   public SSLContextBuilder setKeyManagers(KeyManager... keyManagers) {
      if (ArrayUtil.isNotEmpty((Object[])keyManagers)) {
         this.keyManagers = keyManagers;
      }

      return this;
   }

   public SSLContextBuilder setSecureRandom(SecureRandom secureRandom) {
      if (null != secureRandom) {
         this.secureRandom = secureRandom;
      }

      return this;
   }

   public SSLContext build() {
      return this.buildQuietly();
   }

   public SSLContext buildChecked() throws NoSuchAlgorithmException, KeyManagementException {
      SSLContext sslContext = SSLContext.getInstance(this.protocol);
      sslContext.init(this.keyManagers, this.trustManagers, this.secureRandom);
      return sslContext;
   }

   public SSLContext buildQuietly() throws IORuntimeException {
      try {
         return this.buildChecked();
      } catch (GeneralSecurityException var2) {
         throw new IORuntimeException(var2);
      }
   }
}
