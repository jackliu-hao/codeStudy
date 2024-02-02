/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.template.Version;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ public final class DefaultMemberAccessPolicy
/*     */   implements MemberAccessPolicy
/*     */ {
/*  47 */   private static final DefaultMemberAccessPolicy INSTANCE = new DefaultMemberAccessPolicy();
/*     */   
/*     */   private final Set<Class<?>> whitelistRuleFinalClasses;
/*     */   
/*     */   private final Set<Class<?>> whitelistRuleNonFinalClasses;
/*     */   
/*     */   private final WhitelistMemberAccessPolicy whitelistMemberAccessPolicy;
/*     */   
/*     */   private final BlacklistMemberAccessPolicy blacklistMemberAccessPolicy;
/*     */   private final boolean toStringAlwaysExposed;
/*     */   
/*     */   public static DefaultMemberAccessPolicy getInstance(Version incompatibleImprovements) {
/*  59 */     _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
/*     */ 
/*     */     
/*  62 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   private DefaultMemberAccessPolicy() {
/*     */     try {
/*  67 */       ClassLoader classLoader = DefaultMemberAccessPolicy.class.getClassLoader();
/*     */       
/*  69 */       this.whitelistRuleFinalClasses = new HashSet<>();
/*  70 */       this.whitelistRuleNonFinalClasses = new HashSet<>();
/*  71 */       Set<Class<?>> typesWithBlacklistUnlistedRule = new HashSet<>();
/*  72 */       List<MemberSelectorListMemberAccessPolicy.MemberSelector> whitelistMemberSelectors = new ArrayList<>();
/*  73 */       for (String line : loadMemberSelectorFileLines()) {
/*  74 */         line = line.trim();
/*  75 */         if (!MemberSelectorListMemberAccessPolicy.MemberSelector.isIgnoredLine(line)) {
/*  76 */           MemberSelectorListMemberAccessPolicy.MemberSelector memberSelector; if (line.startsWith("@")) {
/*  77 */             Class<?> upperBoundType; String[] lineParts = line.split("\\s+");
/*  78 */             if (lineParts.length != 2) {
/*  79 */               throw new IllegalStateException("Malformed @ line: " + line);
/*     */             }
/*  81 */             String typeName = lineParts[1];
/*     */             
/*     */             try {
/*  84 */               upperBoundType = classLoader.loadClass(typeName);
/*  85 */             } catch (ClassNotFoundException e) {
/*  86 */               upperBoundType = null;
/*     */             } 
/*  88 */             String rule = lineParts[0].substring(1);
/*  89 */             if (rule.equals("whitelistPolicyIfAssignable")) {
/*  90 */               if (upperBoundType != null) {
/*     */                 
/*  92 */                 Set<Class<?>> targetSet = ((upperBoundType.getModifiers() & 0x10) != 0) ? this.whitelistRuleFinalClasses : this.whitelistRuleNonFinalClasses;
/*     */ 
/*     */                 
/*  95 */                 targetSet.add(upperBoundType);
/*     */               }  continue;
/*  97 */             }  if (rule.equals("blacklistUnlistedMembers")) {
/*  98 */               if (upperBoundType != null)
/*  99 */                 typesWithBlacklistUnlistedRule.add(upperBoundType); 
/*     */               continue;
/*     */             } 
/* 102 */             throw new IllegalStateException("Unhandled rule: " + rule);
/*     */           } 
/*     */ 
/*     */           
/*     */           try {
/* 107 */             memberSelector = MemberSelectorListMemberAccessPolicy.MemberSelector.parse(line, classLoader);
/* 108 */           } catch (ClassNotFoundException|NoSuchMethodException|NoSuchFieldException e) {
/*     */             
/* 110 */             memberSelector = null;
/*     */           } 
/* 112 */           if (memberSelector != null) {
/* 113 */             Class<?> upperBoundType = memberSelector.getUpperBoundType();
/* 114 */             if (upperBoundType != null) {
/* 115 */               if (!this.whitelistRuleFinalClasses.contains(upperBoundType) && 
/* 116 */                 !this.whitelistRuleNonFinalClasses.contains(upperBoundType) && 
/* 117 */                 !typesWithBlacklistUnlistedRule.contains(upperBoundType)) {
/* 118 */                 throw new IllegalStateException("Type without rule: " + upperBoundType.getName());
/*     */               }
/*     */               
/* 121 */               whitelistMemberSelectors.add(memberSelector);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 128 */       this.whitelistMemberAccessPolicy = new WhitelistMemberAccessPolicy(whitelistMemberSelectors);
/*     */ 
/*     */       
/* 131 */       List<MemberSelectorListMemberAccessPolicy.MemberSelector> blacklistMemberSelectors = new ArrayList<>();
/* 132 */       for (Class<?> blacklistUnlistedRuleType : typesWithBlacklistUnlistedRule) {
/* 133 */         ClassMemberAccessPolicy classPolicy = this.whitelistMemberAccessPolicy.forClass(blacklistUnlistedRuleType);
/* 134 */         for (Method method : blacklistUnlistedRuleType.getMethods()) {
/* 135 */           if (!classPolicy.isMethodExposed(method)) {
/* 136 */             blacklistMemberSelectors.add(new MemberSelectorListMemberAccessPolicy.MemberSelector(blacklistUnlistedRuleType, method));
/*     */           }
/*     */         } 
/* 139 */         for (Constructor<?> constructor : blacklistUnlistedRuleType.getConstructors()) {
/* 140 */           if (!classPolicy.isConstructorExposed(constructor)) {
/* 141 */             blacklistMemberSelectors.add(new MemberSelectorListMemberAccessPolicy.MemberSelector(blacklistUnlistedRuleType, constructor));
/*     */           }
/*     */         } 
/* 144 */         for (Field field : blacklistUnlistedRuleType.getFields()) {
/* 145 */           if (!classPolicy.isFieldExposed(field)) {
/* 146 */             blacklistMemberSelectors.add(new MemberSelectorListMemberAccessPolicy.MemberSelector(blacklistUnlistedRuleType, field));
/*     */           }
/*     */         } 
/*     */       } 
/* 150 */       this.blacklistMemberAccessPolicy = new BlacklistMemberAccessPolicy(blacklistMemberSelectors);
/*     */       
/* 152 */       this
/*     */         
/* 154 */         .toStringAlwaysExposed = (this.whitelistMemberAccessPolicy.isToStringAlwaysExposed() && this.blacklistMemberAccessPolicy.isToStringAlwaysExposed());
/* 155 */     } catch (Exception e) {
/* 156 */       throw new IllegalStateException("Couldn't init " + getClass().getName() + " instance", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static List<String> loadMemberSelectorFileLines() throws IOException {
/* 161 */     List<String> whitelist = new ArrayList<>();
/* 162 */     try (BufferedReader reader = new BufferedReader(new InputStreamReader(DefaultMemberAccessPolicy.class
/*     */             
/* 164 */             .getResourceAsStream("DefaultMemberAccessPolicy-rules"), "UTF-8"))) {
/*     */       String line;
/*     */       
/* 167 */       while ((line = reader.readLine()) != null) {
/* 168 */         whitelist.add(line);
/*     */       }
/*     */     } 
/*     */     
/* 172 */     return whitelist;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClassMemberAccessPolicy forClass(Class<?> contextClass) {
/* 177 */     if (isTypeWithWhitelistRule(contextClass)) {
/* 178 */       return this.whitelistMemberAccessPolicy.forClass(contextClass);
/*     */     }
/* 180 */     return this.blacklistMemberAccessPolicy.forClass(contextClass);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isToStringAlwaysExposed() {
/* 186 */     return this.toStringAlwaysExposed;
/*     */   }
/*     */   
/*     */   private boolean isTypeWithWhitelistRule(Class<?> contextClass) {
/* 190 */     if (this.whitelistRuleFinalClasses.contains(contextClass)) {
/* 191 */       return true;
/*     */     }
/* 193 */     for (Class<?> nonFinalClass : this.whitelistRuleNonFinalClasses) {
/* 194 */       if (nonFinalClass.isAssignableFrom(contextClass)) {
/* 195 */         return true;
/*     */       }
/*     */     } 
/* 198 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\DefaultMemberAccessPolicy.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */