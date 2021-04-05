import java.util.*;
import org.json.*;

public class AsftPcrPvpComponent implements AwpBotComponent 
{
	GroupPcrPvpManager InnerGroupPcrPvpManager;
	protected long parseLong(String s)
	{
		long a;
		try
		{
			a = Long.parseLong(s);
		}
		catch(NumberFormatException e)
		{
			a = 0L;
		}		
		return a;
	}
	@Override
	public boolean save(AwpBotInterface bot) 
	{
		if(InnerGroupPcrPvpManager == null) return false;
		return InnerGroupPcrPvpManager.save();
	}

	@Override
	public boolean load(AwpBotInterface bot) 
	{
		if(InnerGroupPcrPvpManager.Bot == null) InnerGroupPcrPvpManager = new GroupPcrPvpManager(bot);
		return InnerGroupPcrPvpManager.load();
	}

	@Override
	public String getComponentName() 
	{
		return "AsftPcrPvpComponent";
	}

	public AsftPcrPvpComponent() 
	{
		InnerGroupPcrPvpManager = new GroupPcrPvpManager(null);
	}

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

		//私聊操作
		if(mevt.raw_message == null) return "continue";
		String message = mevt.raw_message.trim();
		String reply = null;
		boolean continueflag = true;
		if(message.equals("保存") || message.startsWith("保存击剑"))
		{
			if(GroupPcrPvpManager.isOwner(mevt.getUserId())) 
			{
				reply = "灵梦PVP插件：" + (InnerGroupPcrPvpManager.save() ? "保存成功" : "保存失败");
			}
			else if(message.startsWith("保存击剑"))
			{
				reply = "权限不足";
			}
		}
		else if(message.equals("主人"))
		{
			if(GroupPcrPvpManager.isOwner(mevt.getUserId()))
			{
				reply = "保存 保存击剑状态 注销所有击剑群"
				+ "注册击剑群 注销击剑群 设置PVP查询周期 状态";
			}
		}
		else if(message.equals("状态"))
		{
			reply = "已注册的击剑群：" + InnerGroupPcrPvpManager.getAllGroups();
		}
		else if(message.startsWith("设置PVP查询周期"))
		{
			if(GroupPcrPvpManager.isOwner(mevt.getUserId()))
			{
				int minute = AsftInt.parseInt(message.replaceFirst("设置PVP查询周期", "").trim());
				if(minute <= 0) minute = 1;
				InnerGroupPcrPvpManager.setInterval(minute);
				reply = "已设置查询周期为"+ minute + "分钟";
			}
		}
		else if(message.equals("注销所有击剑群"))
		{
			reply = InnerGroupPcrPvpManager.purgeAll(mevt.getUserId())?
				("已" + message)  : "权限不足";
		}
		else if(message.startsWith("拉黑用户"))
		{
			InnerGroupPcrPvpManager.banUser(mevt.getUserId(), 
				parseLong(message.replaceFirst("拉黑用户", "").trim()));
		}
		else if(message.startsWith("解禁用户"))
		{
			InnerGroupPcrPvpManager.banUser(mevt.getUserId(), 
				parseLong(message.replace("解禁用户", "").trim()),false);
		}
		else if(message.startsWith("拉黑群"))
		{
			InnerGroupPcrPvpManager.banGroup(mevt.getUserId(), 
				parseLong(message.replaceFirst("拉黑群", "").trim()));
		}
		else if(message.startsWith("解禁群"))
		{
			InnerGroupPcrPvpManager.banGroup(mevt.getUserId(), 
				parseLong(message.replaceFirst("解禁群", "").trim()),false);
		}
		else if(message.startsWith("注销击剑群"))
		{
			reply = InnerGroupPcrPvpManager.purgeGroup(mevt.getUserId(), 
				parseLong(message.replaceFirst("注销击剑群", "").trim()))?
				("已" + message) : "权限不足或参数错误";
		}
		else if(message.startsWith("注册击剑群"))
		{
			if(GroupPcrPvpManager.isOwner(mevt.getUserId()))
			{
				reply = InnerGroupPcrPvpManager.registerGroup( 
					parseLong(message.replaceFirst("注册击剑群", "").trim()))?
					("已" + message) : "群已经注册或被拉黑";
			}
			else reply = "权限不足或参数错误";
		}
		else if(message.toLowerCase(Locale.ROOT).equals("version"))
		{
			reply = "灵梦PVP插件V1.1\n本插件不可跨群使用，可发送 \"PVP帮助\" 获取使用方法";
		}
		else if(message.equals("反射弧"))
		{
			reply = "当前灵梦PVP插件的反射弧至少为" + InnerGroupPcrPvpManager.Fanshehu + "秒。";
		}
		else if(message.equals("帮助")||message.toLowerCase(Locale.ROOT).equals("pvp帮助"))
		{
			reply = "灵梦PVP插件V1.1\n指令格式(空格不可省略，名称可省略，[]表示选择其一)：\n"+
			"[查询 查询昨日][公会 档线] 名称\n"+
			"（例如：查询昨日公会 灵梦）\n"+
			"[双场 战斗 公主][排名 下降 上升]追踪 数字ID 名称\n"+
			"（例如：战斗下降追踪 1145141919810 野兽先辈）\n"+
			"解除追踪 数字ID\n"+
			"解除全部追踪\n"+
			"[开启 关闭]at\n"+
			"注：排名追踪只能在已注册的击剑群内使用，"+
			"要注册击剑群，请联系主人。\n";
		}
		if(reply != null) 
		{
			AsftOneBotApi.SendPrivateMessage_text(mevt.getUserId(), reply).send(bot);
		}
		return continueflag?"continue":"break";
	}



	//分界线-----------



	protected String handelGroupMessageEvent(AsftOneBotEvent.GroupMessageEvent mevt, AwpBotInterface bot)
	{
		//Handle  group message event here if use default handelMessageEvent method.

		//群聊操作
		if(mevt.raw_message == null|| mevt.message == null) return "continue";
		String message = mevt.raw_message.trim();
		String reply = null;
		boolean continueflag = true;
		int shishanflag = 0;
		if(message.toLowerCase(Locale.ROOT).equals("version"))
		{
			reply = "灵梦PVP插件V1.1\n本插件不可跨群使用，可发送 \"PVP帮助\" 获取使用方法。\n";
			shishanflag = 0x100;
			if(InnerGroupPcrPvpManager.getAllGroups().contains(mevt.getGroupId()))
			{
				reply += "本群已注册为击剑群。\n友情提示：请使用小群，防止扰民。\n";
			}
			else
			{
				reply += "本群未注册为击剑群，如需注册，请联系主人。\n友情提示：请使用小群，防止扰民。\n";
			}
		}
		else if(message.equals("帮助")||message.toLowerCase(Locale.ROOT).equals("pvp帮助"))
		{
			shishanflag = 0x101;
			reply = "灵梦PVP插件V1.1\n指令格式(空格不可省略。[]表示选择其一)：\n"+
			"[查询 查询昨日][公会 档线] 名称\n"+
			"（例如：查询公会 灵梦）\n"+
			"[双场 战斗 公主][排名 下降]追踪 数字ID\n"+
			"[双场 战斗 公主][排名 下降]追踪 数字ID 名称\n"+
			"（例如：战斗下降追踪 1145141919810 野兽先辈）\n"+
			"解除追踪 数字ID\n"+
			"解除追踪 名称\n"+
			"解除全部追踪\n"+
			"备注追踪目标 数字ID 名称\n"+
			"[开启 关闭]at\n"+
			"注：排名追踪只能在已注册的击剑群内使用，"+
			"要注册击剑群，请联系主人。\n";
		}
		else if(message.equals("反射弧"))
		{
			reply = "当前灵梦PVP插件的反射弧至少为" + InnerGroupPcrPvpManager.Fanshehu + "秒。";
		}
		
		else if(message.startsWith("双场排名追踪")) shishanflag = 0x30;
		else if(message.startsWith("战斗排名追踪")) shishanflag = 0x10;
		else if(message.startsWith("公主排名追踪")) shishanflag = 0x20;
		else if(message.startsWith("双场下降追踪")) shishanflag = 0x31;
		else if(message.startsWith("战斗下降追踪")) shishanflag = 0x11;
		else if(message.startsWith("公主下降追踪")) shishanflag = 0x21;
		if(shishanflag > 0x1 && shishanflag <0x100)
		{
			String[] msgargs = message.replaceAll("\\[CQ:.*?\\]","").split("\\s+",3);
			long target = 0;
			if(msgargs.length >= 2) target = parseLong(msgargs[1].trim());
			if(msgargs.length == 1 ) reply = "未提供参数";
			else if(target< 1000000000000L || target > 9999999999999L) reply = "ID位数错误";
			else if(InnerGroupPcrPvpManager.addTarget(mevt.getUserId(),mevt.getGroupId(), target,
				(shishanflag & 0x10)!=0, (shishanflag & 0x20)!=0, (shishanflag & 0x01)!=0))
			{
				reply = "已添加对" + target + "的追踪，至少需要两分钟才能生效。";
				if(msgargs.length==3)
				{
					String alias = msgargs[2];
					if(alias.length()>16) alias =alias.substring(0, 16);
					InnerGroupPcrPvpManager.renameTarget(mevt.getUserId(), mevt.getGroupId(), parseLong(msgargs[1].trim()), alias);
					
					reply += "\n已将" + target + "备注为" + alias;
				}
			}
			else
			{
				reply = "无权使用或参数错误";
			}
		}

		if(shishanflag != 0)
		{
			//doNothing();
		}
		else if(message.startsWith("解除追踪"))
		{
			String[] msgargs = message.replaceAll("\\[CQ:.*?\\]","").split("\\s+",2);
			if(msgargs.length<2) reply = "未提供参数";
			else if(InnerGroupPcrPvpManager.removeTarget(mevt.getUserId(), mevt.getGroupId(), parseLong(msgargs[1].trim()), true, true)
				||InnerGroupPcrPvpManager.removeTarget(mevt.getUserId(), mevt.getGroupId(), msgargs[1].trim(), true, true))
			{
				reply = "已解除对" + msgargs[1].trim() + "的追踪";
			}
			else
			{
				reply = "未找到目标或无权使用";
			}
		}
		else if(message.startsWith("备注追踪目标"))
		{
			String[] msgargs = message.replaceAll("\\[CQ:.*?\\]","").split("\\s+",3);
			String alias = "";
			if(msgargs.length < 2) reply = "未提供参数";
			else if(msgargs.length == 3) alias = msgargs[2].trim();
			if(InnerGroupPcrPvpManager.renameTarget(mevt.getUserId(), mevt.getGroupId(), parseLong(msgargs[1].trim()), alias))
			{
				if(alias.length()>16) alias = alias.substring(0, 16);
				if(alias.equals("")) reply = "已将" + msgargs[1].trim() + "的备注删除";
				else reply = "已将" + msgargs[1].trim() + "备注为" + alias;
			
			}
			else
			{
				reply = "未找到目标或无权使用";
			}
		}
		else if(message.equals("解除全部追踪")||message.equals("全部解除追踪")||message.equals("解除所有追踪"))
		{
			if(InnerGroupPcrPvpManager.removeAllTarget(mevt.getUserId(),mevt.getGroupId())) reply = "已解除所有追踪";
			else reply = "无权使用";
		}
		else if(message.toLowerCase(Locale.ROOT).equals("开启at"))
		{
			if(InnerGroupPcrPvpManager.atOn(mevt.getUserId(),mevt.getGroupId())) reply = "已开启at";
			else reply = "无权使用";
		}
		else if(message.toLowerCase(Locale.ROOT).equals("关闭at"))
		{
			if(InnerGroupPcrPvpManager.atOff(mevt.getUserId(),mevt.getGroupId())) reply = "已关闭at";
			else reply = "无权使用";
		}
		else if(message.startsWith("公会查询") || message.startsWith("查询公会"))
		{
			String[] msgargs = message.replaceAll("\\[CQ:.*?\\]","").split("\\s+",2);
			String msgarg = "";
			if(msgargs.length == 2)  msgarg = msgargs[1];
			InnerGroupPcrPvpManager.asyncQueryGuildStates(bot, mevt.getUserId(),mevt.getGroupId(),msgarg,false);
		}
		else if(message.startsWith("昨日公会查询") || message.startsWith("查询昨日公会"))
		{
			String[] msgargs = message.replaceAll("\\[CQ:.*?\\]","").split("\\s+",2);
			String msgarg = "";
			if(msgargs.length == 2)  msgarg = msgargs[1];
			InnerGroupPcrPvpManager.asyncQueryGuildStates(bot, mevt.getUserId(),mevt.getGroupId(),msgarg,true);
		}
		else if(message.equals("查询档线") ||  message.equals("档线查询"))
		{
			InnerGroupPcrPvpManager.asyncQueryLineGuildStates(bot, mevt.getUserId(),mevt.getGroupId(),false);
		}
		else if(message.equals("查询昨日档线") ||  message.equals("昨日档线查询"))
		{
			InnerGroupPcrPvpManager.asyncQueryLineGuildStates(bot, mevt.getUserId(),mevt.getGroupId(),true);
		}
		else if(message.startsWith("拉黑"))
		{
			AsftOneBotEvent.GroupSender sender = (AsftOneBotEvent.GroupSender)mevt.sender;
			if(
				(sender == null || !sender.isAdmin()
				||InnerGroupPcrPvpManager.isBlackUser(mevt.getUserId())
				||InnerGroupPcrPvpManager.isBlackGroup(mevt.getGroupId()))
				&&!GroupPcrPvpManager.isOwner(mevt.getUserId()))
			{
				//reply =  "用户无权使用";
			}
			else
			{
				for(String qq :AsftOneBotMessage.createFromCqString(message).getElementDataValueVector("at", "qq"))
				{
					InnerGroupPcrPvpManager.banUser(parseLong(qq));
					InnerGroupPcrPvpManager.leaveGroup(parseLong(qq), mevt.getGroupId());
				}
			}
		}
		else if(message.startsWith("解禁"))
		{
			AsftOneBotEvent.GroupSender sender = (AsftOneBotEvent.GroupSender)mevt.sender;
			if(
				(sender == null ||!sender.isAdmin()
				||InnerGroupPcrPvpManager.isBlackUser(mevt.getUserId())
				||InnerGroupPcrPvpManager.isBlackGroup(mevt.getGroupId()))
				&&!GroupPcrPvpManager.isOwner(mevt.getUserId()))
			{
				//reply =  "用户无权使用";
			}
			else
			{
				for(String qq :AsftOneBotMessage.createFromCqString(message).getElementDataValueVector("at", "qq"))
				{
					InnerGroupPcrPvpManager.banUser(parseLong(qq), false);
				}
			}
		}

		if(reply != null) AsftOneBotApi.SendGroupMessage_text(mevt.getGroupId(), reply).send(bot);
		return continueflag?"continue":"break";
	}
	protected String handelNoticeEvent(AsftOneBotEvent.NoticeEvent nevt, AwpBotInterface bot)
	{
		//Handle Notice Event Here.
		if(nevt.isGroupMemberDecreaseEvent())
		{
			if( ((AsftOneBotEvent.GroupMemberDecreaseEvent)nevt).isKickMeEvent()) InnerGroupPcrPvpManager.purgeGroup(nevt.getGroupId());
			if( ((AsftOneBotEvent.GroupMemberDecreaseEvent)nevt).isKickEvent()) 
			{
				InnerGroupPcrPvpManager.leaveGroup(nevt.getUserId(), nevt.getGroupId());
				AsftOneBotApi.SendGroupMessage_text(nevt.getGroupId(), "" + nevt.getUserId()  +"被肃反了。").send(bot);
			}
			else 
			{
				InnerGroupPcrPvpManager.leaveGroup(nevt.getUserId(), nevt.getGroupId());
				AsftOneBotApi.SendGroupMessage_text(nevt.getGroupId(), "" + nevt.getUserId()  +"退群了。").send(bot);
			}
		}
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