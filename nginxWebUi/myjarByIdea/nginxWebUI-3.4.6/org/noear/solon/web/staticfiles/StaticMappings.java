package org.noear.solon.web.staticfiles;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StaticMappings {
   static final Map<StaticRepository, StaticLocation> locationMap = new HashMap();

   public static int count() {
      return locationMap.size();
   }

   public static synchronized void add(String pathPrefix, StaticRepository repository) {
      add(pathPrefix, true, repository);
   }

   public static synchronized void add(String pathPrefix, boolean repositoryIncPrefix, StaticRepository repository) {
      if (!pathPrefix.startsWith("/")) {
         pathPrefix = "/" + pathPrefix;
      }

      if (!pathPrefix.endsWith("/")) {
         pathPrefix = pathPrefix + "/";
      }

      locationMap.putIfAbsent(repository, new StaticLocation(pathPrefix, repository, repositoryIncPrefix));
   }

   public static synchronized void remove(StaticRepository repository) {
      locationMap.remove(repository);
   }

   public static URL find(String path) throws Exception {
      URL rst = null;
      Iterator var2 = locationMap.values().iterator();

      while(var2.hasNext()) {
         StaticLocation m = (StaticLocation)var2.next();
         if (path.startsWith(m.pathPrefix)) {
            if (m.repositoryIncPrefix) {
               rst = m.repository.find(path);
            } else {
               rst = m.repository.find(path.substring(m.pathPrefix.length()));
            }

            if (rst != null) {
               return rst;
            }
         }
      }

      return rst;
   }
}
