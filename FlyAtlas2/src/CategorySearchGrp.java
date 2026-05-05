// Alternative version of Category Search that works on FLyBase GroupIDs
// At moment queries are restricted to protein genes
// 30.09.2021

import java.sql.*;

public class CategorySearchGrp 
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
	
	public CategorySearchGrp(String groupID, TissueCatalogue tCat)
	{
		buildFBgnList(groupID);
		buildExpressGeneLists(tCat, RESULTS_LENGTH);
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

	public void buildFBgnList(String groupID)
	{		
		fbgnList = new String [FBGN_LENGTH];
		
		// conduct appropriate search for categories (GO Term, GO ID, or Free text)
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		ParamQuery parQ = DBQuery.getParamQuery("FBGN_BY_FBGG");
		
		try 
		{
			parQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}

		try 
		{
			PreparedStatement prepStat = parQ.getPrepStatement();
			prepStat.setString(1, groupID);	
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
