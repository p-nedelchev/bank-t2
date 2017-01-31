package com.clouway.nvuapp.adapter.http.controllers;

import com.clouway.nvuapp.FakeRequest;
import com.clouway.nvuapp.adapter.persistence.ConnectionProvider;
import com.clouway.nvuapp.adapter.persistence.PersistentQuestionnaireRepository;
import com.clouway.nvuapp.adapter.persistence.TableManager;
import com.clouway.nvuapp.adapter.persistence.dao.DataStore;
import com.clouway.nvuapp.core.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static com.clouway.nvuapp.core.ResponseReader.reader;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class QuestionnaireListHandlerTest {
    private DataStore dataStore = new DataStore(new ConnectionProvider());
    private TableManager tableManager = new TableManager(dataStore);

    @Before
    public void setUp() throws Exception {
        tableManager.truncateTable("QUESTIONNAIRES");
    }

    @Test
    public void renderListWIthTests() throws Exception {
        QuestionnaireRepository repository = new PersistentQuestionnaireRepository(dataStore);
        Request request = new FakeRequest(Collections.emptyMap());

        Questionnaire questionnaire1 = new Questionnaire(1);
        Questionnaire questionnaire2 = new Questionnaire(2);
        Questionnaire questionnaire3 = new Questionnaire(3);

        repository.register(questionnaire1);
        repository.register(questionnaire2);
        repository.register(questionnaire3);

        QuestionnaireListHandler handler = new QuestionnaireListHandler(repository);
        Response response = handler.handle(request, new Tutor("admin", ""));

        assertThat(reader().read(response), containsString(questionnaire1.getId().toString()));
        assertThat(reader().read(response), containsString(questionnaire2.getId().toString()));
        assertThat(reader().read(response), containsString(questionnaire3.getId().toString()));
    }
}
