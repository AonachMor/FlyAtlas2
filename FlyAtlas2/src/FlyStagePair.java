// Models adult/larval FlyTissue pair with same uniTissue
// DPL 08.12.2015

public class FlyStagePair
{
	private String uniTissue;		// adult/larval unified tissue name (e.g. for matching on table layout)
	private FlyTissue adultTissue;	// adult FlyTissue object (if exists)
	private FlyTissue larvalTissue;	// larval FlyTissue object (if exists)
	private int displayPosition;	// position in which the pair should be displayed (e.g. in a table) - was listPosition
	
	public FlyStagePair(String uniTissue, FlyTissue adultTissue, FlyTissue larvalTissue, int displayPosition)
	{
		this.uniTissue = uniTissue;
		this.adultTissue = adultTissue;
		this.larvalTissue = larvalTissue;
		this.displayPosition = displayPosition;
	}
	
	// Accessor methods	
	
	public String getUniTissue()
	{
		return uniTissue;
	}

	public FlyTissue getAdultTissue()
	{
		return adultTissue;
	}
	
	public FlyTissue getLarvalTissue()
	{
		return larvalTissue;
	}
	
	public int getDisplayPosition()
	{
		return displayPosition;
	}
	
	public boolean hasBothStages()
	{
		if(adultTissue == null || larvalTissue == null)
		{
			return false;		
		}
		else
		{
			return true;
		}
	}
	
	public String toString()
	{
		return("UniTissue: " + uniTissue + " Adult tissue: " + adultTissue + " Larval tissue: " + larvalTissue + " Display position: " + displayPosition);
	}
}


