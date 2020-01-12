package com.mlecot.routeur

import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver

class MidiInputReceiver implements Receiver{

    public String name;
    Receiver sd2Rec,digitRec,claviaRec
    SceneManager sceneManager
    MidiMessageDecoder decoder
    public MidiInputReceiver(Receiver sd2Rec,Receiver digitRec,Receiver claviaRec) {
        sceneManager=SceneManager.getInstance()
        sceneManager.sd2Rec = sd2Rec
        sceneManager.digitRec = digitRec
        sceneManager.claviaRec = claviaRec

        decoder=MidiMessageDecoder.getInstance()
    }


    public void send(MidiMessage msg, long timeStamp) {

        sceneManager.processMidiMessage(msg,timeStamp)
   }
    public void close() {}

}
