package freemarker.ext.beans;

import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class DefaultMemberAccessPolicy implements MemberAccessPolicy {
   private static final DefaultMemberAccessPolicy INSTANCE = new DefaultMemberAccessPolicy();
   private final Set<Class<?>> whitelistRuleFinalClasses;
   private final Set<Class<?>> whitelistRuleNonFinalClasses;
   private final WhitelistMemberAccessPolicy whitelistMemberAccessPolicy;
   private final BlacklistMemberAccessPolicy blacklistMemberAccessPolicy;
   private final boolean toStringAlwaysExposed;

   public static DefaultMemberAccessPolicy getInstance(Version incompatibleImprovements) {
      _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
      return INSTANCE;
   }

   private DefaultMemberAccessPolicy() {
      try {
         ClassLoader classLoader = DefaultMemberAccessPolicy.class.getClassLoader();
         this.whitelistRuleFinalClasses = new HashSet();
         this.whitelistRuleNonFinalClasses = new HashSet();
         Set<Class<?>> typesWithBlacklistUnlistedRule = new HashSet();
         List<MemberSelectorListMemberAccessPolicy.MemberSelector> whitelistMemberSelectors = new ArrayList();
         Iterator var4 = loadMemberSelectorFileLines().iterator();

         while(var4.hasNext()) {
            String line = (String)var4.next();
            line = line.trim();
            if (!MemberSelectorListMemberAccessPolicy.MemberSelector.isIgnoredLine(line)) {
               if (line.startsWith("@")) {
                  String[] lineParts = line.split("\\s+");
                  if (lineParts.length != 2) {
                     throw new IllegalStateException("Malformed @ line: " + line);
                  }

                  String typeName = lineParts[1];

                  Class upperBoundType;
                  try {
                     upperBoundType = classLoader.loadClass(typeName);
                  } catch (ClassNotFoundException var13) {
                     upperBoundType = null;
                  }

                  String rule = lineParts[0].substring(1);
                  if (rule.equals("whitelistPolicyIfAssignable")) {
                     if (upperBoundType != null) {
                        Set<Class<?>> targetSet = (upperBoundType.getModifiers() & 16) != 0 ? this.whitelistRuleFinalClasses : this.whitelistRuleNonFinalClasses;
                        targetSet.add(upperBoundType);
                     }
                  } else {
                     if (!rule.equals("blacklistUnlistedMembers")) {
                        throw new IllegalStateException("Unhandled rule: " + rule);
                     }

                     if (upperBoundType != null) {
                        typesWithBlacklistUnlistedRule.add(upperBoundType);
                     }
                  }
               } else {
                  MemberSelectorListMemberAccessPolicy.MemberSelector memberSelector;
                  try {
                     memberSelector = MemberSelectorListMemberAccessPolicy.MemberSelector.parse(line, classLoader);
                  } catch (NoSuchMethodException | NoSuchFieldException | ClassNotFoundException var12) {
                     memberSelector = null;
                  }

                  if (memberSelector != null) {
                     Class<?> upperBoundType = memberSelector.getUpperBoundType();
                     if (upperBoundType != null) {
                        if (!this.whitelistRuleFinalClasses.contains(upperBoundType) && !this.whitelistRuleNonFinalClasses.contains(upperBoundType) && !typesWithBlacklistUnlistedRule.contains(upperBoundType)) {
                           throw new IllegalStateException("Type without rule: " + upperBoundType.getName());
                        }

                        whitelistMemberSelectors.add(memberSelector);
                     }
                  }
               }
            }
         }

         this.whitelistMemberAccessPolicy = new WhitelistMemberAccessPolicy(whitelistMemberSelectors);
         List<MemberSelectorListMemberAccessPolicy.MemberSelector> blacklistMemberSelectors = new ArrayList();
         Iterator var16 = typesWithBlacklistUnlistedRule.iterator();

         while(var16.hasNext()) {
            Class<?> blacklistUnlistedRuleType = (Class)var16.next();
            ClassMemberAccessPolicy classPolicy = this.whitelistMemberAccessPolicy.forClass(blacklistUnlistedRuleType);
            Method[] var21 = blacklistUnlistedRuleType.getMethods();
            int var22 = var21.length;

            int var25;
            for(var25 = 0; var25 < var22; ++var25) {
               Method method = var21[var25];
               if (!classPolicy.isMethodExposed(method)) {
                  blacklistMemberSelectors.add(new MemberSelectorListMemberAccessPolicy.MemberSelector(blacklistUnlistedRuleType, method));
               }
            }

            Constructor[] var23 = blacklistUnlistedRuleType.getConstructors();
            var22 = var23.length;

            for(var25 = 0; var25 < var22; ++var25) {
               Constructor<?> constructor = var23[var25];
               if (!classPolicy.isConstructorExposed(constructor)) {
                  blacklistMemberSelectors.add(new MemberSelectorListMemberAccessPolicy.MemberSelector(blacklistUnlistedRuleType, constructor));
               }
            }

            Field[] var24 = blacklistUnlistedRuleType.getFields();
            var22 = var24.length;

            for(var25 = 0; var25 < var22; ++var25) {
               Field field = var24[var25];
               if (!classPolicy.isFieldExposed(field)) {
                  blacklistMemberSelectors.add(new MemberSelectorListMemberAccessPolicy.MemberSelector(blacklistUnlistedRuleType, field));
               }
            }
         }

         this.blacklistMemberAccessPolicy = new BlacklistMemberAccessPolicy(blacklistMemberSelectors);
         this.toStringAlwaysExposed = this.whitelistMemberAccessPolicy.isToStringAlwaysExposed() && this.blacklistMemberAccessPolicy.isToStringAlwaysExposed();
      } catch (Exception var14) {
         throw new IllegalStateException("Couldn't init " + this.getClass().getName() + " instance", var14);
      }
   }

   private static List<String> loadMemberSelectorFileLines() throws IOException {
      List<String> whitelist = new ArrayList();
      BufferedReader reader = new BufferedReader(new InputStreamReader(DefaultMemberAccessPolicy.class.getResourceAsStream("DefaultMemberAccessPolicy-rules"), "UTF-8"));
      Throwable var2 = null;

      try {
         String line;
         try {
            while((line = reader.readLine()) != null) {
               whitelist.add(line);
            }
         } catch (Throwable var11) {
            var2 = var11;
            throw var11;
         }
      } finally {
         if (reader != null) {
            if (var2 != null) {
               try {
                  reader.close();
               } catch (Throwable var10) {
                  var2.addSuppressed(var10);
               }
            } else {
               reader.close();
            }
         }

      }

      return whitelist;
   }

   public ClassMemberAccessPolicy forClass(Class<?> contextClass) {
      return this.isTypeWithWhitelistRule(contextClass) ? this.whitelistMemberAccessPolicy.forClass(contextClass) : this.blacklistMemberAccessPolicy.forClass(contextClass);
   }

   public boolean isToStringAlwaysExposed() {
      return this.toStringAlwaysExposed;
   }

   private boolean isTypeWithWhitelistRule(Class<?> contextClass) {
      if (this.whitelistRuleFinalClasses.contains(contextClass)) {
         return true;
      } else {
         Iterator var2 = this.whitelistRuleNonFinalClasses.iterator();

         Class nonFinalClass;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            nonFinalClass = (Class)var2.next();
         } while(!nonFinalClass.isAssignableFrom(contextClass));

         return true;
      }
   }
}
