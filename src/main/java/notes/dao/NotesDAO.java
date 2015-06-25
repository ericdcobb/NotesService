package notes.dao;

import notes.objects.Note;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class NotesDAO {
    private static final NotesDAO ourInstance = new NotesDAO();
    private static ConcurrentHashMap<Integer, Note> notesMap = new ConcurrentHashMap<>();
    private static int maxId = 0;

    private NotesDAO() {
    }

    public static NotesDAO getInstance() {
        return ourInstance;
    }

    public Note addNote(final Note note) {
        note.setId(++maxId);
        notesMap.put(maxId, note);
        return notesMap.get(note.getId());
    }

    public void clearNotes() {
        notesMap.clear();
        maxId = 0;
    }

    public Note getNote(final int id) {
        if (notesMap.containsKey(id)) {
            return notesMap.get(id);
        }
        throw new NotFoundException("Note does not exist for id: " + id);
    }

    public List<Note> getNotes(final String query) {
        final List<Note> theseNotes = new ArrayList<>();

        if (query == null || query.isEmpty()) {
            theseNotes.addAll(notesMap.values());
        } else {
            for (final Note note : notesMap.values()) {
                if (note.getBody().contains(query)) {
                    theseNotes.add(note);
                }
            }
        }

        return theseNotes;
    }

    public void deleteNote(final int id) {
        if (notesMap.containsKey(id)) {
            notesMap.remove(id);
        } else {
            throw new NotFoundException("Note does not exist for id: " + id);
        }
    }

    public Note updateNote(final Note note) {
        final Note gotNote = notesMap.get(note.getId());
        if (gotNote != null) {
            gotNote.setBody(note.getBody());
        } else {
            throw new NotFoundException("Note does not exist for id: " + note.getId());
        }
        return gotNote;
    }
}
