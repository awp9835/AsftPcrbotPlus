public class AsftInt
{
	public static int parseInt(String s)
	{
		int a;
		try
		{
			a = Integer.parseInt(s);
		}
		catch(NumberFormatException e)
		{
			a = 0;
		}		
		return a;
	}
}