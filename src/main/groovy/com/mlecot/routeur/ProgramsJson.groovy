package com.mlecot.routeur

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper

import org.codehaus.groovy.runtime.StackTraceUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory


import javax.sound.midi.ShortMessage

class ProgramsJson {

    Logger logger
    private static ProgramsJson _instance
    Scene defaultScene
    private Map<Integer, Program> programSceneList

    Thread readJSONFileTask

    synchronized Map<Integer, Program> getProgramSceneList() {

        programSceneList

    }

    public synchronized static ProgramsJson getInstance() {

        if (_instance == null) _instance = new ProgramsJson()
        _instance
    }

    private ProgramsJson() {
        defaultScene = new Scene()
        logger = LoggerFactory.getLogger(this.class)
        readJSONFileTask = new Thread(new ReadJSONFileTask())
        readJSONFileTask.start()
//        initializeProgramSceneList()
//        def builder = new groovy.json.JsonBuilder()

//        def root = new groovy.json.JsonBuilder (  programSceneList  )

//        def root0 = new groovy.json.JsonBuilder (  programSceneList[0] )
//        def root1 = new groovy.json.JsonBuilder (  programSceneList[1] )
//        def root2 = new groovy.json.JsonBuilder (  programSceneList[2] )
//        def root3 = new groovy.json.JsonBuilder (  programSceneList[3] )
        //def slurper =new JsonSlurper()
        //def slurpermap =slurper.parseText(new File("file.xml").getText())
        //println slurpermap
/*        new File("./programs/program.json").write(root.toPrettyString())

        new File("./programs/program0.json").write(root0.toPrettyString())
        new File("./programs/program1.json").write(root1.toPrettyString())
        new File("./programs/program2.json").write(root2.toPrettyString())
    //    new File("./programs/program3.json").write(root3.toPrettyString())


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File("programs/car.json"),programSceneList )
        objectMapper.writeValue(new File("programs/car1.json"),programSceneList[1] );
        objectMapper.writeValue(new File("programs/car3.json"),programSceneList[3] );
        Program program1 = objectMapper.readValue(new File("programs/program1.json"), Program.class);
        Program program3 = objectMapper.readValue(new File("programs/program3.json"), Program.class);
     //   Map<String,Program> resultMap = objectMapper.readValue(new File("programs/car.json"), new TypeReference<Map<String,Program>>(){});
        List<Program> resultMap = objectMapper.readValue(new File("programs/program.json"), new TypeReference<List<Program>>(){});


        println resultMap//  Map<Integer, Program> mapResult = []
     //   mapResult.addAll (result.collectEntries{key,value -> [Integer.parseInt((String)key) , getProgram(value) ]})
*/

    }

//    Program getProgram(Map map){
///        return new Program(getSceneList(map["scenes"],getSceneSequence(map["sceneSequence"])))
///    })))
///    }

    //   List<Scene> getSceneList(List sceneList) {
    //       scenelist.collect([]){Map map-> new Scene(getElementList(map["elements"]),getSceneCodeList(map["updateSceneCodes"]))}
    //   }

    def getCurrentMethodName() {
        def marker = new Throwable()
        return StackTraceUtils.sanitize(marker).stackTrace[1].methodName
    }

    private void initializeProgramSceneList() {
        Long lastExpressionTimestamp = 0
        logger.info "Scene Method name: " + getCurrentMethodName()
        List<Scene> sceneListProgram_1 = [
                new Scene(new SceneElement(1, 12), [
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 10, 60, 100), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 7, 7, 0), lastExpressionTimestamp, DeviceType.digit)

                ]),
                new Scene(new SceneElement(2, 12),
                        [
                                new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 10, 60, 0), lastExpressionTimestamp, DeviceType.digit),
                                new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 7, 7, 100), lastExpressionTimestamp, DeviceType.digit)

                        ]),
                new Scene(new SceneElement(3)),
                new Scene([new SceneElement(1), new SceneElement(1, 4)]),
                new Scene(SceneType.SEPARATED, [
                        new SceneElement(1, 12
                        ),
                        new SceneElement(2, 12),
                        new SceneElement(2, 12),
                        new SceneElement(2, 24)
                ])
        ]

        List<Scene> sceneListProgram_2 = [
                new Scene(new SceneElement(1, 12
                )),
                new Scene(SceneType.SEPARATED, [
                        new SceneElement(1, 12
                        ),
                        new SceneElement(2, 12),
                        new SceneElement(2, 12),
                        new SceneElement(2, 24
                        )
                ])

        ]
        List<Scene> sceneListProgram_3 = [
                new Scene([], [
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 10, 7, 75), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 8, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 7, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 6, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 5, 7, 0), lastExpressionTimestamp, DeviceType.digit)
                ]),
                new Scene([], [
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 10, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 8, 7, 120), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 7, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 6, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 5, 7, 0), lastExpressionTimestamp, DeviceType.digit)
                ]),
                new Scene([], [
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 10, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 8, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 7, 7, 70), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 6, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 5, 7, 0), lastExpressionTimestamp, DeviceType.digit)
                ]), new Scene([], [

                new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 10, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 8, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 7, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 6, 7, 126), lastExpressionTimestamp, DeviceType.digit),
                new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 5, 7, 0), lastExpressionTimestamp, DeviceType.digit)
        ]),
                new Scene([], [
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 10, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 8, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 7, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 6, 7, 0), lastExpressionTimestamp, DeviceType.digit),
                        new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, 5, 7, 126), lastExpressionTimestamp, DeviceType.digit)
                ])

        ]

        programSceneList = [new Program(0, [defaultScene]),
                            new Program(1, sceneListProgram_1),
                            new Program(2, sceneListProgram_2),
                            new Program(3, sceneListProgram_3, [0, 1, 0, 3, 2, 0, 4])
        ]
    }

    class ProgramFile {

        Program program
        long timestamp

        ProgramFile(Program program, long timestamp) {
            this.program = program
            this.timestamp = timestamp
        }
    }

    class ReadJSONFileTask implements Runnable {

        Logger log = LoggerFactory.getLogger(this.class)
        Boolean running

        Map<String, ProgramFile> currentPogramFileMap

        String directoryFilePath = "programs/"

        ReadJSONFileTask() {
            super()
            currentPogramFileMap = [:]
            processFileList()
       }

        @Override
        void run() {
            running = true
            currentPogramFileMap = [:]

            while (running) {

                processFileList()
                Thread.sleep(2000) // 2sconds step
            }

        }

        void processFileList() {

            FilenameFilter filenameFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".json");
                }
            }

            File[] files = new File(directoryFilePath).listFiles(filenameFilter);

            boolean anyResult
            files.each { File file ->

                String currentKey = file.getPath()
                boolean result = false
                // boolean result= ( ! (currentPogramFileMap. file.getPath()
                ProgramFile currentProgramfile = currentPogramFileMap.get(currentKey)
                if (!currentProgramfile) {
                    log.info "file: " + currentKey + " Not in database"
                    currentPogramFileMap.put(currentKey, new ProgramFile(getProgram(file), file.lastModified()))
                    anyResult = true

                } else if (file.lastModified() > currentProgramfile.timestamp) {
                    log.info "file: " + currentKey + " Not up to date in database"
                    currentPogramFileMap[currentKey] = new ProgramFile(getProgram(file), file.lastModified())
                    anyResult = true
                }
            }

            if (anyResult) {
                updateProgramSceneList(currentPogramFileMap)

            }
        }

        Program getProgram(File file) {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS)
            Program result
            try {
                result = objectMapper.readValue(file, Program.class);
            } catch (Exception e) {
                log.info("Mapper exception for file " + file.getAbsolutePath(), e)
            }

            result

        }

        public void stop() {
            running = false
        }
    }

    synchronized void updateProgramSceneList(Map<String, ProgramFile> pogramFileMap) {

        programSceneList = [:]

        pogramFileMap.each { String key,ProgramFile pf ->

            Program program = pf.program
            if (program && program.programCode != null) {
                programSceneList.put(program.programCode, program)
                logger.info("program n° " + program.programCode + " added from file "+ key )
            }
        }
        logger.info("programSceneList contains " + programSceneList.size() + " Elements")
        println ("programSceneList contains " + programSceneList.size() + " Elements")
    }

}
