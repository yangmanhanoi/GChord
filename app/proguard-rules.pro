# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontshrink
-dontwarn com.daimajia.easing.Glider*
-keep public class * implements com.bumptech.glide.module.GlideModule
 -keep class * extends com.bumptech.glide.module.AppGlideModule {
  <init>(...);
 }
 -keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
   **[] $VALUES;
   public *;
 }
 -keep class
 com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
   *** rewind();
  }
-keep class cn.pedant.SweetAlert.Rotate3dAnimation {
    public <init>(...);
 }
-keep public class * implements com.dqt.libs.chorddroid.helper.ChordHelper
-dontwarn org.jsoup*
-dontwarn com.github.f0ris.sweetalert*
-dontwarn com.daimajia.easing.Skill*
-dontwarn org.bouncycastle.jsse.BCSSLParameters*
-dontwarn org.bouncycastle.jsse.BCSSLSocket*
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider*
-dontwarn org.conscrypt.Conscrypt$Version*
-dontwarn org.conscrypt.Conscrypt*
-dontwarn org.conscrypt.ConscryptHostnameVerifier*
-dontwarn org.openjsse.javax.net.ssl.SSLParameters*
-dontwarn org.openjsse.javax.net.ssl.SSLSocket*
-dontwarn org.openjsse.net.ssl.OpenJSSE*

-dontwarn java.awt.Color*
-dontwarn java.awt.Component*
-dontwarn java.awt.Container*
-dontwarn java.awt.Dimension*
-dontwarn java.awt.FontMetrics*
-dontwarn java.awt.Graphics2D*
-dontwarn java.awt.Graphics*
-dontwarn java.awt.Point*
-dontwarn java.awt.Shape*
-dontwarn java.awt.event.KeyEvent*
-dontwarn java.awt.event.KeyListener*
-dontwarn java.awt.event.MouseAdapter*
-dontwarn java.awt.event.MouseEvent*
-dontwarn java.awt.event.MouseListener*
-dontwarn java.awt.event.MouseMotionListener*
-dontwarn java.awt.event.MouseWheelEvent*
-dontwarn java.awt.event.MouseWheelListener*
-dontwarn java.awt.geom.AffineTransform*
-dontwarn java.awt.geom.NoninvertibleTransformException*
-dontwarn java.awt.geom.Point2D$Double*
-dontwarn java.awt.geom.Point2D$Float*
-dontwarn java.awt.geom.Point2D*
-dontwarn java.awt.geom.Rectangle2D$Double*
-dontwarn java.awt.geom.Rectangle2D*
-dontwarn javax.sound.sampled.AudioFileFormat$Type*
-dontwarn javax.sound.sampled.AudioFormat$Encoding*
-dontwarn javax.sound.sampled.AudioFormat*
-dontwarn javax.sound.sampled.AudioInputStream*
-dontwarn javax.sound.sampled.AudioSystem*
-dontwarn javax.sound.sampled.DataLine$Info*
-dontwarn javax.sound.sampled.Line$Info*
-dontwarn javax.sound.sampled.Line*
-dontwarn javax.sound.sampled.LineUnavailableException*
-dontwarn javax.sound.sampled.SourceDataLine*
-dontwarn javax.sound.sampled.TargetDataLine*
-dontwarn javax.sound.sampled.UnsupportedAudioFileException*
-dontwarn javax.swing.JFrame*
-dontwarn javax.swing.JPanel*
-dontwarn javax.swing.JSplitPane*
-dontwarn javax.swing.SwingUtilities*
-dontwarn org.jspecify.annotations.NullMarked*