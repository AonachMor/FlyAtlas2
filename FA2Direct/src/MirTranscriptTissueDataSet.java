// Class to hold a set of MirTranscriptTissuedata objects for all the tissues of an expt.for one transcript
// DPL 02.05.2017

public class MirTranscriptTissueDataSet
{
	private String fbgn;
	private String fbtr;
	private MirTranscriptTissueData [] mirTranscriptDataList;
	private final int LIST_LENGTH = 3;
	private int listSize;
	
	public MirTranscriptTissueDataSet(String fbgn, String fbtr)
	{
		this.fbgn = fbgn;
		this.fbtr = fbtr;
		mirTranscriptDataList = new MirTranscriptTissueData [LIST_LENGTH];
	}
	
	public void add(MirTranscriptTissueData data)
	{
		//check for occupancy of array and expand as required
		if(listSize>mirTranscriptDataList.length - 1)
		{
			MirTranscriptTissueData[] newList = new MirTranscriptTissueData[listSize*2];
			System.arraycopy(mirTranscriptDataList, 0, newList, 0, listSize);
			mirTranscriptDataList = newList;
		}
		mirTranscriptDataList[listSize] = data;
		listSize++;
	}
	
	public int getTranscriptDataListSize()
	{
		return listSize;
	}
	
	public String getFBgn()
	{
		return fbgn;
	}	
	
	public String getFBtr()
	{
		return fbtr;
	}
	
	public MirTranscriptTissueData getMirTranscriptTissueData(int pos)
	{
		return mirTranscriptDataList[pos];
	}
	
	public MirTranscriptTissueData getMirTranscriptTissueDataByID(int id)
	{
		for (int i=0; i<listSize; i++)
		{
			if(mirTranscriptDataList[i].getTissueID() == id)
			{
				return mirTranscriptDataList[i];
			}
		}
		return null;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<listSize; i++)
		{
			sb.append(mirTranscriptDataList[i].toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
