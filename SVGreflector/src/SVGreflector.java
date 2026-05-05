/*
 * Servlet for returning HTML5 SVG content as downloadable SVG file
 * David P. Leader 01.02.2016
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class SVGreflector extends HttpServlet 
{

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String svgText = (String) request.getParameter("svgText").trim();
		String graphName = (String) request.getParameter("graphName");
		
		// Handle case where not possible to send name for graph
		if(graphName == null || graphName.equals("undefined"))
		{
			graphName = "geneGraph";
		}
		graphName = graphName.trim();		// remove leading or trailing space
		
		response.setContentType("image/svg+xml");
		response.addHeader("Content-Disposition", "attachment; filename=\"" + graphName + ".svg\"");	

		PrintWriter out = response.getWriter();
		out.println(svgText);
	}
 	
}
