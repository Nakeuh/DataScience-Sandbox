package usage.spark.modulary_data_processing_architecture.modules.utils;

import twitter4j.GeoLocation;

public class ZoneUtils implements Constantes{

	/**
	 * Check if a location is in zone
	 * @param pos
	 * @param northWestLimit
	 * @param southEastLimit
	 * @return
	 */
	public static boolean isInZone(GeoLocation pos, GeoLocation northWestLimit, GeoLocation southEastLimit) {
		boolean test = true;
		test &= pos.getLongitude() >= northWestLimit.getLongitude();
		test &= pos.getLatitude() <= northWestLimit.getLatitude();
		test &= pos.getLongitude() <= southEastLimit.getLongitude();
		test &= pos.getLatitude() >= southEastLimit.getLatitude();
		return test;
	}

	/**
	 * 
	 * @param tweet
	 * @return
	 */
	public static boolean isInParis(GeoLocation pos) {
		boolean test = true;
		test &= pos != null;
		test &= isInZone(pos, PARIS_northWestLimit, PARIS_southEastLimit);
		return test;
	}
	
	public static ZoneID getZoneID(GeoLocation pos){
		
		double widthGlobalZone = PARIS_southEastLimit.getLongitude() - PARIS_northWestLimit.getLongitude();
		double heightGlobalZone = PARIS_northWestLimit.getLatitude() - PARIS_southEastLimit.getLatitude();
		double widthCase = widthGlobalZone / NB_CASE_PER_LINE;
		double heightCase = heightGlobalZone / NB_CASE_PER_LINE;
		
		int col = (int) ((pos.getLatitude() - PARIS_southEastLimit.getLatitude()) / widthCase);
		int row = (int) ((pos.getLongitude() - PARIS_northWestLimit.getLongitude()) / heightCase);

		return new ZoneID(row,col);
	}
}
