 
/*
FlyAtlas 2 
Updated 07.11.2021 for Tomcat 8.5 UTF-8 handling of parameters
Last update 19.06.2023
*/

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class FlyAtlas2 extends HttpServlet 
{
	private TissueCatalogue  tCat;		// stores info about all fly tissues and stages: passed to classes that need to display results
	private MidgutCatalogue mgCat;		// Ditto for Midgut
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
	{	
		// Set Content type
		res.setContentType("text/html;charset=UTF-8");	
		res.setCharacterEncoding("UTF-8");
		res.setHeader("Cache-Control", "no-cache");
		
		// Set security headers
		res.setHeader("X-Frame-Options", "deny");
		res.setHeader("X-Content-Type-Options", "nosniff");
		res.setHeader("X-XSS-Protection", "1; mode=block");
		
		// Do stuff and respond. NB Don't get PrintWriter until ContentType has been set
		PrintWriter writer = res.getWriter();
		
		// tCat = new TissueCatalogue();		// Uncomment for testing to allow page to refresh after code change
		// mgCat = new MidgutCatalogue();		// Do.

		/* CHECK & SET SEARCH PARAMETERS */
		boolean includeErrors = false;
		if (req.getParameter("errors")!=null)		// include SDs
		{
			includeErrors = true;		// JavaScript only creates form with hard-coded value if first error box is checked
		}
		
		boolean showWhole = false;
		if (req.getParameter("whole")!=null)		// include Whole Body data
		{
			showWhole = true;			// JavaScript only creates form with hard-coded value if first "show whole" box is checked
		}
		
		boolean sexStats = false;
		if (req.getParameter("mf")!=null)		// include M v. F data
		{
			sexStats = true;			// JavaScript only creates form with hard-coded value if first "M v. F" box is checked
		}
		
		boolean microRNA = false;
		if (req.getParameter("microgene") != null)	// include on protein-coding genes (for Top)
		{
			microRNA = true;
		}
		
		/* BUILD START PAGE ON LAUNCH */
		if (req.getParameter("page") == null  && req.getParameter("search") == null)	// Defines startup 
		{		
			HomePage home = new HomePage(includeErrors, showWhole, sexStats);
			writer.println(home.getHome());	
			
			tCat = new TissueCatalogue();			// Set once at startup! — comment out for testing and set above
			mgCat = new MidgutCatalogue();			// Do.
		}
		
		/* OR BUILD START PAGES ACCESSED BY LINK FROM OTHER PAGE */
		else if (req.getParameter("page") != null)	// Page request identification to distinguish from results pages
		{		
			if (req.getParameter("page").equals("gene"))			// Gene page
			{
				GenePage genePage = new GenePage(includeErrors, showWhole, sexStats);
				writer.println(genePage.getHTML());
			}
			else if (req.getParameter("page").equals("go"))			// GO page
			{
				CategoryPage categoryPage = new CategoryPage(includeErrors, showWhole, sexStats);
				writer.println(categoryPage.getHTML());
			}
			else if (req.getParameter("page").equals("top"))		// Top page
			{
				TopPage topPage = new TopPage(tCat, includeErrors, showWhole, sexStats, microRNA);
				writer.println(topPage.getHTML());
			}
			else if (req.getParameter("page").equals("profile"))	// Profile page
			{
				ProfilePage profilePage = new ProfilePage(tCat, includeErrors, showWhole, sexStats);
				writer.println(profilePage.getHTML());
			}
			else if (req.getParameter("page").equals("midgut"))			// MidgutGene (midgut) page
			{
				MidgutPage midgutPage = new MidgutPage(includeErrors, showWhole, sexStats);
				writer.println(midgutPage.getHTML());
			}
			else if (req.getParameter("page").equals("home"))		// Home page
			{
				HomePage home = new HomePage(includeErrors, showWhole, sexStats);
				writer.println(home.getHome());
			}
			else if (req.getParameter("page").equals("contact"))		// Feedback page (note use of "contact")
			{
				FeedbackPage feedback = new FeedbackPage(includeErrors, showWhole, sexStats);
				writer.println(feedback.getFeedback());
			}
			else if (req.getParameter("page").equals("help"))		// Documentation page (note use of "help")
			{
				HelpPage help = new HelpPage(includeErrors, showWhole, sexStats);
				writer.println(help.getHelp());
			}
		}	
		
		/* OR BUILD SEARCH PAGE OF APPROPRIATE TYPE */
		else if (req.getParameter("search").equals("gene"))	
		{		
			String searchTerm = req.getParameter("gene");						// Parameter to specify value of gene id		
			// searchTerm = new String(searchTerm.getBytes("8859_1"), "UTF-8");	// Tomcat 8 — Uncomment for Tomcat 6
			searchTerm = searchTerm.trim();										//trim whitespace		
			// find type of gene identifier
			String idType = req.getParameter("idtype");			// Parameter to specify whether FBgn, FBtr, Gene symbol, Gene name or CGnum)
			idType = idType.replaceAll("[^a-zA-Z0-9]", "");		// Prevent cross-scripting	
			// start search
			GeneSearch search = new GeneSearch(searchTerm, idType, tCat);		
			// retrieve info
			Gene gene = search.getGene(); 		
			
			GenePage genePage = null;
			boolean bulk = false;	// Single gene search
			if (!search.isMir())
			{
				GeneExpression expn = search.getExpression();				// retrieve results					
				// construct HTML page, return it, and close the print writer
	    		genePage = new GenePage(gene, expn, searchTerm, idType, tCat, includeErrors, showWhole, sexStats, bulk);
			}
			else
			{
				MirExpression mirExpn = search.getMirExpression();			// retrieve results	
				// construct HTML page, return it, and close the print writer
	    		genePage = new GenePage(gene, mirExpn, searchTerm, idType, tCat, includeErrors, showWhole, sexStats, bulk);				
			} 		
    		
    		writer.println(genePage.getHTML());
			writer.close();
		}
		else if (req.getParameter("search").equals("bulk"))
		{
			boolean bulk = true;
			String searchList = req.getParameter("geneList");
			// searchList = new String(searchList.getBytes("8859_1"), "UTF-8");	// Tomcat 8 — Uncomment for Tomcat 6
			searchList = searchList.replaceAll("[^a-zA-Z0-9\r\n]", "");		// Prevent cross-scripting		
			searchList = searchList.trim();									// trim still needed for e.g. first-line return	

			BulkSearch search = new BulkSearch(searchList, tCat);
			
			Expression[] expressList = search.getExpressList();
			Gene[] geneList = search.getGeneList();
			String[] invalidIDList = search.getInvalidIDList();
			String idType = search.getIDtype();
			int numValidIDs = search.getNumValidIDs();
			int numInvalidIDs = search.getNumInvalidIDs();
			
			GenePage genePage = new GenePage(geneList, expressList, searchList, idType, tCat, includeErrors, showWhole, sexStats, 
								bulk, invalidIDList, numValidIDs, numInvalidIDs);
    		writer.println(genePage.getHTML());
			writer.close();			
		}
		else if (req.getParameter("search").equals("midgut"))	
		{	
			String searchTerm = req.getParameter("gene");					// Parameter to specify value of gene id		
			// searchTerm = new String(searchTerm.getBytes("8859_1"), "UTF-8");	// Tomcat 8 — Uncomment for Tomcat 6
			searchTerm = searchTerm.trim();									//trim whitespace		
			// find type of gene identifier
			String idType = req.getParameter("idtype");			// Parameter to specify whether FBgn, Gene symbol, Gene name or CGnum)
			idType = idType.replaceAll("[^a-zA-Z0-9]", "");		// Prevent cross-scripting	
				
			// start search
			MidgutSearch search = new MidgutSearch(searchTerm, idType, mgCat);
			// retrieve info
			MidgutGene gene = search.getGene(); 			
			MidgutExpression expn = search.getExpression();				// retrieve results		
			boolean isMir = search.isMir;

			// construct HTML page, return it, and close the print writer
			MidgutPage midgutPage = new MidgutPage(gene, expn, searchTerm, idType, mgCat, includeErrors, showWhole, sexStats, isMir);

    		writer.println(midgutPage.getHTML());
			writer.close();
		}
		else if (req.getParameter("search").equals("profile"))
		{
			// get parameters
			String geneQuery = req.getParameter("gene");
			// geneQuery = new String(geneQuery.getBytes("8859_1"), "UTF-8");	// Tomcat 8 — Uncomment for Tomcat 6
			geneQuery = geneQuery.trim();		
			String idType = req.getParameter("idtype");
			String profTiss = req.getParameter("tissues");
			
			boolean byPearson = true;
			if(req.getParameter("correlation").equals("spearman"))
			{
				byPearson = false;
			}
			String rString = req.getParameter("rcut");
			double rCut = Double.parseDouble(rString);
			int displayMax = Integer.parseInt(req.getParameter("maxdisplayed"));
			
			// get GeneExpression/MirExpression object and FBgn corresponding to geneQuery
			GeneSearch geneSearch = new GeneSearch(geneQuery, idType, tCat);
			Gene searchGene = geneSearch.getGene();
					
			ProfilePage profilePage = null;
			boolean isMir = geneSearch.isMir();
			
			if(isMir == false)
			{
				GeneExpression expression = geneSearch.getExpression();				
				String queryFBgn = new String();				
				if(expression != null)
				{
					queryFBgn = geneSearch.getGene().getFBgn();
				}		
				 else		// abort
				{
					profilePage = new ProfilePage (geneQuery, idType, queryFBgn, null, null, null, null, null, displayMax, 
							tCat, profTiss, byPearson, rString, includeErrors, showWhole, sexStats, isMir);
		    		writer.println(profilePage.getHTML());
					return;
				}
				
				ProfileSearch search = new ProfileSearch(queryFBgn, expression, profTiss, byPearson, rCut, displayMax, tCat);
				Expression[] expressList = search.getExpressList();
				Gene[] geneList = search.getGeneList();	
				ProfileTissueData[] profileDataList = search.getDataList();

				profilePage = new ProfilePage (geneQuery, idType, queryFBgn, searchGene, expression, expressList, geneList, profileDataList, displayMax, 
						tCat, profTiss, byPearson, rString, includeErrors, showWhole, sexStats, isMir);
			}
			else				// abort as mirs not yet supported for profile search
			{				
				profilePage = new ProfilePage (geneQuery, idType, null, null, null, null, null, null, displayMax, 
						tCat, profTiss, byPearson, rString, includeErrors, showWhole, sexStats, isMir);			
			}
			
    		writer.println(profilePage.getHTML());
			writer.close();			
		}		

		else if(req.getParameter("search").equals("go"))
		{					
			String goWord = req.getParameter("go");		// parameter for SQL
			// goWord = new String(goWord.getBytes("8859_1"), "UTF-8");	// Tomcat 8 — Uncomment for Tomcat 6
			goWord = goWord.trim();	//trim whitespace
			
			// this was previously with errors etc, at start
			String radioGo = new String();					// radio button checked for type of GO selection
			if (req.getParameter("radioGo") != null)
			{
				radioGo = req.getParameter("radioGo");
			}
			else
			{
				radioGo = "goTerm";
			}
			int maxDisplayed = Integer.parseInt(req.getParameter("maxdisplayed"));
			
			CategorySearch catSearch = new CategorySearch(goWord, radioGo, maxDisplayed, tCat);
			
			Expression[] expressList = catSearch.getExpressList();
			Gene[] geneList = catSearch.getGeneList();
			int actualDisplayed = catSearch.getActualDisplayed();
			int foundNum = catSearch.getFBgnListSize();
			
			String fbgg = "";		// dummy — as only used for group category search
			
			CategoryPage catPage = new CategoryPage(expressList, geneList, foundNum, maxDisplayed, actualDisplayed, 
													includeErrors, showWhole, sexStats, tCat, goWord, radioGo, false, fbgg);
    		writer.println(catPage.getHTML());
			writer.close();
		}	
		
		else if(req.getParameter("search").equals("group"))
		{
			String groupID = req.getParameter("groupID");
			// groupID = new String(groupID.getBytes("8859_1"), "UTF-8");	// Tomcat 8 — Uncomment for Tomcat 6
			
			CategorySearchGrp catSearchGp = new CategorySearchGrp(groupID, tCat);
			
			Expression[] expressList = catSearchGp.getExpressList();
			Gene[] geneList = catSearchGp.getGeneList();
			int actualDisplayed = catSearchGp.getActualDisplayed();
			int foundNum = catSearchGp.getFBgnListSize();
			
			final int MAX_DISPLAYED = 250;	// This is same as max allowed for selection mode in GO (DB fixed so not exceeded)
			String goWord = "";				// dummy — as only used for text entry category search
			String radioGo = "";			// dummy — as only used for text entry category search

			CategoryPage catPage = new CategoryPage(expressList, geneList, foundNum, MAX_DISPLAYED, actualDisplayed, 
													includeErrors, showWhole, sexStats, tCat, goWord, radioGo, true, groupID);

    		writer.println(catPage.getHTML());
			writer.close();			
		}
		
		else if (req.getParameter("search").equals("top"))
		{
			String sex = req.getParameter("sex");
			int tissueID = Integer.parseInt(req.getParameter("tissue"));
			String order = req.getParameter("order");
			boolean byAbundance = true;	
			if (order.equals("enrichment"))
			{
				byAbundance=false;
			}
			int maxDisplayed = Integer.parseInt(req.getParameter("maxdisplayed"));
			
			TopSearch search = new TopSearch(tissueID, byAbundance, microRNA, maxDisplayed, tCat);
			Expression[] expressList = search.getExpressList();
			Gene[] geneList = search.getGeneList();
			int actualDisplayed = search.getActualDisplayed();
			
			TopPage topPage = new TopPage (expressList, geneList, sex, tissueID, byAbundance, 
											maxDisplayed, actualDisplayed, tCat, includeErrors, showWhole, sexStats, microRNA);
			
    		writer.println(topPage.getHTML());
			writer.close();
		}		
	}
}
