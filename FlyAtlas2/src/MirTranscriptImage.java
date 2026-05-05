/*
	Class to create Specialized Bar graph for microRNA transcripts
	modified from original BufferedImage and Create Image
	Many of the dimensions are hard-coded, as are the number of tissues. Ideally this latter should use a lookup on the Tissue Catalogue
	14.10.2022
*/		

import java.awt.*;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

public class MirTranscriptImage
{
	int numTissues;			// number of tissues — must calculate to exclude reference tissues
	Tissue[] tissues;		// array of tissues (in M, F, L order)
	
	// fonts (apologies for specific names, but needed to ensure SVG can be loaded into Adobe Illustrator)
	private Font titleFt = new Font("arial", Font.PLAIN,13);
	private Font labelFt = new Font("arial", Font.PLAIN,12);
	private Font axisFt = new Font("arial", Font.PLAIN,11);	

	// overall dimensions - these need to be hard coded
	private int imgWidth = 810;
	private int imgHeight = 200;
	
	// layout gutters and spacings
	private int gutter = 20;	// minimum stand-off of graphics from edge
	private int lSpace = 150;	// space between left gutter and graph	
	private int topSpace;		// space between top gutter and graph
	private int botSpace;		// space between bottom gutter and graph

	// graph dimensions
	int oriX;
	int oriY;
	int graphHeight;		// this is total height for composite bar
	
	// bar dimensions
	int barWidth = 15;		// width of bar
	int spaceWidth = 0;		// width of space between bars

	Color colourA;
	Color colourB;
	
	String htmlSVG;					// string with untrimmed SVG
	
	private MirTissueTranscriptGroup [] mirTTGList;	// Holds a mirTTG for ea tissue
	private final int MMTG_LIST_LENGTH = 50;			// Sufficient for all tissues
	private int mttgListSize = 0;						// Occupancy
	private String fbgn;
	
	MirTranscriptTissueDataSet mttdsA = null;
	MirTranscriptTissueDataSet mttdsB = null;
	
	// Constructor from MirTranscriptTissueDataSet[]
	public MirTranscriptImage(MirTranscriptTissueDataSet[] mttds, int size, String fbgn, TissueCatalogue tCat)
	{
		this.fbgn = fbgn;
	
		// Get all non-reference Tissue objects
		numTissues = tCat.getTissueListSize();		
		tissues = new Tissue[numTissues];
		int count = 0;
		for(int i=0; i<numTissues; i++)
		{
			if(!tCat.getTissue(i).isReference())
			{
				tissues[count] = tCat.getTissue(i);
				count++;
			}
		}
		numTissues = count;	// Now adjust for three reference tissues
		// MUST come after calc of number of tissues!
		buildGroups( mttds, size);
		
		colourA = new Color(240, 50, 30); 	// Colour-blind red
		colourB = new Color(60, 140, 240); 	// Colour-blind blue
		
		// Get a DOMImplementation.
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		
		// Create an instance of org.w3c.dom.Document.
		String svgNS = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument(svgNS, "svg", null);
		
		// Create an instance of the SVG Generator.
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
		svgGenerator.setSVGCanvasSize(new Dimension(imgWidth, imgHeight));		// Needed! Should match image size 
		
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
	
	// Takes MirTranscriptTissueDataSet and picks each tissue in turn and builds a MirTissueTranscriptGroup
	private void buildGroups(MirTranscriptTissueDataSet[] mttds, int size)
	{
		mirTTGList = new MirTissueTranscriptGroup [MMTG_LIST_LENGTH];
		
		// Capture individual MirTranscriptTissueDataSet objects
		for(int i=0;i<size;i++)
		{
			MirTranscriptTissueDataSet temp = mttds[i];
			String fbtr = temp.getFBtr();
			String transcriptName = new String();
			// Use FBtr to determine transcript name and hence whether RA or RB
			Connect cnt = new Connect();
			Connection conn = cnt.getConnection();
			ParamQuery parTIQ = DBQuery.getParamQuery("INFO_FROM_FBTR");
			try 
			{
				parTIQ.setPrepStatement(conn);
			} 
			catch (SQLException e) 
			{System.out.println(e.toString());}	
			
			try 
			{
				PreparedStatement prepStat = parTIQ.getPrepStatement();
				prepStat.setString(1, fbtr);
				ResultSet resSet = prepStat.executeQuery();
				if(resSet.first())	// move to single tuple
				{
					transcriptName = resSet.getString("TranscriptName");
				}
			}
			catch (SQLException e)
			{
				System.out.println("SQL Exception: " + e.toString());
			}	
			
			// and hence whether RA or RB
			if(transcriptName.endsWith("A"))
				{mttdsA = temp;}
			else if(transcriptName.endsWith("B"))
				{mttdsB = temp;}
		}
		
		// now need to assume ea MirTranscriptTissueDataSet is same length and with tissues in same order. True but Ugh!
		for(int i=0; i<mttdsA.getTranscriptDataListSize(); i++)
		{
			int tissueID = mttdsA.getMirTranscriptTissueData(i).getTissueID();
			//int tpmM = mttdsA.getMirTranscriptTissueData(i).getTPM();
			int tpmA = 0;
			int tpmB = 0;
			if(mttdsA != null)
			{
				tpmA = mttdsA.getMirTranscriptTissueData(i).getTPM();
			}
			if(mttdsB != null)
			{
				tpmB = mttdsB.getMirTranscriptTissueData(i).getTPM();
			}
			MirTissueTranscriptGroup mttg = new MirTissueTranscriptGroup(tissueID, tpmA, tpmB);
			mirTTGList[mttgListSize] = mttg;
			mttgListSize++;
		}	
		
		// Sort mirTTGList by order for presentation i.e. same as names list
			
		// 1. Create a new empty mirTTGList 
		MirTissueTranscriptGroup[] newList = new MirTissueTranscriptGroup[mttgListSize];
		// 2. Find mirTTG in mirTTGList corresponding to first Tissue in (already sorted) tissues list
		// 3. Add to new list
		for(int i=0; i<numTissues; i++)
		{
			int currentID = tissues[i].getTissueID();		// get tissueID from tissue array
			for(int j=0; j<mttgListSize; j++)
			{
				if(currentID == mirTTGList[j].getTissueID() )
				{
					MirTissueTranscriptGroup currentMTTG = mirTTGList[j];
					newList[i] = currentMTTG;
				}
			}
		}
		// 4. Finally replace old list by newly sorted version
		mirTTGList = newList;
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
        
        // draw outline
        g.setColor(Color.black);
		g.drawRect(0, 0, imgWidth, imgHeight);
					
		calculateDimensions(g);	
		drawTitles(g);
		drawGraph(g);
		drawLegend(g);
		drawScale(g);
	}

	//------------------------ Subsidiary drawing methods ---------------------------//
			
	// draw rectangular frame of graph leaving gutter and text space
	private void calculateDimensions(Graphics g)
	{
		//calculateStandoff(g);
		FontMetrics fmt = g.getFontMetrics(titleFt);
		topSpace = 10 + fmt.getAscent() + fmt.getDescent();		
		FontMetrics fma = g.getFontMetrics(axisFt);
		botSpace = 10 + fma.getAscent() + fma.getDescent();	
		
		oriX = gutter + lSpace;
		oriY = gutter + topSpace;
		graphHeight = imgHeight - 2*gutter - topSpace - botSpace;
	}
	
	// Draws FBgn title, and Male, Female and Larval headings
	private void drawTitles(Graphics g)
	{
		g.setColor(Color.black);
		g.setFont(titleFt);
		FontMetrics fm = g.getFontMetrics(titleFt);
		String title = ( fbgn.substring(0,1).toUpperCase() + fbgn.substring(1, fbgn.length()));
		int xTitle = gutter;
		int yTitle = gutter + fm.getAscent();
		g.drawString(title, xTitle, yTitle);
		
		// Draw sex descriptor headings — Male, Female, Larval
		g.setColor(Color.black);
		g.setFont(labelFt);
		FontMetrics lfm = g.getFontMetrics(labelFt);
		int xMale = oriX + 15*barWidth/2 - lfm.stringWidth("Male")/2;
		g.drawString("Male", xMale, yTitle);
		int xFemale = oriX + 15*barWidth + 16*barWidth/2 - lfm.stringWidth("Female")/2;
		g.drawString("Female", xFemale, yTitle);
		int xLarval = oriX + 31*barWidth + 9*barWidth/2 - lfm.stringWidth("Larval")/2;
		g.drawString("Larval", xLarval, yTitle);
	}
	
	// Draws bars for graph and x axis names
	private void drawGraph(Graphics g)
	{
		int barX = oriX;
		for(int i=0; i<numTissues; i++)
		{
			// get details of mir group for one tissue
			MirTissueTranscriptGroup currentTTG = mirTTGList[i];
			double fractA = currentTTG.getFractA();
			double fractB = currentTTG.getFractB();
			int tpmTotal = currentTTG.getTPM(); 
			// calc height of each component
			int htA = (int) Math.round(graphHeight*fractA);
			int htB = (int) Math.round(graphHeight*fractB);
			// calc y for ea component starting at top, i.e. B, A but adding height ea time
			int oriyB = oriY;
			int oriyA = oriyB + htB;
			
			// draw bar components changing colour
			Color shadeB = PageUtility.fadeBlue(tpmTotal);
			g.setColor(shadeB);
			g.fillRect(barX, oriyB, barWidth, htB);
			
			Color shadeA = PageUtility.fadeRed(tpmTotal);
			g.setColor(shadeA);
			g.fillRect(barX, oriyA, barWidth, htA);
									
			// Draw legend (tissue abbreviations) 
			g.setFont(axisFt);
			FontMetrics fm = g.getFontMetrics(axisFt);
			int xName = barX + barWidth/2 - fm.stringWidth(tissues[i].getAbbreviation())/2;
			int yName = imgHeight - gutter - fm.getDescent();		
			g.setColor(Color.black);
			g.drawString( tissues[i].getAbbreviation(), xName, yName);	
			
			// increment barX
			barX = barX + barWidth + spaceWidth;
		}
		// Draw boxes with Male, Female and larval
		g.setColor(Color.white);
		g.drawRect(oriX, oriY+graphHeight, barWidth*15, 40);
		g.drawRect(oriX + barWidth*15, oriY+graphHeight, barWidth*16, 40);
		g.drawRect(oriX + barWidth*31, oriY+graphHeight, barWidth*9, 40);
		
		// Draw final enclosing rectangles to disguise 1 px rounding errors and to separate M, F and L
		g.setColor(Color.black);
		g.drawRect(oriX, oriY, barWidth*numTissues, graphHeight);					// outside whole bar chart
		g.drawRect(oriX + barWidth*15 + 1, oriY, barWidth*16, graphHeight);			// 16 F tissues
	}	
	
	// For checking: mir-34 has both RA and RB, mir-184 is only RB
	private void drawLegend(Graphics g)
	{
		g.setColor(Color.black);
		g.setFont(axisFt);
		FontMetrics fm = g.getFontMetrics(axisFt);
		// positions hard-coded
		int xLeg = gutter + 40;
		int asc = fm.getAscent();
		
		int yA = oriY + (graphHeight/3) + asc/2 + (graphHeight/6);
		int yB = oriY + asc/2 + (graphHeight/6);
		
		// Text for FBtrs
		String fbtrA = "FBtr not assigned";
		String fbtrB = "FBtr not assigned";
		
		if(mttdsA != null)
		{
			fbtrA = mttdsA.getFBtr();
		}
		if(mttdsB != null)
		{
			fbtrB = mttdsB.getFBtr();
		}
		
		// Draw coloured square and RX followed by FBtr
		g.setColor(colourB);
		g.fillRect(xLeg-(3*asc/2), yB-asc, asc, asc);
		g.setColor(Color.black);
		g.drawString(("RB: ") + fbtrB, xLeg, yB);
		g.setColor(colourA);
		g.fillRect(xLeg-(3*asc/2), yA-asc, asc, asc);
		g.setColor(Color.black);	
		g.drawString(("RA: ") + fbtrA, xLeg, yA);
	}
	
	private void drawScale(Graphics g)
	{
		g.setColor(Color.black);
		g.setFont(axisFt);
		
		FontMetrics fm = g.getFontMetrics(axisFt);
		int asc = fm.getAscent();
		
		// positions hard-coded
		int xScale = oriX + barWidth*numTissues + 5;
		int y0 = oriY + graphHeight;
		int y1 = oriY + asc;
		
		String Zero = "0%";
		String Hundred = "100%";
		
		g.drawString(Zero, xScale, y0);
		g.drawString(Hundred, xScale, y1);
	}

}