// Class to hold TPM (and enrichment) data for a microRNA in a single tissue
// DPL 05.09.2025


public class MirTissueData
{
	private String fbgn;
	private int tissueID;
	private int tpm;					// mean
	private int [] repTPMlist;
	private int reptTPMlistSize;		// 2 or 3 to allow for duplicates or triplicates
	private int sd;
	private double enrichment;
	private String status = "OK";		// for consistency with FPKM, because doesn't exist
	
	public MirTissueData(String fbgn, int tissueID, int tpm, int[] repTPMlist, int sd)
	{
		this.fbgn = fbgn;
		this.tissueID = tissueID;
		this.tpm = tpm;
		this.repTPMlist = repTPMlist;
			reptTPMlistSize = repTPMlist.length;
		this.sd = sd;
	}
	
	public void setEnrichment(double value)
	{
		enrichment = value;
	}
	
			// Accessor methods //
	
	public String getFBgn()
	{
		return fbgn;
	}
	
	public int getTissueID()
	{
		return tissueID;
	}
	
	public int getTPM()
	{
		return tpm;
	}
	
	public int getRepTPMlistSize()
	{
		return reptTPMlistSize;
	}
	
	public int getRepTPM(int pos)
	{
		return repTPMlist[pos];
	}
	
	public int getSD()
	{
		return sd;
	}
	
	public double getEnrichment()
	{
		return enrichment;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	// replaces default as tab-separated text suitable for output
	public String toString()
	{
		return fbgn + "\t" + tissueID + "\t" + tpm + "\t" + "(" + repTPMlist[0] + ", " +  repTPMlist[1] + ", " +  repTPMlist[2] + ")" + "\t" + sd;
	}
}
