import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Process implements Runnable {

    private int id;
    private MessagingUtil msger;
    private boolean isTerminate = false;
    private List<Message> pendingMessages;
    private List<Integer> pendingMessagesCount;
    private int roundNumber = 1;
    private int leaderId = 0;

    public Process(int id, MessagingUtil msger){
        this.id = id;
        this.msger = msger;

        pendingMessages = new ArrayList<>();
        pendingMessagesCount = new ArrayList<>();
    }

    @Override
    public void run() {

        while(true){

            // waiting until get new round signal
            while(msger.readNewRoundSignal(id) == false){
                try{
                    Thread.sleep(10);
                }catch( InterruptedException e){
                }
            }

            // run this round

            // if this is the first round
            if(roundNumber == 1){
                // send message
                Message outgoingMsg = new Message(id,false);
                msger.sendMessage(id,outgoingMsg);
            }

            // send pending message
            for (int i = pendingMessages.size()-1; i >= 0; i--) {
                int newCount = pendingMessagesCount.get(i) -1;
                if(newCount == 0){
                    // send this pending message
                    msger.sendMessage(id,pendingMessages.get(i));
                    // remove this pending message from pendingList
                    pendingMessages.remove(i);
                    pendingMessagesCount.remove(i);
                }
                else{
                    pendingMessagesCount.set(i,newCount);
                }
            }

            // read message
            while(true){
                Message msg = msger.readMessage(id);

                if(msg == null)
                    break;

                if(msg.isAnnouncement){
                    if(msg.msg_uid == id){
                        // I am leader, I finish.
                        isTerminate = true;
                    }
                    else{
                        // transfer announcement, I finish
                        msger.sendMessage(id,msg);
                        // I finish
                        isTerminate = true;

                        leaderId = msg.msg_uid;
                    }
                }
                else{ // normal message
                    if(msg.msg_uid == id){ // I am leader

                        // send announce message
                        Message announceMsg = new Message(id,true);
                        msger.sendMessage(id, announceMsg);

                        leaderId = id;
                    }
                    else{
                        int roundToWait = (int)Math.pow(2,msg.msg_uid);
                        pendingMessages.add(msg);
                        pendingMessagesCount.add(roundToWait);
                    }
                }
            }

            roundNumber ++;

            if(!isTerminate)
                msger.replyRoundFinish();
            else{
                msger.claimProcessTerminated(id);
                break;
            }
        }

        // print leader id
        if(leaderId == id){
            System.out.println("Process "+ String.valueOf(id) +" : I am Leader, leader id is " + String.valueOf(leaderId));
        }
        else
            System.out.println("Process "+ String.valueOf(id) +" : I am not Leader, leader id is " + String.valueOf(leaderId));
    }
}
