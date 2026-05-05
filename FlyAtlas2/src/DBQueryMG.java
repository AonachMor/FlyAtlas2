 /*
DBQueryMG
Class with static method(s) to provide access to SQL query strings
DPL 07.09.2021
*/

public class DBQueryMG
{			
    // QUERIES FOR GENE & TRANSCRIPT INFO
    
    // query0 get midgutGene info from FBgn
    final static String name0 = "INFO_FROM_FBGN";
    final static String query0 = "SELECT DISTINCT FBgn, CGNum, Symbol, Name, Locus, BioType FROM Gene WHERE FBgn= ? ";  
    
    // query1 get midgutGene info from CG
    final static String name1 = "INFO_FROM_CGNUM";
    final static String query1 = "SELECT DISTINCT FBgn, CGNum, Symbol, Name, Locus, BioType FROM Gene WHERE CGnum= ? ";
    
    // query2 get midgutGene info from Symbol
    final static String name2 = "INFO_FROM_SYMBOL";
    final static String query2 = "SELECT DISTINCT FBgn, CGNum, Symbol, Name, Locus, BioType FROM Gene "
    		+ "WHERE (Symbol = BINARY ? OR RomanSymbol = BINARY ?) ";
    
    // query3 get midgutGene info from Name 
    final static String name3 = "INFO_FROM_NAME";
    final static String query3 = "SELECT DISTINCT FBgn, CGNum, Symbol, Name, Locus, BioType FROM Gene "
    		+ "WHERE (Name = ? OR RomanName = ?) ";
    
    // query4 get transcript ids corresponding to a single midgutGene (TranscriptName not used, but needed for sorting)
    final static String name4 = "FBTRS_FROM_FBGN";
    final static String query4 = "SELECT DISTINCT FBtr, TranscriptName FROM Transcript WHERE FBgn= ? ORDER BY TranscriptName"; 
    
    // query5 get transcript info from FBtr
    final static String name5 = "INFO_FROM_FBTR";
    final static String query5 = "SELECT DISTINCT FBgn, TranscriptName, Strand, ExonCount, ExonStarts, ExonEnds FROM Transcript WHERE FBtr= ?"; 
    
    // QUERIES TO RETRIEVE EXPERIMENTAL DATA
	
    // query6 get midgutGene FPKM data
    final static String name6 = "GENE_DATA_FROM_FBGN";
    final static String query6 =
		"SELECT DISTINCT TissueID, FPKM, Replicate1, Replicate2, Replicate3, Status, SD  "
		+ "FROM GeneFPKM "
		+ "WHERE FBgn = ? "
		+ "ORDER BY TissueID ";
    
    // query7 get transcript FPKM data
    final static String name7 = "TRANSCRIPT_DATA_FROM_FBTR";
    final static String query7 =
		"SELECT DISTINCT TissueID, FPKM, Status, SD  "
		+ "FROM TranscriptFPKM "
		+ "WHERE FBtr = ? "
		+ "ORDER BY TissueID ";
    
    // QUERIES FOR AMBIGUITY
    
    // query8 get MaskingFBgn(s) from MaskedFBgn
    final static String name8 = "MASKING_FROM_MASKED";
    final static String query8 = "SELECT DISTINCT MaskingFBgn FROM Ambiguity WHERE MaskedFBgn= ? ORDER BY MaskingFBgn";
    
    // query9 get MaskedFBgn(s) from MaskingFBgn
    final static String name9 = "MASKED_FROM_MASKING";
    final static String query9 = "SELECT DISTINCT MaskedFBgn FROM Ambiguity WHERE MaskingFBgn= ? ORDER BY MaskedFBgn";  
 
    // QUERIES FOR DIFFERENTIAL EXPRESSION
    
    // query10 Genes differentially expressed in one tissue with respect to others (excluding RNA genes)
    final static String name10 = "DIFF_EXPRESS_GENE_BY_TISSUE";
    final static String query10 = "SELECT DISTINCT DiffGene.FBgn, DiffGene.DiffRatio "
    		+ "FROM DiffGene, Gene "
    		+ "WHERE Gene.FBgn = DiffGene.FBgn "
    		+ "AND DiffGene.DiffRatio >= 2 "
    		+ "AND DiffGene.TissueID = ? "
    		+ "AND Gene.CGnum LIKE 'CG%' "
    		+ "ORDER BY DiffGene.DiffRatio DESC";  
    
    // query11 Transcripts differentially expressed in one tissue with respect to others where midgutGene not differentially expressed
    final static String name11 = "DIFF_EXPRESS_TRANSCRIPT_BY_TISSUE";
    final static String query11 = "SELECT DISTINCT dg.FBgn, dt.FBtr, dt.DiffRatio "
    		+ "FROM DiffGene dg, DiffTranscript dt, Transcript t "
    		+ "WHERE  dt.FBtr = t.FBtr "
    		+ "AND dg.FBgn = t.FBgn "
    		+ "AND dt.TissueID = dg.TissueID "
    		+ "AND dt.DiffRatio >= 2 "
    		+ "AND dg.DiffRatio < 2 "
    		+ "AND dt.TissueID = ? "
    		+ "ORDER BY dt.DiffRatio DESC "; 
    
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
		new ParamQuery(name7, query7),
		new ParamQuery(name8, query8),
		new ParamQuery(name9, query9),
		new ParamQuery(name10, query10),
		new ParamQuery(name11, query11)
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
			"SELECT TissueID, Stage, Sex, TissueName, UniTissue " +
			"FROM Tissue ";
	
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