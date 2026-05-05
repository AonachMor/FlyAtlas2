import java.awt.Color;
import java.text.NumberFormat;

// Simple class to model TranscriptMG
// Also has method to output HTML using data input
// DPL 12.09.2021


public class TranscriptMG
{
	private String fbtr;
	private String fbgn;
	private String name;
	private char strand;
	private ExonMG [] exonList;
	private final int EXON_LENGTH = 10;
	private int exonListSize;
	
	public TranscriptMG(String fbtr, String fbgn, String name, char strand, int exonCount, String exonStarts, String exonEnds)
	{
		this.fbtr = fbtr;
		this.fbgn = fbgn;
		this.name = name;
		this.strand = strand;
		
		exonList = new ExonMG [EXON_LENGTH];
		parseExonInfo(exonCount, exonStarts, exonEnds);
	}
	
	private void parseExonInfo(int exonCount, String exonStarts, String exonEnds)
	{
		// convert to array of string values of exon positions
		String[] exonS = exonStarts.split(",");
		String[] exonE = exonEnds.split(",");
		// generate exons and add to exon array
		for(int i=0; i<exonCount; i++)
		{
			int exStart = Integer.parseInt(exonS[i]);
			int exEnd = Integer.parseInt(exonE[i]);
			ExonMG ex = new ExonMG(exStart, exEnd);
			addExon(ex);
		}
	}
	
	private void addExon(ExonMG ex)
	{
		//check for occupancy of array and expand as required
		if(exonListSize>exonList.length - 1)
		{
			ExonMG[] newList = new ExonMG[exonListSize*2];
			System.arraycopy(exonList, 0, newList, 0, exonListSize);
			exonList = newList;
		}
		exonList[exonListSize] = ex;
		exonListSize++;
	}
	
	public ExonMG getExon(int pos)
	{
		return exonList[pos];
	}
	
	public String getFBtr()
	{
		return fbtr;
	}
	
	public String getFBgn()
	{
		return fbgn;
	}
	
	public String getName()
	{
		return name;
	}
	
	public char getStrand()
	{
		return strand;
	}
	
	public String getTableHeadFormatted(TranscriptTissueDataSetMG tds, MidgutCatalogue mgCat)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<tr><th>Name</th><th>ID</th>");	// hard-coded
		for(int i=0; i<tds.getTranscriptDataListSize(); i++)
		{
			sb.append("<th>" + mgCat.getTissueNameByID(tds.getTranscriptTissuedata(i).getTissueID()) + "</th>"); //Tissue Names	
		}
		sb.append("</tr>\n");
		return sb.toString();
	}
	
	public String getHTMLFormatted(TranscriptTissueDataSetMG tds, MidgutCatalogue mgCat, int rowNum)
	{
		StringBuilder sb = new StringBuilder();
		// place class on every second <tr> for striping
		if(rowNum % 2 == 0)
		{
			sb.append("<tr>");
		}
		else
		{
			sb.append("<tr class=\"alt\">");
		}
		
		sb.append("<td>" +  name + "</td>");
		sb.append("<td><a href=\"javascript:linkToFBtr('" + fbtr + "');\" title=\"Load FlyBase report for " + fbtr + " in new window\">" + fbtr +"</a></td>");
		
		// format FPKM to two decimal places
		NumberFormat N = NumberFormat.getInstance();
		N.setMaximumFractionDigits(2);	
		
		for(int i=0; i<tds.getTranscriptDataListSize(); i++)
		{
			//sb.append("<td>" + padDecimals(N.format(tds.getTranscriptTissuedata(i).getFPKM()), 2) + "</td>");	
			
			// deal with colour
			Color fpkmColor = getFPKMColor(tds.getTranscriptTissuedata(i).getFPKM());
			String fpkmHTMLcolour = getHTMLcolour(fpkmColor);		
			sb.append("<td style=\"background-color:" + fpkmHTMLcolour + ";");
			
			boolean fpkmTextWhite = isDark(getBrightness(fpkmColor));	
			if(fpkmTextWhite)
			{ sb.append("color: white;"); }
			
			sb.append("\">" + padDecimals(N.format(tds.getTranscriptTissuedata(i).getFPKM()), 2) + "</td>");
		}
		
		sb.append("</tr>\n");

		return sb.toString();
	}
	
	// Returns background colour for FPKM cells on a yellow/white/red divergent scale
	public Color getFPKMColor(double fpkm)
	{
		int red = 0;
		int green = 0;
		int blue = 0;
		Color colour = new Color(red, green, blue);
		double base = 1.55;		// For log 
		int numHighSteps = 7;	// Number of log steps for e > 2
		int gbRange = 210;		// For reds e > 2
		
		if(fpkm > Math.pow(base, numHighSteps))	// deal with extreme high values first - base 1.55 with 7 steps = ca.21.5
		{
			green = 230 - gbRange;
			blue = 230 - gbRange;
			int rRange = 50;
			int addSteps = 15;
			double logVal = Math.log(fpkm) / Math.log(base);	
			red = 250 - (int) (logVal*rRange ) / addSteps;
			colour = new Color(red, green, blue);
		}
		else if(fpkm > 2)
		{
    		double logVal = Math.log(fpkm) / Math.log(base);
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
		else if(fpkm == 2)
		{
			red = 255; green = 255; blue = 40;		// yellow
			colour = new Color(red, green, blue);
		}
		else if(fpkm < 2)			// white
		{		
    		red = 255; green = 255; blue = 255;
    		colour = new Color(red, green, blue);
		}				    
		return colour;
	}
	
	// utility method gets brightness of a colour
	private int getBrightness(Color c) 
	{
	    return (int) Math.sqrt(c.getRed() * c.getRed() * 0.241 +
	      						c.getGreen() * c.getGreen() * 0.691 +
	      						c.getBlue() * c.getBlue() * 0.068);
	}
	
	// utility method takes a brightness value and determines whether above a darkness threshold
	private boolean isDark(int brightness)
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
	private String getHTMLcolour(Color colour)
	{
		return "rgb(" + colour.getRed() + "," + colour.getGreen() + "," + colour.getBlue() + ")";
	}
	
	// Adds zeros to decimals to pad out to fixed number of places
	private String padDecimals(String rawNum, int numPlaces)
	{
		int point = rawNum.indexOf(".");
		if(point == -1)
		{
			// add a decimal point if necessary for 0 or integral value (?)
			rawNum = rawNum + ".";
			point = rawNum.indexOf(".");	
		}
		String afterPoint = rawNum.substring(point+1, rawNum.length());
		int lenAfterPoint = afterPoint.length();	
		if(numPlaces > lenAfterPoint)
		{
			StringBuilder sb = new StringBuilder(rawNum);
			for(int i=0; i<numPlaces-lenAfterPoint; i++)
			{
				sb.append("0");
			}
			//return rawNum;
			return sb.toString();
		}
		else
		{
			return rawNum;
		}
	}
	
	public String exonsToString()
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<exonListSize; i++)
		{
			sb.append(exonList[i].getStart());
			sb.append("-");
			sb.append(exonList[i].getEnd());
			sb.append("\t");
		}
		return sb.toString();
	}
	
	public String toString()
	{
		return fbtr + "\t" + fbgn  + "\t" + name   + "\t" + strand + "\t" + exonsToString();
	}
}
