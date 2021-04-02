public interface AwpBotComponent
{
	public boolean save(AwpBotInterface bot); //return succeed
	public boolean load(AwpBotInterface bot); //return no except
	public String handle(String event, AwpBotInterface bot); //return global command string
	public String getComponentName();
}