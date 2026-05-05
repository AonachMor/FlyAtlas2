// Class to hold FPKM (and enrichment) data for a gene in a single tissue
// DPL 08.01.2025


public class GeneTissueData
{
	private String fbgn;
	private int tissueID;
	private double fpkm;					// mean
	private double [] repFPKMlist;			// 
	private int repFPKMlistSize;			// array should have a size of 2 or 3 (i.e. holds duplicates or triplicates)
	private double sd;
	private double enrichment;
	private String status;
	
	public GeneTissueData(String fbgn, int tissueID, double fpkm, double[] repFPKMlist, double sd, String status)
	{
		this.fbgn = fbgn;
		this.tissueID = tissueID;
		this.fpkm = fpkm;
		this.repFPKMlist = repFPKMlist;
			repFPKMlistSize = repFPKMlist.length;
		this.sd = sd;
		this.status = status;
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
	
	public double getFPKM()
	{
		return fpkm;
	}
	
	public int getRepFPKMlistSize()
	{
		return repFPKMlistSize;
	}
	
	public double getRepFPKM(int pos)
	{
		return repFPKMlist[pos];
	}
	
	public double getSD()
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
		return fbgn + "\t" + tissueID + "\t" + fpkm + "\t" + "(" + repFPKMlist[0] + ", " +  repFPKMlist[1] + ", " +  repFPKMlist[2] + ")" 
					+ "\t" + sd + "\t" + status;
	}
}
