// Class to hold FPKM data for a transcript in a single tissue
// DPL 07.04.2014

public class TranscriptTissueDataMG
{
	private String fbgn;
	private String fbtr;
	private int tissueID;
	private double fpkm;
	private String status;
	private double sd;
	
	public TranscriptTissueDataMG(String fbgn, String fbtr, int tissueID, double fpkm, String status, double sd)
	{
		this.fbgn = fbgn;
		this.fbtr = fbtr;
		this.tissueID = tissueID;
		this.fpkm = fpkm;
		this.status = status;
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
	
	public String getStatus()
	{
		return status;
	}
	
	public double getSD()
	{
		return sd;
	}
	
	// replaces default as tab-separated text suitable for output
	public String toString()
	{
		return fbgn + "\t" +  fbtr + "\t" + tissueID + "\t" + fpkm + "\t" + status + "\t" + sd;
	}
}
