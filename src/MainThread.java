import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainThread {

    public static void main(String[] args) throws Exception{

        // value in the list is id, index of the element is the location of process
        List<Integer> idList = readInputFile();

        // initialize messaging module
        MessagingUtil msger = new MessagingUtil(idList);

        // initialize threads
        List<Thread> threadList = new ArrayList<>();
        for(int i : idList){
            Thread thread = new Thread(new Process(i,msger));
            thread.start();
            threadList.add(thread);
        }

        // start processes
        while(!msger.isAllProcessTerminate()){ // terminate when all process terminate.

            // start a new round
            msger.toNextRound();

            // wait all process stop current round
            while(!msger.isAllProcessFinishRound()){
                try{
                    Thread.sleep(100);
                }catch(InterruptedException e){};
            }

        }

        // make sure all process terminate
        for(Thread thread: threadList){
            try{
                thread.join();
            }catch(InterruptedException e){};
        }
    }

    public static List<Integer> readInputFile() throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader("input.dat"));
        int n = Integer.parseInt(reader.readLine());
        Stream<String> stream = Arrays.stream(reader.readLine().split(" "));
        List<Integer> idList = stream.map((s->Integer.parseInt(s)))
                .collect(Collectors.toList());
        return idList;
    }
}
