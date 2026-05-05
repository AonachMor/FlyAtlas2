// Searches DB for gene info and experimental results
// DPL 23.09.2022, 23.01.2025

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GeneSearch 
{		
	GeneExpression geneExpn;				// Expression object holding results for gene and transcript from search
	MirExpression mirExpn;					// Expression object holding results for gene and transcript from search
	Gene gene;								// Gene object holding info on gene and transcripts
	TissueCatalogue tCat; 
	final String PRE_MIR = "pre-miRNA";		// to check for this biotype which needs different handling
	boolean mir = false;					// gene is microRNA
	
	// constructor takes search term for gene query and type of identifier (ID, symbol etc.)
	public GeneSearch(String flybaseID, TissueCatalogue tCat)
	{			
		this.tCat = tCat;
		
		// Make connection
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		makeGeneInfoQuery(flybaseID, "INFO_FROM_FBGN", conn);
	
		// Check that gene has been found and if so make other queries
		if(gene != null)
		{
			String fbgn = gene.getFBgn();
			makeTranscriptInfoQuery(fbgn, conn);

			if(gene.getBioType().equals(PRE_MIR))
			{
				mir = true;
				mirExpn = new MirExpression(fbgn);		// instantiate object to hold MirDataset and MirTranscriptDataset		
				makeMirDataQuery(fbgn, conn);	
				makeMirTranscriptDataQuery(fbgn, conn);
			}
			else
			{
				geneExpn = new GeneExpression(fbgn);		// instantiate object to hold GeneDataset and TranscriptDataset		
				makeGeneDataQuery(fbgn, conn);	
				makeTranscriptDataQuery(fbgn, conn);
			}
		}
		
		// close connection
		if(conn != null)
		{
			try { conn.close();}
			catch(Exception e){System.out.println("Can't close.");}
		}
			
	}
		
	// Query for gene info for Gene object
	private void makeGeneInfoQuery(String flybaseID, String geneinfoQuery, Connection conn)
	{
		ParamQuery parIQ = DBQuery.getParamQuery(geneinfoQuery);
		try 
		{
			parIQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	
		
		try 
		{
			PreparedStatement prepStat = parIQ.getPrepStatement();
			prepStat.setString(1, flybaseID);
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())	// move to single tuple
			{
				String fbgn = resSet.getString("FBgn");
				String cgNum = resSet.getString("CGNum");
				String symbol = resSet.getString("Symbol");
				String romanSymbol = resSet.getString("romanSymbol");
				String name = resSet.getString("Name");	
				String romanName = resSet.getString("romanName");	
				String locus = resSet.getString("Locus");			
				String biotype = resSet.getString("BioType");
				int neuropeptideID = resSet.getInt("NeuropeptideID");
				boolean para = false;
					String paraString = resSet.getString("Para");			
					if(paraString.equals("Yes")) {para = true;}
				gene = new Gene(fbgn, cgNum, symbol, romanSymbol, name, romanName, locus, biotype, neuropeptideID, para);		
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
		
		ParamQuery parTrQ = DBQuery.getParamQuery(transQuery);
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
		
		// Now find transcript info for each FBtrm, create a Transcript object, and add to Transcript array in Gene object
		String transcriptInfoQuery = "INFO_FROM_FBTR";
		
		for(int i=0; i<nameListSize; i++)
		{
			ParamQuery parTIQ = DBQuery.getParamQuery(transcriptInfoQuery);
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
					//create a Transcript object, and add to Transcript array in Gene object
					Transcript trans = new Transcript(fbtr, fbgn, name);
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
		GeneTissueDataSet gDataset = new GeneTissueDataSet(fbgn, tCat);
		
		String geneDataQuery = "GENE_DATA_FROM_FBGN";		
		ParamQuery parGDQ = DBQuery.getParamQuery(geneDataQuery);
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
					
					double [] repFPKMlist;
					if(resSet.wasNull())
					{
						repFPKMlist = new double[2];
						repFPKMlist[0] = replicate1;
						repFPKMlist[1] = replicate2;
					}
					else
					{
						repFPKMlist = new double[3];
						repFPKMlist[0] = replicate1;
						repFPKMlist[1] = replicate2;
						repFPKMlist[2] = replicate3;
					}				
						
					double sd = resSet.getDouble("SD");
					String status = resSet.getString("Status");
					
					// Construct GeneTissuedata object from query
					GeneTissueData geneData = new GeneTissueData(fbgn, tissueID, fpkm, repFPKMlist, sd, status);
					
					// Add to GeneDataset
					gDataset.add(geneData);
				}
				// Having completed GeneDataset now calculate enrichment
				gDataset.calculateEnrichments();
				// Add GeneDataset to Expression object
				geneExpn.setGeneData(gDataset);
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
	}
	
	// Query for Experimental data for a microRNA
	private void makeMirDataQuery(String fbgn, Connection conn)
	{
		MirTissueDataSet mDataset = new MirTissueDataSet(fbgn, tCat);
		
		String geneDataQuery = "MIR_DATA_FROM_FBGN";	
		ParamQuery parGDQ = DBQuery.getParamQuery(geneDataQuery);
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
					int tpm = resSet.getInt("TPM");
					
					int replicate1 = resSet.getInt("Replicate1");	
					int replicate2 = resSet.getInt("Replicate2");	
					int replicate3 = resSet.getInt("Replicate3");				
					int [] repTPMlist;
					if(resSet.wasNull())
					{
						repTPMlist = new int[2];
						repTPMlist[0] = replicate1;
						repTPMlist[1] = replicate2;
					}
					else
					{
						repTPMlist = new int[3];
						repTPMlist[0] = replicate1;
						repTPMlist[1] = replicate2;
						repTPMlist[2] = replicate3;
					}				
						
					int sd = resSet.getInt("SD");	
					// Construct GeneTissuedata object from query
					MirTissueData mirData = new MirTissueData(fbgn, tissueID, tpm, repTPMlist, sd);
					// Add to GeneDataset
					mDataset.add(mirData);
				}
				// Having completed GeneDataset now calculate enrichment
				mDataset.calculateEnrichments();
				// Add GeneDataset to Expression object
				mirExpn.setMirData(mDataset);
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
			TranscriptTissueDataSet tDataset = new TranscriptTissueDataSet(fbgn, fbtr);
			
			ParamQuery parTDQ = DBQuery.getParamQuery(transcriptDataQuery);
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
						double sd = resSet.getDouble("SD");	
						
						// Construct TranscriptTissuedata object from query
						TranscriptTissueData transcriptData = new TranscriptTissueData(fbgn, fbtr, tissueID, fpkm, sd);					
						// Add to TranscriptDataset
						tDataset.add(transcriptData);
					}
					geneExpn.addTranscriptDataset(tDataset);
				}
			}
			catch (SQLException e)
			{
				System.out.println("SQL Exception: " + e.toString());
			}	
		}
	}
	
	private void makeMirTranscriptDataQuery(String fbgn, Connection conn)
	{
		String mirTranscriptDataQuery = "MIR_TRANSCRIPT_DATA_FROM_FBTR";
		
		// go through set of transcripts belonging to the gene
		for(int i=0; i<gene.getTranscriptListSize(); i++)
		{		
			String fbtr = gene.getTranscript(i).getFBtr();
			MirTranscriptTissueDataSet tDataset = new MirTranscriptTissueDataSet(fbgn, fbtr);
			
			ParamQuery parTDQ = DBQuery.getParamQuery(mirTranscriptDataQuery);
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
						int tpm = resSet.getInt("TPM");
						int sd = resSet.getInt("SD");	
						
						// Construct TranscriptTissuedata object from query
						MirTranscriptTissueData mirTranscriptData = new MirTranscriptTissueData(fbgn, fbtr, tissueID, tpm, sd);					
						// Add to TranscriptDataset
						tDataset.add(mirTranscriptData);
					}
					mirExpn.addMirTranscriptDataset(tDataset);
				}
			}
			catch (SQLException e)
			{
				System.out.println("SQL Exception: " + e.toString());
			}	
		}
	}

	// 'Get' methods to give program access to query results
	
	public Gene getGene()
	{
		return gene;
	}
	
	public GeneExpression getExpression()
	{
		return geneExpn;
	}
	
	public MirExpression getMirExpression()
	{
		return mirExpn;
	}
	
	public boolean isMir()
	{
		return mir;
	}

}