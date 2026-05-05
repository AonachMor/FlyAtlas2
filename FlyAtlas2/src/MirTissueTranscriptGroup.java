/*
	Class to hold a mirTissueTranscriptGroup object with the fractions of total TPM for mir Transcripts A and B
	DPL 14.10.2022
*/	

public class MirTissueTranscriptGroup
{
	int tissueID;
	double fractA = 0.0;
	double fractB = 0.0;
	int tpmTotal; 
	
	// constructor requires a value for ea tpm. If no transcript use 0
	MirTissueTranscriptGroup(int tissueID, int tpmA, int tpmB)
	{
		this.tissueID = tissueID;			
		calcFractions(tpmA, tpmB);
	}
	
	private void calcFractions(int tpmA, int tpmB)
	{
		// Need to ensure not trying to divide by zero or do something that gives NaN
		// Although this will give false ratios, colour coding will zero out.
		if(tpmA < 1)
		{
			tpmA = 1;
		}
		if(tpmB < 1)
		{
			tpmB = 1;
		}
		
		tpmTotal = tpmA + tpmB;
		
		fractA = (double)tpmA/tpmTotal;
		fractB = (double)tpmB/tpmTotal;		
	}
	
	public int getTissueID()
	{
		return tissueID;
	}
	
	public double getFractA()
	{
		return fractA;
	}
	public double getFractB()
	{
		return fractB;
	}
	
	public int getTPM()
	{
		return tpmTotal;
	}
	
	public String toString()
	{
		return ("TissueID: " + tissueID + " fractA: " + fractA + " fractB: " + fractB);
	}
}