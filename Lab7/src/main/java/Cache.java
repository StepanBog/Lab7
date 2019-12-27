import org.zeromq.*;

import java.util.HashMap;
import java.util.Scanner;

public class Cache {
    private static final String SPACE = " ";
    private static final long EPSILON_TIME = 5000;
    private final ZContext zcon;
    private ZMQ.Socket worker;
    private int leftBound;
    private int rightBound;
    private ZMQ.Poller items;
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
        worker.setHWM(0);

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
        long time = System.currentTimeMillis();
        while (!Thread.currentThread().isInterrupted()){
            items.poll(1);
            if (System.currentTimeMillis() - time > EPSILON_TIME){
                ZMsg msg = new ZMsg();
                msg.add("I_AM_ALIVE " + leftBound + " " + rightBound);
                msg.send(worker);
                System.out.println("Sended IAMALIVE");
                time = System.currentTimeMillis();
            }
            if(items.pollin(0))
                handleDealer();
        }
    }

    private void handleDealer() {
        ZMsg msg = ZMsg.recvMsg(worker);
        ZFrame content = msg.getFirst();
        String[] data = content.toString().split(SPACE);
        if (data[0].equals("GET")){
            int pos = Integer.parseInt(data[1]);
            msg.pollLast();
            msg.add(cache.get(pos));
            msg.send(worker);
        }
        if (data[0].equals("PUT")){
            int pos = Integer.parseInt(data[1]);
            String value = data[2];
            cache.put(pos,value);
        }
    }

    private void connect() {
        worker.connect("tcp://localhost:5557");
        items = zcon.createPoller(1);
        items.register(worker, ZMQ.Poller.POLLIN);
    }
}
