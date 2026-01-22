package de.uniwue.jpp.oscidrawing.visualization;

import de.uniwue.jpp.oscidrawing.Channel;
import de.uniwue.jpp.oscidrawing.Signal;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Osci2DPlotterClass implements Osci2DPlotter {

    int size;
    double scale;
    Color bgcol;
    BufferedImage image;

    public Osci2DPlotterClass(int size, double scale, Color bgcol) {
        this.size = size;
        this.scale = scale;
        this.bgcol = bgcol;
        this.image = image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        initializeImage();


    }

    private void initializeImage() {
        // Tüm pikselleri bgcol ile doldur
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                image.setRGB(x, y, bgcol.getRGB());
            }
        }
    }


    public static double map(double value, double in_min, double in_max, double out_min, double out_max) {
        if (in_min == in_max) {
            throw new IllegalArgumentException("in_min and in_max must not be the same");
        }
        return (value - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }


    @Override
    public int signalValToImageXCoord(double val) {
//        Gibt zurück, welche x-Koordinate ein Samplepunkt auf dem Bild hat.
//        Sie können sich auch hier wieder an der map-Funktion orientieren. Das x entspricht hier dem val.
//        in_min ist der Samplepunktwert, der ganz links im Bild gezeichnet wird (-scale).
//                in_max ist der Samplepunktwert, der ganz rechts im Bild gezeichnet wird (scale).
//                out_min ist die x-Koordinate des linkesten Pixels (also 0).
//                out_max ist die x-Koordinate der rechtesten Pixels (size-1).

//        float in_min = (float)-scale;
//        float in_max= (float) scale;
//        float out_min = 0;
//        float out_max= (float) size-1;
//        double mappedValue = map(val, in_min, in_max, out_min, out_max);
//
        double in_min = -scale;
        double in_max=scale;
        double out_min=0;
        double out_max=size-1;
        double mappedValue = map(val,in_min,in_max,out_min,out_max);

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
//        Gibt zurück, welche y-Koordinate ein Samplepunkt auf dem Bild hat.
//        Sie können sich auch hier wieder an der map-Funktion orientieren. Das x entspricht hier dem val.
//        in_min ist der Samplepunktwert, der ganz oben im Bild gezeichnet wird (scale).
//                in_max ist der Samplepunktwert, der ganz rechts im Bild gezeichnet wird (-scale).
//                out_min ist die y-Koordinate des obersten Pixels (also 0).
//                out_max ist die x-Koordinate der untersten Pixels (size-1).
//
        double in_min = scale;
        double in_max=-scale;
        double out_min=0;
        double out_max=size-1;
        double mappedValue = map(val,in_min,in_max,out_min,out_max);

        if (mappedValue >= 0) {
            // Pozitif veya sıfırsa: en yakın küçük tam sayıya yuvarla
            return (int) Math.floor(mappedValue);
        } else {
            // Negatifse: sıfıra daha yakın tam sayıya yuvarla
            return (int) Math.ceil(mappedValue);
        }


    }

    @Override
    public void drawSignalAt(Signal signal, int index, Color col) {
//    Zeichnet einen Punkt für das Wertepaar, das das Stereosignal signal am Samplepunkt index annimmt.
//    Falls signal nicht genau zwei Channels hat, soll eine IllegalArgumentException geworfen werden.
//    Ansonsten verwenden Sie den Wert des 0-ten Channels um die x-Koordinate des einzufärbenden Pixels zu bestimmen,
//    sowie den Wert des 1-ten Channels um die y-Koordinate zu berechnen. Falls der bestimmte Pixel innerhalb des Bildes liegt,
//    soll dieser im Bild auf die Farbe col geändert werden. Falls der Pixel außerhalb liegt,
//    soll die Methode nichts weiter tun (auch keine Exception werfen).
//
     if(signal.getChannelCount()!=2){
         throw new IllegalArgumentException("number of channels : not 2");
     }
     double val1 = signal.getValueAtValid(0,index);   //für x
     double val2 = signal.getValueAtValid(1,index);   //für y
     int Xcoord = signalValToImageXCoord(val1);
     int Ycoord = signalValToImageYCoord(val2);

        if (Xcoord >= 0 && Xcoord < size && Ycoord >= 0 && Ycoord < size) {
            image.setRGB(Xcoord, Ycoord, col.getRGB());
        }
    }

    @Override
    public void drawSignal(Signal signal, Color col) {
//        Diese Methode soll alle Wertepaare des Stereosignals signal in das Bild einzeichnen.
//        Falls signal ein unendliches Signal ist, soll eine IllegalArgumentException geworfen werden.
//        Ansonsten zeichnen Sie die Wertepaare an allen Samplepunkten
//        wie in drawSignalAt beschrieben mit der Farbe col in das Bild.
//
       if(signal.isInfinite()){
           throw new IllegalArgumentException("signal: unendlich");
       }


       int a = signal.getSize();

       for(int i=0; i<a; i++){
           drawSignalAt(signal,i,col);
       }


    }

    @Override
    public BufferedImage getImage() {
        return image;
    }
}
