/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import javax.mail.*;
import javax.mail.internet.*;

import authentication.DbConnection;

import java.util.*;

/**
 *
 * @author Shamanth
 */
public class Mailing {
	public HashMap<String, String> hm  = DbConnection.loadConstant();
	public void postMail(String[] recipients, String subject, String message) throws MessagingException {

		// Get properties object
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		// get Session
		 
		String email = this.hm.get("email");
		String password = this.hm.get("email_password");
		
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email, password);
			}
		});

		// compose message
		try {
			MimeMessage msg = new MimeMessage(session);
			for (int i = 0; i < recipients.length; i++) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients[i]));
				msg.setSubject(subject);
				msg.setContent(message, "text/html");
				Transport.send(msg);
			}

			System.out.println("message sent successfully");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		String email = "johnsonseun15@gmail.com";
		String password = "test";
		String name = "Seun";
		String[] receivers = { email };
		try {
			Mailing m = new Mailing();
			m.postMail(receivers, "FSA - Registration Successful", "Hello " + name
					+ ", <br/><br/> You have been successfully registered on FSA. Kindly find your login details below:<br/><br/> <b>Username/Email: "
					+ email + "</b>. <br/>Password: " + password
					+ ". <br/><br/>Kindly login at <a href='"+m.hm.get("app_url")+"login.jsp'> FSA </a><br/><br/> Thanks for using FSA");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

}
