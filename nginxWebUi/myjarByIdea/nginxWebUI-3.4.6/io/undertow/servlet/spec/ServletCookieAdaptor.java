package io.undertow.servlet.spec;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieSameSiteMode;
import io.undertow.servlet.UndertowServletLogger;
import io.undertow.servlet.UndertowServletMessages;
import java.util.Arrays;
import java.util.Date;

public class ServletCookieAdaptor implements Cookie {
   private final javax.servlet.http.Cookie cookie;
   private boolean sameSite;
   private String sameSiteMode;

   public ServletCookieAdaptor(javax.servlet.http.Cookie cookie) {
      this.cookie = cookie;
   }

   public String getName() {
      return this.cookie.getName();
   }

   public String getValue() {
      return this.cookie.getValue();
   }

   public Cookie setValue(String value) {
      this.cookie.setValue(value);
      return this;
   }

   public String getPath() {
      return this.cookie.getPath();
   }

   public Cookie setPath(String path) {
      this.cookie.setPath(path);
      return this;
   }

   public String getDomain() {
      return this.cookie.getDomain();
   }

   public Cookie setDomain(String domain) {
      this.cookie.setDomain(domain);
      return this;
   }

   public Integer getMaxAge() {
      return this.cookie.getMaxAge();
   }

   public Cookie setMaxAge(Integer maxAge) {
      this.cookie.setMaxAge(maxAge);
      return this;
   }

   public boolean isDiscard() {
      return this.cookie.getMaxAge() < 0;
   }

   public Cookie setDiscard(boolean discard) {
      return this;
   }

   public boolean isSecure() {
      return this.cookie.getSecure();
   }

   public Cookie setSecure(boolean secure) {
      this.cookie.setSecure(secure);
      return this;
   }

   public int getVersion() {
      return this.cookie.getVersion();
   }

   public Cookie setVersion(int version) {
      this.cookie.setVersion(version);
      return this;
   }

   public boolean isHttpOnly() {
      return this.cookie.isHttpOnly();
   }

   public Cookie setHttpOnly(boolean httpOnly) {
      this.cookie.setHttpOnly(httpOnly);
      return this;
   }

   public Date getExpires() {
      return null;
   }

   public Cookie setExpires(Date expires) {
      throw UndertowServletMessages.MESSAGES.notImplemented();
   }

   public String getComment() {
      return this.cookie.getComment();
   }

   public Cookie setComment(String comment) {
      this.cookie.setComment(comment);
      return this;
   }

   public boolean isSameSite() {
      return this.sameSite;
   }

   public Cookie setSameSite(boolean sameSite) {
      this.sameSite = sameSite;
      return this;
   }

   public String getSameSiteMode() {
      return this.sameSiteMode;
   }

   public Cookie setSameSiteMode(String mode) {
      String m = CookieSameSiteMode.lookupModeString(mode);
      if (m != null) {
         UndertowServletLogger.REQUEST_LOGGER.tracef("Setting SameSite mode to [%s] for cookie [%s]", m, this.getName());
         this.sameSiteMode = m;
         this.setSameSite(true);
      } else {
         UndertowServletLogger.REQUEST_LOGGER.warnf(UndertowMessages.MESSAGES.invalidSameSiteMode(mode, Arrays.toString(CookieSameSiteMode.values())), "Ignoring specified SameSite mode [%s] for cookie [%s]", mode, this.getName());
      }

      return this;
   }

   public final int hashCode() {
      int result = 17;
      result = 37 * result + (this.getName() == null ? 0 : this.getName().hashCode());
      result = 37 * result + (this.getPath() == null ? 0 : this.getPath().hashCode());
      result = 37 * result + (this.getDomain() == null ? 0 : this.getDomain().hashCode());
      return result;
   }

   public final boolean equals(Object other) {
      if (other == this) {
         return true;
      } else if (!(other instanceof Cookie)) {
         return false;
      } else {
         Cookie o = (Cookie)other;
         if (this.getName() == null && o.getName() != null) {
            return false;
         } else if (this.getName() != null && !this.getName().equals(o.getName())) {
            return false;
         } else if (this.getPath() == null && o.getPath() != null) {
            return false;
         } else if (this.getPath() != null && !this.getPath().equals(o.getPath())) {
            return false;
         } else if (this.getDomain() == null && o.getDomain() != null) {
            return false;
         } else {
            return this.getDomain() == null || this.getDomain().equals(o.getDomain());
         }
      }
   }

   public final int compareTo(Object other) {
      return Cookie.super.compareTo(other);
   }

   public final String toString() {
      return "{ServletCookieAdaptor@" + System.identityHashCode(this) + " name=" + this.getName() + " path=" + this.getPath() + " domain=" + this.getDomain() + "}";
   }
}
