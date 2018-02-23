package com.sihwan.iteach12.naverrecognitionapitest;

/**
 * Created by iteach12 on 2018. 2. 23..
 */
//[ ] 진도정보 : 이번 학습의 코드명 (상수?)
//[ ] 난이도 : 최상, 상중하 네 가지난이도.
//[ ] 점수 : 문제별로 점수 부여. 최종 학습 종료 시 도달해야 하는 점수가 있음. 점수 도달 정도에 따라 백분율로 점수. 100점 맞으면 보너스? ㅋㅋ
//[ ] 틀린문제 : 이번 학습에서 틀린 문제의 번호 저장
//[ ] 맞춘문제 : null
public class MyCurrentStudy {

    //이번 학습의 코드명
    int currentProgress;

    //난이도
    int currentDifficult;

    //이번 학습의 최소 도달 목표 점수
    int goalPoint;

    //학습자가 얻은 최종 점수
    int userEarnPoint;

    //보너스점수
    int bonusPoint;

    //틀린 문제
    int currentWrongAnswer;

    public MyCurrentStudy(int currentProgress, int currentDifficult, int goalPoint) {
        this.currentProgress = currentProgress;
        this.currentDifficult = currentDifficult;
        this.goalPoint = goalPoint;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public int getCurrentDifficult() {
        return currentDifficult;
    }

    public void setCurrentDifficult(int currentDifficult) {
        this.currentDifficult = currentDifficult;
    }

    public int getGoalPoint() {
        return goalPoint;
    }

    public void setGoalPoint(int goalPoint) {
        this.goalPoint = goalPoint;
    }

    public int getUserEarnPoint() {
        return userEarnPoint;
    }

    public void setUserEarnPoint(int userEarnPoint) {
        this.userEarnPoint = userEarnPoint;
    }

    public int getBonusPoint() {
        return bonusPoint;
    }

    public void setBonusPoint(int bonusPoint) {
        this.bonusPoint = bonusPoint;
    }

    public int getCurrentWrongAnswer() {
        return currentWrongAnswer;
    }

    public void setCurrentWrongAnswer(int currentWrongAnswer) {
        this.currentWrongAnswer = currentWrongAnswer;
    }
}
