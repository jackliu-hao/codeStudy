package com.mysql.cj.conf.url;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.BooleanPropertyDefinition;
import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.ConnectionUrlParser;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.HostsListView;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.InvalidConnectionAttributeException;
import com.mysql.cj.util.StringUtils;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class XDevApiDnsSrvConnectionUrl extends ConnectionUrl {
   private static final String DEFAULT_HOST = "";
   private static final int DEFAULT_PORT = -1;

   public XDevApiDnsSrvConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
      super(connStrParser, info);
      this.type = ConnectionUrl.Type.XDEVAPI_DNS_SRV_SESSION;
      HostInfo srvHost = super.getMainHost();
      Map<String, String> hostProps = srvHost.getHostProperties();
      if ("".equals(srvHost.getHost())) {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.18"));
      } else if (this.hosts.size() != 1) {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.19"));
      } else if (srvHost.getPort() != -1) {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.22"));
      } else if (hostProps.containsKey(PropertyKey.xdevapiDnsSrv.getKeyName()) && !BooleanPropertyDefinition.booleanFrom(PropertyKey.xdevapiDnsSrv.getKeyName(), (String)hostProps.get(PropertyKey.xdevapiDnsSrv.getKeyName()), (ExceptionInterceptor)null)) {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.23", new Object[]{PropertyKey.xdevapiDnsSrv.getKeyName()}));
      }
   }

   protected void preprocessPerTypeHostProperties(Map<String, String> hostProps) {
      if (hostProps.containsKey(PropertyKey.ADDRESS.getKeyName())) {
         String address = (String)hostProps.get(PropertyKey.ADDRESS.getKeyName());
         ConnectionUrlParser.Pair<String, Integer> hostPortPair = ConnectionUrlParser.parseHostPortPair(address);
         String host = StringUtils.safeTrim((String)hostPortPair.left);
         Integer port = (Integer)hostPortPair.right;
         if (!StringUtils.isNullOrEmpty(host) && !hostProps.containsKey(PropertyKey.HOST.getKeyName())) {
            hostProps.put(PropertyKey.HOST.getKeyName(), host);
         }

         if (port != -1 && !hostProps.containsKey(PropertyKey.PORT.getKeyName())) {
            hostProps.put(PropertyKey.PORT.getKeyName(), port.toString());
         }
      }

   }

   public String getDefaultHost() {
      return "";
   }

   public int getDefaultPort() {
      return -1;
   }

   protected void fixProtocolDependencies(Map<String, String> hostProps) {
   }

   public List<HostInfo> getHostsList(HostsListView view) {
      return this.getHostsListFromDnsSrv(this.getMainHost());
   }
}
