package cn.hutool.http.cookie;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpConnection;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalCookieManager {
   private static CookieManager cookieManager;

   public static void setCookieManager(CookieManager customCookieManager) {
      cookieManager = customCookieManager;
   }

   public static CookieManager getCookieManager() {
      return cookieManager;
   }

   public static List<HttpCookie> getCookies(HttpConnection conn) {
      return cookieManager.getCookieStore().get(getURI(conn));
   }

   public static void add(HttpConnection conn) {
      if (null != cookieManager) {
         Map cookieHeader;
         try {
            cookieHeader = cookieManager.get(getURI(conn), new HashMap(0));
         } catch (IOException var3) {
            throw new IORuntimeException(var3);
         }

         conn.header(cookieHeader, false);
      }
   }

   public static void store(HttpConnection conn) {
      if (null != cookieManager) {
         try {
            cookieManager.put(getURI(conn), conn.headers());
         } catch (IOException var2) {
            throw new IORuntimeException(var2);
         }
      }
   }

   private static URI getURI(HttpConnection conn) {
      return URLUtil.toURI(conn.getUrl());
   }

   static {
      cookieManager = new CookieManager(new ThreadLocalCookieStore(), CookiePolicy.ACCEPT_ALL);
   }
}
