package InvernHot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GestorHTTP implements HttpHandler{
	
	public int temperaturaActual = 15;
	public int temperaturaTermostato = 15;
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String requestParamValue = null;
		if("GET".equals(exchange.getRequestMethod())) {
			requestParamValue = handleGETRequest(exchange); //recogemos la info que recibimos del cliente
			//temperaturaActual = requestParamValue; //ajustamos variable de temperatura actual segun los parametros de get			
			handleGETResponse(exchange, requestParamValue); //respuesta que le mandamos
		} else if ("POST".equals(exchange.getRequestMethod())) {
			try {
				requestParamValue = handlePOSTRequest(exchange);
			}catch (InterruptedException e1) {e1.printStackTrace();} 						
			handlePOSTResponse(exchange, requestParamValue);
		}
		
	}
	
	//peticion GET
	private String handleGETRequest(HttpExchange exchange) {
		System.out.println("Recibida URI GET: " + exchange.getRequestURI().toString());
		return exchange.getRequestURI().toString().split("\\?")[1].split("=")[1];		
	}
	
	//peticion POST
	private String handlePOSTRequest(HttpExchange exchange) throws InterruptedException {
		System.out.println("Recibida URI tipo POST: " + exchange.getRequestBody().toString());
		InputStream is = exchange.getRequestBody();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		String line; //aqui guardamos lo recibidor por POST
		
		try {
			while((line = br.readLine()) != null) {
				sb.append(line);	//anyadimos al stringbulder lo recibido en linea
			}
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		String comando = sb.toString(); //anyadimos a comando el contenido del POST
		if(comando.split(":")[0].equals("notificarAveria")) { //si la primera parte del contenido es notificarAveria...
			String datos = comando.split(":")[1];	//extraemos los datos de correo
			String correoRemite = datos.split(";")[0];
			String passRemite = datos.split(";")[1];
			notificarAveria(correoRemite, passRemite);
			String notificacion = "notificacion de averia enviada";
			System.out.println(notificacion);
			return notificacion;
		}else if(comando.split("=")[0].equals("setTemperatura")){
			int temperatura = Integer.parseInt(comando.split("=")[1]);
			temperaturaTermostato = temperatura;
			regularTemperatura(temperatura);			
			String notificacion = "Ajustando termostato";
			return notificacion;
			//return sb.toString().split("=")[1]; //devolvemos el contenido del sb pasado a string
		}
		else {
			return "Error desconocido";
		}
				
	}
	
	//respuesta GET
	private void handleGETResponse(HttpExchange exchange, String requestParamValue) throws IOException {
		OutputStream outputStream = exchange.getResponseBody();
		String htmlResponse = null;
		if(requestParamValue.equals("temperatuaActual")) {
			htmlResponse = "<html><body><h1>Sistema de control de temperatura de InvernHot<h1><h2> Temperatura Actual: " + temperaturaActual + " <h2><h2>Temperatura Termostato: " + temperaturaTermostato + "<h2><body><html>";
		}		
		exchange.sendResponseHeaders(200, htmlResponse.length());
		outputStream.write(htmlResponse.getBytes());
		outputStream.flush();
		outputStream.close();
		System.out.println("Devuelve respuesta HTML " + htmlResponse);
		
	}
	
	//respuesta POST
	private void handlePOSTResponse(HttpExchange exchange, String requestParamValue) throws IOException{
		OutputStream outputStream = exchange.getResponseBody();
		String htmlResponse = "Instruccion recibida, se ha alcanzado la temperatura de : " + temperaturaActual + " grados.";
		exchange.sendResponseHeaders(200, htmlResponse.length());
		outputStream.write(htmlResponse.getBytes());
		outputStream.flush();
		outputStream.close();
		System.out.println("Devuelve respuesta HTML " + htmlResponse);
	}
	
	private void regularTemperatura(int temperatura) throws InterruptedException {		
		
		for(int i = temperaturaActual; i<temperatura; i++) {
			temperaturaActual++;
			Thread.sleep(5000);
			System.out.println("temperatura Actual: " + temperaturaActual + " -- " + "Temperatura termostato: " + temperaturaTermostato);
		}
		
	}
	
	private void notificarAveria(String email_remite, String remite_pass) {
		CorreoCli correo = new CorreoCli();
		correo.notificaAveria(email_remite, remite_pass);
	}

	

}
 