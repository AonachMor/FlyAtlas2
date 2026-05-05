// Searches database for top genes expressed preferentially in a particular gut part
// 13.05.2017

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TopSearch 
{	
	String [] fbgnList;						// array to hold FBgns retrieved in first query
	final int FBGN_LENGTH = 20000;
	int fbgnListSize = 0;
	
	Expression [] expressList;
	final int EXPR_LENGTH = 250;
	int expressListSize = 0;	
	
	Gene [] geneList;
	int geneListSize = 0;	
	
	int actualDisplayed = 0;				// actual number to be displayed (can be less than maxDisplayed)
	int refID = 0;							// 
	
	public TopSearch(int tissueID, boolean byAbundance, boolean microRNA, int maxDisplayed, TissueCatalogue tCat)
	{	
		// Get tissueID of reference whole tissue corr to user selection (for Enrichment calc)
		String refSex = tCat.getSexByID(tissueID);
		refID = tCat.getRefIDbySex(refSex);
		if(refID == -1)
		{ return; }
		
		expressList = new Expression[EXPR_LENGTH];
		geneList = new Gene[EXPR_LENGTH];			// same length as parallel array
		
		
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		if(byAbundance)
		{
			fbgnList = new String [FBGN_LENGTH];			
			makeTopAbundanceQuery(tissueID, microRNA, conn);
			
			// allow for fewer hits than user has selected as max (not really necessary)
			if(maxDisplayed > fbgnListSize) { actualDisplayed = fbgnListSize;}
			else{ actualDisplayed = maxDisplayed;}
			
			for(int i=0; i<actualDisplayed; i++)
			{
				GeneSearch gs = new GeneSearch(fbgnList[i], "fbgn", tCat);
				Expression express;
				if(!microRNA)
				{
					express = gs.getExpression();
				}
				else
				{
					express = gs.getMirExpression();
				}
				expressList[i] = express;
				expressListSize++;
				Gene gene = gs.getGene();
				geneList[i] = gene;
				geneListSize++;
			}
		}
		else		// by Enrichment
		{
			fbgnList = new String [FBGN_LENGTH];			
			makeTopEnrichmentQuery(tissueID, microRNA, conn);
			
			// allow for fewer hits than user has selected as max (not really necessary)
			if(maxDisplayed > fbgnListSize) { actualDisplayed = fbgnListSize;}
			else{ actualDisplayed = maxDisplayed;}
	
			for(int i=0; i<actualDisplayed; i++)
			{
				
				GeneSearch gs = new GeneSearch(fbgnList[i], "fbgn", tCat);
				Expression express;
				if(!microRNA)
				{
					express = gs.getExpression();
				}
				else
				{
					express = gs.getMirExpression();
				}
				expressList[i] = express;
				expressListSize++;
				Gene gene = gs.getGene();
				geneList[i] = gene;
				geneListSize++;
			}		
		}
		
		// close connection
		if(conn != null)
		{
			try { conn.close();}
			catch(Exception e){System.out.println("Can't close.");}
		}		
	}
	
	private void makeTopAbundanceQuery(int tissueID, boolean microRNA, Connection conn)
	{
		// Get sorted list of FBgns of genes that are most abundant in a particular tissue
		String topAbundanceQuery = new String();
		if(!microRNA)
		{
			topAbundanceQuery = "TOP_ABUNDANCE_PR_GENES_BY_TISSUE";
		}
		else
		{
			topAbundanceQuery = "TOP_ABUNDANCE_MIRS_BY_TISSUE";
		}
			
		ParamQuery parAGQ = DBQuery.getParamQuery(topAbundanceQuery);
		try 
		{
			parAGQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	

		try 
		{
			PreparedStatement prepStat = parAGQ.getPrepStatement();
			prepStat.setInt(1, tissueID);			
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					String fbgn = resSet.getString("FBgn");
					fbgnList[fbgnListSize] = fbgn;
					fbgnListSize++;
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
	}
	
	private void makeTopEnrichmentQuery(int tissueID, boolean microRNA, Connection conn)
	{
		// Get sorted list of FBgns of genes that are most enhanced in a particular tissue
		String topEnrichmentQuery = new String();
		if(!microRNA)
		{
			topEnrichmentQuery = "TOP_ENRICHMENT_PR_GENES_BY_TISSUE";
		}
		else
		{
			topEnrichmentQuery = "TOP_ENRICHMENT_MIRS_BY_TISSUE";
		}
		
		ParamQuery parEGQ = DBQuery.getParamQuery(topEnrichmentQuery);
		try 
		{
			parEGQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	

		try 
		{
			PreparedStatement prepStat = parEGQ.getPrepStatement();
			prepStat.setInt(1, refID);
			prepStat.setInt(2, tissueID);
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					String fbgn = resSet.getString("FBgn");
					fbgnList[fbgnListSize] = fbgn;
					fbgnListSize++;
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
	}
	
	// get array of Expression objects from this search
	public Expression[] getExpressList()
	{
		return expressList;
	}
	
	public int getActualDisplayed()
	{
		return actualDisplayed;
	}
	
	public Gene[] getGeneList()
	{
		return geneList;
	}
	
}
