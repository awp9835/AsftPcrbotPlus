import java.io.*;
import java.util.*;
import java.net.*;

public class AwpBotWebsiteServer implements Runnable
{
	ServerSocket server = null;
	byte[] bhtml = null;
	byte[] bjs = null;
	byte[] bcss = null;
	public AwpBotWebsiteServer(int port)
	{
		 try 
		 {
			server = new ServerSocket(port);
			if(server == null) 
			{
				System.out.println("端口 "+ port +" 被占用");
			}
			System.out.println("已在端口 "+ port +" 建立ServerSocket");
			
			File file = new File("./resource/AwpBotWebsite.html");
			FileInputStream stream = new FileInputStream(file);
			bhtml = new byte[stream.available()];
			stream.read(bhtml);
			stream.close();
			
			file = new File("./resource/AwpBotWebsite.css");
			stream = new FileInputStream(file);
			bcss = new byte[stream.available()];
			stream.read(bcss);
			stream.close();
			
			file = new File("./resource/AwpBotWebsite.js");
			stream = new FileInputStream(file);
			bjs = new byte[stream.available()];
			stream.read(bjs);
			stream.close();
			
			new Thread(this).start();
        } 
		catch (Exception e) 
		{
			System.out.println("启动失败，端口被占用或资源文件丢失");
            e.printStackTrace();
        }
	}
	public boolean isEnable()
	{
		return server != null;
	}
	protected static void closeSocket(Socket socket) 
	{
        try 
		{
            socket.close();
			System.out.println(socket.toString() + "已断开连接");
        } 
		catch (IOException e) 
		{
            e.printStackTrace();
        }
    }
	protected class ResponseThread implements Runnable
	{
		Socket User;
		public ResponseThread(Socket user)
		{
			User = user;
		}
		public void run()
		{
			try
			{
				System.out.println(User.toString() + "已连接");
				//System.out.println("接收到Http请求");
				BufferedReader reader = new BufferedReader(new InputStreamReader(User.getInputStream()));
				String line = null;
				for(int i = 1;i <= 300;i++)
				{
					if(reader.ready()) 
					{
						line = reader.readLine();
						break;
					}
					Thread.sleep(10);
				}

				if(line == null) 
				{
					PrintStream output = new PrintStream(User.getOutputStream(), true);
					output.println("HTTP/1.0 204 No Content");// 返回204
					output.println();
					output.close();
					closeSocket(User);
					User = null;
					return;
				}
				System.out.println("Http请求; " + line);
				String res = line.substring(line.indexOf('/'),line.lastIndexOf('/') - 5); //第一个"/"到"HTTP/1.1"之间的部分
				//System.out.println("请求资源; "+ res);
				res = URLDecoder.decode(res, "UTF-8");
				String method = line.split(" ")[0];
				if(method == null) method = "";
				//System.out.println("请求方法; " + method);
				//System.out.println("Http Header;");
				/*
				while (true)	
				{
					line = null;
					if(reader.ready()) line = reader.readLine();
					
					if(line == null) break;
					if (line.equals("")) break;
					//System.out.println(line);
				}
				*/
				if (!method.toLowerCase().equals("get"))
				{
					PrintStream output = new PrintStream(User.getOutputStream(), true);
					output.println("HTTP/1.0 204 Forbidden");// 返回204
					output.println();
					output.close();
					closeSocket(User);
					User = null;
					return;
				}

				if (res.endsWith(".html")||res.equals("/")) 
				{
					sendResource(bhtml, User);
					closeSocket(User);
					User = null;
					return;
				} 
				else if (res.endsWith(".js")) 
				{
					sendResource(bjs, User);
					closeSocket(User);
					User = null;
					return;

				} 
				else if (res.endsWith(".css")) 
				{

					sendResource(bcss, User);
					closeSocket(User);
					User = null;
					return;
				} 
				else 
				{
					PrintStream output = new PrintStream(User.getOutputStream(), true);
					output.println("HTTP/1.0 204 No Content");// 返回204
					output.println();
					output.close();
					closeSocket(User);
					User = null;
					return;
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}		
		}
	}
	protected  void sendResource(byte[] buffer, Socket user) 
	{
        try 
		{	
			PrintStream output = new PrintStream(user.getOutputStream());
			output.println("HTTP/1.0 200 OK");// 应答消息
			if(buffer == bhtml)
			{
				output.println("Content-Type;text/html");
			}
			else if(buffer == bjs)
			{
				output.println("Content-Type;application/javascript");
			}
			else if(buffer == bcss)
			{
				output.println("Content-Type;text/css");
			}
			else
			{
				output.println("Content-Type;application/binary");		
			}
			output.println("Content-Length;" + buffer.length);// 字节数					
			output.println();
			//System.out.println(1);
			//Thread.sleep(10001);
			output.write(buffer);
			output.close();
			//System.out.println(2);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
    }
	public void run()
	{
		int cnt = 0;
		while (true) 
		{

			try 
			{
				Socket user = server.accept();
				if (user == null) continue;
				new Thread(new ResponseThread(user)).start();
				cnt++;
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				System.out.println(e.getLocalizedMessage());
			}
			
			if(cnt == 1000)
			{
				cnt = 0;
				System.gc();
				System.out.println("已连接1000次，触发GC");
			}			
        }	
	}
	public static void main(String[] args)
	{
		AwpBotWebsiteServer sv = null;
		try
		{
			 sv = new AwpBotWebsiteServer(Integer.parseInt(args[0]));
		}
		catch(Exception e)
		{
			 sv  = new AwpBotWebsiteServer(9836);
		}
		if(!sv.isEnable()) System.exit(0);
	}
}