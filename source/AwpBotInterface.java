import org.java_websocket.WebSocket;

public interface AwpBotInterface
{
	public String getBotId();
	public WebSocket getApiWs();
	public WebSocket getEventWs();
	public static boolean sendMessage(WebSocket ws,String s)
	{
		try
		{
			ws.send(s);
			return true;
		}
		catch (Exception e)
		{
			//System.out.println(e);
			return false;
		}
	}
}