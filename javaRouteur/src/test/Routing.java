package test;

import javax.sound.midi.*;
import java.util.ArrayList;

import static javax.sound.midi.MidiSystem.getMidiDevice;
import static javax.sound.midi.MidiSystem.getMidiDeviceInfo;

/**
 * Created by Michel on 29/11/2015.
 */
public class Routing {


    private MidiDevice.Info[]  mididevices;

   private ArrayList<MidiDevice> receivers= new ArrayList<MidiDevice>();
    private ArrayList<MidiDevice> transmitters= new ArrayList<MidiDevice>();

    MidiDevice transmiter=null;
    MidiDevice receiver= null ;

    public static void main(String[] args) {


        Routing routing = new Routing();
        routing.displayInfo();
       // routing.displayInfo3();
        //routing.displayInfo4();
       routing.connect("STUDIOLOGIC" , "Steinberg");

    }
    public  void connect(String transmitterName, String receiverName){

        transmiter  = this.getMidiDevicebyName(transmitters,transmitterName);
        receiver =  this.getMidiDevicebyName(receivers,receiverName);

        if (transmiter!=null && receiver!=null){
            System.out.println("Connecting ....");
            try {
                transmiter.open();
                Transmitter   seqTrans = transmiter.getTransmitter();
                Transmitter   seqTrans2 = transmiter.getTransmitter();
         //       receiver   = MidiSystem.getSynthesizer();
                receiver.open();
               Receiver synthRcvr = receiver.getReceiver();

                  ShortMessage myMsg = new ShortMessage();
                 /// Start playing the note Middle C (60),
                // moderately loud (velocity = 93).
                try {
                    myMsg.setMessage(ShortMessage.NOTE_ON, 0, 60, 93);
                } catch (InvalidMidiDataException e)
                    {e.printStackTrace();
                }
                long timeStamp = -1;
                synthRcvr.send(myMsg, timeStamp);
                seqTrans.setReceiver(synthRcvr);
             //   seqTrans2.setReceiver(new MidiInputReceiver("toto"));
              //  Receiver toto= new MidiInputReceiver("toto");
               // toto.send (myMsg, timeStamp);
             System.out.println("Receiver name :"+receiver.getDeviceInfo().getName()  +"description : "+receiver.getDeviceInfo().getDescription()    );

                try {
                    Thread.sleep(100*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }


        }


    }

    public Routing() {

       mididevices = getMidiDeviceInfo();
        for (MidiDevice.Info deviceInfo : mididevices) {

            MidiDevice device = null;
            try {
                device = getMidiDevice(deviceInfo);
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }

            if ( device.getMaxTransmitters()!=0) {
                transmitters.add(device);
            }
            if (device.getMaxReceivers()!=0) {
                receivers.add(device);
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
        MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info deviceInfo : devices) {
            System.out.println("Device Name : " + deviceInfo.getName());
            System.out.println("Device Description : " + deviceInfo.getDescription());
            MidiDevice device=null;
            try {
                device = MidiSystem.getMidiDevice(deviceInfo);
                System.out.println("Device Class : "+ device.getClass().getName()  + "\n");

            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        }

    }
    public void displayInfo4(){


        System.out.println(" Display info 4");
        MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info deviceInfo : devices) {


              MidiDevice device=null;
            try {
                device = MidiSystem.getMidiDevice(deviceInfo);
                System.out.println("Device Class : "+ device.getClass().getName()  + "\n");
                if ( device.getMaxTransmitters()!=0){

                    System.out.println("Device IN "+device.getMaxTransmitters()+" Name : " + deviceInfo.getName());
                    System.out.println("Device IN Description : " + deviceInfo.getDescription());


                }
                if ( device.getMaxReceivers()!=0){

                    System.out.println("Device OUT "+ device.getMaxReceivers()+" Name : " + deviceInfo.getName());
                    System.out.println("Device OUT Description : " + deviceInfo.getDescription());


                }

            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        }

    }





        public void displayInfo(){

        System.out.println("\nTRANSMITTERS (Midi IN)");
        for (MidiDevice dev : transmitters) {
            System.out.println("name :" + dev.getDeviceInfo().getName());
        }

        System.out.println("\nReceivers (MIDI OUT)");
        for (MidiDevice seq : receivers){
            System.out.println("name "+seq.getDeviceInfo().getName() + " Vendor "+ seq.getDeviceInfo().getVendor() );
        }









    }
    public class MidiInputReceiver implements Receiver {
        public String name;
        public MidiInputReceiver(String name) {
            this.name = name;
        }
        public void send(MidiMessage msg, long timeStamp) {
            System.out.println("midi received");
        }
        public void close() {}
    }



}
