// Models Tissue table from database together with Display Position
// 08.12.2015

public class FlyTissue
{
	private int tissueID;			// TissueID field
	private String stage;			// Adult or Larval		NB case
	private String sex;				// Sex (Male or Female)
	private String tissueName;		// TissueName (can include additional fly info)
	private String uniTissue;		// adult/larval unified tissue name (for matching on table layout)
	private int displayPosition;	// position in which the tissue should be displayed (e.g. in bar chart)
	
	public FlyTissue(int tissueID, String stage, String sex, String tissueName, String uniTissue, int displayPosition)
	{
		this.tissueID = tissueID;
		this.stage = stage;
		this.sex = sex;
		this.tissueName = tissueName;
		this.uniTissue = uniTissue;
		this.displayPosition = displayPosition;
	}
	
	// Accessor methods	
	public int getTissueID()
	{
		return tissueID;
	}
	
	public String getStage()
	{
		return stage;
	}
	
	public String getSex()
	{
		return sex;
	}
	
	public String getTissueName()
	{
		return tissueName;
	}
	
	public String getUniTissue()
	{
		return uniTissue;
	}
	
	public int getDisplayPosition()
	{
		return displayPosition;
	}
}
