package de.uniwue.jpp.oscidrawing.visualization;

import java.awt.Color;
import java.awt.image.BufferedImage;

import de.uniwue.jpp.oscidrawing.Signal;

public interface SignalTimePlotter {

    public int sampleIndexToImageXCoord(int sampleIndex, int sampleRate);

    public int signalValToImageYCoord(double val);

    public void drawSignalAt(Signal signal, int channel, int index, Color col);

    public void drawSignal(Signal signal, int channel, Color col);

    public void drawSignal(Signal signal, Color... colors);

    public BufferedImage getImage();

    public static SignalTimePlotter createSignalTimePlotter(int width, int height, double valScale, double timeScale, Color bgcol, Color axiscol) {
        SignalTimePlotterClass newSignalPlt = new SignalTimePlotterClass(width, height, valScale, timeScale, bgcol, axiscol);
        return newSignalPlt;
    }
}










//     valScale:
//        pozitifse -> o nokta en üstte ner yalir, yani amplitude gibi, uc noktalari söylüyor, simetrik
//        negatifse -> en altta yer alir, alt tepe noktasi

//        valScale, y ekseninin nasıl ölçeklendirileceğini belirtir.
//        Örneğin, değeri 0 olan örnek noktalar resmin ortasındaki satırda çizilir.
//        Değeri valScale olan noktalar en üst satırda yer alır. Değeri -valScale olan noktalar ise en alt satırda yer alır

//      timeScale: x eksenine göre nasil ayrildiklari 0----0.25----0.50----0.75----1.0->timeScale         gibi
        //timeScale 1 ise: en sag noktayi temsil ediyor
//        Bu nedenle timeScale, resimde gösterilecek zaman aralığının sınırıdır.


