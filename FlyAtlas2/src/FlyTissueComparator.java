// Sorts FlyTissue on Display Position in ascending order
// 10.12.2015

import java.util.Comparator;

public class FlyTissueComparator implements Comparator<FlyTissue>
{
	public FlyTissueComparator()
	{			
	}
	
	 public int compare(FlyTissue tissue1, FlyTissue tissue2)
	 {     	   
	    if(tissue1.getDisplayPosition() < tissue2.getDisplayPosition())
	    {
	        return -1;
	    }
	    else if(tissue1.getDisplayPosition() > tissue2.getDisplayPosition())
	    {
	        return 1;
	    }
	    else
	    {
	        return 0;    
	    }
	}
}
