package xcentral;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

public class ProofCentral  {
	private static final String String = null;
	public static String finallink = null;
	public static Properties p, e;
	public static WebDriver d;
	public static ExtentTest test;
	public static String reportScreenshotLocation; 
	public static String serverdirectory;
	public static String serverurl;
	public static String mountpath;
	public static JavascriptExecutor js;
	public static String url;
	public static String browser;
	public static String clipboardvalue;
	public static File filePath;
	public static org.openqa.selenium.interactions.Actions action;
	public static String globaljid;
	public static String globalaid;
	public static String offlineHeader;
	public static String ElsevierHeader;
	
	public ProofCentral() {
		
	}
	
	public ProofCentral(Properties properties, String reportScreenshotLocation, ExtentTest test, String serverdirectory, String serverurl, Properties e, String mountPath, String browser) {
		this.p = properties;
		this.reportScreenshotLocation = reportScreenshotLocation;
		this.serverdirectory = serverdirectory;
		this.serverurl = serverurl;
		this.test = test;
		this.e = e;
		this.mountpath = mountPath;
		this.browser = browser;
	}
	
	public static void intializeActions() {
		action =  new org.openqa.selenium.interactions.Actions(d);
	}
	
	public static void intializeJavascript() throws Exception {
		js = (JavascriptExecutor) d;
	}
	
	public static boolean clearsession(String urlHeader) throws IOException {
		boolean flag = false;
		try {
			String token[] = url.split("/");
			urlHeader = urlHeader+token[token.length-1];                                    
			URL urlobj = new URL(urlHeader);
			HttpURLConnection con = (HttpURLConnection) urlobj.openConnection();
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			System.out.println("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			if(responseCode==200) {
				flag = true;
			}
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			System.out.println(response.toString());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	public static boolean readcolloboratortoken(String colurlHeader,String Value) throws Exception {
		boolean flag = false;
		try {
			String colltoken=null;
			String token[] = url.split("/");
			colurlHeader = colurlHeader+token[token.length-1];                                    
			URL urlobj = new URL(colurlHeader);
			HttpURLConnection con = (HttpURLConnection) urlobj.openConnection();
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
		    System.out.println("Response Code : " + responseCode);
		    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			if(responseCode==200) {
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}		
			in.close();
			String jsonString=response.toString(); 
			org.json.JSONObject mainJsonObject = new org.json.JSONObject(jsonString);
			org.json.JSONArray dataArray = mainJsonObject.getJSONArray("data");
			
		  for (int i = 0; i < dataArray.length(); i++) {
		        org.json.JSONObject jsonObject = dataArray.getJSONObject(i);
		        colltoken = jsonObject.getString("token");
		        System.out.println(colltoken);
		           }	  
		    finallink =Value+colltoken;
			Helper.testlog("pass", finallink, "URL Found", test);
			System.out.println("Collaborator URL Found : "+finallink);
			flag = true;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new Exception("Colloborator URL Not Found");
		}
		return flag;
	}
	
	
	
	
	public static boolean switchtab(String element, String loadElement) throws InterruptedException, Exception {
		boolean flag = false;
		try {
			if(waituntil(p.getProperty(element.toUpperCase()).trim(), element)) {
				String oldTab = d.getWindowHandle();
				d.findElement(By.xpath(p.getProperty(element.toUpperCase()).trim())).click();
				ArrayList<String> newTab = new ArrayList<String>(d.getWindowHandles());
				newTab.remove(oldTab);
				d.switchTo().window(newTab.get(0));
				d.manage().window().maximize();
				Thread.sleep(7000);
				d.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL, "0"));
				Helper.testlog("Pass", "New Tab", "Switched", test);
				flag = pageload(p.getProperty(loadElement.toUpperCase()).trim(), loadElement);
			}
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		finally {
			if(!flag) {
				flag = refresh(loadElement);
			}
		}
		return flag;
	}
	
	public static boolean deletetoken(String Envir,String jid, String aid) throws Exception {
		boolean flag = false;
		try {
			ArrayList<String> tokenlist=new ArrayList<String>();
			String token;
			String geturl=e.getProperty("GETTOKENSERVICEURL").trim();
			String removeurl=e.getProperty("REMOVETOKENSERVICEURL").trim();
			URL urlobj = new URL(geturl+"/"+Envir+"/"+jid+"/"+aid);		
			HttpURLConnection con = (HttpURLConnection) urlobj.openConnection();
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
		    System.out.println("Response Code : " + responseCode);
		    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			if(responseCode==200) {
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}		
			in.close();
			String jsonString=response.toString();
			System.out.println(jsonString);
			org.json.JSONObject mainJsonObject1 = new org.json.JSONObject(jsonString);
			String s=mainJsonObject1.get("data").toString();
			org.json.JSONObject mainJsonObject = new org.json.JSONObject(s);
			org.json.JSONArray dataArray = mainJsonObject.getJSONArray("records");
			  for (int i = 0; i < dataArray.length(); i++) {
		        org.json.JSONObject jsonObject = dataArray.getJSONObject(i);
		        token = jsonObject.getString("token");
		        System.out.println(token);
		        tokenlist.add(token);
		           }	
			  if(tokenlist.size()>=1)
			  {
			for(String tokenid:tokenlist)
			{
			   String json = "{\"db\":\""+Envir+"\",\"list\":[{\"pid\":\""+jid+"\",\"aid\":\""+aid+"\",\"token\":\""+tokenid+"\"}]}";
		       URL url = new URL(removeurl);
	           HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	           conn.setConnectTimeout(5000);
	           conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	           conn.setDoOutput(true);
	           conn.setDoInput(true);
	           conn.setRequestMethod("POST");
	           OutputStream os = conn.getOutputStream();
	           os.write(json.getBytes("UTF-8"));
	           os.close();
	           InputStream inputjson = new BufferedInputStream(conn.getInputStream());
	           String result = org.apache.commons.io.IOUtils.toString(inputjson, "UTF-8");
	           org.json.JSONObject jsonObject = new org.json.JSONObject(result);
	           inputjson.close();
	           conn.disconnect();
	           con.disconnect();
	           Helper.testlog("pass", tokenid, "Token Deleted", test);
			}
			  }
			  else
			  {
				  con.disconnect();
				  Helper.testlog("pass", "Empty", "No Token Found", test);  
			  }
			}
			else
			{
				con.disconnect();
			}
			flag=true;
		}
		catch(Exception e) {
			flag=false;
			Helper.testlog("fail", "Token", "Not Deleted", test);
		}
		return flag;
	}
	
	
	public static boolean pageload(String element, String label) throws Exception {
		boolean flag = false;
		try {
			(new WebDriverWait(d, Integer.parseInt(p.getProperty("PAGELOADTIME").trim()))).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(element)));
			Helper.testlog("Pass", label, "Visibility : True", test);
			Thread.sleep(7000);
			flag = true;
		}
		catch(Exception e) {
			test.log(LogStatus.INFO, test.addBase64ScreenShot(Helper.EncodeImage(d, reportScreenshotLocation+"/LoadImage.jpg")));
			Helper.testlog("fail", label, "Visibility : False", test);
		}
		return flag;
	}
	
	public static boolean openurl(String customUrl, String element) throws Exception {
		boolean flag = false;
		String finalurl = null;
		try {
			d = Controller.browser(browser);
			if(customUrl.equalsIgnoreCase("default")){
				finalurl = url;
			}else if(customUrl.contains("http")){
				finalurl = customUrl;	
			}else if(customUrl.contains("offline")){
				System.out.println(url);
				String token[] = url.split("/");
				offlineHeader=p.getProperty(customUrl.toUpperCase()).trim();
				System.out.println(offlineHeader);
			    offlineHeader = offlineHeader+token[token.length-1];	
			    System.out.println(offlineHeader);
			    finalurl = offlineHeader;
			}
			else if(customUrl.contains("AU")||customUrl.contains("ED")||customUrl.contains("MC")||customUrl.contains("JM")){
		     	String link="Elsevierlink";
				System.out.println(url);
				String token[] = url.split("/");
				ElsevierHeader=p.getProperty(link.toUpperCase()).trim();
				System.out.println(ElsevierHeader);
				ElsevierHeader = ElsevierHeader+token[token.length-1]+"&type="+customUrl;	
			    System.out.println(ElsevierHeader);
			    finalurl = ElsevierHeader;
			}else {
				finalurl = finallink;
			}
			d.get(finalurl);
			if(pageload(p.getProperty(element.toUpperCase()).trim(), element)) {
				Helper.testlog("pass", finalurl ,"Loaded", test);
				flag  = true;
			}	
		}
		catch(NoSuchElementException e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}	
	
	public static boolean waituntil(String element, String label) throws Exception {
		boolean flag = false;
		try {
			(new WebDriverWait(d, 20)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(element)));
			Helper.testlog("Pass", label, "Visibility : True", test);
			flag = true;
		}
		catch(Exception e) {
			Helper.testlog("fail", label, "Visibility : False", test);
			test.log(LogStatus.INFO, test.addBase64ScreenShot(Helper.EncodeImage(d, reportScreenshotLocation+"/"+label+".jpg")));
		}
		return flag;
	}
	
	public static boolean waitfor(String element, String label) throws Exception {
		boolean response = false;
		try {
			if(element.equalsIgnoreCase("alert")) {
				(new WebDriverWait(d, 20)).ignoring(NoAlertPresentException.class).until(ExpectedConditions.alertIsPresent());
				response = true;
			}
			else {
				(new WebDriverWait(d, 20)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(element)));
				response = true;
			}	
		} 
		catch (Exception ex) {
			Helper.testlog("fail", label ,"Not Found", test);
		}
		return response;
	}
	
	public static boolean verifyelement(String element, String inseconds) throws Exception {	 
		boolean flag =false;
		try {
			new WebDriverWait(d, Integer.parseInt(inseconds)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(element.trim())));	
			Helper.testlog("pass", element ,"Display : Pass", test);
			flag = true;
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
			test.log(LogStatus.INFO, test.addBase64ScreenShot(Helper.EncodeImage(d, reportScreenshotLocation+"/"+element+".jpg")));
		}
		return flag;
	}
	
	public static boolean mousehover(String element, String Label) throws Exception {	 
		boolean flag =false;
		try {
			Actions hover = new Actions(d);
			WebElement Elem_to_hover = d.findElement(By.xpath(element));
			hover.moveToElement(Elem_to_hover).build().perform();
			wait("3");
			 String actualTooltip = Elem_to_hover.getAttribute("title");
		        if(actualTooltip.equalsIgnoreCase(Label)) {							
		        	Helper.testlog("pass", Label ,"Display : Pass", test);	
		        	flag = true;
		        }	
		        else
		        {
		        	Helper.testlog("fail", Label ,"Not Displayed", test);	
		        }
			
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
			test.log(LogStatus.INFO, test.addBase64ScreenShot(Helper.EncodeImage(d, reportScreenshotLocation+"/"+Label+".jpg")));
		}
		return flag;
	}
	
	public static boolean verifycolour(String element, String css, String colourcode) throws Exception {	 
		boolean flag =false;
		try {
			 WebElement Ele_Colour = d.findElement(By.xpath(element));
			 String actualcolourcode = Ele_Colour.getCssValue(css);	
			 System.out.println(actualcolourcode);
			 String actualcolour=Helper.getcolourname(actualcolourcode);
			 String rgba=Helper.colourcodetorgba(colourcode);
			 String expectedcolour=Helper.getcolourname(rgba);	 
			 String hex = Color.fromString(actualcolourcode).asHex();
			 System.out.println(hex);

			 
			 if(hex.equalsIgnoreCase(colourcode)) {	
				    Helper.testlog("info", expectedcolour ,"Expected Colour", test);
		        	Helper.testlog("pass", actualcolour ," Colour Display : Pass", test);	
		        	flag = true;
		        }	
		        else
		        {
		        	Helper.testlog("info", expectedcolour ,"Expected Colour", test);
		        	Helper.testlog("fail", actualcolour ," Colour Display : Fail", test);	
		        }
		       		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
			test.log(LogStatus.INFO, test.addBase64ScreenShot(Helper.EncodeImage(d, reportScreenshotLocation+"/"+colourcode+".jpg")));
		}
		return flag;
	}
		
	public static boolean seeelement(String element, String label) throws Exception {
		boolean flag =false;
		if(waitfor(element, label)) {
			Helper.testlog("pass", label ,"Displayed", test);
			flag = true;
		}
		else {
			test.log(LogStatus.INFO, test.addBase64ScreenShot(Helper.EncodeImage(d, reportScreenshotLocation+"/"+label+".jpg")));
		}
		return flag;
	}
	
	protected boolean getattributefromelement(WebElement element, String expectedValue) {
		boolean flag =false;
		
		String actualValue = element.getText();
		if(expectedValue.equalsIgnoreCase(actualValue)) {
			flag = true;
		}
		else {
//			test.log(LogStatus.INFO, test.addBase64ScreenShot(Helper.EncodeImage(d, reportScreenshotLocation+"/"+label+".jpg")));
		}
		return flag;

	}
	
	
	public static boolean iselementenabled(String element, String label) throws Exception {
		boolean flag =false;
		try {			
			System.out.println(1);
			WebElement ele= d.findElement(By.xpath(element));  		
			System.out.println(2);
			WebDriverWait wait = new WebDriverWait(d, 60);
                   wait.until(ExpectedConditions.visibilityOf(ele));
                   System.out.println(3);
                 flag = ele.isEnabled();
                 System.out.println(4);
                 return flag;
                 
		} catch (Exception e) {
			Helper.testlog("fail", label ,"Hide", test);
			test.log(LogStatus.INFO, test.addBase64ScreenShot(Helper.EncodeImage(d, reportScreenshotLocation+"/"+label+".jpg")));
		}		
		return flag;
	}
	
	
	public static boolean dontseeelement(String elementPath, String label) throws Exception {
		boolean flag = false;
		try {
			new WebDriverWait(d, 20 ).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(elementPath)));
			Helper.testlog("pass", label ,"Hide", test);
			flag = true;
		}
		catch(Exception e) {
			Helper.testlog("fail", label ,"Hide", test);
			test.log(LogStatus.INFO, test.addBase64ScreenShot(Helper.EncodeImage(d, reportScreenshotLocation+"/"+label+".jpg")));
		}
		return flag;
	}
	
	
	public static boolean refresh(String elementPath) throws Exception, InterruptedException {
		boolean flag = false;
		try {
			d.navigate().refresh();
			
		}
		catch(NoAlertPresentException e) {
			flag = pageload(elementPath.trim(), elementPath);
		}
		finally {
			if(!flag) {
				d.navigate().refresh();
				flag = pageload(elementPath.trim(), elementPath);
			}
		}
		return flag;
	}
	
	
	public static boolean backward(String elementPath) throws Exception, InterruptedException {
		boolean flag = false;
		try {
			d.navigate().back();			
		}
		catch(NoAlertPresentException e) {
			flag = pageload(p.getProperty(elementPath.toUpperCase()).trim(), elementPath);
		}
		finally {
			if(!flag) {
				d.navigate().back();	
				flag = pageload(p.getProperty(elementPath.toUpperCase()).trim(), elementPath);
			}
		}
		return flag;
	}
	
	public static boolean forward(String elementPath) throws Exception, InterruptedException {
		boolean flag = false;
		try {
			d.navigate().forward();		
		}
		catch(NoAlertPresentException e) {
			flag = pageload(p.getProperty(elementPath.toUpperCase()).trim(), elementPath);
		}
		finally {
			if(!flag) {
				d.navigate().forward();	
				flag = pageload(p.getProperty(elementPath.toUpperCase()).trim(), elementPath);
			}
		}
		return flag;
	}
	
	
	
	
	public static boolean elementshot(String elementname, String path) throws Exception {
		intializeJavascript();
		boolean flag = false;
		try {
			if(waituntil(elementname, FilenameUtils.getBaseName(new File(path).getName()))) {
				WebElement element = d.findElement(By.xpath(elementname));
				File screenshot = ((TakesScreenshot)d).getScreenshotAs(OutputType.FILE);
				BufferedImage  fullImg = ImageIO.read(screenshot);
				Point point = element.getLocation();
				int eleWidth = element.getSize().getWidth();
				int eleHeight = element.getSize().getHeight();
				BufferedImage eleScreenshot= fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
				ImageIO.write(eleScreenshot, "png", screenshot);
				File screenshotLocation = new File(serverdirectory+p.getProperty("OUTPACKAGEPATH").trim()+path);
				FileUtils.copyFile(screenshot, screenshotLocation);
				Helper.testlog("Pass", FilenameUtils.getBaseName(new File(path).getName()), "ScreenShot", test);
				Helper.providehyperlink(serverurl+p.getProperty("OUTPACKAGEPATH").trim()+path, FilenameUtils.getBaseName(new File(path).getName()), test);
				flag = true;
			}	
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean wait(String inSeconds) {
		boolean flag = false;
		try {
			Thread.sleep(Integer.parseInt(inSeconds+"000"));
			flag = true;
		}
		catch(Exception e) {
			test.log(LogStatus.WARNING, inSeconds+" Interuppted Exception");
		}
		return flag;
	}
	
	
	public static boolean scrollintoview(String elementPath) throws Exception {
		intializeJavascript();
		boolean flag = false;
		try {
			WebElement element = d.findElement(By.xpath(elementPath.trim()));
			js.executeScript("arguments[0].scrollIntoView();", element);
			flag = true;
		}
		catch(Exception e) {
			e.printStackTrace();
			test.log(LogStatus.ERROR, test.addBase64ScreenShot((Helper.EncodeImage(d, reportScreenshotLocation+"/Scroll.jpg"))));
		}
		return flag;
	}
	
	public static boolean draganddrop(String fromelementPath,String toelementPath) throws Exception {
		boolean flag = false;
		intializeJavascript();
		try {
			WebElement From=d.findElement(By.xpath(fromelementPath));
			System.out.println(From); 
	         WebElement To=d.findElement(By.xpath(toelementPath));			
	         System.out.println(To);
	         int xto=(To.getLocation().x);
	         int yto=(To.getLocation().y);
	         System.out.println(xto);
	         System.out.println(yto);
	         Actions act= new Actions(d);
	   //      act.clickAndHold(From).moveToElement(To, xto, yto).release().build().perform();
	         js.executeScript("function simulate(f,c,d,e){var b,a=null;for(b in eventMatchers)if(eventMatchers[b].test(c)){a=b;break}if(!a)return!1;document.createEvent?(b=document.createEvent(a),a==\"HTMLEvents\"?b.initEvent(c,!0,!0):b.initMouseEvent(c,!0,!0,document.defaultView,0,d,e,d,e,!1,!1,!1,!1,0,null),f.dispatchEvent(b)):(a=document.createEventObject(),a.detail=0,a.screenX=d,a.screenY=e,a.clientX=d,a.clientY=e,a.ctrlKey=!1,a.altKey=!1,a.shiftKey=!1,a.metaKey=!1,a.button=1,f.fireEvent(\"on\"+c,a));return!0} var eventMatchers={HTMLEvents:/^(?:load|unload|abort|error|select|change|submit|reset|focus|blur|resize|scroll)$/,MouseEvents:/^(?:click|dblclick|mouse(?:down|up|over|move|out))$/}; " +
	         "simulate(arguments[0],\"mousedown\",0,0); simulate(arguments[0],\"mousemove\",arguments[1],arguments[2]); simulate(arguments[0],\"mouseup\",arguments[1],arguments[2]); ",
	         From,xto,yto);
	         Helper.testlog("pass", fromelementPath ,"Dragged and Dropped", test);
			flag = true;
		}
		catch(Exception e) {
			e.printStackTrace();
			test.log(LogStatus.ERROR, test.addBase64ScreenShot((Helper.EncodeImage(d, reportScreenshotLocation+"/Scroll.jpg"))));
		}
		return flag;
	}
	
	
	public static boolean clickelement(String menu) throws Exception {				
		boolean flag = false;
		if(menu.equalsIgnoreCase("annotatesave")) {
			scrollintoview(p.getProperty(menu.toUpperCase()).trim());
		}
		flag = click(p.getProperty(menu.toUpperCase()), menu);
		return flag;	
	}
	

	
	public static boolean click(String elementPath, String label) throws Exception {
		boolean flag =false;
		try {
			if(waituntil(elementPath, label)) {
				d.findElement(By.xpath(elementPath)).click();
				Helper.testlog("pass", label ,"Click : Pass", test);
				flag = true;
			}	
		}
		catch (Exception e) {
			test.log(LogStatus.INFO, test.addBase64ScreenShot(Helper.EncodeImage(d, reportScreenshotLocation+"/"+label+".jpg")));
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean iselementnotpresent(String elementPath, String label) throws Exception {
		boolean flag =false;
		try {
			boolean elementNotDisplayed = d.findElement(By.xpath(elementPath)).isDisplayed();
			if (elementNotDisplayed == false) {
				test.log(LogStatus.INFO, test.addBase64ScreenShot(Helper.EncodeImage(d, reportScreenshotLocation+"/"+label+".jpg")));
			flag = false;		
		}
		}
		catch (Exception e) {	
			Helper.testlog("pass", label ,"Click : Pass", test);			
			flag = true;
			}	
			
		return flag;
	}
	
	
	public static boolean draganddropfromlocal(String file, WebElement target, int offsetX, int offsetY) throws Exception {
		boolean flag =false;
		try {
			
			File filePath = new File(file); 
			
			if(!filePath.exists())
		        throw new WebDriverException("File not found: " + filePath.toString());

		    WebDriver driver = ((RemoteWebElement)target).getWrappedDriver();
		    JavascriptExecutor jse = (JavascriptExecutor)driver;
		    WebDriverWait wait = new WebDriverWait(driver, 30);

		    String JS_DROP_FILE =
		        "var target = arguments[0]," +
		        "    offsetX = arguments[1]," +
		        "    offsetY = arguments[2]," +
		        "    document = target.ownerDocument || document," +
		        "    window = document.defaultView || window;" +
		        "" +
		        "var input = document.createElement('INPUT');" +
		        "input.type = 'file';" +
		        "input.style.display = 'none';" +
		        "input.onchange = function () {" +
		        "  var rect = target.getBoundingClientRect()," +
		        "      x = rect.left + (offsetX || (rect.width >> 1))," +
		        "      y = rect.top + (offsetY || (rect.height >> 1))," +
		        "      dataTransfer = { files: this.files };" +
		        "" +
		        "  ['dragenter', 'dragover', 'drop'].forEach(function (name) {" +
		        "    var evt = document.createEvent('MouseEvent');" +
		        "    evt.initMouseEvent(name, !0, !0, window, 0, 0, 0, x, y, !1, !1, !1, !1, 0, null);" +
		        "    evt.dataTransfer = dataTransfer;" +
		        "    target.dispatchEvent(evt);" +
		        "  });" +
		        "" +
		        "  setTimeout(function () { document.body.removeChild(input); }, 25);" +
		        "};" +
		        "document.body.appendChild(input);" +
		        "return input;";

		    WebElement input =  (WebElement)jse.executeScript(JS_DROP_FILE, target, offsetX, offsetY);
		    input.sendKeys(filePath.getAbsoluteFile().toString());
		    wait.until(ExpectedConditions.stalenessOf(input));
//				Helper.testlog("pass", label ,"Click : Pass", test);
				flag = true;
		}
		catch (Exception e) {
//			test.log(LogStatus.INFO, test.addBase64ScreenShot(Helper.EncodeImage(d, reportScreenshotLocation+"/"+label+".jpg")));
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
//	public static boolean mouseHoverValidation(String label) throws Exception {
//		boolean flag =false;
//		try {
//			//Identify the element on the hover of which you want to         verify the pointer typeWebElement e = driver.findElement("Element Locator"));
//			System.out.println("Cursor before hovering on: " + e.getCssValue("cursor"));
//			System.out.println("Hovering on the element...");
//			//Hover the mouse over that elementActions builder = new Actions(driver);builder.moveToElement(e);builder.build().perform();
//			//Check that the cusrsor does not change to pointerString cursorTypeAfter = e.getCssValue("cursor");System.out.println("Cursor after hovering on: " + cursorTypeAfter);
//			//Verify that the cursor type has not changed to 'pointer'Assert.assertFalse(cursorTypeAfter.equalsIgnoreCase("pointer"), "Cursor type changed !");}
//			
//		}
//		catch (Exception e) {
//			test.log(LogStatus.INFO, test.addBase64ScreenShot(Helper.EncodeImage(d, reportScreenshotLocation+"/"+label+".jpg")));
//			test.log(LogStatus.ERROR, e.getMessage());
//		}
//		return flag;
//	}
	
	
	public static boolean typetext(String elementPath, String text) {
		boolean flag = false;
		try {
			String element = elementPath.contains("/")? elementPath : p.getProperty(elementPath.toUpperCase()).trim();
			String label = elementPath.contains("/")? "TextEditor" : elementPath;
			if(waituntil(element, label)) {
				d.findElement(By.xpath(element)).clear();
				d.findElement(By.xpath(element)).sendKeys(text);
				Helper.testlog("pass", text ," Type : Pass", test);
				flag = true;
			}
		}	
		catch(Exception e) {	
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean selectfromdropdown(String elementPath, String value) {
		boolean flag = false;
		try {
			int index =0;
			if(waitfor(elementPath, value)) {
				Select select = new Select(d.findElement(By.xpath(elementPath)));
				for(WebElement element :select.getOptions()) {
					if(element.getText().equalsIgnoreCase(value)){
						break;
					}
					index++;
				}
					select.selectByIndex(index);
					flag = true;
			}
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	
	public static boolean seetext(String element, String expected) {
		boolean flag = false;
		String actuals ="";
		try {
			if(waitfor(element, "Text With ["+expected+"]")) {
				actuals = d.findElement(By.xpath(element)).getText();
				flag = Helper.compareTwoStrings(actuals, expected);
				if(flag) {
					Helper.testlog("pass", expected ,"Verification - Done", test);
				}
				else {
					Helper.logContentMismatch(test, actuals, expected);
				}
			}	
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR,  e.getMessage());
		}
		return flag;
	}
	
	
	public static boolean alerttextverify(String expected, String label) {
		boolean flag = false;
		try {
			if(waitfor("alert", label)) {
				String actuals = Helper.alertContents(d, test); 
				flag = Helper.compareTwoStrings(actuals, expected);
				if (flag) {
					 Helper.testlog("Pass", actuals, "Verification - Done", test);
				}	 
				else {
					 Helper.logContentMismatch(test, actuals, expected);
				}
			}
		}	
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean handlealertonclick(String option, String label) throws Exception {
		boolean flag = false;
		try {
			if(waitfor("alert", label)) {
				flag = Helper.alertHandle(d, test, option);
			}
		}	
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	
	public static boolean seefile(String sourceDirectory, String fileName) {
		boolean flag =false;
		try {
			flag = Helper.isFilePresent(sourceDirectory, fileName); 
			if(flag) {	
				Helper.testlog("pass", new File(fileName).getName() ," File Present", test);
				flag = true;
			}
			else {
				Helper.testlog("fail", new File(fileName).getName(), " File Present", test);
			}
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	
	public static boolean browser(String toDo) {
		boolean flag = false;
		try {
			if(toDo.equalsIgnoreCase("close")) {
				d.quit();
				Helper.testlog("Pass", "_Instance", "Closed : Pass", test);
				flag = true;
			}	
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean upload(String element, String filePath) throws Exception {
		boolean flag = false;
		try	{
			System.out.println(1);
			if(clickelement(element)) {
				System.out.println(2);
				Thread.sleep(5000);
				System.out.println(3);
		String pwd = System.getProperty("user.dir");
		System.out.println(4);

				String FocusText = null;
				String file=pwd+filePath;
				System.out.println(file);
				if(browser.equalsIgnoreCase("chrome")){
					FocusText="Open";
				}
				else if(browser.equalsIgnoreCase("firefox"))
				{
					FocusText="File Upload";
				}
				else if(browser.equalsIgnoreCase("ie"))
				{
					FocusText="Choose File to Upload";
				}
				Process process = new ProcessBuilder("./Assets/FileUpload.exe",file,FocusText).start();
				Helper.testlog("Pass", new File(filePath).getName(), "Uploaded", test);
				flag = true;
			}
		}
		catch(Exception e)	{
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean multiplefileupload(String element, String filePath) throws Exception {boolean flag = false;
	String file = null;
	String pwd = System.getProperty("user.dir");
	String FocusText = null;
	StringBuilder fileBuilder = new StringBuilder();
	try	{
		if(clickelement(element)) {
			Thread.sleep(5000);
	
//			List<String> filePaths = Arrays.asList(filePath.split(","));
//			for(String singleFile : filePaths) {
//			file=pwd+singleFile.trim();
//			fileBuilder.append("\""+file+"\"");
////			System.out.println(file.toString());
//			System.out.println("File is " + file.toString());
//			
			}
			
			file = fileBuilder.toString();
			if(filePath.contains(","))
			{
				String[] s=filePath.split(",");
				for(int i=0;i<s.length;i++)
				{		
									
					fileBuilder.append("\""+pwd+s[i]+"\"");
					
					if(i<(s.length-1))
					{
						fileBuilder.append("\"\"");
					}
										
				}
				System.out.println(fileBuilder.toString());
			}
			
			
			file="\"\""+fileBuilder.toString();
			file=file+"\"\"";
			System.out.println(file);
			
	
			if(browser.equalsIgnoreCase("chrome")){
				FocusText="Open";
			}
			else if(browser.equalsIgnoreCase("firefox"))
			{
				FocusText="File Upload";
			}
			else if(browser.equalsIgnoreCase("ie"))
			{
				FocusText="Choose File to Upload";
			}
			Process process = new ProcessBuilder("./Assets/FileUpload.exe",file,FocusText).start();
//			Process process = new ProcessBuilder("./Assets/FileUpload.exe","\"D:\\01MyWorkspace\\SR_Central_Regression_Suite\\SR_Central_Regression_New\\SR-Central-Regression-Suite\\Assets\\Manuscript.docx\"\"D:\\01MyWorkspace\\SR_Central_Regression_Suite\\SR_Central_Regression_New\\SR-Central-Regression-Suite\\Assets\\Manuscript1.docx\"",FocusText).start();
			System.out.println("File is " + file.toString());
			Helper.testlog("Pass", new File(filePath).getName(), "Uploaded", test);
			flag = true;
		}
	
	catch(Exception e)	{
		test.log(LogStatus.ERROR, e.getMessage());
	}
	return flag;
}
	
	
	
	public static boolean selection(String path, String start, String end) throws Exception {
		boolean flag = false;
		intializeJavascript();
		path = "\""+path+"\"";
		try	{
			js.executeScript("function getAllTextNodes(node)"
				+ "{"
				+ 	"var len = node.childNodes.length,i = 0, walker, textNodes = [];"
				+ 	"walker = document.createTreeWalker(node, window.NodeFilter.SHOW_ALL, null, false);"
				+ 	"currentNode = walker.nextNode();"
				+ 	"while (currentNode !== null)"
				+ 	"{"
				+ 		"if (currentNode.nodeType === window.Node.TEXT_NODE)"
				+		"{"
				+ 			"textNodes.push(currentNode);"
			    + 		"}"
				+ 			"currentNode = walker.nextNode();"
				+ 	"}"
				+ 	"return textNodes;"
				+ "}"
				+ "var selection = window.getSelection();"
				+ "var element = getAllTextNodes(document.evaluate("+path+",document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue);" 
				+ "node = document.evaluate("+path+",document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"
				+ "node.scrollIntoView(true);"
				+ "range = document.createRange();"
				+ "var z=0, c=0, value1 ="+start+", value2="+end+";"
				+ "for(var x=0;x<element.length;x++)"
				+ "{"
				+ 	"var y = element[x].length;"
				+ 	"var n= y+z;"
				+ 	"if(n>=value1)"
				+ 	"{"
				+ 		"childStart = element[x];"
				+ 		"offsetX = value1-z;"
				+ 		"break;"
				+ 	"}"
				+   "else"
				+ 	"{"
				+ 		"z = y+z;"
				+ 	"}"
				+ "}"
				+ "for(var r=0;r<element.length;r++)"
				+ "{"
				+ 	"var d = element[r].length;"
				+ 	"var t = c+d;"
				+ 	"if(t>=value2)"
				+ 	"{"
				+ 		"childEnd = element[r];"
				+ 		"offsetY = value2-c;"
				+ 		"break;"
				+ 	"}"
				+ 	"else"
				+ 	"{"
				+ 		"c= c+d;"
				+ 	"}"
				+ "}"
				+ 	"range.setStart(childStart, offsetX);"
				+ 	"range.setEnd(childEnd, offsetY);"
				+	"selection.removeAllRanges();"
				+ 	"selection.addRange(range);"
				+ 	"position=node.getBoundingClientRect();"
				+ 	"sel = window.getSelection().getRangeAt(0).getBoundingClientRect();"				
				+ 	"oEvent = document.createEvent('MouseEvents');"
				+ 	"oEvent.initMouseEvent('mouseup', true, true, document.defaultView, 0, 0, 0, sel.left, sel.top, false, false, false, false, 0, node);"
				+ 	"node.dispatchEvent(oEvent);");
				flag = true;
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean clickimageannotate(String elementPath) throws Exception {
		intializeJavascript();
		intializeActions();
		boolean flag = false;
		try {
			String node = "\""+elementPath+"//*[@title='Annotate']"+"\"";
			js.executeScript("var image = document.evaluate("+node+", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"
			+ "image.scrollIntoView();"
			+ "image.click();");
			flag = true;
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean clickcanvas(String xcordinate, String ycoordinate) throws Exception {
		intializeJavascript();
		intializeActions();
		boolean flag = false;
		try {
			String node = "\""+"//*[@class='image-annotator__canvas']"+"\"";
			js.executeScript("var image = document.evaluate("+node+", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"
			+ "var oEvent = document.createEvent('MouseEvents');"
			+ "oEvent.initMouseEvent('mousedown', true, true, document.defaultView, 0, 0, 0, "+Integer.parseInt(xcordinate)+", "+Integer.parseInt(ycoordinate)+", false, false, false, false, 0, null);"
			+ "image.dispatchEvent(oEvent);");
			flag = true;
			Helper.testlog("Pass", xcordinate+":"+ycoordinate, "Canvas Clicked", test);
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
    public static boolean switchtoframe(String elementPath,  String label) throws Exception {
		
		boolean flag =false;
		try {
			WebElement element = d.findElement(By.xpath(elementPath));
			d.switchTo().frame(element);
			Helper.testlog("pass", label ,"Click : Pass", test);			
			flag = true;
		}
		catch (Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}
	
	public static boolean insertcursor(String path, String pos) throws Exception {
		boolean flag = false;
		intializeJavascript();
		path = "\"" + path + "\"";
		try	{
			js.executeScript("function getAllTextNodes(node)"
				+ "{"
				+ 	"var len = node.childNodes.length,i = 0, walker, textNodes = [];"
				+ 	"walker = document.createTreeWalker(node, window.NodeFilter.SHOW_ALL, null, false);"
				+ 	"currentNode = walker.nextNode();"
				+ 	"while (currentNode !== null)"
				+ 	"{"
				+ 		"if (currentNode.nodeType === window.Node.TEXT_NODE)"
				+		"{"
				+ 			"textNodes.push(currentNode);"
			    + 		"}"
				+ 			"currentNode = walker.nextNode();"
				+ 	"}"
				+ 			"return textNodes;"
				+ "}"
				+ "var selection = window.getSelection();"
				+ "var element = getAllTextNodes(document.evaluate("+path+",document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue);" 
				+ "node = document.evaluate("+path+",document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"
				+ "node.scrollIntoView(true);"
				+ "range = document.createRange();"
				+ "var z=0, c=0, value1 ="+pos+";"
				+ "for(var x=0;x<element.length;x++)"
				+ "{"
				+ 	"var y = element[x].length;"
				+ 	"var n= y+z;"
				+ 	"if(n>=value1)"
				+ 	"{"
				+ 		"childStart = element[x];"
				+ 		"offsetX = value1-z;"
				+ 		"break;"
				+ 	"}"
				+   "else"
				+ 	"{"
				+ 	 	"z = y+z;"
				+ 	"}"
				+ "}"
				+ 	"range.setStart(childStart, offsetX);"
				+ 	"range.setEnd(childStart, offsetX);"
				+	"selection.removeAllRanges();"
				+ 	"selection.addRange(range);"
				+ 	"position=node.getBoundingClientRect();"
				+	"sel = window.getSelection().getRangeAt(0).getBoundingClientRect();"
			    + 	"oEvent = document.createEvent('MouseEvents');"
				+ 	"oEvent.initMouseEvent('mouseup', true, true, document.defaultView, 0, 0, 0, sel.left, sel.top, false, false, false, false, 0, node);"
				+ 	"node.dispatchEvent(oEvent);");
				flag = true;
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean citationinsertcursor(String path, String pos) throws Exception {
		boolean flag = false;
		intializeJavascript();
		path = "\"" + path + "\"";
		try	{
			js.executeScript("function getAllTextNodes(node)"
				+ "{"
				+ 	"var len = node.childNodes.length,i = 0, walker, textNodes = [];"
				+ 	"walker = document.createTreeWalker(node, window.NodeFilter.SHOW_ALL, null, false);"
				+ 	"currentNode = walker.nextNode();"
				+ 	"while (currentNode !== null)"
				+ 	"{"
				+ 		"if (currentNode.nodeType === window.Node.TEXT_NODE)"
				+		"{"
				+ 			"textNodes.push(currentNode);"
			    + 		"}"
				+ 			"currentNode = walker.nextNode();"
				+ 	"}"
				+ 			"return textNodes;"
				+ "}"
				+ "var selection = window.getSelection();"
				+ "var element = getAllTextNodes(document.evaluate("+path+",document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue);" 
				+ "node = document.evaluate("+path+",document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"
				+ "node.scrollIntoView(true);"
				+ "range = document.createRange();"
				+ "var z=0, c=0, value1 ="+pos+";"
				+ "for(var x=0;x<element.length;x++)"
				+ "{"
				+ 	"var y = element[x].length;"
				+ 	"var n= y+z;"
				+ 	"if(n>=value1)"
				+ 	"{"
				+ 		"childStart = element[x];"
				+ 		"offsetX = value1-z;"
				+ 		"break;"
				+ 	"}"
				+   "else"
				+ 	"{"
				+ 	 	"z = y+z;"
				+ 	"}"
				+ "}"
				+ 	"range.setStart(childStart, offsetX);"
				+ 	"range.setEnd(childStart, offsetX);"
				+	"selection.removeAllRanges();"
				+ 	"selection.addRange(range);"
				+ 	"position=node.getBoundingClientRect();"
				+	"sel = window.getSelection().getRangeAt(0).getBoundingClientRect();"
			    + 	"oEvent = document.createEvent('MouseEvents');"
				+ 	"oEvent.initMouseEvent('click', true, true, document.defaultView, 0, 0, 0, sel.left, sel.top, false, false, false, false, 0, node);"
				+ 	"node.dispatchEvent(oEvent);");

				flag = true;
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean openequation(String elementPath, String element) throws Exception {	/*Project Specific Action */
		intializeJavascript();
		boolean flag = false;
		try {
			js.executeScript("var equation = document.evaluate("+"\""+elementPath+"\""+",document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"
			+ "equation.scrollIntoView();"
			+ "equation.click();");
			flag = waituntil(p.getProperty(element.toUpperCase()).trim(), element);
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean insertsymbol(String symbolname) {
		boolean flag = false;
		String element = p.getProperty("MATHEDITOR")+"//button[@title='"+symbolname+"']";
		try {
			flag = click(element, symbolname);
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	
	public static boolean openquery(String queryIndex) throws Exception {	/*Project Specific Actions*/
		boolean flag = false;
		try {
			new WebDriverWait(d, 20).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(p.getProperty("QUERYHEADER").trim())));
			d.findElement(By.xpath(p.getProperty("QUERYHEADER").trim())).click();	
		}		
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		finally {
			if(waitfor(p.getProperty("QUERYHEADER").trim()+"//ul/li["+queryIndex+"]", "QUERYHEADER")){
				if(click(p.getProperty("QUERYHEADER").trim()+"//ul/li["+queryIndex+"]", "Q"+queryIndex)) {
					flag = true;
				}	
			}
		}
		return flag;
	}
	
	
	public static boolean replyquery(String index, String value) throws Exception {	/*Project Specific Actions*/
		boolean flag = false;
		try {
			if(waitfor(p.getProperty("QUERYEDITOR").trim(), "QUERYEDITOR")) {
				if(typetext("QUERYEDITOR", "Q"+index)) {
					if(clickelement("QUERYRESPONSE")) {
						Helper.testlog("pass", "Q"+index, "Replied", test);
						flag = true;	
					}
				}
			}
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean signaloutpackageverification(String environment, String path,String values, String jid, String aid, String role, String filetype) throws Exception {
		boolean flag = false;
		int x =0;
		JSONObject obj = Helper.getDetails(environment);
		String hostname = Helper.getData(obj, "hostname");
		String username = Helper.getData(obj, "username");
		String password = Helper.getData(obj, "password");
		if(values.equalsIgnoreCase("signal")){
			System.out.println("Waiting for signal please wait...");
		}else{
			flag = dontseeelement(p.getProperty("SUBMIT_BUTTON").trim(), "submit");
			if(!flag){
				System.out.println("After Submitting....Submit Button is visible.");
				return flag;
			}else{
				System.out.println("Waiting for out-package please wait...");
			}
		}
		String keyword = aid.equals("0") ? jid.toUpperCase().trim()+"-"+role.toUpperCase().trim() : jid.toUpperCase().trim()+"-"+aid.trim()+"-"+role.toUpperCase().trim();
		try {
			do {
				flag = Helper.checkOutSignal(values, hostname, username, password, path, keyword, filetype);
				Thread.sleep(60000);
				x++;
			}
			while((flag ==false)&&(x<Integer.parseInt(p.getProperty("FTPMAXMIMUMWAITTIME"))));
			if(!flag) {
				Helper.testlog("fail", jid+"_"+aid+"_"+role+" In ["+path+"]", "Not Found", test);
			}else{
				if(values.equalsIgnoreCase("signal")){
					downloadsignal(environment, path, jid, aid, role, filetype);
					flag = true;
				}else{
					flag = true;
				}
				
			}
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	
	public static boolean downloadsignal(String environment, String path, String jid, String aid, String role, String f_extension) throws Exception {
		boolean flag = false;
		JSONObject obj = Helper.getDetails(environment);
		String hostname = Helper.getData(obj, "hostname");
		String username = Helper.getData(obj, "username");
		String password = Helper.getData(obj, "password");
		try {
			String keyword = aid.equals("0") ? jid.toUpperCase().trim()+"-"+role.toUpperCase().trim() : jid.toUpperCase().trim()+"-"+aid.trim()+"-"+role.toUpperCase().trim();
			if(Helper.downloadSignal(hostname, username, password, path, keyword, role, f_extension)) {
				flag = true;
			}
			else {
				Helper.testlog("fail", f_extension+" - Signal ["+jid+"_"+aid+"_"+role+"]", "Not Found In "+"["+path+"]", test);
			}
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean getscreenshot(String parentDirectory) {
		File filePath = new File(serverdirectory+p.getProperty("OUTPACKAGEPATH").trim()+parentDirectory);
		String screenshot_location = serverurl+p.getProperty("OUTPACKAGEPATH").trim()+parentDirectory;
		File srcFile = ((TakesScreenshot) d).getScreenshotAs(OutputType.FILE);
		boolean flag = false;
		try {
			filePath = Helper.createFolder(filePath);
			FileUtils.copyFile(srcFile, filePath);
			Helper.testlog("Pass", filePath.getName(),"Screenshot Completed : Pass", test);
			Helper.providehyperlink(screenshot_location, new File(parentDirectory).getName(), test);
			flag = true;
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	
	public static boolean getproofview(String output) throws Exception {
		intializeJavascript();
		boolean flag = false;
		try	{
			if(Helper.proofPrerequisite(js, test, d)) {
				Screenshot shot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(500)).takeScreenshot(d);
				String screenshotLocation = Helper.createFile(serverdirectory+p.getProperty("OUTPACKAGEPATH").trim()+output);
				String imageUrl = serverurl+p.getProperty("OUTPACKAGEPATH").trim()+output;
				ImageIO.write(shot.getImage(), "PNG", new File(screenshotLocation));
				Thread.sleep(5000);
				Helper.providehyperlink(imageUrl, new File(output).getName(), test);
				flag = true;
			}	
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	
	public static boolean checkdownloadedfile(String fileName, String searchDirectory) throws Exception {
		boolean flag = false;
		try {
			int x = 0, filePresent=0, iter = Integer.parseInt(p.getProperty("MAXIMUMTIME").trim())/20000;
			do {
				filePresent = Helper.getFiles(fileName, searchDirectory).size();
				if(filePresent!=0) {
					Helper.testlog("Pass", fileName, "Found", test);
					flag = true;
				}
				Thread.sleep(20000);
				x++;
			}
			while((x<iter)&&(flag==false));
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	
	public static boolean gotoproofview(String element, String inseconds) throws Exception {
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		boolean response = false;
		try {
			if(click(p.getProperty("PROOFBUTTON").trim(), "Proof Button")) {	
				Helper.testlog("info", "Loading Start -", df.format(new Date()), test);
				(new WebDriverWait(d, Integer.parseInt(inseconds))).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(p.getProperty(element.toUpperCase().trim()))));
				Helper.testlog("info", "Loading End -", df.format(new Date()), test);
				response = true;
			}
		} 
		catch (Exception ex)  {	
			test.log(LogStatus.ERROR, "Page Load Failed In"+ inseconds);
			test.log(LogStatus.ERROR, ex.getMessage());
			test.log(LogStatus.INFO, test.addBase64ScreenShot(Helper.EncodeImage(d, reportScreenshotLocation+"/gotoproofview.jpg")));
		}
		return response;
	}
	
	public static boolean imageconvert(String sourcePath) {
		boolean flag = false;
		try	{
			String servicePath = mountpath+p.getProperty("OUTPACKAGEPATH").trim()+new File(sourcePath).getParent().replace("\\", "/")+"/"+FilenameUtils.removeExtension(new File(sourcePath).getName());
			String role = new File(sourcePath).getParent().replace("\\", "/").split("/")[1];
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("pdf", servicePath, ContentType.TEXT_PLAIN);
            builder.addTextBody("role", role, ContentType.TEXT_PLAIN);
            HttpEntity multipart = builder.build();
            HttpResponse response = Helper.publishJSONWITHPARAMS(multipart, e.getProperty("CONVERTSERVICEURL").trim());
            String jsonData = EntityUtils.toString(response.getEntity());
            String errorCode = Helper.parseJSON(jsonData, e.getProperty("ERRORCODE").trim(), test);
            System.out.println("Response: "+jsonData);
            if(errorCode.equals("0")) {	
            	System.out.println("Conversion Completed");
            	String filePath = Helper.parseJSON(jsonData, e.getProperty("FILEPATH").trim(), test); 
            	Helper.testlog("Pass", "Pdf - Png", "Conversion Completed", test);
            	Helper.providehyperlink(filePath, "Edit-Report", test);
            	flag = true;
            }
            else {
            	System.out.println("Response: ["+jsonData+"]");
            	Helper.testlog("Fail", "Pdf - Png", "Conversion Failed", test);
            }
        }
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	
	public static boolean imagecompare(String actualPath, String expectedPath)	{
		boolean flag = false;
		try {
			actualPath = mountpath+p.getProperty("OUTPACKAGEPATH").trim()+new File(actualPath).getParent().replace("\\", "/")+"/"+FilenameUtils.removeExtension(new File(actualPath).getName());
        	expectedPath =FilenameUtils.removeExtension(expectedPath);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("act", actualPath, ContentType.TEXT_PLAIN);
            builder.addTextBody("exp", expectedPath, ContentType.TEXT_PLAIN);
            HttpEntity multipart = builder.build();
            HttpResponse response = Helper.publishJSONWITHPARAMS(multipart, e.getProperty("COMPARESERVICEURL").trim());
            String jsonData = EntityUtils.toString(response.getEntity());
            String responseCode = Helper.parseJSON(jsonData, e.getProperty("ERRORCODE").trim(), test);   
            String url = Helper.parseJSON(jsonData, e.getProperty("FILEPATH").trim(), test);
            if(responseCode.equalsIgnoreCase("0") && (!url.equalsIgnoreCase("NODIFF"))) {
            	Helper.testlog("fail", new File(actualPath).getName(), "Diff Found", test);
            	Helper.providehyperlink(url, "Diff-Image", test);
            }
            else if(responseCode.equalsIgnoreCase("0") && url.equalsIgnoreCase("NODIFF")) {
            	Helper.testlog("pass", "Visual Comparison Completed", "No Diff Found", test);
            	flag = true;
            }
            else {
            	test.log(LogStatus.WARNING, jsonData);
            }
		} catch (Exception e) {
           test.log(LogStatus.ERROR, e.getMessage());
        }
		return flag;
	}
	
	
	
	public static boolean xmldiff(String actualPath, String expectedPath)	{
		boolean flag = false;
		try {
			String fullPath =e.getProperty("SERVERLOCATION").trim()+"/"+mountpath+p.getProperty("OUTPACKAGEPATH").trim()+actualPath;
			actualPath = mountpath+p.getProperty("OUTPACKAGEPATH").trim()+new File(actualPath).getParent().replace("\\", "/")+"/"+FilenameUtils.removeExtension(new File(actualPath).getName());
			Helper.breakdownTags(new File(fullPath).getParent(), new File(fullPath).getName());
			expectedPath =FilenameUtils.removeExtension(expectedPath);
        	MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("act", actualPath, ContentType.TEXT_PLAIN);
            builder.addTextBody("exp", expectedPath, ContentType.TEXT_PLAIN);
            HttpEntity multipart = builder.build();
            HttpResponse response = Helper.publishJSONWITHPARAMS(multipart, e.getProperty("XMLCOMPARE").trim());
            String jsonData = EntityUtils.toString(response.getEntity());
            String responseCode = Helper.parseJSON(jsonData, e.getProperty("ERRORCODE").trim(), test);   
            String url = Helper.parseJSON(jsonData, e.getProperty("FILEPATH").trim(), test);
            if(responseCode.equalsIgnoreCase("0") && (!url.equalsIgnoreCase("NODIFF"))) {
            	Helper.testlog("fail", "XML-DIFF", "Diff Found", test);
            	Helper.providehyperlink(url, "Diff-Image", test);
            }
            else if(responseCode.equalsIgnoreCase("0") && url.equalsIgnoreCase("NODIFF")) {
            	Helper.testlog("pass", "XML-DIFF", "No Diff Found", test);
            	flag = true;
            }
            else {
            	test.log(LogStatus.WARNING, jsonData);
            }
		} catch (Exception e) {
           test.log(LogStatus.ERROR, e.getMessage());
        }
		return flag;
	}
	
	
	public static boolean xmlsignal(String actualPath, String expectedPath)	{
		boolean flag = false;
		try {
			String fullPath =serverdirectory+p.getProperty("OUTPACKAGEPATH").trim()+actualPath;
			String signalname = Helper.getName(new File(serverdirectory+p.getProperty("OUTPACKAGEPATH").trim()+actualPath), "xml");
			actualPath = mountpath+p.getProperty("OUTPACKAGEPATH").trim()+actualPath+"/"+FilenameUtils.removeExtension(signalname);
			Helper.breakdownTags(fullPath, signalname);
			expectedPath =FilenameUtils.removeExtension(expectedPath);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("act", actualPath, ContentType.TEXT_PLAIN);
            builder.addTextBody("exp", expectedPath, ContentType.TEXT_PLAIN);
            HttpEntity multipart = builder.build();
            HttpResponse response = Helper.publishJSONWITHPARAMS(multipart, e.getProperty("XMLCOMPARE").trim());
            String jsonData = EntityUtils.toString(response.getEntity());
            String responseCode = Helper.parseJSON(jsonData, e.getProperty("ERRORCODE").trim(), test);   
            String url = Helper.parseJSON(jsonData, e.getProperty("FILEPATH").trim(), test);
            if(responseCode.equalsIgnoreCase("0") && (!url.equalsIgnoreCase("NODIFF"))) {
            	Helper.testlog("fail", "XML-DIFF", "Diff Found", test);
            	Helper.providehyperlink(url, "Diff-Image", test);
            }
            else if(responseCode.equalsIgnoreCase("0") && url.equalsIgnoreCase("NODIFF")) {
            	Helper.testlog("pass", "XML-DIFF", "No Diff Found", test);
            	flag = true;
            }
            else {
            	test.log(LogStatus.WARNING, jsonData);
            }
		} catch (Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
        }
		return flag;
	}
	
	
	/* Direct Editing Features*/
	
	public static boolean keyin(String value) {
		intializeActions();
		boolean flag = false;
		try {
			action.sendKeys(value).build().perform();
			Helper.testlog("Pass", value, "Insertion", test);
			flag = true;
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static boolean useshortcutkeys(String keycombination) throws AWTException {
		intializeActions();
		boolean flag= false;
		if(Helper.initShortcutKey(action,keycombination)) {
			Helper.testlog("Pass", keycombination, "Triggered", test);
			flag = true;
		}
		return flag;
	}
	
	public static boolean sendkeys(String keyname, String instances) {
		intializeActions();
		boolean flag = false;
		if(keyname.equalsIgnoreCase("backspace")) {
			flag = Helper.throwKeyEvent(action, Keys.BACK_SPACE, instances);
		}
		else if(keyname.equalsIgnoreCase("delete")) {
			flag = Helper.throwKeyEvent(action, Keys.DELETE, instances);
		}
		if(flag) {
			Helper.testlog("Pass", keyname+"("+instances+")", "Event Triggered", test);
		}
		return flag;
	}
	
	public static boolean movefiletoftp(String environment, String remoteFilePath, String localPath) throws Exception {
		boolean flag = false;
		JSONObject obj = Helper.getDetails(environment);
		String hostname = Helper.getData(obj, "hostname");
		String username = Helper.getData(obj, "username");
		String password = Helper.getData(obj, "password");
		try {
			if(Helper.moveFileToFtp(hostname, username, password, remoteFilePath, e.getProperty("TESTDATA_LOCATION").trim()+localPath)) {
				Helper.testlog("Pass", new File(localPath).getName(), "Ftp File Upload", test);
				flag = true;
			}
		}
		catch(Exception e) {
			Helper.testlog("error", new File(localPath).getName(), e.getMessage(), test);
		}
		return flag;
	}
	
	
	public static boolean getinfo(String environment, String remoteFilePath, String jid, String aid, String role) throws Exception {
		boolean flag = false;
		JSONObject obj = Helper.getDetails(environment);
		String hostname = Helper.getData(obj, "hostname");
		String username = Helper.getData(obj, "username");
		String password = Helper.getData(obj, "password");
		String localInput = jid.toUpperCase()+"-"+aid.toUpperCase()+"-"+role.toUpperCase();
		try {
			url = Helper.readftpFile(hostname, username, password, remoteFilePath, localInput, "XML", p.getProperty("URLREGEX").trim());
			if(!url.equals("")) {
				Helper.testlog("Pass", url, "Found", test);
				flag = true;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	public static boolean getinformation(String jid, String aid, String role) throws Exception {
		boolean flag = false;
		globaljid = jid;
		globalaid = aid;
		String signalvalues = jid.toUpperCase()+"-"+aid.toUpperCase()+"-"+role.toUpperCase();
		try {
			url = Helper.readSignalFile(signalvalues, role, "xml", p.getProperty("URLREGEX").trim());
			if(!url.equals("")) {
				Helper.testlog("Pass", url, "Found", test);
				flag = true;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	public static boolean generatesecondrounddataset(String src, String inputXML, String roleKey) {
		boolean packagexmlmodification, outxmlreplacement = false, zipgeneration= false, check =false;
		try {
			JSONObject obj = Helper.getDetails(roleKey);
			
			File datasetZipFile = new File(e.getProperty("TESTDATA_LOCATION").trim()+src); /* Initial Dataset Zip Location */
			File readyXmlFile = new File(datasetZipFile.getParentFile()+"/"+Helper.getName(datasetZipFile.getParentFile(), "xml")); /* Initial Dataset ReadyXML Location */
			
			File authorDatasetZipFile = new File(datasetZipFile.getParentFile()+"/"+Helper.getData(obj, "key")+"/"+datasetZipFile.getName()); /* Next Round Dataset Zip Location */  
			File authorReadyXmlFile = new File(datasetZipFile.getParentFile()+"/"+Helper.getData(obj, "key")+"/"+readyXmlFile.getName());  		/* Next Round Dataset ReadyXML Location */
			
			File authorDatasetZipFolder = Helper.deleteAndCreateFolder(authorDatasetZipFile.getParentFile());  /* Next Round Dataset Zip Folder */
			File cqoutxmlFile = new File(serverdirectory+p.getProperty("OUTPACKAGEPATH").trim()+inputXML);	/* First Round OutXML Location */
			
			FileUtils.copyFile(datasetZipFile, authorDatasetZipFile);
			
			Helper.testlog("Info", "Dataset Duplication Started", roleKey, test);
			System.out.println("\nDataset Duplication Started");
			Thread.sleep(5000);
			Helper.unzipFile(authorDatasetZipFolder);
			Thread.sleep(5000);
			Helper.changeXMLAttribute(authorDatasetZipFolder, "package.xml", Helper.getData(obj, "tag"), "actor", Helper.getData(obj, "actor"));
			Helper.changeXMLAttribute(authorDatasetZipFolder, "package.xml", Helper.getData(obj, "tag"), "role", Helper.getData(obj, "role"));
			Helper.changeXMLAttribute(authorDatasetZipFolder, "package.xml", Helper.getData(obj, "tag"), "task", Helper.getData(obj, "task"));
			packagexmlmodification = true;
			if(packagexmlmodification) {
				Helper.testlog("Info", "Package XML Contents Changed", roleKey, test);
				System.out.println("\nPackage XML Modification Successful");
				File outxmlFolder = Helper.replaceFile(authorDatasetZipFolder, "xml");
				File outxmlFile = new File(outxmlFolder+"/"+cqoutxmlFile.getName());
				FileUtils.copyFile(cqoutxmlFile, outxmlFile);
				Helper.testlog("Info", outxmlFile.toString(), "Replaced", test);
				System.out.println("\nOutXML Replacement Completed");
				outxmlreplacement = Helper.removeTag(outxmlFile, "<opt_COMMENT(.*)+opt_COMMENT>", "");
				Helper.testlog("Info", outxmlFile.toString(), "OPT TAG REMOVED", test);
			}
			if(outxmlreplacement) {
				String rootFolderName = FilenameUtils.removeExtension(authorDatasetZipFile.getName());
				File originalZipLocation = new File(authorDatasetZipFolder+"/"+rootFolderName);
				Helper.createPath(originalZipLocation.getAbsolutePath());
				Helper.deleteFile(authorDatasetZipFile);
				Thread.sleep(5000);
				File extractFolder = authorDatasetZipFile.getParentFile().listFiles()[0].getAbsoluteFile();
				File extractLocation = new File(originalZipLocation+"/"+extractFolder.getName());
				extractFolder.renameTo(extractLocation);
				Thread.sleep(5000);
//				Helper.createZipFile(authorDatasetZipFile.getAbsolutePath(), authorDatasetZipFile.getParentFile().listFiles()[0].getAbsolutePath());
				Helper.testlog("Pass", authorDatasetZipFile.toString(), "Zip Generated", test);
				System.out.println("\nZip Generation Completed");
				zipgeneration = true;
			}
			if(zipgeneration) {
				String checksum = DigestUtils.md5Hex(new FileInputStream(authorDatasetZipFile));
				FileUtils.copyFile(readyXmlFile, authorReadyXmlFile);
				Thread.sleep(5000);
				Helper.changeXMLAttribute(authorDatasetZipFolder, authorReadyXmlFile.getName(), "md5", Helper.getData(obj, "alttag"), checksum);
				Helper.testlog("Pass", authorReadyXmlFile.toString(), "CheckSum Changed", test);
				System.out.println("\nCheckSum Data Generation Completed");
				check = true;
			}
		}
		catch(Exception e) {
			Helper.testlog("Error", src, e.getMessage(), test);
		}
		return check;
	}
	
		public static boolean generatedataset(String src, String inputXML, String roleKey, String dropdatasettype) {
			boolean packagexmlmodification, outxmlreplacement = false, zipgeneration= false, check =false;
			
			try {
				JSONObject obj = Helper.getDetails(roleKey);
				
				File datasetZipFile = new File(e.getProperty("TESTDATA_LOCATION").trim()+src); /* Initial Dataset Zip Location */
				File readyXmlFile = new File(datasetZipFile.getParentFile()+"/"+getName(datasetZipFile.getParentFile(), "xml")); /* Initial Dataset ReadyXML Location */
				
				File authorDatasetZipFile = new File(datasetZipFile.getParentFile()+"/"+Helper.getData(obj, "key")+"/"+datasetZipFile.getName()); /* Next Round Dataset Zip Location */  
				File authorReadyXmlFile = new File(datasetZipFile.getParentFile()+"/"+Helper.getData(obj, "key")+"/"+readyXmlFile.getName()); 
				
				File authorDatasetZipFolder = Helper.deleteAndCreateFolder(authorDatasetZipFile.getParentFile());  /* Next Round Dataset Zip Folder */
				File cqoutxmlFile = new File(serverdirectory+p.getProperty("OUTPACKAGEPATH").trim()+inputXML);	/* First Round OutXML Location */
				String rootFolderName = FilenameUtils.removeExtension(authorDatasetZipFile.getName());
				FileUtils.copyFile(datasetZipFile, authorDatasetZipFile);
				System.out.println("\nDataset Duplication Started");
				Thread.sleep(5000);
				Helper.unzipFile(authorDatasetZipFolder);
				Thread.sleep(5000);
				Helper.changeXMLAttribute(authorDatasetZipFolder, "package.xml", Helper.getData(obj, "tag"), "actor", Helper.getData(obj, "actor"));
				Helper.changeXMLAttribute(authorDatasetZipFolder, "package.xml", Helper.getData(obj, "tag"), "role", Helper.getData(obj, "role"));
				Helper.changeXMLAttribute(authorDatasetZipFolder, "package.xml", Helper.getData(obj, "tag"), "task", Helper.getData(obj, "task"));
				System.out.println("\nXml Changed");
				if(dropdatasettype!=null){
					if(dropdatasettype.equalsIgnoreCase("droppackagexml")){
						File deleteassetfolder = new File(authorDatasetZipFolder+"/"+FilenameUtils.removeExtension(authorDatasetZipFile.getName()));
						File packagexmlpath = Filefinder(authorDatasetZipFolder, "package.xml");
						RemoveParentTagWithChild(packagexmlpath, "file-info");
						System.out.println("file-info tag deleted");	
						deletefolder(deleteassetfolder.getParentFile(), "xml");	/*method call added*/
						outxmlreplacement = true;
					}
					else if(dropdatasettype.equalsIgnoreCase("dropdataset")){
						packagexmlmodification = true;
						if(packagexmlmodification) {
							System.out.println("\nPackage XML Modification Successful");
							File outxmlFolder = Helper.replaceFile(authorDatasetZipFolder, "xml");
							File outxmlFile = new File(outxmlFolder+"/"+cqoutxmlFile.getName());
							FileUtils.copyFile(cqoutxmlFile, outxmlFile);
							Helper.testlog("Info", outxmlFile.toString(), "Replaced", test);
							System.out.println("\nOutXML Replacement Completed");
							outxmlreplacement = Helper.removeTag(outxmlFile, "<opt_COMMENT(.*?)+opt_COMMENT>", "");
							Helper.testlog("Info", outxmlFile.toString(), "OPT TAG REMOVED", test);
						}
					}
				if(outxmlreplacement) {
//					String rootFolderName = FilenameUtils.removeExtension(authorDatasetZipFile.getName());
					File originalZipLocation = new File(authorDatasetZipFolder+"/"+rootFolderName+".zip");/* extn added */
					Helper.createPath(originalZipLocation.getAbsolutePath());
					Helper.deleteFile(authorDatasetZipFile);
					Thread.sleep(5000);
					File extractFolder = authorDatasetZipFile.getParentFile().listFiles()[0].getAbsoluteFile();
					
					File extractLocation = new File(originalZipLocation+"/"+extractFolder.getName());
					extractFolder.renameTo(extractLocation);
					Thread.sleep(5000);
					File createdir = new File(authorDatasetZipFolder+"/"+rootFolderName);
					createdir.mkdir();
					File createdir1 = new File(createdir+"/"+extractFolder.getName());
					createdir1.mkdir();
					
					FileUtils.copyDirectory(extractFolder, createdir1);
					Thread.sleep(3000);
					FileUtils.deleteDirectory(extractFolder);
//					Helper.createZipFile(authorDatasetZipFile.getAbsolutePath(), authorDatasetZipFile.getParentFile().listFiles()[0].getAbsolutePath());
					Helper.testlog("Pass", authorDatasetZipFile.toString(), "Zip Generated", test);
					System.out.println("\nZip Generation Completed");
					zipgeneration = true;
				}
				if(zipgeneration) {
					String checksum = DigestUtils.md5Hex(new FileInputStream(authorDatasetZipFile));
					FileUtils.copyFile(readyXmlFile, authorReadyXmlFile);
					Thread.sleep(5000);
					Helper.changeXMLAttribute(authorDatasetZipFolder, authorReadyXmlFile.getName(), "md5", Helper.getData(obj, "alttag"), checksum);
					Helper.testlog("Pass", authorReadyXmlFile.toString(), "CheckSum Changed", test);
					System.out.println("\nCheckSum Data Generation Completed");
					System.out.println("\n=================== ***** "+roleKey+" - Dataset Generation Completed Successfully"+" ***** ===================");
					check = true;
				}
			}
				else{
					System.out.println("Drop dataset type parameter is incorrect");
				}
				File extractLocation1 = new File(authorDatasetZipFolder+"/"+rootFolderName);
				FileUtils.deleteDirectory(extractLocation1);
				}
			
			catch(Exception e) {
				Helper.testlog("Error", src, e.getMessage(), test);
			}
			
			return check;
		}
		
		public static String getName(File files, String extension) {
			String filename = "";
			File[] fileNames = files.listFiles();
			for (File f : fileNames) {
				if (f.getName().endsWith(extension)) {
					filename = f.getName();
				}
			}
			return filename;
		}
		
		
		public static boolean deletefolder(File folder, String filename) throws Exception {  
			boolean flag= false;
		
		try {
			File[] file = folder.listFiles();
			
			for (File f : file) {
				
				if (f.isFile() && f.getName().endsWith(filename)
						&& !(f.getName().equalsIgnoreCase("package.xml"))) {
					
					File filePath = f.getParentFile();
					FileUtils.deleteDirectory(filePath);
					flag=true;
					break;
										
				} else if (f.isDirectory()) {
					deletefolder(f, filename);
				}
			}
		}
		catch (Exception e) {
			throw new Exception(e);
		}
		return flag;
		}
		
	public static File Filefinder(File path, String filename) throws Exception {
			try {
				File[] file = path.listFiles();
					for (File f : file) {
					if (f.isFile() && (f.getName().equalsIgnoreCase("package.xml"))) {
						filePath = f.getAbsoluteFile();
					break;
					} else if (f.isDirectory()) {
						Filefinder(f, filename);
					}
				}
				
			} catch (Exception e) {
				throw new Exception(e);
			}
			return filePath;
					
		}
	public static boolean RemoveParentTagWithChild(File xmlpath, String tagname){
		boolean bool=false;
		boolean flag = false;
		File tempfile = new File(xmlpath+".tmp");
		
		try {
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader(xmlpath));
			BufferedWriter bw1=new BufferedWriter(new FileWriter(tempfile));
			String line = null;
			while ((line = br.readLine()) != null) {			
				if(!line.contains("<"+tagname+">") && !line.contains("</"+tagname+">")){
					if(!bool){
						bw1.write(line);
						bw1.newLine();
					}
				}
				else{
					bool=true;
					if(bool){
						if(line.contains("</"+tagname+">")){
							bool=false;
						}
					}
				}
				
				sb.append(line);
				
			}
			bw1.close();
			
			br.close();
			BufferedWriter bw = new BufferedWriter(new FileWriter(xmlpath));
			bw.write(sb.toString());
			bw.close();
			Path p1=Paths.get(tempfile.getAbsolutePath());
			Path p2=Paths.get(xmlpath.getAbsolutePath());
			java.nio.file.Files.move(p1, p2, StandardCopyOption.REPLACE_EXISTING);
			flag = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return flag;
		}
	
	public static boolean getreport(String destinationFolder) throws Exception {
		boolean flag = false;
		String[] name = destinationFolder.split("/");
		String filename = name[2];
		String stage = name[1];
		try {
			if(Runner.browsername.equalsIgnoreCase("ie")){
				String token[] = url.split("/");
				if(filename.contains("Proof")){
					FileUtils.copyURLToFile(new URL("http://api.pcpeqa.tnq.co.in/index.php/downloadPageProof/"+token[token.length-1]+"/"+stage), new File(serverdirectory+p.getProperty("OUTPACKAGEPATH").trim()+destinationFolder));
					Helper.testlog("Pass", filename, "Download and Moved", test);
					flag = true;
				}else{
					FileUtils.copyURLToFile(new URL("https://pgc-dev-test.s3.amazonaws.com/pgqasjs/proofs/sjs/"+globaljid+"/"+globalaid+"/1/"+filename), new File(serverdirectory+p.getProperty("OUTPACKAGEPATH").trim()+destinationFolder));
					Helper.testlog("Pass", filename, "Download and Moved", test);
					flag = true;
				}
			}else{
				String fileNameWithoutExtension = FilenameUtils.getBaseName(new File(destinationFolder).getName());
				String fileNameWithExtension = new File(destinationFolder).getName();
				String element = new File(destinationFolder).getName().contains("ViewPageProof") ? p.getProperty("VIEWPAGEPROOF").trim(): p.getProperty("SESSIONREPORT").trim();
				if(click(element, new File(destinationFolder).getName()+" Download Button")) {
					if(checkdownloadedfile(fileNameWithoutExtension, System.getProperty("user.home")+"/Downloads"))	{
						List<String> files = Helper.getFiles(fileNameWithoutExtension, System.getProperty("user.home")+"/Downloads");
						if(files.size()>=1) {
							if(Helper.moveFiles(files.get(0), serverdirectory+p.getProperty("OUTPACKAGEPATH").trim()+destinationFolder)) {
								if(seefile(new File(serverdirectory+p.getProperty("OUTPACKAGEPATH").trim()+destinationFolder).getParent(), fileNameWithExtension)) {
									Helper.providehyperlink(serverurl+p.getProperty("OUTPACKAGEPATH").trim()+destinationFolder, fileNameWithExtension, test);
									Helper.testlog("Pass", fileNameWithExtension, "Download and Moved", test);
									flag = true;
								}
								else {	
									Helper.testlog("Fail", fileNameWithExtension, "File Movement Failed From Local To Server", test);
								}
							}
						}
						else {
							Helper.testlog("fail", fileNameWithExtension, "Download Failed", test);
						}
					}
					else {
						Helper.testlog("Fail", fileNameWithExtension, "Download Exceeds {"+p.getProperty("MAXIMUMTIME").trim()+"} Milliseconds", test);
					}
				}
			}
			
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;
	}
	
	public static Store connectmail() throws Exception {
		Store store;
		String hostname = e.getProperty("HOSTNAME").trim();
		try {
			Properties properties = new Properties();
			Session session = Session.getDefaultInstance(properties);
			store = session.getStore("imaps");
			store.connect(e.getProperty("HOSTNAME").trim(), e.getProperty("USERNAME").trim(), e.getProperty("PASSWORD"));
			Helper.testlog("pass", hostname, "Mail Connected Successfully", test);
			return store;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Connection Problem");
		}
	}

	public static boolean readmail(String subject,String value) throws Exception {
			Store store;
			String theString = null;
			boolean flag = false;
			try {
				store = connectmail();
				Folder emailFolder = store.getFolder("INBOX");
				emailFolder.open(Folder.READ_ONLY);
				Message[] messages = emailFolder.search(new FlagTerm(new Flags(Flag.SEEN), false));
				int s = messages.length;
				for (int i = s; i > 1; i--) {
					Message message = messages[i - 1];
					if (message.getSubject().contains(subject)) {
						System.out.println("Mail Found :" + message.getSubject());
						if(value.equalsIgnoreCase("verify")){
							Helper.testlog("pass", subject, "Contained Mail Subject Found", test);
							flag = true;
							break;
						}
						Object mp = (Object) message.getContent();
						if (mp instanceof MimeMultipart) {
							MimeMultipart mpp = (MimeMultipart) mp;
							for (int count = 0; count < mpp.getCount(); count++) {
								MimeBodyPart bp = (MimeBodyPart) mpp.getBodyPart(count);
							InputStream fileNme = bp.getInputStream();
							StringWriter writer = new StringWriter();
							IOUtils.copy(fileNme, writer, "UTF-8");
							theString = writer.toString();
						}
					} else if (mp instanceof Multipart) {
						Multipart mpp = (Multipart) mp;
						for (int count = 0; count < mpp.getCount(); count++) {
							MimeBodyPart bp = (MimeBodyPart) mpp.getBodyPart(count);
							InputStream fileNme = bp.getInputStream();
							StringWriter writer = new StringWriter();
							IOUtils.copy(fileNme, writer, "UTF-8");
							theString = writer.toString();
						}
					} else if (mp instanceof String) {
						theString = (String) message.getContent();
					}
					System.out.println(theString);
					Pattern pattern = Pattern.compile(p.getProperty("MAILREGEX").trim());
					Matcher matcher = pattern.matcher(theString);
					if (matcher.find()) {
						finallink = matcher.group(1);
					}
					Helper.testlog("pass", finallink, "URL Found", test);
					System.out.println("Collaborator URL Found : "+finallink);
					flag = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(subject + " Not Found");
		}
		store.close();
		return flag;
	}
	
	public static boolean doubleclick(String element, String label) throws Exception {				
		boolean flag = false;
		try {
			Actions action = new Actions(d);
			WebElement ele=d.findElement(By.xpath(element));
			action.doubleClick(ele).release().build().perform();
			Helper.testlog("pass", label," Clicked", test);
			flag = true;
		}
		catch(Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
		}
		return flag;	
	}
	
	public static boolean copypaste(String sourceXpath,String destXpath) throws Exception{
		boolean flag = false;
		try {
			clipboardvalue=d.findElement(By.xpath(sourceXpath)).getText();
			d.findElement(By.xpath(destXpath)).sendKeys(clipboardvalue);
			Helper.testlog("pass", clipboardvalue ," Copied & pasted", test);
			flag = true;
		}
		catch (Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());	
		}
		return flag;
	}
	
	public static void iefiledownload(String fileName,String urlHeader) throws Exception{ 
	boolean flag = false;
		{
		try {
			String token[] = url.split("/");
			FileUtils.copyURLToFile(new URL(urlHeader+token[token.length-1]), new File(fileName));
			Helper.testlog("pass", fileName ," File Downloaded", test);
			flag = true;
		} catch (Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());	
		}
		
		
		}
	}

public static boolean switchframeout(String label) throws Exception {
		
		boolean flag =false;
		try {
			d.switchTo().defaultContent();
			Helper.testlog("pass", label ,"Switch Out : Pass", test);			
			flag = true;
		}
		catch (Exception e) {
			test.log(LogStatus.ERROR, e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}	
	
	
}

