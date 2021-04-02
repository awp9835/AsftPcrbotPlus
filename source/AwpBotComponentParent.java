import org.json.*;

public class AwpBotComponentParent implements AwpBotComponent
{
	@Override
	public boolean save(AwpBotInterface bot)
	{
		return true;
	}
	@Override
	public boolean load(AwpBotInterface bot)
	{
		return true;
	}
	@Override
	public String getComponentName()
	{
		return "AwpBotComponentParent";
	}
	public AwpBotComponentParent(){}

	public String handle(String event, AwpBotInterface bot)
	{
		//Handle api and event here. You should not modify or override this default method.
		//Default:
		try
		{
			JSONObject obj = new JSONObject(event);	
			if(AsftOneBotApi.isApiReturn(obj))
			{
				return handleApiReturn(new AsftOneBotApi.ApiReturn(obj), bot);
			}
			else
			{
				AsftOneBotEvent evt =  AsftOneBotEvent.createFromJSONObject(obj);
				if(evt.isMessageEvent()) return handelMessageEvent((AsftOneBotEvent.MessageEvent)evt, bot);
				else if(evt.isNoticeEvent()) return handelNoticeEvent((AsftOneBotEvent.NoticeEvent)evt, bot);
				else if(evt.isRequestEvent()) return handelRequestEvent((AsftOneBotEvent.RequestEvent)evt, bot );
				else if(evt.isMetaEvent()) return handelMetaEvent(evt, bot);
				else return "continue";
			}

		}
		catch (JSONException|NullPointerException e)
		{
			//System.out.println(e);
			return "break";
		}
	}
	

	protected String handleApiReturn(AsftOneBotApi.ApiReturn apiret, AwpBotInterface bot)
	{
		//Handle Api return here.
		return "continue";
	}
	protected String handelMessageEvent(AsftOneBotEvent.MessageEvent mevt, AwpBotInterface bot)
	{
		//Handle message event here. You should not modify or override this default method.
		//Default:
		if(mevt.isPrivateEvent()) return handelPrivateMessageEvent((AsftOneBotEvent.PrivateMessageEvent)mevt, bot);
		else if(mevt.isGroupEvent()) return handelGroupMessageEvent((AsftOneBotEvent.GroupMessageEvent)mevt, bot);
		else return "continue";
	}
	protected String handelPrivateMessageEvent(AsftOneBotEvent.PrivateMessageEvent mevt, AwpBotInterface bot)
	{
		//Handle private message event here if use default handelMessageEvent method.
		return "continue";
	}
	protected String handelGroupMessageEvent(AsftOneBotEvent.GroupMessageEvent mevt, AwpBotInterface bot)
	{
		//Handle  group message event here if use default handelMessageEvent method.
		return "continue";
	}
	protected String handelNoticeEvent(AsftOneBotEvent.NoticeEvent nevt, AwpBotInterface bot)
	{
		//Handle Notice Event Here.
		return "continue";
	}
	protected String handelRequestEvent(AsftOneBotEvent.RequestEvent revt, AwpBotInterface bot)
	{
		//Handle Request Event Here.
		if(revt.isFriendAddRequestEvent())
		{

		}
		else if(revt.isGroupAddRequestEvent())
		{

		}
		else if(revt.isGroupInviteRequestEvent())
		{
		
		}
		return "continue";
	}
	protected String handelMetaEvent(AsftOneBotEvent mevt, AwpBotInterface bot)
	{
		//Handle Meta Event Here (Who Care?)
		return "continue";
	}
}