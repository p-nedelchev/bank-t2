package com.clouway.nvuapp.adapter.http.controllers;

import com.clouway.nvuapp.adapter.http.servlet.RsFreemarker;
import com.clouway.nvuapp.adapter.http.servlet.RsPdf;
import com.clouway.nvuapp.core.*;
import com.google.common.collect.ImmutableMap;


/**
 * @author Petar Nedelchev (peter.krasimirov@gmail.com)
 */
public class PdfHandler implements SecuredHandler {
    private final QuestionnaireRepository repository;
    private final QuestionnairePdfGenerator pdfGenerator;

    public PdfHandler(QuestionnaireRepository repository, QuestionnairePdfGenerator pdfGenerator) {
        this.repository = repository;
        this.pdfGenerator = pdfGenerator;
    }

    @Override
    public Response handle(Request request, Tutor tutor) {
        String id = request.param("id");
        if (!isNumeric(id)) {
            String message = "Невалидно ИД";
            return new RsFreemarker("notAvailableQuestionnaire.html", ImmutableMap.of("message", message));
        }
        Questionnaire questionnaire = repository.getQuestionnaire(Integer.parseInt(id));
        if (questionnaire == null) {
            String message = "Въпросникът не е наличен";
            return new RsFreemarker("notAvailableQuestionnaire.html", ImmutableMap.of("message", message));
        }
        return new RsPdf(pdfGenerator.generate(questionnaire));
    }

    private boolean isNumeric(String input) {
        try {
            int result = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
