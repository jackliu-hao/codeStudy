/*     */ package io.undertow.protocols.ssl;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import javax.net.ssl.SSLException;
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
/*     */ final class ALPNHackServerHelloExplorer
/*     */ {
/*     */   static byte[] addAlpnExtensionsToServerHello(byte[] source, String selectedAlpnProtocol) throws SSLException {
/*  55 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/*  56 */     ByteBuffer input = ByteBuffer.wrap(source);
/*     */     
/*     */     try {
/*  59 */       exploreHandshake(input, source.length, new AtomicReference<>(selectedAlpnProtocol), out);
/*     */       
/*  61 */       int serverHelloLength = out.size() - 4;
/*  62 */       out.write(source, input.position(), input.remaining());
/*  63 */       byte[] data = out.toByteArray();
/*     */ 
/*     */       
/*  66 */       data[1] = (byte)(serverHelloLength >> 16 & 0xFF);
/*  67 */       data[2] = (byte)(serverHelloLength >> 8 & 0xFF);
/*  68 */       data[3] = (byte)(serverHelloLength & 0xFF);
/*  69 */       return data;
/*  70 */     } catch (AlpnProcessingException e) {
/*  71 */       return source;
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
/*     */   static byte[] removeAlpnExtensionsFromServerHello(ByteBuffer source, AtomicReference<String> selectedAlpnProtocol) throws SSLException {
/*  83 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/*     */ 
/*     */     
/*     */     try {
/*  87 */       exploreHandshake(source, source.remaining(), selectedAlpnProtocol, out);
/*     */       
/*  89 */       int serverHelloLength = out.size() - 4;
/*  90 */       byte[] data = out.toByteArray();
/*     */ 
/*     */       
/*  93 */       data[1] = (byte)(serverHelloLength >> 16 & 0xFF);
/*  94 */       data[2] = (byte)(serverHelloLength >> 8 & 0xFF);
/*  95 */       data[3] = (byte)(serverHelloLength & 0xFF);
/*  96 */       return data;
/*  97 */     } catch (AlpnProcessingException e) {
/*  98 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void exploreHandshake(ByteBuffer input, int recordLength, AtomicReference<String> selectedAlpnProtocol, ByteArrayOutputStream out) throws SSLException {
/* 104 */     byte handshakeType = input.get();
/* 105 */     if (handshakeType != 2) {
/* 106 */       throw UndertowMessages.MESSAGES.expectedServerHello();
/*     */     }
/* 108 */     out.write(handshakeType);
/*     */ 
/*     */     
/* 111 */     int handshakeLength = getInt24(input);
/* 112 */     out.write(0);
/* 113 */     out.write(0);
/* 114 */     out.write(0);
/*     */ 
/*     */ 
/*     */     
/* 118 */     if (handshakeLength > recordLength - 4) {
/* 119 */       throw UndertowMessages.MESSAGES.multiRecordSSLHandshake();
/*     */     }
/* 121 */     int old = input.limit();
/* 122 */     input.limit(handshakeLength + input.position());
/* 123 */     exploreServerHello(input, selectedAlpnProtocol, out);
/* 124 */     input.limit(old);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void exploreServerHello(ByteBuffer input, AtomicReference<String> alpnProtocolReference, ByteArrayOutputStream out) throws SSLException {
/* 130 */     byte helloMajorVersion = input.get();
/* 131 */     byte helloMinorVersion = input.get();
/* 132 */     out.write(helloMajorVersion);
/* 133 */     out.write(helloMinorVersion);
/*     */     
/* 135 */     for (int i = 0; i < 32; i++) {
/* 136 */       out.write(input.get() & 0xFF);
/*     */     }
/*     */ 
/*     */     
/* 140 */     processByteVector8(input, out);
/*     */ 
/*     */     
/* 143 */     out.write(input.get() & 0xFF);
/* 144 */     out.write(input.get() & 0xFF);
/*     */ 
/*     */     
/* 147 */     out.write(input.get() & 0xFF);
/*     */     
/* 149 */     String existingAlpn = null;
/* 150 */     ByteArrayOutputStream extensionsOutput = null;
/* 151 */     if (input.remaining() > 0) {
/* 152 */       extensionsOutput = new ByteArrayOutputStream();
/* 153 */       existingAlpn = exploreExtensions(input, extensionsOutput, (alpnProtocolReference.get() == null));
/*     */     } 
/*     */     
/* 156 */     if (existingAlpn != null) {
/* 157 */       if (alpnProtocolReference.get() != null) {
/* 158 */         throw new AlpnProcessingException();
/*     */       }
/* 160 */       alpnProtocolReference.set(existingAlpn);
/* 161 */       byte[] existing = extensionsOutput.toByteArray();
/* 162 */       out.write(existing, 0, existing.length);
/*     */     }
/* 164 */     else if (alpnProtocolReference.get() != null) {
/* 165 */       String selectedAlpnProtocol = alpnProtocolReference.get();
/* 166 */       ByteArrayOutputStream alpnBits = new ByteArrayOutputStream();
/* 167 */       alpnBits.write(0);
/* 168 */       alpnBits.write(16);
/* 169 */       int length = 3 + selectedAlpnProtocol.length();
/* 170 */       alpnBits.write(length >> 8 & 0xFF);
/* 171 */       alpnBits.write(length & 0xFF);
/* 172 */       length -= 2;
/* 173 */       alpnBits.write(length >> 8 & 0xFF);
/* 174 */       alpnBits.write(length & 0xFF);
/* 175 */       alpnBits.write(selectedAlpnProtocol.length() & 0xFF);
/* 176 */       for (int j = 0; j < selectedAlpnProtocol.length(); j++) {
/* 177 */         alpnBits.write(selectedAlpnProtocol.charAt(j) & 0xFF);
/*     */       }
/*     */       
/* 180 */       if (extensionsOutput != null) {
/* 181 */         byte[] existing = extensionsOutput.toByteArray();
/* 182 */         int newLength = existing.length - 2 + alpnBits.size();
/* 183 */         existing[0] = (byte)(newLength >> 8 & 0xFF);
/* 184 */         existing[1] = (byte)(newLength & 0xFF);
/*     */         try {
/* 186 */           out.write(existing);
/* 187 */           out.write(alpnBits.toByteArray());
/* 188 */         } catch (IOException e) {
/* 189 */           throw new RuntimeException(e);
/*     */         } 
/*     */       } else {
/* 192 */         int al = alpnBits.size();
/* 193 */         out.write(al >> 8 & 0xFF);
/* 194 */         out.write(al & 0xFF);
/*     */         try {
/* 196 */           out.write(alpnBits.toByteArray());
/* 197 */         } catch (IOException e) {
/* 198 */           throw new RuntimeException(e);
/*     */         } 
/*     */       } 
/* 201 */     } else if (extensionsOutput != null) {
/* 202 */       byte[] existing = extensionsOutput.toByteArray();
/* 203 */       out.write(existing, 0, existing.length);
/*     */     } 
/*     */   }
/*     */   
/*     */   static List<ByteBuffer> extractRecords(ByteBuffer data) {
/* 208 */     List<ByteBuffer> ret = new ArrayList<>();
/* 209 */     while (data.hasRemaining()) {
/* 210 */       byte d1 = data.get();
/* 211 */       byte d2 = data.get();
/* 212 */       byte d3 = data.get();
/* 213 */       byte d4 = data.get();
/* 214 */       byte d5 = data.get();
/* 215 */       int length = (d4 & 0xFF) << 8 | d5 & 0xFF;
/* 216 */       byte[] b = new byte[length + 5];
/* 217 */       b[0] = d1;
/* 218 */       b[1] = d2;
/* 219 */       b[2] = d3;
/* 220 */       b[3] = d4;
/* 221 */       b[4] = d5;
/* 222 */       data.get(b, 5, length);
/* 223 */       ret.add(ByteBuffer.wrap(b));
/*     */     } 
/* 225 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String exploreExtensions(ByteBuffer input, ByteArrayOutputStream extensionOut, boolean removeAlpn) throws SSLException {
/* 230 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 231 */     String ret = null;
/* 232 */     int length = getInt16(input);
/* 233 */     out.write(length >> 8 & 0xFF);
/* 234 */     out.write(length & 0xFF);
/* 235 */     int originalLength = length;
/* 236 */     while (length > 0) {
/* 237 */       int extType = getInt16(input);
/* 238 */       int extLen = getInt16(input);
/* 239 */       if (extType == 16) {
/* 240 */         int vlen = getInt16(input);
/* 241 */         ret = readByteVector8(input);
/* 242 */         if (!removeAlpn) {
/*     */           
/* 244 */           out.write(extType >> 8 & 0xFF);
/* 245 */           out.write(extType & 0xFF);
/* 246 */           out.write(extLen >> 8 & 0xFF);
/* 247 */           out.write(extLen & 0xFF);
/* 248 */           out.write(vlen >> 8 & 0xFF);
/* 249 */           out.write(vlen & 0xFF);
/* 250 */           out.write(ret.length() & 0xFF);
/* 251 */           for (int i = 0; i < ret.length(); i++) {
/* 252 */             out.write(ret.charAt(i) & 0xFF);
/*     */           }
/*     */         } else {
/* 255 */           originalLength -= 6;
/* 256 */           originalLength -= vlen;
/*     */         } 
/*     */       } else {
/* 259 */         out.write(extType >> 8 & 0xFF);
/* 260 */         out.write(extType & 0xFF);
/* 261 */         out.write(extLen >> 8 & 0xFF);
/* 262 */         out.write(extLen & 0xFF);
/* 263 */         processByteVector(input, extLen, out);
/*     */       } 
/* 265 */       length -= extLen + 4;
/*     */     } 
/* 267 */     if (removeAlpn && ret == null)
/*     */     {
/* 269 */       throw new AlpnProcessingException();
/*     */     }
/* 271 */     byte[] data = out.toByteArray();
/* 272 */     data[0] = (byte)(originalLength >> 8 & 0xFF);
/* 273 */     data[1] = (byte)(originalLength & 0xFF);
/* 274 */     extensionOut.write(data, 0, data.length);
/* 275 */     return ret;
/*     */   }
/*     */   
/*     */   private static String readByteVector8(ByteBuffer input) {
/* 279 */     int length = getInt8(input);
/* 280 */     byte[] data = new byte[length];
/* 281 */     input.get(data);
/* 282 */     return new String(data, StandardCharsets.US_ASCII);
/*     */   }
/*     */   private static int getInt8(ByteBuffer input) {
/* 285 */     return input.get();
/*     */   }
/*     */   
/*     */   private static int getInt16(ByteBuffer input) {
/* 289 */     return (input.get() & 0xFF) << 8 | input.get() & 0xFF;
/*     */   }
/*     */   
/*     */   private static int getInt24(ByteBuffer input) {
/* 293 */     return (input.get() & 0xFF) << 16 | (input.get() & 0xFF) << 8 | input
/* 294 */       .get() & 0xFF;
/*     */   }
/*     */   private static void processByteVector8(ByteBuffer input, ByteArrayOutputStream out) {
/* 297 */     int int8 = getInt8(input);
/* 298 */     out.write(int8 & 0xFF);
/* 299 */     processByteVector(input, int8, out);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void processByteVector(ByteBuffer input, int length, ByteArrayOutputStream out) {
/* 304 */     for (int i = 0; i < length; i++) {
/* 305 */       out.write(input.get() & 0xFF);
/*     */     }
/*     */   }
/*     */   
/*     */   static ByteBuffer createNewOutputRecords(byte[] newFirstMessage, List<ByteBuffer> records) {
/* 310 */     int length = newFirstMessage.length;
/* 311 */     length += 5;
/* 312 */     for (int i = 1; i < records.size(); i++) {
/*     */       
/* 314 */       ByteBuffer rec = records.get(i);
/* 315 */       length += rec.remaining();
/*     */     } 
/* 317 */     byte[] newData = new byte[length];
/* 318 */     ByteBuffer ret = ByteBuffer.wrap(newData);
/* 319 */     ByteBuffer oldHello = records.get(0);
/* 320 */     ret.put(oldHello.get());
/* 321 */     ret.put(oldHello.get());
/* 322 */     ret.put(oldHello.get());
/* 323 */     ret.put((byte)(newFirstMessage.length >> 8 & 0xFF));
/* 324 */     ret.put((byte)(newFirstMessage.length & 0xFF));
/* 325 */     ret.put(newFirstMessage);
/* 326 */     for (int j = 1; j < records.size(); j++) {
/* 327 */       ByteBuffer rec = records.get(j);
/* 328 */       ret.put(rec);
/*     */     } 
/* 330 */     ret.flip();
/* 331 */     return ret;
/*     */   }
/*     */   
/*     */   private static final class AlpnProcessingException extends RuntimeException {
/*     */     private AlpnProcessingException() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\ALPNHackServerHelloExplorer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */