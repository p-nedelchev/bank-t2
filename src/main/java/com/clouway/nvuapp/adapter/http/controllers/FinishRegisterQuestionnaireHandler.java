package com.clouway.nvuapp.adapter.http.controllers;

import com.clouway.nvuapp.adapter.http.servlet.RsFreemarker;
import com.clouway.nvuapp.core.*;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class FinishRegisterQuestionnaireHandler implements SecuredHandler {
  private final QuestionnaireRepository repository;

  public FinishRegisterQuestionnaireHandler(QuestionnaireRepository repository) {
    this.repository = repository;
  }

  @Override
  public Response handle(Request req, Tutor tutor) {

    Questionnaire questionnaire = repository.getLastOrNewQuestionnaire();
    questionnaire.finish();
    Map<Integer, String> answers = questionnaire.getAnswers();
    repository.update(questionnaire);
    return new RsFreemarker("finishquestionnaire.html", ImmutableMap.of("values", questionnaire.getQuestions(),
            "answers", answers, "message", "Въпросник номер " + questionnaire.getId()));
  }
}
