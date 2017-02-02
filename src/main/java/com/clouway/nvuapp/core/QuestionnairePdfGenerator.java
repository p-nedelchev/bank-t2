package com.clouway.nvuapp.core;

/**
 * @author Petar Nedelchev (peter.krasimirov@gmail.com)
 */
public interface QuestionnairePdfGenerator {
    byte[] generate(Questionnaire questionnaire);
}
