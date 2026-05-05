// Class to hold FPKM data for a transcript in a single tissue
// DPL 10.05.2016

public class TranscriptTissueData
{
	private String fbgn;
	private String fbtr;
	private int tissueID;
	private double fpkm;
	private double sd;
	
	public TranscriptTissueData(String fbgn, String fbtr, int tissueID, double fpkm, double sd)
	{
		this.fbgn = fbgn;
		this.fbtr = fbtr;
		this.tissueID = tissueID;
		this.fpkm = fpkm;
		this.sd = sd;
	}
	
	public String getFBgn()
	{
		return fbgn;
	}
	
	public String getFBtr()
	{
		return fbtr;
	}
	
	public int getTissueID()
	{
		return tissueID;
	}
	
	public double getFPKM()
	{
		return fpkm;
	}
	
	public double getSD()
	{
		return sd;
	}
	
	// replaces default as tab-separated text suitable for output
	public String toString()
	{
		return fbgn + "\t" +  fbtr + "\t" + tissueID + "\t" + fpkm + "\t" + sd;
	}
}
