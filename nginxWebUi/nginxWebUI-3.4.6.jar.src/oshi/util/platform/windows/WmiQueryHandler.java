/*     */ package oshi.util.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.COMException;
/*     */ import com.sun.jna.platform.win32.COM.COMUtils;
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import com.sun.jna.platform.win32.Ole32;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import java.util.HashSet;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.GlobalConfig;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public class WmiQueryHandler
/*     */ {
/*  51 */   private static final Logger LOG = LoggerFactory.getLogger(WmiQueryHandler.class);
/*     */   
/*  53 */   private static int globalTimeout = GlobalConfig.get("oshi.util.wmi.timeout", -1);
/*     */   
/*     */   static {
/*  56 */     if (globalTimeout == 0 || globalTimeout < -1) {
/*  57 */       throw new GlobalConfig.PropertyException("oshi.util.wmi.timeout");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*  62 */   private int wmiTimeout = globalTimeout;
/*     */ 
/*     */   
/*  65 */   private final Set<String> failedWmiClassNames = new HashSet<>();
/*     */ 
/*     */   
/*  68 */   private int comThreading = 0;
/*     */ 
/*     */   
/*     */   private boolean securityInitialized = false;
/*     */   
/*  73 */   private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
/*  74 */   private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
/*     */ 
/*     */   
/*  77 */   private static Class<? extends WmiQueryHandler> customClass = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized WmiQueryHandler createInstance() {
/*  88 */     if (customClass == null) {
/*  89 */       return new WmiQueryHandler();
/*     */     }
/*     */     try {
/*  92 */       return customClass.getConstructor(EMPTY_CLASS_ARRAY).newInstance(EMPTY_OBJECT_ARRAY);
/*  93 */     } catch (NoSuchMethodException|SecurityException e) {
/*  94 */       LOG.error("Failed to find or access a no-arg constructor for {}", customClass);
/*  95 */     } catch (InstantiationException|IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException e) {
/*     */       
/*  97 */       LOG.error("Failed to create a new instance of {}", customClass);
/*     */     } 
/*  99 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void setInstanceClass(Class<? extends WmiQueryHandler> instanceClass) {
/* 110 */     customClass = instanceClass;
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
/*     */   public <T extends Enum<T>> WbemcliUtil.WmiResult<T> queryWMI(WbemcliUtil.WmiQuery<T> query) {
/* 126 */     Objects.requireNonNull(WbemcliUtil.INSTANCE); WbemcliUtil.WmiResult<T> result = new WbemcliUtil.WmiResult(WbemcliUtil.INSTANCE, query.getPropertyEnum());
/* 127 */     if (this.failedWmiClassNames.contains(query.getWmiClassName())) {
/* 128 */       return result;
/*     */     }
/* 130 */     boolean comInit = false;
/*     */     try {
/* 132 */       comInit = initCOM();
/* 133 */       result = query.execute(this.wmiTimeout);
/* 134 */     } catch (COMException e) {
/*     */       
/* 136 */       if (!"ROOT\\OpenHardwareMonitor".equals(query.getNameSpace())) {
/* 137 */         int hresult = (e.getHresult() == null) ? -1 : e.getHresult().intValue();
/* 138 */         switch (hresult) {
/*     */           case -2147217394:
/* 140 */             LOG.warn("COM exception: Invalid Namespace {}", query.getNameSpace());
/*     */             break;
/*     */           case -2147217392:
/* 143 */             LOG.warn("COM exception: Invalid Class {}", query.getWmiClassName());
/*     */             break;
/*     */           case -2147217385:
/* 146 */             LOG.warn("COM exception: Invalid Query: {}", WmiUtil.queryToString(query));
/*     */             break;
/*     */           default:
/* 149 */             handleComException(query, e);
/*     */             break;
/*     */         } 
/* 152 */         this.failedWmiClassNames.add(query.getWmiClassName());
/*     */       } 
/* 154 */     } catch (TimeoutException e) {
/* 155 */       LOG.error("WMI query timed out after {} ms: {}", Integer.valueOf(this.wmiTimeout), WmiUtil.queryToString(query));
/*     */     } 
/* 157 */     if (comInit) {
/* 158 */       unInitCOM();
/*     */     }
/* 160 */     return result;
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
/*     */   protected void handleComException(WbemcliUtil.WmiQuery<?> query, COMException ex) {
/* 175 */     LOG.warn("COM exception querying {}, which might not be on your system. Will not attempt to query it again. Error was {}: {}", new Object[] { query
/*     */           
/* 177 */           .getWmiClassName(), Integer.valueOf(ex.getHresult().intValue()), ex.getMessage() });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean initCOM() {
/* 187 */     boolean comInit = false;
/*     */ 
/*     */     
/* 190 */     comInit = initCOM(getComThreading());
/* 191 */     if (!comInit) {
/* 192 */       comInit = initCOM(switchComThreading());
/*     */     }
/*     */ 
/*     */     
/* 196 */     if (comInit && !isSecurityInitialized()) {
/* 197 */       WinNT.HRESULT hres = Ole32.INSTANCE.CoInitializeSecurity(null, -1, null, null, 0, 3, null, 0, null);
/*     */ 
/*     */ 
/*     */       
/* 201 */       if (COMUtils.FAILED(hres) && hres.intValue() != -2147417831) {
/* 202 */         Ole32.INSTANCE.CoUninitialize();
/* 203 */         throw new COMException("Failed to initialize security.", hres);
/*     */       } 
/* 205 */       this.securityInitialized = true;
/*     */     } 
/* 207 */     return comInit;
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
/*     */   protected boolean initCOM(int coInitThreading) {
/* 220 */     WinNT.HRESULT hres = Ole32.INSTANCE.CoInitializeEx(null, coInitThreading);
/* 221 */     switch (hres.intValue()) {
/*     */ 
/*     */       
/*     */       case 0:
/*     */       case 1:
/* 226 */         return true;
/*     */       
/*     */       case -2147417850:
/* 229 */         return false;
/*     */     } 
/*     */     
/* 232 */     throw new COMException("Failed to initialize COM library.", hres);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unInitCOM() {
/* 241 */     Ole32.INSTANCE.CoUninitialize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getComThreading() {
/* 251 */     return this.comThreading;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int switchComThreading() {
/* 261 */     if (this.comThreading == 2) {
/* 262 */       this.comThreading = 0;
/*     */     } else {
/* 264 */       this.comThreading = 2;
/*     */     } 
/* 266 */     return this.comThreading;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSecurityInitialized() {
/* 276 */     return this.securityInitialized;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWmiTimeout() {
/* 286 */     return this.wmiTimeout;
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
/*     */   public void setWmiTimeout(int wmiTimeout) {
/* 298 */     this.wmiTimeout = wmiTimeout;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\platform\windows\WmiQueryHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */