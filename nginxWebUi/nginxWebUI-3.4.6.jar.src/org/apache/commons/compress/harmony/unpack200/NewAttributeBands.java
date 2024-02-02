/*      */ package org.apache.commons.compress.harmony.unpack200;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.StringReader;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import org.apache.commons.compress.harmony.pack200.BHSDCodec;
/*      */ import org.apache.commons.compress.harmony.pack200.Codec;
/*      */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.Attribute;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPDouble;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPFieldRef;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPFloat;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPInteger;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPInterfaceMethodRef;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPLong;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPMethodRef;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPNameAndType;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPString;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPUTF8;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.NewAttribute;
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
/*      */ public class NewAttributeBands
/*      */   extends BandSet
/*      */ {
/*      */   private final AttributeLayout attributeLayout;
/*      */   private int backwardsCallCount;
/*      */   protected List attributeLayoutElements;
/*      */   
/*      */   public NewAttributeBands(Segment segment, AttributeLayout attributeLayout) throws IOException {
/*   56 */     super(segment);
/*   57 */     this.attributeLayout = attributeLayout;
/*   58 */     parseLayout();
/*   59 */     attributeLayout.setBackwardsCallCount(this.backwardsCallCount);
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
/*      */   public void read(InputStream in) throws IOException, Pack200Exception {}
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
/*      */   public List parseAttributes(InputStream in, int occurrenceCount) throws IOException, Pack200Exception {
/*   83 */     for (int i = 0; i < this.attributeLayoutElements.size(); i++) {
/*   84 */       AttributeLayoutElement element = this.attributeLayoutElements.get(i);
/*   85 */       element.readBands(in, occurrenceCount);
/*      */     } 
/*      */     
/*   88 */     List<Attribute> attributes = new ArrayList(occurrenceCount);
/*   89 */     for (int j = 0; j < occurrenceCount; j++) {
/*   90 */       attributes.add(getOneAttribute(j, this.attributeLayoutElements));
/*      */     }
/*   92 */     return attributes;
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
/*      */   private Attribute getOneAttribute(int index, List<AttributeLayoutElement> elements) {
/*  104 */     NewAttribute attribute = new NewAttribute(this.segment.getCpBands().cpUTF8Value(this.attributeLayout.getName()), this.attributeLayout.getIndex());
/*  105 */     for (int i = 0; i < elements.size(); i++) {
/*  106 */       AttributeLayoutElement element = elements.get(i);
/*  107 */       element.addToAttribute(index, attribute);
/*      */     } 
/*  109 */     return (Attribute)attribute;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void parseLayout() throws IOException {
/*  118 */     if (this.attributeLayoutElements == null) {
/*  119 */       this.attributeLayoutElements = new ArrayList();
/*  120 */       StringReader stream = new StringReader(this.attributeLayout.getLayout());
/*      */       AttributeLayoutElement e;
/*  122 */       while ((e = readNextAttributeElement(stream)) != null) {
/*  123 */         this.attributeLayoutElements.add(e);
/*      */       }
/*  125 */       resolveCalls();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void resolveCalls() {
/*  133 */     int backwardsCalls = 0;
/*  134 */     for (int i = 0; i < this.attributeLayoutElements.size(); i++) {
/*  135 */       AttributeLayoutElement element = this.attributeLayoutElements.get(i);
/*  136 */       if (element instanceof Callable) {
/*  137 */         Callable callable = (Callable)element;
/*  138 */         if (i == 0) {
/*  139 */           callable.setFirstCallable(true);
/*      */         }
/*  141 */         List<LayoutElement> body = callable.body;
/*  142 */         for (int iIndex = 0; iIndex < body.size(); iIndex++) {
/*  143 */           LayoutElement layoutElement = body.get(iIndex);
/*      */           
/*  145 */           backwardsCalls += resolveCallsForElement(i, callable, layoutElement);
/*      */         } 
/*      */       } 
/*      */     } 
/*  149 */     this.backwardsCallCount = backwardsCalls;
/*      */   }
/*      */   
/*      */   private int resolveCallsForElement(int i, Callable currentCallable, LayoutElement layoutElement) {
/*  153 */     int backwardsCalls = 0;
/*  154 */     if (layoutElement instanceof Call) {
/*  155 */       Call call = (Call)layoutElement;
/*  156 */       int index = call.callableIndex;
/*  157 */       if (index == 0) {
/*  158 */         backwardsCalls++;
/*  159 */         call.setCallable(currentCallable);
/*  160 */       } else if (index > 0) {
/*  161 */         for (int k = i + 1; k < this.attributeLayoutElements.size(); k++) {
/*  162 */           AttributeLayoutElement el = this.attributeLayoutElements.get(k);
/*      */           
/*  164 */           index--;
/*  165 */           if (el instanceof Callable && index == 0) {
/*  166 */             call.setCallable((Callable)el);
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       } else {
/*  172 */         backwardsCalls++;
/*  173 */         for (int k = i - 1; k >= 0; k--) {
/*  174 */           AttributeLayoutElement el = this.attributeLayoutElements.get(k);
/*      */           
/*  176 */           index++;
/*  177 */           if (el instanceof Callable && index == 0) {
/*  178 */             call.setCallable((Callable)el);
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       } 
/*  184 */     } else if (layoutElement instanceof Replication) {
/*  185 */       List children = ((Replication)layoutElement).layoutElements;
/*  186 */       for (Iterator<LayoutElement> iterator = children.iterator(); iterator.hasNext(); ) {
/*  187 */         LayoutElement object = iterator.next();
/*  188 */         backwardsCalls += resolveCallsForElement(i, currentCallable, object);
/*      */       } 
/*      */     } 
/*  191 */     return backwardsCalls;
/*      */   }
/*      */   
/*      */   private AttributeLayoutElement readNextAttributeElement(StringReader stream) throws IOException {
/*  195 */     stream.mark(1);
/*  196 */     int nextChar = stream.read();
/*  197 */     if (nextChar == -1) {
/*  198 */       return null;
/*      */     }
/*  200 */     if (nextChar == 91) {
/*  201 */       List body = readBody(getStreamUpToMatchingBracket(stream));
/*  202 */       return new Callable(body);
/*      */     } 
/*  204 */     stream.reset();
/*  205 */     return readNextLayoutElement(stream); } private LayoutElement readNextLayoutElement(StringReader stream) throws IOException { char uint_type; String str, int_type; List<UnionCase> unionCases; UnionCase c; List body; char next;
/*      */     int number;
/*      */     StringBuilder string;
/*      */     char nxt;
/*  209 */     int nextChar = stream.read();
/*  210 */     if (nextChar == -1) {
/*  211 */       return null;
/*      */     }
/*  213 */     switch (nextChar) {
/*      */       
/*      */       case 66:
/*      */       case 72:
/*      */       case 73:
/*      */       case 86:
/*  219 */         return new Integral(new String(new char[] { (char)nextChar }));
/*      */       case 70:
/*      */       case 83:
/*  222 */         return new Integral(new String(new char[] { (char)nextChar, (char)stream.read() }));
/*      */       case 80:
/*  224 */         stream.mark(1);
/*  225 */         if (stream.read() != 79) {
/*  226 */           stream.reset();
/*  227 */           return new Integral("P" + (char)stream.read());
/*      */         } 
/*  229 */         return new Integral("PO" + (char)stream.read());
/*      */       
/*      */       case 79:
/*  232 */         stream.mark(1);
/*  233 */         if (stream.read() != 83) {
/*  234 */           stream.reset();
/*  235 */           return new Integral("O" + (char)stream.read());
/*      */         } 
/*  237 */         return new Integral("OS" + (char)stream.read());
/*      */ 
/*      */ 
/*      */       
/*      */       case 78:
/*  242 */         uint_type = (char)stream.read();
/*  243 */         stream.read();
/*  244 */         str = readUpToMatchingBracket(stream);
/*  245 */         return new Replication("" + uint_type, str);
/*      */ 
/*      */       
/*      */       case 84:
/*  249 */         int_type = "" + (char)stream.read();
/*  250 */         if (int_type.equals("S")) {
/*  251 */           int_type = int_type + (char)stream.read();
/*      */         }
/*  253 */         unionCases = new ArrayList();
/*      */         
/*  255 */         while ((c = readNextUnionCase(stream)) != null) {
/*  256 */           unionCases.add(c);
/*      */         }
/*  258 */         stream.read();
/*  259 */         stream.read();
/*  260 */         stream.read();
/*  261 */         body = null;
/*  262 */         stream.mark(1);
/*  263 */         next = (char)stream.read();
/*  264 */         if (next != ']') {
/*  265 */           stream.reset();
/*  266 */           body = readBody(getStreamUpToMatchingBracket(stream));
/*      */         } 
/*  268 */         return new Union(int_type, unionCases, body);
/*      */ 
/*      */       
/*      */       case 40:
/*  272 */         number = readNumber(stream).intValue();
/*  273 */         stream.read();
/*  274 */         return new Call(number);
/*      */       
/*      */       case 75:
/*      */       case 82:
/*  278 */         string = (new StringBuilder("")).append((char)nextChar).append((char)stream.read());
/*  279 */         nxt = (char)stream.read();
/*  280 */         string.append(nxt);
/*  281 */         if (nxt == 'N') {
/*  282 */           string.append((char)stream.read());
/*      */         }
/*  284 */         return new Reference(string.toString());
/*      */     } 
/*  286 */     return null; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private UnionCase readNextUnionCase(StringReader stream) throws IOException {
/*  297 */     stream.mark(2);
/*  298 */     stream.read();
/*  299 */     char next = (char)stream.read();
/*  300 */     if (next == ')') {
/*  301 */       stream.reset();
/*  302 */       return null;
/*      */     } 
/*  304 */     stream.reset();
/*  305 */     stream.read();
/*  306 */     List<Integer> tags = new ArrayList();
/*      */     
/*      */     while (true) {
/*  309 */       Integer nextTag = readNumber(stream);
/*  310 */       if (nextTag != null) {
/*  311 */         tags.add(nextTag);
/*  312 */         stream.read();
/*      */       } 
/*  314 */       if (nextTag == null) {
/*  315 */         stream.read();
/*  316 */         stream.mark(1);
/*  317 */         next = (char)stream.read();
/*  318 */         if (next == ']') {
/*  319 */           return new UnionCase(tags);
/*      */         }
/*  321 */         stream.reset();
/*  322 */         return new UnionCase(tags, readBody(getStreamUpToMatchingBracket(stream)));
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static interface AttributeLayoutElement
/*      */   {
/*      */     void readBands(InputStream param1InputStream, int param1Int) throws IOException, Pack200Exception;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void addToAttribute(int param1Int, NewAttribute param1NewAttribute);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private abstract class LayoutElement
/*      */     implements AttributeLayoutElement
/*      */   {
/*      */     private LayoutElement() {}
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected int getLength(char uint_type) {
/*  354 */       int length = 0;
/*  355 */       switch (uint_type) {
/*      */         case 'B':
/*  357 */           length = 1;
/*      */           break;
/*      */         case 'H':
/*  360 */           length = 2;
/*      */           break;
/*      */         case 'I':
/*  363 */           length = 4;
/*      */           break;
/*      */         case 'V':
/*  366 */           length = 0;
/*      */           break;
/*      */       } 
/*  369 */       return length;
/*      */     }
/*      */   }
/*      */   
/*      */   public class Integral
/*      */     extends LayoutElement
/*      */   {
/*      */     private final String tag;
/*      */     private int[] band;
/*      */     
/*      */     public Integral(String tag) {
/*  380 */       this.tag = tag;
/*      */     }
/*      */ 
/*      */     
/*      */     public void readBands(InputStream in, int count) throws IOException, Pack200Exception {
/*  385 */       this.band = NewAttributeBands.this.decodeBandInt(NewAttributeBands.this.attributeLayout.getName() + "_" + this.tag, in, NewAttributeBands.this.getCodec(this.tag), count);
/*      */     }
/*      */ 
/*      */     
/*      */     public void addToAttribute(int n, NewAttribute attribute) {
/*  390 */       long value = this.band[n];
/*  391 */       if (this.tag.equals("B") || this.tag.equals("FB")) {
/*  392 */         attribute.addInteger(1, value);
/*  393 */       } else if (this.tag.equals("SB")) {
/*  394 */         attribute.addInteger(1, (byte)(int)value);
/*  395 */       } else if (this.tag.equals("H") || this.tag.equals("FH")) {
/*  396 */         attribute.addInteger(2, value);
/*  397 */       } else if (this.tag.equals("SH")) {
/*  398 */         attribute.addInteger(2, (short)(int)value);
/*  399 */       } else if (this.tag.equals("I") || this.tag.equals("FI")) {
/*  400 */         attribute.addInteger(4, value);
/*  401 */       } else if (this.tag.equals("SI")) {
/*  402 */         attribute.addInteger(4, (int)value);
/*  403 */       } else if (!this.tag.equals("V") && !this.tag.equals("FV") && !this.tag.equals("SV")) {
/*      */ 
/*      */         
/*  406 */         if (this.tag.startsWith("PO")) {
/*  407 */           char uint_type = this.tag.substring(2).toCharArray()[0];
/*  408 */           int length = getLength(uint_type);
/*  409 */           attribute.addBCOffset(length, (int)value);
/*  410 */         } else if (this.tag.startsWith("P")) {
/*  411 */           char uint_type = this.tag.substring(1).toCharArray()[0];
/*  412 */           int length = getLength(uint_type);
/*  413 */           attribute.addBCIndex(length, (int)value);
/*  414 */         } else if (this.tag.startsWith("OS")) {
/*  415 */           char uint_type = this.tag.substring(2).toCharArray()[0];
/*  416 */           int length = getLength(uint_type);
/*  417 */           if (length == 1) {
/*  418 */             value = (byte)(int)value;
/*  419 */           } else if (length == 2) {
/*  420 */             value = (short)(int)value;
/*  421 */           } else if (length == 4) {
/*  422 */             value = (int)value;
/*      */           } 
/*  424 */           attribute.addBCLength(length, (int)value);
/*  425 */         } else if (this.tag.startsWith("O")) {
/*  426 */           char uint_type = this.tag.substring(1).toCharArray()[0];
/*  427 */           int length = getLength(uint_type);
/*  428 */           attribute.addBCLength(length, (int)value);
/*      */         } 
/*      */       } 
/*      */     }
/*      */     long getValue(int index) {
/*  433 */       return this.band[index];
/*      */     }
/*      */     
/*      */     public String getTag() {
/*  437 */       return this.tag;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public class Replication
/*      */     extends LayoutElement
/*      */   {
/*      */     private final NewAttributeBands.Integral countElement;
/*      */ 
/*      */     
/*  449 */     private final List layoutElements = new ArrayList();
/*      */     
/*      */     public Replication(String tag, String contents) throws IOException {
/*  452 */       this.countElement = new NewAttributeBands.Integral(tag);
/*  453 */       StringReader stream = new StringReader(contents);
/*      */       NewAttributeBands.LayoutElement e;
/*  455 */       while ((e = NewAttributeBands.this.readNextLayoutElement(stream)) != null) {
/*  456 */         this.layoutElements.add(e);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void readBands(InputStream in, int count) throws IOException, Pack200Exception {
/*  462 */       this.countElement.readBands(in, count);
/*  463 */       int arrayCount = 0; int i;
/*  464 */       for (i = 0; i < count; i++) {
/*  465 */         arrayCount = (int)(arrayCount + this.countElement.getValue(i));
/*      */       }
/*  467 */       for (i = 0; i < this.layoutElements.size(); i++) {
/*  468 */         NewAttributeBands.LayoutElement element = this.layoutElements.get(i);
/*  469 */         element.readBands(in, arrayCount);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void addToAttribute(int index, NewAttribute attribute) {
/*  476 */       this.countElement.addToAttribute(index, attribute);
/*      */ 
/*      */       
/*  479 */       int offset = 0;
/*  480 */       for (int i = 0; i < index; i++) {
/*  481 */         offset = (int)(offset + this.countElement.getValue(i));
/*      */       }
/*  483 */       long numElements = this.countElement.getValue(index);
/*  484 */       for (int j = offset; j < offset + numElements; j++) {
/*  485 */         for (int it = 0; it < this.layoutElements.size(); it++) {
/*  486 */           NewAttributeBands.LayoutElement element = this.layoutElements.get(it);
/*  487 */           element.addToAttribute(j, attribute);
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*      */     public NewAttributeBands.Integral getCountElement() {
/*  493 */       return this.countElement;
/*      */     }
/*      */     
/*      */     public List getLayoutElements() {
/*  497 */       return this.layoutElements;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public class Union
/*      */     extends LayoutElement
/*      */   {
/*      */     private final NewAttributeBands.Integral unionTag;
/*      */     
/*      */     private final List unionCases;
/*      */     private final List defaultCaseBody;
/*      */     private int[] caseCounts;
/*      */     private int defaultCount;
/*      */     
/*      */     public Union(String tag, List unionCases, List body) {
/*  513 */       this.unionTag = new NewAttributeBands.Integral(tag);
/*  514 */       this.unionCases = unionCases;
/*  515 */       this.defaultCaseBody = body;
/*      */     }
/*      */ 
/*      */     
/*      */     public void readBands(InputStream in, int count) throws IOException, Pack200Exception {
/*  520 */       this.unionTag.readBands(in, count);
/*  521 */       int[] values = this.unionTag.band;
/*      */       
/*  523 */       this.caseCounts = new int[this.unionCases.size()]; int i;
/*  524 */       for (i = 0; i < this.caseCounts.length; i++) {
/*  525 */         NewAttributeBands.UnionCase unionCase = this.unionCases.get(i);
/*  526 */         for (int j = 0; j < values.length; j++) {
/*  527 */           if (unionCase.hasTag(values[j])) {
/*  528 */             this.caseCounts[i] = this.caseCounts[i] + 1;
/*      */           }
/*      */         } 
/*  531 */         unionCase.readBands(in, this.caseCounts[i]);
/*      */       } 
/*      */       
/*  534 */       for (i = 0; i < values.length; i++) {
/*  535 */         boolean found = false;
/*  536 */         for (int it = 0; it < this.unionCases.size(); it++) {
/*  537 */           NewAttributeBands.UnionCase unionCase = this.unionCases.get(it);
/*  538 */           if (unionCase.hasTag(values[i])) {
/*  539 */             found = true;
/*      */           }
/*      */         } 
/*  542 */         if (!found) {
/*  543 */           this.defaultCount++;
/*      */         }
/*      */       } 
/*  546 */       if (this.defaultCaseBody != null) {
/*  547 */         for (i = 0; i < this.defaultCaseBody.size(); i++) {
/*  548 */           NewAttributeBands.LayoutElement element = this.defaultCaseBody.get(i);
/*  549 */           element.readBands(in, this.defaultCount);
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void addToAttribute(int n, NewAttribute attribute) {
/*  556 */       this.unionTag.addToAttribute(n, attribute);
/*  557 */       int offset = 0;
/*  558 */       int[] tagBand = this.unionTag.band;
/*  559 */       long tag = this.unionTag.getValue(n);
/*  560 */       boolean defaultCase = true;
/*  561 */       for (int i = 0; i < this.unionCases.size(); i++) {
/*  562 */         NewAttributeBands.UnionCase element = this.unionCases.get(i);
/*  563 */         if (element.hasTag(tag)) {
/*  564 */           defaultCase = false;
/*  565 */           for (int j = 0; j < n; j++) {
/*  566 */             if (element.hasTag(tagBand[j])) {
/*  567 */               offset++;
/*      */             }
/*      */           } 
/*  570 */           element.addToAttribute(offset, attribute);
/*      */         } 
/*      */       } 
/*  573 */       if (defaultCase) {
/*      */         
/*  575 */         int defaultOffset = 0;
/*  576 */         for (int j = 0; j < n; j++) {
/*  577 */           boolean found = false;
/*  578 */           for (int k = 0; k < this.unionCases.size(); k++) {
/*  579 */             NewAttributeBands.UnionCase element = this.unionCases.get(k);
/*  580 */             if (element.hasTag(tagBand[j])) {
/*  581 */               found = true;
/*      */             }
/*      */           } 
/*  584 */           if (!found) {
/*  585 */             defaultOffset++;
/*      */           }
/*      */         } 
/*  588 */         if (this.defaultCaseBody != null) {
/*  589 */           for (int k = 0; k < this.defaultCaseBody.size(); k++) {
/*  590 */             NewAttributeBands.LayoutElement element = this.defaultCaseBody.get(k);
/*  591 */             element.addToAttribute(defaultOffset, attribute);
/*      */           } 
/*      */         }
/*      */       } 
/*      */     }
/*      */     
/*      */     public NewAttributeBands.Integral getUnionTag() {
/*  598 */       return this.unionTag;
/*      */     }
/*      */     
/*      */     public List getUnionCases() {
/*  602 */       return this.unionCases;
/*      */     }
/*      */     
/*      */     public List getDefaultCaseBody() {
/*  606 */       return this.defaultCaseBody;
/*      */     }
/*      */   }
/*      */   
/*      */   public class Call
/*      */     extends LayoutElement
/*      */   {
/*      */     private final int callableIndex;
/*      */     private NewAttributeBands.Callable callable;
/*      */     
/*      */     public Call(int callableIndex) {
/*  617 */       this.callableIndex = callableIndex;
/*      */     }
/*      */     
/*      */     public void setCallable(NewAttributeBands.Callable callable) {
/*  621 */       this.callable = callable;
/*  622 */       if (this.callableIndex < 1) {
/*  623 */         callable.setBackwardsCallable();
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void readBands(InputStream in, int count) {
/*  634 */       if (this.callableIndex > 0) {
/*  635 */         this.callable.addCount(count);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void addToAttribute(int n, NewAttribute attribute) {
/*  641 */       this.callable.addNextToAttribute(attribute);
/*      */     }
/*      */     
/*      */     public int getCallableIndex() {
/*  645 */       return this.callableIndex;
/*      */     }
/*      */     
/*      */     public NewAttributeBands.Callable getCallable() {
/*  649 */       return this.callable;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public class Reference
/*      */     extends LayoutElement
/*      */   {
/*      */     private final String tag;
/*      */     
/*      */     private Object band;
/*      */     
/*      */     private final int length;
/*      */ 
/*      */     
/*      */     public Reference(String tag) {
/*  665 */       this.tag = tag;
/*  666 */       this.length = getLength(tag.charAt(tag.length() - 1));
/*      */     }
/*      */ 
/*      */     
/*      */     public void readBands(InputStream in, int count) throws IOException, Pack200Exception {
/*  671 */       if (this.tag.startsWith("KI")) {
/*  672 */         this.band = NewAttributeBands.this.parseCPIntReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
/*  673 */       } else if (this.tag.startsWith("KJ")) {
/*  674 */         this.band = NewAttributeBands.this.parseCPLongReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
/*  675 */       } else if (this.tag.startsWith("KF")) {
/*  676 */         this.band = NewAttributeBands.this.parseCPFloatReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
/*  677 */       } else if (this.tag.startsWith("KD")) {
/*  678 */         this.band = NewAttributeBands.this.parseCPDoubleReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
/*  679 */       } else if (this.tag.startsWith("KS")) {
/*  680 */         this.band = NewAttributeBands.this.parseCPStringReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
/*  681 */       } else if (this.tag.startsWith("RC")) {
/*  682 */         this.band = NewAttributeBands.this.parseCPClassReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
/*  683 */       } else if (this.tag.startsWith("RS")) {
/*  684 */         this.band = NewAttributeBands.this.parseCPSignatureReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
/*  685 */       } else if (this.tag.startsWith("RD")) {
/*  686 */         this.band = NewAttributeBands.this.parseCPDescriptorReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
/*  687 */       } else if (this.tag.startsWith("RF")) {
/*  688 */         this.band = NewAttributeBands.this.parseCPFieldRefReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
/*  689 */       } else if (this.tag.startsWith("RM")) {
/*  690 */         this.band = NewAttributeBands.this.parseCPMethodRefReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
/*  691 */       } else if (this.tag.startsWith("RI")) {
/*  692 */         this.band = NewAttributeBands.this.parseCPInterfaceMethodRefReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
/*  693 */       } else if (this.tag.startsWith("RU")) {
/*  694 */         this.band = NewAttributeBands.this.parseCPUTF8References(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void addToAttribute(int n, NewAttribute attribute) {
/*  700 */       if (this.tag.startsWith("KI")) {
/*  701 */         attribute.addToBody(this.length, ((CPInteger[])this.band)[n]);
/*  702 */       } else if (this.tag.startsWith("KJ")) {
/*  703 */         attribute.addToBody(this.length, ((CPLong[])this.band)[n]);
/*  704 */       } else if (this.tag.startsWith("KF")) {
/*  705 */         attribute.addToBody(this.length, ((CPFloat[])this.band)[n]);
/*  706 */       } else if (this.tag.startsWith("KD")) {
/*  707 */         attribute.addToBody(this.length, ((CPDouble[])this.band)[n]);
/*  708 */       } else if (this.tag.startsWith("KS")) {
/*  709 */         attribute.addToBody(this.length, ((CPString[])this.band)[n]);
/*  710 */       } else if (this.tag.startsWith("RC")) {
/*  711 */         attribute.addToBody(this.length, ((CPClass[])this.band)[n]);
/*  712 */       } else if (this.tag.startsWith("RS")) {
/*  713 */         attribute.addToBody(this.length, ((CPUTF8[])this.band)[n]);
/*  714 */       } else if (this.tag.startsWith("RD")) {
/*  715 */         attribute.addToBody(this.length, ((CPNameAndType[])this.band)[n]);
/*  716 */       } else if (this.tag.startsWith("RF")) {
/*  717 */         attribute.addToBody(this.length, ((CPFieldRef[])this.band)[n]);
/*  718 */       } else if (this.tag.startsWith("RM")) {
/*  719 */         attribute.addToBody(this.length, ((CPMethodRef[])this.band)[n]);
/*  720 */       } else if (this.tag.startsWith("RI")) {
/*  721 */         attribute.addToBody(this.length, ((CPInterfaceMethodRef[])this.band)[n]);
/*  722 */       } else if (this.tag.startsWith("RU")) {
/*  723 */         attribute.addToBody(this.length, ((CPUTF8[])this.band)[n]);
/*      */       } 
/*      */     }
/*      */     
/*      */     public String getTag() {
/*  728 */       return this.tag;
/*      */     }
/*      */   }
/*      */   
/*      */   public static class Callable
/*      */     implements AttributeLayoutElement
/*      */   {
/*      */     private final List body;
/*      */     private boolean isBackwardsCallable;
/*      */     private boolean isFirstCallable;
/*      */     private int count;
/*      */     private int index;
/*      */     
/*      */     public Callable(List body) throws IOException {
/*  742 */       this.body = body;
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
/*      */     public void addNextToAttribute(NewAttribute attribute) {
/*  755 */       for (int i = 0; i < this.body.size(); i++) {
/*  756 */         NewAttributeBands.LayoutElement element = this.body.get(i);
/*  757 */         element.addToAttribute(this.index, attribute);
/*      */       } 
/*  759 */       this.index++;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void addCount(int count) {
/*  768 */       this.count += count;
/*      */     }
/*      */ 
/*      */     
/*      */     public void readBands(InputStream in, int count) throws IOException, Pack200Exception {
/*  773 */       if (this.isFirstCallable) {
/*  774 */         count += this.count;
/*      */       } else {
/*  776 */         count = this.count;
/*      */       } 
/*  778 */       for (int i = 0; i < this.body.size(); i++) {
/*  779 */         NewAttributeBands.LayoutElement element = this.body.get(i);
/*  780 */         element.readBands(in, count);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void addToAttribute(int n, NewAttribute attribute) {
/*  786 */       if (this.isFirstCallable) {
/*      */         
/*  788 */         for (int i = 0; i < this.body.size(); i++) {
/*  789 */           NewAttributeBands.LayoutElement element = this.body.get(i);
/*  790 */           element.addToAttribute(this.index, attribute);
/*      */         } 
/*  792 */         this.index++;
/*      */       } 
/*      */     }
/*      */     
/*      */     public boolean isBackwardsCallable() {
/*  797 */       return this.isBackwardsCallable;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setBackwardsCallable() {
/*  804 */       this.isBackwardsCallable = true;
/*      */     }
/*      */     
/*      */     public void setFirstCallable(boolean isFirstCallable) {
/*  808 */       this.isFirstCallable = isFirstCallable;
/*      */     }
/*      */     
/*      */     public List getBody() {
/*  812 */       return this.body;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public class UnionCase
/*      */     extends LayoutElement
/*      */   {
/*      */     private List body;
/*      */     
/*      */     private final List tags;
/*      */ 
/*      */     
/*      */     public UnionCase(List tags) {
/*  826 */       this.tags = tags;
/*      */     }
/*      */     
/*      */     public boolean hasTag(long l) {
/*  830 */       return this.tags.contains(Integer.valueOf((int)l));
/*      */     }
/*      */     
/*      */     public UnionCase(List tags, List body) throws IOException {
/*  834 */       this.tags = tags;
/*  835 */       this.body = body;
/*      */     }
/*      */ 
/*      */     
/*      */     public void readBands(InputStream in, int count) throws IOException, Pack200Exception {
/*  840 */       if (this.body != null) {
/*  841 */         for (int i = 0; i < this.body.size(); i++) {
/*  842 */           NewAttributeBands.LayoutElement element = this.body.get(i);
/*  843 */           element.readBands(in, count);
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void addToAttribute(int index, NewAttribute attribute) {
/*  850 */       if (this.body != null) {
/*  851 */         for (int i = 0; i < this.body.size(); i++) {
/*  852 */           NewAttributeBands.LayoutElement element = this.body.get(i);
/*  853 */           element.addToAttribute(index, attribute);
/*      */         } 
/*      */       }
/*      */     }
/*      */     
/*      */     public List getBody() {
/*  859 */       return (this.body == null) ? Collections.EMPTY_LIST : this.body;
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
/*      */   private StringReader getStreamUpToMatchingBracket(StringReader stream) throws IOException {
/*  872 */     StringBuffer sb = new StringBuffer();
/*  873 */     int foundBracket = -1;
/*  874 */     while (foundBracket != 0) {
/*  875 */       char c = (char)stream.read();
/*  876 */       if (c == ']') {
/*  877 */         foundBracket++;
/*      */       }
/*  879 */       if (c == '[') {
/*  880 */         foundBracket--;
/*      */       }
/*  882 */       if (foundBracket != 0) {
/*  883 */         sb.append(c);
/*      */       }
/*      */     } 
/*  886 */     return new StringReader(sb.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BHSDCodec getCodec(String layoutElement) {
/*  896 */     if (layoutElement.indexOf('O') >= 0) {
/*  897 */       return Codec.BRANCH5;
/*      */     }
/*  899 */     if (layoutElement.indexOf('P') >= 0) {
/*  900 */       return Codec.BCI5;
/*      */     }
/*  902 */     if (layoutElement.indexOf('S') >= 0 && layoutElement.indexOf("KS") < 0 && layoutElement
/*  903 */       .indexOf("RS") < 0) {
/*  904 */       return Codec.SIGNED5;
/*      */     }
/*  906 */     if (layoutElement.indexOf('B') >= 0) {
/*  907 */       return Codec.BYTE1;
/*      */     }
/*  909 */     return Codec.UNSIGNED5;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String readUpToMatchingBracket(StringReader stream) throws IOException {
/*  920 */     StringBuffer sb = new StringBuffer();
/*  921 */     int foundBracket = -1;
/*  922 */     while (foundBracket != 0) {
/*  923 */       char c = (char)stream.read();
/*  924 */       if (c == ']') {
/*  925 */         foundBracket++;
/*      */       }
/*  927 */       if (c == '[') {
/*  928 */         foundBracket--;
/*      */       }
/*  930 */       if (foundBracket != 0) {
/*  931 */         sb.append(c);
/*      */       }
/*      */     } 
/*  934 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Integer readNumber(StringReader stream) throws IOException {
/*  945 */     stream.mark(1);
/*  946 */     char first = (char)stream.read();
/*  947 */     boolean negative = (first == '-');
/*  948 */     if (!negative) {
/*  949 */       stream.reset();
/*      */     }
/*  951 */     stream.mark(100);
/*      */     
/*  953 */     int length = 0; int i;
/*  954 */     while ((i = stream.read()) != -1 && Character.isDigit((char)i)) {
/*  955 */       length++;
/*      */     }
/*  957 */     stream.reset();
/*  958 */     if (length == 0) {
/*  959 */       return null;
/*      */     }
/*  961 */     char[] digits = new char[length];
/*  962 */     int read = stream.read(digits);
/*  963 */     if (read != digits.length) {
/*  964 */       throw new IOException("Error reading from the input stream");
/*      */     }
/*  966 */     return Integer.valueOf(Integer.parseInt((negative ? "-" : "") + new String(digits)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private List readBody(StringReader stream) throws IOException {
/*  977 */     List<LayoutElement> layoutElements = new ArrayList();
/*      */     LayoutElement e;
/*  979 */     while ((e = readNextLayoutElement(stream)) != null) {
/*  980 */       layoutElements.add(e);
/*      */     }
/*  982 */     return layoutElements;
/*      */   }
/*      */   
/*      */   public int getBackwardsCallCount() {
/*  986 */     return this.backwardsCallCount;
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
/*      */   public void setBackwardsCalls(int[] backwardsCalls) throws IOException {
/*  998 */     int index = 0;
/*  999 */     parseLayout();
/* 1000 */     for (int i = 0; i < this.attributeLayoutElements.size(); i++) {
/* 1001 */       AttributeLayoutElement element = this.attributeLayoutElements.get(i);
/* 1002 */       if (element instanceof Callable && ((Callable)element).isBackwardsCallable()) {
/* 1003 */         ((Callable)element).addCount(backwardsCalls[index]);
/* 1004 */         index++;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void unpack() throws IOException, Pack200Exception {}
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\NewAttributeBands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */