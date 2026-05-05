// Class to generate HomePage HTML page
// DPL 02.08.2017
// Last update 28,03.2021

public class HelpPage
{
	private final int PAGE_POS = PageUtility.DOCS;	// Position of page in menu
	private boolean includeErrors = false; 			// show SDs in results
	private boolean showWhole = false;				// show data for Whole Body
	private boolean sexStats = false;				// show M v. F comparison	
	private String intro = "Click or tap on the triangles to expand and collapse the different sections of documentation.";
	
	public HelpPage(boolean includeErrors, boolean showWhole, boolean sexStats)
	{
		this.includeErrors = includeErrors;
		this.showWhole = showWhole;
		this.sexStats = sexStats;
	}
	
	public String getHelp()
	{
		PageUtility pu = new PageUtility(includeErrors, showWhole, sexStats);
		StringBuilder htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");		
		
		htmlBuilder.append(pu.readHTMLfile("htmlText/help.txt"));
		
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
		
		htmlBuilder.append(PageUtility.PAGE_FOOT);
		return htmlBuilder.toString();
	}
}
