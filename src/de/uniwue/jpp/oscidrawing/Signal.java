package de.uniwue.jpp.oscidrawing;

public abstract class Signal {
    int SampleRate;
    boolean Infinite;
    Channel[] Channels;
    int size;


    public Signal(int SampleRate, boolean Infinite, Channel[] Channels){
        if(SampleRate<=0 ){
            throw new IllegalArgumentException("Samplerate: nicht positiv");
        }

        if(Channels==null ){
            throw new IllegalArgumentException("Channels: null");
        }
//        if(size<=0 ){
//            throw new IllegalArgumentException("Size: nicht positiv");
//        }

        for(int a=0; a<Channels.length; a++){
            if(Channels[a]==null){
                throw new IllegalArgumentException("Channels: null");
            }
        }
        for(int a=0; a<Channels.length; a++){
            if( Channels[a].Values==null){
                throw new IllegalArgumentException("Channel values: null");
            }
        }
        int size1= Channels[0].size;
        for(int x=0; x<Channels.length;x++){
            if(size1!=Channels[x].size){
                throw new IllegalArgumentException("Channels haben nicht die gleiche Länge");
            }
        }


        this.SampleRate=SampleRate;
        this.Infinite=Infinite;
        this.size=Channels[0].size;
        this.Channels=Channels;

//        this.Channels= new Channel[Channels.length];
//
//        for(int i=0; i< Channels.length; i++){
//            double[] Values = new double[size];
//            for(int a=0; a<Values.length; a++){
//                Values[a]= Channels[i].Values[a];
//            }
//
//            this.Channels[i]= new Channel(Values);
    //    }

//        for (int i = 0; i < Channels.length; i++) {
//            double[] Values = new double[size];
//            // Kaynak Channels dizisindeki değerleri yeni oluşturulan Values dizisine kopyala
//            System.arraycopy(Channels[i].Values, 0, Values, 0, size);
//            this.Channels[i] = new Channel(Values);
//        }

    }
    public Signal(){
      this.size=0;
      this.SampleRate=1;
      double[] Values = new double[1];
      Values[0]=0;
      this.Channels= new Channel[1];
      Channel newChannel = new Channel(Values);
      this.Channels[0]= newChannel;

    }

    public Channel getChannelAt(int channel){
        if(channel<0 || channel >= Channels.length){    //lenght: 4 ->  0 1 2 3
            throw new IllegalArgumentException("index von channel: ungültig");
        }

        return Channels[channel];
    }

    public Channel[] getAllChannels(){
        return Channels;
    }

    public abstract boolean isInfinite();

    public abstract int getSize();

    public abstract int getChannelCount();

    public abstract int getSampleRate();

    public abstract double getValueAtValid(int channel, int index);

    public double getDuration() {   //Gesamtdauer des Signals= AnzahlSamplePunkte / Samplerate
        if(isInfinite()){                         //    X
            return 0;                           //  t . V
        }
        else{
            double t = getSize()/ getSampleRate();
            return t;
        }
    }

//    Gibt den Wert des Signals von Channel: channel an Stelle index zurück, falls das Signal dort definiert ist.
//    Falls channel oder index ungültig sind, soll stattdessen 0 zurückgegeben werden.
//    Diese Methode ist nicht implementierungsabhängig und kann direkt in der abstrakten Klasse implementiert werden

    public boolean parameterIsValidForGetValueAt(int channel, int index){
        if (channel < 0 || channel >= Channels.length || index < 0 ||  index >= size ){
            return false;
        }
        else{ return true;}
    }

    public double getValueAt(int channel, int index) {

        try {
            return getValueAtValid(channel, index);
        } catch(Exception e){
            return 0;
        }


    }

}

