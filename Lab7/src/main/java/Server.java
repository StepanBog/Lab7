import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Server {
    private ZMQ.Socket frontend;
    private ZMQ.Socket backend;
    private ZContext zcon;

    public Server(ZContext zcon) {
        this.zcon = zcon;
        this.frontend = this.zcon.createSocket(SocketType.ROUTER);
        this.backend = this.zcon.createSocket(SocketType.ROUTER);
        this.bind();
    }

    private void bind() {
        frontend.bind("tcp://localhost:5555";
        backend.bind()
    }

    public static void main(String[] args){
        ZContext zcon = new ZContext();
        Server server = new Server(zcon);
    }

}
