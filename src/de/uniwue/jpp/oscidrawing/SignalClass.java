package de.uniwue.jpp.oscidrawing;

public class SignalClass extends Signal{
//    Wir unterscheiden endliche und unendliche Signale:
//    Beide sind nicht für negative Samplepunkt-Indizes definiert.
//    Ein endliches Signal mit Größe n ist für die Samplepunkt-Indizes von 0 bis n-1 definiert.
//    Ein unendliches Signal ist für alle Samplepunkt-Indizes größer gleich 0 definiert.
//    Wie bereits erwähnt wurde, kann ein Signal mehrere Werteverläufe beschreiben.
//    Ein Velauf wird als Channel bezeichnet. Für ein Signal mit n Channels sind die Channel-Indizes 0 bis n-1 gültig.
//

    public SignalClass(int Samplerate, boolean Infinite, Channel[] Channels){
        super(Samplerate, Infinite, Channels);
    }


    @Override
    public boolean isInfinite() {
        return Infinite;
    }

    public Channel getChannelAtindex(int channel){
        return Channels[channel];
    }

    @Override
    public int getSize() {
        if(isInfinite()){
            return 0;
        }
//        int allSignals =0;
//        for(int i =0; i<Channels.length; i++){
//            allSignals=allSignals+ Channels[i].getNumberofValues();
//        }
//        return allSignals;
    //    return size*Channels.length;
        return size;
    }

    @Override
    public int getChannelCount() {
        return Channels.length;
    }

    @Override
    public int getSampleRate() {
        return SampleRate;
    }

    @Override
    public double getValueAtValid(int channel, int index) {

        if (channel < 0 || channel >= Channels.length || index < 0 ||  index >= size ){
            throw new IllegalArgumentException("parameter ungültig");
        }
        else{ Channel diesesChannel = Channels[channel];
            double diesesValue = diesesChannel.getValues()[index];
            return diesesValue;
        }

    }



}
