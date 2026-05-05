 /*
DBQuery
Class with static method(s) to provide access to SQL query strings
DPL 05.09.2025
*/

public class DBQuery
{			
    // QUERIES FOR GENE & TRANSCRIPT INFO
    
    // query0 get gene info from FBgn (extended version for Roman symbols and names because of Excel not supporting utf8)
    final static String name0 = "INFO_FROM_FBGN";
    final static String query0 = "SELECT DISTINCT FBgn, CGNum, Symbol, RomanSymbol, Name, RomanName, Locus, BioType, NeuropeptideID, Para FROM Gene WHERE FBgn= ? ";  
    
    // query1 get transcript ids corresponding to a single gene (TranscriptName not used, but needed for sorting)
    final static String name1 = "FBTRS_FROM_FBGN";
    final static String query1 = "SELECT DISTINCT FBtr, TranscriptName FROM Transcript WHERE FBgn= ? ORDER BY TranscriptName"; 
    
    // query2 get transcript info from FBtr
    final static String name2 = "INFO_FROM_FBTR";
   // final static String query2 = "SELECT DISTINCT FBgn, TranscriptName, hasFPKM FROM Transcript WHERE FBtr= ?"; 
    final static String query2 = "SELECT DISTINCT FBgn, TranscriptName FROM Transcript WHERE FBtr= ?"; 
    
    // QUERIES TO RETRIEVE EXPERIMENTAL DATA
	
    // query3 get gene FPKM data
    final static String name3 = "GENE_DATA_FROM_FBGN";
    final static String query3 =
		"SELECT DISTINCT TissueID, FPKM, Replicate1, Replicate2, Replicate3, SD, Status "
		+ "FROM GeneFPKM "
		+ "WHERE FBgn = ? "
		+ "ORDER BY TissueID ";
    
    // query4 get transcript FPKM data
    final static String name4 = "TRANSCRIPT_DATA_FROM_FBTR";
    final static String query4 =
		"SELECT DISTINCT TissueID, FPKM, SD  "
		+ "FROM TranscriptFPKM "
		+ "WHERE FBtr = ? "
		+ "ORDER BY TissueID ";
    
    // query5 get microRNA TPM data
    final static String name5 = "MIR_DATA_FROM_FBGN";
    final static String query5 =
		"SELECT DISTINCT TissueID, TPM, Replicate1, Replicate2, Replicate3, SD  "
		+ "FROM GeneTPM "
		+ "WHERE FBgn = ? "
		+ "ORDER BY TissueID ";
    
    // query6 get microRNA transcript TPM data
    final static String name6 = "MIR_TRANSCRIPT_DATA_FROM_FBTR";
    final static String query6 =
		"SELECT DISTINCT TissueID, TPM, SD  "
		+ "FROM TranscriptTPM "
		+ "WHERE FBtr = ? "
		+ "ORDER BY TissueID ";

    // QUERY FOR UNITISSUE NAME
 
    // query7 Get UniTissueName from ID
    final static String name7 = "UNITISSUENAME_FROM_ID";
    final static String query7 = "SELECT UniTissueName FROM UniTissue WHERE UniTissueID = ? ";
    
     
	// creates an array of all ParamQuerys	
	static ParamQuery pqList [] =
	{		
		new ParamQuery(name0, query0),
		new ParamQuery(name1, query1),
		new ParamQuery(name2, query2),
		new ParamQuery(name3, query3),
		new ParamQuery(name4, query4),
		new ParamQuery(name5, query5),
		new ParamQuery(name6, query6),
		new ParamQuery(name7, query7)
	};
    
	// finds ParamQuery object by queryName and returns
	public static ParamQuery getParamQuery(String name)
	{
		for (int i=0; i < pqList.length; i++)
		{
		 	if (pqList[i].getQueryName().equals(name))
		 	{
		 		return pqList[i];
		 	}
		}
		return null;
	}
	
	/* --- Constants for simple entity queries --- */
	
	static String flyTissueQuery = 
			"SELECT DISTINCT TissueID, Stage, Sex, TissueName, Abbreviation, UniTissueID, Reference " +
			"FROM Tissue " + 
			"ORDER BY Stage, Sex, UniTissueID ";
	
	static String allFBgnQuery = "SELECT FBgn FROM Gene ";
	
	static String allFBtrQuery = "SELECT FBtr FROM Transcript  ";
	
	// returns SQL query to retrieve all details from FlyAnat table	
	public static String getFlyTissueQuery()
	{
		return flyTissueQuery;
	}
	
	// returns SQL query to retrieve all FBgn IDs	
	public static String getAllFBgnQuery()
	{
		return allFBgnQuery;
	}
	
	// returns SQL query to retrieve all FBtr IDs
	public static String getAllFBtrQuery()
	{
		return allFBtrQuery;
	}
	
}