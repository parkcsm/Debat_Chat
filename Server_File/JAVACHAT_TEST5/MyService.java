import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

public class MyService { 

	static String mixed_roomclist, arranged_roomclist;
	public static HashMap<Object, ServerThread> HEachCthread = new HashMap<Object, ServerThread>();
	public static HashMap<Object, ArrayList<ServerThread>> HRoomCthread = new HashMap<Object, ArrayList<ServerThread>>();

	
	private ArrayList<ServerThread> allclist;
	private static ArrayList<ServerThread> Roomclist;
	
	
	
	private ServerSocket ss;
	private String reip;

	public MyService() {
		try {
			ss = new ServerSocket(9999);
			System.out.println("Server Start!");
			allclist = new ArrayList<ServerThread>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exe() {
		while (true) {

			Socket s = null;
			try {
				s = ss.accept();
				String reip = s.getInetAddress().getHostAddress();
				// System.out.println("Log : " + reip);

				ServerThread ct = new ServerThread(s, this);

				// System.out.println("Thread getName->" + ct.getName());
				// System.out.println("Thread getId->" + ct.getId());

				allclist.add(ct);

				ct.start();
				System.out.println("Current number of Clients :" + allclist.size());
			} catch (IOException e) {
				e.printStackTrace();
			}
			reip = s.getInetAddress().getHostAddress();
			// System.out.println("Ip : " + reip);

		}
	}

	public static void main(String[] args) {
		new MyService().exe();
	}

	public void sendMsg(String type, String type2, String receiver, String sender, String message) {

		StringBuffer sb = new StringBuffer();
		sb.append(type).append("/");
		sb.append(type2).append("/");
		sb.append(receiver).append("/");
		sb.append(sender).append("/");
		sb.append(message);

		
		
		if (HEachCthread.get(sender) == null) { // HeachCthread Save
			for (ServerThread e : allclist) {
				if (e.getName() == sender) {
					HEachCthread.put(sender, e);
					if (HEachCthread.get(sender).getName() == sender) {
						System.out.println("successfully hm.put");
						System.out.println("hm.get(" + sender + ") = " + HEachCthread.get(sender).getName());
					} else {
						System.out.println("hm.put failed");
					}
					break;
				}
			}
		}
	
		
		if(type2.equals("open_chat")) {
			for (ServerThread e : allclist) {
				if (!e.getName().equals(sender)) {
						 e.getPw().println(sb.toString());
        System.out.println(e.getName()+" : successfully sent : " + sb.toString());
					
				}
			}
			
		} else if(type2.equals("broad_cast")) {
			for (ServerThread e : allclist) {
				if (!e.getName().equals(sender)) {
						 e.getPw().println(sb.toString());
        System.out.println(e.getName()+" : successfully sent : " + sb.toString());
					
				}
			}
			
		} else if(type2.equals("chat")) {
		Roomclist = new ArrayList<ServerThread>();
		mixed_roomclist = sender + "]" + receiver;
		arranged_roomclist = SeperationAndReOrder(mixed_roomclist);
		DoBackground(arranged_roomclist); // Suppose that ThreadsDeclare and thread start! // Add EachThreads from Array
        							      // to Roomclist
		for (ServerThread e : Roomclist) {
			if (e != null) {
				if (!e.getName().equals(sender)) {
					 e.getPw().println(sb.toString());
				}
			}
		}
		
		} else {
			System.out.println("else exeption other than chat/open_chat/broad_cast");
		}
		
		if (type.equals("roomout")) { // If Client sends roomout/// message to Server
										// 1)disconnect server-socket
										// 2)release that user from allclist
			System.out.println("roomout!!");
			try {
				((ServerThread) HEachCthread.get(sender)).getSocket().close();
				System.out.println(sender + " socket has been closed");
				for (int i = 0; i < allclist.size(); i++) {
					if (allclist.get(i).getName().equals(sender)) {
						allclist.remove(i);
						HEachCthread.put(sender, null);
						System.out.println(sender + "has been removed from allclist");
						break;
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

//		 for (ServerThread e : allclist) { // e == each client of everyone
//		 System.out.println("///////////////////////////////////////////////////");
//		 System.out.println(e.getName() + "(thread) travel start!!");
//		 if (!e.getName().equals(sender)) {
//		 System.out.println(e.getName() + "(thread) !=" + "sender(" + sender + ")!! so
//		 came in from clist");
//		
//		 // don't send me own;
//		 // send to client who have only seperation value;
//		 for (int i = 0; i < UserNum; i++) {
//		 System.out.println("Now!! e.get(Name)= " + e.getName() + "/ test[" + i + "]="
//		 + test[i]);
//		 if (e.getName().equals(test[i])) {
//		 e.getPw().println(sb.toString());
//		 System.out.println("receiver send! : " + "because e.get(Name)=" + e.getName()
//		 + "/ equal"
//		 + "/ test[" + i + "]=" + test[i]); // test[i]->each
//		 // seperated
//		 // receiver
//		 } else {
//		 System.out.println("receiver rejected! : " + "because e.get(Name)=" +
//		 e.getName()
//		 + "/ not equal" + "/ test[" + i + "]=" + test[i]); // test[i]->each
//		 // seperated
//		 // receiver
//		 }
//		 }
//		 } else {
//		 System.out.println(e.getName() + "(thread) ==" + "sender(" + sender + ")!! so
//		 out! from for sequence");
//		 }
//		 }
	}

	private static String SeperationAndReOrder(String string) {

		StringTokenizer str = new StringTokenizer(string, "]");
		int UserNum = str.countTokens();
		String[] test = new String[UserNum];
		for (int i = 0; i < UserNum; i++) {
			test[i] = str.nextToken();
			// System.out.println("test[" + i + "] = " + test[i]);
		}

		String sum;
		sum = SplitAndMigrateToString(test, UserNum);
		System.out.println("initial form : " + string);
		System.out.println("rearranged form : " + sum);
		return sum;
	}

	private static String SplitAndMigrateToString(String[] test, int ArraySize) {
		Arrays.sort(test);

		String sum = "";
		for (int i = 0; i < ArraySize; i++) {
			sum += test[i] + "]";
		}
		return sum;
	}

	private static void DoBackground(String string) {
		StringTokenizer str = new StringTokenizer(string, "]");
		int UserNum = str.countTokens();
		String[] test = new String[UserNum];
		for (int i = 0; i < UserNum; i++) {
			test[i] = str.nextToken();
			Roomclist.add(HEachCthread.get(test[i]));
			System.out.println("Thread" + HEachCthread.get(test[i]) + "=>In Roomlist added");
		}
		HRoomCthread.put(string, Roomclist);
		System.out.println("All Threads have been added to Roomclists!! Result =>" + HRoomCthread.get(string));
	}
}
