package utility.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.util.Properties;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPHandler {
	private String host;
	private String username;
	private String password;
	private int port;
	static Logger logger = Logger.getLogger(SFTPHandler.class);
	private ChannelSftp sftp = null;

	public SFTPHandler(String host, int port, String username, String password) {

		// extract command-line arguments
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;

	}

	public void connect() {
		try {
			if (sftp != null) {
				System.out.println("sftp is not null");
			}
			logger.info("Wait 30s before connection...");
			Thread.sleep(30000);
			JSch jsch = new JSch();
			// jsch.getSession(username, host, port);
			Session sshSession = jsch.getSession(username, host, port);
			logger.info("Session created.");
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			logger.info("Session connected.");
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			logger.info("Channel opened.");
			sftp = (ChannelSftp) channel;
			logger.info("Connected to " + host + ":" + port + " with "
					+ username + "/" + password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Disconnect with server
	 */
	public void disconnect() {
		if (this.sftp != null) {
			if (this.sftp.isConnected()) {
				this.sftp.disconnect();
				logger.info("SFTP is closed !");
			} else if (this.sftp.isClosed()) {
				System.out.println("sftp is closed already");
			}
		}

	}

	// Logger.setLevel(Level.INFO);
	public boolean uploadBySFTP(String ftpFolder, String filePath,
			String newFileName) {

		boolean success = false;
		try {
			connect();

			sftp.cd(ftpFolder);
			logger.info("Change working directory to " + ftpFolder
					+ " successfully!");

			sftp.put(new FileInputStream(new File(filePath)), newFileName);
			success = true;
			logger.info("Upload " + filePath + " with new filename "
					+ newFileName + " successfully !");
			// sftp.put(filePath, "123.txt");
		} catch (SftpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			disconnect();
		}

		return success;

	}

	public boolean downLoadBySFTP(String ftpFolder, String filePath,
			String fileName) {

		boolean success = false;
		try {
			connect();

			sftp.cd(ftpFolder);
			logger.info("Change working directory to " + ftpFolder
					+ " successfully!");
			File file = new File(filePath);
			sftp.get(fileName, new FileOutputStream(file));

			success = true;
			logger.info("Download " + fileName + " to " + filePath
					+ " successfully !");
			// sftp.put(filePath, "123.txt");
		} catch (SftpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			disconnect();
		}

		return success;

	}

}
