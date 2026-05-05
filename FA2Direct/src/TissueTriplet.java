// TissueTriplet
// Models male/female/larval FlyTissue 'pair' with same uniTissueID
// Previously FlyStagePair
// DPL 17.06.2016

public class TissueTriplet
{
	private int uniTissueID;		// adult/larval unified tissue ID (e.g. for matching on table layout)
	private Tissue maleTissue;	// adult male FlyTissue object (if exists)
	private Tissue femaleTissue;	// adult female FlyTissue object (if exists)
	private Tissue larvalTissue;	// larval FlyTissue object (if exists)
	private int displayPosition;	// position in which the pair should be displayed (e.g. in a table) - was listPosition
	private String uniTissueName;	// adult/larval unified tissue Name (for matching on table layout)
	private boolean reference;	// flag for whole tissue
	
	public TissueTriplet(int uniTissueID, Tissue maleTissue, Tissue femaleTissue, Tissue larvalTissue, 
							int displayPosition, String uniTissueName)
	{
		this.uniTissueID = uniTissueID;
		this.maleTissue = maleTissue;
		this.femaleTissue = femaleTissue;
		this.larvalTissue = larvalTissue;
		this.displayPosition = displayPosition;
		this.uniTissueName = uniTissueName;
		if(displayPosition == -1)
		{
			reference = true;
		}
		else
		{
			reference = false;
		}
	}
	
	// Accessor methods	
	
	public int getUniTissueID()
	{
		return uniTissueID;
	}
	
	public String getUniTissueName()
	{
		return uniTissueName;
	}

	public Tissue getMaleTissue()
	{
		return maleTissue;
	}
	
	public Tissue getFemaleTissue()
	{
		return femaleTissue;
	}
	
	public Tissue getLarvalTissue()
	{
		return larvalTissue;
	}
	
	public int getDisplayPosition()
	{
		return displayPosition;
	}


	public boolean hasMaleTissue()
	{
		if(maleTissue == null)
		{return false;}
		else
		{return true;}
	}
	public boolean hasFemaleTissue()
	{
		if(femaleTissue == null)
		{return false;}
		else
		{return true;}
	}
	public boolean hasLarvalTissue()
	{
		if(larvalTissue == null)
		{return false;}
		else
		{return true;}
	}
	
	public boolean isReference()
	{
		return reference;
	}
	
	public String toString()
	{
		return("UniTissueID: " + uniTissueID + ", Male tissue: " + maleTissue + ",  Female tissue: " + femaleTissue + ", "
				+ "Larval tissue: " + larvalTissue + ", Display position: " + displayPosition + ", UniTissue Name: " + uniTissueName);
	}
}


