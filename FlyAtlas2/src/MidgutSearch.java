// Searches DB for midgutGene info and experimental results
// DPL 08.08.2022

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MidgutSearch 
{		
	MidgutExpression expr;		// MidgutExpression object holding results for gene and transcript from search
	MidgutGene gene;			// MidgutGene object holding info on gene and transcripts
	MidgutCatalogue mgCat; 
	boolean isMir = false;
	
	// constructor takes search term for gene query and type of identifier (ID, symbol etc.)
	public MidgutSearch(String searchTerm, String idType, MidgutCatalogue mgCat)
	{			
		this.mgCat = mgCat;
		// Set names of two types of query on basis of idType
		String geneinfoQuery = "";
		if(idType.equals("fbgn"))
		{
			geneinfoQuery = "INFO_FROM_FBGN";
		}
		else if(idType.equals("fbtr"))
		{
			// Find corresponding FBgn and set that as search term
			searchTerm = getFBgnFromFBtr(searchTerm);
			geneinfoQuery = "INFO_FROM_FBGN";
		}
		else if(idType.equals("cgnum"))
		{
			geneinfoQuery = "INFO_FROM_CGNUM";			
		}
		else if(idType.equals("symbol"))
		{
			geneinfoQuery = "INFO_FROM_SYMBOL";				
		}
		else if(idType.equals("name"))
		{
			geneinfoQuery = "INFO_FROM_NAME";			
		}

		// check valid search term and if so make queries
		if(!geneinfoQuery.equals(""))
		{
			// Make connection
			ConnectMG cnt = new ConnectMG();
			Connection conn = cnt.getConnection();
			// Make first query
			makeGeneInfoQuery(searchTerm, geneinfoQuery, conn);
	
			// Check that gene has been found and if so make other queries
			if(gene != null)
			{
				if(!gene.getBioType().equals("pre miRNA"))
				{
					String fbgn = gene.getFBgn();
					makeTranscriptInfoQuery(fbgn, conn);
	
					expr = new MidgutExpression(fbgn);		// instantiate object to hold GeneDatasetMG and TranscriptTissueDataSetMG		
					makeGeneDataQuery(fbgn, conn);
					makeTranscriptDataQuery(fbgn, conn);
					
					makeFBgnMaskingQuery(fbgn, conn);
					makeFBgnMaskedQuery(fbgn, conn);
				}
				else
				{
					isMir = true;
				}
			}
			else{System.out.println("Gene is null");}
			
			// close connection
			if(conn != null)
			{
				try { conn.close();}
				catch(Exception e){System.out.println("Can't close.");}
			}
		}
		else{System.out.println("Empty Query");}
	}
	
	// Run query to retrieve FBgn if idType is FBtr
	private String getFBgnFromFBtr(String searchTerm)
	{
		// Make connection
		ConnectMG cnt = new ConnectMG();
		Connection conn = cnt.getConnection();
		
		ParamQuery parQy = DBQuery.getParamQuery("FBGN_FROM_FBTR");
		try 
		{
			parQy.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	
		
		try 
		{
			PreparedStatement prepStat = parQy.getPrepStatement();
			prepStat.setString(1, searchTerm);
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())	// move to single tuple
			{
				searchTerm = resSet.getString("FBgn");		
			}						
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		
		// close connection
		if(conn != null)
		{
			try { conn.close();}
			catch(Exception e){System.out.println("Can't close.");}
		}
		return searchTerm;	
	}
	
	// Query for gene info for MidgutGene object
	private void makeGeneInfoQuery(String searchTerm, String geneinfoQuery, Connection conn)
	{
		ParamQuery parIQ = DBQueryMG.getParamQuery(geneinfoQuery);
		try 
		{
			parIQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	
		
		try 
		{
			PreparedStatement prepStat = parIQ.getPrepStatement();
			prepStat.setString(1, searchTerm);
			if(geneinfoQuery == "INFO_FROM_SYMBOL" || geneinfoQuery == "INFO_FROM_NAME")
			{
				prepStat.setString(2, searchTerm);
			}
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())	// move to single tuple
			{
				String fbgn = resSet.getString("FBgn");
				String cgNum = resSet.getString("CGNum");
				String symbol = resSet.getString("Symbol");
				String name = resSet.getString("Name");	
				String locus = resSet.getString("Locus");			
				String biotype = resSet.getString("BioType");
				gene = new MidgutGene(fbgn, cgNum, symbol, name, locus, biotype);			
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
	}
		
	// Query for transcripts and then transcript info
	private void makeTranscriptInfoQuery(String fbgn, Connection conn)
	{
		// First find and order transcripts
		String transQuery = "FBTRS_FROM_FBGN";
		
		String [] fbtrNameList;						// array to hold names of FBtr
		final int NAME_LENGTH = 10;
		int nameListSize = 0;
		fbtrNameList = new String [NAME_LENGTH];
		
		ParamQuery parTrQ = DBQueryMG.getParamQuery(transQuery);
		try 
		{
			parTrQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	

		try 
		{
			PreparedStatement prepStat = parTrQ.getPrepStatement();
			prepStat.setString(1, fbgn);
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					String fbtr = resSet.getString("FBtr");
					//check for occupancy of array and expand as required
					if(nameListSize>fbtrNameList.length - 1)
					{
						String[] newList = new String[nameListSize*2];
						System.arraycopy(fbtrNameList, 0, newList, 0, nameListSize);
						fbtrNameList = newList;
					}
					fbtrNameList[nameListSize] = fbtr;
					nameListSize++;
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		
		// Now find transcript info for each FBtrm, create a TranscriptMG object, and add to TranscriptMG array in MidgutGene object
		String transcriptInfoQuery = "INFO_FROM_FBTR";
		
		for(int i=0; i<nameListSize; i++)
		{
			ParamQuery parTIQ = DBQueryMG.getParamQuery(transcriptInfoQuery);
			try 
			{
				parTIQ.setPrepStatement(conn);
			} 
			catch (SQLException e) 
			{System.out.println(e.toString());}	
			
			try 
			{
				String fbtr = fbtrNameList[i];
				PreparedStatement prepStat = parTIQ.getPrepStatement();
				prepStat.setString(1, fbtr);
				ResultSet resSet = prepStat.executeQuery();
				if(resSet.first())	// move to single tuple
				{
					String name = resSet.getString("TranscriptName");
					String strandString = resSet.getString("Strand");	// MySQL 25.4.4.3 indicates ENUM -> String OK.
					char strand = strandString.charAt(0);					
					int exonCount = resSet.getInt("ExonCount");
					String exonStarts = resSet.getString("ExonStarts");			
					String exonEnds = resSet.getString("ExonEnds");
					//create a TranscriptMG object, and add to TranscriptMG array in MidgutGene object
					TranscriptMG trans = new TranscriptMG(fbtr, fbgn, name, strand, exonCount, exonStarts, exonEnds);
					gene.addTranscript(trans);
				}
			}
			catch (SQLException e)
			{
				System.out.println("SQL Exception: " + e.toString());
			}			
		}
	}
	
	// Query for Experimental data for a gene
	private void makeGeneDataQuery(String fbgn, Connection conn)
	{
		GeneDatasetMG gDataset = new GeneDatasetMG(fbgn);
		
		String geneDataQuery = "GENE_DATA_FROM_FBGN";		
		ParamQuery parGDQ = DBQueryMG.getParamQuery(geneDataQuery);
		try 
		{
			parGDQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	
		
		try 
		{
			PreparedStatement prepStat = parGDQ.getPrepStatement();
			prepStat.setString(1, fbgn);
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{				
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{
					int tissueID = resSet.getInt("TissueID");
					double fpkm = resSet.getDouble("FPKM");
					
					double replicate1 = resSet.getDouble("Replicate1");	
					double replicate2 = resSet.getDouble("Replicate2");	
					double replicate3 = resSet.getDouble("Replicate3");				
					double [] repFPKMlist = {replicate1, replicate2, replicate3};				
					
					String status = resSet.getString("Status");	
					double sd = resSet.getDouble("SD");	
					// Construct GeneTissuedataMG object from query
					GeneTissuedataMG geneData = new GeneTissuedataMG(fbgn, tissueID, fpkm, repFPKMlist, status, sd);
					// Add to GeneDatasetMG
					gDataset.add(geneData);
				}
				// Add GeneDatasetMG to MidgutExpression object
				expr.setGeneData(gDataset);
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
	}
	
	private void makeTranscriptDataQuery(String fbgn, Connection conn)
	{
		String transcriptDataQuery = "TRANSCRIPT_DATA_FROM_FBTR";
		
		// go through set of transcripts belonging to the gene
		for(int i=0; i<gene.getTranscriptListSize(); i++)
		{		
			String fbtr = gene.getTranscript(i).getFBtr();
			TranscriptTissueDataSetMG tDataset = new TranscriptTissueDataSetMG(fbgn, fbtr);
			
			ParamQuery parTDQ = DBQueryMG.getParamQuery(transcriptDataQuery);
			try 
			{
				parTDQ.setPrepStatement(conn);
			} 
			catch (SQLException e) 
			{System.out.println(e.toString());}	

			try 
			{
				PreparedStatement prepStat = parTDQ.getPrepStatement();
				prepStat.setString(1, fbtr);
				ResultSet resSet = prepStat.executeQuery();
				if(resSet.first())
				{
					resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
					while (resSet.next())		// moves to next row while rows remain
					{	
						int tissueID = resSet.getInt("TissueID"); 
						double fpkm = resSet.getDouble("FPKM");
						String status = resSet.getString("Status");	
						double sd = resSet.getDouble("SD");	
						
						// Construct TranscriptTissueDataMG object from query
						TranscriptTissueDataMG transcriptData = new TranscriptTissueDataMG(fbgn, fbtr, tissueID, fpkm, status, sd);					
						// Add to TranscriptTissueDataSetMG
						tDataset.add(transcriptData);
					}
					expr.addTranscriptDataset(tDataset);
				}
			}
			catch (SQLException e)
			{
				System.out.println("SQL Exception: " + e.toString());
			}	
		}
	}
	
	// If the FPKM data for this gene is masked, gets identity of any masking genes and sets this
		private void makeFBgnMaskingQuery(String fbgn, Connection conn)
		{
			String maskingQuery = "MASKING_FROM_MASKED";
			
			String [] maskingList;						// array to hold names of any masking genes
			final int LENGTH = 5;
			int maskingListSize = 0;
			maskingList = new String [LENGTH];
			
			ParamQuery parMgQ = DBQueryMG.getParamQuery(maskingQuery);
			try 
			{
				parMgQ.setPrepStatement(conn);
			} 
			catch (SQLException e) 
			{System.out.println(e.toString());}	

			try 
			{
				PreparedStatement prepStat = parMgQ.getPrepStatement();
				prepStat.setString(1, fbgn);
				ResultSet resSet = prepStat.executeQuery();
				if(resSet.first())
				{
					resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
					while (resSet.next())		// moves to next row while rows remain
					{
						String maskingGene = resSet.getString("MaskingFBgn");
						maskingList[maskingListSize] = maskingGene;
						maskingListSize++;
					}
				}
				if(maskingListSize > 0)
				{
					gene.setMasking(maskingList, maskingListSize);
				}
			}
			catch (SQLException e)
			{
				System.out.println("SQL Exception: " + e.toString());
			}
		}
		
		// If the FPKM data for this gene masks another, gets identity of any masked genes and sets this
		private void makeFBgnMaskedQuery(String fbgn, Connection conn)
		{
			String maskedQuery = "MASKED_FROM_MASKING";
			
			String [] maskedList;						// array to hold names of any masked genes
			final int LENGTH = 5;
			int maskedListSize = 0;
			maskedList = new String [LENGTH];
			
			ParamQuery parMdQ = DBQueryMG.getParamQuery(maskedQuery);
			try 
			{
				parMdQ.setPrepStatement(conn);
			} 
			catch (SQLException e) 
			{System.out.println(e.toString());}	

			try 
			{
				PreparedStatement prepStat = parMdQ.getPrepStatement();
				prepStat.setString(1, fbgn);
				ResultSet resSet = prepStat.executeQuery();
				if(resSet.first())
				{
					resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
					while (resSet.next())		// moves to next row while rows remain
					{
						String maskingGene = resSet.getString("MaskedFBgn");
						maskedList[maskedListSize] = maskingGene;
						maskedListSize++;
					}
				}
				if(maskedListSize > 0)
				{
					gene.setMasked(maskedList, maskedListSize);
				}
			}
			catch (SQLException e)
			{
				System.out.println("SQL Exception: " + e.toString());
			}
		}
			
	// 'Get' methods to give program access to query results
	
	public MidgutGene getGene()
	{
		return gene;
	}
	
	public MidgutExpression getExpression()
	{
		return expr;
	}
	
	public boolean isMir()
	{
		return isMir;
	}

}