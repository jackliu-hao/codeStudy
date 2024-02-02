package org.codehaus.plexus.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class Xpp3DomBuilder {
   private static final boolean DEFAULT_TRIM = true;

   public static Xpp3Dom build(Reader reader) throws XmlPullParserException, IOException {
      return build(reader, true);
   }

   public static Xpp3Dom build(InputStream is, String encoding) throws XmlPullParserException, IOException {
      return build(is, encoding, true);
   }

   public static Xpp3Dom build(InputStream is, String encoding, boolean trim) throws XmlPullParserException, IOException {
      XmlPullParser parser = new MXParser();
      parser.setInput(is, encoding);

      Xpp3Dom var4;
      try {
         var4 = build((XmlPullParser)parser, trim);
      } finally {
         IOUtil.close(is);
      }

      return var4;
   }

   public static Xpp3Dom build(Reader reader, boolean trim) throws XmlPullParserException, IOException {
      XmlPullParser parser = new MXParser();
      parser.setInput(reader);

      Xpp3Dom var3;
      try {
         var3 = build((XmlPullParser)parser, trim);
      } finally {
         IOUtil.close(reader);
      }

      return var3;
   }

   public static Xpp3Dom build(XmlPullParser parser) throws XmlPullParserException, IOException {
      return build(parser, true);
   }

   public static Xpp3Dom build(XmlPullParser parser, boolean trim) throws XmlPullParserException, IOException {
      List elements = new ArrayList();
      List values = new ArrayList();

      for(int eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
         Xpp3Dom finishedConfiguration;
         if (eventType == 2) {
            String rawName = parser.getName();
            finishedConfiguration = new Xpp3Dom(rawName);
            int depth = elements.size();
            if (depth > 0) {
               Xpp3Dom parent = (Xpp3Dom)elements.get(depth - 1);
               parent.addChild(finishedConfiguration);
            }

            elements.add(finishedConfiguration);
            if (parser.isEmptyElementTag()) {
               values.add((Object)null);
            } else {
               values.add(new StringBuffer());
            }

            int attributesSize = parser.getAttributeCount();

            for(int i = 0; i < attributesSize; ++i) {
               String name = parser.getAttributeName(i);
               String value = parser.getAttributeValue(i);
               finishedConfiguration.setAttribute(name, value);
            }
         } else {
            int depth;
            if (eventType == 4) {
               depth = values.size() - 1;
               StringBuffer valueBuffer = (StringBuffer)values.get(depth);
               String text = parser.getText();
               if (trim) {
                  text = text.trim();
               }

               valueBuffer.append(text);
            } else if (eventType == 3) {
               depth = elements.size() - 1;
               finishedConfiguration = (Xpp3Dom)elements.remove(depth);
               Object accumulatedValue = values.remove(depth);
               if (finishedConfiguration.getChildCount() == 0) {
                  if (accumulatedValue == null) {
                     finishedConfiguration.setValue((String)null);
                  } else {
                     finishedConfiguration.setValue(accumulatedValue.toString());
                  }
               }

               if (depth == 0) {
                  return finishedConfiguration;
               }
            }
         }
      }

      throw new IllegalStateException("End of document found before returning to 0 depth");
   }
}
