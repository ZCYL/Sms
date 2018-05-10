//
// Created by acer-PC on 2018/5/10.
//

#include <iostream>
#include <stdlib.h>
#include <vector>
#include <string>
#include <iterator>
#include <fstream>
#include <sstream>

using namespace std;

const string trainFileName = "trainData.txt";
const string testFileName = "testData.txt";

//训练集中正常短信的条数
long int hamsum = 0;
//训练集中垃圾短信的条数
long int spamsum = 0;
//测试集正确分类的条数
long int corsum = 0;
//测试集错误分类的条数
long int errsum = 0;


//将每条短信拆分为单词
void divideText(string str, vector<string> &wordVector){

    string temp = "";
    for(int i = 0; str[i] != '\0'; i++)
    {
        if(str[i] == ' ' && temp != "")
        {
            wordVector.push_back(temp);
            temp = "";

        }
        else
            temp.append(1, str[i]);
    }
    if(temp != ""){
        wordVector.push_back(temp);
    }
}

//将所有训练数据拆分为单词
void divideTrainDataToWord(vector<string> &trainData,
                           vector<string> &train_ham_word_Data,
                           vector<string> &train_spam_word_Data)
{
    while(!trainData.empty())
    {
        string str = trainData.back();
        if(str[0] == 'h')
        {
            divideText(str.substr(4) , train_ham_word_Data);
            hamsum++;
        }
        else
        {
            divideText(str.substr(5) , train_spam_word_Data);
            spamsum++;
        }
        trainData.pop_back();
    }
}

//求取概率
double getProbality(vector<string> &targetVec, vector<string> &vec){
    double p = 1;
    for(int i =0; i <targetVec.size(); i++){
        long int sum = 0;
        for(int j = 0; j < vec.size(); j++){
            if(targetVec[i] == vec[j]){
                sum++;
            }
        }
        p *= (sum + 1) / (double)(vec.size() + targetVec.size());
    }

    return p;
}

// 读取文件
void inputData(string filename, vector<string> &data){
    ifstream infile(filename);
    if(!infile){
        cout <<"文件不存在！"<<endl;
        return;
    }
    while(!infile.eof())
    {
        string str;
        getline(infile, str);
        data.push_back(str);
    }
}


int main()
{
    vector<string> trainData;
    vector<string> testData;
    vector<string> train_ham_word_Data;
    vector<string> train_spam_word_Data;

    inputData(trainFileName, trainData);
    inputData(testFileName, testData);

    divideTrainDataToWord(trainData, train_ham_word_Data, train_spam_word_Data);

    for(int i = 0; i < testData.size(); i++)
    {
        cout<<"第"<<i<<"次迭代"<<endl;
        string str = testData.at(i);
        vector<string> target;
        divideText(str[0] == 'h' ? str.substr(4) : str.substr(5) , target);
        double hamp = getProbality(target, train_ham_word_Data);
        double spamp = getProbality(target, train_spam_word_Data);

        double c_p = (double)hamsum / (hamsum + spamsum);
        hamp = hamp * c_p;
        spamp = spamp * (1 - c_p);
        if((str[0] == 'h' && hamp >= spamp) || (str[0] == 's' && hamp <= spamp)){
            corsum++;
        }
        else {
            errsum++;
        }

    }
    cout<<"正确分类条数："<< corsum <<"    错误分类条数："<< "          "<<errsum<<endl;
    cout<<"正确率："<<corsum / (double)(corsum + errsum)<<endl;
    system("pause");
    return 0;

}