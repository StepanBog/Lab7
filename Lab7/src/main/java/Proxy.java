import org.zeromq.*;

import java.util.HashMap;

public class Proxy{
    private static final String SPACE = " ";
    private ZMQ.Socket frontend;
    private ZMQ.Socket backend;
    private ZContext zcon;
    private HashMap<ZFrame,Commutator> commutator;

    public Proxy(ZContext zcon) {
        this.zcon = zcon;
        this.frontend = this.zcon.createSocket(SocketType.ROUTER);
        this.backend = this.zcon.createSocket(SocketType.ROUTER);
        this.bind();
        this.handle();
    }

    private void handle() {
        ZMQ.Poller items = zcon.createPoller(2);
        items.register(frontend, ZMQ.Poller.POLLIN);
        items.register(backend, ZMQ.Poller.POLLIN);
        boolean more = false;
        byte[] mes;
        while (!Thread.currentThread().isInterrupted()){
            items.poll();
            if (items.pollin(0)){
                ZMsg msg = ZMsg.recvMsg(frontend);
                if (msg != null)
                    handleClientPollin(msg);
                else
                    break;
            }
            if (items.pollin(1)){
                ZMsg msg = ZMsg.recvMsg(backend);
                if (msg != null)
                    handleDealerPollin(msg);
                else
                    break;;
            }
        }
    }

    private void handleDealerPollin(ZMsg msg) {
        String[] data = msg.getLast().toString().split(SPACE);
        if (msg.getLast().toString().contains("I_AM_ALIVE")) {
            if (!commutator.containsKey(msg.getFirst())) {
                Commutator com = new Commutator(data[1], data[2], System.currentTimeMillis());
                commutator.put(msg.getFirst().duplicate(), com);
            } else {
                commutator.get(msg.getFirst().duplicate()).setTime(System.currentTimeMillis());
            }
        } else {
            msg.poll()
        }
    }

    private void handleClientPollin(ZMsg msg) {
        String[] data = msg.getLast().toString().split(SPACE);
        switch (data[0]){
            case "PUT" : recievePUT(data,msg);
            case "GET" : recieveGET(data,msg);
            default : {
                error(frontend,"error",msg);
            }
        }
    }

    private void recieveGET(String[] data, ZMsg msg) {
    }

    private void error(ZMQ.Socket socket, String error, ZMsg msg) {
        ZMsg e = new ZMsg();
        e.add(msg.getFirst() + " " + error);
        e.send(socket);
    }

    private void recievePUT(String[] data, ZMsg msg) {
        
    }

    private void bind() {
        frontend.bind("tcp://localhost:5556");
        backend.bind("tcp://localhost:5557");
    }

    public static void main(String[] args){
        ZContext zcon = new ZContext();
        Proxy server = new Proxy(zcon);
    }

}
