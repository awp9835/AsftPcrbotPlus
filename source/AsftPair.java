import java.io.*;
public class AsftPair <T1,T2> implements Serializable
{
	private static final long serialVersionUID = 5416139455269199372L;
	public T1 First;
	public T2 Second;
	public T1 first(){return First;}
	public T2 second(){return Second;}
	public AsftPair(T1 t1,T2 t2)
	{
		First = t1;
		Second = t2;
	}
}