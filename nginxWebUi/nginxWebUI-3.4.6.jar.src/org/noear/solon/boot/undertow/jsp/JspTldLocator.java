/*     */ package org.noear.solon.boot.undertow.jsp;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import org.apache.jasper.deploy.FunctionInfo;
/*     */ import org.apache.jasper.deploy.TagAttributeInfo;
/*     */ import org.apache.jasper.deploy.TagFileInfo;
/*     */ import org.apache.jasper.deploy.TagInfo;
/*     */ import org.apache.jasper.deploy.TagLibraryInfo;
/*     */ import org.apache.jasper.deploy.TagLibraryValidatorInfo;
/*     */ import org.apache.jasper.deploy.TagVariableInfo;
/*     */ import org.jboss.annotation.javaee.Icon;
/*     */ import org.jboss.metadata.javaee.spec.DescriptionGroupMetaData;
/*     */ import org.jboss.metadata.javaee.spec.ParamValueMetaData;
/*     */ import org.jboss.metadata.parser.jsp.TldMetaDataParser;
/*     */ import org.jboss.metadata.parser.util.NoopXMLResolver;
/*     */ import org.jboss.metadata.web.spec.AttributeMetaData;
/*     */ import org.jboss.metadata.web.spec.FunctionMetaData;
/*     */ import org.jboss.metadata.web.spec.TagFileMetaData;
/*     */ import org.jboss.metadata.web.spec.TagMetaData;
/*     */ import org.jboss.metadata.web.spec.TldMetaData;
/*     */ import org.jboss.metadata.web.spec.VariableMetaData;
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.JarClassLoader;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.noear.solon.core.util.ScanUtil;
/*     */ import org.noear.solon.ext.SupplierEx;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JspTldLocator
/*     */ {
/*     */   public static HashMap<String, TagLibraryInfo> createTldInfos(String webinfo_path) throws IOException {
/*  50 */     List<URL> urls = getURLs();
/*     */     
/*  52 */     HashMap<String, TagLibraryInfo> tagLibInfos = new HashMap<>();
/*     */ 
/*     */     
/*  55 */     for (URL url : urls) {
/*  56 */       if (url.toString().endsWith(".jar")) {
/*     */         try {
/*  58 */           String file_uri = URLDecoder.decode(url.getFile(), Solon.encoding());
/*     */           
/*  60 */           JarFile jarFile = new JarFile(file_uri);
/*     */           
/*  62 */           Enumeration<JarEntry> entries = jarFile.entries();
/*     */           
/*  64 */           while (entries.hasMoreElements()) {
/*  65 */             JarEntry entry = entries.nextElement();
/*     */             
/*  67 */             if (entry.getName().endsWith(".tld")) {
/*  68 */               loadTagLibraryInfo(tagLibInfos, () -> {
/*     */                     JarEntry fileEntry = jarFile.getJarEntry(entry.getName());
/*     */                     return jarFile.getInputStream(fileEntry);
/*     */                   });
/*     */             }
/*     */           } 
/*  74 */         } catch (Throwable ex) {
/*  75 */           EventBus.push(ex);
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  83 */       ScanUtil.scan((ClassLoader)JarClassLoader.global(), webinfo_path, n -> n.endsWith(".tld")).forEach(uri -> loadTagLibraryInfo(tagLibInfos, ()));
/*     */     
/*     */     }
/*  86 */     catch (Throwable ex) {
/*  87 */       EventBus.push(ex);
/*     */     } 
/*     */     
/*  90 */     return tagLibInfos;
/*     */   }
/*     */   
/*     */   static List<URL> getURLs() {
/*  94 */     List<URL> urls = new ArrayList<>();
/*     */     
/*  96 */     String classPath = System.getProperty("java.class.path");
/*     */     
/*  98 */     if (classPath != null) {
/*  99 */       String separator = System.getProperty("path.separator");
/* 100 */       if (Utils.isEmpty(separator)) {
/* 101 */         separator = ":";
/*     */       }
/*     */       
/* 104 */       String[] list = classPath.split(separator);
/* 105 */       for (String uri : list) {
/*     */ 
/*     */ 
/*     */         
/* 109 */         if (uri.endsWith(".jar") || uri.indexOf(".jar ") > 0) {
/*     */           try {
/* 111 */             if (uri.startsWith("/")) {
/* 112 */               uri = "file:" + uri;
/*     */             }
/*     */             
/* 115 */             URL url = URI.create(uri).toURL();
/* 116 */             urls.add(url);
/* 117 */           } catch (Throwable throwable) {}
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 124 */     return urls;
/*     */   }
/*     */   
/*     */   static void loadTagLibraryInfo(HashMap<String, TagLibraryInfo> tagLibInfos, SupplierEx<InputStream> supplier) {
/* 128 */     InputStream is = null;
/*     */     
/*     */     try {
/* 131 */       is = (InputStream)supplier.get();
/*     */       
/* 133 */       XMLInputFactory inputFactory = XMLInputFactory.newInstance();
/* 134 */       inputFactory.setXMLResolver(NoopXMLResolver.create());
/* 135 */       XMLStreamReader xmlReader = inputFactory.createXMLStreamReader(is);
/* 136 */       TldMetaData tldMetadata = TldMetaDataParser.parse(xmlReader);
/* 137 */       TagLibraryInfo taglibInfo = getTagLibraryInfo(tldMetadata);
/* 138 */       if (!tagLibInfos.containsKey(taglibInfo.getUri())) {
/* 139 */         tagLibInfos.put(taglibInfo.getUri(), taglibInfo);
/*     */       }
/*     */     }
/* 142 */     catch (Throwable e) {
/* 143 */       EventBus.push(e);
/*     */     } finally {
/*     */       try {
/* 146 */         if (is != null) {
/* 147 */           is.close();
/*     */         }
/* 149 */       } catch (IOException iOException) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static TagLibraryInfo getTagLibraryInfo(TldMetaData tldMetaData) {
/* 155 */     TagLibraryInfo tagLibraryInfo = new TagLibraryInfo();
/* 156 */     tagLibraryInfo.setTlibversion(tldMetaData.getTlibVersion());
/* 157 */     if (tldMetaData.getJspVersion() == null) {
/* 158 */       tagLibraryInfo.setJspversion(tldMetaData.getVersion());
/*     */     } else {
/* 160 */       tagLibraryInfo.setJspversion(tldMetaData.getJspVersion());
/*     */     } 
/* 162 */     tagLibraryInfo.setShortname(tldMetaData.getShortName());
/* 163 */     tagLibraryInfo.setUri(tldMetaData.getUri());
/* 164 */     if (tldMetaData.getDescriptionGroup() != null) {
/* 165 */       tagLibraryInfo.setInfo(tldMetaData.getDescriptionGroup().getDescription());
/*     */     }
/*     */     
/* 168 */     if (tldMetaData.getValidator() != null) {
/* 169 */       TagLibraryValidatorInfo tagLibraryValidatorInfo = new TagLibraryValidatorInfo();
/* 170 */       tagLibraryValidatorInfo.setValidatorClass(tldMetaData.getValidator().getValidatorClass());
/* 171 */       if (tldMetaData.getValidator().getInitParams() != null) {
/* 172 */         for (ParamValueMetaData paramValueMetaData : tldMetaData.getValidator().getInitParams()) {
/* 173 */           tagLibraryValidatorInfo.addInitParam(paramValueMetaData.getParamName(), paramValueMetaData.getParamValue());
/*     */         }
/*     */       }
/* 176 */       tagLibraryInfo.setValidator(tagLibraryValidatorInfo);
/*     */     } 
/*     */     
/* 179 */     if (tldMetaData.getTags() != null) {
/* 180 */       for (TagMetaData tagMetaData : tldMetaData.getTags()) {
/* 181 */         TagInfo tagInfo = new TagInfo();
/* 182 */         tagInfo.setTagName(tagMetaData.getName());
/* 183 */         tagInfo.setTagClassName(tagMetaData.getTagClass());
/* 184 */         tagInfo.setTagExtraInfo(tagMetaData.getTeiClass());
/* 185 */         if (tagMetaData.getBodyContent() != null) {
/* 186 */           tagInfo.setBodyContent(tagMetaData.getBodyContent().toString());
/*     */         }
/* 188 */         tagInfo.setDynamicAttributes(tagMetaData.getDynamicAttributes());
/*     */         
/* 190 */         if (tagMetaData.getDescriptionGroup() != null) {
/* 191 */           DescriptionGroupMetaData descriptionGroup = tagMetaData.getDescriptionGroup();
/* 192 */           if (descriptionGroup.getIcons() != null && descriptionGroup.getIcons().value() != null && (descriptionGroup
/* 193 */             .getIcons().value()).length > 0) {
/* 194 */             Icon icon = descriptionGroup.getIcons().value()[0];
/* 195 */             tagInfo.setLargeIcon(icon.largeIcon());
/* 196 */             tagInfo.setSmallIcon(icon.smallIcon());
/*     */           } 
/* 198 */           tagInfo.setInfoString(descriptionGroup.getDescription());
/* 199 */           tagInfo.setDisplayName(descriptionGroup.getDisplayName());
/*     */         } 
/*     */         
/* 202 */         if (tagMetaData.getVariables() != null) {
/* 203 */           for (VariableMetaData variableMetaData : tagMetaData.getVariables()) {
/* 204 */             TagVariableInfo tagVariableInfo = new TagVariableInfo();
/* 205 */             tagVariableInfo.setNameGiven(variableMetaData.getNameGiven());
/* 206 */             tagVariableInfo.setNameFromAttribute(variableMetaData.getNameFromAttribute());
/* 207 */             tagVariableInfo.setClassName(variableMetaData.getVariableClass());
/* 208 */             tagVariableInfo.setDeclare(variableMetaData.getDeclare());
/* 209 */             if (variableMetaData.getScope() != null) {
/* 210 */               tagVariableInfo.setScope(variableMetaData.getScope().toString());
/*     */             }
/* 212 */             tagInfo.addTagVariableInfo(tagVariableInfo);
/*     */           } 
/*     */         }
/*     */         
/* 216 */         if (tagMetaData.getAttributes() != null) {
/* 217 */           for (AttributeMetaData attributeMetaData : tagMetaData.getAttributes()) {
/* 218 */             TagAttributeInfo ari = new TagAttributeInfo();
/* 219 */             ari.setName(attributeMetaData.getName());
/* 220 */             ari.setType(attributeMetaData.getType());
/* 221 */             ari.setReqTime(attributeMetaData.getRtexprvalue());
/* 222 */             ari.setRequired(attributeMetaData.getRequired());
/* 223 */             ari.setFragment(attributeMetaData.getFragment());
/* 224 */             if (attributeMetaData.getDeferredValue() != null) {
/* 225 */               ari.setDeferredValue("true");
/* 226 */               ari.setExpectedTypeName(attributeMetaData.getDeferredValue().getType());
/*     */             } else {
/* 228 */               ari.setDeferredValue("false");
/*     */             } 
/* 230 */             if (attributeMetaData.getDeferredMethod() != null) {
/* 231 */               ari.setDeferredMethod("true");
/* 232 */               ari.setMethodSignature(attributeMetaData.getDeferredMethod().getMethodSignature());
/*     */             } else {
/* 234 */               ari.setDeferredMethod("false");
/*     */             } 
/* 236 */             tagInfo.addTagAttributeInfo(ari);
/*     */           } 
/*     */         }
/* 239 */         tagLibraryInfo.addTagInfo(tagInfo);
/*     */       } 
/*     */     }
/*     */     
/* 243 */     if (tldMetaData.getTagFiles() != null) {
/* 244 */       for (TagFileMetaData tagFileMetaData : tldMetaData.getTagFiles()) {
/* 245 */         TagFileInfo tfi = new TagFileInfo();
/* 246 */         tfi.setName(tagFileMetaData.getName());
/* 247 */         tfi.setPath(tagFileMetaData.getPath());
/* 248 */         tagLibraryInfo.addTagFileInfo(tfi);
/*     */       } 
/*     */     }
/*     */     
/* 252 */     if (tldMetaData.getFunctions() != null) {
/* 253 */       for (FunctionMetaData functionMetaData : tldMetaData.getFunctions()) {
/* 254 */         FunctionInfo fi = new FunctionInfo();
/* 255 */         fi.setName(functionMetaData.getName());
/* 256 */         fi.setFunctionClass(functionMetaData.getFunctionClass());
/* 257 */         fi.setFunctionSignature(functionMetaData.getFunctionSignature());
/* 258 */         tagLibraryInfo.addFunctionInfo(fi);
/*     */       } 
/*     */     }
/*     */     
/* 262 */     return tagLibraryInfo;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boo\\undertow\jsp\JspTldLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */