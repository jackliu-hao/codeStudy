/*     */ package ch.qos.logback.core.util;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.rolling.helper.FileNamePattern;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import java.net.InetAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.SocketException;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
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
/*     */ public class ContextUtil
/*     */   extends ContextAwareBase
/*     */ {
/*     */   public ContextUtil(Context context) {
/*  37 */     setContext(context);
/*     */   }
/*     */   
/*     */   public static String getLocalHostName() throws UnknownHostException, SocketException {
/*     */     try {
/*  42 */       InetAddress localhost = InetAddress.getLocalHost();
/*  43 */       return localhost.getHostName();
/*  44 */     } catch (UnknownHostException e) {
/*  45 */       return getLocalAddressAsString();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static String getLocalAddressAsString() throws UnknownHostException, SocketException {
/*  50 */     Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
/*  51 */     while (interfaces != null && interfaces.hasMoreElements()) {
/*  52 */       Enumeration<InetAddress> addresses = ((NetworkInterface)interfaces.nextElement()).getInetAddresses();
/*  53 */       while (addresses != null && addresses.hasMoreElements()) {
/*  54 */         InetAddress address = addresses.nextElement();
/*  55 */         if (acceptableAddress(address)) {
/*  56 */           return address.getHostAddress();
/*     */         }
/*     */       } 
/*     */     } 
/*  60 */     throw new UnknownHostException();
/*     */   }
/*     */   
/*     */   private static boolean acceptableAddress(InetAddress address) {
/*  64 */     return (address != null && !address.isLoopbackAddress() && !address.isAnyLocalAddress() && !address.isLinkLocalAddress());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String safelyGetLocalHostName() {
/*     */     try {
/*  72 */       String localhostName = getLocalHostName();
/*  73 */       return localhostName;
/*  74 */     } catch (UnknownHostException e) {
/*  75 */       addError("Failed to get local hostname", e);
/*  76 */     } catch (SocketException e) {
/*  77 */       addError("Failed to get local hostname", e);
/*  78 */     } catch (SecurityException e) {
/*  79 */       addError("Failed to get local hostname", e);
/*     */     } 
/*  81 */     return "UNKNOWN_LOCALHOST";
/*     */   }
/*     */   
/*     */   public void addProperties(Properties props) {
/*  85 */     if (props == null) {
/*     */       return;
/*     */     }
/*     */     
/*  89 */     Iterator<String> i = props.keySet().iterator();
/*  90 */     while (i.hasNext()) {
/*  91 */       String key = i.next();
/*  92 */       this.context.putProperty(key, props.getProperty(key));
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Map<String, String> getFilenameCollisionMap(Context context) {
/*  97 */     if (context == null) {
/*  98 */       return null;
/*     */     }
/* 100 */     Map<String, String> map = (Map<String, String>)context.getObject("FA_FILENAME_COLLISION_MAP");
/* 101 */     return map;
/*     */   }
/*     */   
/*     */   public static Map<String, FileNamePattern> getFilenamePatternCollisionMap(Context context) {
/* 105 */     if (context == null) {
/* 106 */       return null;
/*     */     }
/* 108 */     Map<String, FileNamePattern> map = (Map<String, FileNamePattern>)context.getObject("RFA_FILENAME_PATTERN_COLLISION_MAP");
/* 109 */     return map;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addGroovyPackages(List<String> frameworkPackages) {
/* 114 */     addFrameworkPackage(frameworkPackages, "org.codehaus.groovy.runtime");
/*     */   }
/*     */   
/*     */   public void addFrameworkPackage(List<String> frameworkPackages, String packageName) {
/* 118 */     if (!frameworkPackages.contains(packageName))
/* 119 */       frameworkPackages.add(packageName); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\ContextUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */