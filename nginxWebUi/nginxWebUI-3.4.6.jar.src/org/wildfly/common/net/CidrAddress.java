/*     */ package org.wildfly.common.net;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.net.Inet4Address;
/*     */ import java.net.Inet6Address;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Arrays;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.wildfly.common._private.CommonMessages;
/*     */ import org.wildfly.common.math.HashMath;
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
/*     */ public final class CidrAddress
/*     */   implements Serializable, Comparable<CidrAddress>
/*     */ {
/*     */   private static final long serialVersionUID = -6548529324373774149L;
/*  46 */   public static final CidrAddress INET4_ANY_CIDR = new CidrAddress(Inet.INET4_ANY, 0);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   public static final CidrAddress INET6_ANY_CIDR = new CidrAddress(Inet.INET6_ANY, 0);
/*     */   
/*     */   private final InetAddress networkAddress;
/*     */   private final byte[] cachedBytes;
/*     */   private final int netmaskBits;
/*     */   private Inet4Address broadcast;
/*     */   private String toString;
/*     */   private int hashCode;
/*     */   
/*     */   private CidrAddress(InetAddress networkAddress, int netmaskBits) {
/*  61 */     this.networkAddress = networkAddress;
/*  62 */     this.cachedBytes = networkAddress.getAddress();
/*  63 */     this.netmaskBits = netmaskBits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CidrAddress create(InetAddress networkAddress, int netmaskBits) {
/*  74 */     Assert.checkNotNullParam("networkAddress", networkAddress);
/*  75 */     Assert.checkMinimumParameter("netmaskBits", 0, netmaskBits);
/*  76 */     int scopeId = Inet.getScopeId(networkAddress);
/*  77 */     if (networkAddress instanceof Inet4Address) {
/*  78 */       Assert.checkMaximumParameter("netmaskBits", 32, netmaskBits);
/*  79 */       if (netmaskBits == 0) {
/*  80 */         return INET4_ANY_CIDR;
/*     */       }
/*  82 */     } else if (networkAddress instanceof Inet6Address) {
/*  83 */       Assert.checkMaximumParameter("netmaskBits", 128, netmaskBits);
/*  84 */       if (netmaskBits == 0 && scopeId == 0) {
/*  85 */         return INET6_ANY_CIDR;
/*     */       }
/*     */     } else {
/*  88 */       throw Assert.unreachableCode();
/*     */     } 
/*  90 */     byte[] bytes = networkAddress.getAddress();
/*  91 */     maskBits0(bytes, netmaskBits);
/*  92 */     String name = Inet.toOptimalString(bytes);
/*     */     try {
/*  94 */       if (bytes.length == 4) {
/*  95 */         return new CidrAddress(InetAddress.getByAddress(name, bytes), netmaskBits);
/*     */       }
/*  97 */       return new CidrAddress(Inet6Address.getByAddress(name, bytes, scopeId), netmaskBits);
/*     */     }
/*  99 */     catch (UnknownHostException e) {
/* 100 */       throw Assert.unreachableCode();
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
/*     */   public static CidrAddress create(byte[] addressBytes, int netmaskBits) {
/* 112 */     return create(addressBytes, netmaskBits, true);
/*     */   }
/*     */   
/*     */   static CidrAddress create(byte[] addressBytes, int netmaskBits, boolean clone) {
/* 116 */     Assert.checkNotNullParam("networkAddress", addressBytes);
/* 117 */     Assert.checkMinimumParameter("netmaskBits", 0, netmaskBits);
/* 118 */     int length = addressBytes.length;
/* 119 */     if (length == 4) {
/* 120 */       Assert.checkMaximumParameter("netmaskBits", 32, netmaskBits);
/* 121 */       if (netmaskBits == 0) {
/* 122 */         return INET4_ANY_CIDR;
/*     */       }
/* 124 */     } else if (length == 16) {
/* 125 */       Assert.checkMaximumParameter("netmaskBits", 128, netmaskBits);
/* 126 */       if (netmaskBits == 0) {
/* 127 */         return INET6_ANY_CIDR;
/*     */       }
/*     */     } else {
/* 130 */       throw CommonMessages.msg.invalidAddressBytes(length);
/*     */     } 
/* 132 */     byte[] bytes = clone ? (byte[])addressBytes.clone() : addressBytes;
/* 133 */     maskBits0(bytes, netmaskBits);
/* 134 */     String name = Inet.toOptimalString(bytes);
/*     */     try {
/* 136 */       return new CidrAddress(InetAddress.getByAddress(name, bytes), netmaskBits);
/* 137 */     } catch (UnknownHostException e) {
/* 138 */       throw Assert.unreachableCode();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(InetAddress address) {
/* 149 */     Assert.checkNotNullParam("address", address);
/* 150 */     if (address instanceof Inet4Address)
/* 151 */       return matches((Inet4Address)address); 
/* 152 */     if (address instanceof Inet6Address) {
/* 153 */       return matches((Inet6Address)address);
/*     */     }
/* 155 */     throw Assert.unreachableCode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(byte[] bytes) {
/* 166 */     return matches(bytes, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(byte[] bytes, int scopeId) {
/* 177 */     Assert.checkNotNullParam("bytes", bytes);
/* 178 */     return (this.cachedBytes.length == bytes.length && (getScopeId() == 0 || getScopeId() == scopeId) && bitsMatch(this.cachedBytes, bytes, this.netmaskBits));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(Inet4Address address) {
/* 188 */     Assert.checkNotNullParam("address", address);
/* 189 */     return (this.networkAddress instanceof Inet4Address && bitsMatch(this.cachedBytes, address.getAddress(), this.netmaskBits));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(Inet6Address address) {
/* 199 */     Assert.checkNotNullParam("address", address);
/* 200 */     return (this.networkAddress instanceof Inet6Address && bitsMatch(this.cachedBytes, address.getAddress(), this.netmaskBits) && (
/* 201 */       getScopeId() == 0 || getScopeId() == address.getScopeId()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(CidrAddress address) {
/* 212 */     Assert.checkNotNullParam("address", address);
/* 213 */     return (this.netmaskBits <= address.netmaskBits && matches(address.cachedBytes) && (
/* 214 */       getScopeId() == 0 || getScopeId() == address.getScopeId()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InetAddress getNetworkAddress() {
/* 224 */     return this.networkAddress;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Inet4Address getBroadcastAddress() {
/* 234 */     Inet4Address broadcast = this.broadcast;
/* 235 */     if (broadcast == null) {
/* 236 */       int netmaskBits = this.netmaskBits;
/* 237 */       if (netmaskBits >= 31)
/*     */       {
/* 239 */         return null;
/*     */       }
/*     */       
/* 242 */       byte[] cachedBytes = this.cachedBytes;
/* 243 */       if (cachedBytes.length == 4) {
/*     */         
/* 245 */         if (netmaskBits == 0) {
/* 246 */           return this.broadcast = Inet.INET4_BROADCAST;
/*     */         }
/* 248 */         byte[] bytes = maskBits1((byte[])cachedBytes.clone(), netmaskBits);
/*     */         try {
/* 250 */           return this.broadcast = (Inet4Address)InetAddress.getByAddress(Inet.toOptimalString(bytes), bytes);
/* 251 */         } catch (UnknownHostException e) {
/* 252 */           throw Assert.unreachableCode();
/*     */         } 
/*     */       } 
/*     */       
/* 256 */       return null;
/*     */     } 
/* 258 */     return broadcast;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNetmaskBits() {
/* 267 */     return this.netmaskBits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getScopeId() {
/* 276 */     return Inet.getScopeId(getNetworkAddress());
/*     */   }
/*     */   
/*     */   public int compareTo(CidrAddress other) {
/* 280 */     Assert.checkNotNullParam("other", other);
/* 281 */     if (this == other) return 0; 
/* 282 */     return compareAddressBytesTo(other.cachedBytes, other.netmaskBits, other.getScopeId());
/*     */   }
/*     */   
/*     */   public int compareAddressBytesTo(byte[] otherBytes, int otherNetmaskBits, int scopeId) {
/* 286 */     Assert.checkNotNullParam("bytes", otherBytes);
/* 287 */     int otherLength = otherBytes.length;
/* 288 */     if (otherLength != 4 && otherLength != 16) {
/* 289 */       throw CommonMessages.msg.invalidAddressBytes(otherLength);
/*     */     }
/*     */     
/* 292 */     byte[] cachedBytes = this.cachedBytes;
/* 293 */     int res = Integer.signum(cachedBytes.length - otherLength);
/* 294 */     if (res != 0) return res; 
/* 295 */     res = Integer.signum(scopeId - getScopeId());
/* 296 */     if (res != 0) return res;
/*     */     
/* 298 */     int netmaskBits = this.netmaskBits;
/* 299 */     int commonPrefix = Math.min(netmaskBits, otherNetmaskBits);
/*     */     
/* 301 */     int i = 0;
/* 302 */     while (commonPrefix >= 8) {
/* 303 */       res = Integer.signum((cachedBytes[i] & 0xFF) - (otherBytes[i] & 0xFF));
/* 304 */       if (res != 0) return res; 
/* 305 */       i++;
/* 306 */       commonPrefix -= 8;
/*     */     } 
/* 308 */     while (commonPrefix > 0) {
/* 309 */       int bit = 1 << commonPrefix;
/* 310 */       res = Integer.signum((cachedBytes[i] & bit) - (otherBytes[i] & bit));
/* 311 */       if (res != 0) return res; 
/* 312 */       commonPrefix--;
/*     */     } 
/*     */     
/* 315 */     return Integer.signum(netmaskBits - otherNetmaskBits);
/*     */   }
/*     */   
/*     */   public boolean equals(Object obj) {
/* 319 */     return (obj instanceof CidrAddress && equals((CidrAddress)obj));
/*     */   }
/*     */   
/*     */   public boolean equals(CidrAddress obj) {
/* 323 */     return (obj == this || (obj != null && this.netmaskBits == obj.netmaskBits && Arrays.equals(this.cachedBytes, obj.cachedBytes)));
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 327 */     int hashCode = this.hashCode;
/* 328 */     if (hashCode == 0) {
/* 329 */       hashCode = HashMath.multiHashOrdered(this.netmaskBits, Arrays.hashCode(this.cachedBytes));
/* 330 */       if (hashCode == 0) {
/* 331 */         hashCode = 1;
/*     */       }
/* 333 */       this.hashCode = hashCode;
/*     */     } 
/* 335 */     return hashCode;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 339 */     String toString = this.toString;
/* 340 */     if (toString == null) {
/* 341 */       int scopeId = getScopeId();
/* 342 */       if (scopeId == 0) {
/* 343 */         return this.toString = String.format("%s/%d", new Object[] { Inet.toOptimalString(this.cachedBytes), Integer.valueOf(this.netmaskBits) });
/*     */       }
/* 345 */       return this.toString = String.format("%s%%%d/%d", new Object[] { Inet.toOptimalString(this.cachedBytes), Integer.valueOf(scopeId), Integer.valueOf(this.netmaskBits) });
/*     */     } 
/*     */     
/* 348 */     return toString;
/*     */   }
/*     */   
/*     */   Object writeReplace() {
/* 352 */     return new Ser(this.cachedBytes, this.netmaskBits);
/*     */   }
/*     */   
/*     */   static final class Ser
/*     */     implements Serializable {
/*     */     private static final long serialVersionUID = 6367919693596329038L;
/*     */     final byte[] b;
/*     */     final int m;
/*     */     
/*     */     Ser(byte[] b, int m) {
/* 362 */       this.b = b;
/* 363 */       this.m = m;
/*     */     }
/*     */     
/*     */     Object readResolve() {
/* 367 */       return CidrAddress.create(this.b, this.m, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean bitsMatch(byte[] address, byte[] test, int bits) {
/* 373 */     int length = address.length;
/* 374 */     assert length == test.length;
/*     */     
/* 376 */     int i = 0;
/* 377 */     while (bits >= 8 && i < length) {
/* 378 */       if (address[i] != test[i]) {
/* 379 */         return false;
/*     */       }
/* 381 */       i++;
/* 382 */       bits -= 8;
/*     */     } 
/* 384 */     if (bits > 0) {
/* 385 */       assert bits < 8;
/* 386 */       int mask = 255 << 8 - bits;
/* 387 */       if ((address[i] & 0xFF & mask) != (test[i] & 0xFF & mask)) {
/* 388 */         return false;
/*     */       }
/*     */     } 
/* 391 */     return true;
/*     */   }
/*     */   
/*     */   private static byte[] maskBits0(byte[] address, int bits) {
/* 395 */     int length = address.length;
/*     */     
/* 397 */     int i = 0;
/* 398 */     while (bits >= 8 && i < length) {
/* 399 */       i++;
/* 400 */       bits -= 8;
/*     */     } 
/* 402 */     if (bits > 0) {
/* 403 */       assert bits < 8;
/* 404 */       int mask = 255 << 8 - bits;
/* 405 */       address[i++] = (byte)(address[i++] & mask);
/*     */     } 
/* 407 */     while (i < length) {
/* 408 */       address[i++] = 0;
/*     */     }
/* 410 */     return address;
/*     */   }
/*     */   
/*     */   private static byte[] maskBits1(byte[] address, int bits) {
/* 414 */     int length = address.length;
/*     */     
/* 416 */     int i = 0;
/* 417 */     while (bits >= 8 && i < length) {
/* 418 */       i++;
/* 419 */       bits -= 8;
/*     */     } 
/* 421 */     if (bits > 0) {
/* 422 */       assert bits < 8;
/* 423 */       int mask = 255 >>> 8 - bits;
/* 424 */       address[i++] = (byte)(address[i++] | mask);
/*     */     } 
/* 426 */     while (i < length) {
/* 427 */       address[i++] = -1;
/*     */     }
/* 429 */     return address;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\net\CidrAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */