package io.undertow.servlet.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SecurityConstraint extends SecurityInfo<SecurityConstraint> {
   private final Set<WebResourceCollection> webResourceCollections = new HashSet();

   public Set<WebResourceCollection> getWebResourceCollections() {
      return Collections.unmodifiableSet(this.webResourceCollections);
   }

   public SecurityConstraint addWebResourceCollection(WebResourceCollection webResourceCollection) {
      this.webResourceCollections.add(webResourceCollection);
      return this;
   }

   public SecurityConstraint addWebResourceCollections(WebResourceCollection... webResourceCollection) {
      this.webResourceCollections.addAll(Arrays.asList(webResourceCollection));
      return this;
   }

   public SecurityConstraint addWebResourceCollections(List<WebResourceCollection> webResourceCollections) {
      this.webResourceCollections.addAll(webResourceCollections);
      return this;
   }

   protected SecurityConstraint createInstance() {
      return new SecurityConstraint();
   }

   public SecurityConstraint clone() {
      SecurityConstraint info = (SecurityConstraint)super.clone();
      Iterator var2 = this.webResourceCollections.iterator();

      while(var2.hasNext()) {
         WebResourceCollection wr = (WebResourceCollection)var2.next();
         info.addWebResourceCollection(wr.clone());
      }

      return info;
   }
}
