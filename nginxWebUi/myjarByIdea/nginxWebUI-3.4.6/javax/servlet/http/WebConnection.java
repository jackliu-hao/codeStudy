package javax.servlet.http;

import java.io.IOException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

public interface WebConnection extends AutoCloseable {
   ServletInputStream getInputStream() throws IOException;

   ServletOutputStream getOutputStream() throws IOException;
}
