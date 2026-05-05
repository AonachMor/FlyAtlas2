// Models Tissue table from database 
// renamed from FlyTissue
// 17.06.2016

public class Tissue
{
	private int tissueID;			// TissueID field
	private String stage;			// Adult or Larval		NB case
	private String sex;				// Sex (Male, Female or Both) NB case
	private String tissueName;		// TissueName (can include additional fly info)
	private String abbreviation;	// Two-letter abbreviation of tissue name
	private int uniTissueID;		// adult/larval unified tissue ID (for matching on table layout)
	private boolean reference;		// reference 'tissue' (i.e. whole) or not
	
	public Tissue(int tissueID, String stage, String sex, String tissueName, String abbreviation, int uniTissueID, boolean reference)
	{
		this.tissueID = tissueID;
		this.stage = stage;
		this.sex = sex;
		this.tissueName = tissueName;
		this.abbreviation = abbreviation;
		this.uniTissueID = uniTissueID;
		this.reference = reference;
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
	
	public String getAbbreviation()
	{
		return abbreviation;
	}
	
	public int getUniTissueID()
	{
		return uniTissueID;
	}
	
	public boolean isReference()
	{
		return reference;
	}
}
