package org.codehaus.plexus.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class XmlStreamReader extends XmlReader {
   public XmlStreamReader(File file) throws IOException {
      super(file);
   }

   public XmlStreamReader(InputStream is) throws IOException {
      super(is);
   }

   public XmlStreamReader(InputStream is, boolean lenient) throws IOException, XmlStreamReaderException {
      super(is, lenient);
   }

   public XmlStreamReader(URL url) throws IOException {
      super(url);
   }

   public XmlStreamReader(URLConnection conn) throws IOException {
      super(conn);
   }

   public XmlStreamReader(InputStream is, String httpContentType) throws IOException {
      super(is, httpContentType);
   }

   public XmlStreamReader(InputStream is, String httpContentType, boolean lenient, String defaultEncoding) throws IOException, XmlStreamReaderException {
      super(is, httpContentType, lenient, defaultEncoding);
   }

   public XmlStreamReader(InputStream is, String httpContentType, boolean lenient) throws IOException, XmlStreamReaderException {
      super(is, httpContentType, lenient);
   }
}
