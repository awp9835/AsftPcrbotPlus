import org.json.*;

public abstract class AsftOneBotEvent
{
	public static class Sender
	{
		public long user_id;
		public String nickname;
		public int age;
		private int sex;

		public Sender setasMale(){sex = 1;return this;}
		public Sender setasFemale(){sex = 2;return this;}
		public Sender setasUnknownSex(){sex = 3;return this;}
		public String getSex()
		{
			if(sex == 1) return "male";
			else if(sex == 2) return "female";
			else return "unknow";
		}
		public boolean isMale(){return sex == 1;}
		public boolean isFemale(){return sex == 2;}
		public boolean isAdult() {return age >= 18;}
		public boolean isGroupSender(){return this instanceof GroupSender;}

		public Sender setUserId(long user_id){this.user_id = user_id; return this;}
		public Sender setAge(int age){this.age = age; return this;}
		public Sender setNickName(String nickname){this.nickname = nickname; return this;}

		public Sender(){}
		public Sender(Sender src)
		{
			user_id = src.user_id;
			nickname = src.nickname;
			age = src.age;
			sex = src.sex;
		}

		public boolean equals(Sender tar)
		{
			return user_id == tar.user_id;
		}
		public boolean equals(Anonymous tar)
		{
			return user_id == tar.id;
		}

		public JSONObject toJSONObject()
		{
			JSONObject obj = new JSONObject();
			try
			{
				if(user_id != 0) obj.put("user_id",user_id);
				if(nickname != null)obj.put("nickname",nickname);
				if(age != 0) obj.put("age",age);
				if(sex != 0) obj.put("sex",getSex());
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
		@Override
		public String toString(){return toJSONObject().toString();}
	}
	public static final class GroupSender extends Sender
	{
		public String card;
		public String area;
		public String level;
		public String title;
		private int role;
		public GroupSender setasOwner(){role = 2; return this;}
		public GroupSender setasAdmin(){role = 1; return this;}
		public GroupSender setasMember(){role = 0; return this;}
		public boolean isOwner(){return role == 2;}
		public boolean isAdmin(){return role >= 1;}
		public String getRole()
		{
			if(role == 1) return "admin";
			else if(role == 2) return "owner";
			else return "member";
		}

		public GroupSender setCard(String card){this.card = card; return this;}
		public GroupSender setArea(String area){this.area = area; return this;}
		public GroupSender setLevel(String level){this.level = level; return this;}
		public GroupSender setTitle(String title){this.title = title; return this;}

		public GroupSender(){}
		public GroupSender(GroupSender src)
		{
			super(src);
			card = src.card;
			area = src.area;
			level = src.level;
			title = src.title;
			role = src.role;
		}
		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				if(card != null) obj.put("card",card);
				if(area != null)obj.put("area",area);
				if(level != null) obj.put("level",level);
				if(title != null)obj.put("title",title);
				obj.put("role",getRole());
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}
	public static final class Anonymous
	{
		public long id;
		public String name;
		public String flag;

		public Anonymous setId(long id){this.id = id; return this;}
		public Anonymous setName(String name){this.name = name; return this;}
		public Anonymous setFlag(String flag){this.flag = flag; return this;}

		public Anonymous(){}
		public Anonymous(Anonymous src)
		{
			id = src.id;
			name = src.name;
			flag = src.flag;
		}
		public boolean equals(Anonymous tar)
		{
			return id == tar.id;
		}
		public boolean equals(Sender tar)
		{
			return id == tar.user_id;
		}

		public JSONObject toJSONObject()
		{
			JSONObject obj = new JSONObject();
			try
			{
				if(id != 0) obj.put("id",id);
				if(name != null)obj.put("name",name);
				if(flag != null) obj.put("flag",flag);
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
		@Override
		public String toString(){return toJSONObject().toString();}
	}
	public static final class GroupFile
	{
		public String id;
		public String name;
		public long size;
		public long busid;

		public GroupFile setId(String id){this.id = id; return this;}
		public GroupFile setName(String name){this.name = name; return this;}
		public GroupFile setSize(long size){this.size = size; return this;}
		public GroupFile setBusId(long busid){this.busid = busid; return this;}

		public GroupFile(){}
		public GroupFile(GroupFile src)
		{
			id = src.id;
			name = src.name;
			size = src.size;
			busid = src.busid;
		}

		public JSONObject toJSONObject()
		{
			JSONObject obj = new JSONObject();
			try
			{
				if(id != null) obj.put("id",id);
				if(name != null)obj.put("name",name);
				if(size != 0) obj.put("size",size);
				if(busid != 0) obj.put("busid",busid);
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
		@Override
		public String toString(){return toJSONObject().toString();}
	}

	public long time;
	public long self_id;
	protected int post_type;
	protected void setasMessageEvent(){post_type = 1;}
	protected void setasNoticeEvent(){post_type = 2;}
	protected void setasRequestEvent(){post_type = 3;}
	protected void setasMetaEvent(){post_type = 4;}
	public String getPostType()
	{
		if(post_type == 1) return "message";
		else if(post_type == 2) return "notice";
		else if(post_type == 3) return "request";
		else if(post_type == 4) return "meta_event";
		else return null;
	}
	public boolean isMessageEvent(){return post_type == 1;}
	public boolean isNoticeEvent(){return post_type == 2;}
	public boolean isRequestEvent(){return post_type == 3;}
	public boolean isMetaEvent(){return post_type == 4;}


	public AsftOneBotEvent setTime(long time){this.time = time; return this;}
	public AsftOneBotEvent setSelfId(long self_id){this.self_id = self_id; return this;}

	public abstract long getUserId();
	public abstract long getOperatorId();
	public abstract long getGroupId();
	public abstract long getTargetId();
	public abstract String getSecondaryType();
	public abstract String getSubType();
	public abstract String getMainParameter();
	public abstract boolean isGroupEvent();
	public abstract boolean isPrivateEvent();

	public boolean sameGroup(AsftOneBotEvent tar)
	{
		if(getGroupId() == 0) return false;
		else return getGroupId() == tar.getGroupId();
	}
	public boolean sameUser(AsftOneBotEvent tar)
	{
		if(getUserId() == 0) return false;
		else return getUserId() == tar.getUserId();
	}
	public boolean sameTarget(AsftOneBotEvent tar)
	{
		if(getTargetId() == 0) return false;
		else return getTargetId() == tar.getTargetId();
	}

	protected AsftOneBotEvent(){}
	
	public JSONObject toJSONObject()
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("time",time);
			obj.put("self_id",self_id);
			obj.put("post_type",getPostType());
		}
		catch(JSONException e)
		{
			//System.out.println(e);
		}
		return obj;
	}
	@Override
	public String toString(){return toJSONObject().toString();}

	public static abstract class MessageEvent extends AsftOneBotEvent
	{
		private int message_type;
		protected int sub_type;
		public long message_id;
		public long user_id;
		public String raw_message;
		public int font;
		public String message;
		public Sender sender;

		
		protected void setasPrivateMessageEvent(){message_type = 0;}
		protected void setasGroupMessageEvent(){message_type = 1;}
		@Override
		public boolean isPrivateEvent(){return message_type == 0;}
		@Override
		public boolean isGroupEvent(){return message_type == 1;}

		@Override
		public long getUserId(){return user_id;}
		@Override
		public long getOperatorId(){return user_id;}
		@Override
		public String getMainParameter(){return raw_message;}
		@Override
		public String getSecondaryType(){return getMessageType();}

		public abstract String getMessageType();

		public MessageEvent setMessageId(long message_id){this.message_id = message_id; return this;}
		public MessageEvent setUserId(long user_id){this.user_id = user_id; return this;}
		public MessageEvent setRawMessage(String raw_message){this.raw_message = raw_message; return this;}
		public MessageEvent setFont(int font){this.font = font; return this;}
		public MessageEvent setMessage(String message){this.message = message; return this;}
		public MessageEvent setSender(Sender sender){this.sender = sender; return this;}



		protected MessageEvent(){setasMessageEvent();}

		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				obj.put("message_id",message_id);
				obj.put("user_id",user_id);
				obj.put("font",font);
				obj.put("message_type",getMessageType());
				obj.put("sub_type",getSubType());
				if(message != null) obj.put("message",message);
				if(sender != null) obj.put("sender",sender.toJSONObject());
				if(raw_message != null) obj.put("raw_message",raw_message);
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}
	public static class PrivateMessageEvent extends MessageEvent
	{
		public PrivateMessageEvent setasFriendMessageEvent(){sub_type = 0; return this;}
		public PrivateMessageEvent setasGroupTempMessageEvent(){sub_type = 1; return this;}
		public PrivateMessageEvent setasOtherMessageEvent(){sub_type = 2; return this;}
		
		public boolean isFriendMessageEvent(){return sub_type == 0;}
		public boolean isGroupTempMessageEvent(){return sub_type == 1;}
		public boolean isOtherMessageEvent(){return sub_type == 2;}

		@Override
		public String getMessageType(){return "private";}
		@Override
		public long getGroupId(){return 0;}
		@Override
		public long getTargetId(){return self_id;}
		@Override
		public String getSubType()
		{
			if(post_type == 0) return "friend";
			else if(post_type == 1) return "group";
			else return "other";
		}
		
		public PrivateMessageEvent()
		{
			setasPrivateMessageEvent();
		}
	}
	public static class GroupMessageEvent extends MessageEvent
	{
		public long group_id;
		protected Anonymous anonymous;
		public GroupMessageEvent setasNormalMessageEvent()
		{
			anonymous = null;
			sub_type = 0;
			return this;
		}
		public GroupMessageEvent setasAnonymousMessageEvent(Anonymous anoninfo)
		{
			anonymous = anoninfo;
			sub_type = 1;
			return this;
		}
		public GroupMessageEvent setasNoticeMessageEvent()
		{
			anonymous = null;
			sub_type = 2;
			return this;
		}
		public boolean isNormalMessageEvent(){return sub_type == 0;}
		public boolean isAnonymousMessageEvent(){return sub_type == 1;}
		public boolean isNoticeMessageEvent(){return sub_type == 2;}

		public GroupMessageEvent setGroupId(long group_id){this.group_id = group_id; return this;}

		public Anonymous getAnonymous(){return anonymous;}

		@Override
		public long getGroupId(){return group_id;}
		@Override
		public long getTargetId(){return group_id;}
		@Override
		public String getMessageType(){return "group";}
		@Override
		public String getSubType()
		{
			if(post_type == 0) return "normal";
			else if(post_type == 1) return "anonymous";
			else return "notice";
		}

		public GroupMessageEvent()
		{
			setasGroupMessageEvent();
		}

		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				obj.put("group_id",group_id);
				if(anonymous != null) obj.put("anonymous",anonymous.toJSONObject());
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}

	public static abstract class NoticeEvent extends AsftOneBotEvent
	{
		private int notice_type;
		public long user_id;

		protected void setasGroupFileUploadEvent(){notice_type = 0;}
		protected void setasGroupAdminEvent(){notice_type = 1;}
		protected void setasGroupMemberDecreaseEvent(){notice_type = 2;}
		protected void setasGroupMemberIncreaseEvent(){notice_type = 3;}
		protected void setasGroupMemberBanEvent(){notice_type = 4;}
		protected void setasGroupMessageRecallEvent(){notice_type = 5;}
		protected void setasGroupInteractiveEvent(){notice_type = 6;}
		protected void setasFriendAddEvent(){notice_type = 100;}
		protected void setasFriendMessageRecallEvent(){notice_type = 101;}

		public NoticeEvent setUserId(long user_id){this.user_id = user_id; return this;}

		public boolean isGroupFileUploadEvent(){return notice_type == 0;}
		public boolean isGroupAdminEvent(){return notice_type == 1;}
		public boolean isGroupMemberDecreaseEvent(){return notice_type == 2;}
		public boolean isGroupMemberIncreaseEvent(){return notice_type == 3;}
		public boolean isGroupMemberBanEvent(){return notice_type == 4;}
		public boolean isGroupMessageRecallEvent(){return notice_type == 5;}
		public boolean isGroupInteractiveEvent(){return notice_type == 6;}
		public boolean isFriendAddEvent(){return notice_type == 100;}
		public boolean isFriendMessageRecallEvent(){return notice_type == 101;}
		@Override 
		public boolean isGroupEvent(){return notice_type < 100;}
		@Override 
		public boolean isPrivateEvent() {return notice_type >= 100;}
		@Override 
		public long getUserId(){return user_id;}

		@Override
		public String getSecondaryType(){return getNoticeType();}
		
		public abstract String getNoticeType();
		
		protected NoticeEvent(){setasNoticeEvent();}

		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				obj.put("user_id",user_id);
				obj.put("notice_type",getNoticeType());
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}
	public static class GroupFileUploadEvent extends NoticeEvent
	{
		public long group_id;
		public GroupFile file;

		public GroupFileUploadEvent setGroupId(long group_id){this.group_id = group_id; return this;}
		public GroupFileUploadEvent setFile(GroupFile file){this.file = file; return this;}

		@Override
		public String getNoticeType(){return "group_upload";}
		@Override
		public String getSubType(){return null;}
		@Override
		public String getMainParameter(){return file == null ? null : file.toString();}
		@Override
		public long getGroupId(){return group_id;}
		@Override
		public long getTargetId(){return group_id;}
		@Override
		public long getOperatorId(){return user_id;}

		public GroupFileUploadEvent(){setasGroupFileUploadEvent();}

		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				obj.put("group_id",group_id);
				if(file != null) obj.put("file",file.toJSONObject());
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}
	public static class GroupAdminEvent extends NoticeEvent
	{
		public long group_id;
		private int sub_type;
		public GroupAdminEvent setGroupId(long group_id){this.group_id = group_id; return this;}

		public GroupAdminEvent setasSetAdminEvent(){sub_type = 0; return this;}
		public GroupAdminEvent setasUnsetAdminEvent(){sub_type = 1; return this;}

		public boolean isSetAdminEvent(){return sub_type == 0;}
		public boolean isUnsetAdminEvent(){return sub_type == 1;}

		@Override
		public String getNoticeType(){return "group_admin";}
		@Override
		public String getSubType()
		{
			if(sub_type == 0) return "set";
			else return "unset";
		}
		@Override
		public String getMainParameter(){return "" + user_id;}
		@Override
		public long getGroupId(){return group_id;}
		@Override
		public long getTargetId(){return user_id;}
		@Override
		public long getOperatorId(){return group_id;}

		public GroupAdminEvent(){setasGroupAdminEvent();}

		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				obj.put("group_id",group_id);
				obj.put("sub_type",getSubType());
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}
	public static class GroupMemberDecreaseEvent extends NoticeEvent
	{
		public long group_id;
		public long operator_id;
		private int sub_type;

		public GroupMemberDecreaseEvent setGroupId(long group_id){this.group_id = group_id; return this;}
		public GroupMemberDecreaseEvent setOperatorId(long operator_id){this.operator_id = operator_id; return this;}
		public GroupMemberDecreaseEvent setasLeaveEvent(){sub_type = 0; return this;}
		public GroupMemberDecreaseEvent setasKickEvent(){sub_type = 1; return this;}
		public GroupMemberDecreaseEvent setasKickMeEvent(){sub_type = 2; return this;}

		public boolean isLeaveEvent(){return sub_type == 0;}
		public boolean isKickEvent(){return sub_type >= 1;}
		public boolean isKickMeEvent(){return sub_type == 2;}

		@Override
		public String getNoticeType(){return "group_decrease";}
		@Override
		public String getSubType()
		{
			if(sub_type == 0) return "leave";
			else if(sub_type == 1) return "kick";
			else return "kick_me";
		}
		@Override
		public String getMainParameter(){return "" + user_id;}
		@Override
		public long getGroupId(){return group_id;}
		@Override
		public long getTargetId(){return isLeaveEvent()?group_id:user_id;}
		@Override
		public long getOperatorId(){return operator_id;}

		public GroupMemberDecreaseEvent(){setasGroupMemberDecreaseEvent();}

		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				obj.put("group_id",group_id);
				obj.put("operator_id",operator_id);
				obj.put("sub_type",getSubType());
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}

	public static class GroupMemberIncreaseEvent extends NoticeEvent
	{
		public long group_id;
		public long operator_id;
		private int sub_type;

		public GroupMemberIncreaseEvent setGroupId(long group_id){this.group_id = group_id; return this;}
		public GroupMemberIncreaseEvent setOperatorId(long operator_id){this.operator_id = operator_id; return this;}
		public GroupMemberIncreaseEvent setasApproveEvent(){sub_type = 0; return this;}
		public GroupMemberIncreaseEvent setasInviteEvent(){sub_type = 1; return this;}


		public boolean isApproveEvent(){return sub_type == 0;}
		public boolean isInviteEvent(){return sub_type == 1;}

		@Override
		public String getNoticeType(){return "group_increase";}
		@Override
		public String getSubType()
		{
			if(sub_type == 0) return "approve";
			else return "invite";
		}
		@Override
		public String getMainParameter(){return "" + user_id;}
		@Override
		public long getGroupId(){return group_id;}
		@Override
		public long getTargetId(){return user_id;}
		@Override
		public long getOperatorId(){return operator_id;}

		public GroupMemberIncreaseEvent(){setasGroupMemberIncreaseEvent();}

		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				obj.put("group_id",group_id);
				obj.put("operator_id",operator_id);
				obj.put("sub_type",getSubType());
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}

	public static class GroupMemberBanEvent extends NoticeEvent
	{
		public long group_id;
		public long operator_id;
		public long duration;
		private int sub_type;

		public GroupMemberBanEvent setGroupId(long group_id){this.group_id = group_id; return this;}
		public GroupMemberBanEvent setOperatorId(long operator_id){this.operator_id = operator_id; return this;}
		public GroupMemberBanEvent setDuration(long duration){this.duration = duration; return this;}
		public GroupMemberBanEvent setasSetBanEvent(){sub_type = 0; return this;}
		public GroupMemberBanEvent setasLiftBanEvent(){sub_type = 1; return this;}


		public boolean isSetBanEvent(){return sub_type == 0;}
		public boolean isLiftBanEvent(){return sub_type == 1;}

		@Override
		public String getNoticeType(){return "group_ban";}
		@Override
		public String getSubType()
		{
			if(sub_type == 0) return "ban";
			else return "lift_ban";
		}
		@Override
		public String getMainParameter(){return "" + duration;}
		@Override
		public long getGroupId(){return group_id;}
		@Override
		public long getTargetId(){return user_id;}
		@Override
		public long getOperatorId(){return operator_id;}

		public GroupMemberBanEvent(){setasGroupMemberBanEvent();}

		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				obj.put("group_id",group_id);
				obj.put("operator_id",operator_id);
				obj.put("sub_type",getSubType());
				obj.put("duration",duration);
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}
	public static class GroupMessageRecallEvent extends NoticeEvent
	{
		public long group_id;
		public long operator_id;
		public long message_id;

		public GroupMessageRecallEvent setGroupId(long group_id){this.group_id = group_id; return this;}
		public GroupMessageRecallEvent setOperatorId(long operator_id){this.operator_id = operator_id; return this;}
		public GroupMessageRecallEvent setMessageId(long message_id){this.message_id = message_id; return this;}

		@Override
		public String getNoticeType(){return "group_recall";}
		@Override
		public String getSubType(){return null;}
		@Override
		public String getMainParameter(){return "" + message_id;}
		@Override
		public long getGroupId(){return group_id;}
		@Override
		public long getTargetId(){return message_id;}
		@Override
		public long getOperatorId(){return operator_id;}

		public GroupMessageRecallEvent(){setasGroupMessageRecallEvent();}

		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				obj.put("group_id",group_id);
				obj.put("operator_id",operator_id);
				obj.put("message_id",message_id);
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}

	public static class GroupInteractiveEvent extends NoticeEvent
	{
		public long group_id;
		public long target_id;
		private int sub_type;
		private int honor_type;

		public GroupInteractiveEvent setGroupId(long group_id){this.group_id = group_id; return this;}
		public GroupInteractiveEvent setTargetId(long target_id)
		{
			this.target_id = target_id;
			if(isHonorEvent()) user_id  = target_id; 
			return this;
		}

		public GroupInteractiveEvent setasPokeEvent(){sub_type = 0; return this;}
		public GroupInteractiveEvent setasLuckyKingEvent(){sub_type = 1; return this;}
		public GroupInteractiveEvent setasLongwangEvent(){sub_type = 2; honor_type = 0; return this;}
		public GroupInteractiveEvent setasQunliaozhihuoEvent(){sub_type = 2; honor_type = 1; return this;}
		public GroupInteractiveEvent setasKuaileyuanquanEvent(){sub_type = 2; honor_type = 2; return this;}

		public boolean isPokeEvent(){return sub_type == 0;}
		public boolean isLuckyKingEvent(){return sub_type == 1;}
		public boolean isHonorEvent(){return sub_type == 2;}
		public boolean isLongwangEvent(){return sub_type == 2 && honor_type == 0;}
		public boolean isQunliaozhihuoEvent(){return sub_type == 2 && honor_type == 1;}
		public boolean isKuaileyuanquanEvent(){return sub_type == 2 && honor_type == 2;}
		public String getHonorType()
		{
			if(sub_type != 2) return null;
			else if(sub_type == 0) return "talkative";
			else if(sub_type == 1) return "performer";
			else return "emotion";
		}

		@Override
		public String getNoticeType(){return "notify";}
		@Override
		public String getSubType()
		{
			if(sub_type == 0) return "poke";
			else if(sub_type == 1) return "lucky_king";
			else return "honor";
		}
		@Override
		public String getMainParameter(){return "" + getTargetId();}
		@Override
		public long getGroupId(){return group_id;}
		@Override
		public long getTargetId()
		{
			if(isHonorEvent()) return user_id;
			else return target_id;
		}
		@Override
		public long getOperatorId(){return user_id;}

		public GroupInteractiveEvent(){setasGroupInteractiveEvent();}

		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				obj.put("group_id",group_id);
				obj.put("sub_type",getSubType());
				if(isHonorEvent())obj.put("honor_type",getHonorType());
				else obj.put("target_id",target_id);
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}
	public static class FriendAddEvent extends NoticeEvent
	{
		@Override
		public String getNoticeType(){return "friend_add";}
		@Override
		public String getSubType(){return null;}
		@Override
		public String getMainParameter(){return "" + user_id;}
		@Override
		public long getGroupId(){return 0;}
		@Override
		public long getTargetId(){return user_id;}
		@Override
		public long getOperatorId(){return self_id;}

		public FriendAddEvent(){setasFriendAddEvent();}
	}
	public static class FriendMessageRecallEvent extends NoticeEvent
	{
		public long message_id;
		public FriendMessageRecallEvent setMessageId(long message_id){this.message_id = message_id;return this;}

		@Override
		public String getNoticeType(){return "friend_recall";}
		@Override
		public String getSubType(){return null;}
		@Override
		public String getMainParameter(){return "" + message_id;}
		@Override
		public long getGroupId(){return 0;}
		@Override
		public long getTargetId(){return message_id;}
		@Override
		public long getOperatorId(){return user_id;}

		public FriendMessageRecallEvent(){setasFriendMessageRecallEvent();}

		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				obj.put("message_id",message_id);
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}
	public static class RequestEvent extends AsftOneBotEvent
	{
		private int request_type;
		private int sub_type;
		public long user_id;
		public long group_id;
		public String comment;
		public String flag;

		public RequestEvent setUserId(long user_id){this.user_id = user_id; return this;}
		public RequestEvent setGroupId(long group_id){this.group_id = group_id; return this;}
		public RequestEvent setComment(String comment){this.comment = comment; return this;}
		public RequestEvent setFlag(String flag){this.flag = flag; return this;}

		public RequestEvent setasFriendAddRequestEvent(){request_type = 0; return this;}
		public RequestEvent setasGroupAddRequestEvent()
		{
			request_type = 1; 
			sub_type = 0;
			return this;
		}
		public RequestEvent setasGroupInviteRequestEvent()
		{
			request_type = 1; 
			sub_type = 1;
			return this;
		}

		public boolean isFriendAddRequestEvent() {return request_type == 0;}
		public boolean isGroupAddRequestEvent() {return request_type == 1 && sub_type == 0;}
		public boolean isGroupInviteRequestEvent() {return request_type == 1 && sub_type == 1;}
		
		public String getRequestType()
		{
			if(request_type == 0) return "friend";
			else return "group";
		}
		public String getComment(){return comment;}
		public String getFlag(){return flag;}

		@Override
		public String getSecondaryType(){return getRequestType();}
		@Override
		public boolean isGroupEvent(){return request_type == 1;}
		@Override
		public boolean isPrivateEvent(){return request_type == 0;}
		@Override
		public long getUserId(){return user_id;}
		@Override
		public long getGroupId(){return isGroupEvent() ? group_id : 0;}
		@Override
		public long getTargetId()
		{
			if(request_type == 0) return self_id;
			else if(sub_type == 0) return group_id;
			else return self_id;
		}
		@Override
		public long getOperatorId(){return user_id;}
		@Override
		public String getMainParameter(){return flag;}
		@Override
		public String getSubType()
		{
			if(request_type == 0) return null;
			else if(sub_type == 0) return "add";
			else return "invite";
		}


		public RequestEvent(){setasRequestEvent();}

		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				obj.put("request_type",getRequestType());
				obj.put("sub_type",getSubType());
				obj.put("user_id",user_id);
				if(isGroupEvent()) obj.put("group_id",group_id);
				if(comment != null)obj.put("comment",comment);
				if(flag != null)obj.put("flag",flag);
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}

	public static class LifeCycleEvent extends AsftOneBotEvent
	{
		@Override
		public String getSecondaryType(){return "lifecycle";}
		@Override
		public boolean isGroupEvent(){return false;}
		@Override
		public boolean isPrivateEvent(){return false;}
		@Override
		public long getUserId(){return self_id;}
		@Override
		public long getGroupId(){return 0;}
		@Override
		public long getOperatorId(){return self_id;}
		@Override
		public long getTargetId(){return self_id;}
		@Override
		public String getMainParameter(){return "connect";}
		@Override
		public String getSubType(){return "connect";}

		public LifeCycleEvent(){setasMetaEvent();}
		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				obj.put("meta_event_type",getSecondaryType());
				obj.put("sub_type",getSubType());
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}

	public static class HeartBeatEvent extends AsftOneBotEvent
	{
		public String status;
		public long interval;

		public HeartBeatEvent setStatus(String status){this.status = status; return this;}
		public HeartBeatEvent setInterval(long interval){this.interval = interval; return this;}

		@Override
		public String getSecondaryType(){return "heartbeat";}
		@Override
		public boolean isGroupEvent(){return false;}
		@Override
		public boolean isPrivateEvent(){return false;}
		@Override
		public long getUserId(){return self_id;}
		@Override
		public long getGroupId(){return 0;}
		@Override
		public long getOperatorId(){return self_id;}
		@Override
		public long getTargetId(){return self_id;}
		@Override
		public String getMainParameter(){return "" + interval;}
		@Override
		public String getSubType(){return null;}

		public HeartBeatEvent(){setasMetaEvent();}

		@Override
		public JSONObject toJSONObject()
		{
			JSONObject obj = super.toJSONObject();
			try
			{
				obj.put("meta_event_type",getSecondaryType());
				obj.put("interval",interval);
				if(status != null) obj.put("status",status);
			}
			catch(JSONException e)
			{
				//System.out.println(e);
			}
			return obj;
		}
	}


	public static AsftOneBotEvent createFromJsonString(String jstr)
	{
		if(jstr == null) return null;
		try
		{
			return createFromJSONObject(new JSONObject(jstr));
		}
		catch(JSONException | NullPointerException e)
		{
			//System.out.println(e);
			return null;
		}
	}
	private static MessageEvent createMessageEventFromJSONObject(JSONObject obj) throws JSONException,NullPointerException
	{
		String message_type = obj.optString("message_type");
		JSONObject jsd = obj.optJSONObject("sender");
		MessageEvent evt = null;
		Sender sender = null;
		if(message_type.equals("private")) 
		{
			PrivateMessageEvent pevt = new PrivateMessageEvent();
			switch(obj.optString("sub_type"))
			{
			case "friend": pevt.setasFriendMessageEvent(); break;
			case "group":pevt.setasGroupTempMessageEvent(); break;
			case "other":default: pevt.setasOtherMessageEvent();break;
			}
			pevt.setMessageId(obj.optInt("message_id"))
			.setUserId(obj.optLong("user_id"))
			.setMessage(obj.optString("message"))
			.setRawMessage(obj.optString("raw_message"))
			.setFont(obj.optInt("font"));
			evt = pevt;
		}
		else if(message_type.equals("group")) 
		{
			GroupMessageEvent gevt = new GroupMessageEvent();
			switch(obj.optString("sub_type"))
			{
			case "normal": gevt.setasNormalMessageEvent(); break;
			case "anonymous":
			{
				Anonymous anon = new Anonymous();
				JSONObject janon = obj.optJSONObject("anonymous");
				if(janon != null)
				{
					anon.setId(janon.optLong("id"))
					.setName(janon.optString("name"))
					.setFlag(janon.optString("flag"));
				}
				gevt.setasAnonymousMessageEvent(anon); 
				break;
			}
			case "notice":default: gevt.setasNoticeMessageEvent();break;
			}
			if(jsd != null)
			{
				sender = new GroupSender();
				((GroupSender)sender).setCard(obj.optString("card"))
				.setArea(jsd.optString("area"))
				.setLevel(jsd.optString("level"))
				.setTitle(jsd.optString("title"));
				String stmp = jsd.optString("role", null);
				if(stmp != null && stmp.equals("owner")) ((GroupSender)sender).setasOwner();
				else if(stmp != null && stmp.equals("admin")) ((GroupSender)sender).setasAdmin();
				else ((GroupSender)sender).setasMember();
			}
			gevt.setGroupId(obj.optLong("group_id"))
			.setMessageId(obj.optInt("message_id"))
			.setUserId(obj.optLong("user_id"))
			.setMessage(obj.optString("message"))
			.setRawMessage(obj.optString("raw_message"))
			.setFont(obj.optInt("font"));
			evt = gevt;
		}
		if(jsd!= null)
		{
			if(sender == null) sender = new Sender();	
			sender.setUserId(jsd.optLong("user_id"))
			.setAge(jsd.optInt("age"))
			.setNickName(jsd.optString("nickname"));
			String stmp = jsd.optString("sex", null);
			if(stmp!= null && stmp.equals("male")) sender.setasMale();
			else if(stmp!= null && stmp.equals("female")) sender.setasFemale();
			else if(stmp!= null && stmp.equals("unknow")) sender.setasUnknownSex();
		}
		evt.setSender(sender)
		.setTime(obj.optLong("time"))
		.setSelfId(obj.optLong("self_id"));
		return evt;
	}
	private static NoticeEvent createNoticeEventFromJSONObject(JSONObject obj) throws JSONException,NullPointerException
	{
		String message_type = obj.optString("notice_type");
		NoticeEvent evt = null;
		switch(message_type)
		{
		case "group_upload":
		{
			GroupFileUploadEvent gevt = new GroupFileUploadEvent();
			GroupFile file = new GroupFile();
			JSONObject jfile = obj.optJSONObject("file");
			if(jfile != null)
			{
				file.setId(jfile.optString("id"))
				.setName(jfile.optString("name"))
				.setSize(jfile.optLong("size"))
				.setBusId(jfile.optLong("busid"));
			}
			gevt.setFile(file)
			.setGroupId(obj.optLong("group_id"))
			.setUserId(obj.optLong("user_id"));
			evt = gevt;
			break;
		}
		case "group_admin":
		{
			GroupAdminEvent gevt = new GroupAdminEvent();
			gevt.setGroupId(obj.optLong("group_id"))
			.setUserId(obj.optLong("user_id"));
			String stmp = obj.optString("sub_type", null);
			if(stmp != null && stmp.equals("set")) gevt.setasSetAdminEvent();
			else if(stmp != null && stmp.equals("unset")) gevt.setasUnsetAdminEvent();
			evt = gevt;
			break;
		}
		case "group_decrease":
		{
			GroupMemberDecreaseEvent gevt = new GroupMemberDecreaseEvent();
			gevt.setOperatorId(obj.optLong("operator_id"))
			.setGroupId(obj.optLong("group_id"))
			.setUserId(obj.optLong("user_id"));
			String stmp = obj.optString("sub_type", null);
			if(stmp != null && stmp.equals("leave")) gevt.setasLeaveEvent();
			else if(stmp != null && stmp.equals("kick")) gevt.setasKickEvent();
			else if(stmp != null && stmp.equals("kick_me")) gevt.setasKickMeEvent();
			evt = gevt;
			break;
		}
		case "group_increase":
		{
			GroupMemberIncreaseEvent gevt = new GroupMemberIncreaseEvent();
			gevt.setOperatorId(obj.optLong("operator_id"))
			.setGroupId(obj.optLong("group_id"))
			.setUserId(obj.optLong("user_id"));
			String stmp = obj.optString("sub_type", null);
			if(stmp != null && stmp.equals("approve")) gevt.setasApproveEvent();
			else if(stmp != null && stmp.equals("invite")) gevt.setasInviteEvent();
			evt = gevt;
			break;
		}
		case "group_ban":
		{
			GroupMemberBanEvent gevt = new GroupMemberBanEvent();
			gevt.setOperatorId(obj.optLong("operator_id"))
			.setDuration(obj.optLong("duration"))
			.setGroupId(obj.optLong("group_id"))
			.setUserId(obj.optLong("user_id"));
			String stmp = obj.optString("sub_type", null);
			if(stmp != null && stmp.equals("ban")) gevt.setasSetBanEvent();
			else if(stmp != null && stmp.equals("lift_ban")) gevt.setasLiftBanEvent();
			evt = gevt;
			break;
		}
		case "friend_add":
		{
			FriendAddEvent fevt = new FriendAddEvent();
			fevt.setUserId(obj.optLong("user_id"));
			evt = fevt;
			break;
		}
		case "group_recall":
		{
			GroupMessageRecallEvent gevt = new GroupMessageRecallEvent();
			gevt.setOperatorId(obj.optLong("operator_id"))
			.setMessageId(obj.optLong("message_id"))
			.setGroupId(obj.optLong("group_id"))
			.setUserId(obj.optLong("user_id"));
			evt = gevt;
			break;
		}
		case "friend_recall":
		{
			FriendMessageRecallEvent fevt = new FriendMessageRecallEvent();
			fevt.setMessageId(obj.optLong("message_id"))
			.setUserId(obj.optLong("user_id"));
			evt = fevt;
			break;
		}
		case "notify":
		{
			GroupInteractiveEvent gevt = new GroupInteractiveEvent();
			gevt.setGroupId(obj.optLong("group_id"))
			.setUserId(obj.optLong("user_id"));
			String stmp = obj.optString("sub_type", null);
			if(stmp != null && stmp.equals("poke")) gevt.setasPokeEvent().setTargetId(obj.optLong("target_id"));
			else if(stmp != null && stmp.equals("lucky_king")) gevt.setasLuckyKingEvent().setTargetId(obj.optLong("target_id"));
			else if(stmp != null && stmp.equals("honor"))
			{
				stmp = obj.optString("honor_type", null);
				if(stmp != null && stmp.equals("talkative")) gevt.setasLongwangEvent();
				else if(stmp != null && stmp.equals("performer")) gevt.setasQunliaozhihuoEvent();
				else if(stmp != null && stmp.equals("emotion")) gevt.setasKuaileyuanquanEvent();
			}
			evt = gevt;
			break;
		}
		default:
			break;
		}

		evt.setTime(obj.optLong("time"))
		.setSelfId(obj.optLong("self_id"));
		return evt;
	}
	private static RequestEvent createRequestEventFromJSONObject(JSONObject obj) throws JSONException,NullPointerException
	{
		String request_type = obj.optString("request_type");
		RequestEvent evt = new RequestEvent();
		if(request_type.equals("friend")) 
		{
			evt.setasFriendAddRequestEvent();
		}
		else if(request_type.equals("group")) 
		{
			String stmp = obj.optString("sub_type", null);
			if(stmp != null && stmp.equals("add")) evt.setasGroupAddRequestEvent();
			else if(stmp != null && stmp.equals("invite")) evt.setasGroupInviteRequestEvent();
		}
		evt.setComment(obj.optString("comment"))
		.setFlag(obj.optString("flag"))
		.setTime(obj.optLong("time"))
		.setSelfId(obj.optLong("self_id"));
		return evt;
	}
	private static AsftOneBotEvent createMetaEventFromJSONObject(JSONObject obj) throws JSONException,NullPointerException
	{
		String meta_event_type = obj.optString("meta_event_type");
		AsftOneBotEvent evt = null;
		if(meta_event_type.equals("lifecycle"))
		{
			evt = new LifeCycleEvent();
		}
		else if(meta_event_type.equals("heartbeat"))
		{
			evt = new HeartBeatEvent();
			((HeartBeatEvent)evt).setInterval(obj.optLong("interval"))
			.setStatus(obj.optString("status"));
		}
		evt.setTime(obj.optLong("time"))
		.setSelfId(obj.optLong("self_id"));
		return evt;
	}
	public static AsftOneBotEvent createFromJSONObject(JSONObject obj)
	{
		if(obj == null) return null;
		try
		{
			String post_type = obj.optString("post_type");
			switch (post_type)
			{
			case "message": return createMessageEventFromJSONObject(obj);
			case "notice":	return createNoticeEventFromJSONObject(obj);	
			case "request": return createRequestEventFromJSONObject(obj);	
			case "meta":	return createMetaEventFromJSONObject(obj);	
			default: return null;
			}
		}
		catch(JSONException | NullPointerException e)
		{
			//System.out.println(e);
			return null;
		}
	}
}