package xcentral;
import java.net.URL;
public class Dataprovider 
{
	
	public URL URL;
	public String DEV_KEY;
	public String PROJECT_NAME;
	public int PROJECT_ID;
	public String TESTPLAN_NAME;
	public int TESTPLAN_ID;
	public String BUILD_NAME;
	public int BUILD_ID;
	public int PLATFORM_ID;
	public String PLATFORM_NAME;
	public int TESTCASE_ID;
	public String TESTCASE_NAME;
	public String FULLEXTERNAL_ID; 
	
	public int getPLATFORM_ID() {
		return PLATFORM_ID;
	}
	public void setPLATFORM_ID(int pLATFORM_ID) {
		PLATFORM_ID = pLATFORM_ID;
	}
	public String getPLATFORM_NAME() {
		return PLATFORM_NAME;
	}
	public void setPLATFORM_NAME(String pLATFORM_NAME) {
		PLATFORM_NAME = pLATFORM_NAME;
	}
	public int getTESTCASE_ID() {
		return TESTCASE_ID;
	}
	public void setTESTCASE_ID(int tESTCASE_ID) {
		TESTCASE_ID = tESTCASE_ID;
	}
	public String getTESTCASE_NAME() {
		return TESTCASE_NAME;
	}
	public void setTESTCASE_NAME(String tESTCASE_NAME) {
		TESTCASE_NAME = tESTCASE_NAME;
	}
	public String getFULLEXTERNAL_ID() {
		return FULLEXTERNAL_ID;
	}
	public void setFULLEXTERNAL_ID(String fULLEXTERNAL_ID) {
		FULLEXTERNAL_ID = fULLEXTERNAL_ID;
	}
	public String PROJECT_PREFIX;
	public int TEST_SUTIE_ID;
	public int getTEST_SUTIE_ID() {
		return TEST_SUTIE_ID;
	}
	public void setTEST_SUTIE_ID(int tEST_SUTIE_ID) {
		TEST_SUTIE_ID = tEST_SUTIE_ID;
	}
	public String getPROJECT_PREFIX() {
		return PROJECT_PREFIX;
	}
	public void setPROJECT_PREFIX(String pROJECT_PREFIX) {
		PROJECT_PREFIX = pROJECT_PREFIX;
	}
	public URL getURL() {
		return URL;
	}
	public void setURL(URL uRL) {
		URL = uRL;
	}
	public String getDEV_KEY() {
		return DEV_KEY;
	}
	public void setDEV_KEY(String dEV_KEY) {
		DEV_KEY = dEV_KEY;
	}
	public String getPROJECT_NAME() {
		return PROJECT_NAME;
	}
	public void setPROJECT_NAME(String pROJECT_NAME) {
		PROJECT_NAME = pROJECT_NAME;
	}
	public int getPROJECT_ID() {
		return PROJECT_ID;
	}
	public void setPROJECT_ID(int pROJECT_ID) {
		PROJECT_ID = pROJECT_ID;
	}
	public String getTESTPLAN_NAME() {
		return TESTPLAN_NAME;
	}
	public void setTESTPLAN_NAME(String tESTPLAN_NAME) {
		TESTPLAN_NAME = tESTPLAN_NAME;
	}
	public int getTESTPLAN_ID() {
		return TESTPLAN_ID;
	}
	public void setTESTPLAN_ID(int tESTPLAN_ID) {
		TESTPLAN_ID = tESTPLAN_ID;
	}
	public String getBUILD_NAME() {
		return BUILD_NAME;
	}
	public void setBUILD_NAME(String bUILD_NAME) {
		BUILD_NAME = bUILD_NAME;
	}
	public int getBUILD_ID() {
		return BUILD_ID;
	}
	public void setBUILD_ID(int bUILD_ID) {
		BUILD_ID = bUILD_ID;
	}

}
