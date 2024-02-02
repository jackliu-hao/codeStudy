/*      */ package org.codehaus.plexus.util.xml.pull;
/*      */ 
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import org.codehaus.plexus.util.ReaderFactory;
/*      */ import org.codehaus.plexus.util.xml.XmlStreamReader;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MXParser
/*      */   implements XmlPullParser
/*      */ {
/*      */   protected static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
/*      */   protected static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
/*      */   protected static final String FEATURE_XML_ROUNDTRIP = "http://xmlpull.org/v1/doc/features.html#xml-roundtrip";
/*      */   protected static final String FEATURE_NAMES_INTERNED = "http://xmlpull.org/v1/doc/features.html#names-interned";
/*      */   protected static final String PROPERTY_XMLDECL_VERSION = "http://xmlpull.org/v1/doc/properties.html#xmldecl-version";
/*      */   protected static final String PROPERTY_XMLDECL_STANDALONE = "http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone";
/*      */   protected static final String PROPERTY_XMLDECL_CONTENT = "http://xmlpull.org/v1/doc/properties.html#xmldecl-content";
/*      */   protected static final String PROPERTY_LOCATION = "http://xmlpull.org/v1/doc/properties.html#location";
/*      */   protected boolean allStringsInterned;
/*      */   private static final boolean TRACE_SIZING = false;
/*      */   protected boolean processNamespaces;
/*      */   protected boolean roundtripSupported;
/*      */   protected String location;
/*      */   protected int lineNumber;
/*      */   protected int columnNumber;
/*      */   protected boolean seenRoot;
/*      */   protected boolean reachedEnd;
/*      */   protected int eventType;
/*      */   protected boolean emptyElementTag;
/*      */   protected int depth;
/*      */   protected char[][] elRawName;
/*      */   protected int[] elRawNameEnd;
/*      */   protected int[] elRawNameLine;
/*      */   protected String[] elName;
/*      */   protected String[] elPrefix;
/*      */   protected String[] elUri;
/*      */   protected int[] elNamespaceCount;
/*      */   protected int attributeCount;
/*      */   protected String[] attributeName;
/*      */   protected int[] attributeNameHash;
/*      */   protected String[] attributePrefix;
/*      */   protected String[] attributeUri;
/*      */   protected String[] attributeValue;
/*      */   protected int namespaceEnd;
/*      */   protected String[] namespacePrefix;
/*      */   protected int[] namespacePrefixHash;
/*      */   protected String[] namespaceUri;
/*      */   protected int entityEnd;
/*      */   protected String[] entityName;
/*      */   protected char[][] entityNameBuf;
/*      */   protected String[] entityReplacement;
/*      */   protected char[][] entityReplacementBuf;
/*      */   protected int[] entityNameHash;
/*      */   protected static final int READ_CHUNK_SIZE = 8192;
/*      */   protected Reader reader;
/*      */   protected String inputEncoding;
/*      */   
/*      */   protected void resetStringCache() {}
/*      */   
/*      */   protected String newString(char[] cbuf, int off, int len) {
/*   69 */     return new String(cbuf, off, len);
/*      */   }
/*      */   
/*      */   protected String newStringIntern(char[] cbuf, int off, int len) {
/*   73 */     return (new String(cbuf, off, len)).intern();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void ensureElementsCapacity() {
/*  109 */     int elStackSize = (this.elName != null) ? this.elName.length : 0;
/*  110 */     if (this.depth + 1 >= elStackSize) {
/*      */       
/*  112 */       int newSize = ((this.depth >= 7) ? (2 * this.depth) : 8) + 2;
/*      */ 
/*      */ 
/*      */       
/*  116 */       boolean needsCopying = (elStackSize > 0);
/*  117 */       String[] arr = null;
/*      */       
/*  119 */       arr = new String[newSize];
/*  120 */       if (needsCopying) System.arraycopy(this.elName, 0, arr, 0, elStackSize); 
/*  121 */       this.elName = arr;
/*  122 */       arr = new String[newSize];
/*  123 */       if (needsCopying) System.arraycopy(this.elPrefix, 0, arr, 0, elStackSize); 
/*  124 */       this.elPrefix = arr;
/*  125 */       arr = new String[newSize];
/*  126 */       if (needsCopying) System.arraycopy(this.elUri, 0, arr, 0, elStackSize); 
/*  127 */       this.elUri = arr;
/*      */       
/*  129 */       int[] iarr = new int[newSize];
/*  130 */       if (needsCopying) {
/*  131 */         System.arraycopy(this.elNamespaceCount, 0, iarr, 0, elStackSize);
/*      */       } else {
/*      */         
/*  134 */         iarr[0] = 0;
/*      */       } 
/*  136 */       this.elNamespaceCount = iarr;
/*      */ 
/*      */       
/*  139 */       iarr = new int[newSize];
/*  140 */       if (needsCopying) {
/*  141 */         System.arraycopy(this.elRawNameEnd, 0, iarr, 0, elStackSize);
/*      */       }
/*  143 */       this.elRawNameEnd = iarr;
/*      */       
/*  145 */       iarr = new int[newSize];
/*  146 */       if (needsCopying) {
/*  147 */         System.arraycopy(this.elRawNameLine, 0, iarr, 0, elStackSize);
/*      */       }
/*  149 */       this.elRawNameLine = iarr;
/*      */       
/*  151 */       char[][] carr = new char[newSize][];
/*  152 */       if (needsCopying) {
/*  153 */         System.arraycopy(this.elRawName, 0, carr, 0, elStackSize);
/*      */       }
/*  155 */       this.elRawName = carr;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void ensureAttributesCapacity(int size) {
/*  192 */     int attrPosSize = (this.attributeName != null) ? this.attributeName.length : 0;
/*  193 */     if (size >= attrPosSize) {
/*  194 */       int newSize = (size > 7) ? (2 * size) : 8;
/*      */ 
/*      */ 
/*      */       
/*  198 */       boolean needsCopying = (attrPosSize > 0);
/*  199 */       String[] arr = null;
/*      */       
/*  201 */       arr = new String[newSize];
/*  202 */       if (needsCopying) System.arraycopy(this.attributeName, 0, arr, 0, attrPosSize); 
/*  203 */       this.attributeName = arr;
/*      */       
/*  205 */       arr = new String[newSize];
/*  206 */       if (needsCopying) System.arraycopy(this.attributePrefix, 0, arr, 0, attrPosSize); 
/*  207 */       this.attributePrefix = arr;
/*      */       
/*  209 */       arr = new String[newSize];
/*  210 */       if (needsCopying) System.arraycopy(this.attributeUri, 0, arr, 0, attrPosSize); 
/*  211 */       this.attributeUri = arr;
/*      */       
/*  213 */       arr = new String[newSize];
/*  214 */       if (needsCopying) System.arraycopy(this.attributeValue, 0, arr, 0, attrPosSize); 
/*  215 */       this.attributeValue = arr;
/*      */       
/*  217 */       if (!this.allStringsInterned) {
/*  218 */         int[] iarr = new int[newSize];
/*  219 */         if (needsCopying) System.arraycopy(this.attributeNameHash, 0, iarr, 0, attrPosSize); 
/*  220 */         this.attributeNameHash = iarr;
/*      */       } 
/*      */       
/*  223 */       arr = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void ensureNamespacesCapacity(int size) {
/*  235 */     int namespaceSize = (this.namespacePrefix != null) ? this.namespacePrefix.length : 0;
/*  236 */     if (size >= namespaceSize) {
/*  237 */       int newSize = (size > 7) ? (2 * size) : 8;
/*      */ 
/*      */ 
/*      */       
/*  241 */       String[] newNamespacePrefix = new String[newSize];
/*  242 */       String[] newNamespaceUri = new String[newSize];
/*  243 */       if (this.namespacePrefix != null) {
/*  244 */         System.arraycopy(this.namespacePrefix, 0, newNamespacePrefix, 0, this.namespaceEnd);
/*      */         
/*  246 */         System.arraycopy(this.namespaceUri, 0, newNamespaceUri, 0, this.namespaceEnd);
/*      */       } 
/*      */       
/*  249 */       this.namespacePrefix = newNamespacePrefix;
/*  250 */       this.namespaceUri = newNamespaceUri;
/*      */ 
/*      */       
/*  253 */       if (!this.allStringsInterned) {
/*  254 */         int[] newNamespacePrefixHash = new int[newSize];
/*  255 */         if (this.namespacePrefixHash != null) {
/*  256 */           System.arraycopy(this.namespacePrefixHash, 0, newNamespacePrefixHash, 0, this.namespaceEnd);
/*      */         }
/*      */         
/*  259 */         this.namespacePrefixHash = newNamespacePrefixHash;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static final int fastHash(char[] ch, int off, int len) {
/*  272 */     if (len == 0) return 0;
/*      */     
/*  274 */     int hash = ch[off];
/*      */     
/*  276 */     hash = (hash << 7) + ch[off + len - 1];
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  281 */     if (len > 16) hash = (hash << 7) + ch[off + len / 4]; 
/*  282 */     if (len > 8) hash = (hash << 7) + ch[off + len / 2];
/*      */ 
/*      */ 
/*      */     
/*  286 */     return hash;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void ensureEntityCapacity() {
/*  300 */     int entitySize = (this.entityReplacementBuf != null) ? this.entityReplacementBuf.length : 0;
/*  301 */     if (this.entityEnd >= entitySize) {
/*  302 */       int newSize = (this.entityEnd > 7) ? (2 * this.entityEnd) : 8;
/*      */ 
/*      */ 
/*      */       
/*  306 */       String[] newEntityName = new String[newSize];
/*  307 */       char[][] newEntityNameBuf = new char[newSize][];
/*  308 */       String[] newEntityReplacement = new String[newSize];
/*  309 */       char[][] newEntityReplacementBuf = new char[newSize][];
/*  310 */       if (this.entityName != null) {
/*  311 */         System.arraycopy(this.entityName, 0, newEntityName, 0, this.entityEnd);
/*  312 */         System.arraycopy(this.entityNameBuf, 0, newEntityNameBuf, 0, this.entityEnd);
/*  313 */         System.arraycopy(this.entityReplacement, 0, newEntityReplacement, 0, this.entityEnd);
/*  314 */         System.arraycopy(this.entityReplacementBuf, 0, newEntityReplacementBuf, 0, this.entityEnd);
/*      */       } 
/*  316 */       this.entityName = newEntityName;
/*  317 */       this.entityNameBuf = newEntityNameBuf;
/*  318 */       this.entityReplacement = newEntityReplacement;
/*  319 */       this.entityReplacementBuf = newEntityReplacementBuf;
/*      */       
/*  321 */       if (!this.allStringsInterned) {
/*  322 */         int[] newEntityNameHash = new int[newSize];
/*  323 */         if (this.entityNameHash != null) {
/*  324 */           System.arraycopy(this.entityNameHash, 0, newEntityNameHash, 0, this.entityEnd);
/*      */         }
/*  326 */         this.entityNameHash = newEntityNameHash;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  337 */   protected int bufLoadFactor = 95;
/*      */ 
/*      */   
/*  340 */   protected char[] buf = new char[(Runtime.getRuntime().freeMemory() > 1000000L) ? 8192 : 256];
/*      */   
/*  342 */   protected int bufSoftLimit = this.bufLoadFactor * this.buf.length / 100;
/*      */   
/*      */   protected boolean preventBufferCompaction;
/*      */   
/*      */   protected int bufAbsoluteStart;
/*      */   protected int bufStart;
/*      */   protected int bufEnd;
/*      */   protected int pos;
/*      */   protected int posStart;
/*      */   protected int posEnd;
/*  352 */   protected char[] pc = new char[(Runtime.getRuntime().freeMemory() > 1000000L) ? 8192 : 64];
/*      */   
/*      */   protected int pcStart;
/*      */   
/*      */   protected int pcEnd;
/*      */   
/*      */   protected boolean usePC;
/*      */   
/*      */   protected boolean seenStartTag;
/*      */   
/*      */   protected boolean seenEndTag;
/*      */   
/*      */   protected boolean pastEndTag;
/*      */   
/*      */   protected boolean seenAmpersand;
/*      */   
/*      */   protected boolean seenMarkup;
/*      */   
/*      */   protected boolean seenDocdecl;
/*      */   
/*      */   protected boolean tokenize;
/*      */   
/*      */   protected String text;
/*      */   protected String entityRefName;
/*      */   protected String xmlDeclVersion;
/*      */   protected Boolean xmlDeclStandalone;
/*      */   protected String xmlDeclContent;
/*      */   protected char[] charRefOneCharBuf;
/*      */   
/*      */   protected void reset() {
/*  382 */     this.location = null;
/*  383 */     this.lineNumber = 1;
/*  384 */     this.columnNumber = 0;
/*  385 */     this.seenRoot = false;
/*  386 */     this.reachedEnd = false;
/*  387 */     this.eventType = 0;
/*  388 */     this.emptyElementTag = false;
/*      */     
/*  390 */     this.depth = 0;
/*      */     
/*  392 */     this.attributeCount = 0;
/*      */     
/*  394 */     this.namespaceEnd = 0;
/*      */     
/*  396 */     this.entityEnd = 0;
/*      */     
/*  398 */     this.reader = null;
/*  399 */     this.inputEncoding = null;
/*      */     
/*  401 */     this.preventBufferCompaction = false;
/*  402 */     this.bufAbsoluteStart = 0;
/*  403 */     this.bufEnd = this.bufStart = 0;
/*  404 */     this.pos = this.posStart = this.posEnd = 0;
/*      */     
/*  406 */     this.pcEnd = this.pcStart = 0;
/*      */     
/*  408 */     this.usePC = false;
/*      */     
/*  410 */     this.seenStartTag = false;
/*  411 */     this.seenEndTag = false;
/*  412 */     this.pastEndTag = false;
/*  413 */     this.seenAmpersand = false;
/*  414 */     this.seenMarkup = false;
/*  415 */     this.seenDocdecl = false;
/*      */     
/*  417 */     this.xmlDeclVersion = null;
/*  418 */     this.xmlDeclStandalone = null;
/*  419 */     this.xmlDeclContent = null;
/*      */     
/*  421 */     resetStringCache();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFeature(String name, boolean state) throws XmlPullParserException {
/*  440 */     if (name == null) throw new IllegalArgumentException("feature name should not be null"); 
/*  441 */     if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name)) {
/*  442 */       if (this.eventType != 0) throw new XmlPullParserException("namespace processing feature can only be changed before parsing", this, null);
/*      */       
/*  444 */       this.processNamespaces = state;
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*  449 */     else if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name)) {
/*  450 */       if (state) {
/*  451 */         throw new XmlPullParserException("interning names in this implementation is not supported");
/*      */       }
/*      */     }
/*  454 */     else if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(name)) {
/*  455 */       if (state) {
/*  456 */         throw new XmlPullParserException("processing DOCDECL is not supported");
/*      */       
/*      */       }
/*      */     
/*      */     }
/*  461 */     else if ("http://xmlpull.org/v1/doc/features.html#xml-roundtrip".equals(name)) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  466 */       this.roundtripSupported = state;
/*      */     } else {
/*  468 */       throw new XmlPullParserException("unsupporte feature " + name);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getFeature(String name) {
/*  475 */     if (name == null) throw new IllegalArgumentException("feature name should not be nulll"); 
/*  476 */     if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name)) {
/*  477 */       return this.processNamespaces;
/*      */     }
/*      */     
/*  480 */     if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name))
/*  481 */       return false; 
/*  482 */     if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(name)) {
/*  483 */       return false;
/*      */     }
/*      */     
/*  486 */     if ("http://xmlpull.org/v1/doc/features.html#xml-roundtrip".equals(name))
/*      */     {
/*  488 */       return this.roundtripSupported;
/*      */     }
/*  490 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setProperty(String name, Object value) throws XmlPullParserException {
/*  497 */     if ("http://xmlpull.org/v1/doc/properties.html#location".equals(name)) {
/*  498 */       this.location = (String)value;
/*      */     } else {
/*  500 */       throw new XmlPullParserException("unsupported property: '" + name + "'");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getProperty(String name) {
/*  507 */     if (name == null) throw new IllegalArgumentException("property name should not be nulll"); 
/*  508 */     if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-version".equals(name))
/*  509 */       return this.xmlDeclVersion; 
/*  510 */     if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone".equals(name))
/*  511 */       return this.xmlDeclStandalone; 
/*  512 */     if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-content".equals(name))
/*  513 */       return this.xmlDeclContent; 
/*  514 */     if ("http://xmlpull.org/v1/doc/properties.html#location".equals(name)) {
/*  515 */       return this.location;
/*      */     }
/*  517 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInput(Reader in) throws XmlPullParserException {
/*  523 */     reset();
/*  524 */     this.reader = in;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
/*      */     XmlStreamReader xmlStreamReader;
/*  531 */     if (inputStream == null) {
/*  532 */       throw new IllegalArgumentException("input stream can not be null");
/*      */     }
/*      */     
/*      */     try {
/*  536 */       if (inputEncoding != null) {
/*  537 */         Reader reader = ReaderFactory.newReader(inputStream, inputEncoding);
/*      */       } else {
/*  539 */         xmlStreamReader = ReaderFactory.newXmlReader(inputStream);
/*      */       } 
/*  541 */     } catch (UnsupportedEncodingException une) {
/*  542 */       throw new XmlPullParserException("could not create reader for encoding " + inputEncoding + " : " + une, this, une);
/*      */     
/*      */     }
/*  545 */     catch (IOException e) {
/*      */       
/*  547 */       throw new XmlPullParserException("could not create reader : " + e, this, e);
/*      */     } 
/*      */     
/*  550 */     setInput((Reader)xmlStreamReader);
/*      */     
/*  552 */     this.inputEncoding = inputEncoding;
/*      */   }
/*      */   
/*      */   public String getInputEncoding() {
/*  556 */     return this.inputEncoding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
/*  565 */     if (!replacementText.startsWith("&#") && this.entityName != null && replacementText.length() > 1) {
/*      */       
/*  567 */       String tmp = replacementText.substring(1, replacementText.length() - 1);
/*  568 */       for (int i = 0; i < this.entityName.length; i++) {
/*      */         
/*  570 */         if (this.entityName[i] != null && this.entityName[i].equals(tmp))
/*      */         {
/*  572 */           replacementText = this.entityReplacement[i];
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  578 */     ensureEntityCapacity();
/*      */ 
/*      */     
/*  581 */     this.entityName[this.entityEnd] = newString(entityName.toCharArray(), 0, entityName.length());
/*  582 */     this.entityNameBuf[this.entityEnd] = entityName.toCharArray();
/*      */     
/*  584 */     this.entityReplacement[this.entityEnd] = replacementText;
/*  585 */     this.entityReplacementBuf[this.entityEnd] = replacementText.toCharArray();
/*  586 */     if (!this.allStringsInterned) {
/*  587 */       this.entityNameHash[this.entityEnd] = fastHash(this.entityNameBuf[this.entityEnd], 0, (this.entityNameBuf[this.entityEnd]).length);
/*      */     }
/*      */     
/*  590 */     this.entityEnd++;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getNamespaceCount(int depth) throws XmlPullParserException {
/*  598 */     if (!this.processNamespaces || depth == 0) {
/*  599 */       return 0;
/*      */     }
/*      */ 
/*      */     
/*  603 */     if (depth < 0 || depth > this.depth) throw new IllegalArgumentException("napespace count mayt be for depth 0.." + this.depth + " not " + depth);
/*      */     
/*  605 */     return this.elNamespaceCount[depth];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getNamespacePrefix(int pos) throws XmlPullParserException {
/*  614 */     if (pos < this.namespaceEnd) {
/*  615 */       return this.namespacePrefix[pos];
/*      */     }
/*  617 */     throw new XmlPullParserException("position " + pos + " exceeded number of available namespaces " + this.namespaceEnd);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getNamespaceUri(int pos) throws XmlPullParserException {
/*  626 */     if (pos < this.namespaceEnd) {
/*  627 */       return this.namespaceUri[pos];
/*      */     }
/*  629 */     throw new XmlPullParserException("position " + pos + " exceedded number of available namespaces " + this.namespaceEnd);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getNamespace(String prefix) {
/*  638 */     if (prefix != null) {
/*  639 */       for (int i = this.namespaceEnd - 1; i >= 0; i--) {
/*  640 */         if (prefix.equals(this.namespacePrefix[i])) {
/*  641 */           return this.namespaceUri[i];
/*      */         }
/*      */       } 
/*  644 */       if ("xml".equals(prefix))
/*  645 */         return "http://www.w3.org/XML/1998/namespace"; 
/*  646 */       if ("xmlns".equals(prefix)) {
/*  647 */         return "http://www.w3.org/2000/xmlns/";
/*      */       }
/*      */     } else {
/*  650 */       for (int i = this.namespaceEnd - 1; i >= 0; i--) {
/*  651 */         if (this.namespacePrefix[i] == null) {
/*  652 */           return this.namespaceUri[i];
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  657 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDepth() {
/*  663 */     return this.depth;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static int findFragment(int bufMinPos, char[] b, int start, int end) {
/*  669 */     if (start < bufMinPos) {
/*  670 */       start = bufMinPos;
/*  671 */       if (start > end) start = end; 
/*  672 */       return start;
/*      */     } 
/*  674 */     if (end - start > 65) {
/*  675 */       start = end - 10;
/*      */     }
/*  677 */     int i = start + 1;
/*  678 */     while (--i > bufMinPos && 
/*  679 */       end - i <= 65) {
/*  680 */       char c = b[i];
/*  681 */       if (c == '<' && start - i > 10)
/*      */         break; 
/*  683 */     }  return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPositionDescription() {
/*  693 */     String fragment = null;
/*  694 */     if (this.posStart <= this.pos) {
/*  695 */       int start = findFragment(0, this.buf, this.posStart, this.pos);
/*      */       
/*  697 */       if (start < this.pos) {
/*  698 */         fragment = new String(this.buf, start, this.pos - start);
/*      */       }
/*  700 */       if (this.bufAbsoluteStart > 0 || start > 0) fragment = "..." + fragment;
/*      */     
/*      */     } 
/*      */ 
/*      */     
/*  705 */     return " " + TYPES[this.eventType] + ((fragment != null) ? (" seen " + printable(fragment) + "...") : "") + " " + ((this.location != null) ? this.location : "") + "@" + getLineNumber() + ":" + getColumnNumber();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getLineNumber() {
/*  713 */     return this.lineNumber;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getColumnNumber() {
/*  718 */     return this.columnNumber;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isWhitespace() throws XmlPullParserException {
/*  724 */     if (this.eventType == 4 || this.eventType == 5) {
/*  725 */       if (this.usePC) {
/*  726 */         for (int j = this.pcStart; j < this.pcEnd; j++) {
/*      */           
/*  728 */           if (!isS(this.pc[j])) return false; 
/*      */         } 
/*  730 */         return true;
/*      */       } 
/*  732 */       for (int i = this.posStart; i < this.posEnd; i++) {
/*      */         
/*  734 */         if (!isS(this.buf[i])) return false; 
/*      */       } 
/*  736 */       return true;
/*      */     } 
/*  738 */     if (this.eventType == 7) {
/*  739 */       return true;
/*      */     }
/*  741 */     throw new XmlPullParserException("no content available to check for whitespaces");
/*      */   }
/*      */ 
/*      */   
/*      */   public String getText() {
/*  746 */     if (this.eventType == 0 || this.eventType == 1)
/*      */     {
/*      */ 
/*      */ 
/*      */       
/*  751 */       return null;
/*      */     }
/*  753 */     if (this.eventType == 6) {
/*  754 */       return this.text;
/*      */     }
/*  756 */     if (this.text == null) {
/*  757 */       if (!this.usePC || this.eventType == 2 || this.eventType == 3) {
/*  758 */         this.text = new String(this.buf, this.posStart, this.posEnd - this.posStart);
/*      */       } else {
/*  760 */         this.text = new String(this.pc, this.pcStart, this.pcEnd - this.pcStart);
/*      */       } 
/*      */     }
/*  763 */     return this.text;
/*      */   }
/*      */ 
/*      */   
/*      */   public char[] getTextCharacters(int[] holderForStartAndLength) {
/*  768 */     if (this.eventType == 4) {
/*  769 */       if (this.usePC) {
/*  770 */         holderForStartAndLength[0] = this.pcStart;
/*  771 */         holderForStartAndLength[1] = this.pcEnd - this.pcStart;
/*  772 */         return this.pc;
/*      */       } 
/*  774 */       holderForStartAndLength[0] = this.posStart;
/*  775 */       holderForStartAndLength[1] = this.posEnd - this.posStart;
/*  776 */       return this.buf;
/*      */     } 
/*      */     
/*  779 */     if (this.eventType == 2 || this.eventType == 3 || this.eventType == 5 || this.eventType == 9 || this.eventType == 6 || this.eventType == 8 || this.eventType == 7 || this.eventType == 10) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  788 */       holderForStartAndLength[0] = this.posStart;
/*  789 */       holderForStartAndLength[1] = this.posEnd - this.posStart;
/*  790 */       return this.buf;
/*  791 */     }  if (this.eventType == 0 || this.eventType == 1) {
/*      */ 
/*      */       
/*  794 */       holderForStartAndLength[1] = -1; holderForStartAndLength[0] = -1;
/*  795 */       return null;
/*      */     } 
/*  797 */     throw new IllegalArgumentException("unknown text eventType: " + this.eventType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getNamespace() {
/*  812 */     if (this.eventType == 2)
/*      */     {
/*  814 */       return this.processNamespaces ? this.elUri[this.depth] : ""; } 
/*  815 */     if (this.eventType == 3) {
/*  816 */       return this.processNamespaces ? this.elUri[this.depth] : "";
/*      */     }
/*  818 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getName() {
/*  839 */     if (this.eventType == 2)
/*      */     {
/*  841 */       return this.elName[this.depth]; } 
/*  842 */     if (this.eventType == 3)
/*  843 */       return this.elName[this.depth]; 
/*  844 */     if (this.eventType == 6) {
/*  845 */       if (this.entityRefName == null) {
/*  846 */         this.entityRefName = newString(this.buf, this.posStart, this.posEnd - this.posStart);
/*      */       }
/*  848 */       return this.entityRefName;
/*      */     } 
/*  850 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPrefix() {
/*  856 */     if (this.eventType == 2)
/*      */     {
/*  858 */       return this.elPrefix[this.depth]; } 
/*  859 */     if (this.eventType == 3) {
/*  860 */       return this.elPrefix[this.depth];
/*      */     }
/*  862 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEmptyElementTag() throws XmlPullParserException {
/*  871 */     if (this.eventType != 2) throw new XmlPullParserException("parser must be on START_TAG to check for empty element", this, null);
/*      */     
/*  873 */     return this.emptyElementTag;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getAttributeCount() {
/*  878 */     if (this.eventType != 2) return -1; 
/*  879 */     return this.attributeCount;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getAttributeNamespace(int index) {
/*  884 */     if (this.eventType != 2) throw new IndexOutOfBoundsException("only START_TAG can have attributes");
/*      */     
/*  886 */     if (!this.processNamespaces) return ""; 
/*  887 */     if (index < 0 || index >= this.attributeCount) throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
/*      */     
/*  889 */     return this.attributeUri[index];
/*      */   }
/*      */ 
/*      */   
/*      */   public String getAttributeName(int index) {
/*  894 */     if (this.eventType != 2) throw new IndexOutOfBoundsException("only START_TAG can have attributes");
/*      */     
/*  896 */     if (index < 0 || index >= this.attributeCount) throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
/*      */     
/*  898 */     return this.attributeName[index];
/*      */   }
/*      */ 
/*      */   
/*      */   public String getAttributePrefix(int index) {
/*  903 */     if (this.eventType != 2) throw new IndexOutOfBoundsException("only START_TAG can have attributes");
/*      */     
/*  905 */     if (!this.processNamespaces) return null; 
/*  906 */     if (index < 0 || index >= this.attributeCount) throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
/*      */     
/*  908 */     return this.attributePrefix[index];
/*      */   }
/*      */   
/*      */   public String getAttributeType(int index) {
/*  912 */     if (this.eventType != 2) throw new IndexOutOfBoundsException("only START_TAG can have attributes");
/*      */     
/*  914 */     if (index < 0 || index >= this.attributeCount) throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
/*      */     
/*  916 */     return "CDATA";
/*      */   }
/*      */   
/*      */   public boolean isAttributeDefault(int index) {
/*  920 */     if (this.eventType != 2) throw new IndexOutOfBoundsException("only START_TAG can have attributes");
/*      */     
/*  922 */     if (index < 0 || index >= this.attributeCount) throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
/*      */     
/*  924 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getAttributeValue(int index) {
/*  929 */     if (this.eventType != 2) throw new IndexOutOfBoundsException("only START_TAG can have attributes");
/*      */     
/*  931 */     if (index < 0 || index >= this.attributeCount) throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
/*      */     
/*  933 */     return this.attributeValue[index];
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String getAttributeValue(String namespace, String name) {
/*  939 */     if (this.eventType != 2) throw new IndexOutOfBoundsException("only START_TAG can have attributes" + getPositionDescription());
/*      */     
/*  941 */     if (name == null) {
/*  942 */       throw new IllegalArgumentException("attribute name can not be null");
/*      */     }
/*      */     
/*  945 */     if (this.processNamespaces) {
/*  946 */       if (namespace == null) {
/*  947 */         namespace = "";
/*      */       }
/*      */       
/*  950 */       for (int i = 0; i < this.attributeCount; i++) {
/*  951 */         if ((namespace == this.attributeUri[i] || namespace.equals(this.attributeUri[i])) && name.equals(this.attributeName[i]))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  957 */           return this.attributeValue[i];
/*      */         }
/*      */       } 
/*      */     } else {
/*  961 */       if (namespace != null && namespace.length() == 0) {
/*  962 */         namespace = null;
/*      */       }
/*  964 */       if (namespace != null) throw new IllegalArgumentException("when namespaces processing is disabled attribute namespace must be null");
/*      */       
/*  966 */       for (int i = 0; i < this.attributeCount; i++) {
/*  967 */         if (name.equals(this.attributeName[i]))
/*      */         {
/*  969 */           return this.attributeValue[i];
/*      */         }
/*      */       } 
/*      */     } 
/*  973 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getEventType() throws XmlPullParserException {
/*  980 */     return this.eventType;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
/*  986 */     if (!this.processNamespaces && namespace != null) {
/*  987 */       throw new XmlPullParserException("processing namespaces must be enabled on parser (or factory) to have possible namespaces delcared on elements" + " (postion:" + getPositionDescription() + ")");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  992 */     if (type != getEventType() || (namespace != null && !namespace.equals(getNamespace())) || (name != null && !name.equals(getName())))
/*      */     {
/*      */ 
/*      */       
/*  996 */       throw new XmlPullParserException("expected event " + TYPES[type] + ((name != null) ? (" with name '" + name + "'") : "") + ((namespace != null && name != null) ? " and" : "") + ((namespace != null) ? (" with namespace '" + namespace + "'") : "") + " but got" + ((type != getEventType()) ? (" " + TYPES[getEventType()]) : "") + ((name != null && getName() != null && !name.equals(getName())) ? (" name '" + getName() + "'") : "") + ((namespace != null && name != null && getName() != null && !name.equals(getName()) && getNamespace() != null && !namespace.equals(getNamespace())) ? " and" : "") + ((namespace != null && getNamespace() != null && !namespace.equals(getNamespace())) ? (" namespace '" + getNamespace() + "'") : "") + " (postion:" + getPositionDescription() + ")");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void skipSubTree() throws XmlPullParserException, IOException {
/* 1024 */     require(2, null, null);
/* 1025 */     int level = 1;
/* 1026 */     while (level > 0) {
/* 1027 */       int eventType = next();
/* 1028 */       if (eventType == 3) {
/* 1029 */         level--; continue;
/* 1030 */       }  if (eventType == 2) {
/* 1031 */         level++;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String nextText() throws XmlPullParserException, IOException {
/* 1066 */     if (getEventType() != 2) {
/* 1067 */       throw new XmlPullParserException("parser must be on START_TAG to read next text", this, null);
/*      */     }
/*      */     
/* 1070 */     int eventType = next();
/* 1071 */     if (eventType == 4) {
/* 1072 */       String result = getText();
/* 1073 */       eventType = next();
/* 1074 */       if (eventType != 3) {
/* 1075 */         throw new XmlPullParserException("TEXT must be immediately followed by END_TAG and not " + TYPES[getEventType()], this, null);
/*      */       }
/*      */ 
/*      */       
/* 1079 */       return result;
/* 1080 */     }  if (eventType == 3) {
/* 1081 */       return "";
/*      */     }
/* 1083 */     throw new XmlPullParserException("parser must be on START_TAG or TEXT to read text", this, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int nextTag() throws XmlPullParserException, IOException {
/* 1090 */     next();
/* 1091 */     if (this.eventType == 4 && isWhitespace()) {
/* 1092 */       next();
/*      */     }
/* 1094 */     if (this.eventType != 2 && this.eventType != 3) {
/* 1095 */       throw new XmlPullParserException("expected START_TAG or END_TAG not " + TYPES[getEventType()], this, null);
/*      */     }
/*      */     
/* 1098 */     return this.eventType;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int next() throws XmlPullParserException, IOException {
/* 1104 */     this.tokenize = false;
/* 1105 */     return nextImpl();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int nextToken() throws XmlPullParserException, IOException {
/* 1111 */     this.tokenize = true;
/* 1112 */     return nextImpl();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int nextImpl() throws XmlPullParserException, IOException {
/* 1119 */     this.text = null;
/* 1120 */     this.pcEnd = this.pcStart = 0;
/* 1121 */     this.usePC = false;
/* 1122 */     this.bufStart = this.posEnd;
/* 1123 */     if (this.pastEndTag) {
/* 1124 */       this.pastEndTag = false;
/* 1125 */       this.depth--;
/* 1126 */       this.namespaceEnd = this.elNamespaceCount[this.depth];
/*      */     } 
/* 1128 */     if (this.emptyElementTag) {
/* 1129 */       this.emptyElementTag = false;
/* 1130 */       this.pastEndTag = true;
/* 1131 */       return this.eventType = 3;
/*      */     } 
/*      */ 
/*      */     
/* 1135 */     if (this.depth > 0) {
/*      */       char ch;
/* 1137 */       if (this.seenStartTag) {
/* 1138 */         this.seenStartTag = false;
/* 1139 */         return this.eventType = parseStartTag();
/*      */       } 
/* 1141 */       if (this.seenEndTag) {
/* 1142 */         this.seenEndTag = false;
/* 1143 */         return this.eventType = parseEndTag();
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1149 */       if (this.seenMarkup) {
/* 1150 */         this.seenMarkup = false;
/* 1151 */         ch = '<';
/* 1152 */       } else if (this.seenAmpersand) {
/* 1153 */         this.seenAmpersand = false;
/* 1154 */         ch = '&';
/*      */       } else {
/* 1156 */         ch = more();
/*      */       } 
/* 1158 */       this.posStart = this.pos - 1;
/*      */ 
/*      */       
/* 1161 */       boolean hadCharData = false;
/*      */ 
/*      */       
/* 1164 */       boolean needsMerging = false;
/*      */ 
/*      */ 
/*      */       
/*      */       while (true) {
/* 1169 */         if (ch == '<') {
/* 1170 */           if (hadCharData)
/*      */           {
/* 1172 */             if (this.tokenize) {
/* 1173 */               this.seenMarkup = true;
/* 1174 */               return this.eventType = 4;
/*      */             } 
/*      */           }
/* 1177 */           ch = more();
/* 1178 */           if (ch == '/') {
/* 1179 */             if (!this.tokenize && hadCharData) {
/* 1180 */               this.seenEndTag = true;
/*      */               
/* 1182 */               return this.eventType = 4;
/*      */             } 
/* 1184 */             return this.eventType = parseEndTag();
/* 1185 */           }  if (ch == '!') {
/* 1186 */             ch = more();
/* 1187 */             if (ch == '-') {
/*      */               
/* 1189 */               parseComment();
/* 1190 */               if (this.tokenize) return this.eventType = 9; 
/* 1191 */               if (!this.usePC && hadCharData) {
/* 1192 */                 needsMerging = true;
/*      */               } else {
/* 1194 */                 this.posStart = this.pos;
/*      */               } 
/* 1196 */             } else if (ch == '[') {
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1201 */               parseCDSect(hadCharData);
/* 1202 */               if (this.tokenize) return this.eventType = 5; 
/* 1203 */               int cdStart = this.posStart;
/* 1204 */               int cdEnd = this.posEnd;
/* 1205 */               int cdLen = cdEnd - cdStart;
/*      */ 
/*      */               
/* 1208 */               if (cdLen > 0) {
/* 1209 */                 hadCharData = true;
/* 1210 */                 if (!this.usePC) {
/* 1211 */                   needsMerging = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*      */                 }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             }
/*      */             else {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1253 */               throw new XmlPullParserException("unexpected character in markup " + printable(ch), this, null);
/*      */             }
/*      */           
/* 1256 */           } else if (ch == '?') {
/* 1257 */             parsePI();
/* 1258 */             if (this.tokenize) return this.eventType = 8; 
/* 1259 */             if (!this.usePC && hadCharData) {
/* 1260 */               needsMerging = true;
/*      */             } else {
/* 1262 */               this.posStart = this.pos;
/*      */             } 
/*      */           } else {
/* 1265 */             if (isNameStartChar(ch)) {
/* 1266 */               if (!this.tokenize && hadCharData) {
/* 1267 */                 this.seenStartTag = true;
/*      */                 
/* 1269 */                 return this.eventType = 4;
/*      */               } 
/* 1271 */               return this.eventType = parseStartTag();
/*      */             } 
/* 1273 */             throw new XmlPullParserException("unexpected character in markup " + printable(ch), this, null);
/*      */           
/*      */           }
/*      */         
/*      */         }
/* 1278 */         else if (ch == '&') {
/*      */ 
/*      */           
/* 1281 */           if (this.tokenize && hadCharData) {
/* 1282 */             this.seenAmpersand = true;
/* 1283 */             return this.eventType = 4;
/*      */           } 
/* 1285 */           int oldStart = this.posStart + this.bufAbsoluteStart;
/* 1286 */           int oldEnd = this.posEnd + this.bufAbsoluteStart;
/* 1287 */           char[] resolvedEntity = parseEntityRef();
/* 1288 */           if (this.tokenize) return this.eventType = 6;
/*      */           
/* 1290 */           if (resolvedEntity == null) {
/* 1291 */             if (this.entityRefName == null) {
/* 1292 */               this.entityRefName = newString(this.buf, this.posStart, this.posEnd - this.posStart);
/*      */             }
/* 1294 */             throw new XmlPullParserException("could not resolve entity named '" + printable(this.entityRefName) + "'", this, null);
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1300 */           this.posStart = oldStart - this.bufAbsoluteStart;
/* 1301 */           this.posEnd = oldEnd - this.bufAbsoluteStart;
/* 1302 */           if (!this.usePC) {
/* 1303 */             if (hadCharData) {
/* 1304 */               joinPC();
/* 1305 */               needsMerging = false;
/*      */             } else {
/* 1307 */               this.usePC = true;
/* 1308 */               this.pcStart = this.pcEnd = 0;
/*      */             } 
/*      */           }
/*      */ 
/*      */           
/* 1313 */           for (int i = 0; i < resolvedEntity.length; i++) {
/*      */             
/* 1315 */             if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 1316 */             this.pc[this.pcEnd++] = resolvedEntity[i];
/*      */           } 
/*      */           
/* 1319 */           hadCharData = true;
/*      */         }
/*      */         else {
/*      */           
/* 1323 */           if (needsMerging) {
/*      */             
/* 1325 */             joinPC();
/*      */             
/* 1327 */             needsMerging = false;
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1338 */           hadCharData = true;
/*      */           
/* 1340 */           boolean normalizedCR = false;
/* 1341 */           boolean normalizeInput = (!this.tokenize || !this.roundtripSupported);
/*      */           
/* 1343 */           boolean seenBracket = false;
/* 1344 */           boolean seenBracketBracket = false;
/*      */ 
/*      */           
/*      */           do {
/* 1348 */             if (ch == ']')
/* 1349 */             { if (seenBracket) {
/* 1350 */                 seenBracketBracket = true;
/*      */               } else {
/* 1352 */                 seenBracket = true;
/*      */               }  }
/* 1354 */             else { if (seenBracketBracket && ch == '>') {
/* 1355 */                 throw new XmlPullParserException("characters ]]> are not allowed in content", this, null);
/*      */               }
/*      */               
/* 1358 */               if (seenBracket) {
/* 1359 */                 seenBracketBracket = seenBracket = false;
/*      */               } }
/*      */ 
/*      */             
/* 1363 */             if (normalizeInput)
/*      */             {
/* 1365 */               if (ch == '\r') {
/* 1366 */                 normalizedCR = true;
/* 1367 */                 this.posEnd = this.pos - 1;
/*      */                 
/* 1369 */                 if (!this.usePC) {
/* 1370 */                   if (this.posEnd > this.posStart) {
/* 1371 */                     joinPC();
/*      */                   } else {
/* 1373 */                     this.usePC = true;
/* 1374 */                     this.pcStart = this.pcEnd = 0;
/*      */                   } 
/*      */                 }
/*      */                 
/* 1378 */                 if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 1379 */                 this.pc[this.pcEnd++] = '\n';
/* 1380 */               } else if (ch == '\n') {
/*      */                 
/* 1382 */                 if (!normalizedCR && this.usePC) {
/* 1383 */                   if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 1384 */                   this.pc[this.pcEnd++] = '\n';
/*      */                 } 
/* 1386 */                 normalizedCR = false;
/*      */               } else {
/* 1388 */                 if (this.usePC) {
/* 1389 */                   if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 1390 */                   this.pc[this.pcEnd++] = ch;
/*      */                 } 
/* 1392 */                 normalizedCR = false;
/*      */               } 
/*      */             }
/*      */             
/* 1396 */             ch = more();
/* 1397 */           } while (ch != '<' && ch != '&');
/* 1398 */           this.posEnd = this.pos - 1;
/*      */           continue;
/*      */         } 
/* 1401 */         ch = more();
/*      */       } 
/*      */     } 
/* 1404 */     if (this.seenRoot) {
/* 1405 */       return parseEpilog();
/*      */     }
/* 1407 */     return parseProlog();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int parseProlog() throws XmlPullParserException, IOException {
/*      */     char ch;
/* 1419 */     if (this.seenMarkup) {
/* 1420 */       ch = this.buf[this.pos - 1];
/*      */     } else {
/* 1422 */       ch = more();
/*      */     } 
/*      */     
/* 1425 */     if (this.eventType == 0) {
/*      */ 
/*      */ 
/*      */       
/* 1429 */       if (ch == '￾') {
/* 1430 */         throw new XmlPullParserException("first character in input was UNICODE noncharacter (0xFFFE)- input requires int swapping", this, null);
/*      */       }
/*      */ 
/*      */       
/* 1434 */       if (ch == '﻿')
/*      */       {
/* 1436 */         ch = more();
/*      */       }
/*      */     } 
/* 1439 */     this.seenMarkup = false;
/* 1440 */     boolean gotS = false;
/* 1441 */     this.posStart = this.pos - 1;
/* 1442 */     boolean normalizeIgnorableWS = (this.tokenize == true && !this.roundtripSupported);
/* 1443 */     boolean normalizedCR = false;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 1449 */       if (ch == '<') {
/* 1450 */         if (gotS && this.tokenize) {
/* 1451 */           this.posEnd = this.pos - 1;
/* 1452 */           this.seenMarkup = true;
/* 1453 */           return this.eventType = 7;
/*      */         } 
/* 1455 */         ch = more();
/* 1456 */         if (ch == '?') {
/*      */ 
/*      */           
/* 1459 */           boolean isXMLDecl = parsePI();
/* 1460 */           if (this.tokenize) {
/* 1461 */             if (isXMLDecl) {
/* 1462 */               return this.eventType = 0;
/*      */             }
/* 1464 */             return this.eventType = 8;
/*      */           } 
/* 1466 */         } else if (ch == '!') {
/* 1467 */           ch = more();
/* 1468 */           if (ch == 'D') {
/* 1469 */             if (this.seenDocdecl) {
/* 1470 */               throw new XmlPullParserException("only one docdecl allowed in XML document", this, null);
/*      */             }
/*      */             
/* 1473 */             this.seenDocdecl = true;
/* 1474 */             parseDocdecl();
/* 1475 */             if (this.tokenize) return this.eventType = 10; 
/* 1476 */           } else if (ch == '-') {
/* 1477 */             parseComment();
/* 1478 */             if (this.tokenize) return this.eventType = 9; 
/*      */           } else {
/* 1480 */             throw new XmlPullParserException("unexpected markup <!" + printable(ch), this, null);
/*      */           } 
/*      */         } else {
/* 1483 */           if (ch == '/') {
/* 1484 */             throw new XmlPullParserException("expected start tag name and not " + printable(ch), this, null);
/*      */           }
/* 1486 */           if (isNameStartChar(ch)) {
/* 1487 */             this.seenRoot = true;
/* 1488 */             return parseStartTag();
/*      */           } 
/* 1490 */           throw new XmlPullParserException("expected start tag name and not " + printable(ch), this, null);
/*      */         }
/*      */       
/* 1493 */       } else if (isS(ch)) {
/* 1494 */         gotS = true;
/* 1495 */         if (normalizeIgnorableWS) {
/* 1496 */           if (ch == '\r') {
/* 1497 */             normalizedCR = true;
/*      */ 
/*      */ 
/*      */             
/* 1501 */             if (!this.usePC) {
/* 1502 */               this.posEnd = this.pos - 1;
/* 1503 */               if (this.posEnd > this.posStart) {
/* 1504 */                 joinPC();
/*      */               } else {
/* 1506 */                 this.usePC = true;
/* 1507 */                 this.pcStart = this.pcEnd = 0;
/*      */               } 
/*      */             } 
/*      */             
/* 1511 */             if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 1512 */             this.pc[this.pcEnd++] = '\n';
/* 1513 */           } else if (ch == '\n') {
/* 1514 */             if (!normalizedCR && this.usePC) {
/* 1515 */               if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 1516 */               this.pc[this.pcEnd++] = '\n';
/*      */             } 
/* 1518 */             normalizedCR = false;
/*      */           } else {
/* 1520 */             if (this.usePC) {
/* 1521 */               if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 1522 */               this.pc[this.pcEnd++] = ch;
/*      */             } 
/* 1524 */             normalizedCR = false;
/*      */           } 
/*      */         }
/*      */       } else {
/* 1528 */         throw new XmlPullParserException("only whitespace content allowed before start tag and not " + printable(ch), this, null);
/*      */       } 
/*      */ 
/*      */       
/* 1532 */       ch = more();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int parseEpilog() throws XmlPullParserException, IOException {
/* 1539 */     if (this.eventType == 1) {
/* 1540 */       throw new XmlPullParserException("already reached end of XML input", this, null);
/*      */     }
/* 1542 */     if (this.reachedEnd) {
/* 1543 */       return this.eventType = 1;
/*      */     }
/* 1545 */     boolean gotS = false;
/* 1546 */     boolean normalizeIgnorableWS = (this.tokenize == true && !this.roundtripSupported);
/* 1547 */     boolean normalizedCR = false;
/*      */     
/*      */     try {
/*      */       char ch;
/* 1551 */       if (this.seenMarkup) {
/* 1552 */         ch = this.buf[this.pos - 1];
/*      */       } else {
/* 1554 */         ch = more();
/*      */       } 
/* 1556 */       this.seenMarkup = false;
/* 1557 */       this.posStart = this.pos - 1;
/* 1558 */       if (!this.reachedEnd) {
/*      */         
/*      */         do {
/*      */           
/* 1562 */           if (ch == '<') {
/* 1563 */             if (gotS && this.tokenize) {
/* 1564 */               this.posEnd = this.pos - 1;
/* 1565 */               this.seenMarkup = true;
/* 1566 */               return this.eventType = 7;
/*      */             } 
/* 1568 */             ch = more();
/* 1569 */             if (this.reachedEnd) {
/*      */               break;
/*      */             }
/* 1572 */             if (ch == '?') {
/*      */ 
/*      */               
/* 1575 */               parsePI();
/* 1576 */               if (this.tokenize) return this.eventType = 8;
/*      */             
/* 1578 */             } else if (ch == '!') {
/* 1579 */               ch = more();
/* 1580 */               if (this.reachedEnd) {
/*      */                 break;
/*      */               }
/* 1583 */               if (ch == 'D') {
/* 1584 */                 parseDocdecl();
/* 1585 */                 if (this.tokenize) return this.eventType = 10; 
/* 1586 */               } else if (ch == '-') {
/* 1587 */                 parseComment();
/* 1588 */                 if (this.tokenize) return this.eventType = 9; 
/*      */               } else {
/* 1590 */                 throw new XmlPullParserException("unexpected markup <!" + printable(ch), this, null);
/*      */               } 
/*      */             } else {
/* 1593 */               if (ch == '/') {
/* 1594 */                 throw new XmlPullParserException("end tag not allowed in epilog but got " + printable(ch), this, null);
/*      */               }
/* 1596 */               if (isNameStartChar(ch)) {
/* 1597 */                 throw new XmlPullParserException("start tag not allowed in epilog but got " + printable(ch), this, null);
/*      */               }
/*      */               
/* 1600 */               throw new XmlPullParserException("in epilog expected ignorable content and not " + printable(ch), this, null);
/*      */             }
/*      */           
/*      */           }
/* 1604 */           else if (isS(ch)) {
/* 1605 */             gotS = true;
/* 1606 */             if (normalizeIgnorableWS) {
/* 1607 */               if (ch == '\r') {
/* 1608 */                 normalizedCR = true;
/*      */ 
/*      */ 
/*      */                 
/* 1612 */                 if (!this.usePC) {
/* 1613 */                   this.posEnd = this.pos - 1;
/* 1614 */                   if (this.posEnd > this.posStart) {
/* 1615 */                     joinPC();
/*      */                   } else {
/* 1617 */                     this.usePC = true;
/* 1618 */                     this.pcStart = this.pcEnd = 0;
/*      */                   } 
/*      */                 } 
/*      */                 
/* 1622 */                 if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 1623 */                 this.pc[this.pcEnd++] = '\n';
/* 1624 */               } else if (ch == '\n') {
/* 1625 */                 if (!normalizedCR && this.usePC) {
/* 1626 */                   if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 1627 */                   this.pc[this.pcEnd++] = '\n';
/*      */                 } 
/* 1629 */                 normalizedCR = false;
/*      */               } else {
/* 1631 */                 if (this.usePC) {
/* 1632 */                   if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 1633 */                   this.pc[this.pcEnd++] = ch;
/*      */                 } 
/* 1635 */                 normalizedCR = false;
/*      */               } 
/*      */             }
/*      */           } else {
/* 1639 */             throw new XmlPullParserException("in epilog non whitespace content is not allowed but got " + printable(ch), this, null);
/*      */           } 
/*      */ 
/*      */           
/* 1643 */           ch = more();
/* 1644 */         } while (!this.reachedEnd);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */ 
/*      */     
/*      */     }
/* 1654 */     catch (EOFException ex) {
/* 1655 */       this.reachedEnd = true;
/*      */     } 
/* 1657 */     if (this.reachedEnd) {
/* 1658 */       if (this.tokenize && gotS) {
/* 1659 */         this.posEnd = this.pos;
/* 1660 */         return this.eventType = 7;
/*      */       } 
/* 1662 */       return this.eventType = 1;
/*      */     } 
/* 1664 */     throw new XmlPullParserException("internal error in parseEpilog");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int parseEndTag() throws XmlPullParserException, IOException {
/* 1672 */     char ch = more();
/* 1673 */     if (!isNameStartChar(ch)) {
/* 1674 */       throw new XmlPullParserException("expected name start and not " + printable(ch), this, null);
/*      */     }
/*      */     
/* 1677 */     this.posStart = this.pos - 3;
/* 1678 */     int nameStart = this.pos - 1 + this.bufAbsoluteStart;
/*      */     do {
/* 1680 */       ch = more();
/* 1681 */     } while (isNameChar(ch));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1690 */     int off = nameStart - this.bufAbsoluteStart;
/*      */     
/* 1692 */     int len = this.pos - 1 - off;
/* 1693 */     char[] cbuf = this.elRawName[this.depth];
/* 1694 */     if (this.elRawNameEnd[this.depth] != len) {
/*      */       
/* 1696 */       String startname = new String(cbuf, 0, this.elRawNameEnd[this.depth]);
/* 1697 */       String endname = new String(this.buf, off, len);
/* 1698 */       throw new XmlPullParserException("end tag name </" + endname + "> must match start tag name <" + startname + ">" + " from line " + this.elRawNameLine[this.depth], this, null);
/*      */     } 
/*      */ 
/*      */     
/* 1702 */     for (int i = 0; i < len; i++) {
/*      */       
/* 1704 */       if (this.buf[off++] != cbuf[i]) {
/*      */         
/* 1706 */         String startname = new String(cbuf, 0, len);
/* 1707 */         String endname = new String(this.buf, off - i - 1, len);
/* 1708 */         throw new XmlPullParserException("end tag name </" + endname + "> must be the same as start tag <" + startname + ">" + " from line " + this.elRawNameLine[this.depth], this, null);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1714 */     for (; isS(ch); ch = more());
/* 1715 */     if (ch != '>') {
/* 1716 */       throw new XmlPullParserException("expected > to finsh end tag not " + printable(ch) + " from line " + this.elRawNameLine[this.depth], this, null);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1724 */     this.posEnd = this.pos;
/* 1725 */     this.pastEndTag = true;
/* 1726 */     return this.eventType = 3;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int parseStartTag() throws XmlPullParserException, IOException {
/* 1733 */     this.depth++;
/*      */     
/* 1735 */     this.posStart = this.pos - 2;
/*      */     
/* 1737 */     this.emptyElementTag = false;
/* 1738 */     this.attributeCount = 0;
/*      */     
/* 1740 */     int nameStart = this.pos - 1 + this.bufAbsoluteStart;
/* 1741 */     int colonPos = -1;
/* 1742 */     char ch = this.buf[this.pos - 1];
/* 1743 */     if (ch == ':' && this.processNamespaces) throw new XmlPullParserException("when namespaces processing enabled colon can not be at element name start", this, null);
/*      */ 
/*      */     
/*      */     while (true) {
/* 1747 */       ch = more();
/* 1748 */       if (!isNameChar(ch))
/* 1749 */         break;  if (ch == ':' && this.processNamespaces) {
/* 1750 */         if (colonPos != -1) throw new XmlPullParserException("only one colon is allowed in name of element when namespaces are enabled", this, null);
/*      */ 
/*      */         
/* 1753 */         colonPos = this.pos - 1 + this.bufAbsoluteStart;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1758 */     ensureElementsCapacity();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1763 */     int elLen = this.pos - 1 - nameStart - this.bufAbsoluteStart;
/* 1764 */     if (this.elRawName[this.depth] == null || (this.elRawName[this.depth]).length < elLen) {
/* 1765 */       this.elRawName[this.depth] = new char[2 * elLen];
/*      */     }
/* 1767 */     System.arraycopy(this.buf, nameStart - this.bufAbsoluteStart, this.elRawName[this.depth], 0, elLen);
/* 1768 */     this.elRawNameEnd[this.depth] = elLen;
/* 1769 */     this.elRawNameLine[this.depth] = this.lineNumber;
/*      */     
/* 1771 */     String name = null;
/*      */ 
/*      */     
/* 1774 */     String prefix = null;
/* 1775 */     if (this.processNamespaces) {
/* 1776 */       if (colonPos != -1) {
/* 1777 */         prefix = this.elPrefix[this.depth] = newString(this.buf, nameStart - this.bufAbsoluteStart, colonPos - nameStart);
/*      */         
/* 1779 */         name = this.elName[this.depth] = newString(this.buf, colonPos + 1 - this.bufAbsoluteStart, this.pos - 2 - colonPos - this.bufAbsoluteStart);
/*      */       }
/*      */       else {
/*      */         
/* 1783 */         prefix = this.elPrefix[this.depth] = null;
/* 1784 */         name = this.elName[this.depth] = newString(this.buf, nameStart - this.bufAbsoluteStart, elLen);
/*      */       } 
/*      */     } else {
/*      */       
/* 1788 */       name = this.elName[this.depth] = newString(this.buf, nameStart - this.bufAbsoluteStart, elLen);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 1795 */       for (; isS(ch); ch = more());
/*      */       
/* 1797 */       if (ch == '>')
/*      */         break; 
/* 1799 */       if (ch == '/') {
/* 1800 */         if (this.emptyElementTag) throw new XmlPullParserException("repeated / in tag declaration", this, null);
/*      */         
/* 1802 */         this.emptyElementTag = true;
/* 1803 */         ch = more();
/* 1804 */         if (ch != '>') throw new XmlPullParserException("expected > to end empty tag not " + printable(ch), this, null); 
/*      */         break;
/*      */       } 
/* 1807 */       if (isNameStartChar(ch)) {
/* 1808 */         ch = parseAttribute();
/* 1809 */         ch = more();
/*      */         continue;
/*      */       } 
/* 1812 */       throw new XmlPullParserException("start tag unexpected character " + printable(ch), this, null);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1819 */     if (this.processNamespaces) {
/* 1820 */       String uri = getNamespace(prefix);
/* 1821 */       if (uri == null) {
/* 1822 */         if (prefix == null) {
/* 1823 */           uri = "";
/*      */         } else {
/* 1825 */           throw new XmlPullParserException("could not determine namespace bound to element prefix " + prefix, this, null);
/*      */         } 
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1831 */       this.elUri[this.depth] = uri;
/*      */ 
/*      */ 
/*      */       
/*      */       int i;
/*      */ 
/*      */ 
/*      */       
/* 1839 */       for (i = 0; i < this.attributeCount; i++) {
/*      */         
/* 1841 */         String attrPrefix = this.attributePrefix[i];
/* 1842 */         if (attrPrefix != null) {
/* 1843 */           String attrUri = getNamespace(attrPrefix);
/* 1844 */           if (attrUri == null) {
/* 1845 */             throw new XmlPullParserException("could not determine namespace bound to attribute prefix " + attrPrefix, this, null);
/*      */           }
/*      */ 
/*      */ 
/*      */           
/* 1850 */           this.attributeUri[i] = attrUri;
/*      */         } else {
/* 1852 */           this.attributeUri[i] = "";
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1860 */       for (i = 1; i < this.attributeCount; i++)
/*      */       {
/* 1862 */         for (int j = 0; j < i; j++)
/*      */         {
/* 1864 */           if (this.attributeUri[j] == this.attributeUri[i] && ((this.allStringsInterned && this.attributeName[j].equals(this.attributeName[i])) || (!this.allStringsInterned && this.attributeNameHash[j] == this.attributeNameHash[i] && this.attributeName[j].equals(this.attributeName[i]))))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1872 */             String attr1 = this.attributeName[j];
/* 1873 */             if (this.attributeUri[j] != null) attr1 = this.attributeUri[j] + ":" + attr1; 
/* 1874 */             String attr2 = this.attributeName[i];
/* 1875 */             if (this.attributeUri[i] != null) attr2 = this.attributeUri[i] + ":" + attr2; 
/* 1876 */             throw new XmlPullParserException("duplicated attributes " + attr1 + " and " + attr2, this, null);
/*      */           
/*      */           }
/*      */         
/*      */         }
/*      */       
/*      */       }
/*      */     
/*      */     }
/*      */     else {
/*      */       
/* 1887 */       for (int i = 1; i < this.attributeCount; i++) {
/*      */         
/* 1889 */         for (int j = 0; j < i; j++) {
/*      */           
/* 1891 */           if ((this.allStringsInterned && this.attributeName[j].equals(this.attributeName[i])) || (!this.allStringsInterned && this.attributeNameHash[j] == this.attributeNameHash[i] && this.attributeName[j].equals(this.attributeName[i]))) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1898 */             String attr1 = this.attributeName[j];
/* 1899 */             String attr2 = this.attributeName[i];
/* 1900 */             throw new XmlPullParserException("duplicated attributes " + attr1 + " and " + attr2, this, null);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1907 */     this.elNamespaceCount[this.depth] = this.namespaceEnd;
/* 1908 */     this.posEnd = this.pos;
/* 1909 */     return this.eventType = 2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char parseAttribute() throws XmlPullParserException, IOException {
/* 1918 */     int prevPosStart = this.posStart + this.bufAbsoluteStart;
/* 1919 */     int nameStart = this.pos - 1 + this.bufAbsoluteStart;
/* 1920 */     int colonPos = -1;
/* 1921 */     char ch = this.buf[this.pos - 1];
/* 1922 */     if (ch == ':' && this.processNamespaces) throw new XmlPullParserException("when namespaces processing enabled colon can not be at attribute name start", this, null);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1927 */     boolean startsWithXmlns = (this.processNamespaces && ch == 'x');
/* 1928 */     int xmlnsPos = 0;
/*      */     
/* 1930 */     ch = more();
/* 1931 */     while (isNameChar(ch)) {
/* 1932 */       if (this.processNamespaces) {
/* 1933 */         if (startsWithXmlns && xmlnsPos < 5) {
/* 1934 */           xmlnsPos++;
/* 1935 */           if (xmlnsPos == 1) { if (ch != 'm') startsWithXmlns = false;  }
/* 1936 */           else if (xmlnsPos == 2) { if (ch != 'l') startsWithXmlns = false;  }
/* 1937 */           else if (xmlnsPos == 3) { if (ch != 'n') startsWithXmlns = false;  }
/* 1938 */           else if (xmlnsPos == 4) { if (ch != 's') startsWithXmlns = false;  }
/* 1939 */           else if (xmlnsPos == 5 && 
/* 1940 */             ch != ':') { throw new XmlPullParserException("after xmlns in attribute name must be colonwhen namespaces are enabled", this, null); }
/*      */         
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1946 */         if (ch == ':') {
/* 1947 */           if (colonPos != -1) throw new XmlPullParserException("only one colon is allowed in attribute name when namespaces are enabled", this, null);
/*      */ 
/*      */           
/* 1950 */           colonPos = this.pos - 1 + this.bufAbsoluteStart;
/*      */         } 
/*      */       } 
/* 1953 */       ch = more();
/*      */     } 
/*      */     
/* 1956 */     ensureAttributesCapacity(this.attributeCount);
/*      */ 
/*      */     
/* 1959 */     String name = null;
/* 1960 */     String prefix = null;
/*      */     
/* 1962 */     if (this.processNamespaces) {
/* 1963 */       if (xmlnsPos < 4) startsWithXmlns = false; 
/* 1964 */       if (startsWithXmlns) {
/* 1965 */         if (colonPos != -1)
/*      */         {
/* 1967 */           int nameLen = this.pos - 2 - colonPos - this.bufAbsoluteStart;
/* 1968 */           if (nameLen == 0) {
/* 1969 */             throw new XmlPullParserException("namespace prefix is required after xmlns:  when namespaces are enabled", this, null);
/*      */           }
/*      */ 
/*      */           
/* 1973 */           name = newString(this.buf, colonPos - this.bufAbsoluteStart + 1, nameLen);
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/* 1978 */         if (colonPos != -1) {
/* 1979 */           int prefixLen = colonPos - nameStart;
/* 1980 */           prefix = this.attributePrefix[this.attributeCount] = newString(this.buf, nameStart - this.bufAbsoluteStart, prefixLen);
/*      */ 
/*      */           
/* 1983 */           int nameLen = this.pos - 2 - colonPos - this.bufAbsoluteStart;
/* 1984 */           name = this.attributeName[this.attributeCount] = newString(this.buf, colonPos - this.bufAbsoluteStart + 1, nameLen);
/*      */         
/*      */         }
/*      */         else {
/*      */ 
/*      */           
/* 1990 */           prefix = this.attributePrefix[this.attributeCount] = null;
/* 1991 */           name = this.attributeName[this.attributeCount] = newString(this.buf, nameStart - this.bufAbsoluteStart, this.pos - 1 - nameStart - this.bufAbsoluteStart);
/*      */         } 
/*      */ 
/*      */         
/* 1995 */         if (!this.allStringsInterned) {
/* 1996 */           this.attributeNameHash[this.attributeCount] = name.hashCode();
/*      */         }
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/* 2002 */       name = this.attributeName[this.attributeCount] = newString(this.buf, nameStart - this.bufAbsoluteStart, this.pos - 1 - nameStart - this.bufAbsoluteStart);
/*      */ 
/*      */ 
/*      */       
/* 2006 */       if (!this.allStringsInterned) {
/* 2007 */         this.attributeNameHash[this.attributeCount] = name.hashCode();
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 2012 */     for (; isS(ch); ch = more());
/* 2013 */     if (ch != '=') throw new XmlPullParserException("expected = after attribute name", this, null);
/*      */     
/* 2015 */     ch = more();
/* 2016 */     for (; isS(ch); ch = more());
/*      */ 
/*      */ 
/*      */     
/* 2020 */     char delimit = ch;
/* 2021 */     if (delimit != '"' && delimit != '\'') throw new XmlPullParserException("attribute value must start with quotation or apostrophe not " + printable(delimit), this, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2029 */     boolean normalizedCR = false;
/* 2030 */     this.usePC = false;
/* 2031 */     this.pcStart = this.pcEnd;
/* 2032 */     this.posStart = this.pos;
/*      */     
/*      */     while (true) {
/* 2035 */       ch = more();
/* 2036 */       if (ch == delimit)
/*      */         break; 
/* 2038 */       if (ch == '<') {
/* 2039 */         throw new XmlPullParserException("markup not allowed inside attribute value - illegal < ", this, null);
/*      */       }
/* 2041 */       if (ch == '&') {
/*      */         
/* 2043 */         this.posEnd = this.pos - 1;
/* 2044 */         if (!this.usePC) {
/* 2045 */           boolean hadCharData = (this.posEnd > this.posStart);
/* 2046 */           if (hadCharData) {
/*      */             
/* 2048 */             joinPC();
/*      */           } else {
/* 2050 */             this.usePC = true;
/* 2051 */             this.pcStart = this.pcEnd = 0;
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/* 2056 */         char[] resolvedEntity = parseEntityRef();
/*      */         
/* 2058 */         if (resolvedEntity == null) {
/* 2059 */           if (this.entityRefName == null) {
/* 2060 */             this.entityRefName = newString(this.buf, this.posStart, this.posEnd - this.posStart);
/*      */           }
/* 2062 */           throw new XmlPullParserException("could not resolve entity named '" + printable(this.entityRefName) + "'", this, null);
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 2067 */         for (int i = 0; i < resolvedEntity.length; i++) {
/*      */           
/* 2069 */           if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2070 */           this.pc[this.pcEnd++] = resolvedEntity[i];
/*      */         } 
/* 2072 */       } else if (ch == '\t' || ch == '\n' || ch == '\r') {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2077 */         if (!this.usePC) {
/* 2078 */           this.posEnd = this.pos - 1;
/* 2079 */           if (this.posEnd > this.posStart) {
/* 2080 */             joinPC();
/*      */           } else {
/* 2082 */             this.usePC = true;
/* 2083 */             this.pcEnd = this.pcStart = 0;
/*      */           } 
/*      */         } 
/*      */         
/* 2087 */         if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2088 */         if (ch != '\n' || !normalizedCR) {
/* 2089 */           this.pc[this.pcEnd++] = ' ';
/*      */         
/*      */         }
/*      */       }
/* 2093 */       else if (this.usePC) {
/* 2094 */         if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2095 */         this.pc[this.pcEnd++] = ch;
/*      */       } 
/*      */       
/* 2098 */       normalizedCR = (ch == '\r');
/*      */     } 
/*      */ 
/*      */     
/* 2102 */     if (this.processNamespaces && startsWithXmlns) {
/* 2103 */       String ns = null;
/* 2104 */       if (!this.usePC) {
/* 2105 */         ns = newStringIntern(this.buf, this.posStart, this.pos - 1 - this.posStart);
/*      */       } else {
/* 2107 */         ns = newStringIntern(this.pc, this.pcStart, this.pcEnd - this.pcStart);
/*      */       } 
/* 2109 */       ensureNamespacesCapacity(this.namespaceEnd);
/* 2110 */       int prefixHash = -1;
/* 2111 */       if (colonPos != -1) {
/* 2112 */         if (ns.length() == 0) {
/* 2113 */           throw new XmlPullParserException("non-default namespace can not be declared to be empty string", this, null);
/*      */         }
/*      */ 
/*      */         
/* 2117 */         this.namespacePrefix[this.namespaceEnd] = name;
/* 2118 */         if (!this.allStringsInterned) {
/* 2119 */           prefixHash = this.namespacePrefixHash[this.namespaceEnd] = name.hashCode();
/*      */         }
/*      */       } else {
/*      */         
/* 2123 */         this.namespacePrefix[this.namespaceEnd] = null;
/* 2124 */         if (!this.allStringsInterned) {
/* 2125 */           prefixHash = this.namespacePrefixHash[this.namespaceEnd] = -1;
/*      */         }
/*      */       } 
/* 2128 */       this.namespaceUri[this.namespaceEnd] = ns;
/*      */ 
/*      */       
/* 2131 */       int startNs = this.elNamespaceCount[this.depth - 1];
/* 2132 */       for (int i = this.namespaceEnd - 1; i >= startNs; i--) {
/*      */         
/* 2134 */         if (((this.allStringsInterned || name == null) && this.namespacePrefix[i] == name) || (!this.allStringsInterned && name != null && this.namespacePrefixHash[i] == prefixHash && name.equals(this.namespacePrefix[i]))) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2140 */           String s = (name == null) ? "default" : ("'" + name + "'");
/* 2141 */           throw new XmlPullParserException("duplicated namespace declaration for " + s + " prefix", this, null);
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 2146 */       this.namespaceEnd++;
/*      */     } else {
/*      */       
/* 2149 */       if (!this.usePC) {
/* 2150 */         this.attributeValue[this.attributeCount] = new String(this.buf, this.posStart, this.pos - 1 - this.posStart);
/*      */       } else {
/*      */         
/* 2153 */         this.attributeValue[this.attributeCount] = new String(this.pc, this.pcStart, this.pcEnd - this.pcStart);
/*      */       } 
/*      */       
/* 2156 */       this.attributeCount++;
/*      */     } 
/* 2158 */     this.posStart = prevPosStart - this.bufAbsoluteStart;
/* 2159 */     return ch;
/*      */   }
/*      */   public MXParser() {
/* 2162 */     this.charRefOneCharBuf = new char[1];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] parseEntityRef() throws XmlPullParserException, IOException {
/* 2171 */     this.entityRefName = null;
/* 2172 */     this.posStart = this.pos;
/* 2173 */     char ch = more();
/* 2174 */     StringBuffer sb = new StringBuffer();
/* 2175 */     if (ch == '#') {
/*      */       
/* 2177 */       char charRef = Character.MIN_VALUE;
/* 2178 */       ch = more();
/* 2179 */       if (ch == 'x') {
/*      */         
/*      */         while (true) {
/* 2182 */           ch = more();
/* 2183 */           if (ch >= '0' && ch <= '9') {
/* 2184 */             charRef = (char)(charRef * 16 + ch - 48);
/* 2185 */             sb.append(ch); continue;
/* 2186 */           }  if (ch >= 'a' && ch <= 'f') {
/* 2187 */             charRef = (char)(charRef * 16 + ch - 87);
/* 2188 */             sb.append(ch); continue;
/* 2189 */           }  if (ch >= 'A' && ch <= 'F')
/* 2190 */           { charRef = (char)(charRef * 16 + ch - 55);
/* 2191 */             sb.append(ch); continue; }  break;
/* 2192 */         }  if (ch != ';')
/*      */         {
/*      */           
/* 2195 */           throw new XmlPullParserException("character reference (with hex value) may not contain " + printable(ch), this, null);
/*      */         
/*      */         }
/*      */       }
/*      */       else {
/*      */         
/*      */         while (true) {
/*      */           
/* 2203 */           if (ch >= '0' && ch <= '9')
/* 2204 */           { charRef = (char)(charRef * 10 + ch - 48); }
/* 2205 */           else { if (ch == ';') {
/*      */               break;
/*      */             }
/* 2208 */             throw new XmlPullParserException("character reference (with decimal value) may not contain " + printable(ch), this, null); }
/*      */ 
/*      */ 
/*      */           
/* 2212 */           ch = more();
/*      */         } 
/*      */       } 
/* 2215 */       this.posEnd = this.pos - 1;
/* 2216 */       if (sb.length() > 0) {
/*      */         
/* 2218 */         char[] tmp = toChars(Integer.parseInt(sb.toString(), 16));
/* 2219 */         this.charRefOneCharBuf = tmp;
/* 2220 */         if (this.tokenize)
/*      */         {
/* 2222 */           this.text = newString(this.charRefOneCharBuf, 0, this.charRefOneCharBuf.length);
/*      */         }
/* 2224 */         return this.charRefOneCharBuf;
/*      */       } 
/* 2226 */       this.charRefOneCharBuf[0] = charRef;
/* 2227 */       if (this.tokenize) {
/* 2228 */         this.text = newString(this.charRefOneCharBuf, 0, 1);
/*      */       }
/* 2230 */       return this.charRefOneCharBuf;
/*      */     } 
/*      */ 
/*      */     
/* 2234 */     if (!isNameStartChar(ch)) {
/* 2235 */       throw new XmlPullParserException("entity reference names can not start with character '" + printable(ch) + "'", this, null);
/*      */     }
/*      */ 
/*      */     
/*      */     while (true) {
/* 2240 */       ch = more();
/* 2241 */       if (ch == ';') {
/*      */         break;
/*      */       }
/* 2244 */       if (!isNameChar(ch)) {
/* 2245 */         throw new XmlPullParserException("entity reference name can not contain character " + printable(ch) + "'", this, null);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 2250 */     this.posEnd = this.pos - 1;
/*      */     
/* 2252 */     int len = this.posEnd - this.posStart;
/* 2253 */     if (len == 2 && this.buf[this.posStart] == 'l' && this.buf[this.posStart + 1] == 't') {
/* 2254 */       if (this.tokenize) {
/* 2255 */         this.text = "<";
/*      */       }
/* 2257 */       this.charRefOneCharBuf[0] = '<';
/* 2258 */       return this.charRefOneCharBuf;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2263 */     if (len == 3 && this.buf[this.posStart] == 'a' && this.buf[this.posStart + 1] == 'm' && this.buf[this.posStart + 2] == 'p') {
/*      */       
/* 2265 */       if (this.tokenize) {
/* 2266 */         this.text = "&";
/*      */       }
/* 2268 */       this.charRefOneCharBuf[0] = '&';
/* 2269 */       return this.charRefOneCharBuf;
/* 2270 */     }  if (len == 2 && this.buf[this.posStart] == 'g' && this.buf[this.posStart + 1] == 't') {
/* 2271 */       if (this.tokenize) {
/* 2272 */         this.text = ">";
/*      */       }
/* 2274 */       this.charRefOneCharBuf[0] = '>';
/* 2275 */       return this.charRefOneCharBuf;
/* 2276 */     }  if (len == 4 && this.buf[this.posStart] == 'a' && this.buf[this.posStart + 1] == 'p' && this.buf[this.posStart + 2] == 'o' && this.buf[this.posStart + 3] == 's') {
/*      */ 
/*      */       
/* 2279 */       if (this.tokenize) {
/* 2280 */         this.text = "'";
/*      */       }
/* 2282 */       this.charRefOneCharBuf[0] = '\'';
/* 2283 */       return this.charRefOneCharBuf;
/* 2284 */     }  if (len == 4 && this.buf[this.posStart] == 'q' && this.buf[this.posStart + 1] == 'u' && this.buf[this.posStart + 2] == 'o' && this.buf[this.posStart + 3] == 't') {
/*      */ 
/*      */       
/* 2287 */       if (this.tokenize) {
/* 2288 */         this.text = "\"";
/*      */       }
/* 2290 */       this.charRefOneCharBuf[0] = '"';
/* 2291 */       return this.charRefOneCharBuf;
/*      */     } 
/* 2293 */     char[] result = lookuEntityReplacement(len);
/* 2294 */     if (result != null) {
/* 2295 */       return result;
/*      */     }
/*      */     
/* 2298 */     if (this.tokenize) this.text = null; 
/* 2299 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char[] lookuEntityReplacement(int entitNameLen) throws XmlPullParserException, IOException {
/* 2307 */     if (!this.allStringsInterned) {
/* 2308 */       int hash = fastHash(this.buf, this.posStart, this.posEnd - this.posStart);
/*      */       int i;
/* 2310 */       label30: for (i = this.entityEnd - 1; i >= 0; i--) {
/*      */         
/* 2312 */         if (hash == this.entityNameHash[i] && entitNameLen == (this.entityNameBuf[i]).length) {
/* 2313 */           char[] entityBuf = this.entityNameBuf[i];
/* 2314 */           for (int j = 0; j < entitNameLen; j++) {
/*      */             
/* 2316 */             if (this.buf[this.posStart + j] != entityBuf[j])
/*      */               continue label30; 
/* 2318 */           }  if (this.tokenize) this.text = this.entityReplacement[i]; 
/* 2319 */           return this.entityReplacementBuf[i];
/*      */         } 
/*      */       } 
/*      */     } else {
/* 2323 */       this.entityRefName = newString(this.buf, this.posStart, this.posEnd - this.posStart);
/* 2324 */       for (int i = this.entityEnd - 1; i >= 0; i--) {
/*      */ 
/*      */         
/* 2327 */         if (this.entityRefName == this.entityName[i]) {
/* 2328 */           if (this.tokenize) this.text = this.entityReplacement[i]; 
/* 2329 */           return this.entityReplacementBuf[i];
/*      */         } 
/*      */       } 
/*      */     } 
/* 2333 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void parseComment() throws XmlPullParserException, IOException {
/* 2343 */     char ch = more();
/* 2344 */     if (ch != '-') throw new XmlPullParserException("expected <!-- for comment start", this, null);
/*      */     
/* 2346 */     if (this.tokenize) this.posStart = this.pos;
/*      */     
/* 2348 */     int curLine = this.lineNumber;
/* 2349 */     int curColumn = this.columnNumber;
/*      */     try {
/* 2351 */       boolean normalizeIgnorableWS = (this.tokenize == true && !this.roundtripSupported);
/* 2352 */       boolean normalizedCR = false;
/*      */       
/* 2354 */       boolean seenDash = false;
/* 2355 */       boolean seenDashDash = false;
/*      */       
/*      */       while (true) {
/* 2358 */         ch = more();
/* 2359 */         if (seenDashDash && ch != '>') {
/* 2360 */           throw new XmlPullParserException("in comment after two dashes (--) next character must be > not " + printable(ch), this, null);
/*      */         }
/*      */ 
/*      */         
/* 2364 */         if (ch == '-') {
/* 2365 */           if (!seenDash) {
/* 2366 */             seenDash = true;
/*      */           } else {
/* 2368 */             seenDashDash = true;
/* 2369 */             seenDash = false;
/*      */           } 
/* 2371 */         } else if (ch == '>') {
/* 2372 */           if (seenDashDash) {
/*      */             break;
/*      */           }
/* 2375 */           seenDashDash = false;
/*      */           
/* 2377 */           seenDash = false;
/*      */         } else {
/* 2379 */           seenDash = false;
/*      */         } 
/* 2381 */         if (normalizeIgnorableWS) {
/* 2382 */           if (ch == '\r') {
/* 2383 */             normalizedCR = true;
/*      */ 
/*      */ 
/*      */             
/* 2387 */             if (!this.usePC) {
/* 2388 */               this.posEnd = this.pos - 1;
/* 2389 */               if (this.posEnd > this.posStart) {
/* 2390 */                 joinPC();
/*      */               } else {
/* 2392 */                 this.usePC = true;
/* 2393 */                 this.pcStart = this.pcEnd = 0;
/*      */               } 
/*      */             } 
/*      */             
/* 2397 */             if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2398 */             this.pc[this.pcEnd++] = '\n'; continue;
/* 2399 */           }  if (ch == '\n') {
/* 2400 */             if (!normalizedCR && this.usePC) {
/* 2401 */               if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2402 */               this.pc[this.pcEnd++] = '\n';
/*      */             } 
/* 2404 */             normalizedCR = false; continue;
/*      */           } 
/* 2406 */           if (this.usePC) {
/* 2407 */             if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2408 */             this.pc[this.pcEnd++] = ch;
/*      */           } 
/* 2410 */           normalizedCR = false;
/*      */         }
/*      */       
/*      */       }
/*      */     
/* 2415 */     } catch (EOFException ex) {
/*      */       
/* 2417 */       throw new XmlPullParserException("comment started on line " + curLine + " and column " + curColumn + " was not closed", this, ex);
/*      */     } 
/*      */ 
/*      */     
/* 2421 */     if (this.tokenize) {
/* 2422 */       this.posEnd = this.pos - 3;
/* 2423 */       if (this.usePC) {
/* 2424 */         this.pcEnd -= 2;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean parsePI() throws XmlPullParserException, IOException {
/* 2437 */     if (this.tokenize) this.posStart = this.pos; 
/* 2438 */     int curLine = this.lineNumber;
/* 2439 */     int curColumn = this.columnNumber;
/* 2440 */     int piTargetStart = this.pos + this.bufAbsoluteStart;
/* 2441 */     int piTargetEnd = -1;
/* 2442 */     boolean normalizeIgnorableWS = (this.tokenize == true && !this.roundtripSupported);
/* 2443 */     boolean normalizedCR = false;
/*      */     
/*      */     try {
/* 2446 */       boolean seenQ = false;
/* 2447 */       char ch = more();
/* 2448 */       if (isS(ch)) {
/* 2449 */         throw new XmlPullParserException("processing instruction PITarget must be exactly after <? and not white space character", this, null);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       while (true) {
/* 2457 */         if (ch == '?') {
/* 2458 */           seenQ = true;
/* 2459 */         } else if (ch == '>') {
/* 2460 */           if (seenQ) {
/*      */             break;
/*      */           }
/* 2463 */           seenQ = false;
/*      */         } else {
/* 2465 */           if (piTargetEnd == -1 && isS(ch)) {
/* 2466 */             piTargetEnd = this.pos - 1 + this.bufAbsoluteStart;
/*      */ 
/*      */             
/* 2469 */             if (piTargetEnd - piTargetStart == 3 && (
/* 2470 */               this.buf[piTargetStart] == 'x' || this.buf[piTargetStart] == 'X') && (this.buf[piTargetStart + 1] == 'm' || this.buf[piTargetStart + 1] == 'M') && (this.buf[piTargetStart + 2] == 'l' || this.buf[piTargetStart + 2] == 'L')) {
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 2475 */               if (piTargetStart > 3) {
/* 2476 */                 throw new XmlPullParserException("processing instruction can not have PITarget with reserveld xml name", this, null);
/*      */               }
/*      */ 
/*      */               
/* 2480 */               if (this.buf[piTargetStart] != 'x' && this.buf[piTargetStart + 1] != 'm' && this.buf[piTargetStart + 2] != 'l')
/*      */               {
/*      */ 
/*      */                 
/* 2484 */                 throw new XmlPullParserException("XMLDecl must have xml name in lowercase", this, null);
/*      */               }
/*      */ 
/*      */ 
/*      */               
/* 2489 */               parseXmlDecl(ch);
/* 2490 */               if (this.tokenize) this.posEnd = this.pos - 2; 
/* 2491 */               int off = piTargetStart - this.bufAbsoluteStart + 3;
/* 2492 */               int len = this.pos - 2 - off;
/* 2493 */               this.xmlDeclContent = newString(this.buf, off, len);
/* 2494 */               return false;
/*      */             } 
/*      */           } 
/*      */           
/* 2498 */           seenQ = false;
/*      */         } 
/* 2500 */         if (normalizeIgnorableWS) {
/* 2501 */           if (ch == '\r') {
/* 2502 */             normalizedCR = true;
/*      */ 
/*      */ 
/*      */             
/* 2506 */             if (!this.usePC) {
/* 2507 */               this.posEnd = this.pos - 1;
/* 2508 */               if (this.posEnd > this.posStart) {
/* 2509 */                 joinPC();
/*      */               } else {
/* 2511 */                 this.usePC = true;
/* 2512 */                 this.pcStart = this.pcEnd = 0;
/*      */               } 
/*      */             } 
/*      */             
/* 2516 */             if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2517 */             this.pc[this.pcEnd++] = '\n';
/* 2518 */           } else if (ch == '\n') {
/* 2519 */             if (!normalizedCR && this.usePC) {
/* 2520 */               if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2521 */               this.pc[this.pcEnd++] = '\n';
/*      */             } 
/* 2523 */             normalizedCR = false;
/*      */           } else {
/* 2525 */             if (this.usePC) {
/* 2526 */               if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2527 */               this.pc[this.pcEnd++] = ch;
/*      */             } 
/* 2529 */             normalizedCR = false;
/*      */           } 
/*      */         }
/* 2532 */         ch = more();
/*      */       } 
/* 2534 */     } catch (EOFException ex) {
/*      */       
/* 2536 */       throw new XmlPullParserException("processing instruction started on line " + curLine + " and column " + curColumn + " was not closed", this, ex);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2541 */     if (piTargetEnd == -1) {
/* 2542 */       piTargetEnd = this.pos - 2 + this.bufAbsoluteStart;
/*      */     }
/*      */ 
/*      */     
/* 2546 */     piTargetStart -= this.bufAbsoluteStart;
/* 2547 */     piTargetEnd -= this.bufAbsoluteStart;
/* 2548 */     if (this.tokenize) {
/* 2549 */       this.posEnd = this.pos - 2;
/* 2550 */       if (normalizeIgnorableWS) {
/* 2551 */         this.pcEnd--;
/*      */       }
/*      */     } 
/* 2554 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2563 */   protected static final char[] VERSION = "version".toCharArray();
/* 2564 */   protected static final char[] NCODING = "ncoding".toCharArray();
/* 2565 */   protected static final char[] TANDALONE = "tandalone".toCharArray();
/* 2566 */   protected static final char[] YES = "yes".toCharArray();
/* 2567 */   protected static final char[] NO = "no".toCharArray();
/*      */ 
/*      */   
/*      */   protected static final int LOOKUP_MAX = 1024;
/*      */ 
/*      */   
/*      */   protected static final char LOOKUP_MAX_CHAR = 'Ѐ';
/*      */ 
/*      */   
/*      */   protected void parseXmlDecl(char ch) throws XmlPullParserException, IOException {
/* 2577 */     this.preventBufferCompaction = true;
/* 2578 */     this.bufStart = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2584 */     ch = skipS(ch);
/* 2585 */     ch = requireInput(ch, VERSION);
/*      */     
/* 2587 */     ch = skipS(ch);
/* 2588 */     if (ch != '=') {
/* 2589 */       throw new XmlPullParserException("expected equals sign (=) after version and not " + printable(ch), this, null);
/*      */     }
/*      */     
/* 2592 */     ch = more();
/* 2593 */     ch = skipS(ch);
/* 2594 */     if (ch != '\'' && ch != '"') {
/* 2595 */       throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after version and not " + printable(ch), this, null);
/*      */     }
/*      */ 
/*      */     
/* 2599 */     char quotChar = ch;
/*      */     
/* 2601 */     int versionStart = this.pos;
/* 2602 */     ch = more();
/*      */     
/* 2604 */     while (ch != quotChar) {
/* 2605 */       if ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && (ch < '0' || ch > '9') && ch != '_' && ch != '.' && ch != ':' && ch != '-')
/*      */       {
/*      */         
/* 2608 */         throw new XmlPullParserException("<?xml version value expected to be in ([a-zA-Z0-9_.:] | '-') not " + printable(ch), this, null);
/*      */       }
/*      */ 
/*      */       
/* 2612 */       ch = more();
/*      */     } 
/* 2614 */     int versionEnd = this.pos - 1;
/* 2615 */     parseXmlDeclWithVersion(versionStart, versionEnd);
/* 2616 */     this.preventBufferCompaction = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void parseXmlDeclWithVersion(int versionStart, int versionEnd) throws XmlPullParserException, IOException {
/* 2624 */     if (versionEnd - versionStart != 3 || this.buf[versionStart] != '1' || this.buf[versionStart + 1] != '.' || this.buf[versionStart + 2] != '0')
/*      */     {
/*      */ 
/*      */ 
/*      */       
/* 2629 */       throw new XmlPullParserException("only 1.0 is supported as <?xml version not '" + printable(new String(this.buf, versionStart, versionEnd - versionStart)) + "'", this, null);
/*      */     }
/*      */ 
/*      */     
/* 2633 */     this.xmlDeclVersion = newString(this.buf, versionStart, versionEnd - versionStart);
/*      */ 
/*      */     
/* 2636 */     char ch = more();
/* 2637 */     ch = skipS(ch);
/* 2638 */     if (ch == 'e') {
/* 2639 */       ch = more();
/* 2640 */       ch = requireInput(ch, NCODING);
/* 2641 */       ch = skipS(ch);
/* 2642 */       if (ch != '=') {
/* 2643 */         throw new XmlPullParserException("expected equals sign (=) after encoding and not " + printable(ch), this, null);
/*      */       }
/*      */       
/* 2646 */       ch = more();
/* 2647 */       ch = skipS(ch);
/* 2648 */       if (ch != '\'' && ch != '"') {
/* 2649 */         throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after encoding and not " + printable(ch), this, null);
/*      */       }
/*      */ 
/*      */       
/* 2653 */       char quotChar = ch;
/* 2654 */       int encodingStart = this.pos;
/* 2655 */       ch = more();
/*      */       
/* 2657 */       if ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z'))
/*      */       {
/* 2659 */         throw new XmlPullParserException("<?xml encoding name expected to start with [A-Za-z] not " + printable(ch), this, null);
/*      */       }
/*      */ 
/*      */       
/* 2663 */       ch = more();
/* 2664 */       while (ch != quotChar) {
/* 2665 */         if ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && (ch < '0' || ch > '9') && ch != '.' && ch != '_' && ch != '-')
/*      */         {
/*      */           
/* 2668 */           throw new XmlPullParserException("<?xml encoding value expected to be in ([A-Za-z0-9._] | '-') not " + printable(ch), this, null);
/*      */         }
/*      */ 
/*      */         
/* 2672 */         ch = more();
/*      */       } 
/* 2674 */       int encodingEnd = this.pos - 1;
/*      */ 
/*      */ 
/*      */       
/* 2678 */       this.inputEncoding = newString(this.buf, encodingStart, encodingEnd - encodingStart);
/* 2679 */       ch = more();
/*      */     } 
/*      */     
/* 2682 */     ch = skipS(ch);
/*      */     
/* 2684 */     if (ch == 's') {
/* 2685 */       ch = more();
/* 2686 */       ch = requireInput(ch, TANDALONE);
/* 2687 */       ch = skipS(ch);
/* 2688 */       if (ch != '=') {
/* 2689 */         throw new XmlPullParserException("expected equals sign (=) after standalone and not " + printable(ch), this, null);
/*      */       }
/*      */ 
/*      */       
/* 2693 */       ch = more();
/* 2694 */       ch = skipS(ch);
/* 2695 */       if (ch != '\'' && ch != '"') {
/* 2696 */         throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after encoding and not " + printable(ch), this, null);
/*      */       }
/*      */ 
/*      */       
/* 2700 */       char quotChar = ch;
/* 2701 */       int standaloneStart = this.pos;
/* 2702 */       ch = more();
/* 2703 */       if (ch == 'y') {
/* 2704 */         ch = requireInput(ch, YES);
/*      */         
/* 2706 */         this.xmlDeclStandalone = new Boolean(true);
/* 2707 */       } else if (ch == 'n') {
/* 2708 */         ch = requireInput(ch, NO);
/*      */         
/* 2710 */         this.xmlDeclStandalone = new Boolean(false);
/*      */       } else {
/* 2712 */         throw new XmlPullParserException("expected 'yes' or 'no' after standalone and not " + printable(ch), this, null);
/*      */       } 
/*      */ 
/*      */       
/* 2716 */       if (ch != quotChar) {
/* 2717 */         throw new XmlPullParserException("expected " + quotChar + " after standalone value not " + printable(ch), this, null);
/*      */       }
/*      */ 
/*      */       
/* 2721 */       ch = more();
/*      */     } 
/*      */ 
/*      */     
/* 2725 */     ch = skipS(ch);
/* 2726 */     if (ch != '?') {
/* 2727 */       throw new XmlPullParserException("expected ?> as last part of <?xml not " + printable(ch), this, null);
/*      */     }
/*      */ 
/*      */     
/* 2731 */     ch = more();
/* 2732 */     if (ch != '>') {
/* 2733 */       throw new XmlPullParserException("expected ?> as last part of <?xml not " + printable(ch), this, null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void parseDocdecl() throws XmlPullParserException, IOException {
/* 2743 */     char ch = more();
/* 2744 */     if (ch != 'O') throw new XmlPullParserException("expected <!DOCTYPE", this, null);
/*      */     
/* 2746 */     ch = more();
/* 2747 */     if (ch != 'C') throw new XmlPullParserException("expected <!DOCTYPE", this, null);
/*      */     
/* 2749 */     ch = more();
/* 2750 */     if (ch != 'T') throw new XmlPullParserException("expected <!DOCTYPE", this, null);
/*      */     
/* 2752 */     ch = more();
/* 2753 */     if (ch != 'Y') throw new XmlPullParserException("expected <!DOCTYPE", this, null);
/*      */     
/* 2755 */     ch = more();
/* 2756 */     if (ch != 'P') throw new XmlPullParserException("expected <!DOCTYPE", this, null);
/*      */     
/* 2758 */     ch = more();
/* 2759 */     if (ch != 'E') throw new XmlPullParserException("expected <!DOCTYPE", this, null);
/*      */     
/* 2761 */     this.posStart = this.pos;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2766 */     int bracketLevel = 0;
/* 2767 */     boolean normalizeIgnorableWS = (this.tokenize == true && !this.roundtripSupported);
/* 2768 */     boolean normalizedCR = false;
/*      */     while (true) {
/* 2770 */       ch = more();
/* 2771 */       if (ch == '[') bracketLevel++; 
/* 2772 */       if (ch == ']') bracketLevel--; 
/* 2773 */       if (ch == '>' && bracketLevel == 0)
/* 2774 */         break;  if (normalizeIgnorableWS) {
/* 2775 */         if (ch == '\r') {
/* 2776 */           normalizedCR = true;
/*      */ 
/*      */ 
/*      */           
/* 2780 */           if (!this.usePC) {
/* 2781 */             this.posEnd = this.pos - 1;
/* 2782 */             if (this.posEnd > this.posStart) {
/* 2783 */               joinPC();
/*      */             } else {
/* 2785 */               this.usePC = true;
/* 2786 */               this.pcStart = this.pcEnd = 0;
/*      */             } 
/*      */           } 
/*      */           
/* 2790 */           if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2791 */           this.pc[this.pcEnd++] = '\n'; continue;
/* 2792 */         }  if (ch == '\n') {
/* 2793 */           if (!normalizedCR && this.usePC) {
/* 2794 */             if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2795 */             this.pc[this.pcEnd++] = '\n';
/*      */           } 
/* 2797 */           normalizedCR = false; continue;
/*      */         } 
/* 2799 */         if (this.usePC) {
/* 2800 */           if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2801 */           this.pc[this.pcEnd++] = ch;
/*      */         } 
/* 2803 */         normalizedCR = false;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 2808 */     this.posEnd = this.pos - 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void parseCDSect(boolean hadCharData) throws XmlPullParserException, IOException {
/* 2822 */     char ch = more();
/* 2823 */     if (ch != 'C') throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
/*      */     
/* 2825 */     ch = more();
/* 2826 */     if (ch != 'D') throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
/*      */     
/* 2828 */     ch = more();
/* 2829 */     if (ch != 'A') throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
/*      */     
/* 2831 */     ch = more();
/* 2832 */     if (ch != 'T') throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
/*      */     
/* 2834 */     ch = more();
/* 2835 */     if (ch != 'A') throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
/*      */     
/* 2837 */     ch = more();
/* 2838 */     if (ch != '[') throw new XmlPullParserException("expected <![CDATA[ for comment start", this, null);
/*      */ 
/*      */ 
/*      */     
/* 2842 */     int cdStart = this.pos + this.bufAbsoluteStart;
/* 2843 */     int curLine = this.lineNumber;
/* 2844 */     int curColumn = this.columnNumber;
/* 2845 */     boolean normalizeInput = (!this.tokenize || !this.roundtripSupported);
/*      */     try {
/* 2847 */       if (normalizeInput && 
/* 2848 */         hadCharData && 
/* 2849 */         !this.usePC)
/*      */       {
/* 2851 */         if (this.posEnd > this.posStart) {
/* 2852 */           joinPC();
/*      */         } else {
/* 2854 */           this.usePC = true;
/* 2855 */           this.pcStart = this.pcEnd = 0;
/*      */         } 
/*      */       }
/*      */ 
/*      */       
/* 2860 */       boolean seenBracket = false;
/* 2861 */       boolean seenBracketBracket = false;
/* 2862 */       boolean normalizedCR = false;
/*      */       
/*      */       while (true) {
/* 2865 */         ch = more();
/* 2866 */         if (ch == ']') {
/* 2867 */           if (!seenBracket) {
/* 2868 */             seenBracket = true;
/*      */           } else {
/* 2870 */             seenBracketBracket = true;
/*      */           }
/*      */         
/* 2873 */         } else if (ch == '>') {
/* 2874 */           if (seenBracket && seenBracketBracket) {
/*      */             break;
/*      */           }
/* 2877 */           seenBracketBracket = false;
/*      */           
/* 2879 */           seenBracket = false;
/*      */         }
/* 2881 */         else if (seenBracket) {
/* 2882 */           seenBracket = false;
/*      */         } 
/*      */         
/* 2885 */         if (normalizeInput)
/*      */         {
/* 2887 */           if (ch == '\r') {
/* 2888 */             normalizedCR = true;
/* 2889 */             this.posStart = cdStart - this.bufAbsoluteStart;
/* 2890 */             this.posEnd = this.pos - 1;
/* 2891 */             if (!this.usePC) {
/* 2892 */               if (this.posEnd > this.posStart) {
/* 2893 */                 joinPC();
/*      */               } else {
/* 2895 */                 this.usePC = true;
/* 2896 */                 this.pcStart = this.pcEnd = 0;
/*      */               } 
/*      */             }
/*      */             
/* 2900 */             if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2901 */             this.pc[this.pcEnd++] = '\n'; continue;
/* 2902 */           }  if (ch == '\n') {
/* 2903 */             if (!normalizedCR && this.usePC) {
/* 2904 */               if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2905 */               this.pc[this.pcEnd++] = '\n';
/*      */             } 
/* 2907 */             normalizedCR = false; continue;
/*      */           } 
/* 2909 */           if (this.usePC) {
/* 2910 */             if (this.pcEnd >= this.pc.length) ensurePC(this.pcEnd); 
/* 2911 */             this.pc[this.pcEnd++] = ch;
/*      */           } 
/* 2913 */           normalizedCR = false;
/*      */         }
/*      */       
/*      */       } 
/* 2917 */     } catch (EOFException ex) {
/*      */       
/* 2919 */       throw new XmlPullParserException("CDATA section started on line " + curLine + " and column " + curColumn + " was not closed", this, ex);
/*      */     } 
/*      */ 
/*      */     
/* 2923 */     if (normalizeInput && 
/* 2924 */       this.usePC) {
/* 2925 */       this.pcEnd -= 2;
/*      */     }
/*      */     
/* 2928 */     this.posStart = cdStart - this.bufAbsoluteStart;
/* 2929 */     this.posEnd = this.pos - 3;
/*      */   }
/*      */   
/*      */   protected void fillBuf() throws IOException, XmlPullParserException {
/* 2933 */     if (this.reader == null) throw new XmlPullParserException("reader must be set before parsing is started");
/*      */ 
/*      */ 
/*      */     
/* 2937 */     if (this.bufEnd > this.bufSoftLimit) {
/*      */ 
/*      */       
/* 2940 */       boolean compact = (this.bufStart > this.bufSoftLimit);
/* 2941 */       boolean expand = false;
/* 2942 */       if (this.preventBufferCompaction) {
/* 2943 */         compact = false;
/* 2944 */         expand = true;
/* 2945 */       } else if (!compact) {
/*      */         
/* 2947 */         if (this.bufStart < this.buf.length / 2) {
/*      */           
/* 2949 */           expand = true;
/*      */         } else {
/*      */           
/* 2952 */           compact = true;
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 2957 */       if (compact) {
/*      */ 
/*      */         
/* 2960 */         System.arraycopy(this.buf, this.bufStart, this.buf, 0, this.bufEnd - this.bufStart);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/* 2968 */       else if (expand) {
/* 2969 */         int newSize = 2 * this.buf.length;
/* 2970 */         char[] newBuf = new char[newSize];
/*      */         
/* 2972 */         System.arraycopy(this.buf, this.bufStart, newBuf, 0, this.bufEnd - this.bufStart);
/* 2973 */         this.buf = newBuf;
/* 2974 */         if (this.bufLoadFactor > 0) {
/* 2975 */           this.bufSoftLimit = this.bufLoadFactor * this.buf.length / 100;
/*      */         }
/*      */       } else {
/*      */         
/* 2979 */         throw new XmlPullParserException("internal error in fillBuffer()");
/*      */       } 
/* 2981 */       this.bufEnd -= this.bufStart;
/* 2982 */       this.pos -= this.bufStart;
/* 2983 */       this.posStart -= this.bufStart;
/* 2984 */       this.posEnd -= this.bufStart;
/* 2985 */       this.bufAbsoluteStart += this.bufStart;
/* 2986 */       this.bufStart = 0;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2993 */     int len = (this.buf.length - this.bufEnd > 8192) ? 8192 : (this.buf.length - this.bufEnd);
/* 2994 */     int ret = this.reader.read(this.buf, this.bufEnd, len);
/* 2995 */     if (ret > 0) {
/* 2996 */       this.bufEnd += ret;
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */ 
/*      */     
/* 3003 */     if (ret == -1) {
/* 3004 */       if (this.bufAbsoluteStart == 0 && this.pos == 0) {
/* 3005 */         throw new EOFException("input contained no data");
/*      */       }
/* 3007 */       if (this.seenRoot && this.depth == 0) {
/* 3008 */         this.reachedEnd = true;
/*      */         return;
/*      */       } 
/* 3011 */       StringBuffer expectedTagStack = new StringBuffer();
/* 3012 */       if (this.depth > 0) {
/*      */ 
/*      */         
/* 3015 */         expectedTagStack.append(" - expected end tag");
/* 3016 */         if (this.depth > 1) {
/* 3017 */           expectedTagStack.append("s");
/*      */         }
/* 3019 */         expectedTagStack.append(" "); int i;
/* 3020 */         for (i = this.depth; i > 0; i--) {
/*      */           
/* 3022 */           String tagName = new String(this.elRawName[i], 0, this.elRawNameEnd[i]);
/* 3023 */           expectedTagStack.append("</").append(tagName).append('>');
/*      */         } 
/* 3025 */         expectedTagStack.append(" to close");
/* 3026 */         for (i = this.depth; i > 0; i--) {
/*      */           
/* 3028 */           if (i != this.depth) {
/* 3029 */             expectedTagStack.append(" and");
/*      */           }
/* 3031 */           String tagName = new String(this.elRawName[i], 0, this.elRawNameEnd[i]);
/* 3032 */           expectedTagStack.append(" start tag <" + tagName + ">");
/* 3033 */           expectedTagStack.append(" from line " + this.elRawNameLine[i]);
/*      */         } 
/* 3035 */         expectedTagStack.append(", parser stopped on");
/*      */       } 
/* 3037 */       throw new EOFException("no more data available" + expectedTagStack.toString() + getPositionDescription());
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 3042 */     throw new IOException("error reading input, returned " + ret);
/*      */   }
/*      */ 
/*      */   
/*      */   protected char more() throws IOException, XmlPullParserException {
/* 3047 */     if (this.pos >= this.bufEnd) {
/* 3048 */       fillBuf();
/*      */       
/* 3050 */       if (this.reachedEnd) return Character.MAX_VALUE; 
/*      */     } 
/* 3052 */     char ch = this.buf[this.pos++];
/*      */     
/* 3054 */     if (ch == '\n') { this.lineNumber++; this.columnNumber = 1; }
/* 3055 */     else { this.columnNumber++; }
/*      */     
/* 3057 */     return ch;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void ensurePC(int end) {
/* 3072 */     int newSize = (end > 8192) ? (2 * end) : 16384;
/* 3073 */     char[] newPC = new char[newSize];
/*      */     
/* 3075 */     System.arraycopy(this.pc, 0, newPC, 0, this.pcEnd);
/* 3076 */     this.pc = newPC;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void joinPC() {
/* 3083 */     int len = this.posEnd - this.posStart;
/* 3084 */     int newEnd = this.pcEnd + len + 1;
/* 3085 */     if (newEnd >= this.pc.length) ensurePC(newEnd);
/*      */     
/* 3087 */     System.arraycopy(this.buf, this.posStart, this.pc, this.pcEnd, len);
/* 3088 */     this.pcEnd += len;
/* 3089 */     this.usePC = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected char requireInput(char ch, char[] input) throws XmlPullParserException, IOException {
/* 3096 */     for (int i = 0; i < input.length; i++) {
/*      */       
/* 3098 */       if (ch != input[i]) {
/* 3099 */         throw new XmlPullParserException("expected " + printable(input[i]) + " in " + new String(input) + " and not " + printable(ch), this, null);
/*      */       }
/*      */ 
/*      */       
/* 3103 */       ch = more();
/*      */     } 
/* 3105 */     return ch;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected char requireNextS() throws XmlPullParserException, IOException {
/* 3111 */     char ch = more();
/* 3112 */     if (!isS(ch)) {
/* 3113 */       throw new XmlPullParserException("white space is required and not " + printable(ch), this, null);
/*      */     }
/*      */     
/* 3116 */     return skipS(ch);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected char skipS(char ch) throws XmlPullParserException, IOException {
/* 3122 */     for (; isS(ch); ch = more());
/* 3123 */     return ch;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 3131 */   protected static boolean[] lookupNameStartChar = new boolean[1024];
/* 3132 */   protected static boolean[] lookupNameChar = new boolean[1024]; private static final char MIN_HIGH_SURROGATE = '?'; private static final char MAX_HIGH_SURROGATE = '?'; private static final int MIN_CODE_POINT = 0; private static final int MAX_CODE_POINT = 1114111;
/*      */   private static final int MIN_SUPPLEMENTARY_CODE_POINT = 65536;
/*      */   
/*      */   private static final void setName(char ch) {
/* 3136 */     lookupNameChar[ch] = true;
/*      */   }
/*      */   private static final void setNameStart(char ch) {
/* 3139 */     lookupNameStartChar[ch] = true; setName(ch);
/*      */   }
/*      */   static {
/* 3142 */     setNameStart(':'); char ch;
/* 3143 */     for (ch = 'A'; ch <= 'Z'; ) { setNameStart(ch); ch = (char)(ch + 1); }
/* 3144 */      setNameStart('_');
/* 3145 */     for (ch = 'a'; ch <= 'z'; ) { setNameStart(ch); ch = (char)(ch + 1); }
/* 3146 */      for (ch = 'À'; ch <= '˿'; ) { setNameStart(ch); ch = (char)(ch + 1); }
/* 3147 */      for (ch = 'Ͱ'; ch <= 'ͽ'; ) { setNameStart(ch); ch = (char)(ch + 1); }
/* 3148 */      for (ch = 'Ϳ'; ch < 'Ѐ'; ) { setNameStart(ch); ch = (char)(ch + 1); }
/*      */     
/* 3150 */     setName('-');
/* 3151 */     setName('.');
/* 3152 */     for (ch = '0'; ch <= '9'; ) { setName(ch); ch = (char)(ch + 1); }
/* 3153 */      setName('·');
/* 3154 */     for (ch = '̀'; ch <= 'ͯ'; ) { setName(ch); ch = (char)(ch + 1); }
/*      */   
/*      */   }
/*      */   
/*      */   protected boolean isNameStartChar(char ch) {
/* 3159 */     return ((ch < 'Ѐ' && lookupNameStartChar[ch]) || (ch >= 'Ѐ' && ch <= '‧') || (ch >= '‪' && ch <= '↏') || (ch >= '⠀' && ch <= '￯'));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isNameChar(char ch) {
/* 3193 */     return ((ch < 'Ѐ' && lookupNameChar[ch]) || (ch >= 'Ѐ' && ch <= '‧') || (ch >= '‪' && ch <= '↏') || (ch >= '⠀' && ch <= '￯'));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isS(char ch) {
/* 3215 */     return (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String printable(char ch) {
/* 3225 */     if (ch == '\n')
/* 3226 */       return "\\n"; 
/* 3227 */     if (ch == '\r')
/* 3228 */       return "\\r"; 
/* 3229 */     if (ch == '\t')
/* 3230 */       return "\\t"; 
/* 3231 */     if (ch == '\'')
/* 3232 */       return "\\'"; 
/* 3233 */     if (ch > '' || ch < ' ') {
/* 3234 */       return "\\u" + Integer.toHexString(ch);
/*      */     }
/* 3236 */     return "" + ch;
/*      */   }
/*      */   
/*      */   protected String printable(String s) {
/* 3240 */     if (s == null) return null; 
/* 3241 */     int sLen = s.length();
/* 3242 */     StringBuffer buf = new StringBuffer(sLen + 10);
/* 3243 */     for (int i = 0; i < sLen; i++) {
/* 3244 */       buf.append(printable(s.charAt(i)));
/*      */     }
/* 3246 */     s = buf.toString();
/* 3247 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int toCodePoint(char high, char low) {
/* 3259 */     int h = (high & 0x3FF) << 10;
/* 3260 */     int l = low & 0x3FF;
/* 3261 */     return (h | l) + 65536;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean isHighSurrogate(char ch) {
/* 3269 */     return ('?' <= ch && '?' >= ch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean isValidCodePoint(int codePoint) {
/* 3278 */     return (0 <= codePoint && 1114111 >= codePoint);
/*      */   }
/*      */ 
/*      */   
/*      */   private static boolean isSupplementaryCodePoint(int codePoint) {
/* 3283 */     return (65536 <= codePoint && 1114111 >= codePoint);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static char[] toChars(int codePoint) {
/* 3294 */     if (!isValidCodePoint(codePoint))
/*      */     {
/* 3296 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/* 3299 */     if (isSupplementaryCodePoint(codePoint)) {
/*      */       
/* 3301 */       int cpPrime = codePoint - 65536;
/* 3302 */       int high = 0xD800 | cpPrime >> 10 & 0x3FF;
/* 3303 */       int low = 0xDC00 | cpPrime & 0x3FF;
/* 3304 */       return new char[] { (char)high, (char)low };
/*      */     } 
/*      */     
/* 3307 */     return new char[] { (char)codePoint };
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\pull\MXParser.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */