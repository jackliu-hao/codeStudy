package com.mysql.cj.protocol.a;

import com.mysql.cj.Messages;
import com.mysql.cj.ServerVersion;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.UnableToConnectException;
import com.mysql.cj.protocol.ServerCapabilities;

public class NativeCapabilities implements ServerCapabilities {
   private NativePacketPayload initialHandshakePacket;
   private byte protocolVersion = 0;
   private ServerVersion serverVersion;
   private long threadId = -1L;
   private String seed;
   private int capabilityFlags;
   private int serverDefaultCollationIndex;
   private int statusFlags = 0;
   private int authPluginDataLength = 0;
   private boolean serverHasFracSecsSupport = true;

   public NativeCapabilities(NativePacketPayload initialHandshakePacket) {
      this.initialHandshakePacket = initialHandshakePacket;
      this.protocolVersion = (byte)((int)initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT1));

      try {
         this.serverVersion = ServerVersion.parseVersion(initialHandshakePacket.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII"));
         this.threadId = initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT4);
         this.seed = initialHandshakePacket.readString(NativeConstants.StringLengthDataType.STRING_FIXED, "ASCII", 8);
         initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT1);
         int flags = 0;
         if (initialHandshakePacket.getPosition() < initialHandshakePacket.getPayloadLength()) {
            flags = (int)initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT2);
         }

         this.serverDefaultCollationIndex = (int)initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT1);
         this.statusFlags = (int)initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT2);
         flags |= (int)initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT2) << 16;
         this.setCapabilityFlags(flags);
         if ((flags & 524288) != 0) {
            this.authPluginDataLength = (int)initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT1);
         } else {
            initialHandshakePacket.readInteger(NativeConstants.IntegerDataType.INT1);
         }

         initialHandshakePacket.setPosition(initialHandshakePacket.getPosition() + 10);
         this.serverHasFracSecsSupport = this.serverVersion.meetsMinimum(new ServerVersion(5, 6, 4));
      } catch (Throwable var3) {
         if (this.protocolVersion == 11 && IndexOutOfBoundsException.class.isAssignableFrom(var3.getClass())) {
            throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, Messages.getString("NativeCapabilites.001", new Object[]{this.protocolVersion}));
         } else {
            throw var3;
         }
      }
   }

   public NativePacketPayload getInitialHandshakePacket() {
      return this.initialHandshakePacket;
   }

   public int getCapabilityFlags() {
      return this.capabilityFlags;
   }

   public void setCapabilityFlags(int capabilityFlags) {
      this.capabilityFlags = capabilityFlags;
   }

   public ServerVersion getServerVersion() {
      return this.serverVersion;
   }

   public long getThreadId() {
      return this.threadId;
   }

   public void setThreadId(long threadId) {
      this.threadId = threadId;
   }

   public String getSeed() {
      return this.seed;
   }

   public int getServerDefaultCollationIndex() {
      return this.serverDefaultCollationIndex;
   }

   public int getStatusFlags() {
      return this.statusFlags;
   }

   public int getAuthPluginDataLength() {
      return this.authPluginDataLength;
   }

   public boolean serverSupportsFracSecs() {
      return this.serverHasFracSecsSupport;
   }
}
