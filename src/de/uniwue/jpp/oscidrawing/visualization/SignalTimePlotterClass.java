package de.uniwue.jpp.oscidrawing.visualization;

import de.uniwue.jpp.oscidrawing.Channel;
import de.uniwue.jpp.oscidrawing.Signal;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SignalTimePlotterClass implements SignalTimePlotter {
    private int width;
    private int height;
    private double valScale;
    private double timeScale;
    private Color bgcol;
    private Color axiscol;
    private BufferedImage image;

    public SignalTimePlotterClass(int width, int height, double valScale, double timeScale, Color bgcol, Color axiscol) {
        this.width = width;
        this.height = height;
        this.valScale = valScale;
        this.timeScale = timeScale;
        this.bgcol = bgcol;
        this.axiscol = axiscol;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        initializeImage();
    }
//    Diyelim ki 100x100 boyutlarında bir BufferedImage nesnesi oluşturdunuz:
//
//    Sol üst köşe: (0,0)                      (0,0).....................(99,0)
//    Sağ üst köşe: (99,0)                       .                          .
//    Sol alt köşe: (0,99)                       .                          .
//    Sağ alt köşe: (99,99)                    (0.99).....................(99,99)

    public static double map(double value, double in_min, double in_max, double out_min, double out_max) {
        if (in_min == in_max) {
            throw new IllegalArgumentException("in_min and in_max must not be the same");
        }
        return (value - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }




    private void initializeImage() {
        // Tüm pikselleri bgcol ile doldur
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, bgcol.getRGB());
            }
        }
        // Orta çizgiyi axiscol ile çiz
        int midY = height % 2 == 0 ? (height / 2) : (height / 2);
        for (int x = 0; x < width; x++) {
            image.setRGB(x, midY, axiscol.getRGB());
        }
    }

    @Override
    public int sampleIndexToImageXCoord(int sampleIndex, int sampleRate) {
//    Gibt zurück, welche x-Koordinate ein Samplepunkt auf dem Bild hat.
//    Sie können sich an dieser map-Funktion orientieren. Das x entspricht hier dem sampleIndex.

//    in_min ist der kleinste Samplepunktindex (also 0).
//    in_max ist der größte Samplepunktindex, der noch auf dem Bild zu sehen ist (sampleRate*timeScale-1).
//    out_min ist die kleinste x-Koordinate (also 0).
//    out_max ist die größte x-Koordinate (width-1).

//       double in_maxDouble = (sampleRate*timeScale)-1;
//
//      float in_min=0;
//      float in_max = (float) in_maxDouble;
//      float out_min=0;
//      float out_max= width-1;
//
//      float mapped =(map(sampleIndex,in_min,in_max,out_min,out_max));
//      double mappedD = (double) mapped;
//      double mappedValueRunden = Math.floor(mappedD);
//      int xCoordInteger = (int) mappedValueRunden;
//
//      return xCoordInteger;


        double in_min = 0;
        double in_max = sampleRate * timeScale - 1;
        double out_min = 0;
        double out_max = width - 1;

        // `sampleIndex`'i görüntü koordinatlarına map eden değeri hesapla
        double mappedValue = map(sampleIndex, in_min, in_max, out_min, out_max);

        if (mappedValue >= 0) {
            // Pozitif veya sıfırsa: en yakın küçük tam sayıya yuvarla
            return (int) Math.floor(mappedValue);
        } else {
            // Negatifse: sıfıra daha yakın tam sayıya yuvarla
            return (int) Math.ceil(mappedValue);
        }

    }

    @Override
    public int signalValToImageYCoord(double val) {
//     Gibt zurück, welche y-Koordinate ein Samplepunkt auf dem Bild hat.
//     Sie können sich auch hier wieder an der map-Funktion orientieren. Das x entspricht hier dem val.
//     in_min ist der Samplepunktwert, der ganz oben im Bild gezeichnet wird (valScale).
//     in_max ist der Samplepunktwert, der ganz unten im Bild gezeichnet wird (-valScale).
//     out_min ist die y-Koordinate der obersten Pixel (also 0).
//     out_max ist die y-Koordinate der untersten Pixel (height-1).

//        float in_min =(float) valScale;
//        float in_max = (float) -valScale;
//        float out_min= 0;
//        float out_max= height-1;
//        float valFL = (float) val;
//
        double in_min = valScale;
        double in_max = -valScale;
        double out_min = 0;
        double out_max = height - 1;

        double mappedValue = map(val, in_min, in_max, out_min, out_max);
        if (mappedValue >= 0) {
            // Pozitif veya sıfırsa: en yakın küçük tam sayıya yuvarla
            return (int) Math.floor(mappedValue);
        } else {
            // Negatifse: sıfıra daha yakın tam sayıya yuvarla
            return (int) Math.ceil(mappedValue);
        }


    }

    @Override
    public void drawSignalAt(Signal signal, int channel, int index, Color col) {
//  Diese Methode soll den Samplepunkt mit dem Index index aus dem Channel channel
//  von signal auf dem Bild in der Farbe col einzeichnen.
//  Bestimmen Sie dazu die Pixelkoordinaten des Samplepunktes.
//  Liegen diese innerhalb der Bildes, soll der entsprechende Pixes mit col gefärbt werden.
//  Liegen sie außerhalb des Bildes, soll diese Methode nichts tun (auch keine Exceptions werfen).
//  kanalin o noktasindaki sinyali ciziyor
        double drawValue = signal.getValueAt(channel, index);   //o kanalin value arrayindan o indexli deger = value

        int Ycord = signalValToImageYCoord(drawValue);
        int Xcord = sampleIndexToImageXCoord(index, signal.getSampleRate());

        if (Xcord >= 0 && Xcord < width && Ycord >= 0 && Ycord < height) {
            image.setRGB(Xcord, Ycord, col.getRGB());
        }

    }


    @Override
    public void drawSignal(Signal signal, int channel, Color col) {
        //bir kanalin bütün valuelarina ihtiyac var
        //o kanalin values arrayinin tüm degerlerini cizecek

        //    Diese Methode soll den Verlauf des Channels channel von signal in der Farbe col auf dem Plot einzeichnen


//        Channel drawThisChannel = signal.getChannelAt(channel);   //channel indexli kanali aldik
//      int indx = drawThisChannel.getValues().length;            //kanalin values arrayinin indexleri
//
//      for(int i=0; i<indx; i++){
//          drawSignalAt(signal, channel, i, col);
//      }

        try {

            Channel drawThisChannel = signal.getChannelAt(channel);




            for (int i = 0; i < signal.getSize(); i++) {
                drawSignalAt(signal, channel, i, col);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("ungültige channel");
        }


    }

    @Override
    public void drawSignal(Signal signal, Color... colors) {
//        Diese Methode soll alle Channels von signal in das Bild einzeichnen.
//        Der i-te Channel soll dabei in der Farbe colors[i] geplottet werden.
//        Falls die Anzahl der übergebenen Farben nicht mit der Anzahl der Channels von signal übereinstimmt,
//        soll eine IllegalArgumentException geworfen werden.

        int nChannels = signal.getChannelCount();    //kanal sayisi
        if (nChannels != colors.length) {
            throw new IllegalArgumentException("invalid");
        }

        for (int i = 0; i < nChannels; i++) {
            drawSignal(signal, i, colors[i]);
        }
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }
}
