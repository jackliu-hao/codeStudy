package com.mysql.cj.conf.url;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.ConnectionUrlParser;
import java.util.Properties;

public class FailoverConnectionUrl extends ConnectionUrl {
   public FailoverConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
      super(connStrParser, info);
      this.type = ConnectionUrl.Type.FAILOVER_CONNECTION;
   }
}
