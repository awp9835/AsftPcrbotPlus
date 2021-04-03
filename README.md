# AsftPcrbotPlus   
公主连结bot增强插件：     
本插件基于AwpBot,可添加AwpBot组件，自动连接到yobot，也可连接到其他bot。    
目前实现的功能：
1. 灵梦记轴插件：记录和查询   
2. 连接到yobot  
3. 查询公会排名，jjc排名追踪提醒   

群内发送 帮助 可获取使用方法。
如果你是主人，请私聊bot发送 主人 获取主人可用的功能。

# 技术引用和API #   
OneBot标准：https://github.com/howmanybots/onebot   
Yobot：https://yobot.win/
Kyouka：https://github.com/Kengxxiao/Kyouka   
竞技场API：参考自
```   
//每查询一次排名要调用至少2次API，请至少间隔1000ms调用。    
https://help.tencentbot.top/enqueue?target_viewer_id=...   
//target_viewer_id：你的游戏内数字ID   
//返回json，request_id用于下一个api，注意返回的key有拼写错误，写成了reqeust_id   
   
https://help.tencentbot.top/query?request_id=...
//request_id：上一个API返回的reqeust_id   
//如果succeed，返回的json如下:   
{   
	"status":"done",   
	"data":
	{
		"user_info":
		{
			"arena_data": 战斗竞技场排名,
			"grand_arena_data": 公主竞技场排名
		}
	}
}   
//也可能需要在1s后重新查询：  
{   
	"status":"queue",   
	"pos": 正在排队查询的人数   
}   

```   
# 部署过程 #   
0. 要运行本插件，你得有个jdk，也就是java，如果连装java都不会我就没什么说的了。所有java文件都使用UTF-8编码，不带BOM，注意别用windows的记事本修改（小心“锘锘锘”“锟斤拷”），用别的编辑器（如VSCode，Notepad++）。
1. 打开GroupChatSaveManager.java 找到public boolean isOwner(long operatorid)，修改主人QQ（后面加L，如1145141919L），多个主人可以用逗号隔开。   
2. 打开AwpBot.java 找到protected void config()，添加或删除要载入的AwpBot组件，默认不需要修改。注意：添加顺序不同会影响钩子的效果。    
3. 本插件自动连线到yobot，如果你的yobot使用了非默认配置，打开UserYobotBridge1.java修改设置。      
4. 编译，详见下文。   
5. 配置并启动onebot（例如mirai），yobot等服务。你需要设置onebot（例如mirai）连接到反向ws: ```ws://localhost:9835/ws```。你不需要再设置onebot连接到yobot，因为本插件已经进行了连接。    
如果你不会配置onebot，可以查看mirai或者yobot配置教程（如果你会部署yobot，只需要把mirai连接的端口从9222改成9835）。   
6. 启动本插件，详见下文。   
# 鉴权设置 # 
本插件：打开AwpBot.java, 在InnerWebSocketServer构造方法中给AccessToken赋值；   
AwpBotBridge连接到其他bot：打开对应的java文件，在config函数中调用setAccessToken方法。   
# 连接到其他bot插件 #   
只需要两个步骤     
1. 创建一个java文件，继承AwpBotBridge类，实现public void config() ，作为AwpBot组件使用。config负责配置ws和其他bot的鉴权，可以通过调用setEventWsUri，setApiWsUri，setUniversalWsUri，setAccessToken四个方法进行设置。你可以参考UserYobotBridge1.java。      
2. 在部署过程的第2步中添加该组件。
# 注意 #   
你可以在AwpBot.java中的public void run()设置自动保存时间。     
你可以修改命令行，更换本插件使用的端口，同时onebot也要更改相关设置。

# 编译 #
如果第一次使用，，需要创建文件夹class，该目录和source同目录   
```   
mkdir class 
```   
如果第一次使用，或者修改了配置，或者增加了组件，需要进行编译：   
 
```   
//如果已经编译过了，并且进行了改动，需要删除class目录下的文件，否则改动有可能失灵

linux命令为   
rm class/*   
```   
如果使用了windows系统，直接双击command文件夹中的compile.cmd。   
如果使用linux系统，compile.sh不好使，需要输入命令:
```   
 cd command
 javac -classpath .:../source:../class:../thirdpartjar/* -encoding UTF-8 -d ../class -sourcepath ../source ../source/AwpBot.java
```   
注意：不同jdk的命令有所差别，如果冒号不行就用分号。  

你也可以在本地编译，再把class文件夹scp到服务器上。  
# 启动 #
在此之前别忘了先启动onebot（例如mirai）和yobot。    
windows下运行很简单，看见那几个cmd没？ （有黑框运行：run.cmd，无黑框运行：jawawrun.cmd）    
linux下运行也很简单，看见那几个sh没？ （终端运行：sh run.sh,  nohup运行：sh nhrun.sh, nohup无nohup.out运行：sh nhnlrun.sh。注意：nohup需要root权限）如果出问题，可以手动输入命令运行，冒号不行就用分号。     
你可以在命令中手动决定本插件的端口，默认9835。   

