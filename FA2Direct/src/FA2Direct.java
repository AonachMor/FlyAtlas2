
// FA2Direct: Servlet class of text download utility for tables in FlyAtlas 2
// Updated to include whole tissue data
// DPL 23.09.2022 with error explanation amplification 01.05.2025

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.NumberFormat;

public class FA2Direct extends HttpServlet 
{
	private TissueCatalogue  tCat;		// stores info about all fly tissues and stages: 
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
	{			
		// capture and deal with parameters
		String fbgn = req.getParameter("fbgn");				// Parameter to specify FlyBaseID FBgn or FBtr
		String tableOut = req.getParameter("tableOut");		// Parameter to specify whether "gene" or "transcript" table(s) or same for mir		
		// handle nulls
		if(fbgn == null)
		{
			fbgn = "";
		}
		if(tableOut == null)
		{
			tableOut = "";
		}		
		// To prevent cross-site scripting, accept only letters or numbers
		fbgn = fbgn.replaceAll("[^a-zA-Z0-9]", "");
		tableOut = tableOut.replaceAll("[^a-zA-Z]", "");
		
		// Output filename
		String filename = new String();
		if((fbgn != "" && tableOut.equals("gene")) || (fbgn != "" && tableOut.equals("mir")))
		{
			filename = fbgn +  "G.txt";
		}
		else if((fbgn != "" && tableOut.equals("transcriptGene")) || (fbgn != "" && tableOut.equals("transcriptMir")))
		{
			filename = fbgn +  "T.txt";
		}
		else {filename = "error.txt";}
		
		// Set Content type
		res.setContentType("text/plain;charset=UTF-8");
		res.setCharacterEncoding("UTF-8");
		res.setHeader("Cache-Control", "no-cache");			
		// For forcing download 
		res.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");	
		// Set security headers
		res.setHeader("X-Frame-Options", "deny");
		res.setHeader("X-Content-Type-Options", "nosniff");
		res.setHeader("X-XSS-Protection", "1; mode=block");		
		PrintWriter writer = res.getWriter();		
			
		tCat = new TissueCatalogue();
		if(!tableOut.equals(""))
		{
    		GeneSearch search = new GeneSearch(fbgn, tCat);	
    		Gene gene = search.getGene();
    		Expression express;
    		if(tableOut.equals("gene") || tableOut.equals("transcriptGene"))
    		{
    			express = search.getExpression();
    		}
    		else
    		{
    			express = search.getMirExpression();
    		}
    		
    		if(tableOut.equals("gene") && gene != null && express != null)
    		{
    			writer.println(gene.getGeneInfoText());  
       			writer.println(getGeneTable(gene, express));
    		}
    		else if(tableOut.equals("mir") && gene != null && express != null)
    		{
    			writer.println(gene.getGeneInfoText());  
       			writer.println(getMirTable(gene, express));
    		}
    		else if(tableOut.equals("transcriptGene") && gene != null && express != null)
    		{
    			writer.println(gene.getGeneInfoText());   			
    			writer.println(getTranscriptTable(gene, express, tCat));
    		}
    		else if(tableOut.equals("transcriptMir") && gene != null && express != null)
    		{
    			writer.println(gene.getGeneInfoText());   			
    			writer.println(getMirTranscriptTable(gene, express, tCat));
    		}
    		else if(tableOut.equals("gene") && gene == null)
    		{
    			writer.println("Error — No gene!");
    		}
    		else if(tableOut.equals("gene") && express == null)
    		{
    			writer.println("Error — No expression for gene!");
    		}
    		else{writer.println("An error has occurred.");}
		}
		else{writer.println("Invalid parameters.");}
		writer.close();		
	}
	

	// Construct tab-separated table of gene results: FPKMs and enhancements, including SD
	private String getGeneTable(Gene gene, Expression expression)
	{	
		GeneExpression express = (GeneExpression) expression;
		GeneTissueDataSet dataset = express.getGeneData();		
		NumberFormat N = NumberFormat.getInstance();
		N.setMaximumFractionDigits(2);		
		
		StringBuilder sb = new StringBuilder();
		// Write two header lines for numerical data
		sb.append("\tAdult Male\t\t\tAdult Female\t\t\tMale v. Female\t\tLarval\n");	
		sb.append("Tissue\tFPKM\tSD\tEnrichment\tFPKM\tSD\tEnrichment\tM/F\tp value\tFPKM\tSD\tEnrichment\n");	
		
		// Write each table row in order specified in tCat object's TissueTrthisTript list
		int nonrefCount = 0; 	// counter for included rows — i.e. those other than reference pairs
		for (int i=0; i < tCat.getTripletListSize(); i++)
		{
			TissueTriplet thisTrip = tCat.getTissueTriplet(i);		
			String nextUniTissueName = thisTrip.getUniTissueName();		// Cell1: next non-reference uniTissue in list
			if(nonrefCount > 0)
			{
				sb.append("\n" + nextUniTissueName);
			}
			else
			{
				sb.append(nextUniTissueName);
			}
			
			// MALE //
			Tissue maleTissue = tCat.getTissueTriplet(i).getMaleTissue();				
			if(maleTissue == null)	// if not present write empty cells 2 and 3 
			{
				sb.append("\t-\t-\t-");
			}
			else					// Get appropriate GeneTissueData object and build table columns
			{
				GeneTissueData maleData = dataset.findDataByID(maleTissue.getTissueID());				
				if(maleData != null)
				{	
					sb.append("\t" + maleData.getFPKM() + "\t"  + N.format(maleData.getSD()));
					sb.append("\t" + N.format(maleData.getEnrichment()));
				}
				else { sb.append("\t\t\t");} // temp hack for tissues with data pending
			}
			
			// FEMALE //
			Tissue femaleTissue = tCat.getTissueTriplet(i).getFemaleTissue();				
			if(femaleTissue == null)	// if not present write empty cells 4 and 5 
			{
				sb.append("\t-\t-\t-");
			}
			else					// Get appropriate GeneTissueData object and build table columns
			{
				GeneTissueData femaleData = dataset.findDataByID(femaleTissue.getTissueID());				
				if(femaleData != null)
				{	
					sb.append("\t" + femaleData.getFPKM() + "\t"  +  N.format(femaleData.getSD()));
					sb.append("\t" + N.format(femaleData.getEnrichment()));	
				}
				else { sb.append("\t\t\t");} // temp hack for tissues with data pending
			}			
			
			// MALE v. FEMALE //
			if(maleTissue == null || femaleTissue == null)	// if not present write empty cells 6 and 7 
			{
				sb.append("\t-\t-");
			}
			else					// Get appropriate GeneTissueData object and build table columns
			{
				GeneTissueData femaleData = dataset.findDataByID(femaleTissue.getTissueID());
				GeneTissueData maleData = dataset.findDataByID(maleTissue.getTissueID());
				if(femaleData != null && maleData != null)
				{	
					getMFdata(sb, maleData, femaleData);
				}
				else { sb.append("\t\t");} // temp hack for tissues with data pending
			}								
			
			// LARVAL //
			Tissue larvalTissue = tCat.getTissueTriplet(i).getLarvalTissue();			
			if(larvalTissue == null)	// if not present write empty cells 2 and 3 
			{
				sb.append("\t-\t-\t-");
			}
			else					// Get appropriate GeneTissueData object and build table columns
			{
				GeneTissueData larvalData = dataset.findDataByID(larvalTissue.getTissueID());			
				if(larvalData != null)
				{	
					sb.append("\t" + larvalData.getFPKM() + "\t"  +  N.format(larvalData.getSD()));
					sb.append("\t"  + N.format(larvalData.getEnrichment()));	
				}
				else { sb.append("\t\t\t");} // temp hack for tissues with data pending
			}
			nonrefCount++;
		}
		return sb.toString();
	}
	

	// Construct tab-separated table of gene results: FPKMs and enhancements, including SD
	private String getMirTable(Gene gene, Expression expression)
	{	
		MirExpression express = (MirExpression) expression;
		MirTissueDataSet dataset = express.getMirData();		
		NumberFormat N = NumberFormat.getInstance();
		N.setMaximumFractionDigits(2);		
		
		StringBuilder sb = new StringBuilder();
		// Write two header lines for numerical data
		sb.append("\tAdult Male\t\t\tAdult Female\t\t\tMale v. Female\t\tLarval\n");	
		sb.append("Tissue\tTPM\tSD\tEnrichment\tTPM\tSD\tEnrichment\tM/F\tp value\tTPM\tSD\tEnrichment\n");			
		// Write each table row in order specified in tCat object's TissueTrthisTript list
		int nonrefCount = 0; 	// counter for included rows — i.e. those other than reference pairs
		for (int i=0; i < tCat.getTripletListSize(); i++)
		{
			TissueTriplet thisTrip = tCat.getTissueTriplet(i);		
			String nextUniTissueName = thisTrip.getUniTissueName();		// Cell1: next non-reference uniTissue in list
			if(nonrefCount > 0)
			{
				sb.append("\n" + nextUniTissueName);
			}
			else
			{
				sb.append(nextUniTissueName);
			}			
			// MALE //
			Tissue maleTissue = tCat.getTissueTriplet(i).getMaleTissue();				
			if(maleTissue == null)	// if not present write empty cells 2 and 3 
			{
				sb.append("\t-\t-\t-");
			}
			else					// Get appropriate GeneTissueData object and build table columns
			{
				MirTissueData maleData = dataset.findDataByID(maleTissue.getTissueID());				
				if(maleData != null)
				{	
					sb.append("\t" + maleData.getTPM() + "\t"  + N.format(maleData.getSD()));
					sb.append("\t" + N.format(maleData.getEnrichment()));
				}
				else { sb.append("\t\t\t");} // temp hack for tissues with data pending
			}			
			// FEMALE //
			Tissue femaleTissue = tCat.getTissueTriplet(i).getFemaleTissue();				
			if(femaleTissue == null)	// if not present write empty cells 4 and 5 
			{
				sb.append("\t-\t-\t-");
			}
			else					// Get appropriate GeneTissueData object and build table columns
			{
				MirTissueData femaleData = dataset.findDataByID(femaleTissue.getTissueID());				
				if(femaleData != null)
				{	
					sb.append("\t" + femaleData.getTPM() + "\t"  +  N.format(femaleData.getSD()));
					sb.append("\t" + N.format(femaleData.getEnrichment()));	
				}
				else { sb.append("\t\t\t");} // temp hack for tissues with data pending
			}
			// MALE v. FEMALE //
			if(maleTissue == null || femaleTissue == null)	// if not present write empty cells 6 and 7 
			{
				sb.append("\t-\t-");
			}
			else					// Get appropriate GeneTissueData object and build table columns
			{
				MirTissueData femaleData = dataset.findDataByID(femaleTissue.getTissueID());
				MirTissueData maleData = dataset.findDataByID(maleTissue.getTissueID());
				if(femaleData != null && maleData != null)
				{	
					getMFMirdata(sb, maleData, femaleData);
				}
				else { sb.append("\t\t");} // temp hack for tissues with data pending
			}			
			// LARVAL //
			Tissue larvalTissue = tCat.getTissueTriplet(i).getLarvalTissue();			
			if(larvalTissue == null)	// if not present write empty cells 2 and 3 
			{
				sb.append("\t-\t-\t-");
			}
			else					// Get appropriate GeneTissueData object and build table columns
			{
				MirTissueData larvalData = dataset.findDataByID(larvalTissue.getTissueID());			
				if(larvalData != null)
				{	
					sb.append("\t" + larvalData.getTPM() + "\t"  +  N.format(larvalData.getSD()));
					sb.append("\t"  + N.format(larvalData.getEnrichment()));	
				}
				else { sb.append("\t\t\t");} // temp hack for tissues with data pending
			}
			nonrefCount++;
		}
		return sb.toString();
	}
		
	private String getTranscriptTable(Gene gene, Expression expression, TissueCatalogue tCat)
    {
		GeneExpression express = (GeneExpression) expression;	
		
    	StringBuilder sb = new StringBuilder();    	
    	sb.append("\t\t");
		for(int w=0; w<express.getTranscriptDataSize(); w++)
		{
			String fbtr = express.getTranscriptData(w).getFBtr();
			sb.append(gene.getTranscriptByFBTR(fbtr).getNameSuffix() + "/");	
			sb.append(fbtr + "\t\t");
		}
		sb.append("\n");
		
    	sb.append("\t\t");
		for(int z=0; z<express.getTranscriptDataSize(); z++)
		{
			sb.append("FPKM\tSD\t");
		}   
		sb.append("\n");
   	
		for (int i=0; i<tCat.getTissueListSize(); i++)
		{
			sb.append(tCat.getTissue(i).getDescription() + "\t" + tCat.getTissue(i).getTissueName() + "\t");	//  stage and name columns
			int tissID = tCat.getTissue(i).getTissueID();
			for(int k=0; k<express.getTranscriptDataSize(); k++)
			{
				TranscriptTissueDataSet ttds = express.getTranscriptData(k);
				TranscriptTissueData ttd = ttds.getTranscriptTissueDataByID(tissID);
				if(ttd != null)
				{
					sb.append(ttd.getFPKM() + "\t" + ttd.getSD() + "\t");
				}
				else
				{
					sb.append("-\t-\t");						
				}
			}
			sb.append("\n");
		} 
    	return sb.toString();
    }
	
	private String getMirTranscriptTable(Gene gene, Expression expression, TissueCatalogue tCat)
    {
		MirExpression express = (MirExpression) expression;
		
    	StringBuilder sb = new StringBuilder();    	
    	sb.append("\t\t");
		for(int w=0; w<express.getMirTranscriptDataSize(); w++)
		{
			String fbtr = express.getMirTranscriptData(w).getFBtr();
			sb.append(gene.getTranscriptByFBTR(fbtr).getNameSuffix() + "/");	
			sb.append(fbtr + "\t\t");
		}
		sb.append("\n");
		
    	sb.append("\t\t");
		for(int z=0; z<express.getMirTranscriptDataSize(); z++)
		{
			sb.append("TPM\tSD\t");
		}   
		sb.append("\n");
   	
		for (int i=0; i<tCat.getTissueListSize(); i++)
		{
			sb.append(tCat.getTissue(i).getDescription() + "\t" + tCat.getTissue(i).getTissueName() + "\t");	//  stage and name columns
			int tissID = tCat.getTissue(i).getTissueID();
			for(int k=0; k<express.getMirTranscriptDataSize(); k++)
			{
				MirTranscriptTissueDataSet ttds = express.getMirTranscriptData(k);
				MirTranscriptTissueData ttd = ttds.getMirTranscriptTissueDataByID(tissID);
				if(ttd != null)
				{
					sb.append(ttd.getTPM() + "\t" + ttd.getSD() + "\t");
				}
				else
				{
					sb.append("-\t-\t");						
				}
			}
			sb.append("\n");
		} 
    	return sb.toString();
    }
	
	private void getMFdata(StringBuilder sb, GeneTissueData maleData, GeneTissueData femaleData)
	{
		double maleFPKM = maleData.getFPKM();
		double femaleFPKM = femaleData.getFPKM();
		if(maleFPKM < 2)
		{
			maleFPKM = 2;
		}
		if(femaleFPKM < 2)
		{
			femaleFPKM = 2;
		}
		double ratio = maleFPKM/femaleFPKM;
		String ratioText = PageUtility.formatValues(ratio, 2);
		
		MeanComparator mc = new MeanComparator(femaleData, maleData);
		String signif = mc.getSignificance();
		sb.append("\t"+ ratioText + "\t" + signif);
	}
	
	private void getMFMirdata(StringBuilder sb, MirTissueData maleData, MirTissueData femaleData)
	{
		double maleTPM = maleData.getTPM();
		double femaleTPM = femaleData.getTPM();
		if(maleTPM < 2)
		{
			maleTPM = 2;
		}
		if(femaleTPM < 2)
		{
			femaleTPM = 2;
		}
		double ratio = maleTPM/femaleTPM;
		String ratioText = PageUtility.formatValues(ratio, 2);
		
		MeanComparator mc = new MeanComparator(femaleData, maleData);
		String signif = mc.getSignificance();
		sb.append("\t"+ ratioText + "\t" + signif);
	}

}
