package com.clouway.nvuapp.adapter.http.controllers;

import com.clouway.nvuapp.adapter.http.servlet.RsFreemarker;
import com.clouway.nvuapp.core.*;
import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.List;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class ShowRegisterQuestionnaireHandler implements SecuredHandler {
  private final QuestionnaireRepository repository;

  public ShowRegisterQuestionnaireHandler(QuestionnaireRepository repository) {
    this.repository = repository;
  }

  @Override
  public Response handle(Request req, Tutor tutor) {
    Questionnaire questionnaire = repository.getLastOrNewQuestionnaire();
      if (questionnaire.isEmpty()) {
        String message="Нов въпросник номер:"+questionnaire.getId();
        return new RsFreemarker("generateQuestionnaire.html", ImmutableMap.of("values", Collections.emptyList(),"message",message));
      }
    String message = "Има незавършен въпросник номер " +questionnaire.getId() + ", моля завършете първо него";
    List<Question> unfinishedQUestionnairyQuestions = questionnaire.getQuestions();
    return new RsFreemarker("generateQuestionnaire.html", ImmutableMap.of("values",unfinishedQUestionnairyQuestions, "message", message));
  }
}
