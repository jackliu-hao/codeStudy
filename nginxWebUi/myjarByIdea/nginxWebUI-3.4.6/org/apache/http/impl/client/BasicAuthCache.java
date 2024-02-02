package org.apache.http.impl.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScheme;
import org.apache.http.client.AuthCache;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class BasicAuthCache implements AuthCache {
   private final Log log;
   private final Map<HttpHost, byte[]> map;
   private final SchemePortResolver schemePortResolver;

   public BasicAuthCache(SchemePortResolver schemePortResolver) {
      this.log = LogFactory.getLog(this.getClass());
      this.map = new ConcurrentHashMap();
      this.schemePortResolver = (SchemePortResolver)(schemePortResolver != null ? schemePortResolver : DefaultSchemePortResolver.INSTANCE);
   }

   public BasicAuthCache() {
      this((SchemePortResolver)null);
   }

   protected HttpHost getKey(HttpHost host) {
      if (host.getPort() <= 0) {
         int port;
         try {
            port = this.schemePortResolver.resolve(host);
         } catch (UnsupportedSchemeException var4) {
            return host;
         }

         return new HttpHost(host.getHostName(), port, host.getSchemeName());
      } else {
         return host;
      }
   }

   public void put(HttpHost host, AuthScheme authScheme) {
      Args.notNull(host, "HTTP host");
      if (authScheme != null) {
         if (authScheme instanceof Serializable) {
            try {
               ByteArrayOutputStream buf = new ByteArrayOutputStream();
               ObjectOutputStream out = new ObjectOutputStream(buf);
               out.writeObject(authScheme);
               out.close();
               this.map.put(this.getKey(host), buf.toByteArray());
            } catch (IOException var5) {
               if (this.log.isWarnEnabled()) {
                  this.log.warn("Unexpected I/O error while serializing auth scheme", var5);
               }
            }
         } else if (this.log.isDebugEnabled()) {
            this.log.debug("Auth scheme " + authScheme.getClass() + " is not serializable");
         }

      }
   }

   public AuthScheme get(HttpHost host) {
      Args.notNull(host, "HTTP host");
      byte[] bytes = (byte[])this.map.get(this.getKey(host));
      if (bytes != null) {
         try {
            ByteArrayInputStream buf = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(buf);
            AuthScheme authScheme = (AuthScheme)in.readObject();
            in.close();
            return authScheme;
         } catch (IOException var6) {
            if (this.log.isWarnEnabled()) {
               this.log.warn("Unexpected I/O error while de-serializing auth scheme", var6);
            }

            return null;
         } catch (ClassNotFoundException var7) {
            if (this.log.isWarnEnabled()) {
               this.log.warn("Unexpected error while de-serializing auth scheme", var7);
            }

            return null;
         }
      } else {
         return null;
      }
   }

   public void remove(HttpHost host) {
      Args.notNull(host, "HTTP host");
      this.map.remove(this.getKey(host));
   }

   public void clear() {
      this.map.clear();
   }

   public String toString() {
      return this.map.toString();
   }
}
