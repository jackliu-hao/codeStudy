package io.undertow.security.impl;

import io.undertow.UndertowMessages;
import io.undertow.security.api.SessionNonceManager;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.FlexBase64;
import io.undertow.util.WorkerUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;

public class SimpleNonceManager implements SessionNonceManager {
   private static final String DEFAULT_HASH_ALG = "MD5";
   private final Set<String> invalidNonces;
   private final Map<String, Nonce> knownNonces;
   private final Map<NonceHolder, String> forwardMapping;
   private final String secret;
   private final String hashAlg;
   private final int hashLength;
   private static final long firstUseTimeOut = 300000L;
   private static final long overallTimeOut = 900000L;
   private static final long cacheTimePostExpiry = 300000L;

   public SimpleNonceManager() {
      this("MD5");
   }

   public SimpleNonceManager(String hashAlg) {
      this.invalidNonces = Collections.synchronizedSet(new HashSet());
      this.knownNonces = Collections.synchronizedMap(new HashMap());
      this.forwardMapping = Collections.synchronizedMap(new WeakHashMap());
      MessageDigest digest = this.getDigest(hashAlg);
      this.hashAlg = hashAlg;
      this.hashLength = digest.getDigestLength();
      Random rand = new SecureRandom();
      byte[] secretBytes = new byte[32];
      rand.nextBytes(secretBytes);
      this.secret = FlexBase64.encodeString(digest.digest(secretBytes), false);
   }

   private MessageDigest getDigest(String hashAlg) {
      try {
         return MessageDigest.getInstance(hashAlg);
      } catch (NoSuchAlgorithmException var3) {
         throw UndertowMessages.MESSAGES.hashAlgorithmNotFound(hashAlg);
      }
   }

   public String nextNonce(String lastNonce, HttpServerExchange exchange) {
      if (lastNonce == null) {
         return this.createNewNonceString();
      } else if (this.invalidNonces.contains(lastNonce)) {
         return this.createNewNonceString();
      } else {
         String nonce = lastNonce;
         synchronized(this.forwardMapping) {
            NonceHolder holder;
            for(holder = new NonceHolder(lastNonce); this.forwardMapping.containsKey(holder); holder = new NonceHolder(nonce)) {
               nonce = (String)this.forwardMapping.get(holder);
            }

            synchronized(this.knownNonces) {
               Nonce value = (Nonce)this.knownNonces.get(nonce);
               if (value == null) {
                  nonce = this.createNewNonceString();
               } else {
                  long now = System.currentTimeMillis();
                  long earliestAccepted = now - 300000L;
                  if (value.timeStamp < earliestAccepted || value.timeStamp > now) {
                     Nonce replacement = this.createNewNonce(holder);
                     if (value.executorKey != null) {
                        value.executorKey.remove();
                     }

                     nonce = replacement.nonce;
                     this.forwardMapping.put(holder, nonce);
                     replacement.setSessionKey(value.getSessionKey());
                     this.knownNonces.remove(holder.nonce);
                     this.knownNonces.put(nonce, replacement);
                     earliestAccepted = now - 1200000L;
                     long timeTillExpiry = replacement.timeStamp - earliestAccepted;
                     replacement.executorKey = WorkerUtils.executeAfter(exchange.getIoThread(), new KnownNonceCleaner(nonce), timeTillExpiry, TimeUnit.MILLISECONDS);
                  }
               }
            }

            return nonce;
         }
      }
   }

   private String createNewNonceString() {
      return this.createNewNonce((NonceHolder)null).nonce;
   }

   private Nonce createNewNonce(NonceHolder previousNonce) {
      byte[] prefix = new byte[8];
      ThreadLocalRandom.current().nextBytes(prefix);
      long timeStamp = System.currentTimeMillis();
      byte[] now = Long.toString(timeStamp).getBytes(StandardCharsets.UTF_8);
      String nonce = this.createNonce(prefix, now);
      return new Nonce(nonce, timeStamp, previousNonce);
   }

   public boolean validateNonce(String nonce, int nonceCount, HttpServerExchange exchange) {
      if (nonceCount < 0) {
         if (this.invalidNonces.contains(nonce)) {
            return false;
         }
      } else {
         if (this.knownNonces.containsKey(nonce)) {
            return this.validateNonceWithCount(new Nonce(nonce), nonceCount, exchange.getIoThread());
         }

         if (this.forwardMapping.containsKey(new NonceHolder(nonce))) {
            return false;
         }
      }

      Nonce value = this.verifyUnknownNonce(nonce, nonceCount);
      if (value == null) {
         return false;
      } else {
         long now = System.currentTimeMillis();
         long earliestAccepted = now - 300000L;
         if (value.timeStamp >= earliestAccepted && value.timeStamp <= now) {
            return nonceCount < 0 ? this.addInvalidNonce(value, exchange.getIoThread()) : this.validateNonceWithCount(value, nonceCount, exchange.getIoThread());
         } else {
            return false;
         }
      }
   }

   private boolean validateNonceWithCount(Nonce nonce, int nonceCount, XnioIoThread executor) {
      synchronized(this.knownNonces) {
         Nonce value = (Nonce)this.knownNonces.get(nonce.nonce);
         long now = System.currentTimeMillis();
         long earliestAccepted = now - 1200000L;
         if (value == null) {
            if (nonce.timeStamp < 0L) {
               return false;
            } else if (nonce.timeStamp > earliestAccepted && nonce.timeStamp <= now) {
               this.knownNonces.put(nonce.nonce, nonce);
               long timeTillExpiry = nonce.timeStamp - earliestAccepted;
               nonce.executorKey = WorkerUtils.executeAfter(executor, new KnownNonceCleaner(nonce.nonce), timeTillExpiry, TimeUnit.MILLISECONDS);
               return true;
            } else {
               return false;
            }
         } else if (value.timeStamp >= earliestAccepted && value.timeStamp <= now) {
            if (value.getMaxNonceCount() < nonceCount) {
               value.setMaxNonceCount(nonceCount);
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private boolean addInvalidNonce(Nonce nonce, XnioExecutor executor) {
      long now = System.currentTimeMillis();
      long invalidBefore = now - 300000L;
      long timeTillInvalid = nonce.timeStamp - invalidBefore;
      if (timeTillInvalid > 0L) {
         if (this.invalidNonces.add(nonce.nonce)) {
            executor.executeAfter(new InvalidNonceCleaner(nonce.nonce), timeTillInvalid, TimeUnit.MILLISECONDS);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private Nonce verifyUnknownNonce(String nonce, int nonceCount) {
      byte[] complete;
      int offset;
      int length;
      try {
         ByteBuffer decode = FlexBase64.decode(nonce);
         complete = decode.array();
         offset = decode.arrayOffset();
         length = decode.limit() - offset;
      } catch (IOException var12) {
         throw UndertowMessages.MESSAGES.invalidBase64Token(var12);
      }

      int timeStampLength = complete[offset + 8];
      if (this.hashLength > 0) {
         int expectedLength = 9 + timeStampLength + this.hashLength;
         if (length != expectedLength) {
            throw UndertowMessages.MESSAGES.invalidNonceReceived();
         }

         if (timeStampLength + 1 >= length) {
            throw UndertowMessages.MESSAGES.invalidNonceReceived();
         }
      }

      byte[] prefix = new byte[8];
      System.arraycopy(complete, offset, prefix, 0, 8);
      byte[] timeStampBytes = new byte[timeStampLength];
      System.arraycopy(complete, offset + 9, timeStampBytes, 0, timeStampBytes.length);
      String expectedNonce = this.createNonce(prefix, timeStampBytes);
      if (expectedNonce.equals(nonce)) {
         try {
            long timeStamp = Long.parseLong(new String(timeStampBytes, StandardCharsets.UTF_8));
            return new Nonce(expectedNonce, timeStamp, nonceCount);
         } catch (NumberFormatException var13) {
         }
      }

      return null;
   }

   private String createNonce(byte[] prefix, byte[] timeStamp) {
      byte[] hashedPart = this.generateHash(prefix, timeStamp);
      byte[] complete = new byte[9 + timeStamp.length + hashedPart.length];
      System.arraycopy(prefix, 0, complete, 0, 8);
      complete[8] = (byte)timeStamp.length;
      System.arraycopy(timeStamp, 0, complete, 9, timeStamp.length);
      System.arraycopy(hashedPart, 0, complete, 9 + timeStamp.length, hashedPart.length);
      return FlexBase64.encodeString(complete, false);
   }

   private byte[] generateHash(byte[] prefix, byte[] timeStamp) {
      MessageDigest digest = this.getDigest(this.hashAlg);
      digest.update(prefix);
      digest.update(timeStamp);
      return digest.digest(this.secret.getBytes(StandardCharsets.UTF_8));
   }

   public void associateHash(String nonce, byte[] hash) {
   }

   public byte[] lookupHash(String nonce) {
      return null;
   }

   private class KnownNonceCleaner implements Runnable {
      private final String nonce;

      private KnownNonceCleaner(String nonce) {
         if (nonce == null) {
            throw new NullPointerException("nonce must not be null.");
         } else {
            this.nonce = nonce;
         }
      }

      public void run() {
         SimpleNonceManager.this.knownNonces.remove(this.nonce);
      }

      // $FF: synthetic method
      KnownNonceCleaner(String x1, Object x2) {
         this(x1);
      }
   }

   private class InvalidNonceCleaner implements Runnable {
      private final String nonce;

      private InvalidNonceCleaner(String nonce) {
         if (nonce == null) {
            throw new NullPointerException("nonce must not be null.");
         } else {
            this.nonce = nonce;
         }
      }

      public void run() {
         SimpleNonceManager.this.invalidNonces.remove(this.nonce);
      }

      // $FF: synthetic method
      InvalidNonceCleaner(String x1, Object x2) {
         this(x1);
      }
   }

   private static class Nonce {
      private final String nonce;
      private final long timeStamp;
      private int maxNonceCount;
      private final NonceHolder previousNonce;
      private byte[] sessionKey;
      private XnioExecutor.Key executorKey;

      private Nonce(String nonce) {
         this(nonce, -1L, -1);
      }

      private Nonce(String nonce, long timeStamp, int initialNC) {
         this(nonce, timeStamp, initialNC, (NonceHolder)null);
      }

      private Nonce(String nonce, long timeStamp, NonceHolder previousNonce) {
         this(nonce, timeStamp, -1, (NonceHolder)previousNonce);
      }

      private Nonce(String nonce, long timeStamp, int initialNC, NonceHolder previousNonce) {
         this.nonce = nonce;
         this.timeStamp = timeStamp;
         this.maxNonceCount = initialNC;
         this.previousNonce = previousNonce;
      }

      byte[] getSessionKey() {
         return this.sessionKey;
      }

      void setSessionKey(byte[] sessionKey) {
         this.sessionKey = sessionKey;
      }

      int getMaxNonceCount() {
         return this.maxNonceCount;
      }

      void setMaxNonceCount(int maxNonceCount) {
         this.maxNonceCount = maxNonceCount;
      }

      // $FF: synthetic method
      Nonce(String x0, long x1, NonceHolder x2, Object x3) {
         this(x0, x1, x2);
      }

      // $FF: synthetic method
      Nonce(String x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Nonce(String x0, long x1, int x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   private static class NonceHolder {
      private final String nonce;

      private NonceHolder(String nonce) {
         if (nonce == null) {
            throw new NullPointerException("nonce must not be null.");
         } else {
            this.nonce = nonce;
         }
      }

      public int hashCode() {
         return this.nonce.hashCode();
      }

      public boolean equals(Object obj) {
         return obj instanceof NonceHolder ? this.nonce.equals(((NonceHolder)obj).nonce) : false;
      }

      // $FF: synthetic method
      NonceHolder(String x0, Object x1) {
         this(x0);
      }
   }
}
