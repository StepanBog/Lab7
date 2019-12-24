import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Client {
    public static void main(String[] args){
        System.out.println("connecting");
        ZContext context = new ZContext();
        ZMQ.Socket socket = new context.createSocket(SocketType.REP);
        socket.connect(tcp://localhost:)


    }

}
