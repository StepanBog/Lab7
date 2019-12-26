import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class Server {
    private ZMQ.Socket frontend;
    private ZMQ.Socket backend;
    private ZContext zcon;

    public Server(ZContext zcon) {
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
    }

    private void handleClientPollin(ZMsg msg) {
    }

    private void bind() {
        frontend.bind("tcp://localhost:5556");
        backend.bind("tcp://localhost:5557");
    }

    public static void main(String[] args){
        ZContext zcon = new ZContext();
        Server server = new Server(zcon);
    }

}
