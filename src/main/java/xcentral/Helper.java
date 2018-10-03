package xcentral;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Signature;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;








import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import xcentral.Runner;

public class Helper extends Runner
{
	public static File filePath;
	public static File[] finalfile; 
	public static FTPFile[] files;
	public static FTPClient ftp = new FTPClient();
    public static Session session = null;
	public static Channel channel = null;
	public static String protocol="SFTP";
	public static ChannelSftp channelSftp = null;
	private static FileInputStream imageFile;
	public static InputStream iStream;
	
	
	public static void testlog(String keyword, String value, String remarks, ExtentTest testfeed) {
		if(keyword.equalsIgnoreCase("pass")) {
			testfeed.log(LogStatus.PASS, "<b>"+StringUtils.capitalize(value)+"<span style='color:green'> : "+StringUtils.capitalize(remarks)+"</span></b>");
		}
		else if(keyword.equalsIgnoreCase("fail")) {
			testfeed.log(LogStatus.FAIL, "<b>"+StringUtils.capitalize(value)+"<span style='color:red'> : "+StringUtils.capitalize(remarks)+"</span></b>");
		}
		else if(keyword.equalsIgnoreCase("info")) {
			testfeed.log(LogStatus.INFO, "<b>"+StringUtils.capitalize(value)+"<span style='color:blue'> : "+StringUtils.capitalize(remarks)+"</span></b>");
		}
	}
	
	
	public static void providehyperlink(String url, String linktext, ExtentTest testfeed) {
		testfeed.log(LogStatus.INFO, "<a href='"+url+"' target='_blank' style='color:blue'><u>"+StringUtils.capitalize(linktext)+"</u></a>");
	}
	
	
	public static JSONObject getDetails(String envId) throws Exception	{
		JSONObject finalObj = null;
		try	{
			JSONParser parser = new JSONParser();
			JSONArray jsonarray = (JSONArray) parser.parse(new InputStreamReader(new FileInputStream(new File("ftp.json"))));
			for(Object jsonobj : jsonarray) {
			  JSONObject myObj = (JSONObject) jsonobj;
			  String id = myObj.get("id").toString();
			  if(id.equalsIgnoreCase(envId)) {
				  finalObj = (JSONObject) myObj.get("data");
			  }
			}
		}	 
		catch(Exception ex) {
			ex.printStackTrace();
			throw new Exception("Unable to Find TestCase"+testcaseid);
		}
		return finalObj;
	}
	
	
	public static String getData(JSONObject object, String keyName) throws Exception	{
		try	{
			return object.get(keyName.toLowerCase()).toString(); 
		}
		catch(Exception e)	{
			e.printStackTrace();
			throw new Exception(keyName+" not found");
		}
	}
	
	public static boolean initShortcutKey(Actions actions,String keyCombination) throws AWTException {
		boolean flag = false;
		Robot rb = new Robot();
		try {
				if(keyCombination.equalsIgnoreCase("bold")) {
				if(browsername.equalsIgnoreCase("ie")){
				rb.keyPress(KeyEvent.VK_CONTROL);
				rb.keyPress(KeyEvent.VK_B);				
				rb.keyRelease(KeyEvent.VK_B);
				rb.keyRelease(KeyEvent.VK_CONTROL);
				}
				else{
					actions.keyDown(Keys.CONTROL).sendKeys("b").keyUp(Keys.CONTROL).build().perform();
				}	
			}
			else if(keyCombination.equalsIgnoreCase("italic")) {
				if(browsername.equalsIgnoreCase("ie")){
					rb.keyPress(KeyEvent.VK_CONTROL);
					rb.keyPress(KeyEvent.VK_I);
					rb.keyRelease(KeyEvent.VK_I);
					rb.keyRelease(KeyEvent.VK_CONTROL);
					}
					else{
						actions.keyDown(Keys.CONTROL).sendKeys("i").keyUp(Keys.CONTROL).build().perform();
					}	
			}
			else if(keyCombination.equalsIgnoreCase("superscript")) {
				if(browsername.equalsIgnoreCase("ie")){
					rb.keyPress(KeyEvent.VK_CONTROL);
					rb.keyPress(KeyEvent.VK_SHIFT);
					rb.keyPress(KeyEvent.VK_EQUALS);
					rb.keyRelease(KeyEvent.VK_EQUALS);
					rb.keyRelease(KeyEvent.VK_SHIFT);
					rb.keyRelease(KeyEvent.VK_CONTROL);
					}
					else{
						actions.keyDown(Keys.CONTROL).keyDown(Keys.SHIFT).sendKeys("=").keyUp(Keys.SHIFT).keyUp(Keys.CONTROL).build().perform();
					}
			
			}
			else if(keyCombination.equalsIgnoreCase("subscript")) {
				if(browsername.equalsIgnoreCase("ie")){
					rb.keyPress(KeyEvent.VK_CONTROL);
					rb.keyPress(KeyEvent.VK_EQUALS);
					rb.keyRelease(KeyEvent.VK_EQUALS);
					rb.keyRelease(KeyEvent.VK_CONTROL);
					}
					else{
						actions.keyDown(Keys.CONTROL).sendKeys("=").keyUp(Keys.CONTROL).build().perform();
					}

			}
			else if(keyCombination.equalsIgnoreCase("monospace")) {
				if(browsername.equalsIgnoreCase("ie")){
					rb.keyPress(KeyEvent.VK_CONTROL);
					rb.keyPress(KeyEvent.VK_SHIFT);
					rb.keyPress(KeyEvent.VK_CAPS_LOCK);
					rb.keyPress(KeyEvent.VK_M);
					rb.keyRelease(KeyEvent.VK_M);
					rb.keyRelease(KeyEvent.VK_CAPS_LOCK);
					rb.keyRelease(KeyEvent.VK_SHIFT);
					rb.keyRelease(KeyEvent.VK_CONTROL);
					}
					else{
						actions.keyDown(Keys.CONTROL).keyDown(Keys.SHIFT).sendKeys("M").keyUp(Keys.SHIFT).keyUp(Keys.CONTROL).build().perform();
					}
			}
			else if(keyCombination.equalsIgnoreCase("smallcaps")) {
				if(browsername.equalsIgnoreCase("ie")){
					rb.keyPress(KeyEvent.VK_CONTROL);
					rb.keyPress(KeyEvent.VK_SHIFT);
					rb.keyPress(KeyEvent.VK_CAPS_LOCK);
					rb.keyPress(KeyEvent.VK_K);
					rb.keyRelease(KeyEvent.VK_K);
					rb.keyRelease(KeyEvent.VK_CAPS_LOCK);
					rb.keyRelease(KeyEvent.VK_SHIFT);
					rb.keyRelease(KeyEvent.VK_CONTROL);
					}
					else{
						actions.keyDown(Keys.CONTROL).keyDown(Keys.SHIFT).sendKeys("K").keyUp(Keys.SHIFT).keyUp(Keys.CONTROL).build().perform();
					}
			}
			flag = true;	
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return flag;
	}
	
	
	public static boolean throwKeyEvent(Actions actions, Keys keys, String instances) {
		boolean flag = false;
		try {
			for(int i=0;i<Integer.parseInt(instances);i++) {
				actions.sendKeys(keys).build().perform();
			}
			flag = true;
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return flag;
	}
	
	
	public static String captureScreen(WebDriver d, String imagePath) {
		TakesScreenshot oScn = (TakesScreenshot) d;
	    File oScnShot = oScn.getScreenshotAs(OutputType.FILE);
	    File oDest = new File(imagePath);
	    try  {
	    	FileUtils.copyFile(oScnShot, oDest);
	    } 
	    catch (IOException e) {
	    	System.out.println(e.getMessage());
	    }
	    return imagePath;
	}
	
	
	public static void sftpConnect(String hostname, String username, String password, String path) throws Exception {
		boolean flag = false;
		try {
			if(protocol.equalsIgnoreCase("SFTP"))
			{
		    JSch jsch = new JSch();
            session = jsch.getSession(username.trim(), hostname.trim(), 22);
            session.setPassword(password.trim());
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            System.out.println("Host connected.");
            channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(path);
			}
			else if(protocol.equalsIgnoreCase("FTP"))
			{
				ftp.connect(hostname.trim(), 21);
				ftp.login(username.trim(), password.trim());
				ftp.enterLocalPassiveMode();
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
				ftp.changeWorkingDirectory(path);
				files= ftp.listFiles();
			}
			testlog("info", "FTP Connection", "Established", test);
			flag = true;
		}
		catch(Exception e) {
			System.out.println("FTp_Status: Connection Error Trying To Reconnect");
		}
		finally {
			if(!flag) {
				sftpConnect(hostname, username, password, path);
			}
		}
	}
	
	
	public static void breakdownTags(String sourceDirectory, String fileName) {
		try {
			File[] files = (new File(sourceDirectory).listFiles());
			for(File file: files) {
				if(file.getName().equalsIgnoreCase(fileName)) {
					String content = FileUtils.readFileToString(file.getAbsoluteFile(),"UTF-8");
					content = content.replace("><", ">\n<");
					File tempFile = new File(file.getAbsoluteFile().toString());
					FileUtils.writeStringToFile(tempFile, content, "UTF-8");
					testlog("info", "Tag Breakdown", "Completed", test);
				}
			}
		}
		catch(Exception e) {
			testlog("info", "Tags ", "In Required Format", test);
		}
	}
	
	public static boolean checkOutSignal(String values, String hostname, String username, String password, String path, String keyword, String fileType) throws Exception {
		boolean flags = false;
		try {
			if(protocol.equalsIgnoreCase("SFTP"))
			{			
			sftpConnect(hostname, username, password, path);
			String dateTime =getDate("yyyy-MM-dd");
			Vector filelist = channelSftp.ls(path);
			for(int i=0; i<filelist.size();i++){
                LsEntry file = (LsEntry) filelist.get(i);
				if(file.getFilename().contains(dateTime)) {
					if(file.getFilename().contains(keyword)) {
						if(file.getFilename().toUpperCase().endsWith(fileType.toUpperCase())) {
							if(fileType.equalsIgnoreCase("xml")){
								testlog("pass", fileType+" Signal Found", file.getFilename(), test);
								System.out.println("\nSignal Found In FTP : ["+file.getFilename()+"]");
							}else{
								testlog("pass", fileType+" Outpackage Found", file.getFilename(), test);
								System.out.println("\nOutpackage Found In FTP : ["+file.getFilename()+"]");
							}
							flags = true;
							break;
						}
					}
				}
			}
		}
			else if(protocol.equalsIgnoreCase("FTP"))
			{
				sftpConnect(hostname, username, password, path);
				files= ftp.listFiles();
				String dateTime =getDate("yyyy-MM-dd");
				for(FTPFile file: files) {
					if(file.getName().contains(dateTime)) {
						if(file.getName().contains(keyword)) {
							if(file.getName().toUpperCase().endsWith(fileType.toUpperCase())) {
								if(fileType.equalsIgnoreCase("xml")){
									testlog("pass", fileType+" Signal Found", file.getName(), test);
									System.out.println("\nSignal Found In FTP : ["+file.getName()+"]");
								}else{
									testlog("pass", fileType+" Outpackage Found", file.getName(), test);
									System.out.println("\nOutpackage Found In FTP : ["+file.getName()+"]");
								}
								ftp.disconnect();
								flags = true;
								break;
							}
						}
					}
				}
			}
	}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally
		{
			if(protocol.equalsIgnoreCase("SFTP"))
			{
			     channelSftp.exit();
		         System.out.println("sftp Channel exited.");
		         channel.disconnect();
		         System.out.println("Channel disconnected.");
		         session.disconnect();
		         System.out.println("Host Session disconnected.");
			}
		}
		return flags;
	}
	
	public static File deleteAndCreateFolder(File filePath) throws InterruptedException, Exception {
		if(filePath.exists()) {
			FileUtils.deleteDirectory(filePath);
			Thread.sleep(5000);
		}	
		filePath.mkdirs();
		return filePath;
	}
	
	
	public static File createFolder(File filePath) {
		if(!filePath.getParentFile().exists()) {
			filePath.getParentFile().mkdirs();
		}
		return filePath;
	}
	
	
	public static String getcolourname(String rgba) {

		 Getcolour s=new Getcolour();
		 String[] numbers = rgba.replace("rgba(", "").replace(")", "").split(",");
		 int r = Integer.parseInt(numbers[0].trim());
		 int g = Integer.parseInt(numbers[1].trim());
		 int b = Integer.parseInt(numbers[2].trim());
		 String Colourname=s.getColorNameFromRgb(r, g, b);
		 return Colourname;
	}
	
	public static String colourcodetorgba(String rgba) {

		 Color c = new Color(
		            Integer.valueOf(rgba.substring(1, 3), 16), 
		            Integer.valueOf(rgba.substring(3, 5), 16), 
		            Integer.valueOf(rgba.substring(5, 7), 16));

		        StringBuffer sb = new StringBuffer();
		        sb.append("rgba(");
		        sb.append(c.getRed());
		        sb.append(",");
		        sb.append(c.getGreen());
		        sb.append(",");
		        sb.append(c.getBlue());
		        sb.append(")");
		        return sb.toString();
	}

	
	
	public static String createFile(String filePath) {
		try	{
			if(!new File(filePath).getParentFile().exists()) {
				new File(filePath).getParentFile().mkdirs();
				System.out.println("["+filePath+"]"+" Created");
			}
		}
		catch(Exception e) {
			System.out.println("Unable to create File"+e.getMessage());
		}
		return filePath;
	}
	
	public static File createPath(String path) throws Exception{
		File file = new File(path);
		if(!file.exists()){
			file.mkdir();
		}else{
			file.delete();
			Thread.sleep(3000);
			file.mkdir();
		}
		
		return file;
	}
	
	public static File replaceFile(File path, String filename) throws Exception {
		try {
			File[] file = path.listFiles();
			for(File f: file) {
				if(f.isFile() && f.getName().endsWith(filename) && !(f.getName().equalsIgnoreCase("package.xml"))) {
					f.delete();
					filePath = f.getParentFile();
					break;
				}
				else if(f.isDirectory()) {
					replaceFile(f, filename);
				}
			}
			return filePath;
		}
		catch(Exception e) {
			throw new Exception(e);
		}	
	}
	
	
	public static boolean unzipFile(File path) {
		boolean flag = false;
		try {
			File[] zipfiles = path.listFiles();
			for(File zipfile:zipfiles) {
				if(zipfile.getName().endsWith("zip")) {
					net.lingala.zip4j.core.ZipFile zipfileObject = new net.lingala.zip4j.core.ZipFile(path+"/"+zipfile.getName());
					zipfileObject.extractAll(path.toString());
					flag = true;
				}
			}	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	public static String getDate(String format) {
		Date  mydate = new Date();
		String date_time = new SimpleDateFormat(format).format(mydate);
		if(!_elementProperties.getProperty("DATE").trim().equalsIgnoreCase("undefined")) {
			date_time = _elementProperties.getProperty("DATE").trim();
		}
		return date_time;
	}
	
	public static boolean moveFileToFtp(String hostname, String username, String password, String ftpFilePath, String localInput) {
		boolean flag = false;
		try {
			if(protocol.equalsIgnoreCase("SFTP"))
			{			
			sftpConnect(hostname, username, password, ftpFilePath);
    		InputStream inputStream = new FileInputStream(new File(localInput));
			channelSftp.put(inputStream,new File(localInput).getName());
			flag = true;
			}
			else if(protocol.equalsIgnoreCase("FTP"))
			{	
				sftpConnect(hostname, username, password, ftpFilePath);
				InputStream inputStream = new FileInputStream(new File(localInput));
				flag = ftp.storeFile(new File(localInput).getName(), inputStream);	
			}
		}
		catch(Exception e) {
			testlog("error",new File(localInput).getName(), e.getMessage(), test);
		}
		finally
		{
			if(protocol.equalsIgnoreCase("SFTP"))
			{
			     channelSftp.exit();
		         System.out.println("sftp Channel exited.");
		         channel.disconnect();
		         System.out.println("Channel disconnected.");
		         session.disconnect();
		         System.out.println("Host Session disconnected.");
			}
		}
		return flag;
	}
	
	
	public static String readftpFile(String hostname, String username, String password, String ftpFilePath, String localInput, String extension, String regex) {
		String url = "";
		try {
			if(protocol.equalsIgnoreCase("SFTP"))
			{			
			sftpConnect(hostname, username, password, ftpFilePath);
			String dateTime =getDate("yyyy-MM-dd");
			Vector filelist = channelSftp.ls(ftpFilePath);
			for(int i=0; i<filelist.size();i++){
                LsEntry file = (LsEntry) filelist.get(i);
				if(file.getFilename().contains(dateTime)) {
					if(file.getFilename().contains(localInput)&&(file.getFilename().toUpperCase().endsWith(extension.toUpperCase()))) {
						url = readxml(regex, file.getFilename());
					}
				}
			}
		}
			else if(protocol.equalsIgnoreCase("FTP"))
			{
				sftpConnect(hostname, username, password, ftpFilePath);
				String dateTime =getDate("yyyy-MM-dd");
				files = ftp.listFiles();
				for(FTPFile file: files) {
					if(file.getName().contains(dateTime)) {
						if(file.getName().contains(localInput)&&(file.getName().toUpperCase().endsWith(extension.toUpperCase()))) {
							url = readxml(regex, file.getName());
						}
					}
				}	
			}
	}	
		catch(Exception e) {
			testlog("error",new File(localInput).getName(), e.getMessage(), test);
		}
		finally
		{			
			if(protocol.equalsIgnoreCase("SFTP"))
			{
			     channelSftp.exit();
		         System.out.println("sftp Channel exited.");
		         channel.disconnect();
		         System.out.println("Channel disconnected.");
		         session.disconnect();
		         System.out.println("Host Session disconnected.");
			}
		}
		return url;
	}
	
	public static String readSignalFile(String signalvalues, String role, String extension, String regex) {
		String url = "";
		String dateTime =getDate("yyyy-MM-dd");
		String path = serverDirectory+_elementProperties.getProperty("OUTPACKAGEPATH").trim()+"/"+role.toUpperCase()+"/SIGNAL";
		try {
			File modfile = lastmodifiedfile(path,signalvalues,dateTime,extension);	
			url = readlocalxml(regex, modfile);
		}
		catch(Exception e) {
			testlog("error",new File(signalvalues).getName(), e.getMessage(), test);
		}
		return url;
	}
	
	
	public static File lastmodifiedfile(String path,String signalvalues, String datetime, String extension) throws Exception{
		File lastModifiedFile = null;
		try {
			File src = new File(path);
			File[] files = src.listFiles();
			List<File> sameFileList =new ArrayList<File>();
			for(int i=0;i<files.length;i++)
			{
				if(files[i].getName().contains(signalvalues) && files[i].getName().contains(datetime) && files[i].getName().contains(extension))
				{
					sameFileList.add(files[i]);					
				}
			}
			if(sameFileList.size()!=0)
			lastModifiedFile=sameFileList.get(0);
			for(int i=1;i<sameFileList.size();i++)
				{
				if (lastModifiedFile.lastModified() < sameFileList.get(i).lastModified()) 
					lastModifiedFile = sameFileList.get(i);       
				}
			}
		catch (Exception e) {
			e.printStackTrace();
		}
		return lastModifiedFile;
	}
	
	public static boolean downloadSignal(String hostname, String username, String password, String ftpFilePath, String signal, String role, String fileExtension) throws Exception {
		boolean flag = false;
		try {
			sftpConnect(hostname, username, password, ftpFilePath);	
			String date = getDate("yyyy-MM-dd");
			String filePath = role.equalsIgnoreCase("aex")||role.equalsIgnoreCase("pc")?serverDirectory+_elementProperties.getProperty("OUTPACKAGEPATH").trim()+"/"+role.toUpperCase()+"/SIGNAL":(serverDirectory+_elementProperties.getProperty("OUTPACKAGEPATH").trim()+"/"+role.toUpperCase()+"/ZIP");
			signal = signal.contains("aex")||signal.contains("AEX")||signal.contains("Aex")||signal.contains("pc")||signal.contains("PC")||signal.contains("Pc")?signal+"-validation":signal;
			File filepath = createPath(filePath);
			if(protocol.equalsIgnoreCase("FTP"))
			{
				ftp.changeWorkingDirectory(ftpFilePath);
				files =ftp.listFiles();
			}
			if(getFilesfromFTp(signal+"-"+date, fileExtension, "D:/Ftptemp", filepath, ftpFilePath)) {
				flag = true;
			}
		}
		catch(Exception e)
		{
			test.log(LogStatus.ERROR, e.getMessage());
		}
		finally
		{			
			if(protocol.equalsIgnoreCase("SFTP"))
			{
			     channelSftp.exit();
		         System.out.println("sftp Channel exited.");
		         channel.disconnect();
		         System.out.println("Channel disconnected.");
		         session.disconnect();
		         System.out.println("Host Session disconnected.");
			}
		}
		
			return flag;
	}
	
	public static boolean getFilesfromFTp(String signalname, String fileExtension, String localfilepath, File remoteFilePath, String ftpfilepath) {
		boolean flag = false;
		try {
			if(protocol.equalsIgnoreCase("SFTP"))
			{
			Vector filelist = channelSftp.ls(ftpfilepath);
			for(int i=0; i<filelist.size();i++){
                LsEntry filename = (LsEntry) filelist.get(i);
				if(filename.getFilename().contains(signalname)) {
					if(filename.getFilename().toUpperCase().endsWith(fileExtension.toUpperCase())) {
						OutputStream out = new FileOutputStream(Helper.createFolder(new File(localfilepath+"//"+filename.getFilename())));
						channelSftp.get(filename.getFilename(), out);
						Thread.sleep(5000);
						FileUtils.copyFile(new File(localfilepath+"//"+filename.getFilename()), new File(remoteFilePath+"//"+filename.getFilename()));
						System.out.println("Downloaded & Moved To Server : "+"["+ftpfilepath+"//"+filename.getFilename()+"]");
						testlog("pass", "Downloaded & Moved To Server", ftpfilepath+"//"+filename.getFilename(), test);
						if(fileExtension.equalsIgnoreCase("zip")) {
							Thread.sleep(5000);
							flag = unzipFile(remoteFilePath);
						}
						else {
							Thread.sleep(20000);
							channelSftp.rm(ftpfilepath+"//"+filename.getFilename());
							System.out.println("Deleted In FTP : "+"["+ftpfilepath+"//"+filename.getFilename()+"]");
							testlog("pass", ftpfilepath+"//"+filename.getFilename(), " Deleted In FTP", test);
							flag = true;
						}
					}
				}
			}
		}
			else if(protocol.equalsIgnoreCase("FTP"))
			{

				for(FTPFile filename: files) {
					if(filename.getName().contains(signalname)) {
						if(filename.getName().toUpperCase().endsWith(fileExtension.toUpperCase())) {
							OutputStream out = new FileOutputStream(Helper.createFolder(new File(localfilepath+"//"+filename.getName())));
							ftp.retrieveFile(filename.getName(), out);
							Thread.sleep(5000);
							FileUtils.copyFile(new File(localfilepath+"//"+filename.getName()), new File(remoteFilePath+"//"+filename.getName()));
							System.out.println("Downloaded & Moved To Server : "+"["+ftpfilepath+"//"+filename.getName()+"]");
							testlog("pass", "Downloaded & Moved To Server", ftpfilepath+"//"+filename.getName(), test);
							if(fileExtension.equalsIgnoreCase("zip")) {
								Thread.sleep(5000);
								flag = unzipFile(remoteFilePath);
							}
							else {
								Thread.sleep(20000);
								ftp.dele(ftpfilepath+"//"+filename.getName());
								System.out.println("Deleted In FTP : "+"["+ftpfilepath+"//"+filename.getName()+"]");
								testlog("pass", ftpfilepath+"//"+filename.getName(), " Deleted In FTP", test);
								flag = true;
							}
						}
					}
				}
			}
	}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		
		return flag;
	}
	
	public static List<String> waitUntilFileDownload(String path, int maxTime) {
		boolean flag = false;
		try {
			int minTime = 0, filePresent;
			do {
				
				Thread.sleep(10000);
				minTime++;
			}
			while((minTime<maxTime)&&(flag==false));
		}
		catch(Exception e) {
			
		}
		return null;
	}

	
	public static List<String> getFiles(String fileName, String searchDirectory) {	
		List<String> filePathList = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			Date date = new Date();
			File files = new File(searchDirectory);
			File[] filesList = files.listFiles();
			Arrays.sort(filesList, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			for(File file: filesList) {
				if(file.getName().toUpperCase().contains(fileName.toUpperCase())) {
					if(sdf.format(file.lastModified()).contains(sdf.format(date))) {
						filePathList.add(file.getAbsoluteFile().toString());
					}
				}
			}
			if(filePathList.size()>=1) {
				testlog("pass", "Downloaded Files "+fileName, filePathList.size()+" Added", test);
			}
		}
		catch(Exception e)
		{
			test.log(LogStatus.ERROR, e.getMessage());
		}
			return filePathList;
	}
	
	public static List<String> getlastmodifiedfile(String searchDirectory) {	
		List<String> filePathList = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = new Date();
			File files = new File(searchDirectory);
			File[] filesList = files.listFiles();
			Arrays.sort(filesList, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			for(File file: filesList) {
				if(sdf.format(file.lastModified()).contains(sdf.format(date))) {
					filePathList.add(file.getAbsoluteFile().toString());
					}
				}
			if(filePathList.size()>=1) {
				testlog("pass", "Downloaded Files "+searchDirectory, filePathList.size()+" Added", test);
			}
		}
		catch(Exception e)
		{
			test.log(LogStatus.ERROR, e.getMessage());
		}
			return filePathList;
	}
	
	
	public static  boolean moveFiles(String source, String destination) throws InterruptedException, IOException {
		boolean flag = false;
		File sourcePath = new File(source);
		File destinationPath = new File(destination);
		File destinParent = new File(destinationPath.getParent());
		if(!destinParent.exists()) {
			destinParent.mkdir();
		}
		try {
			FileUtils.moveFile(sourcePath, destinationPath);
			flag = true;
		}
		catch(FileExistsException fileException) {
			destinationPath.delete();
			Thread.sleep(5000);
			FileUtils.moveFile(sourcePath, destinationPath);
			flag = true;
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static void printFileContents(File file) throws FileNotFoundException, IOException {
		StringBuffer contents = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader(file.toString()));
		try {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {	
				System.out.println("// " + sCurrentLine);
				contents.append(sCurrentLine);
			}
		}
		finally {
			br.close();
		}
	}
	
	
	public static String getName(File files, String extension) {
		String filename ="";
		File[] fileNames = files.listFiles();
		for(File f: fileNames) {
			if(f.getName().endsWith(extension)) {
				filename = f.getName();
			}	
		}	
		return filename;
	}
	
	public static String EncodeImage(WebDriver d, String imgpath) throws Exception {
		String image = "";
		try {
			File imgfile = new File(captureScreen(d, imgpath));
			FileInputStream imageFile = new FileInputStream(imgpath);
            byte imageData[] = new byte[(int) imgfile.length()];
            imageFile.read(imageData);
            byte[] base64EncodedByteArray = Base64.encodeBase64(imageData);
            image = new String(base64EncodedByteArray);
	    }
		catch(Exception e) {
			e.printStackTrace();
		}
		return "data:image/png;base64,"+image;
	}
	
	public static String getImageData(WebDriver d, String imgpath) throws Exception {
		String image = "";
		try {
			imageFile = new FileInputStream(imgpath);
            byte imageData[] = new byte[(int) new File(imgpath).length()];
            imageFile.read(imageData);
            byte[] base64EncodedByteArray = Base64.encodeBase64(imageData);
            image = new String(base64EncodedByteArray);
	    }
		catch(Exception e) {
			e.printStackTrace();
		}
		return "data:image/png;base64,"+image;
	}
	
	
	public static String readFile(File file) throws FileNotFoundException, IOException {
		StringBuffer contents = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader(file.toString()));
		try {
	    	String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null)  {
				contents.append(sCurrentLine);
			}
		} finally {
			br.close();
		}
		return contents.toString();
	}
	
	
	public static String readServerFile(String regex, String filename) {
		String data = "";
		try	{
			if(protocol.equalsIgnoreCase("SFTP"))
			{
				iStream = channelSftp.get(filename);
			}
			else if(protocol.equalsIgnoreCase("FTP"))
			{
				iStream = ftp.retrieveFileStream(filename);
			}
			Scanner sc = new Scanner(iStream);
			while (sc.hasNextLine()) {
				data = sc.nextLine();
			}
			sc.close();
			iStream.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	
	public static String readxml(String regex, String filename) {
		String value="";
		Scanner sc;
		try	{
			if(protocol.equalsIgnoreCase("SFTP"))
			{
				iStream = channelSftp.get(filename);
			}
			else if(protocol.equalsIgnoreCase("FTP"))
			{
				iStream = ftp.retrieveFileStream(filename);
			}
			sc = new Scanner(iStream);
			while (sc.hasNextLine()) {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(sc.nextLine());
				if(matcher.find()) {
					value = matcher.group(1);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public static String readlocalxml(String regex, File finallist) {
		String value="";
		try	{
			FileInputStream stream = new FileInputStream(finallist);
			Scanner sc = new Scanner(stream);
			while (sc.hasNextLine()) {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(sc.nextLine());
				if(matcher.find()) {
					value = matcher.group(1);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	
	public static void logEvents(WebDriver d, ExtentTest test, String file, String fileurl) throws IOException {
		try {
			if(!d.equals("null")) {	
//				file = createFile(file);
				StringBuilder sb = new StringBuilder();
				LogEntries logEntries = d.manage().logs().get(LogType.BROWSER);
				sb.append("<html><head><title>JAVASCRIPTERROR</title></head><body>");
				for(LogEntry logger: logEntries) {
					sb.append("<BR><p>"+logger.getTimestamp()+"</p>");
					sb.append("<p>"+logger.getMessage()+"</p>");
				}
				sb.append("<BR></body></html>");
				OutputStream out = new FileOutputStream(new File(file));
				Writer writer=new OutputStreamWriter(out);
				writer.write(sb.toString());
				writer.close();
				providehyperlink(fileurl, "Console Log", test);
			}	
		}		
		catch(Exception e) {
			testlog("info", "Non-Browser", "TestCases", test);
		}
	}
	
	public static String alertContents(WebDriver driver, ExtentTest test) throws Exception {
		String content = "";
		try	{
			Alert alert = driver.switchTo().alert();
			content = alert.getText();
		}
		catch(Exception e)	{
			test.log(LogStatus.ERROR, e.getMessage());
			test.log(LogStatus.ERROR, test.addBase64ScreenShot((Helper.EncodeImage(driver, reportScreenshotLocation+"/Alert.jpg"))));
		}
		return content;
	}
	
	
	public static boolean alertHandle(WebDriver driver, ExtentTest test, String option) throws Exception {
		boolean flag = false;
		try	{
			Alert alert = driver.switchTo().alert();
			if(option.equalsIgnoreCase("OK")) {
				alert.accept();
				flag = true;
			}
			else if(option.equalsIgnoreCase("Cancel")) {
				alert.dismiss();
				flag = true;
			}
		}
		catch(Exception e)	{
			test.log(LogStatus.ERROR, e.getMessage());
			test.log(LogStatus.ERROR, test.addBase64ScreenShot((Helper.EncodeImage(driver, reportScreenshotLocation+"/Alert.jpg"))));
		}
		return flag;
	}
	
	
	public static boolean compareTwoStrings(String actual, String expected)	{
		boolean flag = false;
		if(actual.trim().equalsIgnoreCase(expected.trim())) {
			flag = true;
		}
		return flag;
	}
	
	
	public static boolean isFilePresent(String sourceDirectory, String fileName) {
		boolean flag =false;
		try {
			File rootPath = new File(sourceDirectory);
			File[] files = rootPath.listFiles();
			for(File file: files) {
				if(file.getName().equalsIgnoreCase(fileName)) {
					flag = true;
					break;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	public static boolean deleteFile(File file) {
		boolean flag =false;
		try {
			if(file.exists()) {
				file.delete();
				System.out.println(file+" Deleted");
			}
			else {
				System.out.println(file+" Not Found");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	
	public static void logContentMismatch(ExtentTest test, String actuals, String expected)	{
		test.log(LogStatus.FAIL, "Content - Mismatch");
		test.log(LogStatus.WARNING, "<b>Actuals: </b><span style='color:red'>"+actuals+"</span>");
		test.log(LogStatus.WARNING, "<b>Expected: </b><span style='color:green'>"+expected+"</span>");
	}
	
	
	public static WebElement returnVisibleWebElement(String locator, WebDriver driver, ExtentTest test) {
		WebElement element = null;
		try	{
			element = new WebDriverWait(driver, 20).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator))); 
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return element;
	}
	
	public static WebElement returnPresentWebElement(String locator, WebDriver driver, ExtentTest test) {
		WebElement element = null;
		try	{
			element = new WebDriverWait(driver, 20).until(ExpectedConditions.presenceOfElementLocated(By.xpath(locator))); 
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return element;
	}
	
	
	public static String parseJSON(String jsonData, String key, ExtentTest test) throws ParseException	{
		String id = "";
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(jsonData);
			JSONObject myObj = (JSONObject) obj;
			 id = myObj.get(key).toString();
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return id;
	}
	
	
	public static HttpResponse publishJSONWITHPARAMS(HttpEntity multipart, String serviceurl) {
		HttpResponse response = null;
		try	{
			HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(serviceurl);
            httpPost.setEntity(multipart);
            response = httpClient.execute(httpPost);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return response;
	}
	
/*	public static void createZipFile(String outputPath, String srcDir) throws IOException {
	Path p = Files.createFile(Paths.get(outputPath));
	try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
        Path pp = Paths.get(srcDir);
        Files.walk(pp)
          .filter(path -> !Files.isDirectory(path))
          .forEach(path -> {
              ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
              try {
                  zs.putNextEntry(zipEntry);
                  Files.copy(path, zs);
                  zs.closeEntry();
            } catch (IOException e) {
                System.err.println(e);
            }
          });
	}
	}*/
	
	
	public static boolean changeXMLAttribute(File fileDir, String filename, String parentTag, String attribute, String valueToBeChanged) throws FileNotFoundException, Exception {
		boolean flag = false;
		File[] files = fileDir.listFiles();
		try {
		for(File file: files) {
			if(file.getName().equalsIgnoreCase(filename)&&(file.isFile())) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(file);
				doc.getDocumentElement().normalize();
				Node requiredTag = doc.getElementsByTagName(parentTag).item(0);
				if(!attribute.equalsIgnoreCase("textcontent")) {
					NamedNodeMap attributes = (NamedNodeMap) requiredTag.getAttributes();
					Node nodeAttr = (Node) attributes.getNamedItem(attribute);
					nodeAttr.setNodeValue(valueToBeChanged);
				}
				else {
//					requiredTag.setTextContent(valueToBeChanged);
				}
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
	            Transformer transformer = transformerFactory.newTransformer();
	            DOMSource source = new DOMSource(doc);
	            StreamResult result = new StreamResult(file);
	            transformer.transform(source, result);
			}
			else if(file.isDirectory()) {
				changeXMLAttribute(file, filename, parentTag, attribute, valueToBeChanged);
			}
		}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/*public static boolean removeTag(File file, String oldTag, String newTag) throws IOException {
		boolean flag = false;
		try {
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.replaceAll(oldTag, newTag);
				sb.append(line);
			}
			br.close();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(sb.toString());
			bw.close();
			flag=true;
		} catch (Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}*/
	
	public static boolean removeTag(File file, String oldTag, String newTag) throws IOException {
		boolean flag = false;
		try {
			StringBuffer sb = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line = null;

			while ((line = in.readLine()) != null) {
				line = line.replaceAll(oldTag, newTag);
				sb.append(line);
			}
			
			 in.close();
			 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			 out.write(sb.toString());
			 out.close();
			 flag=true;
			 
		} catch (Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean proofPrerequisite(JavascriptExecutor js, ExtentTest test, WebDriver d) {
		boolean flag = false;
		try	{
			js.executeScript("arguments[0].style.display='none'", returnVisibleWebElement(_elementProperties.getProperty("PROOFVIEWMODEDIV").trim(), d, test));
			Thread.sleep(3000);
			js.executeScript("arguments[0].style.top=0", returnVisibleWebElement(_elementProperties.getProperty("PAGERCOMPONENT").trim(), d, test));
			testlog("info", "Screenshot", " Proceeding With Prerequisite Completed", test);
			flag = true;
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static String returnOS(String osname, String version) {
		try	{
			if(osname.equalsIgnoreCase("WIN")) {
				osname = "WIN"+version;
			}
			else if(osname.equalsIgnoreCase("mac")){
				osname = "MAC";
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return osname; 
	}
	
	
	public static void buildReport(String testtype, String customer, String projectName, String releasename, String os, String browser, String browserVersion) throws IOException, InterruptedException {
		StringBuilder builder = new StringBuilder();
		int untested = Runner.testedCases-(Runner.passedCases+Runner.failedCases);
		builder.append("<html>"
				+ "<head><style>table {border-collapse: collapse;}"
				+ "table, td, th {border: 2px solid black;font-weight: bold;}</style></head>"
				+ "<body><center><img src='http://pgc-dev-test.s3.amazonaws.com/salomanat/images/final.png' alt='SalmonAT' height='25%' width='25%'></center>"
				+ "<p>Hi All,<p>"
				+ "<p>Automation "+StringUtils.capitalize(testtype)+" Testing Completed for Prc & Pgc "+StringUtils.capitalize(customer)+" "+releasename+" release in ["+StringUtils.capitalize(os)+"-"+StringUtils.capitalize(browser)+": "+browserVersion+"]</p>"
				+ "<table>"
				+ "<tr>"
				+ "<td><font color='Purple'>TestCases: "+Runner.testedCases+"</font></td><td><font color='Green'>Passed: "+Runner.passedCases+"</font></td><td><font color='Red'>Failed: "+Runner.failedCases+"</font></td><td><font color='Indigo'>Untested: "+untested+"</td>"
				+ "</tr>"
				+ "</table>"
				+ "<p><a href ='http://autotestresult.tnq.co.in:81/Projects/"+projectName+"/"+customer+"/"+testtype+"/"+os+"/"+browser+"/"+browserVersion+"/"+releasename+"/Actuals/"+projectName+"_"+customer+"_"+releasename+"_TestResult.html'>Click here </a>to view the result.</p>"
				+ "<p><b>Note:</b> This is an automated mail. Do not reply to this mail. If you have any queries reply to this mail id "
				+ "<u>karthik.nithianandam@tnqsoftware.co.in</u></p>"
				+ "<p>Thanks &amp; regards,</p>"
				+ "<p>TestLab.</p>"
				+ "<pre>******* Happy Testing *******</pre>"
				+ "</blockquote>"
				+ "<br>"
				+ "</html>");
		File summaryLocation = new File("Summary.html");
		if(summaryLocation.exists()) {
			summaryLocation.delete();
			Thread.sleep(5000);
			summaryLocation.createNewFile();
		}
		OutputStream outputstream = new FileOutputStream(summaryLocation);
		Writer writer = new OutputStreamWriter(outputstream);
		writer.write(builder.toString());
		writer.close();
	}
}
