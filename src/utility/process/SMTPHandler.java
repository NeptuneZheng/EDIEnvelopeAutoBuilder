package utility.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

public class SMTPHandler {

	static Logger logger = Logger.getLogger(SMTPHandler.class.getName());

	private String SMTPHost;

	private int SMTPPort;

	private String from;

	private String to;

	// private String ccTo;

	public SMTPHandler() {

		this.SMTPHost = "TESTAPPSMTP";
		this.SMTPPort = 25;
		this.from = "lion.li@cargosmart.com";
		this.to = "integration@vltqa2.cargosmart.com";
		try {
			InetAddress.getLocalHost().getHostName().toString();

		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public SMTPHandler(String SMTPHost, int SMTPPort, String from, String to) {
		this.SMTPHost = SMTPHost;
		this.SMTPPort = SMTPPort;
		this.from = from;
		this.to = to;
	}

	public boolean sendEmail(String subject, String inputFilePath,
			String realInputFileName) {
		Date time = new Date();
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String stime = form.format(time);
		subject = subject + "_" + stime;

		Properties props = System.getProperties();

		String content = "";
		try {
			content = readContent(new File(inputFilePath));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		props.put("mail.smtp.host", SMTPHost);
		props.put("mail.smtp.port", SMTPPort);
		props.put("mail.smtp.auth", "false");
		Session mailSession = Session.getDefaultInstance(props);
		try {

			mailSession.setDebug(true);

			Message message = new MimeMessage(mailSession);

			message.setFrom(new InternetAddress(from));
			// message.setRecipients(Message.RecipientType.TO, InternetAddress
			// .parse(to));
			String toList = getMailList(to.split(";"));

			InternetAddress[] toListAddress = new InternetAddress()
					.parse(toList);
			message.setRecipients(Message.RecipientType.TO, toListAddress);
			// message.setRecipients(Message.RecipientType.CC, InternetAddress
			// .parse(ccTo));
			// message.setRecipients(Message.RecipientType.BCC, InternetAddress
			// .parse(bccTo));

			subject = new String(subject.getBytes("ISO8859-1"), "utf-8");
			message.setSubject(subject);
			message.setSentDate(new Date());
			message.setHeader("X-Priority", "3");

			content = new String(content.getBytes("ISO8859-1"), "utf-8");

			// logger.info(content+"hello");
			// message.setText(content);
			message.setContent(getMultipart(content, inputFilePath,
					realInputFileName));

			message.saveChanges();
			message.setHeader("Message-ID", "B2BAutomation");
			Transport transport = mailSession.getTransport("smtp");
			transport.connect();
			logger.info("SMTP information:" + SMTPHost + "  " + SMTPPort);
			logger.info("Subject: " + subject);

			logger.info("From : " + from);
			logger.info("To: " + to);

			transport.sendMessage(message, message.getAllRecipients());

			transport.close();
			logger.info("Send file " + realInputFileName
					+ " by SMTP successfully !");

		} catch (Exception e) {
			e.printStackTrace();

			logger.info("Fail to send file by SMTP !");

			return false;
		}
		return true;
	}
	
	public boolean sendEmailG6(String subject, String inputFilePath,
			String realInputFileName) {
		Date time = new Date();
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String stime = form.format(time);
		subject = subject + "_" + stime;

		Properties props = System.getProperties();

		String content = "";
		try {
			content = readContent(new File(inputFilePath));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		props.put("mail.smtp.host", SMTPHost);
		props.put("mail.smtp.port", SMTPPort);
		props.put("mail.smtp.auth", "false");
		Session mailSession = Session.getDefaultInstance(props);
		try {

			mailSession.setDebug(true);

			Message message = new MimeMessage(mailSession);

			message.setFrom(new InternetAddress(from));
			// message.setRecipients(Message.RecipientType.TO, InternetAddress
			// .parse(to));
			String toList = getMailList(to.split(";"));

			InternetAddress[] toListAddress = new InternetAddress()
					.parse(toList);
			message.setRecipients(Message.RecipientType.TO, toListAddress);
			// message.setRecipients(Message.RecipientType.CC, InternetAddress
			// .parse(ccTo));
			// message.setRecipients(Message.RecipientType.BCC, InternetAddress
			// .parse(bccTo));

			subject = new String(subject.getBytes("ISO8859-1"), "utf-8");
			message.setSubject(subject);
			message.setSentDate(new Date());
			message.setHeader("X-Priority", "3");

			content = new String(content.getBytes("ISO8859-1"), "utf-8");

			// logger.info(content+"hello");
			 message.setText(content);
			/*message.setContent(getMultipartG6(content, inputFilePath,
					realInputFileName));
*/
			message.saveChanges();
			message.setHeader("Message-ID", realInputFileName);
			//message.setHeader("Message-ID", "B2BAutomation");
			Transport transport = mailSession.getTransport("smtp");
			transport.connect();
			logger.info("SMTP information:" + SMTPHost + "  " + SMTPPort);
			logger.info("Subject: " + subject);

			logger.info("From : " + from);
			logger.info("To: " + to);

			transport.sendMessage(message, message.getAllRecipients());

			transport.close();
			logger.info("Send file " + realInputFileName
					+ " by SMTP successfully !");

		} catch (Exception e) {
			e.printStackTrace();

			logger.info("Fail to send file by SMTP !");

			return false;
		}
		return true;
	}


	private Multipart getMultipart(String html, String inputFilePath,
			String realInputFileName) throws MessagingException {

		Multipart multi = new MimeMultipart("mixed");
		Vector<String> attachedFileVector = new Vector<String>();
		multi.addBodyPart(createContent(html));

		ArrayList attachmentList = new ArrayList();
		attachmentList.add(inputFilePath);

		for (int i = 0; i < attachmentList.size(); i++) {

			String inputFileName = (String) attachmentList.get(i);

			File inputFile = new File(inputFileName);
			if (inputFile.exists() && inputFile.isFile()
					&& newAttachedFile(attachedFileVector, realInputFileName)) {
				attachedFileVector.addElement(realInputFileName);
				multi
						.addBodyPart(createAttachment(inputFile,
								realInputFileName));
			}

		}
		return multi;

	}
	
	private Multipart getMultipartG6(String html, String inputFilePath,
			String realInputFileName) throws MessagingException {

		Multipart multi = new MimeMultipart("mixed");
		Vector<String> attachedFileVector = new Vector<String>();
		multi.addBodyPart(createContent(html));

		/*ArrayList attachmentList = new ArrayList();
		attachmentList.add(inputFilePath);

		for (int i = 0; i < attachmentList.size(); i++) {

			String inputFileName = (String) attachmentList.get(i);

			File inputFile = new File(inputFileName);
			if (inputFile.exists() && inputFile.isFile()
					&& newAttachedFile(attachedFileVector, realInputFileName)) {
				attachedFileVector.addElement(realInputFileName);
				multi.addBodyPart(createAttachment(inputFile,
								realInputFileName));
			}

		}*/
		return multi;

	}

	private BodyPart createAttachment(File file, String FileName)
			throws MessagingException {
		BodyPart attach = new MimeBodyPart();
		FileDataSource ds = new FileDataSource(file);
		attach.setDataHandler(new DataHandler(ds));
		attach.setFileName(FileName);
		attach.setHeader("Content-ID", ds.getName());

		return attach;
	}

	private BodyPart createContent(String html) throws MessagingException {
		BodyPart content = new MimeBodyPart();
		Multipart relate = new MimeMultipart("related");
		relate.addBodyPart(createHtmlBody(html));

		content.setContent(relate);
		return content;
	}

	private BodyPart createHtmlBody(String content) throws MessagingException {
		BodyPart html = new MimeBodyPart();
		html.setContent(content, "text/html;charset=utf-8");
		return html;
	}

	public boolean newAttachedFile(Vector attachedFileVector, String fileName) {
		boolean isFlag = true;
		Enumeration efile = attachedFileVector.elements();
		while (efile.hasMoreElements()) {
			if (efile.nextElement().toString().equals(fileName)) {
				isFlag = false;
			}
		}
		return isFlag;
	}

	private String readContent(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuffer sb = new StringBuffer();

		char[] buffer = new char[1024];
		int length = 0;
		while ((length = reader.read(buffer)) != -1) {
			sb.append(buffer, 0, length);
		}
		return sb.toString();
	}

	public static void receive(String popServer, String popUser,
			String popPassword) {
		Store store = null;
		Folder folder = null;
		try {

			Properties props = System.getProperties();
			props.put("mail.pop3.host", popServer);
			props.put("mail.pop3.auth", "true");
			Authenticator auth = new EmailSecureAuthenticator(popUser,
					popPassword);
			Session session = Session.getDefaultInstance(props, auth);
			session.setDebug(true);
			store = session.getStore("pop3");
			store.connect(popServer, popUser, popPassword);
			// store.connect();

			folder = store.getDefaultFolder();
			if (folder == null)
				throw new Exception("No default folder");

			folder = folder.getFolder("INBOX");
			if (folder == null)
				throw new Exception("No POP3 INBOX");

			folder.open(Folder.READ_ONLY);

			Message[] msgs = folder.getMessages();
			for (int msgNum = 0; msgNum < msgs.length; msgNum++) {
				printMessage(msgs[msgNum]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			try {
				if (folder != null)
					folder.close(false);
				if (store != null)
					store.close();
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
		}
	}

	public static void printMessage(Message message) {
		try {

			String from = ((InternetAddress) message.getFrom()[0])
					.getPersonal();
			if (from == null)
				from = ((InternetAddress) message.getFrom()[0]).getAddress();
			logger.info("FROM: " + from);

			String subject = message.getSubject();
			logger.info("SUBJECT: " + subject);

			Part messagePart = message;
			Object content = messagePart.getContent();

			if (content instanceof Multipart) {
				messagePart = ((Multipart) content).getBodyPart(0);
				logger.info("[ Multipart Message ]");
			}

			String contentType = messagePart.getContentType();

			logger.info("CONTENT:" + contentType);
			if (contentType.startsWith("text/plain")
					|| contentType.startsWith("text/html")) {
				InputStream is = messagePart.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				String thisLine = reader.readLine();
				while (thisLine != null) {
					logger.info(thisLine);
					thisLine = reader.readLine();
				}
			}
			logger.info("-------------- END ---------------");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String getMailList(String[] mailArray) {

		StringBuffer toList = new StringBuffer();
		int length = mailArray.length;
		if (mailArray != null && length < 2) {
			toList.append(mailArray[0]);
		} else {
			for (int i = 0; i < length; i++) {
				toList.append(mailArray[i]);
				if (i != (length - 1)) {
					toList.append(",");
				}

			}
		}
		return toList.toString();

	}

}
