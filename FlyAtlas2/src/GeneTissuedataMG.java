// Class to hold FPKM data for a midgutGene in a single tissue
// DPL 07.09.2021

import java.text.*;				// for number formatting
import java.awt.Color;

public class GeneTissuedataMG
{
	private String fbgn;
	private int tissueID;
	private double fpkm;
	private double [] repFPKMlist;			// This array should have an occupancy of 3 (i.e. triplicate)
	private final int NUM_REPLICATES = 3;	
	private String status;
	private double sd;
	
	public GeneTissuedataMG(String fbgn, int tissueID, double fpkm, double[] repFPKMlist, String status, double sd)
	{
		this.fbgn = fbgn;
		this.tissueID = tissueID;
		this.fpkm = fpkm;
		this.repFPKMlist = repFPKMlist;
		this.status = status;
		this.sd = sd;
	}
	
	public String getFBgn()
	{
		return fbgn;
	}
	
	public int getTissueID()
	{
		return tissueID;
	}
	
	public double getFPKM()
	{
		return fpkm;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public double getRepFPKM(int pos)
	{
		return repFPKMlist[pos];
	}
	
	public double getSD()
	{
		return sd;
	}
	
	// Formats data for HTML output as table row
	public String getHTMLFormatted(int rowNum, MidgutCatalogue ftList)
	{
		Color colour = getBackgroundColour(fpkm);
		String bgColour = "rgb(" + colour.getRed() + "," + colour.getGreen() + "," + colour.getBlue() + ")";;
		
		String txtColour = new String();
		boolean textWhite = isDark(getBrightness(colour));	
		if(textWhite)
		{ 
			txtColour = "white"; 
		}
		else
		{
			txtColour = "black";			
		}
				
		StringBuilder buffer = new StringBuilder();
		buffer.append("<tr>");
		buffer.append("<td>" + ftList.getTissueNameByID(tissueID) + "</td>");

		NumberFormat N = NumberFormat.getInstance();
		N.setMaximumFractionDigits(2);
		
		buffer.append("<td style=\"background-color:" + bgColour + "; color:" + txtColour + ";\">" 
				+ padDecimals(N.format(fpkm), 2) + "</td>");
		for(int i=0; i<NUM_REPLICATES; i++)
		{
			buffer.append("<td>" + padDecimals(N.format(repFPKMlist[i]), 2) + "</td>");
		}
		buffer.append("<td>" + padDecimals(N.format(sd), 2) + "</td>");
		// buffer.append("<td>" + status + "</td>");
		buffer.append("</tr>\n");		
		return buffer.toString();
	}
	
	// Returns background colour for FPKM cells on a yellow/white/red divergent scale for MidGut
	private Color getBackgroundColour(double fpkm)
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
	
	// replaces default as tab-separated text suitable for output
	public String toString()
	{
		return fbgn + "\t" + tissueID + "\t" + fpkm + "\t" + "(" + repFPKMlist[0] + ", " +  repFPKMlist[1] + ", " +  repFPKMlist[2] + ")" + "\t" + status + "\t" + sd;
	}
}
