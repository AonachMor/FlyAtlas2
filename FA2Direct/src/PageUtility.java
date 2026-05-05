 /*
Class to hold HTML code and write specific HTML menus 
for different pages of FlyAtlas 2
Also contains various other utilities, most of which can be accessed in a static manner
31.08.2017 ; 23.09.2022, 24.01.2025
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
		+ "<head>\n<title>FlyAtlas 2</title>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />\n"
		+ "<meta name=\"viewport\" content=\"width=device-width\" />\n"
		+ "<script type=\"text/javascript\" src=\"scripts/flyAtlas2.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/highlight.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/auto.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/menuPop2.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/drag.js\"></script>\n"
		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"scripts/flyAtlas2.css\" />\n"
		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"scripts/auto.css\" />\n"
		+ "</head>\n\n";
	
	// utility boilerplate
	final static String AUTO_DIV =		
		"<div style=\"position:absolute;\" id=\"popup\">\n"
		+ "<table id=\"menuTable\" cellspacing=\"0\" cellpadding=\"0\">\n"           
		+ "<tbody id=\"menuTableBody\"><tr><td></td></tr></tbody>\n"
		+ "</table>\n</div><!-- end of autocomplete div -->\n";
		
	final static String PAGE_FOOT = 
			"\n<div style=\"float: right; padding-top: 10px;\">\n"
			+ "<img class=\"mobileHide\" src=\"images/BBSRC.jpg\" alt=\"\" width=\"180\" height=\"70\" title=\"Funded by the BBSRC\" />\n</div>\n"
			+ "<div style=\"padding-top: 10px;\">\n<a href=\"http://www.gla.ac.uk/\"><img class=\"mobileHide\" src=\"images/UofG.jpg\" alt=\"\" width=\"217\" height=\"70\" title=\"University of Glasgow\" /></a>\n"
			+ "</div>\n<div style=\"clear: both;\"></div>\n"
			+ "</div> <!-- end of wrapper div -->\n"
			+ "</body>\n</html>\n";
	
	// Strings for RNA gene feedback
	final static String MICRO_RNA = 
			"<div class=\"ambiguity\">\n<span style=\"color:red;\">These data are for a <em>microRNA</em>.</span><br />"
			+ "These data were processed separately from those for other genes, and the units and heat-map ranges are different from them, as are the UCSC Genome Browser views "
			+ "<span class=\"mobileHide\">(link above)</span>. "
			+ "Adult crop, salivary gland, fat body, spermatheca and garland cells were sequenced using a different chemistry from the other tissues. "
			+ "For certain microRNAs this results in a difference in which form (3′ or 5′ or both) is detected."
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
	
	// String for Protein gene feedback
	final static String PROTEIN_GENE =
			"<div class=\"ambiguity\">\n<span style=\"color:red;\">Tissue Comparability</span><br />"
			+ "Adult <em>fat body</em> and <em>spermatheca</em> were sequenced using a different chemistry from the other tissues (<em>see</em> Docs). "
			+ "The results for these tissues are comparable within this group, but not with other tissues. The identification of their individual transcripts is also less rigorous."
			+ "\n</div>\n";
	
	
	// Constants to allow safer comparisons
	final static String MALE = "Male";
	final static String FEMALE = "Female";
	final static String BOTH = "Both";
	final static String ADULT = "Adult";
	final static String LARVAL = "Larval";
	
	private static PageDescriptor pageList[];			// list of page descriptor objects
	private final static int LENGTH = 6;				// Number of pages

	public PageUtility(boolean includeErrors, boolean showWhole, boolean sexStats)
	{
		pageList = new PageDescriptor[LENGTH];
		initializePageList(includeErrors, showWhole, sexStats);
	}
	
	///////////////////////////////////////////////
	
	// initializes array of PageDescriptors	
	private void initializePageList(boolean includeErrors, boolean showWhole, boolean sexStats)
	{ 
		pageList[0] = new PageDescriptor(1, "Home", "toHomeForm()", "<body>\n");
		pageList[1] = new PageDescriptor(2, "Gene", "toGeneForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setColumns('geneR'," + sexStats + ");\" onkeypress=\"geneKey(event);\">\n");
		pageList[2] = new PageDescriptor(3, "Category", "toGOForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setColumns('geneR'," + sexStats + ");\" onkeypress=\"goKey(event);\">\n");
		pageList[3] = new PageDescriptor(4, "Tissue", "toTopForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setColumns('geneR'," + sexStats + ");\" onkeypress=\"topKey(event);\">\n");
		pageList[4] = new PageDescriptor(5, "Feedback", "toFeedbackForm()", "<body>\n");
		pageList[5] = new PageDescriptor(6, "Documentation", "toHelpForm()", "<body>\n");
	}
	
	// inner class to hold a utility pageDescriptor object	
	class PageDescriptor
	{
		int pagePos;				// order of page in menu 1 to n
		String pageName;			// name of page as it appears on the menu
		String toMethodName;		// name of javascript method to generate new page	
		String bodyLine;			// html <body> line - differs depending on javascript
		PageDescriptor(int pagePos, String pageName, String toMethodName, String bodyLine)
		{
			this.pagePos = pagePos;
			this.pageName = pageName;
			this.toMethodName = toMethodName;
			this.bodyLine = bodyLine;
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
	}
	
	///////////////////////////////////////////////
	
			// Static ACCESSOR method (depends on PageList being established) //
	
	// builds top section of html page with appropriate names and links 
	public String getPageTop(int pagePos)
	{
		StringBuilder pBuilder = new StringBuilder(PAGE_HEAD);		// boiler plate
		pBuilder.append(pageList[pagePos-1].getBodyLine());			// <body> line
		pBuilder.append("<div id=\"wrapper\">\n");				
		pBuilder.append("<h1>FlyAtlas 2</h1>\n");
		pBuilder.append("<h2>");
		// links
		for(int i=0; i<LENGTH; i++)
		{
			if(pageList[i].getPagePos() == pagePos)		// Page calling the html block
			{
				pBuilder.append("<span class=\"current\">" + pageList[i].getPageName() + "</span>\n");		// no self-link
			}
			else
			{
				pBuilder.append("<a class=\"linkPage\" href=\"javascript:" + pageList[i].getToMethodName() + ";\">" + pageList[i].getPageName() + "</a>\n");
			}
		}
		pBuilder.append("</h2>");
		return pBuilder.toString();
	}
	
	// Static ACCESSOR methods //

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
	
	// Returns background colour for FPKM cells on a white to black log scale
	public static Color getTPMColor(int tpm)
	{	
		int red = 0;
		int green = 0;
		int blue = 0;	
		//double base = 1.6;		// for genes
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
