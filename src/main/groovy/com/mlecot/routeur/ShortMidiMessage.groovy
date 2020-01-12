package com.mlecot.routeur

import javax.sound.midi.ShortMessage

class ShortMidiMessage {

    int command
    int channel
    int data1
    int data2

    ShortMidiMessage() {

    }

    ShortMidiMessage(int command, int channel, int data1, int data2) {

        this.command = command
        this.channel = channel
        this.data1 = data1
        this.data2 = data2
    }

    ShortMidiMessage(ShortMessage shortMessage) {

        this.command = shortMessage.command
        this.channel = shortMessage.channel
        this.data1 = shortMessage.data1
        this.data2 = shortMessage.data2
    }
    ShortMessage shortMessage(){
        new ShortMessage(command,channel, data1, data2)
    }

}
