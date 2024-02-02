/*     */ package org.wildfly.common.net;
/*     */ 
/*     */ import com.oracle.svm.core.annotate.Substitute;
/*     */ import com.oracle.svm.core.annotate.TargetClass;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.graalvm.nativeimage.Platform;
/*     */ import org.graalvm.nativeimage.Platforms;
/*     */ import org.graalvm.nativeimage.StackValue;
/*     */ import org.graalvm.nativeimage.c.CContext;
/*     */ import org.graalvm.nativeimage.c.function.CFunction;
/*     */ import org.graalvm.nativeimage.c.type.CCharPointer;
/*     */ import org.graalvm.nativeimage.c.type.CTypeConversion;
/*     */ import org.graalvm.word.UnsignedWord;
/*     */ import org.graalvm.word.WordFactory;
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
/*     */ final class Substitutions
/*     */ {
/*     */   @TargetClass(className = "org.wildfly.common.net.GetHostInfoAction")
/*     */   @Platforms({Platform.DARWIN.class, Platform.LINUX.class})
/*     */   static final class Target_org_wildfly_common_net_GetHostInfoAction
/*     */   {
/*     */     @Substitute
/*     */     public String[] run() {
/*  46 */       String qualifiedHostName = System.getProperty("jboss.qualified.host.name");
/*  47 */       String providedHostName = System.getProperty("jboss.host.name");
/*  48 */       String providedNodeName = System.getProperty("jboss.node.name");
/*  49 */       if (qualifiedHostName == null) {
/*     */         
/*  51 */         qualifiedHostName = providedHostName;
/*  52 */         if (qualifiedHostName == null) {
/*     */           
/*  54 */           CCharPointer nameBuf = (CCharPointer)StackValue.get(512);
/*  55 */           int res = Substitutions.NativeInfo.gethostname(nameBuf, WordFactory.unsigned(512));
/*  56 */           if (res != -1 && res > 0) {
/*  57 */             if (res == 512)
/*     */             {
/*  59 */               nameBuf.write(511, (byte)0);
/*     */             }
/*  61 */             qualifiedHostName = CTypeConversion.toJavaString(nameBuf);
/*     */           } 
/*     */         } 
/*  64 */         if (qualifiedHostName == null)
/*     */         {
/*  66 */           qualifiedHostName = System.getenv("HOSTNAME");
/*     */         }
/*  68 */         if (qualifiedHostName == null)
/*     */         {
/*  70 */           qualifiedHostName = System.getenv("COMPUTERNAME");
/*     */         }
/*  72 */         if (qualifiedHostName == null) {
/*     */           try {
/*  74 */             qualifiedHostName = HostName.getLocalHost().getHostName();
/*  75 */           } catch (UnknownHostException e) {
/*  76 */             qualifiedHostName = null;
/*     */           } 
/*     */         }
/*  79 */         if (qualifiedHostName != null && (Inet.isInet4Address(qualifiedHostName) || Inet.isInet6Address(qualifiedHostName)))
/*     */         {
/*  81 */           qualifiedHostName = null;
/*     */         }
/*  83 */         if (qualifiedHostName == null) {
/*     */           
/*  85 */           qualifiedHostName = "unknown-host.unknown-domain";
/*     */         } else {
/*  87 */           qualifiedHostName = qualifiedHostName.trim().toLowerCase();
/*     */         } 
/*     */       } 
/*  90 */       if (providedHostName == null) {
/*     */         
/*  92 */         int idx = qualifiedHostName.indexOf('.');
/*  93 */         providedHostName = (idx == -1) ? qualifiedHostName : qualifiedHostName.substring(0, idx);
/*     */       } 
/*  95 */       if (providedNodeName == null) {
/*  96 */         providedNodeName = providedHostName;
/*     */       }
/*  98 */       return new String[] { providedHostName, qualifiedHostName, providedNodeName };
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @CContext(NativeInfoDirectives.class)
/*     */   @Platforms({Platform.DARWIN.class, Platform.LINUX.class})
/*     */   static final class NativeInfo
/*     */   {
/*     */     @CFunction
/*     */     static native int gethostname(CCharPointer param1CCharPointer, UnsignedWord param1UnsignedWord);
/*     */   }
/*     */ 
/*     */   
/*     */   static final class NativeInfoDirectives
/*     */     implements CContext.Directives
/*     */   {
/*     */     public List<String> getHeaderFiles() {
/* 116 */       return Collections.singletonList("<unistd.h>");
/*     */     }
/*     */   }
/*     */   
/*     */   static final class ProcessSubstitutions {
/*     */     static final int SIZE = 512;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\net\Substitutions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */