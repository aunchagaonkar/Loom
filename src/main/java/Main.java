import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args){
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 9092;
    try {
      serverSocket = new ServerSocket(port);
      serverSocket.setReuseAddress(true);
      clientSocket = serverSocket.accept();
      byte[] requestBuffer = new byte[12];
      clientSocket.getInputStream().read(requestBuffer);
      byte[] correlationId = new byte[8];
      System.arraycopy(requestBuffer, 8, correlationId, 0, 4);
      byte[] responseBytes = new byte[8];
      System.arraycopy(correlationId, 0, responseBytes, 4, 4);
      clientSocket.getOutputStream().write(responseBytes);

    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    } finally {
      try {
        if (clientSocket != null) {
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }
}
