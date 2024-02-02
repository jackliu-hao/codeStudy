package javax.mail;

import java.io.IOException;
import java.io.InputStream;

interface StreamLoader {
   void load(InputStream var1) throws IOException;
}
