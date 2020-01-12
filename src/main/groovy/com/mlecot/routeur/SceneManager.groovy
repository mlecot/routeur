package com.mlecot.routeur

import groovy.json.JsonSlurper
import org.codehaus.groovy.runtime.StackTraceUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage

class SceneManager {

    Logger logger
    private static SceneManager _instance
    private static int commandChannel = 0

    Receiver sd2Rec, digitRec, claviaRec
    Integer sceneCandidateNumber
    Integer currentSceneNumber
    Integer programCandidateNumber
    Integer currentProgramNumber

    boolean changeProgramTriggered = false

    int lastExpressionValue = 0
    long lastExpressionTimestamp = 0

    Set<Note> playedNoteList = []
    List<List<Integer>> playedNotesChannels = []
    Scene defaultScene
    Scene currentScene
    List<Scene> currentSceneList
    MidiMessageDecoder decoder


    List<Integer> currentSequence = []
    Integer currentSequenceNumber = 0


    def getCurrentMethodName() {
        def marker = new Throwable()
        return StackTraceUtils.sanitize(marker).stackTrace[1].methodName
    }


    private SceneManager() {
        logger = LoggerFactory.getLogger(SceneManager.class)

        defaultScene = new Scene()
        currentScene = defaultScene

        sceneCandidateNumber = 0
        currentSceneNumber = 0
        programCandidateNumber = 0
        currentProgramNumber = 0

        List<List<Integer>> playedNotesChannels = []
        decoder = MidiMessageDecoder.getInstance()
        currentSceneList = []

        currentSequence = []
        currentSequenceNumber = 0

        changeProgramTriggered = false


    }


    public synchronized static SceneManager getInstance() {

        if (_instance == null) _instance = new SceneManager()
        _instance
    }


    public List<MidiTimeStampMessage> updateSceneCandidate(int programChangeNumber) {

        logger.info " Method : " + getCurrentMethodName() + "PC number" + programChangeNumber


        List<MidiTimeStampMessage> result = []
        if (currentSceneList.size())
            sceneCandidateNumber = programChangeNumber % currentSceneList.size()
        else
            sceneCandidateNumber = 0
        logger.debug "playedNoteList Size : ${playedNoteList.size()} "

        if ((currentSceneNumber != sceneCandidateNumber)
                && playedNoteList.size() == 0) {
            result = changeSceneNumber()
        }
        logger.info("Updating Scene Candidate to Scene ${sceneCandidateNumber}")

        result
    }

    private List<MidiTimeStampMessage> updateProgramCandidate(int controlChangeNumber) {
        logger.info " Method: " + getCurrentMethodName() + "CC number" + controlChangeNumber


        List<MidiTimeStampMessage> result = []
        programCandidateNumber = controlChangeNumber % 10

        logger.debug "playedNoteList Size : ${playedNoteList.size()} "
        changeProgramTriggered = true
        if (playedNoteList.size() == 0) {
            sceneCandidateNumber = 0
            result = changeSceneNumber()
            logger.info("Updating program and Scene Candidate to program ${programCandidateNumber}")

        }
        result

    }

    private void storeExpressionValue(int expressionValue) {
        logger.info "Scene Method name: " + getCurrentMethodName()

        lastExpressionValue = expressionValue
    }

    private List<MidiTimeStampMessage> changeSceneNumber() {

        logger.info "Scene Method name: " + getCurrentMethodName()

        boolean changed = false

        List<MidiTimeStampMessage> result = []


        if (changeProgramTriggered == true) {
            changeProgramTriggered = false
            currentProgramNumber = programCandidateNumber
            sceneCandidateNumber = 0
            currentSequence = ProgramsJson.instance.programSceneList[currentProgramNumber]?.sceneSequence
            if (currentSequence && currentSequence.size() > 0) {
                currentSequenceNumber = 0
                sceneCandidateNumber = currentSequence[currentSequenceNumber]
            }
            if (!currentSequence) currentSequence = []
            changed = true
        } else if (currentSceneNumber != sceneCandidateNumber) changed = true


        if (changed) {
            try {
                currentScene = defaultScene
                println "changing Scene Number to program ${programCandidateNumber}   ${currentSequenceNumber} a" +
                        "" +
                        " Scene ${sceneCandidateNumber}"

                logger.info "changing Scene Number to program ${programCandidateNumber} Scene ${sceneCandidateNumber}"

                currentSceneList = ProgramsJson.getInstance().programSceneList[programCandidateNumber].scenes
                currentSceneNumber = sceneCandidateNumber % currentSceneList.size()
                currentScene = currentSceneList.get(currentSceneNumber)
                result = this.sendSceneCodes(currentScene)
                result.addAll(getExpressions(currentScene))
                List<List<Integer>> playedNotesChannels = []

            } catch (Exception e) {
                logger.info "Error : scene does not exist  program ${programCandidateNumber} Scene ${sceneCandidateNumber}"
                e.printStackTrace()
            }
        }
        logger.info " Program number ${programCandidateNumber} effective scene Number : ${currentSceneNumber}  Scene :  ${currentScene} "

        result
    }

private List<MidiTimeStampMessage> getExpressions(Scene scene) {

        logger.info "Scene Method name: " + getCurrentMethodName()

        List<MidiTimeStampMessage> result = []
        List<SceneElement> elements = scene.elements.findAll { SceneElement se -> se.device == DeviceType.sd2 || se.device == DeviceType.clavia }

        if (elements && elements.size() > 0)
            elements.each { el ->

                if (el != null) {
                    def element = (SceneElement) el
                    result.add(new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, element.channel, 11, lastExpressionValue),
                            lastExpressionTimestamp, element.device))
                }

            }
        result
    }

    private List<MidiTimeStampMessage> sendSceneCodes(Scene scene) {

        logger.info "Scene Method name: " + getCurrentMethodName()

        List<MidiTimeStampMessage> result = scene.updateSceneCodes
        if (!result) result = []
        result
    }

    /*
        Traite des note on sur le canal de commande.
        Comptabilise dans la playnode list le nombre de notes jouées.
        Envoi sur le SD2 ou le clavia le note on avec le numero de canal et la transposition specifiés dans le scene element


     */


    private List<MidiTimeStampMessage> processCommandChannelNoteOn(int noteValue, int velocity, long timeStamp) {

        logger.info "Scene Method name: " + getCurrentMethodName()

        List<MidiTimeStampMessage> result = []

        try {
            if (currentScene.sceneType == SceneType.SIMULTANEOUS) {
                playedNoteList.add(new Note(noteValue,
                        velocity))
                logger.info "playedNoteList Size : ${playedNoteList.size()} "
                List<SceneElement> elements = currentScene.elements.findAll { SceneElement se -> se.device == DeviceType.sd2 || se.device == DeviceType.clavia }
                if ((elements != null) && (elements.size() > 0)) {

                    elements.each { el ->
                        logger.info("element :" + el.toString())
                        SceneElement element = (SceneElement) el
                        if (element) result.add((new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.NOTE_ON, element.channel, noteValue + element.transposition, velocity),
                                timeStamp, element.device)))
                    }
                }
            } else {
                playedNoteList.add(new Note(noteValue,
                        velocity))
                logger.info "playedNoteList: Size ${playedNoteList.size()} "
                List<SceneElement> elements = currentScene.elements.findAll { SceneElement se -> se.device == DeviceType.sd2 || se.device == DeviceType.clavia }
                List<List<Integer>> playedNotesNewChannels = withIndex(playedNoteList.sort { a, b -> b <=> a })
                        .collect {
                    Integer elementSize = elements != null ? elements.size() : 0
                    Integer elementIndex = (elementSize == 0 ? null : (it[1] < elementSize ? it[1] : (elementSize - 1)))
                    [it[0].value, it[0].velocity, elementIndex]


                }

                logger.info("playedNotesNewChannels" + playedNotesNewChannels)
                logger.info("playedNotesNewChannels-playedNotesChannels" + (playedNotesNewChannels - playedNotesChannels))
                logger.info("playedNotesChannels-playedNotesNewChannels" + (playedNotesChannels - playedNotesNewChannels))

                (playedNotesChannels - playedNotesNewChannels).each {
                    if (it[2] != null) {
                        SceneElement el = elements[it[2]]
                        logger.info "SceneElement" + el
                        result.add(new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.NOTE_ON, el.channel, it[0] + el.transposition, 0),
                                timeStamp, el.device))

                    }
                }
                logger.info("playedNotesNewChannels-playedNotesChannels" + (playedNotesNewChannels - playedNotesChannels))
                (playedNotesNewChannels - playedNotesChannels).each {
                    if (it[2] != null) {
                        SceneElement el = elements[it[2]]
                        result.add(new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.NOTE_ON, el.channel, it[0] + el.transposition, it[1]),
                                timeStamp, el.device))
                    }
                }
                playedNotesChannels = playedNotesNewChannels
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
        result
    }

    private List<MidiTimeStampMessage> processCommandChannelNoteOff(int noteValue, long timeStamp) {

        List<SceneElement> elements = currentScene.elements.findAll { SceneElement se -> se.device == DeviceType.sd2 || se.device == DeviceType.clavia }
        logger.info "Scene Method name: " + getCurrentMethodName()
        List<MidiTimeStampMessage> result = []
        try {
            if (currentScene.sceneType == SceneType.SIMULTANEOUS) {
                playedNoteList.remove(new Note(noteValue))
                logger.info "playedNoteList Size : ${playedNoteList.size()} "

                elements.each { SceneElement ele ->
                    result.add(new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.NOTE_ON, ele.channel, noteValue + ele.transposition, 0),
                            timeStamp, ele.device))
                }

            } else {
                playedNoteList.remove(new Note(noteValue, 0))
                logger.info "playedNoteList Size : ${playedNoteList.size()} "
                List<List<Integer>> playedNotesNewChannels = withIndex(playedNoteList.sort { a, b -> b <=> a })
                        .collect {
                    Integer elementSize = elements != null ? elements.size() : 0
                    Integer elementIndex = (elementSize == 0 ? null : (it[1] < elementSize ? it[1] : (elementSize - 1)))
                    [it[0].value, it[0].velocity, elementIndex]
                }
                logger.info("playedNotesNewChannels" + playedNotesNewChannels)
                logger.info("playedNotesNewChannels-playedNotesChannels" + (playedNotesNewChannels - playedNotesChannels))
                logger.info("playedNotesChannels-playedNotesNewChannels" + (playedNotesChannels - playedNotesNewChannels))

                (playedNotesChannels - playedNotesNewChannels).each {
                    if (it[2] != null) {
                        SceneElement el = elements[it[2]]
                        result.add(new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.NOTE_ON, el.channel, it[0] + el.transposition, 0),
                                timeStamp, el.device))
                    }
                }
                (playedNotesNewChannels - playedNotesChannels).each {
                    if (it[2] != null) {
                        SceneElement el = elements[it[2]]
                        result.add(new MidiTimeStampMessage(new ShortMidiMessage(ShortMessage.NOTE_ON, el.channel, it[0] + el.transposition, it[1]),
                                timeStamp, el.device))
                    }
                }
                playedNotesChannels = playedNotesNewChannels
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
        if (playedNoteList.size() == 0)
            result.addAll(changeSceneNumber())
        result
    }

    private List withIndex(List list) {

        def result = []

        int count = 0
        if (list) {
            result = list.collect { it ->
                List elem = []
                elem.add(it)
                elem.add(count++)
                elem
            }
            result
        }

    }

    private List<MidiTimeStampMessage> processCommandChannelExpression(int expressionValue, long timeStamp) {

        logger.debug "Scene Method name: " + getCurrentMethodName()
        List<SceneElement> elements = currentScene.elements?.findAll { SceneElement se -> se.device == DeviceType.sd2 || se.device == DeviceType.clavia }
        List<MidiTimeStampMessage> result = []
        logger.debug(" program number :" + this.currentProgramNumber + " CurrentScene :" + currentScene)

        try {
            elements.each {
                SceneElement ele ->

                    result.add(
                            new MidiTimeStampMessage(
                                    new ShortMidiMessage(ShortMessage.CONTROL_CHANGE, ele.channel, 11, expressionValue)
                                    , timeStamp, ele.device))
            }
        } catch (Exception e) {
            e.printStackTrace()
        }

        result
    }

    /**
     * Called by Midi Input receiver send method
     *
     * @param message
     * @param timeStamp
     */
    synchronized void processMidiMessage(MidiMessage message, long timeStamp) {
        logger.debug "Scene Method name: " + getCurrentMethodName()

        try {
            List<MidiTimeStampMessage> result = []
            if (message instanceof ShortMessage || message.class.canonicalName == "com.sun.media.sound.FastShortMessage") {
                Date date = new Date()
                ShortMessage shortMessage = (ShortMessage) message
                logger.debug date.format("yyyyMMdd-HH:mm:ss.SSS") + " IN " + decoder.decodeMessage(shortMessage) + " HEXA :" + message.message.encodeHex()
                if (shortMessage.channel == commandChannel) {
                    result = processShortMessageCommandChannel(shortMessage, timeStamp)
                } else
                    result = processShortMessageDataChannel(shortMessage, timeStamp)
            }

            sendMidiMessage(result)
        } catch (Exception e) {
            e.printStackTrace()
            logger.error(e.getMessage(), e)
        }
    }

    private void sendMidiMessage(List<MidiTimeStampMessage> result) {
        logger.debug "Scene Method name: " + getCurrentMethodName()

        try {
            if (this.sd2Rec) {
                (result.findAll { MidiTimeStampMessage sentTimeStampMessage -> sentTimeStampMessage.device == DeviceType.sd2 }).each { MidiTimeStampMessage sentTimeStampMessage ->
                    Date date = new Date()
                    logger.info date.format("yyyyMMdd-HH:mm:ss.SSS") + " OUT  " + DeviceType.sd2.name() + " " + decoder.decodeMessage(sentTimeStampMessage.message?.shortMessage()) + " HEXA :" + sentTimeStampMessage.message.shortMessage().data.encodeHex()
                    this.sd2Rec.send(sentTimeStampMessage.message.shortMessage(), sentTimeStampMessage.timeStamp)
                }
            }
            new Thread(new SendMessageTask(this.digitRec, result, DeviceType.digit)).start()
            if (this.claviaRec) {

                (result.findAll { MidiTimeStampMessage sentTimeStampMessage -> sentTimeStampMessage.device == DeviceType.clavia }).each { MidiTimeStampMessage sentTimeStampMessage ->
                    Date date = new Date()
                    logger.info date.format("yyyyMMdd-HH:mm:ss.SSS") + " OUT " + DeviceType.clavia.name() + " " + decoder.decodeMessage(sentTimeStampMessage.message.shortMessage()) + " HEXA :" + sentTimeStampMessage.message.shortMessage().data.encodeHex()
                    this.claviaRec.send(sentTimeStampMessage.message.shortMessage(), sentTimeStampMessage.timeStamp)
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }

    }

    /**
     * called when kee footswitch is hit
     *
     * @param sceneCandidateNumber
     */
    public synchronized void processFootSwitch(int sceneCandidateNumber) {
        logger.info "Scene Method name: " + getCurrentMethodName()

        try {
            List<MidiTimeStampMessage> result = []

            if ((currentSequence && currentSequence.size() > 0) && sceneCandidateNumber == 0) {
                currentSequenceNumber = (currentSequenceNumber + 1) % currentSequence.size()
                sceneCandidateNumber = currentSequence[currentSequenceNumber]
            } else if ((currentSequence && currentSequence.size() > 0) && sceneCandidateNumber == 1) {
                currentSequenceNumber = (currentSequenceNumber + currentSequence.size() - 1) % currentSequence.size()
                sceneCandidateNumber = currentSequence[currentSequenceNumber]

            } else if ((currentSequence && currentSequence.size() > 0) && sceneCandidateNumber == 2) {
                currentSequenceNumber = 0
                sceneCandidateNumber = currentSequence[currentSequenceNumber]
            }
            result = updateSceneCandidate(sceneCandidateNumber)
            sendMidiMessage(result)
        } catch (Exception e) {
            e.printStackTrace()
            logger.error(e.getMessage(), e)
        }

    }

    List<MidiTimeStampMessage> processShortMessageCommandChannel(ShortMessage message, long timeStamp) {
        logger.debug "Scene Method name: " + getCurrentMethodName()

        List<MidiTimeStampMessage> result = []

        switch (message.command) {
            case ShortMessage.NOTE_ON:
                if (message.data2 != 0) // note on
                    result = processCommandChannelNoteOn(message.data1, message.data2, timeStamp)
                else
                    result = processCommandChannelNoteOff(message.data1, timeStamp)
                // note off
                break;
            case ShortMessage.PROGRAM_CHANGE:
                result = updateSceneCandidate(message.data1)
                break;
            case ShortMessage.CONTROL_CHANGE:
                switch (message.data1) {
                    case 11: //expression
                        storeExpressionValue(message.data2)
                        result = processCommandChannelExpression(message.data2, timeStamp)
                        break;
                    case 0: // bank select MSB

                     result = updateProgramCandidate(message.data2)
                        break;
                    case 32:  //bank Select LSB
                        break;
                }
                break;
            case ShortMessage.NOTE_OFF:
                result = processCommandChannelNoteOff(message.data1, timeStamp)
                break;

        }

        result
    }

    List<MidiTimeStampMessage> processShortMessageDataChannel(ShortMessage message, long timeStamp) {
        logger.debug "Scene Method name: " + getCurrentMethodName()

        List<MidiTimeStampMessage> result = []

        switch (message.command) {
            case ShortMessage.NOTE_ON:
                break;
            case ShortMessage.CONTROL_CHANGE:
                switch (message.data1) {
                    case 11: //expressionk ;
                        break;
                    default:
                        result.add(new MidiTimeStampMessage(message, timeStamp))
                        break;
                }
                break;
            case ShortMessage.NOTE_OFF:
                break;
            default:
                result.add(new MidiTimeStampMessage(message, timeStamp))
        }

        result

    }


    class SendMessageTask implements Runnable {


        Receiver receiver
        List<MidiTimeStampMessage> messages
        DeviceType deviceType

        SendMessageTask(Receiver receiver, List<MidiTimeStampMessage> messages, DeviceType deviceType) {

            this.receiver = receiver
            this.messages = messages
            this.deviceType = deviceType


        }

        @Override
        void run() {

            if (this.receiver) {
                Thread.sleep(10)
                //println "message count :" + this.messages.size() + "second count " + this.messages.findAll{MidiTimeStampMessage sentTimeStampMessage -> sentTimeStampMessage.device == this.deviceType }.size()
                (this.messages.findAll { MidiTimeStampMessage sentTimeStampMessage -> sentTimeStampMessage.device == this.deviceType }).each { MidiTimeStampMessage sentTimeStampMessage ->
                    Thread.sleep(10)
                    Date date = new Date()
                    logger.debug date.format("yyyyMMdd-HH:mm:ss.SSS") + " OUT " + DeviceType.digit.name() + " " + decoder.decodeMessage(sentTimeStampMessage.message.shortMessage()) + " HEXA :" + sentTimeStampMessage.message.shortMessage().data.encodeHex()
                    this.receiver.send(sentTimeStampMessage.message.shortMessage(), sentTimeStampMessage.timeStamp)
                }
            }

        }
    }

}
