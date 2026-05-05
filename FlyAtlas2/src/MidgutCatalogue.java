/*
	MidgutCatalogue (was TissueLists)
	Class for maintaining an array of FlyTissue objects, with associated accessor methods
	And list of adult/larval Pairs for laying out tables
	Called from Servlet class immediately on invoking Servlet
	DPL 08.12.2015
*/

import java.sql.*;
import java.io.*;
import java.util.Arrays;	// for Arrays.sort

public class MidgutCatalogue
{
	private FlyTissue[] flyTissues;			// array of FlyTissue objects
	private int LIST_LENGTH = 100;			// length of array (actually only needs 25, so should be ok for a while)
	private int listSize = 0;				// occupancy
	
	private final String TISSUE_FILE = "files/tissuesMG.txt";	// file with displayOrder/tab/tissueID 
	private TissueDisplay[] tissueDisplays;					// array from TISSUE_FILE
	private int tissueDisplaysSize = 0;						// occupancy
	
	private FlyStagePair[] flyPairs;		// array of FlyStagePair objects
	private int PAIR_LIST_LEN = 100;		// length of array (actually only needs 25, so should be ok for a while)
	private int pairListSize = 0;			// occupancy — but also specifies FlyStagePair 'listPosition' 
	
	private final String PAIR_FILE = "files/uniTissuesMG.txt";	// file that lists displayOrder/tab/unifying names for adult and larval tissues
	private PairDisplay[] pairDisplays;							// array from PAIR_FILE
	private int pairDisplaysSize = 0;							// occupancy
	
	// constructor calls methods to creates arrays of FlyTissue and FlyTissuePair objects
	public MidgutCatalogue()
	{
		// Parse text files and place display positions of tissue and pairs in arrays
		tissueDisplays = new TissueDisplay[LIST_LENGTH];
		pairDisplays = new PairDisplay[PAIR_LIST_LEN];	
		populateTissueDisplayList();
		populatePairDisplayList();
		
		// Construct FlyTissue and FlyStagePair arrays
		flyTissues = new FlyTissue[LIST_LENGTH];
		flyPairs = new FlyStagePair[PAIR_LIST_LEN];
		populateLists();
		
		// sort lists (may not be necessary currently, but ensures lists are in Display Order)
		FlyTissueComparator ftComparator = new FlyTissueComparator();
		Arrays.sort(flyTissues, 0,  listSize, ftComparator);
		FlyStagePairComparator fspComparator = new FlyStagePairComparator();
		Arrays.sort(flyPairs, 0, pairListSize, fspComparator);
	}
	
	// returns TissueDisplay displayPos corresponding to tissueID
	 private int getTissueDisplayPos(int tissueID)
	 {
	     for(int i=0; i<tissueDisplaysSize; i++)
	     {
	         if(tissueID == tissueDisplays[i].getTissueID())
	         {
	         	return tissueDisplays[i].getDisplayPos();
	         }
	     } 
	     return -1;
	 }
	
	// Make SQL query to DB to populate ordered list of FlyTissue objects and then call method to make FlyTissuePairs
	private void populateLists()
	{		
		String query = DBQueryMG.getFlyTissueQuery();
		ConnectMG cnt = new ConnectMG();
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
					String uniTissue = resSet.getString("UniTissue");
					
					int displayPos = getTissueDisplayPos(tissueID);

					FlyTissue next = new FlyTissue(tissueID, stage, sex, tissueName, uniTissue, displayPos);
					flyTissues[listSize] = next;
					listSize++;
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
		
		// Generation of FlyStagePairs (only possible after population of FlyTissue list)	
		populatePairList(); 
	}
	
	private void populatePairList()
	{
		for(int i=0; i<pairDisplaysSize; i++)
		{
			String uniTissue = pairDisplays[i].getUniTissue();
			int displayPos = pairDisplays[i].getDisplayPos();
			FlyTissue adultTiss = getFlyTissueByUni(uniTissue, "Adult");
			FlyTissue larvalTiss = getFlyTissueByUni(uniTissue, "Larval");			
			FlyStagePair pair = new FlyStagePair(uniTissue, adultTiss, larvalTiss, displayPos);			
			flyPairs[pairListSize] = pair;
			pairListSize++;  
			// System.out.println(pair.toString());
		}
	}
	
	   // returns FlyTissue object corresponding to a uniTissue and stage
    private FlyTissue getFlyTissueByUni(String uniTissue, String stage)
    {
        for(int i=0; i<listSize; i++)
        {
            String ut = flyTissues[i].getUniTissue();
            String st = flyTissues[i].getStage();
            if(ut.equals(uniTissue) && st.equals(stage))
            {
            	return flyTissues[i];
            }
        } 
        return null;
    }
	
    						/* Accessor/Search methods for FLY TISSUE list */
	
    // number of tissues (i.e. occupancy of array)
    public int getSize()
    {
    	return listSize;
    }
    
    // returns FlyTissue object at a given position in the array
    public FlyTissue getFlyTissue(int pos)
    {
    	return flyTissues[pos];
    }
     
    
    // allows stage description to be retrieved for an id (Not used here as all larval)
    public String getStageByID(int id)
    {
        for(int i=0; i<listSize; i++)
        {
            int flyID = flyTissues[i].getTissueID();
            if(id == flyID)
            {
            	return flyTissues[i].getStage();
            }
        }
        return "none";	// backstop that won't throw an npe
    }
    
    // allows tissue name to be retrieved for an id
    public String getTissueNameByID(int id)
    {
        for(int i=0; i<listSize; i++)
        {
            int flyID = flyTissues[i].getTissueID();
            if(id == flyID)
            {
            	return flyTissues[i].getTissueName();
            }
        }
        return "none";	// backstop that won't throw an npe
    }
  
    // allows UniTissue name to be retrieved for an id (Not used here as all larval)
    public String getUniTissueByID(int id)
    {
        for(int i=0; i<listSize; i++)
        {
            int flyID = flyTissues[i].getTissueID();
            if(id == flyID)
            {
            	return flyTissues[i].getUniTissue();
            }
        }
        return "none";	// backstop that won't throw an npe
    }
    
				/* Accessor/Search methods for FLY STAGE TISSUE list */
	
    // number of pairs (i.e. occupancy of array - gives number of lines in table) (Not used here as all larval)
    public int getPairSize()
    {
    	return pairListSize;
    }
    
    // returns FlyTissue object at a given position in the array (Not used here as all larval)
    public FlyStagePair getFlyStagePair(int pos)
    {
    	return flyPairs[pos];
    }
    
    // returns a FlyTissue object if either adult or larval component ID matches search ID (Not used here as all larval)
    public FlyStagePair getFlyStagePairByID(int id)
    {
    	 for(int i=0; i<pairListSize; i++)
    	{	
    		if(flyPairs[i].getAdultTissue() != null &&
    				flyPairs[i].getAdultTissue().getTissueID() == id)
    		{
    			return flyPairs[i];
    		}
    		else if(flyPairs[i].getLarvalTissue() != null &&
    				flyPairs[i].getLarvalTissue().getTissueID() == id)
    		{
    			return flyPairs[i];
    		}
    	}
    	return null;
    }
    
    								// INNER CLASSES //

 	// For storing a line from tissuesMG.txt
 	class TissueDisplay
 	{
 		int displayPos;		// order of tissue for display (e.g. in bar chart)
 		int tissueID;		// tissueID from DB
 		
 		TissueDisplay(int displayPos, int tissueID)
 		{
 			this.displayPos = displayPos;
 			this.tissueID = tissueID;
 		}
 		public int getDisplayPos()
 		{
 			return displayPos;
 		}
 		public int getTissueID()
 		{
 			return tissueID;
 		}
 	}
 	
 	// For storing a line from uniTissuesMG.txt
 	class PairDisplay
 	{
 		int displayPos;		// order of tissue for display (e.g. in table)
 		String uniTissue;	// uniTissue from DB
 		
 		PairDisplay(int displayPos, String uniTissue)
 		{
 			this.displayPos = displayPos;
 			this.uniTissue = uniTissue;
 		}
 		public int getDisplayPos()
 		{
 			return displayPos;
 		}
 		public String getUniTissue()
 		{
 			return uniTissue;
 		}
 	}
 	
		// METHODS FOR PARSING TEXT FILES WITH DISPLAY POSITIONS TO POPULATE ARRAYS INNER CLASS DISPLAY OBJECTS //
 	
	// Loads tissuesMG.txt into TissueDisplay array
	private void populateTissueDisplayList()
	{
		StreamFile sf = new StreamFile(TISSUE_FILE, true);
		StreamTokenizer st = sf.getStream();
		st.slashSlashComments(true);		// May use Java-style (//) comments
		boolean goOn = true; 
		try
		{
			while (goOn)
			{
				int displayPos = 0;
				int tissueID = 0;				
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
						tissueID = (int) st.nval;
						//System.out.println("tissueID: " + tissueID);
					}
					else
					{System.out.println("Expected tissueID");}
		    				
					TissueDisplay display = new TissueDisplay(displayPos, tissueID);
					tissueDisplays[tissueDisplaysSize] = display;
					tissueDisplaysSize++;  
				}
				else
				{
					goOn = false;
				}
			}
		}
		catch(IOException ioe)
		{
			System.out.println("Problem reading " + PAIR_FILE);
		}		
	}
	
	// Loads uniTissuesMG.txt into PairDisplay array
	private void populatePairDisplayList()
	{
		StreamFile sf = new StreamFile(PAIR_FILE, true);
		StreamTokenizer st = sf.getStream();
		st.slashSlashComments(true);		// May use Java-style (//) comments
		boolean goOn = true; 
		try
		{
			while (goOn)
			{
				int displayPos = 0;
				String uniTiss = null;				
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
					if (tok == StreamTokenizer.TT_WORD)
					{
						uniTiss = st.sval;
						//System.out.println("uniTiss: " + uniTiss);
					}
					else
					{System.out.println("Expected uniTiss");}
		    				
					PairDisplay display = new PairDisplay(displayPos, uniTiss);
					pairDisplays[pairDisplaysSize] = display;
					pairDisplaysSize++;  
				}
				else
				{
					goOn = false;
				}
			}
		}
		catch(IOException ioe)
		{
			System.out.println("Problem reading " + PAIR_FILE);
		}
	}
 	
}
