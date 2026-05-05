
// Sorts FlyTissue on Display Position in ascending order
// 10.12.2015

import java.util.Comparator;

public class FlyStagePairComparator implements Comparator<FlyStagePair>
{
	public FlyStagePairComparator()
	{			
	}
	
	 public int compare(FlyStagePair pair1, FlyStagePair pair2)
	 {     
	    // ascending order
	    if(pair1.getDisplayPosition() < pair2.getDisplayPosition())
	    {
	        return -1;
	    }
	    else if(pair1.getDisplayPosition() > pair2.getDisplayPosition())
	    {
	        return 1;
	    }
	    else
	    {
	        return 0;    
	    }
	}
}
