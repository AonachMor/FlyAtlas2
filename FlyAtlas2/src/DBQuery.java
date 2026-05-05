 /*
DBQuery
Class with static method(s) to provide access to SQL query strings
DPL 22.01.2025 — queries adjusted for new DB
DPL 02.03.2025 — further adjustments for DB changes for Ambiguity
Latest update 21.10.2025
*/

public class DBQuery
{			
    // QUERIES FOR GENE & TRANSCRIPT INFO
    
    // query0 get gene info from FBgn — requires lower case search term 
    final static String name0 = "INFO_FROM_FBGN";
    final static String query0 = "SELECT DISTINCT FBgn, CGNum, Symbol, Name, Locus, BioType, NeuropeptideID, Para, Para99 FROM Gene WHERE FBgn= ? ";    
    
    // query1 get gene info from CG — requires lower case search term 
    final static String name1 = "INFO_FROM_CGNUM";
    final static String query1 = "SELECT DISTINCT FBgn, CGNum, Symbol, Name, Locus, BioType, NeuropeptideID, Para, Para99 FROM Gene WHERE CGnum= ? ";
    
    // query2 get gene info from Symbol
    final static String name2 = "INFO_FROM_SYMBOL";
    final static String query2 = "SELECT DISTINCT FBgn, CGNum, Symbol, Name, Locus, BioType, NeuropeptideID, Para, Para99 FROM Gene "
    		+ "WHERE (Symbol = BINARY ? OR RomanSymbol = BINARY ?) ";
    
    // query20 get gene info from Symbol in a non-case-sensitive manner — requires query cast to lower case
    final static String name20 = "INFO_FROM_SYMBOL_NOCASE";
    final static String query20 = "SELECT DISTINCT FBgn, CGNum, Symbol, Name, Locus, BioType, NeuropeptideID, Para, Para99 FROM Gene "
    		+ "WHERE (LOWER(Symbol) = ? OR LOWER(RomanSymbol) = ?) ";
    
    // query3 get gene info from Name 
    final static String name3 = "INFO_FROM_NAME";
    final static String query3 = "SELECT DISTINCT FBgn, CGNum, Symbol, Name, Locus, BioType, NeuropeptideID, Para, Para99 FROM Gene "
    		+ "WHERE (Name = ? OR RomanName = ?) ";
    
    // query4 get transcript ids corresponding to a single gene (TranscriptName not used, but needed for sorting)
    final static String name4 = "FBTRS_FROM_FBGN";
    final static String query4 = "SELECT DISTINCT FBtr, TranscriptName FROM Transcript WHERE FBgn= ? ORDER BY TranscriptName"; 
    
    // query5 get transcript info from FBtr
    final static String name5 = "INFO_FROM_FBTR";
    final static String query5 = "SELECT DISTINCT FBgn, TranscriptName FROM Transcript WHERE FBtr= ?";
    
    // QUERIES TO RETRIEVE EXPERIMENTAL DATA
	
    // query6 get gene FPKM data
    final static String name6 = "GENE_DATA_FROM_FBGN";
    final static String query6 =
		"SELECT DISTINCT TissueID, FPKM, Replicate1, Replicate2, Replicate3, SD, Status "
		+ "FROM GeneFPKM "
		+ "WHERE FBgn = ? "
		+ "ORDER BY TissueID ";
    
    // query7 get transcript FPKM data
    final static String name7 = "TRANSCRIPT_DATA_FROM_FBTR";
    final static String query7 =
		"SELECT DISTINCT TissueID, FPKM, SD  "
		+ "FROM TranscriptFPKM "
		+ "WHERE FBtr = ? "
		+ "ORDER BY TissueID ";
    
    // query15 get microRNA TPM data
    final static String name15 = "MIR_DATA_FROM_FBGN";
    final static String query15 =
		"SELECT DISTINCT TissueID, TPM, Replicate1, Replicate2, Replicate3, SD  "
		+ "FROM GeneTPM "
		+ "WHERE FBgn = ? "
		+ "ORDER BY TissueID ";
    
    // query16 get microRNA transcript TPM data
    final static String name16 = "MIR_TRANSCRIPT_DATA_FROM_FBTR";
    final static String query16 =
		"SELECT DISTINCT TissueID, TPM, SD  "
		+ "FROM TranscriptTPM "
		+ "WHERE FBtr = ? "
		+ "ORDER BY TissueID ";
 
    // TOP QUERIES
    
    // query10 MicroRNAs with high abundance (arbitrarily above TPM = 100)
    final static String name10 = "TOP_ABUNDANCE_MIRS_BY_TISSUE";
    final static String query10 = "SELECT Distinct FBgn, TPM "
    		+ "FROM GeneTPM "
    		+ "WHERE TPM > 100 "
    		+ "AND TissueID = ? "
    		+ "ORDER BY TPM DESC";  
    
    // query11 MicroRNAs with high enrichment (arbitrarily above 2 for TPM > 100)
    final static String name11 = "TOP_ENRICHMENT_MIRS_BY_TISSUE";
    final static String query11 = "SELECT DISTINCT mtpm1.FBgn AS FBgn, mtpm2.TPM/mtpm1.TPM AS Enrichment "
    		+ "FROM GeneTPM mtpm1, GeneTPM mtpm2 "		// 1 is standard, 2 is tissue
    		+ "WHERE mtpm1.FBgn = mtpm2.FBgn "
    		+ "AND mtpm1.TissueID = ? "
    		+ "AND mtpm2.TissueID = ? "
    		+ "AND mtpm1.TPM > 2 "
    		+ "AND mtpm2.TPM > 100 "
    		+ "AND mtpm2.TPM > mtpm1.TPM "
    		+ "ORDER BY Enrichment DESC "; 
    
    // query12 Protein Genes with high abundance (arbitrarily above FPKM = 10)
    final static String name12 = "TOP_ABUNDANCE_PR_GENES_BY_TISSUE";
    final static String query12 = "SELECT Distinct f.FBgn, f.FPKM "
    		+ "FROM GeneFPKM f, Gene g "
    		+ "WHERE f.FBgn = g.FBgn "
    		+ "AND g.CGNum LIKE 'CG%' "
    		+ "AND f.FPKM > 10 "
    		+ "AND f.TissueID = ? "
    		+ "ORDER BY f.FPKM DESC ";  
    
    // query13 Protein Genes with high enrichment (arbitrarily above 2 for FPKMs > 2)
    final static String name13 = "TOP_ENRICHMENT_PR_GENES_BY_TISSUE";
    final static String query13 = "SELECT DISTINCT gfpkm1.FBgn AS FBgn, gfpkm2.FPKM/gfpkm1.FPKM AS Enrichment "
    		+ "FROM GeneFPKM gfpkm1, GeneFPKM gfpkm2, Gene g "		// 1 is standard, 2 is tissue
    		+ "WHERE gfpkm1.FBgn = g.FBgn "
    		+ "AND g.CGNum LIKE 'CG%' "
    		+ "AND gfpkm1.FBgn = gfpkm2.FBgn "
    		+ "AND gfpkm1.TissueID = ? "
    		+ "AND gfpkm2.TissueID = ? "
    		+ "AND gfpkm1.FPKM > 2 "
    		+ "AND gfpkm2.FPKM > 2 "
    		+ "AND gfpkm2.FPKM > gfpkm1.FPKM "
    		+ "ORDER BY Enrichment DESC "; 
    
    // CATEGORY QUERIES
    
    // query17: Category search by GO Description (no RNA genes) 
    final static String name17 = "FBGN_BY_GONAME";
    final static String query17 =    
			"SELECT DISTINCT Gene.FBgn "
			+ "FROM Gene, GeneWithOntol, Ontology "
			+ "WHERE Gene.CGNum LIKE 'CG%' "
			+ "AND Gene.FBgn = GeneWithOntol.FBgn "
			+ "AND GeneWithOntol.GOid = Ontology.GOid "
			+ "AND (Ontology.GOname = ? ) "
			+ "ORDER BY Gene.FBgn";
    
    // query18: Category search by GO ID (no RNA genes)
    final static String name18 = "FBGN_BY_GOID";
    final static String query18 =    
    		"SELECT DISTINCT Gene.FBgn "
			+ "FROM Gene, GeneWithOntol, Ontology "
			+ "WHERE Gene.CGNum LIKE 'CG%' "
			+ "AND Gene.FBgn = GeneWithOntol.FBgn "
			+ "AND GeneWithOntol.GOid = Ontology.GOid "
			+ "AND (Ontology.GOid = ?) "
			+ "ORDER BY Gene.FBgn";
    
    // query19: Category search by Free text (fragment) of GO Descriptions (no RNA genes)
    final static String name19 = "FBGN_BY_GOFREE";
    final static String query19 =    
    		"SELECT DISTINCT Gene.FBgn "
			+ "FROM Gene, GeneWithOntol, Ontology "
			+ "WHERE Gene.CGNum LIKE 'CG%' "
			+ "AND Gene.FBgn = GeneWithOntol.FBgn "
			+ "AND GeneWithOntol.GOid = Ontology.GOid "
			+ "AND (Ontology.GOname LIKE ?) "
			+ "ORDER BY Gene.FBgn";
    
    // query20: Category search by FBgg
    final static String name21 = "FBGN_BY_FBGG";
    final static String query21 =    
    		" SELECT FBgn FROM FBggCorrelation "
			+ "WHERE FBgg = ? "
			+ "ORDER BY FBgn";
    
    // query23: Retrieve FBgn corresponding to FBtr
    final static String name23 = "FBGN_FROM_FBTR";
    final static String query23 =    
    		" SELECT FBgn FROM Transcript "
			+ "WHERE FBtr = ? ";

    // QUERY FOR UNITISSUE DATA
 
    // query14 Get UniTissueName from ID
    final static String name14 = "UNITISSUE_DATA_FROM_ID";
    final static String query14 = "SELECT UniTissueName, Reference, Mir "
    		+ "FROM UniTissue WHERE UniTissueID = ? ";
    
    // query20: Get GroupName from GroupID
    final static String name22 = "GROUPNAME_FROM_GROUPID";
    final static String query22 =    
    		" SELECT groupName FROM FBggDescription "
			+ "WHERE groupID = ? "; 
    
	// PARALOGUE QUERY //
    
    // query24 get Paralogue(s) from FBgn — Not used as transfered to FlyPara?
    final static String name24 = "PARAS_FROM_FBGN";
    final static String query24 = "SELECT DISTINCT ParaID FROM Paralogue "
    		+ "WHERE (FBgn = ?) ";  
    
	// AMBIGUITY QUERIES //
    
    // query8: Retrieves Masked and Masking FBtrs and Parent Masking Genes from query FBgn which may be masked or masking
    final static String name8 = "AMBIGUITY";
    final static String query8 ="SELECT DISTINCT a.MaskedFBtr, a.MaskingFBtr, t1.FBgn AS 'MaskedFBgn', t2.FBgn AS 'MaskingFBgn', a.ParentFBgns, a.AmbiguityType "
    		+ "FROM Transcript t1, Transcript t2, Ambiguity a "
    		+ "WHERE (t1.FBgn = ? OR t2.FBgn = ?) "
    		+ "AND t1.FBtr = a.MaskedFBtr "
    		+ "AND t2.FBtr = a.MaskingFBtr " 
    		+ "ORDER BY ParentFBgns, MaskingFBgn" ;
    
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
		new ParamQuery(name10, query10),
		new ParamQuery(name11, query11),
		new ParamQuery(name12, query12),
		new ParamQuery(name13, query13),
		new ParamQuery(name14, query14),
		new ParamQuery(name15, query15),
		new ParamQuery(name16, query16),
		new ParamQuery(name17, query17),
		new ParamQuery(name18, query18),
		new ParamQuery(name19, query19),
		new ParamQuery(name20, query20),
		new ParamQuery(name21, query21),
		new ParamQuery(name22, query22),
		new ParamQuery(name23, query23),
		new ParamQuery(name24, query24)
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
	
	// Mixture of M and F tissues and larval tissues, but not carcass or whole
	static String profileQueryAL =
			"SELECT f.FBgn, f.FPKM, f.Status, f.TissueID " 
			+ "FROM GeneFPKM f, Tissue t "
			+ "WHERE f.TissueID = t.TissueID "
			+ "AND t.ProfileAL = 1 "
			+ "ORDER BY f.FBgn, f.TissueID ";
	
	//  Adult tissues only, but not whole
	static String profileQueryAO =
			"SELECT f.FBgn, f.FPKM, f.Status, f.TissueID " 
			+ "FROM GeneFPKM f, Tissue t "
			+ "WHERE f.TissueID = t.TissueID "
			+ "AND t.ProfileAO = 1 "
			+ "ORDER BY f.FBgn, f.TissueID ";
	
	//  Larval tissues only, but not whole
	static String profileQueryLO =
			"SELECT f.FBgn, f.FPKM, f.Status, f.TissueID " 
			+ "FROM GeneFPKM f, Tissue t "
			+ "WHERE f.TissueID = t.TissueID "
			+ "AND t.ProfileLO = 1 "
			+ "ORDER BY f.FBgn, f.TissueID ";
	
	// All adult male tissues except whole
	static String profileQueryMA =
			"SELECT f.FBgn, f.FPKM, f.Status, f.TissueID " 
			+ "FROM GeneFPKM f, Tissue t "
			+ "WHERE f.TissueID = t.TissueID "
			+ "AND t.ProfileMA = 1 "
			+ "ORDER BY f.FBgn, f.TissueID ";
	
	// All adult female tissues except whole
	static String profileQueryFA =
			"SELECT f.FBgn, f.FPKM, f.Status, f.TissueID " 
			+ "FROM GeneFPKM f, Tissue t "
			+ "WHERE f.TissueID = t.TissueID "
			+ "AND t.ProfileFA = 1 "
			+ "ORDER BY f.FBgn, f.TissueID ";
	
	// All intestinal tissues
	static String profileQueryAT =
			"SELECT f.FBgn, f.FPKM, f.Status, f.TissueID " 
			+ "FROM GeneFPKM f, Tissue t "
			+ "WHERE f.TissueID = t.TissueID "
			+ "AND t.ProfileAT = 1 "
			+ "ORDER BY f.FBgn, f.TissueID ";
	
	// FlyBase Groups
	static String groupQuery =
			"SELECT GroupName, GroupID "
			+ "FROM FBggDescription "
			+ "ORDER BY GroupName";
	
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
	
	// Returns default profile query (AL)
	public static String getProfileQueryAL()
	{
		return profileQueryAL;
	}
	
	public static String getProfileQueryAO()
	{
		return profileQueryAO;
	}
	
	public static String getProfileQueryLO()
	{
		return profileQueryLO;
	}
	
	public static String getProfileQueryMA()
	{
		return profileQueryMA;
	}
	
	public static String getProfileQueryFA()
	{
		return profileQueryFA;
	}
	
	public static String getProfileQueryAT()
	{
		return profileQueryAT;
	}
	
	public static String getGroupQuery()
	{
		return groupQuery;
	}
	
}