import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Assume the input id list define the ids of processes in clockwise order.
 * Send message with send to process with larger loc.
 * <p>
 * <p>
 * Messaging Structure:
 * Each process has a list: messagesFromLastRound, for the messages received from last round.
 * Each process also has a list: messagesAtCurrentRound, for messages that received at current round.
 * When a round finish, messages in messagesAtCurrentRound to messagesFromLastRound.
 * clean messagesAtCurrentRound.
 * <p>
 * Round Signal Structure:
 * a bool list used as signal for each process
 * a counter count active process in current round. when counter reach 0, start new round.
 */
public class MessagingUtil {


    private List<Queue<Message>> messagesFromLastRound;
    private List<Queue<Message>> messagesAtCurrentRound;

    private List<Boolean> roundSignalList;
    private AtomicInteger activeProcessCounter;
    private AtomicInteger nonTerminatedProcessCounter;

    // convert id to location in the list
    private Map<Integer, Integer> idLocationMap;

    public MessagingUtil(List<Integer> ids) {

        int n = ids.size();

        messagesFromLastRound = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            messagesFromLastRound.add(new ConcurrentLinkedQueue<>());
        }
        messagesAtCurrentRound = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            messagesAtCurrentRound.add(new ConcurrentLinkedQueue<>());
        }

        // when initializing MessagingUtil, set roundSignal to false.
        roundSignalList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            roundSignalList.add(false);
        }
        activeProcessCounter = new AtomicInteger();
        activeProcessCounter.set(n);

        nonTerminatedProcessCounter = new AtomicInteger(n);
        nonTerminatedProcessCounter.set(n);

        idLocationMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int id = ids.get(i);
            idLocationMap.put(id, i);
        }
    }

    /**
     * return true if new round starts.
     */
    public boolean readNewRoundSignal(int id) {
        int loc = mapIdToLocation(id);
        if (roundSignalList.get(loc)) {
            roundSignalList.set(loc, false);
            return true;
        } else
            return false;
    }

    /**
     * invoke this method when a process finish its current round
     */
    public void replyRoundFinish() {
        activeProcessCounter.getAndAdd(-1);
    }

    /**
     * invode by MainThread
     */
    public boolean isAllProcessFinishRound() {
        return activeProcessCounter.get() == 0;
    }

    public boolean isAllProcessTerminate() {
        return nonTerminatedProcessCounter.get() == 0;
    }


    /**
     * Claim that this is the last round this process have,
     * it will not participate into next round.
     */
    public void claimProcessTerminated(int id) {

        // claim terminate
        nonTerminatedProcessCounter.getAndAdd(-1);

        // claim round finish
        replyRoundFinish();
    }


    /**
     * read messages from last process
     */
    public Message readMessage(int id) {
        int loc = mapIdToLocation(id);
        return messagesFromLastRound.get(loc).poll();
    }

    /**
     * send message to next process
     */
    public void sendMessage(int id, Message msg) {
        // message send in clockwise direction
        // so message should send to process with larger loc.
        int srcLoc = mapIdToLocation(id);
        int targetLoc = (srcLoc + 1) % messagesAtCurrentRound.size();
        messagesAtCurrentRound.get(targetLoc).offer(msg);
    }

    /**
     * This method will run when move to the next round.
     */
    public void toNextRound() {
        // swap two message list
        List<Queue<Message>> tempList = messagesFromLastRound;
        messagesFromLastRound = messagesAtCurrentRound;
        messagesAtCurrentRound = tempList;

        // reset activeProcessCounter
        activeProcessCounter.set(nonTerminatedProcessCounter.get());

        // set newRoundSignal
        for (int i = 0; i < messagesAtCurrentRound.size(); i++) {
            roundSignalList.set(i, true);
        }
    }

    private int mapIdToLocation(int id) {
        return idLocationMap.get(id);
    }
}
