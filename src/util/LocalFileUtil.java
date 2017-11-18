package util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class LocalFileUtil { 
	
	public static String compareStr(String s1, String s2) {
		String[] s1s = s1.split("\r\n");
		String[] s2s = s2.split("\r\n");
		
		if (s1s.length!=s2s.length)
			return "Line count is not same. lines: "+s1s.length+" - "+s2s.length;
		for(int i=0; i<s1s.length; i++) {
			if (! s1s[i].trim().equals(s2s[i].trim()))
				return "Line "+(i+1)+" is not same: "+s1s[i]+" ==> " +s2s[i]; 
		}
		return "";
	}
	
	public static void writeToFileWithEncoding(String fileName, String body, String encoding) throws Exception {
		OutputStreamWriter osw = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileName);
			if (encoding==null || encoding.trim().length()==0)
				osw = new OutputStreamWriter(fos);
			else
				osw = new OutputStreamWriter(fos, encoding);
	        osw.write(body);
	        osw.flush(); 
		} finally {
			if (osw!=null) {
				try {osw.close();}catch(Exception x) {}
			}
			if (fos!=null) {
				try {fos.close();}catch(Exception x) {}
			}
		}
	}
	
	public static void writeToFile(String fileName, String body, boolean append) throws Exception {
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileName, append);
			fw.write(body);
			fw.flush();
			fw.close();
		} finally {
			if (fw!=null) {
				try {fw.close();}catch(Exception x) {}
			}
		}
	}
	
	public static void writeBytesToFile(String fileName, byte[] bodyByteArray) throws Exception {
		FileOutputStream fos = new FileOutputStream(fileName);
		try {
			fos.write(bodyByteArray);
			fos.flush();
		} finally {
			if (fos!=null) {
				try {fos.close();}catch(Exception x) {}
			}
		}
	}
	
	public static String readBigFileContentDirectly(String fileFullName) throws Exception {
		String str = null;
		InputStream is = null;
		File f = new File(fileFullName);
		is = new FileInputStream(f);
		try {
			byte[] b = new byte[(int)f.length()];
			int n;
			while ((n = is.read(b, 0, (int)f.length())) != -1) {
				str = new String(b, 0, n);
			}
			b = null;
		} finally {
			if (is!=null)
				is.close();
		}
		return str;
	}
	
	public static String readBigFile(String fileFullName) throws Exception {
		StringBuffer str = new StringBuffer();
		int bufSize = 1*1024*1024;
		byte[] bs = new byte[bufSize];
		ByteBuffer byteBuf = ByteBuffer.allocate(bufSize);
	    @SuppressWarnings("resource")
		FileChannel channel = new RandomAccessFile(fileFullName, "r").getChannel();
	    int size;
	    while((size = channel.read(byteBuf)) != -1) {
	        byteBuf.rewind();
	        byteBuf.get(bs);
	        str.append(new String(bs, 0, size));
	        byteBuf.clear();
	    }
	    channel.close();
		return str.toString();
	}
	
	public static String readBigFileContentDirectlyWithEncoding(String fileFullName, String encoding) throws Exception {
		String str = null;
		InputStream is = null;
		File f = new File(fileFullName);
		is = new FileInputStream(f);
		try {
			byte[] b = new byte[(int)f.length()];
			int n;
			while ((n = is.read(b, 0, (int)f.length())) != -1) {
				str = new String(b, 0, n, encoding);
			}
			b = null;
		} finally {
			if (is!=null)
				is.close();
		}
		return str;
	}
	
	public static String readBigFileContentDirectlyToBase64(String fileFullName) throws Exception {
		String str = null;
		InputStream is = null;
		File f = new File(fileFullName);
		is = new FileInputStream(f);
		try {
			byte[] b = new byte[is.available()];
			int readLen = is.read(b, 0, b.length);
			if (readLen>0)
				str = (new sun.misc.BASE64Encoder()).encode(b);
			b = null;
		} finally {
			if (is!=null)
				is.close();
		}
		return str;
	}
	
}
