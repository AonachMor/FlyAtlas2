/*
	Class to create SVG Image of Graph, 
	DPL 10.12.2013
	modified from BufferedImage/png version 07.08.2014
	DPL 15.12.2015 Hard-coded font family names to allow export of SVG to Adobe Illustrator etc.
	DPL 01.02.2016 minor bug fixes
*/		

import java.awt.*;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

public class CreateImage
{
	String gene;			// name of midgutGene
	int numTissues;			// number of tissues (gut compartments)
	String[] names;			// array of names of tissues (gut compartments)
	double[] values;		// array of expression values
	
	// fonts (apologies for specific names, but needed to ensure SVG can be loaded into Adobe Illustrator)
	private Font titleFt = new Font("arial", Font.BOLD,16);
	private Font axisFt = new Font("arial", Font.PLAIN,12);	
	private Font ssFt = new Font("arial", Font.PLAIN,8);
	
	// overall dimensions - these need to be hard coded - could vary with number of conds, I suppose
	private int imgWidth = 600;
	private int imgHeight = 400;
	
	// layout gutters and spacings
	private int gutter = 20;	// minimum stand-off of graphics from edge
	
	private int topSpace;		// space between top gutter and graph
	private int botSpace;		// space between bottom gutter and graph
	private int lSpace;			// space between left gutter and graph
	private int rSpace;			// space between right gutter and graph
	
	// graph dimensions
	int oriX;
	int oriY;
	int graphWidth;
	int graphHeight;
	
	int tickWidth = 5;			// hard-code width of tick on y axis
	
	// bar dimensions
	int barWidth;		// width of bar
	int spaceWidth;		// width of space between bars - half of this at start and (poss) end
	
	// value variables	
	double scaleMax;	// value of scale max x 10^-power
	int power;			// powers of 10 scale values are multiplied by
	double factor;		// factor to convert values to pixels
	
	// etc
	Color myBlue;
	
	String htmlSVG;					// string with untrimmed SVG

	public CreateImage(String gene, MidgutCatalogue ftList, double[] values)
	{	
		this.gene = gene;
		this.values = values;
		
		// Use organismID to get tissue info
		numTissues = ftList.getSize();
		names = new String[numTissues];
		for(int i=0; i<numTissues; i++)
		{
			names[i] = ftList.getFlyTissue(i).getTissueName();
		}
		
		myBlue = new Color(35, 82, 130);
		
		// Get a DOMImplementation.
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		
		// Create an instance of org.w3c.dom.Document.
		String svgNS = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument(svgNS, "svg", null);
		
		// Create an instance of the SVG Generator.
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
		svgGenerator.setSVGCanvasSize(new Dimension(600, 400));		// Needed! Should match image size 
		
		// Ask the class to render into the SVG Graphics2D implementation. 
		this.paint(svgGenerator);
		
		boolean useCSS = true;		// we want to use CSS style attributes
		Writer sw = new StringWriter();
		try
		{
			svgGenerator.stream(sw, useCSS);
		}
		catch (IOException io)
		{
			System.out.println(io.toString());
		}
		
		StringBuilder sb = new StringBuilder(sw.toString());
		htmlSVG = sb.toString();
	}
	
	// accessor method for final SVG text
	public String getSVG()
	{
		String trimmedSVG = trimSVG(htmlSVG);	// remove repeat of xml pragma
		String cleanSVG = cleanSVG(trimmedSVG);	// Dialog font to arial for export
		return cleanSVG;
	}
	
	// removes (repeat of) xml version and doctype from SVG text
	private String trimSVG(String untrimmedSVG)
	{
		int i = untrimmedSVG.indexOf("<svg xmlns");
		return untrimmedSVG.substring(i, untrimmedSVG.length());
	}
	
	// replaces generated Dialog font with arial to ensure readability in Adobe Illustrator
	private String cleanSVG(String dirtySVG)
	{
		return dirtySVG.replace("Dialog", "arial");
	}
	
	public void paint(Graphics2D g)
	{
        // Turn anti-aliasing on for text
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHints(hints);
        
        // fill area with white
        g.setColor(Color.white);
		g.fillRect(0, 0, imgWidth, imgHeight);
					
		drawFrame(g);
		drawTitle(g);
		drawGraph(g);
		drawYaxis(g);		
	}

	//------------------------ Subsidiary drawing methods ---------------------------//
			
	// draw rectangular frame of graph leaving gutter and text space
	private void drawFrame(Graphics g)
	{
		calculateStandoff(g);
		
		oriX = gutter + lSpace;
		oriY = gutter + topSpace;	
		graphWidth = imgWidth - 2*gutter - lSpace - rSpace;
		graphHeight = imgHeight - 2*gutter - topSpace - botSpace;
		
		g.setColor(Color.black);
		g.drawRect(oriX, oriY, graphWidth, graphHeight);
	}
	
	// Draws overall title: name of midgutGene
	private void drawTitle(Graphics g)
	{
		g.setColor(Color.black);
		g.setFont(titleFt);
		FontMetrics fm = g.getFontMetrics(titleFt);
		String title = ( gene.substring(0,1).toUpperCase() 
						+ gene.substring(1, gene.length()));
		int xTitle = oriX + (graphWidth - fm.stringWidth(title))/2;			
		int yTitle = gutter + fm.getAscent();

		g.drawString( title, xTitle, yTitle);
	}
	
	// Draws bars for graph and x axis names
	private void drawGraph(Graphics g)
	{
		calculateBars();
		calculateScale();
			
		int barX = oriX + spaceWidth/2;
		for(int i=0; i<numTissues; i++)
		{
			// Draw rectangular bars
			g.setColor(myBlue);
			g.fillRect(barX, oriY + (int) (graphHeight - values[i]*factor) + 1, barWidth, (int) (values[i]*factor) );
			g.setColor(Color.black);
			g.drawRect(barX, oriY + (int) (graphHeight - values[i]*factor) + 1, barWidth, (int) (values[i]*factor) );
									
			// Draw legends (names)
			g.setFont(axisFt);
			FontMetrics fm = g.getFontMetrics(axisFt);
			int xName = barX + barWidth/2 - fm.stringWidth(names[i])/2;			
			int yName = imgHeight - gutter - fm.getDescent();
			
			g.setColor(Color.black);
			g.drawString( names[i], xName, yName);			
			
			// increment barX
			barX = barX + barWidth + spaceWidth;
		}
	}	
		
	// draw a suitable number of ticks along Y axis together with values
	private void drawYaxis(Graphics g)
	{
		double stepSize = 0.0;	// spacing (*10-power) - chosen arbitraly
		
		if(scaleMax>4)
		{
			stepSize = 1.0;
		}
		else if (scaleMax>1.5)
		{
			stepSize = 0.5;
		}
		else
		{
			stepSize = 0.2;
		}
		
		double pxFactor = graphHeight/scaleMax;
		int interval = (int) (stepSize*pxFactor);	// interval between ticks in pixels
		int stepNo = (int)	(scaleMax/stepSize) + 1;	// number of ticks (starting from 0)
		
		g.setColor(Color.black);
		for(int i=0; i< stepNo; i++)
		{
			// tick
			g.drawLine(oriX, (oriY+graphHeight - i*interval), oriX-tickWidth, (oriY+graphHeight - i*interval));
			// number string
			String numString;
			if(stepSize*i == 10.0)
			{
				numString = (String.valueOf(stepSize*i)).substring(0,4);	// four chars including . for 10.0
			}
			else
			{
				numString = (String.valueOf(stepSize*i)).substring(0,3);	// three chars including . for others
			}
			g.setFont(axisFt);
			FontMetrics fm = g.getFontMetrics(axisFt);
			int xNum = oriX - tickWidth*2 - fm.stringWidth(numString);
			int yNum = (oriY+graphHeight - i*interval) + fm.getAscent()/2;
			g.drawString(numString, xNum, yNum);
		}	
		
		// Power factor
		String timesTen = "FPKM \u00D7 10";
		String minusPower = "\u2013" + String.valueOf(power);	
		
		// only draw in full if power > 0
		if(power > 0)
		{
    		FontMetrics fma = g.getFontMetrics(axisFt);
    		
    		g.setFont(axisFt);		
    		int xP = oriX - tickWidth*2 - fma.stringWidth(timesTen)/2;	// same as numbers
    		int yP = imgHeight - gutter - fma.getDescent();				// same as legends
    		g.drawString(timesTen, xP, yP);		
    		
    		g.setFont(ssFt);
    		int xS = xP + fma.stringWidth(timesTen);		// end of 10x
    		int yS = yP - fma.getAscent()/2;				// half way up ?
    		g.drawString(minusPower, xS, yS);	
		}
		else	// just put in FPKM at about the same position
		{
    		FontMetrics fma = g.getFontMetrics(axisFt);
    		String fpkm = "FPKM";
    		
    		g.setFont(axisFt);		
    		int xP = oriX - tickWidth*2 - fma.stringWidth(timesTen)/2;	// same as numbers
    		int yP = imgHeight - gutter - fma.getDescent();				// same as legends
    		g.drawString(fpkm, xP, yP);				
		}
	}


	//--------------------------- Utility calculation methods ------------------------------//	
		
	// calculates stand-off for frame to accommodate text
	private void calculateStandoff(Graphics g)
	{
		FontMetrics fmt = g.getFontMetrics(titleFt);
		topSpace = 20 + fmt.getAscent() + fmt.getDescent();
		
		FontMetrics fma = g.getFontMetrics(axisFt);
		botSpace = 20 + fma.getAscent() + fma.getDescent();	
		
		String leftNumeral = "0.0";
		int leftWidth = fma.stringWidth(leftNumeral);
		lSpace = leftWidth + 10 ;
		
		rSpace = 0;			// no extra space needed
	}
	
	// calculates width of bars and spaces wrt No conditions: max spacing - 2x bar width, min - 0
	private void calculateBars()
	{
		int minBar = 50;
		int maxBar = 75;
				
		if(numTissues*(3*maxBar) < graphWidth)	// need to use max for both
		{
			barWidth = maxBar;
			spaceWidth = 2*maxBar;
		}
		else
		{
			int testBar = graphWidth / (3*numTissues);
			if(testBar >= minBar)				// share in proportion if possible
			{
				barWidth = testBar;
				spaceWidth = 2*testBar;
			}
			else								// use minimum bar width and lower space
			{
				barWidth = minBar;
				spaceWidth = (graphWidth - numTissues*barWidth)/numTissues;
				if(spaceWidth < 0)					//	unlikely event of v.many conditions 
				{
					spaceWidth = 0;
					barWidth = graphWidth/numTissues;
				}
			}
		}
	}
	
	// Find highest bar and decide on what value to set as maximum, and hence set scale value
	private void calculateScale()
	{
		// Find maximum value // + error
		double max = 0;
		for (int i=0; i<numTissues; i++)
		{
			double current = values[i];
			if(current > max)
			{
				max = current;
			}
		}
		
		// Find initial digit for max and power of 10
		boolean done = false;
		int digit = (int) max;		// initial digit in max
		power = 0;					// number of powers of 10 this digit is x by in max
		if(digit < 10)
		{
			done = true;
		}
		while (!done)
		{
			digit = digit/10;
			power++;
			if(digit < 10)
			{
				done = true;
			}
		}

		// Make scaleMax 1 more than digit of max value
		scaleMax = digit + 1.0;
		
		// finally calculate factor for converting values to pixels
		factor = (double) graphHeight / (scaleMax * Math.pow(10, power));
	}	
}