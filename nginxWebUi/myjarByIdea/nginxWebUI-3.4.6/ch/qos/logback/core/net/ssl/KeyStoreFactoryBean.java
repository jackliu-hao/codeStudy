package ch.qos.logback.core.net.ssl;

import ch.qos.logback.core.util.LocationUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class KeyStoreFactoryBean {
   private String location;
   private String provider;
   private String type;
   private String password;

   public KeyStore createKeyStore() throws NoSuchProviderException, NoSuchAlgorithmException, KeyStoreException {
      if (this.getLocation() == null) {
         throw new IllegalArgumentException("location is required");
      } else {
         InputStream inputStream = null;

         KeyStore var4;
         try {
            URL url = LocationUtil.urlForResource(this.getLocation());
            inputStream = url.openStream();
            KeyStore keyStore = this.newKeyStore();
            keyStore.load(inputStream, this.getPassword().toCharArray());
            var4 = keyStore;
         } catch (NoSuchProviderException var16) {
            throw new NoSuchProviderException("no such keystore provider: " + this.getProvider());
         } catch (NoSuchAlgorithmException var17) {
            throw new NoSuchAlgorithmException("no such keystore type: " + this.getType());
         } catch (FileNotFoundException var18) {
            throw new KeyStoreException(this.getLocation() + ": file not found");
         } catch (Exception var19) {
            throw new KeyStoreException(this.getLocation() + ": " + var19.getMessage(), var19);
         } finally {
            try {
               if (inputStream != null) {
                  inputStream.close();
               }
            } catch (IOException var15) {
               var15.printStackTrace(System.err);
            }

         }

         return var4;
      }
   }

   private KeyStore newKeyStore() throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException {
      return this.getProvider() != null ? KeyStore.getInstance(this.getType(), this.getProvider()) : KeyStore.getInstance(this.getType());
   }

   public String getLocation() {
      return this.location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public String getType() {
      return this.type == null ? "JKS" : this.type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getProvider() {
      return this.provider;
   }

   public void setProvider(String provider) {
      this.provider = provider;
   }

   public String getPassword() {
      return this.password == null ? "changeit" : this.password;
   }

   public void setPassword(String password) {
      this.password = password;
   }
}
