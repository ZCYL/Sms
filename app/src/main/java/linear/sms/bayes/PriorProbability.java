package linear.sms.bayes;

import android.content.Context;

import com.huaban.analysis.jieba.SegToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 贝叶斯的先行效率，负责打开应用时初始化数据
 * Created by ZCYL on 2018/5/10.
 */
public class PriorProbability {

    private List<String> trainHamList = new LinkedList<>();//训练出来的有害关键字列表,可重复保存相同字符
    private List<String> trainNormalList = new LinkedList<>();//训练出来的正常关键字列表,可重复保存相同字符

    public static PriorProbability instance = new PriorProbability();
    private boolean isInitFinish = false;

    private PriorProbability() {
    }

    // TODO: 2018/5/11 应该把文件放置在SD卡，允许用户添加新的垃圾短信？或者是在用户目录额外创建新的文件保存
    public void init(Context context) {
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = context.getAssets().open("normal.txt");
//                    FileInputStream fileInputStream = new FileInputStream("C:\\Users\\acer-PC\\Desktop\\机器学习SMS\\normal.txt");
                    InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader br = new BufferedReader(isr);
                    List<String> lineList = new ArrayList<>();
                    String line;
                    while ((line = br.readLine()) != null) {
                        lineList.add(line);
                    }
                    br.close();
                    isr.close();
                    divideTrainDataToWord(lineList);
                    isInitFinish = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 判断是不是垃圾短信
     *
     * @param content 短信内容
     */
    public void isHarmMessage(String content, OnBayesAnalyseFinishListener listener) {
        Schedulers.computation()
                .scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        while (!isInitFinish) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        double hame = getProbality(content, trainHamList);
                        double normal = getProbality(content, trainNormalList);
                        double c_p = normal / (normal + hame);
                        normal = normal * c_p;
                        hame = hame * (1 - c_p);
                        boolean isHarm = hame > normal;
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                            @Override
                            public void run() {
                                listener.onFinish(isHarm);
                            }
                        });
                    }
                });
    }

    /**
     * 将所有训练数据拆分为单词 ,0为正常短信，1为垃圾短信 比如 “0	加拿大平面设计师JoeyCamacho3D雕塑作品”
     */
    private void divideTrainDataToWord(List<String> trainList) {
        for (String s : trainList) {
            String symbol = s.substring(0, 1);
            String content = s.substring(s.indexOf("\t") + 1);
            if ("0".equals(symbol)) {
                dividerText(content, trainNormalList);
            } else {
                dividerText(content, trainHamList);
            }
        }
    }

    /**
     * 将每条短信拆分为单词，存放到关键词列表
     */
    private void dividerText(String string, List<String> list) {
        List<SegToken> tokenList = ChineseSpliter.splitWord(string);
        for (SegToken token : tokenList) {
            list.add(token.word);
        }
    }

    /**
     * 求取概率
     */
    private double getProbality(String content, List<String> keyList) {
        List<SegToken> targetVec = ChineseSpliter.splitWord(content);
        double p = 1;
        for (SegToken segToken : targetVec) {
            long sum = 0;
            String t = segToken.word;
            for (String word : keyList) {
                if (word.equals(t)) {
                    sum++;
                }
            }
            p *= (sum + 1) / (double) (targetVec.size() + targetVec.size());
        }

        return p;
    }

    public interface OnBayesAnalyseFinishListener {
        void onFinish(Boolean isHarmMessage);
    }

    public static void main(String[] args) {
//        PriorProbability p = new PriorProbability();
//        p.init(new OnBayesLoadFinishListener() {
//            @Override
//            public void onFinish() {
//                System.out.println("开始" + System.currentTimeMillis());
//                String[] ss = new String[]{
//                        "电梯事故频发中国这是怎么了",//0
//                        "\"韩国媒体称：\"\"空客波音随着中国飞机市场的膨胀\"",//0
//                        "x.x女人节，预祝姐姐在这重要的日子里节日快乐[鼓掌][鼓掌]，优美x.x节全场秋冬款优惠x折，欢迎选购！?? ?? 琼海金海路xx号",//1
//                        "东莞天富柯式印刷有限公司欢迎您!专业提供彩盒"//1
//                };
//                for (String s : ss) {
//                    System.out.println(p.isHarmMessage(s) + " " + s);
//                }
//                System.out.println("结束"+System.currentTimeMillis());
//            }
//        });
//        try {
//            Thread.sleep(30_000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
