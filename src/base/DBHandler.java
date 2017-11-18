package base;


import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.zip.ZipInputStream;

public class DBHandler {
	static Logger logger = Logger.getLogger(DBHandler.class.getName());
	private Connection connection;
	private String className="oracle.jdbc.driver.OracleDriver";
	private String url = null;
	private String password = null;
	private String username = null;
	private String env = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;
	public DBHandler(String url,String username,String password,String env){

		this.url = url;
		this.username = username;
		this.password = password;
		this.env = env;

	}

	public DBHandler(String env){
		if(env.equals("QA4")){
			this.env = "b2bdbqa4";
			this.url = "jdbc:oracle:thin:@b2bdbqa4:1521:b2bqa4";

		}else {
			this.env = "b2bdbqa3";
			this.url = "jdbc:oracle:thin:@b2bdbqa3:1521:b2bqa3";
		}
		this.username = "b2b_app";
		this.password = "b2bapp";

	}

	public DBHandler(){
		this.env = "b2bdbqa3";
		this.url = "jdbc:oracle:thin:@b2bdbqa3:1521:b2bqa3";
		this.username = "b2b_app";
		this.password = "b2bapp";

	}

	public boolean connectB2BDB(){
		boolean isConnected = false;
		Connection conn = null;
		try {
			Class.forName(className);
			if (connection == null) {
				DriverManager.setLoginTimeout(15);
				conn = DriverManager.getConnection(this.url, this.username, this.password);
				isConnected = true;
				System.out.println(this.env+" DB connection init.");
			}
			if (stmt == null) {
				stmt = conn.createStatement();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}


		return isConnected;
	}

	public String excuteWithB2BDB(String sql) {
		String result = null;
		logger.info("SQL="+sql);
		try {
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				if (rs.getString(1) != null) {
					result = rs.getString(1).trim();
				}
			}

			rs.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
//			// TODO Auto-generated catch block
		}
		return result;
	}
	public String getProcID(String messageID, String procSeq) {
		sql = "select proc_type_id from b2b_msg_req_process where proc_seq='"
				+ procSeq + "' and msg_req_id ='" + messageID + "'";
		String procID = excuteWithB2BDB(sql);
		return procID;
	}


	public boolean hasMCIDestination(String tp_id, String msg_type_id, String dir_id) {
	    boolean isMCI = false;
		sql = "select msg_guideline_id from B2B_MCI_DESTINATION_QUEUE where tp_id='"
				+ tp_id +"' and msg_type_id ='"+msg_type_id +"' and dir_id ='"+dir_id+"'" ;
		String msgGuidelineID = excuteWithB2BDB(sql);
		System.out.println(msgGuidelineID);
		if(msgGuidelineID!=null &&!msgGuidelineID.equals("")){
            isMCI =  true;
        }
		return isMCI;
	}
	public String getMaxProcSeq(String messageID) {
		String maxProcSeq = null;
		sql = "select max(proc_seq) from b2b_msg_req_process where msg_req_id ='"
				+ messageID + "'";
		maxProcSeq = excuteWithB2BDB(sql);
		return maxProcSeq;
	}
	private String getProcSeq(String messageID, String procID) {
		String procSeq = null;

		sql = "select proc_seq from b2b_msg_req_process where proc_type_id='"
				+ procID + "' and msg_req_id ='" + messageID + "'";

		procSeq = excuteWithB2BDB(sql);
		if (procSeq != null) {
			logger.info("Process Sequence for " + procID + " is : " + procSeq);

		} else {
			logger.warn("Can't find Process Sequence by " + sql);
			String msg_status=null;
			sql="select msg_proc_sts from b2b_msg_req_detail where msg_req_id ='" + messageID + "'";
			msg_status = excuteWithB2BDB(sql);

			if(msg_status.toLowerCase().equals("c")||msg_status.toLowerCase().equals("ce"))
			{
				logger.info("Get Max proc_seq from b2b_msg_req_process by MSG_REQ_ID");
				sql = "select max (proc_seq) from b2b_msg_req_process where msg_req_id ='" + messageID + "'";
				procSeq = excuteWithB2BDB(sql);
				if (procSeq != null){
					logger.info("Max Process Sequence for " + messageID + " is : " + procSeq);
				/*	sql = "select proc_type_id from b2b_msg_req_process where proc_seq='"
							+ procSeq + "' and msg_req_id ='" + messageID + "'";
					procID = excuteWithB2BAccount(sql);*/
					procID = getProcID(messageID,procSeq);
					if(procID.toLowerCase().trim().equals("sd2jms")||procID.toLowerCase().trim().equals("md2sd")){
						int Seq = Integer.parseInt(procSeq) - 1;
						procSeq = Integer.toString(Seq);
					/*	sql = "select proc_type_id from b2b_msg_req_process where proc_seq='"
								+ procSeq + "' and msg_req_id ='" + messageID + "'";
						procID = excuteWithB2BAccount(sql);*/
						procID = getProcID(messageID,procSeq);
						if(procID.toLowerCase().trim().equals("sd2jms")||procID.toLowerCase().trim().equals("md2sd")){
							Seq = Integer.parseInt(procSeq) - 1;
							procSeq = Integer.toString(Seq);
						}

					}
				}
				else
					logger.warn("Can't find Process Sequence by " + sql);
			}
		}
		logger.info("Get output from step :"+procSeq);
		//logger.info("procID="+procID);
		return procSeq;
	}

	public String upzipBlob(byte[] content) {
		String unzippedContent = "";
		try {
			unzippedContent = new String(unzip(content), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return unzippedContent;
	}

	private byte[] unzip(byte[] content) {
		byte[] unzippedContent = null;
		ByteArrayInputStream bais = null;
		ZipInputStream zis = null;
		try {
			bais = new ByteArrayInputStream(content);
			zis = new ZipInputStream(bais);
			zis.getNextEntry();
			unzippedContent = IOUtils.toByteArray(zis);
		} catch (IOException e) {
			logger.error("Cannot unzip the message", e);
		} finally {
			IOUtils.closeQuietly(bais);
			IOUtils.closeQuietly(zis);
		}
		return unzippedContent;
	}
	public String getFileFromDB(String messageID, String procID,
								 String filePath, int adjustStep) {
		String getFileName = "";

		try {
			String procSeq = getProcSeq(messageID, procID);
			String maxProcSeq = getMaxProcSeq(messageID);
			int runTimes = 10;
			while(runTimes>0){
				runTimes--;
				Thread.sleep(3000);
				if ((procSeq != null) && (maxProcSeq != null)) {
					int step = Integer.parseInt(procSeq);
					if (step == Integer.parseInt(maxProcSeq)) {
						sql = "select out_msg from b2b_msg_req_process where length(out_msg)>0 and proc_seq ='"
								+ step + "' and msg_req_id ='" + messageID + "'";
					} else {
						step = step + adjustStep;
						sql = "select  in_msg from b2b_msg_req_process where length(in_msg)>0 and proc_seq ='"
								+ step + "' and msg_req_id ='" + messageID + "'";
					}
					logger.info("Get file by " + sql);


					rs = stmt.executeQuery(sql);
					if (rs.next()) {
						Blob blob = rs.getBlob(1);
						byte[] samDocContent = blob.getBytes(1,
								(int) blob.length());

						String unzippedContent = upzipBlob(samDocContent);
						String fileName = filePath+messageID+"_"+procID+".in";
						File outputfolder = new File(fileName);
						if (!outputfolder.getParentFile().exists()) {
							outputfolder.getParentFile().mkdirs();
							logger.info("Create folder : "
									+ outputfolder.getParentFile().getPath());
						}
						BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8"));
						bw.write(unzippedContent);
						bw.close();
						getFileName = fileName;
						logger.info("Get file from db : " + fileName);
						break;
						// }
					} else {
						logger.warn("Step:" + step + " has no output data !");
					}

					rs.close();

//				}

				} else {
					logger.warn("Can't get the step for getting output.");
				}
			}


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//		} finally {
//			close();
		}
		return getFileName;
	}
	public String getMessageRequestIdByAssociatedID(String msgAssociatedID,
													String dirID, int timesToRun, int interval) {
		String messageId = null;
		int maxTime = timesToRun;
		try {
			logger.info("Associated Message Request ID: " + msgAssociatedID);
			logger.info("Waiting for message request id shows up......");
			Thread.sleep(1000 * interval);
			// sql =
			// "select msg_req_id from b2b_msg_req_detail_asso where msg_req_detail_asso like '%"
			// + msgAssociatedID
			// +
			// "%' and msg_req_detail_asso_type_id ='ASS_MSG_REQ_ID' and dir_id='"
			// + dirID + "' order by create_ts desc";

			sql = "select msg_req_id from b2b_msg_req_detail_asso where msg_req_detail_asso like '%"
						+ msgAssociatedID
						+ "%' and msg_req_detail_asso_type_id ='ASS_MSG_REQ_ID' "
						//Modify to avoid sometimes get IRA message ID by Susie Huang at 20120823
						+ "and DIR_ID ='"+ dirID + "'"
						+ " order by create_ts desc";
			while (true) {
				messageId = excuteWithB2BDB(sql);
				if (messageId != null) {

					logger.info("Message Request Id: " + messageId);
					break;
				} else {
					logger.info("Waiting for message request id shows up......");
					Thread.sleep(2000 * interval);

					maxTime--;

					if (maxTime <= 0) {

						logger.warn("Overtime: " + sql);
						break;

					}

				}

			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messageId;
	}
	public boolean close() {
		try {
			if (rs != null)
				rs.close();
			rs = null;
			if (stmt != null)
				stmt.close();
			stmt = null;
			if (connection != null)
				connection.close();
			connection = null;
			return true;
		} catch (SQLException ex) {
			System.out.println("Close Exception:" + ex.getMessage());
			return false;
		}
	}


}
