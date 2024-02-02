/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.StringReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NewAttributeBands
/*     */   extends BandSet
/*     */ {
/*     */   protected List attributeLayoutElements;
/*     */   private int[] backwardsCallCounts;
/*     */   private final CpBands cpBands;
/*     */   private final AttributeDefinitionBands.AttributeDefinition def;
/*     */   private boolean usedAtLeastOnce;
/*     */   private Integral lastPIntegral;
/*     */   
/*     */   public NewAttributeBands(int effort, CpBands cpBands, SegmentHeader header, AttributeDefinitionBands.AttributeDefinition def) throws IOException {
/*  50 */     super(effort, header);
/*  51 */     this.def = def;
/*  52 */     this.cpBands = cpBands;
/*  53 */     parseLayout();
/*     */   }
/*     */   
/*     */   public void addAttribute(NewAttribute attribute) {
/*  57 */     this.usedAtLeastOnce = true;
/*  58 */     InputStream stream = new ByteArrayInputStream(attribute.getBytes());
/*  59 */     for (Iterator<AttributeLayoutElement> iterator = this.attributeLayoutElements.iterator(); iterator.hasNext(); ) {
/*  60 */       AttributeLayoutElement layoutElement = iterator.next();
/*  61 */       layoutElement.addAttributeToBand(attribute, stream);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void pack(OutputStream out) throws IOException, Pack200Exception {
/*  67 */     for (Iterator<AttributeLayoutElement> iterator = this.attributeLayoutElements.iterator(); iterator.hasNext(); ) {
/*  68 */       AttributeLayoutElement layoutElement = iterator.next();
/*  69 */       layoutElement.pack(out);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getAttributeName() {
/*  74 */     return this.def.name.getUnderlyingString();
/*     */   }
/*     */   
/*     */   public int getFlagIndex() {
/*  78 */     return this.def.index;
/*     */   }
/*     */   
/*     */   public int[] numBackwardsCalls() {
/*  82 */     return this.backwardsCallCounts;
/*     */   }
/*     */   
/*     */   public boolean isUsedAtLeastOnce() {
/*  86 */     return this.usedAtLeastOnce;
/*     */   }
/*     */   
/*     */   private void parseLayout() throws IOException {
/*  90 */     String layout = this.def.layout.getUnderlyingString();
/*  91 */     if (this.attributeLayoutElements == null) {
/*  92 */       this.attributeLayoutElements = new ArrayList();
/*  93 */       StringReader stream = new StringReader(layout);
/*     */       AttributeLayoutElement e;
/*  95 */       while ((e = readNextAttributeElement(stream)) != null) {
/*  96 */         this.attributeLayoutElements.add(e);
/*     */       }
/*  98 */       resolveCalls();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void resolveCalls() {
/* 108 */     for (int i = 0; i < this.attributeLayoutElements.size(); i++) {
/* 109 */       AttributeLayoutElement element = this.attributeLayoutElements.get(i);
/* 110 */       if (element instanceof Callable) {
/* 111 */         Callable callable = (Callable)element;
/* 112 */         List<LayoutElement> body = callable.body;
/* 113 */         for (int iIndex = 0; iIndex < body.size(); iIndex++) {
/* 114 */           LayoutElement layoutElement = body.get(iIndex);
/*     */           
/* 116 */           resolveCallsForElement(i, callable, layoutElement);
/*     */         } 
/*     */       } 
/*     */     } 
/* 120 */     int backwardsCallableIndex = 0;
/* 121 */     for (int j = 0; j < this.attributeLayoutElements.size(); j++) {
/* 122 */       AttributeLayoutElement element = this.attributeLayoutElements.get(j);
/* 123 */       if (element instanceof Callable) {
/* 124 */         Callable callable = (Callable)element;
/* 125 */         if (callable.isBackwardsCallable) {
/* 126 */           callable.setBackwardsCallableIndex(backwardsCallableIndex);
/* 127 */           backwardsCallableIndex++;
/*     */         } 
/*     */       } 
/*     */     } 
/* 131 */     this.backwardsCallCounts = new int[backwardsCallableIndex];
/*     */   }
/*     */ 
/*     */   
/*     */   private void resolveCallsForElement(int i, Callable currentCallable, LayoutElement layoutElement) {
/* 136 */     if (layoutElement instanceof Call) {
/* 137 */       Call call = (Call)layoutElement;
/* 138 */       int index = call.callableIndex;
/* 139 */       if (index == 0) {
/* 140 */         call.setCallable(currentCallable);
/* 141 */       } else if (index > 0) {
/* 142 */         for (int k = i + 1; k < this.attributeLayoutElements.size(); k++) {
/* 143 */           AttributeLayoutElement el = this.attributeLayoutElements.get(k);
/*     */           
/* 145 */           index--;
/* 146 */           if (el instanceof Callable && index == 0) {
/* 147 */             call.setCallable((Callable)el);
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } else {
/* 153 */         for (int k = i - 1; k >= 0; k--) {
/* 154 */           AttributeLayoutElement el = this.attributeLayoutElements.get(k);
/*     */           
/* 156 */           index++;
/* 157 */           if (el instanceof Callable && index == 0) {
/* 158 */             call.setCallable((Callable)el);
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/* 164 */     } else if (layoutElement instanceof Replication) {
/* 165 */       List children = ((Replication)layoutElement).layoutElements;
/* 166 */       for (Iterator<LayoutElement> iterator = children.iterator(); iterator.hasNext(); ) {
/* 167 */         LayoutElement object = iterator.next();
/* 168 */         resolveCallsForElement(i, currentCallable, object);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private AttributeLayoutElement readNextAttributeElement(StringReader stream) throws IOException {
/* 174 */     stream.mark(1);
/* 175 */     int nextChar = stream.read();
/* 176 */     if (nextChar == -1) {
/* 177 */       return null;
/*     */     }
/* 179 */     if (nextChar == 91) {
/* 180 */       List body = readBody(getStreamUpToMatchingBracket(stream));
/* 181 */       return new Callable(body);
/*     */     } 
/* 183 */     stream.reset();
/* 184 */     return readNextLayoutElement(stream); } private LayoutElement readNextLayoutElement(StringReader stream) throws IOException { char uint_type; String str, int_type; List<UnionCase> unionCases; UnionCase c; List body; char next;
/*     */     int number;
/*     */     StringBuilder string;
/*     */     char nxt;
/* 188 */     int nextChar = stream.read();
/* 189 */     if (nextChar == -1) {
/* 190 */       return null;
/*     */     }
/*     */     
/* 193 */     switch (nextChar) {
/*     */       
/*     */       case 66:
/*     */       case 72:
/*     */       case 73:
/*     */       case 86:
/* 199 */         return new Integral(new String(new char[] { (char)nextChar }));
/*     */       case 70:
/*     */       case 83:
/* 202 */         return new Integral(new String(new char[] { (char)nextChar, (char)stream.read() }));
/*     */       case 80:
/* 204 */         stream.mark(1);
/* 205 */         if (stream.read() != 79) {
/* 206 */           stream.reset();
/* 207 */           this.lastPIntegral = new Integral("P" + (char)stream.read());
/* 208 */           return this.lastPIntegral;
/*     */         } 
/* 210 */         this.lastPIntegral = new Integral("PO" + (char)stream.read(), this.lastPIntegral);
/* 211 */         return this.lastPIntegral;
/*     */       
/*     */       case 79:
/* 214 */         stream.mark(1);
/* 215 */         if (stream.read() != 83) {
/* 216 */           stream.reset();
/* 217 */           return new Integral("O" + (char)stream.read(), this.lastPIntegral);
/*     */         } 
/* 219 */         return new Integral("OS" + (char)stream.read(), this.lastPIntegral);
/*     */ 
/*     */ 
/*     */       
/*     */       case 78:
/* 224 */         uint_type = (char)stream.read();
/* 225 */         stream.read();
/* 226 */         str = readUpToMatchingBracket(stream);
/* 227 */         return new Replication("" + uint_type, str);
/*     */ 
/*     */       
/*     */       case 84:
/* 231 */         int_type = "" + (char)stream.read();
/* 232 */         if (int_type.equals("S")) {
/* 233 */           int_type = int_type + (char)stream.read();
/*     */         }
/* 235 */         unionCases = new ArrayList();
/*     */         
/* 237 */         while ((c = readNextUnionCase(stream)) != null) {
/* 238 */           unionCases.add(c);
/*     */         }
/* 240 */         stream.read();
/* 241 */         stream.read();
/* 242 */         stream.read();
/* 243 */         body = null;
/* 244 */         stream.mark(1);
/* 245 */         next = (char)stream.read();
/* 246 */         if (next != ']') {
/* 247 */           stream.reset();
/* 248 */           body = readBody(getStreamUpToMatchingBracket(stream));
/*     */         } 
/* 250 */         return new Union(int_type, unionCases, body);
/*     */ 
/*     */       
/*     */       case 40:
/* 254 */         number = readNumber(stream).intValue();
/* 255 */         stream.read();
/* 256 */         return new Call(number);
/*     */       
/*     */       case 75:
/*     */       case 82:
/* 260 */         string = (new StringBuilder("")).append((char)nextChar).append((char)stream.read());
/* 261 */         nxt = (char)stream.read();
/* 262 */         string.append(nxt);
/* 263 */         if (nxt == 'N') {
/* 264 */           string.append((char)stream.read());
/*     */         }
/* 266 */         return new Reference(string.toString());
/*     */     } 
/* 268 */     return null; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private UnionCase readNextUnionCase(StringReader stream) throws IOException {
/* 279 */     stream.mark(2);
/* 280 */     stream.read();
/* 281 */     char next = (char)stream.read();
/* 282 */     if (next == ')') {
/* 283 */       stream.reset();
/* 284 */       return null;
/*     */     } 
/* 286 */     stream.reset();
/* 287 */     stream.read();
/* 288 */     List<Integer> tags = new ArrayList();
/*     */     
/*     */     while (true) {
/* 291 */       Integer nextTag = readNumber(stream);
/* 292 */       if (nextTag != null) {
/* 293 */         tags.add(nextTag);
/* 294 */         stream.read();
/*     */       } 
/* 296 */       if (nextTag == null) {
/* 297 */         stream.read();
/* 298 */         stream.mark(1);
/* 299 */         next = (char)stream.read();
/* 300 */         if (next == ']') {
/* 301 */           return new UnionCase(tags);
/*     */         }
/* 303 */         stream.reset();
/* 304 */         return new UnionCase(tags, readBody(getStreamUpToMatchingBracket(stream)));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static interface AttributeLayoutElement
/*     */   {
/*     */     void addAttributeToBand(NewAttribute param1NewAttribute, InputStream param1InputStream);
/*     */ 
/*     */     
/*     */     void pack(OutputStream param1OutputStream) throws IOException, Pack200Exception;
/*     */     
/*     */     void renumberBci(IntList param1IntList, Map param1Map);
/*     */   }
/*     */   
/*     */   public abstract class LayoutElement
/*     */     implements AttributeLayoutElement
/*     */   {
/*     */     protected int getLength(char uint_type) {
/* 324 */       int length = 0;
/* 325 */       switch (uint_type) {
/*     */         case 'B':
/* 327 */           length = 1;
/*     */           break;
/*     */         case 'H':
/* 330 */           length = 2;
/*     */           break;
/*     */         case 'I':
/* 333 */           length = 4;
/*     */           break;
/*     */         case 'V':
/* 336 */           length = 0;
/*     */           break;
/*     */       } 
/* 339 */       return length;
/*     */     }
/*     */   }
/*     */   
/*     */   public class Integral
/*     */     extends LayoutElement
/*     */   {
/*     */     private final String tag;
/* 347 */     private final List band = new ArrayList();
/*     */     
/*     */     private final BHSDCodec defaultCodec;
/*     */     
/*     */     private Integral previousIntegral;
/*     */     private int previousPValue;
/*     */     
/*     */     public Integral(String tag) {
/* 355 */       this.tag = tag;
/* 356 */       this.defaultCodec = NewAttributeBands.this.getCodec(tag);
/*     */     }
/*     */     
/*     */     public Integral(String tag, Integral previousIntegral) {
/* 360 */       this.tag = tag;
/* 361 */       this.defaultCodec = NewAttributeBands.this.getCodec(tag);
/* 362 */       this.previousIntegral = previousIntegral;
/*     */     }
/*     */     
/*     */     public String getTag() {
/* 366 */       return this.tag;
/*     */     }
/*     */ 
/*     */     
/*     */     public void addAttributeToBand(NewAttribute attribute, InputStream stream) {
/* 371 */       Object val = null;
/* 372 */       int value = 0;
/* 373 */       if (this.tag.equals("B") || this.tag.equals("FB")) {
/* 374 */         value = NewAttributeBands.this.readInteger(1, stream) & 0xFF;
/* 375 */       } else if (this.tag.equals("SB")) {
/* 376 */         value = NewAttributeBands.this.readInteger(1, stream);
/* 377 */       } else if (this.tag.equals("H") || this.tag.equals("FH")) {
/* 378 */         value = NewAttributeBands.this.readInteger(2, stream) & 0xFFFF;
/* 379 */       } else if (this.tag.equals("SH")) {
/* 380 */         value = NewAttributeBands.this.readInteger(2, stream);
/* 381 */       } else if (this.tag.equals("I") || this.tag.equals("FI")) {
/* 382 */         value = NewAttributeBands.this.readInteger(4, stream);
/* 383 */       } else if (this.tag.equals("SI")) {
/* 384 */         value = NewAttributeBands.this.readInteger(4, stream);
/* 385 */       } else if (!this.tag.equals("V") && !this.tag.equals("FV") && !this.tag.equals("SV")) {
/*     */         
/* 387 */         if (this.tag.startsWith("PO") || this.tag.startsWith("OS")) {
/* 388 */           char uint_type = this.tag.substring(2).toCharArray()[0];
/* 389 */           int length = getLength(uint_type);
/* 390 */           value = NewAttributeBands.this.readInteger(length, stream);
/* 391 */           value += this.previousIntegral.previousPValue;
/* 392 */           val = attribute.getLabel(value);
/* 393 */           this.previousPValue = value;
/* 394 */         } else if (this.tag.startsWith("P")) {
/* 395 */           char uint_type = this.tag.substring(1).toCharArray()[0];
/* 396 */           int length = getLength(uint_type);
/* 397 */           value = NewAttributeBands.this.readInteger(length, stream);
/* 398 */           val = attribute.getLabel(value);
/* 399 */           this.previousPValue = value;
/* 400 */         } else if (this.tag.startsWith("O")) {
/* 401 */           char uint_type = this.tag.substring(1).toCharArray()[0];
/* 402 */           int length = getLength(uint_type);
/* 403 */           value = NewAttributeBands.this.readInteger(length, stream);
/* 404 */           value += this.previousIntegral.previousPValue;
/* 405 */           val = attribute.getLabel(value);
/* 406 */           this.previousPValue = value;
/*     */         } 
/* 408 */       }  if (val == null) {
/* 409 */         val = Integer.valueOf(value);
/*     */       }
/* 411 */       this.band.add(val);
/*     */     }
/*     */ 
/*     */     
/*     */     public void pack(OutputStream out) throws IOException, Pack200Exception {
/* 416 */       PackingUtils.log("Writing new attribute bands...");
/* 417 */       byte[] encodedBand = NewAttributeBands.this.encodeBandInt(this.tag, NewAttributeBands.this.integerListToArray(this.band), this.defaultCodec);
/* 418 */       out.write(encodedBand);
/* 419 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + this.tag + "[" + this.band.size() + "]");
/*     */     }
/*     */     
/*     */     public int latestValue() {
/* 423 */       return ((Integer)this.band.get(this.band.size() - 1)).intValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {
/* 428 */       if (this.tag.startsWith("O") || this.tag.startsWith("PO")) {
/* 429 */         renumberOffsetBci(this.previousIntegral.band, bciRenumbering, labelsToOffsets);
/* 430 */       } else if (this.tag.startsWith("P")) {
/* 431 */         for (int i = this.band.size() - 1; i >= 0; i--) {
/* 432 */           Object label = this.band.get(i);
/* 433 */           if (label instanceof Integer) {
/*     */             break;
/*     */           }
/* 436 */           if (label instanceof org.objectweb.asm.Label) {
/* 437 */             this.band.remove(i);
/* 438 */             Integer bytecodeIndex = (Integer)labelsToOffsets.get(label);
/* 439 */             this.band.add(i, Integer.valueOf(bciRenumbering.get(bytecodeIndex.intValue())));
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     private void renumberOffsetBci(List<Integer> relative, IntList bciRenumbering, Map labelsToOffsets) {
/* 446 */       for (int i = this.band.size() - 1; i >= 0; i--) {
/* 447 */         Object label = this.band.get(i);
/* 448 */         if (label instanceof Integer) {
/*     */           break;
/*     */         }
/* 451 */         if (label instanceof org.objectweb.asm.Label) {
/* 452 */           this.band.remove(i);
/* 453 */           Integer bytecodeIndex = (Integer)labelsToOffsets.get(label);
/*     */           
/* 455 */           Integer renumberedOffset = Integer.valueOf(bciRenumbering.get(bytecodeIndex.intValue()) - ((Integer)relative.get(i)).intValue());
/* 456 */           this.band.add(i, renumberedOffset);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public class Replication
/*     */     extends LayoutElement
/*     */   {
/*     */     private final NewAttributeBands.Integral countElement;
/*     */ 
/*     */     
/* 470 */     private final List layoutElements = new ArrayList();
/*     */     
/*     */     public NewAttributeBands.Integral getCountElement() {
/* 473 */       return this.countElement;
/*     */     }
/*     */     
/*     */     public List getLayoutElements() {
/* 477 */       return this.layoutElements;
/*     */     }
/*     */     
/*     */     public Replication(String tag, String contents) throws IOException {
/* 481 */       this.countElement = new NewAttributeBands.Integral(tag);
/* 482 */       StringReader stream = new StringReader(contents);
/*     */       NewAttributeBands.LayoutElement e;
/* 484 */       while ((e = NewAttributeBands.this.readNextLayoutElement(stream)) != null) {
/* 485 */         this.layoutElements.add(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void addAttributeToBand(NewAttribute attribute, InputStream stream) {
/* 491 */       this.countElement.addAttributeToBand(attribute, stream);
/* 492 */       int count = this.countElement.latestValue();
/* 493 */       for (int i = 0; i < count; i++) {
/* 494 */         for (Iterator<NewAttributeBands.AttributeLayoutElement> iterator = this.layoutElements.iterator(); iterator.hasNext(); ) {
/* 495 */           NewAttributeBands.AttributeLayoutElement layoutElement = iterator.next();
/* 496 */           layoutElement.addAttributeToBand(attribute, stream);
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void pack(OutputStream out) throws IOException, Pack200Exception {
/* 503 */       this.countElement.pack(out);
/* 504 */       for (Iterator<NewAttributeBands.AttributeLayoutElement> iterator = this.layoutElements.iterator(); iterator.hasNext(); ) {
/* 505 */         NewAttributeBands.AttributeLayoutElement layoutElement = iterator.next();
/* 506 */         layoutElement.pack(out);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {
/* 512 */       for (Iterator<NewAttributeBands.AttributeLayoutElement> iterator = this.layoutElements.iterator(); iterator.hasNext(); ) {
/* 513 */         NewAttributeBands.AttributeLayoutElement layoutElement = iterator.next();
/* 514 */         layoutElement.renumberBci(bciRenumbering, labelsToOffsets);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public class Union
/*     */     extends LayoutElement
/*     */   {
/*     */     private final NewAttributeBands.Integral unionTag;
/*     */     
/*     */     private final List unionCases;
/*     */     private final List defaultCaseBody;
/*     */     
/*     */     public Union(String tag, List unionCases, List body) {
/* 529 */       this.unionTag = new NewAttributeBands.Integral(tag);
/* 530 */       this.unionCases = unionCases;
/* 531 */       this.defaultCaseBody = body;
/*     */     }
/*     */ 
/*     */     
/*     */     public void addAttributeToBand(NewAttribute attribute, InputStream stream) {
/* 536 */       this.unionTag.addAttributeToBand(attribute, stream);
/* 537 */       long tag = this.unionTag.latestValue();
/* 538 */       boolean defaultCase = true; int i;
/* 539 */       for (i = 0; i < this.unionCases.size(); i++) {
/* 540 */         NewAttributeBands.UnionCase element = this.unionCases.get(i);
/* 541 */         if (element.hasTag(tag)) {
/* 542 */           defaultCase = false;
/* 543 */           element.addAttributeToBand(attribute, stream);
/*     */         } 
/*     */       } 
/* 546 */       if (defaultCase) {
/* 547 */         for (i = 0; i < this.defaultCaseBody.size(); i++) {
/* 548 */           NewAttributeBands.LayoutElement element = this.defaultCaseBody.get(i);
/* 549 */           element.addAttributeToBand(attribute, stream);
/*     */         } 
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void pack(OutputStream out) throws IOException, Pack200Exception {
/* 556 */       this.unionTag.pack(out);
/* 557 */       for (Iterator<NewAttributeBands.UnionCase> iterator1 = this.unionCases.iterator(); iterator1.hasNext(); ) {
/* 558 */         NewAttributeBands.UnionCase unionCase = iterator1.next();
/* 559 */         unionCase.pack(out);
/*     */       } 
/* 561 */       for (Iterator<NewAttributeBands.AttributeLayoutElement> iterator = this.defaultCaseBody.iterator(); iterator.hasNext(); ) {
/* 562 */         NewAttributeBands.AttributeLayoutElement layoutElement = iterator.next();
/* 563 */         layoutElement.pack(out);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {
/* 569 */       for (Iterator<NewAttributeBands.UnionCase> iterator1 = this.unionCases.iterator(); iterator1.hasNext(); ) {
/* 570 */         NewAttributeBands.UnionCase unionCase = iterator1.next();
/* 571 */         unionCase.renumberBci(bciRenumbering, labelsToOffsets);
/*     */       } 
/* 573 */       for (Iterator<NewAttributeBands.AttributeLayoutElement> iterator = this.defaultCaseBody.iterator(); iterator.hasNext(); ) {
/* 574 */         NewAttributeBands.AttributeLayoutElement layoutElement = iterator.next();
/* 575 */         layoutElement.renumberBci(bciRenumbering, labelsToOffsets);
/*     */       } 
/*     */     }
/*     */     
/*     */     public NewAttributeBands.Integral getUnionTag() {
/* 580 */       return this.unionTag;
/*     */     }
/*     */     
/*     */     public List getUnionCases() {
/* 584 */       return this.unionCases;
/*     */     }
/*     */     
/*     */     public List getDefaultCaseBody() {
/* 588 */       return this.defaultCaseBody;
/*     */     }
/*     */   }
/*     */   
/*     */   public class Call
/*     */     extends LayoutElement {
/*     */     private final int callableIndex;
/*     */     private NewAttributeBands.Callable callable;
/*     */     
/*     */     public Call(int callableIndex) {
/* 598 */       this.callableIndex = callableIndex;
/*     */     }
/*     */     
/*     */     public void setCallable(NewAttributeBands.Callable callable) {
/* 602 */       this.callable = callable;
/* 603 */       if (this.callableIndex < 1) {
/* 604 */         callable.setBackwardsCallable();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void addAttributeToBand(NewAttribute attribute, InputStream stream) {
/* 610 */       this.callable.addAttributeToBand(attribute, stream);
/* 611 */       if (this.callableIndex < 1) {
/* 612 */         this.callable.addBackwardsCall();
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void pack(OutputStream out) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public int getCallableIndex() {
/* 627 */       return this.callableIndex;
/*     */     }
/*     */     
/*     */     public NewAttributeBands.Callable getCallable() {
/* 631 */       return this.callable;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public class Reference
/*     */     extends LayoutElement
/*     */   {
/*     */     private final String tag;
/*     */     
/*     */     private List band;
/*     */     
/*     */     private boolean nullsAllowed = false;
/*     */ 
/*     */     
/*     */     public Reference(String tag) {
/* 647 */       this.tag = tag;
/* 648 */       this.nullsAllowed = (tag.indexOf('N') != -1);
/*     */     }
/*     */ 
/*     */     
/*     */     public void addAttributeToBand(NewAttribute attribute, InputStream stream) {
/* 653 */       int index = NewAttributeBands.this.readInteger(4, stream);
/* 654 */       if (this.tag.startsWith("RC")) {
/* 655 */         this.band.add(NewAttributeBands.this.cpBands.getCPClass(attribute.readClass(index)));
/* 656 */       } else if (this.tag.startsWith("RU")) {
/* 657 */         this.band.add(NewAttributeBands.this.cpBands.getCPUtf8(attribute.readUTF8(index)));
/* 658 */       } else if (this.tag.startsWith("RS")) {
/* 659 */         this.band.add(NewAttributeBands.this.cpBands.getCPSignature(attribute.readUTF8(index)));
/*     */       } else {
/* 661 */         this.band.add(NewAttributeBands.this.cpBands.getConstant(attribute.readConst(index)));
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public String getTag() {
/* 667 */       return this.tag;
/*     */     }
/*     */ 
/*     */     
/*     */     public void pack(OutputStream out) throws IOException, Pack200Exception {
/*     */       int[] ints;
/* 673 */       if (this.nullsAllowed) {
/* 674 */         ints = NewAttributeBands.this.cpEntryOrNullListToArray(this.band);
/*     */       } else {
/* 676 */         ints = NewAttributeBands.this.cpEntryListToArray(this.band);
/*     */       } 
/* 678 */       byte[] encodedBand = NewAttributeBands.this.encodeBandInt(this.tag, ints, Codec.UNSIGNED5);
/* 679 */       out.write(encodedBand);
/* 680 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + this.tag + "[" + ints.length + "]");
/*     */     }
/*     */ 
/*     */     
/*     */     public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {}
/*     */   }
/*     */ 
/*     */   
/*     */   public class Callable
/*     */     implements AttributeLayoutElement
/*     */   {
/*     */     private final List body;
/*     */     
/*     */     private boolean isBackwardsCallable;
/*     */     
/*     */     private int backwardsCallableIndex;
/*     */ 
/*     */     
/*     */     public Callable(List body) throws IOException {
/* 699 */       this.body = body;
/*     */     }
/*     */     
/*     */     public void setBackwardsCallableIndex(int backwardsCallableIndex) {
/* 703 */       this.backwardsCallableIndex = backwardsCallableIndex;
/*     */     }
/*     */     
/*     */     public void addBackwardsCall() {
/* 707 */       NewAttributeBands.this.backwardsCallCounts[this.backwardsCallableIndex] = NewAttributeBands.this.backwardsCallCounts[this.backwardsCallableIndex] + 1;
/*     */     }
/*     */     
/*     */     public boolean isBackwardsCallable() {
/* 711 */       return this.isBackwardsCallable;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setBackwardsCallable() {
/* 718 */       this.isBackwardsCallable = true;
/*     */     }
/*     */ 
/*     */     
/*     */     public void addAttributeToBand(NewAttribute attribute, InputStream stream) {
/* 723 */       for (Iterator<NewAttributeBands.AttributeLayoutElement> iterator = this.body.iterator(); iterator.hasNext(); ) {
/* 724 */         NewAttributeBands.AttributeLayoutElement layoutElement = iterator.next();
/* 725 */         layoutElement.addAttributeToBand(attribute, stream);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void pack(OutputStream out) throws IOException, Pack200Exception {
/* 731 */       for (Iterator<NewAttributeBands.AttributeLayoutElement> iterator = this.body.iterator(); iterator.hasNext(); ) {
/* 732 */         NewAttributeBands.AttributeLayoutElement layoutElement = iterator.next();
/* 733 */         layoutElement.pack(out);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {
/* 739 */       for (Iterator<NewAttributeBands.AttributeLayoutElement> iterator = this.body.iterator(); iterator.hasNext(); ) {
/* 740 */         NewAttributeBands.AttributeLayoutElement layoutElement = iterator.next();
/* 741 */         layoutElement.renumberBci(bciRenumbering, labelsToOffsets);
/*     */       } 
/*     */     }
/*     */     
/*     */     public List getBody() {
/* 746 */       return this.body;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public class UnionCase
/*     */     extends LayoutElement
/*     */   {
/*     */     private final List body;
/*     */     
/*     */     private final List tags;
/*     */ 
/*     */     
/*     */     public UnionCase(List tags) {
/* 760 */       this.tags = tags;
/* 761 */       this.body = Collections.EMPTY_LIST;
/*     */     }
/*     */     
/*     */     public boolean hasTag(long l) {
/* 765 */       return this.tags.contains(Integer.valueOf((int)l));
/*     */     }
/*     */     
/*     */     public UnionCase(List tags, List body) throws IOException {
/* 769 */       this.tags = tags;
/* 770 */       this.body = body;
/*     */     }
/*     */ 
/*     */     
/*     */     public void addAttributeToBand(NewAttribute attribute, InputStream stream) {
/* 775 */       for (int i = 0; i < this.body.size(); i++) {
/* 776 */         NewAttributeBands.LayoutElement element = this.body.get(i);
/* 777 */         element.addAttributeToBand(attribute, stream);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void pack(OutputStream out) throws IOException, Pack200Exception {
/* 783 */       for (int i = 0; i < this.body.size(); i++) {
/* 784 */         NewAttributeBands.LayoutElement element = this.body.get(i);
/* 785 */         element.pack(out);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {
/* 791 */       for (int i = 0; i < this.body.size(); i++) {
/* 792 */         NewAttributeBands.LayoutElement element = this.body.get(i);
/* 793 */         element.renumberBci(bciRenumbering, labelsToOffsets);
/*     */       } 
/*     */     }
/*     */     
/*     */     public List getBody() {
/* 798 */       return this.body;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private StringReader getStreamUpToMatchingBracket(StringReader stream) throws IOException {
/* 811 */     StringBuffer sb = new StringBuffer();
/* 812 */     int foundBracket = -1;
/* 813 */     while (foundBracket != 0) {
/* 814 */       char c = (char)stream.read();
/* 815 */       if (c == ']') {
/* 816 */         foundBracket++;
/*     */       }
/* 818 */       if (c == '[') {
/* 819 */         foundBracket--;
/*     */       }
/* 821 */       if (foundBracket != 0) {
/* 822 */         sb.append(c);
/*     */       }
/*     */     } 
/* 825 */     return new StringReader(sb.toString());
/*     */   }
/*     */   
/*     */   private int readInteger(int i, InputStream stream) {
/* 829 */     int result = 0;
/* 830 */     for (int j = 0; j < i; j++) {
/*     */       try {
/* 832 */         result = result << 8 | stream.read();
/* 833 */       } catch (IOException e) {
/* 834 */         throw new RuntimeException("Error reading unknown attribute");
/*     */       } 
/*     */     } 
/*     */     
/* 838 */     if (i == 1) {
/* 839 */       result = (byte)result;
/*     */     }
/* 841 */     if (i == 2) {
/* 842 */       result = (short)result;
/*     */     }
/* 844 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private BHSDCodec getCodec(String layoutElement) {
/* 853 */     if (layoutElement.indexOf('O') >= 0) {
/* 854 */       return Codec.BRANCH5;
/*     */     }
/* 856 */     if (layoutElement.indexOf('P') >= 0) {
/* 857 */       return Codec.BCI5;
/*     */     }
/* 859 */     if (layoutElement.indexOf('S') >= 0 && layoutElement.indexOf("KS") < 0 && layoutElement
/* 860 */       .indexOf("RS") < 0) {
/* 861 */       return Codec.SIGNED5;
/*     */     }
/* 863 */     if (layoutElement.indexOf('B') >= 0) {
/* 864 */       return Codec.BYTE1;
/*     */     }
/* 866 */     return Codec.UNSIGNED5;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String readUpToMatchingBracket(StringReader stream) throws IOException {
/* 878 */     StringBuffer sb = new StringBuffer();
/* 879 */     int foundBracket = -1;
/* 880 */     while (foundBracket != 0) {
/* 881 */       char c = (char)stream.read();
/* 882 */       if (c == ']') {
/* 883 */         foundBracket++;
/*     */       }
/* 885 */       if (c == '[') {
/* 886 */         foundBracket--;
/*     */       }
/* 888 */       if (foundBracket != 0) {
/* 889 */         sb.append(c);
/*     */       }
/*     */     } 
/* 892 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Integer readNumber(StringReader stream) throws IOException {
/* 903 */     stream.mark(1);
/* 904 */     char first = (char)stream.read();
/* 905 */     boolean negative = (first == '-');
/* 906 */     if (!negative) {
/* 907 */       stream.reset();
/*     */     }
/* 909 */     stream.mark(100);
/*     */     
/* 911 */     int length = 0; int i;
/* 912 */     while ((i = stream.read()) != -1 && Character.isDigit((char)i)) {
/* 913 */       length++;
/*     */     }
/* 915 */     stream.reset();
/* 916 */     if (length == 0) {
/* 917 */       return null;
/*     */     }
/* 919 */     char[] digits = new char[length];
/* 920 */     int read = stream.read(digits);
/* 921 */     if (read != digits.length) {
/* 922 */       throw new IOException("Error reading from the input stream");
/*     */     }
/* 924 */     return Integer.valueOf(Integer.parseInt((negative ? "-" : "") + new String(digits)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List readBody(StringReader stream) throws IOException {
/* 935 */     List<LayoutElement> layoutElements = new ArrayList();
/*     */     LayoutElement e;
/* 937 */     while ((e = readNextLayoutElement(stream)) != null) {
/* 938 */       layoutElements.add(e);
/*     */     }
/* 940 */     return layoutElements;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {
/* 950 */     for (Iterator<AttributeLayoutElement> iterator = this.attributeLayoutElements.iterator(); iterator.hasNext(); ) {
/* 951 */       AttributeLayoutElement element = iterator.next();
/* 952 */       element.renumberBci(bciRenumbering, labelsToOffsets);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\NewAttributeBands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */