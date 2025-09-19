import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Main {
  public static void main(String[] args){
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 9092;
    try {
      serverSocket = new ServerSocket(port);
      serverSocket.setReuseAddress(true);
      clientSocket = serverSocket.accept();
      
      byte[] requestBytes = new byte[12];
      clientSocket.getInputStream().read(requestBytes);
      ByteBuffer requestWrapper = ByteBuffer.wrap(requestBytes);
      
      short apiVersion = requestWrapper.getShort(6);
      int correlationId = requestWrapper.getInt(8);

      ByteBuffer responseWrapper = ByteBuffer.allocate(10);
       // total length of the response
      // length = correlation ID (4 bytes) + error code (2 bytes)
      responseWrapper.putInt(6); // length of the response

      responseWrapper.putInt(correlationId); // correlation ID

      if (apiVersion < 0 || apiVersion > 4) {
        // invalid API version, send back correlation ID with no payload        
        responseWrapper.putShort((short)35); // error code for unsupported version
      } else {
        responseWrapper.putShort((short)0); // no error
      }
      
      byte[] responseBytes = responseWrapper.array();
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
