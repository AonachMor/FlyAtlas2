// Searches database for genes by a GO identifier or search term that picks up GOs by their description
// Decided not to use GO information in FlyAtlas2
// At moment queries are restricted to protein genes
// 30.09.2017

import java.sql.*;

public class CategorySearch 
{
	String [] fbgnList;						
	final int FBGN_LENGTH = 20000;		// Safe as greater than number of Drosophila genes
	int fbgnListSize = 0;
	
	Expression [] expressList;
	int expressListSize = 0;	
	
	Gene [] geneList;						
	int geneListSize = 0;	

	final int RESULTS_LENGTH = 260;		// This is same as highest maxDisplayed user choice on Category page (and must be at least as large)
	
	int actualDisplayed = 0;				// actual number to be displayed (can be less than maxDisplayed if fewer results found)
	
	public CategorySearch(String keyword, String radioChoice, int maxDisplayed, TissueCatalogue tCat)
	{
		buildFBgnList(keyword, radioChoice);
		buildExpressGeneLists(tCat, maxDisplayed);
	}
	
	private void buildExpressGeneLists(TissueCatalogue tCat, int maxDisplayed)
	{
		expressList = new Expression [RESULTS_LENGTH];
		geneList = new Gene[RESULTS_LENGTH];				// same length as parallel array
		
		// allow for fewer hits than user has selected as max (not really necessary)
		if(maxDisplayed > fbgnListSize) 
		{ 
			actualDisplayed = fbgnListSize;
		}
		else
		{ 
			actualDisplayed = maxDisplayed;
		}
		
		for(int i=0; i<actualDisplayed; i++)
		{
			GeneSearch gs = new GeneSearch(fbgnList[i], "fbgn", tCat);
			
			// Store appropriate number of results
			// No need for expanding array unless user tries to game system (Only add if you want to do this)
			Expression express = gs.getExpression();
			expressList[i] = express;
			expressListSize++;
			
			Gene gene = gs.getGene();
			geneList[i] = gene;
			geneListSize++;
		}	
	}

	public void buildFBgnList(String keyword, String radioChoice)
	{		
		fbgnList = new String [FBGN_LENGTH];
		
		// conduct appropriate search for categories (GO Term, GO ID, or Free text)
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		ParamQuery parQ = null;
		
		if(radioChoice.equals("goTerm"))
		{
			parQ = DBQuery.getParamQuery("FBGN_BY_GONAME");
		}
		else if(radioChoice.equals("goID"))
		{
			parQ = DBQuery.getParamQuery("FBGN_BY_GOID");
		}
		else if(radioChoice.equals("goFree"))
		{
			parQ = DBQuery.getParamQuery("FBGN_BY_GOFREE");
		}
		else
		{
			System.out.println("Error in radio choice at GOSearch");
		}
		
		try 
		{
			parQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}

		try 
		{
			PreparedStatement prepStat = parQ.getPrepStatement();
			
			if(radioChoice.equals("goFree"))
			{
				prepStat.setString(1, "%"+keyword+"%");		// LIKE search in both directions
			}
			else
			{
				prepStat.setString(1, keyword);				// exact searches
			}
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
				resSet.close(); // ! before conn.close()
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		finally // close the connection
		{
			if(conn != null)
			{
				try { conn.close();}
				catch(Exception e){System.out.println("Can't close.");}
			}
		}
	}
	
	// get array of Expression objects from this search
	public Expression[] getExpressList()
	{
		return expressList;
	}
	
	// get array of Gene objects from this search	
	public Gene[] getGeneList()
	{
		return geneList;
	}

	// get size of FBgn from the search — needed to inform user of results not displayed
	public int getFBgnListSize()
	{
		return fbgnListSize;
	}
	
	// get number of results actually displayed
	public int getActualDisplayed()
	{
		return actualDisplayed;
	}
}
