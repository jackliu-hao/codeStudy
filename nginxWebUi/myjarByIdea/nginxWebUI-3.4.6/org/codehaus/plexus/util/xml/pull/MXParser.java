package org.codehaus.plexus.util.xml.pull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import org.codehaus.plexus.util.ReaderFactory;

public class MXParser implements XmlPullParser {
   protected static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
   protected static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
   protected static final String FEATURE_XML_ROUNDTRIP = "http://xmlpull.org/v1/doc/features.html#xml-roundtrip";
   protected static final String FEATURE_NAMES_INTERNED = "http://xmlpull.org/v1/doc/features.html#names-interned";
   protected static final String PROPERTY_XMLDECL_VERSION = "http://xmlpull.org/v1/doc/properties.html#xmldecl-version";
   protected static final String PROPERTY_XMLDECL_STANDALONE = "http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone";
   protected static final String PROPERTY_XMLDECL_CONTENT = "http://xmlpull.org/v1/doc/properties.html#xmldecl-content";
   protected static final String PROPERTY_LOCATION = "http://xmlpull.org/v1/doc/properties.html#location";
   protected boolean allStringsInterned;
   private static final boolean TRACE_SIZING = false;
   protected boolean processNamespaces;
   protected boolean roundtripSupported;
   protected String location;
   protected int lineNumber;
   protected int columnNumber;
   protected boolean seenRoot;
   protected boolean reachedEnd;
   protected int eventType;
   protected boolean emptyElementTag;
   protected int depth;
   protected char[][] elRawName;
   protected int[] elRawNameEnd;
   protected int[] elRawNameLine;
   protected String[] elName;
   protected String[] elPrefix;
   protected String[] elUri;
   protected int[] elNamespaceCount;
   protected int attributeCount;
   protected String[] attributeName;
   protected int[] attributeNameHash;
   protected String[] attributePrefix;
   protected String[] attributeUri;
   protected String[] attributeValue;
   protected int namespaceEnd;
   protected String[] namespacePrefix;
   protected int[] namespacePrefixHash;
   protected String[] namespaceUri;
   protected int entityEnd;
   protected String[] entityName;
   protected char[][] entityNameBuf;
   protected String[] entityReplacement;
   protected char[][] entityReplacementBuf;
   protected int[] entityNameHash;
   protected static final int READ_CHUNK_SIZE = 8192;
   protected Reader reader;
   protected String inputEncoding;
   protected int bufLoadFactor = 95;
   protected char[] buf = new char[Runtime.getRuntime().freeMemory() > 1000000L ? 8192 : 256];
   protected int bufSoftLimit;
   protected boolean preventBufferCompaction;
   protected int bufAbsoluteStart;
   protected int bufStart;
   protected int bufEnd;
   protected int pos;
   protected int posStart;
   protected int posEnd;
   protected char[] pc;
   protected int pcStart;
   protected int pcEnd;
   protected boolean usePC;
   protected boolean seenStartTag;
   protected boolean seenEndTag;
   protected boolean pastEndTag;
   protected boolean seenAmpersand;
   protected boolean seenMarkup;
   protected boolean seenDocdecl;
   protected boolean tokenize;
   protected String text;
   protected String entityRefName;
   protected String xmlDeclVersion;
   protected Boolean xmlDeclStandalone;
   protected String xmlDeclContent;
   protected char[] charRefOneCharBuf;
   protected static final char[] VERSION = "version".toCharArray();
   protected static final char[] NCODING = "ncoding".toCharArray();
   protected static final char[] TANDALONE = "tandalone".toCharArray();
   protected static final char[] YES = "yes".toCharArray();
   protected static final char[] NO = "no".toCharArray();
   protected static final int LOOKUP_MAX = 1024;
   protected static final char LOOKUP_MAX_CHAR = 'Ѐ';
   protected static boolean[] lookupNameStartChar = new boolean[1024];
   protected static boolean[] lookupNameChar = new boolean[1024];
   private static final char MIN_HIGH_SURROGATE = '\ud800';
   private static final char MAX_HIGH_SURROGATE = '\udbff';
   private static final int MIN_CODE_POINT = 0;
   private static final int MAX_CODE_POINT = 1114111;
   private static final int MIN_SUPPLEMENTARY_CODE_POINT = 65536;

   protected void resetStringCache() {
   }

   protected String newString(char[] cbuf, int off, int len) {
      return new String(cbuf, off, len);
   }

   protected String newStringIntern(char[] cbuf, int off, int len) {
      return (new String(cbuf, off, len)).intern();
   }

   protected void ensureElementsCapacity() {
      int elStackSize = this.elName != null ? this.elName.length : 0;
      if (this.depth + 1 >= elStackSize) {
         int newSize = (this.depth >= 7 ? 2 * this.depth : 8) + 2;
         boolean needsCopying = elStackSize > 0;
         String[] arr = null;
         arr = new String[newSize];
         if (needsCopying) {
            System.arraycopy(this.elName, 0, arr, 0, elStackSize);
         }

         this.elName = arr;
         arr = new String[newSize];
         if (needsCopying) {
            System.arraycopy(this.elPrefix, 0, arr, 0, elStackSize);
         }

         this.elPrefix = arr;
         arr = new String[newSize];
         if (needsCopying) {
            System.arraycopy(this.elUri, 0, arr, 0, elStackSize);
         }

         this.elUri = arr;
         int[] iarr = new int[newSize];
         if (needsCopying) {
            System.arraycopy(this.elNamespaceCount, 0, iarr, 0, elStackSize);
         } else {
            iarr[0] = 0;
         }

         this.elNamespaceCount = iarr;
         iarr = new int[newSize];
         if (needsCopying) {
            System.arraycopy(this.elRawNameEnd, 0, iarr, 0, elStackSize);
         }

         this.elRawNameEnd = iarr;
         iarr = new int[newSize];
         if (needsCopying) {
            System.arraycopy(this.elRawNameLine, 0, iarr, 0, elStackSize);
         }

         this.elRawNameLine = iarr;
         char[][] carr = new char[newSize][];
         if (needsCopying) {
            System.arraycopy(this.elRawName, 0, carr, 0, elStackSize);
         }

         this.elRawName = carr;
      }

   }

   protected void ensureAttributesCapacity(int size) {
      int attrPosSize = this.attributeName != null ? this.attributeName.length : 0;
      if (size >= attrPosSize) {
         int newSize = size > 7 ? 2 * size : 8;
         boolean needsCopying = attrPosSize > 0;
         String[] arr = null;
         arr = new String[newSize];
         if (needsCopying) {
            System.arraycopy(this.attributeName, 0, arr, 0, attrPosSize);
         }

         this.attributeName = arr;
         arr = new String[newSize];
         if (needsCopying) {
            System.arraycopy(this.attributePrefix, 0, arr, 0, attrPosSize);
         }

         this.attributePrefix = arr;
         arr = new String[newSize];
         if (needsCopying) {
            System.arraycopy(this.attributeUri, 0, arr, 0, attrPosSize);
         }

         this.attributeUri = arr;
         arr = new String[newSize];
         if (needsCopying) {
            System.arraycopy(this.attributeValue, 0, arr, 0, attrPosSize);
         }

         this.attributeValue = arr;
         if (!this.allStringsInterned) {
            int[] iarr = new int[newSize];
            if (needsCopying) {
               System.arraycopy(this.attributeNameHash, 0, iarr, 0, attrPosSize);
            }

            this.attributeNameHash = iarr;
         }

         arr = null;
      }

   }

   protected void ensureNamespacesCapacity(int size) {
      int namespaceSize = this.namespacePrefix != null ? this.namespacePrefix.length : 0;
      if (size >= namespaceSize) {
         int newSize = size > 7 ? 2 * size : 8;
         String[] newNamespacePrefix = new String[newSize];
         String[] newNamespaceUri = new String[newSize];
         if (this.namespacePrefix != null) {
            System.arraycopy(this.namespacePrefix, 0, newNamespacePrefix, 0, this.namespaceEnd);
            System.arraycopy(this.namespaceUri, 0, newNamespaceUri, 0, this.namespaceEnd);
         }

         this.namespacePrefix = newNamespacePrefix;
         this.namespaceUri = newNamespaceUri;
         if (!this.allStringsInterned) {
            int[] newNamespacePrefixHash = new int[newSize];
            if (this.namespacePrefixHash != null) {
               System.arraycopy(this.namespacePrefixHash, 0, newNamespacePrefixHash, 0, this.namespaceEnd);
            }

            this.namespacePrefixHash = newNamespacePrefixHash;
         }
      }

   }

   protected static final int fastHash(char[] ch, int off, int len) {
      if (len == 0) {
         return 0;
      } else {
         int hash = ch[off];
         hash = (hash << 7) + ch[off + len - 1];
         if (len > 16) {
            hash = (hash << 7) + ch[off + len / 4];
         }

         if (len > 8) {
            hash = (hash << 7) + ch[off + len / 2];
         }

         return hash;
      }
   }

   protected void ensureEntityCapacity() {
      int entitySize = this.entityReplacementBuf != null ? this.entityReplacementBuf.length : 0;
      if (this.entityEnd >= entitySize) {
         int newSize = this.entityEnd > 7 ? 2 * this.entityEnd : 8;
         String[] newEntityName = new String[newSize];
         char[][] newEntityNameBuf = new char[newSize][];
         String[] newEntityReplacement = new String[newSize];
         char[][] newEntityReplacementBuf = new char[newSize][];
         if (this.entityName != null) {
            System.arraycopy(this.entityName, 0, newEntityName, 0, this.entityEnd);
            System.arraycopy(this.entityNameBuf, 0, newEntityNameBuf, 0, this.entityEnd);
            System.arraycopy(this.entityReplacement, 0, newEntityReplacement, 0, this.entityEnd);
            System.arraycopy(this.entityReplacementBuf, 0, newEntityReplacementBuf, 0, this.entityEnd);
         }

         this.entityName = newEntityName;
         this.entityNameBuf = newEntityNameBuf;
         this.entityReplacement = newEntityReplacement;
         this.entityReplacementBuf = newEntityReplacementBuf;
         if (!this.allStringsInterned) {
            int[] newEntityNameHash = new int[newSize];
            if (this.entityNameHash != null) {
               System.arraycopy(this.entityNameHash, 0, newEntityNameHash, 0, this.entityEnd);
            }

            this.entityNameHash = newEntityNameHash;
         }
      }

   }

   protected void reset() {
      this.location = null;
      this.lineNumber = 1;
      this.columnNumber = 0;
      this.seenRoot = false;
      this.reachedEnd = false;
      this.eventType = 0;
      this.emptyElementTag = false;
      this.depth = 0;
      this.attributeCount = 0;
      this.namespaceEnd = 0;
      this.entityEnd = 0;
      this.reader = null;
      this.inputEncoding = null;
      this.preventBufferCompaction = false;
      this.bufAbsoluteStart = 0;
      this.bufEnd = this.bufStart = 0;
      this.pos = this.posStart = this.posEnd = 0;
      this.pcEnd = this.pcStart = 0;
      this.usePC = false;
      this.seenStartTag = false;
      this.seenEndTag = false;
      this.pastEndTag = false;
      this.seenAmpersand = false;
      this.seenMarkup = false;
      this.seenDocdecl = false;
      this.xmlDeclVersion = null;
      this.xmlDeclStandalone = null;
      this.xmlDeclContent = null;
      this.resetStringCache();
   }

   public MXParser() {
      this.bufSoftLimit = this.bufLoadFactor * this.buf.length / 100;
      this.pc = new char[Runtime.getRuntime().freeMemory() > 1000000L ? 8192 : 64];
      this.charRefOneCharBuf = new char[1];
   }

   public void setFeature(String name, boolean state) throws XmlPullParserException {
      if (name == null) {
         throw new IllegalArgumentException("feature name should not be null");
      } else {
         if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name)) {
            if (this.eventType != 0) {
               throw new XmlPullParserException("namespace processing feature can only be changed before parsing", this, (Throwable)null);
            }

            this.processNamespaces = state;
         } else if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name)) {
            if (state) {
               throw new XmlPullParserException("interning names in this implementation is not supported");
            }
         } else if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(name)) {
            if (state) {
               throw new XmlPullParserException("processing DOCDECL is not supported");
            }
         } else {
            if (!"http://xmlpull.org/v1/doc/features.html#xml-roundtrip".equals(name)) {
               throw new XmlPullParserException("unsupporte feature " + name);
            }

            this.roundtripSupported = state;
         }

      }
   }

   public boolean getFeature(String name) {
      if (name == null) {
         throw new IllegalArgumentException("feature name should not be nulll");
      } else if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name)) {
         return this.processNamespaces;
      } else if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name)) {
         return false;
      } else if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(name)) {
         return false;
      } else {
         return "http://xmlpull.org/v1/doc/features.html#xml-roundtrip".equals(name) ? this.roundtripSupported : false;
      }
   }

   public void setProperty(String name, Object value) throws XmlPullParserException {
      if ("http://xmlpull.org/v1/doc/properties.html#location".equals(name)) {
         this.location = (String)value;
      } else {
         throw new XmlPullParserException("unsupported property: '" + name + "'");
      }
   }

   public Object getProperty(String name) {
      if (name == null) {
         throw new IllegalArgumentException("property name should not be nulll");
      } else if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-version".equals(name)) {
         return this.xmlDeclVersion;
      } else if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone".equals(name)) {
         return this.xmlDeclStandalone;
      } else if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-content".equals(name)) {
         return this.xmlDeclContent;
      } else {
         return "http://xmlpull.org/v1/doc/properties.html#location".equals(name) ? this.location : null;
      }
   }

   public void setInput(Reader in) throws XmlPullParserException {
      this.reset();
      this.reader = in;
   }

   public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
      if (inputStream == null) {
         throw new IllegalArgumentException("input stream can not be null");
      } else {
         Object reader;
         try {
            if (inputEncoding != null) {
               reader = ReaderFactory.newReader(inputStream, inputEncoding);
            } else {
               reader = ReaderFactory.newXmlReader(inputStream);
            }
         } catch (UnsupportedEncodingException var5) {
            throw new XmlPullParserException("could not create reader for encoding " + inputEncoding + " : " + var5, this, var5);
         } catch (IOException var6) {
            throw new XmlPullParserException("could not create reader : " + var6, this, var6);
         }

         this.setInput((Reader)reader);
         this.inputEncoding = inputEncoding;
      }
   }

   public String getInputEncoding() {
      return this.inputEncoding;
   }

   public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
      if (!replacementText.startsWith("&#") && this.entityName != null && replacementText.length() > 1) {
         String tmp = replacementText.substring(1, replacementText.length() - 1);

         for(int i = 0; i < this.entityName.length; ++i) {
            if (this.entityName[i] != null && this.entityName[i].equals(tmp)) {
               replacementText = this.entityReplacement[i];
            }
         }
      }

      this.ensureEntityCapacity();
      this.entityName[this.entityEnd] = this.newString(entityName.toCharArray(), 0, entityName.length());
      this.entityNameBuf[this.entityEnd] = entityName.toCharArray();
      this.entityReplacement[this.entityEnd] = replacementText;
      this.entityReplacementBuf[this.entityEnd] = replacementText.toCharArray();
      if (!this.allStringsInterned) {
         this.entityNameHash[this.entityEnd] = fastHash(this.entityNameBuf[this.entityEnd], 0, this.entityNameBuf[this.entityEnd].length);
      }

      ++this.entityEnd;
   }

   public int getNamespaceCount(int depth) throws XmlPullParserException {
      if (this.processNamespaces && depth != 0) {
         if (depth >= 0 && depth <= this.depth) {
            return this.elNamespaceCount[depth];
         } else {
            throw new IllegalArgumentException("napespace count mayt be for depth 0.." + this.depth + " not " + depth);
         }
      } else {
         return 0;
      }
   }

   public String getNamespacePrefix(int pos) throws XmlPullParserException {
      if (pos < this.namespaceEnd) {
         return this.namespacePrefix[pos];
      } else {
         throw new XmlPullParserException("position " + pos + " exceeded number of available namespaces " + this.namespaceEnd);
      }
   }

   public String getNamespaceUri(int pos) throws XmlPullParserException {
      if (pos < this.namespaceEnd) {
         return this.namespaceUri[pos];
      } else {
         throw new XmlPullParserException("position " + pos + " exceedded number of available namespaces " + this.namespaceEnd);
      }
   }

   public String getNamespace(String prefix) {
      int i;
      if (prefix != null) {
         for(i = this.namespaceEnd - 1; i >= 0; --i) {
            if (prefix.equals(this.namespacePrefix[i])) {
               return this.namespaceUri[i];
            }
         }

         if ("xml".equals(prefix)) {
            return "http://www.w3.org/XML/1998/namespace";
         }

         if ("xmlns".equals(prefix)) {
            return "http://www.w3.org/2000/xmlns/";
         }
      } else {
         for(i = this.namespaceEnd - 1; i >= 0; --i) {
            if (this.namespacePrefix[i] == null) {
               return this.namespaceUri[i];
            }
         }
      }

      return null;
   }

   public int getDepth() {
      return this.depth;
   }

   private static int findFragment(int bufMinPos, char[] b, int start, int end) {
      if (start < bufMinPos) {
         start = bufMinPos;
         if (bufMinPos > end) {
            start = end;
         }

         return start;
      } else {
         if (end - start > 65) {
            start = end - 10;
         }

         int i = start + 1;

         char c;
         do {
            --i;
            if (i <= bufMinPos || end - i > 65) {
               break;
            }

            c = b[i];
         } while(c != '<' || start - i <= 10);

         return i;
      }
   }

   public String getPositionDescription() {
      String fragment = null;
      if (this.posStart <= this.pos) {
         int start = findFragment(0, this.buf, this.posStart, this.pos);
         if (start < this.pos) {
            fragment = new String(this.buf, start, this.pos - start);
         }

         if (this.bufAbsoluteStart > 0 || start > 0) {
            fragment = "..." + fragment;
         }
      }

      return " " + TYPES[this.eventType] + (fragment != null ? " seen " + this.printable(fragment) + "..." : "") + " " + (this.location != null ? this.location : "") + "@" + this.getLineNumber() + ":" + this.getColumnNumber();
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   public int getColumnNumber() {
      return this.columnNumber;
   }

   public boolean isWhitespace() throws XmlPullParserException {
      if (this.eventType != 4 && this.eventType != 5) {
         if (this.eventType == 7) {
            return true;
         } else {
            throw new XmlPullParserException("no content available to check for whitespaces");
         }
      } else {
         int i;
         if (this.usePC) {
            for(i = this.pcStart; i < this.pcEnd; ++i) {
               if (!this.isS(this.pc[i])) {
                  return false;
               }
            }

            return true;
         } else {
            for(i = this.posStart; i < this.posEnd; ++i) {
               if (!this.isS(this.buf[i])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public String getText() {
      if (this.eventType != 0 && this.eventType != 1) {
         if (this.eventType == 6) {
            return this.text;
         } else {
            if (this.text == null) {
               if (this.usePC && this.eventType != 2 && this.eventType != 3) {
                  this.text = new String(this.pc, this.pcStart, this.pcEnd - this.pcStart);
               } else {
                  this.text = new String(this.buf, this.posStart, this.posEnd - this.posStart);
               }
            }

            return this.text;
         }
      } else {
         return null;
      }
   }

   public char[] getTextCharacters(int[] holderForStartAndLength) {
      if (this.eventType == 4) {
         if (this.usePC) {
            holderForStartAndLength[0] = this.pcStart;
            holderForStartAndLength[1] = this.pcEnd - this.pcStart;
            return this.pc;
         } else {
            holderForStartAndLength[0] = this.posStart;
            holderForStartAndLength[1] = this.posEnd - this.posStart;
            return this.buf;
         }
      } else if (this.eventType != 2 && this.eventType != 3 && this.eventType != 5 && this.eventType != 9 && this.eventType != 6 && this.eventType != 8 && this.eventType != 7 && this.eventType != 10) {
         if (this.eventType != 0 && this.eventType != 1) {
            throw new IllegalArgumentException("unknown text eventType: " + this.eventType);
         } else {
            holderForStartAndLength[0] = holderForStartAndLength[1] = -1;
            return null;
         }
      } else {
         holderForStartAndLength[0] = this.posStart;
         holderForStartAndLength[1] = this.posEnd - this.posStart;
         return this.buf;
      }
   }

   public String getNamespace() {
      if (this.eventType == 2) {
         return this.processNamespaces ? this.elUri[this.depth] : "";
      } else if (this.eventType == 3) {
         return this.processNamespaces ? this.elUri[this.depth] : "";
      } else {
         return null;
      }
   }

   public String getName() {
      if (this.eventType == 2) {
         return this.elName[this.depth];
      } else if (this.eventType == 3) {
         return this.elName[this.depth];
      } else if (this.eventType == 6) {
         if (this.entityRefName == null) {
            this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);
         }

         return this.entityRefName;
      } else {
         return null;
      }
   }

   public String getPrefix() {
      if (this.eventType == 2) {
         return this.elPrefix[this.depth];
      } else {
         return this.eventType == 3 ? this.elPrefix[this.depth] : null;
      }
   }

   public boolean isEmptyElementTag() throws XmlPullParserException {
      if (this.eventType != 2) {
         throw new XmlPullParserException("parser must be on START_TAG to check for empty element", this, (Throwable)null);
      } else {
         return this.emptyElementTag;
      }
   }

   public int getAttributeCount() {
      return this.eventType != 2 ? -1 : this.attributeCount;
   }

   public String getAttributeNamespace(int index) {
      if (this.eventType != 2) {
         throw new IndexOutOfBoundsException("only START_TAG can have attributes");
      } else if (!this.processNamespaces) {
         return "";
      } else if (index >= 0 && index < this.attributeCount) {
         return this.attributeUri[index];
      } else {
         throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
      }
   }

   public String getAttributeName(int index) {
      if (this.eventType != 2) {
         throw new IndexOutOfBoundsException("only START_TAG can have attributes");
      } else if (index >= 0 && index < this.attributeCount) {
         return this.attributeName[index];
      } else {
         throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
      }
   }

   public String getAttributePrefix(int index) {
      if (this.eventType != 2) {
         throw new IndexOutOfBoundsException("only START_TAG can have attributes");
      } else if (!this.processNamespaces) {
         return null;
      } else if (index >= 0 && index < this.attributeCount) {
         return this.attributePrefix[index];
      } else {
         throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
      }
   }

   public String getAttributeType(int index) {
      if (this.eventType != 2) {
         throw new IndexOutOfBoundsException("only START_TAG can have attributes");
      } else if (index >= 0 && index < this.attributeCount) {
         return "CDATA";
      } else {
         throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
      }
   }

   public boolean isAttributeDefault(int index) {
      if (this.eventType != 2) {
         throw new IndexOutOfBoundsException("only START_TAG can have attributes");
      } else if (index >= 0 && index < this.attributeCount) {
         return false;
      } else {
         throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
      }
   }

   public String getAttributeValue(int index) {
      if (this.eventType != 2) {
         throw new IndexOutOfBoundsException("only START_TAG can have attributes");
      } else if (index >= 0 && index < this.attributeCount) {
         return this.attributeValue[index];
      } else {
         throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
      }
   }

   public String getAttributeValue(String namespace, String name) {
      if (this.eventType != 2) {
         throw new IndexOutOfBoundsException("only START_TAG can have attributes" + this.getPositionDescription());
      } else if (name == null) {
         throw new IllegalArgumentException("attribute name can not be null");
      } else {
         int i;
         if (this.processNamespaces) {
            if (namespace == null) {
               namespace = "";
            }

            for(i = 0; i < this.attributeCount; ++i) {
               if ((namespace == this.attributeUri[i] || namespace.equals(this.attributeUri[i])) && name.equals(this.attributeName[i])) {
                  return this.attributeValue[i];
               }
            }
         } else {
            if (namespace != null && namespace.length() == 0) {
               namespace = null;
            }

            if (namespace != null) {
               throw new IllegalArgumentException("when namespaces processing is disabled attribute namespace must be null");
            }

            for(i = 0; i < this.attributeCount; ++i) {
               if (name.equals(this.attributeName[i])) {
                  return this.attributeValue[i];
               }
            }
         }

         return null;
      }
   }

   public int getEventType() throws XmlPullParserException {
      return this.eventType;
   }

   public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
      if (!this.processNamespaces && namespace != null) {
         throw new XmlPullParserException("processing namespaces must be enabled on parser (or factory) to have possible namespaces delcared on elements" + " (postion:" + this.getPositionDescription() + ")");
      } else if (type != this.getEventType() || namespace != null && !namespace.equals(this.getNamespace()) || name != null && !name.equals(this.getName())) {
         throw new XmlPullParserException("expected event " + TYPES[type] + (name != null ? " with name '" + name + "'" : "") + (namespace != null && name != null ? " and" : "") + (namespace != null ? " with namespace '" + namespace + "'" : "") + " but got" + (type != this.getEventType() ? " " + TYPES[this.getEventType()] : "") + (name != null && this.getName() != null && !name.equals(this.getName()) ? " name '" + this.getName() + "'" : "") + (namespace != null && name != null && this.getName() != null && !name.equals(this.getName()) && this.getNamespace() != null && !namespace.equals(this.getNamespace()) ? " and" : "") + (namespace != null && this.getNamespace() != null && !namespace.equals(this.getNamespace()) ? " namespace '" + this.getNamespace() + "'" : "") + " (postion:" + this.getPositionDescription() + ")");
      }
   }

   public void skipSubTree() throws XmlPullParserException, IOException {
      this.require(2, (String)null, (String)null);
      int level = 1;

      while(level > 0) {
         int eventType = this.next();
         if (eventType == 3) {
            --level;
         } else if (eventType == 2) {
            ++level;
         }
      }

   }

   public String nextText() throws XmlPullParserException, IOException {
      if (this.getEventType() != 2) {
         throw new XmlPullParserException("parser must be on START_TAG to read next text", this, (Throwable)null);
      } else {
         int eventType = this.next();
         if (eventType == 4) {
            String result = this.getText();
            eventType = this.next();
            if (eventType != 3) {
               throw new XmlPullParserException("TEXT must be immediately followed by END_TAG and not " + TYPES[this.getEventType()], this, (Throwable)null);
            } else {
               return result;
            }
         } else if (eventType == 3) {
            return "";
         } else {
            throw new XmlPullParserException("parser must be on START_TAG or TEXT to read text", this, (Throwable)null);
         }
      }
   }

   public int nextTag() throws XmlPullParserException, IOException {
      this.next();
      if (this.eventType == 4 && this.isWhitespace()) {
         this.next();
      }

      if (this.eventType != 2 && this.eventType != 3) {
         throw new XmlPullParserException("expected START_TAG or END_TAG not " + TYPES[this.getEventType()], this, (Throwable)null);
      } else {
         return this.eventType;
      }
   }

   public int next() throws XmlPullParserException, IOException {
      this.tokenize = false;
      return this.nextImpl();
   }

   public int nextToken() throws XmlPullParserException, IOException {
      this.tokenize = true;
      return this.nextImpl();
   }

   protected int nextImpl() throws XmlPullParserException, IOException {
      this.text = null;
      this.pcEnd = this.pcStart = 0;
      this.usePC = false;
      this.bufStart = this.posEnd;
      if (this.pastEndTag) {
         this.pastEndTag = false;
         --this.depth;
         this.namespaceEnd = this.elNamespaceCount[this.depth];
      }

      if (this.emptyElementTag) {
         this.emptyElementTag = false;
         this.pastEndTag = true;
         return this.eventType = 3;
      } else if (this.depth <= 0) {
         return this.seenRoot ? this.parseEpilog() : this.parseProlog();
      } else if (this.seenStartTag) {
         this.seenStartTag = false;
         return this.eventType = this.parseStartTag();
      } else if (this.seenEndTag) {
         this.seenEndTag = false;
         return this.eventType = this.parseEndTag();
      } else {
         char ch;
         if (this.seenMarkup) {
            this.seenMarkup = false;
            ch = '<';
         } else if (this.seenAmpersand) {
            this.seenAmpersand = false;
            ch = '&';
         } else {
            ch = this.more();
         }

         this.posStart = this.pos - 1;
         boolean hadCharData = false;
         boolean needsMerging = false;

         while(true) {
            while(true) {
               int cdStart;
               int cdEnd;
               if (ch == '<') {
                  if (hadCharData && this.tokenize) {
                     this.seenMarkup = true;
                     return this.eventType = 4;
                  }

                  ch = this.more();
                  if (ch == '/') {
                     if (!this.tokenize && hadCharData) {
                        this.seenEndTag = true;
                        return this.eventType = 4;
                     }

                     return this.eventType = this.parseEndTag();
                  }

                  if (ch == '!') {
                     ch = this.more();
                     if (ch == '-') {
                        this.parseComment();
                        if (this.tokenize) {
                           return this.eventType = 9;
                        }

                        if (!this.usePC && hadCharData) {
                           needsMerging = true;
                           break;
                        }

                        this.posStart = this.pos;
                        break;
                     }

                     if (ch != '[') {
                        throw new XmlPullParserException("unexpected character in markup " + this.printable(ch), this, (Throwable)null);
                     }

                     this.parseCDSect(hadCharData);
                     if (this.tokenize) {
                        return this.eventType = 5;
                     }

                     cdStart = this.posStart;
                     cdEnd = this.posEnd;
                     int cdLen = cdEnd - cdStart;
                     if (cdLen > 0) {
                        hadCharData = true;
                        if (!this.usePC) {
                           needsMerging = true;
                        }
                     }
                     break;
                  }

                  if (ch != '?') {
                     if (this.isNameStartChar(ch)) {
                        if (!this.tokenize && hadCharData) {
                           this.seenStartTag = true;
                           return this.eventType = 4;
                        }

                        return this.eventType = this.parseStartTag();
                     }

                     throw new XmlPullParserException("unexpected character in markup " + this.printable(ch), this, (Throwable)null);
                  }

                  this.parsePI();
                  if (this.tokenize) {
                     return this.eventType = 8;
                  }

                  if (!this.usePC && hadCharData) {
                     needsMerging = true;
                     break;
                  }

                  this.posStart = this.pos;
                  break;
               }

               if (ch == '&') {
                  if (this.tokenize && hadCharData) {
                     this.seenAmpersand = true;
                     return this.eventType = 4;
                  }

                  cdStart = this.posStart + this.bufAbsoluteStart;
                  cdEnd = this.posEnd + this.bufAbsoluteStart;
                  char[] resolvedEntity = this.parseEntityRef();
                  if (this.tokenize) {
                     return this.eventType = 6;
                  }

                  if (resolvedEntity == null) {
                     if (this.entityRefName == null) {
                        this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);
                     }

                     throw new XmlPullParserException("could not resolve entity named '" + this.printable(this.entityRefName) + "'", this, (Throwable)null);
                  }

                  this.posStart = cdStart - this.bufAbsoluteStart;
                  this.posEnd = cdEnd - this.bufAbsoluteStart;
                  if (!this.usePC) {
                     if (hadCharData) {
                        this.joinPC();
                        needsMerging = false;
                     } else {
                        this.usePC = true;
                        this.pcStart = this.pcEnd = 0;
                     }
                  }

                  for(int i = 0; i < resolvedEntity.length; ++i) {
                     if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                     }

                     this.pc[this.pcEnd++] = resolvedEntity[i];
                  }

                  hadCharData = true;
                  break;
               }

               if (needsMerging) {
                  this.joinPC();
                  needsMerging = false;
               }

               hadCharData = true;
               boolean normalizedCR = false;
               boolean normalizeInput = !this.tokenize || !this.roundtripSupported;
               boolean seenBracket = false;
               boolean seenBracketBracket = false;

               do {
                  if (ch == ']') {
                     if (seenBracket) {
                        seenBracketBracket = true;
                     } else {
                        seenBracket = true;
                     }
                  } else {
                     if (seenBracketBracket && ch == '>') {
                        throw new XmlPullParserException("characters ]]> are not allowed in content", this, (Throwable)null);
                     }

                     if (seenBracket) {
                        seenBracket = false;
                        seenBracketBracket = false;
                     }
                  }

                  if (normalizeInput) {
                     if (ch == '\r') {
                        normalizedCR = true;
                        this.posEnd = this.pos - 1;
                        if (!this.usePC) {
                           if (this.posEnd > this.posStart) {
                              this.joinPC();
                           } else {
                              this.usePC = true;
                              this.pcStart = this.pcEnd = 0;
                           }
                        }

                        if (this.pcEnd >= this.pc.length) {
                           this.ensurePC(this.pcEnd);
                        }

                        this.pc[this.pcEnd++] = '\n';
                     } else if (ch == '\n') {
                        if (!normalizedCR && this.usePC) {
                           if (this.pcEnd >= this.pc.length) {
                              this.ensurePC(this.pcEnd);
                           }

                           this.pc[this.pcEnd++] = '\n';
                        }

                        normalizedCR = false;
                     } else {
                        if (this.usePC) {
                           if (this.pcEnd >= this.pc.length) {
                              this.ensurePC(this.pcEnd);
                           }

                           this.pc[this.pcEnd++] = ch;
                        }

                        normalizedCR = false;
                     }
                  }

                  ch = this.more();
               } while(ch != '<' && ch != '&');

               this.posEnd = this.pos - 1;
            }

            ch = this.more();
         }
      }
   }

   protected int parseProlog() throws XmlPullParserException, IOException {
      char ch;
      if (this.seenMarkup) {
         ch = this.buf[this.pos - 1];
      } else {
         ch = this.more();
      }

      if (this.eventType == 0) {
         if (ch == '\ufffe') {
            throw new XmlPullParserException("first character in input was UNICODE noncharacter (0xFFFE)- input requires int swapping", this, (Throwable)null);
         }

         if (ch == '\ufeff') {
            ch = this.more();
         }
      }

      this.seenMarkup = false;
      boolean gotS = false;
      this.posStart = this.pos - 1;
      boolean normalizeIgnorableWS = this.tokenize && !this.roundtripSupported;
      boolean normalizedCR = false;

      while(true) {
         if (ch == '<') {
            if (gotS && this.tokenize) {
               this.posEnd = this.pos - 1;
               this.seenMarkup = true;
               return this.eventType = 7;
            }

            ch = this.more();
            if (ch == '?') {
               boolean isXMLDecl = this.parsePI();
               if (this.tokenize) {
                  if (isXMLDecl) {
                     return this.eventType = 0;
                  }

                  return this.eventType = 8;
               }
            } else {
               if (ch != '!') {
                  if (ch == '/') {
                     throw new XmlPullParserException("expected start tag name and not " + this.printable(ch), this, (Throwable)null);
                  }

                  if (this.isNameStartChar(ch)) {
                     this.seenRoot = true;
                     return this.parseStartTag();
                  }

                  throw new XmlPullParserException("expected start tag name and not " + this.printable(ch), this, (Throwable)null);
               }

               ch = this.more();
               if (ch == 'D') {
                  if (this.seenDocdecl) {
                     throw new XmlPullParserException("only one docdecl allowed in XML document", this, (Throwable)null);
                  }

                  this.seenDocdecl = true;
                  this.parseDocdecl();
                  if (this.tokenize) {
                     return this.eventType = 10;
                  }
               } else {
                  if (ch != '-') {
                     throw new XmlPullParserException("unexpected markup <!" + this.printable(ch), this, (Throwable)null);
                  }

                  this.parseComment();
                  if (this.tokenize) {
                     return this.eventType = 9;
                  }
               }
            }
         } else {
            if (!this.isS(ch)) {
               throw new XmlPullParserException("only whitespace content allowed before start tag and not " + this.printable(ch), this, (Throwable)null);
            }

            gotS = true;
            if (normalizeIgnorableWS) {
               if (ch == '\r') {
                  normalizedCR = true;
                  if (!this.usePC) {
                     this.posEnd = this.pos - 1;
                     if (this.posEnd > this.posStart) {
                        this.joinPC();
                     } else {
                        this.usePC = true;
                        this.pcStart = this.pcEnd = 0;
                     }
                  }

                  if (this.pcEnd >= this.pc.length) {
                     this.ensurePC(this.pcEnd);
                  }

                  this.pc[this.pcEnd++] = '\n';
               } else if (ch == '\n') {
                  if (!normalizedCR && this.usePC) {
                     if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                     }

                     this.pc[this.pcEnd++] = '\n';
                  }

                  normalizedCR = false;
               } else {
                  if (this.usePC) {
                     if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                     }

                     this.pc[this.pcEnd++] = ch;
                  }

                  normalizedCR = false;
               }
            }
         }

         ch = this.more();
      }
   }

   protected int parseEpilog() throws XmlPullParserException, IOException {
      if (this.eventType == 1) {
         throw new XmlPullParserException("already reached end of XML input", this, (Throwable)null);
      } else if (this.reachedEnd) {
         return this.eventType = 1;
      } else {
         boolean gotS = false;
         boolean normalizeIgnorableWS = this.tokenize && !this.roundtripSupported;
         boolean normalizedCR = false;

         try {
            char ch;
            if (this.seenMarkup) {
               ch = this.buf[this.pos - 1];
            } else {
               ch = this.more();
            }

            this.seenMarkup = false;
            this.posStart = this.pos - 1;
            if (!this.reachedEnd) {
               do {
                  if (ch == '<') {
                     if (gotS && this.tokenize) {
                        this.posEnd = this.pos - 1;
                        this.seenMarkup = true;
                        return this.eventType = 7;
                     }

                     ch = this.more();
                     if (this.reachedEnd) {
                        break;
                     }

                     if (ch == '?') {
                        this.parsePI();
                        if (this.tokenize) {
                           return this.eventType = 8;
                        }
                     } else {
                        if (ch != '!') {
                           if (ch == '/') {
                              throw new XmlPullParserException("end tag not allowed in epilog but got " + this.printable(ch), this, (Throwable)null);
                           }

                           if (this.isNameStartChar(ch)) {
                              throw new XmlPullParserException("start tag not allowed in epilog but got " + this.printable(ch), this, (Throwable)null);
                           }

                           throw new XmlPullParserException("in epilog expected ignorable content and not " + this.printable(ch), this, (Throwable)null);
                        }

                        ch = this.more();
                        if (this.reachedEnd) {
                           break;
                        }

                        if (ch == 'D') {
                           this.parseDocdecl();
                           if (this.tokenize) {
                              return this.eventType = 10;
                           }
                        } else {
                           if (ch != '-') {
                              throw new XmlPullParserException("unexpected markup <!" + this.printable(ch), this, (Throwable)null);
                           }

                           this.parseComment();
                           if (this.tokenize) {
                              return this.eventType = 9;
                           }
                        }
                     }
                  } else {
                     if (!this.isS(ch)) {
                        throw new XmlPullParserException("in epilog non whitespace content is not allowed but got " + this.printable(ch), this, (Throwable)null);
                     }

                     gotS = true;
                     if (normalizeIgnorableWS) {
                        if (ch == '\r') {
                           normalizedCR = true;
                           if (!this.usePC) {
                              this.posEnd = this.pos - 1;
                              if (this.posEnd > this.posStart) {
                                 this.joinPC();
                              } else {
                                 this.usePC = true;
                                 this.pcStart = this.pcEnd = 0;
                              }
                           }

                           if (this.pcEnd >= this.pc.length) {
                              this.ensurePC(this.pcEnd);
                           }

                           this.pc[this.pcEnd++] = '\n';
                        } else if (ch == '\n') {
                           if (!normalizedCR && this.usePC) {
                              if (this.pcEnd >= this.pc.length) {
                                 this.ensurePC(this.pcEnd);
                              }

                              this.pc[this.pcEnd++] = '\n';
                           }

                           normalizedCR = false;
                        } else {
                           if (this.usePC) {
                              if (this.pcEnd >= this.pc.length) {
                                 this.ensurePC(this.pcEnd);
                              }

                              this.pc[this.pcEnd++] = ch;
                           }

                           normalizedCR = false;
                        }
                     }
                  }

                  ch = this.more();
               } while(!this.reachedEnd);
            }
         } catch (EOFException var5) {
            this.reachedEnd = true;
         }

         if (this.reachedEnd) {
            if (this.tokenize && gotS) {
               this.posEnd = this.pos;
               return this.eventType = 7;
            } else {
               return this.eventType = 1;
            }
         } else {
            throw new XmlPullParserException("internal error in parseEpilog");
         }
      }
   }

   public int parseEndTag() throws XmlPullParserException, IOException {
      char ch = this.more();
      if (!this.isNameStartChar(ch)) {
         throw new XmlPullParserException("expected name start and not " + this.printable(ch), this, (Throwable)null);
      } else {
         this.posStart = this.pos - 3;
         int nameStart = this.pos - 1 + this.bufAbsoluteStart;

         do {
            ch = this.more();
         } while(this.isNameChar(ch));

         int off = nameStart - this.bufAbsoluteStart;
         int len = this.pos - 1 - off;
         char[] cbuf = this.elRawName[this.depth];
         String startname;
         if (this.elRawNameEnd[this.depth] != len) {
            String startname = new String(cbuf, 0, this.elRawNameEnd[this.depth]);
            startname = new String(this.buf, off, len);
            throw new XmlPullParserException("end tag name </" + startname + "> must match start tag name <" + startname + ">" + " from line " + this.elRawNameLine[this.depth], this, (Throwable)null);
         } else {
            for(int i = 0; i < len; ++i) {
               if (this.buf[off++] != cbuf[i]) {
                  startname = new String(cbuf, 0, len);
                  String endname = new String(this.buf, off - i - 1, len);
                  throw new XmlPullParserException("end tag name </" + endname + "> must be the same as start tag <" + startname + ">" + " from line " + this.elRawNameLine[this.depth], this, (Throwable)null);
               }
            }

            while(this.isS(ch)) {
               ch = this.more();
            }

            if (ch != '>') {
               throw new XmlPullParserException("expected > to finsh end tag not " + this.printable(ch) + " from line " + this.elRawNameLine[this.depth], this, (Throwable)null);
            } else {
               this.posEnd = this.pos;
               this.pastEndTag = true;
               return this.eventType = 3;
            }
         }
      }
   }

   public int parseStartTag() throws XmlPullParserException, IOException {
      ++this.depth;
      this.posStart = this.pos - 2;
      this.emptyElementTag = false;
      this.attributeCount = 0;
      int nameStart = this.pos - 1 + this.bufAbsoluteStart;
      int colonPos = -1;
      char ch = this.buf[this.pos - 1];
      if (ch == ':' && this.processNamespaces) {
         throw new XmlPullParserException("when namespaces processing enabled colon can not be at element name start", this, (Throwable)null);
      } else {
         while(true) {
            ch = this.more();
            if (!this.isNameChar(ch)) {
               this.ensureElementsCapacity();
               int elLen = this.pos - 1 - (nameStart - this.bufAbsoluteStart);
               if (this.elRawName[this.depth] == null || this.elRawName[this.depth].length < elLen) {
                  this.elRawName[this.depth] = new char[2 * elLen];
               }

               System.arraycopy(this.buf, nameStart - this.bufAbsoluteStart, this.elRawName[this.depth], 0, elLen);
               this.elRawNameEnd[this.depth] = elLen;
               this.elRawNameLine[this.depth] = this.lineNumber;
               String name = null;
               String prefix = null;
               if (this.processNamespaces) {
                  if (colonPos != -1) {
                     prefix = this.elPrefix[this.depth] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, colonPos - nameStart);
                     name = this.elName[this.depth] = this.newString(this.buf, colonPos + 1 - this.bufAbsoluteStart, this.pos - 2 - (colonPos - this.bufAbsoluteStart));
                  } else {
                     prefix = this.elPrefix[this.depth] = null;
                     name = this.elName[this.depth] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, elLen);
                  }
               } else {
                  name = this.elName[this.depth] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, elLen);
               }

               while(true) {
                  while(this.isS(ch)) {
                     ch = this.more();
                  }

                  if (ch == '>') {
                     break;
                  }

                  if (ch == '/') {
                     if (this.emptyElementTag) {
                        throw new XmlPullParserException("repeated / in tag declaration", this, (Throwable)null);
                     }

                     this.emptyElementTag = true;
                     ch = this.more();
                     if (ch != '>') {
                        throw new XmlPullParserException("expected > to end empty tag not " + this.printable(ch), this, (Throwable)null);
                     }
                     break;
                  }

                  if (!this.isNameStartChar(ch)) {
                     throw new XmlPullParserException("start tag unexpected character " + this.printable(ch), this, (Throwable)null);
                  }

                  ch = this.parseAttribute();
                  ch = this.more();
               }

               int i;
               String attrPrefix;
               String attr1;
               if (this.processNamespaces) {
                  String uri = this.getNamespace(prefix);
                  if (uri == null) {
                     if (prefix != null) {
                        throw new XmlPullParserException("could not determine namespace bound to element prefix " + prefix, this, (Throwable)null);
                     }

                     uri = "";
                  }

                  this.elUri[this.depth] = uri;

                  for(i = 0; i < this.attributeCount; ++i) {
                     attrPrefix = this.attributePrefix[i];
                     if (attrPrefix != null) {
                        attr1 = this.getNamespace(attrPrefix);
                        if (attr1 == null) {
                           throw new XmlPullParserException("could not determine namespace bound to attribute prefix " + attrPrefix, this, (Throwable)null);
                        }

                        this.attributeUri[i] = attr1;
                     } else {
                        this.attributeUri[i] = "";
                     }
                  }

                  for(i = 1; i < this.attributeCount; ++i) {
                     for(int j = 0; j < i; ++j) {
                        if (this.attributeUri[j] == this.attributeUri[i] && (this.allStringsInterned && this.attributeName[j].equals(this.attributeName[i]) || !this.allStringsInterned && this.attributeNameHash[j] == this.attributeNameHash[i] && this.attributeName[j].equals(this.attributeName[i]))) {
                           attr1 = this.attributeName[j];
                           if (this.attributeUri[j] != null) {
                              attr1 = this.attributeUri[j] + ":" + attr1;
                           }

                           String attr2 = this.attributeName[i];
                           if (this.attributeUri[i] != null) {
                              attr2 = this.attributeUri[i] + ":" + attr2;
                           }

                           throw new XmlPullParserException("duplicated attributes " + attr1 + " and " + attr2, this, (Throwable)null);
                        }
                     }
                  }
               } else {
                  for(int i = 1; i < this.attributeCount; ++i) {
                     for(i = 0; i < i; ++i) {
                        if (this.allStringsInterned && this.attributeName[i].equals(this.attributeName[i]) || !this.allStringsInterned && this.attributeNameHash[i] == this.attributeNameHash[i] && this.attributeName[i].equals(this.attributeName[i])) {
                           attrPrefix = this.attributeName[i];
                           attr1 = this.attributeName[i];
                           throw new XmlPullParserException("duplicated attributes " + attrPrefix + " and " + attr1, this, (Throwable)null);
                        }
                     }
                  }
               }

               this.elNamespaceCount[this.depth] = this.namespaceEnd;
               this.posEnd = this.pos;
               return this.eventType = 2;
            }

            if (ch == ':' && this.processNamespaces) {
               if (colonPos != -1) {
                  throw new XmlPullParserException("only one colon is allowed in name of element when namespaces are enabled", this, (Throwable)null);
               }

               colonPos = this.pos - 1 + this.bufAbsoluteStart;
            }
         }
      }
   }

   protected char parseAttribute() throws XmlPullParserException, IOException {
      int prevPosStart = this.posStart + this.bufAbsoluteStart;
      int nameStart = this.pos - 1 + this.bufAbsoluteStart;
      int colonPos = -1;
      char ch = this.buf[this.pos - 1];
      if (ch == ':' && this.processNamespaces) {
         throw new XmlPullParserException("when namespaces processing enabled colon can not be at attribute name start", this, (Throwable)null);
      } else {
         boolean startsWithXmlns = this.processNamespaces && ch == 'x';
         int xmlnsPos = 0;

         for(ch = this.more(); this.isNameChar(ch); ch = this.more()) {
            if (this.processNamespaces) {
               if (startsWithXmlns && xmlnsPos < 5) {
                  ++xmlnsPos;
                  if (xmlnsPos == 1) {
                     if (ch != 'm') {
                        startsWithXmlns = false;
                     }
                  } else if (xmlnsPos == 2) {
                     if (ch != 'l') {
                        startsWithXmlns = false;
                     }
                  } else if (xmlnsPos == 3) {
                     if (ch != 'n') {
                        startsWithXmlns = false;
                     }
                  } else if (xmlnsPos == 4) {
                     if (ch != 's') {
                        startsWithXmlns = false;
                     }
                  } else if (xmlnsPos == 5 && ch != ':') {
                     throw new XmlPullParserException("after xmlns in attribute name must be colonwhen namespaces are enabled", this, (Throwable)null);
                  }
               }

               if (ch == ':') {
                  if (colonPos != -1) {
                     throw new XmlPullParserException("only one colon is allowed in attribute name when namespaces are enabled", this, (Throwable)null);
                  }

                  colonPos = this.pos - 1 + this.bufAbsoluteStart;
               }
            }
         }

         this.ensureAttributesCapacity(this.attributeCount);
         String name = null;
         String prefix = null;
         if (this.processNamespaces) {
            if (xmlnsPos < 4) {
               startsWithXmlns = false;
            }

            int nameLen;
            if (startsWithXmlns) {
               if (colonPos != -1) {
                  nameLen = this.pos - 2 - (colonPos - this.bufAbsoluteStart);
                  if (nameLen == 0) {
                     throw new XmlPullParserException("namespace prefix is required after xmlns:  when namespaces are enabled", this, (Throwable)null);
                  }

                  name = this.newString(this.buf, colonPos - this.bufAbsoluteStart + 1, nameLen);
               }
            } else {
               if (colonPos != -1) {
                  nameLen = colonPos - nameStart;
                  prefix = this.attributePrefix[this.attributeCount] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, nameLen);
                  int nameLen = this.pos - 2 - (colonPos - this.bufAbsoluteStart);
                  name = this.attributeName[this.attributeCount] = this.newString(this.buf, colonPos - this.bufAbsoluteStart + 1, nameLen);
               } else {
                  prefix = this.attributePrefix[this.attributeCount] = null;
                  name = this.attributeName[this.attributeCount] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, this.pos - 1 - (nameStart - this.bufAbsoluteStart));
               }

               if (!this.allStringsInterned) {
                  this.attributeNameHash[this.attributeCount] = name.hashCode();
               }
            }
         } else {
            name = this.attributeName[this.attributeCount] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, this.pos - 1 - (nameStart - this.bufAbsoluteStart));
            if (!this.allStringsInterned) {
               this.attributeNameHash[this.attributeCount] = name.hashCode();
            }
         }

         while(this.isS(ch)) {
            ch = this.more();
         }

         if (ch != '=') {
            throw new XmlPullParserException("expected = after attribute name", this, (Throwable)null);
         } else {
            for(ch = this.more(); this.isS(ch); ch = this.more()) {
            }

            char delimit = ch;
            if (ch != '"' && ch != '\'') {
               throw new XmlPullParserException("attribute value must start with quotation or apostrophe not " + this.printable(ch), this, (Throwable)null);
            } else {
               boolean normalizedCR = false;
               this.usePC = false;
               this.pcStart = this.pcEnd;
               this.posStart = this.pos;

               while(true) {
                  ch = this.more();
                  int prefixHash;
                  if (ch == delimit) {
                     if (this.processNamespaces && startsWithXmlns) {
                        String ns = null;
                        if (!this.usePC) {
                           ns = this.newStringIntern(this.buf, this.posStart, this.pos - 1 - this.posStart);
                        } else {
                           ns = this.newStringIntern(this.pc, this.pcStart, this.pcEnd - this.pcStart);
                        }

                        this.ensureNamespacesCapacity(this.namespaceEnd);
                        prefixHash = -1;
                        if (colonPos != -1) {
                           if (ns.length() == 0) {
                              throw new XmlPullParserException("non-default namespace can not be declared to be empty string", this, (Throwable)null);
                           }

                           this.namespacePrefix[this.namespaceEnd] = name;
                           if (!this.allStringsInterned) {
                              prefixHash = this.namespacePrefixHash[this.namespaceEnd] = name.hashCode();
                           }
                        } else {
                           this.namespacePrefix[this.namespaceEnd] = null;
                           if (!this.allStringsInterned) {
                              prefixHash = this.namespacePrefixHash[this.namespaceEnd] = -1;
                           }
                        }

                        this.namespaceUri[this.namespaceEnd] = ns;
                        int startNs = this.elNamespaceCount[this.depth - 1];
                        int i = this.namespaceEnd - 1;

                        while(true) {
                           if (i < startNs) {
                              ++this.namespaceEnd;
                              break;
                           }

                           if ((this.allStringsInterned || name == null) && this.namespacePrefix[i] == name || !this.allStringsInterned && name != null && this.namespacePrefixHash[i] == prefixHash && name.equals(this.namespacePrefix[i])) {
                              String s = name == null ? "default" : "'" + name + "'";
                              throw new XmlPullParserException("duplicated namespace declaration for " + s + " prefix", this, (Throwable)null);
                           }

                           --i;
                        }
                     } else {
                        if (!this.usePC) {
                           this.attributeValue[this.attributeCount] = new String(this.buf, this.posStart, this.pos - 1 - this.posStart);
                        } else {
                           this.attributeValue[this.attributeCount] = new String(this.pc, this.pcStart, this.pcEnd - this.pcStart);
                        }

                        ++this.attributeCount;
                     }

                     this.posStart = prevPosStart - this.bufAbsoluteStart;
                     return ch;
                  }

                  if (ch == '<') {
                     throw new XmlPullParserException("markup not allowed inside attribute value - illegal < ", this, (Throwable)null);
                  }

                  if (ch == '&') {
                     this.posEnd = this.pos - 1;
                     if (!this.usePC) {
                        boolean hadCharData = this.posEnd > this.posStart;
                        if (hadCharData) {
                           this.joinPC();
                        } else {
                           this.usePC = true;
                           this.pcStart = this.pcEnd = 0;
                        }
                     }

                     char[] resolvedEntity = this.parseEntityRef();
                     if (resolvedEntity == null) {
                        if (this.entityRefName == null) {
                           this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);
                        }

                        throw new XmlPullParserException("could not resolve entity named '" + this.printable(this.entityRefName) + "'", this, (Throwable)null);
                     }

                     for(prefixHash = 0; prefixHash < resolvedEntity.length; ++prefixHash) {
                        if (this.pcEnd >= this.pc.length) {
                           this.ensurePC(this.pcEnd);
                        }

                        this.pc[this.pcEnd++] = resolvedEntity[prefixHash];
                     }
                  } else if (ch != '\t' && ch != '\n' && ch != '\r') {
                     if (this.usePC) {
                        if (this.pcEnd >= this.pc.length) {
                           this.ensurePC(this.pcEnd);
                        }

                        this.pc[this.pcEnd++] = ch;
                     }
                  } else {
                     if (!this.usePC) {
                        this.posEnd = this.pos - 1;
                        if (this.posEnd > this.posStart) {
                           this.joinPC();
                        } else {
                           this.usePC = true;
                           this.pcEnd = this.pcStart = 0;
                        }
                     }

                     if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                     }

                     if (ch != '\n' || !normalizedCR) {
                        this.pc[this.pcEnd++] = ' ';
                     }
                  }

                  normalizedCR = ch == '\r';
               }
            }
         }
      }
   }

   protected char[] parseEntityRef() throws XmlPullParserException, IOException {
      this.entityRefName = null;
      this.posStart = this.pos;
      char ch = this.more();
      StringBuffer sb = new StringBuffer();
      char[] result;
      if (ch == '#') {
         char charRef = 0;
         ch = this.more();
         if (ch == 'x') {
            label147:
            while(true) {
               while(true) {
                  while(true) {
                     ch = this.more();
                     if (ch < '0' || ch > '9') {
                        if (ch < 'a' || ch > 'f') {
                           if (ch < 'A' || ch > 'F') {
                              if (ch != ';') {
                                 throw new XmlPullParserException("character reference (with hex value) may not contain " + this.printable(ch), this, (Throwable)null);
                              }
                              break label147;
                           }

                           charRef = (char)(charRef * 16 + (ch - 55));
                           sb.append(ch);
                        } else {
                           charRef = (char)(charRef * 16 + (ch - 87));
                           sb.append(ch);
                        }
                     } else {
                        charRef = (char)(charRef * 16 + (ch - 48));
                        sb.append(ch);
                     }
                  }
               }
            }
         } else {
            while(ch >= '0' && ch <= '9') {
               charRef = (char)(charRef * 10 + (ch - 48));
               ch = this.more();
            }

            if (ch != ';') {
               throw new XmlPullParserException("character reference (with decimal value) may not contain " + this.printable(ch), this, (Throwable)null);
            }
         }

         this.posEnd = this.pos - 1;
         if (sb.length() > 0) {
            result = toChars(Integer.parseInt(sb.toString(), 16));
            this.charRefOneCharBuf = result;
            if (this.tokenize) {
               this.text = this.newString(this.charRefOneCharBuf, 0, this.charRefOneCharBuf.length);
            }

            return this.charRefOneCharBuf;
         } else {
            this.charRefOneCharBuf[0] = charRef;
            if (this.tokenize) {
               this.text = this.newString(this.charRefOneCharBuf, 0, 1);
            }

            return this.charRefOneCharBuf;
         }
      } else if (!this.isNameStartChar(ch)) {
         throw new XmlPullParserException("entity reference names can not start with character '" + this.printable(ch) + "'", this, (Throwable)null);
      } else {
         do {
            ch = this.more();
            if (ch == ';') {
               this.posEnd = this.pos - 1;
               int len = this.posEnd - this.posStart;
               if (len == 2 && this.buf[this.posStart] == 'l' && this.buf[this.posStart + 1] == 't') {
                  if (this.tokenize) {
                     this.text = "<";
                  }

                  this.charRefOneCharBuf[0] = '<';
                  return this.charRefOneCharBuf;
               } else if (len == 3 && this.buf[this.posStart] == 'a' && this.buf[this.posStart + 1] == 'm' && this.buf[this.posStart + 2] == 'p') {
                  if (this.tokenize) {
                     this.text = "&";
                  }

                  this.charRefOneCharBuf[0] = '&';
                  return this.charRefOneCharBuf;
               } else if (len == 2 && this.buf[this.posStart] == 'g' && this.buf[this.posStart + 1] == 't') {
                  if (this.tokenize) {
                     this.text = ">";
                  }

                  this.charRefOneCharBuf[0] = '>';
                  return this.charRefOneCharBuf;
               } else if (len == 4 && this.buf[this.posStart] == 'a' && this.buf[this.posStart + 1] == 'p' && this.buf[this.posStart + 2] == 'o' && this.buf[this.posStart + 3] == 's') {
                  if (this.tokenize) {
                     this.text = "'";
                  }

                  this.charRefOneCharBuf[0] = '\'';
                  return this.charRefOneCharBuf;
               } else if (len == 4 && this.buf[this.posStart] == 'q' && this.buf[this.posStart + 1] == 'u' && this.buf[this.posStart + 2] == 'o' && this.buf[this.posStart + 3] == 't') {
                  if (this.tokenize) {
                     this.text = "\"";
                  }

                  this.charRefOneCharBuf[0] = '"';
                  return this.charRefOneCharBuf;
               } else {
                  result = this.lookuEntityReplacement(len);
                  if (result != null) {
                     return result;
                  } else {
                     if (this.tokenize) {
                        this.text = null;
                     }

                     return null;
                  }
               }
            }
         } while(this.isNameChar(ch));

         throw new XmlPullParserException("entity reference name can not contain character " + this.printable(ch) + "'", this, (Throwable)null);
      }
   }

   protected char[] lookuEntityReplacement(int entitNameLen) throws XmlPullParserException, IOException {
      int i;
      if (!this.allStringsInterned) {
         i = fastHash(this.buf, this.posStart, this.posEnd - this.posStart);

         label53:
         for(int i = this.entityEnd - 1; i >= 0; --i) {
            if (i == this.entityNameHash[i] && entitNameLen == this.entityNameBuf[i].length) {
               char[] entityBuf = this.entityNameBuf[i];

               for(int j = 0; j < entitNameLen; ++j) {
                  if (this.buf[this.posStart + j] != entityBuf[j]) {
                     continue label53;
                  }
               }

               if (this.tokenize) {
                  this.text = this.entityReplacement[i];
               }

               return this.entityReplacementBuf[i];
            }
         }
      } else {
         this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);

         for(i = this.entityEnd - 1; i >= 0; --i) {
            if (this.entityRefName == this.entityName[i]) {
               if (this.tokenize) {
                  this.text = this.entityReplacement[i];
               }

               return this.entityReplacementBuf[i];
            }
         }
      }

      return null;
   }

   protected void parseComment() throws XmlPullParserException, IOException {
      char ch = this.more();
      if (ch != '-') {
         throw new XmlPullParserException("expected <!-- for comment start", this, (Throwable)null);
      } else {
         if (this.tokenize) {
            this.posStart = this.pos;
         }

         int curLine = this.lineNumber;
         int curColumn = this.columnNumber;

         try {
            boolean normalizeIgnorableWS = this.tokenize && !this.roundtripSupported;
            boolean normalizedCR = false;
            boolean seenDash = false;
            boolean seenDashDash = false;

            while(true) {
               ch = this.more();
               if (seenDashDash && ch != '>') {
                  throw new XmlPullParserException("in comment after two dashes (--) next character must be > not " + this.printable(ch), this, (Throwable)null);
               }

               if (ch == '-') {
                  if (!seenDash) {
                     seenDash = true;
                  } else {
                     seenDashDash = true;
                     seenDash = false;
                  }
               } else if (ch == '>') {
                  if (seenDashDash) {
                     break;
                  }

                  seenDashDash = false;
                  seenDash = false;
               } else {
                  seenDash = false;
               }

               if (normalizeIgnorableWS) {
                  if (ch == '\r') {
                     normalizedCR = true;
                     if (!this.usePC) {
                        this.posEnd = this.pos - 1;
                        if (this.posEnd > this.posStart) {
                           this.joinPC();
                        } else {
                           this.usePC = true;
                           this.pcStart = this.pcEnd = 0;
                        }
                     }

                     if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                     }

                     this.pc[this.pcEnd++] = '\n';
                  } else if (ch == '\n') {
                     if (!normalizedCR && this.usePC) {
                        if (this.pcEnd >= this.pc.length) {
                           this.ensurePC(this.pcEnd);
                        }

                        this.pc[this.pcEnd++] = '\n';
                     }

                     normalizedCR = false;
                  } else {
                     if (this.usePC) {
                        if (this.pcEnd >= this.pc.length) {
                           this.ensurePC(this.pcEnd);
                        }

                        this.pc[this.pcEnd++] = ch;
                     }

                     normalizedCR = false;
                  }
               }
            }
         } catch (EOFException var8) {
            throw new XmlPullParserException("comment started on line " + curLine + " and column " + curColumn + " was not closed", this, var8);
         }

         if (this.tokenize) {
            this.posEnd = this.pos - 3;
            if (this.usePC) {
               this.pcEnd -= 2;
            }
         }

      }
   }

   protected boolean parsePI() throws XmlPullParserException, IOException {
      if (this.tokenize) {
         this.posStart = this.pos;
      }

      int curLine = this.lineNumber;
      int curColumn = this.columnNumber;
      int piTargetStart = this.pos + this.bufAbsoluteStart;
      int piTargetEnd = -1;
      boolean normalizeIgnorableWS = this.tokenize && !this.roundtripSupported;
      boolean normalizedCR = false;

      try {
         boolean seenQ = false;
         char ch = this.more();
         if (this.isS(ch)) {
            throw new XmlPullParserException("processing instruction PITarget must be exactly after <? and not white space character", this, (Throwable)null);
         }

         while(true) {
            if (ch == '?') {
               seenQ = true;
            } else if (ch == '>') {
               if (seenQ) {
                  break;
               }

               seenQ = false;
            } else {
               if (piTargetEnd == -1 && this.isS(ch)) {
                  piTargetEnd = this.pos - 1 + this.bufAbsoluteStart;
                  if (piTargetEnd - piTargetStart == 3 && (this.buf[piTargetStart] == 'x' || this.buf[piTargetStart] == 'X') && (this.buf[piTargetStart + 1] == 'm' || this.buf[piTargetStart + 1] == 'M') && (this.buf[piTargetStart + 2] == 'l' || this.buf[piTargetStart + 2] == 'L')) {
                     if (piTargetStart > 3) {
                        throw new XmlPullParserException("processing instruction can not have PITarget with reserveld xml name", this, (Throwable)null);
                     }

                     if (this.buf[piTargetStart] != 'x' && this.buf[piTargetStart + 1] != 'm' && this.buf[piTargetStart + 2] != 'l') {
                        throw new XmlPullParserException("XMLDecl must have xml name in lowercase", this, (Throwable)null);
                     }

                     this.parseXmlDecl(ch);
                     if (this.tokenize) {
                        this.posEnd = this.pos - 2;
                     }

                     int off = piTargetStart - this.bufAbsoluteStart + 3;
                     int len = this.pos - 2 - off;
                     this.xmlDeclContent = this.newString(this.buf, off, len);
                     return false;
                  }
               }

               seenQ = false;
            }

            if (normalizeIgnorableWS) {
               if (ch == '\r') {
                  normalizedCR = true;
                  if (!this.usePC) {
                     this.posEnd = this.pos - 1;
                     if (this.posEnd > this.posStart) {
                        this.joinPC();
                     } else {
                        this.usePC = true;
                        this.pcStart = this.pcEnd = 0;
                     }
                  }

                  if (this.pcEnd >= this.pc.length) {
                     this.ensurePC(this.pcEnd);
                  }

                  this.pc[this.pcEnd++] = '\n';
               } else if (ch == '\n') {
                  if (!normalizedCR && this.usePC) {
                     if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                     }

                     this.pc[this.pcEnd++] = '\n';
                  }

                  normalizedCR = false;
               } else {
                  if (this.usePC) {
                     if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                     }

                     this.pc[this.pcEnd++] = ch;
                  }

                  normalizedCR = false;
               }
            }

            ch = this.more();
         }
      } catch (EOFException var11) {
         throw new XmlPullParserException("processing instruction started on line " + curLine + " and column " + curColumn + " was not closed", this, var11);
      }

      if (piTargetEnd == -1) {
         piTargetEnd = this.pos - 2 + this.bufAbsoluteStart;
      }

      int var10000 = piTargetStart - this.bufAbsoluteStart;
      var10000 = piTargetEnd - this.bufAbsoluteStart;
      if (this.tokenize) {
         this.posEnd = this.pos - 2;
         if (normalizeIgnorableWS) {
            --this.pcEnd;
         }
      }

      return true;
   }

   protected void parseXmlDecl(char ch) throws XmlPullParserException, IOException {
      this.preventBufferCompaction = true;
      this.bufStart = 0;
      ch = this.skipS(ch);
      ch = this.requireInput(ch, VERSION);
      ch = this.skipS(ch);
      if (ch != '=') {
         throw new XmlPullParserException("expected equals sign (=) after version and not " + this.printable(ch), this, (Throwable)null);
      } else {
         ch = this.more();
         ch = this.skipS(ch);
         if (ch != '\'' && ch != '"') {
            throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after version and not " + this.printable(ch), this, (Throwable)null);
         } else {
            char quotChar = ch;
            int versionStart = this.pos;

            for(ch = this.more(); ch != quotChar; ch = this.more()) {
               if ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && (ch < '0' || ch > '9') && ch != '_' && ch != '.' && ch != ':' && ch != '-') {
                  throw new XmlPullParserException("<?xml version value expected to be in ([a-zA-Z0-9_.:] | '-') not " + this.printable(ch), this, (Throwable)null);
               }
            }

            int versionEnd = this.pos - 1;
            this.parseXmlDeclWithVersion(versionStart, versionEnd);
            this.preventBufferCompaction = false;
         }
      }
   }

   protected void parseXmlDeclWithVersion(int versionStart, int versionEnd) throws XmlPullParserException, IOException {
      if (versionEnd - versionStart == 3 && this.buf[versionStart] == '1' && this.buf[versionStart + 1] == '.' && this.buf[versionStart + 2] == '0') {
         this.xmlDeclVersion = this.newString(this.buf, versionStart, versionEnd - versionStart);
         char ch = this.more();
         ch = this.skipS(ch);
         char quotChar;
         int encodingStart;
         if (ch == 'e') {
            ch = this.more();
            ch = this.requireInput(ch, NCODING);
            ch = this.skipS(ch);
            if (ch != '=') {
               throw new XmlPullParserException("expected equals sign (=) after encoding and not " + this.printable(ch), this, (Throwable)null);
            }

            ch = this.more();
            ch = this.skipS(ch);
            if (ch != '\'' && ch != '"') {
               throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after encoding and not " + this.printable(ch), this, (Throwable)null);
            }

            quotChar = ch;
            encodingStart = this.pos;
            ch = this.more();
            if ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z')) {
               throw new XmlPullParserException("<?xml encoding name expected to start with [A-Za-z] not " + this.printable(ch), this, (Throwable)null);
            }

            for(ch = this.more(); ch != quotChar; ch = this.more()) {
               if ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && (ch < '0' || ch > '9') && ch != '.' && ch != '_' && ch != '-') {
                  throw new XmlPullParserException("<?xml encoding value expected to be in ([A-Za-z0-9._] | '-') not " + this.printable(ch), this, (Throwable)null);
               }
            }

            int encodingEnd = this.pos - 1;
            this.inputEncoding = this.newString(this.buf, encodingStart, encodingEnd - encodingStart);
            ch = this.more();
         }

         ch = this.skipS(ch);
         if (ch == 's') {
            ch = this.more();
            ch = this.requireInput(ch, TANDALONE);
            ch = this.skipS(ch);
            if (ch != '=') {
               throw new XmlPullParserException("expected equals sign (=) after standalone and not " + this.printable(ch), this, (Throwable)null);
            }

            ch = this.more();
            ch = this.skipS(ch);
            if (ch != '\'' && ch != '"') {
               throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after encoding and not " + this.printable(ch), this, (Throwable)null);
            }

            quotChar = ch;
            encodingStart = this.pos;
            ch = this.more();
            if (ch == 'y') {
               ch = this.requireInput(ch, YES);
               this.xmlDeclStandalone = new Boolean(true);
            } else {
               if (ch != 'n') {
                  throw new XmlPullParserException("expected 'yes' or 'no' after standalone and not " + this.printable(ch), this, (Throwable)null);
               }

               ch = this.requireInput(ch, NO);
               this.xmlDeclStandalone = new Boolean(false);
            }

            if (ch != quotChar) {
               throw new XmlPullParserException("expected " + quotChar + " after standalone value not " + this.printable(ch), this, (Throwable)null);
            }

            ch = this.more();
         }

         ch = this.skipS(ch);
         if (ch != '?') {
            throw new XmlPullParserException("expected ?> as last part of <?xml not " + this.printable(ch), this, (Throwable)null);
         } else {
            ch = this.more();
            if (ch != '>') {
               throw new XmlPullParserException("expected ?> as last part of <?xml not " + this.printable(ch), this, (Throwable)null);
            }
         }
      } else {
         throw new XmlPullParserException("only 1.0 is supported as <?xml version not '" + this.printable(new String(this.buf, versionStart, versionEnd - versionStart)) + "'", this, (Throwable)null);
      }
   }

   protected void parseDocdecl() throws XmlPullParserException, IOException {
      char ch = this.more();
      if (ch != 'O') {
         throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
      } else {
         ch = this.more();
         if (ch != 'C') {
            throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
         } else {
            ch = this.more();
            if (ch != 'T') {
               throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
            } else {
               ch = this.more();
               if (ch != 'Y') {
                  throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
               } else {
                  ch = this.more();
                  if (ch != 'P') {
                     throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
                  } else {
                     ch = this.more();
                     if (ch != 'E') {
                        throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
                     } else {
                        this.posStart = this.pos;
                        int bracketLevel = 0;
                        boolean normalizeIgnorableWS = this.tokenize && !this.roundtripSupported;
                        boolean normalizedCR = false;

                        while(true) {
                           ch = this.more();
                           if (ch == '[') {
                              ++bracketLevel;
                           }

                           if (ch == ']') {
                              --bracketLevel;
                           }

                           if (ch == '>' && bracketLevel == 0) {
                              this.posEnd = this.pos - 1;
                              return;
                           }

                           if (normalizeIgnorableWS) {
                              if (ch == '\r') {
                                 normalizedCR = true;
                                 if (!this.usePC) {
                                    this.posEnd = this.pos - 1;
                                    if (this.posEnd > this.posStart) {
                                       this.joinPC();
                                    } else {
                                       this.usePC = true;
                                       this.pcStart = this.pcEnd = 0;
                                    }
                                 }

                                 if (this.pcEnd >= this.pc.length) {
                                    this.ensurePC(this.pcEnd);
                                 }

                                 this.pc[this.pcEnd++] = '\n';
                              } else if (ch == '\n') {
                                 if (!normalizedCR && this.usePC) {
                                    if (this.pcEnd >= this.pc.length) {
                                       this.ensurePC(this.pcEnd);
                                    }

                                    this.pc[this.pcEnd++] = '\n';
                                 }

                                 normalizedCR = false;
                              } else {
                                 if (this.usePC) {
                                    if (this.pcEnd >= this.pc.length) {
                                       this.ensurePC(this.pcEnd);
                                    }

                                    this.pc[this.pcEnd++] = ch;
                                 }

                                 normalizedCR = false;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   protected void parseCDSect(boolean hadCharData) throws XmlPullParserException, IOException {
      char ch = this.more();
      if (ch != 'C') {
         throw new XmlPullParserException("expected <[CDATA[ for comment start", this, (Throwable)null);
      } else {
         ch = this.more();
         if (ch != 'D') {
            throw new XmlPullParserException("expected <[CDATA[ for comment start", this, (Throwable)null);
         } else {
            ch = this.more();
            if (ch != 'A') {
               throw new XmlPullParserException("expected <[CDATA[ for comment start", this, (Throwable)null);
            } else {
               ch = this.more();
               if (ch != 'T') {
                  throw new XmlPullParserException("expected <[CDATA[ for comment start", this, (Throwable)null);
               } else {
                  ch = this.more();
                  if (ch != 'A') {
                     throw new XmlPullParserException("expected <[CDATA[ for comment start", this, (Throwable)null);
                  } else {
                     ch = this.more();
                     if (ch != '[') {
                        throw new XmlPullParserException("expected <![CDATA[ for comment start", this, (Throwable)null);
                     } else {
                        int cdStart = this.pos + this.bufAbsoluteStart;
                        int curLine = this.lineNumber;
                        int curColumn = this.columnNumber;
                        boolean normalizeInput = !this.tokenize || !this.roundtripSupported;

                        try {
                           if (normalizeInput && hadCharData && !this.usePC) {
                              if (this.posEnd > this.posStart) {
                                 this.joinPC();
                              } else {
                                 this.usePC = true;
                                 this.pcStart = this.pcEnd = 0;
                              }
                           }

                           boolean seenBracket = false;
                           boolean seenBracketBracket = false;
                           boolean normalizedCR = false;

                           while(true) {
                              ch = this.more();
                              if (ch == ']') {
                                 if (!seenBracket) {
                                    seenBracket = true;
                                 } else {
                                    seenBracketBracket = true;
                                 }
                              } else if (ch == '>') {
                                 if (seenBracket && seenBracketBracket) {
                                    break;
                                 }

                                 seenBracketBracket = false;
                                 seenBracket = false;
                              } else if (seenBracket) {
                                 seenBracket = false;
                              }

                              if (normalizeInput) {
                                 if (ch == '\r') {
                                    normalizedCR = true;
                                    this.posStart = cdStart - this.bufAbsoluteStart;
                                    this.posEnd = this.pos - 1;
                                    if (!this.usePC) {
                                       if (this.posEnd > this.posStart) {
                                          this.joinPC();
                                       } else {
                                          this.usePC = true;
                                          this.pcStart = this.pcEnd = 0;
                                       }
                                    }

                                    if (this.pcEnd >= this.pc.length) {
                                       this.ensurePC(this.pcEnd);
                                    }

                                    this.pc[this.pcEnd++] = '\n';
                                 } else if (ch == '\n') {
                                    if (!normalizedCR && this.usePC) {
                                       if (this.pcEnd >= this.pc.length) {
                                          this.ensurePC(this.pcEnd);
                                       }

                                       this.pc[this.pcEnd++] = '\n';
                                    }

                                    normalizedCR = false;
                                 } else {
                                    if (this.usePC) {
                                       if (this.pcEnd >= this.pc.length) {
                                          this.ensurePC(this.pcEnd);
                                       }

                                       this.pc[this.pcEnd++] = ch;
                                    }

                                    normalizedCR = false;
                                 }
                              }
                           }
                        } catch (EOFException var10) {
                           throw new XmlPullParserException("CDATA section started on line " + curLine + " and column " + curColumn + " was not closed", this, var10);
                        }

                        if (normalizeInput && this.usePC) {
                           this.pcEnd -= 2;
                        }

                        this.posStart = cdStart - this.bufAbsoluteStart;
                        this.posEnd = this.pos - 3;
                     }
                  }
               }
            }
         }
      }
   }

   protected void fillBuf() throws IOException, XmlPullParserException {
      if (this.reader == null) {
         throw new XmlPullParserException("reader must be set before parsing is started");
      } else {
         if (this.bufEnd > this.bufSoftLimit) {
            boolean compact = this.bufStart > this.bufSoftLimit;
            boolean expand = false;
            if (this.preventBufferCompaction) {
               compact = false;
               expand = true;
            } else if (!compact) {
               if (this.bufStart < this.buf.length / 2) {
                  expand = true;
               } else {
                  compact = true;
               }
            }

            if (compact) {
               System.arraycopy(this.buf, this.bufStart, this.buf, 0, this.bufEnd - this.bufStart);
            } else {
               if (!expand) {
                  throw new XmlPullParserException("internal error in fillBuffer()");
               }

               int newSize = 2 * this.buf.length;
               char[] newBuf = new char[newSize];
               System.arraycopy(this.buf, this.bufStart, newBuf, 0, this.bufEnd - this.bufStart);
               this.buf = newBuf;
               if (this.bufLoadFactor > 0) {
                  this.bufSoftLimit = this.bufLoadFactor * this.buf.length / 100;
               }
            }

            this.bufEnd -= this.bufStart;
            this.pos -= this.bufStart;
            this.posStart -= this.bufStart;
            this.posEnd -= this.bufStart;
            this.bufAbsoluteStart += this.bufStart;
            this.bufStart = 0;
         }

         int len = this.buf.length - this.bufEnd > 8192 ? 8192 : this.buf.length - this.bufEnd;
         int ret = this.reader.read(this.buf, this.bufEnd, len);
         if (ret > 0) {
            this.bufEnd += ret;
         } else if (ret != -1) {
            throw new IOException("error reading input, returned " + ret);
         } else if (this.bufAbsoluteStart == 0 && this.pos == 0) {
            throw new EOFException("input contained no data");
         } else if (this.seenRoot && this.depth == 0) {
            this.reachedEnd = true;
         } else {
            StringBuffer expectedTagStack = new StringBuffer();
            if (this.depth > 0) {
               expectedTagStack.append(" - expected end tag");
               if (this.depth > 1) {
                  expectedTagStack.append("s");
               }

               expectedTagStack.append(" ");

               String tagName;
               int i;
               for(i = this.depth; i > 0; --i) {
                  tagName = new String(this.elRawName[i], 0, this.elRawNameEnd[i]);
                  expectedTagStack.append("</").append(tagName).append('>');
               }

               expectedTagStack.append(" to close");

               for(i = this.depth; i > 0; --i) {
                  if (i != this.depth) {
                     expectedTagStack.append(" and");
                  }

                  tagName = new String(this.elRawName[i], 0, this.elRawNameEnd[i]);
                  expectedTagStack.append(" start tag <" + tagName + ">");
                  expectedTagStack.append(" from line " + this.elRawNameLine[i]);
               }

               expectedTagStack.append(", parser stopped on");
            }

            throw new EOFException("no more data available" + expectedTagStack.toString() + this.getPositionDescription());
         }
      }
   }

   protected char more() throws IOException, XmlPullParserException {
      if (this.pos >= this.bufEnd) {
         this.fillBuf();
         if (this.reachedEnd) {
            return '\uffff';
         }
      }

      char ch = this.buf[this.pos++];
      if (ch == '\n') {
         ++this.lineNumber;
         this.columnNumber = 1;
      } else {
         ++this.columnNumber;
      }

      return ch;
   }

   protected void ensurePC(int end) {
      int newSize = end > 8192 ? 2 * end : 16384;
      char[] newPC = new char[newSize];
      System.arraycopy(this.pc, 0, newPC, 0, this.pcEnd);
      this.pc = newPC;
   }

   protected void joinPC() {
      int len = this.posEnd - this.posStart;
      int newEnd = this.pcEnd + len + 1;
      if (newEnd >= this.pc.length) {
         this.ensurePC(newEnd);
      }

      System.arraycopy(this.buf, this.posStart, this.pc, this.pcEnd, len);
      this.pcEnd += len;
      this.usePC = true;
   }

   protected char requireInput(char ch, char[] input) throws XmlPullParserException, IOException {
      for(int i = 0; i < input.length; ++i) {
         if (ch != input[i]) {
            throw new XmlPullParserException("expected " + this.printable(input[i]) + " in " + new String(input) + " and not " + this.printable(ch), this, (Throwable)null);
         }

         ch = this.more();
      }

      return ch;
   }

   protected char requireNextS() throws XmlPullParserException, IOException {
      char ch = this.more();
      if (!this.isS(ch)) {
         throw new XmlPullParserException("white space is required and not " + this.printable(ch), this, (Throwable)null);
      } else {
         return this.skipS(ch);
      }
   }

   protected char skipS(char ch) throws XmlPullParserException, IOException {
      while(this.isS(ch)) {
         ch = this.more();
      }

      return ch;
   }

   private static final void setName(char ch) {
      lookupNameChar[ch] = true;
   }

   private static final void setNameStart(char ch) {
      lookupNameStartChar[ch] = true;
      setName(ch);
   }

   protected boolean isNameStartChar(char ch) {
      return ch < 1024 && lookupNameStartChar[ch] || ch >= 1024 && ch <= 8231 || ch >= 8234 && ch <= 8591 || ch >= 10240 && ch <= '\uffef';
   }

   protected boolean isNameChar(char ch) {
      return ch < 1024 && lookupNameChar[ch] || ch >= 1024 && ch <= 8231 || ch >= 8234 && ch <= 8591 || ch >= 10240 && ch <= '\uffef';
   }

   protected boolean isS(char ch) {
      return ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t';
   }

   protected String printable(char ch) {
      if (ch == '\n') {
         return "\\n";
      } else if (ch == '\r') {
         return "\\r";
      } else if (ch == '\t') {
         return "\\t";
      } else if (ch == '\'') {
         return "\\'";
      } else {
         return ch <= 127 && ch >= ' ' ? "" + ch : "\\u" + Integer.toHexString(ch);
      }
   }

   protected String printable(String s) {
      if (s == null) {
         return null;
      } else {
         int sLen = s.length();
         StringBuffer buf = new StringBuffer(sLen + 10);

         for(int i = 0; i < sLen; ++i) {
            buf.append(this.printable(s.charAt(i)));
         }

         s = buf.toString();
         return s;
      }
   }

   private static int toCodePoint(char high, char low) {
      int h = (high & 1023) << 10;
      int l = low & 1023;
      return (h | l) + 65536;
   }

   private static boolean isHighSurrogate(char ch) {
      return '\ud800' <= ch && '\udbff' >= ch;
   }

   private static boolean isValidCodePoint(int codePoint) {
      return 0 <= codePoint && 1114111 >= codePoint;
   }

   private static boolean isSupplementaryCodePoint(int codePoint) {
      return 65536 <= codePoint && 1114111 >= codePoint;
   }

   public static char[] toChars(int codePoint) {
      if (!isValidCodePoint(codePoint)) {
         throw new IllegalArgumentException();
      } else if (isSupplementaryCodePoint(codePoint)) {
         int cpPrime = codePoint - 65536;
         int high = '\ud800' | cpPrime >> 10 & 1023;
         int low = '\udc00' | cpPrime & 1023;
         return new char[]{(char)high, (char)low};
      } else {
         return new char[]{(char)codePoint};
      }
   }

   static {
      setNameStart(':');

      char ch;
      for(ch = 'A'; ch <= 'Z'; ++ch) {
         setNameStart(ch);
      }

      setNameStart('_');

      for(ch = 'a'; ch <= 'z'; ++ch) {
         setNameStart(ch);
      }

      for(ch = 192; ch <= 767; ++ch) {
         setNameStart(ch);
      }

      for(ch = 880; ch <= 893; ++ch) {
         setNameStart(ch);
      }

      for(ch = 895; ch < 1024; ++ch) {
         setNameStart(ch);
      }

      setName('-');
      setName('.');

      for(ch = '0'; ch <= '9'; ++ch) {
         setName(ch);
      }

      setName('·');

      for(ch = 768; ch <= 879; ++ch) {
         setName(ch);
      }

   }
}
