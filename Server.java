import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.io.PrintWriter;
import java.util.Date;

public class Server
{
  public static void main(String[] args)
    throws IOException
  {
    int localServerId=Integer.parseInt(args[0]);
    String configFilePath=args[1];

    Scanner configFileIn = new Scanner(Paths.get(configFilePath));

    ArrayList<ServerDirectory> serverList=new ArrayList<ServerDirectory>();
    int noOfRequests=0;
    String localHostName="";
    int localServerPort=0;

    while(configFileIn.hasNextLine())
    {

      String configFileLine=configFileIn.nextLine();
      System.out.println(configFileLine);
      String[] configFileArr=configFileLine.split(" ");
      String hostName=configFileArr[0];
      int serverId=Integer.parseInt(configFileArr[1]);
      int serverPort=Integer.parseInt(configFileArr[2]);

      if(serverId!=localServerId)
      {
        ServerDirectory newServerInfo=new ServerDirectory(hostName, serverId, serverPort);
        serverList.add(newServerInfo);
      }
      else
      {
        localHostName=hostName;
        localServerPort=serverPort;
      }
    }

    Bank bank=new Bank();
    PrintWriter fw=new PrintWriter("TCPServerLogfile "+localServerId);
    fw.append("SERVER LOG FILE\n");

    for(int i=0; i<10; i++)
    {
      int UID=bank.CreateAccount();
      bank.Deposit(UID, 1000);
      System.out.println("The accound "+UID+" has balance: "+bank.GetBalance(UID));
    }

    LamportClock lamportClock=new LamportClock();
    //LocalQueue localQueue=new LocalQueue();
    PriorityBlockingQueue<UniversalRequest> localQueue=new PriorityBlockingQueue<UniversalRequest>();
    // HashMap localHashMap = new HashMap();

    List<Integer> ackList=Collections.synchronizedList(new ArrayList<Integer>());

    for(int i=0; i<serverList.size()+1; i++)
    	ackList.add(i, -1);

    try
    {
      ServerSocket s=new ServerSocket(localServerPort);
      Date date=new Date();
      System.out.println("TCP SERVER " + localServerId + " STARTED");
      List<Thread> threadlist = new ArrayList<Thread>();
      for (;;)
      {
        Socket incoming=s.accept();
        AccountHandler r=new AccountHandler(incoming, bank, serverList, lamportClock, localQueue, ackList, localServerId, s, fw, date);
        noOfRequests++;
        Thread t=new Thread(r);
        t.start();
        threadlist.add(t);

        if(noOfRequests>=2400)
          {
            System.out.println("no of requests got 2400");
            break;
          }
      }
      try{
          for(Thread each : threadlist){
          each.join();
        }
        s.close();
      }catch(InterruptedException e){

      }
      for(int m=0;m<10;m++){
          System.out.println("The accound "+(m+1)+" has balance: "+bank.GetBalance(m+1));
      }
    }
    catch (IOException localIOException)
    {
      //localIOException.printStackTrace();
    }
  }
}
