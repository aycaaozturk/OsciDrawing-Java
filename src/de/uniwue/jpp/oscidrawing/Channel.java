package de.uniwue.jpp.oscidrawing;

public class Channel {
    //    Diese Aufgabe interpretiert ein Signal folgendermaßen:
//
//    Ein Signal beschreibt ein oder mehrere Werteverläufe über die Zeit.
//    Der Signalverlauf wird nicht kontinuierlich gespeichert.
//    Stattdessen definiert sich der Verlauf über die Werte (vom Typ double),
//    die das Signal an gewissen Zeitpunkten annimmt (Samplepunkte).

//    Samplepunkte sind zeitlich äquidistant verteilt.
//    Die Samplerate gibt an, wieviele Samplepunkte eine Sekunde des Signals darstellen.
//    Eine höhere Samplerate erlaubt eine genauere Beschreibung von Signalen, fordert aber natürlich mehr Speicherplatz.
//    Wir unterscheiden endliche und unendliche Signale:
//    Beide sind nicht für negative Samplepunkt-Indizes definiert.
//    Ein endliches Signal mit Größe n ist für die Samplepunkt-Indizes von 0 bis n-1 definiert.
//    Ein unendliches Signal ist für alle Samplepunkt-Indizes größer gleich 0 definiert.
//    Wie bereits erwähnt wurde, kann ein Signal mehrere Werteverläufe beschreiben.
//    Ein Verlauf wird als Channel bezeichnet. Für ein Signal mit n Channels sind die Channel-Indizes 0 bis n-1 gültig.


    double[] Values;
    int size;

    public Channel(double[] Values){
        if(Values==null){
            throw new IllegalArgumentException("Values-Array: null");
        }
        this.Values=Values;
        this.size=Values.length;

    }
//    public Channel(){
//        this.Values=new double[0];
//
//    }

    public int getNumberofValues(){
        return Values.length;         //size ile esit
    }

    public double[] getValues() {
        return Values;
    }

    public void setValues(double[] values) {
        Values = values;
    }
    public void setValueAtIndex(int index, double value){
        Values[index]=value;
    }
    public void MultiplyValuesWith(double mal){
        for(int a=0; a<Values.length; a++){
            Values[a]= Values[a]* mal;
        }
    }

}
