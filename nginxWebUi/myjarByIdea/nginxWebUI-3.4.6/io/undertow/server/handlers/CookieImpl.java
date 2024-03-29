package io.undertow.server.handlers;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import java.util.Arrays;
import java.util.Date;

public class CookieImpl implements Cookie {
   private final String name;
   private String value;
   private String path;
   private String domain;
   private Integer maxAge;
   private Date expires;
   private boolean discard;
   private boolean secure;
   private boolean httpOnly;
   private int version = 0;
   private String comment;
   private boolean sameSite;
   private String sameSiteMode;

   public CookieImpl(String name, String value) {
      this.name = name;
      this.value = value;
   }

   public CookieImpl(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public CookieImpl setValue(String value) {
      this.value = value;
      return this;
   }

   public String getPath() {
      return this.path;
   }

   public CookieImpl setPath(String path) {
      this.path = path;
      return this;
   }

   public String getDomain() {
      return this.domain;
   }

   public CookieImpl setDomain(String domain) {
      this.domain = domain;
      return this;
   }

   public Integer getMaxAge() {
      return this.maxAge;
   }

   public CookieImpl setMaxAge(Integer maxAge) {
      this.maxAge = maxAge;
      return this;
   }

   public boolean isDiscard() {
      return this.discard;
   }

   public CookieImpl setDiscard(boolean discard) {
      this.discard = discard;
      return this;
   }

   public boolean isSecure() {
      return this.secure;
   }

   public CookieImpl setSecure(boolean secure) {
      this.secure = secure;
      return this;
   }

   public int getVersion() {
      return this.version;
   }

   public CookieImpl setVersion(int version) {
      this.version = version;
      return this;
   }

   public boolean isHttpOnly() {
      return this.httpOnly;
   }

   public CookieImpl setHttpOnly(boolean httpOnly) {
      this.httpOnly = httpOnly;
      return this;
   }

   public Date getExpires() {
      return this.expires;
   }

   public CookieImpl setExpires(Date expires) {
      this.expires = expires;
      return this;
   }

   public String getComment() {
      return this.comment;
   }

   public Cookie setComment(String comment) {
      this.comment = comment;
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
         UndertowLogger.REQUEST_LOGGER.tracef("Setting SameSite mode to [%s] for cookie [%s]", m, this.name);
         this.sameSiteMode = m;
         this.setSameSite(true);
      } else {
         UndertowLogger.REQUEST_LOGGER.warnf(UndertowMessages.MESSAGES.invalidSameSiteMode(mode, Arrays.toString(CookieSameSiteMode.values())), "Ignoring specified SameSite mode [%s] for cookie [%s]", mode, this.name);
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
      return "{CookieImpl@" + System.identityHashCode(this) + " name=" + this.getName() + " path=" + this.getPath() + " domain=" + this.getDomain() + "}";
   }
}
