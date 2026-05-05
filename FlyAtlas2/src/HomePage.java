// Class to generate HomePage HTML page
// DPL 01.11.2021

public class HomePage
{
	private final int PAGE_POS = PageUtility.HOME;	// Position of page in menu
	private boolean includeErrors = false; 			// show SDs in results
	private boolean showWhole = false;				// show data for Whole Body
	private boolean sexStats = false;				// show M v. F comparison		
	
	public HomePage(boolean includeErrors, boolean showWhole, boolean sexStats)
	{
		this.includeErrors = includeErrors;
		this.showWhole = showWhole;
		this.sexStats = sexStats;
	}
	
	public String getHome()
	{
		PageUtility pu = new PageUtility(includeErrors, showWhole, sexStats);
		StringBuilder htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(getMobileText());
		htmlBuilder.append(pu.readHTMLfile("htmlText/home.txt"));
		
		// hidden errors checkbox
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
		
		htmlBuilder.append(PageUtility.PAGE_FOOT_HOME);
		return htmlBuilder.toString();
	}
	
	
	// Header and menu text for mobile only on home page
	private String getMobileText()
	{
		StringBuilder mBuilder = new StringBuilder();
		mBuilder.append("<div id=\"title625\"> <img class=\"shrinkfit\" alt=\"\" src=\"images/FA2title625.jpg\" /> </div>\n");
		mBuilder.append("<div id=\"title500\"> <img class=\"shrinkfit\" alt=\"\" src=\"images/FA2title500.jpg\" /> </div>\n");	
		mBuilder.append("<div id=\"title400\"> <img class=\"shrinkfit\" alt=\"\" src=\"images/FA2title400.jpg\" /> </div>\n");		
		
		mBuilder.append("<div id=\"menuI\">\n<ul>\n");	
		mBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toGeneForm();\">Gene</a></li>");	
		mBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toTopForm();\">Tissue</a></li>");	
		mBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toGOForm();\">Category</a></li>");	
		mBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toProfileForm();\">Profile</a></li>");
		mBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toMidgutForm();\">Midgut</a></li>");
		mBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toHelpForm();\">Docs</a></li>");	
		mBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toFeedbackForm();\">Feedback</a></li>");		
		mBuilder.append("</ul>\n</div>\n");			
		return mBuilder.toString();
	}
}
