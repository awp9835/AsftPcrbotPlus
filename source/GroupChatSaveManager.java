import java.io.*;
import java.util.*;
public class GroupChatSaveManager
{
	public static class GroupChatSaves implements Serializable 
	{
		private static final long serialVersionUID = 4814782020797651386L;
		HashMap<Long, String> Id_Msgstr_Map;
		HashMap<String, Vector<Long> >  Key_Id_Map;
		long currentid;
		public GroupChatSaves()
		{
			Id_Msgstr_Map = new HashMap<Long, String>();
			Key_Id_Map = new HashMap<String, Vector<Long> >();
			currentid = 1;
		}
		public boolean removeAllByKeys(String[] keys) 
		{
			if(keys == null || keys.length == 0) return false; 
			for(String key : keys)
			{
				removeAllByKey(key);
			}
			return true;
		}
		public boolean removeAllByKey(String key) 
		{
			if(key == null) return false;
			key = key.trim().toLowerCase(Locale.ROOT);
			if(key.length() == 0) return false;
			Vector<Long> ids = Key_Id_Map.get(key);
			if(ids == null) return false; 
			Key_Id_Map.remove(key);
			for(Long id: ids)
			{
				Id_Msgstr_Map.remove(id);
			}
			return true;
		}
		public boolean removeById(long msgstrid) 
		{
			return null != Id_Msgstr_Map.remove(msgstrid);
		}

		public long record(String[] keys, String msgstr) 
		{
			boolean flag = false;
			if(keys == null) return 0;
			for(String key : keys)
			{
				if(key == null) continue;
				key = key.trim().toLowerCase(Locale.ROOT);
				if(key.length() == 0) continue;
				Vector<Long> ids = Key_Id_Map.get(key);
				if(ids == null)
				{
					ids = new Vector<Long>();
					Key_Id_Map.put(key,ids);
				}
				ids.add(currentid);
				flag = true;
			}
			if(!flag) return 0;
			Id_Msgstr_Map.put(currentid, msgstr);
			currentid ++;
			return currentid - 1;
		}

		public HashMap<Long, String> queryByKeys(String[] keys) 
		{
			HashMap<Long, String> map = new HashMap<Long, String>();
			for(String key : keys)
			{
				if(key == null) continue;
				key = key.trim().toLowerCase(Locale.ROOT);
				if(key.length() == 0) continue;
				Vector<Long> ids = Key_Id_Map.get(key);
				if(ids == null) continue;
				Vector<Integer> tombs = new Vector<Integer>();
				int len = ids.size();
				for(int i = 0; i < len; i++)
				{
					Long id = ids.get(i);
					String stmp = Id_Msgstr_Map.get(id);
					if(stmp == null) 
					{
						tombs.add(i);
					}
					else
					{
						map.putIfAbsent(id, stmp);
					}
				}
				for(int i = tombs.size() - 1; i >= 0; i--)
				{
					ids.remove(tombs.get(i).intValue());
				}
			}
			return map;
		}
		public HashMap<Long, String> queryByKey(String key) 
		{
			HashMap<Long, String> map = new HashMap<Long, String>();
			if(key == null) return map;
			key = key.trim().toLowerCase(Locale.ROOT);
			if(key.length() == 0) return map;
			Vector<Long> ids = Key_Id_Map.get(key);
			if(ids == null) return map;
			Vector<Integer> tombs = new Vector<Integer>();
			int len = ids.size();
			for(int i = 0; i < len; i++)
			{
				Long id = ids.get(i);
				String stmp = Id_Msgstr_Map.get(id);
				if(stmp == null) 
				{
					tombs.add(i);
				}
				else
				{
					map.putIfAbsent(id, stmp);
				}
			}
			for(int i = tombs.size() - 1; i >= 0; i--)
			{
				ids.remove(tombs.get(i).intValue());
			}
			return map;
		}
		public String queryById(long msgstrid) 
		{
			return Id_Msgstr_Map.get(msgstrid);
		}
	}

	private HashMap<Long , GroupChatSaves> GroupChatSavesMap;
	private HashSet<Long> BlackList, BlackGroupList;
	public Vector<GroupChatSaves> getAllGroupChatSaves()
	{
		Vector<GroupChatSaves> rt = new Vector<GroupChatSaves>();
		for (HashMap.Entry<Long, GroupChatSaves> entry : GroupChatSavesMap.entrySet()) 
		{
			rt.add(entry.getValue());
		}
		return rt;
	}
	public Vector<Long> getAllGroups()
	{
		Vector<Long> rt = new Vector<Long>();
		for (HashMap.Entry<Long, GroupChatSaves> entry : GroupChatSavesMap.entrySet()) 
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
		long [] ownerlist = {1145141919L};
		for(long ownerid: ownerlist)
		{
			if(operatorid == ownerid) return true;
		}
		return false;
	}
	public void purgeAll()
	{
		GroupChatSavesMap.clear();
	}
	public void purgeGroup(long groupid)
	{
		GroupChatSavesMap.remove(groupid);
	}
	public boolean registerGroup(long groupid)
	{
		if(isBlackGroup((groupid))) return false;
		if(GroupChatSavesMap.containsKey(groupid)) return false;
		GroupChatSavesMap.put(groupid, new GroupChatSaves());
		return true;
	}
	public boolean purgeAll(long operatorid)
	{
		if(!isOwner(operatorid)) return false;
		GroupChatSavesMap.clear();
		return true;
	}
	public boolean purgeGroup(long operatorid, long groupid)
	{
		if(!isOwner(operatorid)) return false;
		GroupChatSavesMap.remove(groupid);
		return true;
	}
	public boolean removeAllByKey(long operatorid, long groupid, String key)
	{
		if((isBlackUser(operatorid)||isBlackGroup(groupid)) && !isOwner(operatorid)) return false;
		GroupChatSaves gcs = GroupChatSavesMap.get(groupid);
		if(gcs == null) return false;
		return gcs.removeAllByKey(key);
	}
	public boolean removeAllByKeys(long operatorid, long groupid, String[] keys)
	{
		if((isBlackUser(operatorid)||isBlackGroup(groupid)) && !isOwner(operatorid)) return false;
		GroupChatSaves gcs = GroupChatSavesMap.get(groupid);
		if(gcs == null) return false;
		return gcs.removeAllByKeys(keys);
	}
	public boolean removeById(long operatorid, long groupid, long msgstrid)
	{
		if((isBlackUser(operatorid)||isBlackGroup(groupid)) && !isOwner(operatorid)) return false;
		GroupChatSaves gcs = GroupChatSavesMap.get(groupid);
		if(gcs == null) return false;
		return gcs.removeById(msgstrid);
	}
	public long record(long operatorid, long groupid, String[] keys, String msgstr)
	{
		if((isBlackUser(operatorid)||isBlackGroup(groupid)) && !isOwner(operatorid)) return 0;
		GroupChatSaves gcs = GroupChatSavesMap.get(groupid);
		if(gcs == null) 
		{
			gcs = new GroupChatSaves();
			GroupChatSavesMap.put(groupid, gcs);
		}
		return gcs.record(keys, msgstr);
	}
	public HashMap<Long, String> queryByKeys(long operatorid, long groupid, String[] keys)
	{
		if((isBlackUser(operatorid)||isBlackGroup(groupid)) && !isOwner(operatorid)) return null;
		GroupChatSaves gcs = GroupChatSavesMap.get(groupid);
		if(gcs == null) return null;
		return gcs.queryByKeys(keys);
	} 
	public HashMap<Long, String> queryByKey(long operatorid, long groupid, String key)
	{
		if((isBlackUser(operatorid)||isBlackGroup(groupid)) && !isOwner(operatorid)) return null;
		GroupChatSaves gcs = GroupChatSavesMap.get(groupid);
		if(gcs == null) return null;
		return gcs.queryByKey(key);
	} 
	public String queryById(long operatorid, long groupid, long msgstrid)
	{
		if((isBlackUser(operatorid)||isBlackGroup(groupid)) && !isOwner(operatorid)) return null;
		GroupChatSaves gcs = GroupChatSavesMap.get(groupid);
		if(gcs == null) return null;
		return gcs.queryById(msgstrid);
	} 
	public boolean banUser(long userid, boolean set)
	{
		if(userid <= 0) return false;
		if(isOwner(userid)) return false;
		if(set) BlackList.add(userid);
		else BlackList.remove(userid);
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
		if(set) BlackList.add(userid);
		else BlackList.remove(userid);
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
		if(set) BlackGroupList.add(groupid);
		else BlackGroupList.remove(groupid);
		return true;
	}
	public boolean banGroup(long operatorid, long groupid)
	{
		return banGroup(operatorid, groupid,true);
	}
	public boolean isBlackUser(long userid)
	{
		return BlackList.contains(userid);
	}
	public boolean isBlackGroup(long groupid)
	{
		return BlackGroupList.contains(groupid);
	}
	public GroupChatSaveManager()
	{
	}
	public boolean save()
	{
		try
		{
			File filebak  = new File("BlackList.bak");
			if(filebak.exists()) filebak.delete();
			File file  = new File("BlackList");
			if(file.exists()) file.renameTo(filebak);
			ObjectOutputStream otpt = new ObjectOutputStream(new FileOutputStream("BlackList"));
			otpt.writeObject(BlackList);
			otpt.close();
		}
		catch (Exception e) 
		{
			System.out.println("Save BlackList Failed.") ;
		}

		try
		{
			File filebak  = new File("BlackGroupList.bak");
			if(filebak.exists()) filebak.delete();
			File file  = new File("BlackGroupList");
			if(file.exists()) file.renameTo(filebak);
			ObjectOutputStream otpt = new ObjectOutputStream(new FileOutputStream("BlackGroupList"));
			otpt.writeObject(BlackGroupList);
			otpt.close();
		}
		catch (Exception e) 
		{
			System.out.println("Save BlackGroupList Failed.") ;
		}

		try
		{
			File filebak  = new File("GroupChatSavesMap.bak");
			if(filebak.exists()) filebak.delete();
			File file  = new File("GroupChatSavesMap");
			if(file.exists()) file.renameTo(filebak);
			ObjectOutputStream otpt = new ObjectOutputStream(new FileOutputStream("GroupChatSavesMap"));
			otpt.writeObject(GroupChatSavesMap);
			otpt.close();
		}
		catch (Exception e) 
		{
			System.out.println("Save GroupChatSavesMap Failed.") ;
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
			System.out.println("Load BlackList Failed. New BlackList Created.");
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
			System.out.println("Load BlackGroupList Failed. New BlackGroupList Created.");
		}

		try
		{
			ObjectInputStream inpt = new ObjectInputStream(new FileInputStream("GroupChatSavesMap"));
			GroupChatSavesMap = (HashMap<Long , GroupChatSaves>)inpt.readObject();
			inpt.close();
		} 
		catch (Exception e) 
		{
			GroupChatSavesMap = new HashMap<Long , GroupChatSaves>();
			System.out.println("Load GroupChatSavesMap Failed. New GroupChatSavesMap Created.");
			return false;
		}
		return true;
	}

}
