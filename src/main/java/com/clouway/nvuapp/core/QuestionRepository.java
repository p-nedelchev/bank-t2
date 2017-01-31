package com.clouway.nvuapp.core;

import com.google.common.base.Optional;

import java.util.List;


public interface QuestionRepository {

  List<Question> getQuestions(String tutorId);

  String register(Question question);

  List<Question> getQuestions();

  Optional<Question> findQuestionMatching(String tutorId, String category, String modul, String subModul, String theme, String diff, String question);

  List<Question> findQuestionsMatching(String category, String modul, String subModul, String theme, String diff);

  Optional<Question> getRandomQuestionExcluding(Question question, Questionnaire questionnaire);
}