package xcentral;

import java.util.ArrayList;
import java.util.List;

import org.jopendocument.dom.spreadsheet.Sheet;

public class ArrangeTestSet
{
	static Sheet sheet;
	
	/**
	 * Will Get All The Sheets
	 * @return List With Sheet Index Based On The 'TestSet' Given.
	 * @throws Exception
	 */
	public static List<Integer> getAllSheets() throws Exception {
		List<Integer> range = new ArrayList<Integer>();
		try {
			for(int i=2;i<Controller.sheets().getSheetCount();i++) {
				range.add(i);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return range;
	}
	
	public static List<Integer> getSheetByName(String sheetname) throws Exception {
		List<Integer> range = new ArrayList<Integer>();
		try {
			for(int i=2;i<Controller.sheets().getSheetCount();i++) {
				if(Controller.sheets().getSheet(i).getName().equalsIgnoreCase(sheetname)) {
					range.add(i);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return range;
	}
	
	
	public static List<Integer> getSheetsByName(String[] sheetname) throws Exception {
		List<Integer> range = new ArrayList<Integer>();
		try {
			for(int k=0;k<sheetname.length;k++) {
				for(int i=2;i<Controller.sheets().getSheetCount();i++) {
					if(Controller.sheets().getSheet(i).getName().equalsIgnoreCase(sheetname[k])) {
						range.add(i);
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return range;
	}
	
	
	public static List<Integer> removeEntries(List<Integer> mainlist, List<Integer> subList) {
		List<Integer> range = new ArrayList<Integer>();
		for(int x=0;x<mainlist.size();x++) {
			for(int y=0;y<subList.size();y++) {
				if(mainlist.get(x).equals(subList.get(y))) {
					range.remove(y);
				}
			}
		}
		return range;
	}
	
	public static List<Integer> getExcludedSheets(String toexclude) throws Exception {
		List<Integer> range = new ArrayList<Integer>();
		try {
			for(int i=2;i<Controller.sheets().getSheetCount();i++) {
				if(!Controller.sheets().getSheet(i).getName().equalsIgnoreCase(toexclude)) {
					range.add(i);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return range;
	}
	
	public static int returnsheetindex(String sheetname) {
		int index = 0;
		try {
			for(int i=2;i<Controller.sheets().getSheetCount();i++) {
				if(Controller.sheets().getSheet(i).getName().equalsIgnoreCase(sheetname)) {
					index = i;
				}
			}
			if(index==0) {
				throw new RuntimeException("Invalid Parameter ["+sheetname+"]: No Such Sheet");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return index;
	}
	
	
	public static List<Integer> getRangeOfSheets(String sheetname, int start) throws Exception {
		List<Integer> range = new ArrayList<Integer>();
		try {
			for(int i=start;i<Controller.sheets().getSheetCount();i++) {
				if(Controller.sheets().getSheet(i).getName().equalsIgnoreCase(sheetname)) {
					range.add(i);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return range;
	}
	
	
	public static List<Integer> serializeExecution(String from, String to) throws Exception {
		List<Integer> range = new ArrayList<Integer>();
		if(from.equalsIgnoreCase("ALL")&&(to==null))			/*Executes All The Sheets In Excel*/ {
			range = getAllSheets();
		}
		else if(!from.equalsIgnoreCase("all")&&(!from.contains(","))&&(!from.contains("="))&&(to==null))	/*Executes Specified Single Sheet*/ {
			range = getSheetByName(from);
		}
		else if(!from.equalsIgnoreCase("all")&&(to!=null))		/*Executes Specified Range Of Sheets*/ {
			int start = returnsheetindex(from);
			int stop = returnsheetindex(to);
			for(;start<=stop;start++) {
				range.add(start);
			}
		}
		else if(from.contains(",")&&(!from.contains("-exclude="))&&(to==null))		/*Executes Selected Sheets In Excel*/ {
			range = getSheetsByName(from.trim().split(","));
		}
		return range;
	}
}
