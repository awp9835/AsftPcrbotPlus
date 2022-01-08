public class UserYobotBridge1 extends AwpBotBridge
{
	@Override
	public void config() 
	{
		this.setUniversalWsUri("ws://localhost:9222/ws");
		//this.setAccessToken("");
	}	
}