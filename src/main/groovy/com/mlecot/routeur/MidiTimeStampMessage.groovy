package com.mlecot.routeur

import javax.sound.midi.ShortMessage

class MidiTimeStampMessage {

    ShortMidiMessage message
    long    timeStamp
    DeviceType device=DeviceType.sd2

    MidiTimeStampMessage(ShortMidiMessage message, long timeStamp=0, DeviceType device=DeviceType.sd2) {
        this.message = message
        this.timeStamp = timeStamp
        this.device = device
    }

    MidiTimeStampMessage(ShortMessage message, long timeStamp=0, DeviceType device=DeviceType.sd2) {
        this.message = new ShortMidiMessage(message)
        this.timeStamp = timeStamp
        this.device = device
    }
    MidiTimeStampMessage() {
        this.message = null
        this.timeStamp = 0
        this.device = DeviceType.clavia
    }
}
