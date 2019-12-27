import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.Scanner;

public class Cache {
    private final ZContext zcon;
    private int leftBound;
    private int rightBound;
    private HashMap<Integer,String> cache;

    public Cache(ZContext zcon) {
        this.zcon = zcon;
        cache = new HashMap<>();
        Scanner in = new Scanner(System.in);
        leftBound = in.nextInt();
        rightBound = in.nextInt();
        ZMQ.Socket worker = zcon.createSocket(SocketType.DEALER);

    }

    public static void main(String args[]){
        try{
            ZContext zcon = new ZContext();
            Cache cache = new Cache(zcon);

        } catch (Exception e){
            System.out.print(e.toString());
        }
    }
}
