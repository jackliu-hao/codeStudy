package org.apache.http.impl.client;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class BasicCredentialsProvider implements CredentialsProvider {
   private final ConcurrentHashMap<AuthScope, Credentials> credMap = new ConcurrentHashMap();

   public void setCredentials(AuthScope authscope, Credentials credentials) {
      Args.notNull(authscope, "Authentication scope");
      this.credMap.put(authscope, credentials);
   }

   private static Credentials matchCredentials(Map<AuthScope, Credentials> map, AuthScope authscope) {
      Credentials creds = (Credentials)map.get(authscope);
      if (creds == null) {
         int bestMatchFactor = -1;
         AuthScope bestMatch = null;
         Iterator i$ = map.keySet().iterator();

         while(i$.hasNext()) {
            AuthScope current = (AuthScope)i$.next();
            int factor = authscope.match(current);
            if (factor > bestMatchFactor) {
               bestMatchFactor = factor;
               bestMatch = current;
            }
         }

         if (bestMatch != null) {
            creds = (Credentials)map.get(bestMatch);
         }
      }

      return creds;
   }

   public Credentials getCredentials(AuthScope authscope) {
      Args.notNull(authscope, "Authentication scope");
      return matchCredentials(this.credMap, authscope);
   }

   public void clear() {
      this.credMap.clear();
   }

   public String toString() {
      return this.credMap.toString();
   }
}
