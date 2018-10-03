package xcentral;

import java.util.Properties;

public class Template  {
	
	public static String reportScreenshotLocation, reportlocation, mountPath, serverDirectory, serverUrl;
	public static String projectname;
	public static String customer;
	public static String testtype;
	public static String testcaseid;
	public static String browsername;
	public static String browserversion;
	public static String releasename;
	public static Properties properties;
	public static String osname;
	public static String testdataname, testdatalocation;
	public static String environment;	
	public static String driverslocation;
	public static String secretkey;
	public static String accesskey;
	
	
	public static void initializeVariables(Properties property, String[] userDefinedInputs) {
		try {
			properties = property;
			serverDirectory = properties.getProperty("SERVERLOCATION").trim();
			serverUrl = properties.getProperty("SERVERURL").trim();
			projectname= userDefinedInputs[4].toUpperCase();
			customer = userDefinedInputs[2].toUpperCase();
			testdataname = userDefinedInputs[8];
			testtype = userDefinedInputs[6].toUpperCase();
			testcaseid = userDefinedInputs[5].toUpperCase();
			browsername = userDefinedInputs[3].toUpperCase();
			browserversion = userDefinedInputs[7].toUpperCase();
			releasename = userDefinedInputs[1].toUpperCase();
			osname = Helper.returnOS(userDefinedInputs[9], userDefinedInputs[10]);
			environment = userDefinedInputs[11];
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void configurator() {
//		reportlocation = serverDirectory+"/"+projectname+"/"+customer+"/"+testtype+"/"+osname+"/"+browsername+"/"+browserversion+"/"+releasename+"/Actuals"+"/"+projectname+"_"+customer+"_"+releasename+"_TestResult.html";
		reportlocation =  "D:\\01MyWorkspace\\SR_Central_Regression_Suite\\FileManagement\\Report\\TestResult.html";
		reportScreenshotLocation = serverDirectory+"/"+projectname+"/"+customer+"/"+testtype+"/"+osname+"/"+browsername+"/"+browserversion+"/"+releasename+"/Error";
		serverDirectory = serverDirectory+"/"+projectname+"/"+customer+"/"+testtype+"/"+osname+"/"+browsername+"/"+browserversion+"/"+releasename+"/Actuals";
		serverUrl = serverUrl+"/"+projectname+"/"+customer+"/"+testtype+"/"+osname+"/"+browsername+"/"+browserversion+"/"+releasename+"/Actuals";
		mountPath = projectname+"/"+customer+"/"+"/"+"/"+"/"+testtype+"/"+osname+"/"+browsername+"/"+browserversion+"/"+releasename+"/Actuals";
		driverslocation = properties.getProperty("DRIVERS_LOCATION").trim();
		testdatalocation = properties.getProperty("TESTDATA_LOCATION").trim()+testdataname;
		System.out.println(reportlocation);
		System.out.println(reportScreenshotLocation);
		System.out.println(serverDirectory);
		System.out.println(serverUrl);
		System.out.println(mountPath);
		System.out.println(driverslocation);
		System.out.println(testdatalocation);
//		accesskey = properties.getProperty("KEY").trim();
//		secretkey = properties.getProperty("SECRET").trim();
	}
	
	
	
	
	
	
	
	
	
	
}
