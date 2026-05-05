/*
	FlyPara
	Utility Servlet to list paralogues to a gene and allow FlyAtlas2 bulk query
	DPL 06.01.2024
*/	

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FlyPara extends HttpServlet
{		
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException 
	{	
		// get parameters	
		String fbgn = req.getParameter("id");					// GeneID
		fbgn = fbgn.replaceAll("[^-:a-zA-Z0-9]", "");

		String paraList = getParas(fbgn);
			// convert to array of fbgns
		String[] paras = paraList.split(",");
		
		// construct string with eols for bulk FlyAtlas 2 query
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<paras.length;i++)
		{
			sb.append(paras[i]);
			sb.append("%0D%0A");
		}			
		String bulkList = sb.toString();
	
		String url = "/FlyAtlas2/?search=bulk&geneList=" + bulkList;
		//String url = "https://motif.mvls.gla.ac.uk//FlyAtlas2/?search=bulk&geneList=" + bulkList; // IN CASE REQUIRED ON MOTIF
		
		// Make HTTP response
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
		PrintWriter out = res.getWriter();
		
		// head
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		out.println("<html>\n<head>\n");
		out.println("<title>List of Gene Paralogues</title>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">");
		out.println("<script type=\"text/javascript\" src=\"scripts/para.js\"></script>");
		out.println("<style type = \"text/css\">@import url(\"scripts/para.css\");</style>");
		out.println("<link rel=\"icon\" href=\"images/drosophila.ico\" type=\"image/x-icon\">");	
		out.println("</head>\n");
		
		//start of body and first line with button
		out.println("<body>\n<div id=\"heading\">\n<h1>Paralogues of " + fbgn + "</h1>\n</div>\n");
		out.println("<div id=\"main\">\n");
	
		// comments
		out.println("<p class=\"title\">INFORMATION ABOUT THE PARALOGUES</p>\n");
		out.println("<ul><li>The paralogues encode proteins with statistically significant similarity to that encoded by the query gene. This may encompass the length of both proteins or be restricted to a small part of them.</li>");
		out.println("<li>Pressing the ‘Go’ button, below, will open a new FlyAtlas2 window showing the tissue distribution of transcripts of each paralogue.<br>");
		out.println("<li>We do not provide facilities in FlyAtlas2 for comparing the sequences of paralogues. If you wish to do this we suggest that you copy the list below, obtain the protein sequences from FlyBase, and compare them using publicly available alignment tools.</li></ul>\n");
		out.println("<p style=\"text-align:center;\"><button onclick=\"openLinkWindow('" + url + "');\">Go</button></p>");
		// list
		out.println("<p class=\"title\">QUERY &amp; PARALOGUES</p>\n");
				
		out.println("<div class=\"list\">\n");
		
		for(int i=0; i< paras.length; i++)
		{
			out.println(paras[i] + "<br>\n");			
		}
		out.println("</div>\n");
		
		// end of body
		out.println("</div>\n</body>\n</html>");			
	}	
	
	// Make query for Paralogues and construct String
	private String getParas(String fbgn)
	{
		String [] paraList;				// array to hold FBgns retrieved from query
		paraList = new String [200];	// OK as Max No. Paralogues is 155
		int paraListSize = 0;
		
		// Make connection and query
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		ParamQuery parQ = DBQuery.getParamQuery("PARAS_FROM_FBGN");
		try 
		{
			parQ.setPrepStatement(conn);
			PreparedStatement prepStat = parQ.getPrepStatement();
			prepStat.setString(1, fbgn);
			
			ResultSet resSet = prepStat.executeQuery();
			resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
			while (resSet.next())		// moves to next row while rows remain
			{	
				String id = resSet.getString("ParaID");
				paraList[paraListSize] = id;
				paraListSize++;
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		
		StringBuilder sb  = new StringBuilder();
		if(paraListSize>0)
		{
			sb.append(fbgn + ",");			// include original
			for(int i=0;i<paraListSize;i++)
			{
				sb.append(paraList[i]);
				sb.append(",");
			}
		}
		return sb.toString();
	}	
	
/*	public String buildString(String[] inputArray)
	{
		StringBuilder sb = new StringBuilder("");
		for(int i=0; i<inputArray.length; i++)
		{
			sb.append(inputArray[i] + ",");
		}
		return sb.toString();
	}*/

}

