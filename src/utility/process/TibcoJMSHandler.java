package utility.process;

import javax.jms.*;

import java.io.Console;
import java.io.File;

import org.apache.log4j.Logger;

import com.tibco.tibjms.TibjmsConnection;
import com.tibco.tibjms.TibjmsConnectionFactory;
import com.tibco.tibjms.TibjmsQueueConnectionFactory;
import com.tibco.tibjms.TibjmsSession;

public class TibcoJMSHandler {
	static Logger logger = Logger.getLogger(TibcoJMSHandler.class.getName());

	private String serverUrl;

	private String userName;

	private String password;

	private TibjmsConnectionFactory factoryStress;
	private TibjmsConnection connectionStress;
	private TibjmsSession sessionStress;
		

	public TibcoJMSHandler(String serverUrl, String userName, String password) {

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
			String involveMsgReqID, String country, String fileName,
			String inputFile, String tempFileName) {

		boolean publishSuccess = false;
		if (destinationQueue == null) {
			logger.error("Error: must specify queue name !");
		}

		logger.warn(tpID + "  " + msgType + "  " + dirID + "  " + inputFile);
		logger.info("Publish into queue: '" + destinationQueue + "'");
		logger.info("Reply to queue: '" + replyToQueue + "'");

		try {

			TibjmsConnectionFactory factory = new TibjmsConnectionFactory(
					serverUrl);

			if (password.toLowerCase().trim().equals("<null>")) {
				password = null;
			}
			TibjmsConnection connection = (TibjmsConnection)factory.createConnection(
					userName, password);

			TibjmsSession session = (TibjmsSession)connection.createSession(true,
					Session.AUTO_ACKNOWLEDGE);

			/*
			 * Use createQueue() to enable sending into dynamic queues.
			 */
			MessageProducer sender;
			if(destinationQueue.trim().toLowerCase().endsWith("tpc")){
				Topic topic =  session.createTopic(destinationQueue);
				 sender = session.createProducer(topic);
			}else{
				Queue queue = session.createQueue(destinationQueue);
				 sender = session.createProducer(queue);
			}
						
		//	MessageProducer sender = session.createProducer(queue);
			
			TextMessage message = session.createTextMessage();
			File input = new File(inputFile);
			String text = null;
			if (input.isFile() && input.exists()) {
				text = FunctionHelper.readContent(input);
			} else {
				text = inputFile;
			}

			message.setText(text);
			//for OB2B via jms, add some new properties  (D:\SVN\Regression\OB2B\MFESTIB)
			if(msgType.equalsIgnoreCase("MFESTIB") && dirID.equalsIgnoreCase("O")){
				message=addBooleanProperty(message, "JMS_TIBCO_COMPRESS", true);
				message=addStringProperty(message, "MF_RootElement", "CustomsUif");
				message=addStringProperty(message, "Carrier", "OOCL");
				message=addStringProperty(message, "XCID", "");
				message=addStringProperty(message, "DomainId", "CDM");
				message=addStringProperty(message, "ProcessId", "");
				message=addStringProperty(message, "Country", "IN");
				message=addStringProperty(message, "Filename", tempFileName);
				message=addStringProperty(message, "MF_AppNamespace", "com.oocl.ir4.cdm.uif.outgoing");
				message=addIntegerProperty(message, "MFCONSOLE_AutoRetryCount", 0);
				message = addStringProperty(message, "TpId", tpID);
				message = addStringProperty(message, "MsgType", msgType);
			}else{
				message = addStringProperty(message, "MsgRequestId", tempFileName);
				message = addStringProperty(message, "TpId", tpID);
				message = addStringProperty(message, "MsgType", msgType);
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
			}
			//
			if(tpID.equals("WCOCTSLK")){
				message = addStringProperty(message, "Filename", tempFileName);
			}
			if (!replyToQueue.trim().equals("")) {
				if(replyToQueue.endsWith(".TPC")){
					Topic topic = session.createTopic(replyToQueue);
					message.setJMSReplyTo(topic);
				}else{
				Queue rqueue = session.createQueue(replyToQueue);
				message.setJMSReplyTo(rqueue);
			}
			}

			// FOR OOCL EU24
			if (tpID.toLowerCase().startsWith("oocl")
					|| msgType.toLowerCase().startsWith("ie") || tpID.equals("WCOCTSLK")) {
				message = addStringProperty(message, "country", country);
				if(destinationQueue.startsWith("IR4.Customs"))
					message = addStringProperty(message, "Filename", fileName);
				else
					message = addStringProperty(message, "filename", fileName);
				
				message = addStringProperty(message, "tpId", tpID);
				message = addStringProperty(message, "msgType", msgType);
				message.setJMSCorrelationID(tempFileName);
			}
			//
		
			message.setJMSType(null);
			sender.setDeliveryMode(2);
			sender.setPriority(0);
			sender.send(message);
			session.commit();
			session.close();
			connection.close();
			publishSuccess = true;
			logger.info("Publishing is finished.");

		} catch (JMSException e) {
			logger.error("Encounter error when try to publish messages.");
			e.printStackTrace();
			return publishSuccess;
		}
		return publishSuccess;
	}

	
	
	public boolean publishStress(String destinationQueue, String replyToQueue,
			String tpID, String msgType, String dirID, String externalParty,
			String msgFormat, 
			String filecomtext, String tempFileName,String msgreqid) {

		boolean publishSuccess = false;
		if (destinationQueue == null) {
			logger.error("Error: must specify queue name !");
		}

		logger.warn(tpID + "  " + msgType + "  " + dirID + "  " + tempFileName);
		logger.info("Publish into queue: '" + destinationQueue + "'");
		logger.info("Reply to queue: '" + replyToQueue + "'");

		try {

			
			/*
			 * Use createQueue() to enable sending into dynamic queues.
			 */
			MessageProducer sender;
			if(destinationQueue.trim().toLowerCase().endsWith(".tpc")){
				Topic topic =  sessionStress.createTopic(destinationQueue);
				 sender = sessionStress.createProducer(topic);
			}else{
				Queue queue = sessionStress.createQueue(destinationQueue);
				 sender = sessionStress.createProducer(queue);
			}
			
			

		//	MessageProducer sender = session.createProducer(queue);

			TextMessage message = sessionStress.createTextMessage();
		/*	File input = new File(inputFile);
			String text = null;
			if (input.isFile() && input.exists()) {
				text = BaseFunctionHelper.readContent(input);
			} else {
				text = inputFile;
			}*/

			message.setText(filecomtext);
			
			message = addStringProperty(message, "FileName", tempFileName);
			message = addStringProperty(message, "MsgRequestId", msgreqid);
			message = addStringProperty(message, "TpId", tpID);
			message = addStringProperty(message, "MsgType", msgType);
			message = addStringProperty(message, "ExternalParty", "VLTRADER");
			message = addStringProperty(message, "MsgFormat", msgFormat);
			message = addStringProperty(message, "Direction", dirID);
			message = addStringProperty(message, "TransportType", "AS2");
			message = addStringProperty(message, "MessagePriority", "3");
			
			
			
			message = addStringProperty(message,
					"JMS_TIBCO_PRESERVE_UNDELIVERED", "true");


			if (!replyToQueue.trim().equals("")) {
				if(replyToQueue.endsWith(".TPC")){
					Topic topic = sessionStress.createTopic(replyToQueue);
					message.setJMSReplyTo(topic);
				}else{
					Queue rqueue = sessionStress.createQueue(replyToQueue);
					message.setJMSReplyTo(rqueue);
				}
			}

			
			message.setJMSType(null);
			sender.setDeliveryMode(2);
			sender.setPriority(4);
			sender.send(message);
			sessionStress.commit();
		//	sessionStress.close();
		//	connectionStress.close();
			publishSuccess = true;
			logger.info("Publishing is finished.");

		} catch (JMSException e) {
			logger.error("Encounter error when try to publish messages.");
			e.printStackTrace();
			return publishSuccess;
		}
		return publishSuccess;
	}
	
	
	public boolean receive(String destinationQueue) {

		if (password.toLowerCase().trim().equals("<null>")) {
			password = null;
		}

		try {
			TibjmsConnectionFactory factory = new TibjmsConnectionFactory(
					serverUrl);

			if (password.toLowerCase().trim().equals("<null>")) {
				password = null;
			}
			TibjmsConnection connection = (TibjmsConnection)factory.createConnection(
					userName, password);

			TibjmsSession session = (TibjmsSession)connection.createSession(true,
					Session.AUTO_ACKNOWLEDGE);

			/*
			 * Use createQueue() to enable sending into dynamic queues.
			 */
			MessageConsumer messageConsumer;
			if(destinationQueue.trim().toLowerCase().endsWith(".tpc")){
				Topic topic =  session.createTopic(destinationQueue);
				 messageConsumer = session.createConsumer(topic);
			}else{
				Queue queue = session.createQueue(destinationQueue);
				 messageConsumer = session.createConsumer(queue);
			}
			
			Message message=messageConsumer.receive();
			logger.info("Receive message is :" + ((TextMessage) message).getText());
			
			/* messageConsumer.setMessageListener(new MessageListener() {
	                public void onMessage(Message message) {
	                    try {
	                    	logger.info("Receive message is :" + ((TextMessage) message).getText());
	                    } catch (JMSException e) {
	                        e.printStackTrace();
	                    }
	                }
	            });*/
	            
			}catch (Exception e) {
            e.printStackTrace();
			}

		return true;
	}
	public boolean publishMS2SD(String destinationQueue,
			String tpID, String msgType, String dirID,
			String msgFormat, String msgGdlVer, String msgCfgVer,
			String MsgRequestId,String text) {

		boolean publishSuccess = false;
		if (destinationQueue == null) {
			logger.error("Error: must specify queue name !");
		}

		logger.warn(tpID + "  " + msgType + "  " + dirID);
		logger.info("Publish into queue: '" + destinationQueue + "'");

		try {

			QueueConnectionFactory factory = new TibjmsQueueConnectionFactory(
					serverUrl);

			if (password.toLowerCase().trim().equals("<null>")) {
				password = null;
			}
			QueueConnection connection = factory.createQueueConnection(
					userName, password);

			QueueSession session = connection.createQueueSession(true,
					Session.AUTO_ACKNOWLEDGE);

			/*
			 * Use createQueue() to enable sending into dynamic queues.
			 */
			Queue queue = session.createQueue(destinationQueue);

			MessageProducer sender = session.createProducer(queue);

			TextMessage message = session.createTextMessage();

			message.setText(text);

			message = addStringProperty(message, "TpId", tpID);
			message = addStringProperty(message, "MsgType", msgType);
			message = addStringProperty(message, "MsgRequestId", MsgRequestId);
			message = addStringProperty(message, "MsgFormat", msgFormat);
			message = addStringProperty(message, "MsgGdlVer", msgGdlVer);
			message = addStringProperty(message, "MsgCfgVer", msgCfgVer);
			message = addStringProperty(message, "Direction", dirID);
			message = addStringProperty(message, "PrevMsgRequestId",
					"");
			message = addStringProperty(message, "NextMsgRequestId",
					"");

			message = addStringProperty(message,
					"JMS_TIBCO_PRESERVE_UNDELIVERED", "true");
			message = addStringProperty(message,
					"JMS_TIBCO_COMPRESS", "true");
		
			message.setJMSType(null);
			sender.setDeliveryMode(2);
			sender.setPriority(0);
			sender.send(message);
			session.commit();
			session.close();
			connection.close();
			publishSuccess = true;
			logger.info("Publishing is finished.");

		} catch (JMSException e) {
			logger.error("Encounter error when try to publish messages.");
			e.printStackTrace();
			return publishSuccess;
		}
		return publishSuccess;
	}


	public boolean publishForReprocess(String destinationQueue, String text) {

		boolean publishSuccess = false;
		if (destinationQueue == null) {
			logger.error("Error: must specify queue name !");
		}

		logger.info("Publish into queue: '" + destinationQueue + "'");

		try {
			QueueConnectionFactory factory = new TibjmsQueueConnectionFactory(
					serverUrl);

			QueueConnection connection = factory.createQueueConnection(
					userName, password);

			QueueSession session = connection.createQueueSession(true,
					Session.AUTO_ACKNOWLEDGE);

			/*
			 * Use createQueue() to enable sending into dynamic queues.
			 */
			Queue queue = session.createQueue(destinationQueue);

			MessageProducer sender = session.createProducer(queue);

			TextMessage message = session.createTextMessage();

			message.setText(text);

			message = addStringProperty(message,
					"JMS_TIBCO_PRESERVE_UNDELIVERED", "true");

			message.setJMSType(null);

			sender.setDeliveryMode(2);
			sender.setPriority(0);

			sender.send(message);
			session.commit();
			session.close();
			connection.close();
			publishSuccess = true;
			logger.info("Publishing is finished.");

		} catch (JMSException e) {
			logger.error("Encounter error when try to publish messages.");
			e.printStackTrace();
			return publishSuccess;
		}
		return publishSuccess;
	}

	// private String readfile(String inputFile) {
	// FileReader fr;
	// BufferedReader br;
	// StringBuffer sb = new StringBuffer();
	// String readLine = new String();
	// try {
	// fr = new FileReader(inputFile);
	// br = new BufferedReader(fr);
	//
	// while ((readLine = br.readLine()) != null) {
	// sb.append(readLine + "\n");
	// }
	// br.close();
	// fr.close();
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// return sb.toString();
	//
	// }

	private TextMessage addStringProperty(TextMessage message,
			String propertyName, String propertyValue) {
		if ((!propertyName.trim().equals("")) && (propertyValue != null)) {
			try {
				message.setStringProperty(propertyName, propertyValue);
				logger.info("Add String property " + propertyName + ":"
						+ propertyValue);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return message;

	}

	private TextMessage addBooleanProperty(TextMessage message,
			String propertyName, boolean propertyValue) {
		if (!propertyName.trim().equals("")) {
			try {
				message.setBooleanProperty(propertyName, propertyValue);
				logger.info("Add Boolean property " + propertyName + ":"
						+ propertyValue);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return message;

	}
	
	private TextMessage addIntegerProperty(TextMessage message,
			String propertyName, Integer propertyValue) {
		if ((!propertyName.trim().equals("")) && (propertyValue != null)) {
			try {
				message.setIntProperty(propertyName, propertyValue);
				logger.info("Add Integer property " + propertyName + ":"
						+ propertyValue);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return message;

	}
	public boolean connectEMSServer() {
		try {						
			factoryStress = new TibjmsConnectionFactory(serverUrl);
			connectionStress = (TibjmsConnection)factoryStress.createConnection(
					userName, password);

			sessionStress = (TibjmsSession)connectionStress.createSession(true,
					Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public boolean disconnectEMSServer() {
		try {
			sessionStress.close();
			connectionStress.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
}
