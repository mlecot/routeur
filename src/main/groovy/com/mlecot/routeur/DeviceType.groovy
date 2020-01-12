package com.mlecot.routeur

/**
 * Type of device to output
 */

enum DeviceType {


    digit,sd2,clavia

    @Override
    public String toString() {
        return "DeviceType{}"
    }

    DeviceType getDeviceType( String stringValue) {

        DeviceType result = values().find({name == stringValue})
        return (result?result:sd2)

    }
}