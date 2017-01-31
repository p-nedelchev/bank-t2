package com.clouway.nvuapp.adapter.http.controllers;

import com.clouway.nvuapp.core.QuestionRepository;
import com.clouway.nvuapp.core.Questionnaire;
import com.google.common.base.Optional;
import com.clouway.nvuapp.core.Question;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class InMemoryQuestionRepository implements QuestionRepository {
  private final List<Question> questions;

  public InMemoryQuestionRepository(List<Question> questions) {
    this.questions = questions;
  }

  @Override
  public List<Question> getQuestions(String tutorId) {
    List<Question> result = new LinkedList<>();
    for (Question each : questions) {
      if (each.getTutorId().equals(tutorId)) {
        result.add(each);
      }
    }
    return result;
  }

  @Override
  public String register(Question question) {
    if (questions.contains(question)) {
      return "Вече има такъв регистриран въпрос.";
    }
    questions.add(question);
    return "Въпросът е регистриран успешно.";
  }

  @Override
  public List<Question> getQuestions() {
    return questions;
  }

  @Override
  public Optional<Question> findQuestionMatching(String tutorId, String category, String model, String subModel, String theme, String diff, String question) {
    return Optional.absent();
  }

  @Override
  public List<Question> findQuestionsMatching(String category, String modul, String subModul, String theme, String diff) {
    List<Question> result = new LinkedList<>();
    for (Question question : questions) {
      if (question.getCategory().equals(category)
              && question.getModule().equals(Integer.valueOf(modul))
              && question.getSubModule().equals(Integer.valueOf(subModul))
              && question.getTheme().equals(Integer.valueOf(theme))
              && question.getDifficulty().equals(Integer.valueOf(diff))) {
        result.add(question);
      }
    }
    return result;
  }

  @Override
  public Optional<Question> getRandomQuestionExcluding(Question question, Questionnaire questionnaire) {
    List<Question> availableQuestions = findQuestionsMatching(
            question.getCategory(),
            String.valueOf(question.getModule()),
            String.valueOf(question.getSubModule()),
            String.valueOf(question.getTheme()),
            String.valueOf(question.getDifficulty()));
    availableQuestions.remove(question);
    availableQuestions.removeAll(questionnaire.getQuestions());
    if(availableQuestions.isEmpty()) {
      return Optional.absent();
    }
    int randomIndex = ThreadLocalRandom.current().nextInt(availableQuestions.size());
    return Optional.of(availableQuestions.get(randomIndex));
  }
}

