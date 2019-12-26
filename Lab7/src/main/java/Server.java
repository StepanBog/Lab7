import org.zeromq.ZContext;

public class Server {
    private ZContext zcon;

    public Server(ZContext zcon) {
        this.zcon = zcon;
        this.fro
    }

    public static void main(String[] args){
        ZContext zcon = new ZContext();
        Server server = new Server(zcon);
    }

}
