package org.noear.solon.boot.undertow.jsp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.apache.jasper.deploy.FunctionInfo;
import org.apache.jasper.deploy.TagAttributeInfo;
import org.apache.jasper.deploy.TagFileInfo;
import org.apache.jasper.deploy.TagInfo;
import org.apache.jasper.deploy.TagLibraryInfo;
import org.apache.jasper.deploy.TagLibraryValidatorInfo;
import org.apache.jasper.deploy.TagVariableInfo;
import org.jboss.annotation.javaee.Icon;
import org.jboss.metadata.javaee.spec.DescriptionGroupMetaData;
import org.jboss.metadata.javaee.spec.ParamValueMetaData;
import org.jboss.metadata.parser.jsp.TldMetaDataParser;
import org.jboss.metadata.parser.util.NoopXMLResolver;
import org.jboss.metadata.web.spec.AttributeMetaData;
import org.jboss.metadata.web.spec.FunctionMetaData;
import org.jboss.metadata.web.spec.TagFileMetaData;
import org.jboss.metadata.web.spec.TagMetaData;
import org.jboss.metadata.web.spec.TldMetaData;
import org.jboss.metadata.web.spec.VariableMetaData;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ScanUtil;
import org.noear.solon.ext.SupplierEx;

public class JspTldLocator {
   public static HashMap<String, TagLibraryInfo> createTldInfos(String webinfo_path) throws IOException {
      List<URL> urls = getURLs();
      HashMap<String, TagLibraryInfo> tagLibInfos = new HashMap();
      Iterator var3 = urls.iterator();

      while(true) {
         URL url;
         do {
            if (!var3.hasNext()) {
               try {
                  ScanUtil.scan(JarClassLoader.global(), webinfo_path, (n) -> {
                     return n.endsWith(".tld");
                  }).forEach((uri) -> {
                     loadTagLibraryInfo(tagLibInfos, () -> {
                        return Utils.getResource(uri).openStream();
                     });
                  });
               } catch (Throwable var9) {
                  EventBus.push(var9);
               }

               return tagLibInfos;
            }

            url = (URL)var3.next();
         } while(!url.toString().endsWith(".jar"));

         try {
            String file_uri = URLDecoder.decode(url.getFile(), Solon.encoding());
            JarFile jarFile = new JarFile(file_uri);
            Enumeration<JarEntry> entries = jarFile.entries();

            while(entries.hasMoreElements()) {
               JarEntry entry = (JarEntry)entries.nextElement();
               if (entry.getName().endsWith(".tld")) {
                  loadTagLibraryInfo(tagLibInfos, () -> {
                     JarEntry fileEntry = jarFile.getJarEntry(entry.getName());
                     return jarFile.getInputStream(fileEntry);
                  });
               }
            }
         } catch (Throwable var10) {
            EventBus.push(var10);
         }
      }
   }

   static List<URL> getURLs() {
      List<URL> urls = new ArrayList();
      String classPath = System.getProperty("java.class.path");
      if (classPath != null) {
         String separator = System.getProperty("path.separator");
         if (Utils.isEmpty(separator)) {
            separator = ":";
         }

         String[] list = classPath.split(separator);
         String[] var4 = list;
         int var5 = list.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String uri = var4[var6];
            if (uri.endsWith(".jar") || uri.indexOf(".jar ") > 0) {
               try {
                  if (uri.startsWith("/")) {
                     uri = "file:" + uri;
                  }

                  URL url = URI.create(uri).toURL();
                  urls.add(url);
               } catch (Throwable var9) {
               }
            }
         }
      }

      return urls;
   }

   static void loadTagLibraryInfo(HashMap<String, TagLibraryInfo> tagLibInfos, SupplierEx<InputStream> supplier) {
      InputStream is = null;

      try {
         is = (InputStream)supplier.get();
         XMLInputFactory inputFactory = XMLInputFactory.newInstance();
         inputFactory.setXMLResolver(NoopXMLResolver.create());
         XMLStreamReader xmlReader = inputFactory.createXMLStreamReader(is);
         TldMetaData tldMetadata = TldMetaDataParser.parse(xmlReader);
         TagLibraryInfo taglibInfo = getTagLibraryInfo(tldMetadata);
         if (!tagLibInfos.containsKey(taglibInfo.getUri())) {
            tagLibInfos.put(taglibInfo.getUri(), taglibInfo);
         }
      } catch (Throwable var15) {
         EventBus.push(var15);
      } finally {
         try {
            if (is != null) {
               is.close();
            }
         } catch (IOException var14) {
         }

      }

   }

   static TagLibraryInfo getTagLibraryInfo(TldMetaData tldMetaData) {
      TagLibraryInfo tagLibraryInfo = new TagLibraryInfo();
      tagLibraryInfo.setTlibversion(tldMetaData.getTlibVersion());
      if (tldMetaData.getJspVersion() == null) {
         tagLibraryInfo.setJspversion(tldMetaData.getVersion());
      } else {
         tagLibraryInfo.setJspversion(tldMetaData.getJspVersion());
      }

      tagLibraryInfo.setShortname(tldMetaData.getShortName());
      tagLibraryInfo.setUri(tldMetaData.getUri());
      if (tldMetaData.getDescriptionGroup() != null) {
         tagLibraryInfo.setInfo(tldMetaData.getDescriptionGroup().getDescription());
      }

      if (tldMetaData.getValidator() != null) {
         TagLibraryValidatorInfo tagLibraryValidatorInfo = new TagLibraryValidatorInfo();
         tagLibraryValidatorInfo.setValidatorClass(tldMetaData.getValidator().getValidatorClass());
         if (tldMetaData.getValidator().getInitParams() != null) {
            Iterator var3 = tldMetaData.getValidator().getInitParams().iterator();

            while(var3.hasNext()) {
               ParamValueMetaData paramValueMetaData = (ParamValueMetaData)var3.next();
               tagLibraryValidatorInfo.addInitParam(paramValueMetaData.getParamName(), paramValueMetaData.getParamValue());
            }
         }

         tagLibraryInfo.setValidator(tagLibraryValidatorInfo);
      }

      Iterator var8;
      TagInfo tagInfo;
      if (tldMetaData.getTags() != null) {
         for(var8 = tldMetaData.getTags().iterator(); var8.hasNext(); tagLibraryInfo.addTagInfo(tagInfo)) {
            TagMetaData tagMetaData = (TagMetaData)var8.next();
            tagInfo = new TagInfo();
            tagInfo.setTagName(tagMetaData.getName());
            tagInfo.setTagClassName(tagMetaData.getTagClass());
            tagInfo.setTagExtraInfo(tagMetaData.getTeiClass());
            if (tagMetaData.getBodyContent() != null) {
               tagInfo.setBodyContent(tagMetaData.getBodyContent().toString());
            }

            tagInfo.setDynamicAttributes(tagMetaData.getDynamicAttributes());
            if (tagMetaData.getDescriptionGroup() != null) {
               DescriptionGroupMetaData descriptionGroup = tagMetaData.getDescriptionGroup();
               if (descriptionGroup.getIcons() != null && descriptionGroup.getIcons().value() != null && descriptionGroup.getIcons().value().length > 0) {
                  Icon icon = descriptionGroup.getIcons().value()[0];
                  tagInfo.setLargeIcon(icon.largeIcon());
                  tagInfo.setSmallIcon(icon.smallIcon());
               }

               tagInfo.setInfoString(descriptionGroup.getDescription());
               tagInfo.setDisplayName(descriptionGroup.getDisplayName());
            }

            TagVariableInfo tagVariableInfo;
            Iterator var15;
            if (tagMetaData.getVariables() != null) {
               for(var15 = tagMetaData.getVariables().iterator(); var15.hasNext(); tagInfo.addTagVariableInfo(tagVariableInfo)) {
                  VariableMetaData variableMetaData = (VariableMetaData)var15.next();
                  tagVariableInfo = new TagVariableInfo();
                  tagVariableInfo.setNameGiven(variableMetaData.getNameGiven());
                  tagVariableInfo.setNameFromAttribute(variableMetaData.getNameFromAttribute());
                  tagVariableInfo.setClassName(variableMetaData.getVariableClass());
                  tagVariableInfo.setDeclare(variableMetaData.getDeclare());
                  if (variableMetaData.getScope() != null) {
                     tagVariableInfo.setScope(variableMetaData.getScope().toString());
                  }
               }
            }

            TagAttributeInfo ari;
            if (tagMetaData.getAttributes() != null) {
               for(var15 = tagMetaData.getAttributes().iterator(); var15.hasNext(); tagInfo.addTagAttributeInfo(ari)) {
                  AttributeMetaData attributeMetaData = (AttributeMetaData)var15.next();
                  ari = new TagAttributeInfo();
                  ari.setName(attributeMetaData.getName());
                  ari.setType(attributeMetaData.getType());
                  ari.setReqTime(attributeMetaData.getRtexprvalue());
                  ari.setRequired(attributeMetaData.getRequired());
                  ari.setFragment(attributeMetaData.getFragment());
                  if (attributeMetaData.getDeferredValue() != null) {
                     ari.setDeferredValue("true");
                     ari.setExpectedTypeName(attributeMetaData.getDeferredValue().getType());
                  } else {
                     ari.setDeferredValue("false");
                  }

                  if (attributeMetaData.getDeferredMethod() != null) {
                     ari.setDeferredMethod("true");
                     ari.setMethodSignature(attributeMetaData.getDeferredMethod().getMethodSignature());
                  } else {
                     ari.setDeferredMethod("false");
                  }
               }
            }
         }
      }

      if (tldMetaData.getTagFiles() != null) {
         var8 = tldMetaData.getTagFiles().iterator();

         while(var8.hasNext()) {
            TagFileMetaData tagFileMetaData = (TagFileMetaData)var8.next();
            TagFileInfo tfi = new TagFileInfo();
            tfi.setName(tagFileMetaData.getName());
            tfi.setPath(tagFileMetaData.getPath());
            tagLibraryInfo.addTagFileInfo(tfi);
         }
      }

      if (tldMetaData.getFunctions() != null) {
         var8 = tldMetaData.getFunctions().iterator();

         while(var8.hasNext()) {
            FunctionMetaData functionMetaData = (FunctionMetaData)var8.next();
            FunctionInfo fi = new FunctionInfo();
            fi.setName(functionMetaData.getName());
            fi.setFunctionClass(functionMetaData.getFunctionClass());
            fi.setFunctionSignature(functionMetaData.getFunctionSignature());
            tagLibraryInfo.addFunctionInfo(fi);
         }
      }

      return tagLibraryInfo;
   }
}
