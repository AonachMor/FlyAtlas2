// Class to hold all the expression results for a single gene, including transcript data
// DPL 17.06.2016

public class GeneExpression extends Expression
{
	private String fbgn;								// FBgn of gene searched for
	private GeneTissueDataSet dataset;
	private TranscriptTissueDataSet [] transcriptDataList;
	private final int TRANSCRIPSET_LENGTH = 10;
	private int transcriptListSize = 0;
	
	public GeneExpression(String fbgn)
	{
		this.fbgn = fbgn;
		transcriptDataList = new TranscriptTissueDataSet [TRANSCRIPSET_LENGTH];
	}
	
	public void addTranscriptDataset(TranscriptTissueDataSet set)
	{
		//check for occupancy of array and expand as required
		if(transcriptListSize>transcriptDataList.length - 1)
		{
			TranscriptTissueDataSet[] newList = new TranscriptTissueDataSet[transcriptListSize*2];
			System.arraycopy(transcriptDataList, 0, newList, 0, transcriptListSize);
			transcriptDataList = newList;
		}
		transcriptDataList[transcriptListSize] = set;
		transcriptListSize++;
	}
		
	public void setGeneData(GeneTissueDataSet dataset)
	{
		this.dataset = dataset;
	}
	
	// 'Get' methods
	
	public GeneTissueDataSet getGeneData()
	{
		return dataset;
	}
	
	public TranscriptTissueDataSet getTranscriptData(int pos)
	{
		return transcriptDataList[pos];
	}
	
	public int getTranscriptDataSize()
	{
		return transcriptListSize;
	}
	
	public String getFBgn()
	{
		return fbgn;
	}
	
	// for testing
	public String geneDataToString()
	{
		return dataset.toString();
	}

	// for testing
	public String transcriptDataToString()
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<transcriptListSize; i++)
		{
			sb.append(transcriptDataList[i].toString());
			sb.append("\n");
		}
		return sb.toString();
	}

}
