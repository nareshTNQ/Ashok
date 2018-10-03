package xcentral;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Controller extends Template{
	public static WebDriver driver;
	public static File tempFile;
	
	
	public static WebDriver browser(String browsername) throws Exception  {
		if(environment.equalsIgnoreCase("local")) {	
			if(browsername.equalsIgnoreCase("chrome")) {
				System.setProperty("webdriver.chrome.driver",driverslocation+"/chromedriver.exe");
				ChromeOptions options = new ChromeOptions();
				options.addArguments("disable-infobars");
				driver = new ChromeDriver(options);
				driver.manage().window().maximize();
			}
			else if(browsername.equalsIgnoreCase("firefox")) {
				System.setProperty("webdriver.gecko.driver", driverslocation+"/geckodriver.exe");
				DesiredCapabilities capabilities = DesiredCapabilities.firefox();
				capabilities.setCapability("binary", "C:/Program Files/Mozilla Firefox/firefox.exe");
				FirefoxProfile profile = new FirefoxProfile();
				profile.setPreference("browser.download.folderList", 1);
				profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
				profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/msword, application/csv, application/ris, text/csv, image/png, application/pdf, text/html, text/plain, application/zip, application/x-zip, application/x-zip-compressed, application/download, application/octet-stream");
				profile.setPreference("browser.download.manager.showWhenStarting", false);
				profile.setPreference("browser.download.manager.focusWhenStarting", false);  
				profile.setPreference("browser.download.useDownloadDir", true);
				profile.setPreference("browser.helperApps.alwaysAsk.force", false);
				profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
				profile.setPreference("browser.download.manager.closeWhenDone", true);
				profile.setPreference("browser.download.manager.showAlertOnComplete", false);
				profile.setPreference("browser.download.manager.useWindow", false);
				profile.setPreference("services.sync.prefs.sync.browser.download.manager.showWhenStarting", false);
				profile.setPreference("pdfjs.disabled", true);
				capabilities.setCapability(FirefoxDriver.PROFILE, profile);
				FirefoxOptions options = new FirefoxOptions(capabilities);
				driver = new FirefoxDriver(options);
				driver.manage().window().maximize();
			}
			else if(browsername.equalsIgnoreCase("ie")) {
				System.setProperty("webdriver.ie.driver", driverslocation+"/IEDriverServer.exe");
				DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
				caps.setCapability("ignoreZoomSetting", true);
				InternetExplorerOptions options = new InternetExplorerOptions(caps);
				driver = new InternetExplorerDriver(options);
				driver.manage().window().maximize();
			}
		}	
		else if(environment.equalsIgnoreCase("cloud")) {
			String URL = "http://" + accesskey + ":" + secretkey + "@hub.testingbot.com/wd/hub";
			DesiredCapabilities caps = new DesiredCapabilities();
			caps.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
			caps.setCapability("browserName", browsername);
			caps.setCapability("version", browserversion);
			caps.setCapability("platform", osname);
			caps.setCapability("name", releasename);
			driver = new RemoteWebDriver(new URL(URL), caps);
		}
		return driver;		
	}
		
	public static File excelfile() throws Exception {
		File masterFile = null;
		try {
			File[] files = new File(testdatalocation).getParentFile().listFiles();
			for(File file : files) {
				if(file.getName().equalsIgnoreCase(new File(testdatalocation).getName())) {
					masterFile = file;
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return masterFile;
	}
	
	public static void createTempFile() {
		try {
			File file = excelfile();
			String timeStamp = new SimpleDateFormat("ddMMyyyy").format(new Date());
			tempFile = new File(file.getParent()+"/"+releasename+"_"+browsername+"_"+timeStamp+".ods");
			if(!tempFile.exists()) {
				FileUtils.copyFile(file, tempFile);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
		
	public static SpreadSheet sheets() throws Exception {
//		createTempFile();
		SpreadSheet sheet = SpreadSheet.createFromFile(new File(testdatalocation));
		return sheet;
	}
}
