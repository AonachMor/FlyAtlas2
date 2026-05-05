// Class to hold a set of GeneTissueData objects for all the tissues of an expt
// DPL 18.07.2016

public class GeneTissueDataSet
{
	private String fbgn;
	private GeneTissueData [] geneDataList;
	private final int LIST_LENGTH = 30;
	private int listSize;							// occupancy i.e. number of tissues
	private final int NUM_REPLICATES = 3;			// This refers to the replicates in ea GeneTissuedata object which should be the same
	private TissueCatalogue tCat;
	
	public GeneTissueDataSet(String fbgn, TissueCatalogue tCat)
	{
		this.fbgn = fbgn;
		geneDataList = new GeneTissueData [LIST_LENGTH];
		this.tCat = tCat;
	}
	
	public void add(GeneTissueData data)
	{
		//check for occupancy of array and expand as required
		if(listSize>geneDataList.length - 1)
		{
			GeneTissueData[] newList = new GeneTissueData[listSize*2];
			System.arraycopy(geneDataList, 0, newList, 0, listSize);
			geneDataList = newList;
		}
		geneDataList[listSize] = data;
		listSize++;
	}
	
	// For all non-reference GeneTissuedata objects in geneDataList[], enrichment is now calculated and set
	public void calculateEnrichments()
	{
		// Working values for use in calculations - may be massaged to prevent division by zero etc.
		double maleRefFPKM = 0.0;
		double femaleRefFPKM = 0.0;
		double larvalRefFPKM = 0.0;
		// Actual values so can check how to present edge cases
		double rawMaleRefFPKM = 0.0;
		double rawFemaleRefFPKM = 0.0;
		double rawLarvalRefFPKM = 0.0;
		
		// retrieve FPKMs from male/female/larval whole tissue references
		for (int i=0; i<listSize; i++)
		{
			GeneTissueData gtd = geneDataList[i];
			int id = gtd.getTissueID();
			boolean reference = tCat.getRefStatusByID(id);
			if(reference == true)
			{
				String sex = tCat.getSexByID(id);
				if(sex.equals(PageUtility.MALE))
				{
					maleRefFPKM = gtd.getFPKM();
					rawMaleRefFPKM = maleRefFPKM;
				}
				else if(sex.equals(PageUtility.FEMALE))
				{
					femaleRefFPKM = gtd.getFPKM();
					rawFemaleRefFPKM = femaleRefFPKM;
				}
				else if(sex.equals(PageUtility.BOTH))
				{
					larvalRefFPKM = gtd.getFPKM();
					rawLarvalRefFPKM = larvalRefFPKM;
				}
			}
		}
		// Set reference FPKMs to minimum of 2 to avoid misleadingly large enrichments
		if(maleRefFPKM < 2.0)
		{
			maleRefFPKM = 2.0;
		}
		if(femaleRefFPKM < 2.0)
		{
			femaleRefFPKM = 2.0;
		}
		if(larvalRefFPKM < 2.0)
		{
			larvalRefFPKM = 2.0;
		}
		
		// Now go through geneDataList array retrieving objects and setting enrichment
		for (int i=0; i<listSize; i++)
		{		
			// Get tissue FPKM and adjust to 2.0 as minimum for enrichment calculation 
			GeneTissueData data = geneDataList[i];
			double fpkm = data.getFPKM();

			// Get tissueID and find if male/female/larval
			int id = data.getTissueID();
			String sex = tCat.getSexByID(id);
			
			double refFPKM = 0.0;	// declare reference FPKM
			double rawRefFPKM = 0.0;
			if(sex.equals(PageUtility.MALE))
			{
				refFPKM = maleRefFPKM;
				rawRefFPKM = rawMaleRefFPKM;
			}
			else if(sex.equals(PageUtility.FEMALE))
			{
				refFPKM = femaleRefFPKM;
				rawRefFPKM = rawFemaleRefFPKM;
			}
			else if(sex.equals(PageUtility.BOTH))
			{
				refFPKM = larvalRefFPKM;
				rawRefFPKM = rawLarvalRefFPKM;
			}
			// Calculate and set enrichment 
			double enrichment = fpkm / refFPKM;		// Standard
			
			// low expt and low ref — flag that this is meaningless
			if(rawRefFPKM < 2 && fpkm < 2)
			{
				enrichment = -1;
			}
		
			data.setEnrichment(enrichment);
		}
	}
	
			// Accessor methods  //
	
	public GeneTissueData[] getGeneTissuedata()
	{
		return geneDataList;
	}
	
	public GeneTissueData getGeneTissuedata(int pos)
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
	
		// Search method for members of geneDataList

	public GeneTissueData findDataByID(int id)
	{
		for (int i=0; i<listSize; i++)
		{
			if(geneDataList[i].getTissueID() == id)
			{
				return geneDataList[i];
			}
		}
		return null;
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
