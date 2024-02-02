package org.codehaus.plexus.util.xml;

import java.io.InputStream;

public class XmlStreamReaderException extends XmlReaderException {
   public XmlStreamReaderException(String msg, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is) {
      super(msg, bomEnc, xmlGuessEnc, xmlEnc, is);
   }

   public XmlStreamReaderException(String msg, String ctMime, String ctEnc, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is) {
      super(msg, ctMime, ctEnc, bomEnc, xmlGuessEnc, xmlEnc, is);
   }
}
