package custom;

import custom.tester;

public class monitor implements Runnable{

  private tester myclass;

  public monitor(tester myclass) {
    this.myclass = myclass;
  }

  public void run(){
    while (true){
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("Current Password: " + myclass.buildPassword());
    }
  }
}
