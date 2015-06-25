package notes.objects;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Note {
    private int id = 0;
    private String body = "";

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int result = body.isEmpty() ? 0 : body.hashCode();
        result = 31 * result + id;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Note)) {
            return false;
        }

        Note other = (Note) o;
        if (this.id > 0) {
            if ((this.id == other.id) && this.body.equals(other.body)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
