class UniversalRequest extends Request implements Comparable<UniversalRequest>
{
	private boolean ownRequest;
	private int clock;
	private int serverId;
	private TransferRequest transferrequest;

	public UniversalRequest(String requestName, boolean ownRequest, int clock, int serverId, TransferRequest transferrequest)
	{
		super(requestName);
		this.ownRequest=ownRequest;
		this.clock=clock;
		this.serverId=serverId;
		this.transferrequest=transferrequest;
	}


	public int compareTo(UniversalRequest other)
	{
		if(clock==other.getClockValue())
		{
			return serverId-other.getServerId();
		}
		else
			return clock-other.getClockValue();
	}

	public void setOwnRequest(boolean ownRequest)
	{
		this.ownRequest=ownRequest;
	}

	public TransferRequest getTransferRequest()
	{
		return transferrequest;
	}

	public boolean getOwnRequest()
	{
		return ownRequest;
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