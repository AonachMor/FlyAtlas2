/*
 Generates an HTML page for a "Top" search, with or without results
 Now flagged as "Tissue" as decided not to have tissue search with category, and Tissue is clearer.
 David P. Leader 10.08.20
 Last updated 21.09.2023
*/

public class TopPage
{	
	TissueCatalogue tCat;
	private boolean byAbundance = true;					// enrichment or abundance criterion
	private final int PAGE_POS = PageUtility.TISSUE;	// position of page in menu
	private StringBuilder htmlBuilder;					// for accumulating html output
	private String intro = "Find which genes have the greatest expression in a particular tissue.";
	
	// Constructor for page WITHOUT results
	public TopPage(TissueCatalogue tCat, boolean includeErrors, boolean showWhole, boolean sexStats, boolean microRNA)
	{		
		this.tCat = tCat;
		String sex = "";				// start with select instruction
		int tissueID = 0;				// start with no tissue selected
		byAbundance = false; 			// default enrichment
		int displayMax = 20;			// default is lowest value
		boolean atStart = true;			// start with search button dimmed
		
					//-------- Build initial page ----------//
		htmlBuilder = new StringBuilder();
		// Boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole, sexStats);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		
		htmlBuilder.append(getControls(sex, tissueID, byAbundance, displayMax, atStart, microRNA));

		htmlBuilder.append("</div> <!-- end of controls div -->\n");
		
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
	
	// Constructor for page WITH results (displayMax chosen by user, but totalDisplayed could be less than displayMax if fewer found)
	public TopPage(Expression[] expressList, Gene[] geneList, String sex, int tissueID, boolean byAbundance, int displayMax, 
			int totalDisplayed, TissueCatalogue tCat, boolean includeErrors, boolean showWhole, boolean sexStats, boolean microRNA)
	{
		this.tCat = tCat;
		boolean atStart = false;		// start with search button enabled
								//-------- Build results page ----------//
		htmlBuilder = new StringBuilder();
		// Boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole, sexStats);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		
		htmlBuilder.append(getControls(sex, tissueID, byAbundance, displayMax, atStart, microRNA));

		htmlBuilder.append("</div> <!-- end of controls div -->\n");
		
		// div just to act as target
		htmlBuilder.append("<div id=\"mobileTargetT\"></div>\n");
		
		// Build info line to appear above all results
		StringBuilder tissuePhrase = new StringBuilder();
		tissuePhrase.append(tCat.getStageByID(tissueID));
		tissuePhrase.append(" ");
		if(!tCat.getSexByID(tissueID).equals("Both"))
		{
			tissuePhrase.append(tCat.getSexByID(tissueID));	
			tissuePhrase.append(" ");
		}
		tissuePhrase.append(tCat.getTissueNameByID(tissueID));
		if(byAbundance)
		{
			tissuePhrase.append(", by abundance");		
		}
		else
		{
			if(totalDisplayed > 0)
			{
				tissuePhrase.append(", by enrichment");	
			}
			else
			{
				tissuePhrase.append(".");
			}
		}
		
		String geneExpressedPhrase = new String();
		String geneNotExpressedPhrase = new String();
		if(microRNA)
		{
			if(totalDisplayed == 1)
			{
				geneExpressedPhrase = " most expressed microRNA gene in ";		
			}
			else
			{
				geneExpressedPhrase = " most expressed microRNA genes in ";
			}
			geneNotExpressedPhrase = "No significantly enriched microRNA genes in ";
		}
		else
		{
			geneExpressedPhrase = " most expressed protein-coding genes in ";
			geneNotExpressedPhrase = "No significantly enriched protein-coding genes in ";
		}
		
		String revealAllPhrase = "<a href=\"javascript:toggleAll('master'," + totalDisplayed + ",'&#9658;','&#9660;');\" title=\"show all\"> <span id=\"master\" class=\"infoContent onOff\">&#9655;</span></a>";

		// deal with problem that no microRNA for Garland cells (also for any others)
		TissueTriplet tissTrip = tCat.getTissueTripletByTissueID(tissueID);
		if(microRNA && tissTrip.hasMir()==false)
		{
			htmlBuilder.append("<div class=\"explanation2\">MicroRNA data still pending for " + tCat.getTissueNameByID(tissueID) + ".</div><!-- end of explanation div -->\n");
		}
		else
		{
			if(totalDisplayed > 0)
			{
				htmlBuilder.append("<div class=\"explanation2\">Top " + totalDisplayed + geneExpressedPhrase + tissuePhrase.toString() + ":" + revealAllPhrase + "</div><!-- end of explanation div -->\n");
			}
			else
			{
				htmlBuilder.append("<div class=\"explanation2\">" + geneNotExpressedPhrase + tissuePhrase.toString() + "</div><!-- end of explanation div -->\n");
			}
		
		}
		
		
		// Go through each of the Expression objects in list and format results
		if(!microRNA)
		{
			for(int i=0; i<totalDisplayed; i++)
			{
				GeneExpression express = (GeneExpression) expressList[i];
				Gene gene = geneList[i];	
				if(expressList[i]!=null)
				{	
					GeneResult gr = new GeneResult(gene, tCat, express, i, true, includeErrors, showWhole,sexStats);
					htmlBuilder.append(gr.getResultsHTML());
				}
			}
			
			// Summary section
			String criterion = new String();
			if(byAbundance)
			{
				criterion = "by abundance";
			}
			else
			{
				criterion = "by enrichment";			
			}
			
			htmlBuilder.append("<div class=\"results\"> <!-- Start of summary section-->\n");	
			htmlBuilder.append("<div class=\"summarySet\">\n<div class=\"summary\">\n");
			htmlBuilder.append("<a href=\"javascript:toggleConcealed('sumBut','summaryList','&#9658;','&#9660;');\" title=\"reveal summary\"><span id=\"sumBut\" class=\"summaryButton onOff\">&#9658;</span></a>\n");
			htmlBuilder.append("</div>");
			if(tCat.getStageByID(tissueID).equals("Larval"))
			{
				htmlBuilder.append("<div class=\"summary\">\n<span class=\"summaryTitle\">Summary listing of top " + totalDisplayed + 
					" genes in " + tCat.getStageByID(tissueID) + " " + tCat.getTissueNameByID(tissueID) + ", " + criterion + "</span>\n</div>\n");
			}
			else
			{
				htmlBuilder.append("<div class=\"summary\">\n<span class=\"summaryTitle\">Summary listing of top " + totalDisplayed + 
						" genes in " + tCat.getStageByID(tissueID) + " " + tCat.getSexByID(tissueID) + " " + tCat.getTissueNameByID(tissueID) + ", " + criterion + "</span>\n</div>\n");
				
			}
			htmlBuilder.append("<div id=\"summaryList\" class=\"conceal\">\n");
						
			for(int i=0; i<totalDisplayed; i++)
			{
				htmlBuilder.append(geneList[i].getFBgn() + " | ");
				htmlBuilder.append(geneList[i].getAnnotationSymbol() + " | ");
				htmlBuilder.append(geneList[i].getSymbol() + "<br />");
			}
			
			htmlBuilder.append("</div>\n");	
			htmlBuilder.append("</div>\n");	
			htmlBuilder.append("</div><!-- end of summary section-->\n");	
		}
		else
		{
			for(int i=0; i<totalDisplayed; i++)
			{
				MirExpression express = (MirExpression) expressList[i];
				Gene gene = geneList[i];	
				if(expressList[i]!=null)
				{
					MirResult mr = new MirResult(gene, tCat, express, i, true, includeErrors, showWhole, sexStats);
					htmlBuilder.append(mr.getResultsHTML());
				}
			}
		}
			
		// Finish off with footer section	
		htmlBuilder.append(PageUtility.PAGE_FOOT);
	}
	
	// Build contents of controls div with instructions, selections and search button
	private String getControls(String sex, int tissueID, boolean byAbundance, int displayMax, boolean atStart, boolean microRNA)
	{
		String stageLine =  new String();	// sets selected stage for repeat	
		String tissueLine = new String();	// sets selected tissue if stage set
		
		StringBuilder controlBuilder = new StringBuilder();
		// start of div and instructions
		controlBuilder.append("<div id=\"controls\">\n");
		controlBuilder.append("First select a ‘stage’ and next the tissue of interest. Then choose ‘enrichment’ or ‘abundance’ for greatest expression:\n");
		controlBuilder.append("<p>\n"
				+ "<select name=\"stage\" id=\"stage\" onchange=\"processData(); return true;\">\n");
		controlBuilder.append("<option value=\" --- Select a Stage --- \"> --- Select a Stage --- </option>");
		
		// 1. set stageLine; 
		if(sex.equals("Male"))
		{
			stageLine = "<option value=\"Male\" selected=\"selected\">Adult Male</option>\n<option value=\"Female\">Adult Female</option>\n<option value=\"Both\">Larval</option>\n</select>&nbsp;<span class=\"immobileHide\"><br /></span>\n";
		}
		else if (sex.equals("Female"))
		{
			stageLine = "<option value=\"Male\">Adult Male</option>\n<option value=\"Female\" selected=\"selected\">Adult Female</option>\n<option value=\"Both\">Larval</option>\n</select>&nbsp;<span class=\"immobileHide\"><br /></span>\n";
		}
		else if (sex.equals("Both"))
		{
			stageLine = "<option value=\"Male\">Adult Male</option>\n<option value=\"Female\">Adult Female</option>\n<option value=\"Both\" selected=\"selected\">Larval</option>\n</select>&nbsp;<span class=\"immobileHide\"><br /></span>\n";
		}
		else
		{
			stageLine = "<option value=\"Male\">Adult Male</option>\n<option value=\"Female\">Adult Female</option>\n<option value=\"Both\">Larval</option>\n</select>&nbsp;<span class=\"immobileHide\"><br /></span>\n";
		}
		
		// 2. set tissueLine for Adult or Larval
		if(sex.equals("Male") || sex.equals("Female") || sex.equals("Both"))
		{
			StringBuilder tissBuilder = new StringBuilder("<select name=\"tissue\" id=\"tissue\">\n");	// starting <select>
						
			// Construct an array of Tissue objects from tCat to sort names for pulldown without altering array in common TissueCatalogue
			Tissue [] tissueArray = new Tissue[tCat.getTissueListSize()];
			for(int y=0; y<tissueArray.length; y++)
			{
				tissueArray[y] = tCat.getTissue(y);
			}
			tissueArray = sortTissuesForMenu(tissueArray);
			for(int i=0; i<tissueArray.length; i++)
			{
				Tissue ft = tissueArray[i];	// get next Tissue obj in list
				if(ft.getSex().equals(sex) && ft.getTissueID() == tissueID)		// correct sex and repeat id - set selected
				{
					tissBuilder.append("<option selected=\"selected\" value=\"" + ft.getTissueID() + "\">" + ft.getTissueName() + "</option>\n");
				}
				else if(ft.getSex().equals(sex) && !ft.isReference())						// correct sex but not whole
				{
					tissBuilder.append("<option value=\"" + ft.getTissueID() + "\">" + ft.getTissueName() + "</option>\n");
				}
			}	
			tissBuilder.append("</select><span class=\"immobileHide\"><br /></span>\n");	// ending </select>
			tissueLine = tissBuilder.toString();
		}	
		else		// at startup
		{
			tissueLine = "<select name=\"tissue\" id=\"tissue\">\n<option value=\"0\"> --- First select a Stage --- </option>\n</select>&nbsp;<span class=\"immobileHide\"><br /></span>\n";		
		}
		
		controlBuilder.append(stageLine + tissueLine);
		
		// 3. set enrichment or abundance choice
		controlBuilder.append("\n<select id=\"order\">\n");
		if(byAbundance)
		{
			controlBuilder.append("<option value=\"enrichment\">Enrichment</option>\n");
			controlBuilder.append("<option selected=\"selected\" value=\"abundance\">Abundance</option>\n</select>\n");
		}
		else
		{
			controlBuilder.append("<option selected=\"selected\" value=\"enrichment\">Enrichment</option>");
			controlBuilder.append("<option value=\"abundance\">Abundance</option></select>\n");
		}
		controlBuilder.append("</p>\n");
		
		// after line split from para have div containing two floating divs to allow right-alignment of search button
		controlBuilder.append("You may search for transcripts of protein-coding genes or microRNAs, and alter the number of &lsquo;top genes&rsquo; displayed:\n");	
		controlBuilder.append("<div class=\"standard\">\n");
		
		// 4. Protein or microRNA radio controls
		if(microRNA)
		{
			controlBuilder.append("<span style=\"white-space:nowrap\"><input type=\"radio\" name=\"RNAchoice\" id=\"progene\" />&nbsp;<span class=\"rightPad5\">protein-coding&nbsp;</span></span><span style=\"white-space:nowrap\"><input type=\"radio\" name=\"RNAchoice\" id=\"microgene\" checked=\"checked\" /><span class=\"rightPad15\">&nbsp;microRNA</span></span>");
		}
		else
		{
			controlBuilder.append("<span style=\"white-space:nowrap\"><input type=\"radio\" name=\"RNAchoice\" id=\"progene\" checked=\"checked\" />&nbsp;<span class=\"rightPad5\">protein-coding&nbsp;</span></span><span style=\"white-space:nowrap\"><input type=\"radio\" name=\"RNAchoice\" id=\"microgene\" /><span class=\"rightPad15\">&nbsp;microRNA</span></span>");
		}
				
		// 5. set max choice (20 to 50)
		controlBuilder.append("<span class=\"immobileHideS\"><br /></span><span style=\"white-space:nowrap\">Display:&nbsp;<select name=\"maxdisplayed\" id=\"maxdisplayed\" dir=\"rtl\">");
		for(int i=20; i<60; i+=10)
		{
			if(i==displayMax)
			{
				controlBuilder.append("<option selected=\"selected\" value=\"" + i + "\">" + i + "</option>");			
			}
			else
			{
				controlBuilder.append("<option value=\"" + i + "\">" + i + "</option>");		
			}
		}
		controlBuilder.append("</select></span>");
		
		// 6. Set availability of search button (disabled at start, enables at repeat)
		if(atStart)
		{
			controlBuilder.append("<button id=\"runButton\" disabled=\"disabled\" onclick=\"sendSearchTopForm();\">Search</button>");
		}
		else
		{
			controlBuilder.append("<button id=\"runButton\" onclick=\"sendSearchTopForm();\">Search</button>");
		}
		
		// End of div for second line
		controlBuilder.append("</div>\n");
	
		return controlBuilder.toString();
	}
	
	// sorts array of Tissue objects alphabetically by name for menus
	private Tissue[] sortTissuesForMenu(Tissue[] tissueArray)
	{
    	Tissue lowest;		// holder for TissueTriplet with lowest value
		int lowestPos;		
		for (int i=0; i<tissueArray.length-1; i++)		// do series of runs
		{
			for (int j=i+1; j<tissueArray.length; j++)	// for each run process list
			{
				lowest = tissueArray[i];				// first of unsorted assigned to lowest
				lowestPos = i;
				if(lowest.getTissueName().compareTo(tissueArray[j].getTissueName()) > 0)
				{
					lowestPos = j;
				}				
				lowest = tissueArray[lowestPos];
				tissueArray[lowestPos] = tissueArray[i]; 	// shift current first
				tissueArray[i] = lowest;					// replace
			}
		} 
		return tissueArray;
	}

	public String getHTML()
	{
		return htmlBuilder.toString();
	}

}