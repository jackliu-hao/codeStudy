package com.mysql.cj.xdevapi;

import java.util.Properties;

public class ClientFactory {
   public Client getClient(String url, String clientPropsJson) {
      return new ClientImpl(url, clientPropsJson);
   }

   public Client getClient(String url, Properties clientProps) {
      return new ClientImpl(url, clientProps);
   }
}
