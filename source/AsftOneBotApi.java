import org.json.*;

public final class AsftOneBotApi
{
	private AsftOneBotApi(){}
	public static boolean AutoSetAsync = false;
	public static boolean AutoSetRateLimited = false;

	public static boolean isApiReturn(String event)
	{
		try
		{
			JSONObject obj = new JSONObject(event);
			if(obj.optString("status", null) == null) return false;
			if(obj.optString("retcode", null) == null) return false;
			return true;
		}
		catch(JSONException e)
		{
			return false;
		}
	}
	public static boolean isApiReturn(JSONObject eventobj)
	{
		if(eventobj == null) return false;
		if(eventobj.optString("status", null) == null) return false;
		if(eventobj.optString("retcode", null) == null) return false;
		return true;
	}
	public static ApiRequest SendPrivateMessage_text(long user_id,String text)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("user_id",user_id);
			obj.put("message",text);
			obj.put("auto_escape",false);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("send_private_msg").setAllParameters(obj);
	}
	public static ApiRequest SendPrivateMessage_cqstr(long user_id,String cqstr)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("user_id",user_id);
			obj.put("message",cqstr);
			obj.put("auto_escape",true);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("send_private_msg").setAllParameters(obj);
	}
	public static ApiRequest SendPrivateMessage_jsarr(long user_id,String jsarr)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("user_id",user_id);
			obj.put("message",new JSONArray(jsarr));
			obj.put("auto_escape",false);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("send_private_msg").setAllParameters(obj);
	}
	public static ApiRequest SendPrivateMessage_jsarr(long user_id,JSONArray jsarr)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("user_id",user_id);
			obj.put("message",jsarr);
			obj.put("auto_escape",false);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("send_private_msg").setAllParameters(obj);
	}
	public static ApiRequest SendGroupMessage_text(long group_id,String text)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("group_id",group_id);
			obj.put("message",text);
			obj.put("auto_escape",false);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("send_group_msg").setAllParameters(obj);
	}
	public static ApiRequest SendGroupMessage_cqstr(long group_id,String cqstr)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("group_id",group_id);
			obj.put("message",cqstr);
			obj.put("auto_escape",true);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("send_group_msg").setAllParameters(obj);
	}
	public static ApiRequest SendGroupMessage_jsarr(long group_id,String jsarr)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("group_id",group_id);
			obj.put("message",new JSONArray(jsarr));
			obj.put("auto_escape",false);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("send_group_msg").setAllParameters(obj);
	}
	public static ApiRequest SendGroupMessage_jsarr(long group_id,JSONArray jsarr)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("group_id",group_id);
			obj.put("message",jsarr);
			obj.put("auto_escape",false);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("send_group_msg").setAllParameters(obj);
	}

	public static ApiRequest DeleteMessage(long message_id)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("message_id",message_id);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("delete_msg").setAllParameters(obj);
	}

	public static ApiRequest SendLike(long user_id){return SendLike(user_id,1); }
	public static ApiRequest SendLike(long user_id,int times)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("user_id",user_id);
			obj.put("times",times);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("send_like").setAllParameters(obj);
	}

	public static ApiRequest SetGroupKick(long group_id, long user_id){return SetGroupKick(group_id, user_id);}
	public static ApiRequest SetGroupKick(long group_id, long user_id, boolean reject_add_request)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("group_id",group_id);
			obj.put("user_id",user_id);
			obj.put("reject_add_request",reject_add_request);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("set_group_kick").setAllParameters(obj);
	}
	public static ApiRequest SetGroupBan_unset(long group_id, long user_id){return SetGroupBan(group_id,user_id,0);}
	public static ApiRequest SetGroupBan(long group_id, long user_id, long duration)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("group_id",group_id);
			obj.put("user_id",user_id);
			obj.put("duration",duration);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("set_group_ban").setAllParameters(obj);
	}

	
	public static ApiRequest SetGroupAnonymousBan(long group_id, String flag, long duration)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("group_id",group_id);
			obj.put("flag",flag);
			obj.put("anonymous_flag",flag);
			obj.put("duration",duration);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("set_group_anonymous_ban").setAllParameters(obj);
	}

	public static ApiRequest SetGroupWholeBan(long group_id) {return SetGroupWholeBan(group_id,true);}
	public static ApiRequest SetGroupWholeBan_unset(long group_id){return SetGroupWholeBan(group_id,false);}
	public static ApiRequest SetGroupWholeBan(long group_id, boolean enable)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("group_id",group_id);
			obj.put("enable",enable);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("set_group_whole_ban").setAllParameters(obj);
	}
	
	public static ApiRequest SetGroupAdmin(long group_id, long user_id) {return SetGroupAdmin(group_id,user_id,true);}
	public static ApiRequest SetGroupAdmin_unset(long group_id, long user_id) {return SetGroupAdmin(group_id,user_id,false);}
	public static ApiRequest SetGroupAdmin(long group_id, long user_id, boolean enable)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("group_id",group_id);
			obj.put("user_id",user_id);
			obj.put("enable",enable);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("set_group_admin").setAllParameters(obj);
	}


	public static ApiRequest SetGroupAnonymous(long group_id){return SetGroupAnonymous(group_id,true);}
	public static ApiRequest SetGroupAnonymous_unset(long group_id) {return SetGroupAnonymous(group_id,false);}
	public static ApiRequest SetGroupAnonymous(long group_id, boolean enable)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("group_id",group_id);
			obj.put("enable",enable);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("set_group_anonymous").setAllParameters(obj);
	}

	public static ApiRequest SetGroupCard(long group_id, long user_id, String card)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("group_id",group_id);
			obj.put("user_id",user_id);
			obj.put("card",card == null ? "" : card);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("set_group_card").setAllParameters(obj);
	}

	public static ApiRequest SetGroupName(long group_id, String name)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("group_id",group_id);
			obj.put("name",name);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("set_group_name").setAllParameters(obj);
	}

	public static ApiRequest SetGroupLeave(long group_id)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("group_id",group_id);
			obj.put("is_dismiss",false);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("set_group_leave").setAllParameters(obj);
	}

	public static ApiRequest SetGroupSpecialTitle(long group_id,long user_id,String special_title)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("group_id",group_id);
			obj.put("user_id",user_id);
			obj.put("special_title",special_title == null ? "" : special_title);
			obj.put("duration",-1);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("set_group_special_title").setAllParameters(obj);
	}

	public static ApiRequest SetFriendAddRequest(String flag, boolean approve){return SetFriendAddRequest(flag,approve,null);}
	public static ApiRequest SetFriendAddRequest(String flag, boolean approve,String remark)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("flag",flag);
			obj.put("approve",approve);
			obj.put("remark",remark == null? "" : remark);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("set_friend_add_request").setAllParameters(obj);
	}

	public static ApiRequest SetGroupAddRequest_add(String flag, boolean approve)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("flag",flag);
			obj.put("approve",approve);
			obj.put("sub_type","add");
			obj.put("type","add");
			obj.put("reason","");
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("set_group_add_request").setAllParameters(obj);
	}
	public static ApiRequest SetGroupAddRequest_invite(String flag, boolean approve)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("flag",flag);
			obj.put("approve",approve);
			obj.put("sub_type","invite");
			obj.put("type","invite");
			obj.put("reason","");
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("set_group_add_request").setAllParameters(obj);
	}

	public static ApiRequest GetMessage(long message_id)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("message_id",message_id);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("get_msg").setAllParameters(obj);
	}

	public static ApiRequest GetForwardMessage(String id)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("id",id);
		}
		catch(NullPointerException|JSONException e)
		{
			//System.out.println(e);
			return null;
		}
		return new ApiRequest("get_forward_msg").setAllParameters(obj);
	}


	public static final class ApiRequest
	{
		private JSONObject parameters;
		private String action;
		private boolean async;
		private boolean rate_limited;
		private String echo;
		public ApiRequest()
		{
			if(AutoSetAsync) setAsync();
			if(AutoSetRateLimited) setRateLimited();
		}
		public ApiRequest(String action)
		{
			if(AutoSetAsync) setAsync();
			if(AutoSetRateLimited) setRateLimited();
			this.setAction(action);
		}
		public ApiRequest setAllParameters(String json){this.parameters = new JSONObject(json); return this;}
		public ApiRequest setAllParameters(JSONObject obj){this.parameters = obj; return this;}
		public ApiRequest setParameter(String key, String value)
		{
			if(parameters == null) parameters = new JSONObject();
			try
			{
				parameters.put(key, value);
			}
			catch(NullPointerException|JSONException e)
			{
				//System.out.println(e);
			}
			return this;
		}
		public ApiRequest setAction(String action){this.action = action; return this;}
		public ApiRequest setAsync(){async = true; return this;}
		public ApiRequest unsetAsync(){async = false; return this;}
		public ApiRequest setRateLimited(){rate_limited = true; return this;}
		public ApiRequest unsetRateLimited(){rate_limited = false; return this;}
		public ApiRequest SetEcho(String echo){this.echo = echo; return this;}
		@Override
		public String toString()
		{
			StringBuilder stb = new StringBuilder(128);
			stb.append("{\n\t\"action\":\s\"").append(action);
			if(async) stb.append("_async");
			if(rate_limited) stb.append("_rate_limited");
			stb.append("\",\n\t\"params\":\s").append(parameters != null ? parameters.toString() : "null");
			if(echo != null)
			{
				stb.append(",\n\t\"echo\":\s\"").append(echo).append("\"");
			}	
			stb.append("\n}");
			return stb.toString();
		}

		public boolean send(AwpBotInterface bot)
		{
			return AwpBotInterface.sendMessage(bot.getApiWs(),this.toString());
		}
	}
	public static class ApiReturn
	{
		protected String status;
		protected int retcode;
		protected JSONObject data;
		protected String echo;
		protected ApiReturn(){}
		public ApiReturn(String json)
		{
			try
			{
				JSONObject obj = new JSONObject(json);
				status = obj.optString("status", null);
				retcode = obj.optInt("retcode");
				data = obj.optJSONObject("data");
				echo = obj.optString("echo", null);
			}
			catch(JSONException e)
			{
				return ;
			}
		}
		public ApiReturn(JSONObject jsonobj)
		{
			if(jsonobj == null) return;
			status = jsonobj.optString("status", null);
			retcode = jsonobj.optInt("retcode");
			data = jsonobj.optJSONObject("data");
			echo = jsonobj.optString("echo", null);
		}
		public String getData()
		{
			return data == null ? null : data.toString();
		}
		public String getDataValue(String key)
		{
			return data == null ? null : data.optString(key);
		}
		public String getStatus()
		{
			return status;
		}
		public int getRetcode(String key)
		{
			return retcode;
		}
		public String getEcho()
		{
			return echo;
		}
		@Override
		public String toString()
		{
			StringBuilder stb = new StringBuilder(128);
			stb.append("{\n\t\"status\":\s\"").append(status);
			stb.append("\",\n\t\"retcode\":\s").append(retcode);
			stb.append(",\n\t\"data\":\s").append(data != null ? data.toString() : "null");
			if(echo != null)
			{
				stb.append(",\n\t\"echo\":\s\"").append(echo).append("\"");
			}	
			stb.append("\n}");
			return stb.toString();
		}
	}
}