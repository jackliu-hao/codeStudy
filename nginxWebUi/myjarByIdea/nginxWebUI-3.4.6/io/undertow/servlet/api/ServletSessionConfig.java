package io.undertow.servlet.api;

import java.util.Set;
import javax.servlet.SessionTrackingMode;

public class ServletSessionConfig {
   public static final String DEFAULT_SESSION_ID = "JSESSIONID";
   private Set<SessionTrackingMode> sessionTrackingModes;
   private String name = "JSESSIONID";
   private String path;
   private String domain;
   private boolean secure;
   private boolean httpOnly;
   private int maxAge = -1;
   private String comment;

   public String getName() {
      return this.name;
   }

   public ServletSessionConfig setName(String name) {
      this.name = name;
      return this;
   }

   public String getDomain() {
      return this.domain;
   }

   public ServletSessionConfig setDomain(String domain) {
      this.domain = domain;
      return this;
   }

   public String getPath() {
      return this.path;
   }

   public ServletSessionConfig setPath(String path) {
      this.path = path;
      return this;
   }

   public String getComment() {
      return this.comment;
   }

   public ServletSessionConfig setComment(String comment) {
      this.comment = comment;
      return this;
   }

   public boolean isHttpOnly() {
      return this.httpOnly;
   }

   public ServletSessionConfig setHttpOnly(boolean httpOnly) {
      this.httpOnly = httpOnly;
      return this;
   }

   public boolean isSecure() {
      return this.secure;
   }

   public ServletSessionConfig setSecure(boolean secure) {
      this.secure = secure;
      return this;
   }

   public int getMaxAge() {
      return this.maxAge;
   }

   public ServletSessionConfig setMaxAge(int maxAge) {
      this.maxAge = maxAge;
      return this;
   }

   public Set<SessionTrackingMode> getSessionTrackingModes() {
      return this.sessionTrackingModes;
   }

   public ServletSessionConfig setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
      this.sessionTrackingModes = sessionTrackingModes;
      return this;
   }
}
