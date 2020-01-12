package com.mlecot.routeur

class ScannerTest implements Runnable {

    Scanner scanner
    RoutingGroovy routing
    Thread routingThread
    Map<String,Integer> interpretor=[
            "a":0,"aa":0,
            "b":1,"bb":1,
            "c":2,"cc":2,
            "d":3,"dd":3,
            "e":4,"ee":4,
            "f":5,"ff":5,
            "g":6,"gg":6,
            "h":7,"hh":7
    ]
    volatile boolean  running

    ScannerTest(RoutingGroovy routing,Thread routingThread) {

        this.routing=routing
        this.routingThread=routingThread
        scanner = new Scanner(System.in)
        running=true

    }

    @Override
    void run() {
        while (running) {
            String result = scanner.nextLine()
            println   " length: " + result.length() + " " + result
            if (result.length()){
                if (result.equals("hh")){
                    running=false
                }  else{
                    Integer  sceneCandidateNumber=getCandidateNumber(result)
                    if (sceneCandidateNumber!=null) {
                        println "footswitch candidate number: "+ sceneCandidateNumber
                        SceneManager.instance.processFootSwitch(sceneCandidateNumber)
                    }
                }
            }
        }
        routing.running=false
        this.routingThread.join()
        shutdown()
    }

    int getCandidateNumber(String s) {

        Integer candidateNumber=interpretor.get(s)


    }
    void shutdown() {
        String shutdownCmd="shutdown -s"
        Process child =Runtime.getRuntime().exec(shutdownCmd)

    }
}

//RoutingGroovy routing=new RoutingGroovy("UM-ONE" , "UM-ONE", "Real Time Sequencer" )
/**
 *  Main script for the whole application
 *   Two threads are instanciated: RoutingGroovy thread to instanciate and start midi interfaces.
 *                                 ScannerTest gets the commands for the input key board or the footswitch
 *
 */
RoutingGroovy routing=new RoutingGroovy("UM-ONE","UM-ONE","ESI MIDIMATE eX", "MIDIOUT2 (ESI MIDIMATE eX)")
def programSceneList=ProgramsJson.getInstance().getProgramSceneList()


Thread routingThread=new Thread(routing)
routingThread.start()
new Thread(new ScannerTest(routing,routingThread)).start()


