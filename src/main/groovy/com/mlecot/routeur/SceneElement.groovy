package com.mlecot.routeur
class SceneElement {

    int channel  //midi canal
    DeviceType device
    int transposition =0 // semitone transposition


    SceneElement(int channel, int transposition,DeviceType device=DeviceType.sd2) {
        this.channel = channel
        this.transposition = transposition
        this.device=device
    }

    SceneElement(int channel,DeviceType device=DeviceType.sd2) {
        this.channel = channel
        this.transposition=0
        this.device=device
    }

    SceneElement() {
        this.channel = 0
        this.transposition=0
        this.device=DeviceType.sd2
    }


    @Override
    public String toString() {
        return "SceneElement{" +
                "channel=" + channel +
                ", device=" + device +
                ", transposition=" + transposition +
                '}';
    }
}
