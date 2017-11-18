package utility.process;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FTPTransferType;

public class FtpHandler {
	static Logger logger = Logger.getLogger(FtpHandler.class);
	private String host;
	private String username;
	private String password;
	private int port;

	public FtpHandler(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;

	}

	// remove files in FTP
	public boolean moveFileByFTP(String folderPath){
		boolean removed = false;
		FTPClient ftp = null;
		//List fileList = new ArrayList();
		try {

			// create client
			ftp = new FTPClient();
			logger.info("Created FTP client");
			// set remote host
			ftp.setRemoteHost(host);
			ftp.setRemotePort(port);

			// connect to the server
			ftp.connect();
			logger.info("Connected to server " + host + ":" + port);

			// log in
			ftp.login(username, password);

			logger.info("Logged in with username=" + username
					+ " and password=" + password);
			logger.info(folderPath);

			FTPFile[] files = ftp.dirDetails(folderPath);
			if(files.length>0){
				for (int i =0 ;i<files.length;i++){
					FTPFile temp = files[i];
					logger.info("FilePath : "+temp.getPath()+ "; FileName : "+temp.getName());
					ftp.delete(temp.getName());
				}

			}else logger.warn("No file need to move");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FTPException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return removed;
	}
	public boolean uploadByFTP(String folderPath, String filePath,
			String newFileName,String TransferTypeFTP) {

		// set up logger so that we get some output

		boolean success = false;
		FTPClient ftp = null;

		try {
			// create client

			ftp = new FTPClient();
			logger.info("Created FTP client");
			// set remote host

			ftp.setRemoteHost(host);
			ftp.setRemotePort(port);

			// connect to the server

			ftp.connect();
			logger.info("Connected to server " + host + ":" + port);

			// log in
			ftp.login(username, password);

			logger.info("Logged in with username=" + username
					+ " and password=" + password);
			if(!StringUtils.isEmpty(TransferTypeFTP)){
				if(TransferTypeFTP.trim().toLowerCase().equals("ascii"))
					ftp.setType(FTPTransferType.ASCII);
				else
					ftp.setType(FTPTransferType.BINARY);
			}else
				ftp.setType(FTPTransferType.BINARY);
			
			ftp.chdir(folderPath);

			logger.info("Changed current folder to: " + folderPath);

			// logger.info("Uploading file");
			ftp.put(filePath, newFileName);
			logger.info("File uploaded : " + newFileName);
			success = true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Shut down client

			try {
				ftp.quit();
				logger.info("Quited client");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return success;
	}
	
	public boolean uploadByFTPList(String vlPath,ArrayList <String> inputFileList) {

		// set up logger so that we get some output

		boolean success = false;
		FTPClient ftp = null;

		try {
			// create client

			ftp = new FTPClient();
			logger.info("Created FTP client");
			// set remote host

			ftp.setRemoteHost(host);
			ftp.setRemotePort(port);

			// connect to the server

			ftp.connect();
			logger.info("Connected to server " + host + ":" + port);

			// log in
			ftp.login(username, password);

			logger.info("Logged in with username=" + username
					+ " and password=" + password);

			ftp.setType(FTPTransferType.BINARY);

			ftp.chdir(vlPath);

			logger.info("Changed current folder to " + vlPath);

			for(int i=0;i<inputFileList.size();i++){
				File inputFile=new File(inputFileList.get(i));
				logger.info("Upload File "+i+":"+inputFile.getPath());
				ftp.put(inputFile.getPath(), inputFile.getName());
			}
			// logger.info("Uploading file");
			

			success = true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Shut down client

			try {
				ftp.quit();
				logger.info("Quited client");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return success;
	}


	public String downloadByFTP(String folderPath, String outputFolder,
			String msgReqID, String inputFileName) {

		FTPClient ftp = null;
		String ftpFileName, outputFileName = null;
		ftp = new FTPClient();
		logger.info("Created FTP client");
		// set remote host

		try {
			ftp.setRemoteHost(host);
			ftp.setRemotePort(port);

			// connect to the server

			ftp.connect();
			logger.info("Connected to server " + host + ":" + port);

			// log in

			ftp.login(username, password);
			logger.info("Logged in with username=" + username
					+ " and password=" + password);
			ftp.setType(FTPTransferType.BINARY);
			ftp.chdir(folderPath);
			logger.info("Changed current folder to " + folderPath);
			FTPFile[] files = ftp.dirDetails(".");

			for (int i = 0; i < files.length; i++) {
				ftpFileName = files[i].getName();
				if (ftpFileName.startsWith(msgReqID)
						&& !ftpFileName.contains(inputFileName)) {
					outputFileName = ftpFileName;
					break;
				}
			}
			if (outputFileName != null) {

				ftp.get(outputFolder + outputFileName, outputFileName);
				logger.info("File downloaded : " + outputFileName);
			} else {
				logger.error("Can't find output in FTP !");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Shut down client

			try {
				ftp.quit();
				logger.info("Quitted client");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return outputFileName;

	}
	
	public String downloadByFTPOLL(String folderPath, String outputFolder,String inputFileName,String outputFileName) {
		boolean success = false;
		FTPClient ftp = null;
		ftp = new FTPClient();
		logger.info("Created FTP client");
		// set remote host

		try {
			ftp.setRemoteHost(host);
			ftp.setRemotePort(port);

			// connect to the server

			ftp.connect();
			logger.info("Connected to server " + host + ":" + port);

			// log in

			ftp.login(username, password);
			logger.info("Logged in with username=" + username
					+ " and password=" + password);
			
			ftp.setType(FTPTransferType.BINARY);
			ftp.chdir(folderPath);
			logger.info("Changed current folder to " + folderPath);			
		
			ftp.get(outputFolder + inputFileName+".xml", outputFileName);
			logger.info("File downloaded : " + outputFileName);
			logger.info(outputFolder + inputFileName);
			success = true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			// Shut down client

			try {
				ftp.quit();
				logger.info("Quitted client");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (inputFileName+".xml");

	}
	
	public boolean downloadByFTPCNZIP(String folderPath, String outputFolder,String fileName) {
		boolean success = false;
		FTPClient ftp = null;
		ftp = new FTPClient();
		logger.info("Created FTP client");
		// set remote host

		try {
			ftp.setRemoteHost(host);
			ftp.setRemotePort(port);

			// connect to the server

			ftp.connect();
			logger.info("Connected to server " + host + ":" + port);

			// log in

			ftp.login(username, password);
			logger.info("Logged in with username=" + username
					+ " and password=" + password);
			
			ftp.setType(FTPTransferType.BINARY);
			ftp.chdir(folderPath);
			logger.info("Changed current folder to " + folderPath);
			

			logger.info(outputFolder + fileName);
			ftp.get(outputFolder + fileName, fileName);
			logger.info("File downloaded : " + fileName);
			success = true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			// Shut down client

			try {
				ftp.quit();
				logger.info("Quitted client");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return success;

	}

	public boolean downloadByVLFTP(String folderPath, String filePath,
			String fileName) {

		boolean success = false;
		FTPClient ftp = null;
		logger.info("Creating FTP client");
		ftp = new FTPClient();

		// set remote host
		logger.info("Created FTP client");
		// set remote host

		try {
			ftp.setRemoteHost(host);
			ftp.setRemotePort(port);

			// connect to the server

			ftp.connect();
			logger.info("Connected to server " + host + ":" + port);

			// log in

			ftp.login(username, password);
			logger.info("Logged in with username=" + username
					+ " and password=" + password);
			if(fileName.toLowerCase().endsWith(".zip"))
				ftp.setType(FTPTransferType.BINARY);
			ftp.chdir(folderPath);
			logger.info("Changed current folder to " + folderPath);

			ftp.get(filePath, fileName);
			logger.info("File downloaded : " + fileName);

			success = true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Shut down client

			try {
				ftp.quit();
				logger.info("Quitted client");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return success;

	}

}
