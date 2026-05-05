// Generates an HTML page for a Category search, with or without results
// David P. Leader 08.08.2017
// Last update 21.09.2023

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CategoryPage 
{
	private StringBuilder htmlBuilder;					// For building HTML
	private final int PAGE_POS = PageUtility.CATEGORY;	// Position of page in menu
	private String intro = "Find how each gene of a particular category is expressed in different Drosophila tissues.<br />"
			+ "(For more information see <em>Docs — General Instructions for Use.</em>)";	
	
	private FBGroup[] groupList;						// Array of group objects (from inner class)
	private int GROUP_LENGTH = 200;						// Array Length to hold all FBgg groups in DB (currently 178)
	private int groupListSize = 0;
	
	// Instantiate initial page with no results using defaults
	public CategoryPage(boolean includeErrors, boolean showWhole, boolean sexStats)
	{	
		populateGroupList();				// set up groupList array		
		//-------- Build initial page ----------//
		htmlBuilder = new StringBuilder();
		// Boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole, sexStats);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		
		String keyword = "";				// initially keyword field is empty
		String radioChoice = "goTerm";		// initial radio setting
		int displayMax = 25;				// initial cutoff	
		String fbgg = "";					// initially group is empty
		htmlBuilder.append(getControls(keyword, radioChoice, displayMax, false, fbgg));		// build controls with initial settings
		
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
	public CategoryPage(Expression[] expressList, Gene[] geneList, int foundNum, int displayMax, int totalDisplayed, boolean includeErrors, 
							boolean showWhole, boolean sexStats, TissueCatalogue tCat, String keyword, String radioChoice, boolean gpSelect, String fbgg)
	{
		populateGroupList();				// set up groupList array
		// Boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole, sexStats);
		
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));	

		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		
		htmlBuilder.append(getControls(keyword, radioChoice, displayMax, gpSelect, fbgg));			// build controls with settings from previous search

		// div just to act as target
		htmlBuilder.append("<div id=\"mobileTargetC\"></div>\n");
		
		// Results info line
		String numGenes = "";
		String qualifier = "";
		if(foundNum == 0)
		{
			numGenes = "No genes";
		}
		else if(foundNum == 1)
		{
			numGenes = "1 gene";	
		}
		else
		{
			numGenes = foundNum + " genes";
		}
		if(foundNum > totalDisplayed)
		{
			qualifier = ", " + totalDisplayed + " shown";
		}
		
		String revealAllPhrase = "<a href=\"javascript:toggleAll('master'," + totalDisplayed + ",'&#9658;','&#9660;');\" title=\"show all\"> <span id=\"master\" class=\"infoContent onOff\">&#9655;</span></a>";
		
		if(gpSelect)
		{
			String gpName = getGroupName(fbgg); // make DB call to get the actual text					
			htmlBuilder.append("<div class=\"explanation2\" id=\"groupRes\">" + numGenes + " found from the group &lsquo;" + gpName + "&rsquo;" + qualifier + ":" + revealAllPhrase + "</div><!-- end of explanation div -->\n");
			// id needed for javascript hide/show reset
		}
		else
		{
			if(foundNum > 0)
			{
				htmlBuilder.append("<div class=\"explanation2\">" + numGenes + " found using the search term &lsquo;" + keyword + "&rsquo;" + qualifier + ":" + revealAllPhrase + "</div><!-- end of explanation div -->\n");
			}
			else
			{
				htmlBuilder.append("<div class=\"explanation2\">" + numGenes + " found using the search term &lsquo;" + keyword + "&rsquo;" + ".</div><!-- end of explanation div -->\n");			
			}
		}

		if(totalDisplayed > 0)
		{
			// Results		
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
			htmlBuilder.append("<div class=\"results\"> <!-- Start of summary section-->\n");	
			htmlBuilder.append("<div class=\"summarySet\">\n<div class=\"summary\">\n");
			htmlBuilder.append("<a href=\"javascript:toggleConcealed('sumBut','summaryList','&#9658;','&#9660;');\" title=\"reveal summary\"><span id=\"sumBut\" class=\"summaryButton onOff\">&#9658;</span></a>\n");
			htmlBuilder.append("</div>");
			
			// Customize for search type
			if(gpSelect)
			{
				String gpName = getGroupName(fbgg); // make DB call to get the actual text	
				htmlBuilder.append("<div class=\"summary\">\n<span class=\"summaryTitle\">Summary listing of the " + totalDisplayed + 
					" genes retrieved from the group &lsquo;" + gpName + "&rsquo;" + ":" + "</span>\n</div>\n");
			}
			else
			{
				htmlBuilder.append("<div class=\"summary\">\n<span class=\"summaryTitle\">Summary listing of the " + totalDisplayed + 
						" genes retrieved using the search term &lsquo;" + keyword + "&rsquo;" + ":" + "</span>\n</div>\n");
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

		// Finish off with footer section	
		htmlBuilder.append(PageUtility.PAGE_FOOT);
	}
	
	// Builds controls section based on initial or previous choices
	private String getControls(String keyword, String radioChoice, int displayMax, boolean gpSelect, String fbgg)
	{
		StringBuilder controlBuilder = new StringBuilder();	
		controlBuilder.append("<div id=\"controls\">\n");
		
		// Instructions to switch between two sets of controls (A and B)
		controlBuilder.append("<div id=\"switchControls\" style=\"text-align:center;\">&nbsp;</div>\n");
									
													/* controlsA */
		if(gpSelect)
		{
			controlBuilder.append("<div id=\"controlsA\" style=\"display:none;\">\n");
		}
		else
		{
			controlBuilder.append("<div id=\"controlsA\" style=\"display:block;\">\n");
		}
		
		controlBuilder.append("<p>Click a radio button below, enter appropriate search term, then press ‘Search’</p>\n");
		
		// 1. Build the three lines of radioBlock with appropriate selection — default or previous radioChoice
		if(radioChoice.equals("goTerm") || radioChoice.equals(""))
		{
			controlBuilder.append("<input type=\"radio\" name=\"goField\" id=\"goTerm\" checked=\"checked\" /> Term of interest <span class=\"mobileHide\">(e.g. ‘wing’ or ‘kinase’ — then select from the autosuggest menu.)</span><br />\n");			
		}
		else
		{
			controlBuilder.append("<input type=\"radio\" name=\"goField\" id=\"goTerm\" /> Category <span class=\"mobileHide\">(Type a term of interest — e.g. ‘wing’ or ‘kinase’ — then select from the autosuggest menu.)</span><br />\n");						
		}
		if(radioChoice.equals("goID"))
		{
			controlBuilder.append("<input type=\"radio\" name=\"goField\" id=\"goID\" checked=\"checked\" /> Gene Ontology ID (e.g. 0005201 — omit ‘GO:’)<br />\n");			
		}
		else
		{
			controlBuilder.append("<input type=\"radio\" name=\"goField\" id=\"goID\" /> Gene Ontology ID (e.g. 0005201 — omit ‘GO:’)<br />\n");		
		}		
		if(radioChoice.equals("goFree"))
		{
			controlBuilder.append("<input type=\"radio\" name=\"goField\" id=\"goFree\" checked=\"checked\" /> Free Search <span class=\"mobileHide\">(Type a partial or complete term — e.g. ‘mitochon’. No autosuggest, all matching categories searched.)</span><br />\n");		
		}
		else
		{
			controlBuilder.append("<input type=\"radio\" name=\"goField\" id=\"goFree\" /> Free Search <span class=\"mobileHide\">(Type a partial or complete term — e.g. ‘mitochon’. No autosuggest, all matching categories searched.)</span><br />\n");			
		}	
		
		// 2. Build term field of search line 
		controlBuilder.append("<p>\n<span class=\"rightPad5\">Search Term: </span>");
		controlBuilder.append("<input type=\"text\" autocomplete=\"off\" id=\"inputField\" value=\"" + keyword + "\" style=\"height:15px;\" onkeyup=\"findGO();\" />");		
		
		// 3. Build maxDisplay drop-down of search line 
		controlBuilder.append("<span class=\"immobileHide\"><br /></span><span class=\"rightPad5\">Display:</span><select name=\"maxdisplayed\" id=\"maxdisplayed\" dir=\"rtl\">");
	
		if(displayMax==25) {controlBuilder.append("<option selected=\"selected\" value=\"25\">25</option>");}
		else {controlBuilder.append("<option value=\"25\">25</option>");}
		if(displayMax==50) {controlBuilder.append("<option selected=\"selected\" value=\"50\">50</option>");}
		else{controlBuilder.append("<option value=\"50\">50</option>");}
		if(displayMax==100) {controlBuilder.append("<option selected=\"selected\" value=\"100\">100</option>");}
		else {controlBuilder.append("<option value=\"100\">100</option>");}
		if(displayMax==250) {controlBuilder.append("<option selected=\"selected\" value=\"250\">250</option>");}
		else{controlBuilder.append("<option value=\"250\">250</option>");}			
		
		controlBuilder.append("</select>");	
		
		// 4. Add search button search line		
		controlBuilder.append("<button onclick=\"sendSearchGoForm();\">Search</button>\n</p>\n");
		
		// Div with hidden table for autocomplete
		controlBuilder.append(PageUtility.AUTO_DIV);
		
		controlBuilder.append("</div><!-- end of controlsA div -->\n");
		
												/* controlsB */
		
		if(gpSelect)
		{
			controlBuilder.append("<div id=\"controlsB\" style=\"block;\">\n<p>\n");
		}
		else
		{
			controlBuilder.append("<div id=\"controlsB\" style=\"display:none;\">\n<p>\n");
		}
		controlBuilder.append("<select name=\"groupID\" id=\"groupID\">\n");

		// Simple list always starting at bottom until add variable
		if(fbgg.equals(""))		// This covers at start, but also if happens to be first item as none other will be selected
		{
			controlBuilder.append("<option value=\"" + groupList[0].getGroupID() + "\" selected=\"selected\">" + groupList[0].getGroupName() + "</option>");			
		}
		else
		{
			controlBuilder.append("<option value=\"" + groupList[0].getGroupID() + "\">" + groupList[0].getGroupName() + "</option>");
		}
		
		for(int i=1; i<groupListSize; i++)
		{			
			if(fbgg.equals(groupList[i].getGroupID()))
			{
				controlBuilder.append("<option value=\"" + groupList[i].getGroupID() + "\" selected=\"selected\">" + groupList[i].getGroupName() + "</option>");	
			}
			else
			{
				controlBuilder.append("<option value=\"" + groupList[i].getGroupID() + "\">" + groupList[i].getGroupName() + "</option>");
			}
		}
				
		controlBuilder.append("</select>\n");
		controlBuilder.append("<button id=\"runButton\" onclick=\"sendSearchGroupForm();\">Search</button>\n</p>\n");
		
		controlBuilder.append("<p>Select a group from the list above, then press ‘Search’.</p>\n");
		
		controlBuilder.append("</div><!-- end of controlsB div -->\n");
		
		// End controls div
		controlBuilder.append("</div> <!-- end of controls div -->\n");
		
		return controlBuilder.toString();
	}
	
	// Make DB call to create list of all keywords to populate drop-down
	private void populateGroupList()
	{
		groupList = new FBGroup [GROUP_LENGTH];			// initialize list of FBGroup objects for drop-down		
		String query = DBQuery.getGroupQuery();
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		try 
		{		
			Statement stmt = conn.createStatement();
			ResultSet resSet = stmt.executeQuery(query);
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{
					String gpName = resSet.getString("GroupName");
					String gpID = resSet.getString("GroupID");
					FBGroup newGroup = new FBGroup(gpName, gpID); 
					groupList[groupListSize] = newGroup;
					groupListSize++;
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		finally // close the connection
		{
			if(conn != null)
			{
				try { conn.close();}
				catch(Exception e){System.out.println("Can't close.");}
			}
		}
	}
	
	// Make DB call to get "GroupName" corresponding to a "GroupID"
	private String getGroupName(String groupID)
	{
		String groupName = "";
		String nameQuery = "GROUPNAME_FROM_GROUPID";
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		ParamQuery parQy = DBQuery.getParamQuery(nameQuery);
		try 
		{
			parQy.setPrepStatement(conn);
		} 
		catch (SQLException e) {System.out.println(e.toString());}	
		try 
		{
			PreparedStatement prepStat = parQy.getPrepStatement();
			prepStat.setString(1, groupID);
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())	// move to single tuple
			{
				groupName = resSet.getString("groupName");
			}
		}
		catch (SQLException e)
		{
			System.out.println(e.toString());
		}
		finally // close the connection
		{
			if(cnt != null)
			{
				try { conn.close();}
				catch(Exception e){System.out.println("Can't close.");}
			}
		}		
		return groupName;
	}
	
	// inner class to hold a utility FlyBase Group object	
	class FBGroup
	{
		String groupName;			// name of page as it appears on the menu
		String groupID;		// name of javascript method to generate new page	
		FBGroup(String groupName, String groupID)
		{
			this.groupName = groupName;
			this.groupID = groupID;
		}
		public String getGroupName()
		{
			return groupName;
		}
		public String getGroupID()
		{
			return groupID;
		}
	}
	
	// returns page
	public String getHTML()
	{
		return htmlBuilder.toString();
	}
}
