import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class MessagingUtilTest {


    @Test
    public void roundSignalTest(){
        // 3 process, id: 1 2 3
        List<Integer> ids = Arrays.asList(1,2,3);

        MessagingUtil msger = new MessagingUtil(ids);

        // at beginning, no process get start round signal
        Assertions.assertEquals(false,msger.readNewRoundSignal(1));
        Assertions.assertEquals(false,msger.readNewRoundSignal(2));
        Assertions.assertEquals(false,msger.readNewRoundSignal(3));

        // start new round
        msger.toNextRound();

        // now all process should get start round signal
        Assertions.assertEquals(true,msger.readNewRoundSignal(1));
        Assertions.assertEquals(true,msger.readNewRoundSignal(2));
        Assertions.assertEquals(true,msger.readNewRoundSignal(3));

        // not all process finish current round
        Assertions.assertFalse(msger.isAllProcessFinishRound());

        // all process finish current round
        msger.replyRoundFinish(); // for process 1
        msger.replyRoundFinish(); // for process 2
        msger.replyRoundFinish(); // for process 3

        // now we can know all process finish
        Assertions.assertTrue(msger.isAllProcessFinishRound());

    }
}
