package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DataSource {
   InputStream getInputStream() throws IOException;

   OutputStream getOutputStream() throws IOException;

   String getContentType();

   String getName();
}
