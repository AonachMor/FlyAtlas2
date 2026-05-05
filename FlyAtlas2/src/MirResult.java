/*
	One-method class to generate Results block for a single microRNA containing a Gene and Transcript table
	David P. Leader 23.08.2022
*/

import java.awt.Color;
import java.text.NumberFormat;

public class MirResult 
{
	private Gene gene;				// Gene object for which results are tabulated
	private TissueCatalogue tCat;	// TissueCatalogue object which knows about all tissues
	private MirExpression mirExpn;	// Expression object with results
	private int resNum;				// Numerical suffix to append to HTML ID to allow multiple results having unique ids for JavaScript methods
	private boolean conceal;		// If true, provide a hide/show button to conceal table (used where multiple results)
	private boolean includeErrors;	// If true, display SD with TPMs (for toggle button)
	private boolean showWhole;		// If true, display Whole Body abundance data (for toggle button)
	private boolean sexStats;		// If true, show M v. F comparison
	
	private int maleIndex;			// current id to be used for cells for male tissues
	private int femaleIndex;		// current id be used for cells for female tissues
	private int larvalIndex;		// current id be used for cells for larval tissues
	
	public MirResult(Gene gene, TissueCatalogue tCat, MirExpression mirExpn, int resNum, boolean conceal, boolean includeErrors, boolean showWhole, boolean sexStats)
	{
		this.gene = gene;
		this.tCat = tCat;
		this.mirExpn = mirExpn;
		this.includeErrors = includeErrors;
		this.showWhole = showWhole;
		this.sexStats = sexStats;
		this.resNum = resNum;
		this.conceal = conceal;
	}
	
	// Generates output HTML block with results (multiple gene version assigning different ids to tables)
	public String getResultsHTML()
	{	
		initializeIndices(tCat);	// need to reset each time for multiple genes
		
		String geneTableID = "tabGene_" + resNum;
		// String transTableID = "tabTrans_" + resNum;
		
		// Explanatory and gene info
		StringBuilder sb = new StringBuilder();
		
		sb.append("<div class=\"results\">\n");		// changed to allow multiple instances
		sb.append("<div class=\"geneInfoSet\">\n");
		sb.append(gene.getHTMLFormatted(resNum, conceal));
		sb.append("</div><!-- end of geneInfoSet div -->\n");
		
		if(conceal)
		{
			sb.append("<div id=\"hs_"+ resNum + "\" class=\"conceal\">\n");		// start of div for hide/show for multiple results		
		}
			
		if(mirExpn == null)
		{
			System.out.println("mirExpn is null!");
		}
		MirTissueDataSet dataset = mirExpn.getMirData();
		if(dataset == null)
		{
			System.out.println("dataset is null!");
		}
		
		NumberFormat N = NumberFormat.getInstance();
		N.setMaximumFractionDigits(1);	
		
		// Gene title and SD and WholeBody checkboxes both within a wider div, so for float order is reversed
		sb.append("<div class=\"mobileHide\">");
		if(includeErrors)
		{
			sb.append("<div class=\"rightTHead\">" + "<input type=\"checkbox\" checked=\"checked\" class=\"sd\" id=\"errors_" + resNum + 
					"\" value=\"errors\" onclick=\"toggleSpan('.plusMinus'); synchBoxes(this,'sd');\" /> SDs&nbsp;&nbsp;"); 					
		}
		else
		{
			sb.append("<div class=\"rightTHead\">" + "<input type=\"checkbox\" class=\"sd\" id=\"errors_" + resNum + 
				"\" value=\"errors\" onclick=\"toggleSpan('.plusMinus'); synchBoxes(this,'sd');\" /> SDs&nbsp;&nbsp;"); 
		}
		if(showWhole)
		{
			sb.append(" <input type=\"checkbox\" style=\"margin-left:5px;\" checked=\"checked\" class=\"sw\" id=\"whole_" + resNum + 
					"\" value=\"whole\" onclick=\"toggleRow('wholesome'); synchBoxes(this,'sw');\" />  Whole Body&nbsp;&nbsp;");
		}
		else
		{
			sb.append(" <input type=\"checkbox\" style=\"margin-left:5px;\" class=\"sw\" id=\"whole_" + resNum + 
					"\" value=\"whole\" onclick=\"toggleRow('wholesome'); synchBoxes(this,'sw');\" />  Whole Body&nbsp;&nbsp;");			
		}			
		if(sexStats)
		{
			sb.append(" <input type=\"checkbox\" style=\"margin-left:5px;\" checked=\"checked\" class=\"mf\" id=\"mf_" + resNum + 
					"\" value=\"mf\" onclick=\"toggleColumns('geneR'); synchBoxes(this,'mf');\" />  Male v. Female</div>\n");
		}
		else
		{
			sb.append(" <input type=\"checkbox\" style=\"margin-left:5px;\" class=\"mf\" id=\"mf_" + resNum + 
					"\" value=\"mf\" onclick=\"toggleColumns('geneR'); synchBoxes(this,'mf');\" />  Male v. Female</div>\n");			
		}
		
		sb.append("<div class=\"leftTHead\">MicroRNA TPMs and Enrichments"
				+ "<a href=\"javascript:toggleDiv('indexWinG_" + resNum + "');\" title=\"Gene Table Tips\" class=\"infolink\" id=\"qG_" + resNum + "\" style=\"padding-left:75px;\">"
				+ "&nbsp;</a></div>\n");		
		sb.append("</div>");	
		sb.append(PageUtility.getGeneHelp(resNum));
	
		// Table of TPMs and enrichments
		sb.append("<table id=\"" + geneTableID + "\" class=\"geneR pointer\">\n");
		sb.append("<tr class=\"noPointer\"><th></th><th colspan=\"2\"><span class=\"mobileHide400\">Adult</span> Male</th><th colspan=\"2\"><span class=\"mobileHide400\">Adult</span> Female</th><th colspan=\"2\">Male v. Female</th><th colspan=\"2\">Larval</th></tr>\n");	
		sb.append("<tr class=\"noPointer\"><th>Tissue</th><th>TPM</th><th>Enrich<span class=\"mobileHide625\">ment</span></th><th>TPM</th><th>Enrich<span class=\"mobileHide625\">ment</span></th><th>M/F</th><th>p value</th><th>TPM</th><th>Enrich<span class=\"mobileHide625\">ment</span></th></tr>\n");	
		
		// Write each table row in order specified in tCat object's TissueTriplet list
		int nonrefCount = 0; 	// counter for included rows — i.e. those other than reference pairs
		for (int i=0; i < tCat.getTripletListSize(); i++)
		{
			TissueTriplet thisTrip = tCat.getTissueTriplet(i);
			
			if(!thisTrip.isReference() && thisTrip.hasMir())	// Not really necessary to include hasMir()
			{
				// Cell 1: common Tissue Name 
				String nextUniTissueName = thisTrip.getUniTissueName();		// next non-reference uniTissue in list
				if(nonrefCount > 0)
				{
					sb.append("</tr>\n<tr><td class=\"noPointer\">" + nextUniTissueName + "</td>");
				}
				else
				{
					sb.append("<tr><td class=\"noPointer\">" + nextUniTissueName + "</td>");
				}
				
				// MALE //
				Tissue maleTissue = tCat.getTissueTriplet(i).getMaleTissue();
				
				if(maleTissue == null)	// if not present write empty cells 2 and 3 
				{
					sb.append("<td class=\"lVacant\">&nbsp;</td><td class=\"noPointer\">&nbsp;</td>");
				}
				else					// Get appropriate MirTissueData object and build table columns
				{
					MirTissueData maleData = dataset.findDataByID(maleTissue.getTissueID());
					
					if(maleData != null)
					{	
						getGeneTableColumns(sb, maleData, resNum, maleIndex);
						maleIndex++;
					}
					else		// this is temp for tissues with data pending
					{
						sb.append("<td class=\"pending\">pending</td><td class=\"pending\">—</td>"); // no highlighting to avoid confusing testers
						maleIndex++;	// still need to increment count
					}
				}
				
				// FEMALE //
				Tissue femaleTissue = tCat.getTissueTriplet(i).getFemaleTissue();
				
				if(femaleTissue == null)	// if not present write empty cells 4 and 5 
				{
					sb.append("<td class=\"lVacant\">&nbsp;</td><td class=\"noPointer\">&nbsp;</td>");
				}
				else					// Get appropriate MirTissueData object and build table columns
				{
					MirTissueData femaleData = dataset.findDataByID(femaleTissue.getTissueID());
					
					if(femaleData != null)
					{	
						getGeneTableColumns(sb, femaleData, resNum, femaleIndex);
						femaleIndex++;
					}
					else		// this is temp for tissues with data pending
					{
						sb.append("<td class=\"pending\">pending</td><td class=\"pending\">—</td>"); // no highlighting to avoid confusing testers
						femaleIndex++; // still need to increment count
					}
				}

				// MALE v. FEMALE //
				if(maleTissue == null || femaleTissue == null)	// if not present write empty cells 6 and 7 
				{
					sb.append("<td class=\"lVacant\">&nbsp;</td><td class=\"noPointer\">&nbsp;</td>");
				}
				else					// Get appropriate GeneTissueData object and build table columns
				{
					MirTissueData femaleData = dataset.findDataByID(femaleTissue.getTissueID());
					MirTissueData maleData = dataset.findDataByID(maleTissue.getTissueID());
					if(femaleData != null && maleData != null)
					{	
						getMFdata(sb, maleData, femaleData);
					}
					else		// this is for tissues with data pending
					{
						sb.append("<td class=\"noPointer\"></td><td class=\"noPointer\"></td>");
					}
				}	
				
				// LARVAL //
				Tissue larvalTissue = tCat.getTissueTriplet(i).getLarvalTissue();
				
				if(larvalTissue == null)	// if not present write empty cells 2 and 3 
				{
					sb.append("<td class=\"lVacant\">&nbsp;</td><td class=\"noPointer\">&nbsp;</td>");
				}
				else					// Get appropriate MirTissueData object and build table columns
				{
					MirTissueData larvalData = dataset.findDataByID(larvalTissue.getTissueID());
					
					if(larvalData != null)
					{	
						getGeneTableColumns(sb, larvalData, resNum, larvalIndex);
						larvalIndex++;
					}
					else		// this is temp for tissues with data pending
					{
						sb.append("<td class=\"pending\">pending</td><td class=\"pending\">—</td>"); // no highlighting to avoid confusing testers
						larvalIndex++; // still need to increment count
					}
				}
				nonrefCount++;
			}
			// Insert "whole" row at end before reference
			else if(thisTrip.isReference())
			{
				// blank spacer row
				sb.append("</tr>\n<tr class=\"wholesome\"><td colspan=\"7\" style=\"background-color:white;\"></td>");				
				// Cell 1: common Tissue Name 
				String nextUniTissueName = thisTrip.getUniTissueName();
				sb.append("</tr>\n<tr class=\"wholesome\"><td>" + nextUniTissueName + "</td>");
				// MALE //
				Tissue maleTissue = tCat.getTissueTriplet(i).getMaleTissue();
				MirTissueData maleData = dataset.findDataByID(maleTissue.getTissueID());	
				getGeneTableColumns(sb, maleData, resNum);
				// FEMALE //
				Tissue femaleTissue = tCat.getTissueTriplet(i).getFemaleTissue();	
				MirTissueData femaleData = dataset.findDataByID(femaleTissue.getTissueID());
				getGeneTableColumns(sb, femaleData, resNum);
				// MALE v. FEMALE //
				getMFdata(sb, maleData, femaleData);	
				// LARVAL //
				Tissue larvalTissue = tCat.getTissueTriplet(i).getLarvalTissue();	
				MirTissueData larvalData = dataset.findDataByID(larvalTissue.getTissueID());
				getGeneTableColumns(sb, larvalData, resNum);
			}

		}
		
		// close table
		sb.append("</tr>\n</table>\n");
		sb.append(PageUtility.getDownloadLink(gene, false, true) + "\n");

									// TRANSCRIPTS//
		// Start of section with transcripts table
		sb.append("<div class=\"mobileTurn\">Rotate to see Transcript Table</div>\n");
		sb.append("<div class=\"transcript\"><!-- start of transcript div -->\n");
		
		// Transcript title and UCSC link both within a wider div, so for float order is reversed
		sb.append("<div class=\"mobileHide\">");		
		sb.append("<div class=\"rightTHead\"><a href=\"javascript:linkToUCSC('" + gene.getFBgn() + "','" + gene.getLocus() + "','true');\" " 
				+ "title=\"Load RNAseq reads in UCSC browser in a new window\">View in UCSC Genome Browser</a></div>\n");
		// version for old table
/*		sb.append("<div class=\"leftTHead\">Transcript TPMs"
				+ "<a href=\"javascript:toggleDiv('indexWinT_" + resNum + "');\" title=\"Transcript Table Tips\" class=\"infolink\" id=\"qT_" + resNum + "\" style=\"padding-left: 75px;\">"
				+ "&nbsp;</a></div>\n");*/
		
		sb.append("<div class=\"leftTHead\">Transcripts" + "&nbsp;</div>\n");
		sb.append("</div>");				
		sb.append(PageUtility.getTranscriptHelp(resNum));
		
		// Old style table with heat map
/*		// Transcripts Table start and hard-coded first header row		
		sb.append("<table id=\"" + transTableID + "\" class=\"transcriptR\">\n");
		sb.append("<tr><th colspan=\"2\">Transcript</th><th colspan=\"15\">Male</th><th colspan=\"16\">Female</th><th colspan=\"9\">Larval</th></tr>\n");
		
		// TH row
		sb.append(getTranscriptHeaderRow(tCat));
		sb.append("\n");
		
		// Results rows
		sb.append(getMirTranscriptTableRows(mirExpn, gene, tCat));

		sb.append("</table>\n");*/
		
		// Generate SVG image		
		MirTranscriptImage mti = new MirTranscriptImage(mirExpn.getMirTranscriptTissueDataSet(), 
				mirExpn.getMirTranscriptDataSize(), mirExpn.getFBgn(), tCat);
		// add SVG image code
		//sb.append("<div id=\"svg\" style=\"text-align:center;\" class=\"mobileHide625\">\n");
		sb.append("<div class=\"svgImage mobileHide625\" id=\"svg_" + resNum + "\" >\n");
		sb.append(mti.getSVG());
		sb.append("</div>\n");
				
		sb.append(PageUtility.getDownloadLink(gene, true, true));			
		
		// add SVG download button	
		sb.append("<div class=\"svgButton\" style=\"text-align:right;\">");
		sb.append("<button onclick=\"sendSVG("+ resNum +");\">Save as SVG</button>");
		sb.append("</div>");
		String geneID = gene.getFBgn();
		sb.append("<div id=\"graphID_" + resNum + "\" style=\"display:none;\">" + geneID + "</div>");
		
		sb.append(PageUtility.MICRO_RNA);
		
		// close second results div
		sb.append("\n</div><!-- end of transcript div -->\n");	
		
		// close conceal div
		if(conceal)
		{
			sb.append("</div><!-- end of conceal div -->\n");
		}
		
		// close results div (rounded box)
		sb.append("</div> <!-- end of overall results div -->\n");
		return sb.toString();
	}

	// initializes index for cell ids used to link gene and transcript cell selection
	public void initializeIndices(TissueCatalogue  tCat)
	{
		maleIndex = 1;
		int maleCount = tCat.getTissueCountBySex(PageUtility.MALE);		
		femaleIndex = maleCount + 1;
		int femaleCount = tCat.getTissueCountBySex(PageUtility.FEMALE);	
		larvalIndex = maleCount + femaleCount + 1;		
	}
	
	// Create Columns for TPM (abundance) and Enrichment for a particular condition (e.g. M, F, Larvae)
	private void getGeneTableColumns(StringBuilder sb, MirTissueData data, int tableID, int cellID)
	{		
		String sd = "<span class=\"plusMinus\"> &plusmn; " + PageUtility.formatValues(data.getSD()) + "</span>";

		// deal with colour
		Color enrichCol = PageUtility.getEnrichmentColor(data.getEnrichment());
		boolean enrichTextWhite = PageUtility.isDark(PageUtility.getBrightness(enrichCol));	
		String enrichHTMLcolour = PageUtility.getHTMLcolour(enrichCol);
		
		Color abundCol = PageUtility.getTPMColor(data.getTPM());
		boolean abundTextWhite = PageUtility.isDark(PageUtility.getBrightness(abundCol));
		String abundHTMLcolour = PageUtility.getHTMLcolour(abundCol);
		
		String fID = "f" + tableID + "_" + cellID;		// id for tpm cell
		String eID = "e" + tableID + "_" + cellID;		// id for enrichment cell
		
		// Cell 2, 4 or 6: TPM
		if(data.getTPM() >= 2) // no colour if less than 2
		{
			sb.append("<td onclick=\"hiliteGene(this)\" id=\"" + fID + "\" style=\"background-color:" + abundHTMLcolour + ";");
		}
		else
		{
			sb.append("<td onclick=\"hiliteGene(this)\" id=\"" + fID + "\" style=\"background-color:white;");
		}
		if(abundTextWhite)
		{
			sb.append("color: white;");
		}
		sb.append("\">" + PageUtility.formatValues(data.getTPM())  + sd + "</td>");
		
		// Cell 3, 5 or 7: enrichment
		double enrich = data.getEnrichment();
		if(enrich != -1)
		{
			sb.append("<td onclick=\"hiliteGene(this)\" id=\"" + eID + "\" style=\"background-color:" + enrichHTMLcolour + ";");
			if(enrichTextWhite)
			{
				sb.append("color: white;");
			}
			sb.append("\">" + PageUtility.formatValues(enrich)  + "</td>");
		}
		else
		{
			sb.append("<td onclick=\"hiliteGene(this)\" id=\"" + eID + "\" style=\"background-color:white\">n.a.</td>");
		}					
	}
	
	// Create Columns for TPM (abundance) for Whole (e.g. M, F, Larvae)
	private void getGeneTableColumns(StringBuilder sb, MirTissueData data, int tableID)
	{
		String sd = "<span class=\"plusMinus\"> &plusmn; " + PageUtility.formatValues(data.getSD()) + "</span>";
		// deal with colour
		Color abundCol = PageUtility.getTPMColor(data.getTPM());
		boolean abundTextWhite = PageUtility.isDark(PageUtility.getBrightness(abundCol));
		String abundHTMLcolour = PageUtility.getHTMLcolour(abundCol);
		// Cell 2, 4 or 6: TPM
		if(data.getTPM() >= 2) // no colour if less than 2
		{
			sb.append("<td style=\"background-color:" + abundHTMLcolour + ";");
		}
		else
		{
			sb.append("<td style=\"background-color:white;");
		}
		if(abundTextWhite)
		{
			sb.append("color: white;");
		}
		sb.append("\">" + PageUtility.formatValues(data.getTPM())  + sd + "</td>");
		// Cell 3, 5 or 7: blank (don't present enrichment of 1)
		sb.append("<td style=\"background-color:white;");
		sb.append("\"></td>");
	}
	
	private void getMFdata(StringBuilder sb, MirTissueData maleData, MirTissueData femaleData)
	{
		double maleTPM = (double) maleData.getTPM();
		double femaleTPM = (double) femaleData.getTPM();
		if(maleTPM < 2)
		{
			maleTPM = 2;
		}
		if(femaleTPM < 2)
		{
			femaleTPM = 2;
		}
		double ratio = maleTPM/femaleTPM;
		String ratioText = PageUtility.formatValues(ratio, 2);
		
		MeanComparator mc = new MeanComparator(femaleData, maleData);
		String signif = mc.getSignificance();
		sb.append("<td class=\"noPointer\">");
		sb.append(ratioText);
		sb.append("</td><td class=\"noPointer\">");
		sb.append(signif);
		sb.append("</td>");
	}
	
	 // Returns a HTML table header row with sorted Male, Female, Larval tissue abbreviations
    public String getTranscriptHeaderRow(TissueCatalogue tCat)
    { 
    	StringBuilder sb = new StringBuilder("<tr><td>Name</td><td>ID</td>");
  		// Go through array adding abbreviation to StringBuilder
		for (int i=0; i<tCat.getTissueListSize(); i++)
		{
			if(!tCat.getTissue(i).isReference())
			{
				sb.append("<td>" + tCat.getTissue(i).getAbbreviation() + "</td>");
			}
		}
    	sb.append("</tr>");
    	return sb.toString();
    }

/*	private String getMirTranscriptTableRows(MirExpression mirExpn, Gene gene, TissueCatalogue tCat)
	{
		StringBuilder sb = new StringBuilder();
	
		// Go through array of TranscriptTissueDataSet objects (each for a distinct transcript) held in Expression object
		// Might be better sorted by Transcript name suffix
		for (int i=0; i<mirExpn.getMirTranscriptDataSize(); i++)
		{
			MirTranscriptTissueDataSet ttds = mirExpn.getMirTranscriptData(i);
			// get transcript details for left two identifier cells
			String fbtr = ttds.getFBtr();
			String rAlpha = gene.getTranscriptByFBTR(fbtr).getNameSuffix();	// RA, RB etc.
			sb.append("<tr><td>" +  rAlpha + "</td>");	
			sb.append("<td><a href=\"javascript:linkToFBtr('" + fbtr + "');\" title=\"Load FlyBase report for " + fbtr + " in new window\">" + fbtr +"</a></td>");
			
			// go through array of pre-sorted FlyTissue objects and get TissueIDs
			for (int j=0; j<tCat.getTissueListSize(); j++)
			{	
				if(!tCat.getTissue(j).isReference())
				{
					int id = tCat.getTissue(j).getTissueID();
					MirTranscriptTissueData data = ttds.getMirTranscriptTissueDataByID(id);
					if(data != null)
					{
						Color abundCol = PageUtility.getAbundanceColor(data.getTPM());
						String abundHTMLcolour = PageUtility.getHTMLcolour(abundCol);
						sb.append("<td style=\"background-color:" + abundHTMLcolour + "; \"></td>");
					}
					else
					{
						sb.append("<td style=\"background-color:white; \"></td>");
					}
				}
			}
			
			sb.append("</tr>\n");
		}				
    	return sb.toString();		
	}*/	
	
}
