package sig;

import java.util.Arrays;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;

public class TestKernel extends Kernel{

    int[] a,b;
    float[] sum;
    boolean test1,test2;
    final int val=55555555;

    TestKernel(int[] a,int[] b,float[] sum,boolean test1,boolean test2) {
        this.a=a;
        this.b=b;
        this.sum=sum;
        this.test1=test1;
        this.test2=test2;
    }

    /*public static void main(String[] args) {
        int size = 1024;

        final int[] a = new int[size];
        int[] b = new int[size];
  
        for (int i = 0; i < size; i++) {
           a[i] = (int) (Math.random() * 100);
           b[i] = (int) (Math.random() * 100);
        }
  
        float[] sum = new float[size];
  
        TestKernel kernel = new TestKernel(a,b,sum,true,false);
  
        //System.out.println("Start...");
        kernel.execute(Range.create(size));
        //System.out.println("Running...");
        //size=5;
        kernel.b[0]=50;
        kernel.b[1]=45;
        long timer1=System.nanoTime();
        for (int i=0;i<900;i++) {
            kernel.execute(Range.create(size));
        }
  

        System.out.println((System.nanoTime()-timer1)+"ns");
        kernel.dispose();
        System.out.println(Arrays.toString(sum));
        timer1=System.nanoTime();
        for (int j=0;j<900;j++) {
            for (int i=0;i<sum.length;i++) {
                sum[i]=a[i]+b[i];
            }
        }
        System.out.println((System.nanoTime()-timer1)+"ns");
    }*/

    @Override
    public void run() {
        int gid = getGlobalId();
        sum[gid] = a[gid]+b[gid];
    }    

    void addExtra(int numb,int id) {
        a[id]+=numb;
        b[id]+=numb;
    }
}
