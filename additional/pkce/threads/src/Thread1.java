 class Thread1 {
    public static void main(String[] args)
    {
      A t = new A();
      t.start();
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
     static class A extends Thread
    {
        public void run()
        {
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
}
