// Simple class to model Gene and its transcripts
// Revised for DB using new reference genome 22.01.2025 and 27.02.2025
// Last update 21.10.2025

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
	private boolean para99;					// true if fly paralogues with >=99% identity
	private Transcript [] transcriptList;
	private final int TRANSCRIPT_LENGTH = 10;
	private int transcriptListSize;
	
	public Gene(String fbgn, String annotationSymbol, String symbol, String name, String locus, String biotype, 
						int neuropeptideID, boolean para, boolean para99)
	{
		this.fbgn = fbgn;
		this.annotationSymbol = annotationSymbol;
		this.symbol = symbol;
		this.name = name;
		this.locus = locus;
		this.biotype = biotype;
		this.neuropeptideID = neuropeptideID;
		this.para = para;
		this.para99 = para99;
		
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
	
	public boolean isPara99()
	{
		return para99;
	}
	
	// multi-page version for all but profile search
	public String getHTMLFormatted(int index, boolean conceal)
	{
		double 	rStat = 0.0;
		double pStat = 1.0;
		return getHTMLFormatted(index, conceal, rStat, pStat);
	}
	// multi-page version for profile search	
	public String getHTMLFormatted(int index, boolean conceal, double rStat, double pStat)
	{
		StringBuilder sb = new StringBuilder();

		// span with toggle visibility button (down-pointing and up-pointing black triangles, &#9660; and &#9652;
		if(conceal)
		{
			sb.append("<div class=\"geneInfo\">");
			sb.append("<a href=\"javascript:toggleConcealed('bt_" + index + "','hs_" + index + "','&#9658;','&#9660;');\" title=\"reveal results\">");
			sb.append("<span id=\"bt_" + index + "\" class=\"infoContent onOff\">&#9658;</span></a>");
			sb.append("</div>");
		}
		
		if(symbol != null)
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">Symbol</span><span class=\"mobileHide\"><br /></span>");
			sb.append("<span class=\"infoContent\"> " + symbol + "</span></div>\n");
		}
		if(name != null)
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">Name</span><span class=\"mobileHide\"><br /></span>");
			sb.append("<span class=\"infoContent\"> " + PageUtility.checkSuper(name) + "</span></div>\n");
		}
		if(annotationSymbol != null)
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">Annotation Symbol</span><span class=\"mobileHide\"><br /></span>");
			sb.append("<span class=\"infoContent\"> " + annotationSymbol + "</span></div>\n");
		}
		sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">FlyBase ID</span><span class=\"mobileHide\"><br /></span>");
		sb.append("<span class=\"infoContent\"><a href=\"javascript:linkToFBgn('" + fbgn + "');\" title=\"Load FlyBase report for " + fbgn + " in new window\">" + fbgn +"</a></span></div>\n");

		if(fbgn != null)	// modified Para version, not using local getPara() — js method needs changing, called listParalogues in BeetleAtlas
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption mobileHide\">Paralogues</span><span class=\"mobileHide\"><br /></span>");
			if(para)
			{			
				sb.append("<span class=\"infoContent mobileHide\">"
						+ "<a href=\"javascript:listParalogues('" + fbgn + "');\"> "
								+ "Paralogues(s)</a></span></div>\n");
			}
			else
			{
				sb.append("<span class=\"infoContent mobileHide\"> No paralogues</span></div>\n");
			}
		}
					
		if(rStat > 0.0)		// Profile stats, if appropriate
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">Correlation</span><span class=\"mobileHide\"><br /></span>");
			sb.append("<span class=\"infoContent\"><em>r</em> = " + PageUtility.formatValues(rStat, 2) + ", <em>P<sub>B</sub></em> = "  + PageUtility.formatValues(pStat, 3) + "</span></div>\n");
		}
		sb.append("<div class=\"mobileHide\"><a href=\"javascript:loadLinks('" + fbgn + "','" + annotationSymbol + "','" + neuropeptideID + "');\" title=\"Load external links to this gene in new window\"><img src=\"images/extLinks.png\" alt=\"link out\" class=\"linkImg\" /></a></div>");
		
		return sb.toString();
	}
	
	// for testing
	public String geneInfoToString()
	{
		return fbgn + "\t" + annotationSymbol  + "\t" + symbol   + "\t" + name  + "\t" + locus   + "\t" + biotype + "\t" + neuropeptideID + "\t" + para;
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
