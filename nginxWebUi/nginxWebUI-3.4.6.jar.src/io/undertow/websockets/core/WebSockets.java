/*      */ package io.undertow.websockets.core;
/*      */ 
/*      */ import io.undertow.connector.PooledByteBuffer;
/*      */ import io.undertow.util.ImmediatePooledByteBuffer;
/*      */ import io.undertow.util.WorkerUtils;
/*      */ import java.io.Closeable;
/*      */ import java.io.IOException;
/*      */ import java.nio.Buffer;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.Channel;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import org.xnio.Buffers;
/*      */ import org.xnio.ChannelExceptionHandler;
/*      */ import org.xnio.ChannelListener;
/*      */ import org.xnio.ChannelListeners;
/*      */ import org.xnio.IoUtils;
/*      */ import org.xnio.XnioExecutor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class WebSockets
/*      */ {
/*      */   public static void sendText(String message, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*   50 */     sendText(message, wsChannel, callback, (Void)null);
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
/*      */   public static <T> void sendText(String message, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*   62 */     ByteBuffer data = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
/*   63 */     sendInternal(data, WebSocketFrameType.TEXT, wsChannel, callback, context, -1L);
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
/*      */   public static void sendText(String message, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
/*   75 */     sendText(message, wsChannel, callback, (Void)null, timeoutmillis);
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
/*      */   public static <T> void sendText(String message, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
/*   88 */     ByteBuffer data = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
/*   89 */     sendInternal(data, WebSocketFrameType.TEXT, wsChannel, callback, context, timeoutmillis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendText(ByteBuffer message, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  100 */     sendInternal(message, WebSocketFrameType.TEXT, wsChannel, callback, (Void)null, -1L);
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
/*      */   public static <T> void sendText(ByteBuffer message, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  112 */     sendInternal(message, WebSocketFrameType.TEXT, wsChannel, callback, context, -1L);
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
/*      */   public static void sendText(ByteBuffer message, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
/*  124 */     sendInternal(message, WebSocketFrameType.TEXT, wsChannel, callback, (Void)null, timeoutmillis);
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
/*      */   public static <T> void sendText(ByteBuffer message, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
/*  137 */     sendInternal(message, WebSocketFrameType.TEXT, wsChannel, callback, context, timeoutmillis);
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
/*      */   public static void sendText(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  149 */     sendInternal(pooledData, WebSocketFrameType.TEXT, wsChannel, callback, (Void)null, -1L);
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
/*      */   public static <T> void sendText(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  162 */     sendInternal(pooledData, WebSocketFrameType.TEXT, wsChannel, callback, context, -1L);
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
/*      */   public static void sendText(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
/*  175 */     sendInternal(pooledData, WebSocketFrameType.TEXT, wsChannel, callback, (Void)null, timeoutmillis);
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
/*      */   public static <T> void sendText(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
/*  189 */     sendInternal(pooledData, WebSocketFrameType.TEXT, wsChannel, callback, context, timeoutmillis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendTextBlocking(String message, WebSocketChannel wsChannel) throws IOException {
/*  199 */     ByteBuffer data = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
/*  200 */     sendBlockingInternal(data, WebSocketFrameType.TEXT, wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendTextBlocking(ByteBuffer message, WebSocketChannel wsChannel) throws IOException {
/*  210 */     sendBlockingInternal(message, WebSocketFrameType.TEXT, wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendTextBlocking(PooledByteBuffer pooledData, WebSocketChannel wsChannel) throws IOException {
/*  221 */     sendBlockingInternal(pooledData, WebSocketFrameType.TEXT, wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendPing(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  232 */     sendInternal(data, WebSocketFrameType.PING, wsChannel, callback, (Void)null, -1L);
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
/*      */   public static <T> void sendPing(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  244 */     sendInternal(data, WebSocketFrameType.PING, wsChannel, callback, context, -1L);
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
/*      */   public static void sendPing(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
/*  256 */     sendInternal(data, WebSocketFrameType.PING, wsChannel, callback, (Void)null, timeoutmillis);
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
/*      */   public static <T> void sendPing(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
/*  269 */     sendInternal(data, WebSocketFrameType.PING, wsChannel, callback, context, timeoutmillis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendPing(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  280 */     sendInternal(mergeBuffers(data), WebSocketFrameType.PING, wsChannel, callback, (Void)null, -1L);
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
/*      */   public static <T> void sendPing(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  292 */     sendInternal(mergeBuffers(data), WebSocketFrameType.PING, wsChannel, callback, context, -1L);
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
/*      */   public static void sendPing(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
/*  304 */     sendInternal(mergeBuffers(data), WebSocketFrameType.PING, wsChannel, callback, (Void)null, timeoutmillis);
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
/*      */   public static <T> void sendPing(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
/*  317 */     sendInternal(mergeBuffers(data), WebSocketFrameType.PING, wsChannel, callback, context, timeoutmillis);
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
/*      */   public static void sendPing(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  329 */     sendInternal(pooledData, WebSocketFrameType.PING, wsChannel, callback, (Void)null, -1L);
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
/*      */   public static <T> void sendPing(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  342 */     sendInternal(pooledData, WebSocketFrameType.PING, wsChannel, callback, context, -1L);
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
/*      */   public static void sendPing(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
/*  355 */     sendInternal(pooledData, WebSocketFrameType.PING, wsChannel, callback, (Void)null, timeoutmillis);
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
/*      */   public static <T> void sendPing(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
/*  369 */     sendInternal(pooledData, WebSocketFrameType.PING, wsChannel, callback, context, timeoutmillis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendPingBlocking(ByteBuffer data, WebSocketChannel wsChannel) throws IOException {
/*  379 */     sendBlockingInternal(data, WebSocketFrameType.PING, wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendPingBlocking(ByteBuffer[] data, WebSocketChannel wsChannel) throws IOException {
/*  389 */     sendBlockingInternal(mergeBuffers(data), WebSocketFrameType.PING, wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendPingBlocking(PooledByteBuffer pooledData, WebSocketChannel wsChannel) throws IOException {
/*  400 */     sendBlockingInternal(pooledData, WebSocketFrameType.PING, wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendPong(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  411 */     sendInternal(data, WebSocketFrameType.PONG, wsChannel, callback, (Void)null, -1L);
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
/*      */   public static <T> void sendPong(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  423 */     sendInternal(data, WebSocketFrameType.PONG, wsChannel, callback, context, -1L);
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
/*      */   public static void sendPong(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
/*  435 */     sendInternal(data, WebSocketFrameType.PONG, wsChannel, callback, (Void)null, timeoutmillis);
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
/*      */   public static <T> void sendPong(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
/*  448 */     sendInternal(data, WebSocketFrameType.PONG, wsChannel, callback, context, timeoutmillis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendPong(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  459 */     sendInternal(mergeBuffers(data), WebSocketFrameType.PONG, wsChannel, callback, (Void)null, -1L);
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
/*      */   public static <T> void sendPong(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  471 */     sendInternal(mergeBuffers(data), WebSocketFrameType.PONG, wsChannel, callback, context, -1L);
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
/*      */   public static void sendPong(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
/*  483 */     sendInternal(mergeBuffers(data), WebSocketFrameType.PONG, wsChannel, callback, (Void)null, timeoutmillis);
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
/*      */   public static <T> void sendPong(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
/*  496 */     sendInternal(mergeBuffers(data), WebSocketFrameType.PONG, wsChannel, callback, context, timeoutmillis);
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
/*      */   public static void sendPong(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  508 */     sendInternal(pooledData, WebSocketFrameType.PONG, wsChannel, callback, (Void)null, -1L);
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
/*      */   public static <T> void sendPong(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  521 */     sendInternal(pooledData, WebSocketFrameType.PONG, wsChannel, callback, context, -1L);
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
/*      */   public static void sendPong(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
/*  534 */     sendInternal(pooledData, WebSocketFrameType.PONG, wsChannel, callback, (Void)null, timeoutmillis);
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
/*      */   public static <T> void sendPong(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
/*  548 */     sendInternal(pooledData, WebSocketFrameType.PONG, wsChannel, callback, context, timeoutmillis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendPongBlocking(ByteBuffer data, WebSocketChannel wsChannel) throws IOException {
/*  558 */     sendBlockingInternal(data, WebSocketFrameType.PONG, wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendPongBlocking(ByteBuffer[] data, WebSocketChannel wsChannel) throws IOException {
/*  568 */     sendBlockingInternal(mergeBuffers(data), WebSocketFrameType.PONG, wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendPongBlocking(PooledByteBuffer pooledData, WebSocketChannel wsChannel) throws IOException {
/*  579 */     sendBlockingInternal(pooledData, WebSocketFrameType.PONG, wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendBinary(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  590 */     sendInternal(data, WebSocketFrameType.BINARY, wsChannel, callback, (Void)null, -1L);
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
/*      */   public static <T> void sendBinary(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  602 */     sendInternal(data, WebSocketFrameType.BINARY, wsChannel, callback, context, -1L);
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
/*      */   public static void sendBinary(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
/*  614 */     sendInternal(data, WebSocketFrameType.BINARY, wsChannel, callback, (Void)null, timeoutmillis);
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
/*      */   public static <T> void sendBinary(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
/*  627 */     sendInternal(data, WebSocketFrameType.BINARY, wsChannel, callback, context, timeoutmillis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendBinary(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  638 */     sendInternal(mergeBuffers(data), WebSocketFrameType.BINARY, wsChannel, callback, (Void)null, -1L);
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
/*      */   public static <T> void sendBinary(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  650 */     sendInternal(mergeBuffers(data), WebSocketFrameType.BINARY, wsChannel, callback, context, -1L);
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
/*      */   public static void sendBinary(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
/*  662 */     sendInternal(mergeBuffers(data), WebSocketFrameType.BINARY, wsChannel, callback, (Void)null, timeoutmillis);
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
/*      */   public static <T> void sendBinary(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
/*  675 */     sendInternal(mergeBuffers(data), WebSocketFrameType.BINARY, wsChannel, callback, context, timeoutmillis);
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
/*      */   public static void sendBinary(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  687 */     sendInternal(pooledData, WebSocketFrameType.BINARY, wsChannel, callback, (Void)null, -1L);
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
/*      */   public static <T> void sendBinary(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  700 */     sendInternal(pooledData, WebSocketFrameType.BINARY, wsChannel, callback, context, -1L);
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
/*      */   public static void sendBinary(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
/*  713 */     sendInternal(pooledData, WebSocketFrameType.BINARY, wsChannel, callback, (Void)null, timeoutmillis);
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
/*      */   public static <T> void sendBinary(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
/*  727 */     sendInternal(pooledData, WebSocketFrameType.BINARY, wsChannel, callback, context, timeoutmillis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendBinaryBlocking(ByteBuffer data, WebSocketChannel wsChannel) throws IOException {
/*  737 */     sendBlockingInternal(data, WebSocketFrameType.BINARY, wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendBinaryBlocking(ByteBuffer[] data, WebSocketChannel wsChannel) throws IOException {
/*  747 */     sendBlockingInternal(mergeBuffers(data), WebSocketFrameType.BINARY, wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendBinaryBlocking(PooledByteBuffer pooledData, WebSocketChannel wsChannel) throws IOException {
/*  758 */     sendBlockingInternal(pooledData, WebSocketFrameType.BINARY, wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendClose(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  769 */     CloseMessage sm = new CloseMessage(data);
/*  770 */     sendClose(sm, wsChannel, callback);
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
/*      */   public static <T> void sendClose(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  782 */     CloseMessage sm = new CloseMessage(data);
/*  783 */     sendClose(sm, wsChannel, callback, context);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendClose(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  794 */     CloseMessage sm = new CloseMessage(data);
/*  795 */     sendClose(sm, wsChannel, callback);
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
/*      */   public static <T> void sendClose(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  807 */     CloseMessage sm = new CloseMessage(data);
/*  808 */     sendClose(sm, wsChannel, callback, context);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendClose(int code, String reason, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  819 */     sendClose(new CloseMessage(code, reason), wsChannel, callback);
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
/*      */   public static <T> void sendClose(int code, String reason, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  831 */     sendClose(new CloseMessage(code, reason), wsChannel, callback, context);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendClose(CloseMessage closeMessage, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
/*  842 */     sendClose(closeMessage, wsChannel, callback, (Void)null);
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
/*      */   public static <T> void sendClose(CloseMessage closeMessage, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
/*  854 */     wsChannel.setCloseCode(closeMessage.getCode());
/*  855 */     wsChannel.setCloseReason(closeMessage.getReason());
/*  856 */     sendInternal(closeMessage.toByteBuffer(), WebSocketFrameType.CLOSE, wsChannel, callback, context, -1L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendCloseBlocking(CloseMessage closeMessage, WebSocketChannel wsChannel) throws IOException {
/*  866 */     wsChannel.setCloseReason(closeMessage.getReason());
/*  867 */     wsChannel.setCloseCode(closeMessage.getCode());
/*  868 */     sendBlockingInternal(closeMessage.toByteBuffer(), WebSocketFrameType.CLOSE, wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendCloseBlocking(int code, String reason, WebSocketChannel wsChannel) throws IOException {
/*  877 */     sendCloseBlocking(new CloseMessage(code, reason), wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendCloseBlocking(ByteBuffer data, WebSocketChannel wsChannel) throws IOException {
/*  886 */     sendCloseBlocking(new CloseMessage(data), wsChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sendCloseBlocking(ByteBuffer[] data, WebSocketChannel wsChannel) throws IOException {
/*  896 */     sendCloseBlocking(new CloseMessage(data), wsChannel);
/*      */   }
/*      */   
/*      */   private static <T> void sendInternal(ByteBuffer data, WebSocketFrameType type, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
/*  900 */     sendInternal((PooledByteBuffer)new ImmediatePooledByteBuffer(data), type, wsChannel, callback, context, timeoutmillis);
/*      */   }
/*      */   
/*      */   private static <T> void sendInternal(PooledByteBuffer pooledData, WebSocketFrameType type, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
/*  904 */     boolean closePooledData = true;
/*      */     try {
/*  906 */       StreamSinkFrameChannel channel = wsChannel.send(type);
/*      */       
/*  908 */       closePooledData = false;
/*  909 */       if (!channel.send(pooledData)) {
/*  910 */         throw WebSocketMessages.MESSAGES.unableToSendOnNewChannel();
/*      */       }
/*  912 */       flushChannelAsync(wsChannel, callback, channel, context, timeoutmillis);
/*  913 */     } catch (IOException e) {
/*  914 */       if (callback != null) {
/*  915 */         callback.onError(wsChannel, context, e);
/*      */       } else {
/*  917 */         IoUtils.safeClose((Closeable)wsChannel);
/*      */       } 
/*      */     } finally {
/*  920 */       if (closePooledData) {
/*  921 */         pooledData.close();
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private static <T> void flushChannelAsync(final WebSocketChannel wsChannel, final WebSocketCallback<T> callback, StreamSinkFrameChannel channel, final T context, long timeoutmillis) throws IOException {
/*  927 */     final WebSocketFrameType type = channel.getType();
/*  928 */     channel.shutdownWrites();
/*  929 */     if (!channel.flush()) {
/*  930 */       channel.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkFrameChannel>()
/*      */             {
/*      */               public void handleEvent(StreamSinkFrameChannel channel)
/*      */               {
/*  934 */                 if (callback != null) {
/*  935 */                   callback.complete(wsChannel, context);
/*      */                 }
/*  937 */                 if (type == WebSocketFrameType.CLOSE && wsChannel.isCloseFrameReceived()) {
/*  938 */                   IoUtils.safeClose((Closeable)wsChannel);
/*      */                 }
/*      */ 
/*      */                 
/*  942 */                 channel.getWriteSetter().set(null);
/*      */               }
/*      */             }new ChannelExceptionHandler<StreamSinkFrameChannel>()
/*      */             {
/*      */               public void handleException(StreamSinkFrameChannel channel, IOException exception) {
/*  947 */                 if (callback != null) {
/*  948 */                   callback.onError(wsChannel, context, exception);
/*      */                 }
/*  950 */                 IoUtils.safeClose(new Closeable[] { (Closeable)channel, (Closeable)this.val$wsChannel });
/*      */ 
/*      */                 
/*  953 */                 channel.getWriteSetter().set(null);
/*      */               }
/*      */             }));
/*      */       
/*  957 */       if (timeoutmillis > 0L) {
/*  958 */         setupTimeout(channel, timeoutmillis);
/*      */       }
/*  960 */       channel.resumeWrites();
/*      */       return;
/*      */     } 
/*  963 */     if (callback != null) {
/*  964 */       callback.complete(wsChannel, context);
/*      */     }
/*      */   }
/*      */   
/*      */   private static void setupTimeout(final StreamSinkFrameChannel channel, long timeoutmillis) {
/*  969 */     final XnioExecutor.Key key = WorkerUtils.executeAfter(channel.getIoThread(), new Runnable()
/*      */         {
/*      */           public void run() {
/*  972 */             if (channel.isOpen()) {
/*  973 */               IoUtils.safeClose((Closeable)channel);
/*      */             }
/*      */           }
/*      */         },  timeoutmillis, TimeUnit.MILLISECONDS);
/*  977 */     channel.getCloseSetter().set(new ChannelListener<StreamSinkFrameChannel>()
/*      */         {
/*      */           public void handleEvent(StreamSinkFrameChannel channel) {
/*  980 */             key.remove();
/*      */           }
/*      */         });
/*      */   }
/*      */   
/*      */   private static void sendBlockingInternal(ByteBuffer data, WebSocketFrameType type, WebSocketChannel wsChannel) throws IOException {
/*  986 */     sendBlockingInternal((PooledByteBuffer)new ImmediatePooledByteBuffer(data), type, wsChannel);
/*      */   }
/*      */   
/*      */   private static void sendBlockingInternal(PooledByteBuffer pooledData, WebSocketFrameType type, WebSocketChannel wsChannel) throws IOException {
/*  990 */     boolean closePooledData = true;
/*      */     try {
/*  992 */       StreamSinkFrameChannel channel = wsChannel.send(type);
/*      */       
/*  994 */       closePooledData = false;
/*  995 */       if (!channel.send(pooledData)) {
/*  996 */         throw WebSocketMessages.MESSAGES.unableToSendOnNewChannel();
/*      */       }
/*  998 */       channel.shutdownWrites();
/*  999 */       while (!channel.flush()) {
/* 1000 */         channel.awaitWritable();
/*      */       }
/* 1002 */       if (type == WebSocketFrameType.CLOSE && wsChannel.isCloseFrameReceived()) {
/* 1003 */         IoUtils.safeClose((Closeable)wsChannel);
/*      */       }
/*      */     } finally {
/* 1006 */       if (closePooledData) {
/* 1007 */         pooledData.close();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteBuffer mergeBuffers(ByteBuffer... payload) {
/* 1017 */     int size = (int)Buffers.remaining((Buffer[])payload);
/* 1018 */     if (size == 0) {
/* 1019 */       return Buffers.EMPTY_BYTE_BUFFER;
/*      */     }
/* 1021 */     ByteBuffer buffer = ByteBuffer.allocate(size);
/* 1022 */     for (ByteBuffer buf : payload) {
/* 1023 */       buffer.put(buf);
/*      */     }
/* 1025 */     buffer.flip();
/* 1026 */     return buffer;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\WebSockets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */