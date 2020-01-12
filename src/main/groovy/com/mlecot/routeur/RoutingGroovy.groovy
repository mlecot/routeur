package com.mlecot.routeur

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.sound.midi.*

/**
 * Class managing Midi interfaces
 * 
 * 
 */
class RoutingGroovy implements Runnable {
 
    private  ArrayList<MidiDevice> midiTransmitters = []
    private  ArrayList<MidiDevice> midiReceivers = []
    volatile boolean running


    Logger logger = LoggerFactory.getLogger(RoutingGroovy.class)


    String digitTransmiterName, sd2ReceiverName,digitReceiverName, claviaReceiverName, commandTransmitterName

    /**
     * Routing Groovy constructor
     * 
     * @param digitTransmiterName
     * @param sd2ReceiverName
     * @param commandTransmitterName
     */
    RoutingGroovy(String digitTransmiterName, String sd2ReceiverName, String commandTransmitterName) {

        logger.info("Hello World")
        running = true

        this.digitTransmiterName = digitTransmiterName
        this.sd2ReceiverName = sd2ReceiverName
        this.commandTransmitterName = commandTransmitterName

        this.initMidiComponents()
        displayInfo4()
        ///  connect("UM-ONE" , "UM-ONE", "Real Time Sequencer" )

    }
    /**
     * Routing Groovy constructor
     * 
     * @param digitTransmiterName
     * @param sd2ReceiverName
     * @param digitReceiverName
     * @param claviaReceiverName
     */
   RoutingGroovy(String digitTransmiterName, String digitReceiverName, String sd2ReceiverName, String claviaReceiverName) {

        logger.info("Hello World")
        running = true

        this.digitTransmiterName = digitTransmiterName
        this.sd2ReceiverName = sd2ReceiverName
        this.digitReceiverName   = digitReceiverName
        this.claviaReceiverName = claviaReceiverName
        
        this.initMidiComponents()
        displayInfo4()
        ///  connect("UM-ONE" , "UM-ONE", "Real Time Sequencer" )

    }

    void connect() {


        MidiDevice inputTransmiter = this.getMidiDevicebyName(midiTransmitters, digitTransmiterName)
        MidiDevice commandTransmiter = this.getMidiDevicebyName(midiTransmitters, commandTransmitterName)

        MidiDevice sd2Receiver = this.getMidiDevicebyName(midiReceivers, sd2ReceiverName)
        MidiDevice digitReceiver = this.getMidiDevicebyName(midiReceivers, digitReceiverName)
        MidiDevice claviaReceiver = this.getMidiDevicebyName(midiReceivers, claviaReceiverName)


        if (inputTransmiter != null  ) {
            logger.info "Connecting ....   ${inputTransmiter.getDeviceInfo().getName()} to  + " +
                    "${sd2Receiver?.getDeviceInfo()?.getName()}"
            try {
                Transmitter inputTrans = inputTransmiter.getTransmitter()
                inputTransmiter.open()
                Receiver sd2Rec,digitRec,claviaRec
                Transmitter commandTrans

                if (commandTransmiter !=null) {
                 //   commandTrans = commandTransmiter.getTransmitter()
                    logger.info('Command Transmitter opened ?' + (commandTransmiter.isOpen() ? "true" : "false"))
                    commandTransmiter.open()
                    logger.info('Command Transmitter opened ...')
                }
                 if (sd2Receiver != null) {
                      sd2Rec = sd2Receiver.getReceiver()
                     sd2Receiver.open()
                 }

                if (digitReceiver != null) {
                    digitRec = digitReceiver.getReceiver()
                    digitReceiver.open()
                }

                if (claviaReceiver != null) {
                    claviaRec = claviaReceiver.getReceiver()
                    claviaReceiver.open()
                }

                ShortMessage myMsg1 = new ShortMessage()
                /// Start playing the note Middle C (60),
                // moderately loud (velocity = 93).
                ShortMessage myMsg2 = new ShortMessage()


                try {
                    myMsg1.setMessage ShortMessage.NOTE_ON, 0, 60, 93
                    myMsg2.setMessage ShortMessage.NOTE_OFF, 0, 60, 0b1011101


                } catch (InvalidMidiDataException e) {
                    e.printStackTrace()
                }
                long timeStamp = -1
                sd2Rec?.send myMsg1, timeStamp
                sleep 500
                sd2Rec?.send myMsg2, timeStamp

                inputTrans.setReceiver(

                        //using my own MidiInputReceiver
                        new MidiInputReceiver (sd2Rec,digitRec,claviaRec) )

                logger.info "Receiver name :" + sd2Receiver?.getDeviceInfo()?.getName() + "description : " + sd2Receiver?.getDeviceInfo()?.getDescription()



            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }


        }


    }


    public MidiDevice getMidiDevicebyName(ArrayList<MidiDevice> list, String name) {

        MidiDevice device = null;
        String nameUpper = null;
        if ((name != null) && (!name.isEmpty())) {
            nameUpper = name.toUpperCase();

            if (list != null) for (MidiDevice dev : list) {
                String devName = dev.getDeviceInfo().getName().toUpperCase();
                if (devName.endsWith(nameUpper)) {
                    device = dev;
                    break;
                }

            }
        }

        return device;

    }

    public void displayInfo3() {
        def devices = MidiSystem.getMidiDeviceInfo();
        devices.each {
            logger.info "Device Name : ${it.getName()} "
            logger.info "Device Description :  ${it.getDescription()}"
            def device = null;
            try {
                device = MidiSystem.getMidiDevice(it);
                logger.info "Device Class :  ${device.getClass().getName()}"

            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        }

    }

    public void displayInfo4() {


        logger.info " Display info 4"
        MidiDevice.Info[] deviceInfos = MidiSystem.getMidiDeviceInfo();
        deviceInfos.each {
            def device = null;
            try {
                device = MidiSystem.getMidiDevice(it);
                logger.info "Device Class : ${device.getClass().getName()}\n"
                if (device.getMaxTransmitters() != 0) {

                    println "Device IN ${device.getMaxTransmitters()} Name : '${it.getName()}' Description : ${it.getDescription()}"
                    logger.info "Device IN ${device.getMaxTransmitters()} Name : '${it.getName()}' Description : ${it.getDescription()}"


                }
                if (device.getMaxReceivers() != 0) {
                    logger.info "Device OUT  IN ${device.getMaxReceivers()} Name : '${it.getName()}' Description : ${it.getDescription()}"
                    println "Device OUT  IN ${device.getMaxReceivers()} Name : '${it.getName()}' Description : ${it.getDescription()}"
                }

            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Initializing available Midi transmitters and receivers.
     */
 
    private void initMidiComponents() {

        MidiSystem.getMidiDeviceInfo().each { midiDevice ->

            MidiDevice device = null;
            try {
                device = MidiSystem.getMidiDevice(midiDevice);
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }

            if (device.getMaxTransmitters() != 0) {
                this.midiTransmitters.add(device);
                logger.info " Transmiter (Extern Input) Found ; name: ${device.getDeviceInfo().getName()}"
            }
            if (device.getMaxReceivers() != 0) {
                this.midiReceivers.add(device);
                logger.info " Receiver (Extern Output) Found ; name: ${device.getDeviceInfo().getName()}"
            }
        }
    }

    def displayInfo() {

        logger.info "\nTRANSMITTERS (Midi IN)"

        midiTransmitters.each {
            logger.info "name: ${it.getDeviceInfo().getName()}"
        }
        logger.info "\nReceivers (MIDI OUT)";

        midiReceivers.each {
            logger.info "name: ${it.getDeviceInfo().getName()}"
        }
    }

    def displayMessage(MidiMessage msg) {

        Map<String, String> displayCommand = ['$ShortMessage.PROGRAM_CHANGE.toString()': 'PROGRAM_CHANGE',
                                              '$ShortMessage.CONTROL_CHANGE.toString()': 'CONTROL_CHANGE']



        if (msg instanceof ShortMessage) {
            ShortMessage sMsg = (ShortMessage) msg
            logger.info("Display ShortMessage midi: Longueur : {} Commande: {},Canal: {} , Donnée1 : {} Donnée 2 {} ",
                    sMsg.length, displayCommand.get(sMsg.command.toString()), sMsg.channel, sMsg.data1, sMsg.data2)
            logger.info("Display ShortMessage midi:" + sMsg.toString())

        } else {
            if (msg instanceof SysexMessage) {
                SysexMessage sysexMsg = SysexMessage msg
                logger.info("Reception de message Sysex :" + sysexMsg)
            } else
                logger.info("Reception de message inconnu de classe " + msg.class.getCanonicalName())
        }
    }

    @Override
    void run() {

        connect()
        while (running) {
            try {
                Thread.sleep 10 * 1000;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
