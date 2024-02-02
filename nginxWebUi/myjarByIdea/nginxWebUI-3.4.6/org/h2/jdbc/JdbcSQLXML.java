package org.h2.jdbc;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public final class JdbcSQLXML extends JdbcLob implements SQLXML {
   private static final Map<String, Boolean> secureFeatureMap = new HashMap();
   private static final EntityResolver NOOP_ENTITY_RESOLVER = (var0, var1) -> {
      return new InputSource(new StringReader(""));
   };
   private static final URIResolver NOOP_URI_RESOLVER = (var0, var1) -> {
      return new StreamSource(new StringReader(""));
   };
   private DOMResult domResult;
   private Closeable closable;

   public JdbcSQLXML(JdbcConnection var1, Value var2, JdbcLob.State var3, int var4) {
      super(var1, var2, var3, 17, var4);
   }

   void checkReadable() throws SQLException, IOException {
      this.checkClosed();
      if (this.state == JdbcLob.State.SET_CALLED) {
         if (this.domResult != null) {
            Node var1 = this.domResult.getNode();
            this.domResult = null;
            TransformerFactory var2 = TransformerFactory.newInstance();

            try {
               Transformer var3 = var2.newTransformer();
               DOMSource var4 = new DOMSource(var1);
               StringWriter var5 = new StringWriter();
               StreamResult var6 = new StreamResult(var5);
               var3.transform(var4, var6);
               this.completeWrite(this.conn.createClob(new StringReader(var5.toString()), -1L));
            } catch (Exception var7) {
               throw this.logAndConvert(var7);
            }
         } else if (this.closable != null) {
            this.closable.close();
            this.closable = null;
         } else {
            throw DbException.getUnsupportedException("Stream setter is not yet closed.");
         }
      }
   }

   public InputStream getBinaryStream() throws SQLException {
      return super.getBinaryStream();
   }

   public Reader getCharacterStream() throws SQLException {
      return super.getCharacterStream();
   }

   public <T extends Source> T getSource(Class<T> var1) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getSource(" + (var1 != null ? var1.getSimpleName() + ".class" : "null") + ')');
         }

         this.checkReadable();
         Iterator var3;
         Map.Entry var4;
         if (var1 != null && var1 != DOMSource.class) {
            if (var1 != SAXSource.class) {
               if (var1 == StAXSource.class) {
                  XMLInputFactory var11 = XMLInputFactory.newInstance();
                  var11.setProperty("javax.xml.stream.supportDTD", false);
                  var11.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
                  var11.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
                  return new StAXSource(var11.createXMLStreamReader(this.value.getInputStream()));
               } else if (var1 == StreamSource.class) {
                  TransformerFactory var10 = TransformerFactory.newInstance();
                  var10.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
                  var10.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
                  var10.setURIResolver(NOOP_URI_RESOLVER);
                  var10.newTransformer().transform(new StreamSource(this.value.getInputStream()), new SAXResult(new DefaultHandler()));
                  return new StreamSource(this.value.getInputStream());
               } else {
                  throw this.unsupported(var1.getName());
               }
            } else {
               SAXParserFactory var9 = SAXParserFactory.newInstance();
               var3 = secureFeatureMap.entrySet().iterator();

               while(var3.hasNext()) {
                  var4 = (Map.Entry)var3.next();

                  try {
                     var9.setFeature((String)var4.getKey(), (Boolean)var4.getValue());
                  } catch (Exception var6) {
                  }
               }

               XMLReader var13 = var9.newSAXParser().getXMLReader();
               var13.setEntityResolver(NOOP_ENTITY_RESOLVER);
               return new SAXSource(var13, new InputSource(this.value.getInputStream()));
            }
         } else {
            DocumentBuilderFactory var2 = DocumentBuilderFactory.newInstance();
            var3 = secureFeatureMap.entrySet().iterator();

            while(var3.hasNext()) {
               var4 = (Map.Entry)var3.next();

               try {
                  var2.setFeature((String)var4.getKey(), (Boolean)var4.getValue());
               } catch (Exception var7) {
               }
            }

            var2.setXIncludeAware(false);
            var2.setExpandEntityReferences(false);
            var2.setAttribute("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
            DocumentBuilder var12 = var2.newDocumentBuilder();
            var12.setEntityResolver(NOOP_ENTITY_RESOLVER);
            return new DOMSource(var12.parse(new InputSource(this.value.getInputStream())));
         }
      } catch (Exception var8) {
         throw this.logAndConvert(var8);
      }
   }

   public String getString() throws SQLException {
      try {
         this.debugCodeCall("getString");
         this.checkReadable();
         return this.value.getString();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public OutputStream setBinaryStream() throws SQLException {
      try {
         this.debugCodeCall("setBinaryStream");
         this.checkEditable();
         this.state = JdbcLob.State.SET_CALLED;
         return new BufferedOutputStream(this.setClobOutputStreamImpl());
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public Writer setCharacterStream() throws SQLException {
      try {
         this.debugCodeCall("setCharacterStream");
         this.checkEditable();
         this.state = JdbcLob.State.SET_CALLED;
         return this.setCharacterStreamImpl();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public <T extends Result> T setResult(Class<T> var1) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setResult(" + (var1 != null ? var1.getSimpleName() + ".class" : "null") + ')');
         }

         this.checkEditable();
         if (var1 != null && var1 != DOMResult.class) {
            if (var1 == SAXResult.class) {
               SAXTransformerFactory var8 = (SAXTransformerFactory)TransformerFactory.newInstance();
               TransformerHandler var10 = var8.newTransformerHandler();
               Writer var11 = this.setCharacterStreamImpl();
               var10.setResult(new StreamResult(var11));
               SAXResult var5 = new SAXResult(var10);
               this.closable = var11;
               this.state = JdbcLob.State.SET_CALLED;
               return var5;
            } else if (var1 == StAXResult.class) {
               XMLOutputFactory var7 = XMLOutputFactory.newInstance();
               Writer var9 = this.setCharacterStreamImpl();
               StAXResult var4 = new StAXResult(var7.createXMLStreamWriter(var9));
               this.closable = var9;
               this.state = JdbcLob.State.SET_CALLED;
               return var4;
            } else if (StreamResult.class.equals(var1)) {
               Writer var2 = this.setCharacterStreamImpl();
               StreamResult var3 = new StreamResult(var2);
               this.closable = var2;
               this.state = JdbcLob.State.SET_CALLED;
               return var3;
            } else {
               throw this.unsupported(var1.getName());
            }
         } else {
            this.domResult = new DOMResult();
            this.state = JdbcLob.State.SET_CALLED;
            return this.domResult;
         }
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void setString(String var1) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCodeCall("getSource", var1);
         }

         this.checkEditable();
         this.completeWrite(this.conn.createClob(new StringReader(var1), -1L));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   static {
      secureFeatureMap.put("http://javax.xml.XMLConstants/feature/secure-processing", true);
      secureFeatureMap.put("http://apache.org/xml/features/disallow-doctype-decl", true);
      secureFeatureMap.put("http://xml.org/sax/features/external-general-entities", false);
      secureFeatureMap.put("http://xml.org/sax/features/external-parameter-entities", false);
      secureFeatureMap.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
   }
}
