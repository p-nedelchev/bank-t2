package com.clouway.nvuapp.adapter.http.controllers;

import com.clouway.nvuapp.FakeRequest;
import com.clouway.nvuapp.core.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static com.clouway.nvuapp.core.ResponseReader.reader;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author Petar Nedelchev (peter.krasimirov@gmail.com)
 */
public class PdfHandlerTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    @Test
    public void happyPath() throws Exception {
        QuestionnairePdfGenerator pdfGenerator = context.mock(QuestionnairePdfGenerator.class);
        QuestionnaireRepository repository = context.mock(QuestionnaireRepository.class);

        Questionnaire questionnaire = new Questionnaire(1);

        context.checking(new Expectations() {{
            oneOf(repository).getQuestionnaire(with(any(Integer.class)));
            will(returnValue(questionnaire));

            oneOf(pdfGenerator).generate(questionnaire);
            will(returnValue(new byte[] {1, 2, 3}));
        }});

        FakeRequest request = new FakeRequest(ImmutableMap.of("id", 1));

        SecuredHandler handler = new PdfHandler(repository, pdfGenerator);
        Response response = handler.handle(request, new Tutor("", ""));

        assertThat(ByteStreams.toByteArray(response.body()), is((equalTo(new byte[] {1, 2, 3}))));
        assertThat(response.headers(), is(equalTo(ImmutableMap.of("Content-type", "application/pdf", "Content-Disposition", "attachment;filename='questionnaire.pdf'"))));
    }

    @Test
    public void nonNumericIdPassed() throws Exception {
        QuestionnairePdfGenerator pdfGenerator = context.mock(QuestionnairePdfGenerator.class);
        QuestionnaireRepository repository = context.mock(QuestionnaireRepository.class);

        FakeRequest request = new FakeRequest(ImmutableMap.of("id", "char"));

        SecuredHandler handler = new PdfHandler(repository, pdfGenerator);
        Response response = handler.handle(request, new Tutor("", ""));

        assertThat(reader().read(response), containsString("Невалидно ИД"));
    }

    @Test
    public void nonExistingIdPassed() throws Exception {
        QuestionnairePdfGenerator pdfGenerator = context.mock(QuestionnairePdfGenerator.class);
        QuestionnaireRepository repository = context.mock(QuestionnaireRepository.class);

        context.checking(new Expectations() {{
            oneOf(repository).getQuestionnaire(2);
            will(returnValue(null));
        }});

        FakeRequest request = new FakeRequest(ImmutableMap.of("id", 2));

        SecuredHandler handler = new PdfHandler(repository, pdfGenerator);
        Response response = handler.handle(request, new Tutor("", ""));

        assertThat(reader().read(response), containsString("Въпросникът не е наличен"));
    }
}
