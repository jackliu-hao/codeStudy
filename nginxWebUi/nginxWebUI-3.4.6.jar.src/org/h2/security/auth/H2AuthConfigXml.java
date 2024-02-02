/*     */ package org.h2.security.auth;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class H2AuthConfigXml
/*     */   extends DefaultHandler
/*     */ {
/*     */   private H2AuthConfig result;
/*     */   private HasConfigProperties lastConfigProperties;
/*     */   
/*     */   public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
/*     */     RealmConfig realmConfig;
/*     */     UserToRolesMapperConfig userToRolesMapperConfig;
/*  28 */     switch (paramString3) {
/*     */       case "h2Auth":
/*  30 */         this.result = new H2AuthConfig();
/*  31 */         this.result.setAllowUserRegistration("true".equals(
/*  32 */               getAttributeValueOr("allowUserRegistration", paramAttributes, "false")));
/*  33 */         this.result.setCreateMissingRoles("true".equals(
/*  34 */               getAttributeValueOr("createMissingRoles", paramAttributes, "true")));
/*     */         return;
/*     */       case "realm":
/*  37 */         realmConfig = new RealmConfig();
/*  38 */         realmConfig.setName(getMandatoryAttributeValue("name", paramAttributes));
/*  39 */         realmConfig.setValidatorClass(getMandatoryAttributeValue("validatorClass", paramAttributes));
/*  40 */         this.result.getRealms().add(realmConfig);
/*  41 */         this.lastConfigProperties = realmConfig;
/*     */         return;
/*     */       case "userToRolesMapper":
/*  44 */         userToRolesMapperConfig = new UserToRolesMapperConfig();
/*  45 */         userToRolesMapperConfig.setClassName(getMandatoryAttributeValue("className", paramAttributes));
/*  46 */         this.result.getUserToRolesMappers().add(userToRolesMapperConfig);
/*  47 */         this.lastConfigProperties = userToRolesMapperConfig;
/*     */         return;
/*     */       case "property":
/*  50 */         if (this.lastConfigProperties == null) {
/*  51 */           throw new SAXException("property element in the wrong place");
/*     */         }
/*  53 */         this.lastConfigProperties.getProperties().add(new PropertyConfig(
/*  54 */               getMandatoryAttributeValue("name", paramAttributes), 
/*  55 */               getMandatoryAttributeValue("value", paramAttributes)));
/*     */         return;
/*     */     } 
/*  58 */     throw new SAXException("unexpected element " + paramString3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
/*  65 */     if (this.lastConfigProperties != null && !paramString3.equals("property")) {
/*  66 */       this.lastConfigProperties = null;
/*     */     }
/*     */   }
/*     */   
/*     */   private static String getMandatoryAttributeValue(String paramString, Attributes paramAttributes) throws SAXException {
/*  71 */     String str = paramAttributes.getValue(paramString);
/*  72 */     if (str == null || str.trim().equals("")) {
/*  73 */       throw new SAXException("missing attribute " + paramString);
/*     */     }
/*  75 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String getAttributeValueOr(String paramString1, Attributes paramAttributes, String paramString2) {
/*  80 */     String str = paramAttributes.getValue(paramString1);
/*  81 */     if (str == null || str.trim().equals("")) {
/*  82 */       return paramString2;
/*     */     }
/*  84 */     return str;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public H2AuthConfig getResult() {
/*  93 */     return this.result;
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
/*     */   public static H2AuthConfig parseFrom(URL paramURL) throws SAXException, IOException, ParserConfigurationException {
/* 107 */     try (InputStream null = paramURL.openStream()) {
/* 108 */       return parseFrom(inputStream);
/*     */     } 
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
/*     */   public static H2AuthConfig parseFrom(InputStream paramInputStream) throws SAXException, IOException, ParserConfigurationException {
/* 123 */     SAXParser sAXParser = SAXParserFactory.newInstance().newSAXParser();
/* 124 */     H2AuthConfigXml h2AuthConfigXml = new H2AuthConfigXml();
/* 125 */     sAXParser.parse(paramInputStream, h2AuthConfigXml);
/* 126 */     return h2AuthConfigXml.getResult();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\H2AuthConfigXml.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */