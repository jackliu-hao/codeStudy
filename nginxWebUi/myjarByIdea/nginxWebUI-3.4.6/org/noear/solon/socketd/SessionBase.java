package org.noear.solon.socketd;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SessionBase implements Session {
   static final Logger log = LoggerFactory.getLogger(SessionBase.class);
   private String pathNew;
   private int _flag = 0;
   private NvMap headerMap;
   private NvMap paramMap;
   private Map<String, Object> attrMap = null;
   private AtomicBoolean _handshaked = new AtomicBoolean();
   private Listener listener;
   private boolean _sendHeartbeatAuto = false;
   protected Message handshakeMessage;

   public void pathNew(String pathNew) {
      this.pathNew = pathNew;
   }

   public String pathNew() {
      return this.pathNew == null ? this.path() : this.pathNew;
   }

   public int flag() {
      return this._flag;
   }

   public void flagSet(int flag) {
      this._flag = flag;
   }

   public String header(String name) {
      return (String)this.headerMap().get(name);
   }

   public void headerSet(String name, String value) {
      this.headerMap().put(name, value);
   }

   public NvMap headerMap() {
      if (this.headerMap == null) {
         this.headerMap = new NvMap();
      }

      return this.headerMap;
   }

   public String param(String name) {
      return (String)this.paramMap().get(name);
   }

   public void paramSet(String name, String value) {
      this.paramMap().put(name, value);
   }

   public NvMap paramMap() {
      if (this.paramMap == null) {
         this.paramMap = new NvMap();
         if (this.uri() != null) {
            String query = this.uri().getQuery();
            if (Utils.isNotEmpty(query)) {
               String[] ss = query.split("&");
               String[] var3 = ss;
               int var4 = ss.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  String kv = var3[var5];
                  String[] s = kv.split("=");
                  this.paramMap.put(s[0], s[1]);
               }
            }
         }
      }

      return this.paramMap;
   }

   public Map<String, Object> attrMap() {
      if (this.attrMap == null) {
         this.attrMap = new HashMap();
      }

      return this.attrMap;
   }

   public void setHandshaked(boolean handshaked) {
      this._handshaked.set(handshaked);
   }

   public boolean getHandshaked() {
      return this._handshaked.get();
   }

   public void send(Message message) {
      log.trace((String)"Session send: {}", (Object)message);
   }

   public String sendAndResponse(String message) {
      return this.sendAndResponse(Message.wrap(message)).bodyAsString();
   }

   public String sendAndResponse(String message, int timeout) {
      return this.sendAndResponse(Message.wrap(message), timeout).bodyAsString();
   }

   public Message sendAndResponse(Message message) {
      return this.sendAndResponse((Message)message, 0);
   }

   public Message sendAndResponse(Message message, int timeout) {
      if (Utils.isEmpty(message.key())) {
         throw new IllegalArgumentException("SendAndResponse message no key");
      } else {
         if (timeout < 1) {
            timeout = RequestManager.REQUEST_AND_RESPONSE_TIMEOUT_SECONDS;
         }

         CompletableFuture<Message> request = new CompletableFuture();
         RequestManager.register(message, request);
         this.send(message);

         Message var4;
         try {
            var4 = (Message)request.get((long)timeout, TimeUnit.SECONDS);
         } catch (Exception var8) {
            throw new RuntimeException(var8);
         } finally {
            RequestManager.remove(message.key());
         }

         return var4;
      }
   }

   public void sendAndCallback(String message, BiConsumer<String, Throwable> callback) {
      this.sendAndCallback(Message.wrap(message), (msg, err) -> {
         if (msg == null) {
            callback.accept((Object)null, err);
         } else {
            callback.accept(msg.bodyAsString(), err);
         }

      });
   }

   public void sendAndCallback(Message message, BiConsumer<Message, Throwable> callback) {
      if (Utils.isEmpty(message.key())) {
         throw new IllegalArgumentException("sendAndCallback message no key");
      } else {
         CompletableFuture<Message> request = new CompletableFuture();
         RequestManager.register(message, request);
         request.whenCompleteAsync(callback);
         this.send(message);
      }
   }

   public Listener listener() {
      return this.listener;
   }

   public void listener(Listener listener) {
      this.listener = listener;
   }

   protected void onOpen() {
      if (this.listener() != null) {
         this.listener().onOpen(this);
      }

   }

   public void sendHeartbeat() {
      this.send(Message.wrapHeartbeat());
   }

   public void sendHeartbeatAuto(int intervalSeconds) {
      if (!this._sendHeartbeatAuto) {
         synchronized(this) {
            if (!this._sendHeartbeatAuto) {
               this._sendHeartbeatAuto = true;
               Utils.scheduled.scheduleWithFixedDelay(() -> {
                  try {
                     this.sendHeartbeat();
                  } catch (Throwable var2) {
                     EventBus.push(var2);
                  }

               }, 1L, (long)intervalSeconds, TimeUnit.SECONDS);
            }
         }
      }
   }

   public void sendHandshake(Message message) {
      if (message.flag() == 12) {
         try {
            this.send(message);
         } finally {
            this.handshakeMessage = message;
         }

      } else {
         throw new IllegalArgumentException("The message flag not handshake");
      }
   }

   public Message sendHandshakeAndResponse(Message message) {
      if (message.flag() == 12) {
         Message rst = this.sendAndResponse(message);
         this.handshakeMessage = message;
         return rst;
      } else {
         throw new IllegalArgumentException("The message flag not handshake");
      }
   }
}
