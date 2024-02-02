package com.mysql.cj.xdevapi;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.InvalidConnectionAttributeException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class SessionFactory {
   protected ConnectionUrl parseUrl(String url) {
      ConnectionUrl connUrl = ConnectionUrl.getConnectionUrlInstance(url, (Properties)null);
      if (connUrl != null && (connUrl.getType() == ConnectionUrl.Type.XDEVAPI_SESSION || connUrl.getType() == ConnectionUrl.Type.XDEVAPI_DNS_SRV_SESSION)) {
         return connUrl;
      } else {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, "Initialization via URL failed for \"" + url + "\"");
      }
   }

   protected Session getSession(ConnectionUrl connUrl) {
      CJException latestException = null;
      List<HostInfo> hostsList = connUrl.getHostsList();
      Iterator var4 = hostsList.iterator();

      while(var4.hasNext()) {
         HostInfo hi = (HostInfo)var4.next();

         try {
            return new SessionImpl(hi);
         } catch (CJCommunicationsException var7) {
            if (var7.getCause() == null) {
               throw var7;
            }

            latestException = var7;
         }
      }

      if (latestException != null) {
         throw (CJCommunicationsException)ExceptionFactory.createException((Class)CJCommunicationsException.class, (String)Messages.getString("Session.Create.Failover.0"), (Throwable)latestException);
      } else {
         return null;
      }
   }

   public Session getSession(String url) {
      return this.getSession(this.parseUrl(url));
   }

   public Session getSession(Properties properties) {
      ConnectionUrl connUrl;
      if (properties.containsKey(PropertyKey.xdevapiDnsSrv.getKeyName()) && (Boolean)PropertyDefinitions.getPropertyDefinition(PropertyKey.xdevapiDnsSrv).parseObject(properties.getProperty(PropertyKey.xdevapiDnsSrv.getKeyName()), (ExceptionInterceptor)null)) {
         connUrl = ConnectionUrl.getConnectionUrlInstance(ConnectionUrl.Type.XDEVAPI_DNS_SRV_SESSION.getScheme(), properties);
         return this.getSession(connUrl);
      } else {
         connUrl = ConnectionUrl.getConnectionUrlInstance(ConnectionUrl.Type.XDEVAPI_SESSION.getScheme(), properties);
         return new SessionImpl(connUrl.getMainHost());
      }
   }
}
