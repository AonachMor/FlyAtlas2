// Class to hold all the expression results for a single Midgut gene, including transcript data
// DPL 01.11.2021

public class MidgutExpression
{
	private String fbgn;								// FBgn of midgutGene searched for
	private GeneDatasetMG geneData;
	private TranscriptTissueDataSetMG [] transcriptDataList;
	private final int TRANSCRIPSET_LENGTH = 10;
	private int transcriptListSize = 0;
	
	public MidgutExpression(String fbgn)
	{
		this.fbgn = fbgn;
		transcriptDataList = new TranscriptTissueDataSetMG [TRANSCRIPSET_LENGTH];
	}
	
	public void addTranscriptDataset(TranscriptTissueDataSetMG set)
	{
		//check for occupancy of array and expand as required
		if(transcriptListSize>transcriptDataList.length - 1)
		{
			TranscriptTissueDataSetMG[] newList = new TranscriptTissueDataSetMG[transcriptListSize*2];
			System.arraycopy(transcriptDataList, 0, newList, 0, transcriptListSize);
			transcriptDataList = newList;
		}
		transcriptDataList[transcriptListSize] = set;
		transcriptListSize++;
	}
		
	public void setGeneData(GeneDatasetMG dataset)
	{
		geneData = dataset;
	}
	
	// 'Get' methods	
	public GeneDatasetMG getGeneData()
	{
		return geneData;
	}	
	public TranscriptTissueDataSetMG getTransciptData(int pos)
	{
		return transcriptDataList[pos];
	}	
	public String getFBgn()
	{
		return fbgn;
	}
	
	// Generates out HTML block with results
	public String getHTMLFormatted(MidgutGene gene, String searchTerm, MidgutCatalogue mgCat)
	{	
		// Gene info
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"results\">\n");	
		sb.append(gene.getHTMLFormatted());
		
		// check if this gene is masked by others (would have null data)
		if(gene.getMaskingListSize() > 0) 
		{
			sb.append("<div class=\"ambiguity\">\nThe data for this gene is masked by ");
			if(gene.getMaskingListSize() == 1)
			{
				sb.append("gene: ");
			}
			else
			{
				sb.append("genes: ");
			}
			for(int i=0; i<gene.getMaskingListSize(); i++)
			{
				sb.append(gene.getMaskingGene(i) + " ");
			}
			sb.append("<br />Viewing the gene region in UCSC Genome Browser (link below) or FlyBase may clarify this.\n</div>\n");
		}
		else
		{
			// add SVG image code
			sb.append("<div id=\"svg\" style=\"text-align:center;\" class=\"mobileHide625\">\n");
			sb.append(getSVG(mgCat));
			sb.append("</div>\n");

			// Start of section for individual replicates	
			// Transcripts: Explanatory line and clearer
			sb.append("<div class=\"leftTHead\">Gene FPKMs</div>\n");
			sb.append("<div class=\"clearer\"></div>");
			
			sb.append("<div id=\"results\">\n");
			// start table
			sb.append("<table class=\"mgGeneR\">\n");	
			// th row
			sb.append("<tr><th>Midgut section</th><th>Mean</th>");
			for(int j=0; j<geneData.getNumReplicates(); j++)
			{
				sb.append("<th>Replicate " + (j+1) + "</th>\n");
			}
			//sb.append("<th>SD</th><th>Status</th></tr>\n");	
			sb.append("<th>SD</th></tr>\n");	
			// td rows
			for(int i=0; i<geneData.getGeneDataSize(); i++)
			{
				sb.append(geneData.getGeneTissuedata(i).getHTMLFormatted(i, mgCat));		// this will be wrong but what the hell??
			}
			// close table
			sb.append("</table>\n");		
			// End of section for individual replicates
			
			// MidgutSwitch 
			sb.append("<div style=\"margin-top:20px;\">");
			sb.append("<button onclick=\"" + getSwitchLink() + "\">All tissues</button> ");
			sb.append("Run this query on all tissues.");
			sb.append("</div>");
					
			// Transcripts: Explanatory line and clearer
			sb.append("<div class=\"leftTHead\" style=\"padding-top:20px;\">Transcript FPKMs</div>\n");			
			sb.append("<div class=\"clearer\"></div>");
			
			// Transcripts Table		
			sb.append("<table class=\"mgTransR\" cellspacing=\"0\">\n");
			// th row
			sb.append(gene.getTranscript(0).getTableHeadFormatted(transcriptDataList[0], mgCat));		// take Head info from first as common to all
			sb.append("\n");
			// FPKM info for individual transcripts
			for(int i=0; i<transcriptListSize; i++)
			{
				sb.append(gene.getTranscript(i).getHTMLFormatted(transcriptDataList[i], mgCat, i));
				sb.append("\n");
			}
			sb.append("</table>\n");
			
			// check if this gene masks any others
			if(gene.getMaskedListSize() > 0) 
			{
				sb.append("<div class=\"ambiguity\">\n<span style=\"color:red;\">These data should be interpreted with care as this gene masks ");
				if(gene.getMaskedListSize() == 1)
				{
					sb.append("gene: ");
				}
				else
				{
					sb.append("genes: ");
				}
				for(int i=0; i<gene.getMaskedListSize(); i++)
				{
					sb.append(gene.getMaskedGene(i) + " ");
				}
				sb.append("</span><br />Viewing the gene region in UCSC Genome Browser (link below) or FlyBase may clarify this.\n</div>\n");
			}
			
			// check if this is an RNA gene and if so alert user
			String prefix = gene.getAnnotationSymbol().substring(0, 2);
			if(prefix.equals("CR"))
			{
				sb.append("<div class=\"ambiguity\">\n<span style=\"color:red;\">These data are for an RNA transcript, which one would not expect to detect in this study.");
				sb.append("</span><br />FPKM values above background would suggest a small RNA with poor replicates or a longer RNA with an overlapping highly-expressed protein gene."
						+ " In these cases you are advised to examine the sequence reads in the UCSC Genome Browser (link below).\n</div>\n");
			}
		}
		// Link-out to UCSC Browser
		sb.append(getUCSClink(gene));
		// close div
		sb.append("</div><!-- end of results div -->\n");		
		// close results div (rounded box)
		sb.append("</div> <!-- end of results div -->\n");
		
		return sb.toString();
	}

	// generates html string for SVG graphic
	private String getSVG(MidgutCatalogue tissueList)
	{
		int listSize = geneData.getGeneDataSize();
		
		double[] values = new double[listSize];
		for(int i=0; i<listSize; i++)
		{
			values[i] = geneData.getGeneTissuedata(i).getFPKM();
		}
		
		// Create SVG image 600x400 px
		CreateImage ci = new CreateImage(fbgn, tissueList, values);
		return ci.getSVG();
	}
	
	// generates link to load RNAseq bigwig tracks in UCSC browser with customization of other tracks
	private String getUCSClink(MidgutGene gene)
	{
		StringBuilder sb = new StringBuilder("<div class=\"linkText\">");
		sb.append("<a href=\"https://genome.ucsc.edu/cgi-bin/hgTracks?db=dm6&amp;position=");
		sb.append(gene.getLocus());
		sb.append("&amp;hgct_customText=https://motif.mvls.gla.ac.uk/fly/gutTracksDm6.txt");
		sb.append("&xenoRefGene=hide&phyloP27way_sel=0&phastCons27way_sel=0&multiz27way_sel=0&ensGene=pack&refGene=hide\"");
		sb.append(" onclick=\"window.open(this.href); return false;\">");
		sb.append("View transcripts in UCSC Genome Browser");
		sb.append("</a></div>");
		return sb.toString();
	}
	
	// generates link to search for gene in all tissues (must be full path)
	private String getSwitchLink()
	{
		StringBuilder sb = new StringBuilder("");
		sb.append("window.location.href='https://motif.mvls.gla.ac.uk/FlyAtlas2/index.html?search=gene&gene=");
		sb.append(fbgn);
		sb.append("&idtype=fbgn#mobileTargetG\'");
		return sb.toString();		
	}
	
	// for testing
	public String geneDataToString()
	{
		return geneData.toString();
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
