import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class BankClient
{
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException
	{
		int numThreads=Integer.parseInt(args[0]);
		String configFilePath=args[1];

		Scanner configFileIn = new Scanner(Paths.get(configFilePath));

		ArrayList<ServerDirectory> serverList=new ArrayList<ServerDirectory>();
		PrintWriter fw=new PrintWriter("TCPClientLogfile");
		InetAddress ipAddr=null;
		try 
		{
           	ipAddr=InetAddress.getLocalHost();   
        } 
        catch (UnknownHostException ex) 
        {
            ex.printStackTrace();
        }

		while(configFileIn.hasNextLine())
		{

			String configFileLine=configFileIn.nextLine();
			System.out.println(configFileLine);
			String[] configFileArr=configFileLine.split(" ");
			String hostName=configFileArr[0];
			int serverId=Integer.parseInt(configFileArr[1]);
			int serverPort=Integer.parseInt(configFileArr[2]);
			ServerDirectory newServerInfo=new ServerDirectory(hostName, serverId, serverPort);
			serverList.add(newServerInfo);
		}

		System.out.println("TCP CLIENT STARTED");

		// MULTI-THREADED EXECUTION
		ArrayList<Thread> threadList=new ArrayList<Thread>();
		{
			
			ClientLogger cl=new ClientLogger(fw);
			Thread ct=new Thread(cl);
			ct.start();
			//threadList.add(ct);
			
			int k=1;
			for(int i=0;i<numThreads;i++)
			{
				ThreadedEchoClientHandler r=new ThreadedEchoClientHandler(100, serverList, fw, ipAddr);
				Thread t=new Thread(r);
				t.start();
				threadList.add(t);
				k++;
			}
		}

		for (Thread thread:threadList)
		{
            try
            {
                thread.join();
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }

        // HALT OPERATION
        Thread.sleep(15000);

        System.out.println("HALT OPERATION: ");
		Socket incoming=new Socket("localhost", serverList.get(0).getServerPort());
		java.io.OutputStream outStream=incoming.getOutputStream();
		ObjectOutputStream os=new ObjectOutputStream(outStream);
		HaltRequest haltrequest=new HaltRequest("HALT", ipAddr, 9000);
		os.writeObject(haltrequest);
		incoming.close();
  		//fw.close();
	}
}
