/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package osproject;
import java.util.Scanner;


/**
 *
 * @author Peter
 */
public class OSProject {
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner sc = new Scanner(System.in);
        ProcessController control = new ProcessController();
        boolean flag = true;
        while(flag){
            String ID;
            int size;
            try{
                String command = sc.next();
                switch (command) {
                    case "init":
                        control = new ProcessController();
                        break;
                    case "cr":
                        ID = sc.next();
                        int priority = sc.nextInt();
                        control.create(ID, priority);
                        break;
                    case "de":
                        ID = sc.next();
                        control.destroy(ID);
                        break;
                    case "req":
                        ID = sc.next();
                        size = sc.nextInt();
                        control.request(ID, size);
                        break;
                    case "rel":
                        ID = sc.next();
                        size = sc.nextInt();
                        control.release(ID, size);
                        break;
                    case "to":
                        control.timeout();
                        break;
                    case "exit":
                        flag = false;
                        break;
                    default:
                        System.out.println("Wrong command");
                        break;
                }
            }
            catch(RuntimeException e){
                System.out.println("error");
            }
        }
    }
}
