/*      */ package org.codehaus.plexus.util.xml.pull;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.Writer;
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
/*      */ public class MXSerializer
/*      */   implements XmlSerializer
/*      */ {
/*      */   protected static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
/*      */   protected static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
/*      */   private static final boolean TRACE_SIZING = false;
/*   32 */   protected final String FEATURE_SERIALIZER_ATTVALUE_USE_APOSTROPHE = "http://xmlpull.org/v1/doc/features.html#serializer-attvalue-use-apostrophe";
/*      */   
/*   34 */   protected final String FEATURE_NAMES_INTERNED = "http://xmlpull.org/v1/doc/features.html#names-interned";
/*      */   
/*   36 */   protected final String PROPERTY_SERIALIZER_INDENTATION = "http://xmlpull.org/v1/doc/properties.html#serializer-indentation";
/*      */   
/*   38 */   protected final String PROPERTY_SERIALIZER_LINE_SEPARATOR = "http://xmlpull.org/v1/doc/properties.html#serializer-line-separator";
/*      */   
/*      */   protected static final String PROPERTY_LOCATION = "http://xmlpull.org/v1/doc/properties.html#location";
/*      */   
/*      */   protected boolean namesInterned;
/*      */   
/*      */   protected boolean attributeUseApostrophe;
/*      */   
/*   46 */   protected String indentationString = null;
/*   47 */   protected String lineSeparator = "\n";
/*      */   
/*      */   protected String location;
/*      */   
/*      */   protected Writer out;
/*      */   
/*      */   protected int autoDeclaredPrefixes;
/*   54 */   protected int depth = 0;
/*      */ 
/*      */   
/*   57 */   protected String[] elNamespace = new String[2];
/*   58 */   protected String[] elName = new String[this.elNamespace.length];
/*   59 */   protected int[] elNamespaceCount = new int[this.elNamespace.length];
/*      */ 
/*      */   
/*   62 */   protected int namespaceEnd = 0;
/*   63 */   protected String[] namespacePrefix = new String[8];
/*   64 */   protected String[] namespaceUri = new String[this.namespacePrefix.length];
/*      */   
/*      */   protected boolean finished;
/*      */   
/*      */   protected boolean pastRoot;
/*      */   
/*      */   protected boolean setPrefixCalled;
/*      */   
/*      */   protected boolean startTagIncomplete;
/*      */   
/*      */   protected boolean doIndent;
/*      */   protected boolean seenTag;
/*      */   protected boolean seenBracket;
/*      */   protected boolean seenBracketBracket;
/*   78 */   private static final int BUF_LEN = (Runtime.getRuntime().freeMemory() > 1000000L) ? 8192 : 256;
/*   79 */   protected char[] buf = new char[BUF_LEN];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   85 */   protected static final String[] precomputedPrefixes = new String[32]; static {
/*   86 */     for (int i = 0; i < precomputedPrefixes.length; i++)
/*      */     {
/*   88 */       precomputedPrefixes[i] = ("n" + i).intern(); } 
/*      */   }
/*      */   private boolean checkNamesInterned = false;
/*      */   protected int offsetNewLine;
/*      */   protected int indentationJump;
/*      */   
/*      */   private void checkInterning(String name) {
/*   95 */     if (this.namesInterned && name != name.intern())
/*   96 */       throw new IllegalArgumentException("all names passed as arguments must be internedwhen NAMES INTERNED feature is enabled"); 
/*      */   }
/*      */   protected char[] indentationBuf; protected int maxIndentLevel;
/*      */   protected boolean writeLineSepartor;
/*      */   protected boolean writeIndentation;
/*      */   
/*      */   protected void reset() {
/*  103 */     this.location = null;
/*  104 */     this.out = null;
/*  105 */     this.autoDeclaredPrefixes = 0;
/*  106 */     this.depth = 0;
/*      */ 
/*      */     
/*  109 */     for (int i = 0; i < this.elNamespaceCount.length; i++) {
/*      */       
/*  111 */       this.elName[i] = null;
/*  112 */       this.elNamespace[i] = null;
/*  113 */       this.elNamespaceCount[i] = 2;
/*      */     } 
/*      */ 
/*      */     
/*  117 */     this.namespaceEnd = 0;
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
/*  128 */     this.namespacePrefix[this.namespaceEnd] = "xmlns";
/*  129 */     this.namespaceUri[this.namespaceEnd] = "http://www.w3.org/2000/xmlns/";
/*  130 */     this.namespaceEnd++;
/*      */     
/*  132 */     this.namespacePrefix[this.namespaceEnd] = "xml";
/*  133 */     this.namespaceUri[this.namespaceEnd] = "http://www.w3.org/XML/1998/namespace";
/*  134 */     this.namespaceEnd++;
/*      */     
/*  136 */     this.finished = false;
/*  137 */     this.pastRoot = false;
/*  138 */     this.setPrefixCalled = false;
/*  139 */     this.startTagIncomplete = false;
/*      */     
/*  141 */     this.seenTag = false;
/*      */     
/*  143 */     this.seenBracket = false;
/*  144 */     this.seenBracketBracket = false;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void ensureElementsCapacity() {
/*  149 */     int elStackSize = this.elName.length;
/*      */ 
/*      */     
/*  152 */     int newSize = ((this.depth >= 7) ? (2 * this.depth) : 8) + 2;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  157 */     boolean needsCopying = (elStackSize > 0);
/*  158 */     String[] arr = null;
/*      */     
/*  160 */     arr = new String[newSize];
/*  161 */     if (needsCopying) System.arraycopy(this.elName, 0, arr, 0, elStackSize); 
/*  162 */     this.elName = arr;
/*  163 */     arr = new String[newSize];
/*  164 */     if (needsCopying) System.arraycopy(this.elNamespace, 0, arr, 0, elStackSize); 
/*  165 */     this.elNamespace = arr;
/*      */     
/*  167 */     int[] iarr = new int[newSize];
/*  168 */     if (needsCopying) {
/*  169 */       System.arraycopy(this.elNamespaceCount, 0, iarr, 0, elStackSize);
/*      */     } else {
/*      */       
/*  172 */       iarr[0] = 0;
/*      */     } 
/*  174 */     this.elNamespaceCount = iarr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void ensureNamespacesCapacity() {
/*  183 */     int newSize = (this.namespaceEnd > 7) ? (2 * this.namespaceEnd) : 8;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  188 */     String[] newNamespacePrefix = new String[newSize];
/*  189 */     String[] newNamespaceUri = new String[newSize];
/*  190 */     if (this.namespacePrefix != null) {
/*  191 */       System.arraycopy(this.namespacePrefix, 0, newNamespacePrefix, 0, this.namespaceEnd);
/*      */       
/*  193 */       System.arraycopy(this.namespaceUri, 0, newNamespaceUri, 0, this.namespaceEnd);
/*      */     } 
/*      */     
/*  196 */     this.namespacePrefix = newNamespacePrefix;
/*  197 */     this.namespaceUri = newNamespaceUri;
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
/*      */   public void setFeature(String name, boolean state) throws IllegalArgumentException, IllegalStateException {
/*  217 */     if (name == null) {
/*  218 */       throw new IllegalArgumentException("feature name can not be null");
/*      */     }
/*  220 */     if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name)) {
/*  221 */       this.namesInterned = state;
/*  222 */     } else if ("http://xmlpull.org/v1/doc/features.html#serializer-attvalue-use-apostrophe".equals(name)) {
/*  223 */       this.attributeUseApostrophe = state;
/*      */     } else {
/*  225 */       throw new IllegalStateException("unsupported feature " + name);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean getFeature(String name) throws IllegalArgumentException {
/*  231 */     if (name == null) {
/*  232 */       throw new IllegalArgumentException("feature name can not be null");
/*      */     }
/*  234 */     if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name))
/*  235 */       return this.namesInterned; 
/*  236 */     if ("http://xmlpull.org/v1/doc/features.html#serializer-attvalue-use-apostrophe".equals(name)) {
/*  237 */       return this.attributeUseApostrophe;
/*      */     }
/*  239 */     return false;
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
/*      */   protected void rebuildIndentationBuf() {
/*  256 */     if (!this.doIndent)
/*  257 */       return;  int maxIndent = 65;
/*  258 */     int bufSize = 0;
/*  259 */     this.offsetNewLine = 0;
/*  260 */     if (this.writeLineSepartor) {
/*  261 */       this.offsetNewLine = this.lineSeparator.length();
/*  262 */       bufSize += this.offsetNewLine;
/*      */     } 
/*  264 */     this.maxIndentLevel = 0;
/*  265 */     if (this.writeIndentation) {
/*  266 */       this.indentationJump = this.indentationString.length();
/*  267 */       this.maxIndentLevel = 65 / this.indentationJump;
/*  268 */       bufSize += this.maxIndentLevel * this.indentationJump;
/*      */     } 
/*  270 */     if (this.indentationBuf == null || this.indentationBuf.length < bufSize) {
/*  271 */       this.indentationBuf = new char[bufSize + 8];
/*      */     }
/*  273 */     int bufPos = 0;
/*  274 */     if (this.writeLineSepartor) {
/*  275 */       for (int i = 0; i < this.lineSeparator.length(); i++)
/*      */       {
/*  277 */         this.indentationBuf[bufPos++] = this.lineSeparator.charAt(i);
/*      */       }
/*      */     }
/*  280 */     if (this.writeIndentation) {
/*  281 */       for (int i = 0; i < this.maxIndentLevel; i++) {
/*      */         
/*  283 */         for (int j = 0; j < this.indentationString.length(); j++)
/*      */         {
/*  285 */           this.indentationBuf[bufPos++] = this.indentationString.charAt(j);
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void writeIndent() throws IOException {
/*  293 */     int start = this.writeLineSepartor ? 0 : this.offsetNewLine;
/*  294 */     int level = (this.depth > this.maxIndentLevel) ? this.maxIndentLevel : this.depth;
/*  295 */     this.out.write(this.indentationBuf, start, level * this.indentationJump + this.offsetNewLine);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setProperty(String name, Object value) throws IllegalArgumentException, IllegalStateException {
/*  301 */     if (name == null) {
/*  302 */       throw new IllegalArgumentException("property name can not be null");
/*      */     }
/*  304 */     if ("http://xmlpull.org/v1/doc/properties.html#serializer-indentation".equals(name)) {
/*  305 */       this.indentationString = (String)value;
/*  306 */     } else if ("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator".equals(name)) {
/*  307 */       this.lineSeparator = (String)value;
/*  308 */     } else if ("http://xmlpull.org/v1/doc/properties.html#location".equals(name)) {
/*  309 */       this.location = (String)value;
/*      */     } else {
/*  311 */       throw new IllegalStateException("unsupported property " + name);
/*      */     } 
/*  313 */     this.writeLineSepartor = (this.lineSeparator != null && this.lineSeparator.length() > 0);
/*  314 */     this.writeIndentation = (this.indentationString != null && this.indentationString.length() > 0);
/*      */     
/*  316 */     this.doIndent = (this.indentationString != null && (this.writeLineSepartor || this.writeIndentation));
/*      */ 
/*      */     
/*  319 */     rebuildIndentationBuf();
/*  320 */     this.seenTag = false;
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getProperty(String name) throws IllegalArgumentException {
/*  325 */     if (name == null) {
/*  326 */       throw new IllegalArgumentException("property name can not be null");
/*      */     }
/*  328 */     if ("http://xmlpull.org/v1/doc/properties.html#serializer-indentation".equals(name))
/*  329 */       return this.indentationString; 
/*  330 */     if ("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator".equals(name))
/*  331 */       return this.lineSeparator; 
/*  332 */     if ("http://xmlpull.org/v1/doc/properties.html#location".equals(name)) {
/*  333 */       return this.location;
/*      */     }
/*  335 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private String getLocation() {
/*  340 */     return (this.location != null) ? (" @" + this.location) : "";
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Writer getWriter() {
/*  346 */     return this.out;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setOutput(Writer writer) {
/*  351 */     reset();
/*  352 */     this.out = writer;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setOutput(OutputStream os, String encoding) throws IOException {
/*  357 */     if (os == null) throw new IllegalArgumentException("output stream can not be null"); 
/*  358 */     reset();
/*  359 */     if (encoding != null) {
/*  360 */       this.out = new OutputStreamWriter(os, encoding);
/*      */     } else {
/*  362 */       this.out = new OutputStreamWriter(os);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void startDocument(String encoding, Boolean standalone) throws IOException {
/*  368 */     char apos = this.attributeUseApostrophe ? '\'' : '"';
/*  369 */     if (this.attributeUseApostrophe) {
/*  370 */       this.out.write("<?xml version='1.0'");
/*      */     } else {
/*  372 */       this.out.write("<?xml version=\"1.0\"");
/*      */     } 
/*  374 */     if (encoding != null) {
/*  375 */       this.out.write(" encoding=");
/*  376 */       this.out.write(this.attributeUseApostrophe ? 39 : 34);
/*  377 */       this.out.write(encoding);
/*  378 */       this.out.write(this.attributeUseApostrophe ? 39 : 34);
/*      */     } 
/*      */     
/*  381 */     if (standalone != null) {
/*  382 */       this.out.write(" standalone=");
/*  383 */       this.out.write(this.attributeUseApostrophe ? 39 : 34);
/*  384 */       if (standalone.booleanValue()) {
/*  385 */         this.out.write("yes");
/*      */       } else {
/*  387 */         this.out.write("no");
/*      */       } 
/*  389 */       this.out.write(this.attributeUseApostrophe ? 39 : 34);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  396 */     this.out.write("?>");
/*  397 */     if (this.writeLineSepartor) {
/*  398 */       this.out.write(this.lineSeparator);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void endDocument() throws IOException {
/*  405 */     while (this.depth > 0) {
/*  406 */       endTag(this.elNamespace[this.depth], this.elName[this.depth]);
/*      */     }
/*  408 */     if (this.writeLineSepartor) {
/*  409 */       this.out.write(this.lineSeparator);
/*      */     }
/*      */ 
/*      */     
/*  413 */     this.finished = this.pastRoot = this.startTagIncomplete = true;
/*  414 */     this.out.flush();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setPrefix(String prefix, String namespace) throws IOException {
/*  419 */     if (this.startTagIncomplete) closeStartTag();
/*      */ 
/*      */     
/*  422 */     if (prefix == null) {
/*  423 */       prefix = "";
/*      */     }
/*  425 */     if (!this.namesInterned) {
/*  426 */       prefix = prefix.intern();
/*  427 */     } else if (this.checkNamesInterned) {
/*  428 */       checkInterning(prefix);
/*  429 */     } else if (prefix == null) {
/*  430 */       throw new IllegalArgumentException("prefix must be not null" + getLocation());
/*      */     } 
/*      */ 
/*      */     
/*  434 */     for (int i = this.elNamespaceCount[this.depth]; i < this.namespaceEnd; i++) {
/*      */       
/*  436 */       if (prefix == this.namespacePrefix[i]) {
/*  437 */         throw new IllegalStateException("duplicated prefix " + printable(prefix) + getLocation());
/*      */       }
/*      */     } 
/*      */     
/*  441 */     if (!this.namesInterned) {
/*  442 */       namespace = namespace.intern();
/*  443 */     } else if (this.checkNamesInterned) {
/*  444 */       checkInterning(namespace);
/*  445 */     } else if (namespace == null) {
/*  446 */       throw new IllegalArgumentException("namespace must be not null" + getLocation());
/*      */     } 
/*      */     
/*  449 */     if (this.namespaceEnd >= this.namespacePrefix.length) {
/*  450 */       ensureNamespacesCapacity();
/*      */     }
/*  452 */     this.namespacePrefix[this.namespaceEnd] = prefix;
/*  453 */     this.namespaceUri[this.namespaceEnd] = namespace;
/*  454 */     this.namespaceEnd++;
/*  455 */     this.setPrefixCalled = true;
/*      */   }
/*      */   
/*      */   protected String lookupOrDeclarePrefix(String namespace) {
/*  459 */     return getPrefix(namespace, true);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPrefix(String namespace, boolean generatePrefix) {
/*  465 */     if (!this.namesInterned) {
/*      */       
/*  467 */       namespace = namespace.intern();
/*  468 */     } else if (this.checkNamesInterned) {
/*  469 */       checkInterning(namespace);
/*      */     } 
/*      */     
/*  472 */     if (namespace == null)
/*  473 */       throw new IllegalArgumentException("namespace must be not null" + getLocation()); 
/*  474 */     if (namespace.length() == 0) {
/*  475 */       throw new IllegalArgumentException("default namespace cannot have prefix" + getLocation());
/*      */     }
/*      */ 
/*      */     
/*  479 */     for (int i = this.namespaceEnd - 1; i >= 0; i--) {
/*      */       
/*  481 */       if (namespace == this.namespaceUri[i]) {
/*  482 */         String prefix = this.namespacePrefix[i];
/*      */         
/*  484 */         for (int p = this.namespaceEnd - 1; p > i; p--) {
/*      */           
/*  486 */           if (prefix == this.namespacePrefix[p]);
/*      */         } 
/*      */         
/*  489 */         return prefix;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  494 */     if (!generatePrefix) {
/*  495 */       return null;
/*      */     }
/*  497 */     return generatePrefix(namespace);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private String generatePrefix(String namespace) {
/*  503 */     this.autoDeclaredPrefixes++;
/*      */     
/*  505 */     String prefix = (this.autoDeclaredPrefixes < precomputedPrefixes.length) ? precomputedPrefixes[this.autoDeclaredPrefixes] : ("n" + this.autoDeclaredPrefixes).intern();
/*      */ 
/*      */     
/*  508 */     for (int i = this.namespaceEnd - 1; i >= 0; i--) {
/*      */       
/*  510 */       if (prefix == this.namespacePrefix[i]);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  516 */     if (this.namespaceEnd >= this.namespacePrefix.length) {
/*  517 */       ensureNamespacesCapacity();
/*      */     }
/*  519 */     this.namespacePrefix[this.namespaceEnd] = prefix;
/*  520 */     this.namespaceUri[this.namespaceEnd] = namespace;
/*  521 */     this.namespaceEnd++;
/*      */     
/*  523 */     return prefix;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDepth() {
/*  529 */     return this.depth;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getNamespace() {
/*  534 */     return this.elNamespace[this.depth];
/*      */   }
/*      */ 
/*      */   
/*      */   public String getName() {
/*  539 */     return this.elName[this.depth];
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public XmlSerializer startTag(String namespace, String name) throws IOException {
/*  545 */     if (this.startTagIncomplete) {
/*  546 */       closeStartTag();
/*      */     }
/*  548 */     this.seenBracket = this.seenBracketBracket = false;
/*  549 */     if (this.doIndent && this.depth > 0 && this.seenTag) {
/*  550 */       writeIndent();
/*      */     }
/*  552 */     this.seenTag = true;
/*  553 */     this.setPrefixCalled = false;
/*  554 */     this.startTagIncomplete = true;
/*  555 */     this.depth++;
/*  556 */     if (this.depth + 1 >= this.elName.length) {
/*  557 */       ensureElementsCapacity();
/*      */     }
/*      */ 
/*      */     
/*  561 */     if (this.checkNamesInterned && this.namesInterned) checkInterning(namespace); 
/*  562 */     this.elNamespace[this.depth] = (this.namesInterned || namespace == null) ? namespace : namespace.intern();
/*      */ 
/*      */     
/*  565 */     if (this.checkNamesInterned && this.namesInterned) checkInterning(name); 
/*  566 */     this.elName[this.depth] = (this.namesInterned || name == null) ? name : name.intern();
/*  567 */     if (this.out == null) {
/*  568 */       throw new IllegalStateException("setOutput() must called set before serialization can start");
/*      */     }
/*  570 */     this.out.write(60);
/*  571 */     if (namespace != null)
/*      */     {
/*  573 */       if (namespace.length() > 0) {
/*      */         
/*  575 */         String prefix = null;
/*  576 */         if (this.depth > 0 && this.namespaceEnd - this.elNamespaceCount[this.depth - 1] == 1) {
/*      */           
/*  578 */           String uri = this.namespaceUri[this.namespaceEnd - 1];
/*  579 */           if (uri == namespace || uri.equals(namespace)) {
/*  580 */             String elPfx = this.namespacePrefix[this.namespaceEnd - 1];
/*      */             
/*  582 */             for (int pos = this.elNamespaceCount[this.depth - 1] - 1; pos >= 2; pos--) {
/*  583 */               String pf = this.namespacePrefix[pos];
/*  584 */               if (pf == elPfx || pf.equals(elPfx)) {
/*  585 */                 String n = this.namespaceUri[pos];
/*  586 */                 if (n == uri || n.equals(uri)) {
/*  587 */                   this.namespaceEnd--;
/*  588 */                   prefix = elPfx;
/*      */                 } 
/*      */                 break;
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*  595 */         if (prefix == null) {
/*  596 */           prefix = lookupOrDeclarePrefix(namespace);
/*      */         }
/*      */ 
/*      */         
/*  600 */         if (prefix.length() > 0) {
/*  601 */           this.out.write(prefix);
/*  602 */           this.out.write(58);
/*      */         } 
/*      */       } else {
/*      */         
/*  606 */         for (int i = this.namespaceEnd - 1; i >= 0; i--) {
/*      */           
/*  608 */           if (this.namespacePrefix[i] == "") {
/*  609 */             String uri = this.namespaceUri[i];
/*  610 */             if (uri == null) {
/*      */               
/*  612 */               setPrefix("", ""); break;
/*  613 */             }  if (uri.length() > 0) {
/*  614 */               throw new IllegalStateException("start tag can not be written in empty default namespace as default namespace is currently bound to '" + uri + "'" + getLocation());
/*      */             }
/*      */ 
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*  624 */     this.out.write(name);
/*  625 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public XmlSerializer attribute(String namespace, String name, String value) throws IOException {
/*  631 */     if (!this.startTagIncomplete) {
/*  632 */       throw new IllegalArgumentException("startTag() must be called before attribute()" + getLocation());
/*      */     }
/*      */     
/*  635 */     this.out.write(32);
/*      */     
/*  637 */     if (namespace != null && namespace.length() > 0) {
/*      */       
/*  639 */       if (!this.namesInterned) {
/*  640 */         namespace = namespace.intern();
/*  641 */       } else if (this.checkNamesInterned) {
/*  642 */         checkInterning(namespace);
/*      */       } 
/*  644 */       String prefix = lookupOrDeclarePrefix(namespace);
/*      */       
/*  646 */       if (prefix.length() == 0)
/*      */       {
/*      */         
/*  649 */         prefix = generatePrefix(namespace);
/*      */       }
/*  651 */       this.out.write(prefix);
/*  652 */       this.out.write(58);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  659 */     this.out.write(name);
/*  660 */     this.out.write(61);
/*      */     
/*  662 */     this.out.write(this.attributeUseApostrophe ? 39 : 34);
/*  663 */     writeAttributeValue(value, this.out);
/*  664 */     this.out.write(this.attributeUseApostrophe ? 39 : 34);
/*  665 */     return this;
/*      */   }
/*      */   
/*      */   protected void closeStartTag() throws IOException {
/*  669 */     if (this.finished) {
/*  670 */       throw new IllegalArgumentException("trying to write past already finished output" + getLocation());
/*      */     }
/*  672 */     if (this.seenBracket) {
/*  673 */       this.seenBracket = this.seenBracketBracket = false;
/*      */     }
/*  675 */     if (this.startTagIncomplete || this.setPrefixCalled) {
/*  676 */       if (this.setPrefixCalled) {
/*  677 */         throw new IllegalArgumentException("startTag() must be called immediately after setPrefix()" + getLocation());
/*      */       }
/*      */       
/*  680 */       if (!this.startTagIncomplete) {
/*  681 */         throw new IllegalArgumentException("trying to close start tag that is not opened" + getLocation());
/*      */       }
/*      */ 
/*      */       
/*  685 */       writeNamespaceDeclarations();
/*  686 */       this.out.write(62);
/*  687 */       this.elNamespaceCount[this.depth] = this.namespaceEnd;
/*  688 */       this.startTagIncomplete = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeNamespaceDeclarations() throws IOException {
/*  695 */     for (int i = this.elNamespaceCount[this.depth - 1]; i < this.namespaceEnd; i++) {
/*      */       
/*  697 */       if (this.doIndent && this.namespaceUri[i].length() > 40) {
/*  698 */         writeIndent();
/*  699 */         this.out.write(" ");
/*      */       } 
/*  701 */       if (this.namespacePrefix[i] != "") {
/*  702 */         this.out.write(" xmlns:");
/*  703 */         this.out.write(this.namespacePrefix[i]);
/*  704 */         this.out.write(61);
/*      */       } else {
/*  706 */         this.out.write(" xmlns=");
/*      */       } 
/*  708 */       this.out.write(this.attributeUseApostrophe ? 39 : 34);
/*      */ 
/*      */       
/*  711 */       writeAttributeValue(this.namespaceUri[i], this.out);
/*      */       
/*  713 */       this.out.write(this.attributeUseApostrophe ? 39 : 34);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public XmlSerializer endTag(String namespace, String name) throws IOException {
/*  724 */     this.seenBracket = this.seenBracketBracket = false;
/*  725 */     if (namespace != null) {
/*  726 */       if (!this.namesInterned) {
/*  727 */         namespace = namespace.intern();
/*  728 */       } else if (this.checkNamesInterned) {
/*  729 */         checkInterning(namespace);
/*      */       } 
/*      */     }
/*      */     
/*  733 */     if (namespace != this.elNamespace[this.depth])
/*      */     {
/*  735 */       throw new IllegalArgumentException("expected namespace " + printable(this.elNamespace[this.depth]) + " and not " + printable(namespace) + getLocation());
/*      */     }
/*      */ 
/*      */     
/*  739 */     if (name == null) {
/*  740 */       throw new IllegalArgumentException("end tag name can not be null" + getLocation());
/*      */     }
/*  742 */     if (this.checkNamesInterned && this.namesInterned) {
/*  743 */       checkInterning(name);
/*      */     }
/*      */     
/*  746 */     if ((!this.namesInterned && !name.equals(this.elName[this.depth])) || (this.namesInterned && name != this.elName[this.depth]))
/*      */     {
/*      */       
/*  749 */       throw new IllegalArgumentException("expected element name " + printable(this.elName[this.depth]) + " and not " + printable(name) + getLocation());
/*      */     }
/*      */     
/*  752 */     if (this.startTagIncomplete) {
/*  753 */       writeNamespaceDeclarations();
/*  754 */       this.out.write(" />");
/*  755 */       this.depth--;
/*      */     } else {
/*  757 */       this.depth--;
/*      */       
/*  759 */       if (this.doIndent && this.seenTag) writeIndent(); 
/*  760 */       this.out.write("</");
/*  761 */       if (namespace != null && namespace.length() > 0) {
/*      */         
/*  763 */         String prefix = lookupOrDeclarePrefix(namespace);
/*      */         
/*  765 */         if (prefix.length() > 0) {
/*  766 */           this.out.write(prefix);
/*  767 */           this.out.write(58);
/*      */         } 
/*      */       } 
/*  770 */       this.out.write(name);
/*  771 */       this.out.write(62);
/*      */     } 
/*      */     
/*  774 */     this.namespaceEnd = this.elNamespaceCount[this.depth];
/*  775 */     this.startTagIncomplete = false;
/*  776 */     this.seenTag = true;
/*  777 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public XmlSerializer text(String text) throws IOException {
/*  783 */     if (this.startTagIncomplete || this.setPrefixCalled) closeStartTag(); 
/*  784 */     if (this.doIndent && this.seenTag) this.seenTag = false; 
/*  785 */     writeElementContent(text, this.out);
/*  786 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public XmlSerializer text(char[] buf, int start, int len) throws IOException {
/*  791 */     if (this.startTagIncomplete || this.setPrefixCalled) closeStartTag(); 
/*  792 */     if (this.doIndent && this.seenTag) this.seenTag = false; 
/*  793 */     writeElementContent(buf, start, len, this.out);
/*  794 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public void cdsect(String text) throws IOException {
/*  799 */     if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket) closeStartTag(); 
/*  800 */     if (this.doIndent && this.seenTag) this.seenTag = false; 
/*  801 */     this.out.write("<![CDATA[");
/*  802 */     this.out.write(text);
/*  803 */     this.out.write("]]>");
/*      */   }
/*      */ 
/*      */   
/*      */   public void entityRef(String text) throws IOException {
/*  808 */     if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket) closeStartTag(); 
/*  809 */     if (this.doIndent && this.seenTag) this.seenTag = false; 
/*  810 */     this.out.write(38);
/*  811 */     this.out.write(text);
/*  812 */     this.out.write(59);
/*      */   }
/*      */ 
/*      */   
/*      */   public void processingInstruction(String text) throws IOException {
/*  817 */     if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket) closeStartTag(); 
/*  818 */     if (this.doIndent && this.seenTag) this.seenTag = false; 
/*  819 */     this.out.write("<?");
/*  820 */     this.out.write(text);
/*  821 */     this.out.write("?>");
/*      */   }
/*      */ 
/*      */   
/*      */   public void comment(String text) throws IOException {
/*  826 */     if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket) closeStartTag(); 
/*  827 */     if (this.doIndent && this.seenTag) this.seenTag = false; 
/*  828 */     this.out.write("<!--");
/*  829 */     this.out.write(text);
/*  830 */     this.out.write("-->");
/*      */   }
/*      */ 
/*      */   
/*      */   public void docdecl(String text) throws IOException {
/*  835 */     if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket) closeStartTag(); 
/*  836 */     if (this.doIndent && this.seenTag) this.seenTag = false; 
/*  837 */     this.out.write("<!DOCTYPE ");
/*  838 */     this.out.write(text);
/*  839 */     this.out.write(">");
/*      */   }
/*      */ 
/*      */   
/*      */   public void ignorableWhitespace(String text) throws IOException {
/*  844 */     if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket) closeStartTag(); 
/*  845 */     if (this.doIndent && this.seenTag) this.seenTag = false; 
/*  846 */     if (text.length() == 0) {
/*  847 */       throw new IllegalArgumentException("empty string is not allowed for ignorable whitespace" + getLocation());
/*      */     }
/*      */     
/*  850 */     this.out.write(text);
/*      */   }
/*      */ 
/*      */   
/*      */   public void flush() throws IOException {
/*  855 */     if (!this.finished && this.startTagIncomplete) closeStartTag(); 
/*  856 */     this.out.flush();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void writeAttributeValue(String value, Writer out) throws IOException {
/*  864 */     char quot = this.attributeUseApostrophe ? '\'' : '"';
/*  865 */     String quotEntity = this.attributeUseApostrophe ? "&apos;" : "&quot;";
/*      */     
/*  867 */     int pos = 0;
/*  868 */     for (int i = 0; i < value.length(); i++) {
/*      */       
/*  870 */       char ch = value.charAt(i);
/*  871 */       if (ch == '&') {
/*  872 */         if (i > pos) out.write(value.substring(pos, i)); 
/*  873 */         out.write("&amp;");
/*  874 */         pos = i + 1;
/*  875 */       }  if (ch == '<') {
/*  876 */         if (i > pos) out.write(value.substring(pos, i)); 
/*  877 */         out.write("&lt;");
/*  878 */         pos = i + 1;
/*  879 */       } else if (ch == quot) {
/*  880 */         if (i > pos) out.write(value.substring(pos, i)); 
/*  881 */         out.write(quotEntity);
/*  882 */         pos = i + 1;
/*  883 */       } else if (ch < ' ') {
/*      */ 
/*      */         
/*  886 */         if (ch == '\r' || ch == '\n' || ch == '\t') {
/*  887 */           if (i > pos) out.write(value.substring(pos, i)); 
/*  888 */           out.write("&#");
/*  889 */           out.write(Integer.toString(ch));
/*  890 */           out.write(59);
/*  891 */           pos = i + 1;
/*      */         } else {
/*  893 */           throw new IllegalStateException("character " + Integer.toString(ch) + " is not allowed in output" + getLocation());
/*      */         } 
/*      */       } 
/*      */     } 
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
/*  909 */     if (pos > 0) {
/*  910 */       out.write(value.substring(pos));
/*      */     } else {
/*  912 */       out.write(value);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void writeElementContent(String text, Writer out) throws IOException {
/*  920 */     int pos = 0;
/*  921 */     for (int i = 0; i < text.length(); i++) {
/*      */ 
/*      */       
/*  924 */       char ch = text.charAt(i);
/*  925 */       if (ch == ']') {
/*  926 */         if (this.seenBracket) {
/*  927 */           this.seenBracketBracket = true;
/*      */         } else {
/*  929 */           this.seenBracket = true;
/*      */         } 
/*      */       } else {
/*  932 */         if (ch == '&') {
/*  933 */           if (i > pos) out.write(text.substring(pos, i)); 
/*  934 */           out.write("&amp;");
/*  935 */           pos = i + 1;
/*  936 */         } else if (ch == '<') {
/*  937 */           if (i > pos) out.write(text.substring(pos, i)); 
/*  938 */           out.write("&lt;");
/*  939 */           pos = i + 1;
/*  940 */         } else if (this.seenBracketBracket && ch == '>') {
/*  941 */           if (i > pos) out.write(text.substring(pos, i)); 
/*  942 */           out.write("&gt;");
/*  943 */           pos = i + 1;
/*  944 */         } else if (ch < ' ') {
/*      */           
/*  946 */           if (ch != '\t' && ch != '\n' && ch != '\r')
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  956 */             throw new IllegalStateException("character " + Integer.toString(ch) + " is not allowed in output" + getLocation());
/*      */           }
/*      */         } 
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
/*  971 */         if (this.seenBracket) {
/*  972 */           this.seenBracketBracket = this.seenBracket = false;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  977 */     if (pos > 0) {
/*  978 */       out.write(text.substring(pos));
/*      */     } else {
/*  980 */       out.write(text);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void writeElementContent(char[] buf, int off, int len, Writer out) throws IOException {
/*  990 */     int end = off + len;
/*  991 */     int pos = off;
/*  992 */     for (int i = off; i < end; i++) {
/*      */       
/*  994 */       char ch = buf[i];
/*  995 */       if (ch == ']') {
/*  996 */         if (this.seenBracket) {
/*  997 */           this.seenBracketBracket = true;
/*      */         } else {
/*  999 */           this.seenBracket = true;
/*      */         } 
/*      */       } else {
/* 1002 */         if (ch == '&') {
/* 1003 */           if (i > pos) {
/* 1004 */             out.write(buf, pos, i - pos);
/*      */           }
/* 1006 */           out.write("&amp;");
/* 1007 */           pos = i + 1;
/* 1008 */         } else if (ch == '<') {
/* 1009 */           if (i > pos) {
/* 1010 */             out.write(buf, pos, i - pos);
/*      */           }
/* 1012 */           out.write("&lt;");
/* 1013 */           pos = i + 1;
/*      */         }
/* 1015 */         else if (this.seenBracketBracket && ch == '>') {
/* 1016 */           if (i > pos) {
/* 1017 */             out.write(buf, pos, i - pos);
/*      */           }
/* 1019 */           out.write("&gt;");
/* 1020 */           pos = i + 1;
/* 1021 */         } else if (ch < ' ') {
/*      */           
/* 1023 */           if (ch != '\t' && ch != '\n' && ch != '\r')
/*      */           {
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
/* 1036 */             throw new IllegalStateException("character " + Integer.toString(ch) + " is not allowed in output" + getLocation());
/*      */           }
/*      */         } 
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
/* 1051 */         if (this.seenBracket) {
/* 1052 */           this.seenBracketBracket = this.seenBracket = false;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1057 */     if (end > pos) {
/* 1058 */       out.write(buf, pos, end - pos);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected static final String printable(String s) {
/* 1064 */     if (s == null) return "null"; 
/* 1065 */     StringBuffer retval = new StringBuffer(s.length() + 16);
/* 1066 */     retval.append("'");
/*      */     
/* 1068 */     for (int i = 0; i < s.length(); i++) {
/* 1069 */       addPrintable(retval, s.charAt(i));
/*      */     }
/* 1071 */     retval.append("'");
/* 1072 */     return retval.toString();
/*      */   }
/*      */   
/*      */   protected static final String printable(char ch) {
/* 1076 */     StringBuffer retval = new StringBuffer();
/* 1077 */     addPrintable(retval, ch);
/* 1078 */     return retval.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   private static void addPrintable(StringBuffer retval, char ch) {
/* 1083 */     switch (ch) {
/*      */       
/*      */       case '\b':
/* 1086 */         retval.append("\\b");
/*      */         return;
/*      */       case '\t':
/* 1089 */         retval.append("\\t");
/*      */         return;
/*      */       case '\n':
/* 1092 */         retval.append("\\n");
/*      */         return;
/*      */       case '\f':
/* 1095 */         retval.append("\\f");
/*      */         return;
/*      */       case '\r':
/* 1098 */         retval.append("\\r");
/*      */         return;
/*      */       case '"':
/* 1101 */         retval.append("\\\"");
/*      */         return;
/*      */       case '\'':
/* 1104 */         retval.append("\\'");
/*      */         return;
/*      */       case '\\':
/* 1107 */         retval.append("\\\\");
/*      */         return;
/*      */     } 
/* 1110 */     if (ch < ' ' || ch > '~') {
/* 1111 */       String ss = "0000" + Integer.toString(ch, 16);
/* 1112 */       retval.append("\\u" + ss.substring(ss.length() - 4, ss.length()));
/*      */     } else {
/* 1114 */       retval.append(ch);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\pull\MXSerializer.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */