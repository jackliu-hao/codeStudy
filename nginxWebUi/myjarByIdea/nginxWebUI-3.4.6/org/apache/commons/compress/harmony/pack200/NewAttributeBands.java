package org.apache.commons.compress.harmony.pack200;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.Label;

public class NewAttributeBands extends BandSet {
   protected List attributeLayoutElements;
   private int[] backwardsCallCounts;
   private final CpBands cpBands;
   private final AttributeDefinitionBands.AttributeDefinition def;
   private boolean usedAtLeastOnce;
   private Integral lastPIntegral;

   public NewAttributeBands(int effort, CpBands cpBands, SegmentHeader header, AttributeDefinitionBands.AttributeDefinition def) throws IOException {
      super(effort, header);
      this.def = def;
      this.cpBands = cpBands;
      this.parseLayout();
   }

   public void addAttribute(NewAttribute attribute) {
      this.usedAtLeastOnce = true;
      InputStream stream = new ByteArrayInputStream(attribute.getBytes());
      Iterator iterator = this.attributeLayoutElements.iterator();

      while(iterator.hasNext()) {
         AttributeLayoutElement layoutElement = (AttributeLayoutElement)iterator.next();
         layoutElement.addAttributeToBand(attribute, stream);
      }

   }

   public void pack(OutputStream out) throws IOException, Pack200Exception {
      Iterator iterator = this.attributeLayoutElements.iterator();

      while(iterator.hasNext()) {
         AttributeLayoutElement layoutElement = (AttributeLayoutElement)iterator.next();
         layoutElement.pack(out);
      }

   }

   public String getAttributeName() {
      return this.def.name.getUnderlyingString();
   }

   public int getFlagIndex() {
      return this.def.index;
   }

   public int[] numBackwardsCalls() {
      return this.backwardsCallCounts;
   }

   public boolean isUsedAtLeastOnce() {
      return this.usedAtLeastOnce;
   }

   private void parseLayout() throws IOException {
      String layout = this.def.layout.getUnderlyingString();
      if (this.attributeLayoutElements == null) {
         this.attributeLayoutElements = new ArrayList();
         StringReader stream = new StringReader(layout);

         AttributeLayoutElement e;
         while((e = this.readNextAttributeElement(stream)) != null) {
            this.attributeLayoutElements.add(e);
         }

         this.resolveCalls();
      }

   }

   private void resolveCalls() {
      int backwardsCallableIndex;
      for(backwardsCallableIndex = 0; backwardsCallableIndex < this.attributeLayoutElements.size(); ++backwardsCallableIndex) {
         AttributeLayoutElement element = (AttributeLayoutElement)this.attributeLayoutElements.get(backwardsCallableIndex);
         if (element instanceof Callable) {
            Callable callable = (Callable)element;
            List body = callable.body;

            for(int iIndex = 0; iIndex < body.size(); ++iIndex) {
               LayoutElement layoutElement = (LayoutElement)body.get(iIndex);
               this.resolveCallsForElement(backwardsCallableIndex, callable, layoutElement);
            }
         }
      }

      backwardsCallableIndex = 0;

      for(int i = 0; i < this.attributeLayoutElements.size(); ++i) {
         AttributeLayoutElement element = (AttributeLayoutElement)this.attributeLayoutElements.get(i);
         if (element instanceof Callable) {
            Callable callable = (Callable)element;
            if (callable.isBackwardsCallable) {
               callable.setBackwardsCallableIndex(backwardsCallableIndex);
               ++backwardsCallableIndex;
            }
         }
      }

      this.backwardsCallCounts = new int[backwardsCallableIndex];
   }

   private void resolveCallsForElement(int i, Callable currentCallable, LayoutElement layoutElement) {
      if (layoutElement instanceof Call) {
         Call call = (Call)layoutElement;
         int index = call.callableIndex;
         if (index == 0) {
            call.setCallable(currentCallable);
         } else {
            int k;
            AttributeLayoutElement el;
            if (index > 0) {
               for(k = i + 1; k < this.attributeLayoutElements.size(); ++k) {
                  el = (AttributeLayoutElement)this.attributeLayoutElements.get(k);
                  if (el instanceof Callable) {
                     --index;
                     if (index == 0) {
                        call.setCallable((Callable)el);
                        break;
                     }
                  }
               }
            } else {
               for(k = i - 1; k >= 0; --k) {
                  el = (AttributeLayoutElement)this.attributeLayoutElements.get(k);
                  if (el instanceof Callable) {
                     ++index;
                     if (index == 0) {
                        call.setCallable((Callable)el);
                        break;
                     }
                  }
               }
            }
         }
      } else if (layoutElement instanceof Replication) {
         List children = ((Replication)layoutElement).layoutElements;
         Iterator iterator = children.iterator();

         while(iterator.hasNext()) {
            LayoutElement object = (LayoutElement)iterator.next();
            this.resolveCallsForElement(i, currentCallable, object);
         }
      }

   }

   private AttributeLayoutElement readNextAttributeElement(StringReader stream) throws IOException {
      stream.mark(1);
      int nextChar = stream.read();
      if (nextChar == -1) {
         return null;
      } else if (nextChar == 91) {
         List body = this.readBody(this.getStreamUpToMatchingBracket(stream));
         return new Callable(body);
      } else {
         stream.reset();
         return this.readNextLayoutElement(stream);
      }
   }

   private LayoutElement readNextLayoutElement(StringReader stream) throws IOException {
      int nextChar = stream.read();
      if (nextChar == -1) {
         return null;
      } else {
         switch (nextChar) {
            case 40:
               int number = this.readNumber(stream);
               stream.read();
               return new Call(number);
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 67:
            case 68:
            case 69:
            case 71:
            case 74:
            case 76:
            case 77:
            case 81:
            case 85:
            default:
               return null;
            case 66:
            case 72:
            case 73:
            case 86:
               return new Integral(new String(new char[]{(char)nextChar}));
            case 70:
            case 83:
               return new Integral(new String(new char[]{(char)nextChar, (char)stream.read()}));
            case 75:
            case 82:
               StringBuilder string = (new StringBuilder("")).append((char)nextChar).append((char)stream.read());
               char nxt = (char)stream.read();
               string.append(nxt);
               if (nxt == 'N') {
                  string.append((char)stream.read());
               }

               return new Reference(string.toString());
            case 78:
               char uint_type = (char)stream.read();
               stream.read();
               String str = this.readUpToMatchingBracket(stream);
               return new Replication("" + uint_type, str);
            case 79:
               stream.mark(1);
               if (stream.read() != 83) {
                  stream.reset();
                  return new Integral("O" + (char)stream.read(), this.lastPIntegral);
               }

               return new Integral("OS" + (char)stream.read(), this.lastPIntegral);
            case 80:
               stream.mark(1);
               if (stream.read() != 79) {
                  stream.reset();
                  this.lastPIntegral = new Integral("P" + (char)stream.read());
                  return this.lastPIntegral;
               }

               this.lastPIntegral = new Integral("PO" + (char)stream.read(), this.lastPIntegral);
               return this.lastPIntegral;
            case 84:
               String int_type = "" + (char)stream.read();
               if (int_type.equals("S")) {
                  int_type = int_type + (char)stream.read();
               }

               List unionCases = new ArrayList();

               UnionCase c;
               while((c = this.readNextUnionCase(stream)) != null) {
                  unionCases.add(c);
               }

               stream.read();
               stream.read();
               stream.read();
               List body = null;
               stream.mark(1);
               char next = (char)stream.read();
               if (next != ']') {
                  stream.reset();
                  body = this.readBody(this.getStreamUpToMatchingBracket(stream));
               }

               return new Union(int_type, unionCases, body);
         }
      }
   }

   private UnionCase readNextUnionCase(StringReader stream) throws IOException {
      stream.mark(2);
      stream.read();
      char next = (char)stream.read();
      if (next == ')') {
         stream.reset();
         return null;
      } else {
         stream.reset();
         stream.read();
         List tags = new ArrayList();

         Integer nextTag;
         do {
            nextTag = this.readNumber(stream);
            if (nextTag != null) {
               tags.add(nextTag);
               stream.read();
            }
         } while(nextTag != null);

         stream.read();
         stream.mark(1);
         next = (char)stream.read();
         if (next == ']') {
            return new UnionCase(tags);
         } else {
            stream.reset();
            return new UnionCase(tags, this.readBody(this.getStreamUpToMatchingBracket(stream)));
         }
      }
   }

   private StringReader getStreamUpToMatchingBracket(StringReader stream) throws IOException {
      StringBuffer sb = new StringBuffer();
      int foundBracket = -1;

      while(foundBracket != 0) {
         char c = (char)stream.read();
         if (c == ']') {
            ++foundBracket;
         }

         if (c == '[') {
            --foundBracket;
         }

         if (foundBracket != 0) {
            sb.append(c);
         }
      }

      return new StringReader(sb.toString());
   }

   private int readInteger(int i, InputStream stream) {
      int result = 0;

      for(int j = 0; j < i; ++j) {
         try {
            result = result << 8 | stream.read();
         } catch (IOException var6) {
            throw new RuntimeException("Error reading unknown attribute");
         }
      }

      if (i == 1) {
         result = (byte)result;
      }

      if (i == 2) {
         result = (short)result;
      }

      return result;
   }

   private BHSDCodec getCodec(String layoutElement) {
      if (layoutElement.indexOf(79) >= 0) {
         return Codec.BRANCH5;
      } else if (layoutElement.indexOf(80) >= 0) {
         return Codec.BCI5;
      } else if (layoutElement.indexOf(83) >= 0 && layoutElement.indexOf("KS") < 0 && layoutElement.indexOf("RS") < 0) {
         return Codec.SIGNED5;
      } else {
         return layoutElement.indexOf(66) >= 0 ? Codec.BYTE1 : Codec.UNSIGNED5;
      }
   }

   private String readUpToMatchingBracket(StringReader stream) throws IOException {
      StringBuffer sb = new StringBuffer();
      int foundBracket = -1;

      while(foundBracket != 0) {
         char c = (char)stream.read();
         if (c == ']') {
            ++foundBracket;
         }

         if (c == '[') {
            --foundBracket;
         }

         if (foundBracket != 0) {
            sb.append(c);
         }
      }

      return sb.toString();
   }

   private Integer readNumber(StringReader stream) throws IOException {
      stream.mark(1);
      char first = (char)stream.read();
      boolean negative = first == '-';
      if (!negative) {
         stream.reset();
      }

      stream.mark(100);

      int i;
      int length;
      for(length = 0; (i = stream.read()) != -1 && Character.isDigit((char)i); ++length) {
      }

      stream.reset();
      if (length == 0) {
         return null;
      } else {
         char[] digits = new char[length];
         int read = stream.read(digits);
         if (read != digits.length) {
            throw new IOException("Error reading from the input stream");
         } else {
            return Integer.parseInt((negative ? "-" : "") + new String(digits));
         }
      }
   }

   private List readBody(StringReader stream) throws IOException {
      List layoutElements = new ArrayList();

      LayoutElement e;
      while((e = this.readNextLayoutElement(stream)) != null) {
         layoutElements.add(e);
      }

      return layoutElements;
   }

   public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {
      Iterator iterator = this.attributeLayoutElements.iterator();

      while(iterator.hasNext()) {
         AttributeLayoutElement element = (AttributeLayoutElement)iterator.next();
         element.renumberBci(bciRenumbering, labelsToOffsets);
      }

   }

   public class UnionCase extends LayoutElement {
      private final List body;
      private final List tags;

      public UnionCase(List tags) {
         super();
         this.tags = tags;
         this.body = Collections.EMPTY_LIST;
      }

      public boolean hasTag(long l) {
         return this.tags.contains((int)l);
      }

      public UnionCase(List tags, List body) throws IOException {
         super();
         this.tags = tags;
         this.body = body;
      }

      public void addAttributeToBand(NewAttribute attribute, InputStream stream) {
         for(int i = 0; i < this.body.size(); ++i) {
            LayoutElement element = (LayoutElement)this.body.get(i);
            element.addAttributeToBand(attribute, stream);
         }

      }

      public void pack(OutputStream out) throws IOException, Pack200Exception {
         for(int i = 0; i < this.body.size(); ++i) {
            LayoutElement element = (LayoutElement)this.body.get(i);
            element.pack(out);
         }

      }

      public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {
         for(int i = 0; i < this.body.size(); ++i) {
            LayoutElement element = (LayoutElement)this.body.get(i);
            element.renumberBci(bciRenumbering, labelsToOffsets);
         }

      }

      public List getBody() {
         return this.body;
      }
   }

   public class Callable implements AttributeLayoutElement {
      private final List body;
      private boolean isBackwardsCallable;
      private int backwardsCallableIndex;

      public Callable(List body) throws IOException {
         this.body = body;
      }

      public void setBackwardsCallableIndex(int backwardsCallableIndex) {
         this.backwardsCallableIndex = backwardsCallableIndex;
      }

      public void addBackwardsCall() {
         int var10002 = NewAttributeBands.this.backwardsCallCounts[this.backwardsCallableIndex]++;
      }

      public boolean isBackwardsCallable() {
         return this.isBackwardsCallable;
      }

      public void setBackwardsCallable() {
         this.isBackwardsCallable = true;
      }

      public void addAttributeToBand(NewAttribute attribute, InputStream stream) {
         Iterator iterator = this.body.iterator();

         while(iterator.hasNext()) {
            AttributeLayoutElement layoutElement = (AttributeLayoutElement)iterator.next();
            layoutElement.addAttributeToBand(attribute, stream);
         }

      }

      public void pack(OutputStream out) throws IOException, Pack200Exception {
         Iterator iterator = this.body.iterator();

         while(iterator.hasNext()) {
            AttributeLayoutElement layoutElement = (AttributeLayoutElement)iterator.next();
            layoutElement.pack(out);
         }

      }

      public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {
         Iterator iterator = this.body.iterator();

         while(iterator.hasNext()) {
            AttributeLayoutElement layoutElement = (AttributeLayoutElement)iterator.next();
            layoutElement.renumberBci(bciRenumbering, labelsToOffsets);
         }

      }

      public List getBody() {
         return this.body;
      }
   }

   public class Reference extends LayoutElement {
      private final String tag;
      private List band;
      private boolean nullsAllowed = false;

      public Reference(String tag) {
         super();
         this.tag = tag;
         this.nullsAllowed = tag.indexOf(78) != -1;
      }

      public void addAttributeToBand(NewAttribute attribute, InputStream stream) {
         int index = NewAttributeBands.this.readInteger(4, stream);
         if (this.tag.startsWith("RC")) {
            this.band.add(NewAttributeBands.this.cpBands.getCPClass(attribute.readClass(index)));
         } else if (this.tag.startsWith("RU")) {
            this.band.add(NewAttributeBands.this.cpBands.getCPUtf8(attribute.readUTF8(index)));
         } else if (this.tag.startsWith("RS")) {
            this.band.add(NewAttributeBands.this.cpBands.getCPSignature(attribute.readUTF8(index)));
         } else {
            this.band.add(NewAttributeBands.this.cpBands.getConstant(attribute.readConst(index)));
         }

      }

      public String getTag() {
         return this.tag;
      }

      public void pack(OutputStream out) throws IOException, Pack200Exception {
         int[] ints;
         if (this.nullsAllowed) {
            ints = NewAttributeBands.this.cpEntryOrNullListToArray(this.band);
         } else {
            ints = NewAttributeBands.this.cpEntryListToArray(this.band);
         }

         byte[] encodedBand = NewAttributeBands.this.encodeBandInt(this.tag, ints, Codec.UNSIGNED5);
         out.write(encodedBand);
         PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + this.tag + "[" + ints.length + "]");
      }

      public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {
      }
   }

   public class Call extends LayoutElement {
      private final int callableIndex;
      private Callable callable;

      public Call(int callableIndex) {
         super();
         this.callableIndex = callableIndex;
      }

      public void setCallable(Callable callable) {
         this.callable = callable;
         if (this.callableIndex < 1) {
            callable.setBackwardsCallable();
         }

      }

      public void addAttributeToBand(NewAttribute attribute, InputStream stream) {
         this.callable.addAttributeToBand(attribute, stream);
         if (this.callableIndex < 1) {
            this.callable.addBackwardsCall();
         }

      }

      public void pack(OutputStream out) {
      }

      public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {
      }

      public int getCallableIndex() {
         return this.callableIndex;
      }

      public Callable getCallable() {
         return this.callable;
      }
   }

   public class Union extends LayoutElement {
      private final Integral unionTag;
      private final List unionCases;
      private final List defaultCaseBody;

      public Union(String tag, List unionCases, List body) {
         super();
         this.unionTag = NewAttributeBands.this.new Integral(tag);
         this.unionCases = unionCases;
         this.defaultCaseBody = body;
      }

      public void addAttributeToBand(NewAttribute attribute, InputStream stream) {
         this.unionTag.addAttributeToBand(attribute, stream);
         long tag = (long)this.unionTag.latestValue();
         boolean defaultCase = true;

         int i;
         for(i = 0; i < this.unionCases.size(); ++i) {
            UnionCase element = (UnionCase)this.unionCases.get(i);
            if (element.hasTag(tag)) {
               defaultCase = false;
               element.addAttributeToBand(attribute, stream);
            }
         }

         if (defaultCase) {
            for(i = 0; i < this.defaultCaseBody.size(); ++i) {
               LayoutElement elementx = (LayoutElement)this.defaultCaseBody.get(i);
               elementx.addAttributeToBand(attribute, stream);
            }
         }

      }

      public void pack(OutputStream out) throws IOException, Pack200Exception {
         this.unionTag.pack(out);
         Iterator iterator = this.unionCases.iterator();

         while(iterator.hasNext()) {
            UnionCase unionCase = (UnionCase)iterator.next();
            unionCase.pack(out);
         }

         iterator = this.defaultCaseBody.iterator();

         while(iterator.hasNext()) {
            AttributeLayoutElement layoutElement = (AttributeLayoutElement)iterator.next();
            layoutElement.pack(out);
         }

      }

      public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {
         Iterator iterator = this.unionCases.iterator();

         while(iterator.hasNext()) {
            UnionCase unionCase = (UnionCase)iterator.next();
            unionCase.renumberBci(bciRenumbering, labelsToOffsets);
         }

         iterator = this.defaultCaseBody.iterator();

         while(iterator.hasNext()) {
            AttributeLayoutElement layoutElement = (AttributeLayoutElement)iterator.next();
            layoutElement.renumberBci(bciRenumbering, labelsToOffsets);
         }

      }

      public Integral getUnionTag() {
         return this.unionTag;
      }

      public List getUnionCases() {
         return this.unionCases;
      }

      public List getDefaultCaseBody() {
         return this.defaultCaseBody;
      }
   }

   public class Replication extends LayoutElement {
      private final Integral countElement;
      private final List layoutElements = new ArrayList();

      public Integral getCountElement() {
         return this.countElement;
      }

      public List getLayoutElements() {
         return this.layoutElements;
      }

      public Replication(String tag, String contents) throws IOException {
         super();
         this.countElement = NewAttributeBands.this.new Integral(tag);
         StringReader stream = new StringReader(contents);

         LayoutElement e;
         while((e = NewAttributeBands.this.readNextLayoutElement(stream)) != null) {
            this.layoutElements.add(e);
         }

      }

      public void addAttributeToBand(NewAttribute attribute, InputStream stream) {
         this.countElement.addAttributeToBand(attribute, stream);
         int count = this.countElement.latestValue();

         for(int i = 0; i < count; ++i) {
            Iterator iterator = this.layoutElements.iterator();

            while(iterator.hasNext()) {
               AttributeLayoutElement layoutElement = (AttributeLayoutElement)iterator.next();
               layoutElement.addAttributeToBand(attribute, stream);
            }
         }

      }

      public void pack(OutputStream out) throws IOException, Pack200Exception {
         this.countElement.pack(out);
         Iterator iterator = this.layoutElements.iterator();

         while(iterator.hasNext()) {
            AttributeLayoutElement layoutElement = (AttributeLayoutElement)iterator.next();
            layoutElement.pack(out);
         }

      }

      public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {
         Iterator iterator = this.layoutElements.iterator();

         while(iterator.hasNext()) {
            AttributeLayoutElement layoutElement = (AttributeLayoutElement)iterator.next();
            layoutElement.renumberBci(bciRenumbering, labelsToOffsets);
         }

      }
   }

   public class Integral extends LayoutElement {
      private final String tag;
      private final List band = new ArrayList();
      private final BHSDCodec defaultCodec;
      private Integral previousIntegral;
      private int previousPValue;

      public Integral(String tag) {
         super();
         this.tag = tag;
         this.defaultCodec = NewAttributeBands.this.getCodec(tag);
      }

      public Integral(String tag, Integral previousIntegral) {
         super();
         this.tag = tag;
         this.defaultCodec = NewAttributeBands.this.getCodec(tag);
         this.previousIntegral = previousIntegral;
      }

      public String getTag() {
         return this.tag;
      }

      public void addAttributeToBand(NewAttribute attribute, InputStream stream) {
         Object val = null;
         int value = 0;
         if (!this.tag.equals("B") && !this.tag.equals("FB")) {
            if (this.tag.equals("SB")) {
               value = NewAttributeBands.this.readInteger(1, stream);
            } else if (!this.tag.equals("H") && !this.tag.equals("FH")) {
               if (this.tag.equals("SH")) {
                  value = NewAttributeBands.this.readInteger(2, stream);
               } else if (!this.tag.equals("I") && !this.tag.equals("FI")) {
                  if (this.tag.equals("SI")) {
                     value = NewAttributeBands.this.readInteger(4, stream);
                  } else if (!this.tag.equals("V") && !this.tag.equals("FV") && !this.tag.equals("SV")) {
                     char uint_type;
                     int length;
                     if (!this.tag.startsWith("PO") && !this.tag.startsWith("OS")) {
                        if (this.tag.startsWith("P")) {
                           uint_type = this.tag.substring(1).toCharArray()[0];
                           length = this.getLength(uint_type);
                           value = NewAttributeBands.this.readInteger(length, stream);
                           val = attribute.getLabel(value);
                           this.previousPValue = value;
                        } else if (this.tag.startsWith("O")) {
                           uint_type = this.tag.substring(1).toCharArray()[0];
                           length = this.getLength(uint_type);
                           value = NewAttributeBands.this.readInteger(length, stream);
                           value += this.previousIntegral.previousPValue;
                           val = attribute.getLabel(value);
                           this.previousPValue = value;
                        }
                     } else {
                        uint_type = this.tag.substring(2).toCharArray()[0];
                        length = this.getLength(uint_type);
                        value = NewAttributeBands.this.readInteger(length, stream);
                        value += this.previousIntegral.previousPValue;
                        val = attribute.getLabel(value);
                        this.previousPValue = value;
                     }
                  }
               } else {
                  value = NewAttributeBands.this.readInteger(4, stream);
               }
            } else {
               value = NewAttributeBands.this.readInteger(2, stream) & '\uffff';
            }
         } else {
            value = NewAttributeBands.this.readInteger(1, stream) & 255;
         }

         if (val == null) {
            val = value;
         }

         this.band.add(val);
      }

      public void pack(OutputStream out) throws IOException, Pack200Exception {
         PackingUtils.log("Writing new attribute bands...");
         byte[] encodedBand = NewAttributeBands.this.encodeBandInt(this.tag, NewAttributeBands.this.integerListToArray(this.band), this.defaultCodec);
         out.write(encodedBand);
         PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + this.tag + "[" + this.band.size() + "]");
      }

      public int latestValue() {
         return (Integer)this.band.get(this.band.size() - 1);
      }

      public void renumberBci(IntList bciRenumbering, Map labelsToOffsets) {
         if (!this.tag.startsWith("O") && !this.tag.startsWith("PO")) {
            if (this.tag.startsWith("P")) {
               for(int i = this.band.size() - 1; i >= 0; --i) {
                  Object label = this.band.get(i);
                  if (label instanceof Integer) {
                     break;
                  }

                  if (label instanceof Label) {
                     this.band.remove(i);
                     Integer bytecodeIndex = (Integer)labelsToOffsets.get(label);
                     this.band.add(i, bciRenumbering.get(bytecodeIndex));
                  }
               }
            }
         } else {
            this.renumberOffsetBci(this.previousIntegral.band, bciRenumbering, labelsToOffsets);
         }

      }

      private void renumberOffsetBci(List relative, IntList bciRenumbering, Map labelsToOffsets) {
         for(int i = this.band.size() - 1; i >= 0; --i) {
            Object label = this.band.get(i);
            if (label instanceof Integer) {
               break;
            }

            if (label instanceof Label) {
               this.band.remove(i);
               Integer bytecodeIndex = (Integer)labelsToOffsets.get(label);
               Integer renumberedOffset = bciRenumbering.get(bytecodeIndex) - (Integer)relative.get(i);
               this.band.add(i, renumberedOffset);
            }
         }

      }
   }

   public abstract class LayoutElement implements AttributeLayoutElement {
      protected int getLength(char uint_type) {
         int length = 0;
         switch (uint_type) {
            case 'B':
               length = 1;
               break;
            case 'H':
               length = 2;
               break;
            case 'I':
               length = 4;
               break;
            case 'V':
               length = 0;
         }

         return length;
      }
   }

   public interface AttributeLayoutElement {
      void addAttributeToBand(NewAttribute var1, InputStream var2);

      void pack(OutputStream var1) throws IOException, Pack200Exception;

      void renumberBci(IntList var1, Map var2);
   }
}
