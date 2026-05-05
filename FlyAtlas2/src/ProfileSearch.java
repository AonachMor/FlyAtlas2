// Does all the Profile Search stuff — modified from Scott's version that did a lot of this in the Servlet class
// Beetle version 08.03.2021
// Latest update to Fly version 26.09.2021

import java.util.Arrays;

public class ProfileSearch 
{	
	private Expression [] expressList;			// Array of Expression
	private Gene [] geneList;	
	private ProfileTissueData[] boncompList;	
	int displayNum = 0;
	
	public ProfileSearch(String queryFBgn, GeneExpression expresssion, String profTiss, boolean byPearson, double rCut, int displayMax, TissueCatalogue tCat)
	{	
		// make a ProfileTissueDataSet for the query FBgn and retrieve the array
		ProfileTissueDataSet dataSet = new ProfileTissueDataSet(queryFBgn, profTiss);
		ProfileTissueData[] dataList = dataSet.getList();
		int dataSetListSize = dataSet.getListSize();						// 17157 for FA2 = total number of non-mir genes?
		
		// make a ProfileTissueData object for the query gene and populate it from GeneExpression object
		ProfileTissueData queryData = new ProfileTissueData(queryFBgn);
		for (int i = 0; i< expresssion.getGeneData().getGeneDataSize(); i++)	
		{
			if(expresssion.getGeneData().getGeneTissuedata(i) != null)
			{
				ProfileDatum datum = new ProfileDatum(expresssion.getGeneData().getGeneTissuedata(i).getFBgn(),	
						expresssion.getGeneData().getGeneTissuedata(i).getFPKM(), expresssion.getGeneData().getGeneTissuedata(i).getStatus(),
						expresssion.getGeneData().getGeneTissuedata(i).getTissueID());
				queryData.addDatum(datum);
			}			
		}	
		makeComparison(queryData, dataList, dataSetListSize, byPearson, rCut, displayMax, tCat);				
	}
	
/*	public ProfileSearch(String queryFBgn, MirExpression expresssion, String profTiss, boolean byPearson, double rCut, int displayMax, TissueCatalogue tCat)
	{	
		// make a ProfileTissueDataSet for the query FBgn and retrieve the array
		ProfileTissueDataSet dataSet = new ProfileTissueDataSet(queryFBgn, profTiss);
		ProfileTissueData[] dataList = dataSet.getList();
		int dataSetListSize = dataSet.getListSize();						// 17246 for FA2, i.e. total number of genes			
		
		// make a ProfileTissueData object for the query gene and populate it from GeneExpression object
		ProfileTissueData queryData = new ProfileTissueData(queryFBgn);
		for (int i = 0; i< expresssion.getMirData().getMirDataSize(); i++)	
		{
			if(expresssion.getMirData().getMirTissuedata(i) != null)
			{
				ProfileDatum datum = new ProfileDatum(expresssion.getMirData().getMirTissuedata(i).getFBgn(),	
						expresssion.getMirData().getMirTissuedata(i).getTPM(), expresssion.getMirData().getMirTissuedata(i).getStatus(),
						expresssion.getMirData().getMirTissuedata(i).getTissueID());
				queryData.addDatum(datum);
			}			
		}	
		makeComparison(queryData, dataList, dataSetListSize, byPearson, rCut, displayMax, tCat);
	}*/
	
	private void makeComparison(ProfileTissueData queryData, ProfileTissueData[] dataList, int dataSetListSize, 
			boolean byPearson, double rCut, int displayMax, TissueCatalogue tCat)
	{
		// Do the comparison
		ProfileComparison comparison = new ProfileComparison(queryData, dataList, dataSetListSize, byPearson, rCut);
		ProfileTissueData[] compList = comparison.getGenExCorList();		// 9129 for Drosophila	

		// Make the Bonferroni P correction
		double pCut = 0.05;			
		boncompList = bonferroniCorr(compList, pCut);						// Size of this list was 29 for FBgn0040931 (= number of hits)

		// Find how many above rCutoff
		int aboveR = 0;		
		for(int i = 0; i < boncompList.length; i++)
		{
			if(boncompList[i].getRstat() > rCut)
			{
				aboveR++;
			}
		}	
		
		// Reorder list by r, sort, and cut to number above rCutoff
		CorrelationComparator correlationComparator = new CorrelationComparator();
		Arrays.sort(boncompList, correlationComparator);		
		ProfileTissueData[] newList = new ProfileTissueData[aboveR];
		System.arraycopy(boncompList, 0, newList, 0, aboveR);
		boncompList = newList;											
		int boncompListSize = boncompList.length;		// or could just use aboveR — No of genes found  by search that match profile
		
		// Allow for fewer hits than user has selected as max 
		if(displayMax > boncompListSize) { displayNum = boncompListSize;}
		else{ displayNum = displayMax;}
		
		expressList = new Expression[displayNum];
		geneList = new Gene[displayNum];	
		
		for(int i=0; i<displayNum; i++)
		{
			GeneSearch gs = new GeneSearch(boncompList[i].getFBgn(), "fbgn", tCat);
			Expression express;
			express = gs.getExpression();
			expressList[i] = express;
			Gene gene = gs.getGene();
			geneList[i] = gene;
		}	

	}

	
	// Bonferroni correction of P values in Profile Search 
	public ProfileTissueData[] bonferroniCorr(ProfileTissueData[] genExCorList, double pCut)
	{		
		for(int i = 0; i < genExCorList.length; i++)
		{	
			genExCorList[i].setPstat(genExCorList[i].getPstat() * genExCorList.length);
		}	
	
		ProfileTissueData[] cutList = new ProfileTissueData[genExCorList.length];	
		int count = 0;
		
		//cut out results with p value less than the cut-off
		for(int i = 0; i < genExCorList.length; i++)
		{	
			if(genExCorList[i].getPstat() < pCut)
			{
				cutList[count] = genExCorList[i];
				count++;
			}
		}
		
		ProfileTissueData[] shorterList = new ProfileTissueData[count];	
		System.arraycopy(cutList, 0, shorterList, 0, count);	
		genExCorList = shorterList;

		return genExCorList;
	}

	// get array of Expression objects from this search
	public Expression[] getExpressList()
	{
		return expressList;
	}
	
	public Gene[] getGeneList()
	{
		return geneList;
	}
	
	public int getDisplayNumber()
	{
		return displayNum;
	}

	public ProfileTissueData[] getDataList()
	{
		return boncompList;
	}
}
