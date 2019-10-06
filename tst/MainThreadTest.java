import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MainThreadTest {

    @Test
    public void readInputFileTest() throws Exception{
        List<Integer> ids = MainThread.readInputFile();

        Assertions.assertEquals(2,ids.get(0));
        Assertions.assertEquals(3,ids.get(1));
        Assertions.assertEquals(4,ids.get(2));
        Assertions.assertEquals(1,ids.get(3));
        Assertions.assertEquals(5,ids.get(4));
    }
}
