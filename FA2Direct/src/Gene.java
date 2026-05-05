// Simple class to model Gene and its transcripts (As for FA2 but no HTML output)
// DPL 26.06.2017
// Updated 24.01.25 for more recent additions

public class Gene
{
	private String fbgn;
	private String annotationSymbol;
	private String symbol;
	private String name;
	private String locus;
	private String biotype;
	private int neuropeptideID = 0;
	private boolean para;					// true if fly paralogues
	
	private Transcript [] transcriptList;
	private final int TRANSCRIPT_LENGTH = 10;
	private int transcriptListSize;
	
	// Arrays for any genes masking this gene, or genes this one masks
	private String[] maskingList;
	private int maskingListSize = 0;
	private String[] maskedList;
	private int maskedListSize = 0;
	
	// etc
	private String romanSymbol;
	private String romanName;
	
	public Gene(String fbgn, String annotationSymbol, String symbol, String romanSymbol, String name, String romanName, 
				String locus, String biotype, int neuropeptideID, boolean para)
	{
		this.fbgn = fbgn;
		this.annotationSymbol = annotationSymbol;
		this.symbol = symbol;
		this.romanSymbol = romanSymbol;
		this.name = name;
		this.romanName = romanName;
		this.locus = locus;
		this.biotype = biotype;
		this.neuropeptideID = neuropeptideID;
		this.para = para;
		
		transcriptList = new Transcript [TRANSCRIPT_LENGTH];
	}
	
	public void addTranscript(Transcript trans)
	{
		//check for occupancy of array and expand as required
		if(transcriptListSize>transcriptList.length - 1)
		{
			Transcript[] newList = new Transcript[transcriptListSize*2];
			System.arraycopy(transcriptList, 0, newList, 0, transcriptListSize);
			transcriptList = newList;
		}
		transcriptList[transcriptListSize] = trans;
		transcriptListSize++;
	}
	
	public Transcript getTranscript(int pos)
	{
		return transcriptList[pos];
	}
	
	public int getTranscriptListSize()
	{
		return transcriptListSize;
	}
	
	// Masking and Masked set and get methods
	
	public void setMasking(String[] maskingList, int maskingListSize)
	{
		this.maskingList = maskingList;
		this.maskingListSize = maskingListSize;
	}
	
	public void setMasked(String[] maskedList, int maskedListSize)
	{
		this.maskedList = maskedList;
		this.maskedListSize = maskedListSize;
	}
	
	public String getMaskingGene(int pos)
	{
		return maskingList[pos];
	}
	
	public int getMaskingListSize()
	{
		return maskingListSize;
	}
	
	public String getMaskedGene(int pos)
	{
		return maskedList[pos];
	}
	
	public int getMaskedListSize()
	{
		return maskedListSize;
	}
	

	// Returns transcript corresponding to a particular FBtr
	public Transcript getTranscriptByFBTR(String fbtr)
	{
		for(int i=0; i<transcriptListSize; i++)
		{
			if (fbtr.equals(transcriptList[i].getFBtr()))
			{
				return transcriptList[i];
			}
		}
		return null;
	}
	
	//-----------------------------------------------//
	
	public String getFBgn()
	{
		return fbgn;
	}
	
	public String getAnnotationSymbol()
	{
		if(annotationSymbol==null){ annotationSymbol = "";}
		return annotationSymbol;
	}
	
	public String getSymbol()
	{
		if(symbol==null){ symbol = "";}
		return symbol;
	}
	
	public String getName()
	{
		if(name==null){ name = "";}
		return name;
	}
	
	public String getLocus()
	{
		if(locus==null){ locus = "";}
		return locus;
	}
	
	public String getBioType()
	{
		if(biotype==null){ biotype = "";}
		return biotype;
	}
	
	public int getNeuropeptideID()
	{
		return neuropeptideID;
	}
	
	public boolean hasPara()
	{
		return para;
	}
	
	
	// Header info re Gene for text file
	public String getGeneInfoText()
	{
		StringBuilder sb = new StringBuilder();		
		
		sb.append("FlyBase ID\t" + fbgn + "\n");
		
		if(annotationSymbol != null)
		{
			sb.append("Annotation Symbol\t" + annotationSymbol + "\n");
		}
		
		if(romanSymbol != null)
		{
			sb.append("Symbol\t"  + romanSymbol + "\n");
		}
		else if(symbol != null)
		{
			sb.append("Symbol\t"  + symbol + "\n");
		}
		
		if(romanName != null)
		{
			sb.append("Name\t" + romanName + "\n");
		}
		else if(name != null)
		{
			sb.append("Name\t" + name + "\n");
		}
		
		return sb.toString();
	}
	
}
