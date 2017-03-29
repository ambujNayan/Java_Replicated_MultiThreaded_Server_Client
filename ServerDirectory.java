class ServerDirectory
{
	private String hostName;
	private int serverId;
	private int serverPort;

	public ServerDirectory(String hostName, int serverId, int serverPort)
	{
		this.hostName=hostName;
		this.serverId=serverId;
		this.serverPort=serverPort;
	}

	public String getHostName()
	{
		return hostName;
	}

	public int getServerId()
	{
		return serverId;
	}

	public int getServerPort()
	{
		return serverPort;
	}
}