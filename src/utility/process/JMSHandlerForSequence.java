package utility.process;

import javax.jms.*;

import java.io.File;

import org.apache.log4j.Logger;

import com.tibco.tibjms.TibjmsQueueConnectionFactory;

public class JMSHandlerForSequence {
	static Logger logger = Logger.getLogger(JMSHandlerForSequence.class
			.getName());

	private String serverUrl;

	private String userName;

	private String password;

	private QueueConnectionFactory factory;
	private QueueConnection connection;
	private QueueSession session;

	public JMSHandlerForSequence(String serverUrl, String userName,
			String password) {

		this.serverUrl = serverUrl;
		this.userName = userName;
		this.password = password;
	}

	/**
	 * 
	 */
	public boolean publish(String destinationQueue, String replyToQueue,
			String tpID, String msgType, String dirID, String externalParty,
			String msgFormat, String msgGdlVer, String msgCfgVer,
			String prevMsgRequestId, String nextMsgRequestId,
			String involveMsgReqID, String country, String inputFile,
			String tempFileName) {

		boolean publishSuccess = false;
		if (destinationQueue == null) {
			logger.error("Error: must specify queue name !");
		}

		// logger.warn(tpID + "  " + msgType + "  " + dirID + "  " + inputFile);
		// logger.info("Publish into queue: '" + destinationQueue + "'");
		// logger.info("Reply to queue: '" + replyToQueue + "'");

		try {

			/*
			 * Use createQueue() to enable sending into dynamic queues.
			 */
			Queue queue = session.createQueue(destinationQueue);

			MessageProducer sender = session.createProducer(queue);

			TextMessage message = session.createTextMessage();
			File input = new File(inputFile);
			String text = null;
			if (input.isFile() && input.exists()) {
				text = FunctionHelper.readContent(input);
			} else {
				text = inputFile;
			}

			message.setText(text);
			// message.setStringProperty("TpId", tpID);
			// message.setStringProperty("MsgType", msgType);
			// message.setStringProperty("MsgRequestId", tempFileName);
			// message.setStringProperty("ExternalParty", externalParty);
			// message.setBooleanProperty("JMS_TIBCO_PRESERVE_UNDELIVERED",
			// true);
			message = addStringProperty(message, "TpId", tpID);
			message = addStringProperty(message, "MsgType", msgType);
			message = addStringProperty(message, "MsgRequestId", tempFileName);
			message = addStringProperty(message, "ExternalParty", externalParty);
			message = addStringProperty(message, "MsgFormat", msgFormat);
			message = addStringProperty(message, "MsgGdlVer", msgGdlVer);
			message = addStringProperty(message, "MsgCfgVer", msgCfgVer);
			message = addStringProperty(message, "Direction", dirID);
			message = addStringProperty(message,
					"JMS_TIBCO_PRESERVE_UNDELIVERED", "true");
			message = addStringProperty(message, "PrevMsgRequestId",
					prevMsgRequestId);
			message = addStringProperty(message, "NextMsgRequestId",
					nextMsgRequestId);

			// FOR OOCL EU24
			if (tpID.toLowerCase().startsWith("oocl")
					|| msgType.toLowerCase().startsWith("ie")) {
				message = addStringProperty(message, "country", country);
				message = addStringProperty(message, "filename", tempFileName);
				message = addStringProperty(message, "tpId", tpID);
				message = addStringProperty(message, "msgType", msgType);
				message.setJMSCorrelationID(tempFileName);
			}
			//

			if(destinationQueue.trim().equals("CS.MCI2.JOB_REQ.B2B.OUT.QUE")){
				message = addStringProperty(message, "ContentTransferEncoding", "base64");
				message = addStringProperty(message, "ContentType", "application/octet-stream");
				message = addStringProperty(message, "country", country);
			}
			if (!replyToQueue.trim().equals("")) {
				if(replyToQueue.endsWith(".TPC")){
				Topic topic =  session.createTopic(destinationQueue);
				 message.setJMSReplyTo(topic);
				}else{
				
				Queue rqueue = session.createQueue(replyToQueue);
				message.setJMSReplyTo(rqueue);
				}
			}
			message.setJMSType(null);
			sender.setDeliveryMode(2);
			sender.setPriority(0);
			sender.send(message);
			session.commit();

			publishSuccess = true;
			// logger.info("Publishing is finished.");

		} catch (JMSException e) {
			logger.error("Encounter error when try to publish messages.");
			e.printStackTrace();
			return publishSuccess;
		}
		return publishSuccess;
	}
	
	public boolean publishStress(String destinationQueue, String replyToQueue,
			String tpID, String msgType,String externalParty, String fileContent,
			String tempFileName) {

		boolean publishSuccess = false;
		if (destinationQueue == null) {
			logger.error("Error: must specify queue name !");
		}

		// logger.warn(tpID + "  " + msgType + "  " + dirID + "  " + inputFile);
		// logger.info("Publish into queue: '" + destinationQueue + "'");
		// logger.info("Reply to queue: '" + replyToQueue + "'");

		try {

			/*
			 * Use createQueue() to enable sending into dynamic queues.
			 */
			Queue queue = session.createQueue(destinationQueue);

			MessageProducer sender = session.createProducer(queue);

			TextMessage message = session.createTextMessage();
	

			message.setText(fileContent);
			// message.setStringProperty("TpId", tpID);
			// message.setStringProperty("MsgType", msgType);
			// message.setStringProperty("MsgRequestId", tempFileName);
			// message.setStringProperty("ExternalParty", externalParty);
			// message.setBooleanProperty("JMS_TIBCO_PRESERVE_UNDELIVERED",
			// true);
			message = addStringProperty(message, "TpId", tpID);
			message = addStringProperty(message, "MsgType", msgType);
			message = addStringProperty(message, "ACKReceiver", "CARGOSMART");
			message = addStringProperty(message, "ExternalParty", externalParty);
			message = addStringProperty(message, "SCAC", "CMDU");
			message = addStringProperty(message, "MsgRequestId", tempFileName);

			message = addStringProperty(message,
					"JMS_TIBCO_PRESERVE_UNDELIVERED", "true");


			if (!replyToQueue.trim().equals("")) {
				if(replyToQueue.endsWith(".TPC")){
				Topic topic =  session.createTopic(replyToQueue);
				 message.setJMSReplyTo(topic);
				}else{
				
				Queue rqueue = session.createQueue(replyToQueue);
				message.setJMSReplyTo(rqueue);
				}
			}
			message.setJMSType(null);
			sender.setDeliveryMode(2);
			sender.setPriority(0);
			sender.send(message);
			session.commit();

			publishSuccess = true;
			// logger.info("Publishing is finished.");

		} catch (JMSException e) {
			logger.error("Encounter error when try to publish messages.");
			e.printStackTrace();
			return publishSuccess;
		}
		return publishSuccess;
	}
	
	
	public boolean receive(String destinationQueue, String replyToQueue,
			String tpID, String msgType, String dirID, String externalParty,
			String msgFormat, String msgGdlVer, String msgCfgVer,
			String prevMsgRequestId, String nextMsgRequestId,
			String involveMsgReqID, String country, String inputFile,
			String tempFileName) {
		
		return true;
	}

	private TextMessage addStringProperty(TextMessage message,
			String propertyName, String propertyValue) {
		if ((!propertyName.trim().equals("")) && (propertyValue != null)) {
			try {
				message.setStringProperty(propertyName, propertyValue);
				// logger.info("Add String property " + propertyName + ":"
				// + propertyValue);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return message;

	}

	public boolean connectEMSServer() {

		try {
			factory = new TibjmsQueueConnectionFactory(serverUrl);
			connection = factory.createQueueConnection(userName, password);

			session = connection.createQueueSession(true,
					Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public boolean disconnectEMSServer() {
		try {
			session.close();
			connection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

}
