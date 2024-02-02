package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.win32.W32APITypeMapper;

public interface Winevt {
   int EVT_VARIANT_TYPE_ARRAY = 128;
   int EVT_VARIANT_TYPE_MASK = 127;
   int EVT_READ_ACCESS = 1;
   int EVT_WRITE_ACCESS = 2;
   int EVT_ALL_ACCESS = 7;
   int EVT_CLEAR_ACCESS = 4;

   public static class EVT_HANDLE extends WinNT.HANDLE {
      public EVT_HANDLE() {
      }

      public EVT_HANDLE(Pointer p) {
         super(p);
      }
   }

   public interface EVT_EVENT_PROPERTY_ID {
      int EvtEventQueryIDs = 0;
      int EvtEventPath = 1;
      int EvtEventPropertyIdEND = 2;
   }

   public interface EVT_QUERY_PROPERTY_ID {
      int EvtQueryNames = 0;
      int EvtQueryStatuses = 1;
      int EvtQueryPropertyIdEND = 2;
   }

   public interface EVT_EVENT_METADATA_PROPERTY_ID {
      int EventMetadataEventID = 0;
      int EventMetadataEventVersion = 1;
      int EventMetadataEventChannel = 2;
      int EventMetadataEventLevel = 3;
      int EventMetadataEventOpcode = 4;
      int EventMetadataEventTask = 5;
      int EventMetadataEventKeyword = 6;
      int EventMetadataEventMessageID = 7;
      int EventMetadataEventTemplate = 8;
      int EvtEventMetadataPropertyIdEND = 9;
   }

   public interface EVT_PUBLISHER_METADATA_PROPERTY_ID {
      int EvtPublisherMetadataPublisherGuid = 0;
      int EvtPublisherMetadataResourceFilePath = 1;
      int EvtPublisherMetadataParameterFilePath = 2;
      int EvtPublisherMetadataMessageFilePath = 3;
      int EvtPublisherMetadataHelpLink = 4;
      int EvtPublisherMetadataPublisherMessageID = 5;
      int EvtPublisherMetadataChannelReferences = 6;
      int EvtPublisherMetadataChannelReferencePath = 7;
      int EvtPublisherMetadataChannelReferenceIndex = 8;
      int EvtPublisherMetadataChannelReferenceID = 9;
      int EvtPublisherMetadataChannelReferenceFlags = 10;
      int EvtPublisherMetadataChannelReferenceMessageID = 11;
      int EvtPublisherMetadataLevels = 12;
      int EvtPublisherMetadataLevelName = 13;
      int EvtPublisherMetadataLevelValue = 14;
      int EvtPublisherMetadataLevelMessageID = 15;
      int EvtPublisherMetadataTasks = 16;
      int EvtPublisherMetadataTaskName = 17;
      int EvtPublisherMetadataTaskEventGuid = 18;
      int EvtPublisherMetadataTaskValue = 19;
      int EvtPublisherMetadataTaskMessageID = 20;
      int EvtPublisherMetadataOpcodes = 21;
      int EvtPublisherMetadataOpcodeName = 22;
      int EvtPublisherMetadataOpcodeValue = 23;
      int EvtPublisherMetadataOpcodeMessageID = 24;
      int EvtPublisherMetadataKeywords = 25;
      int EvtPublisherMetadataKeywordName = 26;
      int EvtPublisherMetadataKeywordValue = 27;
      int EvtPublisherMetadataKeywordMessageID = 28;
      int EvtPublisherMetadataPropertyIdEND = 29;
   }

   public interface EVT_CHANNEL_REFERENCE_FLAGS {
      int EvtChannelReferenceImported = 1;
   }

   public interface EVT_CHANNEL_SID_TYPE {
      int EvtChannelSidTypeNone = 0;
      int EvtChannelSidTypePublishing = 1;
   }

   public interface EVT_CHANNEL_CLOCK_TYPE {
      int EvtChannelClockTypeSystemTime = 0;
      int EvtChannelClockTypeQPC = 1;
   }

   public interface EVT_CHANNEL_ISOLATION_TYPE {
      int EvtChannelIsolationTypeApplication = 0;
      int EvtChannelIsolationTypeSystem = 1;
      int EvtChannelIsolationTypeCustom = 2;
   }

   public interface EVT_CHANNEL_TYPE {
      int EvtChannelTypeAdmin = 0;
      int EvtChannelTypeOperational = 1;
      int EvtChannelTypeAnalytic = 2;
      int EvtChannelTypeDebug = 3;
   }

   public interface EVT_CHANNEL_CONFIG_PROPERTY_ID {
      int EvtChannelConfigEnabled = 0;
      int EvtChannelConfigIsolation = 1;
      int EvtChannelConfigType = 2;
      int EvtChannelConfigOwningPublisher = 3;
      int EvtChannelConfigClassicEventlog = 4;
      int EvtChannelConfigAccess = 5;
      int EvtChannelLoggingConfigRetention = 6;
      int EvtChannelLoggingConfigAutoBackup = 7;
      int EvtChannelLoggingConfigMaxSize = 8;
      int EvtChannelLoggingConfigLogFilePath = 9;
      int EvtChannelPublishingConfigLevel = 10;
      int EvtChannelPublishingConfigKeywords = 11;
      int EvtChannelPublishingConfigControlGuid = 12;
      int EvtChannelPublishingConfigBufferSize = 13;
      int EvtChannelPublishingConfigMinBuffers = 14;
      int EvtChannelPublishingConfigMaxBuffers = 15;
      int EvtChannelPublishingConfigLatency = 16;
      int EvtChannelPublishingConfigClockType = 17;
      int EvtChannelPublishingConfigSidType = 18;
      int EvtChannelPublisherList = 19;
      int EvtChannelPublishingConfigFileMax = 20;
      int EvtChannelConfigPropertyIdEND = 21;
   }

   public interface EVT_EXPORTLOG_FLAGS {
      int EvtExportLogChannelPath = 1;
      int EvtExportLogFilePath = 2;
      int EvtExportLogTolerateQueryErrors = 4096;
      int EvtExportLogOverwrite = 8192;
   }

   public interface EVT_LOG_PROPERTY_ID {
      int EvtLogCreationTime = 0;
      int EvtLogLastAccessTime = 1;
      int EvtLogLastWriteTime = 2;
      int EvtLogFileSize = 3;
      int EvtLogAttributes = 4;
      int EvtLogNumberOfLogRecords = 5;
      int EvtLogOldestRecordNumber = 6;
      int EvtLogFull = 7;
   }

   public interface EVT_OPEN_LOG_FLAGS {
      int EvtOpenChannelPath = 1;
      int EvtOpenFilePath = 2;
   }

   public interface EVT_FORMAT_MESSAGE_FLAGS {
      int EvtFormatMessageEvent = 1;
      int EvtFormatMessageLevel = 2;
      int EvtFormatMessageTask = 3;
      int EvtFormatMessageOpcode = 4;
      int EvtFormatMessageKeyword = 5;
      int EvtFormatMessageChannel = 6;
      int EvtFormatMessageProvider = 7;
      int EvtFormatMessageId = 8;
      int EvtFormatMessageXml = 9;
   }

   public interface EVT_RENDER_FLAGS {
      int EvtRenderEventValues = 0;
      int EvtRenderEventXml = 1;
      int EvtRenderBookmark = 2;
   }

   public interface EVT_RENDER_CONTEXT_FLAGS {
      int EvtRenderContextValues = 0;
      int EvtRenderContextSystem = 1;
      int EvtRenderContextUser = 2;
   }

   public interface EVT_SYSTEM_PROPERTY_ID {
      int EvtSystemProviderName = 0;
      int EvtSystemProviderGuid = 1;
      int EvtSystemEventID = 2;
      int EvtSystemQualifiers = 3;
      int EvtSystemLevel = 4;
      int EvtSystemTask = 5;
      int EvtSystemOpcode = 6;
      int EvtSystemKeywords = 7;
      int EvtSystemTimeCreated = 8;
      int EvtSystemEventRecordId = 9;
      int EvtSystemActivityID = 10;
      int EvtSystemRelatedActivityID = 11;
      int EvtSystemProcessID = 12;
      int EvtSystemThreadID = 13;
      int EvtSystemChannel = 14;
      int EvtSystemComputer = 15;
      int EvtSystemUserID = 16;
      int EvtSystemVersion = 17;
      int EvtSystemPropertyIdEND = 18;
   }

   public interface EVT_SUBSCRIBE_NOTIFY_ACTION {
      int EvtSubscribeActionError = 0;
      int EvtSubscribeActionDeliver = 1;
   }

   public interface EVT_SUBSCRIBE_FLAGS {
      int EvtSubscribeToFutureEvents = 1;
      int EvtSubscribeStartAtOldestRecord = 2;
      int EvtSubscribeStartAfterBookmark = 3;
      int EvtSubscribeOriginMask = 3;
      int EvtSubscribeTolerateQueryErrors = 4096;
      int EvtSubscribeStrict = 65536;
   }

   public interface EVT_SEEK_FLAGS {
      int EvtSeekRelativeToFirst = 1;
      int EvtSeekRelativeToLast = 2;
      int EvtSeekRelativeToCurrent = 3;
      int EvtSeekRelativeToBookmark = 4;
      int EvtSeekOriginMask = 7;
      int EvtSeekStrict = 65536;
   }

   public interface EVT_QUERY_FLAGS {
      int EvtQueryChannelPath = 1;
      int EvtQueryFilePath = 2;
      int EvtQueryForwardDirection = 256;
      int EvtQueryReverseDirection = 512;
      int EvtQueryTolerateQueryErrors = 4096;
   }

   @Structure.FieldOrder({"Server", "User", "Domain", "Password", "Flags"})
   public static class EVT_RPC_LOGIN extends Structure {
      public String Server;
      public String User;
      public String Domain;
      public String Password;
      public int Flags;

      public EVT_RPC_LOGIN() {
         super(W32APITypeMapper.UNICODE);
      }

      public EVT_RPC_LOGIN(String Server, String User, String Domain, String Password, int Flags) {
         super(W32APITypeMapper.UNICODE);
         this.Server = Server;
         this.User = User;
         this.Domain = Domain;
         this.Password = Password;
         this.Flags = Flags;
      }

      public EVT_RPC_LOGIN(Pointer peer) {
         super(peer, 0, W32APITypeMapper.UNICODE);
      }

      public static class ByValue extends EVT_RPC_LOGIN implements Structure.ByValue {
      }

      public static class ByReference extends EVT_RPC_LOGIN implements Structure.ByReference {
      }
   }

   public interface EVT_RPC_LOGIN_FLAGS {
      int EvtRpcLoginAuthDefault = 0;
      int EvtRpcLoginAuthNegotiate = 1;
      int EvtRpcLoginAuthKerberos = 2;
      int EvtRpcLoginAuthNTLM = 3;
   }

   @Structure.FieldOrder({"field1", "Count", "Type"})
   public static class EVT_VARIANT extends Structure {
      public field1_union field1;
      public int Count;
      public int Type;
      private Object holder;

      public EVT_VARIANT() {
         super(W32APITypeMapper.DEFAULT);
      }

      public EVT_VARIANT(Pointer peer) {
         super(peer, 0, W32APITypeMapper.DEFAULT);
      }

      public void use(Pointer m) {
         this.useMemory(m, 0);
      }

      private int getBaseType() {
         return this.Type & 127;
      }

      public boolean isArray() {
         return (this.Type & 128) == 128;
      }

      public EVT_VARIANT_TYPE getVariantType() {
         return Winevt.EVT_VARIANT_TYPE.values()[this.getBaseType()];
      }

      public void setValue(EVT_VARIANT_TYPE type, Object value) {
         this.allocateMemory();
         if (type == null) {
            throw new IllegalArgumentException("setValue must not be called with type set to NULL");
         } else {
            this.holder = null;
            if (value != null && type != Winevt.EVT_VARIANT_TYPE.EvtVarTypeNull) {
               Memory mem;
               StringArray sa;
               switch (type) {
                  case EvtVarTypeAnsiString:
                     if (value.getClass().isArray() && value.getClass().getComponentType() == String.class) {
                        this.Type = type.ordinal() | 128;
                        sa = new StringArray((String[])((String[])value), false);
                        this.holder = sa;
                        this.Count = ((String[])((String[])value)).length;
                        this.field1.writeField("pointerValue", sa);
                     } else {
                        if (value.getClass() != String.class) {
                           throw new IllegalArgumentException(type.name() + " must be set from String/String[]");
                        }

                        this.Type = type.ordinal();
                        mem = new Memory((long)(((String)value).length() + 1));
                        mem.setString(0L, (String)value);
                        this.holder = mem;
                        this.Count = 0;
                        this.field1.writeField("pointerValue", mem);
                     }
                     break;
                  case EvtVarTypeBoolean:
                     if (value.getClass().isArray() && value.getClass().getComponentType() == WinDef.BOOL.class) {
                        this.Type = type.ordinal() | 128;
                        mem = new Memory((long)(((WinDef.BOOL[])((WinDef.BOOL[])value)).length * 4));

                        for(int i = 0; i < ((WinDef.BOOL[])((WinDef.BOOL[])value)).length; ++i) {
                           mem.setInt((long)(i * 4), ((WinDef.BOOL[])((WinDef.BOOL[])value))[i].intValue());
                        }

                        this.holder = mem;
                        this.Count = 0;
                        this.field1.writeField("pointerValue", mem);
                     } else {
                        if (value.getClass() != WinDef.BOOL.class) {
                           throw new IllegalArgumentException(type.name() + " must be set from BOOL/BOOL[]");
                        }

                        this.Type = type.ordinal();
                        this.Count = 0;
                        this.field1.writeField("intValue", ((WinDef.BOOL)value).intValue());
                     }
                     break;
                  case EvtVarTypeString:
                  case EvtVarTypeEvtXml:
                     if (value.getClass().isArray() && value.getClass().getComponentType() == String.class) {
                        this.Type = type.ordinal() | 128;
                        sa = new StringArray((String[])((String[])value), true);
                        this.holder = sa;
                        this.Count = ((String[])((String[])value)).length;
                        this.field1.writeField("pointerValue", sa);
                     } else {
                        if (value.getClass() != String.class) {
                           throw new IllegalArgumentException(type.name() + " must be set from String/String[]");
                        }

                        this.Type = type.ordinal();
                        mem = new Memory((long)((((String)value).length() + 1) * 2));
                        mem.setWideString(0L, (String)value);
                        this.holder = mem;
                        this.Count = 0;
                        this.field1.writeField("pointerValue", mem);
                     }
                     break;
                  case EvtVarTypeSByte:
                  case EvtVarTypeByte:
                     if (value.getClass().isArray() && value.getClass().getComponentType() == Byte.TYPE) {
                        this.Type = type.ordinal() | 128;
                        mem = new Memory((long)(((byte[])((byte[])value)).length * 1));
                        mem.write(0L, (byte[])((byte[])((byte[])value)), 0, ((byte[])((byte[])value)).length);
                        this.holder = mem;
                        this.Count = 0;
                        this.field1.writeField("pointerValue", mem);
                     } else {
                        if (value.getClass() != Byte.TYPE) {
                           throw new IllegalArgumentException(type.name() + " must be set from byte/byte[]");
                        }

                        this.Type = type.ordinal();
                        this.Count = 0;
                        this.field1.writeField("byteValue", value);
                     }
                     break;
                  case EvtVarTypeInt16:
                  case EvtVarTypeUInt16:
                     if (value.getClass().isArray() && value.getClass().getComponentType() == Short.TYPE) {
                        this.Type = type.ordinal() | 128;
                        mem = new Memory((long)(((short[])((short[])value)).length * 2));
                        mem.write(0L, (short[])((short[])((short[])value)), 0, ((short[])((short[])value)).length);
                        this.holder = mem;
                        this.Count = 0;
                        this.field1.writeField("pointerValue", mem);
                     } else {
                        if (value.getClass() != Short.TYPE) {
                           throw new IllegalArgumentException(type.name() + " must be set from short/short[]");
                        }

                        this.Type = type.ordinal();
                        this.Count = 0;
                        this.field1.writeField("shortValue", value);
                     }
                     break;
                  case EvtVarTypeHexInt32:
                  case EvtVarTypeInt32:
                  case EvtVarTypeUInt32:
                     if (value.getClass().isArray() && value.getClass().getComponentType() == Integer.TYPE) {
                        this.Type = type.ordinal() | 128;
                        mem = new Memory((long)(((int[])((int[])value)).length * 4));
                        mem.write(0L, (int[])((int[])((int[])value)), 0, ((int[])((int[])value)).length);
                        this.holder = mem;
                        this.Count = 0;
                        this.field1.writeField("pointerValue", mem);
                     } else {
                        if (value.getClass() != Integer.TYPE) {
                           throw new IllegalArgumentException(type.name() + " must be set from int/int[]");
                        }

                        this.Type = type.ordinal();
                        this.Count = 0;
                        this.field1.writeField("intValue", value);
                     }
                     break;
                  case EvtVarTypeHexInt64:
                  case EvtVarTypeInt64:
                  case EvtVarTypeUInt64:
                     if (value.getClass().isArray() && value.getClass().getComponentType() == Long.TYPE) {
                        this.Type = type.ordinal() | 128;
                        mem = new Memory((long)(((long[])((long[])value)).length * 4));
                        mem.write(0L, (long[])((long[])((long[])value)), 0, ((long[])((long[])value)).length);
                        this.holder = mem;
                        this.Count = 0;
                        this.field1.writeField("pointerValue", mem);
                     } else {
                        if (value.getClass() != Long.TYPE) {
                           throw new IllegalArgumentException(type.name() + " must be set from long/long[]");
                        }

                        this.Type = type.ordinal();
                        this.Count = 0;
                        this.field1.writeField("longValue", value);
                     }
                     break;
                  case EvtVarTypeSingle:
                     if (value.getClass().isArray() && value.getClass().getComponentType() == Float.TYPE) {
                        this.Type = type.ordinal() | 128;
                        mem = new Memory((long)(((float[])((float[])value)).length * 4));
                        mem.write(0L, (float[])((float[])((float[])value)), 0, ((float[])((float[])value)).length);
                        this.holder = mem;
                        this.Count = 0;
                        this.field1.writeField("pointerValue", mem);
                     } else {
                        if (value.getClass() != Float.TYPE) {
                           throw new IllegalArgumentException(type.name() + " must be set from float/float[]");
                        }

                        this.Type = type.ordinal();
                        this.Count = 0;
                        this.field1.writeField("floatValue", value);
                     }
                     break;
                  case EvtVarTypeDouble:
                     if (value.getClass().isArray() && value.getClass().getComponentType() == Double.TYPE) {
                        this.Type = type.ordinal() | 128;
                        mem = new Memory((long)(((double[])((double[])value)).length * 4));
                        mem.write(0L, (double[])((double[])((double[])value)), 0, ((double[])((double[])value)).length);
                        this.holder = mem;
                        this.Count = 0;
                        this.field1.writeField("pointerValue", mem);
                     } else {
                        if (value.getClass() != Double.TYPE) {
                           throw new IllegalArgumentException(type.name() + " must be set from double/double[]");
                        }

                        this.Type = type.ordinal();
                        this.Count = 0;
                        this.field1.writeField("doubleVal", value);
                     }
                     break;
                  case EvtVarTypeBinary:
                     if (!value.getClass().isArray() || value.getClass().getComponentType() != Byte.TYPE) {
                        throw new IllegalArgumentException(type.name() + " must be set from byte[]");
                     }

                     this.Type = type.ordinal();
                     mem = new Memory((long)(((byte[])((byte[])value)).length * 1));
                     mem.write(0L, (byte[])((byte[])((byte[])value)), 0, ((byte[])((byte[])value)).length);
                     this.holder = mem;
                     this.Count = 0;
                     this.field1.writeField("pointerValue", mem);
                     break;
                  case EvtVarTypeFileTime:
                  case EvtVarTypeEvtHandle:
                  case EvtVarTypeSysTime:
                  case EvtVarTypeGuid:
                  case EvtVarTypeSid:
                  case EvtVarTypeSizeT:
                  default:
                     throw new IllegalStateException(String.format("NOT IMPLEMENTED: getValue(%s) (Array: %b, Count: %d)", type, this.isArray(), this.Count));
               }
            } else {
               this.Type = Winevt.EVT_VARIANT_TYPE.EvtVarTypeNull.ordinal();
               this.Count = 0;
               this.field1.writeField("pointerValue", Pointer.NULL);
            }

            this.write();
         }
      }

      public Object getValue() {
         EVT_VARIANT_TYPE type = this.getVariantType();
         int i;
         switch (type) {
            case EvtVarTypeAnsiString:
               return this.isArray() ? this.field1.getPointer().getPointer(0L).getStringArray(0L, this.Count) : this.field1.getPointer().getPointer(0L).getString(0L);
            case EvtVarTypeBoolean:
               if (!this.isArray()) {
                  return new WinDef.BOOL((long)this.field1.getPointer().getInt(0L));
               }

               int[] rawValue = this.field1.getPointer().getPointer(0L).getIntArray(0L, this.Count);
               WinDef.BOOL[] result = new WinDef.BOOL[rawValue.length];

               for(i = 0; i < result.length; ++i) {
                  result[i] = new WinDef.BOOL((long)rawValue[i]);
               }

               return result;
            case EvtVarTypeString:
            case EvtVarTypeEvtXml:
               return this.isArray() ? this.field1.getPointer().getPointer(0L).getWideStringArray(0L, this.Count) : this.field1.getPointer().getPointer(0L).getWideString(0L);
            case EvtVarTypeSByte:
            case EvtVarTypeByte:
               return this.isArray() ? this.field1.getPointer().getPointer(0L).getByteArray(0L, this.Count) : this.field1.getPointer().getByte(0L);
            case EvtVarTypeInt16:
            case EvtVarTypeUInt16:
               return this.isArray() ? this.field1.getPointer().getPointer(0L).getShortArray(0L, this.Count) : this.field1.getPointer().getShort(0L);
            case EvtVarTypeHexInt32:
            case EvtVarTypeInt32:
            case EvtVarTypeUInt32:
               return this.isArray() ? this.field1.getPointer().getPointer(0L).getIntArray(0L, this.Count) : this.field1.getPointer().getInt(0L);
            case EvtVarTypeHexInt64:
            case EvtVarTypeInt64:
            case EvtVarTypeUInt64:
               return this.isArray() ? this.field1.getPointer().getPointer(0L).getLongArray(0L, this.Count) : this.field1.getPointer().getLong(0L);
            case EvtVarTypeSingle:
               return this.isArray() ? this.field1.getPointer().getPointer(0L).getFloatArray(0L, this.Count) : this.field1.getPointer().getFloat(0L);
            case EvtVarTypeDouble:
               return this.isArray() ? this.field1.getPointer().getPointer(0L).getDoubleArray(0L, this.Count) : this.field1.getPointer().getDouble(0L);
            case EvtVarTypeBinary:
               assert !this.isArray();

               return this.field1.getPointer().getPointer(0L).getByteArray(0L, this.Count);
            case EvtVarTypeFileTime:
               WinBase.FILETIME result;
               if (this.isArray()) {
                  result = (WinBase.FILETIME)Structure.newInstance(WinBase.FILETIME.class, this.field1.getPointer().getPointer(0L));
                  result.read();
                  return result.toArray(this.Count);
               }

               result = new WinBase.FILETIME(this.field1.getPointer());
               result.read();
               return result;
            case EvtVarTypeEvtHandle:
               if (!this.isArray()) {
                  return new WinNT.HANDLE(this.field1.getPointer().getPointer(0L));
               }

               Pointer[] rawValue = this.field1.getPointer().getPointer(0L).getPointerArray(0L, this.Count);
               WinNT.HANDLE[] result = new WinNT.HANDLE[rawValue.length];

               for(i = 0; i < result.length; ++i) {
                  result[i] = new WinNT.HANDLE(rawValue[i]);
               }

               return result;
            case EvtVarTypeSysTime:
               WinBase.SYSTEMTIME result;
               if (this.isArray()) {
                  result = (WinBase.SYSTEMTIME)Structure.newInstance(WinBase.SYSTEMTIME.class, this.field1.getPointer().getPointer(0L));
                  result.read();
                  return result.toArray(this.Count);
               }

               result = (WinBase.SYSTEMTIME)Structure.newInstance(WinBase.SYSTEMTIME.class, this.field1.getPointer().getPointer(0L));
               result.read();
               return result;
            case EvtVarTypeGuid:
               Guid.GUID result;
               if (this.isArray()) {
                  result = (Guid.GUID)Structure.newInstance(Guid.GUID.class, this.field1.getPointer().getPointer(0L));
                  result.read();
                  return result.toArray(this.Count);
               }

               result = (Guid.GUID)Structure.newInstance(Guid.GUID.class, this.field1.getPointer().getPointer(0L));
               result.read();
               return result;
            case EvtVarTypeSid:
               WinNT.PSID result;
               if (this.isArray()) {
                  result = (WinNT.PSID)Structure.newInstance(WinNT.PSID.class, this.field1.getPointer().getPointer(0L));
                  result.read();
                  return result.toArray(this.Count);
               }

               result = (WinNT.PSID)Structure.newInstance(WinNT.PSID.class, this.field1.getPointer().getPointer(0L));
               result.read();
               return result;
            case EvtVarTypeSizeT:
               if (!this.isArray()) {
                  return new BaseTSD.SIZE_T(this.field1.getPointer().getLong(0L));
               }

               long[] rawValue = this.field1.getPointer().getPointer(0L).getLongArray(0L, this.Count);
               BaseTSD.SIZE_T[] result = new BaseTSD.SIZE_T[rawValue.length];

               for(i = 0; i < result.length; ++i) {
                  result[i] = new BaseTSD.SIZE_T(rawValue[i]);
               }

               return result;
            case EvtVarTypeNull:
               return null;
            default:
               throw new IllegalStateException(String.format("NOT IMPLEMENTED: getValue(%s) (Array: %b, Count: %d)", type, this.isArray(), this.Count));
         }
      }

      public static class ByValue extends EVT_VARIANT implements Structure.ByValue {
         public ByValue(Pointer p) {
            super(p);
         }

         public ByValue() {
         }
      }

      public static class ByReference extends EVT_VARIANT implements Structure.ByReference {
         public ByReference(Pointer p) {
            super(p);
         }

         public ByReference() {
         }
      }

      public static class field1_union extends Union {
         public byte byteValue;
         public short shortValue;
         public int intValue;
         public long longValue;
         public float floatValue;
         public double doubleVal;
         public Pointer pointerValue;
      }
   }

   public interface EVT_LOGIN_CLASS {
      int EvtRpcLogin = 1;
   }

   public static enum EVT_VARIANT_TYPE {
      EvtVarTypeNull(""),
      EvtVarTypeString("String"),
      EvtVarTypeAnsiString("AnsiString"),
      EvtVarTypeSByte("SByte"),
      EvtVarTypeByte("Byte"),
      EvtVarTypeInt16("Int16"),
      EvtVarTypeUInt16("UInt16"),
      EvtVarTypeInt32("Int32"),
      EvtVarTypeUInt32("UInt32"),
      EvtVarTypeInt64("Int64"),
      EvtVarTypeUInt64("UInt64"),
      EvtVarTypeSingle("Single"),
      EvtVarTypeDouble("Double"),
      EvtVarTypeBoolean("Boolean"),
      EvtVarTypeBinary("Binary"),
      EvtVarTypeGuid("Guid"),
      EvtVarTypeSizeT("SizeT"),
      EvtVarTypeFileTime("FileTime"),
      EvtVarTypeSysTime("SysTime"),
      EvtVarTypeSid("Sid"),
      EvtVarTypeHexInt32("Int32"),
      EvtVarTypeHexInt64("Int64"),
      EvtVarTypeEvtHandle("EvtHandle"),
      EvtVarTypeEvtXml("Xml");

      private final String field;

      private EVT_VARIANT_TYPE(String field) {
         this.field = field;
      }

      public String getField() {
         return this.field.isEmpty() ? "" : this.field + "Val";
      }

      public String getArrField() {
         return this.field.isEmpty() ? "" : this.field + "Arr";
      }
   }
}
