/*     */ package org.h2.security.auth;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.h2.api.CredentialsValidator;
/*     */ import org.h2.api.UserToRolesMapper;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.Right;
/*     */ import org.h2.engine.RightOwner;
/*     */ import org.h2.engine.Role;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.engine.User;
/*     */ import org.h2.engine.UserBuilder;
/*     */ import org.h2.message.Trace;
/*     */ import org.h2.security.auth.impl.AssignRealmNameRole;
/*     */ import org.h2.security.auth.impl.JaasCredentialsValidator;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultAuthenticator
/*     */   implements Authenticator
/*     */ {
/*     */   public static final String DEFAULT_REALMNAME = "H2";
/*  56 */   private Map<String, CredentialsValidator> realms = new HashMap<>();
/*  57 */   private List<UserToRolesMapper> userToRolesMappers = new ArrayList<>();
/*     */   private boolean allowUserRegistration;
/*     */   private boolean persistUsers;
/*     */   private boolean createMissingRoles;
/*     */   private boolean skipDefaultInitialization;
/*     */   private boolean initialized;
/*     */   private static DefaultAuthenticator instance;
/*     */   
/*     */   protected static final DefaultAuthenticator getInstance() {
/*  66 */     if (instance == null) {
/*  67 */       instance = new DefaultAuthenticator();
/*     */     }
/*  69 */     return instance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultAuthenticator() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultAuthenticator(boolean paramBoolean) {
/*  86 */     this.skipDefaultInitialization = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPersistUsers() {
/*  96 */     return this.persistUsers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPersistUsers(boolean paramBoolean) {
/* 106 */     this.persistUsers = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAllowUserRegistration() {
/* 116 */     return this.allowUserRegistration;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllowUserRegistration(boolean paramBoolean) {
/* 126 */     this.allowUserRegistration = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCreateMissingRoles() {
/* 137 */     return this.createMissingRoles;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCreateMissingRoles(boolean paramBoolean) {
/* 148 */     this.createMissingRoles = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addRealm(String paramString, CredentialsValidator paramCredentialsValidator) {
/* 160 */     this.realms.put(StringUtils.toUpperEnglish(paramString), paramCredentialsValidator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<UserToRolesMapper> getUserToRolesMappers() {
/* 169 */     return this.userToRolesMappers;
/*     */   }
/*     */   
/*     */   public void setUserToRolesMappers(UserToRolesMapper... paramVarArgs) {
/* 173 */     ArrayList<UserToRolesMapper> arrayList = new ArrayList();
/* 174 */     for (UserToRolesMapper userToRolesMapper : paramVarArgs) {
/* 175 */       arrayList.add(userToRolesMapper);
/*     */     }
/* 177 */     this.userToRolesMappers = arrayList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(Database paramDatabase) throws AuthConfigException {
/* 194 */     if (this.skipDefaultInitialization) {
/*     */       return;
/*     */     }
/* 197 */     if (this.initialized) {
/*     */       return;
/*     */     }
/* 200 */     synchronized (this) {
/* 201 */       if (this.initialized) {
/*     */         return;
/*     */       }
/* 204 */       Trace trace = paramDatabase.getTrace(2);
/* 205 */       URL uRL = null;
/*     */       try {
/* 207 */         String str = SysProperties.AUTH_CONFIG_FILE;
/* 208 */         if (str != null) {
/* 209 */           if (trace.isDebugEnabled()) {
/* 210 */             trace.debug("DefaultAuthenticator.config: configuration read from system property h2auth.configurationfile={0}", new Object[] { str });
/*     */           }
/*     */           
/* 213 */           uRL = new URL(str);
/*     */         } 
/* 215 */         if (uRL == null) {
/* 216 */           if (trace.isDebugEnabled()) {
/* 217 */             trace.debug("DefaultAuthenticator.config: default configuration");
/*     */           }
/* 219 */           defaultConfiguration();
/*     */         } else {
/* 221 */           configureFromUrl(uRL);
/*     */         } 
/* 223 */       } catch (Exception exception) {
/* 224 */         trace.error(exception, "DefaultAuthenticator.config: an error occurred during configuration from {0} ", new Object[] { uRL });
/*     */         
/* 226 */         throw new AuthConfigException("Failed to configure authentication from " + uRL, exception);
/*     */       } 
/*     */       
/* 229 */       this.initialized = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void defaultConfiguration() {
/* 234 */     this.createMissingRoles = false;
/* 235 */     this.allowUserRegistration = true;
/* 236 */     this.realms = new HashMap<>();
/* 237 */     JaasCredentialsValidator jaasCredentialsValidator = new JaasCredentialsValidator();
/* 238 */     jaasCredentialsValidator.configure(new ConfigProperties());
/* 239 */     this.realms.put("H2", jaasCredentialsValidator);
/* 240 */     AssignRealmNameRole assignRealmNameRole = new AssignRealmNameRole();
/* 241 */     assignRealmNameRole.configure(new ConfigProperties());
/* 242 */     this.userToRolesMappers.add(assignRealmNameRole);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void configureFromUrl(URL paramURL) throws AuthenticationException, SAXException, IOException, ParserConfigurationException {
/* 256 */     H2AuthConfig h2AuthConfig = H2AuthConfigXml.parseFrom(paramURL);
/* 257 */     configureFrom(h2AuthConfig);
/*     */   }
/*     */   
/*     */   private void configureFrom(H2AuthConfig paramH2AuthConfig) throws AuthenticationException {
/* 261 */     this.allowUserRegistration = paramH2AuthConfig.isAllowUserRegistration();
/* 262 */     this.createMissingRoles = paramH2AuthConfig.isCreateMissingRoles();
/* 263 */     HashMap<Object, Object> hashMap = new HashMap<>();
/* 264 */     for (RealmConfig realmConfig : paramH2AuthConfig.getRealms()) {
/* 265 */       String str = realmConfig.getName();
/* 266 */       if (str == null) {
/* 267 */         throw new AuthenticationException("Missing realm name");
/*     */       }
/* 269 */       str = str.toUpperCase();
/* 270 */       CredentialsValidator credentialsValidator = null;
/*     */       
/*     */       try {
/* 273 */         credentialsValidator = Class.forName(realmConfig.getValidatorClass()).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 274 */       } catch (Exception exception) {
/* 275 */         throw new AuthenticationException("invalid validator class fo realm " + str, exception);
/*     */       } 
/* 277 */       credentialsValidator.configure(new ConfigProperties(realmConfig.getProperties()));
/* 278 */       if (hashMap.putIfAbsent(realmConfig.getName().toUpperCase(), credentialsValidator) != null) {
/* 279 */         throw new AuthenticationException("Duplicate realm " + realmConfig.getName());
/*     */       }
/*     */     } 
/* 282 */     this.realms = (Map)hashMap;
/* 283 */     ArrayList<UserToRolesMapper> arrayList = new ArrayList();
/* 284 */     for (UserToRolesMapperConfig userToRolesMapperConfig : paramH2AuthConfig.getUserToRolesMappers()) {
/* 285 */       UserToRolesMapper userToRolesMapper = null;
/*     */       
/*     */       try {
/* 288 */         userToRolesMapper = Class.forName(userToRolesMapperConfig.getClassName()).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 289 */       } catch (Exception exception) {
/* 290 */         throw new AuthenticationException("Invalid class in UserToRolesMapperConfig", exception);
/*     */       } 
/* 292 */       userToRolesMapper.configure(new ConfigProperties(userToRolesMapperConfig.getProperties()));
/* 293 */       arrayList.add(userToRolesMapper);
/*     */     } 
/* 295 */     this.userToRolesMappers = arrayList;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean updateRoles(AuthenticationInfo paramAuthenticationInfo, User paramUser, Database paramDatabase) throws AuthenticationException {
/* 300 */     boolean bool = false;
/* 301 */     HashSet hashSet = new HashSet();
/* 302 */     for (UserToRolesMapper userToRolesMapper : this.userToRolesMappers) {
/* 303 */       Collection collection = userToRolesMapper.mapUserToRoles(paramAuthenticationInfo);
/* 304 */       if (collection != null && !collection.isEmpty()) {
/* 305 */         hashSet.addAll(collection);
/*     */       }
/*     */     } 
/* 308 */     for (String str : hashSet) {
/* 309 */       if (str == null || str.isEmpty()) {
/*     */         continue;
/*     */       }
/* 312 */       Role role = paramDatabase.findRole(str);
/* 313 */       if (role == null && isCreateMissingRoles()) {
/* 314 */         synchronized (paramDatabase.getSystemSession()) {
/* 315 */           role = new Role(paramDatabase, paramDatabase.allocateObjectId(), str, false);
/* 316 */           paramDatabase.addDatabaseObject(paramDatabase.getSystemSession(), (DbObject)role);
/* 317 */           paramDatabase.getSystemSession().commit(false);
/* 318 */           bool = true;
/*     */         } 
/*     */       }
/* 321 */       if (role == null) {
/*     */         continue;
/*     */       }
/* 324 */       if (paramUser.getRightForRole(role) == null) {
/*     */         
/* 326 */         Right right = new Right(paramDatabase, -1, (RightOwner)paramUser, role);
/* 327 */         right.setTemporary(true);
/* 328 */         paramUser.grantRole(role, right);
/*     */       } 
/*     */     } 
/* 331 */     return bool;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final User authenticate(AuthenticationInfo paramAuthenticationInfo, Database paramDatabase) throws AuthenticationException {
/* 337 */     String str = paramAuthenticationInfo.getFullyQualifiedName();
/* 338 */     User user = paramDatabase.findUser(str);
/* 339 */     if (user == null && !isAllowUserRegistration()) {
/* 340 */       throw new AuthenticationException("User " + str + " not found in db");
/*     */     }
/* 342 */     CredentialsValidator credentialsValidator = this.realms.get(paramAuthenticationInfo.getRealm());
/* 343 */     if (credentialsValidator == null) {
/* 344 */       throw new AuthenticationException("realm " + paramAuthenticationInfo.getRealm() + " not configured");
/*     */     }
/*     */     try {
/* 347 */       if (!credentialsValidator.validateCredentials(paramAuthenticationInfo)) {
/* 348 */         return null;
/*     */       }
/* 350 */     } catch (Exception exception) {
/* 351 */       throw new AuthenticationException(exception);
/*     */     } 
/* 353 */     if (user == null) {
/* 354 */       synchronized (paramDatabase.getSystemSession()) {
/* 355 */         user = UserBuilder.buildUser(paramAuthenticationInfo, paramDatabase, isPersistUsers());
/* 356 */         paramDatabase.addDatabaseObject(paramDatabase.getSystemSession(), (DbObject)user);
/* 357 */         paramDatabase.getSystemSession().commit(false);
/*     */       } 
/*     */     }
/* 360 */     user.revokeTemporaryRightsOnRoles();
/* 361 */     updateRoles(paramAuthenticationInfo, user, paramDatabase);
/* 362 */     return user;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\DefaultAuthenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */