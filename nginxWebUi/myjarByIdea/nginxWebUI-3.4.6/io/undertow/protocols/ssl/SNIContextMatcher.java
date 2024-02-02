package io.undertow.protocols.ssl;

import io.undertow.UndertowMessages;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLContext;

public class SNIContextMatcher {
   private final SSLContext defaultContext;
   private final Map<SNIMatcher, SSLContext> wildcards;
   private final Map<SNIMatcher, SSLContext> exacts;

   SNIContextMatcher(SSLContext defaultContext, Map<SNIMatcher, SSLContext> wildcards, Map<SNIMatcher, SSLContext> exacts) {
      this.defaultContext = defaultContext;
      this.wildcards = wildcards;
      this.exacts = exacts;
   }

   public SSLContext getContext(List<SNIServerName> servers) {
      Iterator var2 = this.exacts.entrySet().iterator();

      Map.Entry entry;
      Iterator var4;
      SNIServerName server;
      while(var2.hasNext()) {
         entry = (Map.Entry)var2.next();
         var4 = servers.iterator();

         while(var4.hasNext()) {
            server = (SNIServerName)var4.next();
            if (((SNIMatcher)entry.getKey()).matches(server)) {
               return (SSLContext)entry.getValue();
            }
         }
      }

      var2 = this.wildcards.entrySet().iterator();

      while(var2.hasNext()) {
         entry = (Map.Entry)var2.next();
         var4 = servers.iterator();

         while(var4.hasNext()) {
            server = (SNIServerName)var4.next();
            if (((SNIMatcher)entry.getKey()).matches(server)) {
               return (SSLContext)entry.getValue();
            }
         }
      }

      return this.defaultContext;
   }

   public SSLContext getDefaultContext() {
      return this.defaultContext;
   }

   public static class Builder {
      private SSLContext defaultContext;
      private final Map<SNIMatcher, SSLContext> wildcards = new LinkedHashMap();
      private final Map<SNIMatcher, SSLContext> exacts = new LinkedHashMap();

      public SNIContextMatcher build() {
         if (this.defaultContext == null) {
            throw UndertowMessages.MESSAGES.defaultContextCannotBeNull();
         } else {
            return new SNIContextMatcher(this.defaultContext, this.wildcards, this.exacts);
         }
      }

      public SSLContext getDefaultContext() {
         return this.defaultContext;
      }

      public Builder setDefaultContext(SSLContext defaultContext) {
         this.defaultContext = defaultContext;
         return this;
      }

      public Builder addMatch(String name, SSLContext context) {
         if (name.contains("*")) {
            this.wildcards.put(SNIHostName.createSNIMatcher(name), context);
         } else {
            this.exacts.put(SNIHostName.createSNIMatcher(name), context);
         }

         return this;
      }
   }
}
