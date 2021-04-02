import org.json.*;
import java.net.*;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public abstract class AwpBotBridge implements AwpBotComponent
//public class AwpBotBridge implements AwpBotComponent
{
	protected class ReconnectWs extends Thread
	{
		private WebSocketClient ws;
		private int delaynms;
		public ReconnectWs(WebSocketClient ws,int delaynms)
		{
			this.ws = ws;
			this.delaynms = delaynms;
		}
		@Override 
		public void run()
		{
			try
			{
				Thread.sleep(delaynms);
				if(Bot.getBotId() != null && Bot.getBotId().trim().length() != 0) ws.addHeader("X-Self-ID", Bot.getBotId());
				ws.reconnect();
			}
			catch (Exception e)
			{
				//System.out.println(e);
			}
		}
	}
	protected class InnerWebSocketClient extends WebSocketClient
	{
		public InnerWebSocketClient(String uri, String role) throws URISyntaxException
		{
			super(new URI(uri));
			String accesstoken = AccessToken;
			if(accesstoken != null && accesstoken.trim().length() != 0)
			{
				this.addHeader("Authorization", "Bearer " + accesstoken.trim());
			}
			this.addHeader("X-Client-Role", role);
			if(Bot.getBotId() != null && Bot.getBotId().trim().length() != 0) this.addHeader("X-Self-ID", Bot.getBotId());
		}
		@Override
		public void onOpen(ServerHandshake handshakedata) 
		{
			String role = "Unknown";
			if(this == ApiWs) role = "API";
			else if(this == EventWs) role = "Event";
			else if(this == UniversalWs) role = "Universal";
			System.out.println(this.getRemoteSocketAddress() + " connected. (" + role +")");
		}
		@Override
		public void onClose(int code, String reason, boolean remote) 
		{
			String role = "Unknown";
			if(this == ApiWs) role = "API";
			else if(this == EventWs) role = "Event";
			else if(this == UniversalWs) role = "Universal";
			System.out.println(this.getRemoteSocketAddress() + " closed. (" + role +")");
			if(this == EventWs || this == ApiWs || this == UniversalWs) new ReconnectWs(this,5000).start();
		}
		@Override
		public void onMessage(String message) 
		{
			System.out.println(message);
			AwpBotInterface bot = Bot;
			try 
			{
				if(this == ApiWs)
				{
					AwpBotInterface.sendMessage(bot.getApiWs(),message);			
				}
				else if(this == EventWs)
				{
					AwpBotInterface.sendMessage(bot.getEventWs(),message);	
				}
				else if(this == UniversalWs)
				{
					if(bot.getEventWs() == bot.getApiWs()) AwpBotInterface.sendMessage(bot.getApiWs(),message);		
					else
					{
						JSONObject obj = new JSONObject(message);	
						if(AsftOneBotApi.isApiReturn(obj))
						{
							AwpBotInterface.sendMessage(bot.getApiWs(),message);	
						}
						else
						{
							AwpBotInterface.sendMessage(bot.getEventWs(),message);
						}
					}
				}
				else 
				{
					this.close();
				}
			}
			catch (Exception e)
			{
				//System.out.println(e);
			}
		}
		@Override
		public void onError(Exception e) 
		{

		}
	}

	protected String EventWsUri;
	protected String ApiWsUri;
	protected String UniversalWsUri;
	protected String AccessToken;

	protected volatile InnerWebSocketClient EventWs;
	protected volatile InnerWebSocketClient ApiWs;
	protected volatile InnerWebSocketClient UniversalWs;
	protected AwpBotInterface Bot;

	public abstract void config();
	public void stop()
	{
		WebSocketClient temp;
		if(UniversalWs != null) 
		{
			temp = UniversalWs;
			UniversalWs = null;
			temp.close();
		}
		if(ApiWs != null) 
		{
			temp = ApiWs;
			ApiWs = null;
			temp.close();
		}
		if(EventWs != null) 
		{
			temp = EventWs;
			EventWs = null;
			temp.close();
		}
	}

	@Override
	public boolean save(AwpBotInterface bot)
	{
		return true;
	}
	@Override
	public boolean load(AwpBotInterface bot)
	{
		Bot = bot; 
		config();
		try
		{
			if(UniversalWsUri != null)
			{
				stop();
				UniversalWs = new InnerWebSocketClient(UniversalWsUri,"Universal");
				UniversalWs.connect();
				return true;
			}
			else 
			{
				stop();
				EventWs = new InnerWebSocketClient(EventWsUri,"Event");
				ApiWs = new InnerWebSocketClient(ApiWsUri,"API");
				EventWs.connect();
				ApiWs.connect();
				return true;
			}
		}
		catch (Exception e)
		{
			//System.out.println(e);
			return false;
		}
	}
	@Override
	public String getComponentName()
	{
		if(UniversalWs != null)
		{
			return "AwpBotBridge: " + "Link to Universal WebSocket " + UniversalWsUri +" ";
		}
		else 
		{
			if(ApiWs == null || EventWs == null) return "AwpBotBridge: not configured correctly";
			else return "AwpBotBridge: " + "Link to Event WebSocket " + EventWsUri + " and API WebSocket " + ApiWsUri +" ";
		}
	}
	public AwpBotBridge(){}

	protected AwpBotBridge setEventWsUri(String eventuri)  //Before 
	{
		UniversalWsUri = null;
		EventWsUri = eventuri;
		return this;
	}
	protected AwpBotBridge setApiWsUri(String apiuri)
	{
		UniversalWsUri = null;
		ApiWsUri = apiuri;
		return this;
	}
	protected AwpBotBridge setUniversalWsUri(String uuri)
	{
		EventWsUri = ApiWsUri = null;
		UniversalWsUri = uuri;
		return this;
	}
	protected AwpBotBridge setAccessToken(String token)
	{
		AccessToken = token;
		return this;
	}
	@Override
	public String handle(String event, AwpBotInterface bot)
	{
		//Handle api and event here.
		//This module only deliver websocket messages.
		if(Bot != bot) Bot = bot;
		if(this.UniversalWs != null)
		{
			AwpBotInterface.sendMessage(this.UniversalWs,event);
			return "continue";
		}
		else
		{
			try
			{
				JSONObject obj = new JSONObject(event);	
				if(AsftOneBotApi.isApiReturn(obj))
				{
					AwpBotInterface.sendMessage(this.ApiWs,event);
					return "continue";
				}
				else
				{
					AwpBotInterface.sendMessage(this.EventWs,event);
					return "continue";
				}
			}
			catch (JSONException e)
			{
				//System.out.println(e);
				return "break";
			}
			catch (Exception e)
			{
				//System.out.println(e);
				return "continue";
			}
		}
	}
}