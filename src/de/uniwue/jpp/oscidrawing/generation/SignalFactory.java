package de.uniwue.jpp.oscidrawing.generation;

import com.sun.jdi.Value;
import de.uniwue.jpp.oscidrawing.Channel;
import de.uniwue.jpp.oscidrawing.Signal;
import de.uniwue.jpp.oscidrawing.SignalClass;
import de.uniwue.jpp.oscidrawing.generation.pathutils.Line;
import de.uniwue.jpp.oscidrawing.generation.pathutils.Point;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.zip.Inflater;

//SignalTimePlotter sınıfı, sinyallerin zaman içindeki değişimini çizer (osziloskopun klasik kullanımına benzer şekilde),

public abstract class SignalFactory {

//    Diese Methode erlaubt es eine Menge von double-Werten, gegeben als Array, als ein Signal darzustellen.
//    Falls sampleRate nicht positiv ist, soll eine IllegalArgumentException geworfen werden.
//    Das zu erzeugende Signal soll endlich sein, soviele Samplepunkte enthalten wie signalData Werte und
//    genau einen Channel besitzen. Die Samplerate ist durch sampleRate gegeben.
//    Der i-te Samplepunkt von Channel 0 soll dem i-ten Werten von signalData entsprechen.

    public static Signal fromValues(double[] signalData, int sampleRate) {
        if (sampleRate <= 0) {
            throw new IllegalArgumentException("Samplerate nicht positiv");
        }
        Channel chnl = new Channel(signalData);
        Channel[] channels = {chnl};
        Signal erzeugteSignal = new SignalClass(sampleRate, false, channels);
        return erzeugteSignal;
    }
//    Diese Methode soll periodische Signale mit einer Periodenlänge von 2π erzeugen, zum Beispiel Sinus- oder Cosinus-Signale.

//    Das zu erzeugende Signal soll endlich, mit Größe sampleRate*duration, sein, genau einen Channel besitzen
//    grösse= bir kanaldaki values arrayinin boyutu
//    und die Samplerate sampleRate haben. Zum Berechnen der Signalwerte gehen Sie folgendermaßen vor:

//    Es ist step = (frequency*2π)/sampleRate.
//    Berechnen Sie den i-ten Samplepunkt von Channel 0 als function.applyAsDouble(i*step).
//    Beispielsweise sieht für den Aufruf wave(Math::sin, 4, 1, 500) der Plot des Signals so aus:
//

    public static Signal wave(DoubleUnaryOperator function, double frequency, double duration, int sampleRate) {
        if (frequency <= 0 || duration <= 0 || sampleRate <= 0) {
            throw new IllegalArgumentException("parameter: ungültig");
        }
        double gr = sampleRate * duration;
        int größe = (int) gr;

        double step = (frequency * 2 * Math.PI) / sampleRate;
        double[] Values = new double[größe];

        for (int i = 0; i < Values.length; i++) {
            Values[i] = function.applyAsDouble(i * step);
        }
        Channel channel = new Channel(Values);
        Channel[] channels = {channel};
        Signal erzeugteSignal = new SignalClass(sampleRate, false, channels);
        return erzeugteSignal;


    }
//    Diese Methode soll ein Signal zurückgeben, das "den Velauf einer Rampe" besitzt,
//    also über die Dauer duration von 0 linear bis 1 ansteigt.

//    Das zu erzeugende Signal soll endlich, mit Größe sampleRate*duration, sein,
//    genau einen Channel besitzen und die Samplerate sampleRate haben.
//    Zum Berechnen der Signalwerte gehen Sie folgendermaßen vor:
//    Es sei im Folgenden samples die Anzahl der Samplepunkte (also die Größe des Signals).
//    Der i-te Samplepunkt soll den Wert i/(samples-1) haben.
//    Beispielsweise sieht für den Aufruf rampUp(1, 500) der Plot des Signals


    public static Signal rampUp(double duration, int sampleRate) {
        if (duration <= 0 || sampleRate <= 0) {
            throw new IllegalArgumentException("parameter: ungültig");
        }
        double gr = sampleRate * duration;
        int größe = (int) gr;
        if (größe < 2) {
            throw new IllegalArgumentException("grösse ist kleiner als 2");
        }
        int samples = größe;
        double[] Values = new double[samples];
        for (int i = 0; i < samples; i++) {
            Values[i] = (double) i / (samples - 1);
        }
        Channel channel = new Channel(Values);
        Channel[] channels = {channel};
        Signal erzeugteSignal = new SignalClass(sampleRate, false, channels);
        return erzeugteSignal;


    }

//    Das kombinierte Signal soll so groß wie das kürzeste Monosignal sein. Das bedeutet folglich,
//    dass das neue Signal nur unendlich ist, wenn alle Signal in signals unendlich sind.
//    Das Signal hat genauso viele Channels wie signals Signal-Objekte enthält.
//    Die neue Samplerate ist die selbe wie die der Signale aus signals.
//    Der neue Werte von Channel channel an Stelle index soll der selbe sein wie der Wert des Signals
//    aus signals mit Index channel von Channel 0 an Stelle index.

    public static Signal combineMonoSignals(List<Signal> signals) {
        if (signals == null) {
            throw new NullPointerException("signals: null");
        }


        if (signals.isEmpty()) {
            throw new IllegalArgumentException("signals: empty");
        }


        List<Integer> Sampleraten = new ArrayList<>();
        for (Signal i : signals) {
            Sampleraten.add(i.getSampleRate());   // listedeki sinyallerin sampleratlerden liste yaptik
        }

        int first = Sampleraten.get(0);
        for (double sR : Sampleraten) {
            if (sR != first) {
                throw new IllegalArgumentException("Sampleraten verschieden");
            }
        }
        for (Signal i : signals) {
            if (isMonosignal(i) == false) {
                throw new IllegalArgumentException("kein Monosignal");
            }
        }

        boolean unendlich = false;
        if (AllSignalsInfinite(signals)) {
            unendlich = true;
        }

        List<Integer> SizesOfSignals = new ArrayList<>();
        for (int i = 0; i < signals.size(); i++) {
            SizesOfSignals.add(signals.get(i).getSize());  //sizelardan liste yaptik
        }

        int kürzesteMonosognalSize = Collections.min(SizesOfSignals);
        int NumberOfChannels = signals.size();                             //sinyal listesinin uzunlugu kadar kanal olacak
        int sampleRate = signals.get(0).getSampleRate();
        Channel[] channels = new Channel[NumberOfChannels];


        for (int i = 0; i < signals.size(); i++) {                          //sinyal listesini dönecek
            double[] ValuesOfChannel = new double[kürzesteMonosognalSize];
            for (int a = 0; a < kürzesteMonosognalSize; a++) {
                ValuesOfChannel[a] = signals.get(i).getValueAt(0, a);
            }
            Channel ChannelNew = new Channel(ValuesOfChannel);
            channels[i] = ChannelNew;

        }
        Signal erzeugteSignal = new SignalClass(sampleRate, unendlich, channels);
        return erzeugteSignal;

    }

    public static boolean isMonosignal(Signal signal) {
        int a = signal.getChannelCount();
        if (a == 1) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean AllSignalsInfinite(List<Signal> signals) {
        for (Signal i : signals) {
            if (i.isInfinite() == false) {
                return false;
            }
        }
        return true;
    }

    public static Signal combineMonoSignals(Signal... signals) {
        List<Signal> signale = new ArrayList<>();
        for (int i = 0; i < signals.length; i++) {
            signale.add(signals[i]);
        }
        return combineMonoSignals(signale);
    }

    public static Signal stereoFromMonos(Signal left, Signal right) {
        List<Signal> LeftAndRight = new ArrayList<>();
        LeftAndRight.add(left);
        LeftAndRight.add(right);
        return combineMonoSignals(LeftAndRight);
    }
//    Diese Methode soll ein neues Signal erzeugen, dessen Channels sich durch das in channels gegebene Mapping
//    aus den Channels von source zusammensetzen.
//    Dementsprechend übernimmt das neue Signal auch Unendlichkeit, Größe und Samplerate vom ursprünglichen Signal.
//    Das neue Signal besitzt "Länge von channels"-viele Channels.
//    Der neue Werte von Channel channel an Stelle index soll der selbe sein wie der Wert
//    des alten Signals von Channel channels[channel] an Stelle index.
//    Dabei sind einige Fehlerfälle zu beachten:

//    Falls channels Werte enthält, die keine gültigen Channel-Indizes in source sind,
//    soll eine IllegalArgumentException geworfen werden. Beispiel:
//    Sei s3 ein Signal mit drei Channels, dann liefert
//    extractChannels(s3, 0) ein Monosignal, das als Channel 0 den Channel 0 von s3 besitzt.
//    extractChannels(s3, 2) ein Monosignal, das als Channel 0 den Channel 2 von s3 besitzt.
//    extractChannels(s3, 2, 1, 0) ein Signal mit drei Channels, nämlich den Channels von s3, aber in umgekehrter Reihenfolge.
//

    public static Signal extractChannels(Signal source, int... channels) {
        if (source == null) {
            throw new NullPointerException("source: null");
        }
        for (int channelIndex : channels) {
            if (channelIndex < 0 || channelIndex >= source.getChannelCount()) {
                throw new IllegalArgumentException("ungültige channels");
            }
        }

        int samplerate = source.getSampleRate();
        boolean endlichkeit = source.isInfinite();

        Channel[] channelsOfNewSignal = new Channel[channels.length];

        int size = source.getSize();

        for (int i = 0; i < channelsOfNewSignal.length; i++) {
            double[] ValuesOfChannel = new double[size];
            Channel channelOfNew = new Channel(ValuesOfChannel);
            channelsOfNewSignal[i] = channelOfNew;
        }

//        Yeni sinyalin channel kanalındaki index konumundaki yeni değeri,
//        eski sinyalin channels[channel] kanalındaki index konumundaki değer ile aynı olacaktır.
//
//        for(int i=0; i<channels.length; i++){
//            for(int a=0; a<size; a++){
//                double ValueofNew = source.getValueAtValid(i,a);
//                Channel cnl = channelsOfNewSignal[i];
//                cnl.getValues()[a]= ValueofNew;
//            }

        for (int i = 0; i < channels.length; i++) {
            int originalChannelIndex = channels[i]; // source'den alınacak orijinal kanal
            for (int a = 0; a < size; a++) {
                double valueOfNew = source.getValueAtValid(originalChannelIndex, a);
                channelsOfNewSignal[i].getValues()[a] = valueOfNew;
            }
        }


        Signal erzeugteSignal = new SignalClass(samplerate, endlichkeit, channelsOfNewSignal);
        return erzeugteSignal;

    }
//    Diese Methode soll ein Signal zurückgeben, das auf dem Oskilloskop einen Kreis zeichnet.

//    Um auf dem Oszilloskop zu zeichnen muss das Signal ein Stereosignal sein.
//    Ein Kreis ergibt sich genau dann, wenn ein Channel eine Sinus-Welle enthält,
//    während der andere eine Cosinus-Welle mit selber Frequenz darstellt.
//    Prinzipiell können die Channel vertauscht werden, allerdings soll sich hier die Sinus-Welle im Channel 0 befinden.
//    Es soll frequency für die Frequenz der beiden Wellen gewählt werden. Daraus ergibt sich,
//    dass der Elektronenstrahl des Oszilloskops frequency oft pro Sekunde den Kreis abfährt.
//    Die Amplitude der Wellen bestimmt den Radius des Kreises.
//    In diesem Fall soll die "Default"-Amplitude von 1 verwendet werden.
//    Weiterhin soll, vermutlich selbstverständlich, das Signal endlich sein,
//    eine Größe von duration*sampleRate und eine Samplerate von sampleRate haben.

    public static Signal circle(double frequency, double duration, int sampleRate) {
        if (frequency <= 0 || duration <= 0 || sampleRate <= 0) {
            throw new IllegalArgumentException("parameter: nicht positiv");
        }
//       int size = (int) (duration*sampleRate);
        int size = (int) Math.floor(duration * sampleRate);


//       if(size<=0){
//           throw new IllegalArgumentException("berechnete size: nicht positiv");
//       }
        double[] sinValues = new double[size];
        double[] cosValues = new double[size];

        double step = (2 * Math.PI * frequency) / sampleRate;

        for (int i = 0; i < size; i++) {
            sinValues[i] = Math.sin(i * step);
            cosValues[i] = Math.cos(i * step);
        }
        Channel channel0 = new Channel(sinValues);
        Channel channel1 = new Channel(cosValues);
        Channel[] channels = {channel0, channel1};
        Signal erzeugteSignal = new SignalClass(sampleRate, false, channels);
        return erzeugteSignal;
    }
//    Nun geht es endlich darum ein unendliches Signal zu erzeugen. Diese Methode soll ein unendliches Signal zurückgeben,
//    das das gegebene signal "in Dauerschleife abspielt".

    //    Das neue Signal soll unendlich sein. Dementsprechend ist das Verhalten von getSize nicht definiert und kann beliebig
//    gewählt werden. Die Anzahl der Channel und die Samplerate werden von signal übernommen.
//    signal darf auch ein unendliches Signal sein. In diesem Fall kann der Wert
//    für den Channel channel und Samplepunkt index direkt von signal abgeleitet werden.
//    Ist signal endlich und index größer oder gleich der Größe von signal,
//    so muss index mittels Modu lorechnung so modifiziert werden,
//    dass er zwischen 0(inklusive) und der Größe von signal(exklusive) liegt.
//
    public static Signal cycle(Signal signal) {
        if (signal == null) {
            throw new NullPointerException("signal ist null");
        }
        if (signal.isInfinite()) {
            return signal;
        } else {
            int AnzahlChannels = signal.getChannelCount();
            int samplerate = signal.getSampleRate();
            boolean endlichkeit = true;


            Signal infiniteSignal = new SignalClass(samplerate, true, signal.getAllChannels()) {
                @Override
                public boolean isInfinite() {
                    return true;
                }

                @Override
                public int getSize() {
                    return 0;
                }

                public int getChannelCount() {
                    return AnzahlChannels;

                }

                @Override
                public double getValueAtValid(int channel, int index) {

                    int modIndex = index % signal.getSize();
                    return signal.getValueAtValid(channel, modIndex);
                }


            };

            return infiniteSignal;


        }
    }

    //    Diese Methode soll ein unendliches Signal erzeugen, das einen Channel besitzt,
//    der an jedem Samplepunkt den Wert value hat.
//    Das neue Signal ist also unendlich, das Verhalten von getSize ist undefiniert,
//    das Signal hat genau einen Channel, als Samplerate ist sampleRate zu wählen und an jeder gültigen Stelle besitzt
//    das Signal den Wert value.
//
    public static Signal infiniteFromValue(double value, int sampleRate) {

        double[] Values = {value};
        Channel channel = new Channel(Values);
        Channel[] channels = {channel};
        Signal infiniteSignalFromValue = new Signal(sampleRate, true, channels) {
            @Override
            public boolean isInfinite() {
                return true;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getChannelCount() {
                return 1;
            }

            @Override
            public int getSampleRate() {
                return sampleRate;
            }

            @Override
            public double getValueAtValid(int channel, int index) {
                if (channel != 0) {
                    throw new IllegalArgumentException("channel index: ungültig");
                } else {
                    return value;
                }
            }
        };
        return infiniteSignalFromValue;

    }

    //    Diese Methode ist dazu gedacht um Signale zu kürzen, auf die gegebene Länge count.
//    Falls count negativ ist, soll eine IllegalArgumentException geworfen werden.
//    Das neue Signal ist endlich mit der Größe count. Anzahl der Channel und Samplerate werden von source übernommen.
//    Falls die Größe von source größer als count ist, wird das source abgeschnitten.
//    Für jeden Channel channel und index kann also der Wert direkt aus source bestimmt werden.
//    Falls source weniger Samplepunkte als count besitzt, sollen alle Werte für Samplepunkt-Indizes,
//    die größer oder gleich der Größe von source sind 0 sein (source mit Nullen auf die neue Größe auffüllen).


    public static Signal take(int count, Signal source) {  //sinyali sonundan count uzunluguna getir
        // signal>count ise sondaki degerleri kes signal
        // size< count ise olmayan degerlere 0 koy

        if (count < 0) {
            throw new IllegalArgumentException("count: negativ");
        }

        int sourceSize = source.getSize();

        Signal CountSizedSignal = new Signal(source.getSampleRate(), false, source.getAllChannels()) {
            @Override
            public boolean isInfinite() {
                return false;
            }

            @Override
            public int getSize() {
                return count;
            }

            @Override
            public int getChannelCount() {
                return source.getChannelCount();
            }

            @Override
            public int getSampleRate() {
                return source.getSampleRate();
            }

            @Override                                          //signal size: 4 -> 0 1 2 3 .0 0 0
            //count 7 ->         0 1 2 3 4 5 6
            //count 2 ve 3. indexi istiyorum


            public double getValueAtValid(int channel, int index) {

                if(source.isInfinite()){
                    return source.getValueAtValid(channel,index);

                }
                else {
                    if (count <= sourceSize) {
                        return source.getValueAtValid(channel, index);
                    } else {   //count>size ise  //sonsuz ise size 0 oldugu icin buraya girdi

                        if (index < sourceSize) {
                            return source.getValueAtValid(channel, index);
                        } else {
                            return 0;
                        }


                    }
                }
            }




    };
        return CountSizedSignal;

}






//    Diese Methode ist dazu gedacht den Anfang eines Signals zu verwerfen, nämlich die ersten count Samplepunkte.
//    Falls count negativ ist, soll eine IllegalArgumentException geworfen werden.
//    Falls source unendlich groß ist, ist das resultierende Signal immer noch unendlich groß und das Verhalten
//    von getSize somit undefiniert. Falls source endlich groß ist, wird die Größe von source um count,
//    bzw. möglicherweise auf 0 reduziert (ein Signal von Größe 0 ist nicht sehr nützlich, aber durchaus legal).
//    Anzahl der Channel und Samplerate werden von source übernommen.
//    Der Wert von Channel channel am Samplepunkt index soll der selbe sein
//    wie source von Channel channel am Samplepunkt index+count.
//

    public static Signal drop(int count, Signal source) { //count kadar size azalacak, baslangictana kesilecek
                                                          // ilk count sayisinda elemani sil
        if (count < 0) {
            throw new IllegalArgumentException("count: negativ");
        }
        //0 1 2 3 4 5 6 7, size=8, count=3 dersek-> yeni size: 5->   3,4,5,6,7   ilk index=count, son index=yeni size


        if (source.isInfinite() == false && count < source.getSize()) {
            int neueSize = source.getSize() - count;


            Channel[] Channels = new Channel[source.getChannelCount()];
            for (int i = 0; i < source.getChannelCount(); i++) {
                double[] Values = new double[neueSize];
                for (int a = 0; a < Values.length; a++) {
                    Values[a] = source.getValueAt(i, a + count);
                }
                Channel newChannel = new Channel(Values);
                Channels[i] = newChannel;

            }
            Signal returnSignal = new SignalClass(source.getSampleRate(), source.isInfinite(), Channels);
            return returnSignal;
        } else if (source.isInfinite() == false && count >= source.getSize()) {
            Channel[] ChannelsSize0 = new Channel[source.getChannelCount()];

            for (int e = 0; e < source.getChannelCount(); e++) {
                double[] ValuesofSize0 = new double[0];
                Channel Channel0 = new Channel(ValuesofSize0);
                ChannelsSize0[e] = Channel0;

            }
            Signal SignalOfSizeZero = new SignalClass(source.getSampleRate(), source.isInfinite(), ChannelsSize0);
            return SignalOfSizeZero;

        } else {  //sonsuzsa
            Signal InfiniteSignal = new Signal(source.getSampleRate(), true, source.getAllChannels()) {
                @Override
                public boolean isInfinite() {
                    return true;
                }

                @Override
                public int getSize() {
                    return 0;
                }

                @Override
                public int getChannelCount() {
                    return source.getChannelCount();
                }

                @Override
                public int getSampleRate() {
                    return source.getSampleRate();
                }

                @Override
                public double getValueAtValid(int channel, int index) {
                    return source.getValueAtValid(channel, index+count);
                }
            };
                 return InfiniteSignal;
        }
    }
//    Ein DoubleUnaryOperator ist ein Interface, welches eine Methode
//    double applyAsDouble(double operand)
//    fordert.
//    Die gegebene Implementierung dieses Interfaces function soll dazu verwendet werden jeden Signalwert von source zu
//    transformieren.
//    Falls function null ist, soll eine NullPointerException geworfen werden.
//    Falls source null ist, soll eine NullPointerException geworfen werden.
//    (Un-)endlichkeit, Größe, Channelzahl und Samplerate sollen identisch zu denen von source sein.
//    Der Wert von Channel channel am Samplepunkt index soll der Wert von source von Channel channel am Samplepunkt index
//    eingesetzt in applyAsDouble von function sein.

    public static Signal transform(DoubleUnaryOperator function, Signal source) {
        if (function == null) {
            throw new NullPointerException("function ist null");
        }
        if (source == null) {
            throw new NullPointerException("source ist null");
        }
        Signal TransformedSignal = new Signal(source.getSampleRate(), source.isInfinite(), source.getAllChannels()) {
            @Override
            public boolean isInfinite() {
                return source.isInfinite();
            }

            @Override
            public int getSize() {
                return source.getSize();
            }

            @Override
            public int getChannelCount() {
                return source.getChannelCount();
            }

            @Override
            public int getSampleRate() {
                return source.getSampleRate();
            }

            @Override
            public double getValueAtValid(int channel, int index) {
                double transform = source.getValueAtValid(channel, index);
                double transformedValue = function.applyAsDouble(transform);
                return transformedValue;

            }
        };
        return TransformedSignal;


    }
//    Diese Methode soll ein Signal zurückliefern das jeden Signalwert von source um amplitude streckt oder staucht.
//    Falls source null ist, soll eine NullPointerException geworfen werden.
//    (Un-)endlichkeit, Größe, Channelzahl und Samplerate sollen identisch zu denen von source sein.
//    Der Wert von Channel channel am Samplepunkt index soll der Wert von source von
//    Channel channel am Samplepunkt index, multipliziert mit amplitude sein.

    public static Signal scale(double amplitude, Signal source) {
        if (source == null) {
            throw new NullPointerException("source: null");
        }
        //in case its infinite:

        Signal result = new Signal(source.getSampleRate(), source.isInfinite(), source.getAllChannels()) {
            @Override
            public boolean isInfinite() {
                return source.isInfinite();
            }

            @Override
            public int getSize() {
                return source.getSize();
            }

            @Override
            public int getChannelCount() {
                return source.getChannelCount();
            }

            @Override
            public int getSampleRate() {
                return source.getSampleRate();
            }

            @Override
            public double getValueAtValid(int channel, int index) {
                return source.getValueAtValid(channel, index) * amplitude;
            }
        };

        return result;


    }

    //    Diese Methode soll ein ein Signal zurückgeben, das die Samplepunkte von source ist umgekehrter Reihenfolge enthält.
//    Falls source null ist, soll eine NullPointerException geworfen werden.
//    Falls source unendlich ist, soll eine IllegalArgumentException geworfen werden.
//    Folglich soll auch das neue Signal endlich sein. Größe, Channelzahl und Samplerate sollen
//    identisch zu denen von source sein. Der Wert von Channel channel am Samplepunkt index soll der Wert von
//    source von Channel channel am Samplepunkt ("Größe von source"-1-index) sein.
//
    public static Signal reverse(Signal source) {
        if (source == null) {
            throw new NullPointerException("source ist null");
        }
        if (source.isInfinite()) {
            throw new IllegalArgumentException("source ist unendlich");
        }

        Signal result = new Signal(source.getSampleRate(), source.isInfinite(), source.getAllChannels()) {
            @Override
            public boolean isInfinite() {
                return source.isInfinite();
            }

            @Override
            public int getSize() {
                return source.getSize();
            }

            @Override
            public int getChannelCount() {
                return source.getChannelCount();
            }

            @Override
            public int getSampleRate() {
                return source.getSampleRate();

            }

            @Override
            public double getValueAtValid(int channel, int index) {
                return source.getValueAtValid(channel, source.getSize() - 1 - index);
            }
        };
        return result;

    }
//    Diese Methode soll sich so verhalten wie rampUp, mit dem Unterschied,
//    dass die Werte im einzigen Channel linear von 1 auf 0 abfallen soll.
//
    //RAM UP:
    //Diese Methode soll ein Signal zurückgeben, das "den Velauf einer Rampe" besitzt,
//    also über die Dauer duration von 0 linear bis 1 ansteigt.

    //    Das zu erzeugende Signal soll endlich, mit Größe sampleRate*duration, sein,
//    genau einen Channel besitzen und die Samplerate sampleRate haben.
//    Zum Berechnen der Signalwerte gehen Sie folgendermaßen vor:
//    Es sei im Folgenden samples die Anzahl der Samplepunkte (also die Größe des Signals).
//    Der i-te Samplepunkt soll den Wert i/(samples-1) haben.
//    Beispielsweise sieht für den Aufruf rampUp(1, 500) der Plot des Signals

    public static Signal rampDown(double duration, int sampleRate) {
        if (duration <= 0 || sampleRate <= 0) {
            throw new IllegalArgumentException("parameter: ungültig");
        }

        double gr = sampleRate * duration;
        int größe = (int) gr;

        if (größe < 2) {
            throw new IllegalArgumentException("grösse ist kleiner als 2");
        }

        int samples = größe;
        double[] values = new double[samples];

        // Değerleri 1'den 0'a doğru lineer olarak ayarla
        for (int i = 0; i < samples; i++) {
            values[i] = 1 - ((double) i / (samples - 1)); // Tersine çevrilmiş değer hesaplaması
        }

        Channel channel = new Channel(values);
        Channel[] channels = {channel};
        Signal erzeugteSignal = new SignalClass(sampleRate, false, channels);

        return erzeugteSignal;
    }

    //    Eine BiFunction<Double, Double, Double> ist ein Interface, welches die Methode
//    Double apply(Double t, Double u) fordert.
//    Die gegebene Implementierung des Interfaces function soll dazu verwendet werden die Signalwerte der Signale
//    s1 und s2 zu kombinieren.
//    Falls s1 null ist, soll eine NullPointerException geworfen werden.
//    Falls s2 null ist, soll eine NullPointerException geworfen werden.
//    Falls function null ist, soll eine NullPointerException geworfen werden.
//    Falls s1 und s2 unterschiedliche Sampleraten haben, soll eine IllegalArgumentException geworfen werden.
//    Falls s1 und s2 unterschiedliche Channelanzahlen haben, soll eine IllegalArgumentException geworfen werden.
//    Sind die Signale von unterschiedlicher Größe, wird die kleinere Größe als neue Größe für das neue Signal gewählt.
//    Folglich ist das resultierende Signal nur dann unendlich, falls sowohl s1 als auch s2 unendlich ist.
//    Zahl der Channels und die Samplerate sollen von s1 und s2 übernommen werden.
//    Das neue Signal soll im Channel channel am Samplepunkt index das Resultat von function haben,
//    wenn die Werte von s1 und s2 jeweils von Channel channel und Samplepunkt index eingesetzt werden
//    (Wert von s1 als erstes Argument.)
//
    public static Signal merge(BiFunction<Double, Double, Double> function, Signal s1, Signal s2) {
        if (s1 == null || s2 == null) {
            throw new NullPointerException("signale : null");
        }
        if (s1.getSampleRate() != s2.getSampleRate() || s1.getChannelCount() != s2.getChannelCount()) {
            throw new IllegalArgumentException("signale: ungleich");
        }
        if (function == null) {
            throw new NullPointerException("funktion: null");
        }
        boolean Endlichkeit = false;
        if (s1.isInfinite() && s2.isInfinite()) {
            Endlichkeit = true;
        }


        Signal MergedSignal = new Signal(s1.getSampleRate(), Endlichkeit, s1.getAllChannels()) {
            @Override
            public boolean isInfinite() {
                return s1.isInfinite() && s2.isInfinite();
            }

            @Override
            public int getSize() {
                return Math.min(s1.getSize(), s2.getSize());
            }

            @Override
            public int getChannelCount() {
                return s1.getChannelCount();
            }

            @Override
            public int getSampleRate() {
                return s1.getSampleRate();
            }

            @Override
            public double getValueAtValid(int channel, int index) {
                double value1 = s1.getValueAtValid(channel, index);
                double value2 = s2.getValueAtValid(channel, index);
                double merged = function.apply(value1, value2);
                return merged;
            }
        };
        return MergedSignal;
    }

//    Diese Methode soll die Signale s1 und s2 mergen, indem die Signalwerte addiert werden.
//    Falls s1 null ist, soll eine NullPointerException geworfen werden.
//    Falls s2 null ist, soll eine NullPointerException geworfen werden.
//    Falls s1 und s2 unterschiedliche Sampleraten haben, soll eine IllegalArgumentException geworfen werden.
//    Falls s1 und s2 unterschiedliche Channelanzahlen haben, soll eine IllegalArgumentException geworfen werden.
//    Sind die Signale von unterschiedlicher Größe, wird die kleinere Größe als neue Größe
//    für das neue Signal gewählt. Folglich ist das summierte Signal nur dann unendlich,
//    falls sowohl s1 als auch s2 unendlich ist. Zahl der Channels und die Samplerate sollen von s1 und s2 übernommen werden.

    public static Signal add(Signal s1, Signal s2) {
        if (s1 == null || s2 == null) {
            throw new NullPointerException("signale : null");
        }
        if (s1.getSampleRate() != s2.getSampleRate() || s1.getChannelCount() != s2.getChannelCount()) {
            throw new IllegalArgumentException("signale: ungleich");
        }


        Channel[] channels;
        int size = 0;
        if (s1.getSize() < s2.getSize()) {
            channels = s1.getAllChannels();
            size = s1.getSize();
        } else {
            channels = s2.getAllChannels();
            size = s2.getSize();
        }
        int finalSize = size;
        Signal result = new Signal(s1.getSampleRate(), s1.isInfinite() && s2.isInfinite(), channels) {
            @Override
            public boolean isInfinite() {
                return s1.isInfinite() && s2.isInfinite();
            }

            @Override
            public int getSize() {
                return finalSize;
            }

            @Override
            public int getChannelCount() {
                return s1.getChannelCount();
            }

            @Override
            public int getSampleRate() {
                return s1.getSampleRate();

            }

            @Override
            public double getValueAtValid(int channel, int index) {
                return s1.getValueAtValid(channel, index) + s2.getValueAtValid(channel, index);
            }
        };
        return result;


    }
//    Diese Methode soll die Signale s1 und s2 mergen, indem die Signalwerte multipliziert werden.
//    Falls s1 null ist, soll eine NullPointerException geworfen werden.
//    Falls s2 null ist, soll eine NullPointerException geworfen werden.
//    Falls s1 und s2 unterschiedliche Sampleraten haben, soll eine IllegalArgumentException geworfen werden.
//    Falls s1 und s2 unterschiedliche Channelanzahlen haben, soll eine IllegalArgumentException geworfen werden.
//    Sind die Signale von unterschiedlicher Größe, wird die kleinere Größe als neue Größe für das neue Signal gewählt.
//    Folglich ist das summierte Signal nur dann unendlich, falls sowohl s1 als auch s2 unendlich ist.
//    Zahl der Channels und die Samplerate sollen von s1 und s2 übernommen werden.

    public static Signal mult(Signal s1, Signal s2) {
        if (s1 == null || s2 == null) {
            throw new NullPointerException("signale : null");
        }
        if (s1.getSampleRate() != s2.getSampleRate() || s1.getChannelCount() != s2.getChannelCount()) {
            throw new IllegalArgumentException("signale: ungleich");
        }
        Signal MultSignal = new Signal(s1.getSampleRate(), s1.isInfinite() && s2.isInfinite(), s1.getAllChannels()) {
            @Override
            public boolean isInfinite() {
                return s1.isInfinite() && s2.isInfinite();
            }

            @Override
            public int getSize() {
                return Math.min(s1.getSize(), s2.getSize());
            }

            @Override
            public int getChannelCount() {
                return s1.getChannelCount();
            }

            @Override
            public int getSampleRate() {
                return s1.getSampleRate();
            }

            @Override
            public double getValueAtValid(int channel, int index) {
                double value1 = s1.getValueAtValid(channel, index);
                double value2 = s2.getValueAtValid(channel, index);
                double merged = value1 * value2;
                return merged;
            }
        };
        return MultSignal;

    }

    //    Diese Methode soll eine Liste von Signalen zu einem Signal zusammenführen,
//    indem die Signale in gegebener Reihenfolge aneinandergehängt werden.
//    Falls signals null ist, soll eine NullPointerException geworfen werden.
//    Falls signals leer ist, soll eine IllegalArgumentException geworfen werden.
//    Falls ein Signal aus signals, das nicht das letzte Signal ist, unendlich ist, soll eine IllegalArgumentException geworfen werden.
//    Falls die Signale in signals nicht alle die gleichen Sampleraten haben, soll eine IllegalArgumentException geworfen werden.
//    Falls die Signale in signals nicht alle die gleichen Channelanzahlen haben, soll eine IllegalArgumentException geworfen werden.
//    Das neue Signal soll genau dann unendlich sein, wenn das letzte Signal in signals unendlich ist. Ist dies nicht der Fall, ist das Signal endlich, wobei die Größe die Summe der Größe der Signale aus signals ist.
//    Zahl der Channels und die Samplerate sollen übernommen werden.
//    Sei s_i mit i = 0,1,2,...,n die Größe des Signals an Stelle i Der Wert von Channel channel an Stelle index soll sein:
//
//    Falls index<s_0, dann Wert des ersten Signals von Channel channel an Stelle index
//    Sonst, falls index<s_0+s_1, dann Wert des zweiten Signals von Channel channel an Stelle index-s_0
//    Sonst, falls index<s_0+s_1+s_2, dann Wert des dritten Signals von Channel channel an Stelle index-s_0-s_1
//...
//    Sonst, der Wert des letzten Signals von Channel channel an Stelle index-s_0-s_1-...-s_(n-1)
    public static Signal append(List<Signal> signals) {
        if (signals == null) {
            throw new NullPointerException("signals: null");
        }
        if (signals.isEmpty()) {
            throw new IllegalArgumentException("signals: empty");
        }
        for (int i = 0; i < signals.size() - 1; i++) {
            if (signals.get(i).isInfinite()) {
                throw new IllegalArgumentException("eine der signals ist unedlich");
            }
        }
        int CompareSampleRate = signals.get(0).getSampleRate();
        int CompareChannelAnzahl = signals.get(0).getChannelCount();

        for (int signalIndex = 0; signalIndex < signals.size(); signalIndex++) {
            if (CompareSampleRate != signals.get(signalIndex).getSampleRate() || CompareChannelAnzahl != signals.get(signalIndex).getChannelCount()) {
                throw new IllegalArgumentException("Attribute der Signals der List: nicht alle gleich");
            }
        }
        boolean Endlichkeit = signals.get(signals.size() - 1).isInfinite();
        int newSize = 0;
        for (int signalind = 0; signalind < signals.size(); signalind++) {
            newSize = newSize + signals.get(signalind).getSize();
        }
        Signal AppendSignal = new Signal(signals.get(0).getSampleRate(), signals.get(signals.size() - 1).isInfinite(), signals.get(0).getAllChannels()) {
            @Override
            public boolean isInfinite() {
                return Endlichkeit;
            }

            @Override
            public int getSize() {
                int newSize = 0;
                for (int signalind = 0; signalind < signals.size(); signalind++) {
                    newSize = newSize + signals.get(signalind).getSize();
                }
                return newSize;
            }

            @Override
            public int getChannelCount() {
                return signals.get(0).getChannelCount();
            }

            @Override
            public int getSampleRate() {
                return signals.get(0).getSampleRate();
            }

            @Override
            public double getValueAtValid(int channel, int index) {
                int gesamteSize = getSize();
                List<Map.Entry<Signal, Integer>> ListOfSizes = new ArrayList<>();
                for (Signal s : signals) {
                    Map.Entry<Signal, Integer> e = new AbstractMap.SimpleEntry<>(s, s.getSize());
                    ListOfSizes.add(e);
                }

                for (int signalsInList = 0; signalsInList < ListOfSizes.size(); signalsInList++) {
                    int currentSize = ListOfSizes.get(signalsInList).getValue();

                    // Index bu sinyalin sınırları içinde mi?
                    if (index < currentSize) {
                        return ListOfSizes.get(signalsInList).getKey().getValueAtValid(channel, index);
                    } else {
                        // Index'i bir sonraki sinyalin başlangıcına kaydır
                        index -= currentSize;
                    }
                }
                throw new IndexOutOfBoundsException("index nicht gültig");

            }
        };
        return AppendSignal;




    }

    public static Signal append(Signal... signals) {
        List<Signal> SignalList = new ArrayList<>();
        for (int i = 0; i < signals.length; i++) {
            SignalList.add(signals[i]);
        }
        return append(SignalList);
    }

//    Diese Methode soll das Signal signal um ein festes Offset verschieben, welches durch distances geben ist.
//    Jeder Eintrag in distances beschreibt die Verschiebung in einem Channel.
//    Falls signal null ist, soll eine NullPointerException geworfen werden.
//    Falls distances null ist, soll eine NullPointerException geworfen werden.
//    Falls distances nicht genauso viele Elemente wie signal Channels besitzt,
//    soll eine IllegalArgumentException geworfen werden.
//    Das neue Signal soll Endlichkeit, Größe, Channelanzahl und Samplerate von signal übernehmen.
//    Der Wert von Channel channel am Samplepunkt index soll gleich dem Wert von signal von Channel channel am Samplepunkt
//    index sein, addiert mit Wert, der an der channel-ten Stelle in distances steht.

    public static Signal translate(List<Double> distances, Signal signal) {
        if (signal == null || distances == null) {
            throw new NullPointerException("parameter: null");
        }
        if (distances.size() != signal.getChannelCount()) {
            throw new IllegalArgumentException("size von distances ist nicht gleich mit der channel-anzahl vom signal");
        }
        boolean Endlichkeit = signal.isInfinite();
        int size = signal.getSize();
        int samplerate = signal.getSampleRate();
        int Channelanzahl = signal.getChannelCount();
        Channel[] ChannelsTranslated = new Channel[Channelanzahl];

        for (int i = 0; i < Channelanzahl; i++) {
            double[] ValuesTranslated = new double[size];
            for (int a = 0; a < size; a++) {
                ValuesTranslated[a] = signal.getValueAt(i, a) + distances.get(i);

            }
            Channel newChannel = new Channel(ValuesTranslated);
            ChannelsTranslated[i] = newChannel;

        }
        Signal SignalTranslated = new SignalClass(samplerate, Endlichkeit, ChannelsTranslated);
        return SignalTranslated;
    }


    public static Signal fromPath(List<Point> points, double frequency, int sampleRate) {
        if (frequency <= 0 || sampleRate <= 0) {
            throw new IllegalArgumentException("parameter: nicht positiv");
        }
        if (points == null || points.isEmpty() || points.size() == 1) {
            throw new IllegalArgumentException("points: ungültig");
        }
        double duration = 1 / frequency;
        List<Line> LinesList = new ArrayList<>();
        for (int a = 0; a < points.size() - 1; a++) {
            Line addLine = new Line(points.get(a), points.get(a + 1));
            LinesList.add(addLine);
        }
        List<Double> LineLengths = new ArrayList<>();
        for (int i = 0; i < LinesList.size(); i++) {
            double lange = LinesList.get(i).length();
            LineLengths.add(lange);
        }
        double PathLength = 0;
        for (double l : LineLengths) {
            PathLength = PathLength + l;
        }
        List<Double> NormalizedLengths = new ArrayList<>();
        for (int s = 0; s < LineLengths.size(); s++) {
            double var = LineLengths.get(s) / PathLength;
            NormalizedLengths.add(var);
        }

//Erzeugen Sie eine Liste pointsPerLine, die für jede Line aus lines speichert,
// wieviele Samplepunkte diese Linie erhalten soll.
//Sei dabei l eine Line und nLen die normalisierte Länge von l.
// Dann kann die Dauer lDur, die der Elektronenstrahl auf dieser Linie verbringt,
// berechnet werden also duration*nLen. Weiter kann dann mit lDur*sampleRate berechnet werden,
// wievele Samplepunkte die Linie erhalten soll. Das Ergebnis ist allerdings ein Fließkommawert.
// Runden Sie diesen ab und speichern das Ergebnis als Integer in der Liste pointsPerLine ab.

        //l: eine Line
        //nLen: normalisierteLänge

        List<Integer> PointsPerLine = new ArrayList<>();
        for (int z = 0; z < NormalizedLengths.size(); z++) {
            double lDur = duration * NormalizedLengths.get(z);
            double anzahlPunkte = lDur * sampleRate;
            int PunkteInteger = (int) Math.floor(anzahlPunkte);
            PointsPerLine.add(PunkteInteger);
        }
//  Erzeugen Sie eine Liste interpolatedPoints, die alle Samplepunkte, gespeichert als Point, enthält.
//  Beginnen Sie mit einer leeren Liste. Für jede Linie aus lines, führen Sie aus:
//  Es sei numPoints die Anzahl der Samplepunkte der Linie.
//  Erzeugen Sie eine Liste indices, die die Ganzzahlen von 0(inklusive) bis numPoints(exklusive) enthält.
//  Erzeugen Sie eine Liste von Double mit dem Namen lineProgress, die die Elemente aus indices geteilt durch numPoints enthält.
//  Erzeugen Sie eine Liste mit dem Namen interpolatedPointsOfLine, die die Punkte enthält,
//  die getPointAt der Linie zurückliefert, wenn man die Elemente von lineProgress einsetzt.
//  Fügen Sie alle Elemente von interpolatedPointsOfLine am Ende von interpolatedPoints ein.
//  Nun enthält interpolatedPoints alle Informationen die für das zu erzeugende Signal notwendig sind.
//
//  Geben Sie ein Stereosignal zurück, dessen Channel 0 die x-Koordinaten der Punkte enthält und
//  dessen Channel 1 die y-Koordinaten der Punkte enthält.

        List<Point> interpolatedPoints = new ArrayList<>();
        for (int t = 0; t < LinesList.size(); t++) {
            int numPoints = PointsPerLine.get(t);
            List<Integer> indices = new ArrayList<>();
            for (int k = 0; k < numPoints; k++) {
                indices.add(k);
            }
            List<Double> LineProgress = new ArrayList<>();
            for (int e : indices) {
                LineProgress.add((double) e / numPoints);
            }
            List<Point> InterpolatedPointsOfLine = new ArrayList<>();
            for (double pr : LineProgress) {
                InterpolatedPointsOfLine.add(LinesList.get(t).getPointAt(pr));
            }
            interpolatedPoints.addAll(InterpolatedPointsOfLine);

        }
        double[] xCoord = new double[interpolatedPoints.size()];
        double[] yCoord = new double[interpolatedPoints.size()];
        for (int d = 0; d < interpolatedPoints.size(); d++) {
            xCoord[d] = interpolatedPoints.get(d).getX();
            yCoord[d] = interpolatedPoints.get(d).getY();
        }
        Channel channelX = new Channel(xCoord);
        Channel channelY = new Channel(yCoord);
        Channel[] ChannelsXY = {channelX, channelY};
        Signal erzeugteSignal = new SignalClass(sampleRate, false, ChannelsXY);
        return erzeugteSignal;
    }

    /* Optional */
    public static Signal myCoolSignal() {
        return null;
    }
}
