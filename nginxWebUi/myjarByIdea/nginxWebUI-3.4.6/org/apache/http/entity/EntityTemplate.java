package org.apache.http.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.util.Args;

public class EntityTemplate extends AbstractHttpEntity {
   private final ContentProducer contentproducer;

   public EntityTemplate(ContentProducer contentproducer) {
      this.contentproducer = (ContentProducer)Args.notNull(contentproducer, "Content producer");
   }

   public long getContentLength() {
      return -1L;
   }

   public InputStream getContent() throws IOException {
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      this.writeTo(buf);
      return new ByteArrayInputStream(buf.toByteArray());
   }

   public boolean isRepeatable() {
      return true;
   }

   public void writeTo(OutputStream outStream) throws IOException {
      Args.notNull(outStream, "Output stream");
      this.contentproducer.writeTo(outStream);
   }

   public boolean isStreaming() {
      return false;
   }
}
