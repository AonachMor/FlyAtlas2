 /*
DBQuery
Class with static method(s) to provide access to SQL query strings
Derived from earlier BeetleAtlas version
Last Update: 06.01.2024
*/

public class DBQuery
{			    
	// PARALOGUE QUERY //
    // query24 get Paralogue(s) from FBgn
    final static String name24 = "PARAS_FROM_FBGN";
    final static String query24 = "SELECT DISTINCT ParaID FROM Paralogue "
    		+ "WHERE (FBgn = ?) ";    
 
     
	// creates an array of all ParamQuerys	
	static ParamQuery pqList [] =
	{		
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
	
}