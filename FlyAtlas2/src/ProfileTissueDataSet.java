// This models all the GenExCorrelation objects EXCEPT A SINGLE QUERY GenExCorrelation (formerly ProbesetList)
// It populates an array of GenExCorrelation objects (for subsequent setting of statistics param) which it creates from ProfileDatum objects from a Qy
// Used with ProfileSearch
// 05.09.2012
// Adapted for BeetleAtlas 24.02.2021
// Modified for FlyAtlas 2 28.03.2021
// Last update 27.09.2021

import java.sql.*;

public class ProfileTissueDataSet 
{
	private ProfileTissueData[] dataList;
	private int dataListSize = 0;
	private int LIST_LENGTH = 20000;			// Needs to be large enough to hold all the genes
	
	public ProfileTissueDataSet(String queryFBgn, String profTiss)
	{	
		dataList = new ProfileTissueData [LIST_LENGTH];
		populateList(queryFBgn, profTiss);
	}
	
	private void populateList(String queryFBgn, String profTiss)
	{
		String query = new String();	// FBgn, FPKM, Status, TissueID for ALL genes
		if(profTiss.equals("AL"))
		{
			query = DBQuery.getProfileQueryAL();	// Adult and Larval profile
		}
		else if(profTiss.equals("LO"))
		{
			query = DBQuery.getProfileQueryLO();	// Larval only profile
		}
		else if(profTiss.equals("AO"))
		{
			query = DBQuery.getProfileQueryAO();	// Adult only profile
		}
		else if(profTiss.equals("MA"))
		{
			query = DBQuery.getProfileQueryMA();	// Male Adult profile
		}
		else if(profTiss.equals("FA"))
		{
			query = DBQuery.getProfileQueryFA();	// Female Adult profile
		}
		else if(profTiss.equals("AT"))
		{
			query = DBQuery.getProfileQueryAT();	// Alimentary tract
		}
		
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		try 
		{
			Statement stmt = conn.createStatement();
			ResultSet resSet = stmt.executeQuery(query);
			
			ProfileTissueData data = null;		// sets up GeneExCorrelation to which FPKM objects will be added for ea tissue/stage

			// Variable external to loop to allow detections of a new group of ProbeSetIDs from experiment table
			String fbgn = "";			
			
			boolean atStart = true;	// to mark the situation when the ResultSet is first read
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					if(!resSet.getString(1).equals(queryFBgn))					// if it's not the query data set
					{
						// checks if new set by by initial value of atStart or change in value of FBgn (String 1)
						if(atStart || !resSet.getString(1).equals(fbgn))
						{
							// store any previously built GeneExCorrelation from the last 25 (or whatever) loops
							if(!atStart)
							{
								dataList[dataListSize - 1] = data;
							}												
							// read this first FBgn and use it to construct the new GeneExCorrelation
							fbgn = resSet.getString("FBgn");						
							data = new ProfileTissueData(fbgn);
							// read other variables and use to construct Abundance object which is added to the GeneExCorrelation object
							double fpkm = resSet.getDouble("FPKM");
							String status = resSet.getString("Status");
							int tissueID = resSet.getInt("TissueID");
							
							ProfileDatum datum = new ProfileDatum(fbgn, fpkm, status, tissueID);
							data.addDatum(datum);
							
							atStart = false;
							dataListSize++;
						}
						else
						{	
							// read variables needed to construct Abundance object to add to the GeneExCorrelation object
							double fpkm = resSet.getDouble("FPKM");
							String status = resSet.getString("Status");
							int tissueID = resSet.getInt("TissueID");
							
							ProfileDatum datum = new ProfileDatum(fbgn, fpkm, status, tissueID);
							data.addDatum(datum);
						}
					}
				}
				// this adds final GeneExCorrelation object to the list after exiting while loop 
				if(dataListSize > 0)
				{
					dataList[dataListSize-1] = data;	
				}
			}
			resSet.close(); // ! before conn.close()
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		finally // close the connection
		{
			if(conn != null)
			{
				try { conn.close();}
				catch(Exception e){System.out.println("Can't close.");}
			}
		}
	}
	
	// getters	
	
	public ProfileTissueData[] getList()
	{
		return dataList;
	}
	
	public int getListSize()
	{
		return dataListSize;	// should be one less than the total number of genes in the database
	}


}
