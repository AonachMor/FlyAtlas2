// Generates an HTML page for a Gene search, with or without results
// David P. Leader 09.08.2017
// Last update 21.09.2023

public class GenePage
{
	private StringBuilder htmlBuilder;					// For building HTML
	private final int PAGE_POS = PageUtility.GENE;		// Position of page in menu
	private String intro = "For a particular Drosophila gene, find the pattern of expression in different tissues.";		
	
	// Instantiate initial page with no results using defaults
	public GenePage(boolean includeErrors, boolean showWhole, boolean sexStats)
	{	
		htmlBuilder = new StringBuilder();
		// Build page starting with boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole, sexStats);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		htmlBuilder.append("<div id=\"controls\">\n");
		htmlBuilder.append("<div id=\"switchControls\" style=\"text-align:center;\">&nbsp;</div>\n");	// Added for Bulk upload
		
		htmlBuilder.append("<div id=\"controlsA\" style=\"display:block;\">\n");						// Added for Bulk upload
		// IDtype radio choice with FBgn selected
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"symbol\" checked=\"checked\" style=\"margin-top:15px;\" /> Gene Symbol (e.g. vkg) <span class=\"mobileHide\">— start typing, then select from the autosuggest menu</span><br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"name\" /> Gene Name (e.g. viking)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"cgnum\" /> Annotation Symbol (e.g. CG16858)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbgn\" /> Gene ID (e.g. FBgn0016075)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbtr\" /> Transcript ID (e.g. FBtr0079036)<br />\n");
		// gene descriptor field empty as default
		htmlBuilder.append("<p>\n<span class=\"rightPad5\">Gene: </span>");
		htmlBuilder.append("<input type=\"text\" autocomplete=\"off\" id=\"inputField\" value=\"\" style=\"height:15px;\" onkeyup=\"findGene();\" />");
		htmlBuilder.append("<button onclick=\"sendSearchGeneForm();\">Search</button>\n</p>\n");		
		// Div with hidden table for autocomplete
		htmlBuilder.append(PageUtility.AUTO_DIV);		
		htmlBuilder.append("</div> <!-- end of controlsA div -->\n");
		
		htmlBuilder.append("<div id=\"controlsB\" style=\"display:none;\">\n");
		htmlBuilder.append("<p>Enter or paste a list of gene identifiers into the text box, one per line (maximum 200 — microRNAs currently not supported)."
				+ "<br />All should be either annotation symbols (e.g. CG16858) or FlyBase IDs (e.g. FBgn0016075).</p>\n");
		htmlBuilder.append("<p><textarea name=\"idList\" id=\"idList\" rows=\"6\" cols=\"28\" placeholder=\"Enter list here\"></textarea><br />\n");
		htmlBuilder.append("<button id=\"runButton\" onclick=\"sendSearchBulkForm();\">Submit</button>\n");
		htmlBuilder.append("<button id=\"clearButton\" onclick=\"getElementById('idList').value='';\">Clear</button></p>\n");
		htmlBuilder.append("</div><!-- end of controlsB div -->\n");
		
		htmlBuilder.append("</div><!-- end of controls div -->\n");
	
		// hidden errors checkbox for start page only
		if(includeErrors)
		{
			htmlBuilder.append("<input style=\"display:none\" type=\"checkbox\" id=\"errors_0\" value=\"errors\" checked=\"checked\" />");			
		}
		else	// not really needed but useful for testing
		{
			htmlBuilder.append("<input style=\"display:none\" type=\"checkbox\" id=\"errors_0\" value=\"noerrors\" />");				
		}
		// hidden show whole checkbox 
		if(showWhole)
		{
			htmlBuilder.append("<input style=\"display:none\" type=\"checkbox\" id=\"whole_0\" value=\"whole\" checked=\"checked\" />");			
		}
		else	// not really needed but useful for testing
		{
			htmlBuilder.append("<input style=\"display:none\" type=\"checkbox\" id=\"whole_0\" value=\"hideWhole\" />");				
		}
		// hidden M v. F checkbox 
		if(sexStats)
		{
			htmlBuilder.append("<input style=\"display:none\" type=\"checkbox\" id=\"mf_0\" value=\"mf\" checked=\"checked\" />");			
		}
		else	// not really needed but useful for testing
		{
			htmlBuilder.append("<input style=\"display:none\" type=\"checkbox\" id=\"mf_0\" value=\"nomf\" />");				
		}
		
		// Finish off with footer section	
		htmlBuilder.append(PageUtility.PAGE_FOOT);
	}
	
	// Instantiate a results page from Expression object, rebuilding  using gene search term, idType (symbol, name etc)
	public GenePage(Gene gene, Expression expression, String searchTerm, String idType, TissueCatalogue  tCat, 
						boolean includeErrors, boolean showWhole, boolean sexStats, boolean bulk)
	{		
		// Build page starting with boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole, sexStats);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		
		htmlBuilder.append(getControls(searchTerm, idType, bulk));			// build controls with settings from previous search
		
		// div just to act as target
		htmlBuilder.append("<div id=\"mobileTargetG\"></div>\n");
		
		// RESULTS FORMATTED
		if(expression!=null)
		{
			if(expression instanceof GeneExpression)
			{
				GeneExpression expn = (GeneExpression) expression;
				int resNum = 0;				// for id
				boolean conceal = false; 	// whether to provide hide/show button
				GeneResult gr = new GeneResult(gene, tCat, expn, resNum, conceal, includeErrors, showWhole, sexStats);
				htmlBuilder.append(gr.getResultsHTML());
			}
			else if(expression instanceof MirExpression)
			{
				MirExpression mirExpn = (MirExpression) expression;
				int resNum = 0;				// for id
				boolean conceal = false; 	// whether to provide hide/show button
				MirResult mr = new MirResult(gene, tCat, mirExpn, resNum, conceal, includeErrors, showWhole, sexStats);
				htmlBuilder.append(mr.getResultsHTML());
			}			
		}
		else
		{
			htmlBuilder.append("<div class=\"explanation2\">");
			htmlBuilder.append("No results found for ‘" + searchTerm +"’.");
			htmlBuilder.append("</div><!-- end of explanation div -->");
		}
		// Finish off with footer section
		htmlBuilder.append(PageUtility.PAGE_FOOT);
	}
	
	// Instantiate a results page from results of bulk search
	public GenePage(Gene[] geneList, Expression[] expressList, String searchList, String idType, TissueCatalogue  tCat, boolean includeErrors, boolean showWhole, boolean sexStats, 
						boolean bulk, String[] invalidIDList, int numValidIDs, int numInvalidIDs)
	{		
		// Build page starting with boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole, sexStats);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		
		htmlBuilder.append(getControls(searchList, idType, bulk));			// build controls with settings from previous search
		
		// div just to act as target
		htmlBuilder.append("<div id=\"mobileTargetG\"></div>\n");
		
									// RESULTS FORMATTED	
		// Results info line
		int numOrigIDs = numValidIDs + numInvalidIDs;
		String revealAllPhrase = "<a href=\"javascript:toggleAll('master'," + numOrigIDs + ",'&#9658;','&#9660;');\" title=\"show all\"> <span id=\"master\" class=\"infoContent onOff\">&#9655;</span></a>";	
		if(numValidIDs > 0)
		{
			if(numValidIDs > 1)
			{
				if(numOrigIDs > 199)
				{
					htmlBuilder.append("<div class=\"explanation2\"> Expression results for " + numValidIDs + " genes from first 200 IDs in list submitted:" + revealAllPhrase + "</div><!-- end of explanation div -->\n");				
				}
				else
				{
					htmlBuilder.append("<div class=\"explanation2\"> Expression results for " + numValidIDs + " genes from list of " + numOrigIDs + " IDs submitted:" + revealAllPhrase + "</div><!-- end of explanation div -->\n");
				}
			}
			else
			{
				htmlBuilder.append("<div class=\"explanation2\"> Expression results for " + numValidIDs + " gene from list of " + numOrigIDs + " IDs submitted:" + revealAllPhrase + "</div><!-- end of explanation div -->\n");				
			}
		}
		else
		{
			if(idType.equalsIgnoreCase("none"))
			{
				htmlBuilder.append("<div class=\"explanation2\"> Invalid identifier for first gene! Must start with “FBgn” or “CG” to define list type.</div><!-- end of explanation div -->\n");
			}
			else
			{
				htmlBuilder.append("<div class=\"explanation2\"> No valid gene IDs in list submitted!</div><!-- end of explanation div -->\n");
			}
		}
		// Results
		if(geneList != null)
		{
			for(int i=0; i<geneList.length; i++)
			{
				GeneExpression express = (GeneExpression) expressList[i];
				Gene gene = geneList[i];	
				if(expressList[i]!=null)
				{	
					GeneResult gr = new GeneResult(gene, tCat, express, i, true, includeErrors, showWhole,sexStats);
					htmlBuilder.append(gr.getResultsHTML());
				}
			}
		}
		
		// Invalid IDs section		
		if(numInvalidIDs > 0 && !idType.equals("none"))
		{
			htmlBuilder.append("<div class=\"results\"> <!-- Start of invalid ID section-->\n");	
			htmlBuilder.append("<div class=\"summarySet\">\n<div class=\"summary\">\n");
			htmlBuilder.append("<a href=\"javascript:toggleConcealed('sumBut','summaryList','&#9658;','&#9660;');\" title=\"reveal summary\"><span id=\"sumBut\" class=\"summaryButton onOff\">&#9658;</span></a>\n");
			htmlBuilder.append("</div>");
			htmlBuilder.append("<div class=\"summary\">\n<span class=\"summaryTitle\">Gene IDs not found in database. "
					+ "See ‘Docs — Questions &amp Problems’ for possible reasons.</span>\n</div>\n");
			htmlBuilder.append("<div id=\"summaryList\" class=\"conceal\">\n");		
			for(int i=0; i<numInvalidIDs; i++)
			{
				htmlBuilder.append(invalidIDList[i] + "<br />");	
			}		
			htmlBuilder.append("</div>\n");	
			htmlBuilder.append("</div>\n");	
			htmlBuilder.append("</div><!-- end of invalid ID section-->\n");
		}
		
		// Finish off with footer section
		htmlBuilder.append(PageUtility.PAGE_FOOT);
	}
	
	// Controls
	private String getControls(String searchTerm, String idType, boolean bulk)
	{
		StringBuilder controlBuilder = new StringBuilder();	
		
		controlBuilder.append("<div id=\"controls\">\n");
		controlBuilder.append("<div id=\"switchControls\" style=\"text-align:center;\">&nbsp;</div>\n");	// Added for Bulk upload
		if(bulk)
		{
			controlBuilder.append("<div id=\"controlsA\" style=\"display:none;\">\n");
		}
		else
		{
			controlBuilder.append("<div id=\"controlsA\" style=\"display:block;\">\n");
		}
		// idtype radio choice  (disabling if appropriate to organism)
		if(idType.equals("symbol"))
		{
			controlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"symbol\" checked=\"checked\" style=\"margin-top:15px;\" /> Gene Symbol (e.g. vkg) <span class=\"mobileHide\">— start typing, then select from the autosuggest menu</span><br />\n");
		}
		else
		{
			controlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"symbol\" style=\"margin-top:15px;\" /> Gene Symbol (e.g. vkg) <span class=\"mobileHide\">— start typing, then select from the autosuggest menu</span><br />\n");
		}
		
		if(idType.equals("name"))
		{
			controlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"name\" checked=\"checked\"  /> Gene Name (e.g. viking)<br />\n");		
		}
		else
		{
			controlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"name\" /> Gene Name (e.g. viking)<br />\n");			
		}
		
		if(idType.equals("cgnum"))
		{
			controlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"cgnum\" checked=\"checked\" /> Annotation Symbol (e.g. CG16858)<br />\n");		
		}
		else
		{
			controlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"cgnum\" /> Annotation Symbol (e.g. CG16858)<br />\n");		
		}	

		if(idType.equals("fbgn"))
		{
			controlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbgn\" checked=\"checked\" /> Gene ID (e.g. FBgn0016075)<br />\n");
			//controlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbtr\" checked=\"checked\" /> Transcript ID (e.g. FBtr0079036)<br />\n");
		}
		else
		{
			controlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbgn\" /> Gene ID (e.g. FBgn0016075)<br />\n");
			//controlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbtr\" /> Transcript ID (e.g. FBtr0079036)<br />\n");
		}
		
		if(idType.equals("fbtr"))
		{
			//controlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbgn\" checked=\"checked\" /> Gene ID (e.g. FBgn0016075)<br />\n");
			controlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbtr\" checked=\"checked\" /> Transcript ID (e.g. FBtr0079036)<br />\n");
		}
		else
		{
			controlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbtr\" /> Transcript ID (e.g. FBtr0079036)<br />\n");
			//controlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbgn\" /> Gene ID (e.g. FBgn0016075)<br />\n");
		}
		
		// gene descriptor field with previous choice — modified so id is inputField
		controlBuilder.append("<p><span class=\"rightPad5\">Gene: </span>");
		if(bulk)
		{		
			controlBuilder.append("<input type=\"text\" autocomplete=\"off\" size=\"40\" id=\"inputField\" style=\"height:15px;\" onkeyup=\"findGene();\" />");		
		}
		else
		{
			controlBuilder.append("<input type=\"text\" autocomplete=\"off\" size=\"40\" id=\"inputField\" value=\"" + searchTerm + "\" style=\"height:15px;\" onkeyup=\"findGene();\" />");		
		}	
		controlBuilder.append("<button onclick=\"sendSearchGeneForm();\">Search</button>\n</p>\n");
			
		// Div with hidden table for autocomplete
		controlBuilder.append(PageUtility.AUTO_DIV);
		controlBuilder.append("</div> <!-- end of controlsA div -->\n");
		if(bulk)
		{
			controlBuilder.append("<div id=\"controlsB\" style=\"display:block;\">\n");
		}
		else
		{
			controlBuilder.append("<div id=\"controlsB\" style=\"display:none;\">\n");			
		}
		controlBuilder.append("<p>Enter or paste a list of gene identifiers into the text box, one per line (maximum 200 — microRNAs currently not supported)."
				+ "<br />All should be either annotation symbols (e.g. CG16858) or FlyBase IDs (e.g. FBgn0016075).</p>\n");
		if(bulk)
		{
			controlBuilder.append("<p><textarea name=\"idList\" id=\"idList\" rows=\"6\" cols=\"28\">" + searchTerm + "</textarea><br />\n");
		}
		else
		{
			controlBuilder.append("<p><textarea name=\"idList\" id=\"idList\" rows=\"6\" cols=\"28\" placeholder=\"Enter list here\"></textarea><br />\n");	
		}
		controlBuilder.append("<button id=\"runButton\" onclick=\"sendSearchBulkForm();\">Submit</button>\n");
		controlBuilder.append("<button id=\"clearButton\" onclick=\"getElementById('idList').value='';\">Clear</button></p>\n");
		controlBuilder.append("</div><!-- end of controlsB div -->\n");
		
		controlBuilder.append("</div><!-- end of controls div -->\n");	
		
		return controlBuilder.toString();
	}
	
	// returns page
	public String getHTML()
	{
		return htmlBuilder.toString();
	}

}
