import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

import java.util.Scanner;

public class Client {
    public static void main(String[] args){
        System.out.println("connecting");
        try {


            ZContext context = new ZContext();
            ZMQ.Socket socket = new context.createSocket(SocketType.REP);
            socket.connect("tcp://localhost:5555");
            Scanner in = new Scanner(System.in);
            String mes = in.nextLine();
            MesToServer mesToServ =
        } catch (ZMQException ex){
            ex.printStackTrace();
        }
    }

}
