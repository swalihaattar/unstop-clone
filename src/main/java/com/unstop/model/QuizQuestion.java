package com.unstop.model;

/**
 * QuizQuestion - one MCQ question belonging to a competition.
 * correctOption is stored server-side and NEVER sent to the browser
 * before the quiz is submitted — prevents cheating.
 */
public class QuizQuestion {

    private int    id;
    private int    competitionId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;  // A / B / C / D  — kept server-side
    private int    marks;
    private int    orderNum;

    // Runtime: what the student answered (not stored in this bean, used in result view)
    private String chosenOption;
    private boolean correct;

    public QuizQuestion() {}

    public int getId()                           { return id; }
    public void setId(int id)                    { this.id = id; }

    public int getCompetitionId()                { return competitionId; }
    public void setCompetitionId(int c)          { this.competitionId = c; }

    public String getQuestionText()              { return questionText; }
    public void setQuestionText(String t)        { this.questionText = t; }

    public String getOptionA()                   { return optionA; }
    public void setOptionA(String a)             { this.optionA = a; }

    public String getOptionB()                   { return optionB; }
    public void setOptionB(String b)             { this.optionB = b; }

    public String getOptionC()                   { return optionC; }
    public void setOptionC(String c)             { this.optionC = c; }

    public String getOptionD()                   { return optionD; }
    public void setOptionD(String d)             { this.optionD = d; }

    public String getCorrectOption()             { return correctOption; }
    public void setCorrectOption(String c)       { this.correctOption = c; }

    public int getMarks()                        { return marks; }
    public void setMarks(int m)                  { this.marks = m; }

    public int getOrderNum()                     { return orderNum; }
    public void setOrderNum(int n)               { this.orderNum = n; }

    public String getChosenOption()              { return chosenOption; }
    public void setChosenOption(String c)        { this.chosenOption = c; }

    public boolean isCorrect()                   { return correct; }
    public void setCorrect(boolean c)            { this.correct = c; }
}
