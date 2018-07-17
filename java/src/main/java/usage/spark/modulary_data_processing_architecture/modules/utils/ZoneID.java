package usage.spark.modulary_data_processing_architecture.modules.utils;

import scala.Serializable;
import scala.Tuple2;

public class ZoneID implements Serializable{
	Tuple2<Integer, Integer> rowCol;
	
	public ZoneID(int row, int col){
		rowCol = new Tuple2<Integer,Integer>(row,col);
	}
	
	public String getID(){
		return rowCol._2+"-"+rowCol._1;
	}
	
	@Override
	public boolean equals(Object object){
		boolean test = true;
		if(!object.getClass().equals(ZoneID.class))
			test = false;
		
		ZoneID zoneId = (ZoneID)object;
		
		if(!zoneId.rowCol.equals(this.rowCol))
			test = false;
			
		return test;
	}

}
