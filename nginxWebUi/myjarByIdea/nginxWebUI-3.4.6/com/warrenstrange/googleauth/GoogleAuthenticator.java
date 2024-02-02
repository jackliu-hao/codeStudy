package com.warrenstrange.googleauth;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;

public final class GoogleAuthenticator implements IGoogleAuthenticator {
   public static final String RNG_ALGORITHM = "com.warrenstrange.googleauth.rng.algorithm";
   public static final String RNG_ALGORITHM_PROVIDER = "com.warrenstrange.googleauth.rng.algorithmProvider";
   private static final Logger LOGGER = Logger.getLogger(GoogleAuthenticator.class.getName());
   private static final int SCRATCH_CODE_LENGTH = 8;
   public static final int SCRATCH_CODE_MODULUS = (int)Math.pow(10.0, 8.0);
   private static final int SCRATCH_CODE_INVALID = -1;
   private static final int BYTES_PER_SCRATCH_CODE = 4;
   private static final String DEFAULT_RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";
   private static final String DEFAULT_RANDOM_NUMBER_ALGORITHM_PROVIDER = "SUN";
   private final GoogleAuthenticatorConfig config;
   private ReseedingSecureRandom secureRandom = new ReseedingSecureRandom(this.getRandomNumberAlgorithm(), this.getRandomNumberAlgorithmProvider());
   private ICredentialRepository credentialRepository;
   private boolean credentialRepositorySearched;

   public GoogleAuthenticator() {
      this.config = new GoogleAuthenticatorConfig();
   }

   public GoogleAuthenticator(GoogleAuthenticatorConfig config) {
      if (config == null) {
         throw new IllegalArgumentException("Configuration cannot be null.");
      } else {
         this.config = config;
      }
   }

   private String getRandomNumberAlgorithm() {
      return System.getProperty("com.warrenstrange.googleauth.rng.algorithm", "SHA1PRNG");
   }

   private String getRandomNumberAlgorithmProvider() {
      return System.getProperty("com.warrenstrange.googleauth.rng.algorithmProvider", "SUN");
   }

   int calculateCode(byte[] key, long tm) {
      byte[] data = new byte[8];
      long value = tm;

      for(int i = 8; i-- > 0; value >>>= 8) {
         data[i] = (byte)((int)value);
      }

      SecretKeySpec signKey = new SecretKeySpec(key, this.config.getHmacHashFunction().toString());

      try {
         Mac mac = Mac.getInstance(this.config.getHmacHashFunction().toString());
         mac.init(signKey);
         byte[] hash = mac.doFinal(data);
         int offset = hash[hash.length - 1] & 15;
         long truncatedHash = 0L;

         for(int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (long)(hash[offset + i] & 255);
         }

         truncatedHash &= 2147483647L;
         truncatedHash %= (long)this.config.getKeyModulus();
         return (int)truncatedHash;
      } catch (InvalidKeyException | NoSuchAlgorithmException var14) {
         LOGGER.log(Level.SEVERE, var14.getMessage(), var14);
         throw new GoogleAuthenticatorException("The operation cannot be performed now.");
      }
   }

   private long getTimeWindowFromTime(long time) {
      return time / this.config.getTimeStepSizeInMillis();
   }

   private boolean checkCode(String secret, long code, long timestamp, int window) {
      byte[] decodedKey = this.decodeSecret(secret);
      long timeWindow = this.getTimeWindowFromTime(timestamp);

      for(int i = -((window - 1) / 2); i <= window / 2; ++i) {
         long hash = (long)this.calculateCode(decodedKey, timeWindow + (long)i);
         if (hash == code) {
            return true;
         }
      }

      return false;
   }

   private byte[] decodeSecret(String secret) {
      switch (this.config.getKeyRepresentation()) {
         case BASE32:
            Base32 codec32 = new Base32();
            return codec32.decode(secret.toUpperCase());
         case BASE64:
            Base64 codec64 = new Base64();
            return codec64.decode(secret);
         default:
            throw new IllegalArgumentException("Unknown key representation type.");
      }
   }

   public GoogleAuthenticatorKey createCredentials() {
      int bufferSize = this.config.getSecretBits() / 8;
      byte[] buffer = new byte[bufferSize];
      this.secureRandom.nextBytes(buffer);
      byte[] secretKey = Arrays.copyOf(buffer, bufferSize);
      String generatedKey = this.calculateSecretKey(secretKey);
      int validationCode = this.calculateValidationCode(secretKey);
      List<Integer> scratchCodes = this.calculateScratchCodes();
      return (new GoogleAuthenticatorKey.Builder(generatedKey)).setConfig(this.config).setVerificationCode(validationCode).setScratchCodes(scratchCodes).build();
   }

   public GoogleAuthenticatorKey createCredentials(String userName) {
      if (userName == null) {
         throw new IllegalArgumentException("User name cannot be null.");
      } else {
         GoogleAuthenticatorKey key = this.createCredentials();
         ICredentialRepository repository = this.getValidCredentialRepository();
         repository.saveUserCredentials(userName, key.getKey(), key.getVerificationCode(), key.getScratchCodes());
         return key;
      }
   }

   private List<Integer> calculateScratchCodes() {
      List<Integer> scratchCodes = new ArrayList();

      for(int i = 0; i < this.config.getNumberOfScratchCodes(); ++i) {
         scratchCodes.add(this.generateScratchCode());
      }

      return scratchCodes;
   }

   private int calculateScratchCode(byte[] scratchCodeBuffer) {
      if (scratchCodeBuffer.length < 4) {
         throw new IllegalArgumentException(String.format("The provided random byte buffer is too small: %d.", scratchCodeBuffer.length));
      } else {
         int scratchCode = 0;

         for(int i = 0; i < 4; ++i) {
            scratchCode = (scratchCode << 8) + (scratchCodeBuffer[i] & 255);
         }

         scratchCode = (scratchCode & Integer.MAX_VALUE) % SCRATCH_CODE_MODULUS;
         return this.validateScratchCode(scratchCode) ? scratchCode : -1;
      }
   }

   boolean validateScratchCode(int scratchCode) {
      return scratchCode >= SCRATCH_CODE_MODULUS / 10;
   }

   private int generateScratchCode() {
      int scratchCode;
      do {
         byte[] scratchCodeBuffer = new byte[4];
         this.secureRandom.nextBytes(scratchCodeBuffer);
         scratchCode = this.calculateScratchCode(scratchCodeBuffer);
      } while(scratchCode == -1);

      return scratchCode;
   }

   private int calculateValidationCode(byte[] secretKey) {
      return this.calculateCode(secretKey, 0L);
   }

   public int getTotpPassword(String secret) {
      return this.getTotpPassword(secret, (new Date()).getTime());
   }

   public int getTotpPassword(String secret, long time) {
      return this.calculateCode(this.decodeSecret(secret), this.getTimeWindowFromTime(time));
   }

   public int getTotpPasswordOfUser(String userName) {
      return this.getTotpPasswordOfUser(userName, (new Date()).getTime());
   }

   public int getTotpPasswordOfUser(String userName, long time) {
      ICredentialRepository repository = this.getValidCredentialRepository();
      return this.calculateCode(this.decodeSecret(repository.getSecretKey(userName)), this.getTimeWindowFromTime(time));
   }

   private String calculateSecretKey(byte[] secretKey) {
      switch (this.config.getKeyRepresentation()) {
         case BASE32:
            return (new Base32()).encodeToString(secretKey);
         case BASE64:
            return (new Base64()).encodeToString(secretKey);
         default:
            throw new IllegalArgumentException("Unknown key representation type.");
      }
   }

   public boolean authorize(String secret, int verificationCode) {
      return this.authorize(secret, verificationCode, (new Date()).getTime());
   }

   public boolean authorize(String secret, int verificationCode, long time) {
      if (secret == null) {
         throw new IllegalArgumentException("Secret cannot be null.");
      } else {
         return verificationCode > 0 && verificationCode < this.config.getKeyModulus() ? this.checkCode(secret, (long)verificationCode, time, this.config.getWindowSize()) : false;
      }
   }

   public boolean authorizeUser(String userName, int verificationCode) {
      return this.authorizeUser(userName, verificationCode, (new Date()).getTime());
   }

   public boolean authorizeUser(String userName, int verificationCode, long time) {
      ICredentialRepository repository = this.getValidCredentialRepository();
      return this.authorize(repository.getSecretKey(userName), verificationCode, time);
   }

   private ICredentialRepository getValidCredentialRepository() {
      ICredentialRepository repository = this.getCredentialRepository();
      if (repository == null) {
         throw new UnsupportedOperationException(String.format("An instance of the %s service must be configured in order to use this feature.", ICredentialRepository.class.getName()));
      } else {
         return repository;
      }
   }

   public ICredentialRepository getCredentialRepository() {
      if (this.credentialRepositorySearched) {
         return this.credentialRepository;
      } else {
         this.credentialRepositorySearched = true;
         ServiceLoader<ICredentialRepository> loader = ServiceLoader.load(ICredentialRepository.class);
         Iterator var2 = loader.iterator();
         if (var2.hasNext()) {
            ICredentialRepository repository = (ICredentialRepository)var2.next();
            this.credentialRepository = repository;
         }

         return this.credentialRepository;
      }
   }

   public void setCredentialRepository(ICredentialRepository repository) {
      this.credentialRepository = repository;
      this.credentialRepositorySearched = true;
   }
}
