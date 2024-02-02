/*     */ package ch.qos.logback.core.joran.action;
/*     */ 
/*     */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*     */ import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
/*     */ import ch.qos.logback.core.util.Loader;
/*     */ import ch.qos.logback.core.util.OptionHelper;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.Properties;
/*     */ import org.xml.sax.Attributes;
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
/*     */ public class PropertyAction
/*     */   extends Action
/*     */ {
/*     */   static final String RESOURCE_ATTRIBUTE = "resource";
/*  45 */   static String INVALID_ATTRIBUTES = "In <property> element, either the \"file\" attribute alone, or the \"resource\" element alone, or both the \"name\" and \"value\" attributes must be set.";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void begin(InterpretationContext ec, String localName, Attributes attributes) {
/*  55 */     if ("substitutionProperty".equals(localName)) {
/*  56 */       addWarn("[substitutionProperty] element has been deprecated. Please use the [property] element instead.");
/*     */     }
/*     */     
/*  59 */     String name = attributes.getValue("name");
/*  60 */     String value = attributes.getValue("value");
/*  61 */     String scopeStr = attributes.getValue("scope");
/*     */     
/*  63 */     ActionUtil.Scope scope = ActionUtil.stringToScope(scopeStr);
/*     */     
/*  65 */     if (checkFileAttributeSanity(attributes)) {
/*  66 */       String file = attributes.getValue("file");
/*  67 */       file = ec.subst(file);
/*     */       try {
/*  69 */         FileInputStream istream = new FileInputStream(file);
/*  70 */         loadAndSetProperties(ec, istream, scope);
/*  71 */       } catch (FileNotFoundException e) {
/*  72 */         addError("Could not find properties file [" + file + "].");
/*  73 */       } catch (IOException e1) {
/*  74 */         addError("Could not read properties file [" + file + "].", e1);
/*     */       } 
/*  76 */     } else if (checkResourceAttributeSanity(attributes)) {
/*  77 */       String resource = attributes.getValue("resource");
/*  78 */       resource = ec.subst(resource);
/*  79 */       URL resourceURL = Loader.getResourceBySelfClassLoader(resource);
/*  80 */       if (resourceURL == null) {
/*  81 */         addError("Could not find resource [" + resource + "].");
/*     */       } else {
/*     */         try {
/*  84 */           InputStream istream = resourceURL.openStream();
/*  85 */           loadAndSetProperties(ec, istream, scope);
/*  86 */         } catch (IOException e) {
/*  87 */           addError("Could not read resource file [" + resource + "].", e);
/*     */         } 
/*     */       } 
/*  90 */     } else if (checkValueNameAttributesSanity(attributes)) {
/*  91 */       value = RegularEscapeUtil.basicEscape(value);
/*     */       
/*  93 */       value = value.trim();
/*  94 */       value = ec.subst(value);
/*  95 */       ActionUtil.setProperty(ec, name, value, scope);
/*     */     } else {
/*     */       
/*  98 */       addError(INVALID_ATTRIBUTES);
/*     */     } 
/*     */   }
/*     */   
/*     */   void loadAndSetProperties(InterpretationContext ec, InputStream istream, ActionUtil.Scope scope) throws IOException {
/* 103 */     Properties props = new Properties();
/* 104 */     props.load(istream);
/* 105 */     istream.close();
/* 106 */     ActionUtil.setProperties(ec, props, scope);
/*     */   }
/*     */   
/*     */   boolean checkFileAttributeSanity(Attributes attributes) {
/* 110 */     String file = attributes.getValue("file");
/* 111 */     String name = attributes.getValue("name");
/* 112 */     String value = attributes.getValue("value");
/* 113 */     String resource = attributes.getValue("resource");
/*     */     
/* 115 */     return (!OptionHelper.isEmpty(file) && OptionHelper.isEmpty(name) && OptionHelper.isEmpty(value) && OptionHelper.isEmpty(resource));
/*     */   }
/*     */   
/*     */   boolean checkResourceAttributeSanity(Attributes attributes) {
/* 119 */     String file = attributes.getValue("file");
/* 120 */     String name = attributes.getValue("name");
/* 121 */     String value = attributes.getValue("value");
/* 122 */     String resource = attributes.getValue("resource");
/*     */     
/* 124 */     return (!OptionHelper.isEmpty(resource) && OptionHelper.isEmpty(name) && OptionHelper.isEmpty(value) && OptionHelper.isEmpty(file));
/*     */   }
/*     */   
/*     */   boolean checkValueNameAttributesSanity(Attributes attributes) {
/* 128 */     String file = attributes.getValue("file");
/* 129 */     String name = attributes.getValue("name");
/* 130 */     String value = attributes.getValue("value");
/* 131 */     String resource = attributes.getValue("resource");
/*     */     
/* 133 */     return (!OptionHelper.isEmpty(name) && !OptionHelper.isEmpty(value) && OptionHelper.isEmpty(file) && OptionHelper.isEmpty(resource));
/*     */   }
/*     */   
/*     */   public void end(InterpretationContext ec, String name) {}
/*     */   
/*     */   public void finish(InterpretationContext ec) {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\PropertyAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */