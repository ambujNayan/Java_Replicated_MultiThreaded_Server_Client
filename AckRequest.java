class AckRequest extends Request
{
	private int clock;
	private int serverId;
	private UniversalRequest universalrequest;

	public AckRequest(String requestName, int clock, int serverId, UniversalRequest universalrequest)
	{
		super(requestName);
		this.clock=clock;
		this.serverId=serverId;
		this.universalrequest=universalrequest;
	}

	public UniversalRequest getUniversalRequest()
	{
		return universalrequest;
	}

	public int getClockValue()
	{
		return clock;
	}

	public int getServerId()
	{
		return serverId;
	}
}