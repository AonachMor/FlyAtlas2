/*
	TissueCatalogue
	(was TissueLists)
	Class for maintaining an array of FlyTissue objects, with associated accessor methods
	And array of TissueTriplets for laying out tables with M, F, L for same tissue
	Called from Servlet class immediately on invoking Servlet
	DPL 03.07.2016
*/

import java.sql.*;
import java.io.*;
import java.util.Arrays;	// for Arrays.sort

public class TissueCatalogue
{
	private Tissue[] tissues;				// array of Tissue objects
	private int TISSUE_LIST_LEN = 100;		// length of array (actually only needs 25, so should be ok for a while)
	private int tissueListSize = 0;			// occupancy
	
	private TissueTriplet[] triplets;		// array of TissueTriplet objects
	private int TRIP_LIST_LEN = 100;		// length of array (actually only needs 25, so should be ok for a while)
	private int tripletListSize = 0;		// occupancy — but also specifies FlyStagePair 'listPosition' 
	
	private final String TRIPLET_FILE = "files/uniTissues.txt";	// file that lists displayOrder/tab/unifying names for adult and larval tissues
	private TripletDisplay[] tripletDisplays;					// array from TRIPLET_FILE
	private int tripletDisplaysSize = 0;						// occupancy
	
	// constructor calls methods to creates arrays of Tissue and TissueTriplet objects
	public TissueCatalogue()
	{
		// Parse text file and create objects of unitissueID and table display position - store in array
		tripletDisplays = new TripletDisplay[TRIP_LIST_LEN];	
		populateTripletDisplayList();
		
		// Construct FlyTissue and FlyStagePair arrays
		tissues = new Tissue[TISSUE_LIST_LEN];
		triplets = new TissueTriplet[TRIP_LIST_LEN];
		populateLists();
		
		// sort flyPairs (ensures FlyStagePair[] is in Display Order for Gene table)
		TissueTripletComparator fspComparator = new TissueTripletComparator();
		Arrays.sort(triplets, 0, tripletListSize, fspComparator);
		
		// sort flyTissues (ensures lyTissue[] is sorted in order for Transcript table)
		sortTissues();
	}
	
	// Make SQL query to DB to populate ordered list of FlyTissue objects and then call method to make FlyTissuePairs
	private void populateLists()
	{		
		String query = DBQuery.getFlyTissueQuery();
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		try 
		{		
			Statement stmt = conn.createStatement();
			ResultSet resSet = stmt.executeQuery(query);
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					int tissueID = resSet.getInt("TissueID");
					String stage = resSet.getString("Stage");
					String sex = resSet.getString("Sex");
					String tissueName = resSet.getString("TissueName");
					String abbreviation = resSet.getString("Abbreviation");
					int uniTissueID = resSet.getInt("UniTissueID");
					String referenceString = resSet.getString("Reference");
							
					boolean reference = false;
					if(referenceString.equals("Yes"))
					{
						reference = true;
					}

					Tissue next = new Tissue(tissueID, stage, sex, tissueName, abbreviation, uniTissueID, reference);
					tissues[tissueListSize] = next;
					tissueListSize++;
				}
			}
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
		
		// Generate FlyStagePairs (only possible after population of FlyTissue list)	
		populateTripletList(); 
	}
	
	// Creates FlyStagePairs by taking each PairDisplay and using UniTisssue to find component M, F and L tissue objects
	private void populateTripletList()
	{
		for(int i=0; i<tripletDisplaysSize; i++)
		{
			int uniTissueID = tripletDisplays[i].getUniTissueID();
			int displayPos = tripletDisplays[i].getDisplayPos();
			String uniTissueName = tripletDisplays[i].getUniTissueName();
			Tissue maleTiss = getTissueByUniID(uniTissueID, "Male");
			Tissue femaleTiss = getTissueByUniID(uniTissueID, "Female");
			Tissue larvalTiss = getTissueByUniID(uniTissueID, "Both");			
			TissueTriplet triplet = new TissueTriplet(uniTissueID, maleTiss, femaleTiss, larvalTiss, displayPos, uniTissueName);			
			triplets[tripletListSize] = triplet;
			tripletListSize++;  
				// System.out.println(pair.toString());		// for debugging
		}
	}
	
	// Gets count of tissues of each 'sex', which must be "Male", "Female" or "Both" (Excludes Whole Body)
	public int getTissueCountBySex(String sex)
	{
		int count=0;
        for(int i=0; i<tissueListSize; i++)
        {
        	if(tissues[i].getSex().equals(sex) && !tissues[i].isReference())
        	{
        		count++;
        	}
        }
        return count;
	}
	
    // Sorts FlyTissue[] flyTissues first by sex (descending order: Both, Female, Male), then by FlyStagePair display order
    private void sortTissues()
    {
    	Tissue lowest;		// holder for Peak with lowest value
		int lowestPos;		
		for (int i=0; i<tissueListSize-1; i++)		// do series of runs
		{
			for (int j=i+1; j<tissueListSize; j++)	// for each run process list
			{
				lowest = tissues[i];			// first of unsorted assigned to lowest
				lowestPos = i;
				
				if(lowest.getSex().compareTo(tissues[j].getSex()) < 0)
				{
					lowestPos = j;
				}
				else if(lowest.getSex().compareTo(tissues[j].getSex()) == 0)
				{
					int displayPosLowest = getTissueTripletByTissueID(lowest.getTissueID()).getDisplayPosition();
					int displayPosJ = getTissueTripletByTissueID(tissues[j].getTissueID()).getDisplayPosition();
					if(displayPosJ < displayPosLowest)
					{
						lowestPos = j;
					}
				}
				
				lowest = tissues[lowestPos];
				tissues[lowestPos] = tissues[i]; 		// shift current first
				tissues[i] = lowest;					// replace
			}
		}
    }
	
    						/* Accessor/Search methods for FLY TISSUE list */
	
    // number of distinct Tissue objects (i.e. occupancy of array)
    public int getTissueListSize()
    {
    	return tissueListSize;
    }
    
    // returns Tissue object at a given position in the array
    public Tissue getTissue(int pos)
    {
    	return tissues[pos];
    }  
    
    // allows stage description to be retrieved for an id 
    public String getStageByID(int id)
    {
        for(int i=0; i<tissueListSize; i++)
        {
            int flyID = tissues[i].getTissueID();
            if(id == flyID)
            {
            	return tissues[i].getStage();
            }
        }
        return "none";	// back-stop that won't throw an npe
    }
    
    // allows determination if tissue is reference for an id 
    public boolean getRefStatusByID(int id)
    {
        for(int i=0; i<tissueListSize; i++)
        {
            int flyID = tissues[i].getTissueID();
            if(id == flyID)
            {
            	return tissues[i].isReference();
            }
        }
        return false;		// back-stop that won't throw an npe
    }
    
    // allows sex to be retrieved for an id
    public String getSexByID(int id)
    {
        for(int i=0; i<tissueListSize; i++)
        {
            int flyID = tissues[i].getTissueID();
            if(id == flyID)
            {
            	return tissues[i].getSex();
            }
        }
        return "none";		// back-stop that won't throw an npe
    }
    
    // allows tissue name to be retrieved for an id
    public String getTissueNameByID(int id)
    {
        for(int i=0; i<tissueListSize; i++)
        {
            int flyID = tissues[i].getTissueID();
            if(id == flyID)
            {
            	return tissues[i].getTissueName();
            }
        }
        return "none";	// back-stop that won't throw an npe
    }
  
    // allows UniTissue name to be retrieved for an id
    public int getUniTissueByID(int id)
    {
        for(int i=0; i<tissueListSize; i++)
        {
            int flyID = tissues[i].getTissueID();
            if(id == flyID)
            {
            	return tissues[i].getUniTissueID();
            }
        }
        return -1;	// back-stop that won't throw an npe
    }
    
	   // returns Tissue object corresponding to a uniTissueID and stage
	 private Tissue getTissueByUniID(int uniTissueID, String sex)
	 {
	     for(int i=0; i<tissueListSize; i++)
	     {
	         int utID = tissues[i].getUniTissueID();
	         String sx = tissues[i].getSex();
	         if(utID == uniTissueID && sx.equals(sex))
	         {
	         	return tissues[i];
	         }
	     } 
	     return null;
	 }
 
    
				/* Accessor/Search methods for FLY STAGE TISSUE list */
	
    // number of triplets (i.e. occupancy of array - gives number of lines in table)
    public int getTripletListSize()
    {
    	return tripletListSize;
    }
    
    // returns TissueTriplet object at a given position in the array
    public TissueTriplet getTissueTriplet(int pos)
    {
    	return triplets[pos];
    }
    
    // returns a FlyTissue object if either adult or larval component ID matches search ID
    public TissueTriplet getTissueTripletByTissueID(int id)
    {
    	 for(int i=0; i<tripletListSize; i++)
    	{	
    		if(triplets[i].getMaleTissue() != null &&
    				triplets[i].getMaleTissue().getTissueID() == id)
    		{
    			return triplets[i];
    		}
    		if(triplets[i].getFemaleTissue() != null &&
    				triplets[i].getFemaleTissue().getTissueID() == id)
    		{
    			return triplets[i];
    		}
    		else if(triplets[i].getLarvalTissue() != null &&
    				triplets[i].getLarvalTissue().getTissueID() == id)
    		{
    			return triplets[i];
    		}
    	}
    	return null;
    }
    
    								// INNER CLASS //
 	
 	// For storing a line from uniTissues.txt and lookup Name
 	class TripletDisplay
 	{
 		int displayPos;		// order of tissue for display (e.g. in table)
 		int uniTissueID;	// uniTissueID
 		String uniTissueName;	// uniTissueID
 		
 		TripletDisplay(int displayPos, int uniTissueID, String uniTissueName)
 		{
 			this.displayPos = displayPos;
 			this.uniTissueID = uniTissueID;
 			this.uniTissueName = uniTissueName;
 		}
 		public int getDisplayPos()
 		{
 			return displayPos;
 		}
 		public int getUniTissueID()
 		{
 			return uniTissueID;
 		}
 		public String getUniTissueName()
 		{
 			return uniTissueName;
 		}
 	}
 	
		// METHODS FOR PARSING TEXT FILES WITH DISPLAY POSITIONS TO POPULATE ARRAYS INNER CLASS DISPLAY OBJECTS //
 	
	// Loads uniTissues.txt into TripletDisplay array
	private void populateTripletDisplayList()
	{
		String unitissueQy = "UNITISSUENAME_FROM_ID";		// Query to get UniTissue Name from ID
		
		StreamFile sf = new StreamFile(TRIPLET_FILE, true);
		StreamTokenizer st = sf.getStream();
		st.slashSlashComments(true);		// May use Java-style (//) comments
		st.commentChar('%');				// 'Official' comment char is '%'
		st.wordChars(' ', ' ');				// Do not regard ' ' as delimiter
		
		boolean goOn = true; 
		try
		{
			while (goOn)
			{
				int displayPos = 0;
				int uniTissID = 0;
				String uniTissName = new String();
				
				int tok = st.nextToken();
				if (tok != StreamTokenizer.TT_EOF) //check at start of 'line'
				{
					if (tok == StreamTokenizer.TT_NUMBER)
					{
						displayPos = (int) st.nval;
						//System.out.println("displayPos: " + displayPos);
					}
					else
					{System.out.println("Expected displayPos");}
		
					tok = st.nextToken();					
					if (tok == StreamTokenizer.TT_NUMBER)
					{
						uniTissID = (int) st.nval;
						//System.out.println("uniTissID: " + uniTissID);
					}
					else
					{System.out.println("Expected uniTiss");}
					
					// Now get UniTissueName by SQL unitissueQy using UniTissueID
					Connect cnt = new Connect();
					Connection conn = cnt.getConnection();
					ParamQuery parQy = DBQuery.getParamQuery(unitissueQy);
					try 
					{
						parQy.setPrepStatement(conn);
					} 
					catch (SQLException e) {System.out.println("SQL Exception 1: " + e.toString());}	
					try 
					{
						PreparedStatement prepStat = parQy.getPrepStatement();
						prepStat.setInt(1, uniTissID);
						ResultSet resSet = prepStat.executeQuery();
						if(resSet.first())	// move to single tuple
						{
							uniTissName = resSet.getString("UniTissueName");	
						}
					}
					catch (SQLException e)
					{
						System.out.println("SQL Exception 2: " + e.toString());
					}
					finally // close the connection
					{
						if(cnt != null)
						{
							try { conn.close();}
							catch(Exception e){System.out.println("Can't close.");}
						}
					}
		    				
					TripletDisplay display = new TripletDisplay(displayPos, uniTissID, uniTissName);
					tripletDisplays[tripletDisplaysSize] = display;
					tripletDisplaysSize++;  
				}
				else
				{
					goOn = false;
				}
			}
		}
		catch(IOException ioe)
		{
			System.out.println("Problem reading " + TRIPLET_FILE);
		}
	}
 	
}
