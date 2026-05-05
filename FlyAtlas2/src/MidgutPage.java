// Generates an HTML page for a Midgut search, with or without results
// DPL 21.09.2023

public class MidgutPage
{
	private StringBuilder htmlBuilder;		// For building HTML
	private final int PAGE_POS = PageUtility.MIDGUT;		// Position of page in menu
	private String intro = "Find the expression of a particular gene in different sections of the midgut of Drosophila larvae.";
	
	// Instantiate initial page with no results using defaults
	public MidgutPage(boolean includeErrors, boolean showWhole, boolean sexStats)
	{			
		htmlBuilder = new StringBuilder();
		// Build page starting with boiler-plate sections	
		PageUtility pu = new PageUtility(includeErrors, showWhole, sexStats);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		htmlBuilder.append("<div><p style=\"text-align:center;padding-left:20px;padding-right:20px;\"><img class=\"shrinkfit\" src=\"images/GutKey.jpg\" alt=\"\" /></p></div>");
		htmlBuilder.append("<div id=\"controls\">\n");
		// idtype radio choice with fbgn selected
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"symbol\" checked=\"checked\" /> Gene Symbol (e.g. vkg) — start typing, then select from the autosuggest menu<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"name\" /> Gene Name (e.g. viking)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"cgnum\" /> Annotation Symbol (e.g. CG16858)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbgn\" /> Gene ID (e.g. FBgn0016075)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbtr\" /> Transcript ID (e.g. FBtr0079036)<br />\n");
		// gene descriptor field empty as default
		htmlBuilder.append("<p>\n<span class=\"rightPad5\">Gene: </span>");
		htmlBuilder.append("<input type=\"text\" autocomplete=\"off\" id=\"inputField\" value=\"\" style=\"height:15px;\" onkeyup=\"findGene();\" />");
		htmlBuilder.append("<button onclick=\"sendSearchMidgutForm();\">Search</button>\n</p>\n");
		// Div with hidden table for autocomplete
		htmlBuilder.append(PageUtility.AUTO_DIV);
		htmlBuilder.append("</div> <!-- end of controls div -->\n");

		// hidden errors checkbox for start page only (not relevant to Midgut, but need to carry over between pages)
		if(includeErrors)
		{ htmlBuilder.append("<input style=\"display:none\" type=\"checkbox\" id=\"errors_0\" value=\"errors\" checked=\"checked\" />"); }
		// hidden show whole checkbox 
		if(showWhole)
		{ htmlBuilder.append("<input style=\"display:none\" type=\"checkbox\" id=\"whole_0\" value=\"whole\" checked=\"checked\" />"); }
		// hidden M v. F checkbox 
		if(sexStats)
		{ htmlBuilder.append("<input style=\"display:none\" type=\"checkbox\" id=\"mf_0\" value=\"mf\" checked=\"checked\" />"); }

		// Finish off with footer section
		htmlBuilder.append(PageUtility.PAGE_FOOT);
	}

	// Instantiate a results page from Experiment object, 
	// rebuilding page using midgutGene search term, idType (symbol, name etc)
	public MidgutPage(MidgutGene midgutGene, MidgutExpression expr, String searchTerm, String idType, MidgutCatalogue  mgCat,
						boolean includeErrors, boolean showWhole, boolean sexStats, boolean isMir)
	{	
		// Build page starting with boiler-plate sections	
		PageUtility pu = new PageUtility(includeErrors, showWhole, sexStats);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		htmlBuilder.append("<div><p style=\"text-align:center;padding-left:20px;padding-right:20px;\"><img class=\"shrinkfit\" src=\"images/GutKey.jpg\" alt=\"\" /></p></div>");
		htmlBuilder.append("<div id=\"controls\">\n");
		// idtype radio choice  (disabling if appropriate to organism)
		if(idType.equals("symbol"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"symbol\" checked=\"checked\" /> Gene Symbol (e.g. vkg) — start typing, then select from the autosuggest menu<br />\n");
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"symbol\" /> Gene Symbol (e.g. vkg) — start typing, then select from the autosuggest menu<br />\n");
		}

		
		if(idType.equals("name"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"name\" checked=\"checked\"  /> Gene Name (e.g. viking)<br />\n");		
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"name\" /> Gene Name (e.g. viking)<br />\n");			
		}

		
		if(idType.equals("cgnum"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"cgnum\" checked=\"checked\" /> Annotation symbol (e.g. CG16858)<br />\n");		
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"cgnum\" /> Annotation symbol (e.g. CG16858)<br />\n");		
		}	

		if(idType.equals("fbgn"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbgn\" checked=\"checked\" /> Gene ID (e.g. FBgn0016075)<br />\n");
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbgn\" /> Gene ID (e.g. FBgn0016075)<br />\n");
		}
		
		if(idType.equals("fbtr"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbtr\" checked=\"checked\" /> Transcript ID (e.g. FBtr0079036)<br />\n");
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbtr\" /> Transcript ID (e.g. FBtr0079036)<br />\n");
		}
		
		// gene descriptor field with previous choice
		htmlBuilder.append("<p>\n<span class=\"rightPad5\">Gene: </span>");
		htmlBuilder.append("<input type=\"text\" autocomplete=\"off\" size=\"40\" id=\"inputField\" value=\"" + searchTerm + "\" style=\"height:15px;\" onkeyup=\"findGene();\" />");		
		htmlBuilder.append("<button onclick=\"sendSearchMidgutForm();\">Search</button>\n</p>\n");

		// Div with hidden table for autocomplete
		htmlBuilder.append(PageUtility.AUTO_DIV);
		htmlBuilder.append("</div> <!-- end of controls div -->\n");	
		
		// RESULTS FORMATTED
		if(expr!=null)
		{
			htmlBuilder.append(expr.getHTMLFormatted(midgutGene, searchTerm, mgCat));
		}
		else
		{
			htmlBuilder.append("<div class=\"explanation2\">");
			if(isMir)
			{
				htmlBuilder.append("Gene ‘" + searchTerm + "’ is a microRNA, for which there are no midgut section data.");
			}
			else
			{
				htmlBuilder.append("No results found for ‘" + searchTerm +"’.");				
			}
			htmlBuilder.append("</div><!-- end of explanation div -->");
		}
		// Finish off with footer section
		htmlBuilder.append(PageUtility.PAGE_FOOT);
	}
	
	// returns page
	public String getHTML()
	{
		return htmlBuilder.toString();
	}

}
