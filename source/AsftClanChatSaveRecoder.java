import java.util.*;
import org.json.*;

public class AsftClanChatSaveRecoder implements AwpBotComponent 
{
	GroupChatSaveManager InnerGroupChatSaveManager;
	protected boolean EnableSetu = true;
	protected boolean AutoRegister = false;
	protected long VtGroup = 0;
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
	protected AsftOneBotMessage createChatRecordAbstract(String chatrecord, boolean pic)
	{
		AsftOneBotMessage tchat = AsftOneBotMessage.createFromCqString(chatrecord);
		String stmp = tchat.toString();
		if(stmp.length() >= 60) stmp = stmp.substring(0, 60) + "...";
		AsftOneBotMessage abstm;
		if(pic) 
		{
			abstm =  AsftOneBotMessage.createFromText(stmp + "\n");
			int imagepos = tchat.getFirstIndexOf("image");
			if(imagepos != -1)
			{
				//tchat.replaceFilesByUrls();
				abstm.appendPicture(tchat.getElementDataValue(imagepos, "file"),tchat.getElementDataValue(imagepos, "url"));
			}
		}
		else
		{
			abstm =  AsftOneBotMessage.createFromText(stmp);
		}
		return abstm;
	}
	@Override
	public boolean save(AwpBotInterface bot) 
	{
		return InnerGroupChatSaveManager.save();
	}

	@Override
	public boolean load(AwpBotInterface bot) 
	{
		return InnerGroupChatSaveManager.load();
	}

	@Override
	public String getComponentName() 
	{
		return "AsftClanChatSaveRecoder";
	}

	public AsftClanChatSaveRecoder() 
	{
		InnerGroupChatSaveManager = new GroupChatSaveManager();
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
		if(message.equals("保存") || message.equals("保存所有记录"))
		{
			if(GroupChatSaveManager.isOwner(mevt.getUserId())) 
			{
				reply = "灵梦记轴插件：" + (InnerGroupChatSaveManager.save() ? "保存成功" : "保存失败");
			}
			else
			{
				reply = "权限不足";
			}
		}
		else if(message.equals("主人"))
		{
			if(GroupChatSaveManager.isOwner(mevt.getUserId()))
			{
				reply = "保存 保存所有记录 允许上传色图（默认） 禁止上传色图 删除所有记录 拉黑用户 解禁用户 "
				+ "拉黑群 解禁群 注册群 删除群 启用自动注册 禁用自动注册（默认） 广播 状态 私聊 群消息 虚拟入群";
			}
		}
		else if(message.equals("允许上传色图"))
		{
			if(GroupChatSaveManager.isOwner(mevt.getUserId()))
			{
				EnableSetu = true;
				reply = "已允许上传色图";
			}
			else
			{
				reply = "权限不足";
			}
		}
		else if(message.equals("禁止上传色图"))
		{
			if(GroupChatSaveManager.isOwner(mevt.getUserId()))
			{
				EnableSetu = false;
				reply = "已禁止上传色图";
			}
			else
			{
				reply = "权限不足";
			}
		}
		else if(message.equals("启用自动注册"))
		{
			if(GroupChatSaveManager.isOwner(mevt.getUserId()))
			{
				AutoRegister = true;
				reply = "已启用自动注册，接收到群消息会自动注册该群";
			}
			else
			{
				reply = "权限不足";
			}
		}
		else if(message.equals("状态"))
		{
			reply = "已注册的群：" + InnerGroupChatSaveManager.getAllGroups();
			reply += "\n已拉黑的群：" + InnerGroupChatSaveManager.getBlackGroups();
			reply += "\n已拉黑用户：" + InnerGroupChatSaveManager.getBlackUsers();
			reply += "\n自动注册：" + (AutoRegister?"启用":"禁用");
			reply += "\n上传色图：" + (EnableSetu?"允许":"禁止");
			if(GroupChatSaveManager.isOwner(mevt.getUserId())) reply += "\n虚拟入群：" + VtGroup;
		}
		else if(message.equals("禁用自动注册"))
		{
			if(GroupChatSaveManager.isOwner(mevt.getUserId()))
			{
				AutoRegister = false;
				reply = "已禁用自动注册，接收到群消息不会注册该群";
			}
			else
			{
				reply = "权限不足";
			}
		}
		else if(message.startsWith("上传色图"))
		{
			String[] msgargs = mevt.message.replaceAll("\\[CQ:.*?\\]","").split("#",3);
			if(!EnableSetu || !GroupChatSaveManager.isOwner(mevt.getUserId()))
			{
				reply = "BOT主人已禁止上传色图";
			}
			else if(msgargs.length == 1)
			{
				;
			}
			else if(msgargs.length < 3 ||!msgargs[0].trim().equals("上传色图")) 
			{
				reply = "指令格式错误";
			}
			else
			{
				String cqstr = mevt.message;
				AsftOneBotMessage tempentire = AsftOneBotMessage.createFromJsonString(cqstr);
				if(tempentire.toJSONArray().length() == 0) tempentire = AsftOneBotMessage.createFromCqString(cqstr);
				else cqstr = tempentire.toCqString();
				for(long group: InnerGroupChatSaveManager.getAllGroups())
				{
					InnerGroupChatSaveManager.record(mevt.getUserId(), group, ("色图 " + msgargs[1]).split("\\s+"), cqstr.split("#",2)[1]);
				}
				reply = "色图已上传至所有已注册且未被拉黑的群";
			}	
		}
		else if(message.startsWith("广播"))
		{
			if(GroupChatSaveManager.isOwner(mevt.getUserId()))
			{
				String broadcast = mevt.message.replaceFirst("广播", "").trim();
				for(long group: InnerGroupChatSaveManager.getAllGroups())
				{
					AsftOneBotApi.SendGroupMessage_jsarr(group, 
						AsftOneBotMessage.createFromCqString(broadcast).toJSONArray()).send(bot);
				}	
			}
		}
		else if(message.startsWith("私聊"))
		{
			if(GroupChatSaveManager.isOwner(mevt.getUserId()))
			{
				String[] ag = mevt.message.replaceFirst("私聊", "").trim().split("\\s+",2);
				if(ag.length == 2) AsftOneBotApi.SendPrivateMessage_jsarr(parseLong(ag[0]), 
                  					AsftOneBotMessage.createFromCqString(ag[1]).toJSONArray()).send(bot);
			}
		}
		else if(message.startsWith("群消息"))
		{
			if(GroupChatSaveManager.isOwner(mevt.getUserId()))
			{
				String[] ag = mevt.message.replaceFirst("群消息", "").trim().split("\\s+",2);
				if(ag.length ==2)
				AsftOneBotApi.SendGroupMessage_jsarr(parseLong(ag[0]), 
					AsftOneBotMessage.createFromCqString(ag[1]).toJSONArray()).send(bot);
			}
		}
		else if(message.equals("删除所有记录"))
		{
			reply = InnerGroupChatSaveManager.purgeAll(mevt.getUserId())?
				("已" + message)  : "权限不足";
		}
		else if(message.startsWith("拉黑用户"))
		{
			reply = InnerGroupChatSaveManager.banUser(mevt.getUserId(), 
				parseLong(message.replaceFirst("拉黑用户", "").trim()))?
				("已" + message) : "权限不足或参数错误";
		}
		else if(message.startsWith("解禁用户"))
		{
			reply = InnerGroupChatSaveManager.banUser(mevt.getUserId(), 
				parseLong(message.replace("解禁用户", "").trim()),false)?
				("已" + message)  : "权限不足或参数错误";
		}
		else if(message.startsWith("拉黑群"))
		{
			reply = InnerGroupChatSaveManager.banGroup(mevt.getUserId(), 
				parseLong(message.replaceFirst("拉黑群", "").trim()))?
				("已" + message) : "权限不足或参数错误";
		}
		else if(message.startsWith("解禁群"))
		{
			reply = InnerGroupChatSaveManager.banGroup(mevt.getUserId(), 
				parseLong(message.replaceFirst("解禁群", "").trim()),false)?
				("已" + message) : "权限不足或参数错误";
		}
		else if(message.startsWith("删除群"))
		{
			reply = InnerGroupChatSaveManager.purgeGroup(mevt.getUserId(), 
				parseLong(message.replaceFirst("删除群", "").trim()))?
				("已" + message) : "权限不足或参数错误";
		}
		else if(message.startsWith("注册群"))
		{
			if(GroupChatSaveManager.isOwner(mevt.getUserId()))
			{
				reply = InnerGroupChatSaveManager.registerGroup( 
					parseLong(message.replaceFirst("注册群", "").trim()))?
					("已" + message) : "群已经注册或被拉黑";
			}
			else reply = "无权限操作";
		}
		else if(message.startsWith("虚拟入群"))
		{
			if(GroupChatSaveManager.isOwner(mevt.getUserId()))
			{
				VtGroup = parseLong(message.replaceFirst("虚拟入群", "").trim());
				if(VtGroup == 0) reply = "已解除虚拟入群";
				else reply = "已虚拟入群" + VtGroup;
			}
		}
		else if(message.toLowerCase(Locale.ROOT).equals("version"))
		{
			reply = "灵梦记轴插件V1.2\n本插件不可跨群使用，可发送 \"帮助\" 获取使用方法。\n";
			reply += "V1.2 更新指令：删除，查询，随机查看；新增指令：查询交集，删除相关\n";
			reply += "github: https://github.com/awp9835/AsftPcrbotPlus";
		}
		else if(message.equals("帮助"))
		{
			reply = "灵梦记轴插件1.2\n指令格式(空格或#不可省略)：\n"+
			"记录#关键字1 关键字2 ...#内容 \n"+
			"查询 关键字1 关键字2 ..."+
			"查询 编号\n"+     
			"查询交集 关键字1 关键字2 ...\n"+
			"随机查看 关键字1 关键字2 ...\n"+
			"查看 编号\n"+
			"查看裂图 编号\n"+
			"删除 编号1 编号2 ...\n"+
 			"删除相关 关键字1 关键字2 ...\n"+
			"拉黑@成员 \n"+
			"解禁@成员 \n"+
			"色图\n"+
			"注册\n"+
			"上传色图#关键字1 关键字2 ...#内容\n"+
			"注：以上指令必须在本群内使用，不可跨群使用；\n内容可以是图片和文字，若图片无法显示请使用查看裂图；\n手机先选图再输入文字可发送文字加图片。\n"+
			"记录关键字中包括色图时可在群内上传色图（请勿上传露点图片，否则拉黑群）。\n";
			reply += "上传色图指令向所有已注册且未被拉黑的群上传色图（无需色图关键字，请勿上传露点图片，否则拉黑）。\n";
		}

		if(reply != null) 
		{
			AsftOneBotApi.SendPrivateMessage_text(mevt.getUserId(), reply).send(bot);
		}







		//虚拟入群操作
		reply = null;
		if(VtGroup != 0 && GroupChatSaveManager.isOwner(mevt.getUserId()))
		{
			if(message.equals("色图"))
			{
				String[] setu = {"色图"}; 
				HashMap<Long, String> qrstrs = InnerGroupChatSaveManager.queryByKeys(mevt.getUserId(), VtGroup, setu);
				if(qrstrs == null) reply = "无色图或无权使用";
				else if(qrstrs.size() == 0) reply = "无色图";
				else 
				{
					int target = new Random().nextInt(qrstrs.size());
					int cnt = 0;
					for (HashMap.Entry<Long, String> entry : qrstrs.entrySet()) 
					{
						if(cnt!=target)
						{
							cnt++;
							continue;
						}
						AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("编号" + entry.getKey() + " \n" + entry.getValue());
						amsg.replaceFilesByUrls();
						AsftOneBotApi.SendPrivateMessage_jsarr(mevt.getUserId(), amsg.toJSONArray()).send(bot);
						break;
					}
				}
			}
			else if(message.startsWith("记录"))
			{
				String[] msgargs = message.replaceAll("\\[CQ:.*?\\]","").split("#",3);
				if(msgargs.length == 1)
				{
					;
				}
				else if(msgargs.length < 3 ||!msgargs[0].trim().equals("记录")) 
				{
					reply = "指令格式错误";
				}
				else
				{
					String cqstr = mevt.message;
					AsftOneBotMessage tempentire = AsftOneBotMessage.createFromJsonString(cqstr);
					if(tempentire.toJSONArray().length() == 0) tempentire = AsftOneBotMessage.createFromCqString(cqstr);
					else cqstr = tempentire.toCqString();
					long id = InnerGroupChatSaveManager.record(mevt.getUserId(), VtGroup, msgargs[1].split("\\s+"), cqstr.split("#",2)[1]);
					if(id == 0)
					{
						reply = "记录失败，指令格式错误或无权操作";
					}
					else
					{
						reply = "记录成功，该记录的编号为" + id;
					}
				}	
			}
			else if(message.startsWith("查询") ||message.startsWith("查看")
				||message.startsWith("随机查看")||message.startsWith("删除"))
			{
				String[] msgargs = message.replaceAll("\\[CQ:.*?\\]","").split("#|\\s+",2);
				if(msgargs.length < 2) 
				{
					;//reply = "指令格式错误";
				}
				else
				{
					switch(msgargs[0].trim())
					{
					default:
						reply = "指令格式错误";
						break;
					case "查询":
					{
						HashMap<Long, String> qrstrs = InnerGroupChatSaveManager.queryByKeys(mevt.getUserId(),VtGroup, msgargs[1].split("\\s+"));
						if(qrstrs == null) reply = "本群未注册或用户无权查看";
						else if(qrstrs.size() == 0) 
						{
							long idlook = parseLong(msgargs[1].trim());
							if(idlook == 0)
							{
								reply = "关键字无查询结果";
								break;
							}
							String strlook = InnerGroupChatSaveManager.queryById(mevt.getUserId(), VtGroup, idlook);
							if(strlook == null) reply = "关键字或指定编号记录不存在或无权查看";
							else 
							{
								AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("编号" + idlook + " \n" + strlook);
								amsg.replaceFilesByUrls();
								AsftOneBotApi.SendPrivateMessage_jsarr(mevt.getUserId(), amsg.toJSONArray()).send(bot);
							}
							break;
						}
						else if(qrstrs.size() == 1)
						{
							for (HashMap.Entry<Long, String> entry : qrstrs.entrySet()) 
							{
								AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("共1条记录：编号" + entry.getKey() + " \n" + entry.getValue());
								AsftOneBotApi.SendPrivateMessage_jsarr(mevt.getUserId(),amsg.toJSONArray()).send(bot);
							}
						}
						else 
						{
							int len = qrstrs.size();
							AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("共"+ len +"条记录，为控制篇幅，每组消息只显示前2张图片。 \n");
							int cnt = 0;
							for (HashMap.Entry<Long, String> entry : qrstrs.entrySet()) 
							{
								if(cnt != 0 )amsg.appendText("\n");
								cnt++;
								amsg.appendText("编号" + entry.getKey() + "摘要： \n");
								amsg.appendAsftOneBotMessage(createChatRecordAbstract(entry.getValue(),cnt <= 2));
								if(cnt == 8) 
								{
									cnt = 0;
									AsftOneBotApi.SendPrivateMessage_jsarr(mevt.getUserId(), amsg.toJSONArray()).send(bot);
									amsg.replaceFilesByUrls();
									amsg = AsftOneBotMessage.createEmpty();
								}
							}
							if(cnt != 0) AsftOneBotApi.SendPrivateMessage_jsarr(mevt.getUserId(), amsg.toJSONArray()).send(bot);
						}
						break;
					}
					case "查看":
					{
						long idlook = parseLong(msgargs[1].trim());
						if(idlook == 0)
						{
							reply = "查看的参数必须是编号，请先查询";
							break;
						}
						String strlook = InnerGroupChatSaveManager.queryById(mevt.getUserId(), VtGroup, idlook);
						if(strlook == null) reply = "本群未注册、指定编号不存在或用户无权查看";
						else 
						{
							AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("编号" + idlook + " \n" + strlook);
							amsg.replaceFilesByUrls();
							AsftOneBotApi.SendPrivateMessage_jsarr(mevt.getUserId(), amsg.toJSONArray()).send(bot);
						}
						break;
					}
					case "查看裂图":
					{
						long idlook = parseLong(msgargs[1].trim());
						if(idlook == 0)
						{
							reply = "查看的参数必须是编号，请先查询";
							break;
						}
						String strlook = InnerGroupChatSaveManager.queryById(mevt.getUserId(), VtGroup, idlook);
						if(strlook == null) reply = "本群未注册、指定编号不存在或用户无权查看";
						else 
						{
							AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("编号" + idlook + " \n" + strlook);
							//amsg.replaceFilesByUrls();
							AsftOneBotApi.SendPrivateMessage_jsarr(mevt.getUserId(), amsg.toJSONArray()).send(bot);
						}
						break;
					}
					case "随机查看":
					{
						HashMap<Long, String> qrstrs = InnerGroupChatSaveManager.queryByKeys(mevt.getUserId(),VtGroup,msgargs[1].split("\\s+"));
						if(qrstrs == null) reply = "本群未注册、无查询结果或用户无权查看";
						else if(qrstrs.size() == 0) reply = "无查询结果";
						else 
						{
							int target = new Random().nextInt(qrstrs.size());
							int cnt = 0;
							for (HashMap.Entry<Long, String> entry : qrstrs.entrySet()) 
							{
								if(cnt!=target)
								{
									cnt++;
									continue;
								}
								AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("编号" + entry.getKey() + " \n" + entry.getValue());
								amsg.replaceFilesByUrls();
								AsftOneBotApi.SendPrivateMessage_jsarr(mevt.getUserId(), amsg.toJSONArray()).send(bot);
								break;
							}
						}
						break;
					}
					case "删除":
					{
						 String[] targets = msgargs[1].trim().split("\\s+");
						boolean succeed = false;
						reply = "";
						 for(String target:targets)
						{
    							long iddel = parseLong(target.trim());
    							if(InnerGroupChatSaveManager.removeById(mevt.getUserId(), VtGroup, iddel))
							{
    						    		if(succeed)  reply+="\n";
								else succeed = true;
								reply += "已删除编号为" + iddel + "的记录";
    							}
						}
						if(!succeed)
						{
							reply = "本群未注册、记录不存在或用户无权删除";
						}
						break;
					}
                    case "删除相关":
					{
						 if(InnerGroupChatSaveManager.removeAllByKeys(mevt.getUserId(), VtGroup, msgargs[1].trim().split("\\s+")))
						{
							 reply = "已删除所有相关记录";
						}
						else
						{
							reply = "本群未注册、记录不存在或用户无权删除";
						}
						break;
					}
					case "查询交集":
					{
						HashMap<Long, String> qrstrs =  InnerGroupChatSaveManager.queryByKey(mevt.getUserId(), VtGroup, msgargs[1].split("\\s+")[0]);
						for(String key : msgargs[1].split("\\s+"))
						{
							if(qrstrs == null || qrstrs.size() == 0) break;
							HashMap<Long, String> nqrstrs = new HashMap<Long, String>();
							HashMap<Long, String> qtmp =  InnerGroupChatSaveManager.queryByKey(mevt.getUserId(), VtGroup, key); 
							for(Long rkey: qtmp.keySet())
							{
								String tmpl = qrstrs.get(rkey);
								if(tmpl != null) nqrstrs.put(rkey, tmpl);
							}
							 qrstrs = nqrstrs;
						}
						if(qrstrs == null)  reply = "本群未注册、无查询结果或用户无权查看";
						else if(qrstrs.size() == 0) 
						{
							reply = "本群未注册、无查询结果或用户无权查看";
						}
						else if(qrstrs.size() == 1)
						{
							for (HashMap.Entry<Long, String> entry : qrstrs.entrySet()) 
							{
								AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("共1条记录：编号" + entry.getKey() + " \n" + entry.getValue());
								AsftOneBotApi.SendPrivateMessage_jsarr(mevt.getUserId(),amsg.toJSONArray()).send(bot);
							}
						}
						else 
						{
							int len = qrstrs.size();
							AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("共"+ len +"条记录，为控制篇幅，每组消息只显示前2张图片。 \n");
							int cnt = 0;
							for (HashMap.Entry<Long, String> entry : qrstrs.entrySet()) 
							{
								if(cnt != 0 )amsg.appendText("\n");
								cnt++;
								amsg.appendText("编号" + entry.getKey() + "摘要： \n");
								amsg.appendAsftOneBotMessage(createChatRecordAbstract(entry.getValue(),cnt <= 2));
								if(cnt == 8) 
								{
									cnt = 0;
									AsftOneBotApi.SendPrivateMessage_jsarr(mevt.getUserId(), amsg.toJSONArray()).send(bot);
									amsg.replaceFilesByUrls();
									amsg = AsftOneBotMessage.createEmpty();
								}
							}
							if(cnt != 0) AsftOneBotApi.SendPrivateMessage_jsarr(mevt.getUserId(), amsg.toJSONArray()).send(bot);
						}
						break;
					}
                   				}
				}
			}
			if(reply != null) 
			{
				AsftOneBotApi.SendPrivateMessage_text(mevt.getUserId(), reply).send(bot);
			}
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
		if(AutoRegister)
		{
			reply = InnerGroupChatSaveManager.registerGroup(mevt.getGroupId())?"已注册本群\n灵梦记轴插件V1.2\n本插件不可跨群使用，可发送 \"帮助\" 获取使用方法。": null;
		}
		if(message.toLowerCase(Locale.ROOT).equals("version")||message.equals("注册"))
		{
			reply = InnerGroupChatSaveManager.registerGroup(mevt.getGroupId())?"已注册本群\n":"";
			reply += "灵梦记轴插件V1.2\n本插件不可跨群使用，可发送 \"帮助\" 获取使用方法。\n";
            reply += "V1.2 更新指令：删除，查询，随机查看；新增指令：查询交集，删除相关\n";
			reply += "github: https://github.com/awp9835/AsftPcrbotPlus";
		}
		else if(message.equals("色图"))
		{
			String[] setu = {"色图"}; 
			HashMap<Long, String> qrstrs = InnerGroupChatSaveManager.queryByKeys(mevt.getUserId(),mevt.getGroupId(), setu);
			if(qrstrs == null) reply = "无色图或无权使用";
			else if(qrstrs.size() == 0) reply = "无色图";
			else 
			{
				int target = new Random().nextInt(qrstrs.size());
				int cnt = 0;
				for (HashMap.Entry<Long, String> entry : qrstrs.entrySet()) 
				{
					if(cnt!=target)
					{
						cnt++;
						continue;
					}
					AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("编号" + entry.getKey() + " \n" + entry.getValue());
					//amsg.replaceFilesByUrls();
					AsftOneBotApi.SendGroupMessage_jsarr(mevt.getGroupId(), amsg.toJSONArray()).send(bot);
					break;
				}
			}
		}
		else if(message.equals("帮助"))
		{
			reply = InnerGroupChatSaveManager.registerGroup(mevt.getGroupId())?"已注册本群\n":"";
			reply = "灵梦记轴插件V1.2\n指令格式(空格或#不可省略)：\n"+
			"记录#关键字1 关键字2 ...#内容 \n"+
			"查询 关键字1 关键字2 ..."+
			"查询 编号\n"+     
			"查询交集 关键字1 关键字2 ...\n"+
			"随机查看 关键字1 关键字2 ...\n"+
			"查看 编号\n"+
			"查看裂图 编号\n"+
			"删除 编号1 编号2 ...\n"+
 			"删除相关 关键字1 关键字2 ...\n"+
			"拉黑@成员 \n"+
			"解禁@成员 \n"+
			"色图\n"+
			"注册\n"+
			"上传色图#关键字1 关键字2 ...#内容\n"+
			"注：以上指令必须在本群内使用，不可跨群使用；\n内容可以是图片和文字，若图片无法显示请使用查看裂图；\n手机先选图再输入文字可发送文字加图片。\n"+
			"记录关键字中包括色图时可在群内上传色图（请勿上传露点图片，否则拉黑群）。\n";
			reply += "上传色图指令向所有已注册且未被拉黑的群上传色图（无需色图关键字，请勿上传露点图片，否则拉黑）。\n";
			
		}
		else if(message.startsWith("上传色图"))
		{
			String[] msgargs = mevt.message.replaceAll("\\[CQ:.*?\\]","").split("#",3);
			if(!EnableSetu || !GroupChatSaveManager.isOwner(mevt.getUserId()))
			{
				reply = "BOT主人已禁止上传色图";
			}
			else if(msgargs.length == 1)
			{
				;
			}
			else if(msgargs.length < 3 ||!msgargs[0].trim().equals("上传色图")) 
			{
				reply = "指令格式错误";
			}
			else
			{
				String cqstr = mevt.message;
				AsftOneBotMessage tempentire = AsftOneBotMessage.createFromJsonString(cqstr);
				if(tempentire.toJSONArray().length() == 0) tempentire = AsftOneBotMessage.createFromCqString(cqstr);
				else cqstr = tempentire.toCqString();
				for(long group: InnerGroupChatSaveManager.getAllGroups())
				{
					InnerGroupChatSaveManager.record(mevt.getUserId(), group, ("色图 " + msgargs[1]).split("\\s+"), cqstr.split("#",2)[1]);
				}
				reply = "色图已上传至所有已注册且未被拉黑的群";
			}	
		}
		else if(message.startsWith("记录"))
		{
			String[] msgargs = message.replaceAll("\\[CQ:.*?\\]","").split("#",3);
			if(msgargs.length == 1)
			{
				;
			}
			else if(msgargs.length < 3 ||!msgargs[0].trim().equals("记录")) 
			{
				reply = "指令格式错误";
			}
			else
			{
				String cqstr = mevt.message;
				AsftOneBotMessage tempentire = AsftOneBotMessage.createFromJsonString(cqstr);
				if(tempentire.toJSONArray().length() == 0) tempentire = AsftOneBotMessage.createFromCqString(cqstr);
				else cqstr = tempentire.toCqString();
				long id = InnerGroupChatSaveManager.record(mevt.getUserId(), mevt.getGroupId(), msgargs[1].split("\\s+"), cqstr.split("#",2)[1]);
				if(id == 0)
				{
					reply = "记录失败，指令格式错误或无权操作";
				}
				else
				{
					reply = "记录成功，该记录的编号为" + id;
				}
			}	
		}
		else if(message.startsWith("查询") ||message.startsWith("查看")
			||message.startsWith("随机查看")||message.startsWith("删除"))
		{
			String[] msgargs = message.replaceAll("\\[CQ:.*?\\]","").split("#|\\s+",2);
			if(msgargs.length < 2) 
			{
				;//reply = "指令格式错误";
			}
			else
			{
				switch(msgargs[0].trim())
				{
				default:
					//reply = "指令格式错误";
					break;
				case "查询":
				{
					HashMap<Long, String> qrstrs = InnerGroupChatSaveManager.queryByKeys(mevt.getUserId(),mevt.getGroupId(), msgargs[1].split("\\s+"));
					if(qrstrs == null) reply = "本群未注册或用户无权查看";
					else if(qrstrs.size() == 0) 
					{
						long idlook = parseLong(msgargs[1].trim());
						if(idlook == 0)
						{
							reply = "关键字无查询结果";
							break;
						}
						String strlook = InnerGroupChatSaveManager.queryById(mevt.getUserId(), mevt.getGroupId(), idlook);
						if(strlook == null) reply = "关键字或指定编号记录不存在或无权查看";
						else 
						{
							AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("编号" + idlook + " \n" + strlook);
							AsftOneBotApi.SendGroupMessage_jsarr(mevt.getGroupId(), amsg.toJSONArray()).send(bot);
						}
						break;
					}
					else if(qrstrs.size() == 1)
					{
						for (HashMap.Entry<Long, String> entry : qrstrs.entrySet()) 
						{
							AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("共1条记录：编号" + entry.getKey() + " \n" + entry.getValue());
							AsftOneBotApi.SendGroupMessage_jsarr(mevt.getGroupId(), amsg.toJSONArray()).send(bot);
						}
					}
					else 
					{
						int len = qrstrs.size();
						AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("共"+ len +"条记录，为控制篇幅，每组消息只显示前2张图片。 \n");
						int cnt = 0;
						for (HashMap.Entry<Long, String> entry : qrstrs.entrySet()) 
						{
							if(cnt != 0 )amsg.appendText("\n");
							cnt++;
							amsg.appendText("编号" + entry.getKey() + "摘要： \n");
							amsg.appendAsftOneBotMessage(createChatRecordAbstract(entry.getValue(),cnt <= 2));
							if(cnt == 8) 
							{
								cnt = 0;
								AsftOneBotApi.SendGroupMessage_jsarr(mevt.getGroupId(), amsg.toJSONArray()).send(bot);
								amsg = AsftOneBotMessage.createEmpty();
							}
						}
						if(cnt != 0) AsftOneBotApi.SendGroupMessage_jsarr(mevt.getGroupId(), amsg.toJSONArray()).send(bot);
					}
					break;
				}
				case "查看":
				{
					long idlook = parseLong(msgargs[1].trim());
					if(idlook == 0)
					{
						reply = "查看的参数必须是编号，请先查询";
						break;
					}
					String strlook = InnerGroupChatSaveManager.queryById(mevt.getUserId(), mevt.getGroupId(), idlook);
					if(strlook == null) reply = "本群未注册、指定编号不存在或用户无权查看";
					else 
					{
						AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("编号" + idlook + " \n" + strlook);
						AsftOneBotApi.SendGroupMessage_jsarr(mevt.getGroupId(), amsg.toJSONArray()).send(bot);
					}
					break;
				}
				case "查看裂图":
				{
					long idlook = parseLong(msgargs[1].trim());
					if(idlook == 0)
					{
						reply = "查看的参数必须是编号，请先查询";
						break;
					}
					String strlook = InnerGroupChatSaveManager.queryById(mevt.getUserId(), mevt.getGroupId(), idlook);
					if(strlook == null) reply = "本群未注册、指定编号不存在或用户无权查看";
					else 
					{
						AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("编号" + idlook + " \n" + strlook);
						amsg.replaceFilesByUrls();
						AsftOneBotApi.SendGroupMessage_jsarr(mevt.getGroupId(), amsg.toJSONArray()).send(bot);
					}
					break;
				}
				case "随机查看":
				{
					HashMap<Long, String> qrstrs = InnerGroupChatSaveManager.queryByKeys(mevt.getUserId(),mevt.getGroupId(),msgargs[1].split("\\s+"));
					if(qrstrs == null) reply = "本群未注册、无查询结果或用户无权查看";
					else if(qrstrs.size() == 0) reply = "无查询结果";
					else 
					{
						int target = new Random().nextInt(qrstrs.size());
						int cnt = 0;
						for (HashMap.Entry<Long, String> entry : qrstrs.entrySet()) 
						{
							if(cnt!=target)
							{
								cnt++;
								continue;
							}
							AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("编号" + entry.getKey() + " \n" + entry.getValue());
							//amsg.replaceFilesByUrls();
							AsftOneBotApi.SendGroupMessage_jsarr(mevt.getGroupId(), amsg.toJSONArray()).send(bot);
							break;
						}
					}
					break;
				}
				case "删除":
				 {
					String[] targets = msgargs[1].trim().split("\\s+");
					boolean succeed = false;
					reply = "";
					for(String target:targets)
					{
    						long iddel = parseLong(target.trim());
    						if(InnerGroupChatSaveManager.removeById(mevt.getUserId(), mevt.getGroupId(), iddel))
    						{
    							if(succeed)  reply += "\n";
							else succeed = true;
  							reply += "已删除编号为" + iddel + "的记录";
    						}
					}
					if(!succeed)
						{
							reply = "本群未注册、记录不存在或用户无权删除";
						}
						break;
					}
					case "删除相关":
					{
						if(InnerGroupChatSaveManager.removeAllByKeys(mevt.getUserId(), mevt.getGroupId(), msgargs[1].trim().split("\\s+")))
 						{
 							reply = "已删除所有相关记录";
						}
						else
						{
							reply = "本群未注册、记录不存在或用户无权删除";
						}
						break;
					}    
				case "查询交集":
  				{
  					HashMap<Long, String> qrstrs =  InnerGroupChatSaveManager.queryByKey(mevt.getUserId(), mevt.getGroupId(), msgargs[1].split("\\s+")[0]);
					for(String key : msgargs[1].split("\\s+"))
					{
						if(qrstrs == null || qrstrs.size() == 0) break;
						HashMap<Long, String> nqrstrs = new HashMap<Long, String>();
						HashMap<Long, String> qtmp =  InnerGroupChatSaveManager.queryByKey(mevt.getUserId(), mevt.getGroupId(), key); 
						for(Long rkey: qtmp.keySet())
						{
							 String tmpl = qrstrs.get(rkey);
							 if(tmpl != null) nqrstrs.put(rkey, tmpl);
						}
						qrstrs = nqrstrs;
					}
  					if(qrstrs == null) reply = "本群未注册、无查询结果或用户无权查看";
  					else if(qrstrs.size() == 0) 
  					{
  						reply = "本群未注册、无查询结果或用户无权查看"; 
  					}
  					else if(qrstrs.size() == 1)
  					{
  						for (HashMap.Entry<Long, String> entry : qrstrs.entrySet()) 
  						{
  							AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("共1条记录：编号" + entry.getKey() + " \n" + entry.getValue());
  							AsftOneBotApi.SendGroupMessage_jsarr(mevt.getGroupId(), amsg.toJSONArray()).send(bot);
  						}
  					}
  					else 
  					{
  						int len = qrstrs.size();
  						AsftOneBotMessage amsg = AsftOneBotMessage.createFromCqString("共"+ len +"条记录，为控制篇幅，每组消息只显示前2张图片。 \n");
  						int cnt = 0;
  						for (HashMap.Entry<Long, String> entry : qrstrs.entrySet()) 
  						{
  							if(cnt != 0 )amsg.appendText("\n");
  							cnt++;
  							amsg.appendText("编号" + entry.getKey() + "摘要： \n");
  							amsg.appendAsftOneBotMessage(createChatRecordAbstract(entry.getValue(),cnt <= 2));
  							if(cnt == 8) 
  							{
  								cnt = 0;
  								AsftOneBotApi.SendGroupMessage_jsarr(mevt.getGroupId(), amsg.toJSONArray()).send(bot);
  								amsg = AsftOneBotMessage.createEmpty();
  							}
  						}
  						if(cnt != 0) AsftOneBotApi.SendGroupMessage_jsarr(mevt.getGroupId(), amsg.toJSONArray()).send(bot);
  					}
  					break;
               				}
				}
			}
		}
		else if(message.startsWith("拉黑"))
		{

			AsftOneBotEvent.GroupSender sender = (AsftOneBotEvent.GroupSender)mevt.sender;
			if(sender == null ||!sender.isAdmin())
			{
				//System.out.println(sender.toJSONObject());
				reply = "权限不足";
			}
			else if(InnerGroupChatSaveManager.isBlackUser(mevt.getUserId())
				||InnerGroupChatSaveManager.isBlackGroup(mevt.getGroupId())
				&&!GroupChatSaveManager.isOwner(mevt.getUserId()))
			{
				reply =  "用户无权使用";
			}
			else
			{
				boolean succeed = false;
				AsftOneBotMessage result = AsftOneBotMessage.createFromText("已禁止以下成员使用：\n");
				for(String qq :AsftOneBotMessage.createFromCqString(message).getElementDataValueVector("at", "qq"))
				{
					if(InnerGroupChatSaveManager.banUser(parseLong(qq)))
					{
						succeed = true;
						result.appendAt(qq);
					}
				}
				if(succeed) AsftOneBotApi.SendGroupMessage_jsarr(mevt.getGroupId(), result.toJSONArray()).send(bot);
				else reply = "权限不足或参数错误";
			}
		}
		else if(message.startsWith("解禁"))
		{
			AsftOneBotEvent.GroupSender sender = (AsftOneBotEvent.GroupSender)mevt.sender;
			if((sender == null ||!sender.isAdmin()))
			{
				reply = "权限不足";
			}
			else if(InnerGroupChatSaveManager.isBlackUser(mevt.getUserId())
				||InnerGroupChatSaveManager.isBlackGroup(mevt.getGroupId())
				&&!GroupChatSaveManager.isOwner(mevt.getUserId()))
			{
				reply =  "用户无权使用";
			}
			else
			{
				boolean succeed = false;
				AsftOneBotMessage result = AsftOneBotMessage.createFromText("已允许以下成员使用： \n");
				for(String qq :AsftOneBotMessage.createFromCqString(message).getElementDataValueVector("at", "qq"))
				{
					if(InnerGroupChatSaveManager.banUser(parseLong(qq), false))
					{
						succeed = true;
						result.appendAt(qq);
					}
				}
				if(succeed) AsftOneBotApi.SendGroupMessage_jsarr(mevt.getGroupId(), result.toJSONArray()).send(bot);
				else reply = "权限不足或参数错误";
			}
		}
		if(reply != null) AsftOneBotApi.SendGroupMessage_text(mevt.getGroupId(), reply).send(bot);
		return continueflag?"continue":"break";
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