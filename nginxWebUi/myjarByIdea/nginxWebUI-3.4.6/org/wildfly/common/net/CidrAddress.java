package org.wildfly.common.net;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import org.wildfly.common.Assert;
import org.wildfly.common._private.CommonMessages;
import org.wildfly.common.math.HashMath;

public final class CidrAddress implements Serializable, Comparable<CidrAddress> {
   private static final long serialVersionUID = -6548529324373774149L;
   public static final CidrAddress INET4_ANY_CIDR;
   public static final CidrAddress INET6_ANY_CIDR;
   private final InetAddress networkAddress;
   private final byte[] cachedBytes;
   private final int netmaskBits;
   private Inet4Address broadcast;
   private String toString;
   private int hashCode;

   private CidrAddress(InetAddress networkAddress, int netmaskBits) {
      this.networkAddress = networkAddress;
      this.cachedBytes = networkAddress.getAddress();
      this.netmaskBits = netmaskBits;
   }

   public static CidrAddress create(InetAddress networkAddress, int netmaskBits) {
      Assert.checkNotNullParam("networkAddress", networkAddress);
      Assert.checkMinimumParameter("netmaskBits", 0, netmaskBits);
      int scopeId = Inet.getScopeId(networkAddress);
      if (networkAddress instanceof Inet4Address) {
         Assert.checkMaximumParameter("netmaskBits", 32, netmaskBits);
         if (netmaskBits == 0) {
            return INET4_ANY_CIDR;
         }
      } else {
         if (!(networkAddress instanceof Inet6Address)) {
            throw Assert.unreachableCode();
         }

         Assert.checkMaximumParameter("netmaskBits", 128, netmaskBits);
         if (netmaskBits == 0 && scopeId == 0) {
            return INET6_ANY_CIDR;
         }
      }

      byte[] bytes = networkAddress.getAddress();
      maskBits0(bytes, netmaskBits);
      String name = Inet.toOptimalString(bytes);

      try {
         return bytes.length == 4 ? new CidrAddress(InetAddress.getByAddress(name, bytes), netmaskBits) : new CidrAddress(Inet6Address.getByAddress(name, bytes, scopeId), netmaskBits);
      } catch (UnknownHostException var6) {
         throw Assert.unreachableCode();
      }
   }

   public static CidrAddress create(byte[] addressBytes, int netmaskBits) {
      return create(addressBytes, netmaskBits, true);
   }

   static CidrAddress create(byte[] addressBytes, int netmaskBits, boolean clone) {
      Assert.checkNotNullParam("networkAddress", addressBytes);
      Assert.checkMinimumParameter("netmaskBits", 0, netmaskBits);
      int length = addressBytes.length;
      if (length == 4) {
         Assert.checkMaximumParameter("netmaskBits", 32, netmaskBits);
         if (netmaskBits == 0) {
            return INET4_ANY_CIDR;
         }
      } else {
         if (length != 16) {
            throw CommonMessages.msg.invalidAddressBytes(length);
         }

         Assert.checkMaximumParameter("netmaskBits", 128, netmaskBits);
         if (netmaskBits == 0) {
            return INET6_ANY_CIDR;
         }
      }

      byte[] bytes = clone ? (byte[])addressBytes.clone() : addressBytes;
      maskBits0(bytes, netmaskBits);
      String name = Inet.toOptimalString(bytes);

      try {
         return new CidrAddress(InetAddress.getByAddress(name, bytes), netmaskBits);
      } catch (UnknownHostException var7) {
         throw Assert.unreachableCode();
      }
   }

   public boolean matches(InetAddress address) {
      Assert.checkNotNullParam("address", address);
      if (address instanceof Inet4Address) {
         return this.matches((Inet4Address)address);
      } else if (address instanceof Inet6Address) {
         return this.matches((Inet6Address)address);
      } else {
         throw Assert.unreachableCode();
      }
   }

   public boolean matches(byte[] bytes) {
      return this.matches(bytes, 0);
   }

   public boolean matches(byte[] bytes, int scopeId) {
      Assert.checkNotNullParam("bytes", bytes);
      return this.cachedBytes.length == bytes.length && (this.getScopeId() == 0 || this.getScopeId() == scopeId) && bitsMatch(this.cachedBytes, bytes, this.netmaskBits);
   }

   public boolean matches(Inet4Address address) {
      Assert.checkNotNullParam("address", address);
      return this.networkAddress instanceof Inet4Address && bitsMatch(this.cachedBytes, address.getAddress(), this.netmaskBits);
   }

   public boolean matches(Inet6Address address) {
      Assert.checkNotNullParam("address", address);
      return this.networkAddress instanceof Inet6Address && bitsMatch(this.cachedBytes, address.getAddress(), this.netmaskBits) && (this.getScopeId() == 0 || this.getScopeId() == address.getScopeId());
   }

   public boolean matches(CidrAddress address) {
      Assert.checkNotNullParam("address", address);
      return this.netmaskBits <= address.netmaskBits && this.matches(address.cachedBytes) && (this.getScopeId() == 0 || this.getScopeId() == address.getScopeId());
   }

   public InetAddress getNetworkAddress() {
      return this.networkAddress;
   }

   public Inet4Address getBroadcastAddress() {
      Inet4Address broadcast = this.broadcast;
      if (broadcast == null) {
         int netmaskBits = this.netmaskBits;
         if (netmaskBits >= 31) {
            return null;
         } else {
            byte[] cachedBytes = this.cachedBytes;
            if (cachedBytes.length == 4) {
               if (netmaskBits == 0) {
                  return this.broadcast = Inet.INET4_BROADCAST;
               } else {
                  byte[] bytes = maskBits1((byte[])cachedBytes.clone(), netmaskBits);

                  try {
                     return this.broadcast = (Inet4Address)InetAddress.getByAddress(Inet.toOptimalString(bytes), bytes);
                  } catch (UnknownHostException var6) {
                     throw Assert.unreachableCode();
                  }
               }
            } else {
               return null;
            }
         }
      } else {
         return broadcast;
      }
   }

   public int getNetmaskBits() {
      return this.netmaskBits;
   }

   public int getScopeId() {
      return Inet.getScopeId(this.getNetworkAddress());
   }

   public int compareTo(CidrAddress other) {
      Assert.checkNotNullParam("other", other);
      return this == other ? 0 : this.compareAddressBytesTo(other.cachedBytes, other.netmaskBits, other.getScopeId());
   }

   public int compareAddressBytesTo(byte[] otherBytes, int otherNetmaskBits, int scopeId) {
      Assert.checkNotNullParam("bytes", otherBytes);
      int otherLength = otherBytes.length;
      if (otherLength != 4 && otherLength != 16) {
         throw CommonMessages.msg.invalidAddressBytes(otherLength);
      } else {
         byte[] cachedBytes = this.cachedBytes;
         int res = Integer.signum(cachedBytes.length - otherLength);
         if (res != 0) {
            return res;
         } else {
            res = Integer.signum(scopeId - this.getScopeId());
            if (res != 0) {
               return res;
            } else {
               int netmaskBits = this.netmaskBits;
               int commonPrefix = Math.min(netmaskBits, otherNetmaskBits);

               int i;
               for(i = 0; commonPrefix >= 8; commonPrefix -= 8) {
                  res = Integer.signum((cachedBytes[i] & 255) - (otherBytes[i] & 255));
                  if (res != 0) {
                     return res;
                  }

                  ++i;
               }

               while(commonPrefix > 0) {
                  int bit = 1 << commonPrefix;
                  res = Integer.signum((cachedBytes[i] & bit) - (otherBytes[i] & bit));
                  if (res != 0) {
                     return res;
                  }

                  --commonPrefix;
               }

               return Integer.signum(netmaskBits - otherNetmaskBits);
            }
         }
      }
   }

   public boolean equals(Object obj) {
      return obj instanceof CidrAddress && this.equals((CidrAddress)obj);
   }

   public boolean equals(CidrAddress obj) {
      return obj == this || obj != null && this.netmaskBits == obj.netmaskBits && Arrays.equals(this.cachedBytes, obj.cachedBytes);
   }

   public int hashCode() {
      int hashCode = this.hashCode;
      if (hashCode == 0) {
         hashCode = HashMath.multiHashOrdered(this.netmaskBits, Arrays.hashCode(this.cachedBytes));
         if (hashCode == 0) {
            hashCode = 1;
         }

         this.hashCode = hashCode;
      }

      return hashCode;
   }

   public String toString() {
      String toString = this.toString;
      if (toString == null) {
         int scopeId = this.getScopeId();
         return scopeId == 0 ? (this.toString = String.format("%s/%d", Inet.toOptimalString(this.cachedBytes), this.netmaskBits)) : (this.toString = String.format("%s%%%d/%d", Inet.toOptimalString(this.cachedBytes), scopeId, this.netmaskBits));
      } else {
         return toString;
      }
   }

   Object writeReplace() {
      return new Ser(this.cachedBytes, this.netmaskBits);
   }

   private static boolean bitsMatch(byte[] address, byte[] test, int bits) {
      int length = address.length;

      assert length == test.length;

      int i;
      for(i = 0; bits >= 8 && i < length; bits -= 8) {
         if (address[i] != test[i]) {
            return false;
         }

         ++i;
      }

      if (bits > 0) {
         assert bits < 8;

         int mask = 255 << 8 - bits;
         if ((address[i] & 255 & mask) != (test[i] & 255 & mask)) {
            return false;
         }
      }

      return true;
   }

   private static byte[] maskBits0(byte[] address, int bits) {
      int length = address.length;

      int i;
      for(i = 0; bits >= 8 && i < length; bits -= 8) {
         ++i;
      }

      if (bits > 0) {
         assert bits < 8;

         int mask = 255 << 8 - bits;
         int var10001 = i++;
         address[var10001] = (byte)(address[var10001] & mask);
      }

      while(i < length) {
         address[i++] = 0;
      }

      return address;
   }

   private static byte[] maskBits1(byte[] address, int bits) {
      int length = address.length;

      int i;
      for(i = 0; bits >= 8 && i < length; bits -= 8) {
         ++i;
      }

      if (bits > 0) {
         assert bits < 8;

         int mask = 255 >>> 8 - bits;
         int var10001 = i++;
         address[var10001] = (byte)(address[var10001] | mask);
      }

      while(i < length) {
         address[i++] = -1;
      }

      return address;
   }

   static {
      INET4_ANY_CIDR = new CidrAddress(Inet.INET4_ANY, 0);
      INET6_ANY_CIDR = new CidrAddress(Inet.INET6_ANY, 0);
   }

   static final class Ser implements Serializable {
      private static final long serialVersionUID = 6367919693596329038L;
      final byte[] b;
      final int m;

      Ser(byte[] b, int m) {
         this.b = b;
         this.m = m;
      }

      Object readResolve() {
         return CidrAddress.create(this.b, this.m, false);
      }
   }
}
