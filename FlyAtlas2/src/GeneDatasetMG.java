// Class to hold a set of GeneTissuedataMG objects for all the tissues of an expt
// DPL 01.02.2016

public class GeneDatasetMG
{
	private String fbgn;
	private GeneTissuedataMG [] geneDataList;
	private final int LIST_LENGTH = 30;
	private int listSize;							// occupancy i.e. number of tissues
	private final int NUM_REPLICATES = 3;			// This refers to the replicates in ea GeneTissuedataMG object which should be the same
	
	public GeneDatasetMG(String fbgn)
	{
		this.fbgn = fbgn;
		geneDataList = new GeneTissuedataMG [LIST_LENGTH];
	}
	
	public void add(GeneTissuedataMG data)
	{
		//check for occupancy of array and expand as required
		if(listSize>geneDataList.length - 1)
		{
			GeneTissuedataMG[] newList = new GeneTissuedataMG[listSize*2];
			System.arraycopy(geneDataList, 0, newList, 0, listSize);
			geneDataList = newList;
		}
		geneDataList[listSize] = data;
		listSize++;
	}
	
	public GeneTissuedataMG[] getGeneTissuedata()
	{
		return geneDataList;
	}
	
	public GeneTissuedataMG getGeneTissuedata(int pos)
	{
		return geneDataList[pos];
	}
	
	public int getGeneDataSize()
	{
		return listSize;
	}
	
	public String getFBgn()
	{
		return fbgn;
	}
	
	public int getNumReplicates()
	{
		return NUM_REPLICATES;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<listSize; i++)
		{
			sb.append(geneDataList[i].toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
