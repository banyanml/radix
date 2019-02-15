package nz.shelto.banyan.radix;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ClientWaiting {

    @Id @GeneratedValue
    private Integer id;

    private String requestingHandle;

    private String requesterHandle;

    public String getRequesterHandle() {
        return requesterHandle;
    }

    public void setRequesterHandle(String requesterHandle) {
        this.requesterHandle = requesterHandle;
    }

    public String getRequestingHandle() {
        return requestingHandle;
    }

    public void setRequestingHandle(String requestingHandle) {
        this.requestingHandle = requestingHandle;
    }
}
