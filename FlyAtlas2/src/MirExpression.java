// Class to hold all the expression results for a single microRNA, including transcript data
// DPL 02.05.2017, 12.09.2022

public class MirExpression extends Expression
{
	private String fbgn;								// FBgn of gene searched for
	private MirTissueDataSet dataset;
	private MirTranscriptTissueDataSet [] MirTranscriptDataList;
	private final int MIR_TRANSCRIPSET_LENGTH = 10;
	private int mirTranscriptListSize = 0;
	
	public MirExpression(String fbgn)
	{
		this.fbgn = fbgn;
		MirTranscriptDataList = new MirTranscriptTissueDataSet [MIR_TRANSCRIPSET_LENGTH];
	}
	
	public void addMirTranscriptDataset(MirTranscriptTissueDataSet set)
	{
		//check for occupancy of array and expand as required
		if(mirTranscriptListSize>MirTranscriptDataList.length - 1)
		{
			MirTranscriptTissueDataSet[] newList = new MirTranscriptTissueDataSet[mirTranscriptListSize*2];
			System.arraycopy(MirTranscriptDataList, 0, newList, 0, mirTranscriptListSize);
			MirTranscriptDataList = newList;
		}
		MirTranscriptDataList[mirTranscriptListSize] = set;
		mirTranscriptListSize++;
	}
		
	public void setMirData(MirTissueDataSet dataset)
	{
		this.dataset = dataset;
	}
	
	// 'Get' methods
	
	public MirTissueDataSet getMirData()
	{
		return dataset;
	}
	
	// for use in Graphic generation
	public MirTranscriptTissueDataSet[] getMirTranscriptTissueDataSet()
	{
		return MirTranscriptDataList;
	}
	
	public MirTranscriptTissueDataSet getMirTranscriptData(int pos)
	{
		return MirTranscriptDataList[pos];
	}
	
	public int getMirTranscriptDataSize()
	{
		return mirTranscriptListSize;
	}
	
	public String getFBgn()
	{
		return fbgn;
	}
	
	// for testing
	public String mirTranscriptDataToString()
	{
		return dataset.toString();
	}

	// for testing
	public String transcriptDataToString()
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<mirTranscriptListSize; i++)
		{
			sb.append(MirTranscriptDataList[i].toString());
			sb.append("\n");
		}
		return sb.toString();
	}

}
