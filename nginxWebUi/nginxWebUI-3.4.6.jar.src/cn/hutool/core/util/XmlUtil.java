/*      */ package cn.hutool.core.util;
/*      */ 
/*      */ import cn.hutool.core.bean.BeanUtil;
/*      */ import cn.hutool.core.collection.CollUtil;
/*      */ import cn.hutool.core.exceptions.UtilException;
/*      */ import cn.hutool.core.io.FileUtil;
/*      */ import cn.hutool.core.io.IORuntimeException;
/*      */ import cn.hutool.core.io.IoUtil;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.map.BiMap;
/*      */ import cn.hutool.core.map.MapUtil;
/*      */ import java.beans.XMLDecoder;
/*      */ import java.beans.XMLEncoder;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringWriter;
/*      */ import java.io.Writer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import javax.xml.namespace.NamespaceContext;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.parsers.DocumentBuilder;
/*      */ import javax.xml.parsers.DocumentBuilderFactory;
/*      */ import javax.xml.parsers.ParserConfigurationException;
/*      */ import javax.xml.parsers.SAXParser;
/*      */ import javax.xml.parsers.SAXParserFactory;
/*      */ import javax.xml.transform.Result;
/*      */ import javax.xml.transform.Source;
/*      */ import javax.xml.transform.Transformer;
/*      */ import javax.xml.transform.TransformerFactory;
/*      */ import javax.xml.transform.dom.DOMSource;
/*      */ import javax.xml.transform.stream.StreamResult;
/*      */ import javax.xml.xpath.XPath;
/*      */ import javax.xml.xpath.XPathConstants;
/*      */ import javax.xml.xpath.XPathExpressionException;
/*      */ import javax.xml.xpath.XPathFactory;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ import org.xml.sax.ContentHandler;
/*      */ import org.xml.sax.InputSource;
/*      */ import org.xml.sax.XMLReader;
/*      */ import org.xml.sax.helpers.DefaultHandler;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class XmlUtil
/*      */ {
/*      */   public static final String NBSP = "&nbsp;";
/*      */   public static final String AMP = "&amp;";
/*      */   public static final String QUOTE = "&quot;";
/*      */   public static final String APOS = "&apos;";
/*      */   public static final String LT = "&lt;";
/*      */   public static final String GT = "&gt;";
/*      */   public static final String INVALID_REGEX = "[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]";
/*      */   public static final String COMMENT_REGEX = "(?s)<!--.+?-->";
/*      */   public static final int INDENT_DEFAULT = 2;
/*  115 */   private static String defaultDocumentBuilderFactory = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean namespaceAware = true;
/*      */ 
/*      */ 
/*      */   
/*      */   private static SAXParserFactory factory;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static synchronized void disableDefaultDocumentBuilderFactory() {
/*  130 */     defaultDocumentBuilderFactory = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static synchronized void setNamespaceAware(boolean isNamespaceAware) {
/*  140 */     namespaceAware = isNamespaceAware;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document readXML(File file) {
/*  152 */     Assert.notNull(file, "Xml file is null !", new Object[0]);
/*  153 */     if (false == file.exists()) {
/*  154 */       throw new UtilException("File [{}] not a exist!", new Object[] { file.getAbsolutePath() });
/*      */     }
/*  156 */     if (false == file.isFile()) {
/*  157 */       throw new UtilException("[{}] not a file!", new Object[] { file.getAbsolutePath() });
/*      */     }
/*      */     
/*      */     try {
/*  161 */       file = file.getCanonicalFile();
/*  162 */     } catch (IOException iOException) {}
/*      */ 
/*      */ 
/*      */     
/*  166 */     BufferedInputStream in = null;
/*      */     try {
/*  168 */       in = FileUtil.getInputStream(file);
/*  169 */       return readXML(in);
/*      */     } finally {
/*  171 */       IoUtil.close(in);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document readXML(String pathOrContent) {
/*  185 */     if (StrUtil.startWith(pathOrContent, '<')) {
/*  186 */       return parseXml(pathOrContent);
/*      */     }
/*  188 */     return readXML(FileUtil.file(pathOrContent));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document readXML(InputStream inputStream) throws UtilException {
/*  201 */     return readXML(new InputSource(inputStream));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document readXML(Reader reader) throws UtilException {
/*  213 */     return readXML(new InputSource(reader));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document readXML(InputSource source) {
/*  225 */     DocumentBuilder builder = createDocumentBuilder();
/*      */     try {
/*  227 */       return builder.parse(source);
/*  228 */     } catch (Exception e) {
/*  229 */       throw new UtilException(e, "Parse XML from stream error!", new Object[0]);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void readBySax(File file, ContentHandler contentHandler) {
/*  242 */     InputStream in = null;
/*      */     try {
/*  244 */       in = FileUtil.getInputStream(file);
/*  245 */       readBySax(new InputSource(in), contentHandler);
/*      */     } finally {
/*  247 */       IoUtil.close(in);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void readBySax(Reader reader, ContentHandler contentHandler) {
/*      */     try {
/*  261 */       readBySax(new InputSource(reader), contentHandler);
/*      */     } finally {
/*  263 */       IoUtil.close(reader);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void readBySax(InputStream source, ContentHandler contentHandler) {
/*      */     try {
/*  277 */       readBySax(new InputSource(source), contentHandler);
/*      */     } finally {
/*  279 */       IoUtil.close(source);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void readBySax(InputSource source, ContentHandler contentHandler) {
/*  293 */     if (null == factory) {
/*  294 */       factory = SAXParserFactory.newInstance();
/*  295 */       factory.setValidating(false);
/*  296 */       factory.setNamespaceAware(namespaceAware);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  302 */       SAXParser parse = factory.newSAXParser();
/*  303 */       if (contentHandler instanceof DefaultHandler) {
/*  304 */         parse.parse(source, (DefaultHandler)contentHandler);
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/*  309 */       XMLReader reader = parse.getXMLReader();
/*  310 */       reader.setContentHandler(contentHandler);
/*  311 */       reader.parse(source);
/*  312 */     } catch (ParserConfigurationException|org.xml.sax.SAXException e) {
/*  313 */       throw new UtilException(e);
/*  314 */     } catch (IOException e) {
/*  315 */       throw new IORuntimeException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document parseXml(String xmlStr) {
/*  326 */     if (StrUtil.isBlank(xmlStr)) {
/*  327 */       throw new IllegalArgumentException("XML content string is empty !");
/*      */     }
/*  329 */     xmlStr = cleanInvalid(xmlStr);
/*  330 */     return readXML(StrUtil.getReader(xmlStr));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T readObjectFromXml(File source) {
/*  341 */     return readObjectFromXml(new InputSource(FileUtil.getInputStream(source)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T readObjectFromXml(String xmlStr) {
/*  353 */     return readObjectFromXml(new InputSource(StrUtil.getReader(xmlStr)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T readObjectFromXml(InputSource source) {
/*      */     Object result;
/*  367 */     XMLDecoder xmldec = null;
/*      */     try {
/*  369 */       xmldec = new XMLDecoder(source);
/*  370 */       result = xmldec.readObject();
/*      */     } finally {
/*  372 */       IoUtil.close(xmldec);
/*      */     } 
/*  374 */     return (T)result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toStr(Node doc) {
/*  389 */     return toStr(doc, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toStr(Document doc) {
/*  401 */     return toStr(doc);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toStr(Node doc, boolean isPretty) {
/*  414 */     return toStr(doc, "UTF-8", isPretty);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toStr(Document doc, boolean isPretty) {
/*  427 */     return toStr(doc, isPretty);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toStr(Node doc, String charset, boolean isPretty) {
/*  441 */     return toStr(doc, charset, isPretty, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toStr(Document doc, String charset, boolean isPretty) {
/*  455 */     return toStr(doc, charset, isPretty);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toStr(Node doc, String charset, boolean isPretty, boolean omitXmlDeclaration) {
/*  470 */     StringWriter writer = StrUtil.getWriter();
/*      */     try {
/*  472 */       write(doc, writer, charset, isPretty ? 2 : 0, omitXmlDeclaration);
/*  473 */     } catch (Exception e) {
/*  474 */       throw new UtilException(e, "Trans xml document to string error!", new Object[0]);
/*      */     } 
/*  476 */     return writer.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String format(Document doc) {
/*  487 */     return toStr(doc, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String format(String xmlStr) {
/*  498 */     return format(parseXml(xmlStr));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void toFile(Document doc, String absolutePath) {
/*  509 */     toFile(doc, absolutePath, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void toFile(Document doc, String path, String charsetName) {
/*  520 */     if (StrUtil.isBlank(charsetName)) {
/*  521 */       charsetName = doc.getXmlEncoding();
/*      */     }
/*  523 */     if (StrUtil.isBlank(charsetName)) {
/*  524 */       charsetName = "UTF-8";
/*      */     }
/*      */     
/*  527 */     BufferedWriter writer = null;
/*      */     try {
/*  529 */       writer = FileUtil.getWriter(path, CharsetUtil.charset(charsetName), false);
/*  530 */       write(doc, writer, charsetName, 2);
/*      */     } finally {
/*  532 */       IoUtil.close(writer);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void write(Node node, Writer writer, String charset, int indent) {
/*  546 */     transform(new DOMSource(node), new StreamResult(writer), charset, indent);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void write(Node node, Writer writer, String charset, int indent, boolean omitXmlDeclaration) {
/*  560 */     transform(new DOMSource(node), new StreamResult(writer), charset, indent, omitXmlDeclaration);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void write(Node node, OutputStream out, String charset, int indent) {
/*  573 */     transform(new DOMSource(node), new StreamResult(out), charset, indent);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void write(Node node, OutputStream out, String charset, int indent, boolean omitXmlDeclaration) {
/*  587 */     transform(new DOMSource(node), new StreamResult(out), charset, indent, omitXmlDeclaration);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void transform(Source source, Result result, String charset, int indent) {
/*  601 */     transform(source, result, charset, indent, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void transform(Source source, Result result, String charset, int indent, boolean omitXmlDeclaration) {
/*  616 */     TransformerFactory factory = TransformerFactory.newInstance();
/*      */     try {
/*  618 */       Transformer xformer = factory.newTransformer();
/*  619 */       if (indent > 0) {
/*  620 */         xformer.setOutputProperty("indent", "yes");
/*      */         
/*  622 */         xformer.setOutputProperty("doctype-public", "yes");
/*  623 */         xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
/*      */       } 
/*  625 */       if (StrUtil.isNotBlank(charset)) {
/*  626 */         xformer.setOutputProperty("encoding", charset);
/*      */       }
/*  628 */       if (omitXmlDeclaration) {
/*  629 */         xformer.setOutputProperty("omit-xml-declaration", "yes");
/*      */       }
/*  631 */       xformer.transform(source, result);
/*  632 */     } catch (Exception e) {
/*  633 */       throw new UtilException(e, "Trans xml document to string error!", new Object[0]);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document createXml() {
/*  647 */     return createDocumentBuilder().newDocument();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DocumentBuilder createDocumentBuilder() {
/*      */     DocumentBuilder builder;
/*      */     try {
/*  659 */       builder = createDocumentBuilderFactory().newDocumentBuilder();
/*  660 */     } catch (Exception e) {
/*  661 */       throw new UtilException(e, "Create xml document error!", new Object[0]);
/*      */     } 
/*  663 */     return builder;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DocumentBuilderFactory createDocumentBuilderFactory() {
/*      */     DocumentBuilderFactory factory;
/*  677 */     if (StrUtil.isNotEmpty(defaultDocumentBuilderFactory)) {
/*  678 */       factory = DocumentBuilderFactory.newInstance(defaultDocumentBuilderFactory, null);
/*      */     } else {
/*  680 */       factory = DocumentBuilderFactory.newInstance();
/*      */     } 
/*      */     
/*  683 */     factory.setNamespaceAware(namespaceAware);
/*  684 */     return disableXXE(factory);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document createXml(String rootElementName) {
/*  695 */     return createXml(rootElementName, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document createXml(String rootElementName, String namespace) {
/*  708 */     Document doc = createXml();
/*  709 */     doc.appendChild((null == namespace) ? doc.createElement(rootElementName) : doc.createElementNS(namespace, rootElementName));
/*  710 */     return doc;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Element getRootElement(Document doc) {
/*  724 */     return (null == doc) ? null : doc.getDocumentElement();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document getOwnerDocument(Node node) {
/*  735 */     return (node instanceof Document) ? (Document)node : node.getOwnerDocument();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String cleanInvalid(String xmlContent) {
/*  745 */     if (xmlContent == null) {
/*  746 */       return null;
/*      */     }
/*  748 */     return xmlContent.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String cleanComment(String xmlContent) {
/*  759 */     if (xmlContent == null) {
/*  760 */       return null;
/*      */     }
/*  762 */     return xmlContent.replaceAll("(?s)<!--.+?-->", "");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<Element> getElements(Element element, String tagName) {
/*  773 */     NodeList nodeList = StrUtil.isBlank(tagName) ? element.getChildNodes() : element.getElementsByTagName(tagName);
/*  774 */     return transElements(element, nodeList);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Element getElement(Element element, String tagName) {
/*  785 */     NodeList nodeList = element.getElementsByTagName(tagName);
/*  786 */     int length = nodeList.getLength();
/*  787 */     if (length < 1) {
/*  788 */       return null;
/*      */     }
/*  790 */     for (int i = 0; i < length; i++) {
/*  791 */       Element childEle = (Element)nodeList.item(i);
/*  792 */       if (childEle == null || childEle.getParentNode() == element) {
/*  793 */         return childEle;
/*      */       }
/*      */     } 
/*  796 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String elementText(Element element, String tagName) {
/*  807 */     Element child = getElement(element, tagName);
/*  808 */     return (child == null) ? null : child.getTextContent();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String elementText(Element element, String tagName, String defaultValue) {
/*  820 */     Element child = getElement(element, tagName);
/*  821 */     return (child == null) ? defaultValue : child.getTextContent();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<Element> transElements(NodeList nodeList) {
/*  831 */     return transElements(null, nodeList);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<Element> transElements(Element parentEle, NodeList nodeList) {
/*  843 */     int length = nodeList.getLength();
/*  844 */     ArrayList<Element> elements = new ArrayList<>(length);
/*      */ 
/*      */     
/*  847 */     for (int i = 0; i < length; i++) {
/*  848 */       Node node = nodeList.item(i);
/*  849 */       if (1 == node.getNodeType()) {
/*  850 */         Element element = (Element)nodeList.item(i);
/*  851 */         if (parentEle == null || element.getParentNode() == parentEle) {
/*  852 */           elements.add(element);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  857 */     return elements;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void writeObjectAsXml(File dest, Object bean) {
/*  868 */     XMLEncoder xmlenc = null;
/*      */     try {
/*  870 */       xmlenc = new XMLEncoder(FileUtil.getOutputStream(dest));
/*  871 */       xmlenc.writeObject(bean);
/*      */     } finally {
/*      */       
/*  874 */       IoUtil.close(xmlenc);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static XPath createXPath() {
/*  886 */     return XPathFactory.newInstance().newXPath();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Element getElementByXPath(String expression, Object source) {
/*  899 */     return (Element)getNodeByXPath(expression, source);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static NodeList getNodeListByXPath(String expression, Object source) {
/*  912 */     return (NodeList)getByXPath(expression, source, XPathConstants.NODESET);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Node getNodeByXPath(String expression, Object source) {
/*  925 */     return (Node)getByXPath(expression, source, XPathConstants.NODE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object getByXPath(String expression, Object source, QName returnType) {
/*  939 */     NamespaceContext nsContext = null;
/*  940 */     if (source instanceof Node) {
/*  941 */       nsContext = new UniversalNamespaceCache((Node)source, false);
/*      */     }
/*  943 */     return getByXPath(expression, source, returnType, nsContext);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object getByXPath(String expression, Object source, QName returnType, NamespaceContext nsContext) {
/*  960 */     XPath xPath = createXPath();
/*  961 */     if (null != nsContext) {
/*  962 */       xPath.setNamespaceContext(nsContext);
/*      */     }
/*      */     try {
/*  965 */       if (source instanceof InputSource) {
/*  966 */         return xPath.evaluate(expression, (InputSource)source, returnType);
/*      */       }
/*  968 */       return xPath.evaluate(expression, source, returnType);
/*      */     }
/*  970 */     catch (XPathExpressionException e) {
/*  971 */       throw new UtilException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String escape(String string) {
/*  990 */     return EscapeUtil.escapeHtml4(string);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String unescape(String string) {
/* 1002 */     return EscapeUtil.unescapeHtml4(string);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Map<String, Object> xmlToMap(String xmlStr) {
/* 1013 */     return xmlToMap(xmlStr, new HashMap<>());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T xmlToBean(Node node, Class<T> bean) {
/* 1027 */     Map<String, Object> map = xmlToMap(node);
/* 1028 */     if (null != map && map.size() == 1) {
/* 1029 */       String simpleName = bean.getSimpleName();
/* 1030 */       if (map.containsKey(simpleName))
/*      */       {
/* 1032 */         return (T)BeanUtil.toBean(map.get(simpleName), bean);
/*      */       }
/*      */     } 
/* 1035 */     return (T)BeanUtil.toBean(map, bean);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Map<String, Object> xmlToMap(Node node) {
/* 1046 */     return xmlToMap(node, new HashMap<>());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Map<String, Object> xmlToMap(String xmlStr, Map<String, Object> result) {
/* 1059 */     Document doc = parseXml(xmlStr);
/* 1060 */     Element root = getRootElement(doc);
/* 1061 */     root.normalize();
/*      */     
/* 1063 */     return xmlToMap(root, result);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Map<String, Object> xmlToMap(Node node, Map<String, Object> result) {
/* 1076 */     if (null == result) {
/* 1077 */       result = new HashMap<>();
/*      */     }
/* 1079 */     NodeList nodeList = node.getChildNodes();
/* 1080 */     int length = nodeList.getLength();
/*      */ 
/*      */     
/* 1083 */     for (int i = 0; i < length; i++) {
/* 1084 */       Node childNode = nodeList.item(i);
/* 1085 */       if (false != isElement(childNode)) {
/*      */         Object newValue;
/*      */ 
/*      */         
/* 1089 */         Element childEle = (Element)childNode;
/* 1090 */         Object value = result.get(childEle.getNodeName());
/*      */         
/* 1092 */         if (childEle.hasChildNodes()) {
/*      */           
/* 1094 */           Map<String, Object> map = xmlToMap(childEle);
/* 1095 */           if (MapUtil.isNotEmpty(map)) {
/* 1096 */             newValue = map;
/*      */           } else {
/* 1098 */             newValue = childEle.getTextContent();
/*      */           } 
/*      */         } else {
/* 1101 */           newValue = childEle.getTextContent();
/*      */         } 
/*      */ 
/*      */         
/* 1105 */         if (null != newValue)
/* 1106 */           if (null != value) {
/* 1107 */             if (value instanceof List) {
/* 1108 */               ((List<Object>)value).add(newValue);
/*      */             } else {
/* 1110 */               result.put(childEle.getNodeName(), CollUtil.newArrayList(new Object[] { value, newValue }));
/*      */             } 
/*      */           } else {
/* 1113 */             result.put(childEle.getNodeName(), newValue);
/*      */           }  
/*      */       } 
/*      */     } 
/* 1117 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String mapToXmlStr(Map<?, ?> data) {
/* 1129 */     return toStr(mapToXml(data, "xml"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String mapToXmlStr(Map<?, ?> data, boolean omitXmlDeclaration) {
/* 1142 */     return toStr(mapToXml(data, "xml"), "UTF-8", false, omitXmlDeclaration);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String mapToXmlStr(Map<?, ?> data, String rootName) {
/* 1155 */     return toStr(mapToXml(data, rootName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String mapToXmlStr(Map<?, ?> data, String rootName, String namespace) {
/* 1169 */     return toStr(mapToXml(data, rootName, namespace));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String mapToXmlStr(Map<?, ?> data, String rootName, String namespace, boolean omitXmlDeclaration) {
/* 1184 */     return toStr(mapToXml(data, rootName, namespace), "UTF-8", false, omitXmlDeclaration);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String mapToXmlStr(Map<?, ?> data, String rootName, String namespace, boolean isPretty, boolean omitXmlDeclaration) {
/* 1200 */     return toStr(mapToXml(data, rootName, namespace), "UTF-8", isPretty, omitXmlDeclaration);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String mapToXmlStr(Map<?, ?> data, String rootName, String namespace, String charset, boolean isPretty, boolean omitXmlDeclaration) {
/* 1217 */     return toStr(mapToXml(data, rootName, namespace), charset, isPretty, omitXmlDeclaration);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document mapToXml(Map<?, ?> data, String rootName) {
/* 1230 */     return mapToXml(data, rootName, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document mapToXml(Map<?, ?> data, String rootName, String namespace) {
/* 1244 */     Document doc = createXml();
/* 1245 */     Element root = appendChild(doc, rootName, namespace);
/*      */     
/* 1247 */     appendMap(doc, root, data);
/* 1248 */     return doc;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document beanToXml(Object bean) {
/* 1260 */     return beanToXml(bean, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document beanToXml(Object bean, String namespace) {
/* 1273 */     return beanToXml(bean, namespace, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Document beanToXml(Object bean, String namespace, boolean ignoreNull) {
/* 1287 */     if (null == bean) {
/* 1288 */       return null;
/*      */     }
/* 1290 */     return mapToXml(BeanUtil.beanToMap(bean, false, ignoreNull), bean
/* 1291 */         .getClass().getSimpleName(), namespace);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isElement(Node node) {
/* 1302 */     return (null != node && 1 == node.getNodeType());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Element appendChild(Node node, String tagName) {
/* 1314 */     return appendChild(node, tagName, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Element appendChild(Node node, String tagName, String namespace) {
/* 1327 */     Document doc = getOwnerDocument(node);
/* 1328 */     Element child = (null == namespace) ? doc.createElement(tagName) : doc.createElementNS(namespace, tagName);
/* 1329 */     node.appendChild(child);
/* 1330 */     return child;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Node appendText(Node node, CharSequence text) {
/* 1342 */     return appendText(getOwnerDocument(node), node, text);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void append(Node node, Object data) {
/* 1353 */     append(getOwnerDocument(node), node, data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void append(Document doc, Node node, Object data) {
/* 1366 */     if (data instanceof Map) {
/*      */       
/* 1368 */       appendMap(doc, node, (Map)data);
/* 1369 */     } else if (data instanceof Iterator) {
/*      */       
/* 1371 */       appendIterator(doc, node, (Iterator)data);
/* 1372 */     } else if (data instanceof Iterable) {
/*      */       
/* 1374 */       appendIterator(doc, node, ((Iterable)data).iterator());
/*      */     } else {
/* 1376 */       appendText(doc, node, data.toString());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void appendMap(Document doc, Node node, Map data) {
/* 1390 */     data.forEach((key, value) -> {
/*      */           if (null != key) {
/*      */             Element child = appendChild(node, key.toString());
/*      */             if (null != value) {
/*      */               append(doc, child, value);
/*      */             }
/*      */           } 
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void appendIterator(Document doc, Node node, Iterator data) {
/* 1409 */     Node parentNode = node.getParentNode();
/* 1410 */     boolean isFirst = true;
/*      */     
/* 1412 */     while (data.hasNext()) {
/* 1413 */       Object eleData = data.next();
/* 1414 */       if (isFirst) {
/* 1415 */         append(doc, node, eleData);
/* 1416 */         isFirst = false; continue;
/*      */       } 
/* 1418 */       Node cloneNode = node.cloneNode(false);
/* 1419 */       parentNode.appendChild(cloneNode);
/* 1420 */       append(doc, cloneNode, eleData);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Node appendText(Document doc, Node node, CharSequence text) {
/* 1435 */     return node.appendChild(doc.createTextNode(StrUtil.str(text)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static DocumentBuilderFactory disableXXE(DocumentBuilderFactory dbf) {
/*      */     try {
/* 1450 */       String feature = "http://apache.org/xml/features/disallow-doctype-decl";
/* 1451 */       dbf.setFeature(feature, true);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1456 */       feature = "http://xml.org/sax/features/external-general-entities";
/* 1457 */       dbf.setFeature(feature, false);
/*      */ 
/*      */ 
/*      */       
/* 1461 */       feature = "http://xml.org/sax/features/external-parameter-entities";
/* 1462 */       dbf.setFeature(feature, false);
/*      */       
/* 1464 */       feature = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
/* 1465 */       dbf.setFeature(feature, false);
/*      */       
/* 1467 */       dbf.setXIncludeAware(false);
/* 1468 */       dbf.setExpandEntityReferences(false);
/* 1469 */     } catch (ParserConfigurationException parserConfigurationException) {}
/*      */ 
/*      */     
/* 1472 */     return dbf;
/*      */   }
/*      */ 
/*      */   
/*      */   public static class UniversalNamespaceCache
/*      */     implements NamespaceContext
/*      */   {
/*      */     private static final String DEFAULT_NS = "DEFAULT";
/*      */     
/* 1481 */     private final BiMap<String, String> prefixUri = new BiMap(new HashMap<>());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public UniversalNamespaceCache(Node node, boolean toplevelOnly) {
/* 1491 */       examineNode(node.getFirstChild(), toplevelOnly);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void examineNode(Node node, boolean attributesOnly) {
/* 1501 */       NamedNodeMap attributes = node.getAttributes();
/*      */       
/* 1503 */       if (null != attributes) {
/* 1504 */         int length = attributes.getLength();
/* 1505 */         for (int i = 0; i < length; i++) {
/* 1506 */           Node attribute = attributes.item(i);
/* 1507 */           storeAttribute(attribute);
/*      */         } 
/*      */       } 
/*      */       
/* 1511 */       if (false == attributesOnly) {
/* 1512 */         NodeList childNodes = node.getChildNodes();
/*      */         
/* 1514 */         if (null != childNodes) {
/*      */           
/* 1516 */           int childLength = childNodes.getLength();
/* 1517 */           for (int i = 0; i < childLength; i++) {
/* 1518 */             Node item = childNodes.item(i);
/* 1519 */             if (item.getNodeType() == 1) {
/* 1520 */               examineNode(item, false);
/*      */             }
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void storeAttribute(Node attribute) {
/* 1533 */       if (null == attribute) {
/*      */         return;
/*      */       }
/*      */       
/* 1537 */       if ("http://www.w3.org/2000/xmlns/".equals(attribute.getNamespaceURI()))
/*      */       {
/* 1539 */         if ("xmlns".equals(attribute.getNodeName())) {
/* 1540 */           this.prefixUri.put("DEFAULT", attribute.getNodeValue());
/*      */         } else {
/*      */           
/* 1543 */           this.prefixUri.put(attribute.getLocalName(), attribute.getNodeValue());
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getNamespaceURI(String prefix) {
/* 1558 */       if (prefix == null || "".equals(prefix)) {
/* 1559 */         return (String)this.prefixUri.get("DEFAULT");
/*      */       }
/* 1561 */       return (String)this.prefixUri.get(prefix);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getPrefix(String namespaceURI) {
/* 1571 */       return (String)this.prefixUri.getInverse().get(namespaceURI);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Iterator<String> getPrefixes(String namespaceURI) {
/* 1577 */       return null;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\XmlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */