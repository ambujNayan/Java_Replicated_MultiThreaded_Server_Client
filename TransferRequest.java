import java.net.InetAddress;
import java.net.UnknownHostException;
class TransferRequest extends Request
{
	private int fromUID;
	private int toUID;
	private int amount;
	private InetAddress clientName;
	private int clientPort;
	

	public TransferRequest(String requestName, int fromUID, int toUID, int amount, InetAddress clientName, int clientPort)
	{
		super(requestName);
		this.fromUID=fromUID;
		this.toUID=toUID;
		this.amount=amount;
		this.clientName=clientName;
		this.clientPort=clientPort;
	}

	public int getFromUID()
	{
		return fromUID;
	}

	public int getToUID()
	{
		return toUID;
	}

	public int getAmount()
	{
		return amount;
	}

	public InetAddress getclientName()
	{
		return clientName;
	}

	public int getPort()
	{
		return clientPort;
	}
	
}