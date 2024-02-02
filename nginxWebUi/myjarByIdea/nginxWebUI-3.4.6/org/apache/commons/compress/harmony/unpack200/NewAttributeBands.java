package org.apache.commons.compress.harmony.unpack200;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.compress.harmony.pack200.BHSDCodec;
import org.apache.commons.compress.harmony.pack200.Codec;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.bytecode.Attribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPDouble;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPFieldRef;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPFloat;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPInteger;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPInterfaceMethodRef;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPLong;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPMethodRef;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPNameAndType;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPString;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPUTF8;
import org.apache.commons.compress.harmony.unpack200.bytecode.NewAttribute;

public class NewAttributeBands extends BandSet {
   private final AttributeLayout attributeLayout;
   private int backwardsCallCount;
   protected List attributeLayoutElements;

   public NewAttributeBands(Segment segment, AttributeLayout attributeLayout) throws IOException {
      super(segment);
      this.attributeLayout = attributeLayout;
      this.parseLayout();
      attributeLayout.setBackwardsCallCount(this.backwardsCallCount);
   }

   public void read(InputStream in) throws IOException, Pack200Exception {
   }

   public List parseAttributes(InputStream in, int occurrenceCount) throws IOException, Pack200Exception {
      for(int i = 0; i < this.attributeLayoutElements.size(); ++i) {
         AttributeLayoutElement element = (AttributeLayoutElement)this.attributeLayoutElements.get(i);
         element.readBands(in, occurrenceCount);
      }

      List attributes = new ArrayList(occurrenceCount);

      for(int i = 0; i < occurrenceCount; ++i) {
         attributes.add(this.getOneAttribute(i, this.attributeLayoutElements));
      }

      return attributes;
   }

   private Attribute getOneAttribute(int index, List elements) {
      NewAttribute attribute = new NewAttribute(this.segment.getCpBands().cpUTF8Value(this.attributeLayout.getName()), this.attributeLayout.getIndex());

      for(int i = 0; i < elements.size(); ++i) {
         AttributeLayoutElement element = (AttributeLayoutElement)elements.get(i);
         element.addToAttribute(index, attribute);
      }

      return attribute;
   }

   private void parseLayout() throws IOException {
      if (this.attributeLayoutElements == null) {
         this.attributeLayoutElements = new ArrayList();
         StringReader stream = new StringReader(this.attributeLayout.getLayout());

         AttributeLayoutElement e;
         while((e = this.readNextAttributeElement(stream)) != null) {
            this.attributeLayoutElements.add(e);
         }

         this.resolveCalls();
      }

   }

   private void resolveCalls() {
      int backwardsCalls = 0;

      for(int i = 0; i < this.attributeLayoutElements.size(); ++i) {
         AttributeLayoutElement element = (AttributeLayoutElement)this.attributeLayoutElements.get(i);
         if (element instanceof Callable) {
            Callable callable = (Callable)element;
            if (i == 0) {
               callable.setFirstCallable(true);
            }

            List body = callable.body;

            for(int iIndex = 0; iIndex < body.size(); ++iIndex) {
               LayoutElement layoutElement = (LayoutElement)body.get(iIndex);
               backwardsCalls += this.resolveCallsForElement(i, callable, layoutElement);
            }
         }
      }

      this.backwardsCallCount = backwardsCalls;
   }

   private int resolveCallsForElement(int i, Callable currentCallable, LayoutElement layoutElement) {
      int backwardsCalls = 0;
      if (layoutElement instanceof Call) {
         Call call = (Call)layoutElement;
         int index = call.callableIndex;
         if (index == 0) {
            ++backwardsCalls;
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
               ++backwardsCalls;

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

         LayoutElement object;
         for(Iterator iterator = children.iterator(); iterator.hasNext(); backwardsCalls += this.resolveCallsForElement(i, currentCallable, object)) {
            object = (LayoutElement)iterator.next();
         }
      }

      return backwardsCalls;
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
                  return new Integral("O" + (char)stream.read());
               }

               return new Integral("OS" + (char)stream.read());
            case 80:
               stream.mark(1);
               if (stream.read() != 79) {
                  stream.reset();
                  return new Integral("P" + (char)stream.read());
               }

               return new Integral("PO" + (char)stream.read());
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

   public BHSDCodec getCodec(String layoutElement) {
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

   public int getBackwardsCallCount() {
      return this.backwardsCallCount;
   }

   public void setBackwardsCalls(int[] backwardsCalls) throws IOException {
      int index = 0;
      this.parseLayout();

      for(int i = 0; i < this.attributeLayoutElements.size(); ++i) {
         AttributeLayoutElement element = (AttributeLayoutElement)this.attributeLayoutElements.get(i);
         if (element instanceof Callable && ((Callable)element).isBackwardsCallable()) {
            ((Callable)element).addCount(backwardsCalls[index]);
            ++index;
         }
      }

   }

   public void unpack() throws IOException, Pack200Exception {
   }

   public class UnionCase extends LayoutElement {
      private List body;
      private final List tags;

      public UnionCase(List tags) {
         super(null);
         this.tags = tags;
      }

      public boolean hasTag(long l) {
         return this.tags.contains((int)l);
      }

      public UnionCase(List tags, List body) throws IOException {
         super(null);
         this.tags = tags;
         this.body = body;
      }

      public void readBands(InputStream in, int count) throws IOException, Pack200Exception {
         if (this.body != null) {
            for(int i = 0; i < this.body.size(); ++i) {
               LayoutElement element = (LayoutElement)this.body.get(i);
               element.readBands(in, count);
            }
         }

      }

      public void addToAttribute(int index, NewAttribute attribute) {
         if (this.body != null) {
            for(int i = 0; i < this.body.size(); ++i) {
               LayoutElement element = (LayoutElement)this.body.get(i);
               element.addToAttribute(index, attribute);
            }
         }

      }

      public List getBody() {
         return this.body == null ? Collections.EMPTY_LIST : this.body;
      }
   }

   public static class Callable implements AttributeLayoutElement {
      private final List body;
      private boolean isBackwardsCallable;
      private boolean isFirstCallable;
      private int count;
      private int index;

      public Callable(List body) throws IOException {
         this.body = body;
      }

      public void addNextToAttribute(NewAttribute attribute) {
         for(int i = 0; i < this.body.size(); ++i) {
            LayoutElement element = (LayoutElement)this.body.get(i);
            element.addToAttribute(this.index, attribute);
         }

         ++this.index;
      }

      public void addCount(int count) {
         this.count += count;
      }

      public void readBands(InputStream in, int count) throws IOException, Pack200Exception {
         if (this.isFirstCallable) {
            count += this.count;
         } else {
            count = this.count;
         }

         for(int i = 0; i < this.body.size(); ++i) {
            LayoutElement element = (LayoutElement)this.body.get(i);
            element.readBands(in, count);
         }

      }

      public void addToAttribute(int n, NewAttribute attribute) {
         if (this.isFirstCallable) {
            for(int i = 0; i < this.body.size(); ++i) {
               LayoutElement element = (LayoutElement)this.body.get(i);
               element.addToAttribute(this.index, attribute);
            }

            ++this.index;
         }

      }

      public boolean isBackwardsCallable() {
         return this.isBackwardsCallable;
      }

      public void setBackwardsCallable() {
         this.isBackwardsCallable = true;
      }

      public void setFirstCallable(boolean isFirstCallable) {
         this.isFirstCallable = isFirstCallable;
      }

      public List getBody() {
         return this.body;
      }
   }

   public class Reference extends LayoutElement {
      private final String tag;
      private Object band;
      private final int length;

      public Reference(String tag) {
         super(null);
         this.tag = tag;
         this.length = this.getLength(tag.charAt(tag.length() - 1));
      }

      public void readBands(InputStream in, int count) throws IOException, Pack200Exception {
         if (this.tag.startsWith("KI")) {
            this.band = NewAttributeBands.this.parseCPIntReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
         } else if (this.tag.startsWith("KJ")) {
            this.band = NewAttributeBands.this.parseCPLongReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
         } else if (this.tag.startsWith("KF")) {
            this.band = NewAttributeBands.this.parseCPFloatReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
         } else if (this.tag.startsWith("KD")) {
            this.band = NewAttributeBands.this.parseCPDoubleReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
         } else if (this.tag.startsWith("KS")) {
            this.band = NewAttributeBands.this.parseCPStringReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
         } else if (this.tag.startsWith("RC")) {
            this.band = NewAttributeBands.this.parseCPClassReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
         } else if (this.tag.startsWith("RS")) {
            this.band = NewAttributeBands.this.parseCPSignatureReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
         } else if (this.tag.startsWith("RD")) {
            this.band = NewAttributeBands.this.parseCPDescriptorReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
         } else if (this.tag.startsWith("RF")) {
            this.band = NewAttributeBands.this.parseCPFieldRefReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
         } else if (this.tag.startsWith("RM")) {
            this.band = NewAttributeBands.this.parseCPMethodRefReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
         } else if (this.tag.startsWith("RI")) {
            this.band = NewAttributeBands.this.parseCPInterfaceMethodRefReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
         } else if (this.tag.startsWith("RU")) {
            this.band = NewAttributeBands.this.parseCPUTF8References(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
         }

      }

      public void addToAttribute(int n, NewAttribute attribute) {
         if (this.tag.startsWith("KI")) {
            attribute.addToBody(this.length, ((CPInteger[])((CPInteger[])this.band))[n]);
         } else if (this.tag.startsWith("KJ")) {
            attribute.addToBody(this.length, ((CPLong[])((CPLong[])this.band))[n]);
         } else if (this.tag.startsWith("KF")) {
            attribute.addToBody(this.length, ((CPFloat[])((CPFloat[])this.band))[n]);
         } else if (this.tag.startsWith("KD")) {
            attribute.addToBody(this.length, ((CPDouble[])((CPDouble[])this.band))[n]);
         } else if (this.tag.startsWith("KS")) {
            attribute.addToBody(this.length, ((CPString[])((CPString[])this.band))[n]);
         } else if (this.tag.startsWith("RC")) {
            attribute.addToBody(this.length, ((CPClass[])((CPClass[])this.band))[n]);
         } else if (this.tag.startsWith("RS")) {
            attribute.addToBody(this.length, ((CPUTF8[])((CPUTF8[])this.band))[n]);
         } else if (this.tag.startsWith("RD")) {
            attribute.addToBody(this.length, ((CPNameAndType[])((CPNameAndType[])this.band))[n]);
         } else if (this.tag.startsWith("RF")) {
            attribute.addToBody(this.length, ((CPFieldRef[])((CPFieldRef[])this.band))[n]);
         } else if (this.tag.startsWith("RM")) {
            attribute.addToBody(this.length, ((CPMethodRef[])((CPMethodRef[])this.band))[n]);
         } else if (this.tag.startsWith("RI")) {
            attribute.addToBody(this.length, ((CPInterfaceMethodRef[])((CPInterfaceMethodRef[])this.band))[n]);
         } else if (this.tag.startsWith("RU")) {
            attribute.addToBody(this.length, ((CPUTF8[])((CPUTF8[])this.band))[n]);
         }

      }

      public String getTag() {
         return this.tag;
      }
   }

   public class Call extends LayoutElement {
      private final int callableIndex;
      private Callable callable;

      public Call(int callableIndex) {
         super(null);
         this.callableIndex = callableIndex;
      }

      public void setCallable(Callable callable) {
         this.callable = callable;
         if (this.callableIndex < 1) {
            callable.setBackwardsCallable();
         }

      }

      public void readBands(InputStream in, int count) {
         if (this.callableIndex > 0) {
            this.callable.addCount(count);
         }

      }

      public void addToAttribute(int n, NewAttribute attribute) {
         this.callable.addNextToAttribute(attribute);
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
      private int[] caseCounts;
      private int defaultCount;

      public Union(String tag, List unionCases, List body) {
         super(null);
         this.unionTag = NewAttributeBands.this.new Integral(tag);
         this.unionCases = unionCases;
         this.defaultCaseBody = body;
      }

      public void readBands(InputStream in, int count) throws IOException, Pack200Exception {
         this.unionTag.readBands(in, count);
         int[] values = this.unionTag.band;
         this.caseCounts = new int[this.unionCases.size()];

         int i;
         int it;
         for(i = 0; i < this.caseCounts.length; ++i) {
            UnionCase unionCasex = (UnionCase)this.unionCases.get(i);

            for(it = 0; it < values.length; ++it) {
               if (unionCasex.hasTag((long)values[it])) {
                  int var10002 = this.caseCounts[i]++;
               }
            }

            unionCasex.readBands(in, this.caseCounts[i]);
         }

         for(i = 0; i < values.length; ++i) {
            boolean found = false;

            for(it = 0; it < this.unionCases.size(); ++it) {
               UnionCase unionCase = (UnionCase)this.unionCases.get(it);
               if (unionCase.hasTag((long)values[i])) {
                  found = true;
               }
            }

            if (!found) {
               ++this.defaultCount;
            }
         }

         if (this.defaultCaseBody != null) {
            for(i = 0; i < this.defaultCaseBody.size(); ++i) {
               LayoutElement element = (LayoutElement)this.defaultCaseBody.get(i);
               element.readBands(in, this.defaultCount);
            }
         }

      }

      public void addToAttribute(int n, NewAttribute attribute) {
         this.unionTag.addToAttribute(n, attribute);
         int offset = 0;
         int[] tagBand = this.unionTag.band;
         long tag = this.unionTag.getValue(n);
         boolean defaultCase = true;

         int defaultOffset;
         for(defaultOffset = 0; defaultOffset < this.unionCases.size(); ++defaultOffset) {
            UnionCase elementxx = (UnionCase)this.unionCases.get(defaultOffset);
            if (elementxx.hasTag(tag)) {
               defaultCase = false;

               for(int j = 0; j < n; ++j) {
                  if (elementxx.hasTag((long)tagBand[j])) {
                     ++offset;
                  }
               }

               elementxx.addToAttribute(offset, attribute);
            }
         }

         if (defaultCase) {
            defaultOffset = 0;

            int ix;
            for(ix = 0; ix < n; ++ix) {
               boolean found = false;

               for(int i = 0; i < this.unionCases.size(); ++i) {
                  UnionCase element = (UnionCase)this.unionCases.get(i);
                  if (element.hasTag((long)tagBand[ix])) {
                     found = true;
                  }
               }

               if (!found) {
                  ++defaultOffset;
               }
            }

            if (this.defaultCaseBody != null) {
               for(ix = 0; ix < this.defaultCaseBody.size(); ++ix) {
                  LayoutElement elementx = (LayoutElement)this.defaultCaseBody.get(ix);
                  elementx.addToAttribute(defaultOffset, attribute);
               }
            }
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

      public Replication(String tag, String contents) throws IOException {
         super(null);
         this.countElement = NewAttributeBands.this.new Integral(tag);
         StringReader stream = new StringReader(contents);

         LayoutElement e;
         while((e = NewAttributeBands.this.readNextLayoutElement(stream)) != null) {
            this.layoutElements.add(e);
         }

      }

      public void readBands(InputStream in, int count) throws IOException, Pack200Exception {
         this.countElement.readBands(in, count);
         int arrayCount = 0;

         int i;
         for(i = 0; i < count; ++i) {
            arrayCount = (int)((long)arrayCount + this.countElement.getValue(i));
         }

         for(i = 0; i < this.layoutElements.size(); ++i) {
            LayoutElement element = (LayoutElement)this.layoutElements.get(i);
            element.readBands(in, arrayCount);
         }

      }

      public void addToAttribute(int index, NewAttribute attribute) {
         this.countElement.addToAttribute(index, attribute);
         int offset = 0;

         for(int ix = 0; ix < index; ++ix) {
            offset = (int)((long)offset + this.countElement.getValue(ix));
         }

         long numElements = this.countElement.getValue(index);

         for(int i = offset; (long)i < (long)offset + numElements; ++i) {
            for(int it = 0; it < this.layoutElements.size(); ++it) {
               LayoutElement element = (LayoutElement)this.layoutElements.get(it);
               element.addToAttribute(i, attribute);
            }
         }

      }

      public Integral getCountElement() {
         return this.countElement;
      }

      public List getLayoutElements() {
         return this.layoutElements;
      }
   }

   public class Integral extends LayoutElement {
      private final String tag;
      private int[] band;

      public Integral(String tag) {
         super(null);
         this.tag = tag;
      }

      public void readBands(InputStream in, int count) throws IOException, Pack200Exception {
         this.band = NewAttributeBands.this.decodeBandInt(NewAttributeBands.this.attributeLayout.getName() + "_" + this.tag, in, NewAttributeBands.this.getCodec(this.tag), count);
      }

      public void addToAttribute(int n, NewAttribute attribute) {
         long value = (long)this.band[n];
         if (!this.tag.equals("B") && !this.tag.equals("FB")) {
            if (this.tag.equals("SB")) {
               attribute.addInteger(1, (long)((byte)((int)value)));
            } else if (!this.tag.equals("H") && !this.tag.equals("FH")) {
               if (this.tag.equals("SH")) {
                  attribute.addInteger(2, (long)((short)((int)value)));
               } else if (!this.tag.equals("I") && !this.tag.equals("FI")) {
                  if (this.tag.equals("SI")) {
                     attribute.addInteger(4, (long)((int)value));
                  } else if (!this.tag.equals("V") && !this.tag.equals("FV") && !this.tag.equals("SV")) {
                     char uint_type;
                     int length;
                     if (this.tag.startsWith("PO")) {
                        uint_type = this.tag.substring(2).toCharArray()[0];
                        length = this.getLength(uint_type);
                        attribute.addBCOffset(length, (int)value);
                     } else if (this.tag.startsWith("P")) {
                        uint_type = this.tag.substring(1).toCharArray()[0];
                        length = this.getLength(uint_type);
                        attribute.addBCIndex(length, (int)value);
                     } else if (this.tag.startsWith("OS")) {
                        uint_type = this.tag.substring(2).toCharArray()[0];
                        length = this.getLength(uint_type);
                        if (length == 1) {
                           value = (long)((byte)((int)value));
                        } else if (length == 2) {
                           value = (long)((short)((int)value));
                        } else if (length == 4) {
                           value = (long)((int)value);
                        }

                        attribute.addBCLength(length, (int)value);
                     } else if (this.tag.startsWith("O")) {
                        uint_type = this.tag.substring(1).toCharArray()[0];
                        length = this.getLength(uint_type);
                        attribute.addBCLength(length, (int)value);
                     }
                  }
               } else {
                  attribute.addInteger(4, value);
               }
            } else {
               attribute.addInteger(2, value);
            }
         } else {
            attribute.addInteger(1, value);
         }

      }

      long getValue(int index) {
         return (long)this.band[index];
      }

      public String getTag() {
         return this.tag;
      }
   }

   private abstract class LayoutElement implements AttributeLayoutElement {
      private LayoutElement() {
      }

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

      // $FF: synthetic method
      LayoutElement(Object x1) {
         this();
      }
   }

   private interface AttributeLayoutElement {
      void readBands(InputStream var1, int var2) throws IOException, Pack200Exception;

      void addToAttribute(int var1, NewAttribute var2);
   }
}
