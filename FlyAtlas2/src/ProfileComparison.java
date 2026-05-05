// Calculates correlation scores for all ProfileTissueData objects and selects those above a selected 'r' value and below a selected 'p' value
// Was ProbeComparison.java
// 10.10.2012
// Revised for BeetleAtlas 24.02.2021
// Variables renamed 08.03.2021

import java.util.Arrays;
import org.apache.commons.math.MathException;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.stat.ranking.NaturalRanking;
import org.apache.commons.math.stat.ranking.RankingAlgorithm;

public class ProfileComparison
{
	int processedCount;
	ProfileTissueData[] processedDataList;
	boolean byPearson;
	ProfileTissueData queryData;
	double [] test, query;
	double [][] grid;

	public ProfileComparison(ProfileTissueData queryData, ProfileTissueData[] queryDataList, int listSize, boolean byPearson, double rCut)
	{	
		this.byPearson = byPearson;
		this.queryData = queryData;

		//removes nulls
		ProfileTissueData[] trimmedSet = new ProfileTissueData[listSize];
		System.arraycopy(queryDataList, 0, trimmedSet, 0, listSize);
		queryDataList = trimmedSet;

		for (int i =0; i < listSize; i++)
		{
			calculateArrays(queryDataList[i]);

			//gets r correlation
			PearsonsCorrelation pearson = new PearsonsCorrelation();
			queryDataList[i].setRstat(pearson.correlation(query, test));

			//gets P values
			pearson = new PearsonsCorrelation(grid);
			try 
			{
				queryDataList[i].setPstat(pearson.getCorrelationPValues().getEntry(0,1));
			} 
			catch (MatrixIndexException e) 
			{
				e.printStackTrace();
			} 
			catch (MathException e) 
			{
				e.printStackTrace();
			}
		}

		int notNaN = 0;
		ProfileTissueData[] NaNfree = new ProfileTissueData[queryDataList.length];

		for(int NaNchecker = 0; NaNchecker < queryDataList.length; NaNchecker++)
		{
			if(!Double.isNaN(queryDataList[NaNchecker].getRstat()))
			{
				NaNfree[notNaN] = queryDataList[NaNchecker];
				notNaN++;
			}
		}
		
		queryDataList = new ProfileTissueData[notNaN];
		System.arraycopy(NaNfree, 0, queryDataList, 0, notNaN);
		listSize = notNaN;
		
		// Scott had omitted this from the new version he sent me
		CorrelationComparator correlationComparator = new CorrelationComparator();
		Arrays.sort(queryDataList, correlationComparator);

		processedCount = 0;

		processedDataList = new ProfileTissueData[listSize];

		//taking only significant and strong correlation (respectively to booleans)
		for(int i = 0; i < listSize; i++)
		{
			processedDataList[processedCount] = queryDataList[i];
			processedCount++;
		}

		//removes nulls
		ProfileTissueData[] trimmedSet2 = new ProfileTissueData[processedCount];
		System.arraycopy(processedDataList, 0, trimmedSet2, 0, processedCount);
		processedDataList = trimmedSet2;
	}

	public int getCorListSize()
	{
		return processedCount;
	}

	public ProfileTissueData[] getGenExCorList()
	{
		return processedDataList;
	}

	public void calculateArrays(ProfileTissueData testSet)
	{
		double[] tempTest = new double[100];
		double[] tempQuery = new double[100];

		int arrayPos = 0;

		//start at '0', empty cells at end will later be trimmed
		for(int i = 1; i < 100; i++)
		{

			if(queryData.getDatum(i)!=null && testSet.getDatum(i)!=null)
			{
				if(queryData.getDatum(i).getStatus().equals("OK"))
				{
					tempQuery[arrayPos] = queryData.getDatum(i).getLogFPKM();
				}
				else
				{
					tempQuery[arrayPos] = 0;
				}
				if(testSet.getDatum(i).getStatus().equals("OK"))
				{
					tempTest[arrayPos] = testSet.getDatum(i).getLogFPKM();
				}
				else
				{
					tempTest[arrayPos] = 0;
				}
				arrayPos++;
			}	
		}

		//trims nulls from end of array
		test = new double[arrayPos];
		System.arraycopy(tempTest, 0, test, 0, arrayPos);
		query = new double[arrayPos];
		System.arraycopy(tempQuery, 0, query, 0, arrayPos);

		if(!byPearson)
		{
			RankingAlgorithm ranking = new NaturalRanking();
			query = ranking.rank(query);
			test = ranking.rank(test);		
		}

		//builds 2D array with COLUMNS as variables (as per Apache commons Pearson spec.)
		grid = new double[arrayPos][2];
		for(int i = 0; i < arrayPos; i++)
		{
			grid[i][0] = query[i];
			grid[i][1] = test[i];
		}
	}
}
