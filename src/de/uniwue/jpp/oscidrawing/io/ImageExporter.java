package de.uniwue.jpp.oscidrawing.io;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;


public class ImageExporter {
    public static boolean writeToPNG(String pathWithoutSuffix, BufferedImage img) {

//        Diese Methode soll an pathWithoutSuffix ein ".png" anhängen und img unter diesem Dateipfad als PNG-Bild abspeichern.
//        Falls dabei irgendetwas schief geht, z.B. an den gegebenen Pfad nicht geschrieben werden kann,
//        soll false zurückgegeben werden, ansonsten soll true zurückgegeben werden.

       String pathWithPNG =pathWithoutSuffix+ ".png";
        try {
            // Görüntüyü belirtilen dosya yoluna kaydetmeye çalışıyoruz.
            File fileObject = new File(pathWithPNG);
            ImageIO.write(img, "png", fileObject);
            //img resmini, ....png uzantisiyla bu dosyaya (fileObject)e kaydediyor


            return true; // Eğer başarılı olursa true döneriz.


        } catch (IOException e) {
            // Eğer herhangi bir hata olursa, false döneriz.
            e.printStackTrace(); // Hatanın ne olduğunu konsola yazdırmak isteyebilirsiniz.
            return false;
        }
    }
}
