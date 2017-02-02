package com.clouway.nvuapp.adapter.http.controllers;

import com.clouway.nvuapp.adapter.http.servlet.RsFreemarker;
import com.clouway.nvuapp.core.*;

import java.util.Collections;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class AdminRegisterQuestionHandler implements SecuredHandler {
  private QuestionRepository repository;

  public AdminRegisterQuestionHandler(QuestionRepository repository) {
    this.repository = repository;
  }

  public Response handle(Request req, Tutor tutor) {
    String category = req.param("category");
    String module = req.param("module");
    String subModl = req.param("submodule");
    String theme = req.param("theme");
    String diff = req.param("difficulty");
    String question = req.param("question");
    String answerA = req.param("answerA");
    String answerB = req.param("answerB");
    String answerC = req.param("answerC");

    if (checkForNullOrEmpty(category, module, subModl, theme, diff, question, answerA, answerB, answerC)) {
      return new RsFreemarker("adminCreateQuestion.html", Collections.<String, Object>singletonMap("message", "Всички полета трябва да бъдат попълнени."));
    }

    String message = repository.register(
            new Question(tutor.tutorId, category, Integer.valueOf(module),
                    Integer.valueOf(subModl), Integer.valueOf(theme),
                    Integer.valueOf(diff), question, answerA, answerB, answerC));
    return new RsFreemarker("adminCreateQuestion.html", Collections.<String, Object>
            singletonMap("message", message));
  }

  private boolean checkForNullOrEmpty(String... values) {
    for (String value : values) {
      if (value == null || value.equals("")) {
        return true;
      }
    }
    return false;
  }
}
