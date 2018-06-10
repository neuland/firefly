package de.neuland.firefly.rest.v1;

import com.google.common.base.Optional;
import de.hybris.platform.core.Registry;
import de.neuland.firefly.migration.LockRepository;
import de.neuland.firefly.model.FireflyLockModel;
import de.neuland.firefly.rest.v1.model.Lock;
import org.springframework.stereotype.Controller;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;


@Controller("lockController-v1")
@Path("/v1/lock")
public class LockController {
    @GET
    @Produces(APPLICATION_JSON)
    public Response getLock() {
        Optional<FireflyLockModel> maybeLock = getLockRepository().findLock();

        if (maybeLock.isPresent()) {
            FireflyLockModel lock = maybeLock.get();
            Lock result = new Lock(lock.getCreationtime(),
                                   lock.getClusterNode());

            return Response.status(OK)
                           .entity(result)
                           .build();
        } else {
            return Response.status(NOT_FOUND)
                           .build();
        }
    }

    @DELETE
    public Response removeLock() {
        LockRepository lockRepository = getLockRepository();
        Optional<FireflyLockModel> maybeLock = lockRepository.findLock();

        if (maybeLock.isPresent()) {
            lockRepository.remove(maybeLock.get());
        }

        return Response.status(NO_CONTENT)
                       .build();
    }

    private LockRepository getLockRepository() {
        return Registry.getGlobalApplicationContext().getBean(LockRepository.class)
    }
}
