import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.json.*;

import java.net.*;
public class GroupPcrPvpManager
{
	public static class PvpTargetStates implements Serializable 
	{
		private static final long serialVersionUID = 6607039160225744416L;
		public int Jjcrank;
		public int Pjcrank;
		public boolean Valid;
		public PvpTargetStates()
		{
			Jjcrank = 0;
			Pjcrank = 0;
			Valid = false;
		}
		public PvpTargetStates(String json)
		{
			try
			{
				JSONObject obj = new JSONObject(json);
				String delta = obj.optString("status","");
				if(delta.equals("done")) Valid = true;
				else if(delta.equals("queue"))
				{
					Jjcrank = -obj.optInt("pos",10000);
					Pjcrank = Jjcrank;
					Valid = false;
				}
				else 
				{
					Jjcrank = 0;
					Pjcrank = 0;
					Valid = false;
					return;
				}
				obj = obj.optJSONObject("data");
				obj = obj.optJSONObject("user_info");
				Jjcrank = obj.optInt("arena_rank",0);
				Pjcrank = obj.optInt("grand_arena_rank",0);
			}
			catch(JSONException|NullPointerException e)
			{
				Jjcrank = 0;
				Pjcrank = 0;
				Valid = false;
			} 
		}
		public PvpTargetStates(JSONObject obj)
		{
			try
			{
				if(obj.optString("status","").equals("done")) Valid = true;
				else 
				{
					Jjcrank = 0;
					Pjcrank = 0;
					Valid = false;
					return;
				}
				obj = obj.optJSONObject("data");
				obj = obj.optJSONObject("user_info");
				Jjcrank = obj.optInt("arena_rank",0);
				Pjcrank = obj.optInt("grand_arena_rank",0);
			}
			catch(JSONException|NullPointerException e)
			{
				Jjcrank = 0;
				Pjcrank = 0;
				Valid = false;
			} 
		}
	}
	public static class PvpTarget implements Serializable 
	{
		private static final long serialVersionUID = 3949346834181658410L;
		public long Target;
		public boolean Jjc;
		public boolean Pjc;
		public boolean Self;
		public String Alias;
		public PvpTarget(long target)
		{
			Target = target;
			Jjc = Pjc = Self = false;
			Alias = "";
		}
		public PvpTarget()
		{
			Target = 0;
			Jjc = Pjc = Self = false;
			Alias = "";
		}
		public PvpTarget(PvpTarget pvpTarget) 
		{
			Target = pvpTarget.Target;
			Jjc = pvpTarget.Jjc;
			Pjc = pvpTarget.Pjc;
			Self = pvpTarget.Self;
			Alias = pvpTarget.Alias;
		}
		public int addTargetType(boolean jjc,boolean pjc, boolean self)
		{
			int ret = 0;
			if(!Jjc && jjc)
			{
				Jjc = true;
				ret ++; 
			}
			if(!Pjc && pjc)
			{
				Pjc = true;
				ret ++; 
			}
			Self = self;
			return ret;
		}
		public int removeTargetType(boolean jjc,boolean pjc)
		{
			int ret = 0;
			if(Jjc && jjc)
			{
				Jjc = false;
				ret --; 
			}
			if(Pjc && pjc)
			{
				Pjc = false;
				ret --; 
			}
			return ret;
		}
	}
	public static class PvpTargetSettings implements Serializable 
	{
		private static final long serialVersionUID = 2310089919185703247L;
		private ConcurrentHashMap <Long, Vector<PvpTarget> > Id_Targets_Map;
		private ConcurrentHashMap <Long, PvpTargetStates[]>  Target_States_Map;
		private ConcurrentHashMap <Long, Integer>  Target_Reference_Map;
		private HashSet<Long> Id_At_Map;
		public PvpTargetSettings()
		{
			Id_Targets_Map = new ConcurrentHashMap <Long, Vector<PvpTarget> >();
			Target_States_Map = new ConcurrentHashMap <Long, PvpTargetStates[]>();
			Target_Reference_Map = new ConcurrentHashMap <Long, Integer>();
			Id_At_Map = new HashSet<Long>();
		}
		public boolean addTarget(long userid,long target,boolean jjc,boolean pjc, boolean self)
		{
			if(!(jjc||pjc)) return false;
			if(!Target_States_Map.containsKey(target))
			{
				PvpTargetStates[] newold = new PvpTargetStates[2];
				newold[0] = new PvpTargetStates();
				newold[1] = new PvpTargetStates();
				Target_States_Map.put(target, newold);
			}
			Vector<PvpTarget> targets = Id_Targets_Map.get(userid); 
			int ref = 0;
			if(targets == null) 
			{
				targets = new Vector<PvpTarget>();
				Id_Targets_Map.put(userid, targets);
			}
			else
			{
				Integer refc = Target_Reference_Map.get(target);
				if(refc == null) refc = 0;
				ref = refc;
			}
			PvpTarget elemfound = null;
			for(PvpTarget elem:targets)
			{
				if(elem.Target == target)
				{
					elemfound = elem;
					break;
				}
			}
			if(elemfound == null) 
			{
				elemfound = new PvpTarget(target);
				targets.add(elemfound);
			}
			ref += elemfound.addTargetType(jjc, pjc, self);
			Target_Reference_Map.put(target,ref);
			return true;
		}
		public boolean renameTarget(long userid,long target,String alias)
		{
			if(alias == null) alias = "";
			else alias = alias.trim();
			if(alias.length() > 16) alias = alias.substring(0, 16);
			Vector<PvpTarget> targets = Id_Targets_Map.get(userid); 
			if(targets == null) return false;
			for(PvpTarget elem:targets)
			{
				if(elem.Target == target)
				{
					elem.Alias = alias;			
					return true;
				}
			}
			return false;
		}
		public boolean removeTarget(long userid,long target,boolean jjc,boolean pjc)
		{
			if(!(jjc||pjc)) return false;
			Vector<PvpTarget> targets = Id_Targets_Map.get(userid); 
			if(targets == null) return false;
			Integer refc = Target_Reference_Map.get(target);
			if(refc == null) refc = 0;
			int ref = refc;
			int len = targets.size();
			for(int i = 0; i < len; i++)
			{
				PvpTarget elem = targets.get(i);
				if(elem.Target == target)
				{
					ref += elem.removeTargetType(jjc, pjc);
					if(!elem.Jjc && !elem.Pjc)
					{
						if(len != 1) targets.remove(i);
						else Id_Targets_Map.remove(userid);
					}
					if(ref <= 0)
					{
						Target_States_Map.remove(target);
					}
					if(ref > 0)
					{
						Target_Reference_Map.put(target,ref);
					}
					else
					{
						Target_Reference_Map.remove(target);
					}
					return true;
				}
			}
			return false;
		}
		public boolean removeTarget(long userid,String name,boolean jjc,boolean pjc)
		{
			if(!(jjc||pjc)) return false;
			Vector<PvpTarget> targets = Id_Targets_Map.get(userid); 
			if(targets == null) return false;
			int len = targets.size();
			for(int i = 0; i < len; i++)
			{
				PvpTarget elem = targets.get(i);
				if(elem.Alias.equals(name))
				{
					Integer refc = Target_Reference_Map.get(elem.Target);
					if(refc == null) refc = 0;
					int ref = refc + elem.removeTargetType(jjc, pjc);
					if(!elem.Jjc && !elem.Pjc)
					{
						if(len != 1) targets.remove(i);
						else Id_Targets_Map.remove(userid);
					}
					if(ref <= 0)
					{
						Target_States_Map.remove(elem.Target);
					}
					if(ref > 0)
					{
						Target_Reference_Map.put(elem.Target,ref);
					}
					else
					{
						Target_Reference_Map.remove(elem.Target);
					}
					return true;
				}
			}
			return false;
		}
		public boolean removeAllTarget(long userid)
		{
			Vector<PvpTarget> targets = null; 

			targets = Id_Targets_Map.remove(userid); 
			if(targets == null) return false;
			for(PvpTarget elem:targets)
			{
				Integer refc = Target_Reference_Map.get(elem.Target);
				if(refc == null) refc = 0;
				int ref = refc;
				ref += elem.removeTargetType(true, true);
				if(ref <= 0)
				{
					Target_Reference_Map.remove(elem.Target);
					Target_States_Map.remove(elem.Target);
				}
				else
				{
					Target_Reference_Map.put(elem.Target,ref);
				}
			}
			return true;
		}
		public void atOn(long userid)
		{
			synchronized(Id_At_Map)
			{
				Id_At_Map.add(userid);
			}
		}
		public void atOff(long userid)
		{
			synchronized(Id_At_Map)
			{
				Id_At_Map.remove(userid);
			}
		}
		public boolean isAtUser(long userid)
		{
			synchronized(Id_At_Map)
			{
				return Id_At_Map.contains(userid);
			}
		}
		public int renew(int halftimeout)
		{
			int cnt = 0;
			for(Long key: Target_States_Map.keySet())
			{
				PvpTargetStates[] newold = Target_States_Map.get(key);
				if(newold == null) continue;
				cnt++;
				PvpTargetStates states = requestPvpTargetStates(key, halftimeout);
				if(!states.Valid) continue;
				newold[1] = newold[0];
				newold[0] = states;	
			}
			return cnt;
		}
	}

	public static PvpTargetStates requestPvpTargetStates(long target, int halftimeout)
	{
		try
		{
			URL url = new URL("https://help.tencentbot.top/enqueue?target_viewer_id=" + target);  
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
			conn.setRequestMethod("GET");
			conn.setReadTimeout(halftimeout);
			conn.setConnectTimeout(halftimeout);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.connect();
			InputStream is = conn.getInputStream();  
			ByteArrayOutputStream ret = new ByteArrayOutputStream();  
			int len = 0;   
			byte buffer[] = new byte[256];   
			while ((len = is.read(buffer)) != -1) 
			{  
				ret.write(buffer, 0, len);  
			}  
			is.close();  
			ret.close();  
			String result = new JSONObject(ret.toString()).optString("request_id",null);
			if(result == null) result = new JSONObject(ret.toString()).getString("reqeust_id");
			Thread.sleep(1024);
			while(true)
			{
				url = new URL("https://help.tencentbot.top/query?request_id=" + result);  
				
				conn = (HttpURLConnection) url.openConnection();  
				conn.setRequestMethod("GET");
				conn.setReadTimeout(halftimeout);
				conn.setConnectTimeout(halftimeout);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.connect();
				is = conn.getInputStream();  
				ret = new ByteArrayOutputStream();  
				len = 0;   
				while ((len = is.read(buffer)) != -1) 
				{  
					ret.write(buffer, 0, len);  
				}  
				is.close();  
				ret.close();  
				//System.out.println(ret.toString());
				PvpTargetStates rst = new PvpTargetStates(ret.toString());
				if(rst.Valid == true) return rst;
				else if(rst.Jjcrank <0) Thread.sleep(1024);
				else return new PvpTargetStates();
			}
		}
		catch (Exception e)
		{
			try
			{
				Thread.sleep(1024);
			}
			catch (Exception e2)
			{
				//donothing
			}
			return new PvpTargetStates();
		}
	}
	
	
	private static int[][] _maxhp = 
	{{600,800,1000,1200,2000},
	{600 ,800,1000,1200,2000},
	{600 ,800,1000,1200,2000},
	{600 ,800,1000,1200,2000}};
	//{700, 900 ,1300,1500,2000},
	//{1500,1600,1800,1900,2000}};
	
	private static int[][] _maxhpsum =
	{{600,1400,2400,3600,5600},
	{600 ,1400,2400,3600,5600},
	{600 ,1400,2400,3600,5600},
	{600 ,1400,2400,3600,5600}};
	//{700,1600,2900,4400,6400},
	//{1500,3100,4900,6800,8800}};


	private static int[] _turns = {0,3,7,24}; 
	private static int[] _totalturns = {0,3,10,34}; 
	private static int[] _totalhpsum = 
	{
		0,
		_maxhpsum[0][4]*_turns[1],
		_maxhpsum[0][4]*_turns[1]+_maxhpsum[1][4]*_turns[2],
		_maxhpsum[0][4]*_turns[1]+_maxhpsum[1][4]*_turns[2]+_maxhpsum[2][4]*_turns[3],
		Integer.MAX_VALUE/16384
	};


	public static String calcBossByDamage(long damage)
	{
		if(damage < 0) damage = 0;
		final long w = 10000L;
		int turn1 = 0, turn2 = 0;
		int boss = 1;
		int[] maxhpsum = null ;
		int[] maxhp = null;
		for(int i = 0; i <= 3; i++) //i : complete steps
		{
			if(i == 3 || damage < _totalhpsum[i + 1] * w)
			{
				maxhpsum = _maxhpsum[i];
				maxhp = _maxhp[i];
				turn1 = _totalturns[i];
				damage -= _totalhpsum[i] * w;
				turn2 = (int)(damage / (maxhpsum[4] * w));
				damage -= turn2 * (maxhpsum[4] * w);
				break;
			}
		}
		for(int i = 0; i <= 4; i++)
		{
			if(damage < maxhpsum[i] * w)
			{
				boss = i + 1;
				break;
			}
		}
		return ""+ (turn1+turn2+1) +"周目"+ boss + "王\n"
		+ String.format("%.4f", (double)maxhpsum[boss-1] - (double)damage/10000.0) + "万/" + maxhp[boss-1] +"万";
	}
	
	private static int[][] _maxscore = 
	{{600 ,800,1300,1560,3000},
	{840 ,1120,1800,2160,4000},
	{1200,1600,2500,3000,6000},
	{1200,1600,2500,3000,6000}};

	private static int[][] _maxscoresum =
	{{600,1400,2700,4260,7260},
	{840 ,1960,3760,5920,9920},
	{1200,2800,5300,8300,14300},
	{1200,2800,5300,8300,14300}};

	private static int[] _totalscoresum = 
	{
		0,
		_maxscoresum[0][4]*_turns[1],
		_maxscoresum[0][4]*_turns[1]+_maxscoresum[1][4]*_turns[2],
		_maxscoresum[0][4]*_turns[1]+_maxscoresum[1][4]*_turns[2]+_maxscoresum[2][4]*_turns[3],
		Integer.MAX_VALUE/16384
	};

	public static String calcBossByScore(long score)
	{
		if(score < 0) score = 0;
		final long w = 10000L;
		int turn1 = 0, turn2 = 0;
		int boss = 5;
		int[] maxhp = null;
		int[] maxscoresum = null ;
		int[] maxscore = null ;
		for(int i = 0; i <= 3; i++) //i : complete steps
		{
			if(i == 3 || score < _totalscoresum[i + 1] * w)
			{
				maxhp = _maxhp[i];
				maxscoresum = _maxscoresum[i];
				maxscore = _maxscore[i];
				turn1 = _totalturns[i];
				score -= _totalscoresum[i] * w;
				turn2 = (int)(score / (maxscoresum[4] * w));
				score -= turn2 * (maxscoresum[4] * w);
				break;
			}
		}
		for(int i = 0; i <= 4; i++)
		{
			if(score < maxscoresum[i] * w)
			{
				boss = i + 1;
				break;
			}
		}
		return ""+ (turn1+turn2+1) +"周目"+ boss + "王\n"
		+ String.format("%.4f", ((double)maxscoresum[boss-1] - (double)score/10000.0) / (double)maxscore[boss-1] * (double)maxhp[boss-1])
		+ "万/" + maxhp[boss-1] +"万";
	}
	public static long calcTotalDamage(long score)
	{
		if(score < 0) score = 0;
		final long w = 10000L;
		int turn2 = 0;
		int boss = 5;
		int[] maxhp = null;
		int[] maxhpsum = null;
		int[] maxscoresum = null ;
		int[] maxscore = null ;
		long damage = 0;
		for(int i = 0; i <= 3; i++) //i : complete steps
		{
			if(i == 3 || score < _totalscoresum[i + 1] * w)
			{
				maxhp = _maxhp[i];
				maxhpsum = _maxhpsum[i];
				maxscoresum = _maxscoresum[i];
				maxscore = _maxscore[i];
				score -= _totalscoresum[i] * w;
				damage += _totalhpsum[i];
				turn2 = (int)(score / (maxscoresum[4] * w));
				score -= turn2 * (maxscoresum[4] * w);
				damage += turn2 * maxhpsum[4];
				break;
			}
		}
		for(int i = 0; i <= 4; i++)
		{
			if(score < maxscoresum[i] * w)
			{
				boss = i + 1;
				break;
			}
		}
		damage += maxhpsum[boss-1];
		damage *= w;
		damage -=  (long)(w * ( ((double)maxscoresum[boss-1] - (double)score/10000.0) / (double)maxscore[boss-1] * (double)maxhp[boss-1]));
		return damage;
	}

	public static class AsyncRequestGuildStates extends Thread
	{
		private AwpBotInterface Bot;
		private long GroupId;
		private long History;
		private String ClanName;
		private boolean Line;
		AsyncRequestGuildStates(AwpBotInterface bot, long groupid, String clanName, boolean line, long history)
		{
			Bot = bot;
			GroupId = groupid;
			ClanName = clanName;
			Line = line;
			History = history;
		}
		@Override
		public void run()
		{
			try
			{
				JSONArray array = requestGuildStates(ClanName, Line, History, 3000);
				//System.out.println(array);
				if(array == null)
				{
					AsftOneBotApi.SendGroupMessage_cqstr(GroupId, "查询失败，未知错误。").send(Bot);
				}
				else if(array.length() == 0)
				{
					AsftOneBotApi.SendGroupMessage_cqstr(GroupId, "无查询结果。").send(Bot);
				}
				else
				{
					StringBuilder result = new StringBuilder(4096);
					result.append("查询结果：");
					boolean fenjie = false;
					for(Object oobj:array)
					{
						JSONObject obj = (JSONObject)oobj;
						long score = obj.optLong("damage");
						long damage = calcTotalDamage(score);
						if(fenjie) result.append("\n");
						result.append("\n")
						.append("公会：").append(obj.optString("clan_name")).append("\n")
						.append("会长：").append(obj.optString("leader_name")).append("\n")
						.append("排名：").append(obj.optString("rank")).append("\n")
						.append("得分：").append(String.format("%.4f",(double)score/10000.0)).append("万\n")
						.append("伤害：").append(String.format("%.4f",(double)damage/10000.0)).append("万\n")
						.append("进度：").append(calcBossByDamage(damage));
						fenjie = true;
					}
					AsftOneBotApi.SendGroupMessage_text(GroupId,result.toString()).setAsync().send(Bot);
				}
			}
			catch(Exception e)
			{
				return ;
			}
		}
	}


	private static JSONArray requestGuildStates(String clanName,boolean line, long history,int timeout) throws Exception
	{
		URL url = null;
		if(line) url = new URL("https://service-kjcbcnmw-1254119946.gz.apigw.tencentcs.com/line");  
		else url = new URL("https://service-kjcbcnmw-1254119946.gz.apigw.tencentcs.com/name/-1");  
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
		conn.setRequestMethod("POST");
		conn.setReadTimeout(timeout);
		conn.setConnectTimeout(timeout);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-type", "application/json");  
		conn.setRequestProperty("Custom-Source","GitHub@var-mixer");  
		conn.setRequestProperty("Referer", "https://kengxxiao.github.io/Kyouka/");  
		OutputStream out = conn.getOutputStream();
		if(line) out.write(("{\"history\":"+ history +"}").getBytes());
		else out.write(("{\"history\":" + history + ",\"clanName\": \""+ clanName.replaceAll("\\\\","\\\\\\\\").replaceAll("\"", "\\\\\"")+"\"}").getBytes());
		out.flush();
		InputStream is = conn.getInputStream();   
		ByteArrayOutputStream ret = new ByteArrayOutputStream();  
		int len = 0;  
		byte buffer[] = new byte[4096];  
		while ((len = is.read(buffer)) != -1) 
		{  
			ret.write(buffer, 0, len);  
		}  
		is.close();  
		ret.close();  
		JSONObject obj = new JSONObject(ret.toString());
		return new JSONArray(obj.getJSONArray("data"));
	}


	private class PvpFollowLoop extends Thread
	{
		public PvpFollowLoop(){}
		@Override
		public void run()
		{
			while(true)
			{
				try
				{
					//Thread.sleep(60000);
					int cnt = 0;
					for(Long groupid: GroupGlobalPvpManagerMap.keySet())
					{
						if(isBlackGroup(groupid)) continue;
						PvpTargetSettings settings = GroupGlobalPvpManagerMap.get(groupid);
						if(settings == null) continue;
						cnt += settings.renew(1000);
						for(Long userid :settings.Id_Targets_Map.keySet())
						{
							if(isBlackUser(userid)) continue;
							//System.out.println(userid);
							Vector<PvpTarget> targets = settings.Id_Targets_Map.get(userid);
							if(targets == null) continue;
							StringBuilder userresult = new StringBuilder(1024);
							for (PvpTarget pvpTarget : new Vector<PvpTarget> (targets)) 
							{
								PvpTarget target = new PvpTarget(pvpTarget);
								PvpTargetStates[] newold =  settings.Target_States_Map.get(target.Target);
								if(newold == null) continue;
								//System.out.println(" "+target + " "+ newold[0].Valid +" "+ newold[1].Valid);
								//System.out.println();
								if(!newold[0].Valid || !newold[1].Valid) continue;
								if(target.Jjc)
								{
									//System.out.println("" + newold[0].Jjcrank+" " + newold[1].Jjcrank +" "+target.Self);
									//System.out.println("" + newold[0].Valid+" " + newold[1].Valid);
									if(newold[0].Jjcrank > newold[1].Jjcrank 
									||(!target.Self && newold[0].Jjcrank < newold[1].Jjcrank))
									{
										userresult.append("\n" +target.Alias);
										if("".equals(target.Alias)) userresult.append(target.Target);
										userresult.append("\n战斗竞技场: ").append(newold[1].Jjcrank).append(" -> ")
										.append(newold[0].Jjcrank);
									}
								}
								if(target.Pjc)
								{
									if(newold[0].Pjcrank > newold[1].Pjcrank 
									||(!target.Self && newold[0].Pjcrank < newold[1].Pjcrank))
									{
										userresult.append("\n" +target.Alias);
										if("".equals(target.Alias)) userresult.append(target.Target);
										userresult.append("\n公主竞技场: ").append(newold[1].Pjcrank).append(" -> ")
										.append(newold[0].Pjcrank);
									}
								}
								
							}
							if(userresult.length()!=0)
							{
								AsftOneBotMessage msg = AsftOneBotMessage.createEmpty();
								if(settings.isAtUser(userid))
								{
									msg.appendAt(String.valueOf(userid));	
								}
								else
								{
									userresult.replace(0, 1, "");
								}
								msg.appendText(userresult.toString());
								AsftOneBotApi.SendGroupMessage_jsarr(groupid, msg.toJSONArray()).setAsync().send(Bot);
							}
						}
					}
					if(cnt< 60L * (IntervalMin - 1)) Thread.sleep(((long) (IntervalMin -1) * 60L - cnt) * 1000);
				}
				catch(Exception e)
				{
					Thread.yield();
					continue;
				}
			}
		}
	}

	private ConcurrentHashMap <Long , PvpTargetSettings> GroupGlobalPvpManagerMap;
	private HashSet<Long> BlackList, BlackGroupList;
	private int IntervalMin;
	public AwpBotInterface Bot;

	public void asyncQueryGuildStates(AwpBotInterface bot, long userid, long groupid, String clanName, boolean fiveoclock)
	{
		if(isBlackUser(userid)||isBlackGroup(groupid)) return;
		long history = 0;
		if(fiveoclock)
		{
			history = System.currentTimeMillis() + 9000000L; //5:30
			history = history / 86400000L* 86400000L - 9000000L;
			history /= 1000L;
		}
		new AsyncRequestGuildStates(bot, groupid, clanName, false, history).start();
	}
	public  void asyncQueryLineGuildStates(AwpBotInterface bot,long userid, long groupid, boolean fiveoclock)
	{
		if(isBlackUser(userid)||isBlackGroup(groupid)) return;
		long history = 0;
		if(fiveoclock)
		{
			history = System.currentTimeMillis() + 9000000L; //5:30
			history = history / 86400000L* 86400000L - 9000000L;
			history /= 1000L;
		}
		new AsyncRequestGuildStates(bot, groupid, null, true, history).start();
	}
	public boolean addTarget(long userid,long groupid,long target,boolean jjc,boolean pjc, boolean self)
	{
		if(isBlackUser(userid)||isBlackGroup(groupid)) return false;
		PvpTargetSettings settings = GroupGlobalPvpManagerMap.get(groupid); 
		if(settings == null) return false;
		return settings.addTarget(userid, target, jjc, pjc, self);
	}
	public boolean addTarget(long userid,long groupid,long target,boolean jjc,boolean pjc, boolean self,String alias)
	{
		if(isBlackUser(userid)||isBlackGroup(groupid)) return false;
		PvpTargetSettings settings = GroupGlobalPvpManagerMap.get(groupid); 
		if(settings == null) return false;
		return settings.addTarget(userid, target, jjc, pjc, self) && settings.renameTarget(userid, target, alias);
	}
	public boolean removeTarget(long userid,long groupid,long target,boolean jjc,boolean pjc)
	{
		if(isBlackUser(userid)||isBlackGroup(groupid)) return false;
		PvpTargetSettings settings = GroupGlobalPvpManagerMap.get(groupid); 
		if(settings == null) return false;
		return settings.removeTarget(userid, target, jjc, pjc);
	}
	public boolean removeTarget(long userid,long groupid,String name,boolean jjc,boolean pjc)
	{
		if(isBlackUser(userid)||isBlackGroup(groupid)) return false;
		PvpTargetSettings settings = GroupGlobalPvpManagerMap.get(groupid); 
		if(settings == null) return false;
		return settings.removeTarget(userid, name, jjc, pjc);
	}
	public boolean removeAllTarget(long userid,long groupid)
	{
		if(isBlackUser(userid)||isBlackGroup(groupid)) return false;
		PvpTargetSettings settings = GroupGlobalPvpManagerMap.get(groupid); 
		if(settings == null) return false;
		return settings.removeAllTarget(userid);
	}
	public boolean renameTarget(long userid,long groupid,long target, String alias)
	{
		if(isBlackUser(userid)||isBlackGroup(groupid)) return false;
		PvpTargetSettings settings = GroupGlobalPvpManagerMap.get(groupid); 
		if(settings == null) return false;
		return settings.renameTarget(userid, target, alias);
	}
	public boolean atOn(long userid,long groupid)
	{
		if(isBlackUser(userid)||isBlackGroup(groupid)) return false;
		PvpTargetSettings settings = GroupGlobalPvpManagerMap.get(groupid); 
		if(settings == null) return false;
		settings.atOn(userid);
		return true;
	}
	public boolean atOff(long userid,long groupid)
	{
		if(isBlackUser(userid)||isBlackGroup(groupid)) return false;
		PvpTargetSettings settings = GroupGlobalPvpManagerMap.get(groupid); 
		if(settings == null) return false;
		settings.atOff(userid);
		return true;
	}
	public Vector<PvpTargetSettings> getAllPvpTargetSettings()
	{
		Vector<PvpTargetSettings> rt = new Vector<PvpTargetSettings>();
		for (HashMap.Entry<Long, PvpTargetSettings> entry : GroupGlobalPvpManagerMap.entrySet()) 
		{
			rt.add(entry.getValue());
		}
		return rt;
	}
	public Vector<Long> getAllGroups()
	{
		Vector<Long> rt = new Vector<Long>();
		for (ConcurrentHashMap.Entry<Long, PvpTargetSettings> entry : GroupGlobalPvpManagerMap.entrySet()) 
		{
			rt.add(entry.getKey());
		}
		return rt;
	}
	public Vector<Long> getBlackUsers()
	{
		Vector<Long> rt = new Vector<Long>(BlackList);
		return rt;
	}
	public Vector<Long> getBlackGroups()
	{
		Vector<Long> rt = new Vector<Long>(BlackGroupList);
		return rt;
	}
	public static boolean isOwner(long operatorid)
	{
		return GroupChatSaveManager.isOwner(operatorid);
	}
	public void purgeAll()
	{
		GroupGlobalPvpManagerMap.clear();
	}
	public void purgeGroup(long groupid)
	{
		GroupGlobalPvpManagerMap.remove(groupid);
	}
	public void setInterval(int minutes)
	{
		if(minutes <= 0) minutes = 1;
		IntervalMin = minutes;
	}
	public boolean registerGroup(long groupid)
	{
		if(isBlackGroup((groupid))) return false;
		if(GroupGlobalPvpManagerMap.containsKey(groupid)) return false;
		GroupGlobalPvpManagerMap.put(groupid, new PvpTargetSettings());
		return true;
	}
	public boolean purgeAll(long operatorid)
	{
		if(!isOwner(operatorid)) return false;
		GroupGlobalPvpManagerMap.clear();
		return true;
	}
	public boolean purgeGroup(long operatorid, long groupid)
	{
		if(!isOwner(operatorid)) return false;
		GroupGlobalPvpManagerMap.remove(groupid);
		return true;
	}
	public boolean banUser(long userid, boolean set)
	{
		if(userid <= 0) return false;
		if(isOwner(userid)) return false;
		synchronized(BlackList)
		{
			if(set) BlackList.add(userid);
			else BlackList.remove(userid);
		}
		return true;
	}
	public boolean banUser(long userid)
	{
		return banUser(userid, true);
	}
	public boolean banUser(long operatorid, long userid, boolean set)
	{
		if(userid <= 0) return false;
		if(!isOwner(operatorid)) return false;
		if(isOwner(userid)) return false;
		synchronized(BlackList)
		{
			if(set) BlackList.add(userid);
			else BlackList.remove(userid);
		}
		return true;
	}
	public boolean banUser(long operatorid, long userid)
	{
		return banUser(operatorid, userid, true);
	}
	public boolean banGroup(long operatorid, long groupid, boolean set)
	{
		if(groupid <= 0) return false;
		if(!isOwner(operatorid)) return false;
		synchronized(BlackGroupList)
		{
			if(set) BlackGroupList.add(groupid);
			else BlackGroupList.remove(groupid);
		}
		return true;
	}
	public boolean banGroup(long operatorid, long groupid)
	{
		return banGroup(operatorid, groupid,true);
	}
	public boolean isBlackUser(long userid)
	{
		synchronized(BlackList)
		{
			return BlackList.contains(userid);
		}
	}
	public boolean isBlackGroup(long groupid)
	{
		synchronized(BlackGroupList)
		{
			return BlackGroupList.contains(groupid);
		}
	}
	public GroupPcrPvpManager(AwpBotInterface bot)
	{
		IntervalMin = 1;
		Bot = bot;
		if(bot != null) new PvpFollowLoop().start();
	}
	public boolean save()
	{
		//使用上一个组件的BlackList，所以不需要对BlackList进行保存
		try
		{
			File filebak  = new File("GroupGlobalPvpManagerMap.bak");
			if(filebak.exists()) filebak.delete();
			File file  = new File("GroupGlobalPvpManagerMap");
			if(file.exists()) file.renameTo(filebak);
			ObjectOutputStream otpt = new ObjectOutputStream(new FileOutputStream("GroupGlobalPvpManagerMap"));
			otpt.writeObject(GroupGlobalPvpManagerMap);
			otpt.close();
		}
		catch (Exception e) 
		{
			System.out.println("Save GroupGlobalPvpManagerMap Failed.") ;
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean load()
	{
		try
		{
			ObjectInputStream inpt = new ObjectInputStream(new FileInputStream("BlackList"));
			BlackList = (HashSet<Long>)inpt.readObject();
			inpt.close();
		} 
		catch (Exception e) 
		{
			BlackList = new HashSet<Long>();
			//System.out.println("Load BlackList Failed. New BlackList Created.");
		}

		try
		{
			ObjectInputStream inpt = new ObjectInputStream(new FileInputStream("BlackGroupList"));
			BlackGroupList = (HashSet<Long>)inpt.readObject();
			inpt.close();
		} 
		catch (Exception e) 
		{
			BlackGroupList = new HashSet<Long>();
			//System.out.println("Load BlackGroupList Failed. New BlackGroupList Created.");
		}

		try
		{
			ObjectInputStream inpt = new ObjectInputStream(new FileInputStream("GroupGlobalPvpManagerMap"));
			GroupGlobalPvpManagerMap = (ConcurrentHashMap <Long , PvpTargetSettings>)inpt.readObject();
			inpt.close();
		} 
		catch (Exception e) 
		{
			GroupGlobalPvpManagerMap = new ConcurrentHashMap <Long, PvpTargetSettings>();
			System.out.println("Load GroupGlobalPvpManagerMap Failed. New GroupGlobalPvpManagerMap Created.");
			return false;
		}
		return true;
	}


}
