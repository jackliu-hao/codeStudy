/*      */ package com.github.jaiimageio.plugins.tiff;
/*      */ 
/*      */ import com.github.jaiimageio.impl.plugins.tiff.TIFFFieldNode;
/*      */ import java.util.StringTokenizer;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
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
/*      */ public class TIFFField
/*      */   implements Comparable
/*      */ {
/*  285 */   private static final String[] typeNames = new String[] { null, "Byte", "Ascii", "Short", "Long", "Rational", "SByte", "Undefined", "SShort", "SLong", "SRational", "Float", "Double", "IFDPointer" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  292 */   private static final boolean[] isIntegral = new boolean[] { 
/*      */       false, true, false, true, true, false, true, true, true, true, 
/*      */       false, false, false, false };
/*      */ 
/*      */   
/*      */   private TIFFTag tag;
/*      */ 
/*      */   
/*      */   private int tagNumber;
/*      */ 
/*      */   
/*      */   private int type;
/*      */ 
/*      */   
/*      */   private int count;
/*      */ 
/*      */   
/*      */   private Object data;
/*      */ 
/*      */ 
/*      */   
/*      */   private TIFFField() {}
/*      */ 
/*      */ 
/*      */   
/*      */   private static String getAttribute(Node node, String attrName) {
/*  318 */     NamedNodeMap attrs = node.getAttributes();
/*  319 */     return attrs.getNamedItem(attrName).getNodeValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void initData(Node node, int[] otype, int[] ocount, Object[] odata) {
/*  326 */     Object data = null;
/*      */     
/*  328 */     String typeName = node.getNodeName();
/*  329 */     typeName = typeName.substring(4);
/*  330 */     typeName = typeName.substring(0, typeName.length() - 1);
/*  331 */     int type = getTypeByName(typeName);
/*  332 */     if (type == -1) {
/*  333 */       throw new IllegalArgumentException("typeName = " + typeName);
/*      */     }
/*      */     
/*  336 */     Node child = node.getFirstChild();
/*      */     
/*  338 */     int count = 0;
/*  339 */     while (child != null) {
/*  340 */       String childTypeName = child.getNodeName().substring(4);
/*  341 */       if (!typeName.equals(childTypeName));
/*      */ 
/*      */ 
/*      */       
/*  345 */       count++;
/*  346 */       child = child.getNextSibling();
/*      */     } 
/*      */     
/*  349 */     if (count > 0) {
/*  350 */       data = createArrayForType(type, count);
/*  351 */       child = node.getFirstChild();
/*  352 */       int idx = 0;
/*  353 */       while (child != null) {
/*  354 */         String numerator, denominator; int slashPos; String value = getAttribute(child, "value");
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  359 */         switch (type) {
/*      */           case 2:
/*  361 */             ((String[])data)[idx] = value;
/*      */             break;
/*      */           case 1:
/*      */           case 6:
/*  365 */             ((byte[])data)[idx] = 
/*  366 */               (byte)Integer.parseInt(value);
/*      */             break;
/*      */           case 3:
/*  369 */             ((char[])data)[idx] = 
/*  370 */               (char)Integer.parseInt(value);
/*      */             break;
/*      */           case 8:
/*  373 */             ((short[])data)[idx] = 
/*  374 */               (short)Integer.parseInt(value);
/*      */             break;
/*      */           case 9:
/*  377 */             ((int[])data)[idx] = 
/*  378 */               Integer.parseInt(value);
/*      */             break;
/*      */           case 4:
/*      */           case 13:
/*  382 */             ((long[])data)[idx] = 
/*  383 */               Long.parseLong(value);
/*      */             break;
/*      */           case 11:
/*  386 */             ((float[])data)[idx] = 
/*  387 */               Float.parseFloat(value);
/*      */             break;
/*      */           case 12:
/*  390 */             ((double[])data)[idx] = 
/*  391 */               Double.parseDouble(value);
/*      */             break;
/*      */           case 10:
/*  394 */             slashPos = value.indexOf("/");
/*  395 */             numerator = value.substring(0, slashPos);
/*  396 */             denominator = value.substring(slashPos + 1);
/*      */             
/*  398 */             ((int[][])data)[idx] = new int[2];
/*  399 */             ((int[][])data)[idx][0] = 
/*  400 */               Integer.parseInt(numerator);
/*  401 */             ((int[][])data)[idx][1] = 
/*  402 */               Integer.parseInt(denominator);
/*      */             break;
/*      */           case 5:
/*  405 */             slashPos = value.indexOf("/");
/*  406 */             numerator = value.substring(0, slashPos);
/*  407 */             denominator = value.substring(slashPos + 1);
/*      */             
/*  409 */             ((long[][])data)[idx] = new long[2];
/*  410 */             ((long[][])data)[idx][0] = 
/*  411 */               Long.parseLong(numerator);
/*  412 */             ((long[][])data)[idx][1] = 
/*  413 */               Long.parseLong(denominator);
/*      */             break;
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  419 */         idx++;
/*  420 */         child = child.getNextSibling();
/*      */       } 
/*      */     } 
/*      */     
/*  424 */     otype[0] = type;
/*  425 */     ocount[0] = count;
/*  426 */     odata[0] = data;
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
/*      */   public static TIFFField createFromMetadataNode(TIFFTagSet tagSet, Node node) {
/*      */     TIFFTag tag;
/*  446 */     if (node == null) {
/*  447 */       throw new IllegalArgumentException("node == null!");
/*      */     }
/*  449 */     String name = node.getNodeName();
/*  450 */     if (!name.equals("TIFFField")) {
/*  451 */       throw new IllegalArgumentException("!name.equals(\"TIFFField\")");
/*      */     }
/*      */     
/*  454 */     int tagNumber = Integer.parseInt(getAttribute(node, "number"));
/*      */     
/*  456 */     if (tagSet != null) {
/*  457 */       tag = tagSet.getTag(tagNumber);
/*      */     } else {
/*  459 */       tag = new TIFFTag("unknown", tagNumber, 0, null);
/*      */     } 
/*      */     
/*  462 */     int type = 7;
/*  463 */     int count = 0;
/*  464 */     Object data = null;
/*      */     
/*  466 */     Node child = node.getFirstChild();
/*  467 */     if (child != null) {
/*  468 */       String typeName = child.getNodeName();
/*  469 */       if (typeName.equals("TIFFUndefined")) {
/*  470 */         String values = getAttribute(child, "value");
/*  471 */         StringTokenizer st = new StringTokenizer(values, ",");
/*  472 */         count = st.countTokens();
/*      */         
/*  474 */         byte[] bdata = new byte[count];
/*  475 */         for (int i = 0; i < count; i++) {
/*  476 */           bdata[i] = (byte)Integer.parseInt(st.nextToken());
/*      */         }
/*      */         
/*  479 */         type = 7;
/*  480 */         data = bdata;
/*      */       } else {
/*  482 */         int[] otype = new int[1];
/*  483 */         int[] ocount = new int[1];
/*  484 */         Object[] odata = new Object[1];
/*      */         
/*  486 */         initData(node.getFirstChild(), otype, ocount, odata);
/*  487 */         type = otype[0];
/*  488 */         count = ocount[0];
/*  489 */         data = odata[0];
/*      */       } 
/*      */     } else {
/*  492 */       int t = 13;
/*  493 */       while (t >= 1 && !tag.isDataTypeOK(t)) {
/*  494 */         t--;
/*      */       }
/*  496 */       type = t;
/*      */     } 
/*      */     
/*  499 */     return new TIFFField(tag, type, count, data);
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
/*      */   public TIFFField(TIFFTag tag, int type, int count, Object data) {
/*  545 */     if (tag == null)
/*  546 */       throw new IllegalArgumentException("tag == null!"); 
/*  547 */     if (type < 1 || type > 13)
/*  548 */       throw new IllegalArgumentException("Unknown data type " + type); 
/*  549 */     if (count < 0) {
/*  550 */       throw new IllegalArgumentException("count < 0!");
/*      */     }
/*  552 */     this.tag = tag;
/*  553 */     this.tagNumber = tag.getNumber();
/*  554 */     this.type = type;
/*  555 */     this.count = count;
/*  556 */     this.data = data;
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
/*      */   public TIFFField(TIFFTag tag, int type, int count) {
/*  568 */     this(tag, type, count, createArrayForType(type, count));
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
/*      */   public TIFFField(TIFFTag tag, int value) {
/*  586 */     if (tag == null) {
/*  587 */       throw new IllegalArgumentException("tag == null!");
/*      */     }
/*  589 */     if (value < 0) {
/*  590 */       throw new IllegalArgumentException("value < 0!");
/*      */     }
/*      */     
/*  593 */     this.tag = tag;
/*  594 */     this.tagNumber = tag.getNumber();
/*  595 */     this.count = 1;
/*      */     
/*  597 */     if (value < 65536) {
/*  598 */       this.type = 3;
/*  599 */       char[] cdata = new char[1];
/*  600 */       cdata[0] = (char)value;
/*  601 */       this.data = cdata;
/*      */     } else {
/*  603 */       this.type = 4;
/*  604 */       long[] ldata = new long[1];
/*  605 */       ldata[0] = value;
/*  606 */       this.data = ldata;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TIFFTag getTag() {
/*  616 */     return this.tag;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTagNumber() {
/*  625 */     return this.tagNumber;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getType() {
/*  636 */     return this.type;
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
/*      */   public static String getTypeName(int dataType) {
/*  649 */     if (dataType < 1 || dataType > 13)
/*      */     {
/*  651 */       throw new IllegalArgumentException("Unknown data type " + dataType);
/*      */     }
/*      */     
/*  654 */     return typeNames[dataType];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getTypeByName(String typeName) {
/*  665 */     for (int i = 1; i <= 13; i++) {
/*  666 */       if (typeName.equals(typeNames[i])) {
/*  667 */         return i;
/*      */       }
/*      */     } 
/*      */     
/*  671 */     return -1;
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
/*      */   public static Object createArrayForType(int dataType, int count) {
/*  686 */     if (count < 0) {
/*  687 */       throw new IllegalArgumentException("count < 0!");
/*      */     }
/*  689 */     switch (dataType) {
/*      */       case 1:
/*      */       case 6:
/*      */       case 7:
/*  693 */         return new byte[count];
/*      */       case 2:
/*  695 */         return new String[count];
/*      */       case 3:
/*  697 */         return new char[count];
/*      */       case 4:
/*      */       case 13:
/*  700 */         return new long[count];
/*      */       case 5:
/*  702 */         return new long[count][2];
/*      */       case 8:
/*  704 */         return new short[count];
/*      */       case 9:
/*  706 */         return new int[count];
/*      */       case 10:
/*  708 */         return new int[count][2];
/*      */       case 11:
/*  710 */         return new float[count];
/*      */       case 12:
/*  712 */         return new double[count];
/*      */     } 
/*  714 */     throw new IllegalArgumentException("Unknown data type " + dataType);
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
/*      */   public Node getAsNativeNode() {
/*  731 */     return (Node)new TIFFFieldNode(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isIntegral() {
/*  741 */     return isIntegral[this.type];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getCount() {
/*  751 */     return this.count;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getData() {
/*  760 */     return this.data;
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
/*      */   public byte[] getAsBytes() {
/*  778 */     return (byte[])this.data;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public char[] getAsChars() {
/*  789 */     return (char[])this.data;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public short[] getAsShorts() {
/*  800 */     return (short[])this.data;
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
/*      */   public int[] getAsInts() {
/*  812 */     if (this.data instanceof int[])
/*  813 */       return (int[])this.data; 
/*  814 */     if (this.data instanceof char[]) {
/*  815 */       char[] cdata = (char[])this.data;
/*  816 */       int[] idata = new int[cdata.length];
/*  817 */       for (int i = 0; i < cdata.length; i++) {
/*  818 */         idata[i] = cdata[i] & Character.MAX_VALUE;
/*      */       }
/*  820 */       return idata;
/*  821 */     }  if (this.data instanceof short[]) {
/*  822 */       short[] sdata = (short[])this.data;
/*  823 */       int[] idata = new int[sdata.length];
/*  824 */       for (int i = 0; i < sdata.length; i++) {
/*  825 */         idata[i] = sdata[i];
/*      */       }
/*  827 */       return idata;
/*      */     } 
/*  829 */     throw new ClassCastException("Data not char[], short[], or int[]!");
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
/*      */   public long[] getAsLongs() {
/*  843 */     return (long[])this.data;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public float[] getAsFloats() {
/*  854 */     return (float[])this.data;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double[] getAsDoubles() {
/*  865 */     return (double[])this.data;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int[][] getAsSRationals() {
/*  876 */     return (int[][])this.data;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long[][] getAsRationals() {
/*  887 */     return (long[][])this.data;
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
/*      */   public int getAsInt(int index) {
/*      */     int[] ivalue;
/*      */     long[] lvalue;
/*      */     String s;
/*  917 */     switch (this.type) { case 1:
/*      */       case 7:
/*  919 */         return ((byte[])this.data)[index] & 0xFF;
/*      */       case 6:
/*  921 */         return ((byte[])this.data)[index];
/*      */       case 3:
/*  923 */         return ((char[])this.data)[index] & Character.MAX_VALUE;
/*      */       case 8:
/*  925 */         return ((short[])this.data)[index];
/*      */       case 9:
/*  927 */         return ((int[])this.data)[index];
/*      */       case 4: case 13:
/*  929 */         return (int)((long[])this.data)[index];
/*      */       case 11:
/*  931 */         return (int)((float[])this.data)[index];
/*      */       case 12:
/*  933 */         return (int)((double[])this.data)[index];
/*      */       case 10:
/*  935 */         ivalue = getAsSRational(index);
/*  936 */         return (int)(ivalue[0] / ivalue[1]);
/*      */       case 5:
/*  938 */         lvalue = getAsRational(index);
/*  939 */         return (int)(lvalue[0] / lvalue[1]);
/*      */       case 2:
/*  941 */         s = ((String[])this.data)[index];
/*  942 */         return (int)Double.parseDouble(s); }
/*      */     
/*  944 */     throw new ClassCastException();
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
/*      */   public long getAsLong(int index) {
/*      */     int[] ivalue;
/*      */     long[] lvalue;
/*      */     String s;
/*  962 */     switch (this.type) { case 1:
/*      */       case 7:
/*  964 */         return (((byte[])this.data)[index] & 0xFF);
/*      */       case 6:
/*  966 */         return ((byte[])this.data)[index];
/*      */       case 3:
/*  968 */         return (((char[])this.data)[index] & Character.MAX_VALUE);
/*      */       case 8:
/*  970 */         return ((short[])this.data)[index];
/*      */       case 9:
/*  972 */         return ((int[])this.data)[index];
/*      */       case 4: case 13:
/*  974 */         return ((long[])this.data)[index];
/*      */       case 10:
/*  976 */         ivalue = getAsSRational(index);
/*  977 */         return (long)(ivalue[0] / ivalue[1]);
/*      */       case 5:
/*  979 */         lvalue = getAsRational(index);
/*  980 */         return (long)(lvalue[0] / lvalue[1]);
/*      */       case 2:
/*  982 */         s = ((String[])this.data)[index];
/*  983 */         return (long)Double.parseDouble(s); }
/*      */     
/*  985 */     throw new ClassCastException();
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
/*      */   public float getAsFloat(int index) {
/*      */     int[] ivalue;
/*      */     long[] lvalue;
/*      */     String s;
/* 1013 */     switch (this.type) { case 1:
/*      */       case 7:
/* 1015 */         return (((byte[])this.data)[index] & 0xFF);
/*      */       case 6:
/* 1017 */         return ((byte[])this.data)[index];
/*      */       case 3:
/* 1019 */         return (((char[])this.data)[index] & Character.MAX_VALUE);
/*      */       case 8:
/* 1021 */         return ((short[])this.data)[index];
/*      */       case 9:
/* 1023 */         return ((int[])this.data)[index];
/*      */       case 4: case 13:
/* 1025 */         return (float)((long[])this.data)[index];
/*      */       case 11:
/* 1027 */         return ((float[])this.data)[index];
/*      */       case 12:
/* 1029 */         return (float)((double[])this.data)[index];
/*      */       case 10:
/* 1031 */         ivalue = getAsSRational(index);
/* 1032 */         return (float)(ivalue[0] / ivalue[1]);
/*      */       case 5:
/* 1034 */         lvalue = getAsRational(index);
/* 1035 */         return (float)(lvalue[0] / lvalue[1]);
/*      */       case 2:
/* 1037 */         s = ((String[])this.data)[index];
/* 1038 */         return (float)Double.parseDouble(s); }
/*      */     
/* 1040 */     throw new ClassCastException();
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
/*      */   public double getAsDouble(int index) {
/*      */     int[] ivalue;
/*      */     long[] lvalue;
/*      */     String s;
/* 1062 */     switch (this.type) { case 1:
/*      */       case 7:
/* 1064 */         return (((byte[])this.data)[index] & 0xFF);
/*      */       case 6:
/* 1066 */         return ((byte[])this.data)[index];
/*      */       case 3:
/* 1068 */         return (((char[])this.data)[index] & Character.MAX_VALUE);
/*      */       case 8:
/* 1070 */         return ((short[])this.data)[index];
/*      */       case 9:
/* 1072 */         return ((int[])this.data)[index];
/*      */       case 4: case 13:
/* 1074 */         return ((long[])this.data)[index];
/*      */       case 11:
/* 1076 */         return ((float[])this.data)[index];
/*      */       case 12:
/* 1078 */         return ((double[])this.data)[index];
/*      */       case 10:
/* 1080 */         ivalue = getAsSRational(index);
/* 1081 */         return ivalue[0] / ivalue[1];
/*      */       case 5:
/* 1083 */         lvalue = getAsRational(index);
/* 1084 */         return lvalue[0] / lvalue[1];
/*      */       case 2:
/* 1086 */         s = ((String[])this.data)[index];
/* 1087 */         return Double.parseDouble(s); }
/*      */     
/* 1089 */     throw new ClassCastException();
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
/*      */   public String getAsString(int index) {
/* 1101 */     return ((String[])this.data)[index];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int[] getAsSRational(int index) {
/* 1112 */     return ((int[][])this.data)[index];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long[] getAsRational(int index) {
/* 1123 */     return ((long[][])this.data)[index];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getValueAsString(int index) {
/*      */     int[] ivalue;
/*      */     String srationalString;
/*      */     long[] lvalue;
/*      */     String rationalString;
/* 1138 */     switch (this.type) {
/*      */       case 2:
/* 1140 */         return ((String[])this.data)[index];
/*      */       case 1: case 7:
/* 1142 */         return Integer.toString(((byte[])this.data)[index] & 0xFF);
/*      */       case 6:
/* 1144 */         return Integer.toString(((byte[])this.data)[index]);
/*      */       case 3:
/* 1146 */         return Integer.toString(((char[])this.data)[index] & Character.MAX_VALUE);
/*      */       case 8:
/* 1148 */         return Integer.toString(((short[])this.data)[index]);
/*      */       case 9:
/* 1150 */         return Integer.toString(((int[])this.data)[index]);
/*      */       case 4: case 13:
/* 1152 */         return Long.toString(((long[])this.data)[index]);
/*      */       case 11:
/* 1154 */         return Float.toString(((float[])this.data)[index]);
/*      */       case 12:
/* 1156 */         return Double.toString(((double[])this.data)[index]);
/*      */       case 10:
/* 1158 */         ivalue = getAsSRational(index);
/*      */         
/* 1160 */         if (ivalue[1] != 0 && ivalue[0] % ivalue[1] == 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1165 */           srationalString = Integer.toString(ivalue[0] / ivalue[1]) + "/1";
/*      */         
/*      */         }
/*      */         else {
/*      */ 
/*      */           
/* 1171 */           srationalString = Integer.toString(ivalue[0]) + "/" + Integer.toString(ivalue[1]);
/*      */         } 
/* 1173 */         return srationalString;
/*      */       case 5:
/* 1175 */         lvalue = getAsRational(index);
/*      */         
/* 1177 */         if (lvalue[1] != 0L && lvalue[0] % lvalue[1] == 0L) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1182 */           rationalString = Long.toString(lvalue[0] / lvalue[1]) + "/1";
/*      */         
/*      */         }
/*      */         else {
/*      */ 
/*      */           
/* 1188 */           rationalString = Long.toString(lvalue[0]) + "/" + Long.toString(lvalue[1]);
/*      */         } 
/* 1190 */         return rationalString;
/*      */     } 
/* 1192 */     throw new ClassCastException();
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
/*      */   public int compareTo(Object o) {
/* 1208 */     if (o == null) {
/* 1209 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/* 1212 */     int oTagNumber = ((TIFFField)o).getTagNumber();
/* 1213 */     if (this.tagNumber < oTagNumber)
/* 1214 */       return -1; 
/* 1215 */     if (this.tagNumber > oTagNumber) {
/* 1216 */       return 1;
/*      */     }
/* 1218 */     return 0;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\TIFFField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */