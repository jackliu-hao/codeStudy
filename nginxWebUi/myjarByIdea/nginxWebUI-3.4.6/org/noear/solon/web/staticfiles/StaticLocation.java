package org.noear.solon.web.staticfiles;

public class StaticLocation {
   public final String pathPrefix;
   public final StaticRepository repository;
   public final boolean repositoryIncPrefix;

   public StaticLocation(String pathPrefix, StaticRepository repository, boolean repositoryIncPrefix) {
      this.pathPrefix = pathPrefix;
      this.repository = repository;
      this.repositoryIncPrefix = repositoryIncPrefix;
   }
}
