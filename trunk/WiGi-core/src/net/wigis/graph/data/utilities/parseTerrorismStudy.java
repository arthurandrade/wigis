package net.wigis.graph.data.utilities;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;



public class parseTerrorismStudy {
	public static void main(String[] args) throws Exception{
		InputStream inp = new FileInputStream("/Users/scarlettteng/Dropbox/terrorisim_study/IrelandNorthernIrelandGB.xlsx");
	    //InputStream inp = new FileInputStream("/Users/scarlettteng/Dropbox/terrorisim_study/sample.xlsx");
	    Workbook wb = WorkbookFactory.create(inp);
	    Sheet sheet = wb.getSheetAt(0);
	    
	    DNVGraph graph = new DNVGraph();
	    HashMap<String, String> countryHash = new HashMap<String,String>();
	    HashMap<String, String> attacktypeHash = new HashMap<String,String>();
	    HashMap<String, String> targettypeHash = new HashMap<String,String>();
	    HashMap<String, String> propertyLossHash = new HashMap<String,String>();
	    
	    HashSet<String> countrySet = new HashSet<String>();
	    String minTime = null;
	    String maxTime = null;
	    for(int rowCnt = 2; rowCnt < sheet.getPhysicalNumberOfRows(); rowCnt++){
	    	Row row = sheet.getRow(rowCnt);
	    	DNVNode node = new DNVNode(graph);
	    	//time
	    	Cell timeCell = row.getCell(0);
	    	timeCell.setCellType(Cell.CELL_TYPE_STRING);
	    	String timeString = timeCell.getStringCellValue().substring(0, 8);
	    	node.setProperty("time", timeString);
	    	
	    	if(rowCnt == 2){
	    		minTime = timeString;
	    		maxTime = timeString;
	    	}else{
	    		if(minTime.compareTo(timeString) > 0){
	    			minTime = timeString;
	    		}
	    		if(maxTime.compareTo(timeString) < 0){
	    			maxTime = timeString;
	    		}
	    	}
	    	
	    	
	    	//country
	    	Cell countryCell = row.getCell(7);
	    	countryCell.setCellType(Cell.CELL_TYPE_STRING);
	    	countrySet.add(row.getCell(8).getStringCellValue());
	    	node.setProperty("country", row.getCell(8).getStringCellValue());
	    	
	    	//city
	    	Cell cityCell = row.getCell(12);
	    	cityCell.setCellType(Cell.CELL_TYPE_STRING);
	    	node.setProperty("city", cityCell.getStringCellValue());
	    	node.setLabel(cityCell.getStringCellValue() + " " + timeString);
	    	//System.out.println(cityCell.getStringCellValue());
	    	
	    	//whether the attack is successful or not
	    	Cell successCell = row.getCell(24);
	    	if(successCell.getNumericCellValue() == 1)
	    		node.setProperty("success","successful");
	    	else{
	    		node.setProperty("success","failed");
	    	}
	    	
	    	//attack type, up to three attack types
	    	Cell attacktype1Cell = row.getCell(26);
	    	attacktype1Cell.setCellType(Cell.CELL_TYPE_STRING);
	    	String attacktype1 = attacktype1Cell.getStringCellValue();
	    	attacktypeHash.put(attacktype1, row.getCell(27).getStringCellValue());
	    	String attacktype = row.getCell(27).getStringCellValue();//attacktype1;
	    	
	    	Cell attacktype2Cell = row.getCell(28);
	    	if(attacktype2Cell != null){
		    	attacktype2Cell.setCellType(Cell.CELL_TYPE_STRING);
		    	attacktype += "\t" + attacktypeHash.get(attacktype2Cell.getStringCellValue());//attacktype2Cell.getStringCellValue();
	    	}

	    	Cell attacktype3Cell = row.getCell(30);
	    	if(attacktype3Cell != null){
		    	attacktype3Cell.setCellType(Cell.CELL_TYPE_STRING);
		    	attacktype += "\t" + attacktypeHash.get(attacktype3Cell.getStringCellValue());//attacktype3Cell.getStringCellValue();
	    	}
	    	node.setProperty("attacktype", attacktype);
	    	//System.out.println("attacktype " + attacktype);
	    	
	    	//target type, up to three target types
	    	Cell targettype1Cell = row.getCell(32);
	    	targettype1Cell.setCellType(Cell.CELL_TYPE_STRING);
	    	String targettype1 = targettype1Cell.getStringCellValue();
	    	targettypeHash.put(targettype1, row.getCell(33).getStringCellValue());
	    	String targettype = row.getCell(33).getStringCellValue();//targettype1;	    	
	    	
	    	Cell targettype2Cell = row.getCell(38);
	    	if(targettype2Cell != null){
	    		targettype2Cell.setCellType(Cell.CELL_TYPE_STRING);
	    		targettype += "\t" + targettypeHash.get(targettype2Cell.getStringCellValue());
	    	}
	    	
	    	Cell targettype3Cell = row.getCell(44);
	    	if(targettype3Cell != null){
	    		targettype3Cell.setCellType(Cell.CELL_TYPE_STRING);
	    		targettype += "\t" + targettypeHash.get(targettype3Cell.getStringCellValue());
	    	}
	    	node.setProperty("targettype", targettype);
	    	//System.out.println("targettype " + targettype);
	    	
	    	//number of victims
	    	int numberVictims = -10;
	    	Cell numberKilledCell = row.getCell(92);
	    	if(numberKilledCell != null && numberKilledCell.getNumericCellValue() > 0){
	    		numberVictims = (int) numberKilledCell.getNumericCellValue();
	    	}
	    	Cell numberWoundedCell = row.getCell(95);
	    	if(numberWoundedCell != null && numberWoundedCell.getNumericCellValue() > 0){
	    		if(numberVictims < 0)
	    			numberVictims = (int) numberWoundedCell.getNumericCellValue();
	    		else
	    			numberVictims += (int) numberWoundedCell.getNumericCellValue();
	    	}
	    	if(numberVictims < 0){
	    		node.setRadius(3);
	    		node.setProperty("numberOfVictims", "unknown");
	    	}else{
	    		node.setRadius((float) (3 + Math.log10(numberVictims)));
	    		node.setProperty("numberOfVictims", String.valueOf(numberVictims));
	    	}//System.out.println("number of victims " + numberVictims);
	    	
	    	//property loss
	    	Cell propertyLoss = row.getCell(99);
	    	if(propertyLoss != null){
	    		propertyLoss.setCellType(Cell.CELL_TYPE_STRING);
	    		node.setProperty("propertyLoss", row.getCell(100).getStringCellValue());
	    		propertyLossHash.put(propertyLoss.getStringCellValue(), row.getCell(100).getStringCellValue());
	    		//System.out.println("property loss " + row.getCell(100).getStringCellValue());
	    	}
	    	//System.out.println();
	    	graph.addNode(0, node);
	    }
	    String countryHashString = "";
	    for(String key : countrySet){
	    	countryHashString += key + "\t";
	    }
	    System.out.println();
	    String attacktypeHashString = "";
	    for(String key : attacktypeHash.keySet()){
	    	attacktypeHashString += attacktypeHash.get(key) + "\t";
	    }
	    System.out.println();
	    String targettypeHashString = "";
	    for(String key : targettypeHash.keySet()){
	    	targettypeHashString += targettypeHash.get(key) + "\t";
	    }
	    System.out.println();
	    String propertyLossHashString = "";
	    for(String key : propertyLossHash.keySet()){
	    	propertyLossHashString += propertyLossHash.get(key) + "\t";
	    }
	    Random generator = new Random();
		for(DNVNode node : graph.getNodes(0)){
			node.setPosition(generator.nextFloat() * 2 - 1, generator.nextFloat() * 2 - 1);
		}
	    String propertyList = "country" + "\t" + "success" + "\t" + "attacktype" + "\t" + "targettype" + "\t" + "propertyLoss";
	    graph.setProperty("propertyList", propertyList);
	    graph.setProperty("country", countryHashString);
	    graph.setProperty("success", "successful"+"\t"+"failed");
	    graph.setProperty("attacktype", attacktypeHashString);
	    graph.setProperty("targettype", targettypeHashString);
	    graph.setProperty("properLoss", propertyLossHashString);
	    
	    graph.setProperty("minTime", minTime);
	    graph.setProperty("maxTime", maxTime);
	    
	    System.out.println("minTime  " + minTime + "  maxTime  " + maxTime);
	    graph.writeGraph("/Users/scarlettteng/dev/graphs/" + "terrorism.dnv");
	}
}
