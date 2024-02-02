/*      */ package io.undertow.util;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class FlexBase64
/*      */ {
/*      */   private static final byte[] STANDARD_ENCODING_TABLE;
/*   42 */   private static final byte[] STANDARD_DECODING_TABLE = new byte[80];
/*      */   private static final byte[] URL_ENCODING_TABLE;
/*   44 */   private static final byte[] URL_DECODING_TABLE = new byte[80];
/*      */   private static final Constructor<String> STRING_CONSTRUCTOR;
/*      */   
/*      */   static {
/*   48 */     STANDARD_ENCODING_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes(StandardCharsets.US_ASCII);
/*   49 */     URL_ENCODING_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".getBytes(StandardCharsets.US_ASCII);
/*      */     int i;
/*   51 */     for (i = 0; i < STANDARD_ENCODING_TABLE.length; i++) {
/*   52 */       int v = (STANDARD_ENCODING_TABLE[i] & 0xFF) - 43;
/*   53 */       STANDARD_DECODING_TABLE[v] = (byte)(i + 1);
/*      */     } 
/*      */     
/*   56 */     for (i = 0; i < URL_ENCODING_TABLE.length; i++) {
/*   57 */       int v = (URL_ENCODING_TABLE[i] & 0xFF) - 43;
/*   58 */       URL_DECODING_TABLE[v] = (byte)(i + 1);
/*      */     } 
/*      */ 
/*      */     
/*   62 */     Constructor<String> c = null;
/*      */     try {
/*   64 */       PrivilegedExceptionAction<Constructor<String>> runnable = new PrivilegedExceptionAction<Constructor<String>>()
/*      */         {
/*      */           public Constructor<String> run() throws Exception
/*      */           {
/*   68 */             Constructor<String> c = String.class.getDeclaredConstructor(new Class[] { char[].class, boolean.class });
/*   69 */             c.setAccessible(true);
/*   70 */             return c;
/*      */           }
/*      */         };
/*   73 */       if (System.getSecurityManager() != null) {
/*   74 */         c = AccessController.<Constructor<String>>doPrivileged(runnable);
/*      */       } else {
/*   76 */         c = runnable.run();
/*      */       } 
/*   78 */     } catch (Throwable throwable) {}
/*      */ 
/*      */     
/*   81 */     STRING_CONSTRUCTOR = c;
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
/*      */   public static Encoder createEncoder(boolean wrap) {
/*   94 */     return new Encoder(wrap, false);
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
/*      */   public static Encoder createURLEncoder(boolean wrap) {
/*  108 */     return new Encoder(wrap, true);
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
/*      */   public static Decoder createDecoder() {
/*  120 */     return new Decoder(false);
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
/*      */   public static Decoder createURLDecoder() {
/*  132 */     return new Decoder(true);
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
/*      */   public static String encodeString(byte[] source, boolean wrap) {
/*  149 */     return Encoder.encodeString(source, 0, source.length, wrap, false);
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
/*      */   public static String encodeStringURL(byte[] source, boolean wrap) {
/*  167 */     return Encoder.encodeString(source, 0, source.length, wrap, true);
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
/*      */   public static String encodeString(byte[] source, int pos, int limit, boolean wrap) {
/*  190 */     return Encoder.encodeString(source, pos, limit, wrap, false);
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
/*      */   public static String encodeStringURL(byte[] source, int pos, int limit, boolean wrap) {
/*  213 */     return Encoder.encodeString(source, pos, limit, wrap, true);
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
/*      */   public static String encodeString(ByteBuffer source, boolean wrap) {
/*  233 */     return Encoder.encodeString(source, wrap, false);
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
/*      */   public static String encodeStringURL(ByteBuffer source, boolean wrap) {
/*  254 */     return Encoder.encodeString(source, wrap, true);
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
/*      */   public static byte[] encodeBytes(byte[] source, int pos, int limit, boolean wrap) {
/*  272 */     return Encoder.encodeBytes(source, pos, limit, wrap, false);
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
/*      */   public static byte[] encodeBytesURL(byte[] source, int pos, int limit, boolean wrap) {
/*  290 */     return Encoder.encodeBytes(source, pos, limit, wrap, true);
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
/*      */   public static ByteBuffer decode(String source) throws IOException {
/*  305 */     return Decoder.decode(source, false);
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
/*      */   public static ByteBuffer decodeURL(String source) throws IOException {
/*  320 */     return Decoder.decode(source, true);
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
/*      */   public static ByteBuffer decode(ByteBuffer source) throws IOException {
/*  335 */     return Decoder.decode(source, false);
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
/*      */   public static ByteBuffer decodeURL(ByteBuffer source) throws IOException {
/*  351 */     return Decoder.decode(source, true);
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
/*      */   public static ByteBuffer decode(byte[] source, int off, int limit) throws IOException {
/*  369 */     return Decoder.decode(source, off, limit, false);
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
/*      */   public static ByteBuffer decodeURL(byte[] source, int off, int limit) throws IOException {
/*  386 */     return Decoder.decode(source, off, limit, true);
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
/*      */   public static EncoderInputStream createEncoderInputStream(InputStream source, int bufferSize, boolean wrap) {
/*  405 */     return new EncoderInputStream(source, bufferSize, wrap, false);
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
/*      */   public static EncoderInputStream createEncoderInputStream(InputStream source) {
/*  422 */     return new EncoderInputStream(source);
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
/*      */   public static DecoderInputStream createDecoderInputStream(InputStream source, int bufferSize) {
/*  443 */     return new DecoderInputStream(source, bufferSize);
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
/*      */   public static DecoderInputStream createDecoderInputStream(InputStream source) {
/*  464 */     return new DecoderInputStream(source);
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
/*      */   public static EncoderOutputStream createEncoderOutputStream(OutputStream target, int bufferSize, boolean wrap) {
/*  482 */     return new EncoderOutputStream(target, bufferSize, wrap);
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
/*      */   public static EncoderOutputStream createEncoderOutputStream(OutputStream output) {
/*  502 */     return new EncoderOutputStream(output);
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
/*      */   public static DecoderOutputStream createDecoderOutputStream(OutputStream output, int bufferSize) {
/*  520 */     return new DecoderOutputStream(output, bufferSize);
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
/*      */   public static DecoderOutputStream createDecoderOutputStream(OutputStream output) {
/*  536 */     return new DecoderOutputStream(output);
/*      */   }
/*      */ 
/*      */   
/*      */   public static final class Encoder
/*      */   {
/*      */     private int state;
/*      */     
/*      */     private int last;
/*      */     
/*      */     private int count;
/*      */     private final boolean wrap;
/*      */     private int lastPos;
/*      */     private final byte[] encodingTable;
/*      */     
/*      */     private Encoder(boolean wrap, boolean url) {
/*  552 */       this.wrap = wrap;
/*  553 */       this.encodingTable = url ? FlexBase64.URL_ENCODING_TABLE : FlexBase64.STANDARD_ENCODING_TABLE;
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
/*      */     public void encode(ByteBuffer source, ByteBuffer target) {
/*  567 */       if (target == null) {
/*  568 */         throw new IllegalStateException();
/*      */       }
/*  570 */       int last = this.last;
/*  571 */       int state = this.state;
/*  572 */       boolean wrap = this.wrap;
/*  573 */       int count = this.count;
/*  574 */       byte[] ENCODING_TABLE = this.encodingTable;
/*      */       
/*  576 */       int remaining = source.remaining();
/*  577 */       while (remaining > 0) {
/*      */         
/*  579 */         int require = 4 - state;
/*  580 */         require = (wrap && count >= 72) ? (require + 2) : require;
/*  581 */         if (target.remaining() < require) {
/*      */           break;
/*      */         }
/*      */         
/*  585 */         int b = source.get() & 0xFF;
/*  586 */         if (state == 0) {
/*  587 */           target.put(ENCODING_TABLE[b >>> 2]);
/*  588 */           last = (b & 0x3) << 4;
/*  589 */           state++;
/*  590 */           if (--remaining <= 0) {
/*      */             break;
/*      */           }
/*  593 */           b = source.get() & 0xFF;
/*      */         } 
/*  595 */         if (state == 1) {
/*  596 */           target.put(ENCODING_TABLE[last | b >>> 4]);
/*  597 */           last = (b & 0xF) << 2;
/*  598 */           state++;
/*  599 */           if (--remaining <= 0) {
/*      */             break;
/*      */           }
/*  602 */           b = source.get() & 0xFF;
/*      */         } 
/*  604 */         if (state == 2) {
/*  605 */           target.put(ENCODING_TABLE[last | b >>> 6]);
/*  606 */           target.put(ENCODING_TABLE[b & 0x3F]);
/*  607 */           last = state = 0;
/*  608 */           remaining--;
/*      */         } 
/*      */ 
/*      */         
/*  612 */         count += 4;
/*  613 */         if (wrap && count >= 76) {
/*  614 */           count = 0;
/*  615 */           target.putShort((short)3338);
/*      */         } 
/*      */       } 
/*      */       
/*  619 */       this.count = count;
/*  620 */       this.last = last;
/*  621 */       this.state = state;
/*  622 */       this.lastPos = source.position();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int encode(byte[] source, int pos, int limit, byte[] target, int opos, int olimit) {
/*  654 */       if (target == null) {
/*  655 */         throw new IllegalStateException();
/*      */       }
/*  657 */       int last = this.last;
/*  658 */       int state = this.state;
/*  659 */       int count = this.count;
/*  660 */       boolean wrap = this.wrap;
/*  661 */       byte[] ENCODING_TABLE = this.encodingTable;
/*      */ 
/*      */       
/*  664 */       while (limit > pos) {
/*      */         
/*  666 */         int require = 4 - state;
/*  667 */         require = (wrap && count >= 72) ? (require + 2) : require;
/*  668 */         if (require + opos > olimit) {
/*      */           break;
/*      */         }
/*      */         
/*  672 */         int b = source[pos++] & 0xFF;
/*  673 */         if (state == 0) {
/*  674 */           target[opos++] = ENCODING_TABLE[b >>> 2];
/*  675 */           last = (b & 0x3) << 4;
/*  676 */           state++;
/*  677 */           if (pos >= limit) {
/*      */             break;
/*      */           }
/*  680 */           b = source[pos++] & 0xFF;
/*      */         } 
/*  682 */         if (state == 1) {
/*  683 */           target[opos++] = ENCODING_TABLE[last | b >>> 4];
/*  684 */           last = (b & 0xF) << 2;
/*  685 */           state++;
/*  686 */           if (pos >= limit) {
/*      */             break;
/*      */           }
/*  689 */           b = source[pos++] & 0xFF;
/*      */         } 
/*  691 */         if (state == 2) {
/*  692 */           target[opos++] = ENCODING_TABLE[last | b >>> 6];
/*  693 */           target[opos++] = ENCODING_TABLE[b & 0x3F];
/*      */           
/*  695 */           last = state = 0;
/*      */         } 
/*      */ 
/*      */         
/*  699 */         count += 4;
/*  700 */         if (wrap && count >= 76) {
/*  701 */           count = 0;
/*  702 */           target[opos++] = 13;
/*  703 */           target[opos++] = 10;
/*      */         } 
/*      */       } 
/*      */       
/*  707 */       this.count = count;
/*  708 */       this.last = last;
/*  709 */       this.state = state;
/*  710 */       this.lastPos = pos;
/*      */       
/*  712 */       return opos;
/*      */     }
/*      */ 
/*      */     
/*      */     private static String encodeString(byte[] source, int pos, int limit, boolean wrap, boolean url) {
/*  717 */       int olimit = limit - pos;
/*  718 */       int remainder = olimit % 3;
/*  719 */       olimit = (olimit + ((remainder == 0) ? 0 : (3 - remainder))) / 3 * 4;
/*  720 */       olimit += wrap ? (olimit / 76 * 2 + 2) : 0;
/*  721 */       char[] target = new char[olimit];
/*  722 */       int opos = 0;
/*  723 */       int last = 0;
/*  724 */       int count = 0;
/*  725 */       int state = 0;
/*  726 */       byte[] ENCODING_TABLE = url ? FlexBase64.URL_ENCODING_TABLE : FlexBase64.STANDARD_ENCODING_TABLE;
/*      */       
/*  728 */       while (limit > pos) {
/*      */         
/*  730 */         int b = source[pos++] & 0xFF;
/*  731 */         target[opos++] = (char)ENCODING_TABLE[b >>> 2];
/*  732 */         last = (b & 0x3) << 4;
/*  733 */         if (pos >= limit) {
/*  734 */           state = 1;
/*      */           break;
/*      */         } 
/*  737 */         b = source[pos++] & 0xFF;
/*  738 */         target[opos++] = (char)ENCODING_TABLE[last | b >>> 4];
/*  739 */         last = (b & 0xF) << 2;
/*  740 */         if (pos >= limit) {
/*  741 */           state = 2;
/*      */           break;
/*      */         } 
/*  744 */         b = source[pos++] & 0xFF;
/*  745 */         target[opos++] = (char)ENCODING_TABLE[last | b >>> 6];
/*  746 */         target[opos++] = (char)ENCODING_TABLE[b & 0x3F];
/*      */ 
/*      */         
/*  749 */         count += 4;
/*  750 */         if (wrap && count >= 76) {
/*  751 */           count = 0;
/*  752 */           target[opos++] = '\r';
/*  753 */           target[opos++] = '\n';
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  758 */       complete(target, opos, state, last, wrap, url);
/*      */ 
/*      */       
/*      */       try {
/*  762 */         if (FlexBase64.STRING_CONSTRUCTOR != null) {
/*  763 */           return FlexBase64.STRING_CONSTRUCTOR.newInstance(new Object[] { target, Boolean.TRUE });
/*      */         }
/*  765 */       } catch (Exception exception) {}
/*      */ 
/*      */ 
/*      */       
/*  769 */       return new String(target);
/*      */     }
/*      */     
/*      */     private static byte[] encodeBytes(byte[] source, int pos, int limit, boolean wrap, boolean url) {
/*  773 */       int olimit = limit - pos;
/*  774 */       int remainder = olimit % 3;
/*  775 */       olimit = (olimit + ((remainder == 0) ? 0 : (3 - remainder))) / 3 * 4;
/*  776 */       olimit += wrap ? (olimit / 76 * 2 + 2) : 0;
/*  777 */       byte[] target = new byte[olimit];
/*  778 */       int opos = 0;
/*  779 */       int count = 0;
/*  780 */       int last = 0;
/*  781 */       int state = 0;
/*  782 */       byte[] ENCODING_TABLE = url ? FlexBase64.URL_ENCODING_TABLE : FlexBase64.STANDARD_ENCODING_TABLE;
/*      */       
/*  784 */       while (limit > pos) {
/*      */         
/*  786 */         int b = source[pos++] & 0xFF;
/*  787 */         target[opos++] = ENCODING_TABLE[b >>> 2];
/*  788 */         last = (b & 0x3) << 4;
/*  789 */         if (pos >= limit) {
/*  790 */           state = 1;
/*      */           break;
/*      */         } 
/*  793 */         b = source[pos++] & 0xFF;
/*  794 */         target[opos++] = ENCODING_TABLE[last | b >>> 4];
/*  795 */         last = (b & 0xF) << 2;
/*  796 */         if (pos >= limit) {
/*  797 */           state = 2;
/*      */           break;
/*      */         } 
/*  800 */         b = source[pos++] & 0xFF;
/*  801 */         target[opos++] = ENCODING_TABLE[last | b >>> 6];
/*  802 */         target[opos++] = ENCODING_TABLE[b & 0x3F];
/*      */ 
/*      */         
/*  805 */         count += 4;
/*  806 */         if (wrap && count >= 76) {
/*  807 */           count = 0;
/*  808 */           target[opos++] = 13;
/*  809 */           target[opos++] = 10;
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  814 */       complete(target, opos, state, last, wrap, url);
/*      */       
/*  816 */       return target;
/*      */     }
/*      */     
/*      */     private static String encodeString(ByteBuffer source, boolean wrap, boolean url) {
/*  820 */       int remaining = source.remaining();
/*  821 */       int remainder = remaining % 3;
/*  822 */       int olimit = (remaining + ((remainder == 0) ? 0 : (3 - remainder))) / 3 * 4;
/*  823 */       olimit += wrap ? (olimit / 76 * 2 + 2) : 0;
/*  824 */       char[] target = new char[olimit];
/*  825 */       int opos = 0;
/*  826 */       int last = 0;
/*  827 */       int state = 0;
/*  828 */       int count = 0;
/*  829 */       byte[] ENCODING_TABLE = url ? FlexBase64.URL_ENCODING_TABLE : FlexBase64.STANDARD_ENCODING_TABLE;
/*      */ 
/*      */       
/*  832 */       while (remaining > 0) {
/*      */         
/*  834 */         int b = source.get() & 0xFF;
/*  835 */         target[opos++] = (char)ENCODING_TABLE[b >>> 2];
/*  836 */         last = (b & 0x3) << 4;
/*  837 */         if (--remaining <= 0) {
/*  838 */           state = 1;
/*      */           break;
/*      */         } 
/*  841 */         b = source.get() & 0xFF;
/*  842 */         target[opos++] = (char)ENCODING_TABLE[last | b >>> 4];
/*  843 */         last = (b & 0xF) << 2;
/*  844 */         if (--remaining <= 0) {
/*  845 */           state = 2;
/*      */           break;
/*      */         } 
/*  848 */         b = source.get() & 0xFF;
/*  849 */         target[opos++] = (char)ENCODING_TABLE[last | b >>> 6];
/*  850 */         target[opos++] = (char)ENCODING_TABLE[b & 0x3F];
/*  851 */         remaining--;
/*      */ 
/*      */         
/*  854 */         count += 4;
/*  855 */         if (wrap && count >= 76) {
/*  856 */           count = 0;
/*  857 */           target[opos++] = '\r';
/*  858 */           target[opos++] = '\n';
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  863 */       complete(target, opos, state, last, wrap, url);
/*      */ 
/*      */       
/*      */       try {
/*  867 */         if (FlexBase64.STRING_CONSTRUCTOR != null) {
/*  868 */           return FlexBase64.STRING_CONSTRUCTOR.newInstance(new Object[] { target, Boolean.TRUE });
/*      */         }
/*  870 */       } catch (Exception exception) {}
/*      */ 
/*      */ 
/*      */       
/*  874 */       return new String(target);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getLastInputPosition() {
/*  885 */       return this.lastPos;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int complete(byte[] target, int pos) {
/*  910 */       if (this.state > 0) {
/*  911 */         target[pos++] = this.encodingTable[this.last];
/*  912 */         for (int i = this.state; i < 3; i++) {
/*  913 */           target[pos++] = 61;
/*      */         }
/*      */         
/*  916 */         this.last = this.state = 0;
/*      */       } 
/*  918 */       if (this.wrap) {
/*  919 */         target[pos++] = 13;
/*  920 */         target[pos++] = 10;
/*      */       } 
/*      */       
/*  923 */       return pos;
/*      */     }
/*      */     
/*      */     private static int complete(char[] target, int pos, int state, int last, boolean wrap, boolean url) {
/*  927 */       if (state > 0) {
/*  928 */         target[pos++] = (char)(url ? FlexBase64.URL_ENCODING_TABLE : FlexBase64.STANDARD_ENCODING_TABLE)[last];
/*  929 */         for (int i = state; i < 3; i++) {
/*  930 */           target[pos++] = '=';
/*      */         }
/*      */       } 
/*  933 */       if (wrap) {
/*  934 */         target[pos++] = '\r';
/*  935 */         target[pos++] = '\n';
/*      */       } 
/*      */       
/*  938 */       return pos;
/*      */     }
/*      */     
/*      */     private static int complete(byte[] target, int pos, int state, int last, boolean wrap, boolean url) {
/*  942 */       if (state > 0) {
/*  943 */         target[pos++] = (url ? FlexBase64.URL_ENCODING_TABLE : FlexBase64.STANDARD_ENCODING_TABLE)[last];
/*  944 */         for (int i = state; i < 3; i++) {
/*  945 */           target[pos++] = 61;
/*      */         }
/*      */       } 
/*  948 */       if (wrap) {
/*  949 */         target[pos++] = 13;
/*  950 */         target[pos++] = 10;
/*      */       } 
/*      */       
/*  953 */       return pos;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void complete(ByteBuffer target) {
/*  964 */       if (this.state > 0) {
/*  965 */         target.put(this.encodingTable[this.last]);
/*  966 */         for (int i = this.state; i < 3; i++) {
/*  967 */           target.put((byte)61);
/*      */         }
/*      */         
/*  970 */         this.last = this.state = 0;
/*      */       } 
/*  972 */       if (this.wrap) {
/*  973 */         target.putShort((short)3338);
/*      */       }
/*      */       
/*  976 */       this.count = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static final class Decoder
/*      */   {
/*      */     private int state;
/*      */     
/*      */     private int last;
/*      */     
/*      */     private int lastPos;
/*      */     
/*      */     private final byte[] decodingTable;
/*      */     
/*      */     private static final int SKIP = 64768;
/*      */     private static final int MARK = 65024;
/*      */     private static final int DONE = 65280;
/*      */     private static final int ERROR = 983040;
/*      */     
/*      */     private Decoder(boolean url) {
/*  997 */       this.decodingTable = url ? FlexBase64.URL_DECODING_TABLE : FlexBase64.STANDARD_DECODING_TABLE;
/*      */     }
/*      */ 
/*      */     
/*      */     private int nextByte(ByteBuffer buffer, int state, int last, boolean ignoreErrors) throws IOException {
/* 1002 */       return nextByte(buffer.get() & 0xFF, state, last, ignoreErrors);
/*      */     }
/*      */     
/*      */     private int nextByte(Object source, int pos, int state, int last, boolean ignoreErrors) throws IOException {
/*      */       int c;
/* 1007 */       if (source instanceof byte[]) {
/* 1008 */         c = ((byte[])source)[pos] & 0xFF;
/* 1009 */       } else if (source instanceof String) {
/* 1010 */         c = ((String)source).charAt(pos) & 0xFF;
/*      */       } else {
/* 1012 */         throw new IllegalArgumentException();
/*      */       } 
/*      */       
/* 1015 */       return nextByte(c, state, last, ignoreErrors);
/*      */     }
/*      */     
/*      */     private int nextByte(int c, int state, int last, boolean ignoreErrors) throws IOException {
/* 1019 */       if (last == 65024) {
/* 1020 */         if (c != 61) {
/* 1021 */           throw new IOException("Expected padding character");
/*      */         }
/* 1023 */         return 65280;
/*      */       } 
/* 1025 */       if (c == 61) {
/* 1026 */         if (state == 2)
/* 1027 */           return 65024; 
/* 1028 */         if (state == 3) {
/* 1029 */           return 65280;
/*      */         }
/* 1031 */         throw new IOException("Unexpected padding character");
/*      */       } 
/*      */       
/* 1034 */       if (c == 32 || c == 9 || c == 13 || c == 10) {
/* 1035 */         return 64768;
/*      */       }
/* 1037 */       if (c < 43 || c > 122) {
/* 1038 */         if (ignoreErrors) {
/* 1039 */           return 983040;
/*      */         }
/* 1041 */         throw new IOException("Invalid base64 character encountered: " + c);
/*      */       } 
/* 1043 */       int b = (this.decodingTable[c - 43] & 0xFF) - 1;
/* 1044 */       if (b < 0) {
/* 1045 */         if (ignoreErrors) {
/* 1046 */           return 983040;
/*      */         }
/* 1048 */         throw new IOException("Invalid base64 character encountered: " + c);
/*      */       } 
/* 1050 */       return b;
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
/*      */     
/*      */     public void decode(ByteBuffer source, ByteBuffer target) throws IOException {
/* 1066 */       if (target == null) {
/* 1067 */         throw new IllegalStateException();
/*      */       }
/* 1069 */       int last = this.last;
/* 1070 */       int state = this.state;
/*      */       
/* 1072 */       int remaining = source.remaining();
/* 1073 */       int targetRemaining = target.remaining();
/* 1074 */       int b = 0;
/* 1075 */       while (remaining-- > 0 && targetRemaining > 0) {
/* 1076 */         b = nextByte(source, state, last, false);
/* 1077 */         if (b == 65024) {
/* 1078 */           last = 65024;
/* 1079 */           if (--remaining <= 0) {
/*      */             break;
/*      */           }
/* 1082 */           b = nextByte(source, state, last, false);
/*      */         } 
/* 1084 */         if (b == 65280) {
/* 1085 */           last = state = 0;
/*      */           break;
/*      */         } 
/* 1088 */         if (b == 64768) {
/*      */           continue;
/*      */         }
/*      */         
/* 1092 */         if (state == 0) {
/* 1093 */           last = b << 2;
/* 1094 */           state++;
/* 1095 */           if (remaining-- <= 0) {
/*      */             break;
/*      */           }
/* 1098 */           b = nextByte(source, state, last, false);
/* 1099 */           if ((b & 0xF000) != 0) {
/* 1100 */             source.position(source.position() - 1);
/*      */             continue;
/*      */           } 
/*      */         } 
/* 1104 */         if (state == 1) {
/* 1105 */           target.put((byte)(last | b >>> 4));
/* 1106 */           last = (b & 0xF) << 4;
/* 1107 */           state++;
/* 1108 */           if (remaining-- <= 0 || --targetRemaining <= 0) {
/*      */             break;
/*      */           }
/* 1111 */           b = nextByte(source, state, last, false);
/* 1112 */           if ((b & 0xF000) != 0) {
/* 1113 */             source.position(source.position() - 1);
/*      */             continue;
/*      */           } 
/*      */         } 
/* 1117 */         if (state == 2) {
/* 1118 */           target.put((byte)(last | b >>> 2));
/* 1119 */           last = (b & 0x3) << 6;
/* 1120 */           state++;
/* 1121 */           if (remaining-- <= 0 || --targetRemaining <= 0) {
/*      */             break;
/*      */           }
/* 1124 */           b = nextByte(source, state, last, false);
/* 1125 */           if ((b & 0xF000) != 0) {
/* 1126 */             source.position(source.position() - 1);
/*      */             continue;
/*      */           } 
/*      */         } 
/* 1130 */         if (state == 3) {
/* 1131 */           target.put((byte)(last | b));
/* 1132 */           last = state = 0;
/* 1133 */           targetRemaining--;
/*      */         } 
/*      */       } 
/*      */       
/* 1137 */       if (remaining > 0) {
/* 1138 */         drain(source, b, state, last);
/*      */       }
/*      */       
/* 1141 */       this.last = last;
/* 1142 */       this.state = state;
/* 1143 */       this.lastPos = source.position();
/*      */     }
/*      */     
/*      */     private void drain(ByteBuffer source, int b, int state, int last) {
/* 1147 */       while (b != 65280 && source.remaining() > 0) {
/*      */         try {
/* 1149 */           b = nextByte(source, state, last, true);
/* 1150 */         } catch (IOException e) {
/* 1151 */           b = 0;
/*      */         } 
/*      */         
/* 1154 */         if (b == 65024) {
/* 1155 */           last = 65024;
/*      */           
/*      */           continue;
/*      */         } 
/*      */         
/* 1160 */         if ((b & 0xF000) == 0) {
/* 1161 */           source.position(source.position() - 1);
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/* 1166 */       if (b == 65280)
/*      */       {
/* 1168 */         while (source.remaining() > 0) {
/* 1169 */           b = source.get();
/* 1170 */           if (b == 10)
/*      */             break; 
/* 1172 */           if (b != 32 && b != 9 && b != 13) {
/* 1173 */             source.position(source.position() - 1);
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     private int drain(Object source, int pos, int limit, int b, int state, int last) {
/* 1182 */       while (b != 65280 && limit > pos) {
/*      */         try {
/* 1184 */           b = nextByte(source, pos++, state, last, true);
/* 1185 */         } catch (IOException e) {
/* 1186 */           b = 0;
/*      */         } 
/*      */         
/* 1189 */         if (b == 65024) {
/* 1190 */           last = 65024;
/*      */           
/*      */           continue;
/*      */         } 
/*      */         
/* 1195 */         if ((b & 0xF000) == 0) {
/* 1196 */           pos--;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/* 1201 */       if (b == 65280)
/*      */       {
/* 1203 */         while (limit > pos) {
/* 1204 */           if (source instanceof byte[]) {
/* 1205 */             b = ((byte[])source)[pos++] & 0xFF;
/* 1206 */           } else if (source instanceof String) {
/* 1207 */             b = ((String)source).charAt(pos++) & 0xFF;
/*      */           } else {
/* 1209 */             throw new IllegalArgumentException();
/*      */           } 
/*      */           
/* 1212 */           if (b == 10)
/*      */             break; 
/* 1214 */           if (b != 32 && b != 9 && b != 13) {
/* 1215 */             pos--;
/*      */ 
/*      */ 
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       }
/*      */ 
/*      */       
/* 1225 */       return pos;
/*      */     }
/*      */     
/*      */     private int decode(Object source, int sourcePos, int sourceLimit, byte[] target, int targetPos, int targetLimit) throws IOException {
/* 1229 */       if (target == null) {
/* 1230 */         throw new IllegalStateException();
/*      */       }
/* 1232 */       int last = this.last;
/* 1233 */       int state = this.state;
/*      */       
/* 1235 */       int pos = sourcePos;
/* 1236 */       int opos = targetPos;
/* 1237 */       int limit = sourceLimit;
/* 1238 */       int olimit = targetLimit;
/*      */       
/* 1240 */       int b = 0;
/* 1241 */       while (limit > pos && olimit > opos) {
/* 1242 */         b = nextByte(source, pos++, state, last, false);
/* 1243 */         if (b == 65024) {
/* 1244 */           last = 65024;
/* 1245 */           if (pos >= limit) {
/*      */             break;
/*      */           }
/* 1248 */           b = nextByte(source, pos++, state, last, false);
/*      */         } 
/* 1250 */         if (b == 65280) {
/* 1251 */           last = state = 0;
/*      */           break;
/*      */         } 
/* 1254 */         if (b == 64768) {
/*      */           continue;
/*      */         }
/*      */         
/* 1258 */         if (state == 0) {
/* 1259 */           last = b << 2;
/* 1260 */           state++;
/* 1261 */           if (pos >= limit) {
/*      */             break;
/*      */           }
/* 1264 */           b = nextByte(source, pos++, state, last, false);
/* 1265 */           if ((b & 0xF000) != 0) {
/* 1266 */             pos--;
/*      */             continue;
/*      */           } 
/*      */         } 
/* 1270 */         if (state == 1) {
/* 1271 */           target[opos++] = (byte)(last | b >>> 4);
/* 1272 */           last = (b & 0xF) << 4;
/* 1273 */           state++;
/* 1274 */           if (pos >= limit || opos >= olimit) {
/*      */             break;
/*      */           }
/* 1277 */           b = nextByte(source, pos++, state, last, false);
/* 1278 */           if ((b & 0xF000) != 0) {
/* 1279 */             pos--;
/*      */             continue;
/*      */           } 
/*      */         } 
/* 1283 */         if (state == 2) {
/* 1284 */           target[opos++] = (byte)(last | b >>> 2);
/* 1285 */           last = (b & 0x3) << 6;
/* 1286 */           state++;
/* 1287 */           if (pos >= limit || opos >= olimit) {
/*      */             break;
/*      */           }
/* 1290 */           b = nextByte(source, pos++, state, last, false);
/* 1291 */           if ((b & 0xF000) != 0) {
/* 1292 */             pos--;
/*      */             continue;
/*      */           } 
/*      */         } 
/* 1296 */         if (state == 3) {
/* 1297 */           target[opos++] = (byte)(last | b);
/* 1298 */           last = state = 0;
/*      */         } 
/*      */       } 
/*      */       
/* 1302 */       if (limit > pos) {
/* 1303 */         pos = drain(source, pos, limit, b, state, last);
/*      */       }
/*      */       
/* 1306 */       this.last = last;
/* 1307 */       this.state = state;
/* 1308 */       this.lastPos = pos;
/* 1309 */       return opos;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getLastInputPosition() {
/* 1320 */       return this.lastPos;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int decode(String source, int sourcePos, int sourceLimit, byte[] target, int targetPos, int targetLimit) throws IOException {
/* 1347 */       return decode(source, sourcePos, sourceLimit, target, targetPos, targetLimit);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int decode(String source, byte[] target) throws IOException {
/* 1368 */       return decode(source, 0, source.length(), target, 0, target.length);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int decode(byte[] source, int sourcePos, int sourceLimit, byte[] target, int targetPos, int targetLimit) throws IOException {
/* 1406 */       return decode(source, sourcePos, sourceLimit, target, targetPos, targetLimit);
/*      */     }
/*      */     
/*      */     private static ByteBuffer decode(String source, boolean url) throws IOException {
/* 1410 */       int remainder = source.length() % 4;
/* 1411 */       int size = (source.length() / 4 + ((remainder == 0) ? 0 : (4 - remainder))) * 3;
/* 1412 */       byte[] buffer = new byte[size];
/* 1413 */       int actual = (new Decoder(url)).decode(source, 0, source.length(), buffer, 0, size);
/* 1414 */       return ByteBuffer.wrap(buffer, 0, actual);
/*      */     }
/*      */     
/*      */     private static ByteBuffer decode(byte[] source, int off, int limit, boolean url) throws IOException {
/* 1418 */       int len = limit - off;
/* 1419 */       int remainder = len % 4;
/* 1420 */       int size = (len / 4 + ((remainder == 0) ? 0 : (4 - remainder))) * 3;
/* 1421 */       byte[] buffer = new byte[size];
/* 1422 */       int actual = (new Decoder(url)).decode(source, off, limit, buffer, 0, size);
/* 1423 */       return ByteBuffer.wrap(buffer, 0, actual);
/*      */     }
/*      */     
/*      */     private static ByteBuffer decode(ByteBuffer source, boolean url) throws IOException {
/* 1427 */       int len = source.remaining();
/* 1428 */       int remainder = len % 4;
/* 1429 */       int size = (len / 4 + ((remainder == 0) ? 0 : (4 - remainder))) * 3;
/* 1430 */       ByteBuffer buffer = ByteBuffer.allocate(size);
/* 1431 */       (new Decoder(url)).decode(source, buffer);
/* 1432 */       buffer.flip();
/* 1433 */       return buffer;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static class DecoderInputStream
/*      */     extends InputStream
/*      */   {
/*      */     private final InputStream input;
/*      */     
/*      */     private final byte[] buffer;
/* 1444 */     private final FlexBase64.Decoder decoder = FlexBase64.createDecoder();
/* 1445 */     private int pos = 0;
/* 1446 */     private int limit = 0;
/*      */     private byte[] one;
/*      */     
/*      */     private DecoderInputStream(InputStream input) {
/* 1450 */       this(input, 8192);
/*      */     }
/*      */     
/*      */     private DecoderInputStream(InputStream input, int bufferSize) {
/* 1454 */       this.input = input;
/* 1455 */       this.buffer = new byte[bufferSize];
/*      */     }
/*      */     
/*      */     private int fill() throws IOException {
/* 1459 */       byte[] buffer = this.buffer;
/* 1460 */       int read = this.input.read(buffer, 0, buffer.length);
/* 1461 */       this.pos = 0;
/* 1462 */       this.limit = read;
/* 1463 */       return read;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int read(byte[] b, int off, int len) throws IOException {
/*      */       while (true) {
/* 1472 */         byte[] source = this.buffer;
/* 1473 */         int pos = this.pos;
/* 1474 */         int limit = this.limit;
/* 1475 */         boolean setPos = true;
/*      */         
/* 1477 */         if (pos >= limit) {
/* 1478 */           if (len > source.length) {
/* 1479 */             source = new byte[len];
/* 1480 */             limit = this.input.read(source, 0, len);
/* 1481 */             pos = 0;
/* 1482 */             setPos = false;
/*      */           } else {
/* 1484 */             limit = fill();
/* 1485 */             pos = 0;
/*      */           } 
/*      */           
/* 1488 */           if (limit == -1) {
/* 1489 */             return -1;
/*      */           }
/*      */         } 
/*      */         
/* 1493 */         int requested = len + pos;
/* 1494 */         limit = (limit > requested) ? requested : limit;
/*      */         
/* 1496 */         int read = this.decoder.decode(source, pos, limit, b, off, off + len) - off;
/* 1497 */         if (setPos) {
/* 1498 */           this.pos = this.decoder.getLastInputPosition();
/*      */         }
/*      */         
/* 1501 */         if (read > 0) {
/* 1502 */           return read;
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int read() throws IOException {
/* 1513 */       byte[] one = this.one;
/* 1514 */       if (one == null) {
/* 1515 */         one = this.one = new byte[1];
/*      */       }
/* 1517 */       int read = read(one, 0, 1);
/* 1518 */       return (read > 0) ? (one[0] & 0xFF) : -1;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void close() throws IOException {
/* 1526 */       this.input.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static class EncoderInputStream
/*      */     extends InputStream
/*      */   {
/*      */     private final InputStream input;
/*      */     private final byte[] buffer;
/* 1536 */     private final byte[] overflow = new byte[6];
/*      */     private int overflowPos;
/*      */     private int overflowLimit;
/*      */     private final FlexBase64.Encoder encoder;
/* 1540 */     private int pos = 0;
/* 1541 */     private int limit = 0;
/*      */     private byte[] one;
/*      */     private boolean complete;
/*      */     
/*      */     private EncoderInputStream(InputStream input) {
/* 1546 */       this(input, 8192, true, false);
/*      */     }
/*      */     
/*      */     private EncoderInputStream(InputStream input, int bufferSize, boolean wrap, boolean url) {
/* 1550 */       this.input = input;
/* 1551 */       this.buffer = new byte[bufferSize];
/* 1552 */       this.encoder = new FlexBase64.Encoder(wrap, url);
/*      */     }
/*      */     
/*      */     private int fill() throws IOException {
/* 1556 */       byte[] buffer = this.buffer;
/* 1557 */       int read = this.input.read(buffer, 0, buffer.length);
/* 1558 */       this.pos = 0;
/* 1559 */       this.limit = read;
/* 1560 */       return read;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int read() throws IOException {
/* 1568 */       byte[] one = this.one;
/* 1569 */       if (one == null) {
/* 1570 */         one = this.one = new byte[1];
/*      */       }
/* 1572 */       int read = read(one, 0, 1);
/* 1573 */       return (read > 0) ? (one[0] & 0xFF) : -1;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int read(byte[] b, int off, int len) throws IOException {
/* 1581 */       byte[] buffer = this.buffer;
/* 1582 */       byte[] overflow = this.overflow;
/* 1583 */       int overflowPos = this.overflowPos;
/* 1584 */       int overflowLimit = this.overflowLimit;
/* 1585 */       boolean complete = this.complete;
/* 1586 */       boolean wrap = this.encoder.wrap;
/*      */       
/* 1588 */       int copy = 0;
/* 1589 */       if (overflowPos < overflowLimit) {
/* 1590 */         copy = copyOverflow(b, off, len, overflow, overflowPos, overflowLimit);
/* 1591 */         if (len <= copy || complete) {
/* 1592 */           return copy;
/*      */         }
/*      */         
/* 1595 */         len -= copy;
/* 1596 */         off += copy;
/* 1597 */       } else if (complete) {
/* 1598 */         return -1;
/*      */       } 
/*      */       
/*      */       while (true) {
/* 1602 */         byte[] source = buffer;
/* 1603 */         int pos = this.pos;
/* 1604 */         int limit = this.limit;
/* 1605 */         boolean setPos = true;
/*      */         
/* 1607 */         if (pos >= limit) {
/* 1608 */           if (len > source.length) {
/*      */ 
/*      */ 
/*      */             
/* 1612 */             int adjust = len / 4 * 3 - 3;
/* 1613 */             if (wrap) {
/* 1614 */               adjust -= adjust / 76 * 2 + 2;
/*      */             }
/* 1616 */             source = new byte[adjust];
/* 1617 */             limit = this.input.read(source, 0, adjust);
/* 1618 */             pos = 0;
/* 1619 */             setPos = false;
/*      */           } else {
/* 1621 */             limit = fill();
/* 1622 */             pos = 0;
/*      */           } 
/*      */           
/* 1625 */           if (limit <= 0) {
/* 1626 */             this.complete = true;
/*      */             
/* 1628 */             if (len < (wrap ? 4 : 2)) {
/* 1629 */               overflowLimit = this.encoder.complete(overflow, 0);
/* 1630 */               this.overflowLimit = overflowLimit;
/* 1631 */               int i = copyOverflow(b, off, len, overflow, 0, overflowLimit) + copy;
/* 1632 */               return (i == 0) ? -1 : i;
/*      */             } 
/*      */             
/* 1635 */             int ret = this.encoder.complete(b, off) - off + copy;
/* 1636 */             return (ret == 0) ? -1 : ret;
/*      */           } 
/*      */         } 
/*      */         
/* 1640 */         if (len < (wrap ? 6 : 4)) {
/* 1641 */           overflowLimit = this.encoder.encode(source, pos, limit, overflow, 0, overflow.length);
/* 1642 */           this.overflowLimit = overflowLimit;
/* 1643 */           this.pos = this.encoder.getLastInputPosition();
/*      */           
/* 1645 */           return copyOverflow(b, off, len, overflow, 0, overflowLimit) + copy;
/*      */         } 
/*      */         
/* 1648 */         int read = this.encoder.encode(source, pos, limit, b, off, off + len) - off;
/* 1649 */         if (setPos) {
/* 1650 */           this.pos = this.encoder.getLastInputPosition();
/*      */         }
/*      */         
/* 1653 */         if (read > 0) {
/* 1654 */           return read + copy;
/*      */         }
/*      */       } 
/*      */     }
/*      */     
/*      */     private int copyOverflow(byte[] b, int off, int len, byte[] overflow, int pos, int limit) {
/* 1660 */       limit -= pos;
/* 1661 */       len = (limit <= len) ? limit : len;
/* 1662 */       System.arraycopy(overflow, pos, b, off, len);
/* 1663 */       this.overflowPos = pos + len;
/* 1664 */       return len;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class EncoderOutputStream
/*      */     extends OutputStream
/*      */   {
/*      */     private final OutputStream output;
/*      */ 
/*      */     
/*      */     private final byte[] buffer;
/*      */     
/*      */     private final FlexBase64.Encoder encoder;
/*      */     
/* 1680 */     private int pos = 0;
/*      */     private byte[] one;
/*      */     
/*      */     private EncoderOutputStream(OutputStream output) {
/* 1684 */       this(output, 8192, true);
/*      */     }
/*      */     
/*      */     private EncoderOutputStream(OutputStream output, int bufferSize, boolean wrap) {
/* 1688 */       this.output = output;
/* 1689 */       this.buffer = new byte[bufferSize];
/* 1690 */       this.encoder = FlexBase64.createEncoder(wrap);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void write(byte[] b, int off, int len) throws IOException {
/* 1698 */       byte[] buffer = this.buffer;
/* 1699 */       FlexBase64.Encoder encoder = this.encoder;
/* 1700 */       int pos = this.pos;
/* 1701 */       int limit = off + len;
/* 1702 */       int ipos = off;
/*      */       
/* 1704 */       while (ipos < limit) {
/* 1705 */         pos = encoder.encode(b, ipos, limit, buffer, pos, buffer.length);
/* 1706 */         int last = encoder.getLastInputPosition();
/* 1707 */         if (last == ipos || pos >= buffer.length) {
/* 1708 */           this.output.write(buffer, 0, pos);
/* 1709 */           pos = 0;
/*      */         } 
/* 1711 */         ipos = last;
/*      */       } 
/* 1713 */       this.pos = pos;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void write(int b) throws IOException {
/* 1721 */       byte[] one = this.one;
/* 1722 */       if (one == null) {
/* 1723 */         this.one = one = new byte[1];
/*      */       }
/*      */       
/* 1726 */       one[0] = (byte)b;
/* 1727 */       write(one, 0, 1);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void flush() throws IOException {
/* 1735 */       OutputStream output = this.output;
/* 1736 */       output.write(this.buffer, 0, this.pos);
/* 1737 */       output.flush();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void complete() throws IOException {
/* 1746 */       OutputStream output = this.output;
/* 1747 */       byte[] buffer = this.buffer;
/* 1748 */       int pos = this.pos;
/*      */       
/* 1750 */       boolean completed = false;
/* 1751 */       if (buffer.length - pos >= (this.encoder.wrap ? 2 : 4)) {
/* 1752 */         this.pos = this.encoder.complete(buffer, pos);
/* 1753 */         completed = true;
/*      */       } 
/*      */       
/* 1756 */       flush();
/*      */       
/* 1758 */       if (!completed) {
/* 1759 */         int len = this.encoder.complete(buffer, 0);
/* 1760 */         output.write(buffer, 0, len);
/* 1761 */         output.flush();
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void close() throws IOException {
/*      */       try {
/* 1771 */         complete();
/* 1772 */       } catch (IOException iOException) {}
/*      */ 
/*      */       
/*      */       try {
/* 1776 */         this.output.flush();
/* 1777 */       } catch (IOException iOException) {}
/*      */ 
/*      */       
/* 1780 */       this.output.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static class DecoderOutputStream
/*      */     extends OutputStream
/*      */   {
/*      */     private final OutputStream output;
/*      */     
/*      */     private final byte[] buffer;
/*      */     
/*      */     private final FlexBase64.Decoder decoder;
/* 1793 */     private int pos = 0;
/*      */     private byte[] one;
/*      */     
/*      */     private DecoderOutputStream(OutputStream output) {
/* 1797 */       this(output, 8192);
/*      */     }
/*      */     
/*      */     private DecoderOutputStream(OutputStream output, int bufferSize) {
/* 1801 */       this.output = output;
/* 1802 */       this.buffer = new byte[bufferSize];
/* 1803 */       this.decoder = FlexBase64.createDecoder();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void write(byte[] b, int off, int len) throws IOException {
/* 1811 */       byte[] buffer = this.buffer;
/* 1812 */       FlexBase64.Decoder decoder = this.decoder;
/* 1813 */       int pos = this.pos;
/* 1814 */       int limit = off + len;
/* 1815 */       int ipos = off;
/*      */       
/* 1817 */       while (ipos < limit) {
/* 1818 */         pos = decoder.decode(b, ipos, limit, buffer, pos, buffer.length);
/* 1819 */         int last = decoder.getLastInputPosition();
/* 1820 */         if (last == ipos || pos >= buffer.length) {
/* 1821 */           this.output.write(buffer, 0, pos);
/* 1822 */           pos = 0;
/*      */         } 
/* 1824 */         ipos = last;
/*      */       } 
/* 1826 */       this.pos = pos;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void write(int b) throws IOException {
/* 1834 */       byte[] one = this.one;
/* 1835 */       if (one == null) {
/* 1836 */         this.one = one = new byte[1];
/*      */       }
/*      */       
/* 1839 */       one[0] = (byte)b;
/* 1840 */       write(one, 0, 1);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void flush() throws IOException {
/* 1848 */       OutputStream output = this.output;
/* 1849 */       output.write(this.buffer, 0, this.pos);
/* 1850 */       output.flush();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void close() throws IOException {
/*      */       try {
/* 1859 */         flush();
/* 1860 */       } catch (IOException iOException) {}
/*      */ 
/*      */       
/*      */       try {
/* 1864 */         this.output.flush();
/* 1865 */       } catch (IOException iOException) {}
/*      */ 
/*      */       
/* 1868 */       this.output.close();
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\FlexBase64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */