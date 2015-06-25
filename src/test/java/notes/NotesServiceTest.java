package notes;

import notes.dao.NotesDAO;
import notes.objects.Note;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class NotesServiceTest {

    private static final String TEST_BODY = "TestBody123-";
    private static HttpServer server;
    private static WebTarget target;

    @BeforeClass
    public static void setUp() throws Exception {

        // the first three notes are known
        final NotesDAO notesDAO = NotesDAO.getInstance();
        notesDAO.clearNotes();

        Note note = new Note();
        note.setBody(TEST_BODY + '1');
        notesDAO.addNote(note);

        note = new Note();
        note.setBody(TEST_BODY + '2');
        notesDAO.addNote(note);

        note = new Note();
        note.setBody(TEST_BODY + '3');
        notesDAO.addNote(note);

        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        target = c.target(Main.BASE_URI);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        NotesDAO.getInstance().clearNotes();
        server.shutdown();
    }

    @Test
    public void testAddNote() {
        final String testBody = "This is a note.";
        final Note newNote = new Note();
        newNote.setBody(testBody);

        final Response responseMsg = target.path("notes").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(newNote, MediaType.APPLICATION_JSON_TYPE));
        final Note testNote = responseMsg.readEntity(Note.class);

        Assert.assertNotSame("Note has unexpected id.", 0, testNote.getId());
        Assert.assertEquals("Note has unexpected body.", testBody, testNote.getBody());
    }

    @Test
    public void testGetNotes() {
        // test the first three (known) notes
        final Response responseMsg = target.path("notes").request(MediaType.APPLICATION_JSON).get();
        final List<Note> testNotesList = responseMsg.readEntity(new GenericType<List<Note>>() {
        });

        for (final Note testNote : testNotesList) {
            if (testNote.getId() == 1) {
                Assert.assertEquals("Note has unexpected id.", 1, testNote.getId());
                Assert.assertEquals("Note has unexpected body.", TEST_BODY + 1, testNote.getBody());
            }
            if (testNote.getId() == 2) {
                Assert.assertEquals("Note has unexpected id.", 2, testNote.getId());
                Assert.assertEquals("Note has unexpected body.", TEST_BODY + 2, testNote.getBody());
            }
            if (testNote.getId() == 3) {
                Assert.assertEquals("Note has unexpected id.", 3, testNote.getId());
                Assert.assertEquals("Note has unexpected body.", TEST_BODY + 3, testNote.getBody());
            }
        }
    }

    @Test
    public void testGetNotesWithQuery() {
        // test the first three (known) notes with a query
        final Response responseMsg = target.path("notes").queryParam("query", TEST_BODY).request(MediaType.APPLICATION_JSON).get();
        final List<Note> testNotesList = responseMsg.readEntity(new GenericType<List<Note>>() {
        });

        Assert.assertEquals("Unexpected number of notes returned.", 3, testNotesList.size());
        for (final Note testNote : testNotesList) {
            if (testNote.getId() == 1) {
                Assert.assertEquals("Note has unexpected id.", 1, testNote.getId());
                Assert.assertEquals("Note has unexpected body.", TEST_BODY + 1, testNote.getBody());
            }
            if (testNote.getId() == 2) {
                Assert.assertEquals("Note has unexpected id.", 2, testNote.getId());
                Assert.assertEquals("Note has unexpected body.", TEST_BODY + 2, testNote.getBody());
            }
            if (testNote.getId() == 3) {
                Assert.assertEquals("Note has unexpected id.", 3, testNote.getId());
                Assert.assertEquals("Note has unexpected body.", TEST_BODY + 3, testNote.getBody());
            }
        }
    }

    @Test
    public void testGetNotesWithQueryNotFound() {
        // test the first three (known) notes with a query
        final Response responseMsg = target.path("notes").queryParam("query", TEST_BODY + "!").request(MediaType.APPLICATION_JSON).get();
        final List<Note> testNotesList = responseMsg.readEntity(new GenericType<List<Note>>() {
        });

        Assert.assertTrue("Expected empty list of notes.", testNotesList.isEmpty());
    }

    @Test
    public void testGetNoteById() {
        final int testId = 1;

        final Response responseMsg = target.path("notes/" + testId).request(MediaType.APPLICATION_JSON).get();
        final Note testNote = responseMsg.readEntity(Note.class);

        Assert.assertEquals("Note has unexpected id.", testId, testNote.getId());
        Assert.assertEquals("Note has unexpected body.", TEST_BODY + testId, testNote.getBody());
    }

    @Test
    public void testGetNoteByIdNotFound() {
        final int testId = 0;

        final Response responseMsg = target.path("notes/" + testId).request(MediaType.APPLICATION_JSON).get();
        final Note testNote = responseMsg.readEntity(Note.class);

        Assert.assertNull("Note should not exist.", testNote);
        Assert.assertEquals("Expected 404 Not Found.", 404, responseMsg.getStatus());
    }

    @Test
    public void testDeleteNoteById() {
        final String testBody = "deleteme";
        final Note newNote = new Note();
        newNote.setBody(testBody);

        Response responseMsg = target.path("notes").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(newNote, MediaType.APPLICATION_JSON_TYPE));
        Note testNote = responseMsg.readEntity(Note.class);

        Assert.assertTrue("Note should have id greater than 0.", testNote.getId() > 0);

        final int testId = testNote.getId();

        target.path("notes/" + testId).request(MediaType.APPLICATION_JSON_TYPE).delete();

        responseMsg = target.path("notes/" + testId).request(MediaType.APPLICATION_JSON_TYPE).get();

        Assert.assertEquals("Expected 404 Not Found.", 404, responseMsg.getStatus());
    }

    @Test
    public void testDeleteNoteNotFound() {
        final int testId = 0;

        final Response responseMsg = target.path("notes/" + testId).request(MediaType.APPLICATION_JSON_TYPE).delete();

        Assert.assertEquals("Expected 404 Not Found.", 404, responseMsg.getStatus());
    }

    @Test
    public void testUpdateNote() {
        final Note newNote = new Note();
        newNote.setBody("updateme");

        Response responseMsg = target.path("notes").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(newNote, MediaType.APPLICATION_JSON_TYPE));
        Note testNote = responseMsg.readEntity(Note.class);
        Assert.assertTrue("Note should have id greater than 0.", testNote.getId() > 0);

        final int testId = testNote.getId();
        final String testBody = "updated";
        testNote.setBody(testBody);

        responseMsg = target.path("notes/" + testId).request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(testNote, MediaType.APPLICATION_JSON_TYPE));
        testNote = responseMsg.readEntity(Note.class);

        Assert.assertEquals("Note has unexpected id.", testId, testNote.getId());
        Assert.assertEquals("Note has unexpected body.", testBody, testNote.getBody());
    }

    @Test
    public void testUpdateNoteBadRequest() {
        final Note newNote = new Note();
        newNote.setBody("updateme");

        Response responseMsg = target.path("notes").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(newNote, MediaType.APPLICATION_JSON_TYPE));
        Note testNote = responseMsg.readEntity(Note.class);
        Assert.assertTrue("Note should have id greater than 0.", testNote.getId() > 0);

        final int testId = 0;
        final String testBody = "updated";
        testNote.setBody(testBody);

        responseMsg = target.path("notes/" + testId).request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(testNote, MediaType.APPLICATION_JSON_TYPE));

        Assert.assertEquals("Expected 400 Bad Request.", 400, responseMsg.getStatus());
    }

    @Test
    public void testUpdateNoteNotFound() {
        final Note newNote = new Note();
        newNote.setBody("updateme");

        final Response responseMsg = target.path("notes/" + newNote.getId()).request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(newNote, MediaType.APPLICATION_JSON_TYPE));

        Assert.assertEquals("Expected 404 Not Found.", 404, responseMsg.getStatus());
    }
}
