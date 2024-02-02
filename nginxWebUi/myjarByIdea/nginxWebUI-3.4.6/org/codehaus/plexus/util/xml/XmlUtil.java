package org.codehaus.plexus.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class XmlUtil {
   public static final int DEFAULT_INDENTATION_SIZE = 2;
   public static final String DEFAULT_LINE_SEPARATOR = System.getProperty("line.separator");

   public static boolean isXml(File f) {
      if (f == null) {
         throw new IllegalArgumentException("f could not be null.");
      } else if (!f.isFile()) {
         throw new IllegalArgumentException("The file '" + f.getAbsolutePath() + "' is not a file.");
      } else {
         Reader reader = null;

         boolean var3;
         try {
            reader = ReaderFactory.newXmlReader(f);
            XmlPullParser parser = new MXParser();
            parser.setInput(reader);
            parser.nextToken();
            var3 = true;
            return var3;
         } catch (Exception var7) {
            var3 = false;
         } finally {
            IOUtil.close((Reader)reader);
         }

         return var3;
      }
   }

   public static void prettyFormat(Reader reader, Writer writer) throws IOException {
      prettyFormat((Reader)reader, (Writer)writer, 2, DEFAULT_LINE_SEPARATOR);
   }

   public static void prettyFormat(Reader reader, Writer writer, int indentSize, String lineSeparator) throws IOException {
      if (reader == null) {
         throw new IllegalArgumentException("The reader is null");
      } else if (writer == null) {
         throw new IllegalArgumentException("The writer is null");
      } else {
         if (indentSize < 0) {
            indentSize = 0;
         }

         PrettyPrintXMLWriter xmlWriter = new PrettyPrintXMLWriter(writer);
         xmlWriter.setLineIndenter(StringUtils.repeat(" ", indentSize));
         xmlWriter.setLineSeparator(lineSeparator);
         XmlPullParser parser = new MXParser();

         try {
            parser.setInput(reader);
            prettyFormatInternal(parser, xmlWriter);
         } catch (XmlPullParserException var7) {
            throw new IOException("Unable to parse the XML: " + var7.getMessage());
         }
      }
   }

   public static void prettyFormat(InputStream is, OutputStream os) throws IOException {
      prettyFormat((InputStream)is, (OutputStream)os, 2, DEFAULT_LINE_SEPARATOR);
   }

   public static void prettyFormat(InputStream is, OutputStream os, int indentSize, String lineSeparator) throws IOException {
      if (is == null) {
         throw new IllegalArgumentException("The is is null");
      } else if (os == null) {
         throw new IllegalArgumentException("The os is null");
      } else {
         if (indentSize < 0) {
            indentSize = 0;
         }

         Reader reader = null;
         Writer out = new OutputStreamWriter(os);
         PrettyPrintXMLWriter xmlWriter = new PrettyPrintXMLWriter(out);
         xmlWriter.setLineIndenter(StringUtils.repeat(" ", indentSize));
         xmlWriter.setLineSeparator(lineSeparator);
         XmlPullParser parser = new MXParser();

         try {
            reader = ReaderFactory.newXmlReader(is);
            parser.setInput(reader);
            prettyFormatInternal(parser, xmlWriter);
         } catch (XmlPullParserException var12) {
            throw new IOException("Unable to parse the XML: " + var12.getMessage());
         } finally {
            IOUtil.close((Reader)reader);
            IOUtil.close((Writer)out);
         }

      }
   }

   private static void prettyFormatInternal(XmlPullParser parser, PrettyPrintXMLWriter writer) throws XmlPullParserException, IOException {
      boolean hasTag = false;
      boolean hasComment = false;

      for(int eventType = parser.getEventType(); eventType != 1; eventType = parser.nextToken()) {
         int i;
         if (eventType == 2) {
            hasTag = true;
            if (hasComment) {
               writer.writeText(writer.getLineIndenter());
               hasComment = false;
            }

            writer.startElement(parser.getName());

            for(i = 0; i < parser.getAttributeCount(); ++i) {
               String key = parser.getAttributeName(i);
               String value = parser.getAttributeValue(i);
               writer.addAttribute(key, value);
            }
         } else if (eventType == 4) {
            String text = parser.getText();
            if (!text.trim().equals("")) {
               text = StringUtils.removeDuplicateWhitespace(text);
               writer.writeText(text);
            }
         } else if (eventType == 3) {
            hasTag = false;
            writer.endElement();
         } else if (eventType != 9) {
            if (eventType == 10) {
               writer.writeMarkup("<!DOCTYPE" + parser.getText() + ">");
               writer.endOfLine();
            } else if (eventType == 8) {
               writer.writeMarkup("<?" + parser.getText() + "?>");
               writer.endOfLine();
            } else if (eventType == 5) {
               writer.writeMarkup("<![CDATA[" + parser.getText() + "]]>");
            } else if (eventType == 6) {
               writer.writeMarkup("&" + parser.getName() + ";");
            }
         } else {
            hasComment = true;
            if (!hasTag) {
               writer.writeMarkup(writer.getLineSeparator());

               for(i = 0; i < writer.getDepth(); ++i) {
                  writer.writeMarkup(writer.getLineIndenter());
               }
            }

            writer.writeMarkup("<!--" + parser.getText().trim() + " -->");
            if (!hasTag) {
               writer.writeMarkup(writer.getLineSeparator());

               for(i = 0; i < writer.getDepth() - 1; ++i) {
                  writer.writeMarkup(writer.getLineIndenter());
               }
            }
         }
      }

   }
}
