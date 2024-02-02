package io.undertow.servlet.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class WebResourceCollection implements Cloneable {
   private final Set<String> httpMethods = new HashSet();
   private final Set<String> httpMethodOmissions = new HashSet();
   private final Set<String> urlPatterns = new HashSet();

   public WebResourceCollection addHttpMethod(String s) {
      this.httpMethods.add(s);
      return this;
   }

   public WebResourceCollection addHttpMethods(String... s) {
      this.httpMethods.addAll(Arrays.asList(s));
      return this;
   }

   public WebResourceCollection addHttpMethods(Collection<String> s) {
      this.httpMethods.addAll(s);
      return this;
   }

   public WebResourceCollection addUrlPattern(String s) {
      this.urlPatterns.add(s);
      return this;
   }

   public WebResourceCollection addUrlPatterns(String... s) {
      this.urlPatterns.addAll(Arrays.asList(s));
      return this;
   }

   public WebResourceCollection addUrlPatterns(Collection<String> s) {
      this.urlPatterns.addAll(s);
      return this;
   }

   public WebResourceCollection addHttpMethodOmission(String s) {
      this.httpMethodOmissions.add(s);
      return this;
   }

   public WebResourceCollection addHttpMethodOmissions(String... s) {
      this.httpMethodOmissions.addAll(Arrays.asList(s));
      return this;
   }

   public WebResourceCollection addHttpMethodOmissions(Collection<String> s) {
      this.httpMethodOmissions.addAll(s);
      return this;
   }

   public Set<String> getHttpMethodOmissions() {
      return this.httpMethodOmissions;
   }

   public Set<String> getUrlPatterns() {
      return this.urlPatterns;
   }

   public Set<String> getHttpMethods() {
      return this.httpMethods;
   }

   protected WebResourceCollection clone() {
      return (new WebResourceCollection()).addHttpMethodOmissions((Collection)this.httpMethodOmissions).addHttpMethods((Collection)this.httpMethods).addUrlPatterns((Collection)this.urlPatterns);
   }
}
