/*      */ package org.apache.maven.model.io.xpp3;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.text.DateFormat;
/*      */ import java.text.ParseException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Set;
/*      */ import org.apache.maven.model.Activation;
/*      */ import org.apache.maven.model.ActivationFile;
/*      */ import org.apache.maven.model.ActivationOS;
/*      */ import org.apache.maven.model.ActivationProperty;
/*      */ import org.apache.maven.model.Build;
/*      */ import org.apache.maven.model.BuildBase;
/*      */ import org.apache.maven.model.CiManagement;
/*      */ import org.apache.maven.model.ConfigurationContainer;
/*      */ import org.apache.maven.model.Contributor;
/*      */ import org.apache.maven.model.Dependency;
/*      */ import org.apache.maven.model.DependencyManagement;
/*      */ import org.apache.maven.model.DeploymentRepository;
/*      */ import org.apache.maven.model.Developer;
/*      */ import org.apache.maven.model.DistributionManagement;
/*      */ import org.apache.maven.model.Exclusion;
/*      */ import org.apache.maven.model.Extension;
/*      */ import org.apache.maven.model.FileSet;
/*      */ import org.apache.maven.model.InputLocation;
/*      */ import org.apache.maven.model.InputSource;
/*      */ import org.apache.maven.model.IssueManagement;
/*      */ import org.apache.maven.model.License;
/*      */ import org.apache.maven.model.MailingList;
/*      */ import org.apache.maven.model.Model;
/*      */ import org.apache.maven.model.ModelBase;
/*      */ import org.apache.maven.model.Notifier;
/*      */ import org.apache.maven.model.Organization;
/*      */ import org.apache.maven.model.Parent;
/*      */ import org.apache.maven.model.PatternSet;
/*      */ import org.apache.maven.model.Plugin;
/*      */ import org.apache.maven.model.PluginConfiguration;
/*      */ import org.apache.maven.model.PluginContainer;
/*      */ import org.apache.maven.model.PluginExecution;
/*      */ import org.apache.maven.model.PluginManagement;
/*      */ import org.apache.maven.model.Prerequisites;
/*      */ import org.apache.maven.model.Profile;
/*      */ import org.apache.maven.model.Relocation;
/*      */ import org.apache.maven.model.ReportPlugin;
/*      */ import org.apache.maven.model.ReportSet;
/*      */ import org.apache.maven.model.Reporting;
/*      */ import org.apache.maven.model.Repository;
/*      */ import org.apache.maven.model.RepositoryBase;
/*      */ import org.apache.maven.model.RepositoryPolicy;
/*      */ import org.apache.maven.model.Resource;
/*      */ import org.apache.maven.model.Scm;
/*      */ import org.apache.maven.model.Site;
/*      */ import org.codehaus.plexus.util.ReaderFactory;
/*      */ import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
/*      */ import org.codehaus.plexus.util.xml.pull.MXParser;
/*      */ import org.codehaus.plexus.util.xml.pull.XmlPullParser;
/*      */ import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
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
/*      */ public class MavenXpp3ReaderEx
/*      */ {
/*      */   private boolean addDefaultEntities = true;
/*      */   
/*      */   private boolean checkFieldWithDuplicate(XmlPullParser parser, String tagName, String alias, Set<String> parsed) throws XmlPullParserException {
/*  113 */     if (!parser.getName().equals(tagName) && !parser.getName().equals(alias))
/*      */     {
/*  115 */       return false;
/*      */     }
/*  117 */     if (!parsed.add(tagName))
/*      */     {
/*  119 */       throw new XmlPullParserException("Duplicated tag: '" + tagName + "'", parser, null);
/*      */     }
/*  121 */     return true;
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
/*      */   private void checkUnknownAttribute(XmlPullParser parser, String attribute, String tagName, boolean strict) throws XmlPullParserException, IOException {
/*  138 */     if (strict)
/*      */     {
/*  140 */       throw new XmlPullParserException("Unknown attribute '" + attribute + "' for tag '" + tagName + "'", parser, null);
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
/*      */   private void checkUnknownElement(XmlPullParser parser, boolean strict) throws XmlPullParserException, IOException {
/*  155 */     if (strict)
/*      */     {
/*  157 */       throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
/*      */     }
/*      */     
/*  160 */     for (int unrecognizedTagCount = 1; unrecognizedTagCount > 0; ) {
/*      */       
/*  162 */       int eventType = parser.next();
/*  163 */       if (eventType == 2) {
/*      */         
/*  165 */         unrecognizedTagCount++; continue;
/*      */       } 
/*  167 */       if (eventType == 3)
/*      */       {
/*  169 */         unrecognizedTagCount--;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAddDefaultEntities() {
/*  181 */     return this.addDefaultEntities;
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
/*      */   private boolean getBooleanValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
/*  196 */     return getBooleanValue(s, attribute, parser, null);
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
/*      */   private boolean getBooleanValue(String s, String attribute, XmlPullParser parser, String defaultValue) throws XmlPullParserException {
/*  212 */     if (s != null && s.length() != 0)
/*      */     {
/*  214 */       return Boolean.valueOf(s).booleanValue();
/*      */     }
/*  216 */     if (defaultValue != null)
/*      */     {
/*  218 */       return Boolean.valueOf(defaultValue).booleanValue();
/*      */     }
/*  220 */     return false;
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
/*      */   private byte getByteValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
/*  236 */     if (s != null) {
/*      */       
/*      */       try {
/*      */         
/*  240 */         return Byte.valueOf(s).byteValue();
/*      */       }
/*  242 */       catch (NumberFormatException nfe) {
/*      */         
/*  244 */         if (strict)
/*      */         {
/*  246 */           throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a byte", parser, nfe);
/*      */         }
/*      */       } 
/*      */     }
/*  250 */     return 0;
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
/*      */   private char getCharacterValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
/*  265 */     if (s != null)
/*      */     {
/*  267 */       return s.charAt(0);
/*      */     }
/*  269 */     return Character.MIN_VALUE;
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
/*      */   private Date getDateValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
/*  284 */     return getDateValue(s, attribute, null, parser);
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
/*      */   private Date getDateValue(String s, String attribute, String dateFormat, XmlPullParser parser) throws XmlPullParserException {
/*  300 */     if (s != null) {
/*      */       
/*  302 */       String effectiveDateFormat = dateFormat;
/*  303 */       if (dateFormat == null)
/*      */       {
/*  305 */         effectiveDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
/*      */       }
/*  307 */       if ("long".equals(effectiveDateFormat)) {
/*      */         
/*      */         try {
/*      */           
/*  311 */           return new Date(Long.parseLong(s));
/*      */         }
/*  313 */         catch (NumberFormatException e) {
/*      */           
/*  315 */           throw new XmlPullParserException(e.getMessage(), parser, e);
/*      */         } 
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/*  322 */         DateFormat dateParser = new SimpleDateFormat(effectiveDateFormat, Locale.US);
/*  323 */         return dateParser.parse(s);
/*      */       }
/*  325 */       catch (ParseException e) {
/*      */         
/*  327 */         throw new XmlPullParserException(e.getMessage(), parser, e);
/*      */       } 
/*      */     } 
/*      */     
/*  331 */     return null;
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
/*      */   private double getDoubleValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
/*  347 */     if (s != null) {
/*      */       
/*      */       try {
/*      */         
/*  351 */         return Double.valueOf(s).doubleValue();
/*      */       }
/*  353 */       catch (NumberFormatException nfe) {
/*      */         
/*  355 */         if (strict)
/*      */         {
/*  357 */           throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a floating point number", parser, nfe);
/*      */         }
/*      */       } 
/*      */     }
/*  361 */     return 0.0D;
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
/*      */   private float getFloatValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
/*  377 */     if (s != null) {
/*      */       
/*      */       try {
/*      */         
/*  381 */         return Float.valueOf(s).floatValue();
/*      */       }
/*  383 */       catch (NumberFormatException nfe) {
/*      */         
/*  385 */         if (strict)
/*      */         {
/*  387 */           throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a floating point number", parser, nfe);
/*      */         }
/*      */       } 
/*      */     }
/*  391 */     return 0.0F;
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
/*      */   private int getIntegerValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
/*  407 */     if (s != null) {
/*      */       
/*      */       try {
/*      */         
/*  411 */         return Integer.valueOf(s).intValue();
/*      */       }
/*  413 */       catch (NumberFormatException nfe) {
/*      */         
/*  415 */         if (strict)
/*      */         {
/*  417 */           throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be an integer", parser, nfe);
/*      */         }
/*      */       } 
/*      */     }
/*  421 */     return 0;
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
/*      */   private long getLongValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
/*  437 */     if (s != null) {
/*      */       
/*      */       try {
/*      */         
/*  441 */         return Long.valueOf(s).longValue();
/*      */       }
/*  443 */       catch (NumberFormatException nfe) {
/*      */         
/*  445 */         if (strict)
/*      */         {
/*  447 */           throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a long integer", parser, nfe);
/*      */         }
/*      */       } 
/*      */     }
/*  451 */     return 0L;
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
/*      */   private String getRequiredAttributeValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
/*  467 */     if (s == null)
/*      */     {
/*  469 */       if (strict)
/*      */       {
/*  471 */         throw new XmlPullParserException("Missing required value for attribute '" + attribute + "'", parser, null);
/*      */       }
/*      */     }
/*  474 */     return s;
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
/*      */   private short getShortValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
/*  490 */     if (s != null) {
/*      */       
/*      */       try {
/*      */         
/*  494 */         return Short.valueOf(s).shortValue();
/*      */       }
/*  496 */       catch (NumberFormatException nfe) {
/*      */         
/*  498 */         if (strict)
/*      */         {
/*  500 */           throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a short integer", parser, nfe);
/*      */         }
/*      */       } 
/*      */     }
/*  504 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String getTrimmedValue(String s) {
/*  515 */     if (s != null)
/*      */     {
/*  517 */       s = s.trim();
/*      */     }
/*  519 */     return s;
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
/*      */   private void initParser(XmlPullParser parser) throws XmlPullParserException {
/*  531 */     if (this.addDefaultEntities) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  537 */       parser.defineEntityReplacementText("nbsp", " ");
/*  538 */       parser.defineEntityReplacementText("iexcl", "¡");
/*  539 */       parser.defineEntityReplacementText("cent", "¢");
/*  540 */       parser.defineEntityReplacementText("pound", "£");
/*  541 */       parser.defineEntityReplacementText("curren", "¤");
/*  542 */       parser.defineEntityReplacementText("yen", "¥");
/*  543 */       parser.defineEntityReplacementText("brvbar", "¦");
/*  544 */       parser.defineEntityReplacementText("sect", "§");
/*  545 */       parser.defineEntityReplacementText("uml", "¨");
/*  546 */       parser.defineEntityReplacementText("copy", "©");
/*  547 */       parser.defineEntityReplacementText("ordf", "ª");
/*  548 */       parser.defineEntityReplacementText("laquo", "«");
/*  549 */       parser.defineEntityReplacementText("not", "¬");
/*  550 */       parser.defineEntityReplacementText("shy", "­");
/*  551 */       parser.defineEntityReplacementText("reg", "®");
/*  552 */       parser.defineEntityReplacementText("macr", "¯");
/*  553 */       parser.defineEntityReplacementText("deg", "°");
/*  554 */       parser.defineEntityReplacementText("plusmn", "±");
/*  555 */       parser.defineEntityReplacementText("sup2", "²");
/*  556 */       parser.defineEntityReplacementText("sup3", "³");
/*  557 */       parser.defineEntityReplacementText("acute", "´");
/*  558 */       parser.defineEntityReplacementText("micro", "µ");
/*  559 */       parser.defineEntityReplacementText("para", "¶");
/*  560 */       parser.defineEntityReplacementText("middot", "·");
/*  561 */       parser.defineEntityReplacementText("cedil", "¸");
/*  562 */       parser.defineEntityReplacementText("sup1", "¹");
/*  563 */       parser.defineEntityReplacementText("ordm", "º");
/*  564 */       parser.defineEntityReplacementText("raquo", "»");
/*  565 */       parser.defineEntityReplacementText("frac14", "¼");
/*  566 */       parser.defineEntityReplacementText("frac12", "½");
/*  567 */       parser.defineEntityReplacementText("frac34", "¾");
/*  568 */       parser.defineEntityReplacementText("iquest", "¿");
/*  569 */       parser.defineEntityReplacementText("Agrave", "À");
/*  570 */       parser.defineEntityReplacementText("Aacute", "Á");
/*  571 */       parser.defineEntityReplacementText("Acirc", "Â");
/*  572 */       parser.defineEntityReplacementText("Atilde", "Ã");
/*  573 */       parser.defineEntityReplacementText("Auml", "Ä");
/*  574 */       parser.defineEntityReplacementText("Aring", "Å");
/*  575 */       parser.defineEntityReplacementText("AElig", "Æ");
/*  576 */       parser.defineEntityReplacementText("Ccedil", "Ç");
/*  577 */       parser.defineEntityReplacementText("Egrave", "È");
/*  578 */       parser.defineEntityReplacementText("Eacute", "É");
/*  579 */       parser.defineEntityReplacementText("Ecirc", "Ê");
/*  580 */       parser.defineEntityReplacementText("Euml", "Ë");
/*  581 */       parser.defineEntityReplacementText("Igrave", "Ì");
/*  582 */       parser.defineEntityReplacementText("Iacute", "Í");
/*  583 */       parser.defineEntityReplacementText("Icirc", "Î");
/*  584 */       parser.defineEntityReplacementText("Iuml", "Ï");
/*  585 */       parser.defineEntityReplacementText("ETH", "Ð");
/*  586 */       parser.defineEntityReplacementText("Ntilde", "Ñ");
/*  587 */       parser.defineEntityReplacementText("Ograve", "Ò");
/*  588 */       parser.defineEntityReplacementText("Oacute", "Ó");
/*  589 */       parser.defineEntityReplacementText("Ocirc", "Ô");
/*  590 */       parser.defineEntityReplacementText("Otilde", "Õ");
/*  591 */       parser.defineEntityReplacementText("Ouml", "Ö");
/*  592 */       parser.defineEntityReplacementText("times", "×");
/*  593 */       parser.defineEntityReplacementText("Oslash", "Ø");
/*  594 */       parser.defineEntityReplacementText("Ugrave", "Ù");
/*  595 */       parser.defineEntityReplacementText("Uacute", "Ú");
/*  596 */       parser.defineEntityReplacementText("Ucirc", "Û");
/*  597 */       parser.defineEntityReplacementText("Uuml", "Ü");
/*  598 */       parser.defineEntityReplacementText("Yacute", "Ý");
/*  599 */       parser.defineEntityReplacementText("THORN", "Þ");
/*  600 */       parser.defineEntityReplacementText("szlig", "ß");
/*  601 */       parser.defineEntityReplacementText("agrave", "à");
/*  602 */       parser.defineEntityReplacementText("aacute", "á");
/*  603 */       parser.defineEntityReplacementText("acirc", "â");
/*  604 */       parser.defineEntityReplacementText("atilde", "ã");
/*  605 */       parser.defineEntityReplacementText("auml", "ä");
/*  606 */       parser.defineEntityReplacementText("aring", "å");
/*  607 */       parser.defineEntityReplacementText("aelig", "æ");
/*  608 */       parser.defineEntityReplacementText("ccedil", "ç");
/*  609 */       parser.defineEntityReplacementText("egrave", "è");
/*  610 */       parser.defineEntityReplacementText("eacute", "é");
/*  611 */       parser.defineEntityReplacementText("ecirc", "ê");
/*  612 */       parser.defineEntityReplacementText("euml", "ë");
/*  613 */       parser.defineEntityReplacementText("igrave", "ì");
/*  614 */       parser.defineEntityReplacementText("iacute", "í");
/*  615 */       parser.defineEntityReplacementText("icirc", "î");
/*  616 */       parser.defineEntityReplacementText("iuml", "ï");
/*  617 */       parser.defineEntityReplacementText("eth", "ð");
/*  618 */       parser.defineEntityReplacementText("ntilde", "ñ");
/*  619 */       parser.defineEntityReplacementText("ograve", "ò");
/*  620 */       parser.defineEntityReplacementText("oacute", "ó");
/*  621 */       parser.defineEntityReplacementText("ocirc", "ô");
/*  622 */       parser.defineEntityReplacementText("otilde", "õ");
/*  623 */       parser.defineEntityReplacementText("ouml", "ö");
/*  624 */       parser.defineEntityReplacementText("divide", "÷");
/*  625 */       parser.defineEntityReplacementText("oslash", "ø");
/*  626 */       parser.defineEntityReplacementText("ugrave", "ù");
/*  627 */       parser.defineEntityReplacementText("uacute", "ú");
/*  628 */       parser.defineEntityReplacementText("ucirc", "û");
/*  629 */       parser.defineEntityReplacementText("uuml", "ü");
/*  630 */       parser.defineEntityReplacementText("yacute", "ý");
/*  631 */       parser.defineEntityReplacementText("thorn", "þ");
/*  632 */       parser.defineEntityReplacementText("yuml", "ÿ");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  638 */       parser.defineEntityReplacementText("OElig", "Œ");
/*  639 */       parser.defineEntityReplacementText("oelig", "œ");
/*  640 */       parser.defineEntityReplacementText("Scaron", "Š");
/*  641 */       parser.defineEntityReplacementText("scaron", "š");
/*  642 */       parser.defineEntityReplacementText("Yuml", "Ÿ");
/*  643 */       parser.defineEntityReplacementText("circ", "ˆ");
/*  644 */       parser.defineEntityReplacementText("tilde", "˜");
/*  645 */       parser.defineEntityReplacementText("ensp", " ");
/*  646 */       parser.defineEntityReplacementText("emsp", " ");
/*  647 */       parser.defineEntityReplacementText("thinsp", " ");
/*  648 */       parser.defineEntityReplacementText("zwnj", "‌");
/*  649 */       parser.defineEntityReplacementText("zwj", "‍");
/*  650 */       parser.defineEntityReplacementText("lrm", "‎");
/*  651 */       parser.defineEntityReplacementText("rlm", "‏");
/*  652 */       parser.defineEntityReplacementText("ndash", "–");
/*  653 */       parser.defineEntityReplacementText("mdash", "—");
/*  654 */       parser.defineEntityReplacementText("lsquo", "‘");
/*  655 */       parser.defineEntityReplacementText("rsquo", "’");
/*  656 */       parser.defineEntityReplacementText("sbquo", "‚");
/*  657 */       parser.defineEntityReplacementText("ldquo", "“");
/*  658 */       parser.defineEntityReplacementText("rdquo", "”");
/*  659 */       parser.defineEntityReplacementText("bdquo", "„");
/*  660 */       parser.defineEntityReplacementText("dagger", "†");
/*  661 */       parser.defineEntityReplacementText("Dagger", "‡");
/*  662 */       parser.defineEntityReplacementText("permil", "‰");
/*  663 */       parser.defineEntityReplacementText("lsaquo", "‹");
/*  664 */       parser.defineEntityReplacementText("rsaquo", "›");
/*  665 */       parser.defineEntityReplacementText("euro", "€");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  671 */       parser.defineEntityReplacementText("fnof", "ƒ");
/*  672 */       parser.defineEntityReplacementText("Alpha", "Α");
/*  673 */       parser.defineEntityReplacementText("Beta", "Β");
/*  674 */       parser.defineEntityReplacementText("Gamma", "Γ");
/*  675 */       parser.defineEntityReplacementText("Delta", "Δ");
/*  676 */       parser.defineEntityReplacementText("Epsilon", "Ε");
/*  677 */       parser.defineEntityReplacementText("Zeta", "Ζ");
/*  678 */       parser.defineEntityReplacementText("Eta", "Η");
/*  679 */       parser.defineEntityReplacementText("Theta", "Θ");
/*  680 */       parser.defineEntityReplacementText("Iota", "Ι");
/*  681 */       parser.defineEntityReplacementText("Kappa", "Κ");
/*  682 */       parser.defineEntityReplacementText("Lambda", "Λ");
/*  683 */       parser.defineEntityReplacementText("Mu", "Μ");
/*  684 */       parser.defineEntityReplacementText("Nu", "Ν");
/*  685 */       parser.defineEntityReplacementText("Xi", "Ξ");
/*  686 */       parser.defineEntityReplacementText("Omicron", "Ο");
/*  687 */       parser.defineEntityReplacementText("Pi", "Π");
/*  688 */       parser.defineEntityReplacementText("Rho", "Ρ");
/*  689 */       parser.defineEntityReplacementText("Sigma", "Σ");
/*  690 */       parser.defineEntityReplacementText("Tau", "Τ");
/*  691 */       parser.defineEntityReplacementText("Upsilon", "Υ");
/*  692 */       parser.defineEntityReplacementText("Phi", "Φ");
/*  693 */       parser.defineEntityReplacementText("Chi", "Χ");
/*  694 */       parser.defineEntityReplacementText("Psi", "Ψ");
/*  695 */       parser.defineEntityReplacementText("Omega", "Ω");
/*  696 */       parser.defineEntityReplacementText("alpha", "α");
/*  697 */       parser.defineEntityReplacementText("beta", "β");
/*  698 */       parser.defineEntityReplacementText("gamma", "γ");
/*  699 */       parser.defineEntityReplacementText("delta", "δ");
/*  700 */       parser.defineEntityReplacementText("epsilon", "ε");
/*  701 */       parser.defineEntityReplacementText("zeta", "ζ");
/*  702 */       parser.defineEntityReplacementText("eta", "η");
/*  703 */       parser.defineEntityReplacementText("theta", "θ");
/*  704 */       parser.defineEntityReplacementText("iota", "ι");
/*  705 */       parser.defineEntityReplacementText("kappa", "κ");
/*  706 */       parser.defineEntityReplacementText("lambda", "λ");
/*  707 */       parser.defineEntityReplacementText("mu", "μ");
/*  708 */       parser.defineEntityReplacementText("nu", "ν");
/*  709 */       parser.defineEntityReplacementText("xi", "ξ");
/*  710 */       parser.defineEntityReplacementText("omicron", "ο");
/*  711 */       parser.defineEntityReplacementText("pi", "π");
/*  712 */       parser.defineEntityReplacementText("rho", "ρ");
/*  713 */       parser.defineEntityReplacementText("sigmaf", "ς");
/*  714 */       parser.defineEntityReplacementText("sigma", "σ");
/*  715 */       parser.defineEntityReplacementText("tau", "τ");
/*  716 */       parser.defineEntityReplacementText("upsilon", "υ");
/*  717 */       parser.defineEntityReplacementText("phi", "φ");
/*  718 */       parser.defineEntityReplacementText("chi", "χ");
/*  719 */       parser.defineEntityReplacementText("psi", "ψ");
/*  720 */       parser.defineEntityReplacementText("omega", "ω");
/*  721 */       parser.defineEntityReplacementText("thetasym", "ϑ");
/*  722 */       parser.defineEntityReplacementText("upsih", "ϒ");
/*  723 */       parser.defineEntityReplacementText("piv", "ϖ");
/*  724 */       parser.defineEntityReplacementText("bull", "•");
/*  725 */       parser.defineEntityReplacementText("hellip", "…");
/*  726 */       parser.defineEntityReplacementText("prime", "′");
/*  727 */       parser.defineEntityReplacementText("Prime", "″");
/*  728 */       parser.defineEntityReplacementText("oline", "‾");
/*  729 */       parser.defineEntityReplacementText("frasl", "⁄");
/*  730 */       parser.defineEntityReplacementText("weierp", "℘");
/*  731 */       parser.defineEntityReplacementText("image", "ℑ");
/*  732 */       parser.defineEntityReplacementText("real", "ℜ");
/*  733 */       parser.defineEntityReplacementText("trade", "™");
/*  734 */       parser.defineEntityReplacementText("alefsym", "ℵ");
/*  735 */       parser.defineEntityReplacementText("larr", "←");
/*  736 */       parser.defineEntityReplacementText("uarr", "↑");
/*  737 */       parser.defineEntityReplacementText("rarr", "→");
/*  738 */       parser.defineEntityReplacementText("darr", "↓");
/*  739 */       parser.defineEntityReplacementText("harr", "↔");
/*  740 */       parser.defineEntityReplacementText("crarr", "↵");
/*  741 */       parser.defineEntityReplacementText("lArr", "⇐");
/*  742 */       parser.defineEntityReplacementText("uArr", "⇑");
/*  743 */       parser.defineEntityReplacementText("rArr", "⇒");
/*  744 */       parser.defineEntityReplacementText("dArr", "⇓");
/*  745 */       parser.defineEntityReplacementText("hArr", "⇔");
/*  746 */       parser.defineEntityReplacementText("forall", "∀");
/*  747 */       parser.defineEntityReplacementText("part", "∂");
/*  748 */       parser.defineEntityReplacementText("exist", "∃");
/*  749 */       parser.defineEntityReplacementText("empty", "∅");
/*  750 */       parser.defineEntityReplacementText("nabla", "∇");
/*  751 */       parser.defineEntityReplacementText("isin", "∈");
/*  752 */       parser.defineEntityReplacementText("notin", "∉");
/*  753 */       parser.defineEntityReplacementText("ni", "∋");
/*  754 */       parser.defineEntityReplacementText("prod", "∏");
/*  755 */       parser.defineEntityReplacementText("sum", "∑");
/*  756 */       parser.defineEntityReplacementText("minus", "−");
/*  757 */       parser.defineEntityReplacementText("lowast", "∗");
/*  758 */       parser.defineEntityReplacementText("radic", "√");
/*  759 */       parser.defineEntityReplacementText("prop", "∝");
/*  760 */       parser.defineEntityReplacementText("infin", "∞");
/*  761 */       parser.defineEntityReplacementText("ang", "∠");
/*  762 */       parser.defineEntityReplacementText("and", "∧");
/*  763 */       parser.defineEntityReplacementText("or", "∨");
/*  764 */       parser.defineEntityReplacementText("cap", "∩");
/*  765 */       parser.defineEntityReplacementText("cup", "∪");
/*  766 */       parser.defineEntityReplacementText("int", "∫");
/*  767 */       parser.defineEntityReplacementText("there4", "∴");
/*  768 */       parser.defineEntityReplacementText("sim", "∼");
/*  769 */       parser.defineEntityReplacementText("cong", "≅");
/*  770 */       parser.defineEntityReplacementText("asymp", "≈");
/*  771 */       parser.defineEntityReplacementText("ne", "≠");
/*  772 */       parser.defineEntityReplacementText("equiv", "≡");
/*  773 */       parser.defineEntityReplacementText("le", "≤");
/*  774 */       parser.defineEntityReplacementText("ge", "≥");
/*  775 */       parser.defineEntityReplacementText("sub", "⊂");
/*  776 */       parser.defineEntityReplacementText("sup", "⊃");
/*  777 */       parser.defineEntityReplacementText("nsub", "⊄");
/*  778 */       parser.defineEntityReplacementText("sube", "⊆");
/*  779 */       parser.defineEntityReplacementText("supe", "⊇");
/*  780 */       parser.defineEntityReplacementText("oplus", "⊕");
/*  781 */       parser.defineEntityReplacementText("otimes", "⊗");
/*  782 */       parser.defineEntityReplacementText("perp", "⊥");
/*  783 */       parser.defineEntityReplacementText("sdot", "⋅");
/*  784 */       parser.defineEntityReplacementText("lceil", "⌈");
/*  785 */       parser.defineEntityReplacementText("rceil", "⌉");
/*  786 */       parser.defineEntityReplacementText("lfloor", "⌊");
/*  787 */       parser.defineEntityReplacementText("rfloor", "⌋");
/*  788 */       parser.defineEntityReplacementText("lang", "〈");
/*  789 */       parser.defineEntityReplacementText("rang", "〉");
/*  790 */       parser.defineEntityReplacementText("loz", "◊");
/*  791 */       parser.defineEntityReplacementText("spades", "♠");
/*  792 */       parser.defineEntityReplacementText("clubs", "♣");
/*  793 */       parser.defineEntityReplacementText("hearts", "♥");
/*  794 */       parser.defineEntityReplacementText("diams", "♦");
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
/*      */   private int nextTag(XmlPullParser parser) throws IOException, XmlPullParserException {
/*  810 */     int eventType = parser.next();
/*  811 */     if (eventType == 4)
/*      */     {
/*  813 */       eventType = parser.next();
/*      */     }
/*  815 */     if (eventType != 2 && eventType != 3)
/*      */     {
/*  817 */       throw new XmlPullParserException("expected START_TAG or END_TAG not " + XmlPullParser.TYPES[eventType], parser, null);
/*      */     }
/*  819 */     return eventType;
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
/*      */   private Activation parseActivation(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/*  835 */     String tagName = parser.getName();
/*  836 */     Activation activation = new Activation();
/*      */     
/*  838 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/*  839 */     activation.setLocation("", _location);
/*  840 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/*  842 */       String name = parser.getAttributeName(i);
/*  843 */       String value = parser.getAttributeValue(i);
/*      */       
/*  845 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  851 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/*  854 */     Set parsed = new HashSet();
/*  855 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/*  857 */       if (checkFieldWithDuplicate(parser, "activeByDefault", null, parsed)) {
/*      */         
/*  859 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/*  860 */         activation.setLocation("activeByDefault", _location);
/*  861 */         activation.setActiveByDefault(getBooleanValue(getTrimmedValue(parser.nextText()), "activeByDefault", parser, "false")); continue;
/*      */       } 
/*  863 */       if (checkFieldWithDuplicate(parser, "jdk", null, parsed)) {
/*      */         
/*  865 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/*  866 */         activation.setLocation("jdk", _location);
/*  867 */         activation.setJdk(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/*  869 */       if (checkFieldWithDuplicate(parser, "os", null, parsed)) {
/*      */         
/*  871 */         activation.setOs(parseActivationOS(parser, strict, source)); continue;
/*      */       } 
/*  873 */       if (checkFieldWithDuplicate(parser, "property", null, parsed)) {
/*      */         
/*  875 */         activation.setProperty(parseActivationProperty(parser, strict, source)); continue;
/*      */       } 
/*  877 */       if (checkFieldWithDuplicate(parser, "file", null, parsed)) {
/*      */         
/*  879 */         activation.setFile(parseActivationFile(parser, strict, source));
/*      */         
/*      */         continue;
/*      */       } 
/*  883 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/*  886 */     return activation;
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
/*      */   private ActivationFile parseActivationFile(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/*  902 */     String tagName = parser.getName();
/*  903 */     ActivationFile activationFile = new ActivationFile();
/*      */     
/*  905 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/*  906 */     activationFile.setLocation("", _location);
/*  907 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/*  909 */       String name = parser.getAttributeName(i);
/*  910 */       String value = parser.getAttributeValue(i);
/*      */       
/*  912 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  918 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/*  921 */     Set parsed = new HashSet();
/*  922 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/*  924 */       if (checkFieldWithDuplicate(parser, "missing", null, parsed)) {
/*      */         
/*  926 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/*  927 */         activationFile.setLocation("missing", _location);
/*  928 */         activationFile.setMissing(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/*  930 */       if (checkFieldWithDuplicate(parser, "exists", null, parsed)) {
/*      */         
/*  932 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/*  933 */         activationFile.setLocation("exists", _location);
/*  934 */         activationFile.setExists(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/*  938 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/*  941 */     return activationFile;
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
/*      */   private ActivationOS parseActivationOS(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/*  957 */     String tagName = parser.getName();
/*  958 */     ActivationOS activationOS = new ActivationOS();
/*      */     
/*  960 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/*  961 */     activationOS.setLocation("", _location);
/*  962 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/*  964 */       String name = parser.getAttributeName(i);
/*  965 */       String value = parser.getAttributeValue(i);
/*      */       
/*  967 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  973 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/*  976 */     Set parsed = new HashSet();
/*  977 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/*  979 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/*  981 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/*  982 */         activationOS.setLocation("name", _location);
/*  983 */         activationOS.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/*  985 */       if (checkFieldWithDuplicate(parser, "family", null, parsed)) {
/*      */         
/*  987 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/*  988 */         activationOS.setLocation("family", _location);
/*  989 */         activationOS.setFamily(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/*  991 */       if (checkFieldWithDuplicate(parser, "arch", null, parsed)) {
/*      */         
/*  993 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/*  994 */         activationOS.setLocation("arch", _location);
/*  995 */         activationOS.setArch(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/*  997 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/*  999 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1000 */         activationOS.setLocation("version", _location);
/* 1001 */         activationOS.setVersion(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 1005 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1008 */     return activationOS;
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
/*      */   private ActivationProperty parseActivationProperty(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 1024 */     String tagName = parser.getName();
/* 1025 */     ActivationProperty activationProperty = new ActivationProperty();
/*      */     
/* 1027 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1028 */     activationProperty.setLocation("", _location);
/* 1029 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1031 */       String name = parser.getAttributeName(i);
/* 1032 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1034 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1040 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1043 */     Set parsed = new HashSet();
/* 1044 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1046 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 1048 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1049 */         activationProperty.setLocation("name", _location);
/* 1050 */         activationProperty.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1052 */       if (checkFieldWithDuplicate(parser, "value", null, parsed)) {
/*      */         
/* 1054 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1055 */         activationProperty.setLocation("value", _location);
/* 1056 */         activationProperty.setValue(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 1060 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1063 */     return activationProperty;
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
/*      */   private Build parseBuild(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 1079 */     String tagName = parser.getName();
/* 1080 */     Build build = new Build();
/*      */     
/* 1082 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1083 */     build.setLocation("", _location);
/* 1084 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1086 */       String name = parser.getAttributeName(i);
/* 1087 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1089 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1095 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1098 */     Set parsed = new HashSet();
/* 1099 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1101 */       if (checkFieldWithDuplicate(parser, "sourceDirectory", null, parsed)) {
/*      */         
/* 1103 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1104 */         build.setLocation("sourceDirectory", _location);
/* 1105 */         build.setSourceDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1107 */       if (checkFieldWithDuplicate(parser, "scriptSourceDirectory", null, parsed)) {
/*      */         
/* 1109 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1110 */         build.setLocation("scriptSourceDirectory", _location);
/* 1111 */         build.setScriptSourceDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1113 */       if (checkFieldWithDuplicate(parser, "testSourceDirectory", null, parsed)) {
/*      */         
/* 1115 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1116 */         build.setLocation("testSourceDirectory", _location);
/* 1117 */         build.setTestSourceDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1119 */       if (checkFieldWithDuplicate(parser, "outputDirectory", null, parsed)) {
/*      */         
/* 1121 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1122 */         build.setLocation("outputDirectory", _location);
/* 1123 */         build.setOutputDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1125 */       if (checkFieldWithDuplicate(parser, "testOutputDirectory", null, parsed)) {
/*      */         
/* 1127 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1128 */         build.setLocation("testOutputDirectory", _location);
/* 1129 */         build.setTestOutputDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1131 */       if (checkFieldWithDuplicate(parser, "extensions", null, parsed)) {
/*      */         
/* 1133 */         List<Extension> extensions = new ArrayList();
/* 1134 */         build.setExtensions(extensions);
/* 1135 */         while (parser.nextTag() == 2) {
/*      */           
/* 1137 */           if ("extension".equals(parser.getName())) {
/*      */             
/* 1139 */             extensions.add(parseExtension(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 1143 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1147 */       if (checkFieldWithDuplicate(parser, "defaultGoal", null, parsed)) {
/*      */         
/* 1149 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1150 */         build.setLocation("defaultGoal", _location);
/* 1151 */         build.setDefaultGoal(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1153 */       if (checkFieldWithDuplicate(parser, "resources", null, parsed)) {
/*      */         
/* 1155 */         List<Resource> resources = new ArrayList();
/* 1156 */         build.setResources(resources);
/* 1157 */         while (parser.nextTag() == 2) {
/*      */           
/* 1159 */           if ("resource".equals(parser.getName())) {
/*      */             
/* 1161 */             resources.add(parseResource(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 1165 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1169 */       if (checkFieldWithDuplicate(parser, "testResources", null, parsed)) {
/*      */         
/* 1171 */         List<Resource> testResources = new ArrayList();
/* 1172 */         build.setTestResources(testResources);
/* 1173 */         while (parser.nextTag() == 2) {
/*      */           
/* 1175 */           if ("testResource".equals(parser.getName())) {
/*      */             
/* 1177 */             testResources.add(parseResource(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 1181 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1185 */       if (checkFieldWithDuplicate(parser, "directory", null, parsed)) {
/*      */         
/* 1187 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1188 */         build.setLocation("directory", _location);
/* 1189 */         build.setDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1191 */       if (checkFieldWithDuplicate(parser, "finalName", null, parsed)) {
/*      */         
/* 1193 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1194 */         build.setLocation("finalName", _location);
/* 1195 */         build.setFinalName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1197 */       if (checkFieldWithDuplicate(parser, "filters", null, parsed)) {
/*      */         
/* 1199 */         List<String> filters = new ArrayList();
/* 1200 */         build.setFilters(filters);
/*      */         
/* 1202 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1203 */         build.setLocation("filters", _locations);
/* 1204 */         while (parser.nextTag() == 2) {
/*      */           
/* 1206 */           if ("filter".equals(parser.getName())) {
/*      */             
/* 1208 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1209 */             _locations.setLocation(Integer.valueOf(filters.size()), _location);
/* 1210 */             filters.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 1214 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1218 */       if (checkFieldWithDuplicate(parser, "pluginManagement", null, parsed)) {
/*      */         
/* 1220 */         build.setPluginManagement(parsePluginManagement(parser, strict, source)); continue;
/*      */       } 
/* 1222 */       if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
/*      */         
/* 1224 */         List<Plugin> plugins = new ArrayList();
/* 1225 */         build.setPlugins(plugins);
/* 1226 */         while (parser.nextTag() == 2) {
/*      */           
/* 1228 */           if ("plugin".equals(parser.getName())) {
/*      */             
/* 1230 */             plugins.add(parsePlugin(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 1234 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 1240 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1243 */     return build;
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
/*      */   private BuildBase parseBuildBase(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 1259 */     String tagName = parser.getName();
/* 1260 */     BuildBase buildBase = new BuildBase();
/*      */     
/* 1262 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1263 */     buildBase.setLocation("", _location);
/* 1264 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1266 */       String name = parser.getAttributeName(i);
/* 1267 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1269 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1275 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1278 */     Set parsed = new HashSet();
/* 1279 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1281 */       if (checkFieldWithDuplicate(parser, "defaultGoal", null, parsed)) {
/*      */         
/* 1283 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1284 */         buildBase.setLocation("defaultGoal", _location);
/* 1285 */         buildBase.setDefaultGoal(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1287 */       if (checkFieldWithDuplicate(parser, "resources", null, parsed)) {
/*      */         
/* 1289 */         List<Resource> resources = new ArrayList();
/* 1290 */         buildBase.setResources(resources);
/* 1291 */         while (parser.nextTag() == 2) {
/*      */           
/* 1293 */           if ("resource".equals(parser.getName())) {
/*      */             
/* 1295 */             resources.add(parseResource(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 1299 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1303 */       if (checkFieldWithDuplicate(parser, "testResources", null, parsed)) {
/*      */         
/* 1305 */         List<Resource> testResources = new ArrayList();
/* 1306 */         buildBase.setTestResources(testResources);
/* 1307 */         while (parser.nextTag() == 2) {
/*      */           
/* 1309 */           if ("testResource".equals(parser.getName())) {
/*      */             
/* 1311 */             testResources.add(parseResource(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 1315 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1319 */       if (checkFieldWithDuplicate(parser, "directory", null, parsed)) {
/*      */         
/* 1321 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1322 */         buildBase.setLocation("directory", _location);
/* 1323 */         buildBase.setDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1325 */       if (checkFieldWithDuplicate(parser, "finalName", null, parsed)) {
/*      */         
/* 1327 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1328 */         buildBase.setLocation("finalName", _location);
/* 1329 */         buildBase.setFinalName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1331 */       if (checkFieldWithDuplicate(parser, "filters", null, parsed)) {
/*      */         
/* 1333 */         List<String> filters = new ArrayList();
/* 1334 */         buildBase.setFilters(filters);
/*      */         
/* 1336 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1337 */         buildBase.setLocation("filters", _locations);
/* 1338 */         while (parser.nextTag() == 2) {
/*      */           
/* 1340 */           if ("filter".equals(parser.getName())) {
/*      */             
/* 1342 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1343 */             _locations.setLocation(Integer.valueOf(filters.size()), _location);
/* 1344 */             filters.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 1348 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1352 */       if (checkFieldWithDuplicate(parser, "pluginManagement", null, parsed)) {
/*      */         
/* 1354 */         buildBase.setPluginManagement(parsePluginManagement(parser, strict, source)); continue;
/*      */       } 
/* 1356 */       if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
/*      */         
/* 1358 */         List<Plugin> plugins = new ArrayList();
/* 1359 */         buildBase.setPlugins(plugins);
/* 1360 */         while (parser.nextTag() == 2) {
/*      */           
/* 1362 */           if ("plugin".equals(parser.getName())) {
/*      */             
/* 1364 */             plugins.add(parsePlugin(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 1368 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 1374 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1377 */     return buildBase;
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
/*      */   private CiManagement parseCiManagement(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 1393 */     String tagName = parser.getName();
/* 1394 */     CiManagement ciManagement = new CiManagement();
/*      */     
/* 1396 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1397 */     ciManagement.setLocation("", _location);
/* 1398 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1400 */       String name = parser.getAttributeName(i);
/* 1401 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1403 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1409 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1412 */     Set parsed = new HashSet();
/* 1413 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1415 */       if (checkFieldWithDuplicate(parser, "system", null, parsed)) {
/*      */         
/* 1417 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1418 */         ciManagement.setLocation("system", _location);
/* 1419 */         ciManagement.setSystem(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1421 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 1423 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1424 */         ciManagement.setLocation("url", _location);
/* 1425 */         ciManagement.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1427 */       if (checkFieldWithDuplicate(parser, "notifiers", null, parsed)) {
/*      */         
/* 1429 */         List<Notifier> notifiers = new ArrayList();
/* 1430 */         ciManagement.setNotifiers(notifiers);
/* 1431 */         while (parser.nextTag() == 2) {
/*      */           
/* 1433 */           if ("notifier".equals(parser.getName())) {
/*      */             
/* 1435 */             notifiers.add(parseNotifier(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 1439 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 1445 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1448 */     return ciManagement;
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
/*      */   private ConfigurationContainer parseConfigurationContainer(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 1464 */     String tagName = parser.getName();
/* 1465 */     ConfigurationContainer configurationContainer = new ConfigurationContainer();
/*      */     
/* 1467 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1468 */     configurationContainer.setLocation("", _location);
/* 1469 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1471 */       String name = parser.getAttributeName(i);
/* 1472 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1474 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1480 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1483 */     Set parsed = new HashSet();
/* 1484 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1486 */       if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
/*      */         
/* 1488 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1489 */         configurationContainer.setLocation("inherited", _location);
/* 1490 */         configurationContainer.setInherited(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1492 */       if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
/*      */         
/* 1494 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1495 */         configurationContainer.setLocation("configuration", _location);
/* 1496 */         configurationContainer.setConfiguration(Xpp3DomBuilder.build(parser));
/*      */         
/*      */         continue;
/*      */       } 
/* 1500 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1503 */     return configurationContainer;
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
/*      */   private Contributor parseContributor(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 1519 */     String tagName = parser.getName();
/* 1520 */     Contributor contributor = new Contributor();
/*      */     
/* 1522 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1523 */     contributor.setLocation("", _location);
/* 1524 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1526 */       String name = parser.getAttributeName(i);
/* 1527 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1529 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1535 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1538 */     Set parsed = new HashSet();
/* 1539 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1541 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 1543 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1544 */         contributor.setLocation("name", _location);
/* 1545 */         contributor.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1547 */       if (checkFieldWithDuplicate(parser, "email", null, parsed)) {
/*      */         
/* 1549 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1550 */         contributor.setLocation("email", _location);
/* 1551 */         contributor.setEmail(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1553 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 1555 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1556 */         contributor.setLocation("url", _location);
/* 1557 */         contributor.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1559 */       if (checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
/*      */         
/* 1561 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1562 */         contributor.setLocation("organization", _location);
/* 1563 */         contributor.setOrganization(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1565 */       if (checkFieldWithDuplicate(parser, "organizationUrl", "organisationUrl", parsed)) {
/*      */         
/* 1567 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1568 */         contributor.setLocation("organizationUrl", _location);
/* 1569 */         contributor.setOrganizationUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1571 */       if (checkFieldWithDuplicate(parser, "roles", null, parsed)) {
/*      */         
/* 1573 */         List<String> roles = new ArrayList();
/* 1574 */         contributor.setRoles(roles);
/*      */         
/* 1576 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1577 */         contributor.setLocation("roles", _locations);
/* 1578 */         while (parser.nextTag() == 2) {
/*      */           
/* 1580 */           if ("role".equals(parser.getName())) {
/*      */             
/* 1582 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1583 */             _locations.setLocation(Integer.valueOf(roles.size()), _location);
/* 1584 */             roles.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 1588 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1592 */       if (checkFieldWithDuplicate(parser, "timezone", null, parsed)) {
/*      */         
/* 1594 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1595 */         contributor.setLocation("timezone", _location);
/* 1596 */         contributor.setTimezone(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1598 */       if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
/*      */ 
/*      */         
/* 1601 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1602 */         contributor.setLocation("properties", _locations);
/* 1603 */         while (parser.nextTag() == 2) {
/*      */           
/* 1605 */           String key = parser.getName();
/* 1606 */           _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1607 */           _locations.setLocation(key, _location);
/* 1608 */           String value = parser.nextText().trim();
/* 1609 */           contributor.addProperty(key, value);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/* 1614 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1617 */     return contributor;
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
/*      */   private Dependency parseDependency(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 1633 */     String tagName = parser.getName();
/* 1634 */     Dependency dependency = new Dependency();
/*      */     
/* 1636 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1637 */     dependency.setLocation("", _location);
/* 1638 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1640 */       String name = parser.getAttributeName(i);
/* 1641 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1643 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1649 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1652 */     Set parsed = new HashSet();
/* 1653 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1655 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 1657 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1658 */         dependency.setLocation("groupId", _location);
/* 1659 */         dependency.setGroupId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1661 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 1663 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1664 */         dependency.setLocation("artifactId", _location);
/* 1665 */         dependency.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1667 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/* 1669 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1670 */         dependency.setLocation("version", _location);
/* 1671 */         dependency.setVersion(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1673 */       if (checkFieldWithDuplicate(parser, "type", null, parsed)) {
/*      */         
/* 1675 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1676 */         dependency.setLocation("type", _location);
/* 1677 */         dependency.setType(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1679 */       if (checkFieldWithDuplicate(parser, "classifier", null, parsed)) {
/*      */         
/* 1681 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1682 */         dependency.setLocation("classifier", _location);
/* 1683 */         dependency.setClassifier(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1685 */       if (checkFieldWithDuplicate(parser, "scope", null, parsed)) {
/*      */         
/* 1687 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1688 */         dependency.setLocation("scope", _location);
/* 1689 */         dependency.setScope(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1691 */       if (checkFieldWithDuplicate(parser, "systemPath", null, parsed)) {
/*      */         
/* 1693 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1694 */         dependency.setLocation("systemPath", _location);
/* 1695 */         dependency.setSystemPath(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1697 */       if (checkFieldWithDuplicate(parser, "exclusions", null, parsed)) {
/*      */         
/* 1699 */         List<Exclusion> exclusions = new ArrayList();
/* 1700 */         dependency.setExclusions(exclusions);
/* 1701 */         while (parser.nextTag() == 2) {
/*      */           
/* 1703 */           if ("exclusion".equals(parser.getName())) {
/*      */             
/* 1705 */             exclusions.add(parseExclusion(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 1709 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1713 */       if (checkFieldWithDuplicate(parser, "optional", null, parsed)) {
/*      */         
/* 1715 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1716 */         dependency.setLocation("optional", _location);
/* 1717 */         dependency.setOptional(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 1721 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1724 */     return dependency;
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
/*      */   private DependencyManagement parseDependencyManagement(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 1740 */     String tagName = parser.getName();
/* 1741 */     DependencyManagement dependencyManagement = new DependencyManagement();
/*      */     
/* 1743 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1744 */     dependencyManagement.setLocation("", _location);
/* 1745 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1747 */       String name = parser.getAttributeName(i);
/* 1748 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1750 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1756 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1759 */     Set parsed = new HashSet();
/* 1760 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1762 */       if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
/*      */         
/* 1764 */         List<Dependency> dependencies = new ArrayList();
/* 1765 */         dependencyManagement.setDependencies(dependencies);
/* 1766 */         while (parser.nextTag() == 2) {
/*      */           
/* 1768 */           if ("dependency".equals(parser.getName())) {
/*      */             
/* 1770 */             dependencies.add(parseDependency(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 1774 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 1780 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1783 */     return dependencyManagement;
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
/*      */   private DeploymentRepository parseDeploymentRepository(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 1799 */     String tagName = parser.getName();
/* 1800 */     DeploymentRepository deploymentRepository = new DeploymentRepository();
/*      */     
/* 1802 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1803 */     deploymentRepository.setLocation("", _location);
/* 1804 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1806 */       String name = parser.getAttributeName(i);
/* 1807 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1809 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1815 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1818 */     Set parsed = new HashSet();
/* 1819 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1821 */       if (checkFieldWithDuplicate(parser, "uniqueVersion", null, parsed)) {
/*      */         
/* 1823 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1824 */         deploymentRepository.setLocation("uniqueVersion", _location);
/* 1825 */         deploymentRepository.setUniqueVersion(getBooleanValue(getTrimmedValue(parser.nextText()), "uniqueVersion", parser, "true")); continue;
/*      */       } 
/* 1827 */       if (checkFieldWithDuplicate(parser, "releases", null, parsed)) {
/*      */         
/* 1829 */         deploymentRepository.setReleases(parseRepositoryPolicy(parser, strict, source)); continue;
/*      */       } 
/* 1831 */       if (checkFieldWithDuplicate(parser, "snapshots", null, parsed)) {
/*      */         
/* 1833 */         deploymentRepository.setSnapshots(parseRepositoryPolicy(parser, strict, source)); continue;
/*      */       } 
/* 1835 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 1837 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1838 */         deploymentRepository.setLocation("id", _location);
/* 1839 */         deploymentRepository.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1841 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 1843 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1844 */         deploymentRepository.setLocation("name", _location);
/* 1845 */         deploymentRepository.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1847 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 1849 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1850 */         deploymentRepository.setLocation("url", _location);
/* 1851 */         deploymentRepository.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1853 */       if (checkFieldWithDuplicate(parser, "layout", null, parsed)) {
/*      */         
/* 1855 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1856 */         deploymentRepository.setLocation("layout", _location);
/* 1857 */         deploymentRepository.setLayout(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 1861 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1864 */     return deploymentRepository;
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
/*      */   private Developer parseDeveloper(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 1880 */     String tagName = parser.getName();
/* 1881 */     Developer developer = new Developer();
/*      */     
/* 1883 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1884 */     developer.setLocation("", _location);
/* 1885 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1887 */       String name = parser.getAttributeName(i);
/* 1888 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1890 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1896 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1899 */     Set parsed = new HashSet();
/* 1900 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1902 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 1904 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1905 */         developer.setLocation("id", _location);
/* 1906 */         developer.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1908 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 1910 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1911 */         developer.setLocation("name", _location);
/* 1912 */         developer.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1914 */       if (checkFieldWithDuplicate(parser, "email", null, parsed)) {
/*      */         
/* 1916 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1917 */         developer.setLocation("email", _location);
/* 1918 */         developer.setEmail(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1920 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 1922 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1923 */         developer.setLocation("url", _location);
/* 1924 */         developer.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1926 */       if (checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
/*      */         
/* 1928 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1929 */         developer.setLocation("organization", _location);
/* 1930 */         developer.setOrganization(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1932 */       if (checkFieldWithDuplicate(parser, "organizationUrl", "organisationUrl", parsed)) {
/*      */         
/* 1934 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1935 */         developer.setLocation("organizationUrl", _location);
/* 1936 */         developer.setOrganizationUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1938 */       if (checkFieldWithDuplicate(parser, "roles", null, parsed)) {
/*      */         
/* 1940 */         List<String> roles = new ArrayList();
/* 1941 */         developer.setRoles(roles);
/*      */         
/* 1943 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1944 */         developer.setLocation("roles", _locations);
/* 1945 */         while (parser.nextTag() == 2) {
/*      */           
/* 1947 */           if ("role".equals(parser.getName())) {
/*      */             
/* 1949 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1950 */             _locations.setLocation(Integer.valueOf(roles.size()), _location);
/* 1951 */             roles.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 1955 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1959 */       if (checkFieldWithDuplicate(parser, "timezone", null, parsed)) {
/*      */         
/* 1961 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1962 */         developer.setLocation("timezone", _location);
/* 1963 */         developer.setTimezone(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1965 */       if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
/*      */ 
/*      */         
/* 1968 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1969 */         developer.setLocation("properties", _locations);
/* 1970 */         while (parser.nextTag() == 2) {
/*      */           
/* 1972 */           String key = parser.getName();
/* 1973 */           _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 1974 */           _locations.setLocation(key, _location);
/* 1975 */           String value = parser.nextText().trim();
/* 1976 */           developer.addProperty(key, value);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/* 1981 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1984 */     return developer;
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
/*      */   private DistributionManagement parseDistributionManagement(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 2000 */     String tagName = parser.getName();
/* 2001 */     DistributionManagement distributionManagement = new DistributionManagement();
/*      */     
/* 2003 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2004 */     distributionManagement.setLocation("", _location);
/* 2005 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2007 */       String name = parser.getAttributeName(i);
/* 2008 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2010 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2016 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2019 */     Set parsed = new HashSet();
/* 2020 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2022 */       if (checkFieldWithDuplicate(parser, "repository", null, parsed)) {
/*      */         
/* 2024 */         distributionManagement.setRepository(parseDeploymentRepository(parser, strict, source)); continue;
/*      */       } 
/* 2026 */       if (checkFieldWithDuplicate(parser, "snapshotRepository", null, parsed)) {
/*      */         
/* 2028 */         distributionManagement.setSnapshotRepository(parseDeploymentRepository(parser, strict, source)); continue;
/*      */       } 
/* 2030 */       if (checkFieldWithDuplicate(parser, "site", null, parsed)) {
/*      */         
/* 2032 */         distributionManagement.setSite(parseSite(parser, strict, source)); continue;
/*      */       } 
/* 2034 */       if (checkFieldWithDuplicate(parser, "downloadUrl", null, parsed)) {
/*      */         
/* 2036 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2037 */         distributionManagement.setLocation("downloadUrl", _location);
/* 2038 */         distributionManagement.setDownloadUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2040 */       if (checkFieldWithDuplicate(parser, "relocation", null, parsed)) {
/*      */         
/* 2042 */         distributionManagement.setRelocation(parseRelocation(parser, strict, source)); continue;
/*      */       } 
/* 2044 */       if (checkFieldWithDuplicate(parser, "status", null, parsed)) {
/*      */         
/* 2046 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2047 */         distributionManagement.setLocation("status", _location);
/* 2048 */         distributionManagement.setStatus(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 2052 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2055 */     return distributionManagement;
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
/*      */   private Exclusion parseExclusion(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 2071 */     String tagName = parser.getName();
/* 2072 */     Exclusion exclusion = new Exclusion();
/*      */     
/* 2074 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2075 */     exclusion.setLocation("", _location);
/* 2076 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2078 */       String name = parser.getAttributeName(i);
/* 2079 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2081 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2087 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2090 */     Set parsed = new HashSet();
/* 2091 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2093 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 2095 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2096 */         exclusion.setLocation("artifactId", _location);
/* 2097 */         exclusion.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2099 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 2101 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2102 */         exclusion.setLocation("groupId", _location);
/* 2103 */         exclusion.setGroupId(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 2107 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2110 */     return exclusion;
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
/*      */   private Extension parseExtension(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 2126 */     String tagName = parser.getName();
/* 2127 */     Extension extension = new Extension();
/*      */     
/* 2129 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2130 */     extension.setLocation("", _location);
/* 2131 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2133 */       String name = parser.getAttributeName(i);
/* 2134 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2136 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2142 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2145 */     Set parsed = new HashSet();
/* 2146 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2148 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 2150 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2151 */         extension.setLocation("groupId", _location);
/* 2152 */         extension.setGroupId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2154 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 2156 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2157 */         extension.setLocation("artifactId", _location);
/* 2158 */         extension.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2160 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/* 2162 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2163 */         extension.setLocation("version", _location);
/* 2164 */         extension.setVersion(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 2168 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2171 */     return extension;
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
/*      */   private FileSet parseFileSet(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 2187 */     String tagName = parser.getName();
/* 2188 */     FileSet fileSet = new FileSet();
/*      */     
/* 2190 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2191 */     fileSet.setLocation("", _location);
/* 2192 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2194 */       String name = parser.getAttributeName(i);
/* 2195 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2197 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2203 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2206 */     Set parsed = new HashSet();
/* 2207 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2209 */       if (checkFieldWithDuplicate(parser, "directory", null, parsed)) {
/*      */         
/* 2211 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2212 */         fileSet.setLocation("directory", _location);
/* 2213 */         fileSet.setDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2215 */       if (checkFieldWithDuplicate(parser, "includes", null, parsed)) {
/*      */         
/* 2217 */         List<String> includes = new ArrayList();
/* 2218 */         fileSet.setIncludes(includes);
/*      */         
/* 2220 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2221 */         fileSet.setLocation("includes", _locations);
/* 2222 */         while (parser.nextTag() == 2) {
/*      */           
/* 2224 */           if ("include".equals(parser.getName())) {
/*      */             
/* 2226 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2227 */             _locations.setLocation(Integer.valueOf(includes.size()), _location);
/* 2228 */             includes.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 2232 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2236 */       if (checkFieldWithDuplicate(parser, "excludes", null, parsed)) {
/*      */         
/* 2238 */         List<String> excludes = new ArrayList();
/* 2239 */         fileSet.setExcludes(excludes);
/*      */         
/* 2241 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2242 */         fileSet.setLocation("excludes", _locations);
/* 2243 */         while (parser.nextTag() == 2) {
/*      */           
/* 2245 */           if ("exclude".equals(parser.getName())) {
/*      */             
/* 2247 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2248 */             _locations.setLocation(Integer.valueOf(excludes.size()), _location);
/* 2249 */             excludes.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 2253 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 2259 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2262 */     return fileSet;
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
/*      */   private IssueManagement parseIssueManagement(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 2278 */     String tagName = parser.getName();
/* 2279 */     IssueManagement issueManagement = new IssueManagement();
/*      */     
/* 2281 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2282 */     issueManagement.setLocation("", _location);
/* 2283 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2285 */       String name = parser.getAttributeName(i);
/* 2286 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2288 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2294 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2297 */     Set parsed = new HashSet();
/* 2298 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2300 */       if (checkFieldWithDuplicate(parser, "system", null, parsed)) {
/*      */         
/* 2302 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2303 */         issueManagement.setLocation("system", _location);
/* 2304 */         issueManagement.setSystem(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2306 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 2308 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2309 */         issueManagement.setLocation("url", _location);
/* 2310 */         issueManagement.setUrl(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 2314 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2317 */     return issueManagement;
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
/*      */   private License parseLicense(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 2333 */     String tagName = parser.getName();
/* 2334 */     License license = new License();
/*      */     
/* 2336 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2337 */     license.setLocation("", _location);
/* 2338 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2340 */       String name = parser.getAttributeName(i);
/* 2341 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2343 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2349 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2352 */     Set parsed = new HashSet();
/* 2353 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2355 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 2357 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2358 */         license.setLocation("name", _location);
/* 2359 */         license.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2361 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 2363 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2364 */         license.setLocation("url", _location);
/* 2365 */         license.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2367 */       if (checkFieldWithDuplicate(parser, "distribution", null, parsed)) {
/*      */         
/* 2369 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2370 */         license.setLocation("distribution", _location);
/* 2371 */         license.setDistribution(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2373 */       if (checkFieldWithDuplicate(parser, "comments", null, parsed)) {
/*      */         
/* 2375 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2376 */         license.setLocation("comments", _location);
/* 2377 */         license.setComments(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 2381 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2384 */     return license;
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
/*      */   private MailingList parseMailingList(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 2400 */     String tagName = parser.getName();
/* 2401 */     MailingList mailingList = new MailingList();
/*      */     
/* 2403 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2404 */     mailingList.setLocation("", _location);
/* 2405 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2407 */       String name = parser.getAttributeName(i);
/* 2408 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2410 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2416 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2419 */     Set parsed = new HashSet();
/* 2420 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2422 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 2424 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2425 */         mailingList.setLocation("name", _location);
/* 2426 */         mailingList.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2428 */       if (checkFieldWithDuplicate(parser, "subscribe", null, parsed)) {
/*      */         
/* 2430 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2431 */         mailingList.setLocation("subscribe", _location);
/* 2432 */         mailingList.setSubscribe(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2434 */       if (checkFieldWithDuplicate(parser, "unsubscribe", null, parsed)) {
/*      */         
/* 2436 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2437 */         mailingList.setLocation("unsubscribe", _location);
/* 2438 */         mailingList.setUnsubscribe(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2440 */       if (checkFieldWithDuplicate(parser, "post", null, parsed)) {
/*      */         
/* 2442 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2443 */         mailingList.setLocation("post", _location);
/* 2444 */         mailingList.setPost(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2446 */       if (checkFieldWithDuplicate(parser, "archive", null, parsed)) {
/*      */         
/* 2448 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2449 */         mailingList.setLocation("archive", _location);
/* 2450 */         mailingList.setArchive(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2452 */       if (checkFieldWithDuplicate(parser, "otherArchives", null, parsed)) {
/*      */         
/* 2454 */         List<String> otherArchives = new ArrayList();
/* 2455 */         mailingList.setOtherArchives(otherArchives);
/*      */         
/* 2457 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2458 */         mailingList.setLocation("otherArchives", _locations);
/* 2459 */         while (parser.nextTag() == 2) {
/*      */           
/* 2461 */           if ("otherArchive".equals(parser.getName())) {
/*      */             
/* 2463 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2464 */             _locations.setLocation(Integer.valueOf(otherArchives.size()), _location);
/* 2465 */             otherArchives.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 2469 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 2475 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2478 */     return mailingList;
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
/*      */   private Model parseModel(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 2494 */     String tagName = parser.getName();
/* 2495 */     Model model = new Model();
/*      */     
/* 2497 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2498 */     model.setLocation("", _location);
/* 2499 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2501 */       String name = parser.getAttributeName(i);
/* 2502 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2504 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */         
/* 2508 */         if (!"xmlns".equals(name))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2514 */           checkUnknownAttribute(parser, name, tagName, strict); } 
/*      */       }
/*      */     } 
/* 2517 */     Set parsed = new HashSet();
/* 2518 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2520 */       if (checkFieldWithDuplicate(parser, "modelVersion", null, parsed)) {
/*      */         
/* 2522 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2523 */         model.setLocation("modelVersion", _location);
/* 2524 */         model.setModelVersion(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2526 */       if (checkFieldWithDuplicate(parser, "parent", null, parsed)) {
/*      */         
/* 2528 */         model.setParent(parseParent(parser, strict, source)); continue;
/*      */       } 
/* 2530 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 2532 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2533 */         model.setLocation("groupId", _location);
/* 2534 */         model.setGroupId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2536 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 2538 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2539 */         model.setLocation("artifactId", _location);
/* 2540 */         model.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2542 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/* 2544 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2545 */         model.setLocation("version", _location);
/* 2546 */         model.setVersion(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2548 */       if (checkFieldWithDuplicate(parser, "packaging", null, parsed)) {
/*      */         
/* 2550 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2551 */         model.setLocation("packaging", _location);
/* 2552 */         model.setPackaging(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2554 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 2556 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2557 */         model.setLocation("name", _location);
/* 2558 */         model.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2560 */       if (checkFieldWithDuplicate(parser, "description", null, parsed)) {
/*      */         
/* 2562 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2563 */         model.setLocation("description", _location);
/* 2564 */         model.setDescription(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2566 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 2568 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2569 */         model.setLocation("url", _location);
/* 2570 */         model.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2572 */       if (checkFieldWithDuplicate(parser, "inceptionYear", null, parsed)) {
/*      */         
/* 2574 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2575 */         model.setLocation("inceptionYear", _location);
/* 2576 */         model.setInceptionYear(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2578 */       if (checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
/*      */         
/* 2580 */         model.setOrganization(parseOrganization(parser, strict, source)); continue;
/*      */       } 
/* 2582 */       if (checkFieldWithDuplicate(parser, "licenses", null, parsed)) {
/*      */         
/* 2584 */         List<License> licenses = new ArrayList();
/* 2585 */         model.setLicenses(licenses);
/* 2586 */         while (parser.nextTag() == 2) {
/*      */           
/* 2588 */           if ("license".equals(parser.getName())) {
/*      */             
/* 2590 */             licenses.add(parseLicense(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 2594 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2598 */       if (checkFieldWithDuplicate(parser, "developers", null, parsed)) {
/*      */         
/* 2600 */         List<Developer> developers = new ArrayList();
/* 2601 */         model.setDevelopers(developers);
/* 2602 */         while (parser.nextTag() == 2) {
/*      */           
/* 2604 */           if ("developer".equals(parser.getName())) {
/*      */             
/* 2606 */             developers.add(parseDeveloper(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 2610 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2614 */       if (checkFieldWithDuplicate(parser, "contributors", null, parsed)) {
/*      */         
/* 2616 */         List<Contributor> contributors = new ArrayList();
/* 2617 */         model.setContributors(contributors);
/* 2618 */         while (parser.nextTag() == 2) {
/*      */           
/* 2620 */           if ("contributor".equals(parser.getName())) {
/*      */             
/* 2622 */             contributors.add(parseContributor(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 2626 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2630 */       if (checkFieldWithDuplicate(parser, "mailingLists", null, parsed)) {
/*      */         
/* 2632 */         List<MailingList> mailingLists = new ArrayList();
/* 2633 */         model.setMailingLists(mailingLists);
/* 2634 */         while (parser.nextTag() == 2) {
/*      */           
/* 2636 */           if ("mailingList".equals(parser.getName())) {
/*      */             
/* 2638 */             mailingLists.add(parseMailingList(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 2642 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2646 */       if (checkFieldWithDuplicate(parser, "prerequisites", null, parsed)) {
/*      */         
/* 2648 */         model.setPrerequisites(parsePrerequisites(parser, strict, source)); continue;
/*      */       } 
/* 2650 */       if (checkFieldWithDuplicate(parser, "modules", null, parsed)) {
/*      */         
/* 2652 */         List<String> modules = new ArrayList();
/* 2653 */         model.setModules(modules);
/*      */         
/* 2655 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2656 */         model.setLocation("modules", _locations);
/* 2657 */         while (parser.nextTag() == 2) {
/*      */           
/* 2659 */           if ("module".equals(parser.getName())) {
/*      */             
/* 2661 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2662 */             _locations.setLocation(Integer.valueOf(modules.size()), _location);
/* 2663 */             modules.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 2667 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2671 */       if (checkFieldWithDuplicate(parser, "scm", null, parsed)) {
/*      */         
/* 2673 */         model.setScm(parseScm(parser, strict, source)); continue;
/*      */       } 
/* 2675 */       if (checkFieldWithDuplicate(parser, "issueManagement", null, parsed)) {
/*      */         
/* 2677 */         model.setIssueManagement(parseIssueManagement(parser, strict, source)); continue;
/*      */       } 
/* 2679 */       if (checkFieldWithDuplicate(parser, "ciManagement", null, parsed)) {
/*      */         
/* 2681 */         model.setCiManagement(parseCiManagement(parser, strict, source)); continue;
/*      */       } 
/* 2683 */       if (checkFieldWithDuplicate(parser, "distributionManagement", null, parsed)) {
/*      */         
/* 2685 */         model.setDistributionManagement(parseDistributionManagement(parser, strict, source)); continue;
/*      */       } 
/* 2687 */       if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
/*      */ 
/*      */         
/* 2690 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2691 */         model.setLocation("properties", _locations);
/* 2692 */         while (parser.nextTag() == 2) {
/*      */           
/* 2694 */           String key = parser.getName();
/* 2695 */           _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2696 */           _locations.setLocation(key, _location);
/* 2697 */           String value = parser.nextText().trim();
/* 2698 */           model.addProperty(key, value);
/*      */         }  continue;
/*      */       } 
/* 2701 */       if (checkFieldWithDuplicate(parser, "dependencyManagement", null, parsed)) {
/*      */         
/* 2703 */         model.setDependencyManagement(parseDependencyManagement(parser, strict, source)); continue;
/*      */       } 
/* 2705 */       if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
/*      */         
/* 2707 */         List<Dependency> dependencies = new ArrayList();
/* 2708 */         model.setDependencies(dependencies);
/* 2709 */         while (parser.nextTag() == 2) {
/*      */           
/* 2711 */           if ("dependency".equals(parser.getName())) {
/*      */             
/* 2713 */             dependencies.add(parseDependency(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 2717 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2721 */       if (checkFieldWithDuplicate(parser, "repositories", null, parsed)) {
/*      */         
/* 2723 */         List<Repository> repositories = new ArrayList();
/* 2724 */         model.setRepositories(repositories);
/* 2725 */         while (parser.nextTag() == 2) {
/*      */           
/* 2727 */           if ("repository".equals(parser.getName())) {
/*      */             
/* 2729 */             repositories.add(parseRepository(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 2733 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2737 */       if (checkFieldWithDuplicate(parser, "pluginRepositories", null, parsed)) {
/*      */         
/* 2739 */         List<Repository> pluginRepositories = new ArrayList();
/* 2740 */         model.setPluginRepositories(pluginRepositories);
/* 2741 */         while (parser.nextTag() == 2) {
/*      */           
/* 2743 */           if ("pluginRepository".equals(parser.getName())) {
/*      */             
/* 2745 */             pluginRepositories.add(parseRepository(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 2749 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2753 */       if (checkFieldWithDuplicate(parser, "build", null, parsed)) {
/*      */         
/* 2755 */         model.setBuild(parseBuild(parser, strict, source)); continue;
/*      */       } 
/* 2757 */       if (checkFieldWithDuplicate(parser, "reports", null, parsed)) {
/*      */         
/* 2759 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2760 */         model.setLocation("reports", _location);
/* 2761 */         model.setReports(Xpp3DomBuilder.build(parser)); continue;
/*      */       } 
/* 2763 */       if (checkFieldWithDuplicate(parser, "reporting", null, parsed)) {
/*      */         
/* 2765 */         model.setReporting(parseReporting(parser, strict, source)); continue;
/*      */       } 
/* 2767 */       if (checkFieldWithDuplicate(parser, "profiles", null, parsed)) {
/*      */         
/* 2769 */         List<Profile> profiles = new ArrayList();
/* 2770 */         model.setProfiles(profiles);
/* 2771 */         while (parser.nextTag() == 2) {
/*      */           
/* 2773 */           if ("profile".equals(parser.getName())) {
/*      */             
/* 2775 */             profiles.add(parseProfile(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 2779 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 2785 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2788 */     return model;
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
/*      */   private ModelBase parseModelBase(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 2804 */     String tagName = parser.getName();
/* 2805 */     ModelBase modelBase = new ModelBase();
/*      */     
/* 2807 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2808 */     modelBase.setLocation("", _location);
/* 2809 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2811 */       String name = parser.getAttributeName(i);
/* 2812 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2814 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2820 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2823 */     Set parsed = new HashSet();
/* 2824 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2826 */       if (checkFieldWithDuplicate(parser, "modules", null, parsed)) {
/*      */         
/* 2828 */         List<String> modules = new ArrayList();
/* 2829 */         modelBase.setModules(modules);
/*      */         
/* 2831 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2832 */         modelBase.setLocation("modules", _locations);
/* 2833 */         while (parser.nextTag() == 2) {
/*      */           
/* 2835 */           if ("module".equals(parser.getName())) {
/*      */             
/* 2837 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2838 */             _locations.setLocation(Integer.valueOf(modules.size()), _location);
/* 2839 */             modules.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 2843 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2847 */       if (checkFieldWithDuplicate(parser, "distributionManagement", null, parsed)) {
/*      */         
/* 2849 */         modelBase.setDistributionManagement(parseDistributionManagement(parser, strict, source)); continue;
/*      */       } 
/* 2851 */       if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
/*      */ 
/*      */         
/* 2854 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2855 */         modelBase.setLocation("properties", _locations);
/* 2856 */         while (parser.nextTag() == 2) {
/*      */           
/* 2858 */           String key = parser.getName();
/* 2859 */           _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2860 */           _locations.setLocation(key, _location);
/* 2861 */           String value = parser.nextText().trim();
/* 2862 */           modelBase.addProperty(key, value);
/*      */         }  continue;
/*      */       } 
/* 2865 */       if (checkFieldWithDuplicate(parser, "dependencyManagement", null, parsed)) {
/*      */         
/* 2867 */         modelBase.setDependencyManagement(parseDependencyManagement(parser, strict, source)); continue;
/*      */       } 
/* 2869 */       if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
/*      */         
/* 2871 */         List<Dependency> dependencies = new ArrayList();
/* 2872 */         modelBase.setDependencies(dependencies);
/* 2873 */         while (parser.nextTag() == 2) {
/*      */           
/* 2875 */           if ("dependency".equals(parser.getName())) {
/*      */             
/* 2877 */             dependencies.add(parseDependency(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 2881 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2885 */       if (checkFieldWithDuplicate(parser, "repositories", null, parsed)) {
/*      */         
/* 2887 */         List<Repository> repositories = new ArrayList();
/* 2888 */         modelBase.setRepositories(repositories);
/* 2889 */         while (parser.nextTag() == 2) {
/*      */           
/* 2891 */           if ("repository".equals(parser.getName())) {
/*      */             
/* 2893 */             repositories.add(parseRepository(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 2897 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2901 */       if (checkFieldWithDuplicate(parser, "pluginRepositories", null, parsed)) {
/*      */         
/* 2903 */         List<Repository> pluginRepositories = new ArrayList();
/* 2904 */         modelBase.setPluginRepositories(pluginRepositories);
/* 2905 */         while (parser.nextTag() == 2) {
/*      */           
/* 2907 */           if ("pluginRepository".equals(parser.getName())) {
/*      */             
/* 2909 */             pluginRepositories.add(parseRepository(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 2913 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2917 */       if (checkFieldWithDuplicate(parser, "reports", null, parsed)) {
/*      */         
/* 2919 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2920 */         modelBase.setLocation("reports", _location);
/* 2921 */         modelBase.setReports(Xpp3DomBuilder.build(parser)); continue;
/*      */       } 
/* 2923 */       if (checkFieldWithDuplicate(parser, "reporting", null, parsed)) {
/*      */         
/* 2925 */         modelBase.setReporting(parseReporting(parser, strict, source));
/*      */         
/*      */         continue;
/*      */       } 
/* 2929 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2932 */     return modelBase;
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
/*      */   private Notifier parseNotifier(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 2948 */     String tagName = parser.getName();
/* 2949 */     Notifier notifier = new Notifier();
/*      */     
/* 2951 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2952 */     notifier.setLocation("", _location);
/* 2953 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2955 */       String name = parser.getAttributeName(i);
/* 2956 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2958 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2964 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2967 */     Set parsed = new HashSet();
/* 2968 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2970 */       if (checkFieldWithDuplicate(parser, "type", null, parsed)) {
/*      */         
/* 2972 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2973 */         notifier.setLocation("type", _location);
/* 2974 */         notifier.setType(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2976 */       if (checkFieldWithDuplicate(parser, "sendOnError", null, parsed)) {
/*      */         
/* 2978 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2979 */         notifier.setLocation("sendOnError", _location);
/* 2980 */         notifier.setSendOnError(getBooleanValue(getTrimmedValue(parser.nextText()), "sendOnError", parser, "true")); continue;
/*      */       } 
/* 2982 */       if (checkFieldWithDuplicate(parser, "sendOnFailure", null, parsed)) {
/*      */         
/* 2984 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2985 */         notifier.setLocation("sendOnFailure", _location);
/* 2986 */         notifier.setSendOnFailure(getBooleanValue(getTrimmedValue(parser.nextText()), "sendOnFailure", parser, "true")); continue;
/*      */       } 
/* 2988 */       if (checkFieldWithDuplicate(parser, "sendOnSuccess", null, parsed)) {
/*      */         
/* 2990 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2991 */         notifier.setLocation("sendOnSuccess", _location);
/* 2992 */         notifier.setSendOnSuccess(getBooleanValue(getTrimmedValue(parser.nextText()), "sendOnSuccess", parser, "true")); continue;
/*      */       } 
/* 2994 */       if (checkFieldWithDuplicate(parser, "sendOnWarning", null, parsed)) {
/*      */         
/* 2996 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 2997 */         notifier.setLocation("sendOnWarning", _location);
/* 2998 */         notifier.setSendOnWarning(getBooleanValue(getTrimmedValue(parser.nextText()), "sendOnWarning", parser, "true")); continue;
/*      */       } 
/* 3000 */       if (checkFieldWithDuplicate(parser, "address", null, parsed)) {
/*      */         
/* 3002 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3003 */         notifier.setLocation("address", _location);
/* 3004 */         notifier.setAddress(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3006 */       if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
/*      */ 
/*      */         
/* 3009 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3010 */         notifier.setLocation("configuration", _locations);
/* 3011 */         while (parser.nextTag() == 2) {
/*      */           
/* 3013 */           String key = parser.getName();
/* 3014 */           _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3015 */           _locations.setLocation(key, _location);
/* 3016 */           String value = parser.nextText().trim();
/* 3017 */           notifier.addConfiguration(key, value);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/* 3022 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3025 */     return notifier;
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
/*      */   private Organization parseOrganization(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 3041 */     String tagName = parser.getName();
/* 3042 */     Organization organization = new Organization();
/*      */     
/* 3044 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3045 */     organization.setLocation("", _location);
/* 3046 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3048 */       String name = parser.getAttributeName(i);
/* 3049 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3051 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3057 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3060 */     Set parsed = new HashSet();
/* 3061 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3063 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 3065 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3066 */         organization.setLocation("name", _location);
/* 3067 */         organization.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3069 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 3071 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3072 */         organization.setLocation("url", _location);
/* 3073 */         organization.setUrl(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 3077 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3080 */     return organization;
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
/*      */   private Parent parseParent(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 3096 */     String tagName = parser.getName();
/* 3097 */     Parent parent = new Parent();
/*      */     
/* 3099 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3100 */     parent.setLocation("", _location);
/* 3101 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3103 */       String name = parser.getAttributeName(i);
/* 3104 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3106 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3112 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3115 */     Set parsed = new HashSet();
/* 3116 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3118 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 3120 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3121 */         parent.setLocation("artifactId", _location);
/* 3122 */         parent.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3124 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 3126 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3127 */         parent.setLocation("groupId", _location);
/* 3128 */         parent.setGroupId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3130 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/* 3132 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3133 */         parent.setLocation("version", _location);
/* 3134 */         parent.setVersion(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3136 */       if (checkFieldWithDuplicate(parser, "relativePath", null, parsed)) {
/*      */         
/* 3138 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3139 */         parent.setLocation("relativePath", _location);
/* 3140 */         parent.setRelativePath(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 3144 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3147 */     return parent;
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
/*      */   private PatternSet parsePatternSet(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 3163 */     String tagName = parser.getName();
/* 3164 */     PatternSet patternSet = new PatternSet();
/*      */     
/* 3166 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3167 */     patternSet.setLocation("", _location);
/* 3168 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3170 */       String name = parser.getAttributeName(i);
/* 3171 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3173 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3179 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3182 */     Set parsed = new HashSet();
/* 3183 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3185 */       if (checkFieldWithDuplicate(parser, "includes", null, parsed)) {
/*      */         
/* 3187 */         List<String> includes = new ArrayList();
/* 3188 */         patternSet.setIncludes(includes);
/*      */         
/* 3190 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3191 */         patternSet.setLocation("includes", _locations);
/* 3192 */         while (parser.nextTag() == 2) {
/*      */           
/* 3194 */           if ("include".equals(parser.getName())) {
/*      */             
/* 3196 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3197 */             _locations.setLocation(Integer.valueOf(includes.size()), _location);
/* 3198 */             includes.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 3202 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 3206 */       if (checkFieldWithDuplicate(parser, "excludes", null, parsed)) {
/*      */         
/* 3208 */         List<String> excludes = new ArrayList();
/* 3209 */         patternSet.setExcludes(excludes);
/*      */         
/* 3211 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3212 */         patternSet.setLocation("excludes", _locations);
/* 3213 */         while (parser.nextTag() == 2) {
/*      */           
/* 3215 */           if ("exclude".equals(parser.getName())) {
/*      */             
/* 3217 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3218 */             _locations.setLocation(Integer.valueOf(excludes.size()), _location);
/* 3219 */             excludes.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 3223 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 3229 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3232 */     return patternSet;
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
/*      */   private Plugin parsePlugin(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 3248 */     String tagName = parser.getName();
/* 3249 */     Plugin plugin = new Plugin();
/*      */     
/* 3251 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3252 */     plugin.setLocation("", _location);
/* 3253 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3255 */       String name = parser.getAttributeName(i);
/* 3256 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3258 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3264 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3267 */     Set parsed = new HashSet();
/* 3268 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3270 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 3272 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3273 */         plugin.setLocation("groupId", _location);
/* 3274 */         plugin.setGroupId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3276 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 3278 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3279 */         plugin.setLocation("artifactId", _location);
/* 3280 */         plugin.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3282 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/* 3284 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3285 */         plugin.setLocation("version", _location);
/* 3286 */         plugin.setVersion(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3288 */       if (checkFieldWithDuplicate(parser, "extensions", null, parsed)) {
/*      */         
/* 3290 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3291 */         plugin.setLocation("extensions", _location);
/* 3292 */         plugin.setExtensions(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3294 */       if (checkFieldWithDuplicate(parser, "executions", null, parsed)) {
/*      */         
/* 3296 */         List<PluginExecution> executions = new ArrayList();
/* 3297 */         plugin.setExecutions(executions);
/* 3298 */         while (parser.nextTag() == 2) {
/*      */           
/* 3300 */           if ("execution".equals(parser.getName())) {
/*      */             
/* 3302 */             executions.add(parsePluginExecution(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 3306 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 3310 */       if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
/*      */         
/* 3312 */         List<Dependency> dependencies = new ArrayList();
/* 3313 */         plugin.setDependencies(dependencies);
/* 3314 */         while (parser.nextTag() == 2) {
/*      */           
/* 3316 */           if ("dependency".equals(parser.getName())) {
/*      */             
/* 3318 */             dependencies.add(parseDependency(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 3322 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 3326 */       if (checkFieldWithDuplicate(parser, "goals", null, parsed)) {
/*      */         
/* 3328 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3329 */         plugin.setLocation("goals", _location);
/* 3330 */         plugin.setGoals(Xpp3DomBuilder.build(parser)); continue;
/*      */       } 
/* 3332 */       if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
/*      */         
/* 3334 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3335 */         plugin.setLocation("inherited", _location);
/* 3336 */         plugin.setInherited(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3338 */       if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
/*      */         
/* 3340 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3341 */         plugin.setLocation("configuration", _location);
/* 3342 */         plugin.setConfiguration(Xpp3DomBuilder.build(parser));
/*      */         
/*      */         continue;
/*      */       } 
/* 3346 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3349 */     return plugin;
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
/*      */   private PluginConfiguration parsePluginConfiguration(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 3365 */     String tagName = parser.getName();
/* 3366 */     PluginConfiguration pluginConfiguration = new PluginConfiguration();
/*      */     
/* 3368 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3369 */     pluginConfiguration.setLocation("", _location);
/* 3370 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3372 */       String name = parser.getAttributeName(i);
/* 3373 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3375 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3381 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3384 */     Set parsed = new HashSet();
/* 3385 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3387 */       if (checkFieldWithDuplicate(parser, "pluginManagement", null, parsed)) {
/*      */         
/* 3389 */         pluginConfiguration.setPluginManagement(parsePluginManagement(parser, strict, source)); continue;
/*      */       } 
/* 3391 */       if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
/*      */         
/* 3393 */         List<Plugin> plugins = new ArrayList();
/* 3394 */         pluginConfiguration.setPlugins(plugins);
/* 3395 */         while (parser.nextTag() == 2) {
/*      */           
/* 3397 */           if ("plugin".equals(parser.getName())) {
/*      */             
/* 3399 */             plugins.add(parsePlugin(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 3403 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 3409 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3412 */     return pluginConfiguration;
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
/*      */   private PluginContainer parsePluginContainer(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 3428 */     String tagName = parser.getName();
/* 3429 */     PluginContainer pluginContainer = new PluginContainer();
/*      */     
/* 3431 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3432 */     pluginContainer.setLocation("", _location);
/* 3433 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3435 */       String name = parser.getAttributeName(i);
/* 3436 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3438 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3444 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3447 */     Set parsed = new HashSet();
/* 3448 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3450 */       if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
/*      */         
/* 3452 */         List<Plugin> plugins = new ArrayList();
/* 3453 */         pluginContainer.setPlugins(plugins);
/* 3454 */         while (parser.nextTag() == 2) {
/*      */           
/* 3456 */           if ("plugin".equals(parser.getName())) {
/*      */             
/* 3458 */             plugins.add(parsePlugin(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 3462 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 3468 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3471 */     return pluginContainer;
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
/*      */   private PluginExecution parsePluginExecution(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 3487 */     String tagName = parser.getName();
/* 3488 */     PluginExecution pluginExecution = new PluginExecution();
/*      */     
/* 3490 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3491 */     pluginExecution.setLocation("", _location);
/* 3492 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3494 */       String name = parser.getAttributeName(i);
/* 3495 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3497 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3503 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3506 */     Set parsed = new HashSet();
/* 3507 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3509 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 3511 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3512 */         pluginExecution.setLocation("id", _location);
/* 3513 */         pluginExecution.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3515 */       if (checkFieldWithDuplicate(parser, "phase", null, parsed)) {
/*      */         
/* 3517 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3518 */         pluginExecution.setLocation("phase", _location);
/* 3519 */         pluginExecution.setPhase(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3521 */       if (checkFieldWithDuplicate(parser, "goals", null, parsed)) {
/*      */         
/* 3523 */         List<String> goals = new ArrayList();
/* 3524 */         pluginExecution.setGoals(goals);
/*      */         
/* 3526 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3527 */         pluginExecution.setLocation("goals", _locations);
/* 3528 */         while (parser.nextTag() == 2) {
/*      */           
/* 3530 */           if ("goal".equals(parser.getName())) {
/*      */             
/* 3532 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3533 */             _locations.setLocation(Integer.valueOf(goals.size()), _location);
/* 3534 */             goals.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 3538 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 3542 */       if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
/*      */         
/* 3544 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3545 */         pluginExecution.setLocation("inherited", _location);
/* 3546 */         pluginExecution.setInherited(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3548 */       if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
/*      */         
/* 3550 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3551 */         pluginExecution.setLocation("configuration", _location);
/* 3552 */         pluginExecution.setConfiguration(Xpp3DomBuilder.build(parser));
/*      */         
/*      */         continue;
/*      */       } 
/* 3556 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3559 */     return pluginExecution;
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
/*      */   private PluginManagement parsePluginManagement(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 3575 */     String tagName = parser.getName();
/* 3576 */     PluginManagement pluginManagement = new PluginManagement();
/*      */     
/* 3578 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3579 */     pluginManagement.setLocation("", _location);
/* 3580 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3582 */       String name = parser.getAttributeName(i);
/* 3583 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3585 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3591 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3594 */     Set parsed = new HashSet();
/* 3595 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3597 */       if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
/*      */         
/* 3599 */         List<Plugin> plugins = new ArrayList();
/* 3600 */         pluginManagement.setPlugins(plugins);
/* 3601 */         while (parser.nextTag() == 2) {
/*      */           
/* 3603 */           if ("plugin".equals(parser.getName())) {
/*      */             
/* 3605 */             plugins.add(parsePlugin(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 3609 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 3615 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3618 */     return pluginManagement;
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
/*      */   private Prerequisites parsePrerequisites(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 3634 */     String tagName = parser.getName();
/* 3635 */     Prerequisites prerequisites = new Prerequisites();
/*      */     
/* 3637 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3638 */     prerequisites.setLocation("", _location);
/* 3639 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3641 */       String name = parser.getAttributeName(i);
/* 3642 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3644 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3650 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3653 */     Set parsed = new HashSet();
/* 3654 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3656 */       if (checkFieldWithDuplicate(parser, "maven", null, parsed)) {
/*      */         
/* 3658 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3659 */         prerequisites.setLocation("maven", _location);
/* 3660 */         prerequisites.setMaven(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 3664 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3667 */     return prerequisites;
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
/*      */   private Profile parseProfile(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 3683 */     String tagName = parser.getName();
/* 3684 */     Profile profile = new Profile();
/*      */     
/* 3686 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3687 */     profile.setLocation("", _location);
/* 3688 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3690 */       String name = parser.getAttributeName(i);
/* 3691 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3693 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3699 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3702 */     Set parsed = new HashSet();
/* 3703 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3705 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 3707 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3708 */         profile.setLocation("id", _location);
/* 3709 */         profile.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3711 */       if (checkFieldWithDuplicate(parser, "activation", null, parsed)) {
/*      */         
/* 3713 */         profile.setActivation(parseActivation(parser, strict, source)); continue;
/*      */       } 
/* 3715 */       if (checkFieldWithDuplicate(parser, "build", null, parsed)) {
/*      */         
/* 3717 */         profile.setBuild(parseBuildBase(parser, strict, source)); continue;
/*      */       } 
/* 3719 */       if (checkFieldWithDuplicate(parser, "modules", null, parsed)) {
/*      */         
/* 3721 */         List<String> modules = new ArrayList();
/* 3722 */         profile.setModules(modules);
/*      */         
/* 3724 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3725 */         profile.setLocation("modules", _locations);
/* 3726 */         while (parser.nextTag() == 2) {
/*      */           
/* 3728 */           if ("module".equals(parser.getName())) {
/*      */             
/* 3730 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3731 */             _locations.setLocation(Integer.valueOf(modules.size()), _location);
/* 3732 */             modules.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 3736 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 3740 */       if (checkFieldWithDuplicate(parser, "distributionManagement", null, parsed)) {
/*      */         
/* 3742 */         profile.setDistributionManagement(parseDistributionManagement(parser, strict, source)); continue;
/*      */       } 
/* 3744 */       if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
/*      */ 
/*      */         
/* 3747 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3748 */         profile.setLocation("properties", _locations);
/* 3749 */         while (parser.nextTag() == 2) {
/*      */           
/* 3751 */           String key = parser.getName();
/* 3752 */           _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3753 */           _locations.setLocation(key, _location);
/* 3754 */           String value = parser.nextText().trim();
/* 3755 */           profile.addProperty(key, value);
/*      */         }  continue;
/*      */       } 
/* 3758 */       if (checkFieldWithDuplicate(parser, "dependencyManagement", null, parsed)) {
/*      */         
/* 3760 */         profile.setDependencyManagement(parseDependencyManagement(parser, strict, source)); continue;
/*      */       } 
/* 3762 */       if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
/*      */         
/* 3764 */         List<Dependency> dependencies = new ArrayList();
/* 3765 */         profile.setDependencies(dependencies);
/* 3766 */         while (parser.nextTag() == 2) {
/*      */           
/* 3768 */           if ("dependency".equals(parser.getName())) {
/*      */             
/* 3770 */             dependencies.add(parseDependency(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 3774 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 3778 */       if (checkFieldWithDuplicate(parser, "repositories", null, parsed)) {
/*      */         
/* 3780 */         List<Repository> repositories = new ArrayList();
/* 3781 */         profile.setRepositories(repositories);
/* 3782 */         while (parser.nextTag() == 2) {
/*      */           
/* 3784 */           if ("repository".equals(parser.getName())) {
/*      */             
/* 3786 */             repositories.add(parseRepository(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 3790 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 3794 */       if (checkFieldWithDuplicate(parser, "pluginRepositories", null, parsed)) {
/*      */         
/* 3796 */         List<Repository> pluginRepositories = new ArrayList();
/* 3797 */         profile.setPluginRepositories(pluginRepositories);
/* 3798 */         while (parser.nextTag() == 2) {
/*      */           
/* 3800 */           if ("pluginRepository".equals(parser.getName())) {
/*      */             
/* 3802 */             pluginRepositories.add(parseRepository(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 3806 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 3810 */       if (checkFieldWithDuplicate(parser, "reports", null, parsed)) {
/*      */         
/* 3812 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3813 */         profile.setLocation("reports", _location);
/* 3814 */         profile.setReports(Xpp3DomBuilder.build(parser)); continue;
/*      */       } 
/* 3816 */       if (checkFieldWithDuplicate(parser, "reporting", null, parsed)) {
/*      */         
/* 3818 */         profile.setReporting(parseReporting(parser, strict, source));
/*      */         
/*      */         continue;
/*      */       } 
/* 3822 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3825 */     return profile;
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
/*      */   private Relocation parseRelocation(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 3841 */     String tagName = parser.getName();
/* 3842 */     Relocation relocation = new Relocation();
/*      */     
/* 3844 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3845 */     relocation.setLocation("", _location);
/* 3846 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3848 */       String name = parser.getAttributeName(i);
/* 3849 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3851 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3857 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3860 */     Set parsed = new HashSet();
/* 3861 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3863 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 3865 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3866 */         relocation.setLocation("groupId", _location);
/* 3867 */         relocation.setGroupId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3869 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 3871 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3872 */         relocation.setLocation("artifactId", _location);
/* 3873 */         relocation.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3875 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/* 3877 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3878 */         relocation.setLocation("version", _location);
/* 3879 */         relocation.setVersion(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3881 */       if (checkFieldWithDuplicate(parser, "message", null, parsed)) {
/*      */         
/* 3883 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3884 */         relocation.setLocation("message", _location);
/* 3885 */         relocation.setMessage(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 3889 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3892 */     return relocation;
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
/*      */   private ReportPlugin parseReportPlugin(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 3908 */     String tagName = parser.getName();
/* 3909 */     ReportPlugin reportPlugin = new ReportPlugin();
/*      */     
/* 3911 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3912 */     reportPlugin.setLocation("", _location);
/* 3913 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3915 */       String name = parser.getAttributeName(i);
/* 3916 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3918 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3924 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3927 */     Set parsed = new HashSet();
/* 3928 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3930 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 3932 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3933 */         reportPlugin.setLocation("groupId", _location);
/* 3934 */         reportPlugin.setGroupId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3936 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 3938 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3939 */         reportPlugin.setLocation("artifactId", _location);
/* 3940 */         reportPlugin.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3942 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/* 3944 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3945 */         reportPlugin.setLocation("version", _location);
/* 3946 */         reportPlugin.setVersion(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3948 */       if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
/*      */         
/* 3950 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3951 */         reportPlugin.setLocation("inherited", _location);
/* 3952 */         reportPlugin.setInherited(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3954 */       if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
/*      */         
/* 3956 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 3957 */         reportPlugin.setLocation("configuration", _location);
/* 3958 */         reportPlugin.setConfiguration(Xpp3DomBuilder.build(parser)); continue;
/*      */       } 
/* 3960 */       if (checkFieldWithDuplicate(parser, "reportSets", null, parsed)) {
/*      */         
/* 3962 */         List<ReportSet> reportSets = new ArrayList();
/* 3963 */         reportPlugin.setReportSets(reportSets);
/* 3964 */         while (parser.nextTag() == 2) {
/*      */           
/* 3966 */           if ("reportSet".equals(parser.getName())) {
/*      */             
/* 3968 */             reportSets.add(parseReportSet(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 3972 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 3978 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3981 */     return reportPlugin;
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
/*      */   private ReportSet parseReportSet(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 3997 */     String tagName = parser.getName();
/* 3998 */     ReportSet reportSet = new ReportSet();
/*      */     
/* 4000 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4001 */     reportSet.setLocation("", _location);
/* 4002 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 4004 */       String name = parser.getAttributeName(i);
/* 4005 */       String value = parser.getAttributeValue(i);
/*      */       
/* 4007 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4013 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 4016 */     Set parsed = new HashSet();
/* 4017 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 4019 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 4021 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4022 */         reportSet.setLocation("id", _location);
/* 4023 */         reportSet.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4025 */       if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
/*      */         
/* 4027 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4028 */         reportSet.setLocation("configuration", _location);
/* 4029 */         reportSet.setConfiguration(Xpp3DomBuilder.build(parser)); continue;
/*      */       } 
/* 4031 */       if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
/*      */         
/* 4033 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4034 */         reportSet.setLocation("inherited", _location);
/* 4035 */         reportSet.setInherited(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4037 */       if (checkFieldWithDuplicate(parser, "reports", null, parsed)) {
/*      */         
/* 4039 */         List<String> reports = new ArrayList();
/* 4040 */         reportSet.setReports(reports);
/*      */         
/* 4042 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4043 */         reportSet.setLocation("reports", _locations);
/* 4044 */         while (parser.nextTag() == 2) {
/*      */           
/* 4046 */           if ("report".equals(parser.getName())) {
/*      */             
/* 4048 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4049 */             _locations.setLocation(Integer.valueOf(reports.size()), _location);
/* 4050 */             reports.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 4054 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 4060 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 4063 */     return reportSet;
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
/*      */   private Reporting parseReporting(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 4079 */     String tagName = parser.getName();
/* 4080 */     Reporting reporting = new Reporting();
/*      */     
/* 4082 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4083 */     reporting.setLocation("", _location);
/* 4084 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 4086 */       String name = parser.getAttributeName(i);
/* 4087 */       String value = parser.getAttributeValue(i);
/*      */       
/* 4089 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4095 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 4098 */     Set parsed = new HashSet();
/* 4099 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 4101 */       if (checkFieldWithDuplicate(parser, "excludeDefaults", null, parsed)) {
/*      */         
/* 4103 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4104 */         reporting.setLocation("excludeDefaults", _location);
/* 4105 */         reporting.setExcludeDefaults(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4107 */       if (checkFieldWithDuplicate(parser, "outputDirectory", null, parsed)) {
/*      */         
/* 4109 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4110 */         reporting.setLocation("outputDirectory", _location);
/* 4111 */         reporting.setOutputDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4113 */       if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
/*      */         
/* 4115 */         List<ReportPlugin> plugins = new ArrayList();
/* 4116 */         reporting.setPlugins(plugins);
/* 4117 */         while (parser.nextTag() == 2) {
/*      */           
/* 4119 */           if ("plugin".equals(parser.getName())) {
/*      */             
/* 4121 */             plugins.add(parseReportPlugin(parser, strict, source));
/*      */             
/*      */             continue;
/*      */           } 
/* 4125 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 4131 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 4134 */     return reporting;
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
/*      */   private Repository parseRepository(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 4150 */     String tagName = parser.getName();
/* 4151 */     Repository repository = new Repository();
/*      */     
/* 4153 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4154 */     repository.setLocation("", _location);
/* 4155 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 4157 */       String name = parser.getAttributeName(i);
/* 4158 */       String value = parser.getAttributeValue(i);
/*      */       
/* 4160 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4166 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 4169 */     Set parsed = new HashSet();
/* 4170 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 4172 */       if (checkFieldWithDuplicate(parser, "releases", null, parsed)) {
/*      */         
/* 4174 */         repository.setReleases(parseRepositoryPolicy(parser, strict, source)); continue;
/*      */       } 
/* 4176 */       if (checkFieldWithDuplicate(parser, "snapshots", null, parsed)) {
/*      */         
/* 4178 */         repository.setSnapshots(parseRepositoryPolicy(parser, strict, source)); continue;
/*      */       } 
/* 4180 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 4182 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4183 */         repository.setLocation("id", _location);
/* 4184 */         repository.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4186 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 4188 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4189 */         repository.setLocation("name", _location);
/* 4190 */         repository.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4192 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 4194 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4195 */         repository.setLocation("url", _location);
/* 4196 */         repository.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4198 */       if (checkFieldWithDuplicate(parser, "layout", null, parsed)) {
/*      */         
/* 4200 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4201 */         repository.setLocation("layout", _location);
/* 4202 */         repository.setLayout(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 4206 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 4209 */     return repository;
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
/*      */   private RepositoryBase parseRepositoryBase(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 4225 */     String tagName = parser.getName();
/* 4226 */     RepositoryBase repositoryBase = new RepositoryBase();
/*      */     
/* 4228 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4229 */     repositoryBase.setLocation("", _location);
/* 4230 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 4232 */       String name = parser.getAttributeName(i);
/* 4233 */       String value = parser.getAttributeValue(i);
/*      */       
/* 4235 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4241 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 4244 */     Set parsed = new HashSet();
/* 4245 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 4247 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 4249 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4250 */         repositoryBase.setLocation("id", _location);
/* 4251 */         repositoryBase.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4253 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 4255 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4256 */         repositoryBase.setLocation("name", _location);
/* 4257 */         repositoryBase.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4259 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 4261 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4262 */         repositoryBase.setLocation("url", _location);
/* 4263 */         repositoryBase.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4265 */       if (checkFieldWithDuplicate(parser, "layout", null, parsed)) {
/*      */         
/* 4267 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4268 */         repositoryBase.setLocation("layout", _location);
/* 4269 */         repositoryBase.setLayout(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 4273 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 4276 */     return repositoryBase;
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
/*      */   private RepositoryPolicy parseRepositoryPolicy(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 4292 */     String tagName = parser.getName();
/* 4293 */     RepositoryPolicy repositoryPolicy = new RepositoryPolicy();
/*      */     
/* 4295 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4296 */     repositoryPolicy.setLocation("", _location);
/* 4297 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 4299 */       String name = parser.getAttributeName(i);
/* 4300 */       String value = parser.getAttributeValue(i);
/*      */       
/* 4302 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4308 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 4311 */     Set parsed = new HashSet();
/* 4312 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 4314 */       if (checkFieldWithDuplicate(parser, "enabled", null, parsed)) {
/*      */         
/* 4316 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4317 */         repositoryPolicy.setLocation("enabled", _location);
/* 4318 */         repositoryPolicy.setEnabled(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4320 */       if (checkFieldWithDuplicate(parser, "updatePolicy", null, parsed)) {
/*      */         
/* 4322 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4323 */         repositoryPolicy.setLocation("updatePolicy", _location);
/* 4324 */         repositoryPolicy.setUpdatePolicy(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4326 */       if (checkFieldWithDuplicate(parser, "checksumPolicy", null, parsed)) {
/*      */         
/* 4328 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4329 */         repositoryPolicy.setLocation("checksumPolicy", _location);
/* 4330 */         repositoryPolicy.setChecksumPolicy(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 4334 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 4337 */     return repositoryPolicy;
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
/*      */   private Resource parseResource(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 4353 */     String tagName = parser.getName();
/* 4354 */     Resource resource = new Resource();
/*      */     
/* 4356 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4357 */     resource.setLocation("", _location);
/* 4358 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 4360 */       String name = parser.getAttributeName(i);
/* 4361 */       String value = parser.getAttributeValue(i);
/*      */       
/* 4363 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4369 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 4372 */     Set parsed = new HashSet();
/* 4373 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 4375 */       if (checkFieldWithDuplicate(parser, "targetPath", null, parsed)) {
/*      */         
/* 4377 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4378 */         resource.setLocation("targetPath", _location);
/* 4379 */         resource.setTargetPath(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4381 */       if (checkFieldWithDuplicate(parser, "filtering", null, parsed)) {
/*      */         
/* 4383 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4384 */         resource.setLocation("filtering", _location);
/* 4385 */         resource.setFiltering(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4387 */       if (checkFieldWithDuplicate(parser, "mergeId", null, parsed)) {
/*      */         
/* 4389 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4390 */         resource.setLocation("mergeId", _location);
/* 4391 */         resource.setMergeId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4393 */       if (checkFieldWithDuplicate(parser, "directory", null, parsed)) {
/*      */         
/* 4395 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4396 */         resource.setLocation("directory", _location);
/* 4397 */         resource.setDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4399 */       if (checkFieldWithDuplicate(parser, "includes", null, parsed)) {
/*      */         
/* 4401 */         List<String> includes = new ArrayList();
/* 4402 */         resource.setIncludes(includes);
/*      */         
/* 4404 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4405 */         resource.setLocation("includes", _locations);
/* 4406 */         while (parser.nextTag() == 2) {
/*      */           
/* 4408 */           if ("include".equals(parser.getName())) {
/*      */             
/* 4410 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4411 */             _locations.setLocation(Integer.valueOf(includes.size()), _location);
/* 4412 */             includes.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 4416 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 4420 */       if (checkFieldWithDuplicate(parser, "excludes", null, parsed)) {
/*      */         
/* 4422 */         List<String> excludes = new ArrayList();
/* 4423 */         resource.setExcludes(excludes);
/*      */         
/* 4425 */         InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4426 */         resource.setLocation("excludes", _locations);
/* 4427 */         while (parser.nextTag() == 2) {
/*      */           
/* 4429 */           if ("exclude".equals(parser.getName())) {
/*      */             
/* 4431 */             _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4432 */             _locations.setLocation(Integer.valueOf(excludes.size()), _location);
/* 4433 */             excludes.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 4437 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 4443 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 4446 */     return resource;
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
/*      */   private Scm parseScm(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 4462 */     String tagName = parser.getName();
/* 4463 */     Scm scm = new Scm();
/*      */     
/* 4465 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4466 */     scm.setLocation("", _location);
/* 4467 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 4469 */       String name = parser.getAttributeName(i);
/* 4470 */       String value = parser.getAttributeValue(i);
/*      */       
/* 4472 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4478 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 4481 */     Set parsed = new HashSet();
/* 4482 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 4484 */       if (checkFieldWithDuplicate(parser, "connection", null, parsed)) {
/*      */         
/* 4486 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4487 */         scm.setLocation("connection", _location);
/* 4488 */         scm.setConnection(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4490 */       if (checkFieldWithDuplicate(parser, "developerConnection", null, parsed)) {
/*      */         
/* 4492 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4493 */         scm.setLocation("developerConnection", _location);
/* 4494 */         scm.setDeveloperConnection(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4496 */       if (checkFieldWithDuplicate(parser, "tag", null, parsed)) {
/*      */         
/* 4498 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4499 */         scm.setLocation("tag", _location);
/* 4500 */         scm.setTag(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4502 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 4504 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4505 */         scm.setLocation("url", _location);
/* 4506 */         scm.setUrl(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 4510 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 4513 */     return scm;
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
/*      */   private Site parseSite(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 4529 */     String tagName = parser.getName();
/* 4530 */     Site site = new Site();
/*      */     
/* 4532 */     InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4533 */     site.setLocation("", _location);
/* 4534 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 4536 */       String name = parser.getAttributeName(i);
/* 4537 */       String value = parser.getAttributeValue(i);
/*      */       
/* 4539 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4545 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 4548 */     Set parsed = new HashSet();
/* 4549 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 4551 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 4553 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4554 */         site.setLocation("id", _location);
/* 4555 */         site.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4557 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 4559 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4560 */         site.setLocation("name", _location);
/* 4561 */         site.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 4563 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 4565 */         _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
/* 4566 */         site.setLocation("url", _location);
/* 4567 */         site.setUrl(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 4571 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 4574 */     return site;
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
/*      */   public Model read(Reader reader, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 4590 */     MXParser mXParser = new MXParser();
/*      */     
/* 4592 */     mXParser.setInput(reader);
/*      */     
/* 4594 */     initParser((XmlPullParser)mXParser);
/*      */     
/* 4596 */     return read((XmlPullParser)mXParser, strict, source);
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
/*      */   public Model read(InputStream in, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 4612 */     return read((Reader)ReaderFactory.newXmlReader(in), strict, source);
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
/*      */   private Model read(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
/* 4628 */     int eventType = parser.getEventType();
/* 4629 */     while (eventType != 1) {
/*      */       
/* 4631 */       if (eventType == 2) {
/*      */         
/* 4633 */         if (strict && !"project".equals(parser.getName()))
/*      */         {
/* 4635 */           throw new XmlPullParserException("Expected root element 'project' but found '" + parser.getName() + "'", parser, null);
/*      */         }
/* 4637 */         Model model = parseModel(parser, strict, source);
/* 4638 */         model.setModelEncoding(parser.getInputEncoding());
/* 4639 */         return model;
/*      */       } 
/* 4641 */       eventType = parser.next();
/*      */     } 
/* 4643 */     throw new XmlPullParserException("Expected root element 'project' but found no element at all: invalid XML document", parser, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAddDefaultEntities(boolean addDefaultEntities) {
/* 4653 */     this.addDefaultEntities = addDefaultEntities;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\io\xpp3\MavenXpp3ReaderEx.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */