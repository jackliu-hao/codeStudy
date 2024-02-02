/*     */ package com.sun.mail.iap;
/*     */ 
/*     */ import com.sun.mail.util.ASCIIUtility;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Vector;
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
/*     */ public class Argument
/*     */ {
/*  58 */   protected Vector items = new Vector(1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(Argument arg) {
/*  67 */     this.items.ensureCapacity(this.items.size() + arg.items.size());
/*  68 */     for (int i = 0; i < arg.items.size(); i++) {
/*  69 */       this.items.addElement(arg.items.elementAt(i));
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
/*     */   public void writeString(String s) {
/*  82 */     this.items.addElement(new AString(ASCIIUtility.getBytes(s)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeString(String s, String charset) throws UnsupportedEncodingException {
/*  91 */     if (charset == null) {
/*  92 */       writeString(s);
/*     */     } else {
/*  94 */       this.items.addElement(new AString(s.getBytes(charset)));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeBytes(byte[] b) {
/* 102 */     this.items.addElement(b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeBytes(ByteArrayOutputStream b) {
/* 110 */     this.items.addElement(b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeBytes(Literal b) {
/* 118 */     this.items.addElement(b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeAtom(String s) {
/* 128 */     this.items.addElement(new Atom(s));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeNumber(int i) {
/* 136 */     this.items.addElement(new Integer(i));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeNumber(long i) {
/* 144 */     this.items.addElement(new Long(i));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeArgument(Argument c) {
/* 151 */     this.items.addElement(c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(Protocol protocol) throws IOException, ProtocolException {
/* 159 */     int size = (this.items != null) ? this.items.size() : 0;
/* 160 */     DataOutputStream os = (DataOutputStream)protocol.getOutputStream();
/*     */     
/* 162 */     for (int i = 0; i < size; i++) {
/* 163 */       if (i > 0) {
/* 164 */         os.write(32);
/*     */       }
/* 166 */       Object o = this.items.elementAt(i);
/* 167 */       if (o instanceof Atom) {
/* 168 */         os.writeBytes(((Atom)o).string);
/* 169 */       } else if (o instanceof Number) {
/* 170 */         os.writeBytes(((Number)o).toString());
/* 171 */       } else if (o instanceof AString) {
/* 172 */         astring(((AString)o).bytes, protocol);
/* 173 */       } else if (o instanceof byte[]) {
/* 174 */         literal((byte[])o, protocol);
/* 175 */       } else if (o instanceof ByteArrayOutputStream) {
/* 176 */         literal((ByteArrayOutputStream)o, protocol);
/* 177 */       } else if (o instanceof Literal) {
/* 178 */         literal((Literal)o, protocol);
/* 179 */       } else if (o instanceof Argument) {
/* 180 */         os.write(40);
/* 181 */         ((Argument)o).write(protocol);
/* 182 */         os.write(41);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void astring(byte[] bytes, Protocol protocol) throws IOException, ProtocolException {
/* 192 */     DataOutputStream os = (DataOutputStream)protocol.getOutputStream();
/* 193 */     int len = bytes.length;
/*     */ 
/*     */     
/* 196 */     if (len > 1024) {
/* 197 */       literal(bytes, protocol);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 202 */     boolean quote = (len == 0);
/* 203 */     boolean escape = false;
/*     */     
/*     */     int i;
/* 206 */     for (i = 0; i < len; i++) {
/* 207 */       byte b = bytes[i];
/* 208 */       if (b == 0 || b == 13 || b == 10 || (b & 0xFF) > 127) {
/*     */         
/* 210 */         literal(bytes, protocol);
/*     */         return;
/*     */       } 
/* 213 */       if (b == 42 || b == 37 || b == 40 || b == 41 || b == 123 || b == 34 || b == 92 || (b & 0xFF) <= 32) {
/*     */         
/* 215 */         quote = true;
/* 216 */         if (b == 34 || b == 92) {
/* 217 */           escape = true;
/*     */         }
/*     */       } 
/*     */     } 
/* 221 */     if (quote) {
/* 222 */       os.write(34);
/*     */     }
/* 224 */     if (escape) {
/*     */       
/* 226 */       for (i = 0; i < len; i++) {
/* 227 */         byte b = bytes[i];
/* 228 */         if (b == 34 || b == 92)
/* 229 */           os.write(92); 
/* 230 */         os.write(b);
/*     */       } 
/*     */     } else {
/* 233 */       os.write(bytes);
/*     */     } 
/*     */     
/* 236 */     if (quote) {
/* 237 */       os.write(34);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void literal(byte[] b, Protocol protocol) throws IOException, ProtocolException {
/* 245 */     startLiteral(protocol, b.length).write(b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void literal(ByteArrayOutputStream b, Protocol protocol) throws IOException, ProtocolException {
/* 253 */     b.writeTo(startLiteral(protocol, b.size()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void literal(Literal b, Protocol protocol) throws IOException, ProtocolException {
/* 261 */     b.writeTo(startLiteral(protocol, b.size()));
/*     */   }
/*     */ 
/*     */   
/*     */   private OutputStream startLiteral(Protocol protocol, int size) throws IOException, ProtocolException {
/* 266 */     DataOutputStream os = (DataOutputStream)protocol.getOutputStream();
/* 267 */     boolean nonSync = protocol.supportsNonSyncLiterals();
/*     */     
/* 269 */     os.write(123);
/* 270 */     os.writeBytes(Integer.toString(size));
/* 271 */     if (nonSync) {
/* 272 */       os.writeBytes("+}\r\n");
/*     */     } else {
/* 274 */       os.writeBytes("}\r\n");
/* 275 */     }  os.flush();
/*     */ 
/*     */ 
/*     */     
/* 279 */     if (!nonSync) {
/*     */       while (true) {
/* 281 */         Response r = protocol.readResponse();
/* 282 */         if (r.isContinuation())
/*     */           break; 
/* 284 */         if (r.isTagged()) {
/* 285 */           throw new LiteralException(r);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 290 */     return os;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\iap\Argument.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */