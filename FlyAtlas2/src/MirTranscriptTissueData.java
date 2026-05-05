// Class to hold TPM data for a mir transcript in a single tissue
// DPL 23.09.2022

public class MirTranscriptTissueData
{
	private String fbgn;
	private String fbtr;
	private int tissueID;
	private int tpm;
	private int sd;
	
	public MirTranscriptTissueData(String fbgn, String fbtr, int tissueID, int tpm, int sd)
	{
		this.fbgn = fbgn;
		this.fbtr = fbtr;
		this.tissueID = tissueID;
		this.tpm = tpm;
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
	
	public int getTPM()
	{
		return tpm;
	}
	
	public int getSD()
	{
		return sd;
	}
	
	// replaces default as tab-separated text suitable for output
	public String toString()
	{
		return fbgn + "\t" +  fbtr + "\t" + tissueID + "\t" + tpm + "\t" + sd;
	}
}
