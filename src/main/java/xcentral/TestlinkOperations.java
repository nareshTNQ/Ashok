package xcentral;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseDetails;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.Platform;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import br.eti.kinoshita.testlinkjavaapi.model.TestSuite;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;

import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TestlinkOperations {
	Properties testLinkConfig;
	static Dataprovider dataprovider;
	static TestLinkAPI testlinkAPIClient;
	static String buildName;
	static String testCaseId;
	static String projectName;
	static String testType;
	static List<String> testcasesList=new ArrayList<String>();
	static Map<String,Integer> testCaseList=new LinkedHashMap<String, Integer>();

	public static void main(String args[]) {
		TestlinkOperations operations = new TestlinkOperations();
		dataprovider = new Dataprovider();
		buildName=args[1];
		projectName=args[4];
		testType=args[8];
		operations.dataSetter(projectName, testType, "");
		testlinkAPIClient = new TestLinkAPI(dataprovider.getURL(), dataprovider.DEV_KEY);
		operations.configTestlink("");
	}

	public void createBuild(String buildName) {
		try {
			Build newBuild;
			newBuild = testlinkAPIClient.createBuild(dataprovider.getTESTPLAN_ID(), buildName, "");
			dataprovider.setBUILD_ID(newBuild.getId());
			 dataprovider.setBUILD_NAME(newBuild.getName());
		} catch (TestLinkAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void getTestsuiteDetails() {
		try {
			TestSuite[] geTestSuite = testlinkAPIClient.getTestSuitesForTestPlan(dataprovider.getTESTPLAN_ID());
			for (TestSuite ts : geTestSuite) {
				System.out.println(ts.getId());
				System.out.println("Suite Details:" + ts.getName());
				if (ts.getName().contains("Sanity"))
				{
					System.out.println("TS id :" + ts.getId());
				dataprovider.setTEST_SUTIE_ID(ts.getId());
				}
				}
		} catch (TestLinkAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void getProjectDetails() {
		try {
			TestProject projectDetails = testlinkAPIClient.getTestProjectByName(dataprovider.getPROJECT_NAME());
			System.out.println("Project Details:" + projectDetails);
			System.out.println("Project Name"+projectDetails.getName());
			dataprovider.setPROJECT_ID(projectDetails.getId());
			dataprovider.setPROJECT_PREFIX(projectDetails.getPrefix() + "-");
		} catch (TestLinkAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void getTestplanDetails() {
		try {
			TestPlan planDetails = testlinkAPIClient.getTestPlanByName(dataprovider.getTESTPLAN_NAME(),
					dataprovider.getPROJECT_NAME());
			dataprovider.setTESTPLAN_ID(planDetails.getId());
			System.out.println(dataprovider.getTESTPLAN_ID());
			System.out.println("PLAN Details:" + planDetails);
		} catch (TestLinkAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void resultUpdate(int externalID, ExecutionStatus result, String notes) 
	{
		try {
			testlinkAPIClient.reportTCResult(dataprovider.getTESTCASE_ID(), externalID, dataprovider.getTESTPLAN_ID(),
					result, dataprovider.getBUILD_ID(), dataprovider.getBUILD_NAME(), notes, true, "", dataprovider.getPLATFORM_ID(), dataprovider.getPLATFORM_NAME(), null,
					true);
			/*testlinkAPIClient.reportTCResult(dataprovider.getTESTCASE_ID(), externalID, dataprovider.getTESTPLAN_ID(),
					result, dataprovider.getBUILD_ID(), dataprovider.getBUILD_NAME(), notes, true, "", null, null, null,
					true);*/
		} catch (TestLinkAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getTestCaseDetails()
	{
		try {
			TestCase[] getallTestcases = testlinkAPIClient.getTestCasesForTestSuite(dataprovider.getTEST_SUTIE_ID(), true,
					TestCaseDetails.FULL);

			for (TestCase t : getallTestcases) {
				testCaseList.put(t.getFullExternalId(), t.getId());
			}
		} catch (TestLinkAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Properties loadConfigFile() {
		testLinkConfig = new Properties();
		try {
			String systemPath = System.getProperty("user.dir");
			testLinkConfig.load(new FileReader(systemPath + "/Config/TestlinkConfig.Properties"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return testLinkConfig;

	}

	public void dataSetter(String projectName, String planName, String userName) {
		loadConfigFile();
		try {

			dataprovider.setDEV_KEY(testLinkConfig.getProperty("DEV.KEY"));
			dataprovider.setURL(new URL(testLinkConfig.getProperty("SERVER.URL")));
			
			projectSelection(projectName, planName);
			
			// dataprovider.setBUILD_NAME(buildName);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void getPlatFormDetails(String platform) 
	{
	try {
		Platform[] platForms=testlinkAPIClient.getTestPlanPlatforms(dataprovider.getTESTPLAN_ID());
		for(Platform pf:platForms) 
		{
			if(pf.getName().equalsIgnoreCase(platform))
			{
				dataprovider.setPLATFORM_ID(pf.getId());
				dataprovider.setPLATFORM_NAME(pf.getName());
			}
		}
	} catch (TestLinkAPIException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}
	public static void updateResultToTestLink(String testCaseId, ExecutionStatus result,String notes) 
	{
		try {
			if(testCaseList.containsKey((dataprovider.getPROJECT_PREFIX() + testCaseId)))
					{
				int tID=testCaseList.get(dataprovider.getPROJECT_PREFIX() + testCaseId);
						dataprovider.setTESTCASE_ID(tID);
						
						System.out.println(testlinkAPIClient.getTestCaseByExternalId(dataprovider.getPROJECT_PREFIX() + testCaseId, 1));
						
					}
			
			
			resultUpdate(Integer.parseInt(testCaseId), result, notes);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TestLinkAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public void pla() 
	{
		try {
			Platform[] a=testlinkAPIClient.getProjectPlatforms(dataprovider.getPROJECT_ID());
			for(Platform s:a) 
			{
				System.out.println(s.getName());
				
				
			}
		} catch (TestLinkAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public  void configTestlink(String platformName) 
	{
		try {
			getProjectDetails();
			getTestplanDetails();
			getTestsuiteDetails();
//	pla();
			createBuild(buildName);
			//getPlatFormDetails(platformName);
			getTestCaseDetails();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void projectSelection(String projectName,String planName)
	{
		try {
			if(projectName.equalsIgnoreCase("PGC")||projectName.equalsIgnoreCase("PC"))
			{
				dataprovider.setPROJECT_NAME(testLinkConfig.getProperty("PROJECT_PGC"));
				
				if(planName.contains("2.0"))
				{
					
					dataprovider.setTESTPLAN_NAME(testLinkConfig.getProperty("PGC.2.0.TESTPLAN"));	
				}else if(planName.contains("3.0"))
				{
					dataprovider.setTESTPLAN_NAME(testLinkConfig.getProperty("PGC.3.0.TESTPLAN"));
				}
				else if(planName.contains("3.1"))
				{
					dataprovider.setTESTPLAN_NAME(testLinkConfig.getProperty("PGC.3.1.TESTPLAN"));
				}
				
			}else if(projectName.equalsIgnoreCase("NIMBLE"))
			{
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
