package io.undertow.servlet.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** @deprecated */
@Deprecated
public class DefaultServletConfig {
   private static final String[] DEFAULT_ALLOWED_EXTENSIONS = new String[]{"js", "css", "png", "jpg", "gif", "html", "htm", "txt", "pdf"};
   private static final String[] DEFAULT_DISALLOWED_EXTENSIONS = new String[]{"class", "jar", "war", "zip", "xml"};
   private final boolean defaultAllowed;
   private final Set<String> allowed;
   private final Set<String> disallowed;

   public DefaultServletConfig(boolean defaultAllowed, Set<String> exceptions) {
      this.defaultAllowed = defaultAllowed;
      if (defaultAllowed) {
         this.disallowed = Collections.unmodifiableSet(new HashSet(exceptions));
         this.allowed = null;
      } else {
         this.allowed = Collections.unmodifiableSet(new HashSet(exceptions));
         this.disallowed = null;
      }

   }

   public DefaultServletConfig(boolean defaultAllowed) {
      this.defaultAllowed = defaultAllowed;
      this.allowed = Collections.unmodifiableSet(new HashSet(Arrays.asList(DEFAULT_ALLOWED_EXTENSIONS)));
      this.disallowed = Collections.unmodifiableSet(new HashSet(Arrays.asList(DEFAULT_DISALLOWED_EXTENSIONS)));
   }

   public DefaultServletConfig() {
      this.defaultAllowed = false;
      this.allowed = Collections.unmodifiableSet(new HashSet(Arrays.asList(DEFAULT_ALLOWED_EXTENSIONS)));
      this.disallowed = Collections.unmodifiableSet(new HashSet(Arrays.asList(DEFAULT_DISALLOWED_EXTENSIONS)));
   }

   public boolean isDefaultAllowed() {
      return this.defaultAllowed;
   }

   public Set<String> getAllowed() {
      return this.allowed;
   }

   public Set<String> getDisallowed() {
      return this.disallowed;
   }
}
