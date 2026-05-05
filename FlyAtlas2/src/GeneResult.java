/*
	One-method class to generate Results block for a single gene containing a Gene and Transcript table
	David P. Leader 08.08.2021
	Revised for new DB 02.03.2025
	Last update 21.10.2025
*/

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;

public class GeneResult 
{
	private Gene gene;				// Gene object for which results are tabulated
	private TissueCatalogue tCat;	// TissueCatalogue object which knows about all tissues
	private GeneExpression expn;	// Expression object with results
	private int resNum;				// Numerical suffix to append to HTML ID to allow multiple results having unique ids for JavaScript methods
	private boolean conceal;		// If true, provide a hide/show button to conceal table (used where multiple results)
	private boolean includeErrors;	// If true, display SD with FPKMs (for toggle button)
	private boolean showWhole;		// If true, display Whole Body abundance data (for toggle button)
	private boolean sexStats;		// If true, show M v. F comparison
	
	private int maleIndex;			// current id to be used for cells for male tissues
	private int femaleIndex;		// current id be used for cells for female tissues
	private int larvalIndex;		// current id be used for cells for larval tissues
	
	private double rStat;			// Statistic for Profile searches (need to set at zero so ignored in output of other searches
	private double pStat;			// Statistic for Profile searches
	
	public GeneResult(Gene gene, TissueCatalogue tCat, GeneExpression expn, int resNum, boolean conceal, boolean includeErrors, boolean showWhole, boolean sexStats)
	{
		this.gene = gene;
		this.tCat = tCat;
		this.expn = expn;
		this.includeErrors = includeErrors;
		this.showWhole = showWhole;
		this.sexStats = sexStats;
		this.resNum = resNum;
		this.conceal = conceal;
		rStat = 0.0;
		pStat = 1.0;
	}
	
	public GeneResult(Gene gene, TissueCatalogue tCat, GeneExpression expn, int resNum, boolean conceal, boolean includeErrors, boolean showWhole, boolean sexStats,  double rStat, double pStat)
	{
		this.gene = gene;
		this.tCat = tCat;
		this.expn = expn;
		this.includeErrors = includeErrors;
		this.showWhole = showWhole;
		this.sexStats = sexStats;
		this.resNum = resNum;
		this.conceal = conceal;
		this.rStat = rStat;
		this.pStat = pStat;
	}
	
	// Generates output HTML block with results (multiple gene version assigning different ids to tables)
	public String getResultsHTML()
	{	
		initializeIndices(tCat);	// need to reset each time for multiple genes
		
		String geneTableID = "tabGene_" + resNum;
		String transTableID = "tabTrans_" + resNum;
		
		// Explanatory and gene info
		StringBuilder sb = new StringBuilder();
		
		sb.append("<div class=\"results\">\n");		// changed to allow multiple instances
		sb.append("<div class=\"geneInfoSet\">\n");
		sb.append(gene.getHTMLFormatted(resNum, conceal, rStat, pStat));
		sb.append("</div><!-- end of geneInfoSet div -->\n");
		
		if(conceal)
		{
			sb.append("<div id=\"hs_"+ resNum + "\" class=\"conceal\">\n");		// start of div for hide/show for multiple results		
		}
		
		GeneTissueDataSet dataset = expn.getGeneData();
		if(dataset == null)
		{
			sb.append("<br><div class=\"ambiguity\" style=\"font-size:100%\">\n");		// leave line space and start div for warning
			sb.append(PageUtility.AMBIGUITY_WARNING);									// warning
			sb.append(" In the case of gene " + gene.getFBgn() + " the situation is:\n</div>\n");		
			
			sb.append(getAmbiguityData(gene.getFBgn()));
			// This lacks closing </div> as this is provided by closing transcript div, for which there is no opener in this case //
		}
		
		else
		{			
			if(expn == null)
			{
				System.out.println("expn is null!");
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
			
			sb.append("<div class=\"leftTHead\">Gene FPKMs and Enrichments"
					+ "<a href=\"javascript:toggleDiv('indexWinG_" + resNum + "');\" title=\"Gene Table Tips\" class=\"infolink\" id=\"qG_" + resNum + "\" style=\"padding-left:75px;\">"
					+ "&nbsp;</a></div>\n");		
			sb.append("</div>");	
			sb.append(PageUtility.getGeneHelp(resNum));
		
			// Table of FPKMs and enrichments
			sb.append("<table id=\"" + geneTableID + "\" class=\"geneR pointer\">\n");
			sb.append("<tr class=\"noPointer\"><th></th><th colspan=\"2\"><span class=\"mobileHide400\">Adult</span> Male</th><th colspan=\"2\"><span class=\"mobileHide400\">Adult</span> Female</th><th colspan=\"2\">Male v. Female</th><th colspan=\"2\">Larval</th></tr>\n");	
			sb.append("<tr class=\"noPointer\"><th>Tissue</th><th>FPKM</th><th>Enrich<span class=\"mobileHide625\">ment</span></th><th>FPKM</th><th>Enrich<span class=\"mobileHide625\">ment</span></th><th>M/F</th><th>p value</th><th>FPKM</th><th>Enrich<span class=\"mobileHide625\">ment</span></th></tr>\n");	

			
			// Write each table row in order specified in tCat object's TissueTriplet list
			int nonrefCount = 0; 	// counter for included rows — i.e. those other than reference pairs
			for (int i=0; i < tCat.getTripletListSize(); i++)
			{
				TissueTriplet thisTrip = tCat.getTissueTriplet(i);
				
				// System.out.println(thisTrip.getUniTissueID());
				
				if(!thisTrip.isReference())
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
					else					// Get appropriate GeneTissueData object and build table columns
					{
						GeneTissueData maleData = dataset.findDataByID(maleTissue.getTissueID());
						
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
					else					// Get appropriate GeneTissueData object and build table columns
					{
						GeneTissueData femaleData = dataset.findDataByID(femaleTissue.getTissueID());
						
						if(femaleData != null)
						{	
							getGeneTableColumns(sb, femaleData, resNum, femaleIndex);
							femaleIndex++;
						}
						else		// this is for tissues with data pending
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
						GeneTissueData femaleData = dataset.findDataByID(femaleTissue.getTissueID());
						GeneTissueData maleData = dataset.findDataByID(maleTissue.getTissueID());
						if(femaleData != null && maleData != null)
						{	
							getMFdata(sb, maleData, femaleData);
						}
						else		// this is temp for tissues with data pending
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
					else					// Get appropriate GeneTissueData object and build table columns
					{
						GeneTissueData larvalData = dataset.findDataByID(larvalTissue.getTissueID());
						
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
				// Insert "whole" row at end
				else
				{
					// blank spacer row
					sb.append("</tr>\n<tr class=\"wholesome\"><td colspan=\"9\" style=\"background-color:white;\"></td>");				
					// Cell 1: common Tissue Name 
					String nextUniTissueName = thisTrip.getUniTissueName();
					sb.append("</tr>\n<tr class=\"wholesome\"><td>" + nextUniTissueName + "</td>");
					// MALE //
					Tissue maleTissue = tCat.getTissueTriplet(i).getMaleTissue();
					GeneTissueData maleData = dataset.findDataByID(maleTissue.getTissueID());	
					getGeneTableColumns(sb, maleData, resNum);
					// FEMALE //
					Tissue femaleTissue = tCat.getTissueTriplet(i).getFemaleTissue();	
					GeneTissueData femaleData = dataset.findDataByID(femaleTissue.getTissueID());
					getGeneTableColumns(sb, femaleData, resNum);		
					// MALE v. FEMALE //
					getMFdata(sb, maleData, femaleData);				
					// LARVAL //
					Tissue larvalTissue = tCat.getTissueTriplet(i).getLarvalTissue();	
					GeneTissueData larvalData = dataset.findDataByID(larvalTissue.getTissueID());
					getGeneTableColumns(sb, larvalData, resNum);
				}
			}

			// close table
			sb.append("</tr>\n</table>\n");
			
			sb.append(PageUtility.getDownloadLink(gene, false, false) + "\n");

										// TRANSCRIPTS//
			// Start of section with transcripts table
			sb.append("<div class=\"mobileTurn\">Rotate to see Transcript Table</div>\n");
			sb.append("<div class=\"transcript\"><!-- start of transcript div -->\n");
			
			// Transcript title and UCSC link both within a wider div, so for float order is reversed
			sb.append("<div class=\"mobileHide\">");		
			sb.append("<div class=\"rightTHead\"><a href=\"javascript:linkToUCSC('" + gene.getFBgn() + "','" + gene.getLocus() + "','false');\" " 
					+ "title=\"Load RNAseq reads in UCSC browser in a new window\">View in UCSC Genome Browser</a></div>\n");
			
			sb.append("<div class=\"leftTHead\">Transcript FPKMs"
					+ "<a href=\"javascript:toggleDiv('indexWinT_" + resNum + "');\" title=\"Transcript Table Tips\" class=\"infolink\" id=\"qT_" + resNum + "\" style=\"padding-left: 75px;\">"
					+ "&nbsp;</a></div>\n");		
			sb.append("</div>");				
			sb.append(PageUtility.getTranscriptHelp(resNum));
			
			// Transcripts Table start and hard-coded first header row to accommodate correct number of results
			int maleTotal = tCat.getTissueCountBySex(PageUtility.MALE); 
			int femaleTotal = tCat.getTissueCountBySex(PageUtility.FEMALE);
			int larvalTotal = tCat.getTissueCountBySex(PageUtility.BOTH);			
			sb.append("<table id=\"" + transTableID + "\" class=\"transcriptR\">\n");
			sb.append("<tr><th colspan=\"2\">Transcript</th><th colspan=\"" + maleTotal + "\">Male</th><th colspan=\"" + femaleTotal + 
						"\">Female</th><th colspan=\"" + larvalTotal + "\">Larval</th></tr>\n");
			
			// TH row
			sb.append(getTranscriptHeaderRow(tCat));
			sb.append("\n");
			
			// Results rows
			sb.append(getTranscriptTableRows(expn, gene, tCat));

			sb.append("</table>\n");
			sb.append(PageUtility.getDownloadLink(gene, true, false));
			
			// check if this is an RNA gene and if so alert user
			String prefix = gene.getAnnotationSymbol().substring(0, 2);
			if(prefix.equals("CR"))
			{
				String bioType = gene.getBioType();
				if(bioType.equals("pseudogene"))
				{
					sb.append(PageUtility.PSEUDOGENE);					
				}
				else if(bioType.equals("lncRNA"))
				{
					sb.append(PageUtility.LNC_RNA);					
				}
				else if(bioType.equals("snoRNA"))
				{
					sb.append(PageUtility.SNO_RNA);					
				}
				else if(bioType.equals("ncRNA"))
				{
					sb.append(PageUtility.NC_RNA);					
				}
				else if(bioType.equals("snRNA"))
				{
					sb.append(PageUtility.SN_RNA);					
				}
				else if(bioType.equals("antisense-RNA"))
				{
					sb.append(PageUtility.ANTISENSE_RNA);					
				}
				else
				{
					sb.append(PageUtility.NC_RNA);
				}
			}
			else if (prefix.equals("CG"))
			{
				sb.append(PageUtility.PROTEIN_GENE);				
			}
			
			// Check for repeats and alert user
			boolean para99 = gene.isPara99();
			if(para99 == true)
			{
				sb.append(PageUtility.REPEAT_GENE);
			}
		}
		
		if(dataset != null && !getAmbiguityData(gene.getFBgn()).equals("") )		// avoid repeat of no gene data
		{
			sb.append("<div class=\"ambiguity\" style=\"font-size:100%\">\n");		// start div for warning (don't need extra line space here)
			sb.append(PageUtility.AMBIGUITY_WARNING);			// warning
			sb.append(" In the case of gene " + gene.getFBgn() + " the situation is:\n</div>\n");						
			sb.append(getAmbiguityData(gene.getFBgn()));	
			sb.append("<p>*** More extensive information about transcript ambiguity and its implications can be found in the Docs ***</p>");
		}
		
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
	
	// Create Columns for FPKM (abundance) and Enrichment for a particular condition (e.g. M, F, Larvae)
	private void getGeneTableColumns(StringBuilder sb, GeneTissueData data, int tableID, int cellID)
	{		
		String sd = "<span class=\"plusMinus\"> &plusmn; " + PageUtility.formatValues(data.getSD()) + "</span>";

		// deal with colour
		Color enrichCol = PageUtility.getEnrichmentColor(data.getEnrichment());
		boolean enrichTextWhite = PageUtility.isDark(PageUtility.getBrightness(enrichCol));	
		String enrichHTMLcolour = PageUtility.getHTMLcolour(enrichCol);
		
		Color abundCol = PageUtility.getAbundanceColor(data.getFPKM());
		boolean abundTextWhite = PageUtility.isDark(PageUtility.getBrightness(abundCol));
		String abundHTMLcolour = PageUtility.getHTMLcolour(abundCol);
		
		String fID = "f" + tableID + "_" + cellID;		// id for fpkm cell
		String eID = "e" + tableID + "_" + cellID;		// id for enrichment cell
		
		// Cell 2, 4 or 6: FPKM
		if(data.getFPKM() >= 2) // no colour if less than 2
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
		if(data.getStatus().equals("OK"))
		{
			sb.append("\">" + PageUtility.formatValues(data.getFPKM())  + sd + "</td>");
		}
		else
		{
			sb.append("\">N.A.</td>");
		}
		
		// Cell 3, 5 or 7: enrichment
		double enrich = data.getEnrichment();
		if(enrich != -1)
		{
			sb.append("<td onclick=\"hiliteGene(this)\" id=\"" + eID + "\" style=\"background-color:" + enrichHTMLcolour + ";");
			if(enrichTextWhite)
			{
				sb.append("color: white;");
			}
			if(data.getStatus().equals("OK"))
			{
				sb.append("\">" + PageUtility.formatValues(enrich)  + "</td>");
			}
			else
			{
				sb.append("\">N.A.</td>");
			}
		}
		else
		{
			sb.append("<td onclick=\"hiliteGene(this)\" id=\"" + eID + "\" style=\"background-color:white\">N.A.</td>");
		}					
	}	
	
	// Create Columns for FPKM (abundance) for Whole (e.g. M, F, Larvae)
	private void getGeneTableColumns(StringBuilder sb, GeneTissueData data, int tableID)
	{
		String sd = "<span class=\"plusMinus\"> &plusmn; " + PageUtility.formatValues(data.getSD()) + "</span>";

		// deal with colour
		Color abundCol = PageUtility.getAbundanceColor(data.getFPKM());
		boolean abundTextWhite = PageUtility.isDark(PageUtility.getBrightness(abundCol));
		String abundHTMLcolour = PageUtility.getHTMLcolour(abundCol);
		// Cell 2, 4 or 6: FPKM
		if(data.getFPKM() >= 2) // no colour if less than 2
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
		if(data.getStatus().equals("OK"))
		{
			sb.append("\">" + PageUtility.formatValues(data.getFPKM())  + sd + "</td>");
		}
		else
		{
			sb.append("\">N.A.</td>");
		}
		// Cell 3, 5 or 7: blank (don't present enrichment of 1)
		sb.append("<td style=\"background-color:white;");
		sb.append("\"></td>");
	}
	
	private void getMFdata(StringBuilder sb, GeneTissueData maleData, GeneTissueData femaleData)
	{
		double maleFPKM = maleData.getFPKM();
		double femaleFPKM = femaleData.getFPKM();
		if(maleFPKM < 2)
		{
			maleFPKM = 2;
		}
		if(femaleFPKM < 2)
		{
			femaleFPKM = 2;
		}
		double ratio = maleFPKM/femaleFPKM;
		
		String ratioText = null;
		if(maleData.getStatus().equals("OK") && femaleData.getStatus().equals("OK"))
		{
			ratioText = PageUtility.formatValues(ratio, 2);
		}
		else
		{
			ratioText = "N.A.";
		}
		
		MeanComparator mc = new MeanComparator(femaleData, maleData);
		String signif = null;
		if(maleData.getStatus().equals("OK") && femaleData.getStatus().equals("OK"))
		{		
			signif = mc.getSignificance();
		}
		else
		{
			signif = "N.A.";
		}
		
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

	private String getTranscriptTableRows(GeneExpression expn, Gene gene, TissueCatalogue tCat)
	{
		StringBuilder sb = new StringBuilder();
	
		// Go through array of TranscriptTissueDataSet objects (each for a distinct transcript) held in Expression object
		// Might be better sorted by Transcript name suffix
		for (int i=0; i<expn.getTranscriptDataSize(); i++)
		{
			TranscriptTissueDataSet ttds = expn.getTranscriptData(i);
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
					TranscriptTissueData data = ttds.getTranscriptTissueDataByID(id);
					if(data != null)
					{
						Color abundCol = PageUtility.getAbundanceColor(data.getFPKM());
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
	}
	
	// Make ambiguity query and return results as string
	private String getAmbiguityData(String geneID)
	{
		// Prepare array to hold query results
		final int AMBIGUITY_LENGTH = 10;			// Length of array
		int ambiguitySize = 0;						// Occupancy of array
		Ambiguity[] ambigList = new Ambiguity[AMBIGUITY_LENGTH];
		
		// Make connection
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		ParamQuery ambigQy = DBQuery.getParamQuery("AMBIGUITY");
		try 
		{
			ambigQy.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	

		try 
		{
			PreparedStatement prepStat = ambigQy.getPrepStatement();
			prepStat.setString(1, geneID);
			prepStat.setString(2, geneID);
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{				
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{
					String maskedFBtr = resSet.getString("MaskedFBtr");
					String maskingFBtr = resSet.getString("MaskingFBtr");
					String maskedFBgn = resSet.getString("MaskedFBgn");
					String maskingFBgn = resSet.getString("MaskingFBgn");
					String ambiguityType = resSet.getString("AmbiguityType");
					
					Ambiguity ambig = new Ambiguity(maskedFBtr, maskingFBtr, maskedFBgn, maskingFBgn, ambiguityType);
					ambigList[ambiguitySize] = ambig;
					ambiguitySize++; 
				}
			}		
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		// close connection
		if(conn != null)
		{
			try { conn.close();}
			catch(Exception e){System.out.println("Can't close.");}
		}
		
		// Generate output string
		StringBuilder sb = new StringBuilder("");	// Avoids returning null if no results — Is this best practice?
		
		if(ambiguitySize>0)
		{		
			for(int i=0; i<ambiguitySize; i++)
			{
				String maskedFBtr = ambigList[i].getMaskedFBtr();
				String maskingFBtr = ambigList[i].getMaskingFBtr();
				String maskedFBgn = ambigList[i].getMaskedFBgn();
				String maskingFBgn = ambigList[i].getMaskingFBgn();
				String ambiguityType = ambigList[i].getAmbiguityType();
				
				if(ambiguityType.equals("Transcript"))
				{
					sb.append("<p><em>" + maskedFBtr + "</em> (gene " + maskedFBgn + ") differs from <em>" + maskingFBtr 
							+ "</em> (gene <a href='/FlyAtlas2/?search=gene&gene=" + maskingFBgn + "&idtype=fbgn'>" + maskingFBgn 
							+ "</a>) by only one or two 5′ bases, and these transcripts are annotated as a single Dicistronic mRNA on FlyBase. "
							+ "It may therefore be an artifact that distinct sets of RNAseq data have been generated for them.</p>\n");
				}				
				else if (geneID.equals(maskedFBgn) && !geneID.equals(maskingFBgn))	// Different gene for transcript masking this one
				{
					sb.append("<p>No data for <em>" + maskedFBtr + "</em> — included in <em>" + maskingFBtr 
							+  "</em> (gene <a href='/FlyAtlas2/?search=gene&gene=" + maskingFBgn + "&idtype=fbgn'>" + maskingFBgn + "</a>): " + ambiguityType + "</p>\n");
				}			
				else if((geneID.equals(maskingFBgn) && !geneID.equals(maskedFBgn)))	// Different gene for transcript this one is masking
				{
					sb.append("<p>Data for <em>" + maskingFBtr + "</em> include those for <em>" + maskedFBtr
							+ "</em> (gene <a href='/FlyAtlas2/?search=gene&gene=" + maskedFBgn + "&idtype=fbgn'>" + maskedFBgn + "</a>): " + ambiguityType + "</p>\n");
				}
				else if(geneID.equals(maskedFBgn) && geneID.equals(ambigList[i].getMaskingFBgn())) // Same gene for masked and masking
				{
					sb.append("<p>No data for <em>" + maskedFBtr + "</em> — included in <em>" + maskingFBtr + "</em>: " + ambiguityType + "</p>");
				}
			}
			
		}
	
		return sb.toString();
	}
	
	// INNER CLASS for Ambiguity Object
	class Ambiguity
	{
		String maskedFBtr; 
		String maskingFBtr;
		String maskedFBgn;	
		String maskingFBgn;
		String ambiguityType;
	
		String scrollLine = "";		// text
		Ambiguity(String maskedFBtr, String maskingFBtr, String maskedFBgn, String maskingFBgn, String ambiguityType)
		{
			this.maskedFBtr = maskedFBtr;
			this.maskingFBtr = maskingFBtr;
			this.maskedFBgn = maskedFBgn;
			this.maskingFBgn = maskingFBgn;
			this.ambiguityType =  ambiguityType;
			
			renameAmbiguities();
		}
		
		public void renameAmbiguities()
		{
			if (ambiguityType.equals("AltStart"))
			{
				ambiguityType = "Alternative Start codons in same reading frame";
			}
			else if (ambiguityType.equals("AltStop"))
			{
				ambiguityType = "Alternative Stop codons (readthrough)";				
			}			
			else if (ambiguityType.equals("Frameshift"))
			{
				ambiguityType = "Frameshift";				
			}			
			else if (ambiguityType.equals("Dicistronic"))
			{
				ambiguityType = "Di- or poly-cistronic mRNA";				
			}
		}
		
		public String getMaskedFBtr()
		{
			return maskedFBtr;
		}
		public String getMaskingFBtr()
		{
			return maskingFBtr;
		}
		public String getMaskedFBgn()
		{
			return maskedFBgn;
		}
		public String getMaskingFBgn()
		{
			return maskingFBgn;
		}
		public String getAmbiguityType()
		{
			return ambiguityType;
		}
	}
	
}
