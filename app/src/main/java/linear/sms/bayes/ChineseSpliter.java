package linear.sms.bayes;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import java.util.ArrayList;
import java.util.List;

/**
 * 中文分词器
 * Created by ZCYL on 2018/5/10.
 */
public class ChineseSpliter {
    private static JiebaSegmenter sSegmenter = new JiebaSegmenter();

    public static List<SegToken> splitWord(String message){
        List<SegToken> list = sSegmenter.process(message, JiebaSegmenter.SegMode.INDEX);
        List<SegToken> resultList = new ArrayList<>();
        for (int i = 0;i<list.size();i++){
            SegToken segToken = list.get(i);
            if (segToken.word.length()<=1){
                continue;
            }
            resultList.add(segToken);
        }
        return resultList;
    }
}
