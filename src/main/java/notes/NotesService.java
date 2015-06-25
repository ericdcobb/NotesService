package notes;

import notes.dao.NotesDAO;
import notes.objects.Note;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("notes")
@Produces(MediaType.APPLICATION_JSON)
public class NotesService {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Note addNote(final Note note) {
        final Note newNote = NotesDAO.getInstance().addNote(note);
        if (newNote == null) {
            throw new InternalServerErrorException("Failed to add note: " + note.getBody());
        }
        return newNote;
    }

    @GET
    @Path("/{id}")
    public Note getNote(@PathParam("id") final int id) {
        final Note note = NotesDAO.getInstance().getNote(id);
        if (note == null) {
            throw new InternalServerErrorException("Note not found for id: " + id);
        }
        return note;
    }

    @GET
    public List<Note> getNotes(@QueryParam("query") final String query) {
        return NotesDAO.getInstance().getNotes(query);
    }

    @DELETE
    @Path("/{id}")
    public void deleteNote(@PathParam("id") final int id) {
        NotesDAO.getInstance().deleteNote(id);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Note updateNote(@PathParam("id") final int id, final Note note) {
        if (id != note.getId()) {
            throw new BadRequestException("Resource id does not match id of provided note.");
        }
        return NotesDAO.getInstance().updateNote(note);
    }
}
