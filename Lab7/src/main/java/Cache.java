import org.zeromq.*;

import java.util.HashMap;
import java.util.Scanner;

public class Cache {
    private final ZContext zcon;
    private ZMQ.Socket worker;
    private int leftBound;
    private int rightBound;
    private HashMap<Integer,String> cache;

    public Cache(ZContext zcon) {
        this.zcon = zcon;
        cache = new HashMap<>();
        Scanner in = new Scanner(System.in);
        leftBound = in.nextInt();
        rightBound = in.nextInt();
        for (int i = leftBound; i <= rightBound; i++)
            cache.put(i,Integer.toString(i));
        worker = zcon.createSocket(SocketType.DEALER);

    }

    public static void main(String args[]){
        try{
            ZContext zcon = new ZContext();
            Cache cache = new Cache(zcon);
            cache.connect();
            cache.handler();
        } catch (Exception e){
            System.out.print(e.toString());
        }
    }

    private void handler() {
        ZMsg msg = ZMsg.recvMsg(worker);
        ZFrame content = msg.getFirst();

    }

    private void connect() {
        worker.connect("tcp://localhost:5559");
        ZMQ.Poller items = zcon.createPoller(1);
        items.register(worker, ZMQ.Poller.POLLIN);
    }
}
