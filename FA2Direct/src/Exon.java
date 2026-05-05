// Simple class to model exon by start and end coordinates
// Chromosome and direction not included as always belongs to transcript with gene FBgn which have that info

public class Exon
{
	private int start;
	private int end;
	
	public Exon(int start, int end)
	{
		this.start = start;
		this.end = end;
	}
	
	public int getStart()
	{
		return start;
	}
	
	public int getEnd()
	{
		return end;
	}
}
