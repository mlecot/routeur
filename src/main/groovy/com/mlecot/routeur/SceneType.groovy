package com.mlecot.routeur
enum SceneType {
    SIMULTANEOUS, SEPARATED

    SceneType getSceneType( String stringValue) {

        SceneType result = values().find({name == stringValue})
        return (result?result:SIMULTANEOUS)

    }
}