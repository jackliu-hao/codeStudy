package org.apache.http.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import org.apache.http.util.Args;

public class SerializableEntity extends AbstractHttpEntity {
   private byte[] objSer;
   private Serializable objRef;

   public SerializableEntity(Serializable ser, boolean bufferize) throws IOException {
      Args.notNull(ser, "Source object");
      if (bufferize) {
         this.createBytes(ser);
      } else {
         this.objRef = ser;
      }

   }

   public SerializableEntity(Serializable serializable) {
      Args.notNull(serializable, "Source object");
      this.objRef = serializable;
   }

   private void createBytes(Serializable ser) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(baos);
      out.writeObject(ser);
      out.flush();
      this.objSer = baos.toByteArray();
   }

   public InputStream getContent() throws IOException, IllegalStateException {
      if (this.objSer == null) {
         this.createBytes(this.objRef);
      }

      return new ByteArrayInputStream(this.objSer);
   }

   public long getContentLength() {
      return this.objSer == null ? -1L : (long)this.objSer.length;
   }

   public boolean isRepeatable() {
      return true;
   }

   public boolean isStreaming() {
      return this.objSer == null;
   }

   public void writeTo(OutputStream outStream) throws IOException {
      Args.notNull(outStream, "Output stream");
      if (this.objSer == null) {
         ObjectOutputStream out = new ObjectOutputStream(outStream);
         out.writeObject(this.objRef);
         out.flush();
      } else {
         outStream.write(this.objSer);
         outStream.flush();
      }

   }
}
