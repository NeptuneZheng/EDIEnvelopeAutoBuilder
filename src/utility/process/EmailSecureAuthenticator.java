package utility.process;

import javax.mail.Authenticator;

import javax.mail.PasswordAuthentication;

public class EmailSecureAuthenticator extends Authenticator {

	String userName = null;

	String password = null;

	public EmailSecureAuthenticator() {

	}

	public EmailSecureAuthenticator(String username, String password) {

		this.userName = username;

		this.password = password;

	}

	protected PasswordAuthentication getPasswordAuthentication() {

		return new PasswordAuthentication(userName, password);

	}

}
