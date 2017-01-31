package com.clouway.nvuapp.core;

import com.google.common.base.Optional;

import java.util.*;

public class InMemoryQuestionRepository implements QuestionRepository {

  private final Map<String, List<Question>> tutorToQuestionsListMap;

  public InMemoryQuestionRepository(Map<String, List<Question>> tutorToQuestionsListMap) {
    this.tutorToQuestionsListMap = tutorToQuestionsListMap;
  }

  @Override
  public List<Question> getQuestions(String tutorId) {
    if (tutorToQuestionsListMap.containsKey(tutorId)) {
      return tutorToQuestionsListMap.get(tutorId);
    }
    return Collections.emptyList();
  }

  @Override
  public String register(Question question) {
    List<Question> questionsForTutor = tutorToQuestionsListMap.get(question.getTutorId());
    questionsForTutor.add(question);
    tutorToQuestionsListMap.put(question.getTutorId(), questionsForTutor);
    return null;
  }

  @Override
  public Optional<Question> findQuestionMatching(String tutorId, String category, String model, String subModel, String theme, String diff, String question) {
    return Optional.absent();
  }

  @Override
  public List<Question> findQuestionsMatching( String category, String modul, String subModul, String theme, String diff) {
    return Collections.emptyList();
  }

  @Override
  public Optional<Question> getRandomQuestionExcluding(Question question, Questionnaire questionnaire) {
    return null;
  }

  @Override
    public List<Question> getQuestions() {
        List<Question> list = new LinkedList<>();
        for (String tutorId : tutorToQuestionsListMap.keySet()) {
            for (Question question : tutorToQuestionsListMap.get(tutorId)) {
                list.add(question);
            }
        }
        return list;
    }
}