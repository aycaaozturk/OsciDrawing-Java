package de.uniwue.jpp.oscidrawing.io;

import de.uniwue.jpp.oscidrawing.Signal;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

//Çıkış Akışı (Output Stream): Bir programdan dışarıya veri göndermek (örneğin, bir dosyaya yazmak) için kullanılan bir yoldur.
// Düşünün ki bir dosyaya bir şeyler yazmak istiyorsunuz; bu durumda verilerinizi dosyaya göndermek için bir "çıkış akışı"
// kullanırsınız.
//
//        Giriş Akışı (Input Stream): Bir programın dışarıdan veri alması (örneğin, bir dosyadan veri okumak) için
//        kullanılan bir yoldur. Diyelim ki bir dosyadan bir şeyler okumak istiyorsunuz;
//        bu durumda verilerinizi dosyadan alıp programınıza getirmek için bir "giriş akışı" kullanırsınız.

//OUTPUT: dosyaya yazmak
//INPUT:  dosyadan okumak

public class AudioExporter {

    public static boolean writeChannelToFile(String path, Signal signal, int channel) {
        String endungRaw = path+".raw";

       if(signal.isInfinite()){
           throw new IllegalArgumentException("signal: unendlich");
       }
      try{
          File fileWithRaw = new File(endungRaw);  //bu dosya yolunu bir file nesnesine dönüstürdük
          //file path: bir dosyanin nerede oldugunu söyleyen string formunda adres gibi bi sey
          // fileWithRaw : ....raw yolunu temsil eden bir file objesi



//          Dosyaya veri yazmak için bir byte stream (bayt akışı) oluşturduk

          FileOutputStream fileOStream = new FileOutputStream(fileWithRaw);

//     FileOutputStream, bir dosyaya veri yazmak için kullanılır.
//     Yazılan veriler genellikle bayt veya bayt dizisi olarak geçer.
//     Metin dosyaları, resim dosyaları, veri dosyaları vb. türdeki dosyalar için kullanılabilir.
//     yani filewithraw dosyasina yazi yazmak icin outputStream actik


//     DataOutputStream, Java'da kullanılan bir sınıftır ve bayt düzeyinde veri yazma işlemlerini kolaylaştırmak
//     için kullanılır. FileOutputStream gibi bir çıktı akışını (output stream) sarmalayarak (wrap) çalışır,
//     ancak DataOutputStream, ilkel veri türlerini (int, long, float, double, boolean, vb.) doğrudan bayt olarak yazmanıza
//     olanak tanır.
//
//          Sarmalamadan: Bayt düzeyinde veri yazmak zorundasınız ve her şeyi manuel olarak yapmanız gerekiyor.
//          Sarmalayarak: Daha yüksek seviyeli ve kullanımı kolay yöntemlerle verileri yazabilirsiniz.
//          DataOutputStream, FileOutputStream'u sarmalayarak, verileri daha anlamlı bir şekilde yazmamızı sağlıyor.


          DataOutputStream dataOStream = new DataOutputStream(fileOStream);
          int SamplePunkteAnzahl = signal.getSize();
          for(int i=0; i<SamplePunkteAnzahl; i++){
              double doubleWert = signal.getValueAtValid(channel,i);
              float floatWert = (float) doubleWert;
              dataOStream.writeFloat(floatWert);
          }
      }
      catch(IOException e){
          return false;

      }
      return true;  //true dönerse: islem yapildi, yazildi, basarili, kanal yazilaabildi
    }

    public static boolean writeStereoToFiles(String path, Signal signal) {
//        public static boolean writeChannelToFile(String path, Signal signal, int channel)


       if(signal.isInfinite()){
           throw new IllegalArgumentException("signal unendlich");
       }
       if(signal.getChannelCount()!=2){
           throw new IllegalArgumentException("kanal-anzahl: nicht genau 2");
       }
//       int lange = path.length();
//       String pathWithoutRaw = path.substring(lange-4);
//       String pathLeft = pathWithoutRaw+"left.raw";
//       String pathRight = pathWithoutRaw+"right.raw";

        String pathLeft = path+"left";
        String pathRight = path+"right";

       boolean leftChannel = writeChannelToFile(pathLeft, signal, 0);
       boolean rightChannel = writeChannelToFile(pathRight, signal,1);

       if(leftChannel==false || rightChannel==false){
           return false;

        }
       return true; // her iki kanal da basarili sekilde kaydedildi



    }
}
