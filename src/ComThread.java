import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;

public class ComThread extends Thread{
    ComThread(ChestCollection curSet, Socket socket) {
        this.curSet = curSet;
        this.socket = socket;
    }

    private ChestCollection curSet;
    private Socket socket;

    @Override
    public void run() {

        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            if(!socket.isClosed()) {

                System.out.println("Сервер работает...");
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                    try {
                        out.writeObject(curSet.returnObjects());
                        out.flush();
                    } catch (NoSuchElementException | IOException e) {
                        e.printStackTrace();
                        System.out.println("Клиент" + socket.getInetAddress() + "отключился");
                        }
                    }

            } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
