package utility.process;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.apache.log4j.Logger;

/**
 * ��һ���ʼ�����Ҫ����һ��ReciveMail����
 */
public class POP3Handler {
	static Logger logger = Logger.getLogger(POP3Handler.class.getName());
	private MimeMessage mimeMessage = null;
	private String saveAttachPath = ""; // �������غ�Ĵ��Ŀ¼
	private StringBuffer bodytext = new StringBuffer();// ����ʼ�����
	private StringBuffer contentTypeSB = new StringBuffer();

	private String dateformat = "yy-MM-dd HH:mm"; // Ĭ�ϵ���ǰ��ʾ��ʽ
	private String host;
	private int port;
	private String userName;
	private String password;

	public POP3Handler(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	public POP3Handler(String host, int port, String userName, String password) {
		this.host = host;
		this.port = port;

		this.userName = userName;
		this.password = password;
	}

	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	/**
	 * ��÷����˵ĵ�ַ������
	 */
	public String getFrom() throws Exception {
		InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
		String from = address[0].getAddress();
		if (from == null)
			from = "";
		String personal = address[0].getPersonal();
		if (personal == null)
			personal = "";
		String fromaddr = personal + "<" + from + ">";
		return fromaddr;
	}

	public String getContentTypeSB() {
		return contentTypeSB.toString();
	}

	/**
	 * ����ʼ����ռ��ˣ����ͣ������͵ĵ�ַ�����������������ݵĲ����Ĳ�ͬ "to"----�ռ��� "cc"---�����˵�ַ "bcc"---�����˵�ַ
	 */
	public String getMailAddress(String type) throws Exception {
		String mailaddr = "";
		String addtype = type.toUpperCase();
		InternetAddress[] address = null;
		if (addtype.equals("TO") || addtype.equals("CC")
				|| addtype.equals("BCC")) {
			if (addtype.equals("TO")) {
				address = (InternetAddress[]) mimeMessage
						.getRecipients(Message.RecipientType.TO);
			} else if (addtype.equals("CC")) {
				address = (InternetAddress[]) mimeMessage
						.getRecipients(Message.RecipientType.CC);
			} else {
				address = (InternetAddress[]) mimeMessage
						.getRecipients(Message.RecipientType.BCC);
			}
			if (address != null) {
				for (int i = 0; i < address.length; i++) {
					String email = address[i].getAddress();
					if (email == null)
						email = "";
					else {
						email = MimeUtility.decodeText(email);
					}
					String personal = address[i].getPersonal();
					if (personal == null)
						personal = "";
					else {
						personal = MimeUtility.decodeText(personal);
					}
					String compositeto = personal + "<" + email + ">";
					mailaddr += "," + compositeto;
				}
				mailaddr = mailaddr.substring(1);
			}
		} else {
			throw new Exception("Error emailaddr type!");
		}
		return mailaddr;
	}

	/**
	 * ����ʼ�����
	 */
	public String getSubject() throws MessagingException {
		String subject = "";
		try {
			subject = MimeUtility.decodeText(mimeMessage.getSubject());
			if (subject == null)
				subject = "";
		} catch (Exception exce) {
		}
		return subject;
	}

	/**
	 * ����ʼ���������
	 */
	public String getSentDate() throws Exception {
		Date sentdate = mimeMessage.getSentDate();
		SimpleDateFormat format = new SimpleDateFormat(dateformat);
		return format.format(sentdate);
	}

	/**
	 * ����ʼ���������
	 */
	public String getBodyText() {
		return bodytext.toString();
	}

	/**
	 * �����ʼ����ѵõ����ʼ����ݱ��浽һ��StringBuffer�����У������ʼ� ��Ҫ�Ǹ���MimeType���͵Ĳ�ִͬ�в�ͬ�Ĳ�����һ��һ���Ľ���
	 */
	public void getMailContent(Part part) throws Exception {
		String contenttype = part.getContentType();
		int nameindex = contenttype.indexOf("name");
		boolean conname = false;
		if (nameindex != -1)
			conname = true;
		logger.info("CONTENTTYPE: " + contenttype);
		contentTypeSB.append(contenttype);

		if (part.isMimeType("text/plain") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("text/html") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int counts = multipart.getCount();
			for (int i = 0; i < counts; i++) {
				getMailContent(multipart.getBodyPart(i));
			}
		} else if (part.isMimeType("message/rfc822")) {
			getMailContent((Part) part.getContent());
		} else {
		}
	}

	/**
	 * �жϴ��ʼ��Ƿ���Ҫ��ִ�������Ҫ��ִ����"true",���򷵻�"false"
	 */
	public boolean getReplySign() throws MessagingException {
		boolean replysign = false;
		String needreply[] = mimeMessage
				.getHeader("Disposition-Notification-To");
		if (needreply != null) {
			replysign = true;
		}
		return replysign;
	}

	/**
	 * ��ô��ʼ���Message-ID
	 */
	public String getMessageId() throws MessagingException {
		return mimeMessage.getMessageID();
	}

	/**
	 * ���жϴ��ʼ��Ƿ��Ѷ������δ�����ط���false,��֮����true��
	 */
	public boolean isNew() throws MessagingException {
		boolean isnew = false;
		Flags flags = ((Message) mimeMessage).getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		logger.info("flags's length: " + flag.length);
		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == Flags.Flag.SEEN) {
				isnew = true;
				logger.info("seen Message.......");
				break;
			}
		}
		return isnew;
	}

	/**
	 * �жϴ��ʼ��Ƿ��������
	 */
	public boolean isContainAttach(Part part) throws Exception {
		boolean attachflag = false;
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition
								.equals(Part.INLINE))))
					attachflag = true;
				else if (mpart.isMimeType("multipart/*")) {
					attachflag = isContainAttach((Part) mpart);
				} else {
					String contype = mpart.getContentType();
					if (contype.toLowerCase().indexOf("application") != -1)
						attachflag = true;
					if (contype.toLowerCase().indexOf("name") != -1)
						attachflag = true;
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			attachflag = isContainAttach((Part) part.getContent());
		}
		return attachflag;
	}

	/**
	 * �����渽����
	 */
	public boolean saveAttachMent(Part part, String outputFileName)
			throws Exception {
		String fileName = "";
		boolean getOutputFlag = false;
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition
								.equals(Part.INLINE)))) {
					fileName = mpart.getFileName();
					if (fileName.toLowerCase().indexOf("gb2312") != -1) {
						fileName = MimeUtility.decodeText(fileName);
					}
					if (fileName.equals(outputFileName)) {
						saveFile(fileName, mpart.getInputStream());
						getOutputFlag = true;

					}
				} else if (mpart.isMimeType("multipart/*")) {
					saveAttachMent(mpart, outputFileName);
				} else {
					fileName = mpart.getFileName();
					if ((fileName != null)
							&& (fileName.toLowerCase().indexOf("GB2312") != -1)) {
						fileName = MimeUtility.decodeText(fileName);
						if (fileName.equals(outputFileName)) {
							saveFile(fileName, mpart.getInputStream());
							getOutputFlag = true;
						}
					}
				}
				if (getOutputFlag) {
					break;
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			saveAttachMent((Part) part.getContent(), outputFileName);
		}
		return getOutputFlag;
	}

	/**
	 * �����ø������·����
	 */

	public void setAttachPath(String attachpath) {
		this.saveAttachPath = attachpath;
	}

	/**
	 * ������������ʾ��ʽ��
	 */
	public void setDateFormat(String format) throws Exception {
		this.dateformat = format;
	}

	/**
	 * ����ø������·����
	 */
	public String getAttachPath() {
		return saveAttachPath;
	}

	/**
	 * �������ı��渽����ָ��Ŀ¼�
	 */
	private void saveFile(String fileName, InputStream in) throws Exception {
		String osName = System.getProperty("os.name");
		String storedir = getAttachPath();
		String separator = "";
		if (osName == null)
			osName = "";
		if (osName.toLowerCase().indexOf("win") != -1) {
			separator = "\\";
			if (storedir == null || storedir.equals(""))
				storedir = "c:\\tmp";
		} else {
			separator = "/";
			storedir = "/tmp";
		}
		File storefile = new File(storedir + separator + fileName);
		logger.info("storefile's path: " + storefile.toString());
		// for(int i=0;storefile.exists();i++){
		// storefile = new File(storedir+separator+fileName+i);
		// }
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(storefile));
			bis = new BufferedInputStream(in);
			int c;
			while ((c = bis.read()) != -1) {
				bos.write(c);
				bos.flush();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("�ļ�����ʧ��!");
		} finally {
			bos.close();
			bis.close();
		}
	}

	private void swap(int i1, int i2, Message[] messages) {

		Message tempMessage = messages[i1];
		messages[i1] = messages[i2];
		messages[i2] = tempMessage;

	}

	public Message[] insertSort(Message[] messages) {
		for (int i = 1; i < messages.length; i++) {
			try {
				for (int j = i; j > 0
						&& (((MimeMessage) messages[j - 1]).getSentDate()
								.before(((MimeMessage) messages[j])
										.getSentDate())); j--) {
					{

						swap(j - 1, j, messages);
					}
				}
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return messages;
	}

	public boolean receiveEmail(String subject, String fileName,
			String outputFolder) {
		boolean success = false;
		Properties props = System.getProperties();
		props.put("mail.pop3.host", this.host);
		props.put("mail.pop3.auth", "true");
		props.setProperty("mail.pop3.disabletop", "true");
		Session session = Session.getDefaultInstance(props, null);
		// session.setDebug(true);
		URLName urln = new URLName("pop3", this.host, this.port, null,
				this.userName, this.password);
		Store store;
		try {
			store = session.getStore(urln);
			store.connect();
			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_ONLY);
			logger
					.warn("Wait......Searching the emails with suject containing : "
							+ subject);
			SearchTerm subterm = new SubjectTerm(subject);
			// SearchTerm fterm = new FromTerm(new
			// InternetAddress("csqa2@oocl.com"));
			// SearchTerm st=new AndTerm(subterm,fterm);
			// Message message[] = folder.getMessages();
			Message[] message = folder.search(subterm);

			message = insertSort(message);

			logger.info("Message amount: " + message.length);
			POP3Handler pmm = null;
			for (int i = 0; i < message.length; i++) {
				// logger.info("======================");
				pmm = new POP3Handler((MimeMessage) message[i]);
				// logger.info("Message " + i + " subject: " +
				// pmm.getSubject());
				// if (!pmm.getSubject().contains(subject)){
				// continue;
				// }
				pmm.getMailContent((Part) message[i]);
				if (!pmm.getContentTypeSB().contains(fileName)) {
					continue;
				}
				logger.info("Message : " + (i + 1));

				pmm.setDateFormat("yyyy-MM-dd HH:mm:ss");
				logger.info("Sent date : " + pmm.getSentDate());
				// logger.info(""+ pmm.getReplySign());
				logger.info("Has Read : " + pmm.isNew());
				logger.info("Contain Attachment : "
						+ pmm.isContainAttach((Part) message[i]));
				logger.info("From : " + pmm.getFrom());
				logger.info("To : " + pmm.getMailAddress("to"));

				logger.info("Message-ID : " + pmm.getMessageId());
				// ����ʼ�����===============

				logger.info("Content : " + pmm.getBodyText());
				pmm.setAttachPath(outputFolder);
				if (pmm.saveAttachMent((Part) message[i], fileName)) {
					success = true;
					break;
				}

			}
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;

	}

}
