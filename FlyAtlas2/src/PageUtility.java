 /*
Class to hold HTML code and write specific HTML menus 
for different pages of FlyAtlas 2
Also contains various other utilities, most of which can be accessed in a static manner
David P. Leader 18.10.20255
*/

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;

public class PageUtility
{	
	final static  String PAGE_HEAD = 
		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
		+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n"
		+ "<head>\n"
		+ "<!-- Google tag (gtag.js) -->"
		+ "<script async src=\"https://www.googletagmanager.com/gtag/js?id=G-XZ0PFVLQM5\"></script>"
		+ "<script>"
		+ "window.dataLayer = window.dataLayer || [];"
		+ "function gtag(){dataLayer.push(arguments);}"
		+ "gtag('js', new Date());"		
		+ "gtag('config', 'G-XZ0PFVLQM5');"
		+ "</script>"
		+ "<!-- end of Google tag -->"
		+ "<title>FlyAtlas 2: The RNAseq-based atlas of Drosophila gene expression</title>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />\n"
		+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\" />\n"
		+ "<meta name=\"description\" content=\"Database of transcripts in individual tissues of adult (male and female) and larval Drosophila melanogaster from RNAseq analysis\" />\n"
		+ "<meta name=\"google-site-verification\" content=\"z9170sVBFVRdwqBwQQib_LDcY_1UqSCptW2tH2N_q5o\" />\n"
		+ "<script type=\"text/javascript\" src=\"scripts/flyAtlas2.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/highlight.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/auto.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/menuPop2.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/drag.js\"></script>\n"
		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"scripts/flyAtlas2.css\" />\n"
		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"scripts/auto.css\" />\n"
		+ "<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Archivo+Narrow\" />\n"		
		+ "<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"images/drosoph.ico\" />\n"
		+ "<link rel=\"apple-touch-icon\" href=\"images/fly-touch-icon-57x57.png\" />\n"
		+ "<link rel=\"apple-touch-icon\" sizes=\"72x72\" href=\"images/fly-touch-icon-72x72.png\" />\n"
		+ "<link rel=\"apple-touch-icon\" sizes=\"114x114\" href=\"images/fly-touch-icon-114x114.png\" />\n"
		+ "<link rel=\"apple-touch-icon\" sizes=\"144x144\" href=\"images/fly-touch-icon-144x144.png\" />\n"
		+ "</head>\n\n";
	
	// utility boilerplate
	final static String AUTO_DIV =		
		"<div style=\"position:absolute;\" id=\"popup\">\n"
		+ "<table id=\"menuTable\" cellspacing=\"0\" cellpadding=\"0\">\n"           
		+ "<tbody id=\"menuTableBody\"><tr><td></td></tr></tbody>\n"
		+ "</table>\n</div><!-- end of autocomplete div -->\n";
	
	final static String PAGE_FOOT_HOME = 
			"\n"
			// + "<div style=\"float: right; padding-top: 20px; width: 33%; text-align: right;\">\n"
			// + "<img class=\"mobileHide\" src=\"images/EG.jpg\" alt=\"\" width=\"140\" height=\"70\" title=\"Edinburgh Genomics\" />\n</div>\n"			
			+ "<div style=\"float: right; padding-top: 10px; width: 25%; text-align: centre;\">\n"
			+ "<img class=\"mobileHide\" src=\"images/BBSRCm.jpg\" alt=\"\" width=\"150\" height=\"70\" title=\"Funded by the BBSRC\" />\n</div>\n"		
			+ "<div style=\"float: left; padding-top: 10px; width: 25%; text-align: right;\">\n"
			+ "<a href=\"https://www.gla.ac.uk/\"><img class=\"mobileHide\" src=\"images/UofGs.jpg\" alt=\"\" width=\"180\" height=\"70\" title=\"University of Glasgow\" /></a>\n</div>\n"		
			+ "<div style=\"clear: both;\"></div>\n"
			+ "</div> <!-- end of wrapper div -->\n"
			+ "<script type=\"text/javascript\"> \n"
			+ "(function(d){ \n"
			+ "function C(k){return(d.cookie.match('(^|; )'+k+'=([^;]*)')||0)[2];} \n"
			+ "var ua = navigator.userAgent, \n"
			+ "ismobile = / mobile/i.test(ua),  \n"
			+ "mgecko = !!( / gecko/i.test(ua) && / firefox\\//i.test(ua)), \n"
			+ "wasmobile = C('wasmobile') === \"was\", \n"
			+ "desktopvp = 'user-scalable=yes, maximum-scale=2', \n"
			+ "el;  \n"
			+ "if(ismobile && !wasmobile){ \n"
			+ "d.cookie = \"wasmobile=was\";  \n"
			+ "} \n"
			+ "else if (!ismobile && wasmobile){ \n"
			+ "if (mgecko) { \n"
			+ "el = d.createElement('meta'); \n"
			+ "el.setAttribute('content',desktopvp); \n"
			+ "el.setAttribute('name','viewport'); \n"
			+ "d.getElementsByTagName('head')[0].appendChild( el ); \n"
			+ "}else{ \n"
			+ "d.getElementsByName('viewport')[0].setAttribute('content',desktopvp); \n"
			+ "} \n"
			+ "} \n"
			+ "}(document)); \n"
			+ "</script> \n"	
			+ "<script type=\"text/javascript\">\n" 
	        +  "var _gaq = _gaq || [];\n"
	        + "_gaq.push(['_setAccount', 'UA-3315042-21']);\n"
	        + "_gaq.push(['_trackPageview']);\n"
	        + "(function() {\n"
	        + "var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n"
	        + "ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n"
	        + "var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n"
	        + "})();\n"
	        + "</script>\n"
			+ "</body>\n</html>\n";
	
	final static String PAGE_FOOT = 
			"\n</div> <!-- end of wrapper div -->\n"
			+ "<script type=\"text/javascript\"> \n"
			+ "(function(d){ \n"
			+ "function C(k){return(d.cookie.match('(^|; )'+k+'=([^;]*)')||0)[2];} \n"
			+ "var ua = navigator.userAgent, \n"
			+ "ismobile = / mobile/i.test(ua),  \n"
			+ "mgecko = !!( / gecko/i.test(ua) && / firefox\\//i.test(ua)), \n"
			+ "wasmobile = C('wasmobile') === \"was\", \n"
			+ "desktopvp = 'user-scalable=yes, maximum-scale=2', \n"
			+ "el;  \n"
			+ "if(ismobile && !wasmobile){ \n"
			+ "d.cookie = \"wasmobile=was\";  \n"
			+ "} \n"
			+ "else if (!ismobile && wasmobile){ \n"
			+ "if (mgecko) { \n"
			+ "el = d.createElement('meta'); \n"
			+ "el.setAttribute('content',desktopvp); \n"
			+ "el.setAttribute('name','viewport'); \n"
			+ "d.getElementsByTagName('head')[0].appendChild( el ); \n"
			+ "}else{ \n"
			+ "d.getElementsByName('viewport')[0].setAttribute('content',desktopvp); \n"
			+ "} \n"
			+ "} \n"
			+ "}(document)); \n"
			+ "</script> \n"
			+ "<script type=\"text/javascript\">\n" 
	        +  "var _gaq = _gaq || [];\n"
	        + "_gaq.push(['_setAccount', 'UA-3315042-21']);\n"
	        + "_gaq.push(['_trackPageview']);\n"
	        + "(function() {\n"
	        + "var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n"
	        + "ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n"
	        + "var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n"
	        + "})();\n"
	        + "</script>\n"
			+ "</body>\n</html>\n";
	
	// Strings for RNA gene feedback
	final static String MICRO_RNA = 
			"<div class=\"ambiguity\">\n<span style=\"color:red;\">These data are for a <em>microRNA</em>.</span><br />"
			+ "The data were processed separately from those for other genes, and have different units and heat-map ranges. "
			+ "<br />There are only two possible transcripts for the microRNAs — the cleavage products RA and RB. "
			+ "The relative proportions of the two are represented by the area that they occupy in the transcript graphic, "
			+ "with an indication of the total abundance being provided by the colour intensity."
			+ "<br />Adult crop, salivary gland, fat body, spermatheca and heart were sequenced using a different chemistry from the other tissues, as were larval Garland cells. "
			+ "\n</div>\n";
	
	final static String PSEUDOGENE = 
			"<div class=\"ambiguity\">\n<span style=\"color:red;\">These data are for the transcript of an <em>RNA pseudogene</em> that cannot encode a protein.</span><br />"
			+ "It is advisable to check any positive sequence reads in the UCSC Genome Browser "
			+ "<span class=\"mobileHide\">(link above)</span>. (N.B. Data for Fat Body and Spermatheca may not be comparable with those from other tissues.)\n</div>\n";
	
	final static String LNC_RNA = 
			"<div class=\"ambiguity\">\n<span style=\"color:red;\">These data are for a <em>lncRNA (Long intervening non-coding RNA)</em> that does not encode a protein.</span><br />"
			+ "It is advisable to check any positive sequence reads in the UCSC Genome Browser "
			+ "<span class=\"mobileHide\">(link above)</span>. (N.B. Data for Fat Body and Spermatheca may not be comparable with those from other tissues.)\n</div>\n";
	
	final static String SNO_RNA = 
			"<div class=\"ambiguity\">\n<span style=\"color:red;\">These data are for a <em>snoRNA (small nucleolar RNA)</em> that does not encode a protein.</span><br />"
			+ "There are two main classes of snoRNAs: 1. CD class snoRNAs, involved in rRNA methylation, which are generally <em>not</em> detected by our techniques as they are too small, and "
			+ "2. ACC class snoRNAs, involved in pseudouridylation, which <em>are</em> generally large enough to be detected. (N.B. Data for Fat Body and Spermatheca may not be comparable with those from other tissues.)"
			+ "\n</div>\n";
	
	final static String NC_RNA = 
			"<div class=\"ambiguity\">\n<span style=\"color:red;\">These data are for an <em>ncRNA (non-coding RNA)</em>, i.e. an otherwise uncategorized RNA that does not encode a protein.</span><br />"
			+ "RNAs below a certain size are not detected by RNAseq and therefore zero values for them will be false. "
			+ "Some longer RNAs overlap highly-expressed protein genes. It is therefore advisable to check any positive sequence reads for ncRNAs in the UCSC Genome Browser "
			+ "<span class=\"mobileHide\">(link above)</span>. (N.B. Data for Fat Body and Spermatheca may not be comparable with those from other tissues.)\n</div>\n";
	
	final static String SN_RNA = 
			"<div class=\"ambiguity\">\n<span style=\"color:red;\">These data are for a <em>snRNA (small nuclear RNA)</em> that does not encode a protein.</span><br />"
			+ "Note that Some snRNAs are too small to be detected by RNAseq and therefore zero values for them will be false. (N.B. Data for Fat Body and Spermatheca may not be comparable with those from other tissues.)"
			+ "\n</div>\n";
	
	final static String ANTISENSE_RNA =
			"<div class=\"ambiguity\">\n<span style=\"color:red;\">These data are for an <em>antisense RNA</em> that does not encode a protein.</span><br />"
			+ "(N.B. Data for Fat Body and Spermatheca may not be comparable with those from other tissues.)"
			+ "\n</div>\n";
	
	final static String REPEAT_GENE = 
			"<div class=\"ambiguity\">\n<span style=\"color:red;\">These data are for a gene with identical or very similar paralogues.</span><br />"
			+ "In such situations the software used for analysis generally reports negative results, irrespective of the true situation. "
			+ "You are strongly advised to check negative results in the UCSC Genome Browser "
			+ "<span class=\"mobileHide\">(link above)</span>.\n</div>\n";
	
	// String for Protein gene feedback
	final static String PROTEIN_GENE =
			"<div class=\"ambiguity\" style=\"font-size:100%\">\n<span>*Tissue Comparability: "
			+ " <em>See</em> Docs for info on the chemistry used to sequence different tissues."
			+ "\n</div>\n";
	
	// String for Ambiguity feedback
	final static String AMBIGUITY_WARNING =
			"<span style=\"color:red;\">WARNING: Transcript Ambiguity for this Gene!</span><br />"
			+ "Because FlyBase assigns a unique transcript name and identifier for each  unique protein, "
			+ "the rare cases in which such proteins result from alternative translation of the same mRNA give rise to situations "
			+ "in which there are two or more different identifiers for the same physical transcript. "
			+ "RNAseq analysis can only deal with unique sequences, and in such cases arbitrarily assigns the data from a sequence to one of the different transcript identifiers. "
			+ "The result is that some named transcripts will be missing, or data for some transcripts will actually include that for others. ";
			//+ "\n</div>\n";
	
	// Constants to allow safer comparisons
	final static String MALE = "Male";
	final static String FEMALE = "Female";
	final static String BOTH = "Both";
	final static String ADULT = "Adult";
	final static String LARVAL = "Larval";
	
	final static int HOME = 0;
	final static int GENE = 1;
	final static int TISSUE = 2;
	final static int CATEGORY = 3;
	final static int PROFILE = 4;
	final static int MIDGUT = 5;
	final static int DOCS = 6;
	final static int FEEDBACK = 7;
	
	private static PageDescriptor pageList[];			// list of page descriptor objects
	private final static int LENGTH = 8;				// Number of pages

	public PageUtility(boolean includeErrors, boolean showWhole, boolean sexStats)
	{
		pageList = new PageDescriptor[LENGTH];
		initializePageList(includeErrors, showWhole, sexStats);
	}
	
	///////////////////////////////////////////////
	
	// initializes array of PageDescriptors	
	private void initializePageList(boolean includeErrors, boolean showWhole, boolean sexStats)
	{ 
		pageList[HOME] = new PageDescriptor(HOME, "Home", "toHomeForm()", "<body>\n", false);
		pageList[GENE] = new PageDescriptor(GENE, "Gene", "toGeneForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setColumns('geneR'," + sexStats + "); setFocus(); setHash('mobileTargetG'); createLink('gene');\" onkeypress=\"geneKey(event);\">\n", true);
		pageList[TISSUE] = new PageDescriptor(TISSUE, "Tissue", "toTopForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setColumns('geneR'," + sexStats + "); setHash('mobileTargetT');\" onkeypress=\"topKey(event);\">\n", true);
		pageList[CATEGORY] = new PageDescriptor(CATEGORY, "Category", "toGOForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setColumns('geneR'," + sexStats + "); setFocus(); setHash('mobileTargetC'); createLink('category');\" onkeypress=\"goKey(event);\">\n", true);
		pageList[PROFILE] = new PageDescriptor(PROFILE, "Profile", "toProfileForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setColumns('geneR'," + sexStats + "); setFocus(); setHash('mobileTargetP');\" onkeypress=\"profileKey(event);\">\n", true);
		pageList[MIDGUT] = new PageDescriptor(MIDGUT, "Midgut", "toMidgutForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setColumns('geneR'," + sexStats + "); setFocus(); setHash('mobileTargetM');\" onkeypress=\"midgutKey(event);\">\n", true);
		pageList[DOCS] = new PageDescriptor(DOCS, "Docs", "toHelpForm()", "<body>\n", false);
		pageList[FEEDBACK] = new PageDescriptor(FEEDBACK, "Feedback", "toFeedbackForm()", "<body>\n", false);
	}
	
	// inner class to hold a utility pageDescriptor object	
	class PageDescriptor
	{
		int pagePos;				// order of page in menu 1 to n
		String pageName;			// name of page as it appears on the menu
		String toMethodName;		// name of javascript method to generate new page	
		String bodyLine;			// html <body> line - differs depending on javascript
		boolean scroll;				// whether or not back-to-top function required
		static final String scrollText = "<button onclick=\"topFunction()\" id=\"upButton\" title=\"Go to top\">&#8963;</button>" + 
										"\n<script>window.onscroll = function() {scrollFunction()};</script>\n";
		String scrollLine = "";		// text
		PageDescriptor(int pagePos, String pageName, String toMethodName, String bodyLine, boolean scroll)
		{
			this.pagePos = pagePos;
			this.pageName = pageName;
			this.toMethodName = toMethodName;
			this.bodyLine = bodyLine;
			if(scroll==true)
			{
				scrollLine = scrollText;
			}
		}
		public int getPagePos()
		{
			return pagePos;
		}
		public String getPageName()
		{
			return pageName;
		}
		public String getToMethodName()
		{
			return toMethodName;
		}
		public String getBodyLine()
		{
			return bodyLine;
		}
		public String getScrollLine()
		{
			return scrollLine;
		}
	}
	
	///////////////////////////////////////////////
	
			// Static ACCESSOR method (depends on PageList being established) //
	
	// builds top section of html page with appropriate names and links — modified for new layout
	public String getPageTop(int pagePos)
	{
		StringBuilder pBuilder = new StringBuilder(PAGE_HEAD);		// boiler plate html head
		pBuilder.append(pageList[pagePos].getBodyLine());			// <body> line
		pBuilder.append(pageList[pagePos].getScrollLine());			// scroll line
		pBuilder.append("<div id=\"wrapper\">\n");					// the finishing div is elsewhere !		
		pBuilder.append("<div id=\"upper\">\n");					// upper area containing title and menu for normal view
		pBuilder.append("<div class=\"menuContainer\">\n");
		
		pBuilder.append("<div class=\"menuL\">\n<ul>\n");			// start first menu list
		
		// link for Gene
		if(pageList[GENE].getPagePos() == pagePos)		// Page calling the html block
		{ pBuilder.append("<li><span class=\"current\">" + pageList[GENE].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[GENE].getToMethodName() + ";\">" + pageList[GENE].getPageName() + "</a></li>\n");}
		
		// link for Tissue
		if(pageList[TISSUE].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[TISSUE].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[TISSUE].getToMethodName() + ";\">" + pageList[TISSUE].getPageName() + "</a></li>\n");}
		
		// link for Category
		if(pageList[CATEGORY].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[CATEGORY].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[CATEGORY].getToMethodName() + ";\">" + pageList[CATEGORY].getPageName() + "</a></li>\n");}	
				
		// link for Profile
		if(pageList[PROFILE].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[PROFILE].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[PROFILE].getToMethodName() + ";\">" + pageList[PROFILE].getPageName() + "</a></li>\n");}
		
		pBuilder.append("</ul>\n</div><!-- end of menuL div -->\n");		// finish first menu list	
		pBuilder.append("<div class=\"menuR\">\n<ul>\n");					// start second menu list

		if(pageList[MIDGUT].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[MIDGUT].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[MIDGUT].getToMethodName() + ";\">" + pageList[MIDGUT].getPageName() + "</a></li>\n");}

		// link for Docs
		if(pageList[DOCS].getPagePos() == pagePos)			// Page calling the html block
		{ pBuilder.append("<li><span class=\"current\">" + pageList[DOCS].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[DOCS].getToMethodName() + ";\">" + pageList[DOCS].getPageName() + "</a></li>\n");}
		
		// link for Feedback
		if(pageList[FEEDBACK].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[FEEDBACK].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[FEEDBACK].getToMethodName() + ";\">" + pageList[FEEDBACK].getPageName() + "</a></li>\n");}
			
		// link for Home
		if(pageList[HOME].getPagePos() == pagePos)	
		{ pBuilder.append("<li><span class=\"current\">" + pageList[HOME].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[HOME].getToMethodName() + ";\">" + pageList[HOME].getPageName() + "</a></li>\n");}
		
		pBuilder.append("</ul>\n</div><!-- end of menuR div -->\n");		// finish second menu list
				
		pBuilder.append("</div><!-- end of menuContainer div -->\n");		
		pBuilder.append("</div><!-- end of upper div -->\n");
		return pBuilder.toString();
	}
	
	// build a top bar for mobile only — needs to be different for home page
	public String getMobileTopstrip (int pagePos)
	{
		StringBuilder mBuilder = new StringBuilder();
		mBuilder.append("<div id=\"topStrip\"><!-- topStrip only seen by mobiles -->\n");
		mBuilder.append("<div id=\"topL\"><a class=\"linkPage\" href=\"javascript:toHomeForm();\">&nbsp;☜</a></div>");
		mBuilder.append("<div id=\"topC\">FlyAtlas 2 – " + pageList[pagePos].getPageName() + "</div>\n");
		mBuilder.append("<div id=\"topR\"></div>\n");
		mBuilder.append("</div><!-- end of topStrip -->\n");	
		return mBuilder.toString();
	}

	// Help/Info pop-up for table of Gene FPKMs
	public static String getGeneHelp(int resNum)
	{
		StringBuilder sb = new StringBuilder("<!-- start of Gene help insert -->\n");
		sb.append("<div class=\"indexWindow\" id=\"indexWinG_" + resNum + "\" style=\"display:none; width:380px;\">\n");
		sb.append("<div class=\"indexBar\" style=\"width:370px;\" onmousedown=\"drag(this.parentNode, event);\">Gene Results");
		sb.append("<div class=\"closebox\"><a href=\"#\" onclick=\"closeDiv('indexWinG_" + resNum + "'); return(false);\">&nbsp;&times;&nbsp;</a>\n");
		sb.append("</div></div>");
		sb.append("<div class=\"indexContent\" style=\"width:360px;\">");
		sb.append("<img class=\"help\" src=\"images/GtoT.png\" alt=\"\" width=\"134\" height=\"180\" />\n");
		sb.append("<p class=\"help\"><strong>Locating Transcripts</strong>");
		sb.append("<br />Click on Gene FPKM or Enrichment value to highlight corresponding transcripts.</p>\n");
		sb.append("<p class=\"help\">\n");
		sb.append("<img src=\"images/download.png\" alt=\"\" style=\"vertical-align:sub\" /> <strong>Download</strong>\n");
		sb.append("<br />The results table may be downloaded as tab-separated text, suitable for importing into a spreadsheet program such as Microsoft Excel.</p>\n");
		sb.append("</div>\n</div>\n<!-- end of  Gene help insert -->\n");
		return sb.toString();
	}

	// Help/Info pop-up for table of Transcript FPKMs
	public static String getTranscriptHelp(int resNum)
	{
		StringBuilder sb = new StringBuilder("\n<!-- start of Transcript help insert -->\n");
		sb.append("<div class=\"indexWindow\" id=\"indexWinT_" + resNum + "\" style=\"display:none; width:430px;\">\n");
		sb.append("<div class=\"indexBar\" style=\"width:420px;\" onmousedown=\"drag(this.parentNode, event);\">Transcript Results");
		sb.append("<div class=\"closebox\"><a href=\"#\" onclick=\"closeDiv('indexWinT_" + resNum + "'); return(false);\">&nbsp;&times;&nbsp;</a>\n");
		sb.append("</div></div>");
		sb.append("<div class=\"indexContent\" style=\"width:410px;\">");
		sb.append("<img class=\"help\" src=\"images/GtoT.png\" alt=\"\" width=\"134\" height=\"180\" />\n");
		sb.append("<p class=\"help\"><strong>Correlating with Genes</strong>");
		sb.append("<br />Clicking on a Gene FPKM or Enrichment value corresponding to a set of transcripts will highlight both.</p>\n");
		sb.append("<p class=\"help\">\n");
		sb.append("<img src=\"images/download.png\" alt=\"\" style=\"vertical-align:sub\" /> <strong>Download</strong>\n");
		sb.append("<br />Although FPKM values are not presented in the table, they may be downloaded as tab-separated text, suitable for importing into a spreadsheet program such as Microsoft Excel.</p>\n");
		sb.append("</div>\n</div>\n<!-- end of Transcript help insert -->\n");
		return sb.toString();
	}

	///////////////////////////////////////////////
	
			// Non-static UTILITY method (calls non-static getClass()) //
	
	// Reads a file into a UTF-8 String - typically a file in the same directory, e.g. "htmlText/mypage"
	public String readHTMLfile(String path)
	{
		String outString;
		InputStream stream = getClass().getResourceAsStream(path);
		if (stream !=null)
		{
			try
			{
				byte [] b = new byte[8092];
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int i = 0;
				while( (i=stream.read(b)) > 0)
				{
					out.write(b, 0, i);
				}
				stream.close();
				outString = out.toString("UTF-8");	// !
			}
			catch (IOException x)
			{
				outString = "Text Misread. Please notify the site owner.";
			}
		}
		else
		{
			outString = "Text Misread. Please notify the site owner.";		
		}
		return outString;
	}
	
	////////////////// GENERAL UTILITY METHODS /////////////////////////////
	
	// Legacy method for Enrichments — calls two parameter method with decDigits = 1
	public static String formatValues(double value)
	{	
		return formatValues(value, 1);
	}
	
	// returns a String value with visually appropriate number of decimal places (not strict num sig figs)
	public static String formatValues(double value, int decDigits)
	{
		NumberFormat N = NumberFormat.getInstance();
		N.setGroupingUsed(false);		// no comma separators for thousands (mainly single digit thous which shouldn't have them)
		if(value < 1.0)
		{
				N.setMaximumFractionDigits(decDigits);
				N.setMinimumFractionDigits(decDigits);
		}
		else if(value < 2.0)
		{
				N.setMaximumFractionDigits(decDigits);
				N.setMinimumFractionDigits(decDigits);
		}
		else if(value < 10.0)
		{
				N.setMaximumFractionDigits(1);
				N.setMinimumFractionDigits(1);
		}
		else
		{
			N.setMaximumFractionDigits(0); 
			N.setMinimumFractionDigits(0);
		}	
		return N.format(value);
	}
	
	// Checks for [+] indication of superscript. If present, marks up for HTML
	public static String checkSuper(String name)
	{
		if(name.indexOf("[+]") != -1)
		{
			int start = name.indexOf("[+]");
			name = name.substring(0, start) + "<sup>+</sup>" + name.substring(start+3);
			// check for second case as in Na[+]/H[+]
			if(name.indexOf("[+]") != -1)
			{
				start = name.indexOf("[+]");
				name = name.substring(0, start) + "<sup>+</sup>" + name.substring(start+3);
			}
		}
		else if(name.indexOf("[2+]") != -1)
		{
			int start = name.indexOf("[2+]");
			name = name.substring(0, start) + "<sup>2+</sup>" + name.substring(start+4);			
		}
		return name;
	}
	
	// Returns background colour for enrichment cells on a yellow/white/red divergent scale
	public static Color getEnrichmentColor(double enrichment)
	{
		int red = 0;
		int green = 0;
		int blue = 0;
		Color colour = new Color(red, green, blue);
		// double base = 1.55;		// For log — FlyAtlas 2013
		double base = 1.4;		// For log — adjusted for slightly narrower range cf. FlyAtlas 2013
		int numHighSteps = 7;	// Number of log steps for e > 1
		int numLowSteps = 4;	// Number of log steps for e < 1
		int gbRange = 210;		// For reds e > 1
		
		if(enrichment > Math.pow(base, numHighSteps))	// deal with extreme high values first - base 1.55 with 7 steps = ca.21.5
		{
			green = 230 - gbRange;
			blue = 230 - gbRange;
			int rRange = 50;
			int addSteps = 15;
			double logVal = Math.log(enrichment) / Math.log(base);	
			red = 250 - (int) (logVal*rRange ) / addSteps;
			colour = new Color(red, green, blue);
		}
		else if(enrichment > 1)
		{
    		double logVal = Math.log(enrichment) / Math.log(base);
    		int rRange = 5;
    		int gRange = 230;
    		int bRange = 15;
    		double rDecrement = (logVal*rRange ) / numHighSteps ;
    		double gDecrement = (logVal*gRange ) / numHighSteps ;
    		double bDecrement = (logVal*bRange ) / numHighSteps ;
    		red = 255 - (int) rDecrement;
    		green = 255 - (int) gDecrement;
    	    blue = 40 - (int) bDecrement;
    		colour = new Color(red, green, blue);
		}
		else if(enrichment == 1)
		{
			red = 255; green = 255; blue = 40;		// yellow
			colour = new Color(red, green, blue);
		}
		else if(enrichment < 1)			// yellow range below 1
		{		
			double lowBase = 1.8;
			if(enrichment < 0)
			{
				enrichment = 0;
			}
			
			int bRange = 215;			// B range from 255 to 40
    		double logVal = Math.log(enrichment) / Math.log(lowBase);
    		double bDecrement = (logVal* bRange)  / numLowSteps; 	
    		
    		if(bDecrement < -bRange)
    		{
    			bDecrement = -bRange;		// 40 minimum value for yellow
    		}
    		red = 255;
    		green = 255;
    		blue = 40 - (int) bDecrement;
    		colour = new Color(red, green, blue);
		}				    
		return colour;
	}
	
	// Returns background colour for FPKM cells on a white to black log scale
	public static Color getAbundanceColor(double abundance)
	{	
		int red = 0;
		int green = 0;
		int blue = 0;	
		//double base = 2;		// FlyAtlas 2013
		double base = 1.6;		// Altered from FlyAtlas 2013 because of changed distribution and effect of transcripts
		int numSteps = 15;
		int range = 255;
		
		double logVal = Math.log(abundance) / Math.log(base);
		red = range - (int) (logVal*range) / numSteps;
		if(red>255)
		{
			red = 255;
		}
		else if(red<0)		// occurs if Abundance > ca 30000 or perhaps lower now — Just a few dozen cases
		{
			red = 0;
		}
		green = red; blue = red;
		
		return new Color(red, green, blue);
	}
	
	// Returns background colour for TPM cells on a white to black log scale
	public static Color getTPMColor(int tpm)
	{	
		int red = 0;
		int green = 0;
		int blue = 0;	
		double base = 2.6;			// Adjusted by eye
		int numSteps = 15;
		int range = 255;
		
		double logVal = Math.log(tpm) / Math.log(base);
		red = range - (int) (logVal*range) / numSteps;
		if(red>255)
		{
			red = 255;
		}
		else if(red<0)		// occurs if Abundance > ca 30000 or perhaps lower now 
		{
			red = 0;
		}
		green = red; blue = red;
		
		return new Color(red, green, blue);
	}
	
	// Returns colour for red (A) component of bar chart of transcript, faded as tpm declines
	public static Color fadeRed(int tpm)
	{	
		int red = 240;
		int green = 50;
		int blue = 30;		
		double base = 2.6;			// Adjusted by eye
		int numSteps = 15;
		int range = 255;
		double logVal = Math.log(tpm) / Math.log(base);
		
		green = range - (int) (logVal*range) / numSteps;
		blue = range - (int) (logVal*range) / numSteps;
		
		if(blue>240)
		{blue = 240;}
		if(blue<0)
		{blue = 0;}
		if(green>240)
		{green = 240;}
		if(green<0)
		{green = 0;}
		
		return new Color(red, green, blue);
	}

	// Returns colour for blue (B) component of bar chart of transcript, faded as tpm declines
	public static Color fadeBlue(int tpm)
	{	
		int red = 60;
		int green = 140;
		int blue = 240;			
		double base = 2.6;			// Adjusted by eye
		int numSteps = 15;
		int range = 255;
		double logVal = Math.log(tpm) / Math.log(base);
		
		green = range - (int) (logVal*range) / numSteps;
		red = range - (int) (logVal*range) / numSteps;
		
		if(red>240)
		{red = 240;}
		if(red<0)
		{red = 0;}
		if(green>240)
		{green = 240;}
		if(green<0)
		{green = 0;}
		
		return new Color(red, green, blue);
	}
	
	// utility method gets brightness of a colour
	public static int getBrightness(Color c) 
	{
	    return (int) Math.sqrt(c.getRed() * c.getRed() * 0.241 +
	      						c.getGreen() * c.getGreen() * 0.691 +
	      						c.getBlue() * c.getBlue() * 0.068);
	}
	
	// utility method takes a brightness value and determines whether above a darkness threshold
	public static boolean isDark(int brightness)
	{
	    if (brightness < 130)
	    {
	    	return true;
	    }
	    else
	    {
	    	return false;
		}
	}
	
	// utility method to generate html colour string of the type rgb(215,65,98) from java Color
	public static String getHTMLcolour(Color colour)
	{
		return "rgb(" + colour.getRed() + "," + colour.getGreen() + "," + colour.getBlue() + ")";
	}
	
	public static String getDownloadLink(Gene gene, boolean transcript, boolean mir)
	{
		String fbgn = gene.getFBgn();
		String tableOut = new String();
		if(transcript && mir)
		{
			tableOut = "transcriptMir";
		}
		else if(transcript)
		{
			tableOut = "transcriptGene";
		}
		else if(mir)
		{
			tableOut = "mir";
		}
		else
		{
			tableOut = "gene";			
		}
		return("<a href=\"/FA2Direct/index.html?fbgn=" + fbgn + "&amp;tableOut=" + tableOut + 
				";\" title=\"Download " + tableOut + " table\"><img src=\"images/download.png\" alt=\"download\" class=\"downloadImg\" /></a>");
	}
	
}
