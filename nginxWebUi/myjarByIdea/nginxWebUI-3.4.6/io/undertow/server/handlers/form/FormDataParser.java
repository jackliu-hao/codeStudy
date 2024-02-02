package io.undertow.server.handlers.form;

import io.undertow.server.HttpHandler;
import io.undertow.util.AttachmentKey;
import java.io.Closeable;
import java.io.IOException;

public interface FormDataParser extends Closeable {
   AttachmentKey<FormData> FORM_DATA = AttachmentKey.create(FormData.class);

   void parse(HttpHandler var1) throws Exception;

   FormData parseBlocking() throws IOException;

   void close() throws IOException;

   void setCharacterEncoding(String var1);
}
