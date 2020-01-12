package com.mlecot.routeur
class Scene {
    SceneType sceneType
    List<SceneElement>   elements
    List<MidiTimeStampMessage> updateSceneCodes



    Scene(SceneType sceneType, List<SceneElement> elements) {
        this.sceneType = sceneType
        this.elements = elements
    }

    Scene() {
        this.sceneType = SceneType.SIMULTANEOUS
        this.elements = []
        this.updateSceneCodes = []
    }
    Scene(List<SceneElement> elements) {
        this()
        this.elements = elements

    }
    Scene(List<SceneElement> elements,List<MidiTimeStampMessage> updateSceneCodes,SceneType sceneType =SceneType.SIMULTANEOUS) {
        this()
        this.elements = elements
        this.updateSceneCodes = updateSceneCodes
    }

    Scene(SceneElement element) {
        this()
        this.elements = [element]

    }

    Scene(SceneElement element,List<MidiTimeStampMessage> updateSceneCodes) {
        this()
        this.elements = [element]
        this.updateSceneCodes = updateSceneCodes
    }




    @Override
    public String toString() {
        return "Scene{" +
                "sceneType=" + sceneType +
                ", elements=" + elements +
                ", updateSceneCodes=" + updateSceneCodes +
                '}';
    }
}

