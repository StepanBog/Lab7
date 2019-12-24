import org.zeromq.*;

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
            ZMsg zmesSend = new ZMsg();
            ZMsg zmesAns = new ZMsg();
            zmesSend.add(mes);
            zmesSend.send(socket);
            zmesAns.
        } catch (ZMQException ex){
            ex.printStackTrace();
        }
    }

}
