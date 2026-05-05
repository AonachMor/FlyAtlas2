// Stores details of the FPKMs for the tissues for a single gene and the correlation values for a query (Formerly Probeset)
// 29.08.2012
// Adapted for BeetleAtllas 24.02.2021
// Latest update to FlyAtlas2 version 28.03.2021

public class ProfileTissueData 
{
	private String fbgn;					// (was geneID for Beetle)
	
	private ProfileDatum[] dataList;		// Array of expression objects corresponding to probe - i.e. one for each tissue/stage combination
	private final int LIST_LENGTH = 100;	// length of array
	private int listSize = 0;				// occupancy of array
	
	private double pStat; 					// P statistic for correlation significance
	private double rStat;					// r statistic for correlation

	// Constructor initializes array to which individual tissue FPKMs etc are added
	public ProfileTissueData(String fbgn)
	{
		this.fbgn = fbgn;
		dataList = new ProfileDatum [LIST_LENGTH];
	}

	public void addDatum(ProfileDatum datum)
	{
		dataList[listSize] = datum;
		listSize++;
	}
	
	// returns an individual ProfileDatum on the basis of TissueID
	public ProfileDatum getDatum(int tissueID)
	{
		for(int i=0; i<listSize; i++)
		{
			ProfileDatum datum = dataList[i];
			if(datum.getTissueID() == tissueID)
			{
				return dataList[i];
			}
		}
		return null;
	}
		
	public String getFBgn()
	{
		return fbgn;
	}	
	
	// Stats setters and getters
	
	public void setRstat(double rStat)
	{
		this.rStat = rStat;
	}
	
	public double getRstat()
	{
		return rStat;
	}
	
	public void setPstat(double pStat)
	{
		this.pStat = pStat;
	}
	
	public double getPstat()
	{
		return pStat;
	}
}
