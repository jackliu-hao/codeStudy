package org.h2.security.auth;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.h2.api.CredentialsValidator;
import org.h2.api.UserToRolesMapper;
import org.h2.engine.Database;
import org.h2.engine.Right;
import org.h2.engine.Role;
import org.h2.engine.SysProperties;
import org.h2.engine.User;
import org.h2.engine.UserBuilder;
import org.h2.message.Trace;
import org.h2.security.auth.impl.AssignRealmNameRole;
import org.h2.security.auth.impl.JaasCredentialsValidator;
import org.h2.util.StringUtils;
import org.xml.sax.SAXException;

public class DefaultAuthenticator implements Authenticator {
   public static final String DEFAULT_REALMNAME = "H2";
   private Map<String, CredentialsValidator> realms = new HashMap();
   private List<UserToRolesMapper> userToRolesMappers = new ArrayList();
   private boolean allowUserRegistration;
   private boolean persistUsers;
   private boolean createMissingRoles;
   private boolean skipDefaultInitialization;
   private boolean initialized;
   private static DefaultAuthenticator instance;

   protected static final DefaultAuthenticator getInstance() {
      if (instance == null) {
         instance = new DefaultAuthenticator();
      }

      return instance;
   }

   public DefaultAuthenticator() {
   }

   public DefaultAuthenticator(boolean var1) {
      this.skipDefaultInitialization = var1;
   }

   public boolean isPersistUsers() {
      return this.persistUsers;
   }

   public void setPersistUsers(boolean var1) {
      this.persistUsers = var1;
   }

   public boolean isAllowUserRegistration() {
      return this.allowUserRegistration;
   }

   public void setAllowUserRegistration(boolean var1) {
      this.allowUserRegistration = var1;
   }

   public boolean isCreateMissingRoles() {
      return this.createMissingRoles;
   }

   public void setCreateMissingRoles(boolean var1) {
      this.createMissingRoles = var1;
   }

   public void addRealm(String var1, CredentialsValidator var2) {
      this.realms.put(StringUtils.toUpperEnglish(var1), var2);
   }

   public List<UserToRolesMapper> getUserToRolesMappers() {
      return this.userToRolesMappers;
   }

   public void setUserToRolesMappers(UserToRolesMapper... var1) {
      ArrayList var2 = new ArrayList();
      UserToRolesMapper[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         UserToRolesMapper var6 = var3[var5];
         var2.add(var6);
      }

      this.userToRolesMappers = var2;
   }

   public void init(Database var1) throws AuthConfigException {
      if (!this.skipDefaultInitialization) {
         if (!this.initialized) {
            synchronized(this) {
               if (!this.initialized) {
                  Trace var3 = var1.getTrace(2);
                  URL var4 = null;

                  try {
                     String var5 = SysProperties.AUTH_CONFIG_FILE;
                     if (var5 != null) {
                        if (var3.isDebugEnabled()) {
                           var3.debug("DefaultAuthenticator.config: configuration read from system property h2auth.configurationfile={0}", var5);
                        }

                        var4 = new URL(var5);
                     }

                     if (var4 == null) {
                        if (var3.isDebugEnabled()) {
                           var3.debug("DefaultAuthenticator.config: default configuration");
                        }

                        this.defaultConfiguration();
                     } else {
                        this.configureFromUrl(var4);
                     }
                  } catch (Exception var7) {
                     var3.error(var7, "DefaultAuthenticator.config: an error occurred during configuration from {0} ", var4);
                     throw new AuthConfigException("Failed to configure authentication from " + var4, var7);
                  }

                  this.initialized = true;
               }
            }
         }
      }
   }

   private void defaultConfiguration() {
      this.createMissingRoles = false;
      this.allowUserRegistration = true;
      this.realms = new HashMap();
      JaasCredentialsValidator var1 = new JaasCredentialsValidator();
      var1.configure(new ConfigProperties());
      this.realms.put("H2", var1);
      AssignRealmNameRole var2 = new AssignRealmNameRole();
      var2.configure(new ConfigProperties());
      this.userToRolesMappers.add(var2);
   }

   public void configureFromUrl(URL var1) throws AuthenticationException, SAXException, IOException, ParserConfigurationException {
      H2AuthConfig var2 = H2AuthConfigXml.parseFrom(var1);
      this.configureFrom(var2);
   }

   private void configureFrom(H2AuthConfig var1) throws AuthenticationException {
      this.allowUserRegistration = var1.isAllowUserRegistration();
      this.createMissingRoles = var1.isCreateMissingRoles();
      HashMap var2 = new HashMap();
      Iterator var3 = var1.getRealms().iterator();

      CredentialsValidator var6;
      while(var3.hasNext()) {
         RealmConfig var4 = (RealmConfig)var3.next();
         String var5 = var4.getName();
         if (var5 == null) {
            throw new AuthenticationException("Missing realm name");
         }

         var5 = var5.toUpperCase();
         var6 = null;

         try {
            var6 = (CredentialsValidator)Class.forName(var4.getValidatorClass()).getDeclaredConstructor().newInstance();
         } catch (Exception var9) {
            throw new AuthenticationException("invalid validator class fo realm " + var5, var9);
         }

         var6.configure(new ConfigProperties(var4.getProperties()));
         if (var2.putIfAbsent(var4.getName().toUpperCase(), var6) != null) {
            throw new AuthenticationException("Duplicate realm " + var4.getName());
         }
      }

      this.realms = var2;
      ArrayList var10 = new ArrayList();
      Iterator var11 = var1.getUserToRolesMappers().iterator();

      while(var11.hasNext()) {
         UserToRolesMapperConfig var12 = (UserToRolesMapperConfig)var11.next();
         var6 = null;

         UserToRolesMapper var13;
         try {
            var13 = (UserToRolesMapper)Class.forName(var12.getClassName()).getDeclaredConstructor().newInstance();
         } catch (Exception var8) {
            throw new AuthenticationException("Invalid class in UserToRolesMapperConfig", var8);
         }

         var13.configure(new ConfigProperties(var12.getProperties()));
         var10.add(var13);
      }

      this.userToRolesMappers = var10;
   }

   private boolean updateRoles(AuthenticationInfo var1, User var2, Database var3) throws AuthenticationException {
      boolean var4 = false;
      HashSet var5 = new HashSet();
      Iterator var6 = this.userToRolesMappers.iterator();

      while(var6.hasNext()) {
         UserToRolesMapper var7 = (UserToRolesMapper)var6.next();
         Collection var8 = var7.mapUserToRoles(var1);
         if (var8 != null && !var8.isEmpty()) {
            var5.addAll(var8);
         }
      }

      var6 = var5.iterator();

      while(var6.hasNext()) {
         String var12 = (String)var6.next();
         if (var12 != null && !var12.isEmpty()) {
            Role var13 = var3.findRole(var12);
            if (var13 == null && this.isCreateMissingRoles()) {
               synchronized(var3.getSystemSession()) {
                  var13 = new Role(var3, var3.allocateObjectId(), var12, false);
                  var3.addDatabaseObject(var3.getSystemSession(), var13);
                  var3.getSystemSession().commit(false);
                  var4 = true;
               }
            }

            if (var13 != null && var2.getRightForRole(var13) == null) {
               Right var9 = new Right(var3, -1, var2, var13);
               var9.setTemporary(true);
               var2.grantRole(var13, var9);
            }
         }
      }

      return var4;
   }

   public final User authenticate(AuthenticationInfo var1, Database var2) throws AuthenticationException {
      String var3 = var1.getFullyQualifiedName();
      User var4 = var2.findUser(var3);
      if (var4 == null && !this.isAllowUserRegistration()) {
         throw new AuthenticationException("User " + var3 + " not found in db");
      } else {
         CredentialsValidator var5 = (CredentialsValidator)this.realms.get(var1.getRealm());
         if (var5 == null) {
            throw new AuthenticationException("realm " + var1.getRealm() + " not configured");
         } else {
            try {
               if (!var5.validateCredentials(var1)) {
                  return null;
               }
            } catch (Exception var9) {
               throw new AuthenticationException(var9);
            }

            if (var4 == null) {
               synchronized(var2.getSystemSession()) {
                  var4 = UserBuilder.buildUser(var1, var2, this.isPersistUsers());
                  var2.addDatabaseObject(var2.getSystemSession(), var4);
                  var2.getSystemSession().commit(false);
               }
            }

            var4.revokeTemporaryRightsOnRoles();
            this.updateRoles(var1, var4, var2);
            return var4;
         }
      }
   }
}
