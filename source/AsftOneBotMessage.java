import org.json.*;
import java.util.*;

public final class AsftOneBotMessage
{
	//factory function
	public static boolean paraIsTrue(String str)
	{
		if(str == null) return false;
		try
		{
			return Integer.parseInt(str) != 0;
		}
		catch (Exception e)
		{
			switch(str)
			{		
			case "false": case "FALSE": case "False":
			case "F": case "f":
			case "N": case "n": case "No": case "no":
			case "假": case "错":
				return false; //break;

			default:
			case "true": case "TRUE": case "True":
			case "T": case "t":
			case "Y": case "y": case "Yes": case "yes":
			case "真": case "对":
				return true; //break
			}
		}
	}

	//member
	private JSONArray MessageElements;

	//create functions
	private void create_jsonMode(String msgjs)
	{
		try
		{
			MessageElements = new JSONArray(msgjs);
		}
		catch(JSONException e1)
		{
			MessageElements = new JSONArray();
			try
			{
				JSONObject obj = new JSONObject(msgjs);
				MessageElements.put(obj);
			}
			catch(JSONException e2)
			{
				//System.out.println(e2);
			}
		}
	}
	private void create_cqMode(String cqstr)
	{
		
		MessageElements = new JSONArray();
		String remain = cqstr;
		while(remain != null)
		{
			String[] part = remain.split("\\[CQ:",2);	
			if(part[0].length() != 0) 
			{	
				try
				{
					JSONObject data = new JSONObject();
					data.put("text",part[0].replaceAll("&#91;","[").replaceAll("&#93;","]").replaceAll("&amp;","&"));
					JSONObject obj = new JSONObject();
					obj.put("type","text");
					obj.put("data",data);
					MessageElements.put(obj);
				}
				catch(JSONException e)
				{
					//System.out.println(e);
				}
			}
			if(part.length == 1) //no [CQ:
			{
				remain = null;
			}
			else //has [CQ:
			{
				part = part[1].split("\\]",2); 
				//part[0] is CQCode without CQ:
				String[] cq = part[0].split(",");
				if(cq[0].length() != 0)
				{
					try
					{
						JSONObject data = new JSONObject();
						//Put data
						for(int i = 1;i < cq.length; i++)
						{
							String[] atr = cq[i].split("=",2);
							if(atr[0].trim().length() == 0) continue;
							if(atr.length == 1) 
							{
								data.put(atr[0].trim(),"");
							}
							else
							{
								data.put(atr[0].trim(),
									atr[1].replaceAll("&#91;","[").replaceAll("&#93;","]").replaceAll("&#44;",",").replaceAll("&amp;","&")
								);
							}
						}
						JSONObject obj = new JSONObject();
						obj.put("type",cq[0]);
						obj.put("data",data);
						MessageElements.put(obj);
					}
					catch(JSONException e)
					{
						//System.out.println(e);
					}
				}
				if(part.length == 1) //no remain
				{
					remain = null;
				}
				else //has remain
				{
					remain = part[1];
				}
			}
		}
	}
	private void create_strOnly(String str) 
	{
		MessageElements = new JSONArray();
		if(str == null || str.length() == 0) return;
		try
		{
			JSONObject data = new JSONObject();
			data.put("text",str);
			JSONObject obj = new JSONObject();
			obj.put("type","text");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
		}
	}
	private AsftOneBotMessage(){}
	public AsftOneBotMessage(AsftOneBotMessage src)
	{
		MessageElements = new JSONArray(src.MessageElements);
	}

	public static AsftOneBotMessage createFromText(String text)
	{
		AsftOneBotMessage temp = new AsftOneBotMessage();
		temp.create_strOnly(text);
		return temp;
	}
	public static AsftOneBotMessage createFromCqString(String cqstr)
	{
		AsftOneBotMessage temp = new AsftOneBotMessage();
		temp.create_cqMode(cqstr);
		return temp;
	}
	public static AsftOneBotMessage createFromJsonString(String js)
	{
		AsftOneBotMessage temp = new AsftOneBotMessage();
		temp.create_jsonMode(js);
		return temp;
	}
	public static AsftOneBotMessage createEmpty()
	{
		return createFromText(null);
	}

	//to str functions
	public String toString()
	{
		StringBuilder strb = new StringBuilder(0x40);
		for(Object elem : MessageElements)
		{
			JSONObject obj = (JSONObject)elem; 
			String stmp = obj.optString("type");
			if(stmp == null) continue;
			if(stmp.equals("text"))
			{
				obj = obj.optJSONObject("data");
				if(obj == null) continue;
				stmp = obj.optString("text");
				if(stmp == null) continue;
				strb.append(stmp);
			}
			else
			{
				strb.append("[");
				strb.append(stmp);
				strb.append("]");
			}
		}
		return strb.toString();
	}
	public String toCqString()
	{
		StringBuilder strb = new StringBuilder(0x40);
		for(Object elem : MessageElements)
		{
			JSONObject obj = (JSONObject)elem; 
			String stmp = obj.optString("type");
			if(stmp == null) continue;
			if(stmp.equals("text"))
			{
				obj = obj.optJSONObject("data");
				if(obj == null) continue;
				stmp = obj.optString("text");
				if(stmp == null) continue;
				strb.append(stmp.replaceAll("&","&amp;").replaceAll("\\[","&#91;").replaceAll("\\]","&#93;"));
			}
			else
			{
				strb.append("[CQ:");
				strb.append(stmp);
				strb.append(",");
				obj = obj.optJSONObject("data");
				if(obj != null)
				{
					Set<String> keys = (Set<String>)obj.keySet();
					for(String key :keys)
					{
						strb.append(key);
						strb.append("=");
						stmp = obj.optString(key);
						if(stmp != null)
						{
							strb.append(stmp.replaceAll("&","&amp;").replaceAll("\\[","&#91;").
										replaceAll("\\]","&#93;").replaceAll(",","&#44;")); 
						}
						strb.append(",");
					}
				}
				strb.delete(strb.length() - 1,strb.length()); //remove last comma
				strb.append("]");
			}
		}
		return strb.toString();
	}
	public String toJsonArrayString()
	{
		return MessageElements.toString();
	}
	public JSONArray toJSONArray()
	{
		return new JSONArray(MessageElements);
	}
	//append functions

	//merge
	public boolean appendAsftOneBotMessage(AsftOneBotMessage secodary)
	{
		if(secodary == null) return false;
		for(Object elem : secodary.MessageElements)
		{
			JSONObject obj = (JSONObject)elem; 
			MessageElements.put(obj);
		}
		return true;
	}
	//cq string
	public boolean appendCqString(String cqstr)
	{
		return appendAsftOneBotMessage(AsftOneBotMessage.createFromCqString(cqstr));
	}
	//json string
	public boolean appendJsonString(String jstr)
	{
		return appendAsftOneBotMessage(AsftOneBotMessage.createFromJsonString(jstr));
	}
	public boolean appendJsonArrayString(String jstr)
	{
		return appendAsftOneBotMessage(AsftOneBotMessage.createFromJsonString(jstr));
	}
	//json object
	public boolean appendJsonObject(JSONObject obj)
	{
		if(obj == null) return false;
		MessageElements.put(obj);
		return true;
	}
	//text
	public boolean appendText(String text)
	{
		if(text == null || text.length() == 0) return false;
		try
		{
			JSONObject data = new JSONObject();
			data.put("text",text);
			JSONObject obj = new JSONObject();
			obj.put("type","text");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}

	//face 
	public boolean appendFace(String id)
	{
		if(id == null || id.length() == 0) return false;
		return appendFace(AsftInt.parseInt(id));
	}
	public boolean appendFace(int id)
	{
		if(id < 0) return false; //the sup limit unknow
		try
		{
			JSONObject data = new JSONObject();
			data.put("id",String.valueOf(id));
			JSONObject obj = new JSONObject();
			obj.put("type","face");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}

	//picture send/receive
	public boolean appendPicture(String file,String url,int timeout,boolean cache,boolean proxy,boolean flash)
	{
		if(file == null) return false;
		try
		{
			JSONObject data = new JSONObject();
			data.put("file",file);
			if(url != null && url.trim().length() != 0) data.put("url",url);
			if(timeout != 0) data.put("timeout",timeout);
			if(!cache) data.put("cache","0");
			if(!proxy) data.put("proxy","0");
			if(flash) data.put("type","flash");
			JSONObject obj = new JSONObject();
			obj.put("type","image");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}

	//picture send
	public boolean appendPicture(String file,int timeout,boolean cache,boolean proxy,boolean flash)
	{
		return appendPicture(file,null,timeout,cache,proxy,flash);
	}
	public boolean appendPicture(String file,int timeout,boolean cache,boolean proxy)
	{
		return appendPicture(file,null,timeout,cache,proxy,false);
	}
	public boolean appendPicture(String file,int timeout)
	{
		return appendPicture(file,null,timeout,true,true,false);
	}
	public boolean appendPicture(String file)
	{
		return appendPicture(file,null,0,true,true,false);
	}
	public boolean appendFlashPicture(String file)
	{
		return appendPicture(file,null,0,true,true,true);
	}

	//picture receive
	public boolean appendPicture(String file,String url)
	{
		return appendPicture(file,url,0,true,true,false);
	}
	public boolean appendFlashPicture(String file,String url)
	{
		return appendPicture(file,url,0,true,true,true);
	}

	//record send/receive
	public boolean appendRecord(String file,String url,int timeout,boolean cache,boolean proxy,boolean magic)
	{
		if(file == null) return false;
		try
		{
			JSONObject data = new JSONObject();
			data.put("file",file);
			if(url != null && url.trim().length() != 0) data.put("url",url);
			if(timeout != 0) data.put("timeout",timeout);
			if(!cache) data.put("cache","0");
			if(!proxy) data.put("proxy","0");
			if(magic) data.put("type","magic");
			JSONObject obj = new JSONObject();
			obj.put("type","record");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}

	//record send
	public boolean appendRecord(String file,int timeout,boolean cache,boolean proxy,boolean magic)
	{
		return appendRecord(file,null,timeout,cache,proxy,magic);
	}
	public boolean appendRecord(String file,int timeout,boolean cache,boolean proxy)
	{
		return appendRecord(file,null,timeout,cache,proxy,false);
	}
	public boolean appendRecord(String file,int timeout)
	{
		return appendRecord(file,null,timeout,true,true,false);
	}
	public boolean appendRecord(String file)
	{
		return appendRecord(file,null,0,true,true,false);
	}
	public boolean appendMagicRecord(String file)
	{
		return appendRecord(file,null,0,true,true,true);
	}

	//record receive
	public boolean appendRecord(String file,String url)
	{
		return appendRecord(file,url,0,true,true,false);
	}
	public boolean appendMagicRecord(String file,String url)
	{
		return appendRecord(file,url,0,true,true,true);
	}


	//video send/receive
	public boolean appendVideo(String file,String url,int timeout,boolean cache,boolean proxy)
	{
		if(file == null) return false;
		try
		{
			JSONObject data = new JSONObject();
			data.put("file",file);
			if(url != null && url.trim().length() != 0) data.put("url",url);
			if(timeout != 0) data.put("timeout",timeout);
			if(!cache) data.put("cache","0");
			if(!proxy) data.put("proxy","0");
			JSONObject obj = new JSONObject();
			obj.put("type","video");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}

	//video send
	public boolean appendVideo(String file,int timeout,boolean cache,boolean proxy)
	{
		return appendVideo(file,null,timeout,cache,proxy);
	}
	public boolean appendVideo(String file,int timeout)
	{
		return appendVideo(file,null,timeout,true,true);
	}
	public boolean appendVideo(String file)
	{
		return appendVideo(file,null,0,true,true);
	}

	//video receive
	public boolean appendVideo(String file,String url)
	{
		return appendVideo(file,url,0,true,true);
	}

	//at
	public boolean appendAt(String target)
	{
		if(target == null) return false;
		try
		{
			JSONObject data = new JSONObject();
			data.put("qq",target);
			JSONObject obj = new JSONObject();
			obj.put("type","at");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}

	//no parameter type
	//rps,dice,shake
	public boolean appendNoParaType(String type)
	{
		if(type == null) return false;
		switch(type)
		{
		case "rps":
		case "dice":
		case "shake":
			break;
		default:
			return false;
			//break
		}
		try
		{
			JSONObject data = new JSONObject();
			JSONObject obj = new JSONObject();
			obj.put("type",type);
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}

	//poke
	public boolean appendPoke(String type,String id)
	{
		if(id == null || id.length() == 0) return false;
		if(type == null || type.length() == 0) return false;
		return appendPoke(AsftInt.parseInt(type), AsftInt.parseInt(id));
	}
	public boolean appendPoke(int type, int id)
	{	
		try
		{
			JSONObject data = new JSONObject();
			data.put("type",String.valueOf(type));
			data.put("id",String.valueOf(id));
			JSONObject obj = new JSONObject();
			obj.put("type","face");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}

	//anonymous
	public boolean appendAnonymous(String target,boolean ignore)
	{
		if(target == null) return false;
		try
		{
			JSONObject data = new JSONObject();
			if(ignore) data.put("ignore","1");
			JSONObject obj = new JSONObject();
			obj.put("type","anonymous");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}

	//share
	public boolean appendShare(String url,String title, String content, String image)
	{
		if(url == null) return false;
		if(url.trim().length() == 0) return false;
		if(title == null) return false;
		if(title.trim().length() == 0) return false;
		try
		{
			JSONObject data = new JSONObject();
			data.put("url",url);
			data.put("title",title);
			if(content != null && content.trim().length() != 0) data.put("content",content);
			if(image != null && image.trim().length() != 0) data.put("image",image);
			JSONObject obj = new JSONObject();
			obj.put("type","share");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}
	public boolean appendShare(String url,String title)
	{
		return appendShare(url, title, null, null);
	}

	//location
	public boolean appendLocation(String lat,String lon,String title, String content)
	{
		if(lat == null || lon == null) return false;
		try
		{
			JSONObject data = new JSONObject();
			data.put("lat",lat);
			data.put("lon",lon);
			if(title != null && title.trim().length() != 0) data.put("title",title);
			if(content != null && content.trim().length() != 0) data.put("content",content);
			JSONObject obj = new JSONObject();
			obj.put("type","location");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}
	public boolean appendLocation(String lat,String lon)
	{
		return appendLocation(lat, lon, null, null);
	}

	//contact
	public boolean appendContact(String id,boolean isgroup)
	{
		if(id == null || id.trim().length() == 0) return false;
		try
		{
			JSONObject data = new JSONObject();
			if(isgroup) data.put("type","group");
			else data.put("type","qq");
			data.put("id",id);
			JSONObject obj = new JSONObject();
			obj.put("type","contact");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}

	//send music
	public boolean appendMusic(String type, String id)
	{
		if(id == null || id.trim().length() == 0) return false;
		if(type == null) return false;
		switch(type)
		{
		case "xm":
		case "qq":
		case "163":
			break;
		default:
			return false;
			//break;
		}
		try
		{
			JSONObject data = new JSONObject();
			data.put("type",type);
			data.put("id",id);
			JSONObject obj = new JSONObject();
			obj.put("type","music");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}

	public boolean appendMusic(String url, String audio, String title, String content, String image)
	{
		if(url == null || url.trim().length() == 0) return false;
		if(audio == null || audio.trim().length() == 0) return false;
		if(title == null || title.trim().length() == 0) return false;
		try
		{
			JSONObject data = new JSONObject();
			data.put("url",url);
			data.put("audio",audio);
			data.put("title",title);
			if(content != null && content.trim().length() != 0) data.put("content",content);
			if(image != null && image.trim().length() != 0) data.put("image",image);
			JSONObject obj = new JSONObject();
			obj.put("type","music");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}
	public boolean appendMusic(String url, String audio, String title)
	{
		return appendMusic(url,audio,title,null,null);
	}

	//reply
	public boolean appendReply(String id)
	{
		if(id == null || id.length() == 0) return false;
		try
		{
			JSONObject data = new JSONObject();
			data.put("id",id);
			JSONObject obj = new JSONObject();
			obj.put("type","reply");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}

	//receive forward
	public boolean appendForward(String id)
	{
		if(id == null || id.length() == 0) return false;
		try
		{
			JSONObject data = new JSONObject();
			data.put("id",id);
			JSONObject obj = new JSONObject();
			obj.put("type","forward");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}

	//send node 
	public boolean appendNode(String id)
	{
		if(id == null || id.length() == 0) return false;
		try
		{
			JSONObject data = new JSONObject();
			data.put("id",id);
			JSONObject obj = new JSONObject();
			obj.put("type","node");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}
	//send node
	public boolean appendNode(String user_id,String nickname, String content)
	{
		if(user_id == null || user_id.length() == 0) return false;
		if(nickname == null || nickname.length() == 0) return false;
		if(content == null || content.length() == 0) return false;
		try
		{
			JSONObject data = new JSONObject();
			data.put("user_id",user_id);
			data.put("nickname",nickname);
			data.put("content",content);
			JSONObject obj = new JSONObject();
			obj.put("type","node");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}
	public boolean appendNode(String user_id, String nickname, AsftOneBotMessage content)
	{
		if(user_id == null || user_id.length() == 0) return false;
		if(nickname == null || nickname.length() == 0) return false;
		if(content == null) return false;
		try
		{
			JSONObject data = new JSONObject();
			data.put("user_id",user_id);
			data.put("nickname",nickname);
			data.put("content",content.MessageElements);
			JSONObject obj = new JSONObject();
			obj.put("type","node");
			obj.put("data",data);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}

	public boolean appendCodeMessage(String type, String data)
	{
		if(type == null) return false;
		if(data == null) return false;
		switch(type)
		{
		case "json":
		case "xml":
			break;
		default:
			return false;
			//break;
		}
		try
		{
			JSONObject data_ = new JSONObject();
			data_.put("data",data);
			JSONObject obj = new JSONObject();
			obj.put("type",type);
			obj.put("data",data_);
			MessageElements.put(obj);
		}
		catch(JSONException e)
		{
			//System.out.println(e);
			return false;
		}
		return true;
	}


	//get functions
	public int length()
	{
		return MessageElements.length();
	}

	//get type
	public Vector<String> getElementTypeVector()
	{
		Vector<String> vstr = new Vector<String>();
		for(Object elem : MessageElements)
		{
			vstr.add(((JSONObject)elem).optString("type"));
		}
		return vstr;
	}
	public String getFirstElementType()
	{
		JSONObject elem = MessageElements.optJSONObject(0);
		if(elem == null) return null;
		return elem.optString("type");
	}
	public String getLastElementType()
	{
		JSONObject elem = MessageElements.optJSONObject(MessageElements.length() - 1);
		if(elem == null) return null;
		return elem.optString("type");
	}
	public String getElementType(int index)
	{
		JSONObject obj = MessageElements.optJSONObject(index);
		if(obj == null) return null;
		return obj.optString("type");
	}

	//get index
	public Vector<Integer> getIndexVectorOf(String type)
	{
		Vector<Integer> vint = new Vector<Integer>();
		if(type == null) return vint;
		for(int i = 0; i < MessageElements.length(); i++)
		{
			JSONObject elem = MessageElements.optJSONObject(i);
			String stmp = elem.optString("type");
			if(stmp == null) continue;
			if(stmp.equals(type)) vint.add(i);
		}
		return vint;
	}
	public int getFirstIndexOf(String type)
	{
		if(type == null) return -1;
		for(int i = 0; i < MessageElements.length(); i++)
		{
			JSONObject elem = MessageElements.optJSONObject(i);
			String stmp = elem.optString("type");
			if(stmp == null) continue;
			if(stmp.equals(type)) return i;
		}
		return -1;
	}
	public int getLastIndexOf(String type)
	{
		if(type == null) return -1;
		for(int i = MessageElements.length() - 1; i >= 0 ; i--)
		{
			JSONObject elem = MessageElements.optJSONObject(i);
			String stmp = elem.optString("type");
			if(stmp == null) continue;
			if(stmp.equals(type)) return i;
		}
		return -1;
	}

	//get text
	public Vector<String> getTextVector()
	{
		Vector<String> vstr = new Vector<String>();
		for(Object elem : MessageElements)
		{
			JSONObject obj = (JSONObject)elem;
			String stmp = obj.optString("type");
			if(stmp == null) continue;
			if(stmp.equals("text")) 
			{
				obj = obj.optJSONObject("data");
				if(obj == null) vstr.add("");
				else
				{
					stmp = obj.optString("text");
					if(stmp == null) vstr.add("");
					else vstr.add(stmp);
				}
			}
		}
		return vstr;
	}
	public String getText(int index)
	{
		return getElementDataValue(index,"text");
	}

	//get value
	public String getElementDataValue(int index,String data_key)
	{
		if(data_key == null) return null;
		String _NULL = data_key.equals("text")?"":null;
		JSONObject obj = MessageElements.optJSONObject(index);
		if(obj == null) return _NULL;
		obj = obj.optJSONObject("data");
		if(obj == null) return _NULL;
		String stmp = obj.optString(data_key);
		if(stmp == null) return _NULL;
		return stmp;
	}
	public Vector<String> getElementDataValueVector(String type, String data_key)
	{
		Vector<String> vstr = new Vector<String>();
		if(type == null) return vstr;
		if(data_key == null) data_key = "";
		String _NULL = data_key.equals("text")?"":null;
		for(Object elem: MessageElements)
		{
			JSONObject obj = (JSONObject)elem;
			String stmp = obj.optString("type");
			if(stmp == null ||!stmp.equals(type)) continue;
			obj = obj.optJSONObject("data");
			if(obj == null) 
			{
				vstr.add(_NULL);
				continue;
			}
			stmp = obj.optString(data_key);
			if(stmp == null) vstr.add(_NULL);
			else vstr.add(stmp);
		}
		return vstr;
	}
	public Vector<String> getElementDataValueVector(String data_key)
	{
		Vector<String> vstr = new Vector<String>();
		if(data_key == null) data_key = "";
		String _NULL = data_key.equals("text")?"":null;
		for(Object elem: MessageElements)
		{
			JSONObject obj = (JSONObject)elem;
			obj = obj.optJSONObject("data");
			if(obj == null) 
			{
				vstr.add(_NULL);
				continue;
			}
			String stmp = obj.optString(data_key);
			if(stmp == null) vstr.add(_NULL);
			else vstr.add(stmp);
		}
		return vstr;
	}
	public Vector<String> getElementDataValueVector(Vector<Integer> index_vct, String data_key)
	{
		Vector<String> vstr = new Vector<String>();
		if(data_key == null) data_key = "";
		String _NULL = data_key.equals("text")?"":null;
		for(Integer idx: index_vct)
		{
			JSONObject obj = MessageElements.optJSONObject(idx);
			if(obj == null) 
			{
				vstr.add(_NULL);
				continue;
			}
			obj = obj.optJSONObject("data");
			if(obj == null) 
			{
				vstr.add(_NULL);
				continue;
			}
			String stmp = obj.optString(data_key);
			if(stmp == null) vstr.add(_NULL);
			else vstr.add(stmp);
		}
		return vstr;
	}
	public AsftOneBotMessage replaceFilesByUrls()
	{
		for(Object elem : MessageElements)
		{
			try
			{
				JSONObject obj = (JSONObject)elem;
				JSONObject data = obj.optJSONObject("data");
				String url = data.optString("url", null);
				if(!data.has("file")
					||url == null || url.trim().length() == 0 
					||url.trim().toLowerCase(Locale.ROOT).equals("null")
				)continue;
				data.put("file", url);
				obj.put("data", data);
			}
			catch(NullPointerException|JSONException e)
			{
				continue;
			}
		}
		return this;
	}
}