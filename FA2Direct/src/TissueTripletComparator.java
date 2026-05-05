
// TissueTripletComparator
//
// previously named FlyStagePairComparator
//
// Sorts FlyTissueTriplet on Display Position in ascending order
// 17.06.2016

import java.util.Comparator;

public class TissueTripletComparator implements Comparator<TissueTriplet>
{
	public TissueTripletComparator()
	{			
	}
	
	 public int compare(TissueTriplet triplet1, TissueTriplet triplet2)
	 {     
	    // ascending order
	    if(triplet1.getDisplayPosition() < triplet2.getDisplayPosition())
	    {
	        return -1;
	    }
	    else if(triplet1.getDisplayPosition() > triplet2.getDisplayPosition())
	    {
	        return 1;
	    }
	    else
	    {
	        return 0;    
	    }
	}
}
