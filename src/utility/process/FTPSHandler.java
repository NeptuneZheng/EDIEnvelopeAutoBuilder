package utility.process;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

public class FTPSHandler {

	private String host;
	private String username;
	private String password;
	private int port;

	static Logger logger = Logger.getLogger(FTPSHandler.class);
	private FTPClient ftp = null;
	public FTPSHandler(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	public void connect() {

		logger.info("Creating FTPS client");

		TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub
			}
		} };
		SSLContext sslContext = null;

		try {
			sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustManager, new SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
		ftp = new FTPClient();
		ftp.setSSLSocketFactory(sslSocketFactory);
		ftp.setSecurity(FTPClient.SECURITY_FTPES);
		//ftp.setPassive(false);

		logger.info("Connecting to server " + host + " , port :" + port);
		try {
			ftp.connect(host, port);

			logger.info("Logging in with username=" + username
					+ " and password=" + password);
			ftp.login(username, password);
			logger.info("Logged in");
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPIllegalReplyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void close() {

		try {
			ftp.disconnect(true);
			ftp = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPIllegalReplyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Quit !");

	}

	public boolean uploadByFtps(String ftpFolder, String filePath,
			String newFileName) {

		boolean success = false;

		try {
			connect();
			
			ftp.changeDirectory(ftpFolder);
			logger.info("Change working folder to " + ftpFolder);
			File localFile = new File(filePath);
			File renamedFile = new File(localFile.getParent() + "/"
					+ newFileName);
			logger.info("Rename local file to " + newFileName);
			localFile.renameTo(renamedFile);
			logger.info("Rename file is: " + renamedFile);
			ftp.upload(renamedFile);
			logger.info("upload file " + newFileName);
			logger.info("Rename " + newFileName + " back to "
					+ localFile.getName());
			renamedFile.renameTo(localFile);

			logger.info("Successfully transferred in ASCII mode");

			success = true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return success;

	}

	public boolean downLoadByFtps(String ftpFolder, String filePath,
			String fileName) {

		boolean success = false;

		connect();
		try {
			ftp.changeDirectory(ftpFolder);
			logger.info("Change working folder to " + ftpFolder);
			ftp.download(fileName, new File(filePath));

			success = true;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPIllegalReplyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPDataTransferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
		return success;

	}

}
