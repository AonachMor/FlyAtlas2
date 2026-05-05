// Class to hold a set of MirTissueData objects for all the tissues of an experiment
// DPL 23.09.2022

public class MirTissueDataSet
{
	private String fbgn;
	private MirTissueData [] mirDataList;
	private final int LIST_LENGTH = 30;
	private int listSize;							// occupancy i.e. number of tissues
	private final int NUM_REPLICATES = 3;			// This refers to the replicates in ea GeneTissuedata object which should be the same
	private TissueCatalogue tCat;
	
	public MirTissueDataSet(String fbgn, TissueCatalogue tCat)
	{
		this.fbgn = fbgn;
		mirDataList = new MirTissueData [LIST_LENGTH];
		this.tCat = tCat;
	}
	
	public void add(MirTissueData data)
	{
		//check for occupancy of array and expand as required
		if(listSize>mirDataList.length - 1)
		{
			MirTissueData[] newList = new MirTissueData[listSize*2];
			System.arraycopy(mirDataList, 0, newList, 0, listSize);
			mirDataList = newList;
		}
		mirDataList[listSize] = data;
		listSize++;
	}
	
	// For all non-reference MirTissuedata objects in mirDataList[], enrichment is now calculated and set
	public void calculateEnrichments()
	{
		// Working values for use in calculations - may be massaged to prevent division by zero etc.
		int maleRefTPM = 0;
		int femaleRefTPM = 0;
		int larvalRefTPM = 0;
		// Actual values so can check how to present edge cases
		int rawMaleRefTPM = 0;
		int rawFemaleRefTPM = 0;
		int rawLarvalRefTPM = 0;
		
		// retrieve FPKMs from male/female/larval whole tissue references
		for (int i=0; i<listSize; i++)
		{
			MirTissueData mtd = mirDataList[i];
			int id = mtd.getTissueID();
			boolean reference = tCat.getRefStatusByID(id);
			if(reference == true)
			{
				String sex = tCat.getSexByID(id);
				if(sex.equals(PageUtility.MALE))
				{
					maleRefTPM = mtd.getTPM();
					rawMaleRefTPM = maleRefTPM;
				}
				else if(sex.equals(PageUtility.FEMALE))
				{
					femaleRefTPM = mtd.getTPM();
					rawFemaleRefTPM = femaleRefTPM;
				}
				else if(sex.equals(PageUtility.BOTH))
				{
					larvalRefTPM = mtd.getTPM();
					rawLarvalRefTPM = larvalRefTPM;
				}
			}
		}
		// Set reference TPMs to minimum of 20 to avoid misleadingly large enrichments
		if(maleRefTPM < 20)
		{
			maleRefTPM = 20;
		}
		if(femaleRefTPM < 20)
		{
			femaleRefTPM = 20;
		}
		if(larvalRefTPM < 20)
		{
			larvalRefTPM = 20;
		}
		
		// Now go through mirDataList array retrieving objects and setting enrichment
		for (int i=0; i<listSize; i++)
		{		
			// Get tissue TPM and adjust to 20 as minimum for enrichment calculation 
			MirTissueData data = mirDataList[i];
			int tpm = data.getTPM();

			// Get tissueID and find if male/female/larval
			int id = data.getTissueID();
			String sex = tCat.getSexByID(id);
			
			int refTPM = 0;	// declare reference FPKM
			int rawRefTPM = 0;
			if(sex.equals(PageUtility.MALE))
			{
				refTPM = maleRefTPM;
				rawRefTPM = rawMaleRefTPM;
			}
			else if(sex.equals(PageUtility.FEMALE))
			{
				refTPM = femaleRefTPM;
				rawRefTPM = rawFemaleRefTPM;
			}
			else if(sex.equals(PageUtility.BOTH))
			{
				refTPM = larvalRefTPM;
				rawRefTPM = rawLarvalRefTPM;
			}
			// Calculate and set enrichment 
			double enrichment = (double) tpm / refTPM;		// Standard - Avoid integer division!
			
			// flag low experimental or reference values as meaningless
			if(rawRefTPM < 20 && tpm < 20)
			{
				enrichment = -1;
			}
		
			data.setEnrichment(enrichment);
		}
	}
	
			// Accessor methods  //
	
	public MirTissueData[] getMirTissuedata()
	{
		return mirDataList;
	}
	
	public MirTissueData getMirTissuedata(int pos)
	{
		return mirDataList[pos];
	}
	
	public int getMirDataSize()
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
	
		// Search method for members of mirDataList

	public MirTissueData findDataByID(int id)
	{
		for (int i=0; i<listSize; i++)
		{
			if(mirDataList[i].getTissueID() == id)
			{
				return mirDataList[i];
			}
		}
		return null;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<listSize; i++)
		{
			sb.append(mirDataList[i].toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
