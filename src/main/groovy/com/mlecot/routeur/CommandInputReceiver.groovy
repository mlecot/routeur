package com.mlecot.routeur

import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver


class CommandInputReceiver implements Receiver {

    private MidiMessageDecoder decoder= MidiMessageDecoder.getInstance()

    public CommandInputReceiver() {

    }


    public void send(MidiMessage msg, long timeStamp) {
        try {
            println "midi message  received length :${msg.length} Status: ${msg.status} Hexa : ${msg.message.encodeHex()}  Class : ${msg.class.canonicalName}"
            // interpreter le message
            //preparer le changement de scène.
            // if (msg instanceof FastShortMessage) {
            //     ShortMessage sMsg= new ShortMessage (msg.message[0],(msg.length > 1) ? msg.message[1]:null,
            //             (msg.length > 2)? msg.message[2]:null )

            Date date = new Date()
            print date.format("yyyyMMdd-HH:mm:ss.SSS")
            println  decoder.decodeMessage(msg)




        } catch (Throwable e){
            e.printStackTrace()
        }




    }
    public void close() {}
}
