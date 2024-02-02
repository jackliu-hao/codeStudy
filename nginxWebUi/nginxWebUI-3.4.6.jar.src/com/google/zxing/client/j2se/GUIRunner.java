/*     */ package com.google.zxing.client.j2se;
/*     */ 
/*     */ import com.google.zxing.Binarizer;
/*     */ import com.google.zxing.BinaryBitmap;
/*     */ import com.google.zxing.LuminanceSource;
/*     */ import com.google.zxing.MultiFormatReader;
/*     */ import com.google.zxing.ReaderException;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.common.HybridBinarizer;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.nio.file.Path;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.text.JTextComponent;
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
/*     */ public final class GUIRunner
/*     */   extends JFrame
/*     */ {
/*     */   private final JLabel imageLabel;
/*     */   private final JTextComponent textArea;
/*     */   
/*     */   private GUIRunner() {
/*  56 */     this.imageLabel = new JLabel();
/*  57 */     this.textArea = new JTextArea();
/*  58 */     this.textArea.setEditable(false);
/*  59 */     this.textArea.setMaximumSize(new Dimension(400, 200));
/*  60 */     Container panel = new JPanel();
/*  61 */     panel.setLayout(new FlowLayout());
/*  62 */     panel.add(this.imageLabel);
/*  63 */     panel.add(this.textArea);
/*  64 */     setTitle("ZXing");
/*  65 */     setSize(400, 400);
/*  66 */     setDefaultCloseOperation(3);
/*  67 */     setContentPane(panel);
/*  68 */     setLocationRelativeTo(null);
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws MalformedURLException {
/*  72 */     GUIRunner runner = new GUIRunner();
/*  73 */     runner.setVisible(true);
/*  74 */     runner.chooseImage();
/*     */   }
/*     */   
/*     */   private void chooseImage() throws MalformedURLException {
/*  78 */     JFileChooser fileChooser = new JFileChooser();
/*  79 */     fileChooser.showOpenDialog(this);
/*  80 */     Path file = fileChooser.getSelectedFile().toPath();
/*  81 */     Icon imageIcon = new ImageIcon(file.toUri().toURL());
/*  82 */     setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight() + 100);
/*  83 */     this.imageLabel.setIcon(imageIcon);
/*  84 */     String decodeText = getDecodeText(file);
/*  85 */     this.textArea.setText(decodeText);
/*     */   }
/*     */   private static String getDecodeText(Path file) {
/*     */     BufferedImage image;
/*     */     Result result;
/*     */     try {
/*  91 */       image = ImageReader.readImage(file.toUri());
/*  92 */     } catch (IOException ioe) {
/*  93 */       return ioe.toString();
/*     */     } 
/*  95 */     LuminanceSource source = new BufferedImageLuminanceSource(image);
/*  96 */     BinaryBitmap bitmap = new BinaryBitmap((Binarizer)new HybridBinarizer(source));
/*     */     
/*     */     try {
/*  99 */       result = (new MultiFormatReader()).decode(bitmap);
/* 100 */     } catch (ReaderException re) {
/* 101 */       return re.toString();
/*     */     } 
/* 103 */     return String.valueOf(result.getText());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\j2se\GUIRunner.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */