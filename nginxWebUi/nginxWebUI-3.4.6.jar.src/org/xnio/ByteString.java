/*      */ package org.xnio;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.util.Arrays;
/*      */ import org.xnio._private.Messages;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class ByteString
/*      */   implements Comparable<ByteString>, Serializable, CharSequence
/*      */ {
/*      */   private static final long serialVersionUID = -5998895518404718196L;
/*      */   private final byte[] bytes;
/*      */   private final int offs;
/*      */   private final int len;
/*      */   private transient int hashCode;
/*      */   private transient int hashCodeIgnoreCase;
/*      */   
/*      */   private ByteString(byte[] bytes, int offs, int len) {
/*   55 */     this.bytes = bytes;
/*   56 */     this.offs = offs;
/*   57 */     this.len = len;
/*   58 */     if (offs < 0) {
/*   59 */       throw Messages.msg.parameterOutOfRange("offs");
/*      */     }
/*   61 */     if (len < 0) {
/*   62 */       throw Messages.msg.parameterOutOfRange("len");
/*      */     }
/*   64 */     if (offs + len > bytes.length) {
/*   65 */       throw Messages.msg.parameterOutOfRange("offs");
/*      */     }
/*      */   }
/*      */   
/*      */   private static int calcHashCode(byte[] bytes, int offs, int len) {
/*   70 */     int hc = 31;
/*   71 */     int end = offs + len;
/*   72 */     for (int i = offs; i < end; i++) {
/*   73 */       hc = (hc << 5) - hc + (bytes[i] & 0xFF);
/*      */     }
/*   75 */     return (hc == 0) ? Integer.MAX_VALUE : hc;
/*      */   }
/*      */   
/*      */   private static int calcHashCodeIgnoreCase(byte[] bytes, int offs, int len) {
/*   79 */     int hc = 31;
/*   80 */     int end = offs + len;
/*   81 */     for (int i = offs; i < end; i++) {
/*   82 */       hc = (hc << 5) - hc + (upperCase(bytes[i]) & 0xFF);
/*      */     }
/*   84 */     return (hc == 0) ? Integer.MAX_VALUE : hc;
/*      */   }
/*      */   
/*      */   private ByteString(byte[] bytes) {
/*   88 */     this(bytes, 0, bytes.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteString of(byte... bytes) {
/*   98 */     return new ByteString((byte[])bytes.clone());
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
/*      */   public static ByteString copyOf(byte[] b, int offs, int len) {
/*  110 */     return new ByteString(Arrays.copyOfRange(b, offs, offs + len));
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
/*      */   public static ByteString getBytes(String str, String charset) throws UnsupportedEncodingException {
/*  122 */     return new ByteString(str.getBytes(charset));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteString getBytes(String str, Charset charset) {
/*  133 */     return new ByteString(str.getBytes(charset));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteString getBytes(String str) {
/*  143 */     int length = str.length();
/*  144 */     return new ByteString(getStringBytes(false, new byte[length], 0, str, 0, length), 0, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteString getBytes(ByteBuffer buffer) {
/*  154 */     return new ByteString(Buffers.take(buffer));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteString getBytes(ByteBuffer buffer, int length) {
/*  165 */     return new ByteString(Buffers.take(buffer, length));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getBytes() {
/*  174 */     return Arrays.copyOfRange(this.bytes, this.offs, this.len);
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
/*      */   public void getBytes(byte[] dest) {
/*  186 */     copyTo(dest);
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
/*      */   public void getBytes(byte[] dest, int offs) {
/*  199 */     copyTo(dest, offs);
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
/*      */   public void getBytes(byte[] dest, int offs, int len) {
/*  213 */     copyTo(dest, offs, len);
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
/*      */   public void copyTo(int srcOffs, byte[] dst, int offs, int len) {
/*  225 */     System.arraycopy(this.bytes, srcOffs + this.offs, dst, offs, Math.min(this.len, len));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void copyTo(byte[] dst, int offs, int len) {
/*  236 */     copyTo(0, dst, offs, len);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void copyTo(byte[] dst, int offs) {
/*  246 */     copyTo(dst, offs, dst.length - offs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void copyTo(byte[] dst) {
/*  255 */     copyTo(dst, 0, dst.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void appendTo(ByteBuffer dest) {
/*  264 */     dest.put(this.bytes, this.offs, this.len);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int tryAppendTo(int offs, ByteBuffer buffer) {
/*  275 */     byte[] b = this.bytes;
/*  276 */     int len = Math.min(buffer.remaining(), b.length - offs);
/*  277 */     buffer.put(b, offs + this.offs, len);
/*  278 */     return len;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeTo(OutputStream output) throws IOException {
/*  289 */     output.write(this.bytes, this.offs, this.len);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int compareTo(ByteString other) {
/*  300 */     if (other == this) return 0; 
/*  301 */     int length = this.len;
/*  302 */     int otherLength = other.len;
/*  303 */     int len1 = Math.min(length, otherLength);
/*  304 */     byte[] bytes = this.bytes;
/*  305 */     byte[] otherBytes = other.bytes;
/*  306 */     int offs = this.offs;
/*  307 */     int otherOffs = other.offs;
/*      */     
/*  309 */     for (int i = 0; i < len1; i++) {
/*  310 */       int res = Integer.signum(bytes[i + offs] - otherBytes[i + otherOffs]);
/*  311 */       if (res != 0) return res;
/*      */     
/*      */     } 
/*  314 */     return Integer.signum(length - otherLength);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int compareToIgnoreCase(ByteString other) {
/*  324 */     if (other == this) return 0; 
/*  325 */     if (other == this) return 0; 
/*  326 */     int length = this.len;
/*  327 */     int otherLength = other.len;
/*  328 */     int len1 = Math.min(length, otherLength);
/*  329 */     byte[] bytes = this.bytes;
/*  330 */     byte[] otherBytes = other.bytes;
/*  331 */     int offs = this.offs;
/*  332 */     int otherOffs = other.offs;
/*      */     
/*  334 */     for (int i = 0; i < len1; i++) {
/*  335 */       int res = Integer.signum(upperCase(bytes[i + offs]) - upperCase(otherBytes[i + otherOffs]));
/*  336 */       if (res != 0) return res;
/*      */     
/*      */     } 
/*  339 */     return Integer.signum(length - otherLength);
/*      */   }
/*      */   
/*      */   private static int upperCase(byte b) {
/*  343 */     return (b >= 97 && b <= 122) ? (b & 0xDF) : b;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString(String charset) throws UnsupportedEncodingException {
/*  354 */     if ("ISO-8859-1".equalsIgnoreCase(charset) || "Latin-1".equalsIgnoreCase(charset) || "ISO-Latin-1".equals(charset)) return toString(); 
/*  355 */     return new String(this.bytes, this.offs, this.len, charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int length() {
/*  364 */     return this.len;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/*  374 */     return new String(this.bytes, 0, this.offs, this.len);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toUtf8String() {
/*  383 */     return new String(this.bytes, this.offs, this.len, StandardCharsets.UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte byteAt(int idx) {
/*  392 */     if (idx < 0 || idx > this.len) {
/*  393 */       throw new ArrayIndexOutOfBoundsException();
/*      */     }
/*  395 */     return this.bytes[idx + this.offs];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteString substring(int offs) {
/*  405 */     return substring(offs, this.len - offs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteString substring(int offs, int len) {
/*  416 */     if (this.len - offs < len) {
/*  417 */       throw new IndexOutOfBoundsException();
/*      */     }
/*  419 */     return new ByteString(this.bytes, this.offs + offs, len);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  428 */     int hashCode = this.hashCode;
/*  429 */     if (hashCode == 0) {
/*  430 */       this.hashCode = hashCode = calcHashCode(this.bytes, this.offs, this.len);
/*      */     }
/*  432 */     return hashCode;
/*      */   }
/*      */   
/*      */   public int hashCodeIgnoreCase() {
/*  436 */     int hashCode = this.hashCodeIgnoreCase;
/*  437 */     if (hashCode == 0) {
/*  438 */       this.hashCodeIgnoreCase = hashCode = calcHashCodeIgnoreCase(this.bytes, this.offs, this.len);
/*      */     }
/*  440 */     return hashCode;
/*      */   }
/*      */   
/*      */   private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
/*  444 */     ois.defaultReadObject();
/*      */   }
/*      */   
/*      */   private static boolean equals(byte[] a, int aoff, byte[] b, int boff, int len) {
/*  448 */     for (int i = 0; i < len; i++) {
/*  449 */       if (a[i + aoff] != b[i + boff]) return false; 
/*      */     } 
/*  451 */     return true;
/*      */   }
/*      */   
/*      */   private static boolean equalsIgnoreCase(byte[] a, int aoff, byte[] b, int boff, int len) {
/*  455 */     for (int i = 0; i < len; i++) {
/*  456 */       if (upperCase(a[i + aoff]) != upperCase(b[i + boff])) return false; 
/*      */     } 
/*  458 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(Object obj) {
/*  468 */     return (obj instanceof ByteString && equals((ByteString)obj));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(ByteString other) {
/*  478 */     int len = this.len;
/*  479 */     return (this == other || (other != null && len == other.len && equals(this.bytes, this.offs, other.bytes, other.offs, len)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equalsIgnoreCase(ByteString other) {
/*  489 */     int len = this.len;
/*  490 */     return (this == other || (other != null && len == other.len && equalsIgnoreCase(this.bytes, this.offs, other.bytes, other.offs, len)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int toInt(int start) {
/*  501 */     int len = this.len;
/*  502 */     if (start >= len) {
/*  503 */       return 0;
/*      */     }
/*  505 */     byte[] bytes = this.bytes;
/*  506 */     int v = 0;
/*      */     
/*  508 */     for (int i = start + this.offs; i < len; i++) {
/*  509 */       byte b = bytes[i];
/*  510 */       if (b < 48 || b > 57) {
/*  511 */         return v;
/*      */       }
/*  513 */       v = (v << 3) + (v << 1) + b - 48;
/*      */     } 
/*  515 */     return v;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int toInt() {
/*  525 */     return toInt(0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long toLong(int start) {
/*  536 */     int len = this.len;
/*  537 */     if (start >= len) {
/*  538 */       return 0L;
/*      */     }
/*  540 */     byte[] bytes = this.bytes;
/*  541 */     long v = 0L;
/*      */     
/*  543 */     for (int i = start; i < len; i++) {
/*  544 */       byte b = bytes[i];
/*  545 */       if (b < 48 || b > 57) {
/*  546 */         return v;
/*      */       }
/*  548 */       v = (v << 3L) + (v << 1L) + (b - 48);
/*      */     } 
/*  550 */     return v;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long toLong() {
/*  560 */     return toLong(0);
/*      */   }
/*      */   
/*      */   private static int decimalCount(int val) {
/*  564 */     assert val >= 0;
/*      */     
/*  566 */     if (val < 10) return 1; 
/*  567 */     if (val < 100) return 2; 
/*  568 */     if (val < 1000) return 3; 
/*  569 */     if (val < 10000) return 4; 
/*  570 */     if (val < 100000) return 5; 
/*  571 */     if (val < 1000000) return 6; 
/*  572 */     if (val < 10000000) return 7; 
/*  573 */     if (val < 100000000) return 8; 
/*  574 */     if (val < 1000000000) return 9; 
/*  575 */     return 10;
/*      */   }
/*      */   
/*      */   private static int decimalCount(long val) {
/*  579 */     assert val >= 0L;
/*      */     
/*  581 */     if (val < 10L) return 1; 
/*  582 */     if (val < 100L) return 2; 
/*  583 */     if (val < 1000L) return 3; 
/*  584 */     if (val < 10000L) return 4; 
/*  585 */     if (val < 100000L) return 5; 
/*  586 */     if (val < 1000000L) return 6; 
/*  587 */     if (val < 10000000L) return 7; 
/*  588 */     if (val < 100000000L) return 8; 
/*  589 */     if (val < 1000000000L) return 9; 
/*  590 */     if (val < 10000000000L) return 10; 
/*  591 */     if (val < 100000000000L) return 11; 
/*  592 */     if (val < 1000000000000L) return 12; 
/*  593 */     if (val < 10000000000000L) return 13; 
/*  594 */     if (val < 100000000000000L) return 14; 
/*  595 */     if (val < 1000000000000000L) return 15; 
/*  596 */     if (val < 10000000000000000L) return 16; 
/*  597 */     if (val < 100000000000000000L) return 17; 
/*  598 */     if (val < 1000000000000000000L) return 18; 
/*  599 */     return 19;
/*      */   }
/*      */   
/*  602 */   private static final ByteString ZERO = new ByteString(new byte[] { 48 });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteString fromLong(long val) {
/*      */     byte[] b;
/*  611 */     if (val == 0L) return ZERO;
/*      */     
/*  613 */     int i = decimalCount(Math.abs(val));
/*      */     
/*  615 */     if (val < 0L) {
/*  616 */       b = new byte[++i];
/*  617 */       b[0] = 45;
/*      */     } else {
/*  619 */       b = new byte[i];
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/*  625 */       long quo = val / 10L;
/*  626 */       int mod = (int)(val - (quo << 3L) + (quo << 1L));
/*  627 */       b[--i] = (byte)(mod + 48);
/*  628 */       val = quo;
/*  629 */       if (i <= 0) {
/*  630 */         return new ByteString(b);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteString fromInt(int val) {
/*      */     byte[] b;
/*  640 */     if (val == 0) return ZERO;
/*      */     
/*  642 */     int i = decimalCount(Math.abs(val));
/*      */     
/*  644 */     if (val < 0) {
/*  645 */       b = new byte[++i];
/*  646 */       b[0] = 45;
/*      */     } else {
/*  648 */       b = new byte[i];
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/*  654 */       int quo = val / 10;
/*  655 */       int mod = val - (quo << 3) + (quo << 1);
/*  656 */       b[--i] = (byte)(mod + 48);
/*  657 */       val = quo;
/*  658 */       if (i <= 0) {
/*  659 */         return new ByteString(b);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equalToString(String str) {
/*  669 */     if (str == null) return false; 
/*  670 */     byte[] bytes = this.bytes;
/*  671 */     int length = bytes.length;
/*  672 */     if (str.length() != length) {
/*  673 */       return false;
/*      */     }
/*      */     
/*  676 */     int end = this.offs + this.len;
/*  677 */     for (int i = this.offs; i < end; i++) {
/*  678 */       char ch = str.charAt(i);
/*  679 */       if (ch > 'ÿ' || bytes[i] != (byte)str.charAt(i)) {
/*  680 */         return false;
/*      */       }
/*      */     } 
/*  683 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equalToStringIgnoreCase(String str) {
/*  693 */     if (str == null) return false; 
/*  694 */     byte[] bytes = this.bytes;
/*  695 */     int length = bytes.length;
/*  696 */     if (str.length() != length) {
/*  697 */       return false;
/*      */     }
/*      */     
/*  700 */     int end = this.offs + this.len;
/*  701 */     for (int i = this.offs; i < end; i++) {
/*  702 */       char ch = str.charAt(i);
/*  703 */       if (ch > 'ÿ' || upperCase(bytes[i]) != upperCase((byte)ch)) {
/*  704 */         return false;
/*      */       }
/*      */     } 
/*  707 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int indexOf(char c) {
/*  717 */     return indexOf(c, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int indexOf(char c, int start) {
/*  727 */     if (c > 'ÿ') {
/*  728 */       return -1;
/*      */     }
/*  730 */     int len = this.len;
/*  731 */     if (start > len) {
/*  732 */       return -1;
/*      */     }
/*  734 */     start = Math.max(0, start) + this.offs;
/*  735 */     byte[] bytes = this.bytes;
/*  736 */     byte bc = (byte)c;
/*  737 */     int end = start + len;
/*  738 */     for (int i = start; i < end; i++) {
/*  739 */       if (bytes[i] == bc) {
/*  740 */         return i;
/*      */       }
/*      */     } 
/*  743 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int lastIndexOf(char c) {
/*  753 */     return lastIndexOf(c, length() - 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int lastIndexOf(char c, int start) {
/*  763 */     if (c > 'ÿ') {
/*  764 */       return -1;
/*      */     }
/*  766 */     byte[] bytes = this.bytes;
/*  767 */     int offs = this.offs;
/*  768 */     start = Math.min(start, this.len - 1) + offs;
/*  769 */     byte bc = (byte)c;
/*  770 */     for (int i = start; i >= offs; i--) {
/*  771 */       if (bytes[i] == bc) {
/*  772 */         return i;
/*      */       }
/*      */     } 
/*  775 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static int arrayIndexOf(byte[] a, int aOffs, byte[] b, int bOffs, int bLen) {
/*  781 */     int aLen = a.length - aOffs;
/*  782 */     if (bLen > aLen || aLen < 0) {
/*  783 */       return -1;
/*      */     }
/*  785 */     aOffs = Math.max(0, aOffs);
/*  786 */     if (bLen == 0) {
/*  787 */       return aOffs;
/*      */     }
/*  789 */     byte startByte = b[bOffs];
/*  790 */     int limit = aLen - bLen;
/*  791 */     for (int i = aOffs; i < limit; i++) {
/*  792 */       if (a[i] == startByte) {
/*  793 */         int j = 1; while (true) { if (j < bLen) {
/*  794 */             if (a[i + j] != b[j + bOffs])
/*      */               break;  j++;
/*      */             continue;
/*      */           } 
/*  798 */           return i; }
/*      */       
/*      */       } 
/*  801 */     }  return -1;
/*      */   }
/*      */   
/*      */   private static int arrayIndexOf(byte[] a, int aOffs, String string) {
/*  805 */     int aLen = a.length - aOffs;
/*  806 */     int bLen = string.length();
/*  807 */     if (bLen > aLen || aLen < 0) {
/*  808 */       return -1;
/*      */     }
/*  810 */     aOffs = Math.max(0, aOffs);
/*  811 */     if (bLen == 0) {
/*  812 */       return aOffs;
/*      */     }
/*  814 */     char startChar = string.charAt(0);
/*  815 */     if (startChar > 'ÿ') {
/*  816 */       return -1;
/*      */     }
/*      */     
/*  819 */     int limit = aLen - bLen;
/*  820 */     for (int i = aOffs; i < limit; i++) {
/*  821 */       if (a[i] == startChar) {
/*  822 */         int j = 1; while (true) { if (j < bLen) {
/*  823 */             char ch = string.charAt(j);
/*  824 */             if (ch > 'ÿ') {
/*  825 */               return -1;
/*      */             }
/*  827 */             if (a[i + j] != ch)
/*      */               break;  j++;
/*      */             continue;
/*      */           } 
/*  831 */           return i; }
/*      */       
/*      */       } 
/*  834 */     }  return -1;
/*      */   }
/*      */   
/*      */   private static int arrayIndexOfIgnoreCase(byte[] a, int aOffs, byte[] b, int bOffs, int bLen) {
/*  838 */     int aLen = a.length - aOffs;
/*  839 */     if (bLen > aLen || aLen < 0) {
/*  840 */       return -1;
/*      */     }
/*  842 */     aOffs = Math.max(0, aOffs);
/*  843 */     if (bLen == 0) {
/*  844 */       return aOffs;
/*      */     }
/*  846 */     int startChar = upperCase(b[bOffs]);
/*  847 */     int limit = aLen - bLen;
/*  848 */     for (int i = aOffs; i < limit; i++) {
/*  849 */       if (upperCase(a[i]) == startChar) {
/*  850 */         int j = 1; while (true) { if (j < bLen) {
/*  851 */             if (upperCase(a[i + j]) != upperCase(b[j + bOffs]))
/*      */               break;  j++;
/*      */             continue;
/*      */           } 
/*  855 */           return i; }
/*      */       
/*      */       } 
/*  858 */     }  return -1;
/*      */   }
/*      */   
/*      */   private static int arrayIndexOfIgnoreCase(byte[] a, int aOffs, String string) {
/*  862 */     int aLen = a.length - aOffs;
/*  863 */     int bLen = string.length();
/*  864 */     if (bLen > aLen || aLen < 0) {
/*  865 */       return -1;
/*      */     }
/*  867 */     aOffs = Math.max(0, aOffs);
/*  868 */     if (bLen == 0) {
/*  869 */       return aOffs;
/*      */     }
/*  871 */     char startChar = string.charAt(0);
/*  872 */     if (startChar > 'ÿ') {
/*  873 */       return -1;
/*      */     }
/*  875 */     int startCP = upperCase((byte)startChar);
/*  876 */     int limit = aLen - bLen;
/*      */     
/*  878 */     for (int i = aOffs; i < limit; i++) {
/*  879 */       if (upperCase(a[i]) == startCP) {
/*  880 */         int j = 1; while (true) { if (j < bLen) {
/*  881 */             char ch = string.charAt(j);
/*  882 */             if (ch > 'ÿ') {
/*  883 */               return -1;
/*      */             }
/*      */             
/*  886 */             if (upperCase(a[i + j]) != upperCase((byte)ch))
/*      */               break;  j++;
/*      */             continue;
/*      */           } 
/*  890 */           return i; }
/*      */       
/*      */       } 
/*  893 */     }  return -1;
/*      */   }
/*      */   
/*      */   private static int arrayLastIndexOf(byte[] a, int aOffs, byte[] b, int bOffs, int bLen) {
/*  897 */     int aLen = a.length - aOffs;
/*  898 */     if (bLen > aLen || aLen < 0 || aOffs < 0) {
/*  899 */       return -1;
/*      */     }
/*      */     
/*  902 */     aOffs = Math.min(aLen - bLen, aOffs);
/*  903 */     if (bLen == 0) {
/*  904 */       return aOffs;
/*      */     }
/*  906 */     byte startByte = b[0];
/*  907 */     for (int i = aOffs - 1; i >= 0; i--) {
/*  908 */       if (a[i] == startByte) {
/*  909 */         int j = 1; if (j < bLen && 
/*  910 */           a[i + j] == b[bOffs + j])
/*      */         {
/*      */           
/*  913 */           return i;
/*      */         }
/*      */       } 
/*      */     } 
/*  917 */     return -1;
/*      */   }
/*      */   
/*      */   private static int arrayLastIndexOf(byte[] a, int aOffs, String string) {
/*  921 */     int aLen = a.length - aOffs;
/*  922 */     int bLen = string.length();
/*  923 */     if (bLen > aLen || aLen < 0 || aOffs < 0) {
/*  924 */       return -1;
/*      */     }
/*      */     
/*  927 */     aOffs = Math.min(aLen - bLen, aOffs);
/*  928 */     if (bLen == 0) {
/*  929 */       return aOffs;
/*      */     }
/*  931 */     char startChar = string.charAt(0);
/*  932 */     if (startChar > 'ÿ') {
/*  933 */       return -1;
/*      */     }
/*  935 */     byte startByte = (byte)startChar;
/*      */     
/*  937 */     for (int i = aOffs - 1; i >= 0; i--) {
/*  938 */       if (a[i] == startByte) {
/*  939 */         int j = 1; if (j < bLen) {
/*  940 */           char ch = string.charAt(j);
/*  941 */           if (ch > 'ÿ') {
/*  942 */             return -1;
/*      */           }
/*  944 */           if (a[i + j] == (byte)ch)
/*      */           {
/*      */             
/*  947 */             return i; } 
/*      */         } 
/*      */       } 
/*      */     } 
/*  951 */     return -1;
/*      */   }
/*      */   
/*      */   private static int arrayLastIndexOfIgnoreCase(byte[] a, int aOffs, byte[] b, int bOffs, int bLen) {
/*  955 */     int aLen = a.length - aOffs;
/*  956 */     if (bLen > aLen || aLen < 0 || aOffs < 0) {
/*  957 */       return -1;
/*      */     }
/*      */     
/*  960 */     aOffs = Math.min(aLen - bLen, aOffs);
/*  961 */     if (bLen == 0) {
/*  962 */       return aOffs;
/*      */     }
/*  964 */     int startCP = upperCase(b[bOffs]);
/*  965 */     for (int i = aOffs - 1; i >= 0; i--) {
/*  966 */       if (upperCase(a[i]) == startCP) {
/*  967 */         int j = 1; if (j < bLen && 
/*  968 */           upperCase(a[i + j]) == upperCase(b[j + bOffs]))
/*      */         {
/*      */           
/*  971 */           return i;
/*      */         }
/*      */       } 
/*      */     } 
/*  975 */     return -1;
/*      */   }
/*      */   
/*      */   private static int arrayLastIndexOfIgnoreCase(byte[] a, int aOffs, String string) {
/*  979 */     int aLen = a.length - aOffs;
/*  980 */     int bLen = string.length();
/*  981 */     if (bLen > aLen || aLen < 0 || aOffs < 0) {
/*  982 */       return -1;
/*      */     }
/*      */     
/*  985 */     aOffs = Math.min(aLen - bLen, aOffs);
/*  986 */     if (bLen == 0) {
/*  987 */       return aOffs;
/*      */     }
/*  989 */     char startChar = string.charAt(0);
/*  990 */     if (startChar > 'ÿ') {
/*  991 */       return -1;
/*      */     }
/*  993 */     int startCP = upperCase((byte)startChar);
/*      */     
/*  995 */     for (int i = aOffs - 1; i >= 0; i--) {
/*  996 */       if (upperCase(a[i]) == startCP) {
/*  997 */         int j = 1; if (j < bLen) {
/*  998 */           char ch = string.charAt(j);
/*  999 */           if (ch > 'ÿ') {
/* 1000 */             return -1;
/*      */           }
/*      */           
/* 1003 */           if (upperCase(a[i + j]) == upperCase((byte)ch))
/*      */           {
/*      */             
/* 1006 */             return i; } 
/*      */         } 
/*      */       } 
/*      */     } 
/* 1010 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean contains(ByteString other) {
/* 1020 */     if (other == this) return true; 
/* 1021 */     if (other == null) return false; 
/* 1022 */     byte[] otherBytes = other.bytes;
/* 1023 */     return (arrayIndexOf(this.bytes, this.offs, otherBytes, other.offs, other.len) != -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean contains(String other) {
/* 1033 */     return (other != null && toString().contains(other));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean containsIgnoreCase(ByteString other) {
/* 1043 */     return (other == this || (other != null && arrayIndexOfIgnoreCase(this.bytes, this.offs, other.bytes, other.offs, other.len) != -1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean containsIgnoreCase(String other) {
/* 1053 */     return (arrayIndexOfIgnoreCase(this.bytes, this.offs, other) != -1);
/*      */   }
/*      */   
/*      */   public int indexOf(ByteString other) {
/* 1057 */     return arrayIndexOf(this.bytes, this.offs, other.bytes, other.offs, other.len);
/*      */   }
/*      */   
/*      */   public int indexOf(ByteString other, int start) {
/* 1061 */     if (start > this.len) return -1; 
/* 1062 */     if (start < 0) start = 0; 
/* 1063 */     return arrayIndexOf(this.bytes, this.offs + start, other.bytes, other.offs, other.len);
/*      */   }
/*      */   
/*      */   public int indexOf(String other) {
/* 1067 */     return arrayIndexOf(this.bytes, this.offs, other);
/*      */   }
/*      */   
/*      */   public int indexOf(String other, int start) {
/* 1071 */     if (start > this.len) return -1; 
/* 1072 */     if (start < 0) start = 0; 
/* 1073 */     return arrayIndexOf(this.bytes, this.offs + start, other);
/*      */   }
/*      */   
/*      */   public int indexOfIgnoreCase(ByteString other) {
/* 1077 */     return arrayIndexOfIgnoreCase(this.bytes, this.offs, other.bytes, other.offs, other.len);
/*      */   }
/*      */   
/*      */   public int indexOfIgnoreCase(ByteString other, int start) {
/* 1081 */     if (start > this.len) return -1; 
/* 1082 */     if (start < 0) start = 0; 
/* 1083 */     return arrayIndexOfIgnoreCase(this.bytes, this.offs + start, other.bytes, other.offs, other.len);
/*      */   }
/*      */   
/*      */   public int indexOfIgnoreCase(String other) {
/* 1087 */     return arrayIndexOfIgnoreCase(this.bytes, this.offs, other);
/*      */   }
/*      */   
/*      */   public int indexOfIgnoreCase(String other, int start) {
/* 1091 */     if (start > this.len) return -1; 
/* 1092 */     if (start < 0) start = 0; 
/* 1093 */     return arrayIndexOfIgnoreCase(this.bytes, this.offs + start, other);
/*      */   }
/*      */   
/*      */   public int lastIndexOf(ByteString other) {
/* 1097 */     return arrayLastIndexOf(this.bytes, this.offs, other.bytes, other.offs, other.len);
/*      */   }
/*      */   
/*      */   public int lastIndexOf(ByteString other, int start) {
/* 1101 */     if (start > this.len) return -1; 
/* 1102 */     if (start < 0) start = 0; 
/* 1103 */     return arrayLastIndexOf(this.bytes, this.offs + start, other.bytes, other.offs, other.len);
/*      */   }
/*      */   
/*      */   public int lastIndexOf(String other) {
/* 1107 */     return arrayLastIndexOf(this.bytes, this.offs, other);
/*      */   }
/*      */   
/*      */   public int lastIndexOf(String other, int start) {
/* 1111 */     return arrayLastIndexOf(this.bytes, this.offs + start, other);
/*      */   }
/*      */   
/*      */   public int lastIndexOfIgnoreCase(ByteString other) {
/* 1115 */     return arrayLastIndexOfIgnoreCase(this.bytes, this.offs, other.bytes, other.offs, other.len);
/*      */   }
/*      */   
/*      */   public int lastIndexOfIgnoreCase(ByteString other, int start) {
/* 1119 */     if (start > this.len) return -1; 
/* 1120 */     if (start < 0) start = 0; 
/* 1121 */     return arrayLastIndexOfIgnoreCase(this.bytes, this.offs + start, other.bytes, other.offs, other.len);
/*      */   }
/*      */   
/*      */   public int lastIndexOfIgnoreCase(String other) {
/* 1125 */     return arrayLastIndexOfIgnoreCase(this.bytes, this.offs, other);
/*      */   }
/*      */   
/*      */   public int lastIndexOfIgnoreCase(String other, int start) {
/* 1129 */     return arrayLastIndexOfIgnoreCase(this.bytes, this.offs + start, other);
/*      */   }
/*      */   
/*      */   public boolean regionMatches(boolean ignoreCase, int offset, byte[] other, int otherOffset, int len) {
/* 1133 */     if (offset < 0 || otherOffset < 0 || offset + len > this.len || otherOffset + len > other.length) {
/* 1134 */       return false;
/*      */     }
/* 1136 */     if (ignoreCase) {
/* 1137 */       return equalsIgnoreCase(this.bytes, offset + this.offs, other, otherOffset, len);
/*      */     }
/* 1139 */     return equals(this.bytes, offset + this.offs, other, otherOffset, len);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean regionMatches(boolean ignoreCase, int offset, ByteString other, int otherOffset, int len) {
/* 1144 */     if (offset < 0 || otherOffset < 0 || offset + len > this.len || otherOffset + len > other.len) {
/* 1145 */       return false;
/*      */     }
/* 1147 */     if (ignoreCase) {
/* 1148 */       return equalsIgnoreCase(this.bytes, offset + this.offs, other.bytes, otherOffset, len);
/*      */     }
/* 1150 */     return equals(this.bytes, offset + this.offs, other.bytes, otherOffset, len);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean regionMatches(boolean ignoreCase, int offset, String other, int otherOffset, int len) {
/* 1155 */     if (offset < 0 || otherOffset < 0 || offset + len > this.len || otherOffset + len > other.length()) {
/* 1156 */       return false;
/*      */     }
/* 1158 */     if (ignoreCase) {
/* 1159 */       return equalsIgnoreCase(this.bytes, offset + this.offs, other, otherOffset, len);
/*      */     }
/* 1161 */     return equals(this.bytes, offset + this.offs, other, otherOffset, len);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean equalsIgnoreCase(byte[] a, int aOffs, String string, int stringOffset, int length) {
/* 1167 */     for (int i = 0; i < length; i++) {
/* 1168 */       char ch = string.charAt(i + stringOffset);
/* 1169 */       if (ch > 'ÿ') {
/* 1170 */         return false;
/*      */       }
/* 1172 */       if (a[i + aOffs] != (byte)ch) {
/* 1173 */         return false;
/*      */       }
/*      */     } 
/* 1176 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   private static boolean equals(byte[] a, int aOffs, String string, int stringOffset, int length) {
/* 1181 */     for (int i = 0; i < length; i++) {
/* 1182 */       char ch = string.charAt(i + stringOffset);
/* 1183 */       if (ch > 'ÿ') {
/* 1184 */         return false;
/*      */       }
/* 1186 */       if (upperCase(a[i + aOffs]) != upperCase((byte)ch)) {
/* 1187 */         return false;
/*      */       }
/*      */     } 
/* 1190 */     return true;
/*      */   }
/*      */   
/*      */   public boolean startsWith(ByteString prefix) {
/* 1194 */     return regionMatches(false, 0, prefix, 0, prefix.length());
/*      */   }
/*      */   
/*      */   public boolean startsWith(String prefix) {
/* 1198 */     return regionMatches(false, 0, prefix, 0, prefix.length());
/*      */   }
/*      */   
/*      */   public boolean startsWith(char prefix) {
/* 1202 */     return (prefix <= 'ÿ' && this.len > 0 && this.bytes[this.offs] == (byte)prefix);
/*      */   }
/*      */   
/*      */   public boolean startsWithIgnoreCase(ByteString prefix) {
/* 1206 */     return regionMatches(true, 0, prefix, 0, prefix.length());
/*      */   }
/*      */   
/*      */   public boolean startsWithIgnoreCase(String prefix) {
/* 1210 */     return regionMatches(true, 0, prefix, 0, prefix.length());
/*      */   }
/*      */   
/*      */   public boolean startsWithIgnoreCase(char prefix) {
/* 1214 */     return (prefix <= 'ÿ' && this.len > 0 && upperCase(this.bytes[this.offs]) == upperCase((byte)prefix));
/*      */   }
/*      */   
/*      */   public boolean endsWith(ByteString suffix) {
/* 1218 */     int suffixLength = suffix.len;
/* 1219 */     return regionMatches(false, this.len - suffixLength, suffix, 0, suffixLength);
/*      */   }
/*      */   
/*      */   public boolean endsWith(String suffix) {
/* 1223 */     int suffixLength = suffix.length();
/* 1224 */     return regionMatches(false, this.len - suffixLength, suffix, 0, suffixLength);
/*      */   }
/*      */   
/*      */   public boolean endsWith(char suffix) {
/* 1228 */     int len = this.len;
/* 1229 */     return (suffix <= 'ÿ' && len > 0 && this.bytes[this.offs + len - 1] == (byte)suffix);
/*      */   }
/*      */   
/*      */   public boolean endsWithIgnoreCase(ByteString suffix) {
/* 1233 */     int suffixLength = suffix.length();
/* 1234 */     return regionMatches(true, this.len - suffixLength, suffix, 0, suffixLength);
/*      */   }
/*      */   
/*      */   public boolean endsWithIgnoreCase(String suffix) {
/* 1238 */     int suffixLength = suffix.length();
/* 1239 */     return regionMatches(true, this.len - suffixLength, suffix, 0, suffixLength);
/*      */   }
/*      */   
/*      */   public boolean endsWithIgnoreCase(char suffix) {
/* 1243 */     int len = this.len;
/* 1244 */     return (suffix <= 'ÿ' && len > 0 && upperCase(this.bytes[this.offs + len - 1]) == upperCase((byte)suffix));
/*      */   }
/*      */   
/*      */   public ByteString concat(byte[] suffixBytes) {
/* 1248 */     return concat(suffixBytes, 0, suffixBytes.length);
/*      */   }
/*      */   
/*      */   public ByteString concat(byte[] suffixBytes, int offs, int len) {
/* 1252 */     if (len <= 0) return this; 
/* 1253 */     int length = this.len;
/* 1254 */     byte[] newBytes = Arrays.copyOfRange(this.bytes, this.offs, length + len);
/* 1255 */     System.arraycopy(suffixBytes, offs, newBytes, length, len);
/* 1256 */     return new ByteString(newBytes);
/*      */   }
/*      */   
/*      */   public ByteString concat(ByteString suffix) {
/* 1260 */     return concat(suffix.bytes, suffix.offs, suffix.len);
/*      */   }
/*      */   
/*      */   public ByteString concat(ByteString suffix, int offs, int len) {
/* 1264 */     return concat(suffix.bytes, offs + suffix.offs, Math.min(len, suffix.len));
/*      */   }
/*      */   
/*      */   public ByteString concat(String suffix) {
/* 1268 */     return concat(suffix, 0, suffix.length());
/*      */   }
/*      */ 
/*      */   
/*      */   private static byte[] getStringBytes(boolean trust, byte[] dst, int dstOffs, String src, int srcOffs, int len) {
/* 1273 */     if (trust) {
/* 1274 */       src.getBytes(srcOffs, srcOffs + len, dst, dstOffs);
/*      */     } else {
/* 1276 */       for (int i = srcOffs; i < len; i++) {
/* 1277 */         char c = src.charAt(i);
/* 1278 */         if (c > 'ÿ') {
/* 1279 */           throw new IllegalArgumentException("Invalid string contents");
/*      */         }
/* 1281 */         dst[i + dstOffs] = (byte)c;
/*      */       } 
/*      */     } 
/* 1284 */     return dst;
/*      */   }
/*      */   
/*      */   public ByteString concat(String suffix, int offs, int len) {
/* 1288 */     if (len <= 0) return this; 
/* 1289 */     byte[] bytes = this.bytes;
/* 1290 */     int length = this.len;
/* 1291 */     byte[] newBytes = Arrays.copyOfRange(bytes, offs, offs + length + len);
/* 1292 */     getStringBytes(false, newBytes, length, suffix, offs, len);
/* 1293 */     return new ByteString(newBytes);
/*      */   }
/*      */   
/*      */   public static ByteString concat(String prefix, ByteString suffix) {
/* 1297 */     int prefixLength = prefix.length();
/* 1298 */     byte[] suffixBytes = suffix.bytes;
/* 1299 */     int suffixLength = suffixBytes.length;
/* 1300 */     byte[] newBytes = new byte[prefixLength + suffixLength];
/* 1301 */     getStringBytes(false, newBytes, 0, prefix, 0, prefixLength);
/* 1302 */     System.arraycopy(suffixBytes, suffix.offs, newBytes, prefixLength, suffixLength);
/* 1303 */     return new ByteString(newBytes);
/*      */   }
/*      */   
/*      */   public static ByteString concat(String prefix, String suffix) {
/* 1307 */     int prefixLength = prefix.length();
/* 1308 */     int suffixLength = suffix.length();
/* 1309 */     byte[] newBytes = new byte[prefixLength + suffixLength];
/* 1310 */     getStringBytes(false, newBytes, 0, prefix, 0, prefixLength);
/* 1311 */     getStringBytes(false, newBytes, prefixLength, suffix, 0, suffixLength);
/* 1312 */     return new ByteString(newBytes);
/*      */   }
/*      */   
/*      */   public char charAt(int index) {
/* 1316 */     if (index < 0 || index > this.len) {
/* 1317 */       throw new ArrayIndexOutOfBoundsException();
/*      */     }
/* 1319 */     return (char)(this.bytes[index + this.offs] & 0xFF);
/*      */   }
/*      */   
/*      */   public ByteString subSequence(int start, int end) {
/* 1323 */     return substring(start, end);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ByteString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */