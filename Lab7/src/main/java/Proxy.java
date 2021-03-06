import org.zeromq.*;

import java.util.HashMap;

public class Proxy{
    private static final String SPACE = " ";
    private static final long EPSILON_TIME = 5000;
    private ZMQ.Socket frontend;
    private ZMQ.Socket backend;
    private ZContext zcon;
    private HashMap<ZFrame,Commutator> commutator;
    private long time;

    public Proxy(ZContext zcon) {
        this.zcon = zcon;
        this.frontend = this.zcon.createSocket(SocketType.ROUTER);
        this.backend = this.zcon.createSocket(SocketType.ROUTER);
        frontend.setHWM(0);
        backend.setHWM(0);
        commutator = new HashMap<>();
        this.bind();
        this.handle();
    }

    private void handle() {
        ZMQ.Poller items = zcon.createPoller(2);
        items.register(frontend, ZMQ.Poller.POLLIN);
        items.register(backend, ZMQ.Poller.POLLIN);
        boolean more = false;
        time = System.currentTimeMillis();
        while (!Thread.currentThread().isInterrupted()){
            items.poll(1);
            if ((!commutator.isEmpty()) && (System.currentTimeMillis() -time > EPSILON_TIME)) {
                deleteDead();
                time = System.currentTimeMillis();
            }
            if (items.pollin(0)){
                ZMsg msg = ZMsg.recvMsg(frontend);
                System.out.println("Have msg from client");
                if (msg != null)
                    handleClientPollin(msg);
                else
                    break;
            }
            if (items.pollin(1)){
                ZMsg msg = ZMsg.recvMsg(backend);
          //      System.out.println("Have msg from cache");
                if (msg != null)
                    handleDealerPollin(msg);
                else
                    break;;
            }
        }
    }

    private void deleteDead() {
        commutator.entrySet().removeIf(com ->((time - com.getValue().getTime()) > EPSILON_TIME * 1.5));
    }

    private void handleDealerPollin(ZMsg msg) {
        String[] data = msg.getLast().toString().split(SPACE);
        if (msg.getLast().toString().contains("I_AM_ALIVE")) {
      //      System.out.println("Have Alive msg ");
            if (!commutator.containsKey(msg.getFirst())) {
                Commutator com = new Commutator(data[1], data[2], System.currentTimeMillis());
                commutator.put(msg.getFirst().duplicate(), com);
            } else {
                commutator.get(msg.getFirst().duplicate()).setTime(System.currentTimeMillis());
            }
        } else {
            msg.pop();
            msg.send(frontend);
        }
    }

    private void handleClientPollin(ZMsg msg) {
        String[] data = msg.getLast().toString().split(SPACE);
        switch (data[0]){
            case "PUT" : {
                System.out.println("Have put mes");
                recievePUT(data,msg);
                break;
            }
            case "GET" : {
                System.out.println("Have get mes");
                recieveGET(data,msg);
                break;
            }
            default : {
                error(frontend,"error",msg);
            }
        }
    }

    private void recieveGET(String[] data, ZMsg msg) {
        for (HashMap.Entry<ZFrame,Commutator> c : commutator.entrySet())
            if (c.getValue().intersect(data[1])){
                ZFrame cache = c.getKey().duplicate();
                msg.addFirst(cache);
                msg.send(backend);
        }
    }

    private void error(ZMQ.Socket socket, String error, ZMsg msg) {
        ZMsg e = new ZMsg();
        e.add(msg.getFirst() + " " + error);
        e.send(socket);
    }

    private void recievePUT(String[] data, ZMsg msg) {
        for (HashMap.Entry<ZFrame,Commutator> c: commutator.entrySet()) {
            if (c.getValue().intersect(data[1])){
                ZMsg mes = msg.duplicate();
                ZFrame cache = c.getKey().duplicate();
                mes.addFirst(cache);
                mes.send(backend);
                System.out.println(mes);
            }
        }
    }

    private void bind() {
        frontend.bind("tcp://localhost:5555");
        backend.bind("tcp://localhost:5557");
    }

    public static void main(String[] args){
        ZContext zcon = new ZContext();
        Proxy server = new Proxy(zcon);
    }

}
