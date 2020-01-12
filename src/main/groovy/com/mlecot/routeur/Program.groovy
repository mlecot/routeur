package com.mlecot.routeur

class Program {
    List<Scene> getScenes() {
        return scenes
    }

    List<Integer> getSceneSequence() {
        return sceneSequence
    }

    Program(List<Scene> scenes, List<Integer> sceneSequence=[]) {

        this.programCode= 0
        this.scenes = scenes
        this.sceneSequence = sceneSequence
    }

    Program(int programCode,List<Scene> scenes, List<Integer> sceneSequence=[]) {

        this.programCode= programCode
        this.scenes = scenes
        this.sceneSequence = sceneSequence
    }
    Program(int programCode = 0) {
        this.programCode= programCode
        this.scenes = []
        this.sceneSequence = []
    }
    List<Scene> scenes
    List<Integer> sceneSequence

    int programCode

}
