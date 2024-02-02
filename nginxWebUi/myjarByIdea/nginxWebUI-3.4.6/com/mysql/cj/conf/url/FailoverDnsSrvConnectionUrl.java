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
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class FailoverDnsSrvConnectionUrl extends ConnectionUrl {
   private static final String DEFAULT_HOST = "";
   private static final int DEFAULT_PORT = -1;

   public FailoverDnsSrvConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
      super(connStrParser, info);
      this.type = ConnectionUrl.Type.FAILOVER_DNS_SRV_CONNECTION;
      HostInfo srvHost = super.getMainHost();
      Map<String, String> hostProps = srvHost.getHostProperties();
      if ("".equals(srvHost.getHost())) {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.18"));
      } else if (this.hosts.size() != 1) {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.19"));
      } else if (srvHost.getPort() != -1) {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.22"));
      } else if (hostProps.containsKey(PropertyKey.dnsSrv.getKeyName()) && !BooleanPropertyDefinition.booleanFrom(PropertyKey.dnsSrv.getKeyName(), (String)hostProps.get(PropertyKey.dnsSrv.getKeyName()), (ExceptionInterceptor)null)) {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.23", new Object[]{PropertyKey.dnsSrv.getKeyName()}));
      } else if (hostProps.containsKey(PropertyKey.PROTOCOL.getKeyName()) && ((String)hostProps.get(PropertyKey.PROTOCOL.getKeyName())).equalsIgnoreCase("PIPE")) {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.24"));
      }
   }

   public String getDefaultHost() {
      return "";
   }

   public int getDefaultPort() {
      return -1;
   }

   public List<HostInfo> getHostsList(HostsListView view) {
      return this.getHostsListFromDnsSrv(this.getMainHost());
   }
}
