import java.net.InetAddress;
import java.net.UnknownHostException;

class HaltRequest extends Request
{
	public HaltRequest(String requestName, InetAddress clientName, int clientPort, int numThreads)
	{
		super(requestName, clientName, clientPort, numThreads);
	}
}