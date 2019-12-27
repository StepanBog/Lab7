import org.zeromq.*;

import java.util.Scanner;

public class Client {
    private static ZContext context;
    private static ZMQ.Socket socket;
    public static void main(String[] args){
        System.out.println("connecting");
        try {
            context = new ZContext();
            socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:5555");
            Scanner in = new Scanner(System.in);
            while (true) {
                String mes = in.nextLine();
                ZMsg zmesSend = new ZMsg();
                ZMsg zmesAns = new ZMsg();
                if (mes.contains("PUT") || mes.contains("GET")) {
                    zmesSend.add(mes);
                    zmesSend.send(socket);
                    ZMsg.recvMsg(socket);
                    System.out.print("IN:" + zmesAns.popString());
                } else {
                    System.out.println("error");
                }
            }
        } catch (ZMQException ex){
            ex.printStackTrace();
        }
    }

}
