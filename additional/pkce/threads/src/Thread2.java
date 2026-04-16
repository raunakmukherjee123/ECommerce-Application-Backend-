import java.io.*;
import java.util.*;

class MyThread implements Runnable{

    // Method to start Thread
    public void run(){
        for(int i=0;i<5;i++)
        {
            System.out.println("Hello");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

public class Thread2{

    public static void main(String[] args){

        MyThread g1 = new MyThread();

        // initializing Thread Object
        Thread t1 = new Thread(g1);

        // Running Thread
        t1.start();

        for(int i=0;i<5;i++)
        {
            System.out.println("World");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
