package InvernHot;


import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class CorreoCli {
	
	public static void enviaMail(String mensaje, String asunto, String mail_remite, String mail_remite_pass, String host_mail, String port_mail, String[] mails_destino, String[] anexos ) throws AddressException, MessagingException {
		
		System.out.println("Sistema de mensajeria de Invernalia");
		System.out.println(" > Remitente: " + mail_remite);
		
		Properties props = System.getProperties();
		props.put("mail.smtp.host", host_mail);
		props.put("mail.smtp.user", mail_remite);
		props.put("mail.smtp.clave", mail_remite_pass);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", port_mail);
		
		Session session = Session.getDefaultInstance(props);
		
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(mail_remite));
		//recorre un for y va anyadiendo los destinatarios 
		for(int i=0; i< mails_destino.length; i++) {
			message.addRecipients(Message.RecipientType.TO, mails_destino[i]);
		}
		
		message.setSubject(asunto);
		
		//declarar primero el multipart, para poder implementarlo dentro del for
		Multipart multipart = new MimeMultipart();
		
		BodyPart messageBodyPart1 = new MimeBodyPart();
		messageBodyPart1.setText(mensaje);
		
		multipart.addBodyPart(messageBodyPart1);
		
		//recorremos un for anyadiendo toda la estructura para agregar anexos
		for(int i = 0; i < anexos.length; i++) {
			BodyPart messageBodyPart2 = new MimeBodyPart();
			DataSource src = new FileDataSource(anexos[i]);
			messageBodyPart2.setDataHandler(new DataHandler(src));
			messageBodyPart2.setFileName(anexos[i]);
			multipart.addBodyPart(messageBodyPart2);
		}			
		
		message.setContent(multipart);
		
		Transport transport = session.getTransport("smtp");
		transport.connect(host_mail, mail_remite, mail_remite_pass);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

	
	public void notificaAveria(String mail_remite, String pass_remite) {
		
		System.out.println("PruebaEmail.java");
		
		String mensaje = "Desde InverHot Sistemas de calefacción le comunicamos que ha habido una incidencia en su sistema de calefacción";
		String asunto = "AVERIA";
		String emailRemite = mail_remite; //correo del que envia		
		String remitePass = pass_remite; //capturamos la entrada por teclado			
		String hostEmail = "smtp.gmail.com";
		String portEmail = "587"; //como trabajaremos con TSL usamos el 587
		String[] emailsDestino = {"bakinjara@hotmail.com", "dj_puchu@hotmail.com"}; //creamos un array para introducir los destinatarios que necesitemos
		
		String[] anexo = {"C:\\Users\\Miguel\\Pictures\\Invernalia.jpg", "C:\\Users\\Miguel\\Pictures\\InverHotCalefacciones.pdf"}; //indicamos donde se encuentran los ficheros que deseamos anexar
		
		try {
			enviaMail(mensaje, asunto, emailRemite, remitePass, hostEmail, portEmail, emailsDestino, anexo);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		System.out.println("Mensaje enviado!!!");
	}

}
