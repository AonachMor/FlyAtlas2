// Simple class to model Transcript
// Also has method to output HTML using data input
// DPL 03.07.2016
// Revised for new DB structure 22.01.2025 and 02.03.2025

public class Transcript
{
	private String fbtr;
	private String fbgn;
	private String name;
	
	//public Transcript(String fbtr, String fbgn, String name, boolean hasFPKM)
	public Transcript(String fbtr, String fbgn, String name)
	{
		this.fbtr = fbtr;
		this.fbgn = fbgn;
		this.name = name;
	}
	
	public String getFBtr()
	{
		return fbtr;
	}
	
	public String getFBgn()
	{
		return fbgn;
	}
	
	public String getName()
	{
		return name;
	}
	
	// Returns part of name after last "-" (RA, RAA etc)
	public String getNameSuffix()
	{
		if(name != null)
		{
			return name.substring(name.lastIndexOf("-") + 1, name.length());
		}
		else
		{
			return "";
		}
	}
	
	public String toString()
	{
		return fbtr + "\t" + fbgn  + "\t" + name ;
	}
}
