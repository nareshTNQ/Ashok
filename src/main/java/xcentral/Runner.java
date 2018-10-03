package xcentral;


import java.util.*;

import org.jopendocument.dom.spreadsheet.Sheet;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

import java.io.File;
import java.io.FileInputStream;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class Runner extends Template
{
	public static Sheet sheet;
	public static List<String> id;
	public static Properties _elementProperties, _generalProperties;
	public static ExtentReports reporter;
	public static ExtentTest test, moduleTest;
	public static int totalCases, testedCases, passedCases=0, failedCases=0, untestedCases;
	public static List<String> tcid;
	
	public static List<String> testCaseNameList(int n) throws Exception {
		List<String> testCase = new ArrayList<String>();
		sheet = Controller.sheets().getSheet(n);
		for(int row=1;row<sheet.getRowCount();row++) {
			if((!sheet.getValueAt(0, row).equals(""))) {
				testCase.add((String) sheet.getValueAt(0, row));
			}
		}
		return testCase;
	}
	
	public static List<Integer> testIndexList(int n) throws Exception {
		List<Integer> testIndex = new ArrayList<Integer>();
		sheet = Controller.sheets().getSheet(n);
		for(int row=1;row<sheet.getRowCount();row++) {
			if((!sheet.getValueAt(0, row).equals(""))) {
				testIndex.add(row);
			}
		}
		return testIndex;
	}
	
	public static String[] setUserParams(String testsetvalue) {
		String[] params = new String[2];
		if(testsetvalue.equalsIgnoreCase("all")||(testsetvalue.contains(",")||((!testsetvalue.equalsIgnoreCase("all")&&(!testsetvalue.contains(",")&&(!testsetvalue.contains(":"))))))) {
			params[0] = testsetvalue;
			params[1] = null;
		}
		else if(!testsetvalue.contains(",")&&(testsetvalue.contains(":"))) {
			params[0] = testsetvalue.split(":")[0].trim();
			params[1] = testsetvalue.split(":")[1].trim();
		}	
		return params;
	}
	
	
	public static String[] parseUserParameters(String[] userparameters) {
		String params[] = new String[userparameters.length];
		try {
			if(userparameters.length==12) {
				for(int index = 0;index<userparameters.length;index++) {
					params[index] =userparameters[index].split("=")[1].trim();
				}
			}
			else if(userparameters.length>12 || userparameters.length<12) {
				throw new RuntimeException("Test Execution Failed - Error : Invalid Argument");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return params;
	}
	
	
	
	public static Map<String, Integer> getTestCases(List<Integer> list) throws Exception {
		LinkedHashMap<String, Integer> testCaseBoundary = new LinkedHashMap<String, Integer>();
		for(int n=0;n<list.size();n++) {
			List<Integer> test_index =testIndexList(list.get(n));
			int[] range = new int[2];
			for(int i=0;i<=test_index.size()-1;i++) {
				range = getTestSteps(test_index, i);
				String testcaseid = sheet.getValueAt(2, range[0]).toString().toUpperCase();
				String tcid = sheet.getValueAt(0, range[0]).toString().toUpperCase();
				testCaseBoundary.put(list.get(n)+"_"+range[0]+"_"+testcaseid+"_"+tcid, range[1]);
			}
		}
		return testCaseBoundary;
	}
	
	
	public static int[] getTestSteps(List<Integer> indexes, int i) {
		int[] startStop = new int[2];
		if(i!=indexes.size()-1) {
			startStop[0] =	indexes.get(i);
			startStop[1] = 	indexes.get(i+1)-1;
		}
		else if(i==indexes.size()-1) {
			startStop[0] =indexes.get(i);
			List<Integer> endValue = new ArrayList<Integer>();
			endValue = iterateRow(indexes.get(indexes.size()-1));
			startStop[1] = ((endValue.size()+(Integer)startStop[0])-1);
		}
		return startStop;
	}
	
	public static Properties properties(String path) throws Exception {
		FileInputStream inputStream = new FileInputStream(path);
		Properties prop = new Properties();
		prop.load(inputStream);
		return prop;
	}
	
	
	public static void main(String args[]) throws Exception 
	{
		
		_generalProperties= properties("_init.properties");
		_elementProperties = properties(_generalProperties.getProperty("CONFIG_LOCATION").trim());
		System.out.println(_elementProperties);
		
		String allParameters[] = parseUserParameters(args);  
		
		String sheetParameters[] = setUserParams(allParameters[0]);
		
		Template.initializeVariables(_generalProperties, allParameters);
		Template.configurator();
		
		Helper.createFile(reportlocation);
		Helper.createFile(reportScreenshotLocation);
		
		reporter = new ExtentReports(reportlocation);
		reporter.addSystemInfo("Project Name", projectname);
		reporter.addSystemInfo("Release Name", releasename);
		reporter.addSystemInfo("Customer Name", customer);
		reporter.addSystemInfo("Browser Name", browsername+" "+browserversion);
		reporter.addSystemInfo("Test Type", testtype);
		reporter.addSystemInfo("Environment", environment);
		reporter.loadConfig(new File("./ExtentConfig.xml"));
//		TestlinkOperations.main(allParameters);
		ProofCentral proofcentral = new ProofCentral(_elementProperties, reportScreenshotLocation, test, serverDirectory, serverUrl, _generalProperties, mountPath, browsername);
		
		serializeTestCase(sheetParameters[0], sheetParameters[1]);
	}
	
	public static void serializeTestCase(String name1, String name2) throws Exception {
		List<Integer> number = ArrangeTestSet.serializeExecution(name1, name2);
		Map<String, Integer> caseBoundary = new LinkedHashMap<String, Integer>();
		caseBoundary = getTestCases(number);
		totalCases = caseBoundary.size();
		caseBoundary = prepareexecutionalcases(caseBoundary);
		testedCases = caseBoundary.size();
		List<String> list = new ArrayList<String>(caseBoundary.keySet());
		tcid =new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			tcid.add(list.get(i).split("_")[3]);
		}
		executeTestCases(caseBoundary);
	}
	
	
	public static Map<String, Integer> prepareexecutionalcases(Map<String, Integer> overallcases) {
		if(!testcaseid.equalsIgnoreCase("all")) {
			for(Iterator<Entry<String, Integer>> it = overallcases.entrySet().iterator(); it.hasNext();)  {
			      Entry<String, Integer> entry = it.next();
			      if(!entry.getKey().contains(testcaseid.toUpperCase())||(entry.getKey().toUpperCase().contains("NR"))) {
			        it.remove();
			      }
			}
		}
		else if(testcaseid.equalsIgnoreCase("all")) {	
			for(Iterator<Entry<String, Integer>> it = overallcases.entrySet().iterator(); it.hasNext();)  {
			      Entry<String, Integer> entry = it.next();
			      if((entry.getKey().toUpperCase().contains("NR")))  {
			    	  it.remove();
			      }
			}
		}
		return overallcases;
	}
	
	public static List<String> iterateColumn(int row) {
		List<String> values = new ArrayList<String>();
		for(int col=4;col<sheet.getColumnCount();col++) {
			if((!sheet.getValueAt(col, row).equals(""))) {
				values.add(sheet.getValueAt(col, row).toString());
			}
		}
		return values;
	}
	
	public static List<Integer> iterateRow(int row) {
		List<Integer> values = new ArrayList<Integer>();
		for(int m=row;m<sheet.getRowCount();m++) {
			if((!sheet.getValueAt(3,m).equals(""))) {
				values.add(m);
			}
		}
		return values;
	}
	
	public static WebElement returnWebElementPresent(WebDriver driver, String locator, int time) {
		WebDriverWait pageLoadWait = new WebDriverWait(driver, time);
		WebElement loadElement = null;
		try {
			loadElement = pageLoadWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(_elementProperties.getProperty(locator.toUpperCase()).trim())));
			System.out.println(StringUtils.capitalize(locator)+" Found");
		}
		catch(Exception e ) {
			System.out.println(StringUtils.capitalize(locator)+" Not Found");
		}
		return loadElement;
	}
	
	/**
	 * Refresh The Page On Failure And Verifies 'Skip Tour' First If Not Present Will Check For 'Continue' Button.
	 * @param driver
	 * @throws Exception
	 */
	
	
	public static void handlePageLoad(WebDriver driver) throws Exception {
		
		WebElement element;
		boolean flag = false;
		try {
			element = returnWebElementPresent(driver, "HOPSCOTCH", Integer.parseInt(_elementProperties.getProperty("WEBDRIVERWAIT").trim()));
			Thread.sleep(5000);
			if(element!=null) {
				element.click();
				System.out.println("Skip Tour Pop Up Found - Handled Ready For Edit");
				flag = true;
			}
			/*else {
				element = returnWebElementPresent(driver, "CONTINUEBUTTON", Integer.parseInt(_elementProperties.getProperty("WEBDRIVERWAIT").trim()));
				Thread.sleep(5000);
				if(element!=null) {
					element.click();
					System.out.println("Continue Button Found - Handled Ready For Edit");
					flag = true;
				}
			}*/
			if(!flag) {
				if(returnWebElementPresent(driver, "REFRESHELEMENT", Integer.parseInt(_elementProperties.getProperty("WEBDRIVERWAIT").trim()))!=null) {
					System.out.println("Page Loaded Ready For Edit");
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void refreshOnFailure() throws Exception {
		WebDriver driver = ProofCentral.d;
		WebDriverWait wait;
		try {
			if(!driver.toString().contains("null")) {
				wait = new WebDriverWait(driver, 60);
				driver.navigate().refresh();
				wait.ignoring(NoAlertPresentException.class).until(ExpectedConditions.alertIsPresent());
				Alert alert = driver.switchTo().alert();
				alert.accept();
				handlePageLoad(driver);
			}
		}	
		catch(Exception e) {
			System.out.println("Page Refresh On Failure No Alert Found");
			handlePageLoad(driver);
		}
	}	
	
	
	public static void executeTestCases(Map<String, Integer> caseLimit) throws Exception {
		int start, end, i = 0;
		for(Map.Entry<String,Integer> value: caseLimit.entrySet()) {
			try {
				sheet = Controller.sheets().getSheet(Integer.parseInt(value.getKey().split("_")[0]));
				String testcase = (String) sheet.getValueAt(1, Integer.parseInt(value.getKey().split("_")[1]));
				System.out.println("\n"+StringUtils.capitalize(testcase)+" Started");
				test = reporter.startTest(testcase);
				start = Integer.parseInt(value.getKey().split("_")[1]);
				end = value.getValue();	
				boolean flag =executeTestSteps(start, end);
//				System.out.println(flag+"<-------"+i);
				if(flag){
//					TestlinkOperations.updateResultToTestLink(tcid.get(i), ExecutionStatus.PASSED, "Testcase Passed");
//					System.out.println(i+"   Pass");
					i++;
					passedCases++;
				}else{
	//				TestlinkOperations.updateResultToTestLink(tcid.get(i), ExecutionStatus.FAILED, "Testcase Failed");
//					System.out.println(i+"  Fail");
					i++;
					failedCases++;
				}
				
			}
			catch(Exception e) {
				e.printStackTrace();
				break;
			}
			finally {
				reporter.endTest(test);
			}
		}
		Helper.buildReport(testtype, customer, projectname, releasename, osname, browsername, browserversion);
		reporter.close();
	}
	
	
	public static boolean executeTestSteps(int start, int end) throws Exception {
		boolean  testcaseflag= false;
		List<String> values = new ArrayList<String>();
		String methodName;
		try {
			boolean flag = false;
			for(;start<=end;start++) {
				values = iterateColumn(start);	
				Class<?> param[] = getParams(values);					
				methodName = sheet.getValueAt(3, start).toString();
				flag = executeMethod(start, methodName.toLowerCase(), param, values);
			
				if(!flag) {
					System.out.println("Retry");
					flag = executeMethod(start, methodName.toLowerCase(), param, values);	
					testcaseflag = true;
				}
				else {
					testcaseflag = true;
				}
				values.clear();
			}
		}
		finally {
			reporter.endTest(test);
			reporter.flush();
		}
		return testcaseflag;
	}
	
	public static boolean executeMethod(int start, String methodname, Class<?>[] parameter, List<String> val) {
		boolean stats = false;
		try {
			System.out.println(methodname);
			Class<?> TestStep =Class.forName("xcentral.ProofCentral");
			Constructor<?> cons = TestStep.getConstructor(Properties.class, String.class, ExtentTest.class, String.class, String.class, Properties.class, String.class, String.class);
			Object testStep = cons.newInstance(_elementProperties, reportScreenshotLocation, test, serverDirectory, serverUrl, _generalProperties, mountPath, browsername);
			Method method;
			if(methodname != null && (!methodname.isEmpty()) && methodname.length() > 0)  {
				method =TestStep.getDeclaredMethod(sheet.getValueAt(3, start).toString().trim().toLowerCase(), parameter);
				stats = (Boolean) method.invoke(testStep,val.toArray());
			}
		}
		catch(NoSuchMethodException me) {
			me.printStackTrace();
			test.log(LogStatus.FATAL, methodname+" Invalid");
		}
		catch(Exception e) {
			e.printStackTrace();
			test.log(LogStatus.ERROR , e.getMessage());
		}
		return stats;
	}
	
	public static Class<?>[] getParams(List<String> values) {
		Class<?> params[] =new Class[values.size()];
		for(int j=0;j<values.size();j++) {
			if(values.get(j) instanceof String) {
				params[j]=String.class;
			}
		}
		return params;
	}
	
	public static String getClassNameForMethods(String className,String methodName) throws ClassNotFoundException {
		boolean flag =false;
		Class<?> c = Class.forName(className);
		Method method[] = c.getDeclaredMethods();
		for(Method m: method) {
			if(methodName.equals(m.getName())) {
				flag = true;
			}
		}	
		if(!flag) {
			className = "xcentral.ProofCentral";
		}
		return className;
	}
}	