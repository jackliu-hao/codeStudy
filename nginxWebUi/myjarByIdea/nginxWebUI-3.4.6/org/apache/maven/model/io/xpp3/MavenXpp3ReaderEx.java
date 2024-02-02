package org.apache.maven.model.io.xpp3;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.FileSet;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.PatternSet;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginConfiguration;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class MavenXpp3ReaderEx {
   private boolean addDefaultEntities = true;

   private boolean checkFieldWithDuplicate(XmlPullParser parser, String tagName, String alias, Set parsed) throws XmlPullParserException {
      if (!parser.getName().equals(tagName) && !parser.getName().equals(alias)) {
         return false;
      } else if (!parsed.add(tagName)) {
         throw new XmlPullParserException("Duplicated tag: '" + tagName + "'", parser, (Throwable)null);
      } else {
         return true;
      }
   }

   private void checkUnknownAttribute(XmlPullParser parser, String attribute, String tagName, boolean strict) throws XmlPullParserException, IOException {
      if (strict) {
         throw new XmlPullParserException("Unknown attribute '" + attribute + "' for tag '" + tagName + "'", parser, (Throwable)null);
      }
   }

   private void checkUnknownElement(XmlPullParser parser, boolean strict) throws XmlPullParserException, IOException {
      if (strict) {
         throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, (Throwable)null);
      } else {
         int unrecognizedTagCount = 1;

         while(unrecognizedTagCount > 0) {
            int eventType = parser.next();
            if (eventType == 2) {
               ++unrecognizedTagCount;
            } else if (eventType == 3) {
               --unrecognizedTagCount;
            }
         }

      }
   }

   public boolean getAddDefaultEntities() {
      return this.addDefaultEntities;
   }

   private boolean getBooleanValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
      return this.getBooleanValue(s, attribute, parser, (String)null);
   }

   private boolean getBooleanValue(String s, String attribute, XmlPullParser parser, String defaultValue) throws XmlPullParserException {
      if (s != null && s.length() != 0) {
         return Boolean.valueOf(s);
      } else {
         return defaultValue != null ? Boolean.valueOf(defaultValue) : false;
      }
   }

   private byte getByteValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
      if (s != null) {
         try {
            return Byte.valueOf(s);
         } catch (NumberFormatException var6) {
            if (strict) {
               throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a byte", parser, var6);
            }
         }
      }

      return 0;
   }

   private char getCharacterValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
      return s != null ? s.charAt(0) : '\u0000';
   }

   private Date getDateValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
      return this.getDateValue(s, attribute, (String)null, parser);
   }

   private Date getDateValue(String s, String attribute, String dateFormat, XmlPullParser parser) throws XmlPullParserException {
      if (s != null) {
         String effectiveDateFormat = dateFormat;
         if (dateFormat == null) {
            effectiveDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
         }

         if ("long".equals(effectiveDateFormat)) {
            try {
               return new Date(Long.parseLong(s));
            } catch (NumberFormatException var7) {
               throw new XmlPullParserException(var7.getMessage(), parser, var7);
            }
         } else {
            try {
               DateFormat dateParser = new SimpleDateFormat(effectiveDateFormat, Locale.US);
               return dateParser.parse(s);
            } catch (ParseException var8) {
               throw new XmlPullParserException(var8.getMessage(), parser, var8);
            }
         }
      } else {
         return null;
      }
   }

   private double getDoubleValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
      if (s != null) {
         try {
            return Double.valueOf(s);
         } catch (NumberFormatException var6) {
            if (strict) {
               throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a floating point number", parser, var6);
            }
         }
      }

      return 0.0;
   }

   private float getFloatValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
      if (s != null) {
         try {
            return Float.valueOf(s);
         } catch (NumberFormatException var6) {
            if (strict) {
               throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a floating point number", parser, var6);
            }
         }
      }

      return 0.0F;
   }

   private int getIntegerValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
      if (s != null) {
         try {
            return Integer.valueOf(s);
         } catch (NumberFormatException var6) {
            if (strict) {
               throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be an integer", parser, var6);
            }
         }
      }

      return 0;
   }

   private long getLongValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
      if (s != null) {
         try {
            return Long.valueOf(s);
         } catch (NumberFormatException var6) {
            if (strict) {
               throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a long integer", parser, var6);
            }
         }
      }

      return 0L;
   }

   private String getRequiredAttributeValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
      if (s == null && strict) {
         throw new XmlPullParserException("Missing required value for attribute '" + attribute + "'", parser, (Throwable)null);
      } else {
         return s;
      }
   }

   private short getShortValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
      if (s != null) {
         try {
            return Short.valueOf(s);
         } catch (NumberFormatException var6) {
            if (strict) {
               throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a short integer", parser, var6);
            }
         }
      }

      return 0;
   }

   private String getTrimmedValue(String s) {
      if (s != null) {
         s = s.trim();
      }

      return s;
   }

   private void initParser(XmlPullParser parser) throws XmlPullParserException {
      if (this.addDefaultEntities) {
         parser.defineEntityReplacementText("nbsp", " ");
         parser.defineEntityReplacementText("iexcl", "¡");
         parser.defineEntityReplacementText("cent", "¢");
         parser.defineEntityReplacementText("pound", "£");
         parser.defineEntityReplacementText("curren", "¤");
         parser.defineEntityReplacementText("yen", "¥");
         parser.defineEntityReplacementText("brvbar", "¦");
         parser.defineEntityReplacementText("sect", "§");
         parser.defineEntityReplacementText("uml", "¨");
         parser.defineEntityReplacementText("copy", "©");
         parser.defineEntityReplacementText("ordf", "ª");
         parser.defineEntityReplacementText("laquo", "«");
         parser.defineEntityReplacementText("not", "¬");
         parser.defineEntityReplacementText("shy", "\u00ad");
         parser.defineEntityReplacementText("reg", "®");
         parser.defineEntityReplacementText("macr", "¯");
         parser.defineEntityReplacementText("deg", "°");
         parser.defineEntityReplacementText("plusmn", "±");
         parser.defineEntityReplacementText("sup2", "²");
         parser.defineEntityReplacementText("sup3", "³");
         parser.defineEntityReplacementText("acute", "´");
         parser.defineEntityReplacementText("micro", "µ");
         parser.defineEntityReplacementText("para", "¶");
         parser.defineEntityReplacementText("middot", "·");
         parser.defineEntityReplacementText("cedil", "¸");
         parser.defineEntityReplacementText("sup1", "¹");
         parser.defineEntityReplacementText("ordm", "º");
         parser.defineEntityReplacementText("raquo", "»");
         parser.defineEntityReplacementText("frac14", "¼");
         parser.defineEntityReplacementText("frac12", "½");
         parser.defineEntityReplacementText("frac34", "¾");
         parser.defineEntityReplacementText("iquest", "¿");
         parser.defineEntityReplacementText("Agrave", "À");
         parser.defineEntityReplacementText("Aacute", "Á");
         parser.defineEntityReplacementText("Acirc", "Â");
         parser.defineEntityReplacementText("Atilde", "Ã");
         parser.defineEntityReplacementText("Auml", "Ä");
         parser.defineEntityReplacementText("Aring", "Å");
         parser.defineEntityReplacementText("AElig", "Æ");
         parser.defineEntityReplacementText("Ccedil", "Ç");
         parser.defineEntityReplacementText("Egrave", "È");
         parser.defineEntityReplacementText("Eacute", "É");
         parser.defineEntityReplacementText("Ecirc", "Ê");
         parser.defineEntityReplacementText("Euml", "Ë");
         parser.defineEntityReplacementText("Igrave", "Ì");
         parser.defineEntityReplacementText("Iacute", "Í");
         parser.defineEntityReplacementText("Icirc", "Î");
         parser.defineEntityReplacementText("Iuml", "Ï");
         parser.defineEntityReplacementText("ETH", "Ð");
         parser.defineEntityReplacementText("Ntilde", "Ñ");
         parser.defineEntityReplacementText("Ograve", "Ò");
         parser.defineEntityReplacementText("Oacute", "Ó");
         parser.defineEntityReplacementText("Ocirc", "Ô");
         parser.defineEntityReplacementText("Otilde", "Õ");
         parser.defineEntityReplacementText("Ouml", "Ö");
         parser.defineEntityReplacementText("times", "×");
         parser.defineEntityReplacementText("Oslash", "Ø");
         parser.defineEntityReplacementText("Ugrave", "Ù");
         parser.defineEntityReplacementText("Uacute", "Ú");
         parser.defineEntityReplacementText("Ucirc", "Û");
         parser.defineEntityReplacementText("Uuml", "Ü");
         parser.defineEntityReplacementText("Yacute", "Ý");
         parser.defineEntityReplacementText("THORN", "Þ");
         parser.defineEntityReplacementText("szlig", "ß");
         parser.defineEntityReplacementText("agrave", "à");
         parser.defineEntityReplacementText("aacute", "á");
         parser.defineEntityReplacementText("acirc", "â");
         parser.defineEntityReplacementText("atilde", "ã");
         parser.defineEntityReplacementText("auml", "ä");
         parser.defineEntityReplacementText("aring", "å");
         parser.defineEntityReplacementText("aelig", "æ");
         parser.defineEntityReplacementText("ccedil", "ç");
         parser.defineEntityReplacementText("egrave", "è");
         parser.defineEntityReplacementText("eacute", "é");
         parser.defineEntityReplacementText("ecirc", "ê");
         parser.defineEntityReplacementText("euml", "ë");
         parser.defineEntityReplacementText("igrave", "ì");
         parser.defineEntityReplacementText("iacute", "í");
         parser.defineEntityReplacementText("icirc", "î");
         parser.defineEntityReplacementText("iuml", "ï");
         parser.defineEntityReplacementText("eth", "ð");
         parser.defineEntityReplacementText("ntilde", "ñ");
         parser.defineEntityReplacementText("ograve", "ò");
         parser.defineEntityReplacementText("oacute", "ó");
         parser.defineEntityReplacementText("ocirc", "ô");
         parser.defineEntityReplacementText("otilde", "õ");
         parser.defineEntityReplacementText("ouml", "ö");
         parser.defineEntityReplacementText("divide", "÷");
         parser.defineEntityReplacementText("oslash", "ø");
         parser.defineEntityReplacementText("ugrave", "ù");
         parser.defineEntityReplacementText("uacute", "ú");
         parser.defineEntityReplacementText("ucirc", "û");
         parser.defineEntityReplacementText("uuml", "ü");
         parser.defineEntityReplacementText("yacute", "ý");
         parser.defineEntityReplacementText("thorn", "þ");
         parser.defineEntityReplacementText("yuml", "ÿ");
         parser.defineEntityReplacementText("OElig", "Œ");
         parser.defineEntityReplacementText("oelig", "œ");
         parser.defineEntityReplacementText("Scaron", "Š");
         parser.defineEntityReplacementText("scaron", "š");
         parser.defineEntityReplacementText("Yuml", "Ÿ");
         parser.defineEntityReplacementText("circ", "ˆ");
         parser.defineEntityReplacementText("tilde", "˜");
         parser.defineEntityReplacementText("ensp", " ");
         parser.defineEntityReplacementText("emsp", " ");
         parser.defineEntityReplacementText("thinsp", " ");
         parser.defineEntityReplacementText("zwnj", "\u200c");
         parser.defineEntityReplacementText("zwj", "\u200d");
         parser.defineEntityReplacementText("lrm", "\u200e");
         parser.defineEntityReplacementText("rlm", "\u200f");
         parser.defineEntityReplacementText("ndash", "–");
         parser.defineEntityReplacementText("mdash", "—");
         parser.defineEntityReplacementText("lsquo", "‘");
         parser.defineEntityReplacementText("rsquo", "’");
         parser.defineEntityReplacementText("sbquo", "‚");
         parser.defineEntityReplacementText("ldquo", "“");
         parser.defineEntityReplacementText("rdquo", "”");
         parser.defineEntityReplacementText("bdquo", "„");
         parser.defineEntityReplacementText("dagger", "†");
         parser.defineEntityReplacementText("Dagger", "‡");
         parser.defineEntityReplacementText("permil", "‰");
         parser.defineEntityReplacementText("lsaquo", "‹");
         parser.defineEntityReplacementText("rsaquo", "›");
         parser.defineEntityReplacementText("euro", "€");
         parser.defineEntityReplacementText("fnof", "ƒ");
         parser.defineEntityReplacementText("Alpha", "Α");
         parser.defineEntityReplacementText("Beta", "Β");
         parser.defineEntityReplacementText("Gamma", "Γ");
         parser.defineEntityReplacementText("Delta", "Δ");
         parser.defineEntityReplacementText("Epsilon", "Ε");
         parser.defineEntityReplacementText("Zeta", "Ζ");
         parser.defineEntityReplacementText("Eta", "Η");
         parser.defineEntityReplacementText("Theta", "Θ");
         parser.defineEntityReplacementText("Iota", "Ι");
         parser.defineEntityReplacementText("Kappa", "Κ");
         parser.defineEntityReplacementText("Lambda", "Λ");
         parser.defineEntityReplacementText("Mu", "Μ");
         parser.defineEntityReplacementText("Nu", "Ν");
         parser.defineEntityReplacementText("Xi", "Ξ");
         parser.defineEntityReplacementText("Omicron", "Ο");
         parser.defineEntityReplacementText("Pi", "Π");
         parser.defineEntityReplacementText("Rho", "Ρ");
         parser.defineEntityReplacementText("Sigma", "Σ");
         parser.defineEntityReplacementText("Tau", "Τ");
         parser.defineEntityReplacementText("Upsilon", "Υ");
         parser.defineEntityReplacementText("Phi", "Φ");
         parser.defineEntityReplacementText("Chi", "Χ");
         parser.defineEntityReplacementText("Psi", "Ψ");
         parser.defineEntityReplacementText("Omega", "Ω");
         parser.defineEntityReplacementText("alpha", "α");
         parser.defineEntityReplacementText("beta", "β");
         parser.defineEntityReplacementText("gamma", "γ");
         parser.defineEntityReplacementText("delta", "δ");
         parser.defineEntityReplacementText("epsilon", "ε");
         parser.defineEntityReplacementText("zeta", "ζ");
         parser.defineEntityReplacementText("eta", "η");
         parser.defineEntityReplacementText("theta", "θ");
         parser.defineEntityReplacementText("iota", "ι");
         parser.defineEntityReplacementText("kappa", "κ");
         parser.defineEntityReplacementText("lambda", "λ");
         parser.defineEntityReplacementText("mu", "μ");
         parser.defineEntityReplacementText("nu", "ν");
         parser.defineEntityReplacementText("xi", "ξ");
         parser.defineEntityReplacementText("omicron", "ο");
         parser.defineEntityReplacementText("pi", "π");
         parser.defineEntityReplacementText("rho", "ρ");
         parser.defineEntityReplacementText("sigmaf", "ς");
         parser.defineEntityReplacementText("sigma", "σ");
         parser.defineEntityReplacementText("tau", "τ");
         parser.defineEntityReplacementText("upsilon", "υ");
         parser.defineEntityReplacementText("phi", "φ");
         parser.defineEntityReplacementText("chi", "χ");
         parser.defineEntityReplacementText("psi", "ψ");
         parser.defineEntityReplacementText("omega", "ω");
         parser.defineEntityReplacementText("thetasym", "ϑ");
         parser.defineEntityReplacementText("upsih", "ϒ");
         parser.defineEntityReplacementText("piv", "ϖ");
         parser.defineEntityReplacementText("bull", "•");
         parser.defineEntityReplacementText("hellip", "…");
         parser.defineEntityReplacementText("prime", "′");
         parser.defineEntityReplacementText("Prime", "″");
         parser.defineEntityReplacementText("oline", "‾");
         parser.defineEntityReplacementText("frasl", "⁄");
         parser.defineEntityReplacementText("weierp", "℘");
         parser.defineEntityReplacementText("image", "ℑ");
         parser.defineEntityReplacementText("real", "ℜ");
         parser.defineEntityReplacementText("trade", "™");
         parser.defineEntityReplacementText("alefsym", "ℵ");
         parser.defineEntityReplacementText("larr", "←");
         parser.defineEntityReplacementText("uarr", "↑");
         parser.defineEntityReplacementText("rarr", "→");
         parser.defineEntityReplacementText("darr", "↓");
         parser.defineEntityReplacementText("harr", "↔");
         parser.defineEntityReplacementText("crarr", "↵");
         parser.defineEntityReplacementText("lArr", "⇐");
         parser.defineEntityReplacementText("uArr", "⇑");
         parser.defineEntityReplacementText("rArr", "⇒");
         parser.defineEntityReplacementText("dArr", "⇓");
         parser.defineEntityReplacementText("hArr", "⇔");
         parser.defineEntityReplacementText("forall", "∀");
         parser.defineEntityReplacementText("part", "∂");
         parser.defineEntityReplacementText("exist", "∃");
         parser.defineEntityReplacementText("empty", "∅");
         parser.defineEntityReplacementText("nabla", "∇");
         parser.defineEntityReplacementText("isin", "∈");
         parser.defineEntityReplacementText("notin", "∉");
         parser.defineEntityReplacementText("ni", "∋");
         parser.defineEntityReplacementText("prod", "∏");
         parser.defineEntityReplacementText("sum", "∑");
         parser.defineEntityReplacementText("minus", "−");
         parser.defineEntityReplacementText("lowast", "∗");
         parser.defineEntityReplacementText("radic", "√");
         parser.defineEntityReplacementText("prop", "∝");
         parser.defineEntityReplacementText("infin", "∞");
         parser.defineEntityReplacementText("ang", "∠");
         parser.defineEntityReplacementText("and", "∧");
         parser.defineEntityReplacementText("or", "∨");
         parser.defineEntityReplacementText("cap", "∩");
         parser.defineEntityReplacementText("cup", "∪");
         parser.defineEntityReplacementText("int", "∫");
         parser.defineEntityReplacementText("there4", "∴");
         parser.defineEntityReplacementText("sim", "∼");
         parser.defineEntityReplacementText("cong", "≅");
         parser.defineEntityReplacementText("asymp", "≈");
         parser.defineEntityReplacementText("ne", "≠");
         parser.defineEntityReplacementText("equiv", "≡");
         parser.defineEntityReplacementText("le", "≤");
         parser.defineEntityReplacementText("ge", "≥");
         parser.defineEntityReplacementText("sub", "⊂");
         parser.defineEntityReplacementText("sup", "⊃");
         parser.defineEntityReplacementText("nsub", "⊄");
         parser.defineEntityReplacementText("sube", "⊆");
         parser.defineEntityReplacementText("supe", "⊇");
         parser.defineEntityReplacementText("oplus", "⊕");
         parser.defineEntityReplacementText("otimes", "⊗");
         parser.defineEntityReplacementText("perp", "⊥");
         parser.defineEntityReplacementText("sdot", "⋅");
         parser.defineEntityReplacementText("lceil", "⌈");
         parser.defineEntityReplacementText("rceil", "⌉");
         parser.defineEntityReplacementText("lfloor", "⌊");
         parser.defineEntityReplacementText("rfloor", "⌋");
         parser.defineEntityReplacementText("lang", "〈");
         parser.defineEntityReplacementText("rang", "〉");
         parser.defineEntityReplacementText("loz", "◊");
         parser.defineEntityReplacementText("spades", "♠");
         parser.defineEntityReplacementText("clubs", "♣");
         parser.defineEntityReplacementText("hearts", "♥");
         parser.defineEntityReplacementText("diams", "♦");
      }

   }

   private int nextTag(XmlPullParser parser) throws IOException, XmlPullParserException {
      int eventType = parser.next();
      if (eventType == 4) {
         eventType = parser.next();
      }

      if (eventType != 2 && eventType != 3) {
         throw new XmlPullParserException("expected START_TAG or END_TAG not " + XmlPullParser.TYPES[eventType], parser, (Throwable)null);
      } else {
         return eventType;
      }
   }

   private Activation parseActivation(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Activation activation = new Activation();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      activation.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "activeByDefault", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            activation.setLocation("activeByDefault", _location);
            activation.setActiveByDefault(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "activeByDefault", parser, "false"));
         } else if (this.checkFieldWithDuplicate(parser, "jdk", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            activation.setLocation("jdk", _location);
            activation.setJdk(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "os", (String)null, parsed)) {
            activation.setOs(this.parseActivationOS(parser, strict, source));
         } else if (this.checkFieldWithDuplicate(parser, "property", (String)null, parsed)) {
            activation.setProperty(this.parseActivationProperty(parser, strict, source));
         } else if (this.checkFieldWithDuplicate(parser, "file", (String)null, parsed)) {
            activation.setFile(this.parseActivationFile(parser, strict, source));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return activation;
   }

   private ActivationFile parseActivationFile(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      ActivationFile activationFile = new ActivationFile();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      activationFile.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "missing", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            activationFile.setLocation("missing", _location);
            activationFile.setMissing(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "exists", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            activationFile.setLocation("exists", _location);
            activationFile.setExists(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return activationFile;
   }

   private ActivationOS parseActivationOS(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      ActivationOS activationOS = new ActivationOS();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      activationOS.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "name", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            activationOS.setLocation("name", _location);
            activationOS.setName(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "family", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            activationOS.setLocation("family", _location);
            activationOS.setFamily(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "arch", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            activationOS.setLocation("arch", _location);
            activationOS.setArch(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "version", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            activationOS.setLocation("version", _location);
            activationOS.setVersion(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return activationOS;
   }

   private ActivationProperty parseActivationProperty(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      ActivationProperty activationProperty = new ActivationProperty();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      activationProperty.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "name", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            activationProperty.setLocation("name", _location);
            activationProperty.setName(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "value", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            activationProperty.setLocation("value", _location);
            activationProperty.setValue(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return activationProperty;
   }

   private Build parseBuild(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Build build = new Build();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      build.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "sourceDirectory", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               build.setLocation("sourceDirectory", _location);
               build.setSourceDirectory(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "scriptSourceDirectory", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               build.setLocation("scriptSourceDirectory", _location);
               build.setScriptSourceDirectory(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "testSourceDirectory", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               build.setLocation("testSourceDirectory", _location);
               build.setTestSourceDirectory(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "outputDirectory", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               build.setLocation("outputDirectory", _location);
               build.setOutputDirectory(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "testOutputDirectory", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               build.setLocation("testOutputDirectory", _location);
               build.setTestOutputDirectory(this.getTrimmedValue(parser.nextText()));
            } else {
               ArrayList plugins;
               if (this.checkFieldWithDuplicate(parser, "extensions", (String)null, parsed)) {
                  plugins = new ArrayList();
                  build.setExtensions(plugins);

                  while(parser.nextTag() == 2) {
                     if ("extension".equals(parser.getName())) {
                        plugins.add(this.parseExtension(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "defaultGoal", (String)null, parsed)) {
                  _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  build.setLocation("defaultGoal", _location);
                  build.setDefaultGoal(this.getTrimmedValue(parser.nextText()));
               } else if (this.checkFieldWithDuplicate(parser, "resources", (String)null, parsed)) {
                  plugins = new ArrayList();
                  build.setResources(plugins);

                  while(parser.nextTag() == 2) {
                     if ("resource".equals(parser.getName())) {
                        plugins.add(this.parseResource(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "testResources", (String)null, parsed)) {
                  plugins = new ArrayList();
                  build.setTestResources(plugins);

                  while(parser.nextTag() == 2) {
                     if ("testResource".equals(parser.getName())) {
                        plugins.add(this.parseResource(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "directory", (String)null, parsed)) {
                  _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  build.setLocation("directory", _location);
                  build.setDirectory(this.getTrimmedValue(parser.nextText()));
               } else if (this.checkFieldWithDuplicate(parser, "finalName", (String)null, parsed)) {
                  _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  build.setLocation("finalName", _location);
                  build.setFinalName(this.getTrimmedValue(parser.nextText()));
               } else if (this.checkFieldWithDuplicate(parser, "filters", (String)null, parsed)) {
                  plugins = new ArrayList();
                  build.setFilters(plugins);
                  InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  build.setLocation("filters", _locations);

                  while(parser.nextTag() == 2) {
                     if ("filter".equals(parser.getName())) {
                        _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                        _locations.setLocation(plugins.size(), _location);
                        plugins.add(this.getTrimmedValue(parser.nextText()));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "pluginManagement", (String)null, parsed)) {
                  build.setPluginManagement(this.parsePluginManagement(parser, strict, source));
               } else if (this.checkFieldWithDuplicate(parser, "plugins", (String)null, parsed)) {
                  plugins = new ArrayList();
                  build.setPlugins(plugins);

                  while(parser.nextTag() == 2) {
                     if ("plugin".equals(parser.getName())) {
                        plugins.add(this.parsePlugin(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else {
                  this.checkUnknownElement(parser, strict);
               }
            }
         }

         return build;
      }
   }

   private BuildBase parseBuildBase(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      BuildBase buildBase = new BuildBase();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      buildBase.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "defaultGoal", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               buildBase.setLocation("defaultGoal", _location);
               buildBase.setDefaultGoal(this.getTrimmedValue(parser.nextText()));
            } else {
               ArrayList plugins;
               if (this.checkFieldWithDuplicate(parser, "resources", (String)null, parsed)) {
                  plugins = new ArrayList();
                  buildBase.setResources(plugins);

                  while(parser.nextTag() == 2) {
                     if ("resource".equals(parser.getName())) {
                        plugins.add(this.parseResource(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "testResources", (String)null, parsed)) {
                  plugins = new ArrayList();
                  buildBase.setTestResources(plugins);

                  while(parser.nextTag() == 2) {
                     if ("testResource".equals(parser.getName())) {
                        plugins.add(this.parseResource(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "directory", (String)null, parsed)) {
                  _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  buildBase.setLocation("directory", _location);
                  buildBase.setDirectory(this.getTrimmedValue(parser.nextText()));
               } else if (this.checkFieldWithDuplicate(parser, "finalName", (String)null, parsed)) {
                  _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  buildBase.setLocation("finalName", _location);
                  buildBase.setFinalName(this.getTrimmedValue(parser.nextText()));
               } else if (this.checkFieldWithDuplicate(parser, "filters", (String)null, parsed)) {
                  plugins = new ArrayList();
                  buildBase.setFilters(plugins);
                  InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  buildBase.setLocation("filters", _locations);

                  while(parser.nextTag() == 2) {
                     if ("filter".equals(parser.getName())) {
                        _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                        _locations.setLocation(plugins.size(), _location);
                        plugins.add(this.getTrimmedValue(parser.nextText()));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "pluginManagement", (String)null, parsed)) {
                  buildBase.setPluginManagement(this.parsePluginManagement(parser, strict, source));
               } else if (this.checkFieldWithDuplicate(parser, "plugins", (String)null, parsed)) {
                  plugins = new ArrayList();
                  buildBase.setPlugins(plugins);

                  while(parser.nextTag() == 2) {
                     if ("plugin".equals(parser.getName())) {
                        plugins.add(this.parsePlugin(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else {
                  this.checkUnknownElement(parser, strict);
               }
            }
         }

         return buildBase;
      }
   }

   private CiManagement parseCiManagement(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      CiManagement ciManagement = new CiManagement();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      ciManagement.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "system", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               ciManagement.setLocation("system", _location);
               ciManagement.setSystem(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "url", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               ciManagement.setLocation("url", _location);
               ciManagement.setUrl(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "notifiers", (String)null, parsed)) {
               List notifiers = new ArrayList();
               ciManagement.setNotifiers(notifiers);

               while(parser.nextTag() == 2) {
                  if ("notifier".equals(parser.getName())) {
                     notifiers.add(this.parseNotifier(parser, strict, source));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return ciManagement;
      }
   }

   private ConfigurationContainer parseConfigurationContainer(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      ConfigurationContainer configurationContainer = new ConfigurationContainer();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      configurationContainer.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "inherited", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            configurationContainer.setLocation("inherited", _location);
            configurationContainer.setInherited(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "configuration", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            configurationContainer.setLocation("configuration", _location);
            configurationContainer.setConfiguration(Xpp3DomBuilder.build(parser));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return configurationContainer;
   }

   private Contributor parseContributor(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Contributor contributor = new Contributor();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      contributor.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "name", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               contributor.setLocation("name", _location);
               contributor.setName(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "email", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               contributor.setLocation("email", _location);
               contributor.setEmail(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "url", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               contributor.setLocation("url", _location);
               contributor.setUrl(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               contributor.setLocation("organization", _location);
               contributor.setOrganization(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "organizationUrl", "organisationUrl", parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               contributor.setLocation("organizationUrl", _location);
               contributor.setOrganizationUrl(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "roles", (String)null, parsed)) {
               List roles = new ArrayList();
               contributor.setRoles(roles);
               InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               contributor.setLocation("roles", _locations);

               while(parser.nextTag() == 2) {
                  if ("role".equals(parser.getName())) {
                     _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                     _locations.setLocation(roles.size(), _location);
                     roles.add(this.getTrimmedValue(parser.nextText()));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else if (this.checkFieldWithDuplicate(parser, "timezone", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               contributor.setLocation("timezone", _location);
               contributor.setTimezone(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "properties", (String)null, parsed)) {
               InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               contributor.setLocation("properties", _locations);

               while(parser.nextTag() == 2) {
                  String key = parser.getName();
                  _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  _locations.setLocation(key, _location);
                  String value = parser.nextText().trim();
                  contributor.addProperty(key, value);
               }
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return contributor;
      }
   }

   private Dependency parseDependency(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Dependency dependency = new Dependency();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      dependency.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "groupId", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               dependency.setLocation("groupId", _location);
               dependency.setGroupId(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "artifactId", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               dependency.setLocation("artifactId", _location);
               dependency.setArtifactId(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "version", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               dependency.setLocation("version", _location);
               dependency.setVersion(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "type", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               dependency.setLocation("type", _location);
               dependency.setType(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "classifier", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               dependency.setLocation("classifier", _location);
               dependency.setClassifier(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "scope", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               dependency.setLocation("scope", _location);
               dependency.setScope(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "systemPath", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               dependency.setLocation("systemPath", _location);
               dependency.setSystemPath(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "exclusions", (String)null, parsed)) {
               List exclusions = new ArrayList();
               dependency.setExclusions(exclusions);

               while(parser.nextTag() == 2) {
                  if ("exclusion".equals(parser.getName())) {
                     exclusions.add(this.parseExclusion(parser, strict, source));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else if (this.checkFieldWithDuplicate(parser, "optional", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               dependency.setLocation("optional", _location);
               dependency.setOptional(this.getTrimmedValue(parser.nextText()));
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return dependency;
      }
   }

   private DependencyManagement parseDependencyManagement(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      DependencyManagement dependencyManagement = new DependencyManagement();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      dependencyManagement.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "dependencies", (String)null, parsed)) {
               List dependencies = new ArrayList();
               dependencyManagement.setDependencies(dependencies);

               while(parser.nextTag() == 2) {
                  if ("dependency".equals(parser.getName())) {
                     dependencies.add(this.parseDependency(parser, strict, source));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return dependencyManagement;
      }
   }

   private DeploymentRepository parseDeploymentRepository(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      DeploymentRepository deploymentRepository = new DeploymentRepository();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      deploymentRepository.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "uniqueVersion", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            deploymentRepository.setLocation("uniqueVersion", _location);
            deploymentRepository.setUniqueVersion(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "uniqueVersion", parser, "true"));
         } else if (this.checkFieldWithDuplicate(parser, "releases", (String)null, parsed)) {
            deploymentRepository.setReleases(this.parseRepositoryPolicy(parser, strict, source));
         } else if (this.checkFieldWithDuplicate(parser, "snapshots", (String)null, parsed)) {
            deploymentRepository.setSnapshots(this.parseRepositoryPolicy(parser, strict, source));
         } else if (this.checkFieldWithDuplicate(parser, "id", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            deploymentRepository.setLocation("id", _location);
            deploymentRepository.setId(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "name", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            deploymentRepository.setLocation("name", _location);
            deploymentRepository.setName(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "url", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            deploymentRepository.setLocation("url", _location);
            deploymentRepository.setUrl(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "layout", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            deploymentRepository.setLocation("layout", _location);
            deploymentRepository.setLayout(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return deploymentRepository;
   }

   private Developer parseDeveloper(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Developer developer = new Developer();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      developer.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "id", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               developer.setLocation("id", _location);
               developer.setId(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "name", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               developer.setLocation("name", _location);
               developer.setName(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "email", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               developer.setLocation("email", _location);
               developer.setEmail(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "url", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               developer.setLocation("url", _location);
               developer.setUrl(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               developer.setLocation("organization", _location);
               developer.setOrganization(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "organizationUrl", "organisationUrl", parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               developer.setLocation("organizationUrl", _location);
               developer.setOrganizationUrl(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "roles", (String)null, parsed)) {
               List roles = new ArrayList();
               developer.setRoles(roles);
               InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               developer.setLocation("roles", _locations);

               while(parser.nextTag() == 2) {
                  if ("role".equals(parser.getName())) {
                     _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                     _locations.setLocation(roles.size(), _location);
                     roles.add(this.getTrimmedValue(parser.nextText()));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else if (this.checkFieldWithDuplicate(parser, "timezone", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               developer.setLocation("timezone", _location);
               developer.setTimezone(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "properties", (String)null, parsed)) {
               InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               developer.setLocation("properties", _locations);

               while(parser.nextTag() == 2) {
                  String key = parser.getName();
                  _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  _locations.setLocation(key, _location);
                  String value = parser.nextText().trim();
                  developer.addProperty(key, value);
               }
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return developer;
      }
   }

   private DistributionManagement parseDistributionManagement(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      DistributionManagement distributionManagement = new DistributionManagement();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      distributionManagement.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "repository", (String)null, parsed)) {
            distributionManagement.setRepository(this.parseDeploymentRepository(parser, strict, source));
         } else if (this.checkFieldWithDuplicate(parser, "snapshotRepository", (String)null, parsed)) {
            distributionManagement.setSnapshotRepository(this.parseDeploymentRepository(parser, strict, source));
         } else if (this.checkFieldWithDuplicate(parser, "site", (String)null, parsed)) {
            distributionManagement.setSite(this.parseSite(parser, strict, source));
         } else if (this.checkFieldWithDuplicate(parser, "downloadUrl", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            distributionManagement.setLocation("downloadUrl", _location);
            distributionManagement.setDownloadUrl(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "relocation", (String)null, parsed)) {
            distributionManagement.setRelocation(this.parseRelocation(parser, strict, source));
         } else if (this.checkFieldWithDuplicate(parser, "status", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            distributionManagement.setLocation("status", _location);
            distributionManagement.setStatus(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return distributionManagement;
   }

   private Exclusion parseExclusion(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Exclusion exclusion = new Exclusion();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      exclusion.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "artifactId", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            exclusion.setLocation("artifactId", _location);
            exclusion.setArtifactId(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "groupId", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            exclusion.setLocation("groupId", _location);
            exclusion.setGroupId(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return exclusion;
   }

   private Extension parseExtension(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Extension extension = new Extension();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      extension.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "groupId", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            extension.setLocation("groupId", _location);
            extension.setGroupId(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "artifactId", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            extension.setLocation("artifactId", _location);
            extension.setArtifactId(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "version", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            extension.setLocation("version", _location);
            extension.setVersion(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return extension;
   }

   private FileSet parseFileSet(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      FileSet fileSet = new FileSet();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      fileSet.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "directory", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               fileSet.setLocation("directory", _location);
               fileSet.setDirectory(this.getTrimmedValue(parser.nextText()));
            } else {
               InputLocation _locations;
               ArrayList excludes;
               if (this.checkFieldWithDuplicate(parser, "includes", (String)null, parsed)) {
                  excludes = new ArrayList();
                  fileSet.setIncludes(excludes);
                  _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  fileSet.setLocation("includes", _locations);

                  while(parser.nextTag() == 2) {
                     if ("include".equals(parser.getName())) {
                        _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                        _locations.setLocation(excludes.size(), _location);
                        excludes.add(this.getTrimmedValue(parser.nextText()));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "excludes", (String)null, parsed)) {
                  excludes = new ArrayList();
                  fileSet.setExcludes(excludes);
                  _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  fileSet.setLocation("excludes", _locations);

                  while(parser.nextTag() == 2) {
                     if ("exclude".equals(parser.getName())) {
                        _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                        _locations.setLocation(excludes.size(), _location);
                        excludes.add(this.getTrimmedValue(parser.nextText()));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else {
                  this.checkUnknownElement(parser, strict);
               }
            }
         }

         return fileSet;
      }
   }

   private IssueManagement parseIssueManagement(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      IssueManagement issueManagement = new IssueManagement();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      issueManagement.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "system", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            issueManagement.setLocation("system", _location);
            issueManagement.setSystem(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "url", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            issueManagement.setLocation("url", _location);
            issueManagement.setUrl(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return issueManagement;
   }

   private License parseLicense(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      License license = new License();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      license.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "name", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            license.setLocation("name", _location);
            license.setName(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "url", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            license.setLocation("url", _location);
            license.setUrl(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "distribution", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            license.setLocation("distribution", _location);
            license.setDistribution(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "comments", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            license.setLocation("comments", _location);
            license.setComments(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return license;
   }

   private MailingList parseMailingList(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      MailingList mailingList = new MailingList();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      mailingList.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "name", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               mailingList.setLocation("name", _location);
               mailingList.setName(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "subscribe", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               mailingList.setLocation("subscribe", _location);
               mailingList.setSubscribe(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "unsubscribe", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               mailingList.setLocation("unsubscribe", _location);
               mailingList.setUnsubscribe(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "post", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               mailingList.setLocation("post", _location);
               mailingList.setPost(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "archive", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               mailingList.setLocation("archive", _location);
               mailingList.setArchive(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "otherArchives", (String)null, parsed)) {
               List otherArchives = new ArrayList();
               mailingList.setOtherArchives(otherArchives);
               InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               mailingList.setLocation("otherArchives", _locations);

               while(parser.nextTag() == 2) {
                  if ("otherArchive".equals(parser.getName())) {
                     _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                     _locations.setLocation(otherArchives.size(), _location);
                     otherArchives.add(this.getTrimmedValue(parser.nextText()));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return mailingList;
      }
   }

   private Model parseModel(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Model model = new Model();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      model.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0 && !"xmlns".equals(name)) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "modelVersion", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               model.setLocation("modelVersion", _location);
               model.setModelVersion(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "parent", (String)null, parsed)) {
               model.setParent(this.parseParent(parser, strict, source));
            } else if (this.checkFieldWithDuplicate(parser, "groupId", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               model.setLocation("groupId", _location);
               model.setGroupId(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "artifactId", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               model.setLocation("artifactId", _location);
               model.setArtifactId(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "version", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               model.setLocation("version", _location);
               model.setVersion(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "packaging", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               model.setLocation("packaging", _location);
               model.setPackaging(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "name", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               model.setLocation("name", _location);
               model.setName(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "description", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               model.setLocation("description", _location);
               model.setDescription(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "url", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               model.setLocation("url", _location);
               model.setUrl(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "inceptionYear", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               model.setLocation("inceptionYear", _location);
               model.setInceptionYear(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
               model.setOrganization(this.parseOrganization(parser, strict, source));
            } else {
               ArrayList profiles;
               if (this.checkFieldWithDuplicate(parser, "licenses", (String)null, parsed)) {
                  profiles = new ArrayList();
                  model.setLicenses(profiles);

                  while(parser.nextTag() == 2) {
                     if ("license".equals(parser.getName())) {
                        profiles.add(this.parseLicense(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "developers", (String)null, parsed)) {
                  profiles = new ArrayList();
                  model.setDevelopers(profiles);

                  while(parser.nextTag() == 2) {
                     if ("developer".equals(parser.getName())) {
                        profiles.add(this.parseDeveloper(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "contributors", (String)null, parsed)) {
                  profiles = new ArrayList();
                  model.setContributors(profiles);

                  while(parser.nextTag() == 2) {
                     if ("contributor".equals(parser.getName())) {
                        profiles.add(this.parseContributor(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "mailingLists", (String)null, parsed)) {
                  profiles = new ArrayList();
                  model.setMailingLists(profiles);

                  while(parser.nextTag() == 2) {
                     if ("mailingList".equals(parser.getName())) {
                        profiles.add(this.parseMailingList(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "prerequisites", (String)null, parsed)) {
                  model.setPrerequisites(this.parsePrerequisites(parser, strict, source));
               } else if (this.checkFieldWithDuplicate(parser, "modules", (String)null, parsed)) {
                  profiles = new ArrayList();
                  model.setModules(profiles);
                  InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  model.setLocation("modules", _locations);

                  while(parser.nextTag() == 2) {
                     if ("module".equals(parser.getName())) {
                        _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                        _locations.setLocation(profiles.size(), _location);
                        profiles.add(this.getTrimmedValue(parser.nextText()));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "scm", (String)null, parsed)) {
                  model.setScm(this.parseScm(parser, strict, source));
               } else if (this.checkFieldWithDuplicate(parser, "issueManagement", (String)null, parsed)) {
                  model.setIssueManagement(this.parseIssueManagement(parser, strict, source));
               } else if (this.checkFieldWithDuplicate(parser, "ciManagement", (String)null, parsed)) {
                  model.setCiManagement(this.parseCiManagement(parser, strict, source));
               } else if (this.checkFieldWithDuplicate(parser, "distributionManagement", (String)null, parsed)) {
                  model.setDistributionManagement(this.parseDistributionManagement(parser, strict, source));
               } else if (this.checkFieldWithDuplicate(parser, "properties", (String)null, parsed)) {
                  InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  model.setLocation("properties", _locations);

                  while(parser.nextTag() == 2) {
                     String key = parser.getName();
                     _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                     _locations.setLocation(key, _location);
                     String value = parser.nextText().trim();
                     model.addProperty(key, value);
                  }
               } else if (this.checkFieldWithDuplicate(parser, "dependencyManagement", (String)null, parsed)) {
                  model.setDependencyManagement(this.parseDependencyManagement(parser, strict, source));
               } else if (this.checkFieldWithDuplicate(parser, "dependencies", (String)null, parsed)) {
                  profiles = new ArrayList();
                  model.setDependencies(profiles);

                  while(parser.nextTag() == 2) {
                     if ("dependency".equals(parser.getName())) {
                        profiles.add(this.parseDependency(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "repositories", (String)null, parsed)) {
                  profiles = new ArrayList();
                  model.setRepositories(profiles);

                  while(parser.nextTag() == 2) {
                     if ("repository".equals(parser.getName())) {
                        profiles.add(this.parseRepository(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "pluginRepositories", (String)null, parsed)) {
                  profiles = new ArrayList();
                  model.setPluginRepositories(profiles);

                  while(parser.nextTag() == 2) {
                     if ("pluginRepository".equals(parser.getName())) {
                        profiles.add(this.parseRepository(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "build", (String)null, parsed)) {
                  model.setBuild(this.parseBuild(parser, strict, source));
               } else if (this.checkFieldWithDuplicate(parser, "reports", (String)null, parsed)) {
                  _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  model.setLocation("reports", _location);
                  model.setReports(Xpp3DomBuilder.build(parser));
               } else if (this.checkFieldWithDuplicate(parser, "reporting", (String)null, parsed)) {
                  model.setReporting(this.parseReporting(parser, strict, source));
               } else if (this.checkFieldWithDuplicate(parser, "profiles", (String)null, parsed)) {
                  profiles = new ArrayList();
                  model.setProfiles(profiles);

                  while(parser.nextTag() == 2) {
                     if ("profile".equals(parser.getName())) {
                        profiles.add(this.parseProfile(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else {
                  this.checkUnknownElement(parser, strict);
               }
            }
         }

         return model;
      }
   }

   private ModelBase parseModelBase(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      ModelBase modelBase = new ModelBase();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      modelBase.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            ArrayList pluginRepositories;
            if (this.checkFieldWithDuplicate(parser, "modules", (String)null, parsed)) {
               pluginRepositories = new ArrayList();
               modelBase.setModules(pluginRepositories);
               InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               modelBase.setLocation("modules", _locations);

               while(parser.nextTag() == 2) {
                  if ("module".equals(parser.getName())) {
                     _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                     _locations.setLocation(pluginRepositories.size(), _location);
                     pluginRepositories.add(this.getTrimmedValue(parser.nextText()));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else if (this.checkFieldWithDuplicate(parser, "distributionManagement", (String)null, parsed)) {
               modelBase.setDistributionManagement(this.parseDistributionManagement(parser, strict, source));
            } else if (this.checkFieldWithDuplicate(parser, "properties", (String)null, parsed)) {
               InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               modelBase.setLocation("properties", _locations);

               while(parser.nextTag() == 2) {
                  String key = parser.getName();
                  _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  _locations.setLocation(key, _location);
                  String value = parser.nextText().trim();
                  modelBase.addProperty(key, value);
               }
            } else if (this.checkFieldWithDuplicate(parser, "dependencyManagement", (String)null, parsed)) {
               modelBase.setDependencyManagement(this.parseDependencyManagement(parser, strict, source));
            } else if (this.checkFieldWithDuplicate(parser, "dependencies", (String)null, parsed)) {
               pluginRepositories = new ArrayList();
               modelBase.setDependencies(pluginRepositories);

               while(parser.nextTag() == 2) {
                  if ("dependency".equals(parser.getName())) {
                     pluginRepositories.add(this.parseDependency(parser, strict, source));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else if (this.checkFieldWithDuplicate(parser, "repositories", (String)null, parsed)) {
               pluginRepositories = new ArrayList();
               modelBase.setRepositories(pluginRepositories);

               while(parser.nextTag() == 2) {
                  if ("repository".equals(parser.getName())) {
                     pluginRepositories.add(this.parseRepository(parser, strict, source));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else if (this.checkFieldWithDuplicate(parser, "pluginRepositories", (String)null, parsed)) {
               pluginRepositories = new ArrayList();
               modelBase.setPluginRepositories(pluginRepositories);

               while(parser.nextTag() == 2) {
                  if ("pluginRepository".equals(parser.getName())) {
                     pluginRepositories.add(this.parseRepository(parser, strict, source));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else if (this.checkFieldWithDuplicate(parser, "reports", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               modelBase.setLocation("reports", _location);
               modelBase.setReports(Xpp3DomBuilder.build(parser));
            } else if (this.checkFieldWithDuplicate(parser, "reporting", (String)null, parsed)) {
               modelBase.setReporting(this.parseReporting(parser, strict, source));
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return modelBase;
      }
   }

   private Notifier parseNotifier(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Notifier notifier = new Notifier();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      notifier.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "type", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               notifier.setLocation("type", _location);
               notifier.setType(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "sendOnError", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               notifier.setLocation("sendOnError", _location);
               notifier.setSendOnError(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "sendOnError", parser, "true"));
            } else if (this.checkFieldWithDuplicate(parser, "sendOnFailure", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               notifier.setLocation("sendOnFailure", _location);
               notifier.setSendOnFailure(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "sendOnFailure", parser, "true"));
            } else if (this.checkFieldWithDuplicate(parser, "sendOnSuccess", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               notifier.setLocation("sendOnSuccess", _location);
               notifier.setSendOnSuccess(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "sendOnSuccess", parser, "true"));
            } else if (this.checkFieldWithDuplicate(parser, "sendOnWarning", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               notifier.setLocation("sendOnWarning", _location);
               notifier.setSendOnWarning(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "sendOnWarning", parser, "true"));
            } else if (this.checkFieldWithDuplicate(parser, "address", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               notifier.setLocation("address", _location);
               notifier.setAddress(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "configuration", (String)null, parsed)) {
               InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               notifier.setLocation("configuration", _locations);

               while(parser.nextTag() == 2) {
                  String key = parser.getName();
                  _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  _locations.setLocation(key, _location);
                  String value = parser.nextText().trim();
                  notifier.addConfiguration(key, value);
               }
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return notifier;
      }
   }

   private Organization parseOrganization(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Organization organization = new Organization();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      organization.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "name", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            organization.setLocation("name", _location);
            organization.setName(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "url", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            organization.setLocation("url", _location);
            organization.setUrl(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return organization;
   }

   private Parent parseParent(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Parent parent = new Parent();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      parent.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "artifactId", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            parent.setLocation("artifactId", _location);
            parent.setArtifactId(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "groupId", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            parent.setLocation("groupId", _location);
            parent.setGroupId(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "version", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            parent.setLocation("version", _location);
            parent.setVersion(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "relativePath", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            parent.setLocation("relativePath", _location);
            parent.setRelativePath(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return parent;
   }

   private PatternSet parsePatternSet(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      PatternSet patternSet = new PatternSet();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      patternSet.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            InputLocation _locations;
            ArrayList excludes;
            if (this.checkFieldWithDuplicate(parser, "includes", (String)null, parsed)) {
               excludes = new ArrayList();
               patternSet.setIncludes(excludes);
               _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               patternSet.setLocation("includes", _locations);

               while(parser.nextTag() == 2) {
                  if ("include".equals(parser.getName())) {
                     _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                     _locations.setLocation(excludes.size(), _location);
                     excludes.add(this.getTrimmedValue(parser.nextText()));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else if (this.checkFieldWithDuplicate(parser, "excludes", (String)null, parsed)) {
               excludes = new ArrayList();
               patternSet.setExcludes(excludes);
               _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               patternSet.setLocation("excludes", _locations);

               while(parser.nextTag() == 2) {
                  if ("exclude".equals(parser.getName())) {
                     _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                     _locations.setLocation(excludes.size(), _location);
                     excludes.add(this.getTrimmedValue(parser.nextText()));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return patternSet;
      }
   }

   private Plugin parsePlugin(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Plugin plugin = new Plugin();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      plugin.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "groupId", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               plugin.setLocation("groupId", _location);
               plugin.setGroupId(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "artifactId", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               plugin.setLocation("artifactId", _location);
               plugin.setArtifactId(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "version", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               plugin.setLocation("version", _location);
               plugin.setVersion(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "extensions", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               plugin.setLocation("extensions", _location);
               plugin.setExtensions(this.getTrimmedValue(parser.nextText()));
            } else {
               ArrayList dependencies;
               if (this.checkFieldWithDuplicate(parser, "executions", (String)null, parsed)) {
                  dependencies = new ArrayList();
                  plugin.setExecutions(dependencies);

                  while(parser.nextTag() == 2) {
                     if ("execution".equals(parser.getName())) {
                        dependencies.add(this.parsePluginExecution(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "dependencies", (String)null, parsed)) {
                  dependencies = new ArrayList();
                  plugin.setDependencies(dependencies);

                  while(parser.nextTag() == 2) {
                     if ("dependency".equals(parser.getName())) {
                        dependencies.add(this.parseDependency(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "goals", (String)null, parsed)) {
                  _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  plugin.setLocation("goals", _location);
                  plugin.setGoals(Xpp3DomBuilder.build(parser));
               } else if (this.checkFieldWithDuplicate(parser, "inherited", (String)null, parsed)) {
                  _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  plugin.setLocation("inherited", _location);
                  plugin.setInherited(this.getTrimmedValue(parser.nextText()));
               } else if (this.checkFieldWithDuplicate(parser, "configuration", (String)null, parsed)) {
                  _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  plugin.setLocation("configuration", _location);
                  plugin.setConfiguration(Xpp3DomBuilder.build(parser));
               } else {
                  this.checkUnknownElement(parser, strict);
               }
            }
         }

         return plugin;
      }
   }

   private PluginConfiguration parsePluginConfiguration(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      PluginConfiguration pluginConfiguration = new PluginConfiguration();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      pluginConfiguration.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "pluginManagement", (String)null, parsed)) {
               pluginConfiguration.setPluginManagement(this.parsePluginManagement(parser, strict, source));
            } else if (this.checkFieldWithDuplicate(parser, "plugins", (String)null, parsed)) {
               List plugins = new ArrayList();
               pluginConfiguration.setPlugins(plugins);

               while(parser.nextTag() == 2) {
                  if ("plugin".equals(parser.getName())) {
                     plugins.add(this.parsePlugin(parser, strict, source));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return pluginConfiguration;
      }
   }

   private PluginContainer parsePluginContainer(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      PluginContainer pluginContainer = new PluginContainer();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      pluginContainer.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "plugins", (String)null, parsed)) {
               List plugins = new ArrayList();
               pluginContainer.setPlugins(plugins);

               while(parser.nextTag() == 2) {
                  if ("plugin".equals(parser.getName())) {
                     plugins.add(this.parsePlugin(parser, strict, source));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return pluginContainer;
      }
   }

   private PluginExecution parsePluginExecution(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      PluginExecution pluginExecution = new PluginExecution();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      pluginExecution.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "id", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               pluginExecution.setLocation("id", _location);
               pluginExecution.setId(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "phase", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               pluginExecution.setLocation("phase", _location);
               pluginExecution.setPhase(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "goals", (String)null, parsed)) {
               List goals = new ArrayList();
               pluginExecution.setGoals(goals);
               InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               pluginExecution.setLocation("goals", _locations);

               while(parser.nextTag() == 2) {
                  if ("goal".equals(parser.getName())) {
                     _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                     _locations.setLocation(goals.size(), _location);
                     goals.add(this.getTrimmedValue(parser.nextText()));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else if (this.checkFieldWithDuplicate(parser, "inherited", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               pluginExecution.setLocation("inherited", _location);
               pluginExecution.setInherited(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "configuration", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               pluginExecution.setLocation("configuration", _location);
               pluginExecution.setConfiguration(Xpp3DomBuilder.build(parser));
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return pluginExecution;
      }
   }

   private PluginManagement parsePluginManagement(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      PluginManagement pluginManagement = new PluginManagement();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      pluginManagement.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "plugins", (String)null, parsed)) {
               List plugins = new ArrayList();
               pluginManagement.setPlugins(plugins);

               while(parser.nextTag() == 2) {
                  if ("plugin".equals(parser.getName())) {
                     plugins.add(this.parsePlugin(parser, strict, source));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return pluginManagement;
      }
   }

   private Prerequisites parsePrerequisites(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Prerequisites prerequisites = new Prerequisites();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      prerequisites.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "maven", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            prerequisites.setLocation("maven", _location);
            prerequisites.setMaven(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return prerequisites;
   }

   private Profile parseProfile(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Profile profile = new Profile();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      profile.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "id", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               profile.setLocation("id", _location);
               profile.setId(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "activation", (String)null, parsed)) {
               profile.setActivation(this.parseActivation(parser, strict, source));
            } else if (this.checkFieldWithDuplicate(parser, "build", (String)null, parsed)) {
               profile.setBuild(this.parseBuildBase(parser, strict, source));
            } else {
               ArrayList pluginRepositories;
               if (this.checkFieldWithDuplicate(parser, "modules", (String)null, parsed)) {
                  pluginRepositories = new ArrayList();
                  profile.setModules(pluginRepositories);
                  InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  profile.setLocation("modules", _locations);

                  while(parser.nextTag() == 2) {
                     if ("module".equals(parser.getName())) {
                        _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                        _locations.setLocation(pluginRepositories.size(), _location);
                        pluginRepositories.add(this.getTrimmedValue(parser.nextText()));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "distributionManagement", (String)null, parsed)) {
                  profile.setDistributionManagement(this.parseDistributionManagement(parser, strict, source));
               } else if (this.checkFieldWithDuplicate(parser, "properties", (String)null, parsed)) {
                  InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  profile.setLocation("properties", _locations);

                  while(parser.nextTag() == 2) {
                     String key = parser.getName();
                     _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                     _locations.setLocation(key, _location);
                     String value = parser.nextText().trim();
                     profile.addProperty(key, value);
                  }
               } else if (this.checkFieldWithDuplicate(parser, "dependencyManagement", (String)null, parsed)) {
                  profile.setDependencyManagement(this.parseDependencyManagement(parser, strict, source));
               } else if (this.checkFieldWithDuplicate(parser, "dependencies", (String)null, parsed)) {
                  pluginRepositories = new ArrayList();
                  profile.setDependencies(pluginRepositories);

                  while(parser.nextTag() == 2) {
                     if ("dependency".equals(parser.getName())) {
                        pluginRepositories.add(this.parseDependency(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "repositories", (String)null, parsed)) {
                  pluginRepositories = new ArrayList();
                  profile.setRepositories(pluginRepositories);

                  while(parser.nextTag() == 2) {
                     if ("repository".equals(parser.getName())) {
                        pluginRepositories.add(this.parseRepository(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "pluginRepositories", (String)null, parsed)) {
                  pluginRepositories = new ArrayList();
                  profile.setPluginRepositories(pluginRepositories);

                  while(parser.nextTag() == 2) {
                     if ("pluginRepository".equals(parser.getName())) {
                        pluginRepositories.add(this.parseRepository(parser, strict, source));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "reports", (String)null, parsed)) {
                  _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  profile.setLocation("reports", _location);
                  profile.setReports(Xpp3DomBuilder.build(parser));
               } else if (this.checkFieldWithDuplicate(parser, "reporting", (String)null, parsed)) {
                  profile.setReporting(this.parseReporting(parser, strict, source));
               } else {
                  this.checkUnknownElement(parser, strict);
               }
            }
         }

         return profile;
      }
   }

   private Relocation parseRelocation(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Relocation relocation = new Relocation();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      relocation.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "groupId", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            relocation.setLocation("groupId", _location);
            relocation.setGroupId(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "artifactId", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            relocation.setLocation("artifactId", _location);
            relocation.setArtifactId(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "version", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            relocation.setLocation("version", _location);
            relocation.setVersion(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "message", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            relocation.setLocation("message", _location);
            relocation.setMessage(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return relocation;
   }

   private ReportPlugin parseReportPlugin(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      ReportPlugin reportPlugin = new ReportPlugin();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      reportPlugin.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "groupId", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               reportPlugin.setLocation("groupId", _location);
               reportPlugin.setGroupId(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "artifactId", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               reportPlugin.setLocation("artifactId", _location);
               reportPlugin.setArtifactId(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "version", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               reportPlugin.setLocation("version", _location);
               reportPlugin.setVersion(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "inherited", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               reportPlugin.setLocation("inherited", _location);
               reportPlugin.setInherited(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "configuration", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               reportPlugin.setLocation("configuration", _location);
               reportPlugin.setConfiguration(Xpp3DomBuilder.build(parser));
            } else if (this.checkFieldWithDuplicate(parser, "reportSets", (String)null, parsed)) {
               List reportSets = new ArrayList();
               reportPlugin.setReportSets(reportSets);

               while(parser.nextTag() == 2) {
                  if ("reportSet".equals(parser.getName())) {
                     reportSets.add(this.parseReportSet(parser, strict, source));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return reportPlugin;
      }
   }

   private ReportSet parseReportSet(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      ReportSet reportSet = new ReportSet();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      reportSet.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "id", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               reportSet.setLocation("id", _location);
               reportSet.setId(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "configuration", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               reportSet.setLocation("configuration", _location);
               reportSet.setConfiguration(Xpp3DomBuilder.build(parser));
            } else if (this.checkFieldWithDuplicate(parser, "inherited", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               reportSet.setLocation("inherited", _location);
               reportSet.setInherited(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "reports", (String)null, parsed)) {
               List reports = new ArrayList();
               reportSet.setReports(reports);
               InputLocation _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               reportSet.setLocation("reports", _locations);

               while(parser.nextTag() == 2) {
                  if ("report".equals(parser.getName())) {
                     _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                     _locations.setLocation(reports.size(), _location);
                     reports.add(this.getTrimmedValue(parser.nextText()));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return reportSet;
      }
   }

   private Reporting parseReporting(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Reporting reporting = new Reporting();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      reporting.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "excludeDefaults", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               reporting.setLocation("excludeDefaults", _location);
               reporting.setExcludeDefaults(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "outputDirectory", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               reporting.setLocation("outputDirectory", _location);
               reporting.setOutputDirectory(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "plugins", (String)null, parsed)) {
               List plugins = new ArrayList();
               reporting.setPlugins(plugins);

               while(parser.nextTag() == 2) {
                  if ("plugin".equals(parser.getName())) {
                     plugins.add(this.parseReportPlugin(parser, strict, source));
                  } else {
                     this.checkUnknownElement(parser, strict);
                  }
               }
            } else {
               this.checkUnknownElement(parser, strict);
            }
         }

         return reporting;
      }
   }

   private Repository parseRepository(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Repository repository = new Repository();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      repository.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "releases", (String)null, parsed)) {
            repository.setReleases(this.parseRepositoryPolicy(parser, strict, source));
         } else if (this.checkFieldWithDuplicate(parser, "snapshots", (String)null, parsed)) {
            repository.setSnapshots(this.parseRepositoryPolicy(parser, strict, source));
         } else if (this.checkFieldWithDuplicate(parser, "id", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            repository.setLocation("id", _location);
            repository.setId(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "name", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            repository.setLocation("name", _location);
            repository.setName(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "url", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            repository.setLocation("url", _location);
            repository.setUrl(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "layout", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            repository.setLocation("layout", _location);
            repository.setLayout(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return repository;
   }

   private RepositoryBase parseRepositoryBase(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      RepositoryBase repositoryBase = new RepositoryBase();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      repositoryBase.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "id", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            repositoryBase.setLocation("id", _location);
            repositoryBase.setId(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "name", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            repositoryBase.setLocation("name", _location);
            repositoryBase.setName(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "url", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            repositoryBase.setLocation("url", _location);
            repositoryBase.setUrl(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "layout", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            repositoryBase.setLocation("layout", _location);
            repositoryBase.setLayout(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return repositoryBase;
   }

   private RepositoryPolicy parseRepositoryPolicy(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      RepositoryPolicy repositoryPolicy = new RepositoryPolicy();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      repositoryPolicy.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "enabled", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            repositoryPolicy.setLocation("enabled", _location);
            repositoryPolicy.setEnabled(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "updatePolicy", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            repositoryPolicy.setLocation("updatePolicy", _location);
            repositoryPolicy.setUpdatePolicy(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "checksumPolicy", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            repositoryPolicy.setLocation("checksumPolicy", _location);
            repositoryPolicy.setChecksumPolicy(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return repositoryPolicy;
   }

   private Resource parseResource(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Resource resource = new Resource();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      resource.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while(true) {
         while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
            if (this.checkFieldWithDuplicate(parser, "targetPath", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               resource.setLocation("targetPath", _location);
               resource.setTargetPath(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "filtering", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               resource.setLocation("filtering", _location);
               resource.setFiltering(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "mergeId", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               resource.setLocation("mergeId", _location);
               resource.setMergeId(this.getTrimmedValue(parser.nextText()));
            } else if (this.checkFieldWithDuplicate(parser, "directory", (String)null, parsed)) {
               _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
               resource.setLocation("directory", _location);
               resource.setDirectory(this.getTrimmedValue(parser.nextText()));
            } else {
               InputLocation _locations;
               ArrayList excludes;
               if (this.checkFieldWithDuplicate(parser, "includes", (String)null, parsed)) {
                  excludes = new ArrayList();
                  resource.setIncludes(excludes);
                  _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  resource.setLocation("includes", _locations);

                  while(parser.nextTag() == 2) {
                     if ("include".equals(parser.getName())) {
                        _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                        _locations.setLocation(excludes.size(), _location);
                        excludes.add(this.getTrimmedValue(parser.nextText()));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else if (this.checkFieldWithDuplicate(parser, "excludes", (String)null, parsed)) {
                  excludes = new ArrayList();
                  resource.setExcludes(excludes);
                  _locations = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                  resource.setLocation("excludes", _locations);

                  while(parser.nextTag() == 2) {
                     if ("exclude".equals(parser.getName())) {
                        _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
                        _locations.setLocation(excludes.size(), _location);
                        excludes.add(this.getTrimmedValue(parser.nextText()));
                     } else {
                        this.checkUnknownElement(parser, strict);
                     }
                  }
               } else {
                  this.checkUnknownElement(parser, strict);
               }
            }
         }

         return resource;
      }
   }

   private Scm parseScm(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Scm scm = new Scm();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      scm.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "connection", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            scm.setLocation("connection", _location);
            scm.setConnection(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "developerConnection", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            scm.setLocation("developerConnection", _location);
            scm.setDeveloperConnection(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "tag", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            scm.setLocation("tag", _location);
            scm.setTag(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "url", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            scm.setLocation("url", _location);
            scm.setUrl(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return scm;
   }

   private Site parseSite(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Site site = new Site();
      InputLocation _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
      site.setLocation("", _location);

      for(int i = parser.getAttributeCount() - 1; i >= 0; --i) {
         String name = parser.getAttributeName(i);
         parser.getAttributeValue(i);
         if (name.indexOf(58) < 0) {
            this.checkUnknownAttribute(parser, name, tagName, strict);
         }
      }

      Set parsed = new HashSet();

      while((strict ? parser.nextTag() : this.nextTag(parser)) == 2) {
         if (this.checkFieldWithDuplicate(parser, "id", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            site.setLocation("id", _location);
            site.setId(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "name", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            site.setLocation("name", _location);
            site.setName(this.getTrimmedValue(parser.nextText()));
         } else if (this.checkFieldWithDuplicate(parser, "url", (String)null, parsed)) {
            _location = new InputLocation(parser.getLineNumber(), parser.getColumnNumber(), source);
            site.setLocation("url", _location);
            site.setUrl(this.getTrimmedValue(parser.nextText()));
         } else {
            this.checkUnknownElement(parser, strict);
         }
      }

      return site;
   }

   public Model read(Reader reader, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      XmlPullParser parser = new MXParser();
      parser.setInput(reader);
      this.initParser(parser);
      return this.read((XmlPullParser)parser, strict, source);
   }

   public Model read(InputStream in, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      return this.read((Reader)ReaderFactory.newXmlReader(in), strict, source);
   }

   private Model read(XmlPullParser parser, boolean strict, InputSource source) throws IOException, XmlPullParserException {
      for(int eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
         if (eventType == 2) {
            if (strict && !"project".equals(parser.getName())) {
               throw new XmlPullParserException("Expected root element 'project' but found '" + parser.getName() + "'", parser, (Throwable)null);
            }

            Model model = this.parseModel(parser, strict, source);
            model.setModelEncoding(parser.getInputEncoding());
            return model;
         }
      }

      throw new XmlPullParserException("Expected root element 'project' but found no element at all: invalid XML document", parser, (Throwable)null);
   }

   public void setAddDefaultEntities(boolean addDefaultEntities) {
      this.addDefaultEntities = addDefaultEntities;
   }
}
