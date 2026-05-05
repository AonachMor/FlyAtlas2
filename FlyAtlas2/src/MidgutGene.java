// Simple class to model MidgutGene and its transcripts
// DPL 10.10.2021

public class MidgutGene
{
	private String fbgn;
	private String annotationSymbol;
	private String symbol;
	private String name;
	private String locus;
	private String biotype;
	private TranscriptMG [] transcriptList;
	private final int TRANSCRIPT_LENGTH = 10;
	private int transcriptListSize;
	
	// Arrays for any genes masking this midgutGene, or genes this one masks
	private String[] maskingList;
	private int maskingListSize = 0;
	private String[] maskedList;
	private int maskedListSize = 0;
	
	public MidgutGene(String fbgn, String annotationSymbol, String symbol, String name, String locus, String biotype)
	{
		this.fbgn = fbgn;
		this.annotationSymbol = annotationSymbol;
		this.symbol = symbol;
		this.name = name;
		this.locus = locus;
		this.biotype = biotype;
		
		transcriptList = new TranscriptMG [TRANSCRIPT_LENGTH];
	}
	
	public void addTranscript(TranscriptMG trans)
	{
		//check for occupancy of array and expand as required
		if(transcriptListSize>transcriptList.length - 1)
		{
			TranscriptMG[] newList = new TranscriptMG[transcriptListSize*2];
			System.arraycopy(transcriptList, 0, newList, 0, transcriptListSize);
			transcriptList = newList;
		}
		transcriptList[transcriptListSize] = trans;
		transcriptListSize++;
	}
	
	public TranscriptMG getTranscript(int pos)
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
	
	public String getHTMLFormatted()
	{
		StringBuilder sb = new StringBuilder();

		if(symbol != null)
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">Symbol</span><br />");
			sb.append("<span class=\"infoContent\"> " + symbol + "</span></div>\n");
		}
		if(name != null)
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">Name</span><br />");
			sb.append("<span class=\"infoContent\"> " + checkSuper(name) + "</span></div>\n");
		}
		if(annotationSymbol != null)
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">Annotation Symbol</span><br />");
			sb.append("<span class=\"infoContent\"> " + annotationSymbol + "</span></div>\n");
		}
		sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">FlyBase ID</span><span class=\"mobileHide\"><br /></span>");
		sb.append("<span class=\"infoContent\" id=\"graphID\"><a href=\"javascript:linkToFBgn('" + fbgn + "');\" title=\"Load FlyBase report for " + fbgn + " in new window\">" + fbgn +"</a></span></div>\n");
		
		// sb.append("<div class=\"mobileHide\"><a href=\"javascript:loadLinks('" + fbgn + "','" + annotationSymbol + "','" + neuropeptideID + "');\" title=\"Load external links to this gene in new window\"><img src=\"images/extLinks.png\" alt=\"link out\" class=\"linkImg\" /></a></div>"); // TODO 
		sb.append("<div class=\"mobileHide\"><a href=\"javascript:loadLinks('" + fbgn + "','" + annotationSymbol + "');\" title=\"Load external links to this gene in new window\"><img src=\"images/extLinks.png\" alt=\"link out\" class=\"linkImg\" /></a></div>"); 
		
		return sb.toString();
	}
	
	// Checks for [+] indication of superscript. If present, marks up for HTML
	private String checkSuper(String name)
	{
		if(name.indexOf("[+]") != -1)
		{
			int start = name.indexOf("[+]");
			name = name.substring(0, start) + "<sup>+</sup>" + name.substring(start+3);
			// check for second case as in Na[+]/H[+]
			if(name.indexOf("[+]") != -1)
			{
				start = name.indexOf("[+]");
				name = name.substring(0, start) + "<sup>+</sup>" + name.substring(start+3);
			}
		}
		else if(name.indexOf("[2+]") != -1)
		{
			int start = name.indexOf("[2+]");
			name = name.substring(0, start) + "<sup>2+</sup>" + name.substring(start+4);			
		}
		return name;
	}
	
	// for testing
	public String geneInfoToString()
	{
		return fbgn + "\t" + annotationSymbol  + "\t" + symbol   + "\t" + name  + "\t" + locus   + "\t" + biotype;
	}

	// for testing
	public String transcriptInfoToString()
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<transcriptListSize; i++)
		{
			sb.append(transcriptList[i].toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
