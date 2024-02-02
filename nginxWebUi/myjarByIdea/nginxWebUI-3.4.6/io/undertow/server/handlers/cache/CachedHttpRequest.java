package io.undertow.server.handlers.cache;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.encoding.AllowedContentEncodings;
import io.undertow.util.DateUtils;
import io.undertow.util.ETag;
import io.undertow.util.ETagUtils;
import io.undertow.util.Headers;
import java.util.Date;

public class CachedHttpRequest {
   private final String path;
   private final ETag etag;
   private final String contentEncoding;
   private final String contentLocation;
   private final String language;
   private final String contentType;
   private final Date lastModified;
   private final int responseCode;

   public CachedHttpRequest(HttpServerExchange exchange) {
      this.path = exchange.getRequestPath();
      this.etag = ETagUtils.getETag(exchange);
      this.contentLocation = exchange.getResponseHeaders().getFirst(Headers.CONTENT_LOCATION);
      this.language = exchange.getResponseHeaders().getFirst(Headers.CONTENT_LANGUAGE);
      this.contentType = exchange.getResponseHeaders().getFirst(Headers.CONTENT_TYPE);
      String lmString = exchange.getResponseHeaders().getFirst(Headers.LAST_MODIFIED);
      if (lmString == null) {
         this.lastModified = null;
      } else {
         this.lastModified = DateUtils.parseDate(lmString);
      }

      AllowedContentEncodings encoding = (AllowedContentEncodings)exchange.getAttachment(AllowedContentEncodings.ATTACHMENT_KEY);
      if (encoding != null) {
         this.contentEncoding = encoding.getCurrentContentEncoding();
      } else {
         this.contentEncoding = exchange.getResponseHeaders().getFirst(Headers.CONTENT_ENCODING);
      }

      this.responseCode = exchange.getStatusCode();
   }

   public String getPath() {
      return this.path;
   }

   public ETag getEtag() {
      return this.etag;
   }

   public String getContentEncoding() {
      return this.contentEncoding;
   }

   public String getLanguage() {
      return this.language;
   }

   public String getContentType() {
      return this.contentType;
   }

   public Date getLastModified() {
      return this.lastModified;
   }

   public String getContentLocation() {
      return this.contentLocation;
   }

   public int getResponseCode() {
      return this.responseCode;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         CachedHttpRequest that = (CachedHttpRequest)o;
         if (this.responseCode != that.responseCode) {
            return false;
         } else {
            label96: {
               if (this.contentEncoding != null) {
                  if (this.contentEncoding.equals(that.contentEncoding)) {
                     break label96;
                  }
               } else if (that.contentEncoding == null) {
                  break label96;
               }

               return false;
            }

            if (this.contentLocation != null) {
               if (!this.contentLocation.equals(that.contentLocation)) {
                  return false;
               }
            } else if (that.contentLocation != null) {
               return false;
            }

            if (this.contentType != null) {
               if (!this.contentType.equals(that.contentType)) {
                  return false;
               }
            } else if (that.contentType != null) {
               return false;
            }

            label75: {
               if (this.etag != null) {
                  if (this.etag.equals(that.etag)) {
                     break label75;
                  }
               } else if (that.etag == null) {
                  break label75;
               }

               return false;
            }

            if (this.language != null) {
               if (!this.language.equals(that.language)) {
                  return false;
               }
            } else if (that.language != null) {
               return false;
            }

            if (this.lastModified != null) {
               if (!this.lastModified.equals(that.lastModified)) {
                  return false;
               }
            } else if (that.lastModified != null) {
               return false;
            }

            if (this.path != null) {
               if (!this.path.equals(that.path)) {
                  return false;
               }
            } else if (that.path != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.path != null ? this.path.hashCode() : 0;
      result = 31 * result + (this.etag != null ? this.etag.hashCode() : 0);
      result = 31 * result + (this.contentEncoding != null ? this.contentEncoding.hashCode() : 0);
      result = 31 * result + (this.contentLocation != null ? this.contentLocation.hashCode() : 0);
      result = 31 * result + (this.language != null ? this.language.hashCode() : 0);
      result = 31 * result + (this.contentType != null ? this.contentType.hashCode() : 0);
      result = 31 * result + (this.lastModified != null ? this.lastModified.hashCode() : 0);
      result = 31 * result + this.responseCode;
      return result;
   }
}
