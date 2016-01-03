import javax.sound.midi.*
import com.sun.media.sound.*

/**
 * Created by Michel on 06/12/2015.
 */


/**
 * Created by Michel on 29/11/2015.
 */
public class RoutingGroovy {

    ArrayList<MidiDevice> t, r

    public RoutingGroovy( tr, re){
        t=tr
        r=re
    }

   public  void connect(String transmitterName, String receiverName){


        def transmiter  = this.getMidiDevicebyName(t,transmitterName)
        def receiver = this.getMidiDevicebyName(r, receiverName)

        if (transmiter!=null && receiver!=null){
            println "Connecting ....   ${transmiter.getDeviceInfo().getName()} to  + ${receiver.getDeviceInfo().getName()}"
            try {
                transmiter.open()
                Transmitter seqTrans = transmiter.getTransmitter()

                receiver.open();
                Receiver synthRcvr = receiver.getReceiver()

                ShortMessage myMsg1 = new ShortMessage()
                /// Start playing the note Middle C (60),
                // moderately loud (velocity = 93).
                ShortMessage myMsg2 = new ShortMessage()

                try {
                    myMsg1.setMessage ShortMessage.NOTE_ON, 0, 60, 93
                    myMsg2.setMessage ShortMessage.NOTE_OFF, 0, 60, 0b1011101


                } catch (InvalidMidiDataException e)
                {e.printStackTrace()
                }
                long timeStamp = -1
                synthRcvr.send myMsg1, timeStamp
                sleep(500)
                synthRcvr.send myMsg2, timeStamp

                seqTrans.setReceiver synthRcvr
                //   seqTrans2.setReceiver(new MidiInputReceiver("toto"));
                //  Receiver toto= new MidiInputReceiver("toto");
                // toto.send (myMsg, timeStamp);
                System.out.println "Receiver name :"+receiver.getDeviceInfo().getName()  +"description : "+receiver.getDeviceInfo().getDescription()

                try {
                    Thread.sleep 100*1000;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }


        }


    }



    public MidiDevice getMidiDevicebyName(ArrayList<MidiDevice>  list, String  name){

        MidiDevice device =null;
        String nameUpper=null;
        if ((name!=null) && (!name.isEmpty())) {
            nameUpper = name.toUpperCase();

            if (list != null) for (MidiDevice dev : list) {
                String devName = dev.getDeviceInfo().getName().toUpperCase();
                if (devName.contains(nameUpper)){
                    device=dev;
                    break;
                }

            }
        }

        return device;

    }

    public void displayInfo3(){
        def devices= MidiSystem.getMidiDeviceInfo();
        devices.each {
            println "Device Name : ${it.getName()} "
            println "Device Description :  ${it.getDescription()}"
            def device=null;
            try {
                device = MidiSystem.getMidiDevice(it);
                println "Device Class :  ${device.getClass().getName()}"

            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        }

    }
    public void displayInfo4(){


       println " Display info 4"
        MidiDevice.Info[] deviceInfos = MidiSystem.getMidiDeviceInfo();
        deviceInfos.each {
         def device=null;
            try {
                device = MidiSystem.getMidiDevice(it);
                println "Device Class : ${ device.getClass().getName()}\n"
                if ( device.getMaxTransmitters()!=0){

                    println "Device IN ${device.getMaxTransmitters()} Name : ${it.getName()} Description : ${it.getDescription()}"


                }
                if ( device.getMaxReceivers()!=0){
                    println "Device OUT  IN ${device.getMaxReceivers()} Name : ${it.getName()} Description : ${it.getDescription()}"
                }

            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        }

    }

    }
      class MidiInputReceiver implements Receiver {
      public void send(MidiMessage msg, long timeStamp) {
             println "midi received"
        }
        public void close() {}
    }



ArrayList<MidiDevice> transmitters =[]
ArrayList<MidiDevice> receivers = []



def initMidiComponents(transmitters,receivers) {
    def mididevices = MidiSystem.getMidiDeviceInfo()

    mididevices.each {

        MidiDevice device = null;
        try {
            device = MidiSystem.getMidiDevice( it);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        if (device.getMaxTransmitters() != 0) {
            transmitters.add(device);
        }
        if (device.getMaxReceivers() != 0) {
            receivers.add(device);
        }
    }
}



def displayInfo(t, r){

    println "\nTRANSMITTERS (Midi IN)"

   t.each{
        println "name: ${it.getDeviceInfo().getName()}"
    }
    println "\nReceivers (MIDI OUT)";

    r.each{
    println "name: ${it.getDeviceInfo().getName()}"
}}



def routing = new RoutingGroovy(transmitters,receivers);
initMidiComponents(transmitters,receivers);
displayInfo(transmitters,transmitters);
//routing.displayInfo3()
routing.connect("STUDIOLOGIC" , "Steinberg")

