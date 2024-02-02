package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public abstract class WevtapiUtil {
   public static String EvtGetExtendedStatus() {
      IntByReference buffUsed = new IntByReference();
      int errorCode = Wevtapi.INSTANCE.EvtGetExtendedStatus(0, (char[])null, buffUsed);
      if (errorCode != 0 && errorCode != 122) {
         throw new Win32Exception(errorCode);
      } else if (buffUsed.getValue() == 0) {
         return "";
      } else {
         char[] mem = new char[buffUsed.getValue()];
         errorCode = Wevtapi.INSTANCE.EvtGetExtendedStatus(mem.length, mem, buffUsed);
         if (errorCode != 0) {
            throw new Win32Exception(errorCode);
         } else {
            return Native.toString(mem);
         }
      }
   }

   public static Memory EvtRender(Winevt.EVT_HANDLE context, Winevt.EVT_HANDLE fragment, int flags, IntByReference propertyCount) {
      IntByReference buffUsed = new IntByReference();
      boolean result = Wevtapi.INSTANCE.EvtRender(context, fragment, flags, 0, (Pointer)null, buffUsed, propertyCount);
      int errorCode = Kernel32.INSTANCE.GetLastError();
      if (!result && errorCode != 122) {
         throw new Win32Exception(errorCode);
      } else {
         Memory mem = new Memory((long)buffUsed.getValue());
         result = Wevtapi.INSTANCE.EvtRender(context, fragment, flags, (int)mem.size(), mem, buffUsed, propertyCount);
         if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            return mem;
         }
      }
   }

   public static String EvtFormatMessage(Winevt.EVT_HANDLE publisherMetadata, Winevt.EVT_HANDLE event, int messageId, int valueCount, Winevt.EVT_VARIANT[] values, int flags) {
      IntByReference bufferUsed = new IntByReference();
      boolean result = Wevtapi.INSTANCE.EvtFormatMessage(publisherMetadata, event, messageId, valueCount, values, flags, 0, (char[])null, bufferUsed);
      int errorCode = Kernel32.INSTANCE.GetLastError();
      if (!result && errorCode != 122) {
         throw new Win32Exception(errorCode);
      } else {
         char[] buffer = new char[bufferUsed.getValue()];
         result = Wevtapi.INSTANCE.EvtFormatMessage(publisherMetadata, event, messageId, valueCount, values, flags, buffer.length, buffer, bufferUsed);
         if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            return Native.toString(buffer);
         }
      }
   }

   public static Winevt.EVT_VARIANT EvtGetChannelConfigProperty(Winevt.EVT_HANDLE channelHandle, int propertyId) {
      IntByReference propertyValueBufferUsed = new IntByReference();
      boolean result = Wevtapi.INSTANCE.EvtGetChannelConfigProperty(channelHandle, propertyId, 0, 0, (Pointer)null, propertyValueBufferUsed);
      int errorCode = Kernel32.INSTANCE.GetLastError();
      if (!result && errorCode != 122) {
         throw new Win32Exception(errorCode);
      } else {
         Memory propertyValueBuffer = new Memory((long)propertyValueBufferUsed.getValue());
         result = Wevtapi.INSTANCE.EvtGetChannelConfigProperty(channelHandle, propertyId, 0, (int)propertyValueBuffer.size(), propertyValueBuffer, propertyValueBufferUsed);
         if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            Winevt.EVT_VARIANT resultEvt = new Winevt.EVT_VARIANT(propertyValueBuffer);
            resultEvt.read();
            return resultEvt;
         }
      }
   }

   public static String EvtNextPublisherId(Winevt.EVT_HANDLE publisherEnum) {
      IntByReference publisherIdBufferUsed = new IntByReference();
      boolean result = Wevtapi.INSTANCE.EvtNextPublisherId(publisherEnum, 0, (char[])null, publisherIdBufferUsed);
      int errorCode = Kernel32.INSTANCE.GetLastError();
      if (!result && errorCode != 122) {
         throw new Win32Exception(errorCode);
      } else {
         char[] publisherIdBuffer = new char[publisherIdBufferUsed.getValue()];
         result = Wevtapi.INSTANCE.EvtNextPublisherId(publisherEnum, publisherIdBuffer.length, publisherIdBuffer, publisherIdBufferUsed);
         if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            return Native.toString(publisherIdBuffer);
         }
      }
   }

   public static Memory EvtGetPublisherMetadataProperty(Winevt.EVT_HANDLE PublisherMetadata, int PropertyId, int Flags) {
      IntByReference publisherMetadataPropertyBufferUsed = new IntByReference();
      boolean result = Wevtapi.INSTANCE.EvtGetPublisherMetadataProperty(PublisherMetadata, PropertyId, Flags, 0, (Pointer)null, publisherMetadataPropertyBufferUsed);
      int errorCode = Kernel32.INSTANCE.GetLastError();
      if (!result && errorCode != 122) {
         throw new Win32Exception(errorCode);
      } else {
         Memory publisherMetadataPropertyBuffer = new Memory((long)publisherMetadataPropertyBufferUsed.getValue());
         result = Wevtapi.INSTANCE.EvtGetPublisherMetadataProperty(PublisherMetadata, PropertyId, Flags, (int)publisherMetadataPropertyBuffer.size(), publisherMetadataPropertyBuffer, publisherMetadataPropertyBufferUsed);
         if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            return publisherMetadataPropertyBuffer;
         }
      }
   }
}
