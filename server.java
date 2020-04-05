// A Java program for a Server 
import java.net.*; 
import java.io.*; 
import java.util.Scanner;
  
public class Server 
{ 
    //initialize socket and input stream 
    private Socket          socket   = null; 
    private ServerSocket    server   = null; 
    private DataInputStream in       =  null;

    //binary authentication array, zero indicates lack of authentication
    private static int[] authent = {0, 0}; 
    private static String password = "";
  
    // constructor with port 
    public Server(int port) 
    { 
       
        try
        { 
            //SOCKET, BIND
            server = new ServerSocket(port); 
            System.out.println("Server started"); 
            //LISTEN
            System.out.println("(listening for connection)"); 
            //ACCEPT
            socket = server.accept(); 
            System.out.println("Hello from VSFTP Service"); 
  
            // takes input from the client socket 
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream())); 
  
            String line = "";
            String command = ""; 
  
            // reads message from client until "DONE" is sent 
            while (!line.equals("DONE")) 
            { 
                try
                { 
                    //converts client input to a string
                    line = in.readUTF(); 
                    //System.out.println("You said " + line);
                    command = line.substring(0,4);//extract first four letters

                    if(line.equals("DONE"))
                        break;

                    //initial authentication sequence
                    if(command.equals("USER")){
                        if(USER(line.substring(5)))
                            authent[0] = 1;
                        continue;
                    }
                    else if(authent[0] == 0){
                        System.out.println("-You must enter a valid USER command");
                        continue;
                    } 
                    else if(command.equals("PASS")){
                        if(PASS(line.substring(5)))
                            authent[1] = 1;
                        continue;    
                    }  
                    else if(authent[1] == 0){
                        System.out.println("-You must enter a valid PASS command");
                        continue;
                    } 
  
                } 
                catch(IOException i) 
                { 
                    System.out.println(i); 
                } 
            } 
            System.out.println("+Goodbye"); 
  
            // CLOSE
            socket.close(); 
            in.close(); 
        } 
        catch(IOException i) 
        { 
            System.out.println(i); 
        } 
    } 

    //searches for username in file and returns corresponding password
    private static String findUsers(String user){
        String password = "";
        String attempt = "";
        File data = new File("users.txt");
        Scanner scan;

        try{
            scan = new Scanner(data);

            while(scan.hasNext()){
                attempt = scan.next(); //only reads user values
                if(attempt.equals(user)){
                    password = scan.next();
                    break;
                }
                else if (scan.hasNext())
                    scan.next(); //skips reading password
            }
        } catch (FileNotFoundException e){
            System.out.println("File was not found");
        }

        return password;
    }

    //authenticates username, prints positive or negative and returns corresponding boolean
    private static boolean USER(String argum){
        String temppass = findUsers(argum);
        if(temppass.equals("")){
            System.out.println("-Invalid user-id, try again");
            return false;
        }   
        else{
            System.out.println("+User-id valid, send password");
            password = temppass;
            return true;
        }
    }

    //authenticates password, prints positive or negative and returns corresponding boolean
    private static boolean PASS(String argum){
        if(argum.equals(password)){
            System.out.println("! Logged in \n Password is ok and you can begin file transfers");
            return true;
        }
        else{
            System.out.println("-Wrong password, try again");
            return false;
        }
    }
  
    public static void main(String args[]) 
    { 
        Server server = new Server(50001); 
    } 
}
