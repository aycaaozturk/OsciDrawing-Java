package de.uniwue.jpp.oscidrawing.visualization;

import java.awt.Color;
import java.awt.image.BufferedImage;

import de.uniwue.jpp.oscidrawing.Signal;

//Osci2DPlotter, stereo bir sinyal tarafından neden olunan osziloskop elektron ışını hareketini görselleştirmeyi sağlar.

public interface Osci2DPlotter {
    public int signalValToImageXCoord(double val);

    public int signalValToImageYCoord(double val);

    public void drawSignalAt(Signal signal, int index, Color col);

    public void drawSignal(Signal signal, Color col);

    public BufferedImage getImage();


//    SignalTimePlotterClass newSignalPlt = new SignalTimePlotterClass(width, height, valScale, timeScale, bgcol, axiscol);
//        return newSignalPlt;
//int size;
//    double scale;
//    Color bgcol;
//    BufferedImage image;


    public static Osci2DPlotter createImageCreator(int size, double scale, Color bgcol) {
       Osci2DPlotterClass newOSCIPLOTTER = new Osci2DPlotterClass(size,scale,bgcol);
       return newOSCIPLOTTER;
    }
}
