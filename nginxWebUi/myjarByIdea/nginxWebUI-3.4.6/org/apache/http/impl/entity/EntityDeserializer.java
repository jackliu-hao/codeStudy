package org.apache.http.impl.entity;

import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.io.ChunkedInputStream;
import org.apache.http.impl.io.ContentLengthInputStream;
import org.apache.http.impl.io.IdentityInputStream;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class EntityDeserializer {
   private final ContentLengthStrategy lenStrategy;

   public EntityDeserializer(ContentLengthStrategy lenStrategy) {
      this.lenStrategy = (ContentLengthStrategy)Args.notNull(lenStrategy, "Content length strategy");
   }

   protected BasicHttpEntity doDeserialize(SessionInputBuffer inBuffer, HttpMessage message) throws HttpException, IOException {
      BasicHttpEntity entity = new BasicHttpEntity();
      long len = this.lenStrategy.determineLength(message);
      if (len == -2L) {
         entity.setChunked(true);
         entity.setContentLength(-1L);
         entity.setContent(new ChunkedInputStream(inBuffer));
      } else if (len == -1L) {
         entity.setChunked(false);
         entity.setContentLength(-1L);
         entity.setContent(new IdentityInputStream(inBuffer));
      } else {
         entity.setChunked(false);
         entity.setContentLength(len);
         entity.setContent(new ContentLengthInputStream(inBuffer, len));
      }

      Header contentTypeHeader = message.getFirstHeader("Content-Type");
      if (contentTypeHeader != null) {
         entity.setContentType(contentTypeHeader);
      }

      Header contentEncodingHeader = message.getFirstHeader("Content-Encoding");
      if (contentEncodingHeader != null) {
         entity.setContentEncoding(contentEncodingHeader);
      }

      return entity;
   }

   public HttpEntity deserialize(SessionInputBuffer inBuffer, HttpMessage message) throws HttpException, IOException {
      Args.notNull(inBuffer, "Session input buffer");
      Args.notNull(message, "HTTP message");
      return this.doDeserialize(inBuffer, message);
   }
}
