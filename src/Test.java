
public class Test {
    private int ChrNum = 6;
    private String[] ipop = new String[ChrNum];	 	//一个种群中染色体总数
    public static final int GENE = 35;//插入位置数量
    private int generation = 0; 	//染色体代号
    private double bestfitness = Double.MIN_VALUE;  //函数最优解
    private int bestgenerations;   	//所有子代与父代中最好的染色体
    private String beststr;   		//最优解的染色体的二进制码

    /**
     * 初始化一条染色体（用二进制字符串表示）
     */
    private String initChr() {
        String res = "";
        for (int i = 0; i < GENE; i++) {
            if (Math.random() >= 0.5) {
                res += "0";
            } else {
                res += "1";
            }
        }
        return res;
    }

    /**
     * 初始化一个种群(6条染色体)
     */
    private String[] initPop() {
        String[] ipop = new String[ChrNum];
        for (int i = 0; i < ChrNum; i++) {
            ipop[i] = initChr();
        }
        return ipop;
    }

    /**
     * 计算fitness
     */
    private double[] calculatefitnessvalue(String str) {


        int a = Integer.parseInt(str.substring(0, 7), 2);
        int b = Integer.parseInt(str.substring(7, 14), 2);
        int c = Integer.parseInt(str.substring(14, 21), 2);
        int d = Integer.parseInt(str.substring(21, 28), 2);
        int e = Integer.parseInt(str.substring(28, 35), 2);

        double x =  a*100  / (Math.pow(2, 7) - 1);
        double y =  b*100  / (Math.pow(2, 7) - 1);
        double z =  c*100  / (Math.pow(2, 7) - 1);
        double v =  d*100  / (Math.pow(2, 7) - 1);
        double w =  e*100  / (Math.pow(2, 7) - 1);


        //需优化的函数
        double fitness = x+y+z+v+w;

        double[] returns = {x,y,z,v,w, fitness };
        return returns;

    }

    /**
     * 轮盘选择
     * 计算群体上每个个体的适应度值;
     * 按由个体适应度值所决定的某个规则选择将进入下一代的个体;
     */
    private void select() {
        double evals[] = new double[ChrNum]; // 所有染色体适应值
        double p[] = new double[ChrNum]; // 各染色体选择概率
        double q[] = new double[ChrNum]; // 累计概率
        double F = 0; // 累计适应值总和
        for (int i = 0; i < ChrNum; i++) {
            evals[i] = calculatefitnessvalue(ipop[i])[5];
            if (evals[i] > bestfitness){  // 记录下种群中的最小值，即最优解
                bestfitness = evals[i];
                bestgenerations = generation;
                beststr = ipop[i];
            }

            F = F + evals[i]; // 所有染色体适应值总和
        }

        for (int i = 0; i < ChrNum; i++) {
            p[i] = evals[i] / F;
            if (i == 0)
                q[i] = p[i];
            else {
                q[i] = q[i - 1] + p[i];
            }
        }
        for (int i = 0; i < ChrNum; i++) {
            double r = Math.random();
            if (r <= q[0]) {
                ipop[i] = ipop[0];
            } else {
                for (int j = 1; j < ChrNum; j++) {
                    if (r < q[j]) {
                        ipop[i] = ipop[j];
                    }
                }
            }
        }
    }

    /**
     * 交叉操作 交叉率为60%，平均为60%的染色体进行交叉
     */
    private void cross() {
        String temp1, temp2;
        for (int i = 0; i < ChrNum; i++) {
            if (Math.random() < 0.60) {
                int pos = (int)(Math.random()*GENE)+1;     //pos位点前后二进制串交叉
                temp1 = ipop[i].substring(0, pos) + ipop[(i + 1) % ChrNum].substring(pos);
                temp2 = ipop[(i + 1) % ChrNum].substring(0, pos) + ipop[i].substring(pos);
                ipop[i] = temp1;
                ipop[(i + 1) / ChrNum] = temp2;
            }
        }
    }

    /**
     * 基因突变操作 1%基因变异
     */
    private void mutation() {
        for (int i = 0; i < 4; i++) {
            int num = (int) (Math.random() * GENE * ChrNum + 1);
            int chromosomeNum = (int) (num / GENE) + 1; // 染色体号

            int mutationNum = num - (chromosomeNum - 1) * GENE; // 基因号
            if (mutationNum == 0)
                mutationNum = 1;
            chromosomeNum = chromosomeNum - 1;
            if (chromosomeNum >= ChrNum)
                chromosomeNum = 5;
            String temp;
            String a;   //记录变异位点变异后的编码
            if (ipop[chromosomeNum].charAt(mutationNum - 1) == '0') {    //当变异位点为0时
                a = "1";
            } else {
                a = "0";
            }
            //当变异位点在首、中段和尾时的突变情况
            if (mutationNum == 1) {
                temp = a + ipop[chromosomeNum].substring(mutationNum);
            } else {
                if (mutationNum != GENE) {
                    temp = ipop[chromosomeNum].substring(0, mutationNum -1) + a
                            + ipop[chromosomeNum].substring(mutationNum);
                } else {
                    temp = ipop[chromosomeNum].substring(0, mutationNum - 1) + a;
                }
            }
            //记录下变异后的染色体
            ipop[chromosomeNum] = temp;
        }
    }


    public static void main(String[] args) {
        Test Noisee = new Test();
        Noisee.ipop = Noisee.initPop();//产生初始种群
        String str = "";

        //迭代次数
        for (int i = 0; i < 100000; i++) {
            Noisee.select();
            Noisee.cross();
            Noisee.mutation();
            Noisee.generation = i;
        }

        double[] x = Noisee.calculatefitnessvalue(Noisee.beststr);

        str = "最优fitness ; " + (int)Noisee.bestfitness + '\n' + "第"
                + Noisee.bestgenerations + "次迭代:(" + (int)x[0] + " , " + (int)x[1] + " , "
                + (int)x[2] + " , " + (int)x[3] + " , " + (int)x[4] + ")" ;

        System.out.println(str);

    }
}
