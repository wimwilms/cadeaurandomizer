package be.discobolus.oudjaar.cadeau;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {
	private Address bccAddress;
	private Address fromAddress;
	private String smtpHost;
	private int smtpPort;
	
	public void sendMail(List<String> mailsTo, String subject, String body) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("cadeaukes@discobolus.be", "*5SeGh9D");
			}
		  });
		MimeMessage message = new MimeMessage(session);
		message.setSubject(subject);
		message.setText(body);
		
		message.setFrom(fromAddress);
		Address[] addresses = new Address[mailsTo.size()];
		for (int i = 0; i < mailsTo.size(); i++) {
			addresses[i] = new InternetAddress(mailsTo.get(i));
		}
		message.setRecipients(RecipientType.TO, addresses);
		message.setRecipient(RecipientType.BCC, bccAddress);
		
		Transport transport = session.getTransport("smtp");
		
		Transport.send(message);
		transport.close();
	}

	public void readConfig() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/config/mail.properties");
		Properties properties = new Properties();
		properties.load(is);
		smtpHost = properties.getProperty("smtp.host");
		smtpPort = Integer.parseInt(properties.getProperty("smtp.port"));
		fromAddress = new InternetAddress(properties.getProperty("mail.from"));
		bccAddress = new InternetAddress(properties.getProperty("mail.bcc"));
	}
}
