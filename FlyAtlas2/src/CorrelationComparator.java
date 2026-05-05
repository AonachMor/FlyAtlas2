// Sorts GenExCorrelation on r correlation stat for Profile searches
// 27.08.2012
// Revised for BeetleAtlas 24.02.2021

import java.util.Comparator;

public class CorrelationComparator implements Comparator<ProfileTissueData>
{
	public CorrelationComparator()
	{			
	}
	
	 public int compare(ProfileTissueData correlation1, ProfileTissueData correlation2)
	 {     	   
	    //reverse order...
	    if(correlation1.getRstat() > correlation2.getRstat())
	    {
	        return -1;
	    }
	    else if(correlation1.getRstat() < correlation2.getRstat())
	    {
	        return 1;
	    }
	    else
	    {
	        return 0;    
	    }
	}
}
