package io.undertow.protocols.http2;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.ParseTimeoutUpdater;
import io.undertow.server.protocol.framed.AbstractFramedChannel;
import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;
import io.undertow.server.protocol.framed.FrameHeaderData;
import io.undertow.util.Attachable;
import io.undertow.util.AttachmentKey;
import io.undertow.util.AttachmentList;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import javax.net.ssl.SSLSession;
import org.xnio.Bits;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.ssl.SslConnection;

public class Http2Channel extends AbstractFramedChannel<Http2Channel, AbstractHttp2StreamSourceChannel, AbstractHttp2StreamSinkChannel> implements Attachable {
   public static final String CLEARTEXT_UPGRADE_STRING = "h2c";
   public static final HttpString METHOD = new HttpString(":method");
   public static final HttpString PATH = new HttpString(":path");
   public static final HttpString SCHEME = new HttpString(":scheme");
   public static final HttpString AUTHORITY = new HttpString(":authority");
   public static final HttpString STATUS = new HttpString(":status");
   static final int FRAME_TYPE_DATA = 0;
   static final int FRAME_TYPE_HEADERS = 1;
   static final int FRAME_TYPE_PRIORITY = 2;
   static final int FRAME_TYPE_RST_STREAM = 3;
   static final int FRAME_TYPE_SETTINGS = 4;
   static final int FRAME_TYPE_PUSH_PROMISE = 5;
   static final int FRAME_TYPE_PING = 6;
   static final int FRAME_TYPE_GOAWAY = 7;
   static final int FRAME_TYPE_WINDOW_UPDATE = 8;
   static final int FRAME_TYPE_CONTINUATION = 9;
   public static final int ERROR_NO_ERROR = 0;
   public static final int ERROR_PROTOCOL_ERROR = 1;
   public static final int ERROR_INTERNAL_ERROR = 2;
   public static final int ERROR_FLOW_CONTROL_ERROR = 3;
   public static final int ERROR_SETTINGS_TIMEOUT = 4;
   public static final int ERROR_STREAM_CLOSED = 5;
   public static final int ERROR_FRAME_SIZE_ERROR = 6;
   public static final int ERROR_REFUSED_STREAM = 7;
   public static final int ERROR_CANCEL = 8;
   public static final int ERROR_COMPRESSION_ERROR = 9;
   public static final int ERROR_CONNECT_ERROR = 10;
   public static final int ERROR_ENHANCE_YOUR_CALM = 11;
   public static final int ERROR_INADEQUATE_SECURITY = 12;
   static final int DATA_FLAG_END_STREAM = 1;
   static final int DATA_FLAG_END_SEGMENT = 2;
   static final int DATA_FLAG_PADDED = 8;
   static final int PING_FRAME_LENGTH = 8;
   static final int PING_FLAG_ACK = 1;
   static final int HEADERS_FLAG_END_STREAM = 1;
   static final int HEADERS_FLAG_END_SEGMENT = 2;
   static final int HEADERS_FLAG_END_HEADERS = 4;
   static final int HEADERS_FLAG_PADDED = 8;
   static final int HEADERS_FLAG_PRIORITY = 32;
   static final int SETTINGS_FLAG_ACK = 1;
   static final int CONTINUATION_FLAG_END_HEADERS = 4;
   public static final int DEFAULT_INITIAL_WINDOW_SIZE = 65535;
   static final byte[] PREFACE_BYTES = new byte[]{80, 82, 73, 32, 42, 32, 72, 84, 84, 80, 47, 50, 46, 48, 13, 10, 13, 10, 83, 77, 13, 10, 13, 10};
   public static final int DEFAULT_MAX_FRAME_SIZE = 16384;
   public static final int MAX_FRAME_SIZE = 16777215;
   public static final int FLOW_CONTROL_MIN_WINDOW = 2;
   private Http2FrameHeaderParser frameParser;
   private final Map<Integer, StreamHolder> currentStreams;
   private final String protocol;
   private final int encoderHeaderTableSize;
   private volatile boolean pushEnabled;
   private volatile int sendMaxConcurrentStreams;
   private final int receiveMaxConcurrentStreams;
   private volatile int sendConcurrentStreams;
   private volatile int receiveConcurrentStreams;
   private final int initialReceiveWindowSize;
   private volatile int sendMaxFrameSize;
   private final int receiveMaxFrameSize;
   private int unackedReceiveMaxFrameSize;
   private final int maxHeaders;
   private final int maxHeaderListSize;
   private static final AtomicIntegerFieldUpdater<Http2Channel> sendConcurrentStreamsAtomicUpdater = AtomicIntegerFieldUpdater.newUpdater(Http2Channel.class, "sendConcurrentStreams");
   private static final AtomicIntegerFieldUpdater<Http2Channel> receiveConcurrentStreamsAtomicUpdater = AtomicIntegerFieldUpdater.newUpdater(Http2Channel.class, "receiveConcurrentStreams");
   private boolean thisGoneAway;
   private boolean peerGoneAway;
   private boolean lastDataRead;
   private int streamIdCounter;
   private int lastGoodStreamId;
   private int lastAssignedStreamOtherSide;
   private final HpackDecoder decoder;
   private final HpackEncoder encoder;
   private final int maxPadding;
   private final Random paddingRandom;
   private int prefaceCount;
   private boolean initialSettingsReceived;
   private Http2HeadersParser continuationParser;
   private boolean initialSettingsSent;
   private final Map<AttachmentKey<?>, Object> attachments;
   private final ParseTimeoutUpdater parseTimeoutUpdater;
   private final Object flowControlLock;
   private volatile int initialSendWindowSize;
   private volatile long sendWindowSize;
   private volatile int receiveWindowSize;

   public Http2Channel(StreamConnection connectedStreamChannel, String protocol, ByteBufferPool bufferPool, PooledByteBuffer data, boolean clientSide, boolean fromUpgrade, OptionMap settings) {
      this(connectedStreamChannel, protocol, bufferPool, data, clientSide, fromUpgrade, true, (ByteBuffer)null, settings);
   }

   public Http2Channel(StreamConnection connectedStreamChannel, String protocol, ByteBufferPool bufferPool, PooledByteBuffer data, boolean clientSide, boolean fromUpgrade, boolean prefaceRequired, OptionMap settings) {
      this(connectedStreamChannel, protocol, bufferPool, data, clientSide, fromUpgrade, prefaceRequired, (ByteBuffer)null, settings);
   }

   public Http2Channel(StreamConnection connectedStreamChannel, String protocol, ByteBufferPool bufferPool, PooledByteBuffer data, boolean clientSide, boolean fromUpgrade, boolean prefaceRequired, ByteBuffer initialOtherSideSettings, OptionMap settings) {
      super(connectedStreamChannel, bufferPool, new Http2FramePriority(clientSide ? (fromUpgrade ? 3 : 1) : 2), data, settings);
      this.currentStreams = new ConcurrentHashMap();
      this.sendMaxConcurrentStreams = -1;
      this.sendConcurrentStreams = 0;
      this.receiveConcurrentStreams = 0;
      this.sendMaxFrameSize = 16384;
      this.unackedReceiveMaxFrameSize = 16384;
      this.thisGoneAway = false;
      this.peerGoneAway = false;
      this.lastDataRead = false;
      this.continuationParser = null;
      this.initialSettingsSent = false;
      this.attachments = Collections.synchronizedMap(new HashMap());
      this.flowControlLock = new Object();
      this.initialSendWindowSize = 65535;
      this.sendWindowSize = (long)this.initialSendWindowSize;
      this.streamIdCounter = clientSide ? (fromUpgrade ? 3 : 1) : 2;
      this.pushEnabled = settings.get(UndertowOptions.HTTP2_SETTINGS_ENABLE_PUSH, true);
      this.initialReceiveWindowSize = settings.get(UndertowOptions.HTTP2_SETTINGS_INITIAL_WINDOW_SIZE, 65535);
      this.receiveWindowSize = this.initialReceiveWindowSize;
      this.receiveMaxConcurrentStreams = settings.get(UndertowOptions.HTTP2_SETTINGS_MAX_CONCURRENT_STREAMS, -1);
      this.protocol = protocol == null ? "h2" : protocol;
      this.maxHeaders = settings.get(UndertowOptions.MAX_HEADERS, clientSide ? -1 : 200);
      this.encoderHeaderTableSize = settings.get(UndertowOptions.HTTP2_SETTINGS_HEADER_TABLE_SIZE, 4096);
      this.receiveMaxFrameSize = settings.get(UndertowOptions.HTTP2_SETTINGS_MAX_FRAME_SIZE, 16384);
      this.maxPadding = settings.get(UndertowOptions.HTTP2_PADDING_SIZE, 0);
      this.maxHeaderListSize = settings.get(UndertowOptions.HTTP2_SETTINGS_MAX_HEADER_LIST_SIZE, settings.get(UndertowOptions.MAX_HEADER_SIZE, -1));
      if (this.maxPadding > 0) {
         this.paddingRandom = new SecureRandom();
      } else {
         this.paddingRandom = null;
      }

      this.decoder = new HpackDecoder(this.encoderHeaderTableSize);
      this.encoder = new HpackEncoder(this.encoderHeaderTableSize);
      if (!prefaceRequired) {
         this.prefaceCount = PREFACE_BYTES.length;
      }

      if (clientSide) {
         this.sendPreface();
         this.prefaceCount = PREFACE_BYTES.length;
         this.sendSettings();
         this.initialSettingsSent = true;
         if (fromUpgrade) {
            StreamHolder streamHolder = new StreamHolder((Http2StreamSinkChannel)null);
            streamHolder.sinkClosed = true;
            sendConcurrentStreamsAtomicUpdater.getAndIncrement(this);
            this.currentStreams.put(1, streamHolder);
         }
      } else if (fromUpgrade) {
         this.sendSettings();
         this.initialSettingsSent = true;
      }

      if (initialOtherSideSettings != null) {
         Http2SettingsParser parser = new Http2SettingsParser(initialOtherSideSettings.remaining());

         try {
            Http2FrameHeaderParser headerParser = new Http2FrameHeaderParser(this, (Http2HeadersParser)null);
            headerParser.length = initialOtherSideSettings.remaining();
            parser.parse(initialOtherSideSettings, headerParser);
            this.updateSettings(parser.getSettings());
         } catch (Throwable var12) {
            IoUtils.safeClose((Closeable)connectedStreamChannel);
            throw new RuntimeException(var12);
         }
      }

      int requestParseTimeout = settings.get(UndertowOptions.REQUEST_PARSE_TIMEOUT, -1);
      int requestIdleTimeout = settings.get(UndertowOptions.NO_REQUEST_TIMEOUT, -1);
      if (requestIdleTimeout < 0 && requestParseTimeout < 0) {
         this.parseTimeoutUpdater = null;
      } else {
         this.parseTimeoutUpdater = new ParseTimeoutUpdater(this, (long)requestParseTimeout, (long)requestIdleTimeout, new Runnable() {
            public void run() {
               Http2Channel.this.sendGoAway(0);
               Http2Channel.this.getIoThread().executeAfter(new Runnable() {
                  public void run() {
                     IoUtils.safeClose((Closeable)Http2Channel.this);
                  }
               }, 2L, TimeUnit.SECONDS);
            }
         });
         this.addCloseTask(new ChannelListener<Http2Channel>() {
            public void handleEvent(Http2Channel channel) {
               Http2Channel.this.parseTimeoutUpdater.close();
            }
         });
      }

   }

   private void sendSettings() {
      List<Http2Setting> settings = new ArrayList();
      settings.add(new Http2Setting(1, (long)this.encoderHeaderTableSize));
      if (this.isClient()) {
         settings.add(new Http2Setting(2, this.pushEnabled ? 1L : 0L));
      }

      settings.add(new Http2Setting(5, (long)this.receiveMaxFrameSize));
      settings.add(new Http2Setting(4, (long)this.initialReceiveWindowSize));
      if (this.maxHeaderListSize > 0) {
         settings.add(new Http2Setting(6, (long)this.maxHeaderListSize));
      }

      if (this.receiveMaxConcurrentStreams > 0) {
         settings.add(new Http2Setting(3, (long)this.receiveMaxConcurrentStreams));
      }

      Http2SettingsStreamSinkChannel stream = new Http2SettingsStreamSinkChannel(this, settings);
      this.flushChannelIgnoreFailure(stream);
   }

   private void sendSettingsAck() {
      if (!this.initialSettingsSent) {
         this.sendSettings();
         this.initialSettingsSent = true;
      }

      Http2SettingsStreamSinkChannel stream = new Http2SettingsStreamSinkChannel(this);
      this.flushChannelIgnoreFailure(stream);
   }

   private void flushChannelIgnoreFailure(StreamSinkChannel stream) {
      try {
         this.flushChannel(stream);
      } catch (IOException var3) {
         UndertowLogger.REQUEST_IO_LOGGER.ioException(var3);
      } catch (Throwable var4) {
         UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var4);
      }

   }

   private void flushChannel(StreamSinkChannel stream) throws IOException {
      stream.shutdownWrites();
      if (!stream.flush()) {
         stream.getWriteSetter().set(ChannelListeners.flushingChannelListener((ChannelListener)null, this.writeExceptionHandler()));
         stream.resumeWrites();
      }

   }

   private void sendPreface() {
      Http2PrefaceStreamSinkChannel preface = new Http2PrefaceStreamSinkChannel(this);
      this.flushChannelIgnoreFailure(preface);
   }

   protected AbstractHttp2StreamSourceChannel createChannel(FrameHeaderData frameHeaderData, PooledByteBuffer frameData) throws IOException {
      AbstractHttp2StreamSourceChannel channel = this.createChannelImpl(frameHeaderData, frameData);
      if (channel instanceof Http2StreamSourceChannel && this.parseTimeoutUpdater != null) {
         if (channel != null) {
            this.parseTimeoutUpdater.requestStarted();
         } else if (this.currentStreams.isEmpty()) {
            this.parseTimeoutUpdater.failedParse();
         }
      }

      return channel;
   }

   protected AbstractHttp2StreamSourceChannel createChannelImpl(FrameHeaderData frameHeaderData, PooledByteBuffer frameData) throws IOException {
      Http2FrameHeaderParser frameParser = (Http2FrameHeaderParser)frameHeaderData;
      if (frameParser.type == 0) {
         this.sendGoAway(1);
         UndertowLogger.REQUEST_LOGGER.tracef("Dropping Frame of length %s for stream %s", frameParser.getFrameLength(), (long)frameParser.streamId);
         return null;
      } else {
         Object channel;
         switch (frameParser.type) {
            case 2:
               Http2PriorityParser parser = (Http2PriorityParser)frameParser.parser;
               if (parser.getStreamDependency() == frameParser.streamId) {
                  this.sendRstStream(frameParser.streamId, 1);
                  return null;
               } else {
                  frameData.close();
                  return null;
               }
            case 3:
               Http2RstStreamParser parser = (Http2RstStreamParser)frameParser.parser;
               if (frameParser.streamId == 0) {
                  if (frameData != null) {
                     frameData.close();
                  }

                  throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.streamIdMustNotBeZeroForFrameType(3));
               }

               channel = new Http2RstStreamStreamSourceChannel(this, frameData, parser.getErrorCode(), frameParser.streamId);
               this.handleRstStream(frameParser.streamId);
               if (this.isIdle(frameParser.streamId)) {
                  this.sendGoAway(1);
               }
               break;
            case 4:
               if (!Bits.anyAreSet(frameParser.flags, 1)) {
                  if (this.updateSettings(((Http2SettingsParser)frameParser.parser).getSettings())) {
                     this.sendSettingsAck();
                  }
               } else if (frameHeaderData.getFrameLength() != 0L) {
                  this.sendGoAway(6);
                  frameData.close();
                  return null;
               }

               channel = new Http2SettingsStreamSourceChannel(this, frameData, frameParser.getFrameLength(), ((Http2SettingsParser)frameParser.parser).getSettings());
               this.unackedReceiveMaxFrameSize = this.receiveMaxFrameSize;
               break;
            case 5:
            case 9:
               if (frameParser.parser instanceof Http2PushPromiseParser) {
                  if (!this.isClient()) {
                     this.sendGoAway(1);
                     throw UndertowMessages.MESSAGES.serverReceivedPushPromise();
                  }

                  Http2PushPromiseParser pushPromiseParser = (Http2PushPromiseParser)frameParser.parser;
                  channel = new Http2PushPromiseStreamSourceChannel(this, frameData, frameParser.getFrameLength(), pushPromiseParser.getHeaderMap(), pushPromiseParser.getPromisedStreamId(), frameParser.streamId);
                  break;
               }
            case 1:
               if (!this.isIdle(frameParser.streamId)) {
                  StreamHolder existing = (StreamHolder)this.currentStreams.get(frameParser.streamId);
                  if (existing == null || existing.sourceClosed) {
                     this.sendGoAway(1);
                     frameData.close();
                     return null;
                  }

                  if (existing.sourceChannel != null && !Bits.allAreSet(frameParser.flags, 1)) {
                     this.sendGoAway(1);
                     frameData.close();
                     return null;
                  }
               } else {
                  if (frameParser.streamId < this.getLastAssignedStreamOtherSide()) {
                     this.sendGoAway(1);
                     frameData.close();
                     return null;
                  }

                  if (frameParser.streamId % 2 == (this.isClient() ? 1 : 0)) {
                     this.sendGoAway(1);
                     frameData.close();
                     return null;
                  }
               }

               Http2HeadersParser parser = (Http2HeadersParser)frameParser.parser;
               channel = new Http2StreamSourceChannel(this, frameData, frameHeaderData.getFrameLength(), parser.getHeaderMap(), frameParser.streamId);
               this.updateStreamIdsCountersInHeaders(frameParser.streamId);
               StreamHolder holder = (StreamHolder)this.currentStreams.get(frameParser.streamId);
               if (holder == null) {
                  receiveConcurrentStreamsAtomicUpdater.getAndIncrement(this);
                  this.currentStreams.put(frameParser.streamId, holder = new StreamHolder((Http2StreamSourceChannel)channel));
               } else {
                  holder.sourceChannel = (Http2StreamSourceChannel)channel;
               }

               if (parser.isHeadersEndStream() && Bits.allAreSet(frameParser.flags, 4)) {
                  ((AbstractHttp2StreamSourceChannel)channel).lastFrame();
                  holder.sourceChannel = null;
                  if (!this.isClient() || !"100".equals(parser.getHeaderMap().getFirst(STATUS))) {
                     holder.sourceClosed = true;
                     if (holder.sinkClosed) {
                        receiveConcurrentStreamsAtomicUpdater.getAndDecrement(this);
                        this.currentStreams.remove(frameParser.streamId);
                     }
                  }
               }

               if (parser.isInvalid()) {
                  ((AbstractHttp2StreamSourceChannel)channel).rstStream(1);
                  this.sendRstStream(frameParser.streamId, 1);
                  channel = null;
               }

               if (parser.getDependentStreamId() == frameParser.streamId) {
                  this.sendRstStream(frameParser.streamId, 1);
                  frameData.close();
                  return null;
               }
               break;
            case 6:
               Http2PingParser pingParser = (Http2PingParser)frameParser.parser;
               frameData.close();
               boolean ack = Bits.anyAreSet(frameParser.flags, 1);
               channel = new Http2PingStreamSourceChannel(this, pingParser.getData(), ack);
               if (!ack) {
                  this.sendPing(pingParser.getData(), new Http2ControlMessageExceptionHandler(), true);
               }
               break;
            case 7:
               Http2GoAwayParser http2GoAwayParser = (Http2GoAwayParser)frameParser.parser;
               channel = new Http2GoAwayStreamSourceChannel(this, frameData, frameParser.getFrameLength(), http2GoAwayParser.getStatusCode(), http2GoAwayParser.getLastGoodStreamId());
               this.peerGoneAway = true;
               Iterator var6 = this.currentStreams.values().iterator();

               while(var6.hasNext()) {
                  StreamHolder holder = (StreamHolder)var6.next();
                  if (holder.sourceChannel != null) {
                     holder.sourceChannel.rstStream();
                  }

                  if (holder.sinkChannel != null) {
                     holder.sinkChannel.rstStream();
                  }
               }

               frameData.close();
               this.sendGoAway(0);
               break;
            case 8:
               Http2WindowUpdateParser parser = (Http2WindowUpdateParser)frameParser.parser;
               this.handleWindowUpdate(frameParser.streamId, parser.getDeltaWindowSize());
               frameData.close();
               return null;
            default:
               UndertowLogger.REQUEST_LOGGER.tracef("Dropping frame of length %s and type %s for stream %s as we do not understand this type of frame", frameParser.getFrameLength(), (long)frameParser.type, (long)frameParser.streamId);
               frameData.close();
               return null;
         }

         return (AbstractHttp2StreamSourceChannel)channel;
      }
   }

   protected FrameHeaderData parseFrame(ByteBuffer data) throws IOException {
      Http2FrameHeaderParser frameParser;
      do {
         frameParser = this.parseFrameNoContinuation(data);
      } while(frameParser != null && frameParser.getContinuationParser() != null && data.hasRemaining());

      return frameParser;
   }

   private Http2FrameHeaderParser parseFrameNoContinuation(ByteBuffer data) throws IOException {
      if (this.prefaceCount < PREFACE_BYTES.length) {
         while(data.hasRemaining() && this.prefaceCount < PREFACE_BYTES.length) {
            if (data.get() != PREFACE_BYTES[this.prefaceCount]) {
               IoUtils.safeClose((Closeable)this.getUnderlyingConnection());
               throw UndertowMessages.MESSAGES.incorrectHttp2Preface();
            }

            ++this.prefaceCount;
         }
      }

      Http2FrameHeaderParser frameParser = this.frameParser;
      if (frameParser == null) {
         this.frameParser = frameParser = new Http2FrameHeaderParser(this, this.continuationParser);
         this.continuationParser = null;
      }

      if (!frameParser.handle(data)) {
         return null;
      } else {
         if (!this.initialSettingsReceived) {
            if (frameParser.type != 4) {
               UndertowLogger.REQUEST_IO_LOGGER.remoteEndpointFailedToSendInitialSettings(frameParser.type);
               this.markReadsBroken(new IOException());
            } else {
               this.initialSettingsReceived = true;
            }
         }

         this.frameParser = null;
         if (frameParser.getActualLength() > this.receiveMaxFrameSize && frameParser.getActualLength() > this.unackedReceiveMaxFrameSize) {
            this.sendGoAway(6);
            throw UndertowMessages.MESSAGES.http2FrameTooLarge();
         } else {
            if (frameParser.getContinuationParser() != null) {
               this.continuationParser = frameParser.getContinuationParser();
            }

            return frameParser;
         }
      }
   }

   protected void lastDataRead() {
      this.lastDataRead = true;
      if (!this.peerGoneAway) {
         IoUtils.safeClose((Closeable)this);
      } else {
         this.peerGoneAway = true;
         if (!this.thisGoneAway) {
            this.sendGoAway(10);
         }
      }

   }

   protected boolean isLastFrameReceived() {
      return this.lastDataRead;
   }

   protected boolean isLastFrameSent() {
      return this.thisGoneAway;
   }

   protected void handleBrokenSourceChannel(Throwable e) {
      UndertowLogger.REQUEST_LOGGER.debugf(e, "Closing HTTP2 channel to %s due to broken read side", this.getPeerAddress());
      if (e instanceof ConnectionErrorException) {
         this.sendGoAway(((ConnectionErrorException)e).getCode(), new Http2ControlMessageExceptionHandler());
      } else {
         this.sendGoAway(e instanceof ClosedChannelException ? 10 : 1, new Http2ControlMessageExceptionHandler());
      }

   }

   protected void handleBrokenSinkChannel(Throwable e) {
      UndertowLogger.REQUEST_LOGGER.debugf(e, "Closing HTTP2 channel to %s due to broken write side", this.getPeerAddress());
      IoUtils.safeClose((Closeable)this);
   }

   protected void closeSubChannels() {
      Iterator var1 = this.currentStreams.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry<Integer, StreamHolder> e = (Map.Entry)var1.next();
         StreamHolder holder = (StreamHolder)e.getValue();
         AbstractHttp2StreamSourceChannel receiver = holder.sourceChannel;
         if (receiver != null) {
            receiver.markStreamBroken();
         }

         Http2StreamSinkChannel sink = holder.sinkChannel;
         if (sink != null) {
            if (sink.isWritesShutdown()) {
               ChannelListeners.invokeChannelListener(sink.getIoThread(), sink, ((ChannelListener.SimpleSetter)sink.getWriteSetter()).get());
            }

            IoUtils.safeClose((Closeable)sink);
         }
      }

   }

   protected Collection<AbstractFramedStreamSourceChannel<Http2Channel, AbstractHttp2StreamSourceChannel, AbstractHttp2StreamSinkChannel>> getReceivers() {
      List<AbstractFramedStreamSourceChannel<Http2Channel, AbstractHttp2StreamSourceChannel, AbstractHttp2StreamSinkChannel>> channels = new ArrayList(this.currentStreams.size());
      Iterator var2 = this.currentStreams.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<Integer, StreamHolder> entry = (Map.Entry)var2.next();
         if (!((StreamHolder)entry.getValue()).sourceClosed) {
            channels.add(((StreamHolder)entry.getValue()).sourceChannel);
         }
      }

      return channels;
   }

   boolean updateSettings(List<Http2Setting> settings) {
      Iterator var2 = settings.iterator();

      while(true) {
         while(true) {
            while(var2.hasNext()) {
               Http2Setting setting = (Http2Setting)var2.next();
               if (setting.getId() != 4) {
                  if (setting.getId() == 5) {
                     if (setting.getValue() > 16777215L || setting.getValue() < 16384L) {
                        UndertowLogger.REQUEST_IO_LOGGER.debug("Invalid value received for SETTINGS_MAX_FRAME_SIZE " + setting.getValue());
                        this.sendGoAway(1);
                        return false;
                     }

                     this.sendMaxFrameSize = (int)setting.getValue();
                  } else if (setting.getId() == 1) {
                     synchronized(this) {
                        this.encoder.setMaxTableSize((int)setting.getValue());
                     }
                  } else if (setting.getId() == 2) {
                     int result = (int)setting.getValue();
                     if (result == 0) {
                        this.pushEnabled = false;
                     } else if (result != 1) {
                        UndertowLogger.REQUEST_IO_LOGGER.debug("Invalid value received for SETTINGS_ENABLE_PUSH " + result);
                        this.sendGoAway(1);
                        return false;
                     }
                  } else if (setting.getId() == 3) {
                     this.sendMaxConcurrentStreams = (int)setting.getValue();
                  }
               } else {
                  synchronized(this.flowControlLock) {
                     if (setting.getValue() > 2147483647L) {
                        this.sendGoAway(3);
                        return false;
                     }

                     this.initialSendWindowSize = (int)setting.getValue();
                  }
               }
            }

            return true;
         }
      }
   }

   public int getHttp2Version() {
      return 3;
   }

   public int getInitialSendWindowSize() {
      return this.initialSendWindowSize;
   }

   public int getInitialReceiveWindowSize() {
      return this.initialReceiveWindowSize;
   }

   public int getSendMaxConcurrentStreams() {
      return this.sendMaxConcurrentStreams;
   }

   public void setSendMaxConcurrentStreams(int sendMaxConcurrentStreams) {
      this.sendMaxConcurrentStreams = sendMaxConcurrentStreams;
      this.sendSettings();
   }

   public int getReceiveMaxConcurrentStreams() {
      return this.receiveMaxConcurrentStreams;
   }

   public void handleWindowUpdate(int streamId, int deltaWindowSize) throws IOException {
      if (streamId == 0) {
         if (deltaWindowSize == 0) {
            UndertowLogger.REQUEST_IO_LOGGER.debug("Invalid flow-control window increment of 0 received with WINDOW_UPDATE frame for connection");
            this.sendGoAway(1);
            return;
         }

         synchronized(this.flowControlLock) {
            boolean exhausted = this.sendWindowSize <= 2L;
            this.sendWindowSize += (long)deltaWindowSize;
            if (exhausted) {
               this.notifyFlowControlAllowed();
            }

            if (this.sendWindowSize > 2147483647L) {
               this.sendGoAway(3);
            }
         }
      } else {
         if (deltaWindowSize == 0) {
            UndertowLogger.REQUEST_IO_LOGGER.debug("Invalid flow-control window increment of 0 received with WINDOW_UPDATE frame for stream " + streamId);
            this.sendRstStream(streamId, 1);
            return;
         }

         StreamHolder holder = (StreamHolder)this.currentStreams.get(streamId);
         Http2StreamSinkChannel stream = holder != null ? holder.sinkChannel : null;
         if (stream == null) {
            if (this.isIdle(streamId)) {
               this.sendGoAway(1);
            }
         } else {
            stream.updateFlowControlWindow(deltaWindowSize);
         }
      }

   }

   synchronized void notifyFlowControlAllowed() throws IOException {
      super.recalculateHeldFrames();
   }

   public void sendPing(byte[] data) {
      this.sendPing(data, new Http2ControlMessageExceptionHandler());
   }

   public void sendPing(byte[] data, ChannelExceptionHandler<AbstractHttp2StreamSinkChannel> exceptionHandler) {
      this.sendPing(data, exceptionHandler, false);
   }

   void sendPing(byte[] data, ChannelExceptionHandler<AbstractHttp2StreamSinkChannel> exceptionHandler, boolean ack) {
      Http2PingStreamSinkChannel ping = new Http2PingStreamSinkChannel(this, data, ack);

      try {
         ping.shutdownWrites();
         if (!ping.flush()) {
            ping.getWriteSetter().set(ChannelListeners.flushingChannelListener((ChannelListener)null, exceptionHandler));
            ping.resumeWrites();
         }
      } catch (IOException var6) {
         if (exceptionHandler != null) {
            exceptionHandler.handleException(ping, var6);
         } else {
            UndertowLogger.REQUEST_LOGGER.debug("Failed to send ping and no exception handler set", var6);
         }
      } catch (Throwable var7) {
         if (exceptionHandler != null) {
            exceptionHandler.handleException(ping, new IOException(var7));
         } else {
            UndertowLogger.REQUEST_LOGGER.debug("Failed to send ping and no exception handler set", var7);
         }
      }

   }

   public void sendGoAway(int status) {
      this.sendGoAway(status, new Http2ControlMessageExceptionHandler());
   }

   public void sendGoAway(int status, ChannelExceptionHandler<AbstractHttp2StreamSinkChannel> exceptionHandler) {
      if (!this.thisGoneAway) {
         this.thisGoneAway = true;
         if (UndertowLogger.REQUEST_IO_LOGGER.isTraceEnabled()) {
            UndertowLogger.REQUEST_IO_LOGGER.tracef(new ClosedChannelException(), "Sending goaway on channel %s", this);
         }

         Http2GoAwayStreamSinkChannel goAway = new Http2GoAwayStreamSinkChannel(this, status, this.getLastGoodStreamId());

         try {
            goAway.shutdownWrites();
            if (!goAway.flush()) {
               goAway.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<Channel>() {
                  public void handleEvent(Channel channel) {
                     IoUtils.safeClose((Closeable)Http2Channel.this);
                  }
               }, exceptionHandler));
               goAway.resumeWrites();
            } else {
               IoUtils.safeClose((Closeable)this);
            }
         } catch (IOException var5) {
            exceptionHandler.handleException(goAway, var5);
         } catch (Throwable var6) {
            exceptionHandler.handleException(goAway, new IOException(var6));
         }

      }
   }

   public void sendUpdateWindowSize(int streamId, int delta) throws IOException {
      Http2WindowUpdateStreamSinkChannel windowUpdateStreamSinkChannel = new Http2WindowUpdateStreamSinkChannel(this, streamId, delta);
      this.flushChannel(windowUpdateStreamSinkChannel);
   }

   public SSLSession getSslSession() {
      StreamConnection con = this.getUnderlyingConnection();
      return con instanceof SslConnection ? ((SslConnection)con).getSslSession() : null;
   }

   public void updateReceiveFlowControlWindow(int read) throws IOException {
      if (read > 0) {
         int delta = -1;
         synchronized(this.flowControlLock) {
            this.receiveWindowSize -= read;
            int initialWindowSize = this.initialReceiveWindowSize;
            if (this.receiveWindowSize < initialWindowSize / 2) {
               delta = initialWindowSize - this.receiveWindowSize;
               this.receiveWindowSize += delta;
            }
         }

         if (delta > 0) {
            this.sendUpdateWindowSize(0, delta);
         }

      }
   }

   public synchronized Http2HeadersStreamSinkChannel createStream(HeaderMap requestHeaders) throws IOException {
      if (!this.isClient()) {
         throw UndertowMessages.MESSAGES.headersStreamCanOnlyBeCreatedByClient();
      } else if (!this.isOpen()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         sendConcurrentStreamsAtomicUpdater.incrementAndGet(this);
         if (this.sendMaxConcurrentStreams > 0 && this.sendConcurrentStreams > this.sendMaxConcurrentStreams) {
            throw UndertowMessages.MESSAGES.streamLimitExceeded();
         } else {
            int streamId = this.streamIdCounter;
            this.streamIdCounter += 2;
            Http2HeadersStreamSinkChannel http2SynStreamStreamSinkChannel = new Http2HeadersStreamSinkChannel(this, streamId, requestHeaders);
            this.currentStreams.put(streamId, new StreamHolder(http2SynStreamStreamSinkChannel));
            return http2SynStreamStreamSinkChannel;
         }
      }
   }

   public synchronized boolean addPushPromiseStream(int pushedStreamId) throws IOException {
      if (this.isClient() && pushedStreamId % 2 == 0) {
         if (!this.isOpen()) {
            throw UndertowMessages.MESSAGES.channelIsClosed();
         } else if (!this.isIdle(pushedStreamId)) {
            UndertowLogger.REQUEST_IO_LOGGER.debugf("Non idle streamId %d received from the server as a pushed stream.", pushedStreamId);
            return false;
         } else {
            StreamHolder holder = new StreamHolder((Http2HeadersStreamSinkChannel)null);
            holder.sinkClosed = true;
            this.lastAssignedStreamOtherSide = Math.max(this.lastAssignedStreamOtherSide, pushedStreamId);
            this.currentStreams.put(pushedStreamId, holder);
            return true;
         }
      } else {
         throw UndertowMessages.MESSAGES.pushPromiseCanOnlyBeCreatedByServer();
      }
   }

   private synchronized int getLastAssignedStreamOtherSide() {
      return this.lastAssignedStreamOtherSide;
   }

   private synchronized int getLastGoodStreamId() {
      return this.lastGoodStreamId;
   }

   private synchronized void updateStreamIdsCountersInHeaders(int streamNo) {
      if (streamNo % 2 != 0) {
         this.lastGoodStreamId = Math.max(this.lastGoodStreamId, streamNo);
         if (!this.isClient()) {
            this.lastAssignedStreamOtherSide = this.lastGoodStreamId;
         }
      } else if (this.isClient()) {
         this.lastAssignedStreamOtherSide = Math.max(this.lastAssignedStreamOtherSide, streamNo);
      }

   }

   public synchronized Http2HeadersStreamSinkChannel sendPushPromise(int associatedStreamId, HeaderMap requestHeaders, HeaderMap responseHeaders) throws IOException {
      if (!this.isOpen()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else if (this.isClient()) {
         throw UndertowMessages.MESSAGES.pushPromiseCanOnlyBeCreatedByServer();
      } else {
         sendConcurrentStreamsAtomicUpdater.incrementAndGet(this);
         if (this.sendMaxConcurrentStreams > 0 && this.sendConcurrentStreams > this.sendMaxConcurrentStreams) {
            throw UndertowMessages.MESSAGES.streamLimitExceeded();
         } else {
            int streamId = this.streamIdCounter;
            this.streamIdCounter += 2;
            Http2PushPromiseStreamSinkChannel pushPromise = new Http2PushPromiseStreamSinkChannel(this, requestHeaders, associatedStreamId, streamId);
            this.flushChannel(pushPromise);
            Http2HeadersStreamSinkChannel http2SynStreamStreamSinkChannel = new Http2HeadersStreamSinkChannel(this, streamId, responseHeaders);
            this.currentStreams.put(streamId, new StreamHolder(http2SynStreamStreamSinkChannel));
            return http2SynStreamStreamSinkChannel;
         }
      }
   }

   int grabFlowControlBytes(int bytesToGrab) {
      if (bytesToGrab <= 0) {
         return 0;
      } else {
         synchronized(this.flowControlLock) {
            int min = (int)Math.min((long)bytesToGrab, this.sendWindowSize);
            if (bytesToGrab > 2 && min <= 2) {
               return 0;
            } else {
               min = Math.min(this.sendMaxFrameSize, min);
               this.sendWindowSize -= (long)min;
               return min;
            }
         }
      }
   }

   void registerStreamSink(Http2HeadersStreamSinkChannel synResponse) {
      StreamHolder existing = (StreamHolder)this.currentStreams.get(synResponse.getStreamId());
      if (existing == null) {
         throw UndertowMessages.MESSAGES.streamNotRegistered();
      } else {
         existing.sinkChannel = synResponse;
      }
   }

   void removeStreamSink(int streamId) {
      StreamHolder existing = (StreamHolder)this.currentStreams.get(streamId);
      if (existing != null) {
         existing.sinkClosed = true;
         existing.sinkChannel = null;
         if (existing.sourceClosed) {
            if (streamId % 2 == (this.isClient() ? 1 : 0)) {
               sendConcurrentStreamsAtomicUpdater.getAndDecrement(this);
            } else {
               receiveConcurrentStreamsAtomicUpdater.getAndDecrement(this);
            }

            this.currentStreams.remove(streamId);
         }

         if (this.isLastFrameReceived() && this.currentStreams.isEmpty()) {
            this.sendGoAway(0);
         } else if (this.parseTimeoutUpdater != null && this.currentStreams.isEmpty()) {
            this.parseTimeoutUpdater.connectionIdle();
         }

      }
   }

   public boolean isClient() {
      return this.streamIdCounter % 2 == 1;
   }

   HpackEncoder getEncoder() {
      return this.encoder;
   }

   HpackDecoder getDecoder() {
      return this.decoder;
   }

   int getMaxHeaders() {
      return this.maxHeaders;
   }

   int getPaddingBytes() {
      return this.paddingRandom == null ? 0 : this.paddingRandom.nextInt(this.maxPadding);
   }

   public <T> T getAttachment(AttachmentKey<T> key) {
      if (key == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("key");
      } else {
         return this.attachments.get(key);
      }
   }

   public <T> List<T> getAttachmentList(AttachmentKey<? extends List<T>> key) {
      if (key == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("key");
      } else {
         Object o = this.attachments.get(key);
         return o == null ? Collections.emptyList() : (List)o;
      }
   }

   public <T> T putAttachment(AttachmentKey<T> key, T value) {
      if (key == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("key");
      } else {
         return key.cast(this.attachments.put(key, key.cast(value)));
      }
   }

   public <T> T removeAttachment(AttachmentKey<T> key) {
      return key.cast(this.attachments.remove(key));
   }

   public <T> void addToAttachmentList(AttachmentKey<AttachmentList<T>> key, T value) {
      if (key == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("key");
      } else {
         Map<AttachmentKey<?>, Object> attachments = this.attachments;
         synchronized(attachments) {
            List<T> list = (List)key.cast(attachments.get(key));
            if (list == null) {
               AttachmentList<T> newList = new AttachmentList(Object.class);
               attachments.put(key, newList);
               newList.add(value);
            } else {
               list.add(value);
            }

         }
      }
   }

   public void sendRstStream(int streamId, int statusCode) {
      if (this.isOpen()) {
         this.handleRstStream(streamId);
         if (UndertowLogger.REQUEST_IO_LOGGER.isDebugEnabled()) {
            UndertowLogger.REQUEST_IO_LOGGER.debugf(new ClosedChannelException(), "Sending rststream on channel %s stream %s", this, streamId);
         }

         Http2RstStreamSinkChannel channel = new Http2RstStreamSinkChannel(this, streamId, statusCode);
         this.flushChannelIgnoreFailure(channel);
      }
   }

   private void handleRstStream(int streamId) {
      StreamHolder holder = (StreamHolder)this.currentStreams.remove(streamId);
      if (holder != null) {
         if (streamId % 2 == (this.isClient() ? 1 : 0)) {
            sendConcurrentStreamsAtomicUpdater.getAndDecrement(this);
         } else {
            receiveConcurrentStreamsAtomicUpdater.getAndDecrement(this);
         }

         if (holder.sinkChannel != null) {
            holder.sinkChannel.rstStream();
         }

         if (holder.sourceChannel != null) {
            holder.sourceChannel.rstStream();
         }
      }

   }

   public synchronized Http2HeadersStreamSinkChannel createInitialUpgradeResponseStream() {
      if (this.lastGoodStreamId != 0) {
         throw new IllegalStateException();
      } else {
         this.updateStreamIdsCountersInHeaders(1);
         Http2HeadersStreamSinkChannel stream = new Http2HeadersStreamSinkChannel(this, 1);
         StreamHolder streamHolder = new StreamHolder(stream);
         streamHolder.sourceClosed = true;
         this.currentStreams.put(1, streamHolder);
         receiveConcurrentStreamsAtomicUpdater.getAndIncrement(this);
         return stream;
      }
   }

   public boolean isPushEnabled() {
      return this.pushEnabled;
   }

   public boolean isPeerGoneAway() {
      return this.peerGoneAway;
   }

   public boolean isThisGoneAway() {
      return this.thisGoneAway;
   }

   Http2StreamSourceChannel removeStreamSource(int streamId) {
      StreamHolder existing = (StreamHolder)this.currentStreams.get(streamId);
      if (existing == null) {
         return null;
      } else {
         existing.sourceClosed = true;
         Http2StreamSourceChannel ret = existing.sourceChannel;
         existing.sourceChannel = null;
         if (existing.sinkClosed) {
            if (streamId % 2 == (this.isClient() ? 1 : 0)) {
               sendConcurrentStreamsAtomicUpdater.getAndDecrement(this);
            } else {
               receiveConcurrentStreamsAtomicUpdater.getAndDecrement(this);
            }

            this.currentStreams.remove(streamId);
         }

         return ret;
      }
   }

   Http2StreamSourceChannel getIncomingStream(int streamId) {
      StreamHolder existing = (StreamHolder)this.currentStreams.get(streamId);
      return existing == null ? null : existing.sourceChannel;
   }

   public int getReceiveMaxFrameSize() {
      return this.receiveMaxFrameSize;
   }

   public int getSendMaxFrameSize() {
      return this.sendMaxFrameSize;
   }

   public String getProtocol() {
      return this.protocol;
   }

   private synchronized boolean isIdle(int streamNo) {
      if (streamNo % 2 == this.streamIdCounter % 2) {
         return streamNo >= this.streamIdCounter;
      } else {
         return streamNo > this.lastAssignedStreamOtherSide;
      }
   }

   int getMaxHeaderListSize() {
      return this.maxHeaderListSize;
   }

   private static final class StreamHolder {
      boolean sourceClosed = false;
      boolean sinkClosed = false;
      Http2StreamSourceChannel sourceChannel;
      Http2StreamSinkChannel sinkChannel;

      StreamHolder(Http2StreamSourceChannel sourceChannel) {
         this.sourceChannel = sourceChannel;
      }

      StreamHolder(Http2StreamSinkChannel sinkChannel) {
         this.sinkChannel = sinkChannel;
      }
   }

   private class Http2ControlMessageExceptionHandler implements ChannelExceptionHandler<AbstractHttp2StreamSinkChannel> {
      private Http2ControlMessageExceptionHandler() {
      }

      public void handleException(AbstractHttp2StreamSinkChannel channel, IOException exception) {
         IoUtils.safeClose((Closeable)channel);
         Http2Channel.this.handleBrokenSinkChannel(exception);
      }

      // $FF: synthetic method
      Http2ControlMessageExceptionHandler(Object x1) {
         this();
      }
   }
}
