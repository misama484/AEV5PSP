package InvernHot;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.sun.net.httpserver.HttpServer;

public class Server {

	public static void main(String[] args) throws IOException {
		
		String host = "127.0.0.1"; //ip del servidor
		int port = 7778; //puerto del server
		InetSocketAddress direccionTCPIP = new InetSocketAddress(host, port);
		
		int backlog = 0;
		
		//creamos server -- importar libreria sun
		HttpServer server = HttpServer.create(direccionTCPIP, backlog);
		
		//creamos un objeto gestor
		GestorHTTP gestor = new GestorHTTP();
		String rutaRespuesta = "/estufa";
		server.createContext(rutaRespuesta, gestor);
		
		ThreadPoolExecutor tpex = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
		server.setExecutor(tpex);
		server.start();
		System.out.println("Servidor arranca en puerto: " + port);

	}

}

