package com.clouway.nvuapp.adapter.http.controllers;

import com.clouway.nvuapp.adapter.http.servlet.RsFreemarker;
import com.clouway.nvuapp.core.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class RegisterQuestionnaireHandler implements SecuredHandler {

  private QuestionRepository questionRepository;
  private QuestionnaireRepository testRepository;
  private String questionMessage = "";
  private String questionnaireMessage = "";


  public RegisterQuestionnaireHandler(QuestionRepository questionRepository, QuestionnaireRepository testRepository) {
    this.questionRepository = questionRepository;
    this.testRepository = testRepository;
  }

  @Override
  public Response handle(Request req, Tutor tutor) {
    String amount = req.param("amount");
    String category = req.param("category");
    String modul = req.param("module");
    String subModul = req.param("submodule");
    String theme = req.param("theme");
    String diff = req.param("difficulty");

    Questionnaire questionnaire = testRepository.getLastOrNewQuestionnaire();
    questionnaireMessage = "Въпросник номер " + questionnaire.getId() + " беше обновен.";

    List<Question> queryQuestions = questionRepository.findQuestionsMatching(category, modul, subModul, theme, diff);
    List<Question> filteredQuestions = compareAndExcludeExistingQuestions(questionnaire, queryQuestions);

    if (filteredQuestions.isEmpty()) {
      questionMessage = "Няма регистрирани въпроси от този вид или вече са добавени";
      questionnaireMessage = "Нямаше с какво да обновим";
      return new RsFreemarker("generateQuestionnaire.html",
              ImmutableMap.of("values", questionnaire.getQuestions(), "message", questionnaireMessage, "questionMessage", questionMessage));
    }
    if (filteredQuestions.size() < Integer.valueOf(amount)) {
      questionMessage = "Имаше само " + filteredQuestions.size() + " въпроса от вида който искахте които можем да добавим.";
      return updateQuestionnaireAndRespond(questionnaire, filteredQuestions, questionnaireMessage, questionMessage);
    }
    List<Question> randomQuestions = getRandomQuestions(Integer.valueOf(amount), filteredQuestions);
    return updateQuestionnaireAndRespond(questionnaire, randomQuestions, questionnaireMessage, questionMessage);
  }

  private List<Question> getRandomQuestions(Integer amount, List<Question> fromQuestions) {
    Integer min = 0;
    Integer max = fromQuestions.size() - 1;
    List<Question> result = new LinkedList<>();
    for (int i = 0; i < amount; i++) {
      int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
      result.add(fromQuestions.get(randomNum));
      fromQuestions.remove(randomNum);
      max -= 1;
    }
    return result;
  }

  private Response updateQuestionnaireAndRespond(Questionnaire questionnaire, List<Question> questions, String questionnaireMessage, String questionMessage) {
    questionnaire.addQuestions(questions);
    testRepository.update(questionnaire);
    return new RsFreemarker("generateQuestionnaire.html", ImmutableMap.of("values", questionnaire.getQuestions(), "message", questionnaireMessage, "questionMessage", questionMessage));
  }

  private List<Question> compareAndExcludeExistingQuestions(Questionnaire questionnaire, List<Question> queryQuestions) {
    List<Question> questionnaireQuestions = questionnaire.getQuestions();
    List<Question> forRemoval = Lists.newLinkedList();
    forRemoval.addAll(queryQuestions.stream().filter(questionnaireQuestions::contains).collect(Collectors.toList()));
    forRemoval.forEach(queryQuestions::remove);
    return queryQuestions;
  }
}
