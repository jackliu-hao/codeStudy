/*      */ package io.undertow.protocols.http2;
/*      */ 
/*      */ import io.undertow.UndertowLogger;
/*      */ import io.undertow.UndertowMessages;
/*      */ import io.undertow.UndertowOptions;
/*      */ import io.undertow.connector.ByteBufferPool;
/*      */ import io.undertow.connector.PooledByteBuffer;
/*      */ import io.undertow.server.protocol.ParseTimeoutUpdater;
/*      */ import io.undertow.server.protocol.framed.AbstractFramedChannel;
/*      */ import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;
/*      */ import io.undertow.server.protocol.framed.FrameHeaderData;
/*      */ import io.undertow.util.Attachable;
/*      */ import io.undertow.util.AttachmentKey;
/*      */ import io.undertow.util.AttachmentList;
/*      */ import io.undertow.util.HeaderMap;
/*      */ import io.undertow.util.HttpString;
/*      */ import java.io.Closeable;
/*      */ import java.io.IOException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.Channel;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.security.SecureRandom;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import org.xnio.Bits;
/*      */ import org.xnio.ChannelExceptionHandler;
/*      */ import org.xnio.ChannelListener;
/*      */ import org.xnio.ChannelListeners;
/*      */ import org.xnio.IoUtils;
/*      */ import org.xnio.OptionMap;
/*      */ import org.xnio.StreamConnection;
/*      */ import org.xnio.channels.ConnectedChannel;
/*      */ import org.xnio.channels.StreamSinkChannel;
/*      */ import org.xnio.ssl.SslConnection;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Http2Channel
/*      */   extends AbstractFramedChannel<Http2Channel, AbstractHttp2StreamSourceChannel, AbstractHttp2StreamSinkChannel>
/*      */   implements Attachable
/*      */ {
/*      */   public static final String CLEARTEXT_UPGRADE_STRING = "h2c";
/*   73 */   public static final HttpString METHOD = new HttpString(":method");
/*   74 */   public static final HttpString PATH = new HttpString(":path");
/*   75 */   public static final HttpString SCHEME = new HttpString(":scheme");
/*   76 */   public static final HttpString AUTHORITY = new HttpString(":authority");
/*   77 */   public static final HttpString STATUS = new HttpString(":status");
/*      */   
/*      */   static final int FRAME_TYPE_DATA = 0;
/*      */   
/*      */   static final int FRAME_TYPE_HEADERS = 1;
/*      */   
/*      */   static final int FRAME_TYPE_PRIORITY = 2;
/*      */   
/*      */   static final int FRAME_TYPE_RST_STREAM = 3;
/*      */   
/*      */   static final int FRAME_TYPE_SETTINGS = 4;
/*      */   
/*      */   static final int FRAME_TYPE_PUSH_PROMISE = 5;
/*      */   
/*      */   static final int FRAME_TYPE_PING = 6;
/*      */   
/*      */   static final int FRAME_TYPE_GOAWAY = 7;
/*      */   
/*      */   static final int FRAME_TYPE_WINDOW_UPDATE = 8;
/*      */   
/*      */   static final int FRAME_TYPE_CONTINUATION = 9;
/*      */   public static final int ERROR_NO_ERROR = 0;
/*      */   public static final int ERROR_PROTOCOL_ERROR = 1;
/*      */   public static final int ERROR_INTERNAL_ERROR = 2;
/*      */   public static final int ERROR_FLOW_CONTROL_ERROR = 3;
/*      */   public static final int ERROR_SETTINGS_TIMEOUT = 4;
/*      */   public static final int ERROR_STREAM_CLOSED = 5;
/*      */   public static final int ERROR_FRAME_SIZE_ERROR = 6;
/*      */   public static final int ERROR_REFUSED_STREAM = 7;
/*      */   public static final int ERROR_CANCEL = 8;
/*      */   public static final int ERROR_COMPRESSION_ERROR = 9;
/*      */   public static final int ERROR_CONNECT_ERROR = 10;
/*      */   public static final int ERROR_ENHANCE_YOUR_CALM = 11;
/*      */   public static final int ERROR_INADEQUATE_SECURITY = 12;
/*      */   static final int DATA_FLAG_END_STREAM = 1;
/*      */   static final int DATA_FLAG_END_SEGMENT = 2;
/*      */   static final int DATA_FLAG_PADDED = 8;
/*      */   static final int PING_FRAME_LENGTH = 8;
/*      */   static final int PING_FLAG_ACK = 1;
/*      */   static final int HEADERS_FLAG_END_STREAM = 1;
/*      */   static final int HEADERS_FLAG_END_SEGMENT = 2;
/*      */   static final int HEADERS_FLAG_END_HEADERS = 4;
/*      */   static final int HEADERS_FLAG_PADDED = 8;
/*      */   static final int HEADERS_FLAG_PRIORITY = 32;
/*      */   static final int SETTINGS_FLAG_ACK = 1;
/*      */   static final int CONTINUATION_FLAG_END_HEADERS = 4;
/*      */   public static final int DEFAULT_INITIAL_WINDOW_SIZE = 65535;
/*  124 */   static final byte[] PREFACE_BYTES = new byte[] { 80, 82, 73, 32, 42, 32, 72, 84, 84, 80, 47, 50, 46, 48, 13, 10, 13, 10, 83, 77, 13, 10, 13, 10 };
/*      */   
/*      */   public static final int DEFAULT_MAX_FRAME_SIZE = 16384;
/*      */   
/*      */   public static final int MAX_FRAME_SIZE = 16777215;
/*      */   
/*      */   public static final int FLOW_CONTROL_MIN_WINDOW = 2;
/*      */   
/*      */   private Http2FrameHeaderParser frameParser;
/*      */   
/*  134 */   private final Map<Integer, StreamHolder> currentStreams = new ConcurrentHashMap<>();
/*      */   
/*      */   private final String protocol;
/*      */   
/*      */   private final int encoderHeaderTableSize;
/*      */   private volatile boolean pushEnabled;
/*  140 */   private volatile int sendMaxConcurrentStreams = -1;
/*      */   private final int receiveMaxConcurrentStreams;
/*  142 */   private volatile int sendConcurrentStreams = 0;
/*  143 */   private volatile int receiveConcurrentStreams = 0;
/*      */   private final int initialReceiveWindowSize;
/*  145 */   private volatile int sendMaxFrameSize = 16384;
/*      */   private final int receiveMaxFrameSize;
/*  147 */   private int unackedReceiveMaxFrameSize = 16384;
/*      */   
/*      */   private final int maxHeaders;
/*      */   private final int maxHeaderListSize;
/*  151 */   private static final AtomicIntegerFieldUpdater<Http2Channel> sendConcurrentStreamsAtomicUpdater = AtomicIntegerFieldUpdater.newUpdater(Http2Channel.class, "sendConcurrentStreams");
/*      */ 
/*      */   
/*  154 */   private static final AtomicIntegerFieldUpdater<Http2Channel> receiveConcurrentStreamsAtomicUpdater = AtomicIntegerFieldUpdater.newUpdater(Http2Channel.class, "receiveConcurrentStreams");
/*      */   
/*      */   private boolean thisGoneAway = false;
/*      */   
/*      */   private boolean peerGoneAway = false;
/*      */   
/*      */   private boolean lastDataRead = false;
/*      */   
/*      */   private int streamIdCounter;
/*      */   
/*      */   private int lastGoodStreamId;
/*      */   private int lastAssignedStreamOtherSide;
/*      */   private final HpackDecoder decoder;
/*      */   private final HpackEncoder encoder;
/*      */   private final int maxPadding;
/*      */   private final Random paddingRandom;
/*      */   private int prefaceCount;
/*      */   private boolean initialSettingsReceived;
/*  172 */   private Http2HeadersParser continuationParser = null;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean initialSettingsSent = false;
/*      */ 
/*      */ 
/*      */   
/*  181 */   private final Map<AttachmentKey<?>, Object> attachments = Collections.synchronizedMap(new HashMap<>());
/*      */   
/*      */   private final ParseTimeoutUpdater parseTimeoutUpdater;
/*      */   
/*  185 */   private final Object flowControlLock = new Object();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  190 */   private volatile int initialSendWindowSize = 65535;
/*      */ 
/*      */ 
/*      */   
/*  194 */   private volatile long sendWindowSize = this.initialSendWindowSize;
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile int receiveWindowSize;
/*      */ 
/*      */ 
/*      */   
/*      */   public Http2Channel(StreamConnection connectedStreamChannel, String protocol, ByteBufferPool bufferPool, PooledByteBuffer data, boolean clientSide, boolean fromUpgrade, OptionMap settings) {
/*  203 */     this(connectedStreamChannel, protocol, bufferPool, data, clientSide, fromUpgrade, true, (ByteBuffer)null, settings);
/*      */   }
/*      */   
/*      */   public Http2Channel(StreamConnection connectedStreamChannel, String protocol, ByteBufferPool bufferPool, PooledByteBuffer data, boolean clientSide, boolean fromUpgrade, boolean prefaceRequired, OptionMap settings) {
/*  207 */     this(connectedStreamChannel, protocol, bufferPool, data, clientSide, fromUpgrade, prefaceRequired, (ByteBuffer)null, settings);
/*      */   }
/*      */   
/*      */   public Http2Channel(StreamConnection connectedStreamChannel, String protocol, ByteBufferPool bufferPool, PooledByteBuffer data, boolean clientSide, boolean fromUpgrade, boolean prefaceRequired, ByteBuffer initialOtherSideSettings, OptionMap settings) {
/*  211 */     super(connectedStreamChannel, bufferPool, new Http2FramePriority(clientSide ? (fromUpgrade ? 3 : 1) : 2), data, settings);
/*  212 */     this.streamIdCounter = clientSide ? (fromUpgrade ? 3 : 1) : 2;
/*      */     
/*  214 */     this.pushEnabled = settings.get(UndertowOptions.HTTP2_SETTINGS_ENABLE_PUSH, true);
/*  215 */     this.initialReceiveWindowSize = settings.get(UndertowOptions.HTTP2_SETTINGS_INITIAL_WINDOW_SIZE, 65535);
/*  216 */     this.receiveWindowSize = this.initialReceiveWindowSize;
/*  217 */     this.receiveMaxConcurrentStreams = settings.get(UndertowOptions.HTTP2_SETTINGS_MAX_CONCURRENT_STREAMS, -1);
/*      */     
/*  219 */     this.protocol = (protocol == null) ? "h2" : protocol;
/*  220 */     this.maxHeaders = settings.get(UndertowOptions.MAX_HEADERS, clientSide ? -1 : 200);
/*      */     
/*  222 */     this.encoderHeaderTableSize = settings.get(UndertowOptions.HTTP2_SETTINGS_HEADER_TABLE_SIZE, 4096);
/*  223 */     this.receiveMaxFrameSize = settings.get(UndertowOptions.HTTP2_SETTINGS_MAX_FRAME_SIZE, 16384);
/*  224 */     this.maxPadding = settings.get(UndertowOptions.HTTP2_PADDING_SIZE, 0);
/*  225 */     this.maxHeaderListSize = settings.get(UndertowOptions.HTTP2_SETTINGS_MAX_HEADER_LIST_SIZE, settings.get(UndertowOptions.MAX_HEADER_SIZE, -1));
/*  226 */     if (this.maxPadding > 0) {
/*  227 */       this.paddingRandom = new SecureRandom();
/*      */     } else {
/*  229 */       this.paddingRandom = null;
/*      */     } 
/*      */     
/*  232 */     this.decoder = new HpackDecoder(this.encoderHeaderTableSize);
/*  233 */     this.encoder = new HpackEncoder(this.encoderHeaderTableSize);
/*  234 */     if (!prefaceRequired) {
/*  235 */       this.prefaceCount = PREFACE_BYTES.length;
/*      */     }
/*      */     
/*  238 */     if (clientSide) {
/*  239 */       sendPreface();
/*  240 */       this.prefaceCount = PREFACE_BYTES.length;
/*  241 */       sendSettings();
/*  242 */       this.initialSettingsSent = true;
/*  243 */       if (fromUpgrade) {
/*  244 */         StreamHolder streamHolder = new StreamHolder((Http2StreamSinkChannel)null);
/*  245 */         streamHolder.sinkClosed = true;
/*  246 */         sendConcurrentStreamsAtomicUpdater.getAndIncrement(this);
/*  247 */         this.currentStreams.put(Integer.valueOf(1), streamHolder);
/*      */       } 
/*  249 */     } else if (fromUpgrade) {
/*  250 */       sendSettings();
/*  251 */       this.initialSettingsSent = true;
/*      */     } 
/*  253 */     if (initialOtherSideSettings != null) {
/*  254 */       Http2SettingsParser parser = new Http2SettingsParser(initialOtherSideSettings.remaining());
/*      */       try {
/*  256 */         Http2FrameHeaderParser headerParser = new Http2FrameHeaderParser(this, null);
/*  257 */         headerParser.length = initialOtherSideSettings.remaining();
/*  258 */         parser.parse(initialOtherSideSettings, headerParser);
/*  259 */         updateSettings(parser.getSettings());
/*  260 */       } catch (Throwable e) {
/*  261 */         IoUtils.safeClose((Closeable)connectedStreamChannel);
/*      */         
/*  263 */         throw new RuntimeException(e);
/*      */       } 
/*      */     } 
/*  266 */     int requestParseTimeout = settings.get(UndertowOptions.REQUEST_PARSE_TIMEOUT, -1);
/*  267 */     int requestIdleTimeout = settings.get(UndertowOptions.NO_REQUEST_TIMEOUT, -1);
/*  268 */     if (requestIdleTimeout < 0 && requestParseTimeout < 0) {
/*  269 */       this.parseTimeoutUpdater = null;
/*      */     } else {
/*  271 */       this.parseTimeoutUpdater = new ParseTimeoutUpdater((ConnectedChannel)this, requestParseTimeout, requestIdleTimeout, new Runnable()
/*      */           {
/*      */             public void run() {
/*  274 */               Http2Channel.this.sendGoAway(0);
/*      */ 
/*      */               
/*  277 */               Http2Channel.this.getIoThread().executeAfter(new Runnable()
/*      */                   {
/*      */                     public void run() {
/*  280 */                       IoUtils.safeClose((Closeable)Http2Channel.this);
/*      */                     }
/*      */                   },  2L, TimeUnit.SECONDS);
/*      */             }
/*      */           });
/*  285 */       addCloseTask(new ChannelListener<Http2Channel>()
/*      */           {
/*      */             public void handleEvent(Http2Channel channel) {
/*  288 */               Http2Channel.this.parseTimeoutUpdater.close();
/*      */             }
/*      */           });
/*      */     } 
/*      */   }
/*      */   
/*      */   private void sendSettings() {
/*  295 */     List<Http2Setting> settings = new ArrayList<>();
/*  296 */     settings.add(new Http2Setting(1, this.encoderHeaderTableSize));
/*  297 */     if (isClient()) {
/*  298 */       settings.add(new Http2Setting(2, this.pushEnabled ? 1L : 0L));
/*      */     }
/*  300 */     settings.add(new Http2Setting(5, this.receiveMaxFrameSize));
/*  301 */     settings.add(new Http2Setting(4, this.initialReceiveWindowSize));
/*  302 */     if (this.maxHeaderListSize > 0) {
/*  303 */       settings.add(new Http2Setting(6, this.maxHeaderListSize));
/*      */     }
/*  305 */     if (this.receiveMaxConcurrentStreams > 0) {
/*  306 */       settings.add(new Http2Setting(3, this.receiveMaxConcurrentStreams));
/*      */     }
/*  308 */     Http2SettingsStreamSinkChannel stream = new Http2SettingsStreamSinkChannel(this, settings);
/*  309 */     flushChannelIgnoreFailure((StreamSinkChannel)stream);
/*      */   }
/*      */   
/*      */   private void sendSettingsAck() {
/*  313 */     if (!this.initialSettingsSent) {
/*  314 */       sendSettings();
/*  315 */       this.initialSettingsSent = true;
/*      */     } 
/*  317 */     Http2SettingsStreamSinkChannel stream = new Http2SettingsStreamSinkChannel(this);
/*  318 */     flushChannelIgnoreFailure((StreamSinkChannel)stream);
/*      */   }
/*      */   
/*      */   private void flushChannelIgnoreFailure(StreamSinkChannel stream) {
/*      */     try {
/*  323 */       flushChannel(stream);
/*  324 */     } catch (IOException e) {
/*  325 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/*  326 */     } catch (Throwable t) {
/*  327 */       UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void flushChannel(StreamSinkChannel stream) throws IOException {
/*  332 */     stream.shutdownWrites();
/*  333 */     if (!stream.flush()) {
/*  334 */       stream.getWriteSetter().set(ChannelListeners.flushingChannelListener(null, writeExceptionHandler()));
/*  335 */       stream.resumeWrites();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void sendPreface() {
/*  340 */     Http2PrefaceStreamSinkChannel preface = new Http2PrefaceStreamSinkChannel(this);
/*  341 */     flushChannelIgnoreFailure((StreamSinkChannel)preface);
/*      */   }
/*      */   
/*      */   protected AbstractHttp2StreamSourceChannel createChannel(FrameHeaderData frameHeaderData, PooledByteBuffer frameData) throws IOException {
/*  345 */     AbstractHttp2StreamSourceChannel channel = createChannelImpl(frameHeaderData, frameData);
/*  346 */     if (channel instanceof Http2StreamSourceChannel && 
/*  347 */       this.parseTimeoutUpdater != null) {
/*  348 */       if (channel != null) {
/*  349 */         this.parseTimeoutUpdater.requestStarted();
/*  350 */       } else if (this.currentStreams.isEmpty()) {
/*  351 */         this.parseTimeoutUpdater.failedParse();
/*      */       } 
/*      */     }
/*      */     
/*  355 */     return channel; } protected AbstractHttp2StreamSourceChannel createChannelImpl(FrameHeaderData frameHeaderData, PooledByteBuffer frameData) throws IOException { AbstractHttp2StreamSourceChannel channel; Http2HeadersParser http2HeadersParser; Http2RstStreamParser http2RstStreamParser; Http2PingParser pingParser; Http2GoAwayParser http2GoAwayParser;
/*      */     Http2WindowUpdateParser http2WindowUpdateParser;
/*      */     Http2PriorityParser parser;
/*      */     StreamHolder holder;
/*      */     boolean ack;
/*  360 */     Http2FrameHeaderParser frameParser = (Http2FrameHeaderParser)frameHeaderData;
/*      */     
/*  362 */     if (frameParser.type == 0) {
/*      */ 
/*      */       
/*  365 */       sendGoAway(1);
/*  366 */       UndertowLogger.REQUEST_LOGGER.tracef("Dropping Frame of length %s for stream %s", frameParser.getFrameLength(), frameParser.streamId);
/*  367 */       return null;
/*      */     } 
/*      */ 
/*      */     
/*  371 */     switch (frameParser.type) {
/*      */ 
/*      */ 
/*      */       
/*      */       case 5:
/*      */       case 9:
/*  377 */         if (frameParser.parser instanceof Http2PushPromiseParser)
/*  378 */         { if (!isClient()) {
/*  379 */             sendGoAway(1);
/*  380 */             throw UndertowMessages.MESSAGES.serverReceivedPushPromise();
/*      */           } 
/*  382 */           Http2PushPromiseParser pushPromiseParser = (Http2PushPromiseParser)frameParser.parser;
/*  383 */           AbstractHttp2StreamSourceChannel abstractHttp2StreamSourceChannel = new Http2PushPromiseStreamSourceChannel(this, frameData, frameParser.getFrameLength(), pushPromiseParser.getHeaderMap(), pushPromiseParser.getPromisedStreamId(), frameParser.streamId);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  536 */           return abstractHttp2StreamSourceChannel; } case 1: if (!isIdle(frameParser.streamId)) { StreamHolder existing = this.currentStreams.get(Integer.valueOf(frameParser.streamId)); if (existing == null || existing.sourceClosed) { sendGoAway(1); frameData.close(); return null; }  if (existing.sourceChannel != null) if (!Bits.allAreSet(frameParser.flags, 1)) { sendGoAway(1); frameData.close(); return null; }   } else { if (frameParser.streamId < getLastAssignedStreamOtherSide()) { sendGoAway(1); frameData.close(); return null; }  if (frameParser.streamId % 2 == (isClient() ? 1 : 0)) { sendGoAway(1); frameData.close(); return null; }  }  http2HeadersParser = (Http2HeadersParser)frameParser.parser; channel = new Http2StreamSourceChannel(this, frameData, frameHeaderData.getFrameLength(), http2HeadersParser.getHeaderMap(), frameParser.streamId); updateStreamIdsCountersInHeaders(frameParser.streamId); holder = this.currentStreams.get(Integer.valueOf(frameParser.streamId)); if (holder == null) { receiveConcurrentStreamsAtomicUpdater.getAndIncrement(this); this.currentStreams.put(Integer.valueOf(frameParser.streamId), holder = new StreamHolder((Http2StreamSourceChannel)channel)); } else { holder.sourceChannel = (Http2StreamSourceChannel)channel; }  if (http2HeadersParser.isHeadersEndStream() && Bits.allAreSet(frameParser.flags, 4)) { channel.lastFrame(); holder.sourceChannel = null; if (!isClient() || !"100".equals(http2HeadersParser.getHeaderMap().getFirst(STATUS))) { holder.sourceClosed = true; if (holder.sinkClosed) { receiveConcurrentStreamsAtomicUpdater.getAndDecrement(this); this.currentStreams.remove(Integer.valueOf(frameParser.streamId)); }  }  }  if (http2HeadersParser.isInvalid()) { channel.rstStream(1); sendRstStream(frameParser.streamId, 1); channel = null; }  if (http2HeadersParser.getDependentStreamId() == frameParser.streamId) { sendRstStream(frameParser.streamId, 1); frameData.close(); return null; }  return channel;case 3: http2RstStreamParser = (Http2RstStreamParser)frameParser.parser; if (frameParser.streamId == 0) { if (frameData != null) frameData.close();  throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.streamIdMustNotBeZeroForFrameType(3)); }  channel = new Http2RstStreamStreamSourceChannel(this, frameData, http2RstStreamParser.getErrorCode(), frameParser.streamId); handleRstStream(frameParser.streamId); if (isIdle(frameParser.streamId)) sendGoAway(1);  return channel;case 4: if (!Bits.anyAreSet(frameParser.flags, 1)) { if (updateSettings(((Http2SettingsParser)frameParser.parser).getSettings())) sendSettingsAck();  } else if (frameHeaderData.getFrameLength() != 0L) { sendGoAway(6); frameData.close(); return null; }  channel = new Http2SettingsStreamSourceChannel(this, frameData, frameParser.getFrameLength(), ((Http2SettingsParser)frameParser.parser).getSettings()); this.unackedReceiveMaxFrameSize = this.receiveMaxFrameSize; return channel;case 6: pingParser = (Http2PingParser)frameParser.parser; frameData.close(); ack = Bits.anyAreSet(frameParser.flags, 1); channel = new Http2PingStreamSourceChannel(this, pingParser.getData(), ack); if (!ack) sendPing(pingParser.getData(), new Http2ControlMessageExceptionHandler(), true);  return channel;case 7: http2GoAwayParser = (Http2GoAwayParser)frameParser.parser; channel = new Http2GoAwayStreamSourceChannel(this, frameData, frameParser.getFrameLength(), http2GoAwayParser.getStatusCode(), http2GoAwayParser.getLastGoodStreamId()); this.peerGoneAway = true; for (StreamHolder streamHolder : this.currentStreams.values()) { if (streamHolder.sourceChannel != null) streamHolder.sourceChannel.rstStream();  if (streamHolder.sinkChannel != null) streamHolder.sinkChannel.rstStream();  }  frameData.close(); sendGoAway(0); return channel;
/*      */       case 8:
/*      */         http2WindowUpdateParser = (Http2WindowUpdateParser)frameParser.parser; handleWindowUpdate(frameParser.streamId, http2WindowUpdateParser.getDeltaWindowSize()); frameData.close(); return null;
/*      */       case 2:
/*      */         parser = (Http2PriorityParser)frameParser.parser; if (parser.getStreamDependency() == frameParser.streamId) { sendRstStream(frameParser.streamId, 1); return null; }
/*      */          frameData.close(); return null;
/*      */     }  UndertowLogger.REQUEST_LOGGER.tracef("Dropping frame of length %s and type %s for stream %s as we do not understand this type of frame", frameParser.getFrameLength(), frameParser.type, frameParser.streamId); frameData.close(); return null; } protected FrameHeaderData parseFrame(ByteBuffer data) throws IOException { Http2FrameHeaderParser frameParser; do {
/*  543 */       frameParser = parseFrameNoContinuation(data);
/*      */     
/*      */     }
/*  546 */     while (frameParser != null && frameParser.getContinuationParser() != null && data.hasRemaining());
/*  547 */     return frameParser; }
/*      */ 
/*      */   
/*      */   private Http2FrameHeaderParser parseFrameNoContinuation(ByteBuffer data) throws IOException {
/*  551 */     if (this.prefaceCount < PREFACE_BYTES.length) {
/*  552 */       while (data.hasRemaining() && this.prefaceCount < PREFACE_BYTES.length) {
/*  553 */         if (data.get() != PREFACE_BYTES[this.prefaceCount]) {
/*  554 */           IoUtils.safeClose((Closeable)getUnderlyingConnection());
/*  555 */           throw UndertowMessages.MESSAGES.incorrectHttp2Preface();
/*      */         } 
/*  557 */         this.prefaceCount++;
/*      */       } 
/*      */     }
/*  560 */     Http2FrameHeaderParser frameParser = this.frameParser;
/*  561 */     if (frameParser == null) {
/*  562 */       this.frameParser = frameParser = new Http2FrameHeaderParser(this, this.continuationParser);
/*  563 */       this.continuationParser = null;
/*      */     } 
/*  565 */     if (!frameParser.handle(data)) {
/*  566 */       return null;
/*      */     }
/*  568 */     if (!this.initialSettingsReceived) {
/*  569 */       if (frameParser.type != 4) {
/*  570 */         UndertowLogger.REQUEST_IO_LOGGER.remoteEndpointFailedToSendInitialSettings(frameParser.type);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  576 */         markReadsBroken(new IOException());
/*      */       } else {
/*  578 */         this.initialSettingsReceived = true;
/*      */       } 
/*      */     }
/*  581 */     this.frameParser = null;
/*  582 */     if (frameParser.getActualLength() > this.receiveMaxFrameSize && frameParser.getActualLength() > this.unackedReceiveMaxFrameSize) {
/*  583 */       sendGoAway(6);
/*  584 */       throw UndertowMessages.MESSAGES.http2FrameTooLarge();
/*      */     } 
/*  586 */     if (frameParser.getContinuationParser() != null) {
/*  587 */       this.continuationParser = frameParser.getContinuationParser();
/*      */     }
/*  589 */     return frameParser;
/*      */   }
/*      */   
/*      */   protected void lastDataRead() {
/*  593 */     this.lastDataRead = true;
/*  594 */     if (!this.peerGoneAway) {
/*      */       
/*  596 */       IoUtils.safeClose((Closeable)this);
/*      */     } else {
/*  598 */       this.peerGoneAway = true;
/*  599 */       if (!this.thisGoneAway)
/*      */       {
/*  601 */         sendGoAway(10);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected boolean isLastFrameReceived() {
/*  608 */     return this.lastDataRead;
/*      */   }
/*      */ 
/*      */   
/*      */   protected boolean isLastFrameSent() {
/*  613 */     return this.thisGoneAway;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void handleBrokenSourceChannel(Throwable e) {
/*  618 */     UndertowLogger.REQUEST_LOGGER.debugf(e, "Closing HTTP2 channel to %s due to broken read side", getPeerAddress());
/*  619 */     if (e instanceof ConnectionErrorException) {
/*  620 */       sendGoAway(((ConnectionErrorException)e).getCode(), new Http2ControlMessageExceptionHandler());
/*      */     } else {
/*  622 */       sendGoAway((e instanceof ClosedChannelException) ? 10 : 1, new Http2ControlMessageExceptionHandler());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void handleBrokenSinkChannel(Throwable e) {
/*  628 */     UndertowLogger.REQUEST_LOGGER.debugf(e, "Closing HTTP2 channel to %s due to broken write side", getPeerAddress());
/*      */ 
/*      */     
/*  631 */     IoUtils.safeClose((Closeable)this);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void closeSubChannels() {
/*  637 */     for (Map.Entry<Integer, StreamHolder> e : this.currentStreams.entrySet()) {
/*  638 */       StreamHolder holder = e.getValue();
/*  639 */       AbstractHttp2StreamSourceChannel receiver = holder.sourceChannel;
/*  640 */       if (receiver != null) {
/*  641 */         receiver.markStreamBroken();
/*      */       }
/*  643 */       Http2StreamSinkChannel sink = holder.sinkChannel;
/*  644 */       if (sink != null) {
/*  645 */         if (sink.isWritesShutdown()) {
/*  646 */           ChannelListeners.invokeChannelListener((Executor)sink.getIoThread(), (Channel)sink, ((ChannelListener.SimpleSetter)sink.getWriteSetter()).get());
/*      */         }
/*  648 */         IoUtils.safeClose((Closeable)sink);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected Collection<AbstractFramedStreamSourceChannel<Http2Channel, AbstractHttp2StreamSourceChannel, AbstractHttp2StreamSinkChannel>> getReceivers() {
/*  656 */     List<AbstractFramedStreamSourceChannel<Http2Channel, AbstractHttp2StreamSourceChannel, AbstractHttp2StreamSinkChannel>> channels = new ArrayList<>(this.currentStreams.size());
/*  657 */     for (Map.Entry<Integer, StreamHolder> entry : this.currentStreams.entrySet()) {
/*  658 */       if (!((StreamHolder)entry.getValue()).sourceClosed) {
/*  659 */         channels.add(((StreamHolder)entry.getValue()).sourceChannel);
/*      */       }
/*      */     } 
/*  662 */     return channels;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean updateSettings(List<Http2Setting> settings) {
/*  671 */     for (Http2Setting setting : settings) {
/*  672 */       if (setting.getId() == 4) {
/*  673 */         synchronized (this.flowControlLock) {
/*  674 */           if (setting.getValue() > 2147483647L) {
/*  675 */             sendGoAway(3);
/*  676 */             return false;
/*      */           } 
/*  678 */           this.initialSendWindowSize = (int)setting.getValue();
/*      */         }  continue;
/*      */       } 
/*  681 */       if (setting.getId() == 5) {
/*  682 */         if (setting.getValue() > 16777215L || setting.getValue() < 16384L) {
/*  683 */           UndertowLogger.REQUEST_IO_LOGGER.debug("Invalid value received for SETTINGS_MAX_FRAME_SIZE " + setting.getValue());
/*  684 */           sendGoAway(1);
/*  685 */           return false;
/*      */         } 
/*  687 */         this.sendMaxFrameSize = (int)setting.getValue(); continue;
/*  688 */       }  if (setting.getId() == 1) {
/*  689 */         synchronized (this) {
/*  690 */           this.encoder.setMaxTableSize((int)setting.getValue());
/*      */         }  continue;
/*  692 */       }  if (setting.getId() == 2) {
/*      */         
/*  694 */         int result = (int)setting.getValue();
/*      */ 
/*      */         
/*  697 */         if (result == 0) {
/*  698 */           this.pushEnabled = false; continue;
/*  699 */         }  if (result != 1) {
/*      */           
/*  701 */           UndertowLogger.REQUEST_IO_LOGGER.debug("Invalid value received for SETTINGS_ENABLE_PUSH " + result);
/*  702 */           sendGoAway(1);
/*  703 */           return false;
/*      */         }  continue;
/*  705 */       }  if (setting.getId() == 3) {
/*  706 */         this.sendMaxConcurrentStreams = (int)setting.getValue();
/*      */       }
/*      */     } 
/*      */     
/*  710 */     return true;
/*      */   }
/*      */   
/*      */   public int getHttp2Version() {
/*  714 */     return 3;
/*      */   }
/*      */   
/*      */   public int getInitialSendWindowSize() {
/*  718 */     return this.initialSendWindowSize;
/*      */   }
/*      */   
/*      */   public int getInitialReceiveWindowSize() {
/*  722 */     return this.initialReceiveWindowSize;
/*      */   }
/*      */   
/*      */   public int getSendMaxConcurrentStreams() {
/*  726 */     return this.sendMaxConcurrentStreams;
/*      */   }
/*      */   
/*      */   public void setSendMaxConcurrentStreams(int sendMaxConcurrentStreams) {
/*  730 */     this.sendMaxConcurrentStreams = sendMaxConcurrentStreams;
/*  731 */     sendSettings();
/*      */   }
/*      */   
/*      */   public int getReceiveMaxConcurrentStreams() {
/*  735 */     return this.receiveMaxConcurrentStreams;
/*      */   }
/*      */   
/*      */   public void handleWindowUpdate(int streamId, int deltaWindowSize) throws IOException {
/*  739 */     if (streamId == 0) {
/*  740 */       if (deltaWindowSize == 0) {
/*  741 */         UndertowLogger.REQUEST_IO_LOGGER.debug("Invalid flow-control window increment of 0 received with WINDOW_UPDATE frame for connection");
/*  742 */         sendGoAway(1);
/*      */         
/*      */         return;
/*      */       } 
/*  746 */       synchronized (this.flowControlLock) {
/*  747 */         boolean exhausted = (this.sendWindowSize <= 2L);
/*      */         
/*  749 */         this.sendWindowSize += deltaWindowSize;
/*  750 */         if (exhausted) {
/*  751 */           notifyFlowControlAllowed();
/*      */         }
/*  753 */         if (this.sendWindowSize > 2147483647L) {
/*  754 */           sendGoAway(3);
/*      */         }
/*      */       } 
/*      */     } else {
/*  758 */       if (deltaWindowSize == 0) {
/*  759 */         UndertowLogger.REQUEST_IO_LOGGER.debug("Invalid flow-control window increment of 0 received with WINDOW_UPDATE frame for stream " + streamId);
/*  760 */         sendRstStream(streamId, 1);
/*      */         return;
/*      */       } 
/*  763 */       StreamHolder holder = this.currentStreams.get(Integer.valueOf(streamId));
/*  764 */       Http2StreamSinkChannel stream = (holder != null) ? holder.sinkChannel : null;
/*  765 */       if (stream == null) {
/*  766 */         if (isIdle(streamId)) {
/*  767 */           sendGoAway(1);
/*      */         }
/*      */       } else {
/*  770 */         stream.updateFlowControlWindow(deltaWindowSize);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   synchronized void notifyFlowControlAllowed() throws IOException {
/*  776 */     recalculateHeldFrames();
/*      */   }
/*      */   
/*      */   public void sendPing(byte[] data) {
/*  780 */     sendPing(data, new Http2ControlMessageExceptionHandler());
/*      */   }
/*      */   
/*      */   public void sendPing(byte[] data, ChannelExceptionHandler<AbstractHttp2StreamSinkChannel> exceptionHandler) {
/*  784 */     sendPing(data, exceptionHandler, false);
/*      */   }
/*      */   
/*      */   void sendPing(byte[] data, ChannelExceptionHandler<AbstractHttp2StreamSinkChannel> exceptionHandler, boolean ack) {
/*  788 */     Http2PingStreamSinkChannel ping = new Http2PingStreamSinkChannel(this, data, ack);
/*      */     try {
/*  790 */       ping.shutdownWrites();
/*  791 */       if (!ping.flush()) {
/*  792 */         ping.getWriteSetter().set(ChannelListeners.flushingChannelListener(null, exceptionHandler));
/*  793 */         ping.resumeWrites();
/*      */       } 
/*  795 */     } catch (IOException e) {
/*  796 */       if (exceptionHandler != null) {
/*  797 */         exceptionHandler.handleException((Channel)ping, e);
/*      */       } else {
/*  799 */         UndertowLogger.REQUEST_LOGGER.debug("Failed to send ping and no exception handler set", e);
/*      */       } 
/*  801 */     } catch (Throwable t) {
/*  802 */       if (exceptionHandler != null) {
/*  803 */         exceptionHandler.handleException((Channel)ping, new IOException(t));
/*      */       } else {
/*  805 */         UndertowLogger.REQUEST_LOGGER.debug("Failed to send ping and no exception handler set", t);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void sendGoAway(int status) {
/*  811 */     sendGoAway(status, new Http2ControlMessageExceptionHandler());
/*      */   }
/*      */   
/*      */   public void sendGoAway(int status, ChannelExceptionHandler<AbstractHttp2StreamSinkChannel> exceptionHandler) {
/*  815 */     if (this.thisGoneAway) {
/*      */       return;
/*      */     }
/*  818 */     this.thisGoneAway = true;
/*  819 */     if (UndertowLogger.REQUEST_IO_LOGGER.isTraceEnabled()) {
/*  820 */       UndertowLogger.REQUEST_IO_LOGGER.tracef(new ClosedChannelException(), "Sending goaway on channel %s", this);
/*      */     }
/*  822 */     Http2GoAwayStreamSinkChannel goAway = new Http2GoAwayStreamSinkChannel(this, status, getLastGoodStreamId());
/*      */     try {
/*  824 */       goAway.shutdownWrites();
/*  825 */       if (!goAway.flush()) {
/*  826 */         goAway.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<Channel>()
/*      */               {
/*      */                 public void handleEvent(Channel channel) {
/*  829 */                   IoUtils.safeClose((Closeable)Http2Channel.this);
/*      */                 }
/*      */               },  exceptionHandler));
/*  832 */         goAway.resumeWrites();
/*      */       } else {
/*  834 */         IoUtils.safeClose((Closeable)this);
/*      */       } 
/*  836 */     } catch (IOException e) {
/*  837 */       exceptionHandler.handleException((Channel)goAway, e);
/*  838 */     } catch (Throwable t) {
/*  839 */       exceptionHandler.handleException((Channel)goAway, new IOException(t));
/*      */     } 
/*      */   }
/*      */   
/*      */   public void sendUpdateWindowSize(int streamId, int delta) throws IOException {
/*  844 */     Http2WindowUpdateStreamSinkChannel windowUpdateStreamSinkChannel = new Http2WindowUpdateStreamSinkChannel(this, streamId, delta);
/*  845 */     flushChannel((StreamSinkChannel)windowUpdateStreamSinkChannel);
/*      */   }
/*      */ 
/*      */   
/*      */   public SSLSession getSslSession() {
/*  850 */     StreamConnection con = getUnderlyingConnection();
/*  851 */     if (con instanceof SslConnection) {
/*  852 */       return ((SslConnection)con).getSslSession();
/*      */     }
/*  854 */     return null;
/*      */   }
/*      */   
/*      */   public void updateReceiveFlowControlWindow(int read) throws IOException {
/*  858 */     if (read <= 0) {
/*      */       return;
/*      */     }
/*  861 */     int delta = -1;
/*  862 */     synchronized (this.flowControlLock) {
/*  863 */       this.receiveWindowSize -= read;
/*      */       
/*  865 */       int initialWindowSize = this.initialReceiveWindowSize;
/*  866 */       if (this.receiveWindowSize < initialWindowSize / 2) {
/*  867 */         delta = initialWindowSize - this.receiveWindowSize;
/*  868 */         this.receiveWindowSize += delta;
/*      */       } 
/*      */     } 
/*  871 */     if (delta > 0) {
/*  872 */       sendUpdateWindowSize(0, delta);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Http2HeadersStreamSinkChannel createStream(HeaderMap requestHeaders) throws IOException {
/*  884 */     if (!isClient()) {
/*  885 */       throw UndertowMessages.MESSAGES.headersStreamCanOnlyBeCreatedByClient();
/*      */     }
/*  887 */     if (!isOpen()) {
/*  888 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*      */     }
/*  890 */     sendConcurrentStreamsAtomicUpdater.incrementAndGet(this);
/*  891 */     if (this.sendMaxConcurrentStreams > 0 && this.sendConcurrentStreams > this.sendMaxConcurrentStreams) {
/*  892 */       throw UndertowMessages.MESSAGES.streamLimitExceeded();
/*      */     }
/*  894 */     int streamId = this.streamIdCounter;
/*  895 */     this.streamIdCounter += 2;
/*  896 */     Http2HeadersStreamSinkChannel http2SynStreamStreamSinkChannel = new Http2HeadersStreamSinkChannel(this, streamId, requestHeaders);
/*  897 */     this.currentStreams.put(Integer.valueOf(streamId), new StreamHolder(http2SynStreamStreamSinkChannel));
/*      */     
/*  899 */     return http2SynStreamStreamSinkChannel;
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
/*      */   public synchronized boolean addPushPromiseStream(int pushedStreamId) throws IOException {
/*  911 */     if (!isClient() || pushedStreamId % 2 != 0) {
/*  912 */       throw UndertowMessages.MESSAGES.pushPromiseCanOnlyBeCreatedByServer();
/*      */     }
/*  914 */     if (!isOpen()) {
/*  915 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*      */     }
/*  917 */     if (!isIdle(pushedStreamId)) {
/*  918 */       UndertowLogger.REQUEST_IO_LOGGER.debugf("Non idle streamId %d received from the server as a pushed stream.", pushedStreamId);
/*  919 */       return false;
/*      */     } 
/*  921 */     StreamHolder holder = new StreamHolder((Http2HeadersStreamSinkChannel)null);
/*  922 */     holder.sinkClosed = true;
/*  923 */     this.lastAssignedStreamOtherSide = Math.max(this.lastAssignedStreamOtherSide, pushedStreamId);
/*  924 */     this.currentStreams.put(Integer.valueOf(pushedStreamId), holder);
/*  925 */     return true;
/*      */   }
/*      */   
/*      */   private synchronized int getLastAssignedStreamOtherSide() {
/*  929 */     return this.lastAssignedStreamOtherSide;
/*      */   }
/*      */   
/*      */   private synchronized int getLastGoodStreamId() {
/*  933 */     return this.lastGoodStreamId;
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
/*      */   private synchronized void updateStreamIdsCountersInHeaders(int streamNo) {
/*  946 */     if (streamNo % 2 != 0) {
/*      */       
/*  948 */       this.lastGoodStreamId = Math.max(this.lastGoodStreamId, streamNo);
/*  949 */       if (!isClient())
/*      */       {
/*  951 */         this.lastAssignedStreamOtherSide = this.lastGoodStreamId;
/*      */       }
/*  953 */     } else if (isClient()) {
/*      */       
/*  955 */       this.lastAssignedStreamOtherSide = Math.max(this.lastAssignedStreamOtherSide, streamNo);
/*      */     } 
/*      */   }
/*      */   
/*      */   public synchronized Http2HeadersStreamSinkChannel sendPushPromise(int associatedStreamId, HeaderMap requestHeaders, HeaderMap responseHeaders) throws IOException {
/*  960 */     if (!isOpen()) {
/*  961 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*      */     }
/*  963 */     if (isClient()) {
/*  964 */       throw UndertowMessages.MESSAGES.pushPromiseCanOnlyBeCreatedByServer();
/*      */     }
/*  966 */     sendConcurrentStreamsAtomicUpdater.incrementAndGet(this);
/*  967 */     if (this.sendMaxConcurrentStreams > 0 && this.sendConcurrentStreams > this.sendMaxConcurrentStreams) {
/*  968 */       throw UndertowMessages.MESSAGES.streamLimitExceeded();
/*      */     }
/*  970 */     int streamId = this.streamIdCounter;
/*  971 */     this.streamIdCounter += 2;
/*  972 */     Http2PushPromiseStreamSinkChannel pushPromise = new Http2PushPromiseStreamSinkChannel(this, requestHeaders, associatedStreamId, streamId);
/*  973 */     flushChannel((StreamSinkChannel)pushPromise);
/*      */     
/*  975 */     Http2HeadersStreamSinkChannel http2SynStreamStreamSinkChannel = new Http2HeadersStreamSinkChannel(this, streamId, responseHeaders);
/*  976 */     this.currentStreams.put(Integer.valueOf(streamId), new StreamHolder(http2SynStreamStreamSinkChannel));
/*  977 */     return http2SynStreamStreamSinkChannel;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int grabFlowControlBytes(int bytesToGrab) {
/*      */     int min;
/*  987 */     if (bytesToGrab <= 0) {
/*  988 */       return 0;
/*      */     }
/*      */     
/*  991 */     synchronized (this.flowControlLock) {
/*  992 */       min = (int)Math.min(bytesToGrab, this.sendWindowSize);
/*  993 */       if (bytesToGrab > 2 && min <= 2)
/*      */       {
/*  995 */         return 0;
/*      */       }
/*  997 */       min = Math.min(this.sendMaxFrameSize, min);
/*  998 */       this.sendWindowSize -= min;
/*      */     } 
/* 1000 */     return min;
/*      */   }
/*      */   
/*      */   void registerStreamSink(Http2HeadersStreamSinkChannel synResponse) {
/* 1004 */     StreamHolder existing = this.currentStreams.get(Integer.valueOf(synResponse.getStreamId()));
/* 1005 */     if (existing == null) {
/* 1006 */       throw UndertowMessages.MESSAGES.streamNotRegistered();
/*      */     }
/* 1008 */     existing.sinkChannel = synResponse;
/*      */   }
/*      */   
/*      */   void removeStreamSink(int streamId) {
/* 1012 */     StreamHolder existing = this.currentStreams.get(Integer.valueOf(streamId));
/* 1013 */     if (existing == null) {
/*      */       return;
/*      */     }
/* 1016 */     existing.sinkClosed = true;
/* 1017 */     existing.sinkChannel = null;
/* 1018 */     if (existing.sourceClosed) {
/* 1019 */       if (streamId % 2 == (isClient() ? 1 : 0)) {
/* 1020 */         sendConcurrentStreamsAtomicUpdater.getAndDecrement(this);
/*      */       } else {
/* 1022 */         receiveConcurrentStreamsAtomicUpdater.getAndDecrement(this);
/*      */       } 
/* 1024 */       this.currentStreams.remove(Integer.valueOf(streamId));
/*      */     } 
/* 1026 */     if (isLastFrameReceived() && this.currentStreams.isEmpty()) {
/* 1027 */       sendGoAway(0);
/* 1028 */     } else if (this.parseTimeoutUpdater != null && this.currentStreams.isEmpty()) {
/* 1029 */       this.parseTimeoutUpdater.connectionIdle();
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean isClient() {
/* 1034 */     return (this.streamIdCounter % 2 == 1);
/*      */   }
/*      */ 
/*      */   
/*      */   HpackEncoder getEncoder() {
/* 1039 */     return this.encoder;
/*      */   }
/*      */   
/*      */   HpackDecoder getDecoder() {
/* 1043 */     return this.decoder;
/*      */   }
/*      */   
/*      */   int getMaxHeaders() {
/* 1047 */     return this.maxHeaders;
/*      */   }
/*      */   
/*      */   int getPaddingBytes() {
/* 1051 */     if (this.paddingRandom == null) {
/* 1052 */       return 0;
/*      */     }
/* 1054 */     return this.paddingRandom.nextInt(this.maxPadding);
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> T getAttachment(AttachmentKey<T> key) {
/* 1059 */     if (key == null) {
/* 1060 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("key");
/*      */     }
/* 1062 */     return (T)this.attachments.get(key);
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> List<T> getAttachmentList(AttachmentKey<? extends List<T>> key) {
/* 1067 */     if (key == null) {
/* 1068 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("key");
/*      */     }
/* 1070 */     Object o = this.attachments.get(key);
/* 1071 */     if (o == null) {
/* 1072 */       return Collections.emptyList();
/*      */     }
/* 1074 */     return (List<T>)o;
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> T putAttachment(AttachmentKey<T> key, T value) {
/* 1079 */     if (key == null) {
/* 1080 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("key");
/*      */     }
/* 1082 */     return (T)key.cast(this.attachments.put(key, key.cast(value)));
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> T removeAttachment(AttachmentKey<T> key) {
/* 1087 */     return (T)key.cast(this.attachments.remove(key));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> void addToAttachmentList(AttachmentKey<AttachmentList<T>> key, T value) {
/* 1093 */     if (key == null) {
/* 1094 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("key");
/*      */     }
/* 1096 */     Map<AttachmentKey<?>, Object> attachments = this.attachments;
/* 1097 */     synchronized (attachments) {
/* 1098 */       List<T> list = (List<T>)key.cast(attachments.get(key));
/* 1099 */       if (list == null) {
/* 1100 */         AttachmentList<T> newList = new AttachmentList(Object.class);
/* 1101 */         attachments.put(key, newList);
/* 1102 */         newList.add(value);
/*      */       } else {
/* 1104 */         list.add(value);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void sendRstStream(int streamId, int statusCode) {
/* 1110 */     if (!isOpen()) {
/*      */       return;
/*      */     }
/*      */     
/* 1114 */     handleRstStream(streamId);
/* 1115 */     if (UndertowLogger.REQUEST_IO_LOGGER.isDebugEnabled()) {
/* 1116 */       UndertowLogger.REQUEST_IO_LOGGER.debugf(new ClosedChannelException(), "Sending rststream on channel %s stream %s", this, Integer.valueOf(streamId));
/*      */     }
/* 1118 */     Http2RstStreamSinkChannel channel = new Http2RstStreamSinkChannel(this, streamId, statusCode);
/* 1119 */     flushChannelIgnoreFailure((StreamSinkChannel)channel);
/*      */   }
/*      */   
/*      */   private void handleRstStream(int streamId) {
/* 1123 */     StreamHolder holder = this.currentStreams.remove(Integer.valueOf(streamId));
/* 1124 */     if (holder != null) {
/* 1125 */       if (streamId % 2 == (isClient() ? 1 : 0)) {
/* 1126 */         sendConcurrentStreamsAtomicUpdater.getAndDecrement(this);
/*      */       } else {
/* 1128 */         receiveConcurrentStreamsAtomicUpdater.getAndDecrement(this);
/*      */       } 
/* 1130 */       if (holder.sinkChannel != null) {
/* 1131 */         holder.sinkChannel.rstStream();
/*      */       }
/* 1133 */       if (holder.sourceChannel != null) {
/* 1134 */         holder.sourceChannel.rstStream();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized Http2HeadersStreamSinkChannel createInitialUpgradeResponseStream() {
/* 1145 */     if (this.lastGoodStreamId != 0) {
/* 1146 */       throw new IllegalStateException();
/*      */     }
/* 1148 */     updateStreamIdsCountersInHeaders(1);
/* 1149 */     Http2HeadersStreamSinkChannel stream = new Http2HeadersStreamSinkChannel(this, 1);
/* 1150 */     StreamHolder streamHolder = new StreamHolder(stream);
/* 1151 */     streamHolder.sourceClosed = true;
/* 1152 */     this.currentStreams.put(Integer.valueOf(1), streamHolder);
/* 1153 */     receiveConcurrentStreamsAtomicUpdater.getAndIncrement(this);
/* 1154 */     return stream;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isPushEnabled() {
/* 1159 */     return this.pushEnabled;
/*      */   }
/*      */   
/*      */   public boolean isPeerGoneAway() {
/* 1163 */     return this.peerGoneAway;
/*      */   }
/*      */   
/*      */   public boolean isThisGoneAway() {
/* 1167 */     return this.thisGoneAway;
/*      */   }
/*      */   
/*      */   Http2StreamSourceChannel removeStreamSource(int streamId) {
/* 1171 */     StreamHolder existing = this.currentStreams.get(Integer.valueOf(streamId));
/* 1172 */     if (existing == null) {
/* 1173 */       return null;
/*      */     }
/* 1175 */     existing.sourceClosed = true;
/* 1176 */     Http2StreamSourceChannel ret = existing.sourceChannel;
/* 1177 */     existing.sourceChannel = null;
/* 1178 */     if (existing.sinkClosed) {
/* 1179 */       if (streamId % 2 == (isClient() ? 1 : 0)) {
/* 1180 */         sendConcurrentStreamsAtomicUpdater.getAndDecrement(this);
/*      */       } else {
/* 1182 */         receiveConcurrentStreamsAtomicUpdater.getAndDecrement(this);
/*      */       } 
/* 1184 */       this.currentStreams.remove(Integer.valueOf(streamId));
/*      */     } 
/* 1186 */     return ret;
/*      */   }
/*      */   
/*      */   Http2StreamSourceChannel getIncomingStream(int streamId) {
/* 1190 */     StreamHolder existing = this.currentStreams.get(Integer.valueOf(streamId));
/* 1191 */     if (existing == null) {
/* 1192 */       return null;
/*      */     }
/* 1194 */     return existing.sourceChannel;
/*      */   }
/*      */   
/*      */   private class Http2ControlMessageExceptionHandler implements ChannelExceptionHandler<AbstractHttp2StreamSinkChannel> { private Http2ControlMessageExceptionHandler() {}
/*      */     
/*      */     public void handleException(AbstractHttp2StreamSinkChannel channel, IOException exception) {
/* 1200 */       IoUtils.safeClose((Closeable)channel);
/* 1201 */       Http2Channel.this.handleBrokenSinkChannel(exception);
/*      */     } }
/*      */ 
/*      */   
/*      */   public int getReceiveMaxFrameSize() {
/* 1206 */     return this.receiveMaxFrameSize;
/*      */   }
/*      */   
/*      */   public int getSendMaxFrameSize() {
/* 1210 */     return this.sendMaxFrameSize;
/*      */   }
/*      */   
/*      */   public String getProtocol() {
/* 1214 */     return this.protocol;
/*      */   }
/*      */   
/*      */   private synchronized boolean isIdle(int streamNo) {
/* 1218 */     if (streamNo % 2 == this.streamIdCounter % 2)
/*      */     {
/* 1220 */       return (streamNo >= this.streamIdCounter);
/*      */     }
/*      */     
/* 1223 */     return (streamNo > this.lastAssignedStreamOtherSide);
/*      */   }
/*      */ 
/*      */   
/*      */   int getMaxHeaderListSize() {
/* 1228 */     return this.maxHeaderListSize;
/*      */   }
/*      */   
/*      */   private static final class StreamHolder {
/*      */     boolean sourceClosed = false;
/*      */     boolean sinkClosed = false;
/*      */     Http2StreamSourceChannel sourceChannel;
/*      */     Http2StreamSinkChannel sinkChannel;
/*      */     
/*      */     StreamHolder(Http2StreamSourceChannel sourceChannel) {
/* 1238 */       this.sourceChannel = sourceChannel;
/*      */     }
/*      */     
/*      */     StreamHolder(Http2StreamSinkChannel sinkChannel) {
/* 1242 */       this.sinkChannel = sinkChannel;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2Channel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */