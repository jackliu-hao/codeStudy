/*     */ package com.google.zxing;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.DirectoryStream;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.w3c.dom.bootstrap.DOMImplementationRegistry;
/*     */ import org.w3c.dom.ls.DOMImplementationLS;
/*     */ import org.w3c.dom.ls.LSSerializer;
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
/*     */ public final class HtmlAssetTranslator
/*     */ {
/*  67 */   private static final Pattern COMMA = Pattern.compile(",");
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) throws IOException {
/*  72 */     if (args.length < 3) {
/*  73 */       System.err.println("Usage: HtmlAssetTranslator android/assets/ (all|lang1[,lang2 ...]) (all|file1.html[ file2.html ...])");
/*     */       
/*     */       return;
/*     */     } 
/*  77 */     Path assetsDir = Paths.get(args[0], new String[0]);
/*  78 */     Collection<String> languagesToTranslate = parseLanguagesToTranslate(assetsDir, args[1]);
/*  79 */     List<String> restOfArgs = Arrays.<String>asList(args).subList(2, args.length);
/*  80 */     Collection<String> fileNamesToTranslate = parseFileNamesToTranslate(assetsDir, restOfArgs);
/*  81 */     for (String language : languagesToTranslate) {
/*  82 */       translateOneLanguage(assetsDir, language, fileNamesToTranslate);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static Collection<String> parseLanguagesToTranslate(Path assetsDir, CharSequence languageArg) throws IOException {
/*  88 */     if ("all".equals(languageArg)) {
/*  89 */       Collection<String> languages = new ArrayList<>();
/*  90 */       DirectoryStream.Filter<Path> fileFilter = new DirectoryStream.Filter<Path>()
/*     */         {
/*     */           public boolean accept(Path entry) {
/*  93 */             String fileName = entry.getFileName().toString();
/*  94 */             return (Files.isDirectory(entry, new java.nio.file.LinkOption[0]) && !Files.isSymbolicLink(entry) && fileName
/*  95 */               .startsWith("html-") && !"html-en".equals(fileName));
/*     */           }
/*     */         };
/*  98 */       try (DirectoryStream<Path> dirs = Files.newDirectoryStream(assetsDir, fileFilter)) {
/*  99 */         for (Path languageDir : dirs) {
/* 100 */           languages.add(languageDir.getFileName().toString().substring(5));
/*     */         }
/*     */       } 
/* 103 */       return languages;
/*     */     } 
/* 105 */     return Arrays.asList(COMMA.split(languageArg));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Collection<String> parseFileNamesToTranslate(Path assetsDir, List<String> restOfArgs) throws IOException {
/* 111 */     if ("all".equals(restOfArgs.get(0))) {
/* 112 */       Collection<String> fileNamesToTranslate = new ArrayList<>();
/* 113 */       Path htmlEnAssetDir = assetsDir.resolve("html-en");
/* 114 */       try (DirectoryStream<Path> files = Files.newDirectoryStream(htmlEnAssetDir, "*.html")) {
/* 115 */         for (Path file : files) {
/* 116 */           fileNamesToTranslate.add(file.getFileName().toString());
/*     */         }
/*     */       } 
/* 119 */       return fileNamesToTranslate;
/*     */     } 
/* 121 */     return restOfArgs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void translateOneLanguage(Path assetsDir, String language, final Collection<String> filesToTranslate) throws IOException {
/* 128 */     Path targetHtmlDir = assetsDir.resolve("html-" + language);
/* 129 */     Files.createDirectories(targetHtmlDir, (FileAttribute<?>[])new FileAttribute[0]);
/* 130 */     Path englishHtmlDir = assetsDir.resolve("html-en");
/*     */ 
/*     */     
/* 133 */     String translationTextTranslated = StringsResourceTranslator.translateString("Translated by Google Translate.", language);
/*     */     
/* 135 */     DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>()
/*     */       {
/*     */         public boolean accept(Path entry) {
/* 138 */           String name = entry.getFileName().toString();
/* 139 */           return (name.endsWith(".html") && (filesToTranslate.isEmpty() || filesToTranslate.contains(name)));
/*     */         }
/*     */       };
/* 142 */     try (DirectoryStream<Path> files = Files.newDirectoryStream(englishHtmlDir, filter)) {
/* 143 */       for (Path sourceFile : files) {
/* 144 */         translateOneFile(language, targetHtmlDir, sourceFile, translationTextTranslated);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void translateOneFile(String language, Path targetHtmlDir, Path sourceFile, String translationTextTranslated) throws IOException {
/*     */     Document document;
/*     */     DOMImplementationRegistry registry;
/* 154 */     Path destFile = targetHtmlDir.resolve(sourceFile.getFileName());
/*     */     
/* 156 */     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
/*     */     
/*     */     try {
/* 159 */       DocumentBuilder builder = factory.newDocumentBuilder();
/* 160 */       document = builder.parse(sourceFile.toFile());
/* 161 */     } catch (ParserConfigurationException pce) {
/* 162 */       throw new IllegalStateException(pce);
/* 163 */     } catch (SAXException sae) {
/* 164 */       throw new IOException(sae);
/*     */     } 
/*     */     
/* 167 */     Element rootElement = document.getDocumentElement();
/* 168 */     rootElement.normalize();
/*     */     
/* 170 */     Queue<Node> nodes = new LinkedList<>();
/* 171 */     nodes.add(rootElement);
/*     */     
/* 173 */     while (!nodes.isEmpty()) {
/* 174 */       Node node = nodes.poll();
/* 175 */       if (shouldTranslate(node)) {
/* 176 */         NodeList children = node.getChildNodes();
/* 177 */         for (int i = 0; i < children.getLength(); i++) {
/* 178 */           nodes.add(children.item(i));
/*     */         }
/*     */       } 
/* 181 */       if (node.getNodeType() == 3) {
/* 182 */         String text = node.getTextContent();
/* 183 */         if (!text.trim().isEmpty()) {
/* 184 */           text = StringsResourceTranslator.translateString(text, language);
/* 185 */           node.setTextContent(' ' + text + ' ');
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 190 */     Node translateText = document.createTextNode(translationTextTranslated);
/* 191 */     Node paragraph = document.createElement("p");
/* 192 */     paragraph.appendChild(translateText);
/* 193 */     Node body = rootElement.getElementsByTagName("body").item(0);
/* 194 */     body.appendChild(paragraph);
/*     */ 
/*     */     
/*     */     try {
/* 198 */       registry = DOMImplementationRegistry.newInstance();
/* 199 */     } catch (ClassNotFoundException|InstantiationException|IllegalAccessException e) {
/* 200 */       throw new IllegalStateException(e);
/*     */     } 
/*     */     
/* 203 */     DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
/* 204 */     LSSerializer writer = impl.createLSSerializer();
/* 205 */     String fileAsString = writer.writeToString(document);
/*     */     
/* 207 */     fileAsString = fileAsString.replaceAll("<\\?xml[^>]+>", "<!DOCTYPE HTML>");
/* 208 */     Files.write(destFile, Collections.singleton(fileAsString), StandardCharsets.UTF_8, new java.nio.file.OpenOption[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean shouldTranslate(Node node) {
/* 213 */     NamedNodeMap attributes = node.getAttributes();
/* 214 */     if (attributes != null) {
/* 215 */       Node classAttribute = attributes.getNamedItem("class");
/* 216 */       if (classAttribute != null) {
/* 217 */         String str = classAttribute.getTextContent();
/* 218 */         if (str != null && str.contains("notranslate")) {
/* 219 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/* 223 */     String nodeName = node.getNodeName();
/* 224 */     if ("script".equalsIgnoreCase(nodeName)) {
/* 225 */       return false;
/*     */     }
/*     */     
/* 228 */     String textContent = node.getTextContent();
/* 229 */     if (textContent != null) {
/* 230 */       for (int i = 0; i < textContent.length(); i++) {
/* 231 */         if (Character.isLetter(textContent.charAt(i))) {
/* 232 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/* 236 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\HtmlAssetTranslator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */